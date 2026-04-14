package com.bunnbougu.app.controller;

import com.bunnbougu.app.model.Shipment;
import com.bunnbougu.app.service.SessionService;
import com.bunnbougu.app.service.ShipmentService;
import com.bunnbougu.app.util.ApiResponse;
import com.bunnbougu.app.util.RequestUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;

public class ShipmentController extends BaseController implements HttpHandler {
    private final ShipmentService service = new ShipmentService();

    public ShipmentController(SessionService sessionService) {
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
                    Shipment shipment = RequestUtil.readJsonBody(exchange, Shipment.class);
                    ApiResponse.sendJson(exchange, 201, service.create(shipment));
                } else {
                    ApiResponse.sendMessage(exchange, 405, "未対応のメソッドです。");
                }
                return;
            }

            int id = Integer.parseInt(parts[3]);
            if ("PUT".equals(method)) {
                Shipment shipment = RequestUtil.readJsonBody(exchange, Shipment.class);
                Shipment updated = service.update(id, shipment);
                if (updated == null) {
                    ApiResponse.sendMessage(exchange, 404, "出荷が見つかりません。");
                    return;
                }
                ApiResponse.sendJson(exchange, 200, updated);
            } else if ("DELETE".equals(method)) {
                boolean deleted = service.delete(id);
                if (!deleted) {
                    ApiResponse.sendMessage(exchange, 404, "出荷が見つかりません。");
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
}
