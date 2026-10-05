package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.AccountStatus;
import dto.AccountType;
import dto.CurrencyAccountDTO;

public class CurrencyAccountDAO
{
	 private AccountDAO accountDAO      = new AccountDAO();
	    private AccountStatusDAO statusDAO = new AccountStatusDAO();
	    private AccountTypeDAO typeDAO     = new AccountTypeDAO();

	    // CREATE
	    public CurrencyAccountDTO insert(CurrencyAccountDTO dto) throws SQLException {
	        String sql = "INSERT INTO currency_accounts (account_id, account_currency, exchange_rate, balance_foreign_currency) "
	                   + "VALUES (?, ?, ?, ?)";

	        try (Connection conn = DBConnection.getConnection()) {
	            conn.setAutoCommit(false);
	            try {
	                accountDAO.insert(dto, conn);
	                int generatedId = Integer.parseInt(dto.getId());

	                try (PreparedStatement ps = conn.prepareStatement(sql)) {
	                    ps.setInt(1,    generatedId);
	                    ps.setString(2, dto.getAccount_currency());
	                    ps.setDouble(3, Double.parseDouble(dto.getExchange_rate()));
	                    ps.setDouble(4, Double.parseDouble(dto.getBalance_foreign_currency()));
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
	    public CurrencyAccountDTO getById(int id) throws SQLException {
	        String sql = "SELECT a.id, a.customer_id, a.status_id, a.type_id, a.balance, a.opening_date, "
	                   + "cu.account_currency, cu.exchange_rate, cu.balance_foreign_currency "
	                   + "FROM accounts a JOIN currency_accounts cu ON a.id = cu.account_id "
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
	    public List<CurrencyAccountDTO> getAll() throws SQLException {
	        String sql = "SELECT a.id, a.customer_id, a.status_id, a.type_id, a.balance, a.opening_date, "
	                   + "cu.account_currency, cu.exchange_rate, cu.balance_foreign_currency "
	                   + "FROM accounts a JOIN currency_accounts cu ON a.id = cu.account_id";
	        List<CurrencyAccountDTO> list = new ArrayList<>();
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql);
	             ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) list.add(mapRow(rs));
	        }
	        return list;
	    }

	    // UPDATE
	    public void update(CurrencyAccountDTO dto) throws SQLException {
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
	    void update(CurrencyAccountDTO dto, Connection conn) throws SQLException {
	        accountDAO.update(dto, conn);
	        String sql = "UPDATE currency_accounts SET account_currency = ?, exchange_rate = ?, balance_foreign_currency = ? "
	                   + "WHERE account_id = ?";
	        try (PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setString(1, dto.getAccount_currency());
	            ps.setDouble(2, Double.parseDouble(dto.getExchange_rate()));
	            ps.setDouble(3, Double.parseDouble(dto.getBalance_foreign_currency()));
	            ps.setInt(4,    Integer.parseInt(dto.getId()));
	            ps.executeUpdate();
	        }
	    }

	    // DELETE
	    public void delete(int id) throws SQLException {
	        accountDAO.delete(id);
	    }

	    // CONVERT TO LOCAL — persists new local balance after Service computed it
	    public void convertToLocal(CurrencyAccountDTO dto) throws SQLException {
	        try (Connection conn = DBConnection.getConnection()) {
	            conn.setAutoCommit(false);
	            try {
	                accountDAO.update(dto, conn);
	                conn.commit();
	            } catch (SQLException e) {
	                conn.rollback();
	                throw e;
	            } finally {
	                conn.setAutoCommit(true);
	            }
	        }
	    }

	    // CONVERT TO FOREIGN — persists new balance_foreign_currency after Service computed it
	    public void convertToForeign(CurrencyAccountDTO dto) throws SQLException {
	        String sql = "UPDATE currency_accounts SET balance_foreign_currency = ? WHERE account_id = ?";
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setDouble(1, Double.parseDouble(dto.getBalance_foreign_currency()));
	            ps.setInt(2,    Integer.parseInt(dto.getId()));
	            ps.executeUpdate();
	        }
	    }

	    private CurrencyAccountDTO mapRow(ResultSet rs) throws SQLException {
	        AccountStatus status = statusDAO.getById(rs.getInt("status_id"));
	        AccountType   type   = typeDAO.getById(rs.getInt("type_id"));
	        CurrencyAccountDTO dto = new CurrencyAccountDTO(
	            String.valueOf(rs.getInt("id")),
	            String.valueOf(rs.getInt("customer_id")),
	            status, type,
	            String.valueOf(rs.getDouble("balance")),
	            null,
	            rs.getString("account_currency"),
	            String.valueOf(rs.getDouble("exchange_rate"))
	        );
	        dto.setBalance_foreign_currency(String.valueOf(rs.getDouble("balance_foreign_currency")));
	        dto.setOpening_date(rs.getDate("opening_date").toLocalDate());
	        return dto;
	    }
}