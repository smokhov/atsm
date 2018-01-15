package marf.Preprocessing.Dummy;

import marf.Preprocessing.IPreprocessing;
import marf.Preprocessing.Preprocessing;
import marf.Preprocessing.PreprocessingException;
import marf.Storage.Sample;


/**
 * <p>Implements dummy preprocessing module for testing purposes
 * that does only normalization.</p>
 *
 * $Id: Dummy.java,v 1.27 2007/12/16 01:11:05 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.27 $
 * @since 0.0.1
 */
public class Dummy
extends Preprocessing
{
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = -8360158324170431628L;

	/**
	 * Default constructor for reflective creation of Preprocessing
	 * clones. Typically should not be used unless really necessary
	 * for the frameworked modules.
	 * @since 0.3.0.5
	 */
	public Dummy()
	{
		super();
	}

	/**
	 * Implementation of the preprocessing pipeline.
	 * @param poPreprocessing successor preprocessing module
	 * @throws PreprocessingException
	 * @since 0.3.0.3
	 */
	public Dummy(IPreprocessing poPreprocessing)
	throws PreprocessingException
	{
		super(poPreprocessing);
	}

	/**
	 * Dummy Constructor.
	 * @param poSample incoming sample
	 * @throws PreprocessingException
	 */
	public Dummy(Sample poSample)
	throws PreprocessingException
	{
		super(poSample);
	}

	/**
	 * Dummy implementation of <code>preprocess()</code> for testing
	 * that doesn't do any real work and simply defers the call to the
	 * generic implementation.
	 * @return <code>true</code> if preprocessing occurred and changes to the sample were made
	 * @throws PreprocessingException
	 */
	public boolean preprocess()
	throws PreprocessingException
	{
		return super.preprocess();
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.27 $";
	}
}

// EOF
