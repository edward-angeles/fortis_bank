package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import dto.AccountDTO;
import dto.AccountType;
import dto.AlertDTO;
import dto.CustomerDTO;
import dto.TransactionDTO;
import dto.TransactionType;

public class CustomerDAO
{
    // add
    public int add(CustomerDTO customer)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "INSERT INTO customers (first_name, last_name, pin, email, phone) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, customer.getFirst_name());
            ps.setString(2, customer.getLast_name());
            ps.setInt(3, Integer.parseInt(customer.getPin()));
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getPhone());
            return ps.executeUpdate();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }


    // update
    public int update(CustomerDTO customer)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "UPDATE customers SET first_name=?, last_name=?, pin=?, email=?, phone=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, customer.getFirst_name());
            ps.setString(2, customer.getLast_name());
            ps.setInt(3, Integer.parseInt(customer.getPin()));
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getPhone());
            ps.setInt(6, Integer.parseInt(customer.getId()));
            return ps.executeUpdate();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // updatePersonalInfo
    public int updatePersonalInfo(String id, String first_name, String last_name, String email, String phone)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "UPDATE customers SET first_name=?, last_name=?, email=?, phone=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, first_name);
            ps.setString(2, last_name);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setInt(5, Integer.parseInt(id));
            return ps.executeUpdate();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // getById
    public CustomerDTO getById(String id)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT * FROM customers WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                CustomerDTO c = new CustomerDTO(rs.getString("id"), rs.getString("first_name"), rs.getString("last_name"), 
                rs.getString("pin"), rs.getString("email"), rs.getString("phone"));
                return c;
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // getAll
    public ArrayList<CustomerDTO> getAll()
    {
        ArrayList<CustomerDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT * FROM customers";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
            	 CustomerDTO c = new CustomerDTO(rs.getString("id"), rs.getString("first_name"), rs.getString("last_name"), 
                         rs.getString("pin"), rs.getString("email"), rs.getString("phone"));
                list.add(c);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return list;
    }


    // getBmcId 
    public String getBMCId(String customer_id)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT id FROM bank_managers_customers WHERE customer_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(customer_id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("id");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // requestAccountOpening
    public int requestAccountOpening(CustomerDTO customer, AccountType type, String bank_manager_customer_id)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "INSERT INTO alerts (message, type_id, bank_manager_customer_id) VALUES (?, 4, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "Customer " + customer.getId() + " requests opening of account type " + type.getName());
            ps.setInt(2, Integer.parseInt(bank_manager_customer_id));
            return ps.executeUpdate();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // getAccountList
    // Uses AccountDAO.getByCustomerId() which already handles AccountStatus and AccountType
    public ArrayList<AccountDTO> getAccountListByCustomerId(String customer_id)
    {
        ArrayList<AccountDTO> list = new ArrayList<>();
        AccountDAO accountDAO = new AccountDAO();

        try
        {
            list = (ArrayList<AccountDTO>) accountDAO.getAccountsByCustomerId(Integer.parseInt(customer_id));
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return list;
    }

    // getTransactionHistory
    public ArrayList<TransactionDTO> getTransactionHistory(String account_id)
    {
        ArrayList<TransactionDTO> list = new ArrayList<>();      
        TransactionTypeDAO transTypeDAO = new TransactionTypeDAO();
        AlertDAO alertDAO = new AlertDAO();
        
        try (Connection conn = DBConnection.getConnection())
        {
        	String sql = "SELECT * FROM transactions WHERE source_account_id = ? OR target_account_id = ? ORDER BY transaction_date DESC";
//            String sql = "SELECT t.id, tt.name as transaction_type, t.amount, t.transaction_date, t.source_account_id,  t.target_account_id, t.fee, at.name, a.message"
//            		+ " FROM transactions t JOIN transaction_types tt ON t.type_id = tt.id"
//            		+ " JOIN alerts a ON t.alert_id = a.id"
//            		+ " JOIN alert_types at ON a.type_id = at.id"
//            		+ " WHERE source_account_id=? OR target_account_id=? ORDER BY transaction_date DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(account_id));
            ps.setInt(2, Integer.parseInt(account_id));
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
            	
            	//I should get the transaction type object by the id
            	//I should get the alert object by the id
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

    // checkAccountBalance
    public double checkAccountBalance(String account_id, String customer_id)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT balance FROM accounts WHERE id=? AND customer_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(account_id));
            ps.setInt(2, Integer.parseInt(customer_id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return 0;
    }
    
    public CustomerDTO login(String email, String pin)
    {
    	
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT * FROM customers WHERE email=? AND pin=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, email);
            ps.setString(2, pin);

            ResultSet rs = ps.executeQuery();

            if(rs.next())
            {
            	CustomerDTO c = new CustomerDTO(rs.getString("id"), rs.getString("first_name"), rs.getString("last_name"), 
                        rs.getString("pin"), rs.getString("email"), rs.getString("phone"));
                        return c;
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return null;
    }

}