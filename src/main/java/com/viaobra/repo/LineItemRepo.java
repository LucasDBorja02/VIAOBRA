package com.viaobra.repo;

import com.viaobra.db.DB;
import com.viaobra.model.LineItem;
import com.viaobra.model.LineType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LineItemRepo {

    public void insert(LineItem it) {
        String sql = """
          INSERT INTO line_items(project_id,kind,category,name,unit_cost,quantity,recurrence,month_start,month_end)
          VALUES(?,?,?,?,?,?,?,?,?)
        """;
        try (Connection c = DB.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, it.projectId);
            ps.setString(2, it.kind.name());
            ps.setString(3, it.category);
            ps.setString(4, it.name);
            ps.setDouble(5, it.unitCost);
            ps.setDouble(6, it.quantity);
            ps.setString(7, it.recurrence);
            ps.setInt(8, it.monthStart);
            ps.setInt(9, it.monthEnd);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir item: " + e.getMessage(), e);
        }
    }

    public void deleteById(long id) {
        try (Connection c = DB.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM line_items WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover item: " + e.getMessage(), e);
        }
    }

    public List<LineItem> listByProject(long projectId) {
        String sql = "SELECT * FROM line_items WHERE project_id=? ORDER BY id DESC";
        try (Connection c = DB.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                List<LineItem> out = new ArrayList<>();
                while (rs.next()) {
                    LineItem it = new LineItem();
                    it.id = rs.getLong("id");
                    it.projectId = rs.getLong("project_id");
                    it.kind = LineType.valueOf(rs.getString("kind"));
                    it.category = rs.getString("category");
                    it.name = rs.getString("name");
                    it.unitCost = rs.getDouble("unit_cost");
                    it.quantity = rs.getDouble("quantity");
                    it.recurrence = rs.getString("recurrence");
                    it.monthStart = rs.getInt("month_start");
                    it.monthEnd = rs.getInt("month_end");
                    out.add(it);
                }
                return out;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar itens: " + e.getMessage(), e);
        }
    }
}
