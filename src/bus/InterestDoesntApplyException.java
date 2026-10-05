package bus;

public class InterestDoesntApplyException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InterestDoesntApplyException() {
        super("No outstanding balance, interest does not apply.");
    }

    public InterestDoesntApplyException(String message) {
        super(message);
    }
}
