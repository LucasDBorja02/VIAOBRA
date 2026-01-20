package com.viaobra.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DB {
    private static final String URL = "jdbc:sqlite:viaobra.db";

    private DB() {}

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
