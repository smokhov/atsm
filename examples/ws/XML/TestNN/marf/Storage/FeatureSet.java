package marf.Storage;

import java.util.Vector;

import marf.math.Matrix;
import marf.util.Arrays;
import marf.util.NotImplementedException;


/**
 * <p>FeatureSet -- Encapsulates subject's feature vectors.
 * Additionally, can compute mean and median vectors off
 * the present feature vectors.
 * </p>
 *
 * @author Serguei Mokhov
 * @version $Id: FeatureSet.java,v 1.20 2010/05/30 19:04:14 mokhov Exp $
 * @since 0.3.0.1
 */
public class FeatureSet
extends Cluster
{
	/**
	 * A Vector of TrainingSamples.
	 */
	protected Vector<double[]> oFeatureVectors = new Vector<double[]>();

	/**
	 * Cached vector's data is invalid (untrustworthy), i.e.
	 * was either never computed yet or some other operation
	 * on the class invalidated it.
	 * @since 0.3.0.6
	 */
	private static final int CACHED_VECTOR_TYPE_INVALID = -1;
	
	/**
	 * The cached vector data represents a freshly computed <em>mean</em> vector.
	 * @since 0.3.0.6
	 */
	private static final int CACHED_VECTOR_TYPE_MEAN = 1;

	/**
	 * The cached vector data represents a freshly computed <em>median</em> vector.
	 * @since 0.3.0.6
	 */
	private static final int CACHED_VECTOR_TYPE_MEDIAN = 2;
	
	/**
	 * Type of the primary vector data member computed last.
	 * @see CACHED_VECTOR_TYPE_INVALID
	 * @see CACHED_VECTOR_TYPE_MEAN
	 * @see CACHED_VECTOR_TYPE_MEDIAN
	 */
	private int iLastTypeVectorComputed = CACHED_VECTOR_TYPE_INVALID;
	
	private int iMaxColumns = 0;
	
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = -1931299425905008139L;

	/**
	 * Construct a training set object.
	 */
	public FeatureSet()
	{
	}

	/**
	 * Retrieves training samples.
	 * @return vector of training samples.
	 */
	public Vector<double[]> getFeatureVectors()
	{
		// Invalidate the internal stuff as we cannot trust it's not altered outside
		this.iLastTypeVectorComputed = CACHED_VECTOR_TYPE_INVALID;
		return this.oFeatureVectors;
	}

	/**
	 * Sizes of the feature vectors set.
	 * @return number of training samples in the set
	 */
	public int size()
	{
		return this.oFeatureVectors.size();
	}

	/**
	 * Retrieve the current training set from disk.
	 * TODO: implement.
	 * @throws StorageException
	 * @throws NotImplementedException
	 * @since 0.3.0.5
	 */
	public void restoreCSV()
	throws StorageException
	{
		this.iLastTypeVectorComputed = CACHED_VECTOR_TYPE_INVALID;
		throw new NotImplementedException();
/*	
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(this.strFeatureSetFile));

			int num = Integer.parseInt(br.readLine());

			for(int i = 0; i < num; i++)
			{
				TrainingSample ts = new TrainingSample();

				ts.restore(br);
				oFeatureVectors.add(ts);
			}

			br.close();

			Debug.debug("Training set loaded successfully: " + oFeatureVectors.size() + " feature vectors.");
		}
		catch (FileNotFoundException e)
		{
			Debug.debug("FileNotFoundException in FeatureSet.restore()!");
		}
		catch (NumberFormatException e)
		{
			Debug.debug("Number format exception in FeatureSet.restore()!");
		}
*/
	}


	/**
	 * Dump the current training set to disk.
	 * TODO: implement.
	 * @throws StorageException
	 * @throws NotImplementedException
	 * @since 0.3.0.5
	 */
	public void dumpCSV()
	throws StorageException
	{
		throw new NotImplementedException();
/*	
		BufferedWriter bw = new BufferedWriter(new FileWriter(this.strFeatureSetFile));

		bw.write(Integer.toString(oFeatureVectors.size()));
		bw.newLine();

		Debug.debug
		(
			"Wrote " + Integer.toString(oFeatureVectors.size()) +
			" to file " + this.strFeatureSetFile
		);

		for(int i = 0; i < oFeatureVectors.size(); i++)
			((TrainingSample)oFeatureVectors.get(i)).dump(bw);

		bw.close();
*/
	}

	/* (non-Javadoc)
	 * @see marf.Storage.TrainingSample#getMeanVector()
	 * @since 0.3.0.6
	 */
	public double[] getMeanVector()
	{
		if(this.iLastTypeVectorComputed == CACHED_VECTOR_TYPE_MEAN)
		{
			return super.getMeanVector();
		}
		else
		{
			this.adDataVector = new double[this.iMaxColumns];

			for(int i = 0; i < this.oFeatureVectors.size(); i++)
			{
				double[] adFeatureVector = (double[])this.oFeatureVectors.get(i);
				
				for(int j = 0; j < adFeatureVector.length; j++)
				{
					this.adDataVector[j] += adFeatureVector[j];
				}
			}
			
			for(int i = 0; i < this.adDataVector.length; i++)
			{
				this.adDataVector[i] /= size();
			}
			
			this.iLastTypeVectorComputed = CACHED_VECTOR_TYPE_MEAN;

			return getDataVector();
		}
	}

	public double[] getMedianVector()
	{
		if(this.iLastTypeVectorComputed == CACHED_VECTOR_TYPE_MEDIAN)
		{
			return getDataVector();
		}
		else
		{
			this.adDataVector = new double[this.iMaxColumns];
			double[][] addFeatureSetMatrix = new double[size()][this.iMaxColumns];
			
			for(int i = 0; i < this.oFeatureVectors.size(); i++)
			{
				double[] adFeatureVector = (double[])this.oFeatureVectors.get(i);
				Arrays.copy(addFeatureSetMatrix[i], 0, adFeatureVector);
			}
			
			Matrix oMatrix = new Matrix(addFeatureSetMatrix);
			marf.math.Vector oVector = null;
			
			for(int i = 0; i < oMatrix.getCols(); i++)
			{
				oVector = oMatrix.getColumn(i);
				double[] adSortedVector = oVector.getMatrixArray();
				Arrays.sort(adSortedVector);

				this.adDataVector[i] = adSortedVector[adSortedVector.length / 2];
			}

			this.iLastTypeVectorComputed = CACHED_VECTOR_TYPE_MEDIAN;
			return getDataVector();
		}
	}

	/* (non-Javadoc)
	 * @see marf.Storage.Cluster#addFeatureVector(double[], java.lang.String, int)
	 * @since 0.3.0.6
	 */
	public boolean addFeatureVector(double[] padFeatureVector, String pstrFilename, int piSubjectID)
	{
		// What if piSubjectID is different from the one in this.iSubjectID?
		if(size() == 0)
		{
			this.iSubjectID = piSubjectID;
		}
		else
		{
			assert this.iSubjectID == piSubjectID;
		}
		
		/*
		 * If this file has been trained on already, no retraining
		 * required. Return false to indicate that no changes are made.
		 */
		if(addFilename(pstrFilename) == false)
		{
			return false;
		}

		// Dimensionality by X
		if(this.iMaxColumns < padFeatureVector.length)
		{
			this.iMaxColumns = padFeatureVector.length;
		}
		
		this.oFeatureVectors.add(padFeatureVector.clone());
		
		// Invalidate any previously compute mean or median vector
		this.iLastTypeVectorComputed = CACHED_VECTOR_TYPE_INVALID;

		return true;
	}

	/* (non-Javadoc)
	 * @see marf.Storage.Cluster#getMeanCount()
	 * @since 0.3.0.6
	 */
	public int getMeanCount()
	{
		return size();
	}

	/**
	 * Implements Cloneable interface for the FeatureSet object.
	 * Performs a "deep" copy of this object including all the feature vectors.
	 * @see java.lang.Object#clone()
	 * @since 0.3.0.5
	 */
	@SuppressWarnings("unchecked")
	public Object clone()
	{
		//try
		{
			FeatureSet oClone = (FeatureSet)super.clone();

			oClone.oFeatureVectors =
				this.oFeatureVectors == null ?
				null : (Vector<double[]>)this.oFeatureVectors.clone();

			return oClone;
		}
		/*
		catch(CloneNotSupportedException e)
		{
			throw new InternalError(e.getMessage());
		}*/
	}

	/**
	 * Provides string representation of the training set data in addition
	 * to that of the parent Cluster.
	 * @see marf.Storage.Cluster#toString()
	 * @since 0.3.0.6
	 */
	public synchronized String toString()
	{
		StringBuffer oBuffer = new StringBuffer(super.toString());

		oBuffer
			.append("Max Columns: ").append(this.iMaxColumns).append("\n")
			.append("Last Type Vector Computed: ").append(this.iLastTypeVectorComputed).append("\n")
			.append("Feature Vectors: ").append(this.oFeatureVectors).append("\n")
			.append("FeatureSet Source code revision: ").append(getMARFSourceCodeRevision()).append("\n");

		return oBuffer.toString();
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.20 $";
	}
}

// EOF
