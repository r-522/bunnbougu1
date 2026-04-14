package com.bunnbougu.app.controller;

import com.bunnbougu.app.model.SessionInfo;
import com.bunnbougu.app.service.SessionService;
import com.bunnbougu.app.util.ApiResponse;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public abstract class BaseController {
    protected final SessionService sessionService;

    protected BaseController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    protected SessionInfo requireSession(HttpExchange exchange) throws IOException {
        String token = exchange.getRequestHeaders().getFirst("X-Session-Token");
        if (token == null || token.isBlank()) {
            ApiResponse.sendMessage(exchange, 401, "X-Session-Token が必要です。");
            return null;
        }
        SessionInfo info = sessionService.validate(token);
        if (info == null) {
            ApiResponse.sendMessage(exchange, 401, "セッションが無効です。再ログインしてください。");
            return null;
        }
        return info;
    }
}
