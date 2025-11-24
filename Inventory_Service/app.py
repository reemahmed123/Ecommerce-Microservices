from flask import Flask
import mysql.connector

app = Flask(__name__)


db_config = {
    'host': 'localhost',
    'user': 'ecommerce_user',      
    'password': 'secure_password', 
    'database': 'ecommerce_system' 
}

@app.route('/test-db')
def test_connection():
    try:
        
        conn = mysql.connector.connect(**db_config)
        
        if conn.is_connected():
            return "Successful!"
            
        conn.close()
    except Exception as e:
        
        return f"connection failed"

if __name__ == '__main__':
    
    app.run(port=5002, debug=True)
