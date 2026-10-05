package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import dto.ReportDTO;
import dto.ReportType;

public class ReportDAO
{
    // add
    public int add(ReportDTO report)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "INSERT INTO reports (report_type_id, report_content, author_id, recipient_id) VALUES (?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, report.getType().getId());
            ps.setString(2, report.getReportContent());
            ps.setInt(3, Integer.parseInt(report.getManagerId()));
            ps.setInt(4, Integer.parseInt(report.getCustomerId()));

            return ps.executeUpdate();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // updat, no change of author
    public int update(ReportDTO report)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "UPDATE reports SET report_type_id=?, report_content=?, recipient_id=? WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, report.getType().getId());
            ps.setString(2, report.getReportContent());
            ps.setInt(3, Integer.parseInt(report.getCustomerId()));
            ps.setInt(4, Integer.parseInt(report.getReportId()));

            return ps.executeUpdate();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // delete
    public int delete(String reportId)
    {
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "DELETE FROM reports WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, Integer.parseInt(reportId));

            return ps.executeUpdate();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    // getById
    public ReportDTO getById(String reportId)
    {
    	ReportTypeDAO reportTypeDAO = new ReportTypeDAO();
    	
        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT * FROM reports WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, Integer.parseInt(reportId));

            ResultSet rs = ps.executeQuery();

            if(rs.next())
            {
            	ReportType type = reportTypeDAO.getById(rs.getInt("type_id"));
                ReportDTO r = new ReportDTO(rs.getString("id"), rs.getTimestamp("generated_at").toLocalDateTime(), type,  
                		rs.getString("report_content"), rs.getString("author_id"), rs.getString("recipient_id"));
                return r;
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return null;
    }

    // getAll
    public ArrayList<ReportDTO> getAll()
    {
        ArrayList<ReportDTO> list = new ArrayList<>();
    	ReportTypeDAO reportTypeDAO = new ReportTypeDAO();

        try (Connection conn = DBConnection.getConnection())
        {
            String sql = "SELECT * FROM reports";

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
            	ReportType type = reportTypeDAO.getById(rs.getInt("type_id"));
                ReportDTO r = new ReportDTO(rs.getString("id"), rs.getTimestamp("generated_at").toLocalDateTime(), type,  
                		rs.getString("report_content"), rs.getString("author_id"), rs.getString("recipient_id"));

                list.add(r);
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return list;
    }
}