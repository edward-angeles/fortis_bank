package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.AccountStatus;
import dto.AccountType;
import dto.LineOfCreditDTO;

public class LineOfCreditDAO
{
	private AccountDAO accountDAO      = new AccountDAO();
    private AccountStatusDAO statusDAO = new AccountStatusDAO();
    private AccountTypeDAO typeDAO     = new AccountTypeDAO();

    // CREATE
    public LineOfCreditDTO insert(LineOfCreditDTO dto) throws SQLException {
        String sql = "INSERT INTO lines_of_credit (account_id, credit_limit, annual_interest_rate, number_of_installments) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                accountDAO.insert(dto, conn);
                int generatedId = Integer.parseInt(dto.getId());

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1,    generatedId);
                    ps.setDouble(2, Double.parseDouble(dto.getCredit_limit()));
                    ps.setDouble(3, Double.parseDouble(dto.getAnnual_interest_rate()));
                    ps.setInt(4,    Integer.parseInt(dto.getNumber_of_installments()));
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
    public LineOfCreditDTO getById(int id) throws SQLException {
        String sql = "SELECT a.id, a.customer_id, a.status_id, a.type_id, a.balance, a.opening_date, "
                   + "l.credit_limit, l.annual_interest_rate, l.number_of_installments "
                   + "FROM accounts a JOIN lines_of_credit l ON a.id = l.account_id "
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
    public List<LineOfCreditDTO> getAll() throws SQLException {
        String sql = "SELECT a.id, a.customer_id, a.status_id, a.type_id, a.balance, a.opening_date, "
                   + "l.credit_limit, l.annual_interest_rate, l.number_of_installments "
                   + "FROM accounts a JOIN lines_of_credit l ON a.id = l.account_id";
        List<LineOfCreditDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // Public entry point — owns connection and transaction
    public void update(LineOfCreditDTO dto) throws SQLException {
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

    // Shared-connection overload — reused by insert() if needed in the future
    void update(LineOfCreditDTO dto, Connection conn) throws SQLException {
        accountDAO.update(dto, conn);
        String sql = "UPDATE lines_of_credit SET credit_limit = ?, annual_interest_rate = ?, number_of_installments = ? "
                   + "WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, Double.parseDouble(dto.getCredit_limit()));
            ps.setDouble(2, Double.parseDouble(dto.getAnnual_interest_rate()));
            ps.setInt(3,    Integer.parseInt(dto.getNumber_of_installments()));
            ps.setInt(4,    Integer.parseInt(dto.getId()));
            ps.executeUpdate();
        }
    }

    
    
    // DELETE
    public void delete(int id) throws SQLException {
        accountDAO.delete(id);
    }

   
   // BORROW 
    public void borrow(int accountId, double amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }

    // REPAY 
    public void repay(int accountId, double amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }

    // APPLY INTEREST — persists new balance after Service computed it
    public void applyInterest(LineOfCreditDTO dto) throws SQLException {
        accountDAO.update(dto);
    }

    private LineOfCreditDTO mapRow(ResultSet rs) throws SQLException {
        AccountStatus status = statusDAO.getById(rs.getInt("status_id"));
        AccountType   type   = typeDAO.getById(rs.getInt("type_id"));
        LineOfCreditDTO dto = new LineOfCreditDTO(
            String.valueOf(rs.getInt("id")),
            String.valueOf(rs.getInt("customer_id")),
            status, type,
            String.valueOf(rs.getDouble("balance")),
            String.valueOf(rs.getDouble("credit_limit")),
            String.valueOf(rs.getDouble("annual_interest_rate")),
            String.valueOf(rs.getInt("number_of_installments"))
        );
        dto.setOpening_date(rs.getDate("opening_date").toLocalDate());
        return dto;
    }
}