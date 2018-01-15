package marf.Preprocessing;

import marf.util.MARFException;

/**
 * <p>Class PreprocessingException indicates an error in one of the preprocessing
 * modules if an error happens somewhere throughout the process.</p>
 *
 * $Id: PreprocessingException.java,v 1.11 2006/09/03 21:29:33 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.11 $
 * @since 0.0.1
 */
public class PreprocessingException
extends MARFException
{
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = 7005439039328507785L;

	/**
	 * Default preprocessing exception with empty message. 
	 * @since 0.3.0.5
	 */
	public PreprocessingException()
	{
		super();
	}

	/**
	 * Preprocessing exception.
	 * @param pstrMessage Error message string
	 */
	public PreprocessingException(String pstrMessage)
	{
		super(pstrMessage);
	}

	/**
	 * Preprocessing exception with wrapped exception of another type.
	 * @param poException the exception to wrap
	 * @since 0.3.0.5
	 */
	public PreprocessingException(Exception poException)
	{
		super(poException);
	}

	/**
	 * Preprocessing exception with wrapped exception of another type
	 * and a customized error message.
	 *
	 * @param pstrMessage the customized message
	 * @param poException the exception to wrap
	 * @since 0.3.0.5
	 */
	public PreprocessingException(String pstrMessage, Exception poException)
	{
		super(pstrMessage, poException);
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.11 $";
	}
}

// EOF
