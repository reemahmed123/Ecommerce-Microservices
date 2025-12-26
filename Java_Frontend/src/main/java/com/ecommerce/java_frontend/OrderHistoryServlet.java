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

@WebServlet("/history")
public class OrderHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerId = request.getParameter("customer_id");  // من session أو parameter

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        // 1. Get orders list
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

        List<String> orderIds = mapper.readValue(ordersRes.body(), new TypeReference<>() {});

        // 2. Get details for each order
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

            Map<String, Object> detail = mapper.readValue(detailRes.body(), Map.class);
            orderDetails.add(detail);
        }

        request.setAttribute("orders", orderDetails);
        request.getRequestDispatcher("view_orders_history.jsp").forward(request, response);
    }
}