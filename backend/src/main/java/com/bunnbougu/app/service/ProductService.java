package com.bunnbougu.app.service;

import com.bunnbougu.app.model.Product;
import com.bunnbougu.app.repository.ProductRepository;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductRepository repository = new ProductRepository();

    public List<Product> list() throws SQLException {
        return repository.findAll();
    }

    public Product get(int id) throws SQLException {
        return repository.findById(id);
    }

    public Product create(Product product) throws SQLException {
        return repository.create(product);
    }

    public Product update(int id, Product product) throws SQLException {
        return repository.update(id, product);
    }

    public boolean delete(int id) throws SQLException {
        return repository.delete(id);
    }
}
