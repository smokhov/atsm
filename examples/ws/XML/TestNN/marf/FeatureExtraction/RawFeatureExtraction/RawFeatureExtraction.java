package marf.FeatureExtraction.RawFeatureExtraction;

import marf.FeatureExtraction.FeatureExtraction;
import marf.FeatureExtraction.FeatureExtractionException;
import marf.Preprocessing.IPreprocessing;
import marf.Preprocessing.Dummy.Crop;
import marf.Storage.Sample;
import marf.util.Arrays;


/**
 * For some applications it is desired to pass-through the
 * preprocessed sample data "as-is", or with very little "massaging";
 * this module does that. Note, unlike other feature extractors,
 * in one of its modes of operation, RawFeatureExtraction allows
 * arbitrary-length output in the pass-through mode, with which
 * far not all classifiers can cope with. 
 * 
 * @author Serguei Mokhov
 * @since 0.3.0.6
 * @version $Id: RawFeatureExtraction.java,v 1.4 2012/07/18 16:00:20 mokhov Exp $
 */
public class RawFeatureExtraction
extends FeatureExtraction
{
	/**
	 * For serialization versioning.
	 */
	private static final long serialVersionUID = 3373111942101382658L;

	public static final int MODE_RAW_PASS_THROUGH = 0;

	public static final int MODE_CROP_PASS_THROUGH = 1;

	/**
	 * Expand to the chunk size if shorter
	 * (guarantees fixed-length output).
	 */
	public static final int MODE_CROP_PASS_THROUGH_PADDED = 2;

	public static final int MODE_ADD_MOD = 3;

	protected int iMode = MODE_CROP_PASS_THROUGH;
	
	/**
	 * @param poPreprocessing
	 */
	public RawFeatureExtraction(IPreprocessing poPreprocessing)
	{
		super(poPreprocessing);
	}

	/**
	 * @param padSampleData
	 * @since 0.3.0.6, July 2012; MARFPCAT
	 */
	public RawFeatureExtraction(double[] padSampleData)
	{
		super(null);
		this.adFeatures = padSampleData;
	}

	/* (non-Javadoc)
	 * @see marf.FeatureExtraction.IFeatureExtraction#extractFeatures(double[])
	 */
	public boolean extractFeatures(double[] padSampleData)
	throws FeatureExtractionException
	{
		try
		{
			switch(this.iMode)
			{
				case MODE_RAW_PASS_THROUGH:
				{
					//this.adFeatures = new double[padSampleData.length];
					//Arrays.copy(this.adFeatures, 0, padSampleData, 0);
					this.adFeatures = padSampleData;
					return true;
				}

				case MODE_CROP_PASS_THROUGH:
				{
					Crop oCrop = new Crop(new Sample(padSampleData));
					boolean bChanges = oCrop.preprocess();
					this.adFeatures = oCrop.getSample().getSampleArray();
					return bChanges | this.adFeatures.length != 0;
				}

				case MODE_CROP_PASS_THROUGH_PADDED:
				{
					Crop oCrop = new Crop(new Sample(padSampleData));
					boolean bChanges = oCrop.preprocess();
					this.adFeatures = new double[oCrop.getCropChunkSize()];
					Arrays.copy(this.adFeatures, 0, oCrop.getSample().getSampleArray(), 0);
					return bChanges | this.adFeatures.length != 0;
				}
			}
		}
		catch(Exception e)
		{
			throw new FeatureExtractionException(e);
		}

		return false;
	}

	/**
	 * @return
	 * @since 0.3.0.6, July 2012; MARFPCAT
	 */
	public int getMode()
	{
		return this.iMode;
	}

	/**
	 * @param piMode
	 * @since 0.3.0.6, July 2012; MARFPCAT
	 */
	public void setMode(int piMode)
	{
		this.iMode = piMode;
	}
}

// EOF
