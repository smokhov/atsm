package marf.util;


/**
 * <p>Class MARFException.</p>
 * <p>This class extends Exception for MARF specifics.</p>
 *
 * $Id: MARFException.java,v 1.21 2007/12/23 06:29:47 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.21 $
 * @since 0.0.1
 */
public class MARFException
extends Exception
implements IMARFException
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
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = -36051376447916491L;

	/**
	 * Default MARF exception.
	 * Better be overridden for normal internal message.
	 * @since 0.3.0.3
	 */
	public MARFException()
	{
		this("Just a MARF Exception");
	}

	/**
	 * Generic exception.
	 * @param pstrMessage Error message string
	 */
	public MARFException(String pstrMessage)
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
	public MARFException(String pstrMessage, Exception poException)
	{
		super(pstrMessage);
		this.strMessage = ExceptionUtils.getStackTraceAsString(pstrMessage, poException);
	}

	/**
	 * Wraps Exception object around.
	 * @param poException Exception to wrap around
	 * @since 0.3.0.2
	 */
	public MARFException(Exception poException)
	{
		this(poException.getMessage(), poException);
	}

	/**
	 * Returns string representation of the error message.
	 * Definalize in 0.3.0.6 to allow derivatives.
	 * @return error string
	 */
	public String getMessage()
	{
		return this.strMessage;
	}

	/* (non-Javadoc)
	 * @see marf.util.IMARFException#create()
	 */
	public IMARFException create()
	{
		return create(null, null);
	}

	/* (non-Javadoc)
	 * @see marf.util.IMARFException#create(java.lang.Exception)
	 */
	public IMARFException create(Exception poException)
	{
		return create(null, poException);
	}

	/* (non-Javadoc)
	 * @see marf.util.IMARFException#create(java.lang.String)
	 */
	public IMARFException create(String pstrMessage)
	{
		return create(pstrMessage, null);
	}

	/**
	 * @see marf.util.IMARFException#create(java.lang.String, java.lang.Exception)
	 */
	public IMARFException create(String pstrMessage, Exception poException)
	{
		if(pstrMessage == null && poException == null)
		{
			return new MARFException();
		}
		else if(pstrMessage == null && poException != null)
		{
			return new MARFException(poException);
		}
		else if(pstrMessage != null && poException == null)
		{
			return new MARFException(pstrMessage);
		}
		else if(pstrMessage != null && poException != null)
		{
			return new MARFException(pstrMessage, poException);
		}

		return null;
	}

	/**
	 * Override <code>toString()</code> to display our message.
	 * @return string representation of this exception
	 * @since 0.3.0.3
	 */
	public String toString()
	{
		return getMessage();
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.21 $";
	}
}

// EOF
