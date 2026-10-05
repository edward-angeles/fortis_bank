package dto;
import java.time.LocalDate;


public class SavingsAccountDTO extends AccountDTO
{
    private String annual_interest_rate;
    private LocalDate last_interest_date;

    // --- Getters ---

    public String getAnnual_interest_rate()
    {
        return annual_interest_rate;
    }

    public LocalDate getLast_interest_date()
    {
        return last_interest_date;
    }

    // --- Setters ---

    public void setAnnual_interest_rate(String annual_interest_rate)
    {
        this.annual_interest_rate = annual_interest_rate;
    }

    public void setLast_interest_date(LocalDate last_interest_date)
    {
        this.last_interest_date = last_interest_date;
    }

    // --- Constructors ---
    public SavingsAccountDTO(String id, String customer_id, AccountStatus status, AccountType type, 
            String balance, String account_id, String annual_interest_rate)
    {
    	super(id, customer_id, status, type, balance);
        this.annual_interest_rate = annual_interest_rate;
        this.last_interest_date = LocalDate.now();
    }

    
    // --- toString ---

    @Override
	public String toString() {
		return super.toString() + "SavingsAccountDTO [annual_interest_rate=" + annual_interest_rate
				+ ", last_interest_date=" + last_interest_date + "]";
	}
}