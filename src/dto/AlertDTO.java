package dto;

public class AlertDTO
{
    // Attributes
    private String id;
	private String message;
    private AlertType alertType;
    private String bmc_id;

    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public AlertType getAlertType() {
		return alertType;
	}

	public void setAlertType(AlertType alertType) {
		this.alertType = alertType;
	}

	public String getBmc_id() {
		return bmc_id;
	}

	public void setBmc_id(String bmc_id) {
		this.bmc_id = bmc_id;
	}
    

    // Constructor with parameters
    public AlertDTO(String id, String message, AlertType type, String recipient)
    {
        this.id = id;
        this.message = message;
        this.alertType = type;
        this.bmc_id = recipient;
    }

	@Override
	public String toString() {
		return "AlertDTO [id=" + id + ", message=" + message + ", alertType=" + alertType + ", bmc_id=" + bmc_id + "]";
	}

}