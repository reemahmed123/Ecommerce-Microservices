<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>🧾 Confirm Your Order</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        body { font-family: Arial, sans-serif; background: #f4f6f9; margin: 0; padding: 20px; }
        .container { max-width: 800px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 15px rgba(0,0,0,0.1); }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th, td { border: 1px solid #ccc; padding: 12px; text-align: center; }
        th { background: #3498db; color: white; }
        .total { font-size: 1.4em; font-weight: bold; color: #27ae60; text-align: right; margin: 20px 0; }
        .buttons { text-align: center; margin: 30px 0; }
        button { padding: 12px 30px; font-size: 1.1em; border: none; border-radius: 5px; cursor: pointer; margin: 0 10px; }
        .confirm { background: #27ae60; color: white; }
        .cancel { background: #e74c3c; color: white; text-decoration: none; padding: 12px 30px; border-radius: 5px; }
    </style>
</head>
<body>

<div class="container">
    <h2>🧾 Order Confirmation</h2>

    <% if (request.getAttribute("error") != null) { %>
    <p style="color:red; font-weight:bold; background:#ffe6e6; padding:15px; border-radius:5px;">
        <%= request.getAttribute("error") %>
    </p>
    <% } %>

    <p><strong>Customer ID:</strong> <%= request.getAttribute("customer_id") %></p>

    <table>
        <thead>
        <tr>
            <th>Product ID</th>
            <th>Quantity</th>
            <th>Unit Price</th>
            <th>Subtotal</th>
        </tr>
        </thead>
        <tbody>
        <%
            List<Map<String, Object>> products = (List<Map<String, Object>>) request.getAttribute("selected_products");
            double total = 0;
            if (products != null) {
                for (Map<String, Object> item : products) {
                    int qty = Integer.parseInt(item.get("quantity").toString());
                    double price = Double.parseDouble(item.get("price").toString());
                    double subtotal = qty * price;
                    total += subtotal;
        %>
        <tr>
            <td><%= item.get("product_id") %></td>
            <td><%= qty %></td>
            <td>$<%= String.format("%.2f", price) %></td>
            <td>$<%= String.format("%.2f", subtotal) %></td>
        </tr>
        <%
                }
            }
        %>
        </tbody>
    </table>

    <div class="total">
        Total Amount: $<%= String.format("%.2f", request.getAttribute("total_amount")) %>
    </div>

    <form action="submitOrder" method="post" class="buttons">
        <input type="hidden" name="customer_id" value="<%= request.getAttribute("customer_id") %>">
        <input type="hidden" name="total_amount" value="<%= request.getAttribute("total_amount") %>">

        <%
            if (products != null) {
                for (Map<String, Object> item : products) {
        %>
        <input type="hidden" name="product_id[]" value="<%= item.get("product_id") %>">
        <input type="hidden" name="quantity[]" value="<%= item.get("quantity") %>">
        <%
                }
            }
        %>

        <button type="submit" class="confirm">✅ Confirm Order</button>
        <a href="products" class="cancel">❌ Cancel</a>
    </form>
</div>
</body>
</html>