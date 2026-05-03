<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="java.util.List, java.util.Map" %>
        <!DOCTYPE html>
        <html>

        <head>
            <title>🛒 Product Catalog</title>
            <link rel="stylesheet" href="css/style.css">
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background: #f9f9f9;
                    margin: 0;
                    padding: 20px;
                }

                .container {
                    max-width: 900px;
                    margin: auto;
                    background: white;
                    padding: 20px;
                    border-radius: 10px;
                    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                }

                h1 {
                    color: #2c3e50;
                    text-align: center;
                }

                .product {
                    border: 1px solid #ddd;
                    padding: 15px;
                    margin: 10px 0;
                    border-radius: 8px;
                    background: #f8f9fa;
                }

                .product input[type="number"] {
                    width: 60px;
                    padding: 5px;
                }

                .links {
                    text-align: center;
                    margin: 20px 0;
                }

                .links a {
                    margin: 0 15px;
                    font-size: 1.1em;
                    text-decoration: none;
                }
            </style>
        </head>

        <body>
            <% if (request.getAttribute("products")==null) { response.sendRedirect("products"); return; } %>

                <div class="container">
                    <h1>🛒 Product Catalog</h1>
                    <% if (request.getAttribute("error") !=null) { %>
                        <p style="color:red; font-weight:bold;">
                            <%= request.getAttribute("error") %>
                        </p>
                        <% } %>


                            <!-- لينكات المطلوبة في السيناريو -->
                            <div class="links">
                                <a href="#" onclick="goToProfile()">👤 View Profile</a>
                                <a href="#" onclick="goToHistory()">📋 Orders History</a>

                            </div>

                            <form action="checkout" method="post">
                                <label><strong>Customer ID:</strong></label>
                                <input type="number" name="customer_id" id="customerIdInput" required placeholder="Enter your ID"
                                    style="padding:8px; width:200px; margin-bottom:20px;"><br><br>

                                <h2>Available Products</h2>

                                <% List<Map<String, Object>> products = (List<Map<String, Object>>)
                                        request.getAttribute("products");
                                        if (products == null || products.isEmpty()) {
                                        %>
                                        <p>No products available at the moment.</p>
                                        <% } else { for (Map<String, Object> product : products) {
                                            int qty = Integer.parseInt(product.get("quantity_available").toString());
                                            %>
                                            <div class="product">
                                                <strong>Product ID: <%= product.get("product_id") %></strong><br>
                                                Name: <%= product.get("product_name") !=null ?
                                                    product.get("product_name") : "Unknown" %><br>
                                                    Price: $<%= product.get("unit_price") %><br>
                                                        Available: <%= qty %><br>

                                                            <label>
                                                                Quantity to buy:
                                                                <input type="number" name="quantity[]" min="0"
                                                                    max="<%= qty %>" value="0"
                                                                    style="margin-left:10px;">
                                                            </label>
                                                            <input type="hidden" name="product_id[]"
                                                                value="<%= product.get(" product_id") %>">
                                            </div>
                                            <% } } %>

                                                <br>
                                                <button type="submit"
                                                    style="background:#27ae60; color:white; padding:12px 30px; border:none; border-radius:5px; font-size:1.1em; cursor:pointer;">
                                                    🛒 Make Order
                                                </button>
                            </form>

                            <br>
                            <a href="products">🔄 Refresh Catalog</a>
                </div>
        </body>
<script>
    function goToProfile() {
        const customerId = document.getElementById("customerIdInput").value;

        if (!customerId) {
            alert("⚠️ Please enter Customer ID first");
            return;
        }

        window.location.href = "profile?customer_id=" + customerId;
    }
    function goToHistory() {
            const customerId = document.getElementById("customerIdInput").value;

            if (!customerId) {
                alert("⚠️ Please enter Customer ID first");
                return;
            }

            window.location.href = "history?customer_id=" + customerId;
        }
</script>

        </html>
