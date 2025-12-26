<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <html>

        <head>
            <title>Customer Profile</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    margin: 40px;
                }

                .profile-box {
                    border: 1px solid #ccc;
                    padding: 20px;
                    width: 400px;
                }

                h2 {
                    color: #2c3e50;
                }

                p {
                    font-size: 16px;
                }
            </style>
        </head>

        <body>

            <h2>👤 Customer Profile</h2>

            <c:if test="${empty profile}">
                <p style="color:red;">Customer profile not found.</p>
            </c:if>

            <c:if test="${not empty profile}">
                <div class="profile-box">
                    <p><strong>ID:</strong> ${profile.customer_id}</p>
                    <p><strong>Name:</strong> ${profile.name}</p>
                    <p><strong>Email:</strong> ${profile.email}</p>
                    <p><strong>Phone:</strong> ${profile.phone}</p>
                    <p><strong>Loyalty Points:</strong> ${profile.loyalty_points}</p>
                </div>
            </c:if>

            <br>
            <a href="index.jsp">⬅ Back to Home</a>

        </body>

        </html>