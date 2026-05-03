package com.ecommerce.java_frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("🔥 CheckoutServlet HIT 🔥");

        String customerId = request.getParameter("customer_id");
        String[] productIds = request.getParameterValues("product_id[]");  // دعم متعدد
        String[] quantities = request.getParameterValues("quantity[]");

        if (productIds == null || quantities == null || productIds.length != quantities.length) {
            request.setAttribute("error", "❌ Invalid selection.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>> selectedProducts = new ArrayList<>();
        double totalAmount = 0.0;

        for (int i = 0; i < productIds.length; i++) {
            int qty = Integer.parseInt(quantities[i]);
            if (qty <= 0) continue;
            int productId = Integer.parseInt(productIds[i]);

            // 1. Check inventory
            HttpRequest invReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5002/api/inventory/check/" + productId))
                    .GET()
                    .build();

            HttpResponse<String> invRes;
            try {
                invRes = client.send(invReq, HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException e) {
                throw new ServletException(e);
            }

            if (invRes.statusCode() != 200) {
                request.setAttribute("error", "❌ Product " + productId + " not found.");
                request.getRequestDispatcher("index.jsp").forward(request, response);
                return;
            }

            Map<String, Object> product = mapper.readValue(invRes.body(), Map.class);
            int availableQty = Integer.parseInt(product.get("quantity_available").toString());

//            if (qty > availableQty || qty <= 0) {
//                request.setAttribute("error", "❌ Invalid quantity for product " + productId + ".");
//                request.getRequestDispatcher("index.jsp").forward(request, response);
//                return;
//            }

            selectedProducts.add(Map.of("product_id", productId, "quantity", qty, "price", product.get("unit_price")));
        }

        // 2. Calculate total using Pricing Service
        Map<String, Object> pricingPayload = new HashMap<>();
        pricingPayload.put("products", selectedProducts);

        String jsonPayload = mapper.writeValueAsString(pricingPayload);

        HttpRequest pricingReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5003/api/pricing/calculate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> pricingRes;
        try {
            pricingRes = client.send(pricingReq, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }

        if (pricingRes.statusCode() != 200) {
            request.setAttribute("error", "❌ Error calculating total.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        Map pricingResult = mapper.readValue(pricingRes.body(), Map.class);
        totalAmount = (Double) pricingResult.get("total_amount");


        // Pass to check out.jsp
        request.setAttribute("customer_id", customerId);
        request.setAttribute("selected_products", selectedProducts);
        request.setAttribute("total_amount", totalAmount);
        request.getRequestDispatcher("checkout.jsp").forward(request, response);
    }
}