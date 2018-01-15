package marf.math;

import java.io.Serializable;

import marf.util.Arrays;
import marf.util.Debug;


/**
 * <p>Provides linear math and storage matrix operations.
 * This class implements a 2-dimensional matrix of doubles;
 * internally implemented as a single-dimensional array with
 * a common linear algebra operations as well as methods to
 * cut/add rows or columns and glue matrices together.
 * </p>
 *
 * <p><b>NOTE:</b> this class provides a lot of useful and working functionality, but
 * requires a lot of improvements. In particular in performance, documentation, and
 * styles and consistency in operators. Some missing features will be added/filled in as well.
 * Requires a lot of thorough testing and <code>MathTestApp</code> serves that purpose.</p>
 *
 * $Id: Matrix.java,v 1.47 2007/12/18 03:45:42 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @author Shuxin Fan
 *
 * @version $Revision: 1.47 $
 * @since 0.3.0.1
 * @see marf.math.Vector
 */
public class Matrix
implements Cloneable, Serializable
{
	/**
	 * Default dimension of Y of matrix is 4.
	 */
	public static final int DEFAULT_ROWS = 4;

	/**
	 * Default dimension of X of matrix is 4.
	 */
	public static final int DEFAULT_COLS = 4;

	/**
	 * Default precision delta to compute nearly-identity matrices
	 * or other error-based calculations.
	 * @since 0.3.0.3
	 */
	public static final double DEFAULT_ERROR_DELTA = 0.000000000000001;

	// Matrix Dimensions

	/**
	 * Actual number of rows in the matrix.
	 */
	protected int iRows;

	/**
	 * Actual number of columns in the matrix.
	 */
	protected int iCols;

	/**
	 * Matrix itself.
	 */
	protected double[] adMatrix = null;

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = -4663728617934529725L;


	/*
	 * ------------------
	 * Object' Life Cycle
	 * ------------------
	 */

	/**
	 * Default constructor.
	 * Equivalent to <code>Matrix(Matrix.DEFAULT_ROWS, Matrix.DEFAULT_COLS)</code>.
	 * @see #Matrix(int, int)
	 */
	public Matrix()
	{
		this(DEFAULT_ROWS, DEFAULT_COLS);
	}

	/**
	 * Constructor with custom dimensions.
	 * Equivalent to <code>Matrix(piRows, piCols, 0.0)</code>.
	 * @param piRows custom # of rows.
	 * @param piCols custom # of columns.
	 * @see #Matrix(int, int, double)
	 */
	public Matrix(final int piRows, final int piCols)
	{
		this(piRows, piCols, 0.0);
	}

	/**
	 * Constructor with custom dimensions and initial fill value.
	 * @param piRows custom # of rows.
	 * @param piCols custom # of columns.
	 * @param pdFillValue initial value of all the elements of the matrix.
	 */
	public Matrix(final int piRows, final int piCols, final double pdFillValue)
	{
		this.iRows    = piRows;
		this.iCols    = piCols;
		this.adMatrix = new double[this.iRows * this.iCols];

		setAll(pdFillValue);
	}

	/**
	 * A-la copy-constructor.
	 * @param poMatrix Matrix object to copy data members off from
	 */
	public Matrix(final Matrix poMatrix)
	{
		this.iRows    = poMatrix.iRows;
		this.iCols    = poMatrix.iCols;
		this.adMatrix = (double[])poMatrix.adMatrix.clone();
	}

	/**
	 * This constructor makes up a vector.
	 * Equivalent to <code>Matrix(pad1DMatrix, false)</code>.
	 * @param pad1DMatrix vector's elements
	 * @see #Matrix(double[], boolean)
	 */
	public Matrix(final double[] pad1DMatrix)
	{
		this(pad1DMatrix, false);
	}

	/**
	 * This constructor makes up a transposed vector.
	 * @param pad1DMatrix vector's elements
	 * @param pbTransposed true if transposed, false otherwise
	 */
	public Matrix(final double[] pad1DMatrix, boolean pbTransposed)
	{
		if(pbTransposed == false)
		{
			this.iRows = 1;
			this.iCols = pad1DMatrix.length;
		}
		else
		{
			this.iRows = pad1DMatrix.length;
			this.iCols = 1;
		}

		this.adMatrix = (double[])pad1DMatrix.clone();
	}

	/**
	 * Constructs a 2D matrix from a 2D array.
	 * Internally calls setMatrix2D().
	 * @param padd2DMatrix initial array elements
	 * @see #setMatrix2D
	 */
	public Matrix(final double[][] padd2DMatrix)
	{
		setMatrix2D(padd2DMatrix);
	}

	/**
	 * "Destroys" the internal array matrix by setting it to <code>null</code>.
	 * Also, the garbage collector is hinted to collect as soon as it can with
	 * <code>System.gc()</code>. If called very often, may actually slowdown
	 * the system during the garbage collection runs.
	 * The cardinality of the matrix is not affected and a new instance of it
	 * is not created; use <code>setMatrixArray()</code> to set the new matrix
	 * data.
	 * @see #setMatrixArray(double[])
	 * @see #setMatrix2D(double[][])
	 * @see System#gc()
	 */
	public void exhaustMatrix()
	{
		// Hint garbage collector.
		this.adMatrix = null;
		System.gc();
	}


	/*
	 * -----------------
	 * Getters & Setters
	 * -----------------
	 */

	/**
	 * Retrieves number of the rows in this matrix.
	 * @return number of rows
	 */
	public final int getRows()
	{
		return this.iRows;
	}

	/**
	 * Retrieves number of the columns in this matrix.
	 * @return number of columns
	 */
	public final int getCols()
	{
		return this.iCols;
	}

	/**
	 * Allows setting number of rows of the matrix.
	 * @param piRows new number of rows
	 */
	public void setRows(final int piRows)
	{
		this.iRows = piRows;
	}

	/**
	 * Allows setting number of columns of the matrix.
	 * @param piCols new number of cloumns
	 */
	public void setCols(final int piCols)
	{
		this.iCols = piCols;
	}

	/**
	 * Allows getting internal array representing the matrix data.
	 * @return inner array of doubles
	 */
	public final double[] getMatrixArray()
	{
		return this.adMatrix;
	}

	/**
	 * Allows setting the new internal array of matrix data
	 * from the parameter. The cardinality is kept before
	 * the setting as no assumptions is made about the new
	 * data. The array is copied entirely into internal storage.
	 * @param padNewMatrix new matrix data
	 * @throws NullPointerException if the parameter is <code>null</code>
	 */
	public void setMatrixArray(final double[] padNewMatrix)
	{
		this.adMatrix = (double[])padNewMatrix.clone();
	}


	/*
	 * ----------
	 * Operations
	 * ----------
	 */

	/**
	 * Sets all elements of the matrix to a default of 0.0.
	 */
	public void setAll()
	{
		setAll(0.0);
	}

	/**
	 * Sets all elements of the matrix to the specified value.
	 * @param pdFillValue the value to fill the matrix with.
	 */
	public void setAll(final double pdFillValue)
	{
		Arrays.fill(this.adMatrix, pdFillValue);
	}

	/**
	 * Sets all elements of the matrix at pseudo-random order.
	 * Used primarily for testing. The range of the values
	 * is [- Double.MAX_VALUE / 2, Double.MAX_VALUE / 2].
	 * @since 0.3.0.3
	 * @see Double#MAX_VALUE
	 */
	public void setAllRandom()
	{
		Arrays.fillRandom(this.adMatrix);
	}

	/**
	 * Returns number of the elements of the matrix.
	 * @return element count
	 */
	public final int getElements()
	{
		// Technically, same as (this.iCols * this.iRows)
		assert
			this.adMatrix.length == this.iCols * this.iRows
			: "Array length ("
				+ this.adMatrix.length + ") != cols x rows ("
				+ this.iCols * this.iRows + ")";

		return this.adMatrix.length;
	}

	/**
	 * Analogous to <code>getElements()</code>.
	 * @return element count
	 * @since 0.3.0.1
	 * @see #getElements()
	 */
	public final int size()
	{
		return getElements();
	}

	/**
	 * Retrieves matrix element's value given row and column.
	 * @param piRow row index of the element
	 * @param piCol column index of the element
	 * @return element's value
	 * @throws ArrayIndexOutOfBoundsException if at least one of the indices is out of range
	 */
	public final double getElement(final int piRow, final int piCol)
	{
		int iOffset = piRow * this.iCols + piCol; 

		assert
			(iOffset < this.adMatrix.length && iOffset >= 0) :
				"row or column (" + piRow + "," + piCol
				+ ") out of limits (" + (this.iRows - 1) + "," + (this.iCols - 1)
				+ "); target offset is: " + iOffset + ", array length: " + this.adMatrix.length;

		return this.adMatrix[iOffset];
	}

	/**
	 * Sets matrix element's value at specified position.
	 * @param piRow row index of the element to set
	 * @param piCol column index of the element to set
	 * @param pdValue the value of the element
	 * @throws ArrayIndexOutOfBoundsException if at least one of the indices is out of range
	 */
	public void setElement(final int piRow, final int piCol, final double pdValue)
	{
		this.adMatrix[piRow * this.iCols + piCol] = pdValue;
	}

	/**
	 * Allows loading a row of a matrix with values from a vector.
	 * @param piRowNum index of the row to load
	 * @param poVector the values to take
	 * @throws ArrayIndexOutOfBoundsException if the row number is out
	 * of range or there are insufficient number of elements in the vector
	 */
	public void loadRow(final int piRowNum, final Vector poVector)
	{
		// TODO: Needs Validation
		Debug.debug("public loadRow() - row: " + piRowNum + "\n");

		// memcpy(&adMatrix[piRowNum * iCols], p_oVector.getMatrixArray(), sizeof(double) * p_oVector.getElements());
		Arrays.copy(this.adMatrix, piRowNum * this.iCols, poVector.adMatrix, poVector.size());
	}

	/**
	 * @param piRowNum
	 * @return
	 * @since 0.3.0.6
	 */
	public Vector getRow(final int piRowNum)
	{
		Vector oVector = new Vector(this.iCols);
		Arrays.copy(oVector.adMatrix, 0, this.adMatrix, piRowNum * this.iCols, this.iCols); 
		return oVector;
	}

	/**
	 * Allows loading a row of a matrix with values from a vector.
	 *
	 * @param piColNum index of the column to load
	 * @param poVector the values to take
	 * @throws ArrayIndexOutOfBoundsException if the column number is out
	 * of range or there are insufficient number of elements in the vector
	 *
	 * @since 0.3.0.6
	 */
	public void loadColumn(final int piColNum, final Vector poVector)
	{
		// TODO: Needs Validation
		Debug.debug("public loadColumn() - row: " + piColNum + "\n");

		for(int i = 0; i < this.iRows; i++)
		{
			setElement(i, piColNum, poVector.getElement(i));
		}
	}

	/**
	 * @param piColNum
	 * @return
	 * @since 0.3.0.6
	 */
	public Vector getColumn(final int piColNum)
	{
		Vector oVector = new Vector(this.iRows);
		
		for(int i = 0; i < this.iRows; i++)
		{
			oVector.setElement(i, getElement(i, piColNum));
		}
		
		return oVector;
	}
	
	/**
	 * Calculates determinant of this matrix using Laplace's formula.
	 * Also, det(0) = 0, i.e. a square matrix of 0x0 has a determinant
	 * of zero. See <a href="http://en.wikipedia.org/wiki/Determinant">this</a>
	 * for details.
	 * @return the determinant value
	 */
	public final double getDeterminant()
	{
		double dDet = 0.0;

		if(this.iRows == this.iCols)
		{
			switch(this.iRows)
			{
				// 0x0 matrix
				case 0:
				{
					return dDet;
				}

				// 1x1 matrix
				case 1:
				{
					return getElement(0, 0);
				}

				// A shortcut for 2x2 matrices
				case 2:
				{
					return
						getElement(0, 0) * getElement(1, 1) -
						getElement(1, 0) * getElement(0, 1);
				}

				// General case
				default:
				{
					int i = 0;

					for(int j = 0; j < this.iCols; j++)
					{
						Matrix oCastratedMatrix = new Matrix(this);

						oCastratedMatrix.cutRow(i);
						oCastratedMatrix.cutColumn(j);

						dDet += Math.pow(-1, i + 1 + j + 1) * getElement(i, j) * oCastratedMatrix.getDeterminant();
					}
				}
			} // switch
		}
		else
		{
			Debug.debug("Matrix is not square: [" + this.iRows + "," + this.iCols + "]");
		}

		return dDet;
	}

	/**
	 * Performs a '+' linear row operation given the A and Rc parameters.
	 * @param pdA A
	 * @param piRc Rc
	 * @return <code>true</code> if the operation was successful
	 */
	public boolean rowOperation(final double pdA, final int piRc)
	{
		return rowOperation(pdA, piRc, '+', 0.0, 0);
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
		final double pdA,
		final int piRc,
		final char pcOp,
		final double pdB,
		final int piRp
	)
	{
		if(pdA < 0 || pdB < 0 || piRc > this.iRows - 1 || piRp > this.iRows - 1)
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
			this.adMatrix[iRowChangeOffset + i] =
				pdA * this.adMatrix[iRowChangeOffset + i] +
				iSign * pdB * this.adMatrix[iRowPivotOffset + i];
		}

		return true;
	}
	
	/**
	 * Applies row-reduce operation in this matrix.
	 * @return <code>true</code> if the operation was successful
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
					double dA = getElement(i, i);
					double dB = getElement(j, i);

					char cOp;

					if((dA > 0 && dB > 0) || (dA < 0 && dB < 0))
					{
						cOp = '-';
					}
					else
					{
						cOp = '+';
					}

					dA = Math.abs(dA);
					dB = Math.abs(dB);

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
			double dPivot = getElement(i, i);

			for(j = 0; j < this.iCols; j++)
			{
				setElement(i, j, getElement(i, j) / dPivot);
			}
		}

		return true;
	}

	/**
	 * Allows examine if this matrix was row-reduced already.
	 * @return <code>true</code> if the matrix if row-reduced
	 * @see #rowReduce()
	 */
	public boolean isReduced()
	{
		Matrix oM = (Matrix)clone();
		//Matrix oM = getMatrixCopy(this);
		oM.rowReduce();

		return equals(oM);
	}

	/**
	 * Extends this matrix by another (by gluing two matrices together) assuming
	 * the extension direction of <code>Direction.EAST</code>. NOTICE, if doubling
	 * the same <code>this</code> matrix, make a copy for the argument instead.
	 * @param poExtensionMatrix the matrix to extend the current by
	 * @return <code>true</code> if the operation was successful
	 * @see Direction#EAST
	 */
	public boolean extend(final Matrix poExtensionMatrix)
	{
		return extend(poExtensionMatrix, new Direction(Direction.EAST));
	}

	/**
	 * Extends this matrix by another (by gluing two matrices together) given
	 * the extension direction.  NOTICE, if doubling the same matrix,
	 * make a copy for the argument instead.
	 *
	 * @param poExtensionMatrix the matrix to extend the current by
	 * @param poDirection in which direction the extension should happen
	 * @return <code>true</code> if the operation was successful
	 * @see Direction
	 */
	public boolean extend(final Matrix poExtensionMatrix, final Direction poDirection)
	{
		//boolean bExtendedSuccess = extend
		//double[] bExtendedSuccess = extend
		Matrix bExtendedSuccess = extend
		(
			this.adMatrix,
			this.iRows,
			this.iCols,
			poExtensionMatrix.adMatrix,
			poExtensionMatrix.iRows,
			poExtensionMatrix.iCols,
			poDirection
		);
		
//		this.adMatrix = bExtendedSuccess;
		this.adMatrix = bExtendedSuccess.getMatrixArray();
		
		// Update the new dimensionality
//		this.iRows += poExtensionMatrix.iRows;
//		this.iCols += poExtensionMatrix.iCols;
		this.iRows = bExtendedSuccess.iRows;
		this.iCols = bExtendedSuccess.iCols;
		
//		return bExtendedSuccess;
		return bExtendedSuccess != null;
	}

	/**
	 * Refactors matrix array extension to operate on raw arrays.
	 * @param padMatrixToExtend
	 * @param piMatrixToExtendRows
	 * @param piMatrixToExtendCols
	 * @param padExtensionMatrix
	 * @param piExtensionMatrixRows
	 * @param piExtensionMatrixCols
	 * @param poDirection
	 * @return
	 * @since 0.3.0.6
	 */
	public static Matrix extend
	//public static double[] extend
	//public static boolean extend
	(
		//double[] padMatrixToExtend,
		final double[] padMatrixToExtend,
		int piMatrixToExtendRows,
		int piMatrixToExtendCols,
		final double[] padExtensionMatrix,
		int piExtensionMatrixRows,
		int piExtensionMatrixCols,
		final Direction poDirection
	)
	{
		//int iOldMatrixCols = this.iCols;
		int iOldMatrixCols = piMatrixToExtendCols;

		//int iNewCols = this.iCols;
		//int iNewRows = this.iRows;
		int iNewCols = piMatrixToExtendCols;
		int iNewRows = piMatrixToExtendRows;

		// Determine new dimensions of the extended matrix
		switch(poDirection.getDirection())
		{
			case Direction.EAST:
			case Direction.WEST:
			{
				//if(getRows() != poExtensionMatrix.getRows())
				if(piMatrixToExtendRows != piExtensionMatrixRows)
				{
//					return false;
					return null;
				}

				//iNewCols += poExtensionMatrix.getCols();
				iNewCols += piExtensionMatrixCols;
				break;
			}

			case Direction.NORTH:
			case Direction.SOUTH:
			{
//				if(getCols() != poExtensionMatrix.getCols())
				if(piMatrixToExtendCols != piExtensionMatrixCols)
				{
//					return false;
					return null;
				}

//				iNewRows += poExtensionMatrix.getRows();
				iNewRows += piExtensionMatrixRows;
				break;
			}

			// Disallow any other directions
			default:
			{
//				return false;
				return null;
			}
		}

//		double[] adExtendedMatrix = new double[size() + poExtensionMatrix.size()];
		double[] adExtendedMatrix = new double[padMatrixToExtend.length + padExtensionMatrix.length];

		// Do actual extension in the specified direction
		switch(poDirection.getDirection())
		{
			case Direction.EAST:
			{
//				double[] adExtensionMatrix = poExtensionMatrix.getMatrixArray();
//				double[] adThisMatrix = getMatrixArray();
				double[] adExtensionMatrix = padExtensionMatrix;
				double[] adThisMatrix = padMatrixToExtend;

				for(int i = 0; i < iNewRows; i++)
				{
					Arrays.copy
					(
						adExtendedMatrix,        // To
						i * iNewCols,            // To Index
						adThisMatrix,            // From
						i * iOldMatrixCols,      // From Index
						iOldMatrixCols           // How many
					);

					Arrays.copy
					(
						adExtendedMatrix,
						i * iNewCols + iOldMatrixCols,
						adExtensionMatrix,
//						i * poExtensionMatrix.getCols(),
//						poExtensionMatrix.getCols()
						i * piExtensionMatrixCols,
						piExtensionMatrixCols
					);
				}

				break;
			}

			case Direction.WEST:
			{
//				double[] adExtensionMatrix = poExtensionMatrix.getMatrixArray();
//				double[] adThisMatrix = getMatrixArray();
				double[] adExtensionMatrix = padExtensionMatrix;
				double[] adThisMatrix = padMatrixToExtend;

				for(int i = 0; i < iNewRows; i++)
				{
					Arrays.copy
					(
						adExtendedMatrix,
						i * iNewCols,
						adExtensionMatrix,
//						i * poExtensionMatrix.getCols(),
//						poExtensionMatrix.getCols()
						i * piExtensionMatrixCols,
						piExtensionMatrixCols
					);

					Arrays.copy
					(
						adExtendedMatrix,
//						i * iNewCols + poExtensionMatrix.getCols(),
						i * iNewCols + piExtensionMatrixCols,
						adThisMatrix,
						i * iOldMatrixCols,
						iOldMatrixCols
					);
				}

				break;
			}
			
			case Direction.NORTH:
			{
				Arrays.copy
				(
					adExtendedMatrix,
					0,
//					poExtensionMatrix.getMatrixArray(),
					padExtensionMatrix,
					0,
//					poExtensionMatrix.size()
					padExtensionMatrix.length
				);

				Arrays.copy
				(
					adExtendedMatrix,
//					poExtensionMatrix.size(),
//					getMatrixArray(),
					padExtensionMatrix.length,
					padMatrixToExtend,
					0,
//					size()
					padMatrixToExtend.length
				);

				break;
			}

			case Direction.SOUTH:
			{
				Arrays.copy
				(
					adExtendedMatrix,
					0,
//					getMatrixArray(),
					padMatrixToExtend,
					0,
					padMatrixToExtend.length
				);

				Arrays.copy
				(
					adExtendedMatrix,
//					size(),
//					poExtensionMatrix.getMatrixArray(),
					padMatrixToExtend.length,
					padExtensionMatrix,
					0,
//					poExtensionMatrix.size()
					padExtensionMatrix.length
				);

				break;
			}

			default:
			{
				//return false;
				return null;
			}
		}

		// Finalize setting the new matrix and its dimensions
//		this.iRows = iNewRows;
//		this.iCols = iNewCols;
//		setMatrixArray(adExtendedMatrix);

		//padMatrixToExtend = adExtendedMatrix;

		// Hint the JVM to recycle the copy faster
//		adExtendedMatrix = null;

		//return true;
		//Matrix oMatrix = createMatrix(iNewRows, iNewCols);
		Matrix oMatrix = new Matrix(iNewRows, iNewCols);
		oMatrix.setMatrixArray(adExtendedMatrix);
//		oMatrix.setRows(iNewRows);
//		oMatrix.setCols(iNewCols);
		
		return oMatrix;
	}
	
	/**
	 * A general routine that allows arbitrary cropping of a matrix.
	 * The cropping rectangle specified by the parameters is
	 * (left, top) - (right, bottom) -- anything inside is kept including the borders,
	 * all of the outside is thrown out.
	 *
	 * @param piLeft left index border to crop after
	 * @param piTop top index border to crop after
	 * @param piRight right index border to crop after
	 * @param piBottom bottom index border to crop after
	 * @return <code>true</code> if the cropping was successful
	 */
	public boolean crop(final int piLeft, final int piTop, final int piRight, final int piBottom)
	{
		/*boolean bChanges*/ Matrix bChanges = crop(this.adMatrix, this.iRows, this.iCols, piLeft, piTop, piRight, piBottom);
		
		// Update cardinality after successful cropping.
		//if(bChanges == true)
		if(bChanges != null)
		{
			this.iCols = piRight  - piLeft + 1;
			this.iRows = piBottom - piTop  + 1;
			setMatrixArray(bChanges.adMatrix);
		}
		
		return bChanges != null;
	}

	/**
	 * @param padMatrixToCrop
	 * @param piRows
	 * @param piCols
	 * @param piLeft
	 * @param piTop
	 * @param piRight
	 * @param piBottom
	 * @return
	 * @since 0.3.0.6
	 */
	public static Matrix crop
	(
		final double[] padMatrixToCrop,
		final int piRows,
		final int piCols,
		final int piLeft,
		final int piTop,
		final int piRight,
		final int piBottom
	)
	{
		Debug.debug("Cropping rect: (" + piLeft + "," + piTop + ") - (" + piRight + "," + piBottom + ")\n");

		if
		(
//			piLeft   < 0 || piLeft   > this.iCols - 1 ||
//			piTop    < 0 || piTop    > this.iRows - 1 ||
//			piRight  < 0 || piRight  > this.iCols - 1 ||
//			piBottom < 0 || piBottom > this.iRows - 1
//			piLeft   < 0 || piLeft   > poMatrixToCrop.iCols - 1 ||
//			piTop    < 0 || piTop    > poMatrixToCrop.iRows - 1 ||
//			piRight  < 0 || piRight  > poMatrixToCrop.iCols - 1 ||
//			piBottom < 0 || piBottom > poMatrixToCrop.iRows - 1
			piLeft   < 0 || piLeft   > piCols - 1 ||
			piTop    < 0 || piTop    > piRows - 1 ||
			piRight  < 0 || piRight  > piCols - 1 ||
			piBottom < 0 || piBottom > piRows - 1
		)
		{
			Debug.debug("Cropping rectangle is out of range.");
//			return false;
			return null;
		}

		// Set the new dimensions of the cropped-to-be matrix.
		int iNewCols = piRight  - piLeft + 1;
		int iNewRows = piBottom - piTop  + 1;

		// Allocate enough storage for the contents of the new matrix data.
		double[] adCroppedMatrix = new double[iNewRows * iNewCols];

		// Temporary index pointer within the cropped matrix.
		int iTmpPtr = 0;

		// Temporary index pointer within the current matrix.
//		int iTmpThisPtr = piLeft + piTop * this.iCols;
//		int iTmpThisPtr = piLeft + piTop * poMatrixToCrop.iCols;
		int iTmpThisPtr = piLeft + piTop * piCols;

		// Perform data copy row-by-row WRT the new dimensions.
		for(int i = 0; i < iNewRows; i++)
		{
//			Arrays.copy(pdCroppedMatrix, iTmpPtr, this.adMatrix, iTmpThisPtr, iNewCols);
//			Arrays.copy(pdCroppedMatrix, iTmpPtr, poMatrixToCrop.adMatrix, iTmpThisPtr, iNewCols);
			Arrays.copy(adCroppedMatrix, iTmpPtr, padMatrixToCrop, iTmpThisPtr, iNewCols);

			iTmpPtr     += iNewCols;
//			iTmpThisPtr += this.iCols;
			iTmpThisPtr += piCols;
		}

		// Update the current matrix's attributes.
//		this.iRows = iNewRows;
//		this.iCols = iNewCols;
		//poMatrixToCrop.iRows = iNewRows;
		//poMatrixToCrop.iCols = iNewCols;

//		setMatrixArray(pdCroppedMatrix);
		//poMatrixToCrop.adMatrix = pdCroppedMatrix;
		//padMatrixToCrop = adCroppedMatrix;
		Matrix oMatrix = new Matrix(iNewRows, iNewCols);
		oMatrix.setMatrixArray(adCroppedMatrix);
		
		// Garbage-collect the tmp matrix
//		pdCroppedMatrix = null;

		//return true;
		return oMatrix;
	}

	/**
	 * Cuts a specified row away from the matrix.
	 * @param piRowNum row to remove
	 * @return <code>true</code> if cutting was successful
	 */
	public boolean cutRow(final int piRowNum)
	{
		if(piRowNum < 0 || piRowNum > this.iRows - 1)
		{
			return false;
		}

		// First row
		if(piRowNum == 0)
		{
			return cutFirstRow();
		}

		// Last row
		if(piRowNum == this.iRows - 1)
		{
			return cutLastRow();
		}

		// Middle row
//		Matrix oUpperMatrix = new Matrix(this);
//		Matrix oLowerMatrix = new Matrix(this);
		Matrix oUpperMatrix = getMatrixCopy(this);
		Matrix oLowerMatrix = getMatrixCopy(this);

		oUpperMatrix.crop(0, 0, this.iCols - 1, piRowNum - 1);
		oLowerMatrix.crop(0, piRowNum + 1, this.iCols - 1, this.iRows - 1);

		oUpperMatrix.extend(oLowerMatrix, new Direction(Direction.SOUTH));

		this.iRows--;

//		setMatrixArray(oUpperMatrix.getMatrixArray());
		setMatrixData(oUpperMatrix);

		// Hint garbage collector earlier than later
		oUpperMatrix = null;
		oLowerMatrix = null;

		return true;
	}

	
	/**
	 * @param poMatrix
	 * @return
	 * @since 0.3.0.6
	 */
	private /*static*/ Matrix getMatrixCopy(final Matrix poMatrix)
	{
		return new Matrix(poMatrix);
	}
	
	/**
	 * @param poMatrix
	 * @since 0.3.0.6
	 */
	private void setMatrixData(final Matrix poMatrix)
	{
		setMatrixArray(poMatrix.getMatrixArray());
	}
	
	/**
	 * @return
	 * @since 0.3.0.6
	 */
	public boolean cutLastRow()
	{
		return crop(0, 0, this.iCols - 1, this.iRows - 2);
	}

	/**
	 * @return
	 * @since 0.3.0.6
	 */
	public boolean cutFirstRow()
	{
		return crop(0, 1, this.iCols - 1, this.iRows - 1);
	}

	/**
	 * Shrinks a matrix by removing a specified column from it.
	 * @param piColNum a valid index of the column to be removed.
	 * @return <code>true</code> if the column was actually removed; <code>false</code> otherwise
	 */
	public boolean cutColumn(final int piColNum)
	{
		if(piColNum < 0 || piColNum > this.iCols - 1)
		{
			return false;
		}

		// If the first column, just crop it.
		if(piColNum == 0)
		{
			return cutFirstColumn();
		}

		// If the last column, just crop it.
		if(piColNum == this.iCols - 1)
		{
			return cutLastColumn();
		}

		/*
		 * General case:
		 * - Split into two matrices at the piColNum not including piColNum itself
		 * - Glue the two pieces
		 */

		Matrix oLeftMatrix = getMatrixCopy(this);
		Matrix oRightMatrix = getMatrixCopy(this);

		oLeftMatrix.crop(0, 0, piColNum - 1, this.iRows - 1);
		oRightMatrix.crop(piColNum + 1, 0, this.iCols - 1, this.iRows - 1);

		oLeftMatrix.extend(oRightMatrix, new Direction(Direction.EAST));

		this.iCols--;

		setMatrixData(oLeftMatrix);

		oLeftMatrix = null;
		oRightMatrix = null;

		return true;
	}

	/**
	 * @return
	 * @since 0.3.0.6
	 */
	public boolean cutLastColumn()
	{
		return crop(0, 0, this.iCols - 2, this.iRows - 1);
	}

	/**
	 * @return
	 * @since 0.3.0.6
	 */
	public boolean cutFirstColumn()
	{
		return crop(1, 0, this.iCols - 1, this.iRows - 1);
	}

	/**
	 * Outputs matrix in text format to STDOUT.
	 */
	public void display()
	{
		System.out.println(this);
	}

	/**
	 * Textual representation of the matrix.
	 * @return String representation
	 */
	public String toString()
	{
		StringBuffer oMatrix = new StringBuffer();

		for(int i = 0; i < this.iRows; i++)
		{
			for(int j = 0; j < this.iCols; j++)
			{
				oMatrix.append("[").append(getElement(i, j)).append("]\t");
			}

			oMatrix.append("\n");
		}

		return oMatrix.toString();
	}

	/**
	 * @return
	 */
	protected Matrix getNewMatrix()
	{
		System.out.println("[[[ in default Matrix ]]]");
		return new Matrix(this.iRows, this.iCols);		
	}

	/**
	 * @param piRows
	 * @param piCols
	 * @return
	 */
	protected /*static*/ Matrix getNewMatrix(int piRows, int piCols)
	{
		System.out.println("[[[ in Matrix ]]]");
		return new Matrix(piRows, piCols);
	}
	
	/**
	 * Matrix inversion.
	 * @return <code>true</code> if matrix was possible to invert, <code>false</code> otherwise
	 */
	public boolean inverse()
	{
		if(this.iRows != this.iCols)
		{
			Debug.debug("Matrix.inverse() - Matrix (" + this.iRows + "," + this.iCols + ") is not inversible!\n");
			return false;
		}

		Matrix oI = getNewMatrix();

		if(!oI.makeIdentity())
		{
			return false;
		}

		Matrix oCombo = getMatrixCopy(this);

		//System.out.println("Boo copy");
		//System.out.println(oCombo);
		
		if(!oCombo.extend(oI))
		{
			return false;
		}

		//System.out.println("Baa extension");
		//System.out.println(oCombo);

		if(!oCombo.rowReduce())
		{
			return false;
		}

		//System.out.println("Bee row reduction");
		//System.out.println(oCombo);

		if(!oCombo.crop(oCombo.getCols() / 2, 0, oCombo.getCols() - 1, oCombo.getRows() - 1))
		{
			return false;
		}

		//System.out.println(oCombo);

		setMatrixData(oCombo);

		return true;
	}

	/**
	 * Matrix transpose.
	 * @return <code>true</code> if matrix was transposed, <code>false</code> otherwise
	 */
	public boolean transpose()
	{
		if(this.iRows == 0 || this.iCols == 0)
		{
			return false;
		}

		// Create a copy
//		Matrix oM = (Matrix)clone();
		Matrix oM = getMatrixCopy(this);

		// Swap dimensions
		int iOldRows = this.iRows;
		this.iRows = this.iCols;
		this.iCols = iOldRows;

		// Swap data elements
		if(this.iRows != 1 && this.iCols != 1)
		{
			for(int i = 0; i < oM.getRows(); i++)
			{
				for(int j = 0; j < oM.getCols(); j++)
				{
					if(i != j)
					{
						swapElement(oM, i, j);
					}
				}
			}
		}

		oM = null;

		return true;
	}

	/**
	 * @param poSourceMatrix
	 * @param i
	 * @param j
	 */
	private void swapElement(Matrix poSourceMatrix, int i, int j)
	{
		setElement(j, i, poSourceMatrix.getElement(i, j));
	}

	/**
	 * Makes current matrix an identity one.
	 * @return <code>true</code> if the operation completed successfully
	 */
	public boolean makeIdentity()
	{
		setAll(0);

		// Pick a smaller dimension to do not go outside boundaries
		int iSmallerDim = this.iCols > this.iRows ? this.iRows : this.iCols;

		for(int i = 0; i < iSmallerDim; i++)
		{
			setElement(i, i, 1);
		}

		return true;
	}

	/**
	 * Checks for strict identity matrix.
	 * Any deviations due to errors in floating point arithmetic
	 * will return <code>false</code>. If imprecise values are allowed,
	 * consider using <code>isNearlyIdentity()</code>.
	 * @return <code>true</code> if the matrix is strictly identity
	 * @see #isNearlyIdentity()
	 * @see #isNearlyIdentity(double)
	 */
	public final boolean isIdentity()
	{
		// Check if we're identity
		// then I = I * I should hold
		Matrix oMatrix = multiply(this);
		return oMatrix.equals(this);
	}

	/**
	 * Tests nearly-identity matrices with the specified delta.
	 * @param pdDelta the FP error to assume when checking for identity
	 * @return <code>true</code> if the matrix is nearly identity
	 */
	public final boolean isNearlyIdentity(final double pdDelta)
	{
		for(int i = 0; i < getRows(); i++)
		{
			for(int j = 0; j < getRows(); j++)
			{
				if(i == j)
				{
					if(1 - getElement(i, j) > pdDelta)
					{
						return false;
					}
				}
				else
				{
					if(getElement(i, j) > pdDelta)
					{
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Tests nearly-identity matrices with the default delta.
	 * @return <code>true</code> if the matrix is nearly identity
	 * @see #DEFAULT_ERROR_DELTA
	 */
	public final boolean isNearlyIdentity()
	{
		return isNearlyIdentity(DEFAULT_ERROR_DELTA);
	}

	/*
	 * ---------
	 * Operators
	 * ---------
	 */

	/**
	 * M3 = M1 + M2.
	 * @param poLHSMatrix M1
	 * @param poRHSMatrix M2
	 * @return M3; if cardinalities of M1 and M2 do not match; M1 is returned
	 */
	public static Matrix add(final Matrix poLHSMatrix, final Matrix poRHSMatrix)
	{
		Matrix oMatrix = (Matrix)poLHSMatrix.clone();
		//Matrix oMatrix = getMatrixCopy(poLHSMatrix);

		if(poLHSMatrix.getCols() != poRHSMatrix.getCols() || poLHSMatrix.getRows() != poRHSMatrix.getRows())
		{
			return oMatrix;
		}

		for(int i = 0; i < poLHSMatrix.getRows(); i++)
		{
			for(int j = 0; j < poLHSMatrix.getCols(); j++)
			{
				oMatrix.applyAdd(poLHSMatrix, poRHSMatrix, i, j);
			}
		}

		return oMatrix;
	}

	/**
	 * Actually applies the addition of operation to two elements of a
	 * matrix. Designed to be overridden for complex or otherwise matrices.
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @param i
	 * @param j
	 */
	protected Matrix applyAdd(final Matrix poLHSMatrix, final Matrix poRHSMatrix, int i, int j)
	{
		setElement(i, j, poLHSMatrix.getElement(i, j) + poRHSMatrix.getElement(i, j));
		return this;
	}

	/**
	 * Adds a scalar to the matrix: this = this + N.
	 * @param pdNum the scalar N
	 * @since 0.3.0.6 
	 * @return this
	 */
	public Matrix add(double pdNum)
	{
		for(int i = 0; i < this.adMatrix.length; i++)
		{
			this.adMatrix[i] += pdNum;
		}
		
		return this;
	}

	/**
	 * Adds a scalar to the matrix: M1 = M + N.
	 * @param poLHSMatrix M
	 * @param pdNum the scalar N
	 * @since 0.3.0.6
	 * @return M1
	 */
	public static Matrix add(final Matrix poLHSMatrix, double pdNum)
	{
		//Matrix oMatrix = getMatrixCopy(poLHSMatrix);
		Matrix oMatrix = (Matrix)poLHSMatrix.clone();

		for(int i = 0; i < poLHSMatrix.adMatrix.length; i++)
		{
			oMatrix.adMatrix[i] += pdNum;
		}
		
		return oMatrix; 
	}
	
	/**
	 * M3 = M1 - M2.
	 * @param poLHSMatrix M1
	 * @param poRHSMatrix M2
	 * @return M3
	 */
	public static Matrix minus(final Matrix poLHSMatrix, final Matrix poRHSMatrix)
	{
		//Matrix oMatrix = getMatrixCopy(poLHSMatrix);
		Matrix oMatrix = (Matrix)poLHSMatrix.clone();

		if(poLHSMatrix.iCols != poRHSMatrix.iCols || poRHSMatrix.iRows != poRHSMatrix.iRows)
		{
			return oMatrix;
		}

		for(int i = 0; i < poLHSMatrix.iRows; i++)
		{
			for(int j = 0; j < poLHSMatrix.iCols; j++)
			{
				oMatrix.applyMinus(poLHSMatrix, poRHSMatrix, i, j);
			}
		}

		return oMatrix;
	}

	/**
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @param i
	 * @param j
	 */
	protected Matrix applyMinus(final Matrix poLHSMatrix, final Matrix poRHSMatrix, int i, int j)
	{
		setElement(i, j, poLHSMatrix.getElement(i, j) - poRHSMatrix.getElement(i, j));
		return this;
	}

	/**
	 * M1 = -M.
	 * @param poMatrix M
	 * @return -M1
	 */
	public static Matrix minusUnary(final Matrix poMatrix)
	{
		//Matrix oM = new Matrix(poMatrix.getRows(), poMatrix.getCols());
		//Matrix oM = getNewMatrix(poMatrix.iRows, poMatrix.iCols);
		Matrix oM = poMatrix.getNewMatrix(poMatrix.iRows, poMatrix.iCols);

		// -M = 0 - M
		oM = minus(oM, poMatrix);

		return oM;
	}

	/**
	 * this = this - M.
	 * @param poMatrix M
	 * @return a copy of this matrix after subtraction
	 */
	public Matrix minus(final Matrix poMatrix)
	{
		Matrix oNewMatrix = minus(this, poMatrix);

		if(this.equals(oNewMatrix) == false)
		{
			//setMatrixArray(oNewMatrix.getMatrixArray());
			setMatrixData(oNewMatrix);
		}

		oNewMatrix = null;

		return this;
	}

	/**
	 * Subtracts a scalar from the matrix's each element: this = this - N.
	 * @param pdNum the scalar
	 * @since 0.3.0.6 
	 */
	public Matrix minus(double pdNum)
	{
		for(int i = 0; i < this.adMatrix.length; i++)
		{
			this.adMatrix[i] -= pdNum;
		}
		
		return this;
	}

	/**
	 * Subtracts a scalar from the matrix's each element: M1 = M - N.
	 * @param pdNum the scalar
	 * @since 0.3.0.6 
	 */
	public static Matrix minus(final Matrix poLHSMatrix, double pdNum)
	{
		Matrix oMatrix = (Matrix)poLHSMatrix.clone();
//		Matrix oMatrix = getMatrixCopy(poLHSMatrix);

		for(int i = 0; i < poLHSMatrix.adMatrix.length; i++)
		{
			oMatrix.adMatrix[i] -= pdNum;
		}
		
		return oMatrix; 
	}

	/**
	 * M1 = this * M.
	 * @param poMatrix M
	 * @return M1
	 */
	public Matrix multiply(final Matrix poMatrix)
	{
		return multiply(this, poMatrix);
	}

	/**
	 * M3 = M1 * M2.
	 * @param poLHSMatrix M1
	 * @param poRHSMatrix M2
	 * @return M3; M3 = 0x0 if the columns of M1 != rows of M2
	 */
	public static Matrix multiply(final Matrix poLHSMatrix, final Matrix poRHSMatrix)
	{
		if(poLHSMatrix.iCols != poRHSMatrix.iRows)
		{
			//return new Matrix(0, 0);
			//return getNewMatrix(0, 0);
			return poLHSMatrix.getNewMatrix(0, 0);
		}

		//Matrix oMatrix = new Matrix(poLHSMatrix.getRows(), poRHSMatrix.getCols());
		//Matrix oMatrix = getNewMatrix(poLHSMatrix.iRows, poRHSMatrix.iCols);
		Matrix oMatrix = poLHSMatrix.getNewMatrix(poLHSMatrix.iRows, poRHSMatrix.iCols);
		oMatrix.applyMultiply(poLHSMatrix, poRHSMatrix);
/*
		for(int i = 0; i < oMatrix.iRows; i++)
		{
			for(int j = 0; j < oMatrix.iCols; j++)
			{
				oMatrix.applyMultiply(poLHSMatrix, poRHSMatrix, i, j);
			}
		}
*/
		return oMatrix;
	}

	protected Matrix applyMultiply(final Matrix poLHSMatrix, final Matrix poRHSMatrix)
	{
		for(int i = 0; i < iRows; i++)
		{
			for(int j = 0; j < iCols; j++)
			{
				double dRowColSum = 0;
		
				for(int k = 0; k < poLHSMatrix.iCols; k++)
				{
					dRowColSum += poLHSMatrix.getElement(i, k) * poRHSMatrix.getElement(k, j);
				}
		
				setElement(i, j, dRowColSum);
			}
		}
		
		return this;
	}

	/**
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @param i
	 * @param j
	 */
	protected Matrix applyMultiply(final Matrix poLHSMatrix, final Matrix poRHSMatrix, int i, int j)
	{
		double dRowColSum = 0;

		for(int k = 0; k < poLHSMatrix.iCols; k++)
		{
			dRowColSum += poLHSMatrix.getElement(i, k) * poRHSMatrix.getElement(k, j);
		}

		setElement(i, j, dRowColSum);
		
		return this;
	}

	/**
	 * V2 = M * V1.
	 * @param poMatrix M
	 * @param poVector V1
	 * @return V2
	 * @since 0.3.0.3
	 */
	public static Vector multiply(final Matrix poMatrix, final Vector poVector)
	{
		// Here the cast of Vector to Matrix is necessary
		// to avoid infinite recursive calls to self
		return new Vector(multiply(poMatrix, (Matrix)poVector));
	}

	/**
	 * V1 = this * V.
	 * @param poVector V
	 * @return V1
	 * @since 0.3.0.3
	 */
	public Vector multiply(final Vector poVector)
	{
		return multiply(this, poVector);
	}

	/**
	 * M1 = M * d.
	 * @param poMatrix M
	 * @param pdNum d
	 * @return M1
	 */
	public static Matrix multiply(final Matrix poMatrix, final double pdNum)
	{
		Matrix oMatrix = (Matrix)poMatrix.clone();
		//Matrix oMatrix = getMatrixCopy(poMatrix);

		for(int i = 0; i < poMatrix.getElements(); i++)
		{
			oMatrix.adMatrix[i] *= pdNum;
		}
		
		return oMatrix;
	}

	/**
	 * this = this * d.
	 * @param pdNum d
	 * @return this
	 */
	public Matrix multiply(final double pdNum)
	{
		setMatrixArray(multiply(this, pdNum).getMatrixArray());
		return this;
	}

	/**
	 * M1 = d * M.
	 * @param pdNum d
	 * @param poMatrix M
	 * @return M1
	 */
	public static Matrix multiply(final double pdNum, final Matrix poMatrix)
	{
		return multiply(poMatrix, pdNum);
	}

	/**
	 * M = M / d.
	 * @param poMatrix M
	 * @param pdNum d
	 * @return modified M
	 */
	public static Matrix divide(Matrix poMatrix, final double pdNum)
	{
		double[] pdMd = poMatrix.getMatrixArray();

		for(int i = 0; i < poMatrix.getElements(); i++)
		{
			pdMd[i] /= pdNum;
		}
		
		return poMatrix;
	}

	/**
	 * @param poLHSMatrix
	 * @param poRHSMatrix
	 * @return
	 * @since 0.3.0.6
	 */
	public static Matrix divide(final Matrix poLHSMatrix, final Matrix poRHSMatrix)
	{
		Matrix oMatrix = (Matrix)poRHSMatrix.clone();
//		Matrix oMatrix = getMatrixCopy(poRHSMatrix);

		// Multiply by the inverse if possible.
		if(oMatrix.inverse() == true)
		{
			return multiply(poLHSMatrix, oMatrix);
		}
		else
		{
			assert false : "Matrix is not invertible";
			return null;
		}
	}
	
	/**
	 * this = this / d.
	 * @param pdNum d
	 * @since 0.3.0.3
	 */
	public Matrix divide(final double pdNum)
	{
		return divide(this, pdNum);
	}

	/**
	 * @param piPow
	 * @return
	 * @since 0.3.0.6
	 */
	public Matrix pow(int piPow)
	{
		Matrix oMatrix = pow(this, piPow);
		setMatrixData(oMatrix);
		return this;
	}
	
	/**
	 * @param poMatrix
	 * @param piPow
	 * @return
	 * @since 0.3.0.6
	 */
	public static Matrix pow(final Matrix poMatrix, int piPow)
	{
		Matrix oMatrix = null;

		if(piPow == 0)
		{
			oMatrix = new Matrix(poMatrix.iRows, poMatrix.iCols);
			oMatrix.makeIdentity();
		}
		else
		{
			//oMatrix = getMatrixCopy(poMatrix);
			oMatrix = (Matrix)poMatrix.clone();

			if(piPow == 1)
			{
				// just do nothing
			}
			else
			{
				// This is true for both > or < 0
				// M ^ n
				for(int i = 0; i < Math.abs(piPow); i++)
				{
					oMatrix.multiply(oMatrix);
				}
				
				// < 0; I / M ^ n
				if(piPow < 0)
				{
					Matrix oI = new Matrix(oMatrix.iRows, oMatrix.iCols);
					oI.makeIdentity();

					oMatrix = divide(oI, oMatrix);
				}
			}
		}
		
		return oMatrix;
	}
	
	/**
	 * this == M.
	 * @param poMatrix M
	 * @return <code>true</code> if this and parameter matrices are equal
	 */
	public boolean equals(final Matrix poMatrix)
	{
		return equals(this, poMatrix);
	}

	/**
	 * M1 == M2. The equality of two matrices determined by
	 * the equality of their dimensions and the contents.
	 * @param poLHSMatrix M1
	 * @param poRHSMatrix M2
	 * @return <code>true</code> of the matrices are equal
	 */
	public static boolean equals(final Matrix poLHSMatrix, final Matrix poRHSMatrix)
	{
		return
			poLHSMatrix.iRows == poRHSMatrix.iRows &&
			poLHSMatrix.iCols == poRHSMatrix.iCols &&
			Arrays.equals(poLHSMatrix.adMatrix, poRHSMatrix.adMatrix);
	}


	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object poMatrix)
	{
		if(poMatrix instanceof Matrix)
		{
			return this.equals((Matrix)poMatrix);
		}
		else
		{
			return false;
		}
	}


	/**
	 * Returns internal matrix representation as 2-dimensional array of doubles.
	 * @return 2-dimensional array of doubles
	 */
	public double[][] getMatrix2D()
	{
		// Do an assertion check first
		getElements();

		double[][] add2DMatrix = new double[this.iRows][this.iCols];

		for(int i = 0; i < this.iRows; i++)
		{
			Arrays.copy(add2DMatrix[i], 0, this.adMatrix, i * iCols, this.iCols);
		}

		return add2DMatrix;
	}

	/**
	 * Sets internal array based on 2-dimensional parameter.
	 * Works under assumption that all rows are of the same length.
	 * @param padd2DMatrix the two dimensional array of doubles to convert from
	 */
	public void setMatrix2D(final double[][] padd2DMatrix)
	{
		this.iRows = padd2DMatrix.length;
		this.iCols = padd2DMatrix[0].length;

		this.adMatrix = new double[this.iRows * this.iCols];

		for(int i = 0; i < this.iRows; i++)
		{
			Arrays.copy(this.adMatrix, i * iCols, padd2DMatrix[i], this.iCols);
		}
	}

	/**
	 * Implements Clonable interface.
	 * Creates a deep copy of the object by cloning the internal matrix array.
	 * @return object copy of this matrix
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		return new Matrix(this);
	}

	/**
	 * Returns a scaled vector.
	 *
	 * @param poPointToScale the vector of objects to be scaled.
	 * @param poScaleFactors the vector of objects containing the vector's scaling factors
	 * @return a scaled vector
	 * @throws IllegalArgumentException if the sizes of the two vectors are different
	 * @since 0.3.0.3
	 */
	public Vector scale(Vector poPointToScale, Vector poScaleFactors)
	{
		if(poScaleFactors.getElements() == DEFAULT_COLS)
		{
			for(int i = 0; i < DEFAULT_COLS; i++)
			{
				setElement(i, i, poScaleFactors.getElement(i));
			}

			return multiply(poPointToScale);
		}
		else
		{
			throw new IllegalArgumentException
			(
				"Size does not match: " + poPointToScale.getElements() + " != "
				+ poScaleFactors.getElements()
			);
		}
	}

	/**
	 * Returns a scaled vector.
	 *
	 * @param poPointToScale the vector of objects to be scaled.
	 * @param padScaleFactors the array of doubles containing the vector's scaling factors
	 * @return a scaled vector
	 * @throws IllegalArgumentException if the sizes of the array and the vector are different
	 * @since 0.3.0.3
	 */
	public Vector scale(Vector poPointToScale, double[] padScaleFactors)
	{
	 	return scale(poPointToScale, new Vector(padScaleFactors));
	}

	/**
	 * Returns a scaled vector.
	 *
	 * @param poPointToScale the vector of objects to be scaled.
	 * @param pdScaleX the X axis scaled factor of double
	 * @param pdScaleY the Y axis scaled factor of double
	 * @param pdScaleZ the Z axis scaled factor of double
	 * @return a scaled vector
	 * @since 0.3.0.3
	 */
	public Vector scale(Vector poPointToScale, double pdScaleX, double pdScaleY, double pdScaleZ)
	{
		double[] padScaleFactors =
		{
			pdScaleX, pdScaleY, pdScaleZ, 1
		};

		return scale(poPointToScale, padScaleFactors);
	}

	/**
	 * Translates a vector according to the given size.
	 *
	 * @param poPointToTranslate the vector of objects to be translated.
	 * @param poTranslateVals the vector of objects that contains the vector's translating values
	 * @return a translated Vector
	 * @throws IllegalArgumentException if the sizes of the array and the vector are different
	 * @since 0.3.0.3
	 */
	public Vector translate(Vector poPointToTranslate, Vector poTranslateVals)
	{
		if(poTranslateVals.getElements() == poPointToTranslate.getElements())
		{
			for(int i = 0; i < poPointToTranslate.getElements(); i++)
			{
				setElement(i, poPointToTranslate.getElements() - 1,  poTranslateVals.getElement(i));
			}

			return multiply(poPointToTranslate);
		}
		else
		{
			throw new IllegalArgumentException
			(
				"Size does not match: " + poPointToTranslate.getElements() + " != "
				+ poTranslateVals.getElements()
			);
		}
	}

	/**
	 * Translates a vector according to the given size.
	 *
	 * @param poPointToTranslate the vector of objects to be translated.
	 * @param padTranslateVals the Array of doubles that contains the vector's translating values
	 * @return a translated Vector
	 * @throws IllegalArgumentException if the sizes of the array and the vector are different
	 * @since 0.3.0.3
	 */
	public Vector translate(Vector poPointToTranslate, double[] padTranslateVals)
	{
		return translate(poPointToTranslate, new Vector(padTranslateVals));
	}

	/**
	 * Translates a vector according to the given size.
	 *
	 * @param poPointToTranslate the vector of objects to be translated.
	 * @param pdTranslateX the X axis translated factor of double
	 * @param pdTranslateY the Y axis translated factor of double
	 * @param pdTranslateZ the Z axis translated factor of double
	 * @return a translated Vector
	 * @throws IllegalArgumentException if the sizes of the vector is not one bigger than the number of scaled factors
	 * @since 0.3.0.3
	 */
	public Vector translate(Vector poPointToTranslate, double pdTranslateX, double pdTranslateY, double pdTranslateZ)
	{
		double[] padTranslateVals =
		{
			pdTranslateX, pdTranslateY, pdTranslateZ, 1
		};

		return translate(poPointToTranslate, padTranslateVals);
	}

	/**
	 * Rotates a vector according to the given axis' factor and angle's degree.
	 * @param poPointToRotate the vector of objects to be rotated
	 * @param poRotateFactors the vector of objects that contains the vector's rotating factors
	 * @return a rotated Vector
	 * @throws IllegalArgumentException if the sizes of two vectors are different
	 * @since 0.3.0.3
	 */
	public Vector rotate(Vector poPointToRotate, Vector poRotateFactors)
	{
		if(poRotateFactors.getElements() == poPointToRotate.getElements())
		{
			for(int i = 0; poRotateFactors.getElement(i) != 1 && i < poRotateFactors.getElements() - 2; i++)
			{
				for(int j = 0; poRotateFactors.getElement(j) != 1 && i < poRotateFactors.getElements() - 2; j++)
				{
					if(i > j)
					{
						//set the elements of the upper half part of the Matrix
						setElement
						(
							i, j,
							Math.pow(-1, i + j + 1) * Math.sin(Math.toRadians(poRotateFactors.getElement(poRotateFactors.size() - 1)))
						);
					}
					else if(i == j)
					{
						//set the diagonal elements of the Matrix
						setElement(i, i, Math.cos(Math.toRadians((poRotateFactors.getElement(poRotateFactors.size() - 1)))));
					}
					else
					{
						//set the elements of the lower half part of the Matrix
						setElement(i, j, Math.pow(-1, i + j) * Math.sin(Math.toRadians(poRotateFactors.getElement(poRotateFactors.size() - 1))));
					}
				}
			}

			return multiply(poPointToRotate);
		}
		else
		{
			throw new IllegalArgumentException
			(
				"Size does not match: " + poPointToRotate.getElements() + " != "
				+ poRotateFactors.getElements()
			);
		}
	  }

	/**
	 * Rotates a vector according to the given axis' factor and angle's degree.
	 *
	 * @param poPointToRotate the vector of objects to be rotated.
	 * @param padRotateFactors the Array of doubles that contains the vector's rotating values
	 * @return a rotated Vector
	 * @throws IllegalArgumentException if the sizes of the array and the vector are different
	 * @since 0.3.0.3
	 */
	public Vector rotate(Vector poPointToRotate, double[] padRotateFactors)
	{
		return rotate(poPointToRotate, new Vector(padRotateFactors));
	}

	/**
	 * Rotates a vector according to the given axis' factor and angle's degree.
	 * @param poPointToRotate the vector of objects to be rotated.
	 * @param pdRotateX the X axis rotated factor of double
	 * @param pdRotateY the Y axis rotated factor of double
	 * @param pdRotateZ the Z axis rotated factor of double
	 * @param pdRotateAngle the rotate angle of degree around one axis
	 * @return a rotated Vector
	 * @throws IllegalArgumentException if the sizes of the vector is not one bigger than the number of rotated factors
	 * @since 0.3.0.3
	 */
	public Vector rotate(Vector poPointToRotate, double pdRotateX,double pdRotateY,double pdRotateZ, double pdRotateAngle)
	{
		double[] padRotateFactors = {pdRotateX, pdRotateY, pdRotateZ, pdRotateAngle};
		return rotate(poPointToRotate, padRotateFactors);
	}

	/**
	 * Returns a sheared vector.
	 *
	 * @param poPointToShear the vector of objects to be sheared.
	 * @param poShearFactors the vector of objects containing the vector's shearing factors
	 * @return a sheared vector
	 * @throws IllegalArgumentException if the sizes of the two vectors are different
	 * @since 0.3.0.3
	 */
	public Vector shear(Vector poPointToShear, Vector poShearFactors)
	{
		if(poShearFactors.getElements() + 1 == DEFAULT_COLS)
		{
			for(int i = 0; i < poShearFactors.getElements(); i++)
			{
				if(poShearFactors.getElement(i) == 0)
				{
					for(int j = 0; j != i && j < poShearFactors.getElements(); j++)
					{
						setElement(j, i, poShearFactors.getElement(j));
					}
				}
			}

			return multiply(poPointToShear);
		}
		else
		{
			throw new IllegalArgumentException
			(
				"Size does not match: " + poPointToShear.getElements() + " != "
				+ poShearFactors.getElements()
			);
		}
	}

	/**
	 * Returns a sheared vector.
	 *
	 * @param poPointToShear the vector of objects to be sheared
	 * @param padShearFactors the array of doubles containing the vector's shearing factors
	 * @return a sheared vector
	 * @throws IllegalArgumentException if the sizes of the array and the vector are different
	 * @since 0.3.0.3
	 */
	public Vector shear(Vector poPointToShear, double[] padShearFactors)
	{
		return shear(poPointToShear, new Vector(padShearFactors));
	}

	/**
	 * Returns a sheared vector.
	 *
	 * @param poPointToShear the vector of objects to be sheared
	 * @param pdShearX the X axis sheared factor of double
	 * @param pdShearY the Y axis sheared factor of double
	 * @param pdShearZ the Z axis sheared factor of double
	 * @return a sheared vector
	 * @since 0.3.0.3
	 */
	public Vector shear(Vector poPointToShear, double pdShearX, double pdShearY, double pdShearZ)
	{
		double[] padShearFactors =
		{
			pdShearX, pdShearY, pdShearZ, 1
		};

		return shear(poPointToShear, padShearFactors);
	}

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.47 $";
	}

	/**
	 * Indicates the direction in which a matrix to be extended.
	 * Might emerge to generic integer Enum in marf.util eventually.
	 *
	 * @author Serguei Mokhov
	 */
	public static class Direction
	{
		/**
		 * Indicates East direction.
		 */
		public static final int EAST  = 0;

		/**
		 * Indicates West direction.
		 */
		public static final int WEST  = 1;

		/**
		 * Indicates North direction.
		 */
		public static final int NORTH = 2;

		/**
		 * Indicates South direction.
		 */
		public static final int SOUTH = 3;

		/**
		 * Default direction is <code>EAST</code>.
		 */
		private int iDirection = EAST;

		/**
		 * Default constructor.
		 */
		public Direction()
		{
		}

		/**
		 * Direction Constructor. Calls <code>setDirection()</code> internally.
		 * @param piDirection custom direction to extend
		 * @throws RuntimeException if piDirection is out of range
		 * @see #setDirection(int)
		 */
		public Direction(final int piDirection)
		{
			setDirection(piDirection);
		}

		/**
		 * Copy Constructor.
		 * @param poDirection custom direction object to extend
		 */
		public Direction(final Direction poDirection)
		{
			this.iDirection = poDirection.getDirection();
		}

		/**
		 * Retrieves current direction.
		 * @return current value of direction
		 */
		public int getDirection()
		{
			return this.iDirection;
		}

		/**
		 * Sets new value of current direction.
		 * @param piDirection current value of direction to be
		 * @throws RuntimeException if piDirection is outside of valid range of values.
		 */
		public void setDirection(final int piDirection)
		{
			if(piDirection < EAST || piDirection > SOUTH)
			{
				throw new RuntimeException
				(
					getClass().getName() +
					".setDirection() - Invalid direction: " +
					piDirection
				);
			}

			this.iDirection = piDirection;
		}
	}
}

// EOF
