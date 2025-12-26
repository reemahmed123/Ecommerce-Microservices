package com.ecommerce.java_frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/products")
public class InventoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5002/api/inventory/all"))
                .GET()
                .build();

        HttpResponse<String> res;
        try {
            res = client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> allProducts = mapper.readValue(res.body(), new TypeReference<>() {});

        // quantity_available > 0
        List<Map<String, Object>> filteredProducts = allProducts.stream()
                .filter(p -> Integer.parseInt(p.get("quantity_available").toString()) > 0)
                .collect(Collectors.toList());

        request.setAttribute("products", filteredProducts);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}