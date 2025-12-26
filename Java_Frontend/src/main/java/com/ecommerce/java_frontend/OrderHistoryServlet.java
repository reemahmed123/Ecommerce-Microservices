package com.ecommerce.java_frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*@WebServlet("/history")
public class OrderHistoryServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerId = request.getParameter("customer_id");

        if (customerId == null || customerId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Customer ID is required");
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        // ===============================
        // 1️⃣ Call Customer Service
        // ===============================
        HttpRequest ordersReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5004/api/customers/" + customerId + "/orders"))
                .GET()
                .build();

        HttpResponse<String> ordersRes;
        
        try {
            ordersRes = client.send(ordersReq, HttpResponse.BodyHandlers.ofString());
            if (ordersRes.statusCode() == 404) {
                request.setAttribute("errorMessage", "Customer not found. Please check the ID.");
                request.getRequestDispatcher("view_orders_history.jsp")
                        .forward(request, response);
                return;
            }
            if (ordersRes.statusCode() != 200) {
                request.setAttribute("errorMessage", "Unable to retrieve orders. Please try again later.");
                request.getRequestDispatcher("view_orders_history.jsp")
                        .forward(request, response);
                return;
            }
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }

        // ===============================
        // 2️⃣ Parse FULL response as MAP
        // ===============================
        Map<String, Object> responseMap = mapper.readValue(ordersRes.body(), new TypeReference<>() {
        });

        Map<String, Object> ordersWrapper = (Map<String, Object>) responseMap.get("orders");

        List<Map<String, Object>> ordersList = (List<Map<String, Object>>) ordersWrapper.get("orders");

        // ===============================
        // 3️⃣ Fetch order details
        // ===============================
        List<Map<String, Object>> orderDetails = new ArrayList<>();

        for (Map<String, Object> order : ordersList) {
            String orderId = order.get("order_id").toString();

            HttpRequest detailReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5001/api/orders/" + orderId))
                    .GET()
                    .build();

            HttpResponse<String> detailRes;
            try {
                detailRes = client.send(detailReq, HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException e) {
                throw new ServletException(e);
            }

            Map<String, Object> detail = mapper.readValue(detailRes.body(), new TypeReference<>() {
            });
            orderDetails.add(detail);
        }

        // ===============================
        // 4️⃣ Forward to JSP
        // ===============================
        request.setAttribute("orders", orderDetails);
        request.getRequestDispatcher("view_orders_history.jsp")
                .forward(request, response);
    }
}*/



@WebServlet("/history")
public class OrderHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerId = request.getParameter("customer_id");

        // ===============================
        // 0️⃣ Validate input
        // ===============================
        if (customerId == null || customerId.isEmpty()) {
            request.setAttribute("errorMessage", "Customer ID is required.");
            request.getRequestDispatcher("view_orders_history.jsp")
                    .forward(request, response);
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        // ===============================
        // 1️⃣ Call Customer Service
        // ===============================
        HttpRequest ordersReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5004/api/customers/" + customerId + "/orders"))
                .GET()
                .build();

        HttpResponse<String> ordersRes;

        try {
            ordersRes = client.send(ordersReq, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }

        // ===============================
        // 2️⃣ Handle Customer Service errors
        // ===============================
        if (ordersRes.statusCode() == 404) {
            request.setAttribute("errorMessage", "Customer not found.");
            request.getRequestDispatcher("view_orders_history.jsp")
                    .forward(request, response);
            return;
        }

        if (ordersRes.statusCode() != 200) {
            request.setAttribute("errorMessage", "Unable to retrieve orders. Please try again later.");
            request.getRequestDispatcher("view_orders_history.jsp")
                    .forward(request, response);
            return;
        }

        // ===============================
        // 3️⃣ Parse order IDs (ARRAY OF STRINGS)
        // ===============================
        List<String> orderIds;

        try {
            orderIds = mapper.readValue(
                    ordersRes.body(),
                    new TypeReference<List<String>>() {}
            );
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Invalid response from Customer Service.");
            request.getRequestDispatcher("view_orders_history.jsp")
                    .forward(request, response);
            return;
        }

        if (orderIds.isEmpty()) {
            request.setAttribute("message", "No orders found for this customer.");
            request.getRequestDispatcher("view_orders_history.jsp")
                    .forward(request, response);
            return;
        }

        // ===============================
        // 4️⃣ Fetch order details from Order Service
        // ===============================
        List<Map<String, Object>> orderDetails = new ArrayList<>();

        for (String orderId : orderIds) {

            HttpRequest detailReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5001/api/orders/" + orderId))
                    .GET()
                    .build();

            HttpResponse<String> detailRes;

            try {
                detailRes = client.send(detailReq, HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException e) {
                throw new ServletException(e);
            }

            if (detailRes.statusCode() != 200)
                continue;

            Map<String, Object> detail = mapper.readValue(
                    detailRes.body(),
                    new TypeReference<>() {}
            );

            orderDetails.add(detail);
        }

    // ===============================
    // 5️⃣ Forward to JSP
    // ===============================
    request.setAttribute("orders",orderDetails);request.getRequestDispatcher("view_orders_history.jsp").forward(request,response);
}}
