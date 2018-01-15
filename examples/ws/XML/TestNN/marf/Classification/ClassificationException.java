package marf.Classification;

import marf.util.MARFException;


/**
 * <p>Class ClassificationException indicates an error
 * during classification process.</p>
 *
 * $Id: ClassificationException.java,v 1.12 2007/11/30 15:58:25 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.12 $
 * @since 0.0.1
 */
public class ClassificationException
extends MARFException
{
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = -1088263414931478219L;

	/**
	 * Constructs a default classification exception with the message
	 * the same as the class name.
	 * @since 0.3.0.6
	 */
	public ClassificationException()
	{
		super(ClassificationException.class.getName());
	}

	/**
	 * Generic exception.
	 * @param pstrMessage Error message string
	 */
	public ClassificationException(String pstrMessage)
	{
		super(pstrMessage);
	}

	/**
	 * Replicates parent's constructor.
	 * @param poException Exception object to encapsulate
	 */
	public ClassificationException(Exception poException)
	{
		super(poException);
	}

	/**
	 * Replicates parent's constructor.
	 * @param pstrMessage Error message string
	 * @param poException Exception object to encapsulate
	 */
	public ClassificationException(String pstrMessage, Exception poException)
	{
		super(pstrMessage, poException);
	}

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.12 $";
	}
}

// EOF
