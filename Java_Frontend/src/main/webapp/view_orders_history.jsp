<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ page import="java.util.List, java.util.Map" %>

        <!DOCTYPE html>
        <html>

        <head>
            <title>📋 Orders History</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background: #f4f6f8;
                    padding: 20px;
                }

                .container {
                    max-width: 900px;
                    margin: auto;
                    background: white;
                    padding: 20px;
                    border-radius: 10px;
                }

                .order {
                    border: 1px solid #ddd;
                    padding: 15px;
                    margin-bottom: 15px;
                    border-radius: 8px;
                    background: #fafafa;
                }

                h2 {
                    color: #2c3e50;
                }
            </style>
        </head>

        <body>
            <div class="container">
                <h2>📋 Orders History</h2>

                <% List<Map<String, Object>> orders =
                    (List<Map<String, Object>>) request.getAttribute("orders");

                        if (orders == null || orders.isEmpty()) {
                        %>
                        <p>No orders found.</p>
                        <% } else { for (Map<String, Object> order : orders) {
                            %>
                            <div class="order">
                                <strong>Order ID:</strong>
                                <%= order.get("order_id") %><br>
                                    <strong>Total Amount:</strong>
                                    <%= order.get("total_amount") %> EGP<br>
                                        <strong>Date:</strong>
                                        <%= order.get("order_date") %>

                                            <h4>Products:</h4>
                                            <ul>
                                                <% List<Map<String, Object>> products =
                                                    (List<Map<String, Object>>) order.get("products");
                                                        for (Map<String, Object> p : products) {
                                                            %>
                                                            <li>
                                                                Product ID: <%= p.get("product_id") %> |
                                                                    Quantity: <%= p.get("quantity") %>
                                                            </li>
                                                            <% } %>
                                            </ul>
                            </div>
                            <% } } %>

                                <a href="products">⬅ Back to Products</a>
            </div>
        </body>

        </html>