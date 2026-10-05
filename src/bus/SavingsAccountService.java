package bus;

import dao.SavingsAccountDAO;
import dto.SavingsAccountDTO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class SavingsAccountService extends AccountService {

    private SavingsAccountDAO dao = new SavingsAccountDAO();

    // CREATE
    public SavingsAccountDTO create(SavingsAccountDTO dto) throws SQLException, NotNullException, NotInRangeException {
        validateAccount(dto);
        validateSavings(dto);
        return dao.insert(dto);
    }

    // READ - by ID
    public SavingsAccountDTO getById(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Account ID must be a positive number.");
        SavingsAccountDTO dto = dao.getById(id);
        if (dto == null)
            throw new NotNullException("No savings account found with id: " + id);
        return dto;
    }

    // READ - all
    public List<SavingsAccountDTO> getAllSavingAccounts() throws SQLException {
        return dao.getAll();
    }

    // UPDATE
    public void update(SavingsAccountDTO dto) throws SQLException, NotNullException, NotInRangeException {
        if (dto.getId() == null || dto.getId().trim().isEmpty())
            throw new NotNullException("Account ID cannot be null.");
        validateAccount(dto);
        validateSavings(dto);
        dao.update(dto);
    }

    // DELETE
    public void delete(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Account ID must be a positive number.");
        dao.delete(id);
    }

    // CALCULATE ANNUAL GAIN — pure calculation, no DB write
    public double calculateAnnualGain(SavingsAccountDTO dto) throws NotNullException {
        if (dto == null)
            throw new NotNullException("Savings account cannot be null.");
        double balance = Double.parseDouble(dto.getBalance());
        double rate    = Double.parseDouble(dto.getAnnual_interest_rate());
        return balance * (rate / 100);
    }

    // APPLY ANNUAL INTEREST — returns 1 if applied, -1 if not yet due
    public int applyAnnualInterest(SavingsAccountDTO dto) throws SQLException, NotNullException {
        if (dto == null)
            throw new NotNullException("Savings account cannot be null.");

        LocalDate lastInterest = dto.getLast_interest_date();
        if (LocalDate.now().isBefore(lastInterest.plusYears(1)))
            return -1; // not yet due

        double gain    = calculateAnnualGain(dto);
        double balance = Double.parseDouble(dto.getBalance());
        dto.setBalance(String.valueOf(balance + gain));
        dto.setLast_interest_date(LocalDate.now());

        dao.applyAnnualInterest(dto);
        return 1;
    }

    // Savings-specific validation
    private void validateSavings(SavingsAccountDTO dto) throws NotNullException, NotInRangeException {
        if (dto.getAnnual_interest_rate() == null || dto.getAnnual_interest_rate().trim().isEmpty())
            throw new NotNullException("Annual interest rate cannot be null or empty.");
        if (dto.getLast_interest_date() == null)
            throw new NotNullException("Last interest date cannot be null.");

        double rate;
        try {
            rate = Double.parseDouble(dto.getAnnual_interest_rate());
        } catch (NumberFormatException e) {
            throw new NotInRangeException("Annual interest rate must be a valid number.");
        }
        if (rate < 0 || rate > 22)
            throw new NotInRangeException("Annual interest rate must be between 0% and 22%.");
    }
}
