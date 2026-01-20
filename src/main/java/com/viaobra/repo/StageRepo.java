package com.viaobra.repo;

import com.viaobra.db.DB;
import com.viaobra.model.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StageRepo {

    public void insert(Stage s) {
        String sql = """
          INSERT INTO stages(project_id,name,month_start,month_end,capex_percent)
          VALUES(?,?,?,?,?)
        """;
        try (Connection c = DB.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, s.projectId);
            ps.setString(2, s.name);
            ps.setInt(3, s.monthStart);
            ps.setInt(4, s.monthEnd);
            ps.setDouble(5, s.capexPercent);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir etapa: " + e.getMessage(), e);
        }
    }

    public void deleteById(long id) {
        try (Connection c = DB.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM stages WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover etapa: " + e.getMessage(), e);
        }
    }

    public List<Stage> listByProject(long projectId) {
        String sql = "SELECT * FROM stages WHERE project_id=? ORDER BY id DESC";
        try (Connection c = DB.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                List<Stage> out = new ArrayList<>();
                while (rs.next()) {
                    Stage s = new Stage();
                    s.id = rs.getLong("id");
                    s.projectId = rs.getLong("project_id");
                    s.name = rs.getString("name");
                    s.monthStart = rs.getInt("month_start");
                    s.monthEnd = rs.getInt("month_end");
                    s.capexPercent = rs.getDouble("capex_percent");
                    out.add(s);
                }
                return out;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar etapas: " + e.getMessage(), e);
        }
    }

    public void deleteAllByProject(long projectId) {
        try (Connection c = DB.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM stages WHERE project_id=?")) {
            ps.setLong(1, projectId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao limpar etapas: " + e.getMessage(), e);
        }
    }
}
