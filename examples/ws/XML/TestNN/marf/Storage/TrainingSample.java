package marf.Storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.StringTokenizer;
import java.util.Vector;

import marf.util.Arrays;


/**
 * <p>TrainingSample contains one item in the training set.
 * Each training sample consists of the feature vector plus
 * information describing that feature vector.
 * Has been extracted from TrainingSet in 0.3.0.
 * TODO: fix CSV dumps.
 * </p>
 *
 * $Id: TrainingSample.java,v 1.17 2010/05/30 19:04:14 mokhov Exp $
 *
 * @author Stephen Sinclair
 * @author Serguei Mokhov
 *
 * @version $Revision: 1.17 $
 * @since 0.0.1
 */
public class TrainingSample
implements ITrainingSample
{
   	/**
	 * Which subject this feature vector is associated with.
	 *
	 * This is an application-independent ID within MARF to distinguish
	 * from other subjects. Typical subjects may include speakers, languages
	 * instruments, emotions, etc. that a give application is taken care of.
	 */
	protected int iSubjectID;

	/**
	 * Array representing either a feature vector, mean
	 * vector describing the cluster, or a median vector.
	 */
	protected double[] adDataVector = null;

	/**
	 * A list of filenames that were used in training for this sample.
	 * Used to avoid duplicate training on the same filename.
	 */
	protected Vector<String> oFilenames = new Vector<String>();

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = 440451144821982021L;

	/**
	 * Default training sample constructor.
	 * Explicitly appeared in 0.3.0.5.
	 * @since 0.3.0.5
	 */
	public TrainingSample()
	{
		super();
	}

	/**
	 * Copy-constructor.
	 * @param poTrainingSample TrainingSample object to copy
	 * @since 0.3.0.5
	 */
	@SuppressWarnings("unchecked")
	public TrainingSample(final TrainingSample poTrainingSample)
	{
		this();

		this.iSubjectID = poTrainingSample.iSubjectID;
		this.oFilenames = (Vector<String>)poTrainingSample.oFilenames.clone();

		if(poTrainingSample.adDataVector != null)
		{
			this.adDataVector = (double[])poTrainingSample.adDataVector.clone();
		}
	}

	/* (non-Javadoc)
	 * @see marf.Storage.ITrainingSample#setFeatureVector(double[], java.lang.String, int)
	 */
	public boolean setFeatureVector
	(
		double[] padFeatureVector,
		String pstrFilename,
		int piSubjectID
	)
	{
		this.iSubjectID = piSubjectID;
		addFilename(pstrFilename);
		this.adDataVector = new double[padFeatureVector.length];
		Arrays.copy(this.adDataVector, padFeatureVector, padFeatureVector.length);

		return true;
	}

	/* (non-Javadoc)
	 * @see marf.Storage.ITrainingSample#addFeatureVector(double[], java.lang.String, int)
	 */
	public boolean addFeatureVector
	(
		double[] padFeatureVector,
		String pstrFilename,
		int piSubjectID
	)
	{
		return setFeatureVector(padFeatureVector, pstrFilename, piSubjectID);
	}

	/* (non-Javadoc)
	 * @see marf.Storage.ITrainingSample#setFilename(java.lang.String)
	 */
	public /*final*/ void setFilename(String pstrFilename)
	{
		if(this.oFilenames.size() == 0)
		{
			this.oFilenames.add(pstrFilename);
		}
		else
		{
			this.oFilenames.set(0, pstrFilename);
		}
	}

	/**
	 * Adds a filename to the training sample.
	 *
	 * The method is <code>protected</code> and can be made <code>public</code>
	 * by the extending classes that allow more than one filename.
	 *
	 * @param pstrFilename filename to add
	 * @return <code>false</code> if the filename is already there; <code>true</code> otherwise
	 * @see #existsFilename(String)
	 * @since 0.3.0.6
	 */
	public boolean addFilename(String pstrFilename)
	{
		if(existsFilename(pstrFilename))
		{
			return false;
		}

		this.oFilenames.add(pstrFilename);

		return true;
	}

	/* (non-Javadoc)
	 * @see marf.Storage.ITrainingSample#existsFilename(java.lang.String)
	 */
	public boolean existsFilename(String pstrFilename)
	{
		if
		(
			this.oFilenames.size() == 0
			|| this.oFilenames.firstElement().toString().equals(pstrFilename) == false
		)
		{
			return false;
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see marf.Storage.ITrainingSample#getSubjectID()
	 */
	public final int getSubjectID()
	{
		return this.iSubjectID;
	}

	/* (non-Javadoc)
	 * @see marf.Storage.ITrainingSample#getDataVector()
	 */
	public final double[] getDataVector()
	{
		return this.adDataVector;
	}

	/* (non-Javadoc)
	 * @see marf.Storage.ITrainingSample#setSubjectID(int)
	 */
	public /*final*/ void setSubjectID(final int piSubjectID)
	{
		this.iSubjectID = piSubjectID;
	}

	/* (non-Javadoc)
	 * @see marf.Storage.ITrainingSample#setDataVector(double[])
	 */
	public /*final*/ void setDataVector(double[] padDataVector)
	{
		this.adDataVector = padDataVector;
	}

	/**
	 * Writes one training sample to a CSV file.
	 * @param poBufferedWriter BufferedWriter
	 * @throws StorageException in case of I/O or otherwise error
	 */
	public void dumpCSV(BufferedWriter poBufferedWriter)
	throws StorageException
	{
		try
		{
			StringBuffer oDump = new StringBuffer();

			oDump
				.append(this.iSubjectID).append(",").append(this.adDataVector.length).append(",")
				.append(Arrays.arrayToCSV(this.adDataVector));

			poBufferedWriter.write(oDump.toString());
			poBufferedWriter.newLine();
		}
		catch(Exception e)
		{
			throw new StorageException(e);
		}
	}

	/**
	 * Retrieve one training sample from a CSV file.
	 * TODO: FIX, for it's BROKEN.
	 * @param poBufferedReader BufferedReader
	 * @throws StorageException in case of I/O or otherwise error
	 */
	public void restoreCSV(BufferedReader poBufferedReader)
	throws StorageException
	{
		try
		{
			StringTokenizer oTokenizer = new StringTokenizer(poBufferedReader.readLine(), ",");

			int iLen = 0;

			if(oTokenizer.hasMoreTokens())
			{
				this.iSubjectID = Integer.parseInt(oTokenizer.nextToken());
			}

	//		if(oTokenizer.hasMoreTokens())
	//			iFeatureExtractionMethod = Integer.parseInt(oTokenizer.nextToken());

	//		if(oTokenizer.hasMoreTokens())
	//			iPreprocessingMethod = Integer.parseInt(oTokenizer.nextToken());

			if(oTokenizer.hasMoreTokens())
			{
				iLen = Integer.parseInt(oTokenizer.nextToken());
			}

			this.adDataVector = new double[iLen];

			int i = 0;

			while(oTokenizer.hasMoreTokens())
			{
				this.adDataVector[i++] = Double.parseDouble(oTokenizer.nextToken());
			}
		}
		catch(Exception e)
		{
			throw new StorageException(e);
		}
	}

	/**
	 * @since 0.3.0.6
	 * @see marf.Storage.ITrainingSample#getMeanCount()
	 */
	public int getMeanCount()
	{
		return size();
	}

	/**
	 * Simply retrieves the data vector.
	 * Internally calls <code>getDataVector()</code>.
	 * @return array of doubles representing the mean for that cluster
	 * @since 0.3.0.6
	 * @see #getDataVector()
	 */
	public double[] getMeanVector()
	{
		return getDataVector();
	}

	/**
	 * Simply retrieves the data vector.
	 * Internally calls <code>getDataVector()</code>.
	 * @return array of doubles representing the mean for that cluster
	 * @since 0.3.0.6
	 * @see #getDataVector()
	 */
	public double[] getMedianVector()
	{
		return getDataVector();
	}

	/**
	 * @since 0.3.0.6
	 * @see marf.Storage.ITrainingSample#size()
	 */
	public int size()
	{
		return this.adDataVector == null ? 0 : 1;
	}

	/**
	 * Implements Cloneable interface for the TrainingSample object.
	 * @see java.lang.Object#clone()
	 * @since 0.3.0.5
	 */
	public Object clone()
	{
		return new TrainingSample(this);
	}

	/**
	 * Provides string representation of the training sample data.
	 * @see java.lang.Object#toString()
	 * @since 0.3.0.6
	 */
	public String toString()
	{
		StringBuffer oBuffer = new StringBuffer();

		oBuffer
			.append("Subject ID: ").append(this.iSubjectID).append("\n")
			.append("Data vector reference: ").append(this.adDataVector).append("\n")
			.append("Size: ").append(size()).append("\n")
			.append("Filenames: ").append(this.oFilenames).append("\n")
			.append("TrainingSample Source code revision: ").append(getMARFSourceCodeRevision()).append("\n");

		return oBuffer.toString();
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.17 $";
	}
}

// EOF
