package marf.Preprocessing;

import marf.Storage.Sample;


/**
 * <p>Filter class implements generic filtering.</p>
 * <p>Derivatives must implement filter API based on the type of filter
 * they are and override <code>preprocess()</code> as needed.</p>
 *
 * @author Serguei Mokhov
 * @version $Id: Filter.java,v 1.2 2011/11/21 20:47:05 mokhov Exp $
 * @since 0.3.0.6
 */
public abstract class Filter
extends Preprocessing
implements IFilter
{
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 */
	private static final long serialVersionUID = -2938260317576401518L;

	/**
	 * Default constructor for reflective creation of Preprocessing
	 * clones. Typically should not be used unless really necessary
	 * for the frameworked modules.
	 */
	public Filter()
	{
		super();
	}

	/**
	 * Pipelined constructor.
	 * @param poPreprocessing the follow-up preprocessing module
	 * @throws PreprocessingException
	 */
	public Filter(IPreprocessing poPreprocessing)
	throws PreprocessingException
	{
		super(poPreprocessing);
	}

	/**
	 * Filter Constructor.
	 * @param poSample incoming sample
	 * @throws PreprocessingException
	 */
	public Filter(Sample poSample)
	throws PreprocessingException
	{
		super(poSample);
	}

	/**
	 * Filter implementation of <code>preprocess()</code>.
	 * <p>It does call <code>removeNoise()</code> and <code>removeSilence()</code>
	 * if they were explicitly requested by an app <em>after</em> applying 
	 * the primary filtering algorithm.</p>
	 *
	 * <b>NOTE</b>: it alters inner Sample by resetting its data array to the new
	 * filtered values.
	 *
	 * @return <code>true</code> if there was something filtered out
	 * @throws PreprocessingException if the frequency response is null
	 */
	public boolean preprocess()
	throws PreprocessingException
	{
		// Normalize all first
		boolean bChanges = normalize();

		double[] adSampleData = this.oSample.getSampleArray();
		double[] adFilteredData = new double[adSampleData.length];

		// Perform whatever filtering there maybe to do
		// (may not at all be related to filtering out noise, etc.).
		// May result in some "silence" gaps.
		bChanges |= filter(adSampleData, adFilteredData);

		// Return the filtered data back to the stored sample
		this.oSample.setSampleArray(adFilteredData);

		// Remove noise, which may result in more silence gaps.
		// XXX: consider moving this (or as an option?) before the filter() call
		// XXX: need to experiment more with this.
		if(this.bRemoveNoise == true)
		{
			bChanges |= removeNoise();
		}

		// Remove all the silence gaps if requested
		if(this.bRemoveSilence == true)
		{
			bChanges |= removeSilence();
		}

		return bChanges;
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
