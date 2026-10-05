package bus;


import dao.LineOfCreditDAO;
import dto.LineOfCreditDTO;
import java.sql.SQLException;
import java.util.List;

public class LineOfCreditAccountService extends AccountService {

    private LineOfCreditDAO dao = new LineOfCreditDAO();

    // CREATE
    public LineOfCreditDTO create(LineOfCreditDTO dto) throws SQLException, NotNullException, NotInRangeException {
        validateAccount(dto);
        validateLineOfCredit(dto);
        return dao.insert(dto);
    }

    // READ - by ID
    public LineOfCreditDTO getById(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Account ID must be a positive number.");
        LineOfCreditDTO dto = dao.getById(id);
        if (dto == null)
            throw new NotNullException("No line of credit found with id: " + id);
        return dto;
    }

    // READ - all
    public List<LineOfCreditDTO> getAll() throws SQLException {
        return dao.getAll();
    }

    // UPDATE
    public void update(LineOfCreditDTO dto) throws SQLException, NotNullException, NotInRangeException {
        if (dto.getId() == null || dto.getId().trim().isEmpty())
            throw new NotNullException("Account ID cannot be null.");
        validateAccount(dto);
        validateLineOfCredit(dto);
        dao.update(dto);
    }

    // DELETE
    public void delete(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Account ID must be a positive number.");
        dao.delete(id);
    }
   
    // BORROW — High-level action: Calculate -> Validate -> Targeted Save
    public int borrow(LineOfCreditDTO dto, double amount) throws SQLException, NotNullException, NotInRangeException {
        if (amount <= 0) throw new NotInRangeException("Borrow amount must be greater than 0.");
        if (dto == null) throw new NotNullException("Line of credit cannot be null.");
     // FIX: Always fetch the LATEST data from the DB before doing math
        LineOfCreditDTO latestData = getById(Integer.parseInt(dto.getId()));
        
        double currentBalance = Double.parseDouble(latestData.getBalance());
        double limit = Double.parseDouble(latestData.getCredit_limit());
        double newBalance = currentBalance + amount;

        if (newBalance > limit) {
        	throw new NotInRangeException(String.format(
                    "Transaction failed: Borrowing $%.2f would exceed your limit of $%.2f.", 
                    amount, limit));
        }

        dao.borrow(Integer.parseInt(dto.getId()), amount); // DB does balance + amount
        applyInterest(dto);                                // interest on top after borrow
        // Update the DTO with the combined total
        dto.setBalance(String.valueOf(newBalance));
        return 1;
    }

 // REPAY — Fetches latest balance, reduces debt, returns success message
    public int repay(LineOfCreditDTO dto, double amount) throws SQLException, NotNullException, NotInRangeException {
        if (dto == null) throw new NotNullException("Line of credit cannot be null.");
        if (amount <= 0) throw new NotInRangeException("Repay amount must be greater than zero.");

        // FIX: Get latest balance to ensure we don't repay more than what is actually owed
        LineOfCreditDTO latest = getById(Integer.parseInt(dto.getId()));
        double currentBalance = Double.parseDouble(latest.getBalance());

        if (amount > currentBalance) {
            throw new NotInRangeException(String.format(
                "Transaction rejected: Cannot repay $%.2f because you only owe $%.2f.", 
                amount, currentBalance));
        }
        
        dao.repay(Integer.parseInt(dto.getId()), amount); // DB does balance - amount
        dto.setBalance(String.valueOf(currentBalance - amount));
        return 1;
    }

    
 // GET MIN MONTHLY PAYMENT — Now refreshes data to ensure accurate calculation
    public double getMinMonthlyPayment(LineOfCreditDTO dto) throws SQLException, NotNullException {
        if (dto == null)
            throw new NotNullException("Line of credit cannot be null.");

        // FIX: Fetch latest balance and rate from DB
        LineOfCreditDTO latest = getById(Integer.parseInt(dto.getId()));
        
        double balance = Double.parseDouble(latest.getBalance());
        double rate    = Double.parseDouble(latest.getAnnual_interest_rate());
        
        // Calculate interest-only minimum payment
        return Math.round((balance * (rate / 100) / 12) * 100) / 100.0;
    }

    // CALCULATE MONTHLY PAYMENT — Now refreshes data to ensure accurate calculation
    public double calculateMonthlyPayment(LineOfCreditDTO dto) throws SQLException, NotNullException {
        if (dto == null)
            throw new NotNullException("Line of credit cannot be null.");

        // FIX: Fetch latest balance, rate, and installments from DB
        LineOfCreditDTO latest = getById(Integer.parseInt(dto.getId()));
        
        double balance      = Double.parseDouble(latest.getBalance());
        double rate         = Double.parseDouble(latest.getAnnual_interest_rate());
        int installments    = Integer.parseInt(latest.getNumber_of_installments());
        
        if (balance <= 0) return 0.0;

        double monthlyRate = rate / 100 / 12;
        
        // Amortization formula
        double payment = balance * monthlyRate / (1 - Math.pow(1 + monthlyRate, -installments));
        
        return Math.round(payment * 100) / 100.0;
    }

    
 // APPLY INTEREST — Now refreshes data and persists the new balance
    public void applyInterest(LineOfCreditDTO dto) throws SQLException, NotNullException {
        if (dto == null)
            throw new NotNullException("Line of credit cannot be null.");

        // FIX: Fetch the absolute latest balance and rate from the database
        LineOfCreditDTO latest = getById(Integer.parseInt(dto.getId()));
        
        double currentBalance = Double.parseDouble(latest.getBalance());
        
        // Only apply interest if there is actually a debt
        if (currentBalance <= 0) return;

        double rate = Double.parseDouble(latest.getAnnual_interest_rate());
        
        // Calculate one month of interest: (Balance * AnnualRate / 100 / 12 months)
        double interest = currentBalance * (rate / 100 / 12);
        double newBalance = currentBalance + interest;

        // Update the DTO with the interest-added balance
        dto.setBalance(String.valueOf(newBalance));

        try {
            // Persist the new balance back to the database
            dao.applyInterest(dto);
        } catch (SQLException e) {
            // Revert memory state if the database update fails
            dto.setBalance(String.valueOf(currentBalance));
            throw e;
        }
    }

    // Line of credit-specific validation
    private void validateLineOfCredit(LineOfCreditDTO dto) throws NotNullException, NotInRangeException {
        if (dto.getCredit_limit() == null || dto.getCredit_limit().trim().isEmpty())
            throw new NotNullException("Credit limit cannot be null or empty.");
        if (dto.getAnnual_interest_rate() == null || dto.getAnnual_interest_rate().trim().isEmpty())
            throw new NotNullException("Annual interest rate cannot be null or empty.");
        if (dto.getNumber_of_installments() == null || dto.getNumber_of_installments().trim().isEmpty())
            throw new NotNullException("Number of installments cannot be null or empty.");

        double limit, rate;
        int installments;
        try {
            limit        = Double.parseDouble(dto.getCredit_limit());
            rate         = Double.parseDouble(dto.getAnnual_interest_rate());
            installments = Integer.parseInt(dto.getNumber_of_installments());
        } catch (NumberFormatException e) {
            throw new NotInRangeException("Credit limit, interest rate, and installments must be valid numbers.");
        }
        if (limit <= 0 || limit > 999999.99)
            throw new NotInRangeException("Credit limit must be between 0 and 999,999.99.");
        if (rate < 0 || rate > 22)
            throw new NotInRangeException("Annual interest rate must be between 0% and 22%.");
        if (installments < 1 || installments > 99)
            throw new NotInRangeException("Number of installments must be between 1 and 99.");
    }
}
