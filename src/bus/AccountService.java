package bus;

import dao.AccountDAO;
import dto.AccountDTO;
import dto.AccountStatus;

import java.sql.SQLException;
import java.util.List;

import javax.security.auth.login.AccountException;

public class AccountService {

    private AccountDAO dao = new AccountDAO();

    // CREATE
    public AccountDTO create(AccountDTO account) throws SQLException, NotNullException, NotInRangeException {
        validateAccount(account);
            return dao.insert(account);
    }

    // READ - by ID
    public AccountDTO getById(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Account ID must be a positive number.");
        AccountDTO account = dao.getById(id);
        if (account == null)
            throw new NotNullException("No account found with id: " + id);
        return account;
    }

    // READ - all
    public List<AccountDTO> getAllAcccounts() throws SQLException {
        return dao.getAll();
    }

    // READ - by customer
    public List<AccountDTO> getAccountsByCustomerId(int customerId) throws SQLException, NotNullException {
        if (customerId <= 0)
            throw new NotNullException("Customer ID must be a positive number.");
        return dao.getAccountsByCustomerId(customerId);
    }

    // UPDATE
    public void update(AccountDTO account) throws SQLException, NotNullException, NotInRangeException {
        if (account.getId() == null || account.getId().trim().isEmpty())
            throw new NotNullException("Account ID cannot be null.");
        validateAccount(account);
        dao.update(account);
    }

    // DELETE
    public void delete(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Account ID must be a positive number.");
        dao.delete(id);
    }

    // CLOSE ACCOUNT
    public void closeAccount(int id) throws SQLException, NotNullException, AccountException {
        if (id <= 0)
            throw new NotNullException("Account ID must be a positive number.");
        AccountDTO account = dao.getById(id);
        if (account == null)
            throw new AccountException("Account not found.");
        if (account.getStatus().getId() == 3)
            throw new AccountException("Account is already closed.");
        if (Double.parseDouble(account.getBalance()) > 0)
            throw new AccountException("Cannot close account with a remaining balance.");
        dao.closeAccount(id);
    }

    // BLOCK ACCOUNT
    public void blockAccount(int id) throws SQLException, NotNullException, AccountException {
        if (id <= 0)
            throw new NotNullException("Account ID must be a positive number.");
        AccountDTO account = dao.getById(id);
        if (account == null)
            throw new AccountException("Account not found.");
        if (account.getStatus().getId() == 2)
            throw new AccountException("Account is already blocked.");
        if (account.getStatus().getId() == 3)
            throw new AccountException("Cannot block a closed account.");
        // status_id = 2 is 'blocked'
        account.setStatus(new AccountStatus(2, "blocked", null));
        dao.update(account);
    }

    // Shared validation
    protected void validateAccount(AccountDTO account) throws NotNullException, NotInRangeException {
        if (account == null)
            throw new NotNullException("Account cannot be null.");
        if (account.getCustomer_id() == null || account.getCustomer_id().trim().isEmpty())
            throw new NotNullException("Customer ID cannot be null or empty.");
        if (account.getStatus() == null)
            throw new NotNullException("Account status cannot be null.");
        if (account.getType() == null)
            throw new NotNullException("Account type cannot be null.");
        if (account.getBalance() == null || account.getBalance().trim().isEmpty())
            throw new NotNullException("Balance cannot be null or empty.");

        double balance;
        try {
            balance = Double.parseDouble(account.getBalance());
        } catch (NumberFormatException e) {
            throw new NotInRangeException("Balance must be a valid number.");
        }
        if (balance < 0)
            throw new NotInRangeException("Balance cannot be negative.");
    }
}