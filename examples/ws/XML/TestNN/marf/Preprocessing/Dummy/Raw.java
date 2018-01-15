package marf.Preprocessing.Dummy;

import marf.Preprocessing.IPreprocessing;
import marf.Preprocessing.PreprocessingException;
import marf.Storage.Sample;
import marf.util.Debug;


/**
 * <p>Implements raw preprocessing module for testing purposes
 * that does <b>not</b> do any preprocessing.</p>
 *
 * $Id: Raw.java,v 1.15 2007/12/16 01:11:05 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.15 $
 * @since 0.3.0.2
 */
public class Raw
extends Dummy
{
    /**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = 6915809043983583380L;

	/**
	 * Default constructor for reflective creation of Preprocessing
	 * clones. Typically should not be used unless really necessary
	 * for the frameworked modules.
	 * @since 0.3.0.5
	 */
	public Raw()
	{
		super();
	}

	/**
	 * Implementation of the preprocessing pipeline.
	 * @param poPreprocessing successor preprocessing module
	 * @throws PreprocessingException
	 */
	public Raw(IPreprocessing poPreprocessing)
	throws PreprocessingException
	{
		super(poPreprocessing);
		//Debug.debug("Raw constructed with preprocessing [" + poPreprocessing + "].");
	}

	/**
	 * Raw Constructor.
	 * @param poSample incoming sample
	 * @throws PreprocessingException
	 */
	public Raw(Sample poSample)
	throws PreprocessingException
	{
		super(poSample);
		//Debug.debug("Raw constructed with sample [" + poSample + "].");
	}

	/**
	 * Raw implementation of <code>preprocess()</code> for testing.
	 * Does not do any preprocessing.
	 * @return <code>true</code>
	 * @throws PreprocessingException never thrown
	 */
	public boolean preprocess()
	throws PreprocessingException
	{
		Debug.debug("Raw preprocess() done.");
		return true;
	}

	/**
	 * Raw implementation of <code>removeNoise()</code> for testing.
	 * Does not do any noise removal.
	 * @return <code>true</code>
	 * @throws PreprocessingException never thrown
	 */
	public final boolean removeNoise()
	throws PreprocessingException
	{
		Debug.debug(this.getClass().getName() + ".removeNoise()");
		return true;
	}

	/**
	 * Raw implementation of <code>removeSilence()</code> for testing.
	 * Does not do any silence removal.
	 * @return <code>true</code>
	 * @throws PreprocessingException never thrown
	 */
	public final boolean removeSilence()
	throws PreprocessingException
	{
		Debug.debug(this.getClass().getName() + ".removeSilence()");
		return true;
	}

	/**
	 * Raw implementation of <code>cropAudio()</code> for testing.
	 * @param pdStartingFrequency unused
	 * @param pdEndFrequency unused
	 * @return <code>true</code>
	 * @throws PreprocessingException never thrown
	 */
	public final boolean cropAudio(double pdStartingFrequency, double pdEndFrequency)
	throws PreprocessingException
	{
		Debug.debug(this.getClass().getName() + ".cropAudio()");
		return true;
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.15 $";
	}
}

// EOF
