package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import dto.CustomerDTO;
import java.util.List;
import java.sql.SQLException;

import dto.BankManagerDTO;
import dto.JobStatus;

public class BankManagerDAO
{
	//Nuevo
	public List<CustomerDTO> getCustomersByManagerId(int managerId) throws SQLException
	{		
		String sql =
	        "SELECT c.id, c.first_name,  c.last_name, c.pin, c.email, c.phone " +
	        "FROM bank_managers_customers bmc " +
	        "JOIN customers c ON c.id = bmc.customer_id " +
	        "WHERE bmc.bank_manager_id = ?";

	    List<CustomerDTO> list = new ArrayList<>();

	    try(Connection conn = DBConnection.getConnection();
	        PreparedStatement ps = conn.prepareStatement(sql))
	    {
	        ps.setInt(1, managerId);

	        try(ResultSet rs = ps.executeQuery())
	        {
	            while(rs.next())
	            {
	                CustomerDTO customer = new CustomerDTO(String.valueOf(rs.getInt("id")), rs.getString("first_name"), rs.getString("last_name"), 
	                rs.getString("pin"), rs.getString("email"), rs.getString("phone"));

	                list.add(customer);
	            }
	        }
	    }

	    return list;
	}

    // assignCustomer - nuevo 
    public int assignCustomer(String managerId, String customerId)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "INSERT INTO bank_managers_customers (bank_manager_id, customer_id) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(managerId));
            ps.setInt(2, Integer.parseInt(customerId));
            return ps.executeUpdate();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // add
    public int add(BankManagerDTO manager)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "INSERT INTO bank_managers (first_name, last_name, email, status_id) VALUES (?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, manager.getFirstName());
            ps.setString(2, manager.getLastName());
            ps.setString(3, manager.getEmail());
            ps.setInt(4, manager.getStatus().getId());

            return ps.executeUpdate();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // update
    public int update(BankManagerDTO manager)
    {
        try (Connection conn = DBConnection.getConnection())
        {
        	String sql = "UPDATE bank_managers SET first_name=?, last_name=?, status_id=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, manager.getFirstName());
            ps.setString(2, manager.getLastName());
            ps.setInt(3, manager.getStatus().getId());
            ps.setInt(4, Integer.parseInt(manager.getManagerId()));

            return ps.executeUpdate();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // getById
    public BankManagerDTO getById(String id)
    {
    	JobStatusDAO jobStatusDAO = new JobStatusDAO();
    	
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT * FROM bank_managers WHERE id = ?";  
            
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setInt(1, Integer.parseInt(id));

            ResultSet rs = ps.executeQuery();

            if(rs.next())
            {
            	JobStatus jobStatus = jobStatusDAO.getById(rs.getInt("status_id"));
                BankManagerDTO bm = new BankManagerDTO(rs.getString("id"), rs.getString("first_name"), 
                									rs.getString("last_name"), rs.getString("email"), jobStatus); 

                return bm;
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return null;
    }
    //LOGIN
    public BankManagerDTO login(String id, String email)
    {
    	JobStatusDAO jobStatusDAO = new JobStatusDAO();
    	
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT * FROM bank_managers WHERE bm.id=? AND bm.email=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, Integer.parseInt(id));
            ps.setString(2, email);

            ResultSet rs = ps.executeQuery();

            if(rs.next())
            {
            	JobStatus jobStatus = jobStatusDAO.getById(rs.getInt("status_id"));
                BankManagerDTO bm = new BankManagerDTO(rs.getString("id"), rs.getString("first_name"), 
                									rs.getString("last_name"), rs.getString("email"), jobStatus); 

                return bm;
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return null;
    }

    // getAll
    public ArrayList<BankManagerDTO> getAll()
    {
        ArrayList<BankManagerDTO> list = new ArrayList<>();
    	JobStatusDAO jobStatusDAO = new JobStatusDAO();

        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT bm.id, bm.first_name, bm.last_name, bm.email, " +
                         "js.id AS status_id, js.name AS status_name, js.description AS status_description " +
                         "FROM bank_managers bm " +
                         "JOIN job_statuses js ON bm.status_id = js.id";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
               JobStatus jobStatus = jobStatusDAO.getById(rs.getInt("status_id"));
               BankManagerDTO bm = new BankManagerDTO(rs.getString("id"), rs.getString("first_name"), 
						rs.getString("last_name"), rs.getString("email"), jobStatus); 

                list.add(bm);
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return list;
    }
}