package com.bunnbougu.app;

import com.bunnbougu.app.controller.*;
import com.bunnbougu.app.service.SessionService;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws Exception {
        SessionService sessionService = new SessionService();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/session/login", new SessionController(sessionService));
        server.createContext("/api/products", new ProductController(sessionService));
        server.createContext("/api/inventories", new InventoryController(sessionService));
        server.createContext("/api/orders", new OrderController(sessionService));
        server.createContext("/api/shipments", new ShipmentController(sessionService));

        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
        System.out.println("Backend started: http://localhost:8080");
    }
}
