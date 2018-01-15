package marf.math;

import marf.util.Debug;


/**
 * <p>Algebraic operations on ComplexVectors.</p>
 * <p><b>NOTE:</b> this class has the same considerations as
 * <code>marf.math.ComplexMatrix</code>.</p>
 *
 * <p>$Id: ComplexVector.java,v 1.3 2007/12/18 03:45:42 mokhov Exp $</p>
 *
 * @author Serguei A. Mokhov
 * @since 0.3.0.6
 * @version $Revision: 1.3 $
 * @see ComplexMatrix
 */
public class ComplexVector
extends ComplexMatrix
{
    /**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 */
	//private static final long serial Versi onUID = -220324533429950897L;

	/**
	 * Default ComplexVector's length is 3 elements.
	 */
	public static final int DEFAULT_VECTOR_LENGTH = 3;

	// Object Live Cycle

	/**
	 * Constructs a ComplexVector of default length of 3.
	 */
	public ComplexVector()
	{
		this(DEFAULT_VECTOR_LENGTH);
	}

	/**
	 * Constructs a ComplexVector with the specified length.
	 * @param piComplexVectorLength require ComplexVector length
	 */
	public ComplexVector(final int piComplexVectorLength)
	{
		super(piComplexVectorLength, 1);
	}

	/**
	 * Constructs a ComplexVector from another ComplexVector; a-la copy conctructor.
	 * @param poComplexVector a ComplexVector to make a copy of
	 */
	public ComplexVector(final ComplexVector poComplexVector)
	{
		super(poComplexVector);
	}

	/**
	 * Constructs a ComplexVector out of a matrix and forces the rows
	 * dimension to 1 and "flattens" the matrix to have the number
	 * of columns as the same number of elements in the original
	 * matrix.
	 * @param poMatrix base matrix to construct the ComplexVector from
	 */
	public ComplexVector(final Matrix poMatrix)
	{
		super(poMatrix);
		this.iRows = 1;
		this.iCols = poMatrix.getElements();
	}

	/**
	 * Constructs a ComplexVector out of a raw array of doubles.
	 * @param padComplexVectorData source data for the ComplexVector
	 */
	public ComplexVector(final double[] padComplexVectorData)
	{
		super(padComplexVectorData);
	}

	/**
	 * Allows having a transposed ComplexVector upon construction.
	 * Useful for example before doing linear operations.
	 * @param pad1DMatrix ComplexVector data
	 * @param pbTransposed <code>true</code> if the transposed ComplexVector is desired
	 */
	public ComplexVector(double[] pad1DMatrix, boolean pbTransposed)
	{
		super(pad1DMatrix, pbTransposed);
	}

	// ComplexVector Specific Operations

	/**
	 * Allows getting the value of ComplexVector's element at specified position.
	 * @param piPosition index to get the element from
	 * @return ComplexVector element's value
	 */
	public final double getElement(final int piPosition)
	{
		return this.iRows == 1 ?
			super.getElement(0, piPosition) :
			super.getElement(piPosition, 0);
	}

	/**
	 * @param piPosition
	 * @return
	 */
	public final ComplexNumber getComplexElement(final int piPosition)
	{
		return this.iRows == 1 ?
			super.getComplexElement(0, piPosition) :
			super.getComplexElement(piPosition, 0);
	}

	/**
	 * Allows setting the value of ComplexVector's element at specified position.
	 * @param piPosition index to det the element at
	 * @param pdValue the element's value
	 */
	public void setElement(final int piPosition, final double pdValue)
	{
		if(this.iRows == 1)
		{
			super.setElement(0, piPosition, pdValue);
		}
		else
		{
			super.setElement(piPosition, 0, pdValue);
		}
	}

	/**
	 * Retrieves the mathematical length of the ComplexVector.
	 * @return the length value
	 */
	public double getLength()
	{
		double dSqSum = 0.0;

		for(int i = 0; i < getElements(); i++)
		{
			dSqSum += this.adMatrix[i] * this.adMatrix[i];
		}

		return Math.sqrt(dSqSum);
	}

	/**
	 * V1 = this + V.
	 * @param poComplexVector V
	 * @return V1
	 */
	public ComplexVector add(final ComplexVector poComplexVector)
	{
		return add(this, poComplexVector);
	}

	/**
	 * V3 = V1 + V2.
	 * @param poLHSComplexVector V1
	 * @param poRHSComplexVector V2
	 * @return V3
	 */
	public static ComplexVector add(final ComplexVector poLHSComplexVector, final ComplexVector poRHSComplexVector)
	{
		return new ComplexVector(Matrix.add(poLHSComplexVector, poRHSComplexVector));
	}

	/**
	 * Returns a unit-ComplexVector representation of this ComplexVector.
	 * This ComplexVector remains intact, instead a "unitized" copy is returned.
	 * @return corresponding unit ComplexVector
	 */
	public final ComplexVector getUnitComplexVector()
	{
		ComplexVector oUnitComplexVector = new ComplexVector(this);
		oUnitComplexVector.normalize();
		return oUnitComplexVector;
	}

	/**
	 * Calculates an inner product of two ComplexVectors, V3 = V1 * V2.
	 * @param poLHSComplexVector first ComplexVector, V1
	 * @param poRHSComplexVector second ComplexVector, V2
	 * @return the inner product ComplexVector of the two, V3
	 */
	public static ComplexVector getInnerProduct(final ComplexVector poLHSComplexVector, final ComplexVector poRHSComplexVector)
	{
		ComplexVector oXComplexVector = new ComplexVector();

		oXComplexVector.setElement(0, (poLHSComplexVector.getElement(1) * poRHSComplexVector.getElement(2) - poLHSComplexVector.getElement(2) * poRHSComplexVector.getElement(1)));
		oXComplexVector.setElement(1, (poLHSComplexVector.getElement(2) * poRHSComplexVector.getElement(0) - poLHSComplexVector.getElement(0) * poRHSComplexVector.getElement(2)));
		oXComplexVector.setElement(2, (poLHSComplexVector.getElement(0) * poRHSComplexVector.getElement(1) - poLHSComplexVector.getElement(1) * poRHSComplexVector.getElement(0)));

		return oXComplexVector;
	}

	/**
	 * Calculates the cross product of two ComplexVectors, V3 = V1 x V2.
	 * TODO: complete all cases.
	 *
	 * @param poLHSComplexVector first ComplexVector, V1
	 * @param poRHSComplexVector second ComplexVector, V2
	 * @return the cross product, V3
	 */
	public static ComplexVector getCrossProduct(final ComplexVector poLHSComplexVector, final ComplexVector poRHSComplexVector)
	{
		Debug.debug("ComplexVector.getInnerProduct() - WARNING: Implementation is incomplete!\n");

		if(poLHSComplexVector.getElements() != poRHSComplexVector.getElements())
		{
			return new ComplexVector(0);
		}

		ComplexVector oComplexVectorIP = new ComplexVector(poLHSComplexVector.getElements());

		for(int i = 0; i < oComplexVectorIP.getElements(); i++)
		{
			oComplexVectorIP.setElement(i, poLHSComplexVector.getElement(i) * poRHSComplexVector.getElement(i));
		}

		return oComplexVectorIP;
	}

	/**
	 * Calculates the dot product of two ComplexVectors, d = V1 * V2.
	 * @param poLHSComplexVector first ComplexVector, V1
	 * @param poRHSComplexVector second ComplexVector, V2
	 * @return the dot product, d
	 */
	public static double getDotProduct(final ComplexVector poLHSComplexVector, final ComplexVector poRHSComplexVector)
	{
		double dDotProduct = 0.0;

		if(poLHSComplexVector.getElements() != poRHSComplexVector.getElements())
		{
			Debug.debug
			(
				"ComplexVector.getDotProduct() - WARNING: Number of elements in ComplexVectors do not match: lhs=" +
				poLHSComplexVector.getElements() + ", rhs=" + poRHSComplexVector.getElements() + "\n"
			);

			return -1.0;
		}

		for(int i = 0; i < poLHSComplexVector.getElements(); i++)
		{
			dDotProduct += poLHSComplexVector.getElement(i) * poRHSComplexVector.getElement(i);
		}

		return dDotProduct;
	}

	/**
	 * Tells whether this and the parameter ComplexVectors are orthogonal or not.
	 * @param poComplexVector another ComplexVector to compare to
	 * @return <code>true</code> if the ComplexVectors are orthogonal
	 */
	public boolean isOrthogonal(final ComplexVector poComplexVector)
	{
		return (getDotProduct(this, poComplexVector) == 0);
	}

	/**
	 * Normalizes the ComplexVector's components by its length.
	 */
	public void normalize()
	{
		double dLength = getLength();

		// If fraction is too small avoid division by zero
		// and just set all elements to zero...
		if(Math.abs(dLength) < Double.MIN_VALUE)
		{
			setAll();
		}

		// ... or divide by the length otherwise
		else
		{
			for(int i = 0; i < this.iCols * this.iRows; i++)
			{
				this.adMatrix[i] /= dLength;
			}
		}
	}

	/**
	 * Creates a unit-ComplexVector "i".
	 * @return a new ComplexVector instante of {1, 0, 0}
	 */
	public final static ComplexVector i()
	{
		return new ComplexVector(new double[] {1, 0, 0});
	}

	/**
	 * Creates a unit-ComplexVector "j".
	 * @return a new ComplexVector instante of {0, 1, 0}
	 */
	public final static ComplexVector j()
	{
		return new ComplexVector(new double[] {0, 1, 0});
	}

	/**
	 * Creates a unit-ComplexVector "k".
	 * @return a new ComplexVector instante of {0, 0, 1}
	 */
	public final static ComplexVector k()
	{
		return new ComplexVector(new double[] {0, 0, 1});
	}

	/**
	 * Implements Clonable interface.
	 * Creates a deep copy of the object by cloning the internal matrix array.
	 * @return object copy of this ComplexVector
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		return new ComplexVector(this);
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
