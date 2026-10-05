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
import dto.CreditAccountDTO;

public class CreditAccountDAO
{
	 private AccountDAO accountDAO      = new AccountDAO();
	    private AccountStatusDAO statusDAO = new AccountStatusDAO();
	    private AccountTypeDAO typeDAO     = new AccountTypeDAO();

	    // CREATE
	    public CreditAccountDTO insert(CreditAccountDTO dto) throws SQLException {
	        String sql = "INSERT INTO credit_accounts "
	                   + "(account_id, credit_limit, annual_interest_rate, min_monthly_payment, payment_due_date, last_interest_date) "
	                   + "VALUES (?, ?, ?, ?, ?, ?)";

	        try (Connection conn = DBConnection.getConnection()) {
	            conn.setAutoCommit(false);
	            try {
	                // 1. Base insert updates the DTO with the generated ID
	                accountDAO.insert(dto, conn); 
	                int generatedId = Integer.parseInt(dto.getId());

	                // 2. Specific insert for credit table
	                try (PreparedStatement ps = conn.prepareStatement(sql)) {
	                    ps.setInt(1,    generatedId);
	                    ps.setDouble(2, Double.parseDouble(dto.getCredit_limit()));
	                    ps.setDouble(3, Double.parseDouble(dto.getAnnual_interest_rate()));
	                    ps.setDouble(4, Double.parseDouble(dto.getMin_monthly_payment()));
	                    ps.setDate(5,   Date.valueOf(dto.getPayment_due_date()));
	                    ps.setDate(6,   Date.valueOf(dto.getLast_interest_date()));
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
	    public CreditAccountDTO getById(int id) throws SQLException {
	        String sql = "SELECT a.id, a.customer_id, a.status_id, a.type_id, a.balance, a.opening_date, "
	                   + "c.credit_limit, c.annual_interest_rate, c.min_monthly_payment, c.payment_due_date, c.last_interest_date "
	                   + "FROM accounts a JOIN credit_accounts c ON a.id = c.account_id "
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
	    public List<CreditAccountDTO> getAll() throws SQLException {
	        String sql = "SELECT a.id, a.customer_id, a.status_id, a.type_id, a.balance, a.opening_date, "
	                   + "c.credit_limit, c.annual_interest_rate, c.min_monthly_payment, c.payment_due_date, c.last_interest_date "
	                   + "FROM accounts a JOIN credit_accounts c ON a.id = c.account_id";
	        List<CreditAccountDTO> list = new ArrayList<>();
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql);
	             ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) list.add(mapRow(rs));
	        }
	        return list;
	    }

	    // UPDATE
	    // Public entry point — owns the connection and transaction
	    public void update(CreditAccountDTO dto) throws SQLException {
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

	    // Shared-connection overload — used by update() and insert()
	    public void update(CreditAccountDTO dto, Connection conn) throws SQLException {
	        accountDAO.update(dto, conn); // accounts table first

	        String sql = "UPDATE credit_accounts "
	                   + "SET credit_limit = ?, annual_interest_rate = ?, min_monthly_payment = ?, "
	                   + "payment_due_date = ?, last_interest_date = ? "
	                   + "WHERE account_id = ?";
	        try (PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setDouble(1, Double.parseDouble(dto.getCredit_limit()));
	            ps.setDouble(2, Double.parseDouble(dto.getAnnual_interest_rate()));
	            ps.setDouble(3, Double.parseDouble(dto.getMin_monthly_payment()));
	            ps.setDate(4,   Date.valueOf(dto.getPayment_due_date()));
	            ps.setDate(5,   Date.valueOf(dto.getLast_interest_date()));
	            ps.setInt(6,    Integer.parseInt(dto.getId()));
	            ps.executeUpdate();
	        }
	    }
	    // DELETE
	    public void delete(int id) throws SQLException {
	        accountDAO.delete(id);
	    }

	    // BORROW — persists new balance + credit_limit + last_interest_date after Service computed them
	    public void borrow(int accountId, double amount) throws SQLException {
	        String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setDouble(1, amount);
	            ps.setInt(2, accountId);
	            ps.executeUpdate();
	        }
	    }

	    // REPAY — persists new balance + restored credit_limit after Service computed them
	    public void repay(int accountId, double amount) throws SQLException {
	        String sql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setDouble(1, amount);
	            ps.setInt(2, accountId);
	            ps.executeUpdate();
	        }
	    }
	    
	 
	    public void applyMonthlyInterest(CreditAccountDTO dto) throws SQLException {
	        String sqlAccount = "UPDATE accounts SET balance = ? WHERE id = ?";
	        String sqlCredit = "UPDATE credit_accounts SET last_interest_date = ? WHERE account_id = ?";

	        try (Connection conn = DBConnection.getConnection()) {
	            conn.setAutoCommit(false); // Start transaction
	            try {
	                // 1. Update Balance in parent table
	                try (PreparedStatement ps = conn.prepareStatement(sqlAccount)) {
	                    ps.setDouble(1, Double.parseDouble(dto.getBalance()));
	                    ps.setInt(2, Integer.parseInt(dto.getId()));
	                    ps.executeUpdate();
	                }

	                // 2. Update Last Interest Date in child table
	                try (PreparedStatement ps = conn.prepareStatement(sqlCredit)) {
	                    ps.setDate(1, java.sql.Date.valueOf(dto.getLast_interest_date()));
	                    ps.setInt(2, Integer.parseInt(dto.getId()));
	                    ps.executeUpdate();
	                }

	                conn.commit(); // Save both changes
	            } catch (SQLException e) {
	                conn.rollback(); // Undo everything if any part fails
	                throw e;
	            } finally {
	                conn.setAutoCommit(true);
	            }
	        }
	    }

	    // CHECK CREDIT AVAILABLE — pure read, no DB write needed
	    // (Computed in Service from in-memory DTO, but can also query directly)
	    public double checkCreditAvailable(int accountId) throws SQLException {
	        String sql = "SELECT (c.credit_limit - a.balance) AS available "
	                   + "FROM accounts a JOIN credit_accounts c ON a.id = c.account_id "
	                   + "WHERE a.id = ?";
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setInt(1, accountId);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) return rs.getDouble("available");
	            }
	        }
	        return 0.0;
	    }

	    private CreditAccountDTO mapRow(ResultSet rs) throws SQLException {
	        AccountStatus status = statusDAO.getById(rs.getInt("status_id"));
	        AccountType   type   = typeDAO.getById(rs.getInt("type_id"));
	        CreditAccountDTO dto = new CreditAccountDTO(
	            String.valueOf(rs.getInt("id")),
	            String.valueOf(rs.getInt("customer_id")),
	            status, type,
	            String.valueOf(rs.getDouble("balance")),
	            String.valueOf(rs.getDouble("credit_limit")),
	            String.valueOf(rs.getDouble("annual_interest_rate")),
	            String.valueOf(rs.getDouble("min_monthly_payment")),
	            rs.getDate("payment_due_date").toLocalDate()
	        );
	        dto.setLast_interest_date(rs.getDate("last_interest_date").toLocalDate());
	        dto.setOpening_date(rs.getDate("opening_date").toLocalDate());
	        return dto;
	    }
}