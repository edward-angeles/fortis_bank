package dto;

import java.time.LocalDate;

public class TransactionDTO
{
    private String id;
    private TransactionType type;
    private double amount;
    private LocalDate transaction_date;
    private String source_account_id;
    private String target_account_id;
    private double fee;
    private AlertDTO alert;

    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public TransactionType getType() {
		return type;
	}
	public void setType(TransactionType type) {
		this.type = type;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public LocalDate getTransaction_date() {
		return transaction_date;
	}
	public void setTransaction_date(LocalDate transaction_date) {
		this.transaction_date = transaction_date;
	}
	public String getSource_account_id() {
		return source_account_id;
	}
	public void setSource_account_id(String source_account_id) {
		this.source_account_id = source_account_id;
	}
	public String getTarget_account_id() {
		return target_account_id;
	}
	public void setTarget_account_id(String target_account_id) {
		this.target_account_id = target_account_id;
	}
	public double getFee() {
		return fee;
	}
	public void setFee(double fee) {
		this.fee = fee;
	}
	public AlertDTO getAlert() {
		return alert;
	}
	public void setAlert(AlertDTO alert) {
		this.alert = alert;
	}
	public TransactionDTO(String id, TransactionType type, double amount, LocalDate transaction_date,
            String source_account_id, String target_account_id, double fee, AlertDTO alert)
    {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.transaction_date = transaction_date;
        this.source_account_id = source_account_id;
        this.target_account_id = target_account_id;
        this.fee = fee;
        this.alert = alert;
    }
	@Override
	public String toString() {
		return "TransactionDTO [id=" + id + ", type=" + type + ", amount=" + amount + ", transaction_date="
				+ transaction_date + ", source_account_id=" + source_account_id + ", target_account_id="
				+ target_account_id + ", fee=" + fee + ", alert=" + alert + "]";
	}

  
}