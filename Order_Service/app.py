from flask import Flask, request, jsonify
import requests
import uuid
from datetime import datetime

app = Flask(__name__)

orders = {}

@app.route("/api/orders/create", methods=["POST"])
def create_order():
    data = request.get_json()

    pricing = requests.post(
        "http://localhost:5003/api/pricing/calculate", json={"products": data["products"]}
    ).json()

    order_id = str(uuid.uuid4())

    orders[order_id] = {
        "order_id": order_id,
        "message": "Order placed successfully",
        "customer_id": data["customer_id"],
        "products": data["products"],
        "total": pricing["total_amount"],
        "timestamp": str(datetime.now())
    }

    return jsonify(orders[order_id]), 201

@app.route("/api/orders/<order_id>")
def get_order(order_id):
    return jsonify(orders.get(order_id, "Order not found"))

if __name__ == "__main__":
    app.run(port=5001, debug= True)
