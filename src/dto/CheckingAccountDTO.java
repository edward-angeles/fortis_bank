package dto;

import java.time.LocalDate;


public class CheckingAccountDTO extends AccountDTO
{
//    private String account_id;
    private String free_transactions_counter;
    private String transaction_fee;
    private LocalDate last_reset_date;

    // --- Getters ---

    public String getFree_transactions_counter()
    {
        return free_transactions_counter;
    }

    public String getTransaction_fee()
    {
        return transaction_fee;
    }

    public LocalDate getLast_reset_date()
    {
        return last_reset_date;
    }

    // --- Setters ---

    public void setFree_transactions_counter(String free_transactions_counter)
    {
        this.free_transactions_counter = free_transactions_counter;
    }

    public void setTransaction_fee(String transaction_fee)
    {
        this.transaction_fee = transaction_fee;
    }

    public void setLast_reset_date(LocalDate last_reset_date)
    {
        this.last_reset_date = last_reset_date;
    }

    // --- Constructors ---

    public CheckingAccountDTO(String id, String customer_id, AccountStatus status, AccountType type, 
            String balance, String transaction_fee, LocalDate last_reset_date)
    {
    	super(id, customer_id, status, type, balance);
        this.free_transactions_counter = "3";
        this.transaction_fee = transaction_fee;
        this.last_reset_date = last_reset_date;
    }
    
    // --- toString ---

    @Override
	public String toString() {
		return super.toString() + "| CheckingAccountDTO [free_transactions_counter="
				+ free_transactions_counter + ", transaction_fee=" + transaction_fee + ", last_reset_date="
				+ last_reset_date + "]";
	}
}