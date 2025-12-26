<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper, java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>✅ Order Placed Successfully</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        body { font-family: Arial, sans-serif; background: #f0f8f0; margin: 0; padding: 20px; text-align: center; }
        .container { max-width: 700px; margin: 50px auto; background: white; padding: 40px; border-radius: 15px; box-shadow: 0 0 20px rgba(0,150,0,0.2); }
        h1 { color: #27ae60; }
        .details { background: #f9f9f9; padding: 20px; border-radius: 8px; text-align: left; margin: 20px 0; }
        a { display: inline-block; margin-top: 20px; padding: 12px 30px; background: #3498db; color: white; text-decoration: none; border-radius: 5px; }
    </style>
</head>
<body>
<div class="container">
    <h1>✅ Order Placed Successfully!</h1>
    <p>Thank you for your purchase. Your order has been processed.</p>

    <div class="details">
        <h3>Order Details:</h3>
        <pre style="background:#fff; padding:15px; border:1px solid #ddd; border-radius:5px;">
<%= request.getAttribute("orderResponse") %>
        </pre>
    </div>

    <a href="products">🏠 Back to Catalog</a>
</div>
</body>
</html>