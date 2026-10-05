package bus;


import dao.CreditAccountDAO;
import dto.CreditAccountDTO;
import java.sql.SQLException;
import java.util.List;

public class CreditAccountService extends AccountService {

    private CreditAccountDAO dao = new CreditAccountDAO();

    // CREATE
    public CreditAccountDTO create(CreditAccountDTO dto) throws SQLException, NotNullException, NotInRangeException {
        validateAccount(dto);
        validateCredit(dto);
        return dao.insert(dto);
    }

    // READ - by ID
    public CreditAccountDTO getById(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Account ID must be a positive number.");
        CreditAccountDTO dto = dao.getById(id);
        if (dto == null)
            throw new NotNullException("No credit account found with id: " + id);
        return dto;
    }

    // READ - all
    public List<CreditAccountDTO> getAll() throws SQLException {
        return dao.getAll();
    }

    // UPDATE
    public void update(CreditAccountDTO dto) throws SQLException, NotNullException, NotInRangeException {
        if (dto.getId() == null || dto.getId().trim().isEmpty())
            throw new NotNullException("Account ID cannot be null.");
        validateAccount(dto);
        validateCredit(dto);
        dao.update(dto);
    }

    // DELETE
    public void delete(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Account ID must be a positive number.");
        dao.delete(id);
    }

    // BORROW
    public int borrow(CreditAccountDTO dto, double amount) throws SQLException, NotNullException, NotInRangeException {
        if (dto == null)
            throw new NotNullException("Credit account cannot be null.");
        if (amount <= 0)
            throw new NotInRangeException("Borrow amount must be greater than zero.");

        // Fetch fresh from DB to validate against real current state
        CreditAccountDTO latest = getById(Integer.parseInt(dto.getId()));
        double currentBalance = Double.parseDouble(latest.getBalance());
        double limit = Double.parseDouble(latest.getCredit_limit());

        if (currentBalance + amount > limit)
            throw new NotInRangeException("Transaction rejected: Insufficient credit available.");

        dao.borrow(Integer.parseInt(dto.getId()), amount); // DB handles the math
        return 1;
    }

    // REPAY
    public int repay(CreditAccountDTO dto, double amount) throws SQLException, NotNullException, NotInRangeException {
        if (dto == null)
            throw new NotNullException("Credit account cannot be null.");
        if (amount <= 0)
            throw new NotInRangeException("Repay amount must be greater than zero.");

    
       
     // SYNC: Ensure we don't repay more than the actual current debt
        CreditAccountDTO latest = getById(Integer.parseInt(dto.getId()));
        double currentBalance = Double.parseDouble(latest.getBalance());

        if (amount > currentBalance) {
        	 throw new NotInRangeException(String.format(
        	            "Transaction rejected: Cannot repay $%.2f because you only owe $%.2f.", 
        	            amount, currentBalance));
        }

        try {
        	
        dao.repay(Integer.parseInt(dto.getId()), amount);
        return 1;
        
        } catch (Exception e) {
            // 5. REVERT IF DATABASE FAILS
            // If the update() or the DAO failed, we must reset the DTO 
            // to match the last known state in the database.
            dto.setBalance(String.valueOf(currentBalance));
            throw e; 
        }
    }

    // CHECK CREDIT AVAILABLE — pure calculation, no DB write
 // CHECK CREDIT AVAILABLE
    public double checkCreditAvailable(CreditAccountDTO dto) throws SQLException, NotNullException {
        CreditAccountDTO latest = getById(Integer.parseInt(dto.getId()));
        double balance = Double.parseDouble(latest.getBalance());
        double limit = Double.parseDouble(latest.getCredit_limit());
        
        return limit - balance;
    }

    // APPLY MONTHLY INTEREST — returns 1 if applied, -1 if not yet due
    public void applyMonthlyInterest(CreditAccountDTO dto) throws SQLException, NotNullException {
        CreditAccountDTO latest = getById(Integer.parseInt(dto.getId()));
        double balance = Double.parseDouble(latest.getBalance());
        
        if (balance <= 0) return;

        double annualRate = Double.parseDouble(latest.getAnnual_interest_rate());
        double monthlyInterest = balance * (annualRate / 100 / 12);
        
        dto.setBalance(String.valueOf(balance + monthlyInterest));
        dao.applyMonthlyInterest(dto);
    }

    public double calculateMonthlyPayment(CreditAccountDTO dto) throws SQLException, NotNullException {
        CreditAccountDTO latest = getById(Integer.parseInt(dto.getId()));
        double balance = Double.parseDouble(latest.getBalance());
        double rate = Double.parseDouble(latest.getAnnual_interest_rate());

        // Standard 3% of balance or interest-based minimum
        double minPayment = (balance * (rate / 100) / 12);
        return Math.round(minPayment * 100) / 100.0;
    }
    // Credit-specific validation
    private void validateCredit(CreditAccountDTO dto) throws NotNullException, NotInRangeException {
        if (dto.getCredit_limit() == null || dto.getCredit_limit().trim().isEmpty())
            throw new NotNullException("Credit limit cannot be null or empty.");
        if (dto.getAnnual_interest_rate() == null || dto.getAnnual_interest_rate().trim().isEmpty())
            throw new NotNullException("Annual interest rate cannot be null or empty.");
        if (dto.getMin_monthly_payment() == null || dto.getMin_monthly_payment().trim().isEmpty())
            throw new NotNullException("Minimum monthly payment cannot be null or empty.");
        if (dto.getPayment_due_date() == null)
            throw new NotNullException("Payment due date cannot be null.");
        if (dto.getLast_interest_date() == null)
            throw new NotNullException("Last interest date cannot be null.");

        double limit, rate, minPayment, currentBalance;
        try {
            limit          = Double.parseDouble(dto.getCredit_limit());
            rate           = Double.parseDouble(dto.getAnnual_interest_rate());
            minPayment     = Double.parseDouble(dto.getMin_monthly_payment());
            currentBalance = Double.parseDouble(dto.getBalance()); // Get the balance
        } catch (NumberFormatException e) {
            throw new NotInRangeException("Credit limit, interest rate, balance, and min payment must be valid numbers.");
        }
        if (limit <= 0 || limit > 999999.99)
            throw new NotInRangeException("Credit limit must be between 0 and 999,999.99.");
        if (rate < 0 || rate > 22)
            throw new NotInRangeException("Annual interest rate must be between 0% and 22%.");
        if (minPayment < 0)
            throw new NotInRangeException("Minimum monthly payment cannot be negative.");
        if (currentBalance > limit)
            throw new NotInRangeException("Balance owed ($" + currentBalance + 
                ") cannot exceed the credit limit ($" + limit + ").");
        if (limit <= 0 || limit > 999999.99)
            throw new NotInRangeException("Credit limit must be between 0 and 999,999.99.");
        if (rate < 0 || rate > 22)
            throw new NotInRangeException("Annual interest rate must be between 0% and 22%.");
    }
}
