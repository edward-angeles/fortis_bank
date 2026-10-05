package bus;

public class InsufficientFundsException extends Exception{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InsufficientFundsException() {
	        super("Insufficient funds.");
	    }

	    public InsufficientFundsException(String message) 
	    {
	        super(message);
	    }
}
