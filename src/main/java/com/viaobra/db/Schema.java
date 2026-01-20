package com.viaobra.db;

import java.sql.Connection;
import java.sql.Statement;

public final class Schema {
    private Schema() {}

    public static void init() {
        try (Connection c = DB.connect(); Statement st = c.createStatement()) {
            st.executeUpdate("""
                PRAGMA foreign_keys = ON;
            """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS projects (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  name TEXT NOT NULL,
                  type TEXT NOT NULL,
                  location TEXT,
                  months INTEGER NOT NULL,
                  discount_rate_month REAL NOT NULL,
                  tax_rate REAL NOT NULL,
                  contingency_rate REAL NOT NULL
                );
            """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS line_items (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  project_id INTEGER NOT NULL,
                  kind TEXT NOT NULL,
                  category TEXT NOT NULL,
                  name TEXT NOT NULL,
                  unit_cost REAL NOT NULL,
                  quantity REAL NOT NULL,
                  recurrence TEXT NOT NULL,
                  month_start INTEGER NOT NULL,
                  month_end INTEGER NOT NULL,
                  FOREIGN KEY(project_id) REFERENCES projects(id) ON DELETE CASCADE
                );
            """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS stages (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  project_id INTEGER NOT NULL,
                  name TEXT NOT NULL,
                  month_start INTEGER NOT NULL,
                  month_end INTEGER NOT NULL,
                  capex_percent REAL NOT NULL,
                  FOREIGN KEY(project_id) REFERENCES projects(id) ON DELETE CASCADE
                );
            """);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao iniciar banco: " + e.getMessage(), e);
        }
    }
}
