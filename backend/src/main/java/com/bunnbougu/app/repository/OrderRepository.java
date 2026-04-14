package com.bunnbougu.app.repository;

import com.bunnbougu.app.model.OrderRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    public List<OrderRecord> findAll() throws SQLException {
        List<OrderRecord> list = new ArrayList<>();
        String sql = "SELECT id, product_id, quantity, customer_name, order_date, status FROM orders ORDER BY id DESC";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                list.add(toOrder(rs));
            }
        }
        return list;
    }

    public OrderRecord create(OrderRecord order) throws SQLException {
        String sql = "INSERT INTO orders(product_id, quantity, customer_name, order_date, status) VALUES(?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, order.getProductId());
            statement.setInt(2, order.getQuantity());
            statement.setString(3, order.getCustomerName());
            statement.setString(4, order.getOrderDate());
            statement.setString(5, order.getStatus());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    order.setId(keys.getInt(1));
                }
            }
        }
        return order;
    }

    public OrderRecord update(int id, OrderRecord order) throws SQLException {
        String sql = "UPDATE orders SET product_id = ?, quantity = ?, customer_name = ?, order_date = ?, status = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, order.getProductId());
            statement.setInt(2, order.getQuantity());
            statement.setString(3, order.getCustomerName());
            statement.setString(4, order.getOrderDate());
            statement.setString(5, order.getStatus());
            statement.setInt(6, id);
            if (statement.executeUpdate() == 0) {
                return null;
            }
        }
        order.setId(id);
        return order;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private OrderRecord toOrder(ResultSet rs) throws SQLException {
        OrderRecord order = new OrderRecord();
        order.setId(rs.getInt("id"));
        order.setProductId(rs.getInt("product_id"));
        order.setQuantity(rs.getInt("quantity"));
        order.setCustomerName(rs.getString("customer_name"));
        order.setOrderDate(rs.getString("order_date"));
        order.setStatus(rs.getString("status"));
        return order;
    }
}
