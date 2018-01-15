package marf.FeatureExtraction.Cepstral;

import marf.FeatureExtraction.FeatureExtraction;
import marf.FeatureExtraction.FeatureExtractionException;
import marf.Preprocessing.IPreprocessing;
import marf.util.NotImplementedException;


/**
 * <p>Cepstral Analysis.</p>
 *
 * $Id: Cepstral.java,v 1.14 2006/08/04 03:31:04 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.14 $
 * @since 0.0.1
 */
public class Cepstral
extends FeatureExtraction
{
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = -1963503086097237420L;

	/**
	 * Cepstral Constructor.
	 * @param poPreprocessing Preprocessing module reference
	 */
	public Cepstral(IPreprocessing poPreprocessing)
	{
		super(poPreprocessing);
	}

	/**
	 * Not Implemented.
	 * @return nothing
	 * @throws NotImplementedException
	 * @throws FeatureExtractionException never thrown
	 * @see marf.FeatureExtraction.IFeatureExtraction#extractFeatures(double[])
	 * @since 0.3.0.6
	 */
	public final boolean extractFeatures(double[] padSampleData)
	throws FeatureExtractionException
	{
		throw new NotImplementedException("Cepstral.extractFeatures()");
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.14 $";
	}
}

// EOF
