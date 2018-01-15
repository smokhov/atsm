package marf.Classification.RandomClassification;

import java.util.Random;
import java.util.Vector;

import marf.MARF;
import marf.Classification.Classification;
import marf.Classification.ClassificationException;
import marf.FeatureExtraction.IFeatureExtraction;
import marf.Storage.Result;
import marf.Storage.StorageException;
import marf.util.Debug;


/**
 * <p>Random Classification Module is for testing purposes.</p>
 *
 * <p>This represents the bottom-line of the classification results.
 * All the other modules should be better than this 99% of the time.
 * If they are not, debug them.</p>
 *
 * @author Serguei Mokhov
 * @version $Id: RandomClassification.java,v 1.22 2012/07/09 03:53:32 mokhov Exp $
 * @since 0.2.0
 */
public class RandomClassification
extends Classification
{
	/**
	 * Vector of integer IDs.
	 */
	private Vector<Integer> oIDs = new Vector<Integer>();

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = -6770780209979417110L;

	/**
	 * RandomClassification Constructor.
	 * @param poFeatureExtraction FeatureExtraction module reference
	 */
	public RandomClassification(IFeatureExtraction poFeatureExtraction)
	{
		super(poFeatureExtraction);

		this.strFilename = new StringBuffer()
			.append(getClass().getName()).append(".")
			.append(MARF.getPreprocessingMethod()).append(".")
			.append(MARF.getFeatureExtractionMethod()).append(".")
			.append(getDefaultExtension())
			.toString();
		
		this.oObjectToSerialize = this.oIDs;
	}

	/**
	 * Picks an ID at random.
	 * In 0.3.0.6 the generic pipelined version of this API
	 * <code>classify()</code> was refactored into the
	 * <code>Classification</code>.
	 *
	 * @param padFeatureVector unused
	 * @return <code>true</code>
	 * @throws ClassificationException
	 * @since 0.3.0.6
	 */
	public final boolean classify(double[] padFeatureVector)
	throws ClassificationException
	{
		try
		{
			int iFirstID = 0;
			int iSecondID = 0;

			restore();

			if(this.oIDs.size() == 0)
			{
				Debug.debug("RandomClassification.classify() --- ID set is of 0 length.");

				this.oIDs.add(iFirstID);

				this.oResultSet.addResult
				(
					iFirstID,
					0,
					getClass().getName() +
					" out of the blue"
				);
			}
			else
			{
				// Collect for stats
				// XXX: Move to StatsCollector
				iFirstID = this.oIDs.elementAt((int)(this.oIDs.size() * (new Random().nextDouble())));
				iSecondID = iFirstID;

				// Pick a different second best ID if there are > 1 IDs in there
				while(iSecondID == iFirstID && this.oIDs.size() > 1)
				{
					iSecondID = this.oIDs.elementAt((int)(this.oIDs.size() * (new Random().nextDouble())));
				}

				// If they are still equal (in case of one ID in oIDs), just add one.
				if(iSecondID == iFirstID)
				{
					iSecondID++;
				}

				Debug.debug("RandomClassification.classify() --- ID1 = " + iFirstID + ", ID2 = " + iSecondID);

				this.oResultSet.addResult(iFirstID, 0, getClass().getName());
				this.oResultSet.addResult(iSecondID, 1, getClass().getName());
			}

			return true;
		}
		catch(StorageException e)
		{
			e.printStackTrace(System.err);
			throw new ClassificationException(e);
		}
	}

	/**
	 * Simply stores incoming ID's to later pick one at random.
	 * @param padFeatureVector unused
	 * @return <code>true</code> if training was successful
	 * @throws ClassificationException
	 * @since 0.3.0.6
	 */
	public final boolean train(double[] padFeatureVector)
	throws ClassificationException
	{
		try
		{
			Integer oIntegerID = MARF.getCurrentSubject();

			restore();

			// Avoid adding duplicates.
			if(this.oIDs.contains(oIntegerID) == false)
			{
				this.oIDs.add(oIntegerID);

				dump();

				Debug.debug
				(
					new StringBuffer("RandomClassification.train() --- added ID: ")
						.append(MARF.getCurrentSubject()).append(",\n")
						.append("all IDs: ").append(this.oIDs)
				);
			}

			return true;
		}
		catch(StorageException e)
		{
			e.printStackTrace(System.err);
			throw new ClassificationException("Exception in RandomClassification.train() --- " + e.getMessage());
		}
	}

	/* From Storage Manager */

	/**
	 * Dumps "training set" of IDs.
	 * @throws StorageException
	 */
	public final void dump()
	throws StorageException
	{
		switch(this.iCurrentDumpMode)
		{
			case DUMP_BINARY:
				dumpBinary();
				break;

			case DUMP_GZIP_BINARY:
				dumpGzipBinary();
				break;

			default:
				super.dump();
		}
	}

	/**
	 * Restores "training set" of IDs.
	 * @throws StorageException if there was an error loading the data file.
	 */
	public final void restore()
	throws StorageException
	{
		switch(this.iCurrentDumpMode)
		{
			case DUMP_BINARY:
				restoreBinary();
				break;

			case DUMP_GZIP_BINARY:
				restoreGzipBinary();
				break;

			default:
				super.restore();
		}
		
		if(this.oIDs == null)
		{
			this.oIDs = new Vector<Integer>();
			this.oObjectToSerialize = this.oIDs;
		}
	}

	/**
	 * @see marf.Storage.StorageManager#backSynchronizeObject()
	 * @since 0.3.0.6
	 */
	@SuppressWarnings("unchecked")
	public synchronized void backSynchronizeObject()
	{
		this.oIDs = (Vector<Integer>)this.oObjectToSerialize;
	}

	/**
	 * Retrieves the classification result.
	 *
	 * @return Result object
	 *
	 * @since 0.3.0.2
	 */
	public Result getResult()
	{
		return this.oResultSet.getMinimumResult();
	}

	/**
	 * Returns string representation of the internals of this object.
	 * @return String
	 * @since 0.3.0.1
	 */
	public String toString()
	{
		StringBuffer oBuffer = new StringBuffer();

		oBuffer
			.append("Dump mode: ").append(this.iCurrentDumpMode).append("\n")
			.append("Dump file: ").append(this.strFilename).append("\n")
			.append("ID data: ").append(this.oIDs);

		return oBuffer.toString();
	}

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.22 $";
	}
}

// EOF
