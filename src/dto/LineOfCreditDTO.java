package dto;


public class LineOfCreditDTO extends AccountDTO
{
    private String credit_limit;
    private String annual_interest_rate;
    private String number_of_installments;

    // --- Getters ---


    public String getCredit_limit()
    {
        return credit_limit;
    }

    public String getAnnual_interest_rate()
    {
        return annual_interest_rate;
    }

    public String getNumber_of_installments()
    {
        return number_of_installments;
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

    public void setNumber_of_installments(String number_of_installments)
    {
        this.number_of_installments = number_of_installments;
    }

    // --- Constructors ---

    public LineOfCreditDTO(String id, String customer_id, AccountStatus status, AccountType type, 
            String balance, String credit_limit, String annual_interest_rate, String number_of_installments)
    {
    	super(id, customer_id, status, type, balance);	
        this.credit_limit = credit_limit;
        this.annual_interest_rate = annual_interest_rate;
        this.number_of_installments = number_of_installments;
    }

   
    // --- toString ---

    @Override
	public String toString() {
		return super.toString() + "LineOfCreditDTO [credit_limit=" + credit_limit
				+ ", annual_interest_rate=" + annual_interest_rate + ", number_of_installments="
				+ number_of_installments + "]";
	}
}