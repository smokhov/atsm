package marf.Classification;

import marf.FeatureExtraction.IFeatureExtraction;
import marf.Storage.Result;
import marf.Storage.ResultSet;


/**
 * <p>Classification Interface.</p>
 *
 * $Id: IClassification.java,v 1.6 2006/07/31 02:19:13 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.6 $
 * @since 0.3.0.3
 */
public interface IClassification
{
	/**
	 * Interface source code revision.
	 */
	String MARF_INTERFACE_CODE_REVISION = "$Revision: 1.6 $";

	/* Classification API */

	/**
	 * Generic classification routine.
	 * Assumes presence of a valid, non-null IFeatureExtraction module.
	 * 
	 * @return <code>true</code> if classification was successful; <code>false</code> otherwise
	 * @throws ClassificationException if there was an error while classifying
	 */
	boolean classify()
	throws ClassificationException;

	/**
	 * Generic training routine for building/updating
	 * mean vectors in the training set.
	 * Assumes presence of a valid, non-null IFeatureExtraction module.
	 *
	 * @return <code>true</code> if training was successful; <code>false</code> otherwise
	 * @throws ClassificationException if there was a problem while training
	 */
	boolean train()
	throws ClassificationException;

	/**
	 * Generic classification routine.
	 *
	 * @param padFeatureVector vector of features to compare with the stored ones
	 * @return <code>true</code> if classification was successful; <code>false</code> otherwise
	 * @throws ClassificationException if there was an error while classifying
	 * @since 0.3.0.6
	 */
	boolean classify(double[] padFeatureVector)
	throws ClassificationException;

	/**
	 * Generic training routine for building/updating
	 * mean vectors in the training set.
	 *
	 * @param padFeatureVector vector of features to train on
	 * @return <code>true</code> if training was successful; <code>false</code> otherwise
	 * @throws ClassificationException if there was a problem while training
	 * @since 0.3.0.6
	 */
	boolean train(double[] padFeatureVector)
	throws ClassificationException;

	/**
	 * Retrieves the likely classification result.
	 * If there were many, this will return the result with the
	 * highest statistical score or probability. The decision
	 * of whether to retrieve a maximum result (with maximum probability)
	 * or minimum result (with minimum distance) from the sample is
	 * left to be made by concrete implementations.
	 *
	 * @return Result object
	 */
	Result getResult();

	/**
	 * Retrieves the enclosed result set.
	 * @return the enclosed ResultSet object
	 */
	ResultSet getResultSet();

	/**
	 * Retrieves the features source.
	 * @return returns the FeatureExtraction reference
	 * @since 0.3.0.4
	 */
	IFeatureExtraction getFeatureExtraction();

	/**
	 * Allows setting the features source.
	 * @param poFeatureExtraction the FeatureExtraction object to set
	 * @since 0.3.0.4
	 */
	void setFeatureExtraction(IFeatureExtraction poFeatureExtraction);
}

// EOF
