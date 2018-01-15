package marf.Storage;

import java.io.Serializable;


/**
 * <p>All training set beans should implement this interface.
 * </p>
 * 
 * $Id: ITrainingSample.java,v 1.2 2007/12/23 06:29:46 mokhov Exp $
 * 
 * @author Serguei Mokhov
 * @since 0.3.0.6
 * @version $Revision: 1.2 $
 */
public interface ITrainingSample
extends Serializable, Cloneable
{
	/**
	 * @param padFeatureVector
	 * @param pstrFilename
	 * @param piSubjectID
	 * @return
	 */
	boolean setFeatureVector(double[] padFeatureVector, String pstrFilename, int piSubjectID);

	/**
	 * Fully equivalent to setFeatureVector().
	 * @param padFeatureVector
	 * @param pstrFilename
	 * @param piSubjectID
	 * @return
	 * 
	 * @see #setFeatureVector(double[], String, int)
	 */
	boolean addFeatureVector(double[] padFeatureVector, String pstrFilename, int piSubjectID);

	/**
	 * Sets a filename of the training sample.
	 * Always set the first element of the list of filenames.
	 * @param pstrFilename filename to set
	 */
	void setFilename(String pstrFilename);

	/**
	 * @param pstrFilename
	 * @return
	 */
	boolean addFilename(String pstrFilename);

	/**
	 * Checks existence of the file in the training sample.
	 * Serves as an indication that we already trained on the given file.
	 * @param pstrFilename filename to check
	 * @return <code>true</code> if the filename is there; <code>false</code> if not
	 */
	boolean existsFilename(String pstrFilename);

	/**
	 * Retrieves Subject ID of a particular training sample.
	 * @return int ID
	 */
	int getSubjectID();

	/**
	 * Retrieves the data vector.
	 * @return array of doubles representing the data for this subject
	 */
	double[] getDataVector();

	/**
	 * Sets new Subject ID.
	 * @param piSubjectID integer ID
	 */
	void setSubjectID(final int piSubjectID);

	/**
	 * Sets new mean vector.
	 * @param padDataVector double array representing the mean vector
	 */
	void setDataVector(double[] padDataVector);
	
	/**
	 * Retrieves current mean count.
	 * @return mean count
	 */
	int getMeanCount();

	/**
	 * Retrieves the mean vector.
	 * @return array of doubles representing the mean for that cluster
	 */
	double[] getMeanVector();

	/**
	 * Sets new mean vector.
	 * @param padMeanVector double array representing the mean vector
	 */
//	void setMeanVector(double[] padMeanVector);

	/**
	 * @return
	 */
	double[] getMedianVector();
	
	/**
	 * @return
	 */
	int size();
}

// EOF
