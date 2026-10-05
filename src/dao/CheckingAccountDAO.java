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
import dto.CheckingAccountDTO;

public class CheckingAccountDAO
{
    	private AccountDAO accountDAO         = new AccountDAO();
	    private AccountStatusDAO statusDAO    = new AccountStatusDAO();
	    private AccountTypeDAO typeDAO        = new AccountTypeDAO();

	    // CREATE — inserts into accounts + checking_accounts
	    public CheckingAccountDTO insert(CheckingAccountDTO dto) throws SQLException {
	        String sql = "INSERT INTO checking_accounts (account_id, free_transactions_counter, transaction_fee, last_reset_date) "
	                   + "VALUES (?, ?, ?, ?)";
	        try (Connection conn = DBConnection.getConnection()) {
	            // Turn off auto-commit to treat both inserts as one "unit of work"
	            conn.setAutoCommit(false); 
	            
	            try {
	                // 1. Insert into base accounts table using the SHARED connection
	            	
	                accountDAO.insert(dto, conn);
	                int generatedId = Integer.parseInt(dto.getId());

	                // 2. Insert into checking_accounts table
	                try (PreparedStatement ps = conn.prepareStatement(sql)) {
	                    ps.setInt(1,    generatedId);
	                    ps.setInt(2,    Integer.parseInt(dto.getFree_transactions_counter()));
	                    ps.setDouble(3, Double.parseDouble(dto.getTransaction_fee()));
	                    ps.setDate(4,   Date.valueOf(dto.getLast_reset_date()));
	                    ps.executeUpdate();
	                }

	                // 3. Commit everything at once
	                conn.commit();
	                System.out.println("Checking Account successfully persisted with ID: " + generatedId);
	                return dto;
	                
	            } catch (SQLException e) {
	            	System.err.println("DAO ERROR: " + e.getMessage()); 
	                conn.rollback(); // Undo everything if any part fails
	                throw e;
	            } finally {
	                conn.setAutoCommit(true);
	            }
	        }
	    }

    	    // READ - by ID (joins accounts + checking_accounts)
    	    public CheckingAccountDTO getById(int id) throws SQLException {
    	        String sql = "SELECT a.id, a.customer_id, a.status_id, a.type_id, a.balance, a.opening_date, "
    	                   + "c.free_transactions_counter, c.transaction_fee, c.last_reset_date "
    	                   + "FROM accounts a JOIN checking_accounts c ON a.id = c.account_id "
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
    	    public List<CheckingAccountDTO> getAll() throws SQLException {
    	        String sql = "SELECT a.id, a.customer_id, a.status_id, a.type_id, a.balance, a.opening_date, "
    	                   + "c.free_transactions_counter, c.transaction_fee, c.last_reset_date "
    	                   + "FROM accounts a JOIN checking_accounts c ON a.id = c.account_id";
    	        List<CheckingAccountDTO> list = new ArrayList<>();
    	        try (Connection conn = DBConnection.getConnection();
    	             PreparedStatement ps = conn.prepareStatement(sql);
    	             ResultSet rs = ps.executeQuery()) {
    	            while (rs.next()) list.add(mapRow(rs));
    	        }
    	        return list;
    	    }

    	    // UPDATE — persists balance + checking-specific fields
    	    public void update(CheckingAccountDTO dto) throws SQLException {
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
    	    void update(CheckingAccountDTO dto, Connection conn) throws SQLException {
    	        accountDAO.update(dto, conn);
    	        String sql = "UPDATE checking_accounts SET free_transactions_counter = ?, transaction_fee = ?, last_reset_date = ? "
    	                   + "WHERE account_id = ?";
    	        try (PreparedStatement ps = conn.prepareStatement(sql)) {
    	            ps.setInt(1,    Integer.parseInt(dto.getFree_transactions_counter()));
    	            ps.setDouble(2, Double.parseDouble(dto.getTransaction_fee()));
    	            ps.setDate(3,   Date.valueOf(dto.getLast_reset_date()));
    	            ps.setInt(4,    Integer.parseInt(dto.getId()));
    	            ps.executeUpdate();
    	        }
    	    }

    	    // DELETE — CASCADE handles checking_accounts row
    	    public void delete(int id) throws SQLException {
    	        accountDAO.delete(id);
    	    }

    	    // RESET monthly transactions counter + last_reset_date to start of current month
    	    public void resetMonthlyTransactions(int accountId) throws SQLException {
    	        String sql = "UPDATE checking_accounts "
    	                   + "SET free_transactions_counter = 3, last_reset_date = TRUNC(SYSDATE, 'MM') "
    	                   + "WHERE account_id = ?";
    	        try (Connection conn = DBConnection.getConnection();
    	             PreparedStatement ps = conn.prepareStatement(sql)) {
    	            ps.setInt(1, accountId);
    	            ps.executeUpdate();
    	        }
    	    }

    	    // APPLY TRANSACTION FEE — persists balance and counter after Service computed them
    	    public void applyTransactionFee(CheckingAccountDTO dto) throws SQLException {
    	        String sql = "UPDATE checking_accounts SET free_transactions_counter = ? WHERE account_id = ?";
    	        
    	        try (Connection conn = DBConnection.getConnection()) {
    	            conn.setAutoCommit(false);
    	            try {
    	                // 1. Update balance using shared connection
    	                accountDAO.update(dto, conn); 

    	                // 2. Update counter
    	                try (PreparedStatement ps = conn.prepareStatement(sql)) {
    	                    ps.setInt(1, Integer.parseInt(dto.getFree_transactions_counter()));
    	                    ps.setInt(2, Integer.parseInt(dto.getId()));
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
    	    private CheckingAccountDTO mapRow(ResultSet rs) throws SQLException {
    	        AccountStatus status = statusDAO.getById(rs.getInt("status_id"));
    	        AccountType   type   = typeDAO.getById(rs.getInt("type_id"));
    	        CheckingAccountDTO dto = new CheckingAccountDTO(
    	            String.valueOf(rs.getInt("id")),
    	            String.valueOf(rs.getInt("customer_id")),
    	            status, type,
    	            String.valueOf(rs.getDouble("balance")),
    	            String.valueOf(rs.getDouble("transaction_fee")),
    	            rs.getDate("last_reset_date").toLocalDate()
    	        );
    	        dto.setFree_transactions_counter(String.valueOf(rs.getInt("free_transactions_counter")));
    	        dto.setOpening_date(rs.getDate("opening_date").toLocalDate());
    	        return dto;
    	    }
    
}
