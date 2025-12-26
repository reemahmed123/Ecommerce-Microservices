from flask import Flask, jsonify, request
import mysql.connector

app = Flask(__name__)

db_config = {
    "host": "localhost",
    "user": "ecommerce_user",
    "password": "secure_password",
    "database": "ecommerce_system"
}

def get_db_connection():
    return mysql.connector.connect(**db_config)

@app.route("/api/inventory/all", methods=['GET'])
def get_all_inventory():
    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)
    try:
        # تأكد من أن أسماء الأعمدة تطابق الـ JSP (name, price)
        # وفلترة المنتجات المتاحة فقط [cite: 2]
        cursor.execute("SELECT product_id, product_name, unit_price, quantity_available FROM inventory WHERE quantity_available > 0")
        products = cursor.fetchall()
        return jsonify(products), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        cursor.close()
        conn.close()

@app.route("/api/inventory/check/<int:product_id>", methods=['GET'])
def check_inventory(product_id):
    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT * FROM inventory WHERE product_id = %s", (product_id,))
    product = cursor.fetchone()
    cursor.close()
    conn.close()
    return jsonify(product) if product else (jsonify({"error": "Not found"}), 404)

@app.route("/api/inventory/update", methods=["PUT"])
def update_inventory():
    data = request.get_json()
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("UPDATE inventory SET quantity_available = quantity_available - %s WHERE product_id = %s",
                   (data['quantity'], data['product_id']))
    conn.commit()
    cursor.close()
    conn.close()
    return jsonify({"message": "Updated"}), 200


@app.route("/api/inventory/check-stock", methods=["POST"])
def check_stock():
    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)
    data = request.get_json()
    products = data.get("products", [])

    if not products:
        return jsonify({"error": "Products list is required"}), 400

    for item in products:
        product_id = item.get("product_id")
        required_qty = item.get("quantity")

        cursor.execute(
            "SELECT quantity_available FROM inventory WHERE product_id = %s",
            (product_id,)
        )
        product = cursor.fetchone()

        if not product:
            return jsonify({
                "status": "FAILED",
                "message": f"Product {product_id} not found"
            }), 404

        if product["quantity_available"] < required_qty:
            return jsonify({
                "status": "FAILED",
                "message": f"Product {product_id} is out of stock",
                "available_quantity": product["quantity_available"]
            }), 400

    return jsonify({
        "status": "OK",
        "message": "All products are available"
    }), 200

if __name__ == "__main__":
    app.run(port=5002, debug=True)
