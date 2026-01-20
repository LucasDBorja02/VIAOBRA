package com.viaobra.repo;

import com.viaobra.db.DB;
import com.viaobra.model.Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectRepo {

    public long insert(Project p) {
        String sql = """
          INSERT INTO projects(name,type,location,months,discount_rate_month,tax_rate,contingency_rate)
          VALUES(?,?,?,?,?,?,?)
        """;
        try (Connection c = DB.connect();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.name);
            ps.setString(2, p.type);
            ps.setString(3, p.location);
            ps.setInt(4, p.months);
            ps.setDouble(5, p.discountRateMonth);
            ps.setDouble(6, p.taxRate);
            ps.setDouble(7, p.contingencyRate);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir projeto: " + e.getMessage(), e);
        }
    }

    public List<Project> list() {
        try (Connection c = DB.connect();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM projects ORDER BY id DESC")) {

            List<Project> out = new ArrayList<>();
            while (rs.next()) {
                Project p = new Project();
                p.id = rs.getLong("id");
                p.name = rs.getString("name");
                p.type = rs.getString("type");
                p.location = rs.getString("location");
                p.months = rs.getInt("months");
                p.discountRateMonth = rs.getDouble("discount_rate_month");
                p.taxRate = rs.getDouble("tax_rate");
                p.contingencyRate = rs.getDouble("contingency_rate");
                out.add(p);
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar projetos: " + e.getMessage(), e);
        }
    }
}
