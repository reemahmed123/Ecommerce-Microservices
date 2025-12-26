from flask import Flask, request, jsonify
import mysql.connector
import uuid
from datetime import datetime

app = Flask(__name__)


db_config = {
    "host": "localhost",
    "user": "ecommerce_user",
    "password": "secure_password",
    "database": "ecommerce_system"
}

def get_db_connection():
    return mysql.connector.connect(**db_config)

@app.route("/api/orders/create", methods=["POST"])
def create_order():
    data = request.get_json()
    order_id = str(uuid.uuid4())
    customer_id = data.get("customer_id")
    products = data.get("products") # قائمة المنتجات المختارة

    # حساب الإجمالي أو استقباله (حسب تفضيل التصميم)
    # هنا سنفترض أن الخدمة ستقوم بعملها الأساسي وهو التخزين
    total_amount = data.get("total_amount", 0)

    conn = get_db_connection()
    cursor = conn.cursor()

    try:

        query_order = """
                      INSERT INTO orders (order_id, customer_id, total_amount, order_date)
                      VALUES (%s, %s, %s, %s) \
                      """
        cursor.execute(query_order, (order_id, customer_id, total_amount, datetime.now()))


        query_items = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (%s, %s, %s)"
        for item in products:
            cursor.execute(query_items, (order_id, item['product_id'], item['quantity']))

        conn.commit()

        response = {
            "order_id": order_id,
            "status": "success",
            "message": "Order placed successfully", # [cite: 47]
            "total_amount": total_amount
        }
        return jsonify(response), 201

    except Exception as e:
        conn.rollback()
        return jsonify({"error": str(e)}), 500
    finally:
        cursor.close()
        conn.close()


@app.route("/api/orders/<order_id>", methods=["GET"])
def get_order(order_id):
    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)

    try:
        # جلب بيانات الطلب الأساسية
        cursor.execute("SELECT * FROM orders WHERE order_id = %s", (order_id,))
        order = cursor.fetchone()

        if not order:
            return jsonify({"error": "Order not found"}), 404

        # جلب المنتجات التابعة لهذا الطلب
        cursor.execute("SELECT product_id, quantity FROM order_items WHERE order_id = %s", (order_id,))
        items = cursor.fetchall()

        order['products'] = items
        return jsonify(order), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500
    finally:
        cursor.close()
        conn.close()

@app.route("/api/orders", methods=["GET"])
def get_orders_by_customer():
    customer_id = request.args.get("customer_id")

    if not customer_id:
        return jsonify({"error": "customer_id query parameter is required"}), 400

    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)

    try:
        # جلب كل الطلبات الخاصة بالعميل
        cursor.execute(
            "SELECT * FROM orders WHERE customer_id = %s",
            (customer_id,)
        )
        orders = cursor.fetchall()

        if not orders:
            return jsonify({
                "customer_id": customer_id,
                "orders": []
            }), 200

        # لكل طلب، نجيب المنتجات التابعة له
        for order in orders:
            cursor.execute(
                "SELECT product_id, quantity FROM order_items WHERE order_id = %s",
                (order['order_id'],)
            )
            order['products'] = cursor.fetchall()

        return jsonify({
            "customer_id": customer_id,
            "orders": orders
        }), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500

    finally:
        cursor.close()
        conn.close()


if __name__ == "__main__":

    app.run(port=5001, debug=True)
