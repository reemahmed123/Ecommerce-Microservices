from flask import Flask, request, jsonify
import requests
import mysql.connector

app = Flask(__name__)

@app.route("/")
def home():
    return "Notification Service is Running!"

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
# SEND NOTIFICATION
# ======================
@app.route("/api/notifications/send", methods=["POST"])
def send_notification():
    data = request.get_json()
    order_id = data.get("order_id")

    if not order_id:
        return jsonify({"error": "order_id is required"}), 400

    # ======================
    # 1️⃣ Call Order Service
    # ======================
    order_url = f"http://localhost:5001/api/orders/{order_id}"
    order_res = requests.get(order_url)

    if order_res.status_code != 200:
        return jsonify({"error": "Order not found"}), 404

    order = order_res.json()
    customer_id = order["customer_id"]
    products = order["products"]

    # ======================
    # 2️⃣ Call Customer Service
    # ======================
    customer_url = f"http://localhost:5004/api/customers/{customer_id}"
    customer_res = requests.get(customer_url)

    if customer_res.status_code != 200:
        return jsonify({"error": "Customer Service unavailable"}), 503

    customer = customer_res.json()
    customer_email = customer["email"]
    customer_phone = customer["phone"]

    # ======================
    # 3️⃣ Call Inventory Service
    # ======================
    inventory_url = "http://localhost:5002/api/inventory/check-stock"
    inventory_res = requests.post(inventory_url, json={"products": products})
    
    if inventory_res.status_code != 200:
        return jsonify({
        "error": "Order cannot be handled",
        "details": inventory_res.json()
    }), 400

    inventory_result = inventory_res.json()

    if inventory_result.get("status") != "OK":
       return jsonify({
        "error": "Order cannot be handled",
        "reason": inventory_result.get("message")
    }), 400

# ✅ Fixed delivery estimation
    delivery_estimate = "3-5 days"

    message = (
        f"Your order #{order_id} has been confirmed.\n"
        f"Estimated delivery: {delivery_estimate}."
    )

    # ======================
    # 5️⃣ Simulate Email / SMS
    # ======================
    print(f"EMAIL SENT TO: {customer_email}")
    print(f"Subject: Order #{order_id} Confirmed")
    print(f"Body: {message}")

    print(f"SMS SENT TO: {customer_phone}")
    print(f"Message: {message}")

    # ======================
    # 6️⃣ Log to Database
    # ======================
    cursor.execute(
        """
        INSERT INTO notification_log
        (order_id, customer_id, notification_type, message)
        VALUES (%s, %s, %s, %s)
        """,
        (order_id, customer_id, "EMAIL_SMS", message)
    )
    db.commit()

    return jsonify({
        "message": "Notification sent successfully",
        "order_id": order_id,
        "customer_id": customer_id
    }), 200


if __name__ == "__main__":
    app.run(port=5005, debug=True)
