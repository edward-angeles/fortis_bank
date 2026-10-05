package bus;

public class NotNullException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotNullException() 
	{
        super("Value cannot be null or empty.");
    }

    public NotNullException(String message) 
    {
        super(message);
    }
}
