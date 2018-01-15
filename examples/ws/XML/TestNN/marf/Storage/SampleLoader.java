package marf.Storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import marf.util.NotImplementedException;


/**
 * <p>Abstract class that provides a generic implementation of the
 * sample loading interface. Must be overridden by a concrete sample
 * loader.</p>
 *
 * $Id: SampleLoader.java,v 1.30 2009/02/22 02:16:01 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @author Jimmy Nicolacopoulos
 *
 * @version $Revision: 1.30 $
 * @since 0.0.1
 */
public abstract class SampleLoader
implements ISampleLoader
{
	/*
	 * ----------------
	 * Data Members
	 * ----------------
	 */

	/**
	 * Current bit size of a sample.
	 */
	protected int iRequiredBitSize = DEFAULT_SAMPLE_BIT_SIZE;

	/**
	 * Current number of channels.
	 */
	protected int iRequiredChannels = DEFAULT_CHANNELS;

	/**
	 * Current frequency.
	 * @since 0.3.0
	 */
	protected float fRequiredFrequency = DEFAULT_FREQUENCY;

	/**
	 * Sample references of the sample to be loaded.
	 */
	protected Sample oSample = null;

	/**
	 * Output stream used for writing audio data.
	 */
	protected ByteArrayOutputStream oByteArrayOutputStream = null;

	/**
	 * Default constructor.
	 * Instantiates <code>ByteArrayOutputStream</code>.
	 */
	public SampleLoader()
	{
		this.oByteArrayOutputStream = new ByteArrayOutputStream();
	}

	/**
	 * Same as <code>loadSample(File)</code> but takes filename as an argument.
	 * @param pstrFilename filename of a sample to be read from
	 * @return Sample object reference
	 * @throws StorageException if there was an error loading the sample
	 * @see #loadSample(File)
	 */
	public Sample loadSample(final String pstrFilename)
	throws StorageException
	{
		return loadSample(new File(pstrFilename));
	}


	/**
	 * Loads sample data from a file. In a nutshell, converts the
	 * File into a buffered file input streams and passes it on
	 * the appropriate method.
	 *
	 * @param poInFile incoming sample File object
	 * @return Sample object
	 * @throws StorageException if there was a problem loading the sample
	 * @since 0.3.0.6
	 * @see #loadSample(InputStream)
	 */
	public Sample loadSample(File poInFile)
	throws StorageException
	{
		try
		{
			if(poInFile != null && poInFile.isFile())
			{
				return loadSample(new BufferedInputStream(new FileInputStream(poInFile)));
			}
			else
			{
				throw new FileNotFoundException("Filename is either null or is not a regular file.");
			}
		}

		// To avoid re-wrapping into StorageException again.
		catch(StorageException e)
		{
			throw e;
		}

		// Wrap all the other exceptions here.
		catch(Exception e)
		{
			throw new StorageException(e);
		}
	}

	/**
	 * Converts the byte array into a buffered byte array input stream
	 * and passes it on.
	 *
	 * @see marf.Storage.ISampleLoader#loadSample(byte[])
	 * @since 0.3.0.6
	 * @see #loadSample(InputStream)
	 */
	public Sample loadSample(byte[] patFileData)
	throws StorageException
	{
		return loadSample(new BufferedInputStream(new ByteArrayInputStream(patFileData)));
	}

	/**
	 * Not implemented. Must be overridden to work.
	 * @see marf.Storage.ISampleLoader#loadSample(java.io.InputStream)
	 * @since 0.3.0.6
	 * @throws NotImplementedException
	 */
	public Sample loadSample(InputStream poDataInputStream)
	throws StorageException
	{
		throw new NotImplementedException("Base class does not implemn this. Must be overridden.");
	}

	/**
	 * Same as saveSample(File) but takes filename as an argument.
	 * @param pstrFilename filename of a sample to be saved to
	 * @throws StorageException if there was an error saving the sample
	 */
	public void saveSample(final String pstrFilename)
	throws StorageException
	{
		saveSample(new File(pstrFilename));
	}

	/**
	 * @see marf.Storage.ISampleLoader#saveSample(byte[])
	 * @since 0.3.0.6
	 * @throws NotImplementedException incomplete
	 */
	public void saveSample(byte[] patFileData)
	throws StorageException
	{
		saveSample(new BufferedOutputStream(new ByteArrayOutputStream(patFileData.length)));
		// XXX
		throw new NotImplementedException("incomplete");
	}

	/**
	 * @see marf.Storage.ISampleLoader#saveSample(java.io.File)
	 * @since 0.3.0.6
	 */
	public void saveSample(File poOutFile)
	throws StorageException
	{
		try
		{
			saveSample(new BufferedOutputStream(new FileOutputStream(poOutFile)));
		}
		catch(StorageException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new StorageException(e);
		}
	}

	/**
	 * @see marf.Storage.ISampleLoader#saveSample(java.io.OutputStream)
	 * @since 0.3.0.6
	 */
	public void saveSample(OutputStream poDataOutputStream)
	throws StorageException
	{
		throw new NotImplementedException("Base class does not implement this. Must be overridden.");
	}

	/**
	 * <p><code>updateSample()</code> is just used whenever the
	 * <code>AudioInputStream</code> is assigned to a new value (wave file).
	 * Then you would simply call this method to update the
	 * <code>Sample</code> member with the contents of the new <code>AudioInputStream</code>.</p>
	 * @throws StorageException if there was an error updating the sample data array
	 */
	public void updateSample()
	throws StorageException
	{
		double[] adSampleArray = new double[(int)getSampleSize()];
		readSampleData(adSampleArray);
		this.oSample.setSampleArray(adSampleArray);
	}

	/**
	 * Resets the marker for the audio stream. Used after writing audio data
	 * into the sample's audio stream.
	 * @throws StorageException if there was an error resetting the audio stream
	 */
	public void reset()
	throws StorageException
	{
		// does nothing in the generic implementation
		// XXX: is that okay? Should it be removed and
		// forced upon the derivatives?
	}

	/**
	 * Retrieves the length of the sample (# of audio data in the audio stream).
	 * @return sample size, long
	 * @throws StorageException if there was an error getting sample size
	 */
	public long getSampleSize()
	throws StorageException
	{
		return this.oSample == null ? 0 : this.oSample.getSampleSize();
	}

	/**
	 * Returns internal reference to a Sample object.
	 * @return Sample reference
	 */
	public final Sample getSample()
	{
		return this.oSample;
	}

	/**
	 * Sets internal sample reference from outside.
	 * @param poSample Sample object
	 */
	public final void setSample(Sample poSample)
	{
		this.oSample = poSample;
	}

	public int getRequiredBitSize()
	{
		return this.iRequiredBitSize;
	}

	public void setRequiredBitSize(int piRequiredBitSize)
	{
		this.iRequiredBitSize = piRequiredBitSize;
	}

	public int getRequiredChannels()
	{
		return this.iRequiredChannels;
	}

	public void setRequiredChannels(int piRequiredChannels)
	{
		this.iRequiredChannels = piRequiredChannels;
	}

	public float getRequiredFrequency()
	{
		return this.fRequiredFrequency;
	}

	public void setRequiredFrequency(float piRequiredFrequency)
	{
		this.fRequiredFrequency = piRequiredFrequency;
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.30 $";
	}
}

// EOF
