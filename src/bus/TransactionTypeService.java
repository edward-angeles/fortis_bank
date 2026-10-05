package bus;

import dao.TransactionTypeDAO;
import dto.TransactionType;
import java.util.ArrayList;

public class TransactionTypeService {

    private TransactionTypeDAO dao = new TransactionTypeDAO();

    // CREATE
    public int create(TransactionType type) throws NotNullException {
        validate(type);
        int result = dao.add(type);
        if (result == -1)
            throw new NotNullException("Failed to create transaction type. Please try again.");
        return result;
    }

    // READ - by ID
    public TransactionType getById(int id) throws NotNullException {
        if (id <= 0)
            throw new NotNullException("Transaction type ID must be a positive number.");
        TransactionType type = dao.getById(id);
        if (type == null)
            throw new NotNullException("No transaction type found with id: " + id);
        return type;
    }

    // READ - all
    public ArrayList<TransactionType> getAll() {
        return dao.getAll();
    }

    // UPDATE
    public int update(TransactionType type) throws NotNullException {
        if (type.getId() <= 0)
            throw new NotNullException("Transaction type ID must be a positive number.");
        validate(type);
        // Verify it exists before updating
        TransactionType existing = dao.getById(type.getId());
        if (existing == null)
            throw new NotNullException("No transaction type found with id: " + type.getId());
        int result = dao.update(type);
        if (result == -1)
            throw new NotNullException("Failed to update transaction type. Please try again.");
        return result;
    }

    // DELETE
    public int delete(int id) throws NotNullException {
        if (id <= 0)
            throw new NotNullException("Transaction type ID must be a positive number.");
        // Prevent deletion of core types (deposit=1, withdrawal=2, transfer=3)
        if (id <= 3)
            throw new NotNullException("Core transaction types (deposit, withdrawal, transfer) cannot be deleted.");
        TransactionType existing = dao.getById(id);
        if (existing == null)
            throw new NotNullException("No transaction type found with id: " + id);
        int result = dao.delete(id);
        if (result == -1)
            throw new NotNullException("Failed to delete transaction type. Please try again.");
        return result;
    }

    private void validate(TransactionType type) throws NotNullException {
        if (type == null)
            throw new NotNullException("Transaction type cannot be null.");
        if (type.getName() == null || type.getName().trim().isEmpty())
            throw new NotNullException("Transaction type name cannot be null or empty.");
        if (type.getName().length() > 30)
            throw new NotNullException("Transaction type name cannot exceed 30 characters.");
        if (type.getDescription() == null || type.getDescription().trim().isEmpty())
            throw new NotNullException("Transaction type description cannot be null or empty.");
        if (type.getDescription().length() > 250)
            throw new NotNullException("Transaction type description cannot exceed 250 characters.");
    }
}