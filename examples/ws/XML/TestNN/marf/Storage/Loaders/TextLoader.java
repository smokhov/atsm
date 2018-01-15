package marf.Storage.Loaders;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import marf.MARF.ENgramModels;
import marf.Storage.MARFAudioFileFormat;
import marf.Storage.Sample;
import marf.Storage.SampleLoader;
import marf.Storage.StorageException;
import marf.util.Arrays;
import marf.util.Debug;
import marf.util.InvalidSampleFormatException;


/**
 * <p>Loads text samples assuming character code to be the absolute value.
 * </p>
 *
 * $Id: TextLoader.java,v 1.5 2012/07/18 02:45:45 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.5 $
 * @since 0.3.0.6
 */
public class TextLoader
extends SampleLoader
{
	/**
	 * Amount of characters per sample point.
	 */
//	protected int iNgramModel = ENgramModels.UNIGRAM; //NLP.getNgramModel();
	protected int iNgramModel = ENgramModels.BIGRAM; //NLP.getNgramModel();
//	protected int iNgramModel = ENgramModels.TRIGRAM; //NLP.getNgramModel();

	
	public static final int NGRAM_LOGICAL_OR = 0;

	public static final int NGRAM_ARITHMETIC_ADD = 1;

	public static final int NGRAM_ARITHMETIC_POWER_ADD = 2;
	
	/**
	 * Has effect with n-grams with n > 1. 
	 */
//	protected int iNgramCalculationModel = NGRAM_LOGICAL_OR; 
//	protected int iNgramCalculationModel = NGRAM_ARITHMETIC_ADD; 
	protected int iNgramCalculationModel = NGRAM_ARITHMETIC_POWER_ADD; 
	

	public static final int NGRAM_LITTLE_ENDIAN = 0;
	public static final int NGRAM_BIG_ENDIAN = 1;
	
	/**
	 * Indicates whether to interpret little or big endianness. 
	 * Has only effect with n-grams with n > 1.
	 */
	protected int iNgramEndiness = NGRAM_LITTLE_ENDIAN;
//	protected int iNgramEndiness = NGRAM_BIG_ENDIAN;
	
	
	public static final int POWER_BASE_DECIMAL = 10;
	public static final int POWER_BASE_OCTAL = 8;
	public static final int POWER_BASE_HEXADECIMAL = 16;
	public static final int POWER_BASE_BINARY = 2;
	public static final int POWER_BASE_64 = 64;
	public static final int POWER_BASE_ASCIICIMAL = 128;
	public static final int POWER_BASE_BYTECIMAL = 256;
	
	/**
	 * Amplitude numerical base interpretation.
	 * Has only effect with power add n-grams with n > 1.
	 */
	protected int iPowerBase = POWER_BASE_DECIMAL;
//	protected int iPowerBase = POWER_BASE_OCTAL;
//	protected int iPowerBase = POWER_BASE_HEXADECIMAL;
//	protected int iPowerBase = POWER_BASE_BINARY;
//	protected int iPowerBase = POWER_BASE_64;
//	protected int iPowerBase = POWER_BASE_ASCIICIMAL;
//	protected int iPowerBase = POWER_BASE_BYTECIMAL;
	
	/**
	 * Internal text reader object reference.
	 */
	protected Reader oReader = null;
	
	/**
	 * Constructs default Text Loader.
	 * @throws InvalidSampleFormatException
	 */
	public TextLoader()
	throws InvalidSampleFormatException
	{
		this.oSample = new Sample(MARFAudioFileFormat.TEXT);
		this.oSample.setSampleSize(Sample.DEFAULT_SAMPLE_SIZE);
		Debug.debug("TextLoader constructed.");
	}

	/**
	 * Reads of the next chunk of character data as amplitudes.
	 * @param padSampleData to fill in with sample data
	 * @return the length of padSampleData
	 * @throws StorageException
	 */
	public final int readSampleData(double[] padSampleData)
	throws StorageException
	{
		try
		{
			char[] acCharBuffer = new char[padSampleData.length];
			int iNbrDataItemsRead = 0;
			int iNbrChars = this.oReader.read(acCharBuffer);
			Debug.debug(getClass(), "read data " + iNbrChars + ", " + Arrays.arrayToCSV(acCharBuffer));

			if(iNbrChars > 0)
			{
				switch(this.iNgramModel)
				{
					case ENgramModels.BIGRAM:
					{
						int i;

						for(i = 0; i < acCharBuffer.length; i += 2)
						{
							int iLowest = 0;
							int iLow = 0;

							switch(this.iNgramEndiness)
							{
								case NGRAM_LITTLE_ENDIAN:
									iLowest = acCharBuffer[i];
									iLow = (i + 1 >= acCharBuffer.length) ? 0 : acCharBuffer[i + 1];
									break;
									
								case NGRAM_BIG_ENDIAN:
									iLowest = (i + 1 >= acCharBuffer.length) ? 0 : acCharBuffer[i + 1];
									iLow = acCharBuffer[i];
									break;
									
								default:
									assert false;
							}

							switch(this.iNgramCalculationModel)
							{
								case NGRAM_ARITHMETIC_ADD:
									padSampleData[i] = iLow + iLowest;
									break;

								case NGRAM_ARITHMETIC_POWER_ADD:
									padSampleData[i] = iLow * this.iPowerBase+ iLowest;
									break;

								case NGRAM_LOGICAL_OR:
									//padSampleData[i] = (iLow << 8) | (0xFF & iLowest);
									padSampleData[i] = ((0xFFFF & iLow) << 8) | (0xFF & iLowest);
									break;
									
								default:
									assert false;
							}
						}

						iNbrDataItemsRead = i;
						break;
					}
						
					case ENgramModels.TRIGRAM:
					{
						int i;

						for(i = 0; i < acCharBuffer.length; i += 3)
						{
//							int iLowest = acCharBuffer[i];
//							int iLow = (i + 1 >= acCharBuffer.length) ? 0 : acCharBuffer[i + 1];
//							int iMid = (i + 2 >= acCharBuffer.length) ? 0 : acCharBuffer[i + 2];
							long iLowest = 0;
							long iLow = (i + 1 >= acCharBuffer.length) ? 0 : acCharBuffer[i + 1];
							long iMid = 0;

							switch(this.iNgramEndiness)
							{
								case NGRAM_LITTLE_ENDIAN:
									iLowest = acCharBuffer[i];
									iMid = (i + 2 >= acCharBuffer.length) ? 0 : acCharBuffer[i + 2];
									break;
									
								case NGRAM_BIG_ENDIAN:
									iLowest = (i + 2 >= acCharBuffer.length) ? 0 : acCharBuffer[i + 2];
									iMid = acCharBuffer[i];
									break;
									
								default:
									assert false;
							}

							switch(this.iNgramCalculationModel)
							{
								case NGRAM_ARITHMETIC_ADD:
									padSampleData[i] = iMid + iLow + iLowest;
									break;

								case NGRAM_ARITHMETIC_POWER_ADD:
									padSampleData[i]
										= iMid * this.iPowerBase * this.iPowerBase
										+ iLow * this.iPowerBase
										+ iLowest;
									break;

								case NGRAM_LOGICAL_OR:
//									padSampleData[i] = (iMid << 16) | (iLow << 8) | (0xFF & iLowest);
									padSampleData[i] = ((0xFFFFFF & iMid) << 16) | ((0xFFFF & iLow) << 8) | (0xFF & iLowest);
									break;
									
								default:
									assert false;
							}
						}

						iNbrDataItemsRead = i;
						break;
					}

					default:
					{
						System.err.println("WARNING: unsupported n-gram model; falling back to unigram.");
						// There is no break statement here intentionally.
					}

					case ENgramModels.UNIGRAM:
					{
						Arrays.copy(padSampleData, acCharBuffer);
						iNbrDataItemsRead = padSampleData.length;
						break;
					}
				}
			}

			return iNbrDataItemsRead;
		}
		catch(IOException e)
		{
			e.printStackTrace(System.err);
			throw new StorageException(e);
		}
	}

	/**
	 * TODO: Does not do any actual writing.
	 * @param padSample used to verify the length
	 * @param piLength used to verify the length
	 * @return the smallest of padSample.length or piLength
	 * @throws StorageException if piLength is less than zero
	 */
	public final int writeSampleData(final double[] padSample, final int piLength)
	throws StorageException
	{
		int iSampleArrayLength = padSample == null ? 0 : padSample.length;

		if(piLength < 0)
		{
			throw new StorageException("Parameter length of a sample should not be < 0.");
		}

		return iSampleArrayLength < piLength ? iSampleArrayLength : piLength;
	}

	/**
	 * @see marf.Storage.ISampleLoader#loadSample(java.io.InputStream)
	 */
	public Sample loadSample(InputStream poDataInputStream)
	throws StorageException
	{
		this.oReader = new InputStreamReader(poDataInputStream);
		Debug.debug(getClass(), "Constructed a reader");
		updateSample();
		Debug.debug(getClass(), "Updated a sample");
		return this.oSample;
	}

	/**
	 * Retrieves the length of the sample (# of data points in the stream).
	 * @return sample size, long
	 * @throws StorageException if there was an error getting sample size
	 */
	public long getSampleSize()
	throws StorageException
	{
		return this.oSample.getSampleArray().length;
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.5 $";
	}
}

// EOF
