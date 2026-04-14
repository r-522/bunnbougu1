package com.bunnbougu.app.controller;

import com.bunnbougu.app.model.Inventory;
import com.bunnbougu.app.service.InventoryService;
import com.bunnbougu.app.service.SessionService;
import com.bunnbougu.app.util.ApiResponse;
import com.bunnbougu.app.util.RequestUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;

public class InventoryController extends BaseController implements HttpHandler {
    private final InventoryService service = new InventoryService();

    public InventoryController(SessionService sessionService) {
        super(sessionService);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (ApiResponse.handlePreflight(exchange)) {
            return;
        }
        if (requireSession(exchange) == null) {
            return;
        }

        String method = exchange.getRequestMethod().toUpperCase();
        String[] parts = RequestUtil.pathParts(exchange);

        try {
            if (parts.length == 3) {
                if ("GET".equals(method)) {
                    ApiResponse.sendJson(exchange, 200, service.list());
                } else if ("POST".equals(method)) {
                    Inventory inventory = RequestUtil.readJsonBody(exchange, Inventory.class);
                    ApiResponse.sendJson(exchange, 201, service.create(inventory));
                } else {
                    ApiResponse.sendMessage(exchange, 405, "未対応のメソッドです。");
                }
                return;
            }

            int id = Integer.parseInt(parts[3]);
            if (parts.length == 5 && "adjust".equals(parts[4]) && "POST".equals(method)) {
                AdjustRequest request = RequestUtil.readJsonBody(exchange, AdjustRequest.class);
                Inventory adjusted = service.adjust(id, request.delta);
                if (adjusted == null) {
                    ApiResponse.sendMessage(exchange, 404, "在庫が見つかりません。");
                    return;
                }
                ApiResponse.sendJson(exchange, 200, adjusted);
                return;
            }

            if ("PUT".equals(method)) {
                Inventory inventory = RequestUtil.readJsonBody(exchange, Inventory.class);
                Inventory updated = service.update(id, inventory);
                if (updated == null) {
                    ApiResponse.sendMessage(exchange, 404, "在庫が見つかりません。");
                    return;
                }
                ApiResponse.sendJson(exchange, 200, updated);
            } else if ("DELETE".equals(method)) {
                boolean deleted = service.delete(id);
                if (!deleted) {
                    ApiResponse.sendMessage(exchange, 404, "在庫が見つかりません。");
                    return;
                }
                ApiResponse.sendMessage(exchange, 200, "削除しました。");
            } else {
                ApiResponse.sendMessage(exchange, 405, "未対応のメソッドです。");
            }
        } catch (NumberFormatException e) {
            ApiResponse.sendMessage(exchange, 400, "ID は数字で指定してください。");
        } catch (SQLException e) {
            ApiResponse.sendMessage(exchange, 500, "DBエラー: " + e.getMessage());
        }
    }

    private static class AdjustRequest {
        int delta;
    }
}
