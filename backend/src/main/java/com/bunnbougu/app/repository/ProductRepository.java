package com.bunnbougu.app.repository;

import com.bunnbougu.app.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    public List<Product> findAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, code, name, category, unit_price FROM products ORDER BY id";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                products.add(toProduct(rs));
            }
        }
        return products;
    }

    public Product findById(int id) throws SQLException {
        String sql = "SELECT id, code, name, category, unit_price FROM products WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? toProduct(rs) : null;
            }
        }
    }

    public Product create(Product product) throws SQLException {
        String sql = "INSERT INTO products(code, name, category, unit_price) VALUES(?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, product.getCode());
            statement.setString(2, product.getName());
            statement.setString(3, product.getCategory());
            statement.setInt(4, product.getUnitPrice());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    product.setId(keys.getInt(1));
                }
            }
        }
        return product;
    }

    public Product update(int id, Product product) throws SQLException {
        String sql = "UPDATE products SET code = ?, name = ?, category = ?, unit_price = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getCode());
            statement.setString(2, product.getName());
            statement.setString(3, product.getCategory());
            statement.setInt(4, product.getUnitPrice());
            statement.setInt(5, id);
            int updated = statement.executeUpdate();
            if (updated == 0) {
                return null;
            }
        }
        product.setId(id);
        return product;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private Product toProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setCode(rs.getString("code"));
        product.setName(rs.getString("name"));
        product.setCategory(rs.getString("category"));
        product.setUnitPrice(rs.getInt("unit_price"));
        return product;
    }
}
