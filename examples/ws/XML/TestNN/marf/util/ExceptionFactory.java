package marf.util;

import marf.Classification.ClassificationException;


/**
 * <p>Factory for producing various MARF exception objects.</p>
 *
 * $Id: ExceptionFactory.java,v 1.2 2007/12/01 00:31:01 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @since 0.3.0.6, Nov 26, 2007
 * @version $Revision: 1.2 $
 */
public class ExceptionFactory
{
	/**
	 * Factories are often singletons, so technically, this
	 * constructor should be private and the getInstance()
	 * method should be present. We relax this rule to allow
	 * inheritance to the wishing parties.  
	 */
	protected ExceptionFactory()
	{
		super();
	}

/*
 * XXX:
	public static IMARFException create(int piExceptionType)
	{
		switch(piExceptionType)
		{
			case 1:
			{
				return new MARFException();
			}

			case 2:
			{
				return new MARFRuntimeException();
			}
			
			default:
			{
				throw new MARFRuntimeException("Unknown exception type " + piExceptionType);
			}
		}
	}
*/
	
	/**
	 * Creates an instance of the MARFException object.
	 * @return default MARFException
	 * @see MARFException#MARFException()
	 */
	public static MARFException createMARFException()
	{
		return new MARFException();
	}

	/**
	 * Creates an instance of the MARFException object with a custom message.
	 * @param pstrMessage the custom message
	 * @return MARFException with the custom message
	 * @see MARFException#MARFException(String)
	 */
	public static MARFException createMARFException(String pstrMessage)
	{
		return new MARFException(pstrMessage);
	}

	/**
	 * Creates an instance of the MARFException object with a custom message and an exception object.
	 * @param pstrMessage the custom message
	 * @param poException the exception object to encapsulate
	 * @return MARFException with the custom message and the stack trace provided by the poException parameter
	 * @see MARFException#MARFException(String, Exception)
	 */
	public static MARFException createMARFException(String pstrMessage, Exception poException)
	{
		return new MARFException(pstrMessage, poException);
	}

	/**
	 * Creates an instance of the MARFException object.
	 * @param poException the exception object to encapsulate
	 * @return MARFException with the stack trace provided by the poException parameter
	 * @see MARFException#MARFException(Exception)
	 */
	public static MARFException createMARFException(Exception poException)
	{
		return new MARFException(poException);
	}

	
	public static MARFRuntimeException createMARFRuntimeException()
	{
		return new MARFRuntimeException();
	}

	public static MARFRuntimeException createMARFRuntimeException(String pstrMessage)
	{
		return new MARFRuntimeException(pstrMessage);
	}

	public static MARFRuntimeException createMARFRuntimeException(String pstrMessage, Exception poException)
	{
		return new MARFRuntimeException(pstrMessage, poException);
	}

	public static MARFRuntimeException createMARFRuntimeException(Exception poException)
	{
		return new MARFRuntimeException(poException);
	}


	public static ClassificationException createClassificationException()
	{
		return new ClassificationException();
	}

	public static ClassificationException createClassificationException(String pstrMessage)
	{
		return new ClassificationException(pstrMessage);
	}

	public static ClassificationException createClassificationException(String pstrMessage, Exception poException)
	{
		return new ClassificationException(pstrMessage, poException);
	}

	public static ClassificationException createClassificationException(Exception poException)
	{
		return new ClassificationException(poException);
	}
}

// EOF
