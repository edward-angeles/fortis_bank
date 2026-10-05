package bus;

public class NotInRangeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotInRangeException ()
	{
		super("Value is out of the allowed range.");
	}
	
	public NotInRangeException(String message)
	{
		super(message);
	}
}
