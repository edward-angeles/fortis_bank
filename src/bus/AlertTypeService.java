
package bus;

import dao.AlertTypeDAO;
import dto.AlertType;
import java.sql.SQLException;
import java.util.List;

public class AlertTypeService {

    private AlertTypeDAO dao = new AlertTypeDAO();

    // CREATE
    public void create(AlertType type) throws SQLException, NotNullException {
        validate(type);
        dao.insert(type);
    }

    // READ - by ID
    public AlertType getById(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Alert type ID must be a positive number.");
        AlertType type = dao.getById(id);
        if (type == null)
            throw new NotNullException("No alert type found with id: " + id);
        return type;
    }

    // READ - all
    public List<AlertType> getAll() throws SQLException {
        return dao.getAll();
    }

    // UPDATE
    public void update(AlertType type) throws SQLException, NotNullException {
        if (type.getId() <= 0)
            throw new NotNullException("Alert type ID must be a positive number.");
        validate(type);
        dao.update(type);
    }

    // DELETE
    public void delete(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Alert type ID must be a positive number.");
        dao.delete(id);
    }

    private void validate(AlertType type) throws NotNullException {
        if (type == null)
            throw new NotNullException("Alert type cannot be null.");
        if (type.getName() == null || type.getName().trim().isEmpty())
            throw new NotNullException("Alert type name cannot be null or empty.");
        if (type.getName().length() > 30)
            throw new NotNullException("Alert type name cannot exceed 30 characters.");
    }
}
