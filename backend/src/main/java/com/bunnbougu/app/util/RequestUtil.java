package com.bunnbougu.app.util;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class RequestUtil {
    private RequestUtil() {
    }

    public static <T> T readJsonBody(HttpExchange exchange, Class<T> clazz) throws IOException {
        String json = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return JsonUtil.GSON.fromJson(json, clazz);
    }

    public static String[] pathParts(HttpExchange exchange) {
        return exchange.getRequestURI().getPath().split("/");
    }
}
