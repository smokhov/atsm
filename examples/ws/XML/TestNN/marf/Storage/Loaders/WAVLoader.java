package marf.Storage.Loaders;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import marf.Storage.MARFAudioFileFormat;
import marf.Storage.Sample;
import marf.Storage.StorageException;
import marf.util.ByteUtils;
import marf.util.InvalidSampleFormatException;


/**
 * <p>Loads/stores samples if WAVE format.</p>
 *
 * $Id: WAVLoader.java,v 1.34 2009/02/22 02:16:01 mokhov Exp $
 *
 * @author Jimmy Nicolacopoulos
 * @author Serguei Mokhov
 *
 * @version $Revision: 1.34 $
 * @since 0.0.1
 */
public class WAVLoader
extends AudioSampleLoader
{
	/*
	 * ----------------
	 * Methods
	 * ----------------
	 */

	/**
	 * Default WAVLoader Constructor.
	 * @throws InvalidSampleFormatException if the WAV file isn't really in WAV format
	 * or any other error took place.
	 */
	public WAVLoader()
	throws InvalidSampleFormatException
	{
		this
		(
			DEFAULT_FREQUENCY,
			DEFAULT_SAMPLE_BIT_SIZE,
			DEFAULT_CHANNELS,
			AudioFormat.Encoding.PCM_SIGNED
		);
	}

	/**
	 * Allows construction of the WAVE loader with non-default parameters.
	 * @param piRequiredFrequency other than default of 8000
	 * @param piRequiredBitSize other than the default of 16
	 * @param piRequiredChannels other than the default of 1
	 * @param poEncoding other than the default of AudioFormat.Encoding.PCM_SIGNED
	 * @throws InvalidSampleFormatException if the WAV file isn't really in WAV format
	 * or any other error took place.
	 * @since 0.3.0.6
	 * @see AudioFormat.Encoding
	 */
	public WAVLoader
	(
		float pfRequiredFrequency,
		int piRequiredBitSize,
		int piRequiredChannels,
		AudioFormat.Encoding poEncoding
	)
	throws InvalidSampleFormatException
	{
		this.oSample = new Sample(MARFAudioFileFormat.WAV);

		this.oEncoding = poEncoding;

		this.fRequiredFrequency = pfRequiredFrequency;
		this.iRequiredBitSize = piRequiredBitSize;
		this.iRequiredChannels = piRequiredChannels;

		this.oAudioFormat = new AudioFormat
		(
			this.oEncoding,
			this.fRequiredFrequency,
			this.iRequiredBitSize,
			this.iRequiredChannels,
			(this.iRequiredBitSize / 8) * this.iRequiredChannels,
			this.fRequiredFrequency,
			false
		);
	}

	/**
	 * Loads and decodes the WAV sample from the provided stream.
	 * @see marf.Storage.SampleLoader#loadSample(java.io.InputStream)
	 * @since 0.3.0.6
	 */
	public Sample loadSample(InputStream poAudioDataInputStream)
	throws StorageException
	{
		try
		{
			AudioInputStream oNewInputStream = null;

			// The parameter should not be null and should be a regular file.
			if(poAudioDataInputStream != null)
			{
				if(poAudioDataInputStream instanceof AudioInputStream)
				{
					// Get input stream from the parameter we've been gifted
					oNewInputStream = (AudioInputStream)poAudioDataInputStream;
				}
				else
				{
					// Check the file format of the file
					AudioFileFormat oFileFormat = AudioSystem.getAudioFileFormat(poAudioDataInputStream);
	
					if(oFileFormat.getType().equals(AudioFileFormat.Type.WAVE) == false)
					{
						throw new InvalidSampleFormatException("Audio stream type is not WAVE");
					}
	
					// Get input stream from the file
					oNewInputStream = AudioSystem.getAudioInputStream(poAudioDataInputStream);
				}

				// Check internal audio format characteristics we require
				validateAudioFormat(oNewInputStream.getFormat());
			}
			else
			{
				throw new FileNotFoundException("Audio stream or file is either null or is not a regular file.");
			}

			// Set the stream and fill out the sample's buffer with data
			this.oAudioInputStream = oNewInputStream;
			updateSample();

			return this.oSample;
		}

		// To avoid re-wrapping into StorageException again.
		catch(StorageException e)
		{
			throw e;
		}

		// Wrap all the other exceptions here.
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			throw new StorageException(e);
		}
	}

	/**
	 * Buffers out the contents of an audio buffer into the parameter.
	 * @param padAudioData data array to fill in
	 * @return the number of sample points of data read (default is a word of two bytes)
	 * @throws StorageException if there was a problem reading the audio data
	 */
	public final int readSampleData(double[] padAudioData)
	throws StorageException
	{
		try
		{
			int iBytesPerSample = this.iRequiredBitSize / 8;

			byte[] atAudioBuffer = new byte[padAudioData.length * iBytesPerSample];
			int iNbrBytes = this.oAudioInputStream.read(atAudioBuffer);
			int iSamplePointCount = (iNbrBytes / iBytesPerSample) + (iNbrBytes % iBytesPerSample);

			for(int i = 0; i < iSamplePointCount; i++)
			{
				switch(iBytesPerSample)
				{
					case 1:
					{
						padAudioData[i] = (double)atAudioBuffer[i] / 255;
						break;
					}
					
					case 2:
					{
						padAudioData[i] = (double)ByteUtils.byteArrayToShort
						(
							atAudioBuffer,
							iBytesPerSample * i,
							this.oAudioFormat.isBigEndian()
						) / 32768;
						
						break;
					}
					
					default:
					{
						throw new StorageException("Invalid bytes per sample setting: " + iBytesPerSample);
					}
				}
			}

			return iSamplePointCount;
		}
		catch(IOException e)
		{
			e.printStackTrace(System.err);
			throw new StorageException(e);
		}
	}

	/**
	 * Buffers the contents of audio data parameter into the equivalent
	 * AudioInputStream object given the audio format specification.
	 * Essentially, this method converts a double-array data to a byte
	 * array and makes it available again for reading using the standard
	 * means. The double data is assumed not to exceed a two-byte range
	 * prior conversion to bytes. If it does exceed two bytes in useful
	 * precision, then this precision will be LOST. 
	 * 
	 * @param padAudioData array of data to be written
	 * @param piNbrWords number of words to be written
	 * @return the number of data written
	 * @throws StorageException if there was an error writing audio data
	 */
	public final int writeSampleData(final double[] padAudioData, final int piNbrWords)
	throws StorageException
	{
		int iWord = 0;

		byte[] atAudioBytes;
		byte[] atAudioBuffer = new byte[piNbrWords * 2];

		for(int i = 0; i < piNbrWords; i++)
		{
			iWord = (int)(padAudioData[i] * 32768);
			atAudioBuffer[2 * i] = (byte)(iWord & 255);
			atAudioBuffer[2 * i + 1] = (byte)(iWord >>> 8);
		}

		this.oByteArrayOutputStream.write(atAudioBuffer, 0, atAudioBuffer.length);
		atAudioBytes = this.oByteArrayOutputStream.toByteArray();

		ByteArrayInputStream oBAIS = new ByteArrayInputStream(atAudioBytes);

		this.oAudioInputStream = new AudioInputStream
		(
			oBAIS,
			this.oAudioFormat,
			atAudioBytes.length / this.oAudioFormat.getFrameSize()
		);

		return atAudioBuffer.length;
	}

	/**
	 * Saves the wave into a file for playback.
	 * @param poOutFile File object for output
	 * @throws StorageException if there was an error saving sample
	 */
	public final void saveSample(File poOutFile)
	throws StorageException
	{
		try
		{
			AudioSystem.write(this.oAudioInputStream, AudioFileFormat.Type.WAVE, poOutFile);
			reset();
		}
		catch(IOException e)
		{
			e.printStackTrace(System.err);
			throw new StorageException(e);
		}
	}

	/**
	 * Resets the marker for the audio and byte-array streams.
	 * Used after writing audio data into the sample's audio stream.
	 * @throws StorageException if there was an error resetting the streams
	 * @since 0.3.0
	 */
	public void reset()
	throws StorageException
	{
		super.reset();
		this.oByteArrayOutputStream.reset();
	}

	/**
	 * Validates audio file stream format for WAVE files.
	 * Checks the format has the required bit size, number
	 * of channels, and required sampling frequency.
	 * @param poFormat the audio format to validate
	 * @throws UnsupportedAudioFileException if any of the three criteria are not met
	 * @since 0.3.0.5
	 */
	public void validateAudioFormat(final AudioFormat poFormat)
	throws UnsupportedAudioFileException
	{
		if(poFormat.getSampleSizeInBits() != this.iRequiredBitSize)
		{
			throw new UnsupportedAudioFileException
			(
				"Wave stream is not " + this.iRequiredBitSize
				+ "-bit (found " + poFormat.getSampleSizeInBits() + ")"
			);
		}

		if(poFormat.getChannels() != this.iRequiredChannels)
		{
			throw new UnsupportedAudioFileException
			(
				"Wave stream's number of channels (requested: " + poFormat.getChannels()
				+ ") is not the same as required (" + this.iRequiredChannels + ")."
			);
		}

		if(poFormat.getFrameRate() != this.fRequiredFrequency)
		{
			throw new UnsupportedAudioFileException
			(
				"Wave stream's frame rate (requested: " + poFormat.getFrameRate()
				+ " Hz) is not the same as required (" + this.fRequiredFrequency + " Hz)."
			);
		}
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.34 $";
	}
}

// EOF
