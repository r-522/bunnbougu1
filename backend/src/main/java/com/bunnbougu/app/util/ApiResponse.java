package com.bunnbougu.app.util;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class ApiResponse {
    private ApiResponse() {
    }

    public static void sendJson(HttpExchange exchange, int status, Object body) throws IOException {
        byte[] response = JsonUtil.GSON.toJson(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(status, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    public static void sendMessage(HttpExchange exchange, int status, String message) throws IOException {
        Map<String, String> payload = new HashMap<>();
        payload.put("message", message);
        sendJson(exchange, status, payload);
    }

    public static boolean handlePreflight(HttpExchange exchange) throws IOException {
        if (!"OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            return false;
        }
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, X-Session-Token");
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
        return true;
    }
}
