package com.bunnbougu.app.service;

import com.bunnbougu.app.model.Inventory;
import com.bunnbougu.app.repository.InventoryRepository;

import java.sql.SQLException;
import java.util.List;

public class InventoryService {
    private final InventoryRepository repository = new InventoryRepository();

    public List<Inventory> list() throws SQLException {
        return repository.findAll();
    }

    public Inventory create(Inventory inventory) throws SQLException {
        return repository.create(inventory);
    }

    public Inventory update(int id, Inventory inventory) throws SQLException {
        return repository.update(id, inventory);
    }

    public Inventory adjust(int id, int delta) throws SQLException {
        return repository.adjust(id, delta);
    }

    public boolean delete(int id) throws SQLException {
        return repository.delete(id);
    }
}
