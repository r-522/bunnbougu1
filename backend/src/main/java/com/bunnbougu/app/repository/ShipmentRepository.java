package com.bunnbougu.app.repository;

import com.bunnbougu.app.model.Shipment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShipmentRepository {
    public List<Shipment> findAll() throws SQLException {
        List<Shipment> list = new ArrayList<>();
        String sql = "SELECT id, order_id, product_id, shipped_quantity, shipped_date, carrier FROM shipments ORDER BY id DESC";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                list.add(toShipment(rs));
            }
        }
        return list;
    }

    public Shipment create(Shipment shipment) throws SQLException {
        String sql = "INSERT INTO shipments(order_id, product_id, shipped_quantity, shipped_date, carrier) VALUES(?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, shipment.getOrderId());
            statement.setInt(2, shipment.getProductId());
            statement.setInt(3, shipment.getShippedQuantity());
            statement.setString(4, shipment.getShippedDate());
            statement.setString(5, shipment.getCarrier());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    shipment.setId(keys.getInt(1));
                }
            }
        }
        return shipment;
    }

    public Shipment update(int id, Shipment shipment) throws SQLException {
        String sql = "UPDATE shipments SET order_id = ?, product_id = ?, shipped_quantity = ?, shipped_date = ?, carrier = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, shipment.getOrderId());
            statement.setInt(2, shipment.getProductId());
            statement.setInt(3, shipment.getShippedQuantity());
            statement.setString(4, shipment.getShippedDate());
            statement.setString(5, shipment.getCarrier());
            statement.setInt(6, id);
            if (statement.executeUpdate() == 0) {
                return null;
            }
        }
        shipment.setId(id);
        return shipment;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM shipments WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private Shipment toShipment(ResultSet rs) throws SQLException {
        Shipment shipment = new Shipment();
        shipment.setId(rs.getInt("id"));
        shipment.setOrderId(rs.getInt("order_id"));
        shipment.setProductId(rs.getInt("product_id"));
        shipment.setShippedQuantity(rs.getInt("shipped_quantity"));
        shipment.setShippedDate(rs.getString("shipped_date"));
        shipment.setCarrier(rs.getString("carrier"));
        return shipment;
    }
}
