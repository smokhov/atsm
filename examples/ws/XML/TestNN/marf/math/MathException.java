package marf.math;

import marf.util.MARFException;

/**
 * <p>Indicates exceptional situations in MARF's math.</p>
 *
 * $Id: MathException.java,v 1.6 2007/12/18 03:45:42 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.6 $
 * @since 0.3.0.2
 */
public class MathException
extends MARFException
{
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = -5874565130017306067L;

	/**
	 * Encapsulation of another Exception object.
	 * @param poException Exception to wrap around
	 */
	public MathException(Exception poException)
	{
		super(poException);
	}

	/**
	 * Encapsulation of another Exception object and a new message.
	 * @param pstrMessage additional information to add
	 * @param poException Exception to wrap around
	 */
	public MathException(String pstrMessage, Exception poException)
	{
		super(pstrMessage, poException);
	}

	/**
	 * Generic exception.
	 * @param pstrMessage Error message string
	 */
	public MathException(String pstrMessage)
	{
		super(pstrMessage);
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.6 $";
	}
}

// EOF
