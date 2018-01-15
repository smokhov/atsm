package marf.Storage.Loaders;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import marf.Storage.Sample;
import marf.Storage.SampleLoader;
import marf.Storage.StorageException;


/**
 * <p>Abstract class that provides a generic sound-oriented implementation of the
 * sample loading interface. Must be overridden by a concrete audio sample loader.</p>
 *
 * $Id: AudioSampleLoader.java,v 1.7 2009/02/22 02:16:01 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @author Jimmy Nicolacopoulos
 *
 * @version $Revision: 1.7 $
 * @since 0.3.0.6
 */
public abstract class AudioSampleLoader
extends SampleLoader
{
	/*
	 * ----------------
	 * Data Members
	 * ----------------
	 */

	/**
	 * Properties of a sound sample.
	 */
	protected AudioFormat oAudioFormat = null;

	/**
	 * Stream representing sound sample.
	 */
	protected AudioInputStream oAudioInputStream = null;

	/**
	 * Default audio encoding.
	 * @see AudioFormat.Encoding.PCM_SIGNED
	 */
	protected AudioFormat.Encoding oEncoding = AudioFormat.Encoding.PCM_SIGNED;

	/**
	 * Default constructor.
	 * Instantiates <code>ByteArrayOutputStream</code>.
	 */
	public AudioSampleLoader()
	{
		super();
	}

	/**
	 * Overrides the parent to include the AudioInputStream
	 * into the chain in order avoid audio format related errors.
	 * @see marf.Storage.SampleLoader#loadSample(byte[])
	 */
	public Sample loadSample(byte[] patFileData)
	throws StorageException
	{
		return loadSample
		(
			new AudioInputStream
			(
				new BufferedInputStream
				(
					new ByteArrayInputStream(patFileData)
				),

				this.oAudioFormat,
				patFileData.length / this.oAudioFormat.getFrameSize() 
			)
		);
	}

	/**
	 * Reads sample data from the sample's audio stream into padSampleData.
	 * @param padAudioData an array of doubles to store the data read
	 * @return the number of datums read
	 * @throws StorageException if there was an error reading the data
	 */
	public int readAudioData(double[] padAudioData)
	throws StorageException
	{
		return readSampleData(padAudioData);
	}

	/**
	 * Writes sample data into the sample's audio stream.
	 * @param padSampleData an array of doubles
	 * @param piWords the number of audio data items to write from the padSampleData array
	 * @return the actual number of data written
	 * @throws StorageException if there was an error loading the sample
     */
	public int writeAudioData(double[] padSampleData, int piWords)
	throws StorageException
	{
		return writeSampleData(padSampleData, piWords);
	}

	/**
	 * Resets the marker for the audio stream. Used after writing audio data
	 * into the sample's audio stream.
	 * @throws StorageException if there was an error resetting the audio stream
	 */
	public void reset()
	throws StorageException
	{
		try
		{
			super.reset();
			this.oAudioInputStream.reset();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			throw new StorageException(e);
		}
	}

	/**
	 * Retrieves the length of the sample (# of audio data in the audio stream).
	 * @return sample size, long
	 * @throws StorageException if there was an error getting sample size
	 */
	public long getSampleSize()
	throws StorageException
	{
		return this.oAudioInputStream.getFrameLength();
	}

	public AudioFormat getAudioFormat()
	{
		return this.oAudioFormat;
	}

	public void setAudioFormat(AudioFormat poAudioFormat)
	{
		this.oAudioFormat = poAudioFormat;
	}

	public AudioInputStream getAudioInputStream()
	{
		return this.oAudioInputStream;
	}

	public void setAudioInputStream(AudioInputStream poAudioInputStream)
	{
		this.oAudioInputStream = poAudioInputStream;
	}

	public AudioFormat.Encoding getEncoding()
	{
		return this.oEncoding;
	}

	public void setEncoding(AudioFormat.Encoding poEncoding)
	{
		this.oEncoding = poEncoding;
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.7 $";
	}
}

// EOF
