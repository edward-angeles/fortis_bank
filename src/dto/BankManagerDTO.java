package dto;

public class BankManagerDTO
{
    // Attributes
    private String managerId;
    private String firstName;
    private String lastName;
    private String email;
    private JobStatus status;

    // Getters & Setters
    public String getManagerId()
    {
        return managerId;
    }

    public void setManagerId(String managerId)
    {
        this.managerId = managerId;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public JobStatus getStatus()
    {
        return status;
    }

    public void setStatus(JobStatus status)
    {
        this.status = status;
    }

    // Constructor with parameters
    public BankManagerDTO(String managerId, String firstName, String lastName, String email, JobStatus status)
    {
        this.managerId = managerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
    }

	@Override
	public String toString() {
		return "BankManagerDTO [managerId=" + managerId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + ", status=" + status + "]";
	}
}