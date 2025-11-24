from flask import Flask, jsonify

app = Flask(__name__)

@app.route('/')
def home():
    return "Order Service is Running!"

if __name__ == '__main__':
    # انتبهي لرقم البورت المخصص لكل خدمة كما في المستند
    app.run(port=5001, debug=True)