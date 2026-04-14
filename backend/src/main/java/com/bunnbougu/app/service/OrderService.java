package com.bunnbougu.app.service;

import com.bunnbougu.app.model.OrderRecord;
import com.bunnbougu.app.repository.OrderRepository;

import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private final OrderRepository repository = new OrderRepository();

    public List<OrderRecord> list() throws SQLException {
        return repository.findAll();
    }

    public OrderRecord create(OrderRecord order) throws SQLException {
        return repository.create(order);
    }

    public OrderRecord update(int id, OrderRecord order) throws SQLException {
        return repository.update(id, order);
    }

    public boolean delete(int id) throws SQLException {
        return repository.delete(id);
    }
}
