package marf.Storage;

import java.io.IOException;
import java.io.Serializable;

import javax.sound.sampled.AudioFormat;

import marf.util.Arrays;
import marf.util.Debug;
import marf.util.InvalidSampleFormatException;


/**
 * <p>Sample data container.</p>
 *
 * <p>The Sample Bean can contain data samples from audio, images, text, or
 * any other data as a collection of double values. While started with Audio
 * processing, it is not confined to audio data.
 * </p>
 *
 * $Id: Sample.java,v 1.51 2009/02/22 02:16:01 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @author Jimmy Nicolacopoulos
 *
 * @version $Revision: 1.51 $
 * @since 0.0.1
 */
public class Sample
implements Serializable, Cloneable
{
	/*
	 * -------------------
	 * Sample meta-data
	 * -------------------
	 */

	/**
	 * Grouping of file format data.
	 * @since 0.3.0.2
	 */
	protected transient MARFAudioFileFormat oAudioFileFormat = null;

	/**
	 * Local format code.
	 * @since 0.3.0.6
	 */
	private int iFormat = MARFAudioFileFormat.WAV;

	/**
	 * Default sample array's size (1024).
	 * @since 0.3.0.2
	 */
	public static final int DEFAULT_SAMPLE_SIZE = 1024;

	/**
	 * Default sample chunk's size (128).
	 * @since 0.3.0.2
	 */
	public static final int DEFAULT_CHUNK_SIZE = 128;

	/*
	 * -------------------
	 * Data members
	 * -------------------
	 */

	/**
	 * Sample data array (amplitudes).
	 */
	protected double[] adSample = null;

	/**
	 * Chunk pointer in the sample array.
	 */
	protected transient int iArrayIndex = 0;

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = 505671131094530371L;


	/*
	 * -------------------
	 * Methods
	 * -------------------
	 */

	/**
	 * Constructs default sample object.
	 */
	public Sample()
	{
		try
		{
			setAudioFileFormatCode(MARFAudioFileFormat.WAV);
		}
		catch(InvalidSampleFormatException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Accepts pre-set sample; for testing.
	 * @param padData preset amplitude values
	 */
	public Sample(double[] padData)
	{
		try
		{
			setAudioFileFormatCode(MARFAudioFileFormat.WAV);
			setSampleArray(padData);
		}
		catch(InvalidSampleFormatException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Constructor with format indication.
	 * @param piFormat format number for the enumeration
	 * @throws InvalidSampleFormatException if the parameter format is invalid
	 */
	public Sample(final int piFormat)
	throws InvalidSampleFormatException
	{
		setAudioFileFormatCode(piFormat);
	}

	/**
	 * Constructor with format indication and the sampled data.
	 * @param piFormat format number for the enumeration
	 * @param padData preset amplitude values
	 * @throws InvalidSampleFormatException if the parameter format is invalid
	 * @since 0.3.0.5
	 */
	public Sample(final int piFormat, double[] padData)
	throws InvalidSampleFormatException
	{
		setAudioFileFormatCode(piFormat);
		setSampleArray(padData);
	}

	/**
	 * Copy-constructor. Prior getting parameter's data,
	 * it's cloned. Only the data and format are copied.
	 *
	 * @param poSample sample object to copy data off from
	 * @throws InvalidSampleFormatException if the parameter format is invalid
	 * @since 0.3.0.5
	 */
	public Sample(final Sample poSample)
	throws InvalidSampleFormatException
	{
		Sample oCopy = (Sample)poSample.clone();
		setAudioFileFormatCode(oCopy.iFormat);
		setSampleArray(oCopy.adSample);
	}

	/**
	 * Retrieves current sample's format.
	 * @return an integer representing the format of the sample
	 * @since 0.3.0.6
	 */
	public synchronized int getAudioFileFormatCode()
	{
		return this.iFormat;
	}

	/**
	 * Retrieves current sample's audio format data.
	 * In 0.3.0.6 was changed to return AudioFormat instead of int;
	 * a replacement API of getAudioFileFormatCode() was added.
	 * @return an AudioFormat representing the format of the sample
	 * @see #getAudioFileFormatCode()
	 */
	public synchronized AudioFormat getAudioFormat()
	{
		return this.oAudioFileFormat.getFormat();
	}

	/**
	 * Sets current format of a sample.
	 * In 0.3.0.6 was renamed to match getAudioFileFormatCode().
	 * @param piFileFormat format number from the enumeration
	 * @throws InvalidSampleFormatException if the parameter format is invalid
	 * @see #getAudioFileFormatCode()
	 */
	public synchronized void setAudioFileFormatCode(final int piFileFormat)
	throws InvalidSampleFormatException
	{
		// E.g. during deserialization
		if(this.oAudioFileFormat == null)
		{
			this.oAudioFileFormat = new MARFAudioFileFormat(piFileFormat);
		}
		else
		{
			this.oAudioFileFormat.setAudioFormat(piFileFormat);
		}

		this.iFormat = piFileFormat;
	}

	/**
	 * Sets the internal sample array (adSample) with the specified argument.
	 * Index gets reset as well.
	 * @param padSampleArray an array of doubles
	 */
	public synchronized void setSampleArray(double[] padSampleArray)
	{
		this.adSample = padSampleArray;
		this.iArrayIndex = 0;
	}

	/**
	 * Retrieves array containing audio data of the entire sample.
	 * @return an array of doubles
	 */
	public synchronized double[] getSampleArray()
	{
		return this.adSample;
	}

	/**
	 * Gets the next chunk of the data and places it into padChunkArray.
	 * Similar to readAudioData() method only it reads from the array instead of
	 * the audio stream (file).
	 * @param padChunkArray an array of doubles
	 * @return number of datums retrieved
	 */
	public synchronized int getNextChunk(double[] padChunkArray)
	{
		int iCount = 0;
		long lSampleSize = getSampleSize();

		while(iCount < padChunkArray.length && this.iArrayIndex < lSampleSize)
		{
			padChunkArray[iCount] = this.adSample[this.iArrayIndex];
			iCount++;
			this.iArrayIndex++;
		}

		return iCount;
	}

	/**
	 * Resets the marker used for reading audio data from sample array.
	 */
	public synchronized final void resetArrayMark()
	{
		this.iArrayIndex = 0;
	}

	/**
	 * Resets the marker used for reading audio data from sample array.
	 * Added for consistency and may be overridden.
	 * @since 0.3.0.6
	 * @see #resetArrayMark()
	 */
	public synchronized void reset()
	{
		resetArrayMark();
	}

	/**
	 * Returns the length of the sample.
	 * @return long Array length
	 */
	public synchronized long getSampleSize()
	{
		return this.adSample == null ? 0 : this.adSample.length;
	}

	/**
	 * Sets internal size of the sample array.
	 * If array did not exist, it its created,
	 * else cut or enlarged to the desired size.
	 * The previous content is retained in full unless
	 * the desired size is less than the current (in which case
	 * the new size exceeding data is lost).
	 *
	 * @param piDesiredSize new desired size of the array
	 * @throws IllegalArgumentException if the parameter is <= 0
	 * @since 0.3.0.2
	 */
	public synchronized void setSampleSize(int piDesiredSize)
	{
		if(piDesiredSize <= 0)
		{
			throw new IllegalArgumentException("Sample array size should not be 0 or less.");
		}

		if(this.adSample == null)
		{
			this.adSample = new double[piDesiredSize];
		}
		else
		{
			// Same size is already set, nothing to do.
			if(this.adSample.length == piDesiredSize)
			{
				return;
			}

			double[] adCopy = (double[])this.adSample.clone();
			this.adSample = new double[piDesiredSize];

			if(adCopy.length > piDesiredSize)
			{
				// A portion of data is lost
				Arrays.copy(this.adSample, adCopy, piDesiredSize);
			}
			else
			{
				// Trailing data is junk
				Arrays.copy(this.adSample, adCopy, adCopy.length);
			}
		}
	}

	/* Utility Methods */

	/**
	 * Clones this sample object by copying the
	 * sample format and its data.
	 * @see java.lang.Object#clone()
	 * @since 0.3.0.5
	 */
	public synchronized Object clone()
	{
		try
		{
			Sample oCopy = (Sample)super.clone();
			oCopy.setAudioFileFormatCode(getAudioFileFormatCode());
			oCopy.setSampleArray((double[])this.adSample.clone());
			return oCopy;
		}
		catch(InvalidSampleFormatException e)
		{
			throw new RuntimeException(e.getMessage());
		}
		catch(CloneNotSupportedException e)
		{
			throw new InternalError(e.toString());
		}
	}

	/**
	 * Returns textual representation of the sample object.
	 * The contents is the format and length on the first line, and then
	 * a column of data.
	 *
	 * @return the sample data string
	 * @see java.lang.Object#toString()
	 * @since 0.3.0.3
	 */
	public synchronized String toString()
	{
		double[] adSampleRef = this.adSample == null
			? new double[] {}
			: this.adSample;

		StringBuffer oData = new StringBuffer();

		oData.append(this.oAudioFileFormat).append(", sample data length: ");
		oData.append(adSampleRef.length).append("\n");
		oData.append(Arrays.arrayToDelimitedString(adSampleRef, "\n"));

		return oData.toString();
	}

	/**
	 * Custom implementation of the object reading to be able to restore
	 * the transient data.
	 * @param poStream the stream to read this object from
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @since 0.3.0.6
	 */
	private void readObject(java.io.ObjectInputStream poStream)
    throws IOException, ClassNotFoundException
    {
		try
		{
			int iFormatCode = poStream.readInt();
			this.adSample = (double[])poStream.readObject();

			Debug.debug("Format code after reading: " + this.iFormat);

			setAudioFileFormatCode(iFormatCode);
		}
		catch(InvalidSampleFormatException e)
		{
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}
    }

	/**
	 * Implements the serialization of the object data to pair-match
	 * the readObject.
	 * @param poStream
	 * @throws IOException
	 * @see #readObject(java.io.ObjectInputStream)
	 */
	private void writeObject(java.io.ObjectOutputStream poStream)
    throws IOException
    {
		Debug.debug("Format code before writing: " + this.iFormat);
		poStream.writeInt(this.iFormat);
		poStream.writeObject(this.adSample);
    }

	/**
	 * Checks if the parameter is equal to this object.
	 * The comparison is done by equality of the integer
	 * format and the data arrays. The data arrays are
	 * equal when they are both nulls or contain the
	 * same data.
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @since 0.3.0.6
	 */
	public boolean equals(Object poSample)
	{
		if(poSample instanceof Sample)
		{
			Sample oSample = (Sample)poSample;

			return
				oSample.iFormat == this.iFormat &&
				(
					(oSample.adSample == null && this.adSample == null)
					|| (oSample.adSample != null && this.adSample != null && Arrays.equals(oSample.adSample, this.adSample))
				);
		}

		return false;
	}

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.51 $";
	}
}

// EOF
