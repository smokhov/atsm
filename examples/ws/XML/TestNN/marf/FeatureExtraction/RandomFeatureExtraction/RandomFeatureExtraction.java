package marf.FeatureExtraction.RandomFeatureExtraction;

import java.util.Random;

import marf.FeatureExtraction.FeatureExtraction;
import marf.FeatureExtraction.FeatureExtractionException;
import marf.Preprocessing.IPreprocessing;
import marf.Storage.Sample;
import marf.util.Arrays;


/**
 * <p>Implementation of random feature extraction for testing as a baseline.</p>
 *
 * $Id: RandomFeatureExtraction.java,v 1.18 2007/12/18 03:45:42 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.18 $
 * @since 0.2.0
 */
public class RandomFeatureExtraction
extends FeatureExtraction
{
	/**
	 * Default number (256) of doubles per chunk in a feature vector.
	 */
	public static final int DEFAULT_CHUNK_SIZE = 256;

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = -5469714962808143269L;

	/**
	 * RandomFeatureExtraction Constructor.
	 * @param poPreprocessing Preprocessing object reference
	 */
	public RandomFeatureExtraction(IPreprocessing poPreprocessing)
	{
		super(poPreprocessing);

		// XXX: ModuleParams
	}

	/**
	 * Random Gaussian-based feature extraction.
	 * Sample is taken from an IPreprocessing module from the pipeline.
	 *
	 * @return <code>true</code> if successful
	 * @throws FeatureExtractionException in case of any error
	 */
	public final boolean extractFeatures()
	throws FeatureExtractionException
	{
		return extractFeaturesImplementation(this.oPreprocessing.getSample());
	}

	/**
	 * Extracts features from the provided sample array.
	 * @see marf.FeatureExtraction.IFeatureExtraction#extractFeatures(double[])
	 * @since 0.3.0.6
	 */
	public final boolean extractFeatures(double[] padSampleData)
	throws FeatureExtractionException
	{
		return extractFeaturesImplementation(new Sample(padSampleData));
	}

	/**
	 * Does the actual business logic of the random Gaussian feature extraction.
	 * @param poSample sample to extract features from
	 * @return <code>true</code> if there were features extracted, <code>false</code> otherwise
	 * @throws FeatureExtractionException in case of any errors while doing stuff
	 * @since 0.3.0.6
	 */
	protected final boolean extractFeaturesImplementation(Sample poSample)
	throws FeatureExtractionException
	{
		try
		{
			double[] adChunk = new double[DEFAULT_CHUNK_SIZE];
			this.adFeatures  = new double[DEFAULT_CHUNK_SIZE];

			int iDataRecv = poSample.getNextChunk(adChunk);

			while(iDataRecv > 0)
			{
				for(int i = 0; i < DEFAULT_CHUNK_SIZE; i++)
				{
					this.adFeatures[i] += adChunk[i] * (new Random(i).nextGaussian());
				}

				iDataRecv = poSample.getNextChunk(adChunk);

				// Padding to ^2 for the last chunk
				if(iDataRecv < DEFAULT_CHUNK_SIZE && iDataRecv > 0)
				{
					Arrays.fill(adChunk, iDataRecv, DEFAULT_CHUNK_SIZE - 1, 0);
					iDataRecv = 0;
				}
			}

			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			throw new FeatureExtractionException(e);
		}
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.18 $";
	}
}

// EOF
