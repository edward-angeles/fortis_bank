package dto;


public class CurrencyAccountDTO extends AccountDTO
{
    private String account_currency;
    private String exchange_rate;
    private String balance_foreign_currency;

    // --- Getters ---

    public String getAccount_currency()
    {
        return account_currency;
    }

    public String getExchange_rate()
    {
        return exchange_rate;
    }

    public String getBalance_foreign_currency()
    {
        return balance_foreign_currency;
    }

    // --- Setters ---

    public void setAccount_currency(String account_currency)
    {
        this.account_currency = account_currency;
    }

    public void setExchange_rate(String exchange_rate)
    {
        this.exchange_rate = exchange_rate;
    }

    public void setBalance_foreign_currency(String balance_foreign_currency)
    {
        this.balance_foreign_currency = balance_foreign_currency;
    }

    // --- Constructor ---

    public CurrencyAccountDTO(String id, String customer_id, AccountStatus status, AccountType type, 
            String balance, String account_id, String account_currency, String exchange_rate)
    {
    	super(id, customer_id, status, type, balance);

        this.account_currency = account_currency;
        this.exchange_rate = exchange_rate;
        this.balance_foreign_currency = "0.00";
    }

    @Override
	public String toString() {
		return  super.toString() + "|CurrencyAccountDTO [account_currency=" + account_currency
				+ ", exchange_rate=" + exchange_rate + ", balance_foreign_currency=" + balance_foreign_currency + "]";
	}
}