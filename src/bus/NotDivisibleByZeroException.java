package bus;

public class NotDivisibleByZeroException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotDivisibleByZeroException() 
    {
        super("division is never by zero.");
    }

    public NotDivisibleByZeroException(String message) 
    {
        super(message);
    }
}
