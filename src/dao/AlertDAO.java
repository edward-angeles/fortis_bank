package dao;

import java.sql.Statement;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import dto.AlertDTO;
import dto.AlertType;

public class AlertDAO
{
    // add
//    public AlertDTO add(AlertDTO alert)
//    {
//        try (Connection conn = DBConnection.getConnection())
//        {
//            String sql = "INSERT INTO alerts (message, type_id, bank_manager_customer_id) VALUES (?, ?, ?)";
//
//            PreparedStatement ps = conn.prepareStatement(sql, new String[] { "ID" });
//            ps.setString(1, alert.getMessage());
//            ps.setInt(2, alert.getAlertType().getId());
//            if (alert.getBmc_id() == null || alert.getBmc_id().trim().isEmpty()) {
//                ps.setNull(3, java.sql.Types.INTEGER);
//            } else {
//                ps.setInt(3, Integer.parseInt(alert.getBmc_id()));
//            }
//            
//            ps.executeUpdate();
//                        
//            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
//                if (generatedKeys.next()) {
//                    // Use the column name "ID" instead of the index 1
//                    long newId = generatedKeys.getLong("ID"); 
//                    alert.setId(String.valueOf(newId));
//                    return alert;
//                }
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        return null;
//    }
    
    public AlertDTO add(AlertDTO alert) {
        // The SQL now asks the database to send the ID back immediately
        String sql = "BEGIN INSERT INTO alerts (message, type_id, bank_manager_customer_id) " +
                     "VALUES (?, ?, ?) RETURNING id INTO ?; END;";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, alert.getMessage());
            cs.setInt(2, alert.getAlertType().getId());

            if (alert.getBmc_id() == null || alert.getBmc_id().trim().isEmpty()) {
                cs.setNull(3, java.sql.Types.INTEGER);
            } else {
                cs.setInt(3, Integer.parseInt(alert.getBmc_id()));
            }

            // Register the 4th parameter as an OUT parameter (the ID)
            cs.registerOutParameter(4, java.sql.Types.INTEGER);

            cs.execute();

            // Retrieve the ID from the OUT parameter
            int newId = cs.getInt(4);
            alert.setId(String.valueOf(newId));
            
            return alert;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // update
    public int update(AlertDTO alert)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "UPDATE alerts SET message=?, type_id=?, bank_manager_customer_id=? WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, alert.getMessage());
            ps.setInt(2, alert.getAlertType().getId());
            ps.setInt(3, Integer.parseInt(alert.getBmc_id()));
            ps.setInt(4, Integer.parseInt(alert.getId()));

            return ps.executeUpdate();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // getById
    public AlertDTO getById(String id)
    {
        
    	AlertTypeDAO alertTypeDAO = new AlertTypeDAO();
    	
    	try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT * FROM alerts WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, Integer.parseInt(id));

            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
            	AlertType type = alertTypeDAO.getById(rs.getInt("type_id"));
                AlertDTO a = new AlertDTO(rs.getString("id"), rs.getString("message"), type, rs.getString("bank_manager_customer_id"));
                return a;
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return null;
    }

    // getAll
    public ArrayList<AlertDTO> getAll()
    {
        ArrayList<AlertDTO> list = new ArrayList<>();
        
        AlertTypeDAO alertTypeDAO = new AlertTypeDAO();

        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT * FROM alerts";

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
            	AlertType type = alertTypeDAO.getById(rs.getInt("type_id"));
                AlertDTO a = new AlertDTO(rs.getString("id"), rs.getString("message"), type, rs.getString("bank_manager_customer_id"));
                list.add(a);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return list;
    }
}