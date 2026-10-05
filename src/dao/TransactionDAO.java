package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import dto.AlertDTO;
import dto.TransactionDTO;
import dto.TransactionType;

public class TransactionDAO
{
    // INSERT a new transaction record
    // 1 is successful
	public int add(TransactionDTO transaction)
    {
        String sql = "INSERT INTO transactions (type_id, amount, transaction_date, source_account_id, target_account_id, fee, alert_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            // transaction_date uses SYSDATE � Oracle sets it automatically

            ps.setInt(1, transaction.getType().getId());
            ps.setDouble(2, transaction.getAmount());
        	ps.setDate(3, Date.valueOf(transaction.getTransaction_date()));
        	
        	// source_account_id — null for deposits
        	if (transaction.getSource_account_id() == null || transaction.getSource_account_id().trim().isEmpty())
                ps.setNull(4, java.sql.Types.INTEGER);
            else
                ps.setInt(4, Integer.parseInt(transaction.getSource_account_id()));
        	
        	
        	 // target_account_id — null for withdrawals
            if (transaction.getTarget_account_id() == null || transaction.getTarget_account_id().trim().isEmpty())
                ps.setNull(5, java.sql.Types.INTEGER);
            else
                ps.setInt(5, Integer.parseInt(transaction.getTarget_account_id()));
            
            
            ps.setDouble(6, transaction.getFee());
            ps.setInt(7,  Integer.parseInt(transaction.getAlert().getId()));
            return ps.executeUpdate();
           
        }
        catch (Exception e)
        {
        	System.out.println("Error in insert query");
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // SELECT one transaction by id
    public TransactionDTO getById(String id)
    {
   	    TransactionTypeDAO transTypeDAO = new TransactionTypeDAO();
        AlertDAO alertDAO = new AlertDAO();
    	
        String sql = "SELECT * FROM transactions WHERE id=?";
    	try (Connection conn = DBConnection.getConnection())
        {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
            	TransactionType type = transTypeDAO.getById(rs.getInt("type_id"));
            	AlertDTO alert = alertDAO.getById(String.valueOf(rs.getInt("alert_id")));
            	
                TransactionDTO t = new TransactionDTO(rs.getString("id"), type, rs.getDouble("amount"), 
						rs.getDate("transaction_date").toLocalDate(), rs.getString("source_account_id"), rs.getString("target_account_id"), 
						rs.getDouble("fee"), alert);
                return t;
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return null;
    }

    // SELECT all transactions for one account (history)
    public ArrayList<TransactionDTO> getByAccount(String account_id)
    {
        ArrayList<TransactionDTO> list = new ArrayList<>();
        TransactionTypeDAO transTypeDAO = new TransactionTypeDAO();
        AlertDAO alertDAO = new AlertDAO();

        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT * FROM transactions WHERE source_account_id=? OR target_account_id=? ORDER BY transaction_date DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(account_id));
            ps.setInt(2, Integer.parseInt(account_id));
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
            	TransactionType type = transTypeDAO.getById(rs.getInt("type_id"));
            	AlertDTO alert = alertDAO.getById(String.valueOf(rs.getInt("alert_id")));
            	
                TransactionDTO t = new TransactionDTO(rs.getString("id"), type, rs.getDouble("amount"), 
                		rs.getDate("transaction_date").toLocalDate(), rs.getString("source_account_id"), rs.getString("target_account_id"), 
						rs.getDouble("fee"), alert);

                list.add(t);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return list;
    }

    // SELECT all transactions (for manager reports)
    public ArrayList<TransactionDTO> getAll()
    {
        ArrayList<TransactionDTO> list = new ArrayList<>();
        TransactionTypeDAO transTypeDAO = new TransactionTypeDAO();
        AlertDAO alertDAO = new AlertDAO();

        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
            	TransactionType type = transTypeDAO.getById(rs.getInt("type_id"));
            	AlertDTO alert = alertDAO.getById(String.valueOf(rs.getInt("alert_id")));
            	
                TransactionDTO t = new TransactionDTO(rs.getString("id"), type, rs.getDouble("amount"), 
                		rs.getDate("transaction_date").toLocalDate(), rs.getString("source_account_id"), rs.getString("target_account_id"), 
						rs.getDouble("fee"), alert);

                list.add(t);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return list;
    }
}