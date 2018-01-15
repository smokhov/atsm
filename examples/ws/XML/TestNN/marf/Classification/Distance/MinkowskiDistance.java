package marf.Classification.Distance;

import java.io.Serializable;
import java.util.Vector;

import marf.MARF;
import marf.FeatureExtraction.IFeatureExtraction;


/**
 * <p>Class MinkowskiDistance.</p>
 *
 * @author Serguei Mokhov
 * @version $Id: MinkowskiDistance.java,v 1.13 2012/07/09 03:53:32 mokhov Exp $
 * @since 0.2.0
 */
public class MinkowskiDistance
extends Distance
{
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = 5195894141171408028L;

	/**
	 * Minkowski Factor.
	 * Default is 3; r = 1 is Chebyshev distance, r = 2 is Euclidean distance.
	 */
	private double r = 3;

	/**
	 * MinkowskiDistance Constructor.
	 * @param poFeatureExtraction FeatureExtraction module reference
	 */
	public MinkowskiDistance(IFeatureExtraction poFeatureExtraction)
	{
		super(poFeatureExtraction);

		// See if there is a request for another r
		if(MARF.getModuleParams() != null)
		{
			Vector<Serializable> oParams = MARF.getModuleParams().getClassificationParams();

			if(oParams.size() > 1)
			{
				this.r = ((Double)oParams.elementAt(1)).doubleValue();
			}
		}
	}

	/**
	 * Minkowski Distance implementation.
	 * @param paVector1 first vector to compare
	 * @param paVector2 second vector to compare
	 * @return Minkowski distance between two feature vectors
	 */
	public final double distance(final double[] paVector1, final double[] paVector2)
	{
		double dDistance = 0;

		for(int f = 0; f < paVector1.length; f++)
		{
			dDistance += Math.pow(Math.abs(paVector1[f] - paVector2[f]), this.r);
		}

		return Math.pow(dDistance, 1 / this.r);
	}

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.13 $";
	}
}

// EOF
