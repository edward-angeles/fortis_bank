package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import dto.TransactionType;

public class TransactionTypeDAO
{
    // add
    public int add(TransactionType transactionType)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "INSERT INTO transaction_types (name, description) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, transactionType.getName());
            ps.setString(2, transactionType.getDescription());
            return ps.executeUpdate();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // update
    public int update(TransactionType transactionType)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "UPDATE transaction_types SET name=?, description=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, transactionType.getName());
            ps.setString(2, transactionType.getDescription());
            ps.setInt(3, transactionType.getId());
            return ps.executeUpdate();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // delete
    public int delete(int id)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "DELETE FROM transaction_types WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // getById
    public TransactionType getById(int id)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT * FROM transaction_types WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                TransactionType t = new TransactionType(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description")
                );
                return t;
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // getAll
    public ArrayList<TransactionType> getAll()
    {
        ArrayList<TransactionType> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT * FROM transaction_types";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                TransactionType t = new TransactionType(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description")
                );
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