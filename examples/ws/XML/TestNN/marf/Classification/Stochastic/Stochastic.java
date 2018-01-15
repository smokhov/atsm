package marf.Classification.Stochastic;

import marf.Classification.Classification;
import marf.Classification.ClassificationException;
import marf.FeatureExtraction.IFeatureExtraction;
import marf.Storage.Result;
import marf.util.NotImplementedException;


/**
 * <p>Generic Stochastic Classification Module.</p>
 * <p>TODO: partially implemented.</p>
 *
 * $Id: Stochastic.java,v 1.26 2007/12/31 00:17:05 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.26 $
 * @since 0.0.1
 */
public class Stochastic
extends Classification
{
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = -415255678695729045L;

	/**
	 * Stochastic Constructor.
	 * @param poFeatureExtraction FeatureExtraction module reference
	 */
	public Stochastic(IFeatureExtraction poFeatureExtraction)
	{
		super(poFeatureExtraction);
	}

	/**
	 * Not Implemented.
	 * @throws NotImplementedException
	 * @throws ClassificationException never thrown
	 * @return nothing
	 * @since 0.3.0.6
	 */
	public boolean classify(double[] padFeatureVector)
	throws ClassificationException
	{
		throw new NotImplementedException(this, "classify()");
	}

	/**
	 * Not Implemented.
	 * @throws NotImplementedException
	 * @throws ClassificationException never thrown
	 * @return nothing
	 * @since 0.3.0.6
	 */
	public boolean train(double[] padFeatureVector)
	throws ClassificationException
	{
		throw new NotImplementedException(this, "train()");
	}

	/**
	 * Retrieves the maximum-probability classification result.
	 * @return Result object
	 * @since 0.3.0.2
	 */
	public Result getResult()
	{
		return this.oResultSet.getMaximumResult();
	}

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.26 $";
	}
}

// EOF
