
package bus;

import dao.BankManagerDAO;
import dto.BankManagerDTO;
import dto.CustomerDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BankManagerService {

    private BankManagerDAO dao = new BankManagerDAO();

    // CREATE
    public int create(BankManagerDTO manager) throws SQLException, NotNullException {
        validate(manager);
        return dao.add(manager);
    }

    // READ - by ID
    public BankManagerDTO getById(String id) throws SQLException, NotNullException {
        validateId(id);
        BankManagerDTO manager = dao.getById(id);
        if (manager == null)
            throw new NotNullException("No bank manager found with id: " + id);
        return manager;
    }

    // READ - all
    public ArrayList<BankManagerDTO> getAll() throws SQLException {
        return dao.getAll();
    }

    // READ - get customers by manager ID
    public List<CustomerDTO> getCustomersByManagerId(int managerId)
            throws SQLException, NotNullException {
        if (managerId <= 0)
            throw new NotNullException("Manager ID must be a positive number.");
        return dao.getCustomersByManagerId(managerId);
    }

    // UPDATE
    public int update(BankManagerDTO manager) throws SQLException, NotNullException {
        validateId(manager.getManagerId());
        validate(manager);
        return dao.update(manager);
    }

    // ASSIGN CUSTOMER
    public int assignCustomer(String managerId, String customerId)
            throws SQLException, NotNullException {
        validateId(managerId);
        validateId(customerId);
        // Verify manager exists before assigning
        BankManagerDTO manager = dao.getById(managerId);
        if (manager == null)
            throw new NotNullException("No bank manager found with id: " + managerId);
        return dao.assignCustomer(managerId, customerId);
    }

    // LOGIN
    public BankManagerDTO login(String id, String email)
            throws SQLException, NotNullException {
        validateId(id);
        if (email == null || email.trim().isEmpty())
            throw new NotNullException("Email cannot be null or empty.");
        if (!email.contains("@") || !email.contains("."))
            throw new NotNullException("Email format is invalid.");
        BankManagerDTO manager = dao.login(id, email);
        if (manager == null)
            throw new NotNullException("Invalid ID or email.");
        return manager;
    }

    private void validate(BankManagerDTO m) throws NotNullException {
        if (m == null)
            throw new NotNullException("Bank manager cannot be null.");
        if (m.getFirstName() == null || m.getFirstName().trim().isEmpty())
            throw new NotNullException("First name cannot be null or empty.");
        if (m.getFirstName().length() > 25)
            throw new NotNullException("First name cannot exceed 25 characters.");
        if (m.getLastName() == null || m.getLastName().trim().isEmpty())
            throw new NotNullException("Last name cannot be null or empty.");
        if (m.getLastName().length() > 25)
            throw new NotNullException("Last name cannot exceed 25 characters.");
        if (m.getEmail() == null || m.getEmail().trim().isEmpty())
            throw new NotNullException("Email cannot be null or empty.");
        if (!m.getEmail().contains("@") || !m.getEmail().contains("."))
            throw new NotNullException("Email format is invalid.");
        if (m.getStatus() == null)
            throw new NotNullException("Job status cannot be null.");
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