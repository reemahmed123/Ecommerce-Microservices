from flask import Flask, request, jsonify
import requests
import mysql.connector

app = Flask(__name__)

db = mysql.connector.connect(
    host="localhost",
    user="ecommerce_user",
    password="secure_password",
    database="ecommerce_system"
)
cursor = db.cursor(dictionary=True)

@app.route("/api/pricing/calculate", methods=["POST"])
def calculate_price():
    data = request.get_json()
    products = data.get('products', [])
    total_amount = 0
    breakdown_list = []

    for item in products:
        product_id = item["product_id"]
        qantity = item["quantity"]

        inventory = requests.get(
            f"http://localhost:5002/api/inventory/check/{product_id}"
        ).json() 

        price = float(inventory["unit_price"]) * int(qantity)

        cursor.execute(
            "SELECT * FROM pricing_rules WHERE product_id=%s AND min_quantity<=%s",
            (product_id, qantity)
        )
        rule = cursor.fetchone()

        if rule:
            price -= price * (rule["discount_percentage"] / 100)

        breakdown_list.append({
            "product_name": inventory['product_name'],
            "unit_price": inventory["unit_price"],
            "quantity": qantity,
            "subtotal": price
        })
        total_amount += price

    return jsonify({
        "total_amount": total_amount,
        "currency": "EGP",
        "breakdown": breakdown_list
    })

if __name__ == "__main__":
    app.run(port=5003, debug=True)
