package com.ecommerce.java_frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerId = request.getParameter("customer_id"); // افترض يجي من session أو parameter

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5004/api/customers/" + customerId))
                .GET()
                .build();

        HttpResponse<String> res;
        try {
            res = client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> profile = mapper.readValue(res.body(), Map.class);

        request.setAttribute("profile", profile);
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }
}
