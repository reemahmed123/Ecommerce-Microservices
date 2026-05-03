package com.ecommerce.java_frontend;

import java.net.http.*;
import java.io.IOException;
import java.net.URI;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/submitOrder")
public class OrderServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String customerId = request.getParameter("customer_id");
        String[] productIds = request.getParameterValues("product_id[]");
        String[] quantities = request.getParameterValues("quantity[]");
        double totalAmount = Double.parseDouble(request.getParameter("total_amount"));  // من checkout.jsp

        if (productIds == null || quantities == null || productIds.length != quantities.length) {
            request.setAttribute("error", "❌ Invalid order data.");
            request.getRequestDispatcher("checkout.jsp").forward(request, response);
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        // 1. Validate customer
        HttpRequest customerReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5004/api/customers/" + customerId))
                .GET()
                .build();

        HttpResponse<String> customerRes;
        try {
            customerRes = client.send(customerReq, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }

        if (customerRes.statusCode() != 200) {
            request.setAttribute("error", "❌ Customer ID does not exist.");
            request.getRequestDispatcher("checkout.jsp").forward(request, response);
            return;
        }

        // 2. Build products list
        List<Map<String, Object>> products = new ArrayList<>();
        for (int i = 0; i < productIds.length; i++) {
            products.add(Map.of(
                    "product_id", Integer.parseInt(productIds[i]),
                    "quantity", Integer.parseInt(quantities[i])
            ));
        }

        // 3. Place order
        Map<String, Object> payload = Map.of(
                "customer_id", Integer.parseInt(customerId),
                "products", products
        );
        String jsonPayload = mapper.writeValueAsString(payload);

        HttpRequest orderReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5001/api/orders/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> orderRes;
        try {
            orderRes = client.send(orderReq, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }

        if (orderRes.statusCode() != 201) {
            request.setAttribute("error", "❌ Error creating order.");
            request.getRequestDispatcher("checkout.jsp").forward(request, response);
            return;
        }

        Map<String, Object> orderDetails = mapper.readValue(orderRes.body(), Map.class);
        String orderId = (String) orderDetails.get("order_id");

        // 4. Update inventory for each product
        for (Map<String, Object> prod : products) {
            int prodId = (Integer) prod.get("product_id");
            int qty = (Integer) prod.get("quantity");

            Map<String, Object> invPayload = Map.of("product_id", prodId, "quantity", qty);
            String invJson = mapper.writeValueAsString(invPayload);

            HttpRequest invUpdateReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5002/api/inventory/update"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(invJson))
                    .build();

            try {
                client.send(invUpdateReq, HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException e) {
                // Log error, but continue (optional rollback if needed)
            }
        }

        // 5. Update loyalty points
        int loyaltyPointsToAdd = (int) (totalAmount / 10);  // مثال، غير حسب المنطق
        Map<String, Object> loyaltyPayload = Map.of("points", loyaltyPointsToAdd);
        String loyaltyJson = mapper.writeValueAsString(loyaltyPayload);

        HttpRequest loyaltyReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5004/api/customers/" + customerId + "/loyalty"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(loyaltyJson))
                .build();

        try {
            client.send(loyaltyReq, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            // Log error
        }

        // 6. Send notification
        Map<String, Object> notifPayload = Map.of("order_id", orderId, "customer_id", customerId, "message", "Your order has been placed!");
        String notifJson = mapper.writeValueAsString(notifPayload);

        HttpRequest notifReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5005/api/notifications/send"))  // افترض port للـ Notification Service
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(notifJson))
                .build();

        try {
            client.send(notifReq, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            // Log error
        }

        // 7. Success
        request.setAttribute("orderResponse", orderRes.body());
        request.getRequestDispatcher("confirmation.jsp").forward(request, response);
    }
}