from flask import Flask, jsonify, request
import mysql.connector

app = Flask(__name__)

conn = mysql.connector.connect(
    host="localhost",
    user="ecommerce_user",
    password="secure_password",
    database="ecommerce_system"
)
cursor = conn.cursor(dictionary=True)

@app.route("/api/inventory/check/<int:product_id>", methods = ['GET'])
def check_inventory(product_id):
    cursor.execute(
        "SELECT * FROM inventory WHERE product_id = %s", (product_id,)
    )
    product = cursor.fetchone()
    
    if product:
        return jsonify(product), 200
    else:
        return jsonify({"error": "Product not found"}), 404
    

@app.route("/api/inventory/update", methods=["PUT"])
def update_inventory():
    data = request.get_json()
    product_id = data.get('product_id')
    quantity = data.get('quantity')
    cursor.execute(
        "UPDATE inventory SET quantity_available = quantity_available - %s WHERE product_id = %s",
        (quantity, product_id)
    )
    conn.commit()
    return jsonify({"message": "Inventory updated successfully"}), 200

if __name__ == "__main__":
    app.run(port=5002, debug=True)
