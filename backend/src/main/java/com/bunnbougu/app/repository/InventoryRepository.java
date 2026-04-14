package com.bunnbougu.app.repository;

import com.bunnbougu.app.model.Inventory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryRepository {
    public List<Inventory> findAll() throws SQLException {
        List<Inventory> list = new ArrayList<>();
        String sql = "SELECT id, product_id, quantity, location FROM inventories ORDER BY id";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                list.add(toInventory(rs));
            }
        }
        return list;
    }

    public Inventory create(Inventory inventory) throws SQLException {
        String sql = "INSERT INTO inventories(product_id, quantity, location) VALUES(?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, inventory.getProductId());
            statement.setInt(2, inventory.getQuantity());
            statement.setString(3, inventory.getLocation());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    inventory.setId(keys.getInt(1));
                }
            }
        }
        return inventory;
    }

    public Inventory update(int id, Inventory inventory) throws SQLException {
        String sql = "UPDATE inventories SET product_id = ?, quantity = ?, location = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, inventory.getProductId());
            statement.setInt(2, inventory.getQuantity());
            statement.setString(3, inventory.getLocation());
            statement.setInt(4, id);
            if (statement.executeUpdate() == 0) {
                return null;
            }
        }
        inventory.setId(id);
        return inventory;
    }

    public Inventory adjust(int id, int delta) throws SQLException {
        String sql = "UPDATE inventories SET quantity = quantity + ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, delta);
            statement.setInt(2, id);
            if (statement.executeUpdate() == 0) {
                return null;
            }
        }
        return findById(id);
    }

    public Inventory findById(int id) throws SQLException {
        String sql = "SELECT id, product_id, quantity, location FROM inventories WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? toInventory(rs) : null;
            }
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM inventories WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private Inventory toInventory(ResultSet rs) throws SQLException {
        Inventory inventory = new Inventory();
        inventory.setId(rs.getInt("id"));
        inventory.setProductId(rs.getInt("product_id"));
        inventory.setQuantity(rs.getInt("quantity"));
        inventory.setLocation(rs.getString("location"));
        return inventory;
    }
}
