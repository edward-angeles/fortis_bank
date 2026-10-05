package bus;


import dao.AccountStatusDAO;
import dto.AccountStatus;
import java.sql.SQLException;
import java.util.List;

public class AccountStatusService {

    private AccountStatusDAO dao = new AccountStatusDAO();

    // CREATE
    public void create(AccountStatus status) throws SQLException, NotNullException {
        validateAccountStatus(status);
        dao.insert(status);
    }

    // READ - by ID
    public AccountStatus getById(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Status ID must be a positive number.");
        AccountStatus status = dao.getById(id);
        if (status == null)
            throw new NotNullException("No account status found with id: " + id);
        return status;
    }

    // READ - all
    public List<AccountStatus> getAll() throws SQLException {
        return dao.getAll();
    }

    // UPDATE
    public void update(AccountStatus status) throws SQLException, NotNullException {
        if (status.getId() <= 0)
            throw new NotNullException("Status ID must be a positive number.");
        validateAccountStatus(status);
        dao.update(status);
    }

    // DELETE
    public void delete(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Status ID must be a positive number.");
        dao.delete(id);
    }

    // Shared validation
    private void validateAccountStatus(AccountStatus status) throws NotNullException {
        if (status == null)
            throw new NotNullException("Account status cannot be null.");
        if (status.getName() == null || status.getName().trim().isEmpty())
            throw new NotNullException("Status name cannot be null or empty.");
        if (status.getName().length() > 25)
            throw new NotNullException("Status name cannot exceed 25 characters.");
    }
}
