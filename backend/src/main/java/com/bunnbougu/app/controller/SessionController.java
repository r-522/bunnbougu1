package com.bunnbougu.app.controller;

import com.bunnbougu.app.model.SessionInfo;
import com.bunnbougu.app.service.SessionService;
import com.bunnbougu.app.util.ApiResponse;
import com.bunnbougu.app.util.RequestUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class SessionController implements HttpHandler {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (ApiResponse.handlePreflight(exchange)) {
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            ApiResponse.sendMessage(exchange, 405, "POST のみ利用できます。");
            return;
        }

        LoginRequest request = RequestUtil.readJsonBody(exchange, LoginRequest.class);
        if (request == null || request.staffCode == null || request.staffCode.isBlank()) {
            ApiResponse.sendMessage(exchange, 400, "担当者コードは必須です。");
            return;
        }

        SessionInfo sessionInfo = sessionService.login(request.staffCode.trim(), request.displayName);
        ApiResponse.sendJson(exchange, 200, sessionInfo);
    }

    private static class LoginRequest {
        String staffCode;
        String displayName;
    }
}
