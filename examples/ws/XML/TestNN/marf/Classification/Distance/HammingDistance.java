package marf.Classification.Distance;

import marf.FeatureExtraction.IFeatureExtraction;
import marf.util.NotImplementedException;


/**
 * <p>Hamming Distance Classifier.</p>
 *
 * @author Serguei Mokhov
 * @version $Id: HammingDistance.java,v 1.3 2012/07/09 03:53:32 mokhov Exp $
 * @since 0.3.0.6
 */
public class HammingDistance
extends Distance
{
	/**
	 * For serialization versioning.
	 */
	private static final long serialVersionUID = -5941853501910208073L;

	/**
	 * Default allowed difference between two features of 0.01. 
	 */
	public static final double DEFAULT_ALLOWED_ERROR = 0.01;

	/**
	 * Indicates to compare double array elements in
	 * strict equality (e.h. using the == operator).
	 * This is the default.
	 */
	public static final int STRICT_DOUBLE = 0;

	/**
	 * Indicates to compare double array elements leniently
	 * allowing some margin for error. 
	 */
	public static final int LENIENT_DOUBLE = 1;

	/**
	 * Indicates to compare double array for hamming
	 * distance in true bitwise expansion (i.e. bitwise comparison).
	 */
	public static final int STRICT_BITWISE = 2;

	
	protected int iDistanceType = LENIENT_DOUBLE;//STRICT_DOUBLE; 
	protected double dAllowedError = DEFAULT_ALLOWED_ERROR; 

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 */
//	private static final long serialVersionUID = 4805074823902537333L;

	
	/**
	 * Hamming Distance Constructor.
	 * @param poFeatureExtraction FeatureExtraction module reference
	 */
	public HammingDistance(IFeatureExtraction poFeatureExtraction)
	{
		super(poFeatureExtraction);
	}

	/**
	 * Hamming Distance implementation.
	 * @param padVector1 first vector to compare
	 * @param padVector2 second vector to compare
	 * @return diff-distance between two feature vectors
	 */
	public final double distance(final double[] padVector1, final double[] padVector2)
	{
		double dDistance = 0;

		for(int i = 0; i < padVector1.length; i++)
		{
			switch(this.iDistanceType)
			{
				case STRICT_DOUBLE:
				{
					if(padVector1[i] != padVector2[i])
					{
						dDistance += 1;
					}

					break;
				}

				case LENIENT_DOUBLE:
				{
					if(Math.abs(padVector1[i] - padVector2[i]) > this.dAllowedError)
					{
						dDistance += 1;
					}

					break;
				}

				case STRICT_BITWISE:
				{
					// XOR bitwise each element and count how many 1's left;
					// accumulate that count in dDistance
					throw new NotImplementedException();
//					break;
				}
				
				default:
				{
					assert false : "Mode " + this.iDistanceType + " unsupported.";
				}
			}
		}

		return dDistance;
	}

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.3 $";
	}
}

// EOF
