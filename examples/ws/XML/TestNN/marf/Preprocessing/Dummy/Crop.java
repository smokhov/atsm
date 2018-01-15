package marf.Preprocessing.Dummy;

import marf.Preprocessing.IPreprocessing;
import marf.Preprocessing.PreprocessingException;
import marf.Storage.Sample;
import marf.math.Vector;


/**
 * <p>Designed to be a general "cropper". It does no real preprocessing,
 * other than just cutting tails or heads or the middle of the sample
 * array for the fixed-length chunk.
 * 
 * While can be used as a regular preprocessing module (in which case
 * it is equivalent to <code>Raw</code>, except cropped), Crop meant to
 * be used in conjunction with other modules either through Preprocessing
 * pipeline concatenation or the implementation of the cropAudio() API.
 * 
 * If no parameters specified, by default cuts out the first 512 elements
 * of the sample array.
 * </p>
 *
 * @author Serguei Mokhov
 * @since 0.3.0.6
 * @version $Id: Crop.java,v 1.3 2011/11/21 17:48:17 mokhov Exp $
 */
public class Crop
extends Raw
{
	/**
	 * For serialization versioning. 
	 */
	private static final long serialVersionUID = 4268126048318774294L;

	/**
	 * Default amount of elements to keep in the resulting array.
	 */
	public static final int DEFAULT_CROP_CHUNK_SIZE = 512;
	
	/**
	 * Where to start cropping from, by default (chunk-size, i.e.
	 * preserving the elements from 0 to chunk-size - 1). 
	 */
	protected int iCropLeft = 0;

	/**
	 * 
	 */
	protected int iCropChunkSize = DEFAULT_CROP_CHUNK_SIZE;
	
	/**
	 * Where to stop cropping, by default "the rest of the sample".
	 */
	protected int iCropRight = DEFAULT_CROP_CHUNK_SIZE - 1;
	
	
	/**
	 * 
	 */
	public Crop()
	{
		super();
	}

	/**
	 * @param poPreprocessing
	 * @throws PreprocessingException
	 */
	public Crop(IPreprocessing poPreprocessing)
	throws PreprocessingException
	{
		super(poPreprocessing);
	}

	/**
	 * @param poSample
	 * @throws PreprocessingException
	 */
	public Crop(Sample poSample)
	throws PreprocessingException
	{
		super(poSample);
	}

	/* (non-Javadoc)
	 * @see marf.Preprocessing.Dummy.Raw#preprocess()
	 */
	public boolean preprocess()
	throws PreprocessingException
	{
		try
		{
			this.iCropRight
				= this.iCropRight > this.oSample.getSampleArray().length
				? this.oSample.getSampleArray().length
				: this.iCropRight; 

			Vector oSampleToCrop = new Vector(this.oSample.getSampleArray());
			oSampleToCrop.crop(this.iCropLeft, 0, this.iCropRight, 0);
			this.oSample.setSampleArray(oSampleToCrop.getMatrixArray());

			return true;
		}
		catch(Exception e)
		{
			throw new PreprocessingException(e);
		}
	}

	/**
	 * @return
	 */
	public int getCropStart()
	{
		return this.iCropLeft;
	}

	/**
	 * @param piCropStart
	 */
	public void setCropLeft(int piCropStart)
	{
		this.iCropLeft = piCropStart;
	}

	/**
	 * @return
	 */
	public int getCropRight()
	{
		return this.iCropRight;
	}

	/**
	 * @param piCropEnd
	 */
	public void setCropEnd(int piCropEnd)
	{
		this.iCropRight = piCropEnd;
	}

	/**
	 * @return
	 */
	public int getCropChunkSize()
	{
		return this.iCropChunkSize;
	}

	/**
	 * @param piCropChunkSize
	 */
	public void setCropChunkSize(int piCropChunkSize)
	{
		this.iCropChunkSize = piCropChunkSize;
	}
}

// EOF
