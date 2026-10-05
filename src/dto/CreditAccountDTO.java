package dto;
import java.time.LocalDate;

public class CreditAccountDTO extends AccountDTO
{
    private String credit_limit;
    private String annual_interest_rate;
    private String min_monthly_payment;
    private LocalDate payment_due_date;
    private LocalDate last_interest_date;

    // --- Getters ---

    public String getCredit_limit()
    {
        return credit_limit;
    }

    public String getAnnual_interest_rate()
    {
        return annual_interest_rate;
    }

    public String getMin_monthly_payment()
    {
        return min_monthly_payment;
    }

    public LocalDate getPayment_due_date()
    {
        return payment_due_date;
    }

    public LocalDate getLast_interest_date()
    {
        return last_interest_date;
    }

    // --- Setters ---

    public void setCredit_limit(String credit_limit)
    {
        this.credit_limit = credit_limit;
    }

    public void setAnnual_interest_rate(String annual_interest_rate)
    {
        this.annual_interest_rate = annual_interest_rate;
    }

    public void setMin_monthly_payment(String min_monthly_payment)
    {
        this.min_monthly_payment = min_monthly_payment;
    }

    public void setPayment_due_date(LocalDate payment_due_date)
    {
        this.payment_due_date = payment_due_date;
    }

    public void setLast_interest_date(LocalDate last_interest_date)
    {
        this.last_interest_date = last_interest_date;
    }

    // --- Constructors ---

    public CreditAccountDTO(String id, String customer_id, AccountStatus status, AccountType type, 
            String balance, String credit_limit, String annual_interest_rate,
    		String min_monthly_payment, LocalDate payment_due_date)
    {
    	super(id, customer_id, status, type, balance);
        this.credit_limit = credit_limit;
        this.annual_interest_rate = annual_interest_rate;
        this.min_monthly_payment = min_monthly_payment;
        this.payment_due_date = payment_due_date;
        this.last_interest_date = LocalDate.now();
    }

    // --- toString ---

    @Override
	public String toString() {
		return  super.toString() + "|CreditAccountDTO [credit_limit=" + credit_limit
				+ ", annual_interest_rate=" + annual_interest_rate + ", min_monthly_payment=" + min_monthly_payment
				+ ", payment_due_date=" + payment_due_date + ", last_interest_date=" + last_interest_date + "]";
	}
}