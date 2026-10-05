package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.AlertType;

public class AlertTypeDAO
{
	// CREATE
    public void insert(AlertType type) throws SQLException {
        String sql = "INSERT INTO alert_types (name, description) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.getName());
            ps.setString(2, type.getDescription());
            ps.executeUpdate();
        }
    }
    
    // getById
    public AlertType getById(int id) throws SQLException {
        String sql = "SELECT * FROM alert_types WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    // getAll
    public List<AlertType> getAll() throws SQLException {
        String sql = "SELECT * FROM alert_types";
        List<AlertType> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }
    
    // UPDATE
    public void update(AlertType type) throws SQLException {
        String sql = "UPDATE alert_types SET name = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.getName());
            ps.setString(2, type.getDescription());
            ps.setInt(3, type.getId());
            ps.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM alert_types WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    private AlertType mapRow(ResultSet rs) throws SQLException {
        return new AlertType(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description")
        );
    }
}