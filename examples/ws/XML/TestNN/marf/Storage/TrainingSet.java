package marf.Storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;

import marf.util.Debug;


/**
 * <p>TrainingSet -- Encapsulates Subject ID and subject's clusters or a feature set.
 * </p>
 *
 * @author Serguei Mokhov
 * @version $Id: TrainingSet.java,v 1.52 2010/06/23 09:40:19 mokhov Exp $
 * @since 0.0.1, December 5, 2002
 */
public class TrainingSet
extends StorageManager
{
	/*
	 * NOTE: Be careful when you mess with this file. Any new fields
	 *       or structural changes will change the on-disk layout of
	 *       whole TrainingSet. Until an upgrade utility is available,
	 *       you will have to retrain ALL your models that use TrainingSet
	 *       in DUMP_BINARY and DUMP_GZIP_BINARY models.
	 */

	/**
	 * Default TrainingSet file name of <code>marf.training.set</code>.
	 * @since 0.3.0.2
	 */
	public static final String DEFAULT_TRAINING_SET_FILENAME = "marf.training.set";

	public static final int TRAINING_SET_SAMPLES = 0; // TrainingSample
	public static final int TRAINING_SET_CLUSTERS = 1; // Cluster
	public static final int TRAINING_SET_FEATURE_SETS = 2; // FeatureSet
//	public static final int TRAINING_SET_BOTH = 2; // FeatureSet + Cluster
//	public static final int TRAINING_SET_PLAIN = 3; // TrainingSample

	protected int iTrainingSetFormat = TRAINING_SET_CLUSTERS;
//	protected int iTrainingSetFormat = TRAINING_SET_FEATURE_SETS;

	/**
	 * A collection of Clusters.
	 */
//	protected Vector oClusters = new Vector();
	protected Vector<ITrainingSample> oTrainingSamples = new Vector<ITrainingSample>();
//	protected Hashtable oTrainingSamples = new Hashtable();

	/**
	 * Feature Set as opposed to the cluster.
	 * @since 0.3.0.1
	 */
	//protected FeatureSet oFeatureSet = null;

	/**
	 * Which preprocessing method was applied to the sample before this feature vector was extracted.
	 */
	protected int iPreprocessingMethod;

	/**
	 * Which feature extraction method was used to determine this feature vector.
	 */
	protected int iFeatureExtractionMethod;

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = 2733362635058973185L;

	/**
	 * Construct a training set object.
	 */
	public TrainingSet()
	{
		this.iCurrentDumpMode = DUMP_GZIP_BINARY;
		this.strFilename = DEFAULT_TRAINING_SET_FILENAME;
		this.oObjectToSerialize = this;
	}

	/**
	 * Retrieves clusters of training samples.
	 * @return vector of training samples.
	 */
	public final Vector<ITrainingSample> getClusters()
//	public final Hashtable getClusters()
	{
		return this.oTrainingSamples;
	}

	/**
	 * Returns preprocessing method used on this training set.
	 * @return the method
	 */
	public final int getPreprocessingMethod()
	{
		return this.iPreprocessingMethod;
	}

	/**
	 * Returns preprocessing method used on this training set.
	 * @return piPreprocessingMethod the method
	 */
	public final int getFeatureExtractionMethod()
	{
		return this.iFeatureExtractionMethod;
	}

	/**
	 * Sets feature extraction method used on this training set.
	 * @param piPreprocessingMethod the method
	 */
	public final void setPreprocessingMethod(int piPreprocessingMethod)
	{
		this.iPreprocessingMethod = piPreprocessingMethod;
	}

	/**
	 * Sets feature extraction method used on this training set.
	 * @param piFeatureExtractionMethod the method
	 */
	public final void setFeatureExtractionMethod(int piFeatureExtractionMethod)
	{
		this.iFeatureExtractionMethod = piFeatureExtractionMethod;
	}

	/**
	 * Adds new feature vector to the mean and recomputes the mean.
	 * @param padFeatureVector vector to add
	 * @param pstrFilename to check to avoid training on the same file
	 * @param piSubjectID for which subject that vector is
	 * @param piPreprocessingMethod preprocessing method used
	 * @param piFeatureExtractionMethod feature extraction method used
	 * @return <code>true</code> if the vector was added; <code>false</code> otherwise
	 */
	public final boolean addFeatureVector
	(
		double[] padFeatureVector,
		String pstrFilename,
		int piSubjectID,
		int piPreprocessingMethod,
		int piFeatureExtractionMethod
	)
	{
		/*
		 * Check if this sample is already in the training set
		 * for these feature extraction & preprocessing methods.
		 */
//		Cluster oTrainingSetData = null;
		ITrainingSample oTrainingSetData = null;
		boolean bNewSubject = true;

		// Loop trough all the clusters and find the one that
		// pertains to the current subject ID.
		for(int i = 0; (i < this.oTrainingSamples.size()) && (bNewSubject == true); i++)
		{
			oTrainingSetData = this.oTrainingSamples.get(i);
//			oTrainingSetData = (Cluster)this.oTrainingSamples.get(new Integer(piSubjectID));

			// When found ...
			if(piSubjectID == oTrainingSetData.getSubjectID())
			//if(oTrainingSetData != null)
			{
				// Disallow training on the same file twice
				if(oTrainingSetData.existsFilename(pstrFilename))
				{
					Debug.debug
					(
						"TrainingSet.addFeatureVector() --- Attempt to train on the same file: " +
						pstrFilename + ", ignoring..."
					);

					return false;
				}

				// Assert that this is indeed a new training data
				bNewSubject = false;
			}
		}

		if(bNewSubject == true)
//			else
		{
			//oTrainingSetData = new Cluster();

			switch(this.iTrainingSetFormat)
			{
				case TRAINING_SET_SAMPLES:
					oTrainingSetData = new TrainingSample();
					break;

				case TRAINING_SET_CLUSTERS:
					oTrainingSetData = new Cluster();
					break;

				case TRAINING_SET_FEATURE_SETS:
					oTrainingSetData = new FeatureSet();
					break;

				default:
					assert false : "Unsupported format: " + this.iTrainingSetFormat;
			}
		}

		oTrainingSetData.addFeatureVector(padFeatureVector, pstrFilename, piSubjectID);

		setFeatureExtractionMethod(piFeatureExtractionMethod);
		setPreprocessingMethod(piPreprocessingMethod);

		// New ID/sample pair added
		if(bNewSubject == true)
		{
			//this.oClusters.add(oTrainingSetData);
			this.oTrainingSamples.add(oTrainingSetData);
			//this.oTrainingSamples.put(new Integer(piSubjectID), oTrainingSetData);

			Debug.debug
			(
				"TrainingSet.addFeatureVector() -- Added feature vector for subject " + piSubjectID +
				", preprocessing method " + piPreprocessingMethod +
				", feature extraction method " + piFeatureExtractionMethod
			);
		}
		else
		{
			Debug.debug
			(
				"TrainingSet.addFeatureVector() -- Updated mean vector for subject " + piSubjectID +
				", preprocessing method " + piPreprocessingMethod +
				", feature extraction method " + piFeatureExtractionMethod
			);
		}

		return true;
	}

	/**
	 * Gets the size of the feature vectors set.
	 * @return number of training samples in the set
	 */
	public final int size()
	{
		return this.oTrainingSamples.size();
	}

	/**
	 * @see marf.Storage.IStorageManager#restoreCSV()
	 * @since 0.3.0.6
	 */
	public void restoreCSV()
	throws StorageException
	{
		/*
		 * TODO: check whether this code is still valid
		 */

		try
		{
			BufferedReader oReader = new BufferedReader(new FileReader(this.strFilename));

			int iLength = Integer.parseInt(oReader.readLine());

			for(int i = 0; i < iLength; i++)
			{
				Cluster oCluster = new Cluster();

				oCluster.restoreCSV(oReader);
//				this.oClusters.add(oCluster);
			}

			oReader.close();
		}
		catch(FileNotFoundException e)
		{
			Debug.debug
			(
				"TrainingSet.restoreCSV() --- FileNotFoundException for file: \"" +
				this.strFilename + "\", " +
				e.getMessage() + "\n" +
				"Creating one now..."
			);

			dumpCSV();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			throw new StorageException(e);
		}
	}

	/**
	 * Retrieves the current training set from disk.
	 * @throws StorageException in a case of I/O or otherwise error
	 * @see marf.Storage.IStorageManager#restore()
	 */
	public void restore()
	throws StorageException
	{
		super.restore();
		Debug.debug
		(
			"TrainingSet.restore() -- Training set loaded successfully. Size: "
			+ this.oTrainingSamples.size()
			+ " vector(s)."
		);
	}

	/**
	 * @see marf.Storage.IStorageManager#dumpCSV()
	 * @since 0.3.0.6
	 */
	public void dumpCSV()
	throws StorageException
	{
		// TODO: review and test for it's broken
		try
		{
			BufferedWriter oWriter = new BufferedWriter(new FileWriter(this.strFilename));

			oWriter.write(Integer.toString(this.oTrainingSamples.size()));
			oWriter.newLine();

			Debug.debug
			(
				"Wrote " + this.oTrainingSamples.size() + " clusters " +
				"to file " + this.strFilename
			);

			for(int i = 0; i < this.oTrainingSamples.size(); i++)
			{
//				((Cluster)this.oClusters.get(i)).dumpCSV(oWriter);
			}

			oWriter.close();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			throw new StorageException(e);
		}
	}


	/**
	 * @return Returns the iTrainingSetFormat.
	 * @since 0.3.0.6
	 */
	public int getTrainingSetFormat()
	{
		return this.iTrainingSetFormat;
	}

	/**
	 * @param piTrainingSetFormat The iTrainingSetFormat to set.
	 * @since 0.3.0.6
	 */
	public void setTrainingSetFormat(int piTrainingSetFormat)
	{
		this.iTrainingSetFormat = piTrainingSetFormat;
	}

	/**
	 * @see marf.Storage.StorageManager#backSynchronizeObject()
	 * @since 0.3.0.6
	 */
	public synchronized void backSynchronizeObject()
	{
		TrainingSet oNewThis = (TrainingSet)this.oObjectToSerialize;

		this.iFeatureExtractionMethod = oNewThis.iFeatureExtractionMethod;
		this.iPreprocessingMethod = oNewThis.iPreprocessingMethod;

		this.oTrainingSamples = oNewThis.oTrainingSamples;
		//this.oFeatureSet = oNewThis.oFeatureSet;

		this.oObjectToSerialize = this;
	}

	/**
	 * Implements Cloneable interface for the TrainingSet object.
	 * Performs a "deep" copy of this object including all clusters
	 * and the feature set.
	 * @see java.lang.Object#clone()
	 * @since 0.3.0.5
	 */
	@SuppressWarnings("unchecked")
	public Object clone()
	{
		TrainingSet oClone = (TrainingSet)super.clone();

//		oClone.oClusters =
//			this.oClusters == null ?
//			null : (Vector)this.oClusters.clone();

		oClone.oTrainingSamples =
			this.oTrainingSamples == null ?
			null : (Vector<ITrainingSample>)this.oTrainingSamples.clone();

//		oClone.oTrainingSamples =
//			this.oTrainingSamples == null ?
//			null : (Hashtable)this.oTrainingSamples.clone();
/*
		oClone.oFeatureSet =
			this.oFeatureSet == null ?
			null : (FeatureSet)this.oFeatureSet.clone();
*/
		return oClone;
	}

	/**
	 * Provides string representation of the training set data in addition
	 * to that of the parent StorageManager.
	 * @see marf.Storage.StorageManager#toString()
	 * @since 0.3.0.6
	 */
	public synchronized String toString()
	{
		StringBuffer oBuffer = new StringBuffer(super.toString());

		oBuffer
			.append("Training Set Format: ").append(this.iTrainingSetFormat).append("\n")
			.append("Preprocessing Method: ").append(this.iPreprocessingMethod).append("\n")
			.append("Feature Extraction Method: ").append(this.iFeatureExtractionMethod).append("\n")
			.append("TrainingSet Size: ").append(size()).append("\n")
			.append("Training Set Samples: ").append(this.oTrainingSamples).append("\n")
			.append("TrainingSet Source code revision: ").append(getMARFSourceCodeRevision()).append("\n");

		return oBuffer.toString();
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.52 $";
	}
}

// EOF
