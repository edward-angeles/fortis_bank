package dto;

import java.util.ArrayList;

public class CustomerDTO 
{

    private String id;
    private String first_name;
    private String last_name;
    private String pin;
    private String email;
    private String phone;

    // List of the customer's accounts — used in memory (not stored in DB directly)
    private ArrayList<AccountDTO> accountList = new ArrayList<AccountDTO>();

    // --- Getters ---

    public String getId()
    {
        return id;
    }

    public String getFirst_name()
    {
        return first_name;
    }

    public String getLast_name()
    {
        return last_name;
    }

    public String getPin()
    {
        return pin;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPhone()
    {
        return phone;
    }

    public ArrayList<AccountDTO> getAccountList()
    {
        return accountList;
    }

    // --- Setters ---

    public void setId(String id)
    {
        this.id = id;
    }

    public void setFirst_name(String first_name)
    {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name)
    {
        this.last_name = last_name;
    }

    public void setPin(String pin)
    {
        this.pin = pin;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public void setAccountList(ArrayList<AccountDTO> accountList)
    {
        this.accountList = accountList;
    }

    public CustomerDTO(String id, String first_name, String last_name, String pin, String email, String phone)
    {
    	this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.pin = pin;
        this.email = email;
        this.phone = phone;
    }

	@Override
	public String toString() {
		return "CustomerDTO [id=" + id + ", first_name=" + first_name + ", last_name=" + last_name + ", pin=" + pin
				+ ", email=" + email + ", phone=" + phone + ", accountList=" + accountList + "]";
	}

  
}