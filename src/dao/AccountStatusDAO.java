package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.AccountStatus;

public class AccountStatusDAO {
	
	   // CREATE
    public void insert(AccountStatus status) throws SQLException {
        String sql = "INSERT INTO account_statuses (name, description) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.getName());
            ps.setString(2, status.getDescription());
            ps.executeUpdate();
        }
    }

    // READ - by ID
    public AccountStatus getById(int id) throws SQLException {
        String sql = "SELECT id, name, description FROM account_statuses WHERE id = ?";
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
    
    // READ - all
    public List<AccountStatus> getAll() throws SQLException {
        String sql = "SELECT id, name, description FROM account_statuses";
        List<AccountStatus> list = new ArrayList<>();
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
    public void update(AccountStatus status) throws SQLException {
        String sql = "UPDATE account_statuses SET name = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.getName());
            ps.setString(2, status.getDescription());
            ps.setInt(3, status.getId());
            ps.executeUpdate();
        }
    }

    // DELETE
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM account_statuses WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Helper - maps a ResultSet row to an AccountStatus object
    private AccountStatus mapRow(ResultSet rs) throws SQLException {
        return new AccountStatus(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description")
        );
    }
	
}
