package bus;


import dao.CheckingAccountDAO;
import dto.CheckingAccountDTO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CheckingAccountService extends  AccountService{

    private CheckingAccountDAO dao = new CheckingAccountDAO();

    // CREATE
    public CheckingAccountDTO create(CheckingAccountDTO dto) throws SQLException, NotNullException, NotInRangeException {
        validateAccount(dto);              // base validations from AccountService
        validateChecking(dto);             // checking-specific validations
        return dao.insert(dto);
    }

    // READ - by ID
    public CheckingAccountDTO getById(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Account ID must be a positive number.");
        CheckingAccountDTO dto = dao.getById(id);
        if (dto == null)
            throw new NotNullException("No checking account found with id: " + id);
        return dto;
    }

    // READ - all
    public List<CheckingAccountDTO> getAllCheckingAccounts() throws SQLException {
        return dao.getAll();
    }

    // UPDATE
    public void update(CheckingAccountDTO dto) throws SQLException, NotNullException, NotInRangeException {
        if (dto.getId() == null || dto.getId().trim().isEmpty())
            throw new NotNullException("Account ID cannot be null.");
        validateAccount(dto);
        validateChecking(dto);
        dao.update(dto);
    }

    // DELETE
    public void delete(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Account ID must be a positive number.");
        dao.delete(id);
    }

    // RESET monthly transactions counter to 3 and last_reset_date to start of month
    public void resetMonthlyTransactions(CheckingAccountDTO dto) throws SQLException, NotNullException {
        if (dto == null)
            throw new NotNullException("Checking account cannot be null.");
        dto.setFree_transactions_counter("3");
        dto.setLast_reset_date(LocalDate.now().withDayOfMonth(1));
        dao.resetMonthlyTransactions(Integer.parseInt(dto.getId()));
    }

    // APPLY TRANSACTION FEE — resets counter if new month, then deducts fee if needed
    public double applyTransactionFee(CheckingAccountDTO dto) throws SQLException, NotNullException, NotInRangeException {
        if (dto == null)
            throw new NotNullException("Checking account cannot be null.");

        LocalDate lastReset = dto.getLast_reset_date();
        LocalDate today     = LocalDate.now();

        if (today.getMonth() != lastReset.getMonth() || today.getYear() != lastReset.getYear()) {
            resetMonthlyTransactions(dto);
        }

        int counter = Integer.parseInt(dto.getFree_transactions_counter());
        double fee;

        if (counter > 0) {
            dto.setFree_transactions_counter(String.valueOf(counter - 1));
            fee = 0.0;
        } else {
            fee = Double.parseDouble(dto.getTransaction_fee());
            double balance = Double.parseDouble(dto.getBalance());
            if (balance < fee)
                throw new NotInRangeException("Insufficient balance to cover the transaction fee.");
            dto.setBalance(String.valueOf(balance - fee));
        }

        dao.applyTransactionFee(dto);
        return fee;
    }

    // Checking-specific validation
    private void validateChecking(CheckingAccountDTO dto) throws NotNullException, NotInRangeException {
        if (dto.getTransaction_fee() == null || dto.getTransaction_fee().trim().isEmpty())
            throw new NotNullException("Transaction fee cannot be null or empty.");
        if (dto.getLast_reset_date() == null)
            throw new NotNullException("Last reset date cannot be null.");

        double fee;
        try {
            fee = Double.parseDouble(dto.getTransaction_fee());
        } catch (NumberFormatException e) {
            throw new NotInRangeException("Transaction fee must be a valid number.");
        }
        if (fee < 0)
            throw new NotInRangeException("Transaction fee cannot be negative.");

        int counter;
        try {
            counter = Integer.parseInt(dto.getFree_transactions_counter());
        } catch (NumberFormatException e) {
            throw new NotInRangeException("Free transactions counter must be a valid integer.");
        }
        if (counter < 0 || counter > 9)
            throw new NotInRangeException("Free transactions counter must be between 0 and 9.");
    }
}