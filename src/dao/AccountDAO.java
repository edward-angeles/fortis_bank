package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import dto.AccountDTO;
import dto.AccountStatus;
import dto.AccountType;

public class AccountDAO
{
	  private AccountStatusDAO statusDAO = new AccountStatusDAO();
	    private AccountTypeDAO typeDAO    = new AccountTypeDAO();

	    // CREATE � inserts into base accounts table, returns generated id
	    public AccountDTO insert(AccountDTO account) throws SQLException {
	        try (Connection conn = DBConnection.getConnection()) {
	            return insert(account, conn); // Calls the version below
	        }
	    }
	    //ADDED THE CONNECTION PARAMETER -- UDPDATE
	    public AccountDTO insert(AccountDTO account, Connection conn) throws SQLException {
	        String sql = "INSERT INTO accounts (customer_id, status_id, type_id, balance, opening_date) "
	                   + "VALUES (?, ?, ?, ?, ?)";
	        try (PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id"})) {
	            ps.setInt(1,    Integer.parseInt(account.getCustomer_id()));
	            ps.setInt(2,    account.getStatus().getId());
	            ps.setInt(3,    account.getType().getId());
	            ps.setDouble(4, Double.parseDouble(account.getBalance()));
	            ps.setDate(5,   Date.valueOf(account.getOpening_date()));
	            ps.executeUpdate();

	            // Retrieve and return the Oracle-generated ID
	            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    int generatedId = generatedKeys.getInt(1);
	                    account.setId(String.valueOf(generatedId));
	                    return account;
	                }
	            }
	        }
	        throw new SQLException("Creating account failed, no ID obtained.");	    
	      }

	    // READ - by ID
	    // Returns null if not found. Cast to concrete subtype after fetching.
	    public AccountDTO getById(int id) throws SQLException {
	        String sql = "SELECT a.id, a.customer_id, a.status_id, a.type_id, a.balance, a.opening_date "
	                   + "FROM accounts a WHERE a.id = ?";
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

	    // READ - all accounts
	    public List<AccountDTO> getAll() throws SQLException {
	        String sql = "SELECT * FROM accounts";
	        List<AccountDTO> list = new ArrayList<>();
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql);
	             ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(mapRow(rs));
	            }
	        }
	        return list;
	    }

	    // READ - all accounts by customer
	    public List<AccountDTO> getAccountsByCustomerId(int customerId) throws SQLException {
	        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
	        List<AccountDTO> list = new ArrayList<>();
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setInt(1, customerId);
	            try (ResultSet rs = ps.executeQuery()) {
	                while (rs.next()) {
	                    list.add(mapRow(rs));
	                }
	            }
	        }
	        return list;
	    }

	    // UPDATE � updates base account fields only
	    public void update(AccountDTO account) throws SQLException {
	        String sql = "UPDATE accounts SET status_id = ?, balance = ? WHERE id = ?";
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setInt(1,    account.getStatus().getId());
	            ps.setDouble(2, Double.parseDouble(account.getBalance()));
	            ps.setInt(3,    Integer.parseInt(account.getId()));
	            ps.executeUpdate();
	        }
	    }
	    
	    public void update(AccountDTO account, Connection conn) throws SQLException {
	        String sql = "UPDATE accounts SET status_id = ?, balance = ? WHERE id = ?";
	        try (PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setInt(1,    account.getStatus().getId());
	            ps.setDouble(2, Double.parseDouble(account.getBalance()));
	            ps.setInt(3,    Integer.parseInt(account.getId()));
	            ps.executeUpdate();
	        }
	    }

	    // DELETE
	    public void delete(int id) throws SQLException {
	        String sql = "DELETE FROM accounts WHERE id = ?";
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setInt(1, id);
	            ps.executeUpdate();
	        }
	    }

	    // Helper � maps a ResultSet row to a concrete anonymous AccountDTO
	    // In practice, your concrete DAOs (CheckingDAO etc.) will override this
	    // with their own mapRow that returns the correct subtype.
	    private AccountDTO mapRow(ResultSet rs) throws SQLException {
	        AccountStatus status = statusDAO.getById(rs.getInt("status_id"));
	        AccountType   type   = typeDAO.getById(rs.getInt("type_id"));
	        LocalDate openingDate = rs.getDate("opening_date").toLocalDate();

	        // Anonymous concrete instance just for base-level reads
	        AccountDTO dto = new AccountDTO(String.valueOf(rs.getInt("id")),
	            String.valueOf(rs.getInt("customer_id")),
	            status,
	            type,
	            String.valueOf(rs.getDouble("balance"))
	        ) {};
	        dto.setOpening_date(openingDate);
	        return dto;
	    }
	    
	    public void closeAccount(int id) throws SQLException {
	    	String sql = "UPDATE accounts SET status_id = 3 WHERE id = ?";
	    	try (Connection conn = DBConnection.getConnection();
	    		PreparedStatement ps = conn.prepareStatement(sql)) {
	    		ps.setInt(1, id);
	    		ps.executeUpdate();
	    	}
	    }
    
}