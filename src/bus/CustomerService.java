
package bus;

import dao.CustomerDAO;
import dto.AccountDTO;
import dto.AccountType;
import dto.CustomerDTO;
import dto.TransactionDTO;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerService {

    private CustomerDAO dao = new CustomerDAO();

    // CREATE
    public int create(CustomerDTO customer) throws SQLException, NotNullException, NotInRangeException {
        validate(customer);
        return dao.add(customer);
    }

    // READ - by ID
    public CustomerDTO getById(String id) throws SQLException, NotNullException {
        validateId(id);
        CustomerDTO customer = dao.getById(id);
        if (customer == null)
            throw new NotNullException("No customer found with id: " + id);
        return customer;
    }

    // READ - all
    public ArrayList<CustomerDTO> getAll() throws SQLException {
        return dao.getAll();
    }
    
    // UPDATE � full record
    public int update(CustomerDTO customer) throws SQLException, NotNullException, NotInRangeException {
        validateId(customer.getId());
        validate(customer);
        return dao.update(customer);
    }

    // UPDATE � personal info only (no PIN change)
    public int updatePersonalInfo(String id, String firstName, String lastName,
                                  String email, String phone)
            throws SQLException, NotNullException {
        validateId(id);
        if (firstName == null || firstName.trim().isEmpty())
            throw new NotNullException("First name cannot be null or empty.");
        if (lastName == null || lastName.trim().isEmpty())
            throw new NotNullException("Last name cannot be null or empty.");
        if (email == null || email.trim().isEmpty())
            throw new NotNullException("Email cannot be null or empty.");
        if (!email.contains("@") || !email.contains("."))
            throw new NotNullException("Email format is invalid.");
        if (phone == null || phone.trim().isEmpty())
            throw new NotNullException("Phone cannot be null or empty.");
        if (!phone.matches("\\d{3}-\\d{3}-\\d{4}"))
            throw new NotNullException("Phone must follow the format: 111-111-1111.");
        return dao.updatePersonalInfo(id, firstName, lastName, email, phone);
    }

    // LOGIN
    public CustomerDTO login(String email, String pin)
            throws SQLException, NotNullException, NotInRangeException {
        if (email == null || email.trim().isEmpty())
            throw new NotNullException("Email cannot be null or empty.");
        if (pin == null || pin.trim().isEmpty())
            throw new NotNullException("PIN cannot be null or empty.");
        validatePin(pin);
        CustomerDTO customer = dao.login(email, pin);
        if (customer == null)
            throw new NotNullException("Invalid email or PIN.");
        return customer;
    }

    // GET ACCOUNT LIST
    public ArrayList<AccountDTO> getAccountListByCustomer(String customerId)
            throws SQLException, NotNullException {
        validateId(customerId);
        return dao.getAccountListByCustomerId(customerId);
    }

    // GET TRANSACTION HISTORY
    public ArrayList<TransactionDTO> getTransactionHistory(String accountId)
            throws SQLException, NotNullException {
        validateId(accountId);
        return dao.getTransactionHistory(accountId);
    }

    // CHECK ACCOUNT BALANCE
    public double checkAccountBalance(String accountId, String customerId)
            throws SQLException, NotNullException {
        validateId(accountId);
        validateId(customerId);
        return dao.checkAccountBalance(accountId, customerId);
    }

    // REQUEST ACCOUNT OPENING
    public int requestAccountOpening(CustomerDTO customer, AccountType type,
                                     String bankManagerCustomerId)
            throws SQLException, NotNullException {
        if (customer == null)
            throw new NotNullException("Customer cannot be null.");
        if (type == null)
            throw new NotNullException("Account type cannot be null.");
        validateId(bankManagerCustomerId);

        // Fetch BMC id fresh from DB to ensure the relationship exists
        if (bankManagerCustomerId == null)
            throw new NotNullException("No bank manager assigned to this customer.");

        return dao.requestAccountOpening(customer, type, bankManagerCustomerId);
    }
    
    public String getBMCId(String customerId) throws NotNullException {
    	validateId(customerId);
    	return dao.getBMCId(customerId);
    }

    // Shared full validation
    private void validate(CustomerDTO c) throws NotNullException, NotInRangeException {
        if (c == null)
            throw new NotNullException("Customer cannot be null.");
        if (c.getFirst_name() == null || c.getFirst_name().trim().isEmpty())
            throw new NotNullException("First name cannot be null or empty.");
        if (c.getFirst_name().length() > 25)
            throw new NotNullException("First name cannot exceed 25 characters.");
        if (c.getLast_name() == null || c.getLast_name().trim().isEmpty())
            throw new NotNullException("Last name cannot be null or empty.");
        if (c.getLast_name().length() > 25)
            throw new NotNullException("Last name cannot exceed 25 characters.");
        if (c.getEmail() == null || c.getEmail().trim().isEmpty())
            throw new NotNullException("Email cannot be null or empty.");
        if (!c.getEmail().contains("@") || !c.getEmail().contains("."))
            throw new NotNullException("Email format is invalid.");
        if (c.getPhone() == null || c.getPhone().trim().isEmpty())
            throw new NotNullException("Phone cannot be null or empty.");
        if (!c.getPhone().matches("\\d{3}-\\d{3}-\\d{4}"))
            throw new NotNullException("Phone must follow the format: 111-111-1111.");
        validatePin(c.getPin());
    }

    private void validatePin(String pin) throws NotNullException, NotInRangeException {
        if (pin == null || pin.trim().isEmpty())
            throw new NotNullException("PIN cannot be null or empty.");
        int pinValue;
        try {
            pinValue = Integer.parseInt(pin);
        } catch (NumberFormatException e) {
            throw new NotInRangeException("PIN must be a 4-digit number.");
        }
        if (pinValue < 1000 || pinValue > 9999)
            throw new NotInRangeException("PIN must be between 1000 and 9999.");
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
