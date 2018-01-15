package marf.math;

import marf.util.Arrays;
import marf.util.Debug;


/**
 * <p>Similarly to Matrix, this class implements nearly identical API
 * but for operations on matrices with complex numbers.</p>
 * 
 * @author Serguei Mokhov
 * @version $Id: ComplexMatrix.java,v 1.14 2011/11/21 20:51:20 mokhov Exp $
 * @since 0.3.0.6
 * @see Matrix
 */
public class ComplexMatrix
extends Matrix
{
	/**
	 * For serialization versioning. 
	 */
	private static final long serialVersionUID = -6416567531639737308L;
	
	/**
	 * The corresponding imaginary data to the real data array. 
	 */
	protected double[] adImaginaryMatrix = null;
	
	/**
	 * Default matrix.
	 * @see #ComplexMatrix(int, int)
	 * @see Matrix#DEFAULT_ROWS
	 * @see Matrix#DEFAULT_COLS
	 */
	public ComplexMatrix()
	{
		this(DEFAULT_ROWS, DEFAULT_COLS);
	}

	/**
	 * Matrix with specified rows and columns count.
	 * @param piRows number of rows
	 * @param piCols number of columns
	 * @see #ComplexMatrix(int, int, double)
	 */
	public ComplexMatrix(int piRows, int piCols)
	{
		this(piRows, piCols, 0.0);
	}

	/**
	 * @param piRows
	 * @param piCols
	 * @param pdFillValue
	 */
	public ComplexMatrix(int piRows, int piCols, double pdFillValue)
	{
		this(piRows, piCols, new ComplexNumber(pdFillValue));
	}

	/**
	 * @param piRows
	 * @param piCols
	 * @param poFillValue
	 */
	public ComplexMatrix(int piRows, int piCols, final ComplexNumber poFillValue)
	{
		this.iRows = piRows;
		this.iCols = piCols;
		this.adMatrix = new double[this.iRows * this.iCols];
		this.adImaginaryMatrix = new double[this.iRows * this.iCols];
		setAll(poFillValue);
	}

	/**
	 * @param poMatrix
	 */
	public ComplexMatrix(final Matrix poMatrix)
	{
		super(poMatrix);
		this.adImaginaryMatrix = new double[this.iRows * this.iCols];
	}

	/**
	 * @param poMatrix
	 */
	public ComplexMatrix(final ComplexMatrix poMatrix)
	{
		super(poMatrix);
		this.adImaginaryMatrix = (double[])poMatrix.adImaginaryMatrix.clone();
	}

	/**
	 * Constructs a complex vector with zero imaginary part.
	 * @param pad1DMatrix
	 */
	public ComplexMatrix(final double[] pad1DMatrix)
	{
		this(pad1DMatrix, false);
	}

	/**
	 * Constructs a complex vector.
	 * @param padRealMatrix
	 * @param padImaginaryMatrix
	 */
	public ComplexMatrix(final double[] padRealMatrix, final double[] padImaginaryMatrix)
	{
		super(padRealMatrix);
		this.adImaginaryMatrix = (double[])padImaginaryMatrix.clone();
	}
	
	public ComplexMatrix(final ComplexNumber[] pao1DMatrix)
	{
		this(pao1DMatrix, false);
	}

	/**
	 * @param pad1DMatrix
	 * @param pbTransposed
	 */
	public ComplexMatrix(final double[] pad1DMatrix, boolean pbTransposed)
	{
		super(pad1DMatrix, pbTransposed);
		this.adImaginaryMatrix = new double[this.iRows * this.iCols];
	}

	/**
	 * @param pao1DMatrix
	 * @param pbTransposed
	 */
	public ComplexMatrix(final ComplexNumber[] pao1DMatrix, boolean pbTransposed)
	{
		this.adMatrix = new double[pao1DMatrix.length];
		this.adImaginaryMatrix = new double[pao1DMatrix.length];
		
		if(pbTransposed == false)
		{
			this.iRows = 1;
			this.iCols = pao1DMatrix.length;
		}
		else
		{
			this.iRows = pao1DMatrix.length;
			this.iCols = 1;
		}
		
		for(int i = 0; i < pao1DMatrix.length; i++)
		{
			setComplexNumber(i, pao1DMatrix[i]);
		}
	}

	/**
	 * @param padd2DMatrix
	 */
	public ComplexMatrix(final double[][] padd2DMatrix)
	{
		super(padd2DMatrix);
		this.adImaginaryMatrix = new double[this.iRows * this.iCols];
	}

	/**
	 * @param paoo2DMatrix
	 */
	public ComplexMatrix(final ComplexNumber[][] paoo2DMatrix)
	{
		setMatrix2D(paoo2DMatrix);
	}

	/*
	 * ---------
	 * Operators
	 * ---------
	 */
	
	/**
	 * Adds a complex scalar to the matrix: this = this + Z.
	 * @param poNum the scalar Z
	 * @return new value of this matrix
	 */
	public ComplexMatrix add(final ComplexNumber poNum)
	{
		for(int i = 0; i < this.adMatrix.length; i++)
		{
			ComplexNumber oNumber = getComplexNumber(i);
			oNumber.add(poNum);
			setComplexNumber(i, oNumber);
		}
		
		return this;
	}

	/**
	 * Overrides the parent to apply complex number addition.
	 * @see marf.math.Matrix#applyAdd(marf.math.Matrix, marf.math.Matrix, int, int)
	 */
	protected Matrix applyAdd(final Matrix poLHSMatrix, final Matrix poRHSMatrix, int i, int j)
	{
		// Convert if necessary real matrices into complex prior
		// application of addition
		ComplexMatrix oLHSMatrix = getComplexMatrix(poLHSMatrix);
		ComplexMatrix oRHSMatrix = getComplexMatrix(poRHSMatrix);
		
		//poTargetMatrix.setElement(i, j, poLHSMatrix.getElement(i, j) + poRHSMatrix.getElement(i, j));
		setComplexElement(i, j, oLHSMatrix.getComplexElement(i, j).add(oRHSMatrix.getComplexElement(i, j)));
		
		return this;
	}
	
	/**
	 * A convenience wrapper of <code>Matrix.add()</code>.
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @return
	 * @see Matrix#add(Matrix, Matrix)
	 */
	public static ComplexMatrix add(final ComplexMatrix poLHSMatrix, final ComplexMatrix poRHSMatrix)
	{
		return (ComplexMatrix)Matrix.add(poLHSMatrix, poRHSMatrix);
	}

	/**
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @return
	 */
	public static ComplexMatrix add(final Matrix poLHSMatrix, final ComplexMatrix poRHSMatrix)
	{
//		return (ComplexMatrix)Matrix.add(poLHSMatrix, poRHSMatrix);
		return add(getComplexMatrix(poLHSMatrix), poRHSMatrix);
	}

	/**
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @return
	 */
	public static ComplexMatrix add(final ComplexMatrix poLHSMatrix, final Matrix poRHSMatrix)
	{
//		return (ComplexMatrix)Matrix.add(poLHSMatrix, poRHSMatrix);
		return add(poLHSMatrix, getComplexMatrix(poRHSMatrix));
	}

	
	/**
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @return
	 */
	public static ComplexMatrix minus(final ComplexMatrix poLHSMatrix, final ComplexMatrix poRHSMatrix)
	{
		return (ComplexMatrix)Matrix.minus(poLHSMatrix, poRHSMatrix);
	}

	/**
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @return
	 */
	public static ComplexMatrix minus(final Matrix poLHSMatrix, final ComplexMatrix poRHSMatrix)
	{
//		return (ComplexMatrix)Matrix.minus(poLHSMatrix, poRHSMatrix);
		return minus(getComplexMatrix(poLHSMatrix), poRHSMatrix);
	}
	
	/**
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @return
	 */
	public static ComplexMatrix minus(final ComplexMatrix poLHSMatrix, final Matrix poRHSMatrix)
	{
//		return (ComplexMatrix)Matrix.minus(poLHSMatrix, poRHSMatrix);
		return minus(poLHSMatrix, getComplexMatrix(poRHSMatrix));
	}


	/**
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @return
	 */
	public static ComplexMatrix multiply(final ComplexMatrix poLHSMatrix, final ComplexMatrix poRHSMatrix)
	{
		return (ComplexMatrix)Matrix.multiply(poLHSMatrix, poRHSMatrix);
	}

	/**
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @return
	 */
	public static ComplexMatrix multiply(final Matrix poLHSMatrix, final ComplexMatrix poRHSMatrix)
	{
//		return (ComplexMatrix)Matrix.multiply(poLHSMatrix, poRHSMatrix);
		return multiply(getComplexMatrix(poLHSMatrix), poRHSMatrix);
	}

	/**
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @return
	 */
	public static ComplexMatrix multiply(final ComplexMatrix poLHSMatrix, final Matrix poRHSMatrix)
	{
//		return (ComplexMatrix)Matrix.multiply(poLHSMatrix, poRHSMatrix);
		return multiply(poLHSMatrix, getComplexMatrix(poRHSMatrix));
	}


	/**
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @return
	 */
	public static ComplexMatrix divide(final ComplexMatrix poLHSMatrix, final ComplexMatrix poRHSMatrix)
	{
		return (ComplexMatrix)Matrix.divide(poLHSMatrix, poRHSMatrix);
	}

	/**
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @return
	 */
	public static ComplexMatrix divide(final Matrix poLHSMatrix, final ComplexMatrix poRHSMatrix)
	{
		//return (ComplexMatrix)Matrix.divide(poLHSMatrix, poRHSMatrix);
		return divide(getComplexMatrix(poLHSMatrix), poRHSMatrix);
	}

	/**
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @return
	 */
	public static ComplexMatrix divide(final ComplexMatrix poLHSMatrix, final Matrix poRHSMatrix)
	{
		//return (ComplexMatrix)Matrix.divide(poLHSMatrix, poRHSMatrix);
		return divide(poLHSMatrix, getComplexMatrix(poRHSMatrix));
	}
	
	
	/**
	 * @param poLHSMatrix
	 * @param pdNum
	 * @return
	 */
	public static ComplexMatrix add(final ComplexMatrix poLHSMatrix, double pdNum)
	{
		return (ComplexMatrix)Matrix.add(poLHSMatrix, pdNum);
	}
	
	/**
	 * @param poLHSMatrix
	 * @param pdNum
	 * @return
	 */
	public static ComplexMatrix add(final ComplexMatrix poLHSMatrix, final ComplexNumber pdNum)
	{
		ComplexMatrix oMatrix = new ComplexMatrix(poLHSMatrix);

		for(int i = 0; i < poLHSMatrix.adMatrix.length; i++)
		{
			//oMatrix.adMatrix[i] += pdNum;
			ComplexNumber oNumber = new ComplexNumber(oMatrix.adMatrix[i], oMatrix.adImaginaryMatrix[i]);
			oNumber.add(pdNum);
			oMatrix.setComplexNumber(i, pdNum);
		}
		
		return oMatrix; 
	}

	/**
	 * @param poMatrix
	 * @param pdNum
	 * @return
	 */
	public static ComplexMatrix multiply(final ComplexMatrix poMatrix, final double pdNum)
	{
		return (ComplexMatrix)Matrix.multiply(poMatrix, pdNum);
	}

	/**
	 * @param pdNum
	 * @param poMatrix
	 * @return
	 */
	public static ComplexMatrix multiply(final double pdNum, final ComplexMatrix poMatrix)
	{
		return multiply(poMatrix, pdNum);
	}

	/**
	 * @param poMatrix
	 * @param piPow
	 * @return
	 */
	public static ComplexMatrix pow(final ComplexMatrix poMatrix, int piPow)
	{
		return (ComplexMatrix)Matrix.pow(poMatrix, piPow);
	}
	
	/**
	 * @param piPow
	 * @return
	 */
	public ComplexMatrix powComplex(int piPow)
	{
		ComplexMatrix oMatrix = pow(this, piPow);
		setMatrixData(oMatrix);
		return this;
	}

	/**
	 * Overrides the parent to apply complex number addition.
	 * @see marf.math.Matrix#applyAdd(marf.math.Matrix, marf.math.Matrix, int, int)
	 */
	protected Matrix applyMinus(final Matrix poLHSMatrix, final Matrix poRHSMatrix, int i, int j)
	{
		// Convert if necessary real matrices into complex prior
		// application of addition
		ComplexMatrix oLHSMatrix = getComplexMatrix(poLHSMatrix);
		ComplexMatrix oRHSMatrix = getComplexMatrix(poRHSMatrix);
		
		//poTargetMatrix.setElement(i, j, poLHSMatrix.getElement(i, j) + poRHSMatrix.getElement(i, j));
		setComplexElement(i, j, oLHSMatrix.getComplexElement(i, j).subtract(oRHSMatrix.getComplexElement(i, j)));
		
		return this;
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#applyMultiply(marf.math.Matrix, marf.math.Matrix, int, int)
	 */
	protected Matrix applyMultiply(final Matrix poLHSMatrix, final Matrix poRHSMatrix, int i, int j)
	{
		//double dRowColSum = 0;
		ComplexNumber oRowColSum = new ComplexNumber();

		ComplexMatrix oLHSMatrix = getComplexMatrix(poLHSMatrix);
		ComplexMatrix oRHSMatrix = getComplexMatrix(poRHSMatrix);

		for(int k = 0; k < oLHSMatrix.iCols; k++)
		{
			oRowColSum.add(oLHSMatrix.getComplexElement(i, k).multiply(oRHSMatrix.getComplexElement(k, j)));
		}

		setComplexElement(i, j, oRowColSum);
		
		return this;
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#applyMultiply(marf.math.Matrix, marf.math.Matrix)
	 */
	protected Matrix applyMultiply(final Matrix poLHSMatrix, final Matrix poRHSMatrix)
	{
		//System.out.println("<< Complex ("+poLHSMatrix.iRows+"x"+poLHSMatrix.iCols+") * ("+poRHSMatrix.iRows+"x"+poRHSMatrix.iCols+") apply *      >>: " + new Date());
		ComplexMatrix oLHSMatrix = getComplexMatrix(poLHSMatrix);
		ComplexMatrix oRHSMatrix = getComplexMatrix(poRHSMatrix);

		for(int i = 0; i < iRows; i++)
		{
			for(int j = 0; j < iCols; j++)
			{
				//double dRowColSum = 0;
				ComplexNumber oRowColSum = new ComplexNumber();

				for(int k = 0; k < oLHSMatrix.iCols; k++)
				{
//					oRowColSum.add(oLHSMatrix.getComplexElement(i, k).multiply(oRHSMatrix.getComplexElement(k, j)));
					oRowColSum.add(ComplexNumber.multiply(oLHSMatrix.getComplexElement(i, k), oRHSMatrix.getComplexElement(k, j)));
				}

				setComplexElement(i, j, oRowColSum);
			}
		}

//		System.gc();

		//System.out.println("<< Complex ("+poLHSMatrix.iRows+"x"+poLHSMatrix.iCols+") * ("+poRHSMatrix.iRows+"x"+poRHSMatrix.iCols+") apply * done >>: " + new Date());
		return this;
	}

	/**
	 * Converts a real matrix into complex by making a copy.
	 * If the parameter is already a complex matrix; then just
	 * cast it and return it.
	 * @param poMatrix
	 * @return
	 */
	public static ComplexMatrix getComplexMatrix(Matrix poMatrix)
	{
		ComplexMatrix oMatrix;
		
		if(poMatrix instanceof ComplexMatrix)
		{
			oMatrix = (ComplexMatrix)poMatrix; 
		}
		else
		{
			oMatrix = new ComplexMatrix(poMatrix);
		}
		
		return oMatrix;
	}
	
	/**
	 * Subtracts a complex scalar from the matrix: this = this - Z.
	 * @param poNum the scalar Z
	 * @return new value of this matrix
	 */
	public ComplexMatrix minus(final ComplexNumber poNum)
	{
		for(int i = 0; i < this.adMatrix.length; i++)
		{
			ComplexNumber oNumber = getComplexNumber(i);
			oNumber.subtract(poNum);
			setComplexNumber(i, oNumber);
		}
		
		return this;
	}

	/**
	 * Multiplies the matrix by a complex scalar: this = this * Z.
	 * @param poNum the scalar Z
	 * @return new value of this matrix
	 */
	public ComplexMatrix multiply(final ComplexNumber poNum)
	{
		for(int i = 0; i < this.adMatrix.length; i++)
		{
			ComplexNumber oNumber = getComplexNumber(i);
			oNumber.multiply(poNum);
			setComplexNumber(i, oNumber);
		}
		
		return this;
	}

	/**
	 * Divides the matrix by a complex scalar: this = this / Z.
	 * @param poNum the scalar Z
	 * @return new value of this matrix
	 */
	public ComplexMatrix divide(final ComplexNumber poNum)
	{
		for(int i = 0; i < this.adMatrix.length; i++)
		{
			ComplexNumber oNumber = getComplexNumber(i);
			oNumber.divide(poNum);
			setComplexNumber(i, oNumber);
		}
		
		return this;
	}

	/**
	 * @param iRawOffset
	 * @return
	 */
	protected ComplexNumber getComplexNumber(int iRawOffset)
	{
		return new ComplexNumber(this.adMatrix[iRawOffset], this.adImaginaryMatrix[iRawOffset]);
	}
	
	/**
	 * @param iRawOffset
	 * @param poNumber
	 */
	protected void setComplexNumber(int iRawOffset, final ComplexNumber poNumber)
	{
		this.adMatrix[iRawOffset] = poNumber.dReal;
		this.adImaginaryMatrix[iRawOffset] = poNumber.dImaginary;
	}

	/**
	 * @see marf.math.Matrix#clone()
	 */
	public Object clone()
	{
		return new ComplexMatrix(this);
	}

	/**
	 * @see marf.math.Matrix#crop(int, int, int, int)
	 */
	public boolean crop(int piLeft, int piTop, int piRight, int piBottom)
	{
//		boolean bChanges = crop(this.adImaginaryMatrix, this.iRows, this.iCols, piLeft, piTop, piRight, piBottom);
//		bChanges |= super.crop(piLeft, piTop, piRight, piBottom);
//		return bChanges;
		Matrix oMatrix = crop(this.adImaginaryMatrix, this.iRows, this.iCols, piLeft, piTop, piRight, piBottom);
		setImaginaryMatrixArray(oMatrix.adMatrix);
		
		boolean bChanges /*|=*/ = super.crop(piLeft, piTop, piRight, piBottom);
		return bChanges;
	}

	/**
	 * @param poMatrix
	 * @return
	 */
	private Matrix getMatrixCopy(final Matrix poMatrix)
	{
		return new ComplexMatrix(poMatrix);
	}

	/**
	 * @param poMatrix
	 */
	private void setMatrixData(final Matrix poMatrix)
	{
		setRealMatrixArray(poMatrix.adMatrix);
		setImaginaryMatrixArray(((ComplexMatrix)poMatrix).adImaginaryMatrix);
	}
	
	/* (non-Javadoc)
	 * @see marf.math.Matrix#getNewMatrix()
	 */
	protected Matrix getNewMatrix()
	{
		//System.out.println("[[[ in default ComplexMatrix ]]]");
		return new ComplexMatrix(this.iRows, this.iCols);		
	}
	
	/* (non-Javadoc)
	 * @see marf.math.Matrix#getNewMatrix(int, int)
	 */
	protected /*static*/ Matrix getNewMatrix(int piRows, int piCols)
	{
		//System.out.println("[[[ in ComplexMatrix ]]]");
		return new ComplexMatrix(piRows, piCols);
	}

	/**
	 * @param poSourceMatrix
	 * @param i
	 * @param j
	 */
	private void swapElement(Matrix poSourceMatrix, int i, int j)
	{
		setComplexElement(j, i, ((ComplexMatrix)poSourceMatrix).getComplexElement(i, j));
	}
	
	/* (non-Javadoc)
	 * @see marf.math.Matrix#divide(double)
	 */
	public Matrix divide(double pdNum)
	{
		// TODO Auto-generated method stub
		return super.divide(pdNum);
	}

	/**
	 * @see marf.math.Matrix#equals(marf.math.Matrix)
	 */
	public boolean equals(Matrix poMatrix)
	{
		boolean bEquals = super.equals(poMatrix);
		
		if(poMatrix instanceof ComplexMatrix)
		{
			bEquals |= Arrays.equals(this.adImaginaryMatrix, ((ComplexMatrix)poMatrix).adImaginaryMatrix);
		}
		else
		{
			// Imaginary array of this must be all zeros if
			// comparing to an real matrix
			for(int i = 0; i < this.adImaginaryMatrix.length; i++)
			{
				if(this.adImaginaryMatrix[i] != 0)
				{
					bEquals = false;
					break;
				}
			}
		}

		return bEquals;
	}
	
	/**
	 * @see marf.math.Matrix#exhaustMatrix()
	 */
	public void exhaustMatrix()
	{
		this.adImaginaryMatrix = null;
		super.exhaustMatrix();
	}

	/**
	 * @see marf.math.Matrix#extend(marf.math.Matrix, marf.math.Matrix.Direction)
	 */
	public boolean extend(Matrix poExtensionMatrix, Direction poDirection)
	{
		// Extend the imaginary part

		double[] adExtensionImaginaryMatrix = null;
		
		if(poExtensionMatrix instanceof ComplexMatrix)
		{
			// If this is an intance of the complex matrix; get
			// get a hold of its imaginary data array
			adExtensionImaginaryMatrix = ((ComplexMatrix)poExtensionMatrix).adImaginaryMatrix; 
		}
		else
		{
			// ... else just create a new empty one. 
			adExtensionImaginaryMatrix = new double[poExtensionMatrix.size()];
		}
		
//		boolean bExtendedSuccess = extend
//		double[] blah = extend
		Matrix blah = extend
		(
			this.adImaginaryMatrix,
			this.iRows,
			this.iCols,
			adExtensionImaginaryMatrix,
			poExtensionMatrix.iRows,
			poExtensionMatrix.iCols,
			poDirection
		);
		
		setImaginaryMatrixArray(blah.adMatrix);
		
		// Extend the real part
		
		/*bExtendedSuccess |=*/ super.extend(poExtensionMatrix, poDirection); 
		
		return blah != null;//bExtendedSuccess;
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#getMatrix2D()
	 */
	public double[][] getMatrix2D()
	{
		// TODO Auto-generated method stub
		return super.getMatrix2D();
	}

	/**
	 * Allows getting internal array represeting the real matrix data.
	 * It is equivalent to getMatrixArray().
	 * @return inner array of doubles
	 * @see Matrix#getMatrixArray()
	 */
	public final double[] getRealMatrixArray()
	{
		return this.adMatrix;
	}

	/**
	 * Allows getting internal array represeting the imaginary matrix data.
	 * It is equivalent to getMatrixArray().
	 * @return inner array of doubles
	 */
	public final double[] getImaginaryMatrixArray()
	{
		return this.adImaginaryMatrix;
	}

	/**
	 * Retrieves complex matrix element's value given row and column.
	 * @param piRow row index of the element
	 * @param piCol column index of the element
	 * @return element's value
	 * @throws ArrayIndexOutOfBoundsException if at least one of the indices is out of range
	 */
	public final ComplexNumber getComplexElement(final int piRow, final int piCol)
	{
		int iTargetIndex = piRow * this.iCols + piCol;

		return
			new ComplexNumber
			(
				this.adMatrix[iTargetIndex],
				this.adImaginaryMatrix[iTargetIndex]
			);
	}

	/**
	 * Calculates complex determinant of this matrix using Laplace's formula.
	 * Also, det(0) = 0, i.e. a square matrix of 0x0 has a determinant
	 * of zero. See <a href="http://en.wikipedia.org/wiki/Determinant">this</a>
	 * for details.
	 * @return the determinant value
	 */
	public final ComplexNumber getComplexDeterminant()
	{
		ComplexNumber oDet = new ComplexNumber(0, 0);

		if(this.iRows == this.iCols)
		{
			switch(this.iRows)
			{
				// 0x0 matrix
				case 0:
				{
					return oDet;
				}

				// 1x1 matrix
				case 1:
				{
					return getComplexElement(0, 0);
				}

				// A shortcut for 2x2 matrices
				case 2:
				{
					return
						ComplexNumber.subtract
						(
							ComplexNumber.multiply(getComplexElement(0, 0), getComplexElement(1, 1)),
							ComplexNumber.multiply(getComplexElement(1, 0), getComplexElement(0, 1))
						);
				}

				// General case
				default:
				{
					int i = 0;

					for(int j = 0; j < this.iCols; j++)
					{
						ComplexMatrix oCastratedMatrix = new ComplexMatrix(this);

						oCastratedMatrix.cutRow(i);
						oCastratedMatrix.cutColumn(j);

						//oDet +=	Math.pow(-1, i + 1 + j + 1) * getElement(i, j) * oCastratedMatrix.getDeterminant();
						oDet.add
						(
							ComplexNumber.multiply
							(
								ComplexNumber.multiply
								(
									new ComplexNumber(Math.pow(-1, i + 1 + j + 1)),
									getComplexElement(i, j)
								),

								oCastratedMatrix.getComplexDeterminant()
							)
						);
					}
				}
			} // switch
		}
		else
		{
			Debug.debug("Matrix is not square: [" + this.iRows + "," + this.iCols + "]");
		}

		return oDet;
	}
	
	/* (non-Javadoc)
	 * @see marf.math.Matrix#inverse()
	 */
	public boolean inverse()
	{
		// TODO Auto-generated method stub
		return super.inverse();
	}

	/**
	 * @see marf.math.Matrix#isReduced()
	 */
	public boolean isReduced()
	{
		ComplexMatrix oM = (ComplexMatrix)clone();
		oM.rowReduce();

		return equals(oM);
	}

	/**
	 * @see marf.math.Matrix#loadColumn(int, marf.math.Vector)
	 */
	public void loadColumn(int piColNum, ComplexVector poVector)
	{
		for(int i = 0; i < this.iRows; i++)
		{
			setComplexElement(i, piColNum, poVector.getComplexElement(i));
		}
	}

	/**
	 * @param piColNum
	 * @return
	 */
	public ComplexVector getComplexColumn(final int piColNum)
	{
		ComplexVector oVector = new ComplexVector(this.iRows);
		
		for(int i = 0; i < this.iRows; i++)
		{
			oVector.setComplexNumber(i, getComplexElement(i, piColNum));
		}
		
		return oVector;
	}

	/**
	 * @see marf.math.Matrix#loadRow(int, marf.math.Vector)
	 */
	public void loadRow(int piRowNum, ComplexVector poVector)
	{
		int iElementCount = piRowNum * this.iCols;
		int iSize = poVector.size();
		
		Arrays.copy(this.adMatrix, iElementCount, poVector.getRealMatrixArray(), iSize);
		Arrays.copy(this.adImaginaryMatrix, iElementCount, poVector.getImaginaryMatrixArray(), iSize);
	}

	/**
	 * @param piRowNum
	 * @return
	 */
	public ComplexVector getComplexRow(final int piRowNum)
	{
		ComplexVector oVector = new ComplexVector(this.iCols);
		Arrays.copy(oVector.adMatrix, 0, this.adMatrix, piRowNum * this.iCols, this.iCols); 
		Arrays.copy(oVector.adImaginaryMatrix, 0, this.adImaginaryMatrix, piRowNum * this.iCols, this.iCols); 
		return oVector;
	}

	/**
	 * @see marf.math.Matrix#makeIdentity()
	 */
	public boolean makeIdentity()
	{
		this.adImaginaryMatrix = new double[size()];
		return super.makeIdentity();
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#minus(double)
	 */
	public Matrix minus(double pdNum)
	{
		// TODO Auto-generated method stub
		return super.minus(pdNum);
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#minus(marf.math.Matrix)
	 */
	public Matrix minus(Matrix poMatrix)
	{
		// TODO Auto-generated method stub
		return super.minus(poMatrix);
	}

	/**
	 * @param poMatrix
	 * @return
	 * @see marf.math.Matrix#multiply(marf.math.Matrix)
	 */
	public ComplexMatrix multiply(ComplexMatrix poMatrix)
	{
		return multiply(this, poMatrix);
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#multiply(marf.math.Vector)
	 */
	public Vector multiply(Vector poVector)
	{
		// TODO Auto-generated method stub
		return super.multiply(poVector);
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#rotate(marf.math.Vector, double, double, double, double)
	 */
	public Vector rotate(Vector poPointToRotate, double pdRotateX, double pdRotateY, double pdRotateZ, double pdRotateAngle)
	{
		// TODO Auto-generated method stub
		return super.rotate(poPointToRotate, pdRotateX, pdRotateY, pdRotateZ, pdRotateAngle);
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#rotate(marf.math.Vector, double[])
	 */
	public Vector rotate(Vector poPointToRotate, double[] padRotateFactors)
	{
		// TODO Auto-generated method stub
		return super.rotate(poPointToRotate, padRotateFactors);
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#rotate(marf.math.Vector, marf.math.Vector)
	 */
	public Vector rotate(Vector poPointToRotate, Vector poRotateFactors)
	{
		// TODO Auto-generated method stub
		return super.rotate(poPointToRotate, poRotateFactors);
	}

	/**
	 * Performs a specified linear row operation given the parameters.
	 * @param pdA A
	 * @param piRc Rc
	 * @param pcOp operation ('+' or '-')
	 * @param pdB B
	 * @param piRp Rb
	 * @return <code>true</code> if the operation was successful
	 */
	public boolean rowOperation
	(
		final ComplexNumber pdA,
		final int piRc,
		final char pcOp,
		final ComplexNumber pdB,
		final int piRp
	)
	{
		if(pdA.getReal() < 0 || pdB.getReal() < 0 || piRc > this.iRows - 1 || piRp > this.iRows - 1)
		{
			return false;
		}

		int iRowChangeOffset = piRc * this.iCols;
		int iRowPivotOffset = piRp * this.iCols;

		int iSign;

		switch(pcOp)
		{
			case '+':
				iSign = 1;
				break;

			case '-':
				iSign = -1;
				break;

			default:
				return false;
		}

		for(int i = 0; i < this.iCols; i++)
		{
			//this.adMatrix[piRowChangeOffset + i] =
			//	pdA * this.adMatrix[piRowChangeOffset + i] +
			//	piSign * pdB * this.adMatrix[piRowPivotOffset + i];
			
			ComplexNumber oChangeNumber = new ComplexNumber
			(
				this.adMatrix[iRowChangeOffset + i],
				this.adImaginaryMatrix[iRowChangeOffset + i]
			);

			ComplexNumber oPivotNumber = new ComplexNumber
			(
				this.adMatrix[iRowPivotOffset + i],
				this.adImaginaryMatrix[iRowPivotOffset + i]
			);

			ComplexNumber oNumber = ComplexNumber.add
			(
				ComplexNumber.multiply(pdA, oChangeNumber),
				ComplexNumber.multiply(new ComplexNumber(iSign).multiply(pdB), oPivotNumber)
			);
			
			this.adMatrix[iRowChangeOffset + i] = oNumber.getReal(); 
			this.adImaginaryMatrix[iRowChangeOffset + i] = oNumber.getImaginary(); 
		}

		return true;
	}
	
	/**
	 * @see marf.math.Matrix#rowReduce()
	 */
	public boolean rowReduce()
	{
		int i, j;

		for(i = 0; i < this.iRows; i++)
		{
			for(j = 0; j < this.iRows; j++)
			{
				if(i != j)
				{
					//double dA = getElement(i, i);
					//double dB = getElement(j, i);
					
					ComplexNumber dA = getComplexElement(i, i);
					ComplexNumber dB = getComplexElement(j, i);

					char cOp;

					//if((dA > 0 && dB > 0) || (dA < 0 && dB < 0))
					if((dA.getReal() > 0 && dB.getReal() > 0) || (dA.getReal() < 0 && dB.getReal() < 0))
					{
						cOp = '-';
					}
					else
					{
						cOp = '+';
					}

					//dA = Math.abs(dA);
					//dB = Math.abs(dB);
					dA.setReal(ComplexNumber.abs(dA));
					dB.setReal(ComplexNumber.abs(dB));

					rowOperation(dA, j, cOp, dB, i);
				}
				else
				{
					continue;
				}
			}
		}

		for(i = 0; i < this.iRows; i++)
		{
			//double dPivot = getElement(i, i);
			ComplexNumber dPivot = getComplexElement(i, i);

			for(j = 0; j < this.iCols; j++)
			{
				//setElement(i, j, getElement(i, j) / dPivot);
				getComplexElement(i, j).divide(dPivot);
			}
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#scale(marf.math.Vector, double, double, double)
	 */
	public Vector scale(Vector poPointToScale, double pdScaleX, double pdScaleY, double pdScaleZ)
	{
		// TODO Auto-generated method stub
		return super.scale(poPointToScale, pdScaleX, pdScaleY, pdScaleZ);
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#scale(marf.math.Vector, double[])
	 */
	public Vector scale(Vector poPointToScale, double[] padScaleFactors)
	{
		// TODO Auto-generated method stub
		return super.scale(poPointToScale, padScaleFactors);
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#scale(marf.math.Vector, marf.math.Vector)
	 */
	public Vector scale(Vector poPointToScale, Vector poScaleFactors)
	{
		// TODO Auto-generated method stub
		return super.scale(poPointToScale, poScaleFactors);
	}

	/**
	 * @see marf.math.Matrix#setAll()
	 */
	public void setAll()
	{
		setAll(0.0);
	}

	/**
	 * @see marf.math.Matrix#setAll(double)
	 */
	public void setAll(final ComplexNumber poFillValue)
	{
		Arrays.fill(this.adMatrix, poFillValue.dReal);
		Arrays.fill(this.adImaginaryMatrix, poFillValue.dImaginary);
	}

	/**
	 * @see marf.math.Matrix#setAllRandom()
	 */
	public void setAllRandom()
	{
		super.setAllRandom();
		Arrays.fillRandom(this.adImaginaryMatrix);
	}

	/**
	 * @see marf.math.Matrix#setElement(int, int, double)
	 */
	public void setComplexElement(int piRow, int piCol, final ComplexNumber poValue)
	{
		this.adMatrix[piRow * this.iCols + piCol] = poValue.dReal;
		this.adImaginaryMatrix[piRow * this.iCols + piCol] = poValue.dImaginary;
	}

	/**
	 * @param paoo2DMatrix
	 */
	public void setMatrix2D(final ComplexNumber[][] paoo2DMatrix)
	{
		this.iRows = paoo2DMatrix.length;
		this.iCols = paoo2DMatrix[0].length;

		this.adMatrix = new double[this.iRows * this.iCols];
		this.adImaginaryMatrix = new double[this.iRows * this.iCols];

		for(int i = 0; i < this.iRows; i++)
		{
			for(int j = 0; j < this.iCols; j++)
			{
				setComplexElement(i, j, paoo2DMatrix[i][j]);
			}
		}
	}

	/**
	 * @param padNewMatrix
	 * @see marf.math.Matrix#setMatrixArray(double[])
	 */
	public void setRealMatrixArray(final double[] padNewMatrix)
	{
		super.setMatrixArray(padNewMatrix);
	}

	/**
	 * @param padNewMatrix
	 */
	public void setImaginaryMatrixArray(final double[] padNewMatrix)
	{
		this.adImaginaryMatrix = (double[])padNewMatrix.clone();
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#shear(marf.math.Vector, double, double, double)
	 */
	public Vector shear(Vector poPointToShear, double pdShearX, double pdShearY, double pdShearZ)
	{
		// TODO Auto-generated method stub
		return super.shear(poPointToShear, pdShearX, pdShearY, pdShearZ);
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#shear(marf.math.Vector, double[])
	 */
	public Vector shear(Vector poPointToShear, double[] padShearFactors)
	{
		// TODO Auto-generated method stub
		return super.shear(poPointToShear, padShearFactors);
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#shear(marf.math.Vector, marf.math.Vector)
	 */
	public Vector shear(Vector poPointToShear, Vector poShearFactors)
	{
		// TODO Auto-generated method stub
		return super.shear(poPointToShear, poShearFactors);
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#toString()
	 */
	public String toString()
	{
		// TODO Auto-generated method stub
		return super.toString();
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#translate(marf.math.Vector, double, double, double)
	 */
	public Vector translate(Vector poPointToTranslate, double pdTranslateX, double pdTranslateY, double pdTranslateZ)
	{
		// TODO Auto-generated method stub
		return super.translate(poPointToTranslate, pdTranslateX, pdTranslateY, pdTranslateZ);
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#translate(marf.math.Vector, double[])
	 */
	public Vector translate(Vector poPointToTranslate, double[] padTranslateVals)
	{
		// TODO Auto-generated method stub
		return super.translate(poPointToTranslate, padTranslateVals);
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#translate(marf.math.Vector, marf.math.Vector)
	 */
	public Vector translate(Vector poPointToTranslate, Vector poTranslateVals)
	{
		// TODO Auto-generated method stub
		return super.translate(poPointToTranslate, poTranslateVals);
	}

	/* (non-Javadoc)
	 * @see marf.math.Matrix#transpose()
	 */
	public boolean transpose()
	{
		// TODO Auto-generated method stub
		return super.transpose();
	}
}

// EOF
