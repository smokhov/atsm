package marf.util;


/**
 * <p>This class extends RuntimeException for MARF specifics.</p>
 *
 * @author Serguei Mokhov
 * @version $Id: MARFRuntimeException.java,v 1.3 2012/07/18 02:45:45 mokhov Exp $
 * @since 0.3.0.6
 */
public class MARFRuntimeException
extends RuntimeException
//implements IMARFException
{
	/**
	 * Our own error message container.
	 * Needed as we don't have access to the parent's.
	 * Initially an empty string.
	 */
	protected String strMessage = "";

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 */
	private static final long serialVersionUID = 3675298315402480553L;

	/**
	 * Default MARF exception.
	 * Better be overridden for normal internal message.
	 */
	public MARFRuntimeException()
	{
		this("Just a Run-time MARF Exception");
	}

	/**
	 * Generic exception.
	 * @param pstrMessage Error message string
	 */
	public MARFRuntimeException(String pstrMessage)
	{
		super(pstrMessage);
		this.strMessage = pstrMessage;
	}

	/**
	 * This is used for debug purposes only with some unusual Exception's.
	 * It allows the originating Exceptions stack trace to be returned.
	 * @param pstrMessage Error message string
	 * @param poException Exception object to dump
	 */
	public MARFRuntimeException(String pstrMessage, Exception poException)
	{
		super(pstrMessage);
		this.strMessage = ExceptionUtils.getStackTraceAsString(pstrMessage, poException);
	}

	/**
	 * Wraps Exception object around.
	 * @param poException Exception to wrap around
	 */
	public MARFRuntimeException(Exception poException)
	{
		this(poException.getMessage(), poException);
	}

	/**
	 * Returns string representation of the error message.
	 * @return error string
	 */
	public String getMessage()
	{
		return this.strMessage;
	}

	/**
	 * Override <code>toString()</code> to display our message.
	 * @return string representation of this exception
	 */
	public String toString()
	{
		return getMessage();
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.3 $";
	}
}

// EOF
