package dto;

import java.time.LocalDate;
import java.util.ArrayList;


public abstract class AccountDTO 
{
	private String id;
    private String customer_id;
    private AccountStatus status; //ids rule
    private AccountType type; //ids rule
    private String balance;
    private LocalDate opening_date;
	private ArrayList<TransactionDTO> transactions; 
	
    public AccountDTO(String id, String customer_id, AccountStatus status, AccountType type, String balance)
    {
    	this.id = id;
        this.customer_id = customer_id;
        this.status = status;
        this.type = type;
        this.balance = balance;
        this.opening_date = LocalDate.now();
        this.transactions = new ArrayList<TransactionDTO>();
    }
	
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	public AccountType getType() {
		return type;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public LocalDate getOpening_date() {
		return opening_date;
	}

	public void setOpening_date(LocalDate opening_date) {
		this.opening_date = opening_date;
	}

	public ArrayList<TransactionDTO> getTransactions() {
		return transactions;
	}

	public void setTransactions(ArrayList<TransactionDTO> transactions) {
		this.transactions = transactions;
	}

    // --- toString ---

    @Override
    public String toString()
    {
        return this.getId() + " " + this.getCustomer_id() + " " + this.getStatus() +  "" + this.getType() + " " + this.getBalance() + " " + this.getOpening_date() + " "+ this.getTransactions();
    }
}