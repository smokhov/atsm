package marf.Preprocessing.FFTFilter;

import marf.Preprocessing.IPreprocessing;
import marf.Preprocessing.PreprocessingException;
import marf.Storage.Sample;
import marf.util.Debug;


/**
 * <p>Band-stop Filter Implementation based on the FFTFilter.</p>
 *
 * $Id: BandStopFilter.java,v 1.1 2007/12/16 07:14:52 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.1 $
 * @since 0.3.0.6
 * @see FFTFilter
 */
public class BandStopFilter
extends FFTFilter
{
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 */
	private static final long serialVersionUID = -8413024016319719417L;

	/**
	 * Default constructor for reflective creation of Preprocessing
	 * clones. Typically should not be used unless really necessary
	 * for the frameworked modules.
	 */
	public BandStopFilter()
	{
		super();
	}

	/**
	 * Implements preprocessing pipeline.
	 * @param poPreprocessing follow up preprocessing module
	 * @throws PreprocessingException
	 */
	public BandStopFilter(IPreprocessing poPreprocessing)
	throws PreprocessingException
	{
		super(poPreprocessing);
	}

	/**
	 * BandpassFilter Constructor.
	 * @param poSample incoming sample
	 * @throws PreprocessingException
	 */
	public BandStopFilter(Sample poSample)
	throws PreprocessingException
	{
		super(poSample);
	}

	/**
	 * Stub implementation of <code>cropAudio()</code>.
	 * @param pdStartingFrequency unused
	 * @param pdEndFrequency unused
	 * @return <code>false</code>
	 * @throws PreprocessingException never thrown
	 */
	public final boolean cropAudio(double pdStartingFrequency, double pdEndFrequency)
	throws PreprocessingException
	{
		Debug.debug("BandStopFilter.cropAudio()");
		return false;
	}

	/**
	 * Creates band-pass frequency response coefficients and sets applies
	 * them to the frequency response vector.
	 */
	public void generateResponseCoefficients()
	{
		double[] adResponse = new double[DEFAULT_FREQUENCY_RESPONSE_SIZE];

		// Note: Frequencies kept: ~= 1000Hz - 2853Hz
		for(int i = 0; i < adResponse.length; i++)
		{
			adResponse[i] = (i >= 25 && i <= 70) ? 0 : 1;
		}

		setFrequencyResponse(adResponse);
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.1 $";
	}
}

// EOF
