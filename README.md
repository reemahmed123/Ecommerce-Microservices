# E-Commerce Order Management System

A microservices-based e-commerce system built as part of the Service-Oriented 
Architecture course at Cairo University. The system demonstrates microservices 
architecture principles using Python Flask backend services and a Java JSP frontend.

## 👥 Contributors
- [Reem Ahmed](https://github.com/reemahmed123)
- [Mariam Assem](https://github.com/mar-yam11)

---

## 🏗️ Architecture Overview

A Java JSP web application acts as the API gateway and communicates with 
5 independent Python Flask microservices, each running on a dedicated port.
[Java JSP Frontend :8080]
|
├──> Order Service        :5001
├──> Inventory Service    :5002
├──> Pricing Service      :5003
├──> Customer Service     :5004
└──> Notification Service :5005

---

## 🛠️ Tech Stack

| Layer     | Technology                        |
|-----------|-----------------------------------|
| Frontend  | Java JSP, Jakarta EE, Servlets    |
| Backend   | Python 3.8+, Flask                |
| Database  | MySQL 8.0                         |
| Server    | Apache Tomcat 10.x                |
| Comm      | REST APIs, JSON                   |

---

## ⚙️ Microservices

### 1. Order Service (port 5001)
Handles order creation and validation.
- `POST /api/orders/create` — Create new order
- `GET /api/orders/{order_id}` — Retrieve order details
- `GET /api/orders?customer_id={id}` — Get orders by customer

### 2. Inventory Service (port 5002)
Manages product stock with MySQL database.
- `GET /api/inventory/check/{product_id}` — Check stock availability
- `PUT /api/inventory/update` — Update stock after order

### 3. Pricing Service (port 5003)
Calculates final pricing with discounts and tax rules.
- `POST /api/pricing/calculate` — Calculate order total

### 4. Customer Service (port 5004)
Manages customer profiles and loyalty points.
- `GET /api/customers/{customer_id}` — Get customer profile
- `GET /api/customers/{customer_id}/orders` — Get order history
- `PUT /api/customers/{customer_id}/loyalty` — Update loyalty points

### 5. Notification Service (port 5005)
Aggregates data from multiple services and logs notifications.
- `POST /api/notifications/send` — Send order notification

---

## 🗄️ Database Setup

```sql
CREATE DATABASE ecommerce_system;
USE ecommerce_system;
```

Then run the table creation scripts found in the `/Database` folder.

---

## 🚀 How to Run

### 1. Start MySQL and set up the database
```bash
# Run all SQL scripts in /Database folder
```

### 2. Start each Flask service
```bash
cd Order_Service
pip install -r requirements.txt
python app.py

# Repeat for each service folder
```

### 3. Start the Java frontend
```bash
# Open Java_Frontend in NetBeans or IntelliJ
# Deploy to Apache Tomcat 10.x on port 8080
```

### 4. Open in browser
http://localhost:8080

---

## 📁 Project Structure
├── Order_Service/
├── Inventory_Service/
├── Pricing_Service/
├── Customer_Service/
├── Notification_Service/
├── Java_Frontend/
└── Database/

---

## 📌 Key Concepts Demonstrated
- Microservices architecture
- Inter-service REST communication
- Service composition and orchestration
- Role-based data access
- MySQL database integration with parameterized queries
