package marf.FeatureExtraction;

import marf.Preprocessing.IPreprocessing;
import marf.Storage.FeatureSet;
import marf.Storage.StorageManager;


/**
 * <p>Generic Feature Extraction Module.
 * Every feature extraction module must extend this class; if it cannot
 * then they must implement the <code>IFeatureExtraction</code> interface.
 * </p>
 *
 * $Id: FeatureExtraction.java,v 1.38 2012/05/30 16:24:18 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.38 $
 * @since 0.0.1
 * 
 * @see IFeatureExtraction
 */
public abstract class FeatureExtraction
extends StorageManager
implements IFeatureExtraction
{
	/**
	 * Internal reference to the Preprocessing module.
	 */
	protected IPreprocessing oPreprocessing = null;

	/**
	 * An array of features extracted (coefficients and/or amplitude values).
	 */
	protected double[] adFeatures = null;

	/**
	 * A collection of feature vectors or sets for a subject.
	 * @since 0.3.0.6
	 */
	protected FeatureSet oFeatureSet = new FeatureSet();
	
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.5
	 */
	private static final long serialVersionUID = 7608607169147548576L;

	/**
	 * Main FeatureExtraction constructor.
	 * @param poPreprocessing preprocessing object ref.
	 */
	protected FeatureExtraction(IPreprocessing poPreprocessing)
	{
		this.oPreprocessing = poPreprocessing;
		this.iCurrentDumpMode = DUMP_GZIP_BINARY;
		this.oObjectToSerialize = this.adFeatures;
	}

	/**
	 * Provides default implementation of the API by using the sample data
	 * provided by the IPreprocessing module. The module should be set either
	 * through a constructor or a setter prior invocation.
	 * @see marf.FeatureExtraction.IFeatureExtraction#extractFeatures()
	 * @since 0.3.0.6
	 */
	public boolean extractFeatures()
	throws FeatureExtractionException
	{
		return this.extractFeatures(this.oPreprocessing.getSample().getSampleArray());
	}


	/**
	 * Allows retrieval of the internal feature vector.
	 * @return array of features (<code>double</code> values)
	 */
	public final double[] getFeaturesArray()
	{
		return this.adFeatures;
	}

	/**
	 * Retrieves inner preprocessing reference.
	 * @return the preprocessing reference
	 * @since 0.3.0.4
	 */
	public IPreprocessing getPreprocessing()
	{
		return this.oPreprocessing;
	}

	/**
	 * Allows setting the source preprocessing module.
	 * @param poPreprocessing the preprocessing object to set
	 * @since 0.3.0.4
	 */
	public void setPreprocessing(IPreprocessing poPreprocessing)
	{
		this.oPreprocessing = poPreprocessing;
	}

	/**
	 * Implementation of back-synchronization of loaded object.
	 * @since 0.3.0.3
	 * @see marf.Storage.StorageManager#backSynchronizeObject()
	 */
	public void backSynchronizeObject()
	{
		this.adFeatures = (double[])this.oObjectToSerialize;
	}

	/**
	 * Implements Cloneable interface for the FeatureExtraction object.
	 * The contained Preprocessing isn't cloned at this point,
	 * and is just assigned to the clone.
	 * @see java.lang.Object#clone()
	 * @since 0.3.0.5
	 */
	public Object clone()
	{
		FeatureExtraction oClone = (FeatureExtraction)super.clone();
		oClone.adFeatures = (double[])this.adFeatures.clone();
		oClone.oPreprocessing = this.oPreprocessing;
		return oClone;
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.38 $";
	}
}

// EOF
