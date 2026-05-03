import requests
from flask import Flask, jsonify, request
import mysql.connector

app = Flask(__name__)

@app.route('/')
def home():
    return "Customer Service is Running!"

# ======================
# Database Connection
# ======================
db = mysql.connector.connect(
    host="localhost",
    user="root",
    password="reem",
    database="ecommerce_system"
)

cursor = db.cursor(dictionary=True)

# ======================
# GET Customer By ID
# ======================
@app.route("/api/customers/<int:customer_id>", methods=["GET"])
def get_customer(customer_id):
    cursor.execute(
        "SELECT customer_id, name, email, phone, loyalty_points FROM customers WHERE customer_id = %s",
        (customer_id,)
    )
    customer = cursor.fetchone()

    if customer is None:
        return jsonify({"error": "Customer not found"}), 404

    return jsonify(customer), 200


@app.route("/api/customers/<int:customer_id>/orders", methods=["GET"])
def get_customer_orders(customer_id):

    # 1️⃣ Check customer exists
    cursor.execute(
        "SELECT customer_id FROM customers WHERE customer_id = %s",
        (customer_id,)
    )
    if not cursor.fetchone():
        return jsonify({"error": "Customer not found"}), 404

    # 2️⃣ Call Order Service
    order_service_url = f"http://localhost:5001/api/orders?customer_id={customer_id}"
    response = requests.get(order_service_url)

    if response.status_code != 200:
        return jsonify({"error": "Order Service unavailable"}), 503

    # 3️⃣ Extract ONLY order IDs
    data = response.json()
    orders = data.get("orders", [])

    order_ids = [order["order_id"] for order in orders]

    # 4️⃣ Return LIST OF STRINGS
    return jsonify(order_ids), 200

    
    


# ======================
# UPDATE Loyalty Points
# ======================
@app.route("/api/customers/<int:customer_id>/loyalty", methods=["PUT"])
def update_loyalty(customer_id):
    data = request.get_json()
    new_points = data.get("loyalty_points")

    if new_points is None:
        return jsonify({"error": "loyalty_points is required"}), 400

    cursor.execute(
        "UPDATE customers SET loyalty_points = %s WHERE customer_id = %s",
        (new_points, customer_id)
    )

    if cursor.rowcount == 0:
        return jsonify({"error": "Customer not found"}), 404

    db.commit()

    return jsonify({"message": "Loyalty points updated successfully"}), 200


if __name__ == '__main__':
    app.run(port=5004, debug=True)
