
package bus;

import dao.TransactionDAO;
import dto.TransactionDTO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class TransactionService {

    private TransactionDAO dao = new TransactionDAO();

    // CREATE
    public int create(TransactionDTO transaction) throws SQLException, NotNullException, NotInRangeException {
        validate(transaction);
        return dao.add(transaction);
    }

    // READ - by ID
    public TransactionDTO getById(String id) throws SQLException, NotNullException {
        validateId(id);
        TransactionDTO t = dao.getById(id);
        if (t == null)
            throw new NotNullException("No transaction found with id: " + id);
        return t;
    }

    // READ - by account (history)
    public ArrayList<TransactionDTO> getByAccount(String accountId)
            throws SQLException, NotNullException {
        validateId(accountId);
        return dao.getByAccount(accountId);
    }

    // READ - all
    public ArrayList<TransactionDTO> getAll() throws SQLException {
        return dao.getAll();
    }

    private void validate(TransactionDTO t) throws NotNullException, NotInRangeException {
        if (t == null)
            throw new NotNullException("Transaction cannot be null.");
        if (t.getType() == null)
            throw new NotNullException("Transaction type cannot be null.");
        if (t.getAmount() <= 0)
            throw new NotInRangeException("Transaction amount must be greater than zero.");
        if (t.getFee() < 0)
            throw new NotInRangeException("Transaction fee cannot be negative.");
        if (t.getAlert() == null)
            throw new NotNullException("Transaction alert cannot be null.");
        if (t.getTransaction_date() == null)
            throw new NotNullException("Transaction date cannot be null.");
        if (t.getTransaction_date().isAfter(LocalDate.now()))
            throw new NotInRangeException("Transaction date cannot be in the future.");

        // At least one account must be involved
        boolean hasSource = t.getSource_account_id() != null && !t.getSource_account_id().trim().isEmpty();
        boolean hasTarget = t.getTarget_account_id() != null && !t.getTarget_account_id().trim().isEmpty();
        if (!hasSource && !hasTarget)
            throw new NotNullException("Transaction must have at least a source or target account.");
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