
package bus;

import dao.ReportDAO;
import dto.ReportDTO;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReportService {

    private ReportDAO dao = new ReportDAO();

    // CREATE
    public int create(ReportDTO report) throws SQLException, NotNullException {
        validate(report);
        return dao.add(report);
    }

    // READ - by ID
    public ReportDTO getById(String id) throws SQLException, NotNullException {
        validateId(id);
        ReportDTO report = dao.getById(id);
        if (report == null)
            throw new NotNullException("No report found with id: " + id);
        return report;
    }

    // READ - all
    public ArrayList<ReportDTO> getAll() throws SQLException {
        return dao.getAll();
    }

    // UPDATE
    public int update(ReportDTO report) throws SQLException, NotNullException {
        validateId(report.getReportId());
        validate(report);
        return dao.update(report);
    }

    // DELETE
    public int delete(String id) throws SQLException, NotNullException {
        validateId(id);
        // Verify report exists before deleting
        ReportDTO report = dao.getById(id);
        if (report == null)
            throw new NotNullException("No report found with id: " + id);
        return dao.delete(id);
    }

    private void validate(ReportDTO r) throws NotNullException {
        if (r == null)
            throw new NotNullException("Report cannot be null.");
        if (r.getType() == null)
            throw new NotNullException("Report type cannot be null.");
        if (r.getReportContent() == null || r.getReportContent().trim().isEmpty())
            throw new NotNullException("Report content cannot be null or empty.");
        if (r.getManagerId() == null || r.getManagerId().trim().isEmpty())
            throw new NotNullException("Author (manager) ID cannot be null or empty.");
        if (r.getCustomerId() == null || r.getCustomerId().trim().isEmpty())
            throw new NotNullException("Recipient (customer) ID cannot be null or empty.");
    }

    private void validateId(String id) throws NotNullException {
        if (id == null || id.trim().isEmpty())
            throw new NotNullException("ID cannot be null or empty.");
        try {
            if (Integer.parseInt(id) <= 0)
                throw new NotNullException("ID must be a positive number.");
        } catch (NumberFormatException e) {
            throw new NotNullException("ID must be a valid number.");
        }
    }
}
