package marf.Classification;

import java.io.Serializable;
import java.util.Vector;

import marf.MARF;
import marf.FeatureExtraction.IFeatureExtraction;
import marf.Storage.ResultSet;
import marf.Storage.StorageException;
import marf.Storage.StorageManager;
import marf.Storage.TrainingSet;


/**
 * <p>Abstract Classification Module.
 * A generic implementation of the <code>IClassification</code> interface.
 * The derivatives must inherit from this class, and if they cannot,
 * they should implement <code>IClassification</code> themselves. 
 * </p>
 *
 * @author Serguei Mokhov
 * @version $Id: Classification.java,v 1.54 2012/07/18 16:00:20 mokhov Exp $
 * @since 0.0.1
 */
public abstract class Classification
extends StorageManager
implements IClassification
{
	/* Data Members */

	/**
	 * Reference to the enclosed FeatureExtraction object.
	 */
	protected IFeatureExtraction oFeatureExtraction = null;

	/**
	 * TrainingSet Container.
	 */
	protected TrainingSet oTrainingSet = null;

	/**
	 * Local reference to the array of features, either obtained
	 * from the feature extraction module or passed directly to
	 * train() or classify.
	 * Used in prevention of NullPointerException, bug #1539695.
	 * @since 0.3.0.6
	 * @see #train(double[])
	 * @see IClassification#classify(double[])
	 */
	protected double[] adFeatureVector = null;
	
	/**
	 * Classification result set. May contain
	 * one or more results (in case of similarity).
	 *
	 * @since 0.3.0.2
	 */
	protected ResultSet oResultSet = new ResultSet();

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.5
	 */
	private static final long serialVersionUID = 7933249658173204609L;

	/* Constructors */

	/**
	 * Generic Classification Constructor.
	 * @param poFeatureExtraction FeatureExtraction module reference; may be null
	 */
	protected Classification(IFeatureExtraction poFeatureExtraction)
	{
		this.oFeatureExtraction = poFeatureExtraction;

		// See if there is a request for dump format
		if(MARF.getModuleParams() != null)
		{
			Vector<Serializable> oParams = MARF.getModuleParams().getClassificationParams();

			// TODO: Must be validated of what's coming in
			if(oParams != null && oParams.size() > 0)
			{
				this.iCurrentDumpMode = ((Integer)oParams.elementAt(0)).intValue();
			}
		}
	}

	/* Classification API */

	/**
	 * Generic training routine for building/updating
	 * mean vectors in the training set. Assumes presence
	 * of a non-null feature extraction module for pipelining.
	 * Can be overridden, and if the overriding classifier is using
	 * <code>TrainingSet</code>, it should call <code>super.train();</code>
	 *
	 * @return <code>true</code> if training was successful
	 * (i.e. mean vector was updated); <code>false</code> otherwise
	 * @throws ClassificationException if there was a problem while training
	 * @see TrainingSet
	 */
	public boolean train()
	throws ClassificationException
	{
		return train(this.oFeatureExtraction.getFeaturesArray());
	}

	/**
	 * Generic training routine for building/updating
	 * mean vectors in the training set.
	 * Can be overridden, and if the overriding classifier is using
	 * <code>TrainingSet</code>, it should call <code>super.train();</code>
	 *
	 * @param padFeatureVector feature vector to train on
	 * @return <code>true</code> if training was successful
	 * (i.e. mean vector was updated); <code>false</code> otherwise
	 * @throws ClassificationException if there was a problem while training
	 * @see TrainingSet
	 * @since 0.3.0.6
	 */
	public boolean train(double[] padFeatureVector)
	throws ClassificationException
	{
		// For exception handling
		String strPhase = "[start]";
		
		// Bug #1539695
		this.adFeatureVector = padFeatureVector;

		/*
		 * It is important to use saveTrainingSet() and loadTrainingSet()
		 * throughout this method, as the dump() and restore() may easily
		 * (and likely) to be overridden by the derivatives.
		 */
		try
		{
			if(this.oTrainingSet != null)
			{
				// Wrong global cluster loaded, reload the correct one.
				if
				(
					(this.oTrainingSet.getPreprocessingMethod() != MARF.getPreprocessingMethod())
					||
					(this.oTrainingSet.getFeatureExtractionMethod() != MARF.getFeatureExtractionMethod())
				)
				{
					strPhase = "[dumping previous cluster]";

					saveTrainingSet();
					this.oTrainingSet = null;
				}
			}

			strPhase = "[restoring training set]";
			loadTrainingSet();

			// Add the new feature vector.
			strPhase = "[adding feature vector]";

			boolean bVectorAdded = this.oTrainingSet.addFeatureVector
			(
				this.adFeatureVector,
				MARF.getSampleFile(),
				MARF.getCurrentSubject(),
				MARF.getPreprocessingMethod(),
				MARF.getFeatureExtractionMethod()
			);

			// No point of doing I/O if we didn't add anything.
			if(bVectorAdded == true)
			{
				strPhase = "[dumping updated training set]";
				saveTrainingSet();
			}

			return true;
		}
		catch(NullPointerException e)
		{
			e.printStackTrace(System.err);

			throw new ClassificationException
			(
				new StringBuffer()
					.append("NullPointerException in Classification.train(): oTrainingSet = ")
					.append(this.oTrainingSet)
					.append(", oFeatureExtraction = ").append(this.oFeatureExtraction)
					.append(", FeaturesArray = ").append(this.adFeatureVector)
					.append(", phase: ").append(strPhase)
					.toString()
			);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			throw new ClassificationException("Phase: " + strPhase, e);
		}
	}

	/**
	 * Generic classification routine that assumes a presence of
	 * a valid non-null feature extraction module for pipeline operation.
	 * @see marf.Classification.IClassification#classify()
	 * @since 0.3.0.6
	 */
	public boolean classify()
	throws ClassificationException
	{
		return classify(this.oFeatureExtraction.getFeaturesArray());
	}

	/* From Storage Manager */

	/**
	 * Generic implementation of dump() to dump the TrainingSet.
	 * @since 0.2.0
	 * @throws StorageException if there's a problem saving training set to disk
	 */
	public void dump()
	throws StorageException
	{
		switch(this.iCurrentDumpMode)
		{
			case DUMP_GZIP_BINARY:
			case DUMP_BINARY:
				saveTrainingSet();
				break;

			default:
				super.dump();
		}
	}

	/**
	 * Generic implementation of restore() for TrainingSet.
	 * @since 0.2.0
	 * @throws StorageException if there is a problem loading the training set from disk
	 */
	public void restore()
	throws StorageException
	{
		switch(this.iCurrentDumpMode)
		{
			case DUMP_GZIP_BINARY:
			case DUMP_BINARY:
				loadTrainingSet();
				break;

			default:
				super.restore();
		}
	}

	/**
	 * Saves TrainingSet to a file. Called by <code>dump()</code>.
	 * @since 0.2.0
	 * @throws StorageException if there's a problem saving training set to disk
	 * @see #dump()
	 * @see TrainingSet
	 */
	private final void saveTrainingSet()
	throws StorageException
	{
		try
		{
			// Dump stuff is there's anything to dump
			if(this.oTrainingSet != null)
			{
				this.oTrainingSet.setDumpMode(this.iCurrentDumpMode);
				this.oTrainingSet.setFilename(getTrainingSetFilename());
				this.oTrainingSet.dump();
			}

			// TODO: if TrainingSet is null
			else
			{
				// [SM, 2003-05-02] Should here be something? Like a debug() call or
				// more severe things?
				System.err.println
				(
					"WARNING: Classification.saveTrainingSet() -- TrainingSet is null.\n" +
					"         No TrainingSet is saved."
				);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			throw new StorageException(e);
		}
	}

	/**
	 * Loads TrainingSet from a file. Called by <code>restore()</code>.
	 * @since 0.2.0
	 * @throws StorageException if there is a problem loading the training set from disk
	 * @see #restore()
	 */
	private final void loadTrainingSet()
	throws StorageException
	{
		try
		{
			if(this.oTrainingSet == null)
			{
				this.oTrainingSet = loadTrainingSet(this.iCurrentDumpMode, getTrainingSetFilename());
			}

			//TODO: if TrainingSet is not null
			else
			{
				// [SM, 2003-05-02] Should here be something? Like a debug() call or
				// more severe things?
				System.err.println
				(
					"WARNING: Classification.loadTrainingSet() -- TrainingSet is not null.\n" +
					"         No TrainingSet is loaded."
				);
			}
		}
		catch(StorageException e)
		{
			e.printStackTrace(System.err);
			throw e;
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			throw new StorageException(e);
		}
	}

	/**
	 * Allows loading of the training sets for
	 * debugging and browsing purposes by external
	 * classes.
	 *
	 * @param piDumpMode
	 * @param pstrFilename
	 * @throws StorageException
	 * @return loaded training set bean if I/O was successful
	 * 
	 * @since 0.3.0.6
	 */
	public static TrainingSet loadTrainingSet(int piDumpMode, String pstrFilename)
	throws StorageException
	{
		try
		{
			TrainingSet oTrainingSet = new TrainingSet();
			oTrainingSet.setDumpMode(piDumpMode);
			oTrainingSet.setFilename(pstrFilename);
			oTrainingSet.restore();
			return oTrainingSet;
		}
		catch(StorageException e)
		{
			e.printStackTrace(System.err);
			throw e;
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			throw new StorageException(e);
		}
	}

	/**
	 * Retrieves the enclosed result set.
	 * @return the enclosed ResultSet object
	 * @since 0.3.0.2
	 */
	public ResultSet getResultSet()
	{
		return this.oResultSet;
	}

	/**
	 * Constructs a global cluster file name for the TrainingSet.
	 *
	 * <p>Filename is constructed using fully-qualified class of
	 * either TrainingSet or a classifier name with global
	 * clustering info such as preprocessing and feature
	 * extraction methods, so that only that cluster can be reloaded
	 * after.</p>
	 *
	 * May be overridden by the derivatives when necessary.
	 *
	 * @return String, filename
	 * @since 0.2.0
	 */
	protected String getTrainingSetFilename()
	{
		// Prevents NullPointerException, bug #1539695
		int iFeaturesCount
			= this.oFeatureExtraction == null
			? (this.adFeatureVector == null ? 0 : this.adFeatureVector.length)
			: this.oFeatureExtraction.getFeaturesArray().length;

		// For comparison, distinguish samples with or without
		// noise and silence removed
		int iNoiseRemoved = 0;
		int iSilenceRemoved = 0;

		if(MARF.getModuleParams() != null)
		{
			Vector<Serializable> oPreprocessingParams = MARF.getModuleParams().getPreprocessingParams();
			
			if(oPreprocessingParams != null)
			{
				switch(oPreprocessingParams.size())
				{
					case 0:
						break;
					
					case 1:
					{
						if(oPreprocessingParams.firstElement() instanceof Boolean)
						{
							iNoiseRemoved = ((Boolean)oPreprocessingParams.firstElement()).booleanValue() == true ? 1 : 0;
						}
						
						break;
					}
					
					default:
					{
						if(oPreprocessingParams.firstElement() instanceof Boolean)
						{
							iNoiseRemoved = ((Boolean)oPreprocessingParams.firstElement()).booleanValue() == true ? 1 : 0;
						}
						
						if(oPreprocessingParams.elementAt(1) instanceof Boolean)
						{
							iSilenceRemoved = ((Boolean)oPreprocessingParams.elementAt(1)).booleanValue() == true ? 1 : 0;
						}

						break;
					}
				}
			}
		}

		return new StringBuffer()
			// Any prefix specified by an application
			.append(MARF.getTrainingSetFilenamePrefix())
			
			// Fully-qualified class name
			.append(TrainingSet.class.getName()).append(".")
			
			// Loaders affect the training sets, so put the loader info here;
			// all custom loader plug-ins also get the class name in the mix in
			// case there are more than one custom loader in the same application.
			.append(MARF.getSampleFormat())
			.append(MARF.getSampleFormat() == MARF.CUSTOM ? "-".concat(MARF.getSampleLoader().getClass().getName()) : "")
			.append(".")

			// Distinguish between samples with removed noise or silence
			// or both or none
			.append(iNoiseRemoved).append(".")
			.append(iSilenceRemoved).append(".")

			// Global cluster: <PR>.<FE>.<FVS>
			// For the same FE method we may have different feature vector sizes
			.append(MARF.getPreprocessingMethod()).append(".")
			.append(MARF.getFeatureExtractionMethod()).append(".")
			.append(iFeaturesCount).append(".")

			// Extension depending on the dump type
			.append(getDefaultExtension())
			.toString();
	}

	/**
	 * Retrieves the features source.
	 * @return returns the FeatureExtraction reference
	 * @since 0.3.0.4
	 */
	public IFeatureExtraction getFeatureExtraction()
	{
		return this.oFeatureExtraction;
	}

	/**
	 * Allows setting the features source.
	 * @param poFeatureExtraction the FeatureExtraction object to set
	 * @since 0.3.0.4
	 */
	public void setFeatureExtraction(IFeatureExtraction poFeatureExtraction)
	{
		this.oFeatureExtraction = poFeatureExtraction;
	}

	/**
	 * Implements Cloneable interface for the Classification object.
	 * The contained FeatureExtraction isn't cloned at this point,
	 * and is just assigned to the clone.
	 * @see java.lang.Object#clone()
	 * @since 0.3.0.5
	 */
	public Object clone()
	{
		Classification oClone = (Classification)super.clone();
		oClone.oResultSet = (ResultSet)this.oResultSet.clone();
		oClone.oTrainingSet = (TrainingSet)this.oTrainingSet.clone();
		oClone.oFeatureExtraction = this.oFeatureExtraction;
		return oClone;
	}

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.54 $";
	}
}

// EOF
