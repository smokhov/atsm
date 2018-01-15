package marf.Storage;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * <p>Common sample loading interface.
 * 
 * Must be overridden by a concrete sample loader. Derivatives
 * should try their best to inherit from the SampleLoader class; otherwise,
 * they must implement this interface.</p>
 *
 * $Id: ISampleLoader.java,v 1.14 2007/12/23 06:29:46 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @since 0.3.0.2
 * @version $Revision: 1.14 $
 * 
 * @see SampleLoader
 */
public interface ISampleLoader
{
	/*
	 * ----------------
	 * Constants
	 * ----------------
	 */

	/**
	 * Default number of bits per amplitude.
	 * Default 16.
	 */
	int DEFAULT_SAMPLE_BIT_SIZE = 16;

	/**
	 * Mono.
	 * Default 1.
	 */
	int DEFAULT_CHANNELS = 1;

	/**
	 * Default sampling frequency of 8000 Hz.
	 */
	float DEFAULT_FREQUENCY = 8000;

	/**
	 * Interface source code revision.
	 */
	String MARF_INTERFACE_CODE_REVISION = "$Revision: 1.14 $";

	/**
	 * Reads sample data from the sample's stream into the parameter.
	 * 
	 * In 0.3.0.6 was renamed from <code>readAudioData()</code> to <code>readSampleData()</code>
	 * to accommodate non-audio loaders. <code>readAudioData()</code> was moved down
	 * to <code>AudioSampleLoader</code> as a wrapper for this method for some
	 * semblance of backwards compatibility.
	 * 
	 * @param padSampleData an array of doubles to store the data read
	 * @return the number of datums read
	 * @throws StorageException if there was an error reading the data
	 * @see marf.Storage.Loaders.AudioSampleLoader#readAudioData(double[])
	 */
	int readSampleData(double[] padSampleData)
	throws StorageException;

	/**
	 * Writes sample data into the sample's stream.
	 * 
	 * In 0.3.0.6 this method was renamed from <code>writeAudioData()</code>
	 * to <code>writeSampleData()</code> to accommodate non-audio loaders.
	 * <code>writeAudioData()</code> was moved down to <code>AudioSampleLoader</code>
	 * as a wrapper for this method for some semblance of backwards compatibility.
	 * 
	 * @param padSampleData an array of doubles
	 * @param piWords the number of data items (words) to write from the padSampleData
	 * @return the number of data actually written
	 * @throws StorageException if there was an error loading the sample
	 * @see marf.Storage.Loaders.AudioSampleLoader#writeAudioData(double[], int)
     */
	int writeSampleData(double[] padSampleData, int piWords)
	throws StorageException;

	/**
	 * Prime SampleLoader interface.
	 * Must be overridden by a concrete loader that knows how to load a specific sample format.
	 * @param poInFile file object a sample to be read from
	 * @return Sample object reference
	 * @throws StorageException
	 */
	Sample loadSample(File poInFile)
	throws StorageException;

	/**
	 * Same as loadSample(File) but takes filename as an argument.
	 * @param pstrFilename filename of a sample to be read from
	 * @return Sample object reference
	 * @throws StorageException if there was an error loading the sample
	 */
	Sample loadSample(final String pstrFilename)
	throws StorageException;

	/**
	 * Assumes the file data is in the array of bytes for loading.
	 * @param patFileData the byte data of a sample to be read from
	 * @return Sample object reference
	 * @throws StorageException if there was an error loading the sample
	 * @since 0.3.0.6
	 */
	Sample loadSample(byte[] patFileData)
	throws StorageException;

	/**
	 * Assumes the incoming stream is the sample file data.
	 * @param poDataInputStream the stream of a sample to be read from
	 * @return Sample object reference
	 * @throws StorageException if there was an error loading the sample
	 * @since 0.3.0.6
	 */
	Sample loadSample(InputStream poDataInputStream)
	throws StorageException;

	/**
	 * Prime SampleLoader interface.
	 * Must be overridden by a concrete loader that knows how to save a specific sample.
	 * @param poOutFile File object of a sample to be saved to
	 * @throws StorageException if there was an error saving the sample
	 */
	void saveSample(File poOutFile)
	throws StorageException;

	/**
	 * Same as saveSample(File) but takes filename as an argument.
	 * @param pstrFilename filename of a sample to be saved to
	 * @throws StorageException if there was an error saving the sample
	 */
	void saveSample(final String pstrFilename)
	throws StorageException;

	/**
	 * Assumes the file data is in the array of bytes for loading.
	 * @param patFileData the byte data of a sample to be read from
	 * @throws StorageException if there was an error loading the sample
	 * @since 0.3.0.6
	 */
	void saveSample(byte[] patFileData)
	throws StorageException;

	/**
	 * Assumes the output stream is the sample file data.
	 * @param poDataOutputStream the stream of a sample to be written to
	 * @throws StorageException if there was an error loading the sample
	 * @since 0.3.0.6
	 */
	void saveSample(OutputStream poDataOutputStream)
	throws StorageException;

	/**
	 * <p><code>updateSample()</code> is just used whenever the
	 * <code>AudioInputStream</code> is assigned to a new value (wave file).
	 * Then you would simply call this method to update the
	 * <code>Sample</code> member with the contents of the new <code>AudioInputStream</code>.</p>
	 * @throws StorageException if there was an error updating the sample data array
	 */
	void updateSample()
	throws StorageException;

	/**
	 * Resets the marker for the audio stream. Used after writing audio data
	 * into the sample's audio stream.
	 * @throws StorageException if there was an error resetting the audio stream
	 */
	void reset()
	throws StorageException;

	/**
	 * Retrieves the length of the sample (# of audio data in the audio stream).
	 * @return sample size, long
	 * @throws StorageException if there was an error getting sample size
	 */
	long getSampleSize()
	throws StorageException;

	/**
	 * Returns internal reference to a Sample object.
	 * @return Sample reference
	 */
	Sample getSample();

	/**
	 * Sets internal sample reference from outside.
	 * @param poSample Sample object
	 */
	void setSample(Sample poSample);
}

// EOF
