package com.bunnbougu.app.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static final String DB_URL = "jdbc:sqlite:backend/data/app.db";

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
