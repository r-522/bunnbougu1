package com.bunnbougu.app.controller;

import com.bunnbougu.app.model.Product;
import com.bunnbougu.app.service.ProductService;
import com.bunnbougu.app.service.SessionService;
import com.bunnbougu.app.util.ApiResponse;
import com.bunnbougu.app.util.RequestUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;

public class ProductController extends BaseController implements HttpHandler {
    private final ProductService service = new ProductService();

    public ProductController(SessionService sessionService) {
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
                    Product product = RequestUtil.readJsonBody(exchange, Product.class);
                    ApiResponse.sendJson(exchange, 201, service.create(product));
                } else {
                    ApiResponse.sendMessage(exchange, 405, "未対応のメソッドです。");
                }
                return;
            }

            int id = Integer.parseInt(parts[3]);
            if ("GET".equals(method)) {
                Product product = service.get(id);
                if (product == null) {
                    ApiResponse.sendMessage(exchange, 404, "商品が見つかりません。");
                    return;
                }
                ApiResponse.sendJson(exchange, 200, product);
            } else if ("PUT".equals(method)) {
                Product product = RequestUtil.readJsonBody(exchange, Product.class);
                Product updated = service.update(id, product);
                if (updated == null) {
                    ApiResponse.sendMessage(exchange, 404, "商品が見つかりません。");
                    return;
                }
                ApiResponse.sendJson(exchange, 200, updated);
            } else if ("DELETE".equals(method)) {
                boolean deleted = service.delete(id);
                if (!deleted) {
                    ApiResponse.sendMessage(exchange, 404, "商品が見つかりません。");
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
