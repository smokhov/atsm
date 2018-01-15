package marf.gui;

import marf.util.MARFException;


/**
 * <p>GUIException is a root of exception hierarchy in the
 * exception processing of the GUI-related modules of MARF.
 * </p>
 *
 * $Id: GUIException.java,v 1.2 2007/12/31 00:17:05 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.2 $
 * @since 0.3.0.6
 */
public class GUIException
extends MARFException
{
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 */
	private static final long serialVersionUID = -2165583195552754018L;

	/**
	 * Default GUI exception.
	 */
	public GUIException()
	{
		super();
	}

	/**
	 * Generic exception.
	 * @param pstrMessage Error message string
	 */
	public GUIException(String pstrMessage)
	{
		super(pstrMessage);
	}

	/**
	 * Exception wrapper constructor.
	 * @param poException Exception object to wrap
	 */
	public GUIException(Exception poException)
	{
		super(poException);
	}

	/**
	 * Accepts custom message and the exception object.
	 * @param pstrMessage custom error message
	 * @param poException exception happened
	 */
	public GUIException(String pstrMessage, Exception poException)
	{
		super(pstrMessage, poException);
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.2 $";
	}
}

// EOF
