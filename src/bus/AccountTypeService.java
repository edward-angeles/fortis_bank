package bus;


import dao.AccountTypeDAO;
import dto.AccountType;
import java.sql.SQLException;
import java.util.List;

public class AccountTypeService {

    private AccountTypeDAO dao = new AccountTypeDAO();

    // CREATE
    public void create(AccountType type) throws SQLException, NotNullException {
        validateAccountType(type);
        dao.insert(type);
    }

    // READ - by ID
    public AccountType getById(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Type ID must be a positive number.");
        AccountType type = dao.getById(id);
        if (type == null)
            throw new NotNullException("No account type found with id: " + id);
        return type;
    }

    // READ - all
    public List<AccountType> getAll() throws SQLException {
        return dao.getAll();
    }

    // UPDATE
    public void update(AccountType type) throws SQLException, NotNullException {
        if (type.getId() <= 0)
            throw new NotNullException("Type ID must be a positive number.");
        validateAccountType(type);
        dao.update(type);
    }

    // DELETE
    public void delete(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Type ID must be a positive number.");
        dao.delete(id);
    }

    // Shared validation
    private void validateAccountType(AccountType type) throws NotNullException {
        if (type == null)
            throw new NotNullException("Account type cannot be null.");
        if (type.getName() == null || type.getName().trim().isEmpty())
            throw new NotNullException("Type name cannot be null or empty.");
        if (type.getName().length() > 25)
            throw new NotNullException("Type name cannot exceed 25 characters.");
    }
}