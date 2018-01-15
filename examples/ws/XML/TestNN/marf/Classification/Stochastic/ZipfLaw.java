package marf.Classification.Stochastic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Hashtable;

import marf.Classification.ClassificationException;
import marf.Classification.Distance.DiffDistance;
import marf.FeatureExtraction.IFeatureExtraction;
import marf.Stats.StatisticalObject;
import marf.Stats.WordStats;
import marf.Storage.Result;
import marf.Storage.StorageException;
import marf.util.SortComparator;
import marf.util.comparators.FrequencyComparator;


/**
 * <p>Module exercising Zipf's Law.</p>
 *
 * @author Serguei Mokhov
 * @version $Id: ZipfLaw.java,v 1.35 2012/07/09 03:53:32 mokhov Exp $
 * @since 0.3.0.2
 */
public class ZipfLaw
extends Stochastic
{
	/**
	 * Default number of entries display/output per page.
	 * @since 0.3.0.5
	 */
	public static final int DEFAULT_OUTPUT_PAGE_SIZE = 100;

	/**
	 * Local collection of word statistics.
	 */
	private Hashtable<Object, StatisticalObject> oStats = null;

	/**
	 * Sorted references to statistics.
	 * As of 0.3.0.6 was set to the base type StatisticalObject
	 * instead of WordStats to allow other than word elements. 
	 */
	private StatisticalObject[] aoSortedStatRefs = null;

	/**
	 * Indicates whether to dump in log-log scale format or not.
	 */
	private boolean bDumpLogariphm = true;

	/**
	 * The length of a longest word found, in characters (unigrams).
	 * @since 0.3.0.5
	 */
	private int iMaxWordLength = 0;

	/**
	 * The length of a smallest word found, in characters (unigrams).
	 * @since 0.3.0.5
	 */
	private int iMinWordLength = Integer.MAX_VALUE;

	/**
	 * When the results are dumped in the text mode, tell how
	 * many records to show per page.
	 * @since 0.3.0.6
	 */
	private int iOutputPageSize = DEFAULT_OUTPUT_PAGE_SIZE;
	
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = -7356104653992493029L;

	/**
	 * Takes a filename argument.
	 * @param pstrStatsFilename the desired file to process
	 */
	public ZipfLaw(String pstrStatsFilename)
	{
		super(null);
		this.strFilename = pstrStatsFilename;
		this.oStats = new Hashtable<Object, StatisticalObject>();
		this.oObjectToSerialize = this;
		this.iCurrentDumpMode = DUMP_GZIP_BINARY;
	}

	/**
	 * Classification API.
	 * @param poFeatureExtraction preprocessing module to get the data from
	 */
	public ZipfLaw(IFeatureExtraction poFeatureExtraction)
	{
		super(poFeatureExtraction);
		this.strFilename = getTrainingSetFilename().replaceAll("marf.Storage.TrainingSet", getClass().getName());
		this.oStats = new Hashtable<Object, StatisticalObject>();
		this.oObjectToSerialize = this;
	}
	
	/**
	 * @since 0.3.0.6
	 * @see marf.Classification.IClassification#classify(double[])
	 */
	public boolean classify(double[] padFeatureVector)
	throws ClassificationException
	{
		try
		{
			// Unseen data
			collectStatistics(padFeatureVector);
			
			// Back up its stats
			//Hashtable<Object, StatisticalObject> oBackupStats = (Hashtable<Object, StatisticalObject>)this.oStats.clone();
			StatisticalObject[] aoSortedDataBackup = (StatisticalObject[])this.aoSortedStatRefs.clone();
			
			// Restore data from the training set
			restore();
			
			double[] adUnseenData = new double[aoSortedDataBackup.length];
			double[] adSeenData = new double[this.aoSortedStatRefs.length];
			
			// The unseen and trained vectors have to have identical
			// observations on the LHS. The ones that are missing on
			// either one get a frequency of zero. This is required
			// for meaningful component-wise distance comparison
			// afterwards as raw percentages will not do that.
			// This can be approximated with the DiffDistance classifier
			// as a temporary workaround.
			// XXX
			
			// Compute totals prior conversion to percentages
			int iUnseenObservationsTotal = 0;
			int iTrainingObservationsTotal = 0;
			
			for(int i = 0; i < aoSortedDataBackup.length; i++)
			{
				iUnseenObservationsTotal += aoSortedDataBackup[i].getFrequency();
			}

			for(int i = 0; i < this.aoSortedStatRefs.length; i++)
			{
				iTrainingObservationsTotal += this.aoSortedStatRefs[i].getFrequency();
			}

			// Convert unseen and stored data to just plain double[] arrays
			for(int i = 0; i < aoSortedDataBackup.length; i++)
			{
				adUnseenData[i] = (double)aoSortedDataBackup[i].getFrequency() / iUnseenObservationsTotal;
			}

			for(int i = 0; i < this.aoSortedStatRefs.length; i++)
			{
				adSeenData[i] = (double)this.aoSortedStatRefs[i].getFrequency() / iTrainingObservationsTotal;
			}
			
			// Compare the unseen and stored data using a specified
			// Distance classifier
			// XXX
			//DiffDistance oDistance = ClassifcationFactory.create(this.iDistanceMethod);
			DiffDistance oDistance = new DiffDistance(null);
			double dDistance = oDistance.distance(adSeenData, adUnseenData);
			this.oResultSet.addResult(1, dDistance);
			
			return true;
		}
		catch(ClassificationException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new ClassificationException(e);
		}
	}

	/**
	 * @since 0.3.0.6
	 * @see marf.Classification.IClassification#train(double[])
	 */
	public boolean train(double[] padFeatureVector)
	throws ClassificationException
	{
		try
		{
			restore();
			collectStatistics(padFeatureVector);
			dump();
			
			return true;
		}
		catch(ClassificationException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new ClassificationException(e);
		}
	}

	/**
	 * @since 0.3.0.6
	 * @see marf.Classification.IClassification#getResult()
	 */
	public Result getResult()
	{
		// TODO Auto-generated method stub
		return super.getResult();
	}

	/**
	 * Collects result statistics.
	 * TODO: employ StatsCollector.
	 * @param padFeatures desired stream tokenizer
	 * @throws ClassificationException in case of inner exceptions
	 */
	public final void collectStatistics(double[] padFeatures)
	throws ClassificationException
	{
		try
		{
			// All items are one unit in length
			this.iMinWordLength = this.iMaxWordLength = 1;

			// Collect Stats
			for(int i = 0; i < padFeatures.length; i++)
			{
				// Look up if we already have an entry for this.
				StatisticalObject oFeatureStats = (StatisticalObject)this.oStats.get(new Double(padFeatures[i]));

				// New entry
				if(oFeatureStats == null)
				{
					oFeatureStats = new StatisticalObject(1);
					this.oStats.put(new Double(padFeatures[i]), oFeatureStats);
				}

				// Update existing entry
				else
				{
					oFeatureStats.incFrequency();
				}
			}

			// Sort and assign ranks
			sort();
			rankAll();
		}
		catch(RuntimeException e)
		{
			throw new ClassificationException(e);
		}
	}

	/**
	 * Collects result statistics.
	 * TODO: employ StatsCollector.
	 * @param poStreamTokenizer desired stream tokenizer
	 * @throws ClassificationException in case of inner exceptions
	 */
	public final void collectStatistics(StreamTokenizer poStreamTokenizer)
	throws ClassificationException
	{
		try
		{
			// Collect Stats
			while(poStreamTokenizer.nextToken() != StreamTokenizer.TT_EOF)
			{
				String strToken = poStreamTokenizer.sval;

				// Something which what we didn't ask for (not a string)
				if(strToken == null)
				{
					System.err.println
					(
						"WARNING: null sval for token type: (" + poStreamTokenizer.ttype
						+ "," + (char)poStreamTokenizer.ttype + ")"
					);

					continue;
				}

				if(strToken.length() > this.iMaxWordLength)
				{
					this.iMaxWordLength = strToken.length();
				}

				if(strToken.length() < this.iMinWordLength)
				{
					this.iMinWordLength = strToken.length();
				}

				// Look up if we already have an entry for this.
				WordStats oWordStats = (WordStats)oStats.get(strToken);

				// New entry
				if(oWordStats == null)
				{
					oWordStats = new WordStats(1, strToken);
					oStats.put(new String(strToken), oWordStats);
				}

				// Update existing entry
				else
				{
					oWordStats.incFrequency();
				}
			}

			// Sort and assign ranks
			sort();
			rankAll();
		}
		catch(Exception e)
		{
			throw new ClassificationException(e);
		}
	}

	/**
	 * Sorts results.
	 */
	private void sort()
	{
		this.aoSortedStatRefs = this.oStats.values().toArray(new StatisticalObject[0]);
		marf.util.Arrays.sort(this.aoSortedStatRefs, new FrequencyComparator(SortComparator.DESCENDING));
	}

	/**
	 * Ranks results.
	 */
	private final void rankAll()
	{
		for(int i = 0; i < this.aoSortedStatRefs.length; i++)
		{
			this.aoSortedStatRefs[i].setRank(i + 1);
		}
	}

	/**
	 * Dumps results to STDOUT.
	 */
	public final void dumpAll()
	{
		System.out.println("f = Frequency, r = Rank");

		for(int i = 0; i < this.aoSortedStatRefs.length; i += 10 * this.iOutputPageSize)
		{
			System.out.println
			(
				"\n" +
				"---------------------------------\n" +
				"Words from " + (i + 1) + " to " + (i + this.iOutputPageSize) + "\n" +
				"---------------------------------\n\n"
			);

			System.out.println("Columns: r, f, f*r, word");

			StringBuffer oStatsDump = new StringBuffer();
			
			for
			(
				int j = 0;
				j < (this.aoSortedStatRefs.length - i > this.iOutputPageSize ? this.iOutputPageSize : this.aoSortedStatRefs.length - i);
				j++
			)
			{
				StatisticalObject oStatsItem = this.aoSortedStatRefs[i + j];

				oStatsDump
					.append(oStatsItem.getRank()).append("\t")
					.append(oStatsItem.getFrequency()).append("\t")
					.append(oStatsItem.getFrequency() * oStatsItem.getRank()).append("\t");

				if(oStatsItem instanceof WordStats)
				{
					oStatsDump.append(((WordStats)oStatsItem).getLexeme());
				}
				
				oStatsDump.append("\n");
			}
			
			System.out.print(oStatsDump);
		}

		// Frequency count
		int aiFrequencies[] = new int[this.iOutputPageSize];
		int iCurrFrequency = 1;

		for(int i = this.aoSortedStatRefs.length - 1; i > 0; i--)
		{
			//Debug.debug("freq: " + iCurrFrequency + ", i=" + i + ", len = " + aoSortedStatRefs.length);

			if(this.aoSortedStatRefs[i].getFrequency() == iCurrFrequency)
			{
				// Such a frequency happened before
				aiFrequencies[iCurrFrequency - 1]++;
			}
			else
			{
				// First occurrence of such a frequency
				iCurrFrequency = aoSortedStatRefs[i].getFrequency();
				
				if(iCurrFrequency < this.iOutputPageSize)
				{
					aiFrequencies[iCurrFrequency - 1] = 1;
				}
				else
				{
					System.err.println
					(
						"WARNING: Occurence of a frequency (" + iCurrFrequency + ") exceeds "
						+ "output page size (" + this.iOutputPageSize + "), and, therefore, ignored."
					);

					iCurrFrequency = 1; 
				}
			}
		}

		System.out.println
		(
			"\n" +
			"Frequency of frequencies\n" +
			"------------------------\n" +
			"f\tC(f,w)"
		);

		for(int i = 0; i < this.iOutputPageSize; i++)
		{
			System.out.println((i + 1) + "\t" + aiFrequencies[i]);
		}
	}

	/**
	 * Dumps CVS values of the rank and frequency into a file.
	 * Filename is composed from the original corpus name plus the .csv extension.
	 * By default the dump is in the log() scale.
	 * @throws IOException
	 */
	public final void dumpGraphValues()
	throws IOException
	{
		BufferedWriter oBufferedWriter = new BufferedWriter(new FileWriter(this.strFilename + ".csv"));

		if(this.bDumpLogariphm == false)
		{
			oBufferedWriter.write("rank,frequency");
		}
		else
		{
			oBufferedWriter.write("log(rank),log(frequency)");
		}

		for(int i = 0; i < aoSortedStatRefs.length; i++)
		{
			if(this.bDumpLogariphm == true)
			{
				oBufferedWriter.write
				(
					Math.log(aoSortedStatRefs[i].getRank()) + ","
					+ Math.log(aoSortedStatRefs[i].getFrequency())
				);
			}
			else
			{
				oBufferedWriter.write
				(
					aoSortedStatRefs[i].getRank() + ","
					+ aoSortedStatRefs[i].getFrequency()
				);
			}

			oBufferedWriter.newLine();
		}

		oBufferedWriter.close();
	}

	/**
	 * @since 0.3.0.5
	 * @see #restore()
	 * @see marf.Storage.StorageManager#backSynchronizeObject()
	 */
	public synchronized void backSynchronizeObject()
	{
		ZipfLaw oZipfLaw = (ZipfLaw)this.oObjectToSerialize;

		this.oStats = oZipfLaw.getStats();
		this.aoSortedStatRefs = oZipfLaw.getSortedStatRefs();
		this.bDumpLogariphm = oZipfLaw.isDumpLogariphmOn();
		this.iMaxWordLength = oZipfLaw.getMaxWordLength();
		this.iMinWordLength = oZipfLaw.getMinWordLength();

		this.oObjectToSerialize = this;
	}

	/**
	 * An object must know how dump itself or its data structures to a file.
	 * Options are: Object serialization and CSV. Internally, the method
	 * calls all the <code>dump*()</code> methods based on the current dump mode.
	 * This derivative uses only <code>DUMP_GZIP_BINARY</code>, <code>DUMP_BINARY</code>
	 * and <code>DUMP_CSV_TEXT</code> modes.
	 *
	 * @throws StorageException if saving to a file for some reason fails or
	 * the dump mode set to an unsupported value
	 *
	 * @see #dumpGzipBinary()
	 * @see #dumpCSV()
	 * @see #dumpBinary()
	 * @see #backSynchronizeObject()
	 * @since 0.3.0.5
	 */
	public synchronized void dump()
	throws StorageException
	{
		switch(this.iCurrentDumpMode)
		{
			case DUMP_GZIP_BINARY:
				dumpGzipBinary();
				break;

			case DUMP_CSV_TEXT:
				dumpCSV();
				break;

			case DUMP_BINARY:
				dumpBinary();
				break;

			default:
				throw new StorageException("Unsupported dump mode: " + this.iCurrentDumpMode);
		}
	}

	/**
	 * An object must know how restore itself or its data structures from a file.
	 * Options are: Object serialization and CSV. Internally, the method
	 * calls all the <code>restore*()</code> methods based on the current dump mode.
	 *
	 * @throws StorageException if loading from a file for some reason fails or
	 * the dump mode set to an unsupported value
	 *
	 * @see #DUMP_GZIP_BINARY
	 * @see #DUMP_BINARY
	 * @see #DUMP_CSV_TEXT
	 * @see #dumpGzipBinary()
	 * @see #dumpBinary()
	 * @see #dumpCSV()
	 * @see #backSynchronizeObject()
	 * @see #iCurrentDumpMode
	 * @since 0.3.0.5
	 */
	public synchronized void restore()
	throws StorageException
	{
		switch(this.iCurrentDumpMode)
		{
			case DUMP_GZIP_BINARY:
				restoreGzipBinary();
				break;

			case DUMP_BINARY:
				restoreBinary();
				break;

			case DUMP_CSV_TEXT:
				restoreCSV();
				break;

			default:
				throw new StorageException("Unsupported dump mode: " + this.iCurrentDumpMode);
		}
	}

	/**
	 * Implements CSV dump through the <code>dumpGraphValues()</code>
	 * method.
	 * @throws StorageException in case of any I/O error
	 * @since 0.3.0.5
	 * @see #dumpGraphValues()
	 */
	public synchronized void dumpCSV()
	throws StorageException
	{
		try
		{
			dumpGraphValues();
		}
		catch(IOException e)
		{
			throw new StorageException(e);
		}
	}

	/**
	 * Allows examining the value of the log-log flag.
	 * @return the current value of the flag
	 * @since 0.3.0.5
	 * @see #setDumpLogariphm(boolean)
	 */
	public boolean isDumpLogariphmOn()
	{
		return this.bDumpLogariphm;
	}

	/**
	 * Allows setting the dump log-log flag to indicate
	 * the module to dump graphs in the log-log scale.
	 * @param pbDumpLogariphm new value of the log-log flag
	 * @since 0.3.0.5
	 */
	public void setDumpLogariphm(boolean pbDumpLogariphm)
	{
		this.bDumpLogariphm = pbDumpLogariphm;
	}

	/**
	 * Allows getting an array of sorted references to the statistical objects.
	 * @return the sorted StatisticalObject array
	 * @since 0.3.0.5
	 */
	public final StatisticalObject[] getSortedStatRefs()
	{
		return this.aoSortedStatRefs;
	}

	/**
	 * Allows getting raw Hashtable of the WordStats objects.
	 * @return the stats hashtable
	 * @since 0.3.0.5
	 */
	public final Hashtable<Object, StatisticalObject> getStats()
	{
		return this.oStats;
	}

	/**
	 * Allows getting a particular WordStats object by its lexeme.
	 *
	 * @param pstrLexeme lexeme to look up the WordStats entry
	 * @return the corresponding WordStats entry or <code>null</code> if not found
	 * @since 0.3.0.5
	 */
	public final WordStats getWordStats(final String pstrLexeme)
	{
		//Debug.debug("Lexeme to lookup: " + pstrLexeme);
		return (WordStats)this.oStats.get(pstrLexeme);
	}

	/**
	 * Allows getting the length of the longest word found (in characters).
	 * @return the length of the longest word in the dictionary
	 * @since 0.3.0.5
	 */
	public final int getMaxWordLength()
	{
		return this.iMaxWordLength;
	}

	/**
	 * Allows getting the length of the smallest word found (in characters).
	 * @return the length of the smallest word in the dictionary
	 * @since 0.3.0.5
	 */
	public final int getMinWordLength()
	{
		return this.iMinWordLength;
	}

	/**
	 * Reports minimum and maximum word lengths and the dictionary
	 * itself in a form of a String.
	 * @see java.lang.Object#toString()
	 * @since 0.3.0.5
	 */
	public String toString()
	{
		StringBuffer oBuffer = new StringBuffer();

		oBuffer
			.append("Minimum word length: ").append(this.iMinWordLength).append("\n")
			.append("Maximum word length: ").append(this.iMaxWordLength).append("\n")
			.append("Dictionary size: ").append(this.oStats.size()).append("\n")
			.append("Stats Dictionary:\n")
			.append(this.oStats);

		return oBuffer.toString();
	}
	
	/**
	 * Retrieves class' revision.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.35 $";
	}
}

// EOF
