package bus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.InputMismatchException;

public class Validator {
	
	// Validar que un string tenga solo dígitos (para PIN, número de cuenta, etc.)
	public static void isDigit(String value) throws InputMismatchException 
	{

        for (int i = 0; i < value.length(); i++) 
        {
            if (!Character.isDigit(value.charAt(i))) 
            {
                throw new InputMismatchException("Input must contain digits only.");
            }
        } 
	}
	
	// Validar que un string tenga solo letras (para nombres)
    public static void isAlphabetLetter(String value) throws InputMismatchException 
    {

        for (int i = 0; i < value.length(); i++) 
        {
            if (!Character.isLetter(value.charAt(i))) 
            {
                throw new InputMismatchException("Input must contain letters only.");
            }
        }
    }
    
 // Validar formato básico de email
    public static void isValidEmail(String email) throws InputMismatchException 
    {

        if (!email.contains("@") || !email.contains(".")) 
        {
            throw new InputMismatchException("Invalid email format.");
        }
    }
    
    //validate Pin have 4 numbers
    public static void isValidPin(String pin) throws InputMismatchException 
    {
        isDigit(pin);

        if (pin.length() != 4) {
            throw new InputMismatchException("PIN must be exactly 4 digits.");
        }
    }
    
    //validate int
    public static void isInteger(int value) throws InputMismatchException 
    {
    	if (value < 0) 
    	{
            throw new InputMismatchException("Value must be a positive integer.");
        }
    }
    
 
    //validate double
    public static void isDouble(double value) throws InputMismatchException 
    {
        
    	if (value % 1 == 0) 
    	{
    	    throw new InputMismatchException("Value must have decimal part.");
    	}

        if (value < 0) 
        {
            throw new InputMismatchException("Double value cannot be negative.");
        }
    }

    //validate date
    public static void isDate(LocalDateTime date) throws InputMismatchException 
    {
        if (date == null) 
        {
            throw new InputMismatchException("Date cannot be null.");
        }

        // no permitir fechas futuras
        if (date.isAfter(LocalDateTime.now())) 
        {
            throw new InputMismatchException("Date cannot be in the future.");
        }
    }
    
    //validate date LocalDateTime
    public static void isLocalDate(LocalDate date) throws InputMismatchException 
    {
        if (date == null) 
        {
            throw new InputMismatchException("Date cannot be null.");
        }

        // no permitir fechas futuras
        if (date.isAfter(LocalDate.now())) 
        {
            throw new InputMismatchException("Date cannot be in the future.");
        }
    }
}
