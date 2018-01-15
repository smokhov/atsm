package marf.Preprocessing;

import java.io.Serializable;
import java.util.Vector;

import marf.MARF;
import marf.Preprocessing.FFTFilter.LowPassFilter;
import marf.Storage.ModuleParams;
import marf.Storage.Sample;
import marf.Storage.StorageManager;
import marf.util.Arrays;
import marf.util.Debug;
import marf.util.NotImplementedException;


/**
 * <p>Abstract Preprocessing Module.</p>
 *
 * $Id: Preprocessing.java,v 1.51 2010/06/28 10:14:57 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.51 $
 * @since 0.0.1
 */
public abstract class Preprocessing
extends StorageManager
implements IPreprocessing
{
	/**
	 * Default amplitude value and below to what consider as a silence.
	 * Should be applied after normalization.
	 * @since 0.3.0.6 
	 */
	public final static double DEFAULT_SILENCE_THRESHOLD = 0.001;

	/**
	 * Sample reference.
	 */
	protected Sample oSample = null;

	/**
	 * Current silence removal threshold.
	 * @since 0.3.0.6
	 * @see #DEFAULT_SILENCE_THRESHOLD
	 */
	protected double dSilenceThreshold = DEFAULT_SILENCE_THRESHOLD; 

	/**
	 * By default we do not remove noise.
	 */
	protected boolean bRemoveNoise = false;

	/**
	 * By default we do not remove silence.
	 */
	protected boolean bRemoveSilence = false;

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.5
	 */
	private static final long serialVersionUID = 1311696194896319668L;

	
	/**
	 * Default constructor for reflective creation of Preprocessing
	 * clones. Typically should not be used unless really necessary
	 * for the frameworked modules.
	 * @since 0.3.0.5
	 */
	protected Preprocessing()
	{
	}

	/**
	 * Main Preprocessing constructor that performs normalization
	 * as a part of construction process.
	 *
	 * @param poSample loaded sample by a concrete implementation of SampleLoader
	 * @throws PreprocessingException if <code>normalize()</code> fails
	 * @see #normalize()
	 */
	protected Preprocessing(Sample poSample)
	throws PreprocessingException
	{
		super
		(
			poSample,

			new StringBuffer(poSample.getClass().getName()).append(".")
				.append(poSample.getAudioFileFormatCode()).append(".")
				.append(MARF.getPreprocessingMethod()).append(".sample")
				.toString()
		);

		this.oSample = poSample;

		extractParameters();

		//Debug.debug(Preprocessing.class, "constructed");
	}

	/**
	 * Allows <i>chaining</i> of preprocessing modules.
	 * Makes it possible to apply several preprocessing modules on the
	 * same incoming sample, in the form of
	 * <code>new FooBarBaz(new HighFrequencyBoost(new HighPassFilter(poSample)))</code>.
	 *
	 * Notice, it calls <code>preprocess()</code> for all inner preprocessing,
	 * but not the outer. The outer is supposed to be called by either <code>MARF</code>
	 * or an application as a part of defined API.
	 *
	 * @param poPreprocessing follow up preprocessing module
	 *
	 * @throws PreprocessingException if underlying <code>preprocess()</code> fails or
	 * the parameter is null.
	 *
	 * @since 0.3.0.3
	 *
	 * @see #preprocess()
	 * @see MARF
	 * @see marf.Preprocessing.FFTFilter.HighFrequencyBoost
	 * @see marf.Preprocessing.FFTFilter.HighPassFilter
	 */
	protected Preprocessing(IPreprocessing poPreprocessing)
	throws PreprocessingException
	{
		if(poPreprocessing == null)
		{
			throw new IllegalArgumentException("Preprocessing parameter cannot be null.");
		}

		boolean bChanged = poPreprocessing.preprocess();

		if(bChanged == false)
		{
			Debug.debug
			(
				"WARNING: " +
				poPreprocessing.getClass().getName() +
				".preprocess() returned false."
			);
		}

		this.oObjectToSerialize = this.oSample = poPreprocessing.getSample();

		extractParameters();
	}

	/**
	 * Default implementation of <code>preprocess()</code> includes
	 * normalization of the sample, and optionally removal of noise and
	 * silence.
	 *
	 * @return <code>true</code> if there are any changes made
	 * @throws PreprocessingException in case of any error
	 * @since 0.3.0.6
	 */
	public boolean preprocess()
	throws PreprocessingException
	{
		// Normalize prior doing anything
		boolean bChanges = normalize();

		// Remove noise, which may result in more silence gaps
		if(this.bRemoveNoise == true)
		{
			bChanges |= removeNoise();
		}

		// Remove all silence gaps
		if(this.bRemoveSilence == true)
		{
			bChanges |= removeSilence();
		}
		
		return bChanges;
	}

	/**
	 * Implements the noise in a relatively general default way by invoking the
	 * low-pass FFT filter with the default settings. Derivatives may override
	 * this default implementation the way they deem necessary.
	 * This default implementation of the noise removal appeared in 0.3.0.6,
	 * December 2007 and may be a subject for further parameterization.
	 *
	 * @return boolean that sample has changed (noise removed)
	 * @throws PreprocessingException declared but never thrown
	 * @see LowPassFilter
	 */
	public boolean removeNoise()
	throws PreprocessingException
	{
		LowPassFilter oNoiseRemover = new LowPassFilter(this.oSample);

		// These are needed to prevent indirect recursion of noise
		// and silence removal
		oNoiseRemover.bRemoveNoise = false;
		oNoiseRemover.bRemoveSilence = false;

		boolean bChanges = oNoiseRemover.preprocess();

		this.oSample.setSampleArray(oNoiseRemover.getSample().getSampleArray());
		oNoiseRemover = null;

		return bChanges;
	}

	/**
	 * Remove silence (amplitudes below certain threshold) from the sample
	 * thereby making it smaller and more unique compared to other samples.
	 *
	 * @return boolean that sample has changed (silence removed)
	 * @throws PreprocessingException in case of any error
	 * @see #removeSilence(double[], double)
	 * @see #dSilenceThreshold
	 */
	public boolean removeSilence()
	throws PreprocessingException
	{
		this.oSample.setSampleArray(removeSilence(this.oSample.getSampleArray(), this.dSilenceThreshold));
		return true;
	}

	/**
	 * Normalization of entire incoming samples by amplitude.
	 * Equivalent to <code>normalize(0)</code>.
	 *
	 * @return <code>true</code> if the sample has been successfully normalized;
	 * <code>false</code> otherwise
	 * @throws PreprocessingException if internal sample reference is null
	 * @see #normalize(int)
	 */
	public final boolean normalize()
	throws PreprocessingException
	{
		return normalize(0);
	}

	/**
	 * Normalization of incoming samples by amplitude starting from certain index.
	 * Useful in case where only the last portion of a sample needs to be normalized, like
	 * in <code>HighFrequencyBoost</code>.
	 *
	 * Equivalent to <code>normalize(piIndexFrom, sample array length - 1)</code>.
	 *
	 * @param piIndexFrom sample array index to start normalization from
	 *
	 * @return <code>true</code> if the sample has been successfully normalized;
	 * <code>false</code> otherwise
	 * @throws PreprocessingException if internal sample reference is null
	 * @since 0.3.0
	 *
	 * @see #normalize(int, int)
	 * @see marf.Preprocessing.FFTFilter.HighFrequencyBoost
	 */
	public final boolean normalize(int piIndexFrom)
	throws PreprocessingException
	{
		if(this.oSample == null)
		{
			throw new PreprocessingException
			(
				"Preprocessing.normalize(from) - sample is not available (null)"
			);
		}

		return normalize(piIndexFrom, this.oSample.getSampleArray().length - 1);
	}

	/**
	 * Normalization of incoming samples by amplitude between specified indexes.
	 * Useful in case where only a portion of a sample needs to be normalized.
	 *
	 * @param piIndexFrom sample array index to start normalization from
	 * @param piIndexTo sample array index to end normalization at
	 *
	 * @return <code>true</code> if the sample has been successfully normalized;
	 * <code>false</code> otherwise
	 * @throws PreprocessingException if internal sample reference is null or one or
	 * both indexes are out of range
	 * @since 0.3.0
	 */
	public final boolean normalize(int piIndexFrom, int piIndexTo)
	throws PreprocessingException
	{
		try
		{
			if(this.oSample == null)
			{
				throw new PreprocessingException
				(
					"Preprocessing.normalize(from, to) - sample is not available (null)"
				);
			}

			Debug.debug
			(
				"Preprocessing.normalize(" + piIndexFrom + "," +
				piIndexTo + ") has begun..."
			);

			double dMax = Double.MIN_VALUE;

			double[] adAmplitude = this.oSample.getSampleArray();

			// Find max absolute amplitude (peak)
			for(int i = piIndexFrom; i < piIndexTo; i++)
			{
				if(Math.abs(adAmplitude[i]) > dMax)
				{
					dMax = Math.abs(adAmplitude[i]);
				}
			}

			// Prevent devision by zero
			if(dMax == 0.0)
			{
				Debug.debug("NOTICE: Preprocessing.normalize() - dMax = " + dMax);
				return false;
			}

			// Actual normalization
			for(int i = piIndexFrom; i < piIndexTo; i++)
			{
				adAmplitude[i] /= dMax;
			}

			Debug.debug
			(
				"Preprocessing.normalize(" + piIndexFrom + "," + piIndexTo +
				") has normally finished..."
			);

			return true;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			throw new PreprocessingException
			(
				"Normalization period [" + piIndexFrom + "," + piIndexTo + "] is " +
				"out of range [0," + this.oSample.getSampleArray().length + "]."
			);
		}
	}

	/**
	 * Compresses input data by removing duplicate values.
	 * @param padDataVector source data vector to apply compression to
	 * @return a newly allocated compressed array of doubles;
	 * if the parameter is empty, it is returned instead
	 * @since 0.3.0.5
	 */
	public static double[] compress(final double[] padDataVector)
	{
		// Empty in, empty out.
		if(padDataVector.length == 0)
		{
			return padDataVector;
		}
		
		int iLastIndex = -1;
		
		// At least one element in the out would exist
		// (if all the elements in the input are the same)
		double[] adTempData = new double[padDataVector.length];
		adTempData[++iLastIndex] = padDataVector[0];
		
		for(int i = 1; i < padDataVector.length; i++)
		{
			if(padDataVector[i] != padDataVector[i - 1])
			{
				adTempData[++iLastIndex] = padDataVector[i]; 
			}
		}
		
		double[] adCompressed = new double[iLastIndex + 1];
		Arrays.copy(adCompressed, 0, adTempData, 0, adCompressed.length);

		return adCompressed;
	}
	
	/**
	 * Compresses input data by removing values below silence threshold.
	 * @param padDataVector source data vector to apply compression to
	 * @param pdSilenceThreshold the silence threshold below or equal to which amplitude values are ignored
	 * @return a newly allocated compressed array of doubles; may be empty if all input
	 * elements are below the threshold or input is empty
	 * @since 0.3.0.6
	 */
	public static double[] removeSilence(final double[] padDataVector, final double pdSilenceThreshold)
	{
		int iLastIndex = 0;
		
		double[] adTempData = new double[padDataVector.length];

		for(int i = 0; i < padDataVector.length; i++)
		{
			if(Math.abs(padDataVector[i]) > pdSilenceThreshold)
			{
				adTempData[iLastIndex++] = padDataVector[i]; 
			}
		}
		
		double[] adCompressed = new double[iLastIndex];
		Arrays.copy(adCompressed, 0, adTempData, 0, adCompressed.length);

		return adCompressed;
	}

	/**
	 * Compresses the instance of the stored sample by removing duplicates.
	 * @return <code>true</code>, if the compression was successful.
	 * @see #compress(double[])
	 * @since 0.3.0.5
	 */
	public synchronized boolean compress()
	{
		this.oSample.setSampleArray(compress(this.oSample.getSampleArray()));
		return true;
	}
	
	/**
	 * Derivatives implement this method to crop arbitrary
	 * part of the audio sample.
	 *
	 * @param pdStartingFrequency double Frequency to start to crop from
	 * @param pdEndFrequency double Frequency to crop the sample to
	 *
	 * @return boolean <code>true</code> - cropped, <code>false</code> - not
	 *
	 * @throws NotImplementedException
	 * @throws PreprocessingException declared, but is never thrown
	 */
	public boolean cropAudio(double pdStartingFrequency, double pdEndFrequency)
	throws PreprocessingException
	{
		throw new NotImplementedException(this, "cropAudio()");
	}

	/**
	 * Returns enclosed sample.
	 * @return Sample object
	 */
	public final Sample getSample()
	{
		return this.oSample;
	}

	/**
	 * Allows setting a sample object reference.
	 * @param poSample new sample object
	 * @since 0.3.0.4
	 */
	public void setSample(Sample poSample)
	{
		this.oSample = poSample;
	}

	/* StorageManager Interface */

	/**
	 * Implementation of back-synchronization of Sample loaded object.
	 * @since 0.3.0.2
	 */
	public void backSynchronizeObject()
	{
		this.oSample = (Sample)this.oObjectToSerialize;
	}

	/* Utility Methods */

	/**
	 * Implements Cloneable interface for the Preprocessing object.
	 * Performs "deep" copy, including the contained sample.
	 * @see java.lang.Object#clone()
	 * @since 0.3.0.5
	 */
	public Object clone()
	{
		Preprocessing oCopy = (Preprocessing)super.clone();

		oCopy.oSample =
			this.oSample == null ?
				null :
				(Sample)this.oSample.clone();

		return oCopy;
	}

	/**
	 * Performs general preprocessing module parameters extraction.
	 * E.g. parameters include the fact whether to remove noise and
	 * silence and the silence threshold. Typically invoked from
	 * constructors and is not meant for general public.
	 *
	 * @since 0.3.0.6
	 */
	protected void extractParameters()
	{
		// Extract any additional params if supplied

		ModuleParams oModuleParams = MARF.getModuleParams();

		if(oModuleParams != null)
		{
			Vector<Serializable> oParams = oModuleParams.getPreprocessingParams();

			if(oParams != null)
			{
				switch(oParams.size())
				{
					case 0:
						break;
					
					case 1:
						this.bRemoveNoise = ((Boolean)oParams.elementAt(0)).booleanValue();
						break;
						
					case 2:
						this.bRemoveNoise = ((Boolean)oParams.elementAt(0)).booleanValue();
						this.bRemoveSilence = ((Boolean)oParams.elementAt(1)).booleanValue();
						break;

					default:
						this.bRemoveNoise = ((Boolean)oParams.elementAt(0)).booleanValue();
						this.bRemoveSilence = ((Boolean)oParams.elementAt(1)).booleanValue();
						this.dSilenceThreshold = ((Double)oParams.elementAt(2)).doubleValue();
						break;
				}
			}
		}
	}
	
	/**
	 * Returns source code revision information.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.51 $";
	}
}

// EOF
