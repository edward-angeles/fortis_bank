
package bus;

import dao.ReportTypeDAO;
import dto.ReportType;
import java.sql.SQLException;
import java.util.List;

public class ReportTypeService {

    private ReportTypeDAO dao = new ReportTypeDAO();

    // CREATE
    public void create(ReportType type) throws SQLException, NotNullException {
        validate(type);
        dao.insert(type);
    }

    // READ - by ID
    public ReportType getById(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Report type ID must be a positive number.");
        ReportType type = dao.getById(id);
        if (type == null)
            throw new NotNullException("No report type found with id: " + id);
        return type;
    }

    // READ - all
    public List<ReportType> getAll() throws SQLException {
        return dao.getAll();
    }

    // UPDATE
    public void update(ReportType type) throws SQLException, NotNullException {
        if (type.getId() <= 0)
            throw new NotNullException("Report type ID must be a positive number.");
        validate(type);
        dao.update(type);
    }

    // DELETE
    public void delete(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Report type ID must be a positive number.");
        dao.delete(id);
    }

    private void validate(ReportType type) throws NotNullException {
        if (type == null)
            throw new NotNullException("Report type cannot be null.");
        if (type.getName() == null || type.getName().trim().isEmpty())
            throw new NotNullException("Report type name cannot be null or empty.");
        if (type.getName().length() > 15)
            throw new NotNullException("Report type name cannot exceed 15 characters.");
    }
}