package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.AccountStatus;
import dto.AccountType;
import dto.SavingsAccountDTO;

public class SavingsAccountDAO
{
	 private AccountDAO accountDAO      = new AccountDAO();
	    private AccountStatusDAO statusDAO = new AccountStatusDAO();
	    private AccountTypeDAO typeDAO     = new AccountTypeDAO();

	    // CREATE
	    public SavingsAccountDTO insert(SavingsAccountDTO dto) throws SQLException {
	        String sql = "INSERT INTO saving_accounts (account_id, annual_interest_rate, last_interest_date) VALUES (?, ?, ?)";

	        try (Connection conn = DBConnection.getConnection()) {
	            conn.setAutoCommit(false);
	            try {
	                accountDAO.insert(dto, conn);
	                int generatedId = Integer.parseInt(dto.getId());

	                try (PreparedStatement ps = conn.prepareStatement(sql)) {
	                    ps.setInt(1,    generatedId);
	                    ps.setDouble(2, Double.parseDouble(dto.getAnnual_interest_rate()));
	                    ps.setDate(3,   Date.valueOf(dto.getLast_interest_date()));
	                    ps.executeUpdate();
	                }
	                conn.commit();
	                return dto;
	            } catch (SQLException e) {
	                conn.rollback();
	                throw e;
	            } finally {
	                conn.setAutoCommit(true);
	            }
	        }
	    }

	    // READ - by ID
	    public SavingsAccountDTO getById(int id) throws SQLException {
	        String sql = "SELECT a.id, a.customer_id, a.status_id, a.type_id, a.balance, a.opening_date, "
	                   + "s.annual_interest_rate, s.last_interest_date "
	                   + "FROM accounts a JOIN saving_accounts s ON a.id = s.account_id "
	                   + "WHERE a.id = ?";
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setInt(1, id);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) return mapRow(rs);
	            }
	        }
	        return null;
	    }

	    // READ - all
	    public List<SavingsAccountDTO> getAll() throws SQLException {
	        String sql = "SELECT a.id, a.customer_id, a.status_id, a.type_id, a.balance, a.opening_date, "
	                   + "s.annual_interest_rate, s.last_interest_date "
	                   + "FROM accounts a JOIN saving_accounts s ON a.id = s.account_id";
	        List<SavingsAccountDTO> list = new ArrayList<>();
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql);
	             ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) list.add(mapRow(rs));
	        }
	        return list;
	    }

	    // UPDATE
	    public void update(SavingsAccountDTO dto) throws SQLException {
	        try (Connection conn = DBConnection.getConnection()) {
	            conn.setAutoCommit(false);
	            try {
	                update(dto, conn);
	                conn.commit();
	            } catch (SQLException e) {
	                conn.rollback();
	                throw e;
	            } finally {
	                conn.setAutoCommit(true);
	            }
	        }
	    }

	    // Shared-connection overload
	    void update(SavingsAccountDTO dto, Connection conn) throws SQLException {
	        accountDAO.update(dto, conn);
	        String sql = "UPDATE saving_accounts SET annual_interest_rate = ?, last_interest_date = ? "
	                   + "WHERE account_id = ?";
	        try (PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setDouble(1, Double.parseDouble(dto.getAnnual_interest_rate()));
	            ps.setDate(2,   Date.valueOf(dto.getLast_interest_date()));
	            ps.setInt(3,    Integer.parseInt(dto.getId()));
	            ps.executeUpdate();
	        }
	    }
	    // DELETE
	    public void delete(int id) throws SQLException {
	        accountDAO.delete(id);
	    }

	    // APPLY ANNUAL INTEREST — persists new balance + last_interest_date after Service computed them
	    public void applyAnnualInterest(SavingsAccountDTO dto) throws SQLException {
	        try (Connection conn = DBConnection.getConnection()) {
	            conn.setAutoCommit(false);
	            try {
	                // 1. Update balance in accounts table
	                accountDAO.update(dto, conn);

	                // 2. Update last_interest_date in saving_accounts table
	                String sql = "UPDATE saving_accounts SET last_interest_date = ? WHERE account_id = ?";
	                try (PreparedStatement ps = conn.prepareStatement(sql)) {
	                    ps.setDate(1, Date.valueOf(dto.getLast_interest_date()));
	                    ps.setInt(2,  Integer.parseInt(dto.getId()));
	                    ps.executeUpdate();
	                }
	                conn.commit();
	            } catch (SQLException e) {
	                conn.rollback();
	                throw e;
	            } finally {
	                conn.setAutoCommit(true);
	            }
	        }
	    }

	    private SavingsAccountDTO mapRow(ResultSet rs) throws SQLException {
	        AccountStatus status = statusDAO.getById(rs.getInt("status_id"));
	        AccountType   type   = typeDAO.getById(rs.getInt("type_id"));
	        SavingsAccountDTO dto = new SavingsAccountDTO(
	            String.valueOf(rs.getInt("id")),
	            String.valueOf(rs.getInt("customer_id")),
	            status, type,
	            String.valueOf(rs.getDouble("balance")),
	            null,
	            String.valueOf(rs.getDouble("annual_interest_rate"))
	        );
	        dto.setLast_interest_date(rs.getDate("last_interest_date").toLocalDate());
	        dto.setOpening_date(rs.getDate("opening_date").toLocalDate());
	        return dto;
	    }
}