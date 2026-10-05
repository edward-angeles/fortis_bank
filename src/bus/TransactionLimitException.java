package bus;

public class TransactionLimitException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransactionLimitException() 
	{
        super("Transaction limit exceeded: only 3 free transactions allowed per month.");
    }

    public TransactionLimitException(String message) 
    {
        super(message);
    }
}
