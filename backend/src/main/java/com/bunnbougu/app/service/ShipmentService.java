package com.bunnbougu.app.service;

import com.bunnbougu.app.model.Shipment;
import com.bunnbougu.app.repository.ShipmentRepository;

import java.sql.SQLException;
import java.util.List;

public class ShipmentService {
    private final ShipmentRepository repository = new ShipmentRepository();

    public List<Shipment> list() throws SQLException {
        return repository.findAll();
    }

    public Shipment create(Shipment shipment) throws SQLException {
        return repository.create(shipment);
    }

    public Shipment update(int id, Shipment shipment) throws SQLException {
        return repository.update(id, shipment);
    }

    public boolean delete(int id) throws SQLException {
        return repository.delete(id);
    }
}
