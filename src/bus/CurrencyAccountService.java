package bus;


import dao.CurrencyAccountDAO;
import dto.CurrencyAccountDTO;
import java.sql.SQLException;
import java.util.List;

public class CurrencyAccountService extends AccountService {

    private CurrencyAccountDAO dao = new CurrencyAccountDAO();

    // CREATE
    public CurrencyAccountDTO create(CurrencyAccountDTO dto) throws SQLException, NotNullException, NotInRangeException {
        validateAccount(dto);
        validateCurrency(dto);
        return dao.insert(dto);
    }

    // READ - by ID
    public CurrencyAccountDTO getById(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Account ID must be a positive number.");
        CurrencyAccountDTO dto = dao.getById(id);
        if (dto == null)
            throw new NotNullException("No currency account found with id: " + id);
        return dto;
    }

    // READ - all
    public List<CurrencyAccountDTO> getAll() throws SQLException {
        return dao.getAll();
    }

    // UPDATE
    public void update(CurrencyAccountDTO dto) throws SQLException, NotNullException, NotInRangeException {
        if (dto.getId() == null || dto.getId().trim().isEmpty())
            throw new NotNullException("Account ID cannot be null.");
        validateAccount(dto);
        validateCurrency(dto);
        dao.update(dto);
    }

    // DELETE
    public void delete(int id) throws SQLException, NotNullException {
        if (id <= 0)
            throw new NotNullException("Account ID must be a positive number.");
        dao.delete(id);
    }

    // CONVERT TO LOCAL — persists new local balance
    public void convertToLocal(CurrencyAccountDTO dto, double foreignAmount) throws SQLException, NotNullException, NotInRangeException {
        if (dto == null)
            throw new NotNullException("Currency account cannot be null.");
        if (foreignAmount <= 0)
            throw new NotInRangeException("Foreign amount must be greater than zero.");

        double rate    = Double.parseDouble(dto.getExchange_rate());
        double balance = Double.parseDouble(dto.getBalance());
        dto.setBalance(String.valueOf(balance + (foreignAmount * rate)));
        dao.convertToLocal(dto);
    }

    // CONVERT TO FOREIGN — persists new foreign balance
    public void convertToForeign(CurrencyAccountDTO dto, double localAmount) throws SQLException, NotNullException, NotInRangeException {
        if (dto == null)
            throw new NotNullException("Currency account cannot be null.");
        if (localAmount <= 0)
            throw new NotInRangeException("Local amount must be greater than zero.");

        double rate = Double.parseDouble(dto.getExchange_rate());
        if (rate == 0)
            throw new NotInRangeException("Exchange rate cannot be zero.");

        dto.setBalance_foreign_currency(String.format("%.2f", localAmount / rate));
        dao.convertToForeign(dto);
    }

    // SEE FOREIGN CURRENCY CONVERSION — pure calculation, no DB write
    public double seeForeignCurrencyConversion(CurrencyAccountDTO dto, double localAmount) throws NotNullException, NotInRangeException {
        if (dto == null)
            throw new NotNullException("Currency account cannot be null.");
        if (localAmount <= 0)
            throw new NotInRangeException("Local amount must be greater than zero.");
        return localAmount * Double.parseDouble(dto.getExchange_rate());
    }

    // Currency-specific validation
    private void validateCurrency(CurrencyAccountDTO dto) throws NotNullException, NotInRangeException {
        if (dto.getAccount_currency() == null || dto.getAccount_currency().trim().isEmpty())
            throw new NotNullException("Account currency cannot be null or empty.");
        if (dto.getAccount_currency().length() > 30)
            throw new NotInRangeException("Currency name cannot exceed 30 characters.");
        if (dto.getExchange_rate() == null || dto.getExchange_rate().trim().isEmpty())
            throw new NotNullException("Exchange rate cannot be null or empty.");

        double rate;
        try {
            rate = Double.parseDouble(dto.getExchange_rate());
        } catch (NumberFormatException e) {
            throw new NotInRangeException("Exchange rate must be a valid number.");
        }
        if (rate <= 0)
            throw new NotInRangeException("Exchange rate must be greater than zero.");
        if (rate > 9999.99)
            throw new NotInRangeException("Exchange rate cannot exceed 9999.99.");
    }
}