package marf.Stats;

import java.io.Serializable;
import java.util.Vector;

import marf.MARF;
import marf.util.Debug;
import marf.util.Matrix;


/**
 * <p>Probabilities Table for N-grams.
 * The class is properly synchronized as of 0.3.0.5.
 * </p>
 *
 * @author Serguei A. Mokhov
 * @version $Id: ProbabilityTable.java,v 1.42 2010/08/21 21:56:55 mokhov Exp $
 * @since 0.3.0.2
 */
public class ProbabilityTable
implements Serializable
{
	/**
	 * Observations by columns.
	 * Observation's index in the vector is the one in the table
	 * by n-grams.
	 */
	private Vector<Object> oColumnIndex = new Vector<Object>();

	/**
	 * Vector of vectors of probabilities, which are Double
	 * objects. To save space <code>null</code> means 0.0.
	 */
	private Matrix oNMatrix = new Matrix();

	/**
	 * Current natural language.
	 * Default is "en".
	 */
	private String strLang = "en";

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = 8675912614927409276L;

	/**
	 * Constructor with the language parameter other than default.
	 * @param pstrLanguage the language this table is for
	 * @see #strLang
	 */
	public ProbabilityTable(String pstrLanguage)
	{
		setLang(pstrLanguage);
	}

	/**
	 * Not implemented.
	 * @param poObservationCause unused
	 * @param poObservationEffect unused
	 * @return 0
	 */
	public final synchronized double p(final Observation poObservationCause, final Observation poObservationEffect)
	{
		return 0;
	}

	/**
	 * Not implemented.
	 * @param poObservationCause unused
	 * @param poObservationList unused
	 * @return 0
	 */
	public final synchronized double p(final Observation poObservationCause, final Vector poObservationList)
    {
		return 0;
    }

	/**
	 * Retrieves the probability of a list of observations
	 * as a number of occurrences.
	 * @param poObservationList a collection of observations
	 * @return the probability
	 */
	//public final synchronized double p(final Vector poObservationList)
	public final synchronized Double p(final Vector<String> poObservationList)
    {
		double dProbability = getOccurrence(poObservationList);

		Debug.debug
		(
			"poObservationList.size=" + poObservationList.size()
			+ ", Observations=" + poObservationList
			+ ", P=" + dProbability
			+ ", lang=" + getLang()
		);

		return dProbability;
    }

	/**
	 * Retrieves the size of the table.
	 * @return the size (as the index size)
	 */
	public final synchronized int size()
	{
		return this.oColumnIndex.size();
	}

	/**
	 * Allows setting an occurrence given the <i>x</i> coordinate.
	 * An unigram model is assumed.
	 * @param piX the <i>x</i> index
	 * @param pdOccurence the occurrence
	 * @throws ArrayIndexOutOfBoundsException if the coordinate is out of range
	 */
	public final synchronized void setOccurrence(final int piX, final double pdOccurence)
	{
		if
		(
			(piX > this.oColumnIndex.size() - 1)
			||
			piX < 0
		)
		throw new ArrayIndexOutOfBoundsException
		(
			"ProbabilityTable.setOccurence(" + piX +
			") out of bounds (" + this.oColumnIndex.size() + ")"
		);

		Vector oObservationList = new Vector();
		oObservationList.add(this.oColumnIndex.elementAt(piX));

		setOccurrence(oObservationList, pdOccurence);
	}

	/**
	 * Allows setting an occurrence given the <i>x</i> and <i>y</i> coordinates.
	 * A bigram model is assumed.
	 * @param piX the <i>x</i> index
	 * @param piY the <i>y</i> index
	 * @param pdOccurrence the occurrence
	 * @throws ArrayIndexOutOfBoundsException if either of the coordinates is out of range
	 */
	public final synchronized void setOccurrence(final int piX, final int piY, final double pdOccurrence)
	{
		if
		(
			(piX > this.oColumnIndex.size() - 1)
			||
			(piY > this.oColumnIndex.size() - 1)
			||
			piX < 0
			||
			piY < 0
		)
		throw new ArrayIndexOutOfBoundsException
		(
			"ProbabilityTable.setOccurence(" + piX + "," + piY +
			") out of bounds (" + this.oColumnIndex.size() + ")"
		);

		Vector oObservationList = new Vector();
		oObservationList.add(this.oColumnIndex.elementAt(piY));
		oObservationList.add(this.oColumnIndex.elementAt(piX));

		setOccurrence(oObservationList, pdOccurrence);
	}

	/**
	 * Allows setting an occurrence given the <i>x</i>, <i>y</i>, and <i>z</i> coordinates.
	 * A trigram model is assumed.
	 * @param piX the <i>x</i> index
	 * @param piY the <i>y</i> index
	 * @param piZ the <i>z</i> index
	 * @param pdOccurrence the occurrence
	 * @throws ArrayIndexOutOfBoundsException if either of the coordinates is out of range
	 */
	public final synchronized void setOccurrence(final int piX, final int piY, final int piZ, final double pdOccurrence)
	{
		if
		(
			(piX > this.oColumnIndex.size() - 1)
			||
			(piY > this.oColumnIndex.size() - 1)
			||
			(piZ > this.oColumnIndex.size() - 1)
			||
			piX < 0
			||
			piY < 0
			||
			piZ < 0
		)
		throw new ArrayIndexOutOfBoundsException
		(
			"ProbabilityTable.setOccurence(" + piX + "," + piY + ") out of bounds (" +
			this.oColumnIndex.size() + ")"
		);

		Vector oObservationList = new Vector();
		oObservationList.add(this.oColumnIndex.elementAt(piZ));
		oObservationList.add(this.oColumnIndex.elementAt(piY));
		oObservationList.add(this.oColumnIndex.elementAt(piX));

		setOccurrence(oObservationList, pdOccurrence);
	}

	/**
	 * Allows getting an occurrence given the <i>x</i> coordinate.
	 * An unigram model is assumed.
	 * @param piX the <i>x</i> index
	 * @return the number of occurrences of the item at the coordinates.
	 * @throws ArrayIndexOutOfBoundsException if the coordinate is out of range
	 */
	public final synchronized double getOccurrence(final int piX)
	{
		if
		(
			(piX > this.oColumnIndex.size() - 1)
			||
			piX < 0
		)
		throw new ArrayIndexOutOfBoundsException
		(
			"ProbabilityTable.getOccurence(" + piX + ") out of bounds (" +
			this.oColumnIndex.size() + ")"
		);

		Vector oObservationList = new Vector();
		oObservationList.add(this.oColumnIndex.elementAt(piX));

		return getOccurrence(oObservationList);
	}

	/**
	 * Allows getting an occurrence given <i>x</i> and <i>y</i> coordinates.
	 * A bigram model is assumed.
	 * @param piX the <i>x</i> index
	 * @param piY the <i>y</i> index
	 * @return the number of occurrences of the item at the coordinates.
	 * @throws ArrayIndexOutOfBoundsException if either of the coordinates is out of range
	 */
	public final synchronized double getOccurrence(final int piX, final int piY)
	{
		Debug.debug("getOccurence("+piX+","+piY+")");

		if
		(
			(piX > this.oColumnIndex.size() - 1)
			||
			(piY > this.oColumnIndex.size() - 1)
			||
			piX < 0
			||
			piY < 0
		)
		throw new ArrayIndexOutOfBoundsException
		(
			"ProbabilityTable.getOccurence(" + piX + "," + piY + ") out of bounds (" +
			this.oColumnIndex.size() + ")"
		);

		Vector oObservationList = new Vector();
		oObservationList.add(this.oColumnIndex.elementAt(piY));
		oObservationList.add(this.oColumnIndex.elementAt(piX));

		return getOccurrence(oObservationList);
	}

	/**
	 * Allows getting an occurrence given <i>x</i>, <i>y</i>, and <i>z</i> coordinates.
	 * A trigram model is assumed.
	 * @param piX the <i>x</i> index
	 * @param piY the <i>y</i> index
	 * @param piZ the <i>z</i> index
	 * @return the number of occurrences of the item at the coordinates.
	 * @throws ArrayIndexOutOfBoundsException if either of the coordinates is out of range
	 */
	public final double getOccurrence(final int piX, final int piY, final int piZ)
	{
		if
		(
			(piX > this.oColumnIndex.size() - 1)
			||
			(piY > this.oColumnIndex.size() - 1)
			||
			(piZ > this.oColumnIndex.size() - 1)
			||
			piX < 0
			||
			piY < 0
			||
			piZ < 0
		)
		throw new ArrayIndexOutOfBoundsException
		(
			"ProbabilityTable.getOccurence(" + piX + "," + piY + "," + piZ + ") out of bounds (" +
			this.oColumnIndex.size() + ")"
		);

		Vector oObservationList = new Vector();
		oObservationList.add(this.oColumnIndex.elementAt(piZ));
		oObservationList.add(this.oColumnIndex.elementAt(piY));
		oObservationList.add(this.oColumnIndex.elementAt(piX));

		return getOccurrence(oObservationList);
	}

	/**
	 * Allows setting an occurrence of a list of observations.
	 * Works with uni-, bi-, and trigram models.
	 * @param poObservationList the list
	 * @param pdOccurrence the desired occurrence
	 */
	public final synchronized void setOccurrence(final Vector<String> poObservationList, final double pdOccurrence)
	{
		int iUniIndex;
		int iBiIndex;
		int iTriIndex;

//		Debug.debug("setOccurrence(poObservationList["+poObservationList+"])");

		switch(poObservationList.size())
		{
			// Unigram
			case 1:
			{
				iUniIndex = this.oColumnIndex.indexOf(poObservationList.elementAt(0));

				if(iUniIndex == -1)
				{
					this.oColumnIndex.add(poObservationList.elementAt(0));
					this.oNMatrix.add(new Double(pdOccurrence));
				}
				else
				{
					this.oNMatrix.setElementAt(new Double(pdOccurrence), iUniIndex);
				}

				break;
			}

			// Bigram
			case 2:
			{
				iUniIndex = this.oColumnIndex.indexOf(poObservationList.elementAt(0));

				Vector<Double> oRow;

				if(iUniIndex == -1)
				{
					this.oColumnIndex.add(poObservationList.elementAt(0));
					iUniIndex = this.oColumnIndex.size() - 1;

					oRow = new Vector<Double>();
					this.oNMatrix.add(oRow);
				}
				else
				{
					oRow = (Vector)this.oNMatrix.elementAt(iUniIndex);

					// Fill in a gap
					if(oRow == null)
					{
						oRow = new Vector<Double>();
						this.oNMatrix.setElementAt(oRow, iUniIndex);
					}
				}

				iBiIndex = this.oColumnIndex.indexOf(poObservationList.elementAt(1));

				if(iBiIndex == -1)
				{
					this.oColumnIndex.add(poObservationList.elementAt(1));
					iBiIndex = this.oColumnIndex.size() - 1;
					this.oNMatrix.add(new Vector());
				}

				oRow.ensureCapacity(iBiIndex + 1);
				oRow.setSize(iBiIndex + 1);

				oRow.setElementAt(pdOccurrence, iBiIndex);

				break;
			}

			// Trigram
			case 3:
			{
				iUniIndex = this.oColumnIndex.indexOf(poObservationList.elementAt(0));

				Vector<Vector<Double>> oPlane;
				Vector<Double> oRow;

				if(iUniIndex == -1)
				{
					this.oColumnIndex.add(poObservationList.elementAt(0));
					iUniIndex = this.oColumnIndex.size() - 1;

					oPlane = new Vector<Vector<Double>>();
					this.oNMatrix.add(oPlane);
				}
				else
				{
/*					Debug.debug
					(
						"oColumnIndex.size=" + this.oColumnIndex.size() + ", " +
						"iUniIndex=" + iUniIndex + ", " +
						"oNMatrix.size = " + this.oNMatrix.size()
					);
*/
					oPlane = (Vector)this.oNMatrix.elementAt(iUniIndex);

					if(oPlane == null)
					{
						oPlane = new Vector<Vector<Double>>();

						oPlane.ensureCapacity(iUniIndex + 1);
						oPlane.setSize(iUniIndex + 1);

						this.oNMatrix.setElementAt(oPlane, iUniIndex);
					}
				}

				iBiIndex = this.oColumnIndex.indexOf(poObservationList.elementAt(1));

				if(iBiIndex == -1)
				{
					this.oColumnIndex.add(poObservationList.elementAt(1));
					iBiIndex = this.oColumnIndex.size() - 1;

					this.oNMatrix.ensureCapacity(iBiIndex + 1);
					this.oNMatrix.setSize(iBiIndex + 1);

					oRow = new Vector<Double>();

					oPlane.ensureCapacity(iBiIndex + 1);
					oPlane.setSize(iBiIndex + 1);

					oPlane.setElementAt(oRow, iBiIndex);
				}
				else
				{
/*					Debug.debug
					(
						"oColumnIndex.size=" + oColumnIndex.size() + ", " +
						"iBiIndex=" + iBiIndex + ", " +
						"oPlane.size = " + oPlane.size()
					);
*/
					oPlane.ensureCapacity(iBiIndex + 1);
					oPlane.setSize(iBiIndex + 1);

					oRow = oPlane.elementAt(iBiIndex);

					// fill in gap created by tri-index
					if(oRow == null)
					{
						oRow = new Vector<Double>();
						oPlane.setElementAt(oRow, iBiIndex);
					}
				}

				iTriIndex = this.oColumnIndex.indexOf(poObservationList.elementAt(2));

				if(iTriIndex == -1)
				{
					this.oColumnIndex.add(poObservationList.elementAt(2));
					iTriIndex = this.oColumnIndex.size() - 1;

					this.oNMatrix.ensureCapacity(iTriIndex + 1);
					this.oNMatrix.setSize(iTriIndex + 1);

					oPlane.ensureCapacity(iTriIndex + 1);
					oPlane.setSize(iTriIndex + 1);

//					oPlane.setElementAt(new Vector(), iTriIndex);
				}

				oRow.ensureCapacity(iTriIndex + 1);
				oRow.setSize(iTriIndex + 1);

				oRow.setElementAt(pdOccurrence, iTriIndex);

				break;
			}
		}
	}

	/**
	 * Allows getting the occurrence of a list of observations.
	 * Works with uni-, bi-, and trigram models.
	 * @param poObservationList the list
	 * @return the number of occurrences
	 */
	public final synchronized double getOccurrence(final Vector<String> poObservationList)
	{
		double dOccurrence = 0.0;

		int iUniIndex;
		int iBiIndex;
		int iTriIndex;

//		Debug.debug("getOccurence(poObservationList["+poObservationList+"])");

		// Size is the n-gram
		switch(poObservationList.size())
		{
			case 1:
			{
				iUniIndex = this.oColumnIndex.indexOf(poObservationList.elementAt(0));

				if(iUniIndex == -1)
				{
					return 0.0;
				}

				Double oOccurrence = (Double)this.oNMatrix.elementAt(iUniIndex);

				if(oOccurrence == null)
				{
					return 0.0;
				}

				dOccurrence = oOccurrence.doubleValue();

				break;
			}

			case 2:
			{
				iUniIndex = this.oColumnIndex.indexOf(poObservationList.elementAt(0));
				iBiIndex = this.oColumnIndex.indexOf(poObservationList.elementAt(1));

				if(iUniIndex == -1 || iBiIndex == -1)
				{
					return 0.0;
				}

				Vector<Double> oRow = (Vector<Double>)this.oNMatrix.elementAt(iUniIndex);

				if(oRow == null || oRow.size() < iBiIndex + 1)
				{
					return 0.0;
				}

				Double oOccurrence = oRow.elementAt(iBiIndex);

				if(oOccurrence == null)
				{
					return 0.0;
				}

				dOccurrence = oOccurrence.doubleValue();

				break;
			}

			case 3:
			{
				iUniIndex = this.oColumnIndex.indexOf(poObservationList.elementAt(0));
				iBiIndex  = this.oColumnIndex.indexOf(poObservationList.elementAt(1));
				iTriIndex = this.oColumnIndex.indexOf(poObservationList.elementAt(2));

				if(iUniIndex == -1 || iBiIndex == -1 || iTriIndex == -1)
				{
					return 0.0;
				}

				Vector oPlane = (Vector)this.oNMatrix.elementAt(iUniIndex);

				if(oPlane == null || oPlane.size() < iBiIndex + 1)
				{
					return 0.0;
				}

				Vector<Double> oRow = (Vector<Double>)oPlane.elementAt(iBiIndex);

				if(oRow == null || oRow.size() < iTriIndex + 1)
				{
					return 0.0;
				}

				Double oOccurrence = oRow.elementAt(iTriIndex);

				if(oOccurrence == null)
				{
					return 0.0;
				}

				dOccurrence = oOccurrence.doubleValue();

				break;
			}
		}

		//Debug.debug("dOccurence = " + dOccurence);

		return dOccurrence;
	}

	/**
	 * Increments the frequency of occurrences of a list of observations.
	 * @param poObservationList the list to work with
	 * @return the new frequency
	 * In 0.3.0.6 changed from int to Double to avoid the loss of precision
	 * that was not warranted anyway.
	 *//*
	public final synchronized int incFrequency(final Vector poObservationList)
	{
		int iFrequency = (int)getOccurrence(poObservationList) + 1;
		setOccurrence(poObservationList, iFrequency);
		return iFrequency;
	}*/
	public final synchronized Double incFrequency(final Vector<String> poObservationList)
	{
		Double dFrequency = getOccurrence(poObservationList) + 1;
		setOccurrence(poObservationList, dFrequency);
		return dFrequency;
	}

	/**
	 * Not implemented.
	 * @return false
	 */
	public final synchronized boolean normalize()
	{
		return false;
	}

	/**
	 * Allows dumping the contents of the table in
	 * the CSV format.
	 */
	public synchronized void dumpCSV()
	{
		switch(MARF.NLP.getNgramModel())
		{
			case MARF.ENgramModels.UNIGRAM:
			{
				for(int n = 0; n < this.oColumnIndex.size(); n++)
				{
					System.out.print("," + this.oColumnIndex.elementAt(n));
				}

				System.out.println();

				for(int j = 0; j < this.oColumnIndex.size(); j++)
				{
					System.out.print(getOccurrence(j) + ",");
				}

				break;
			}

			case MARF.ENgramModels.BIGRAM:
			{
				for(int n = 0; n < this.oColumnIndex.size(); n++)
				{
					System.out.print("," + this.oColumnIndex.elementAt(n));
				}

				System.out.println();

				for(int i = 0; i < this.oColumnIndex.size(); i++)
				{
					System.out.print(this.oColumnIndex.elementAt(i).toString());

					for(int j = 0; j < this.oColumnIndex.size(); j++)
					{
						System.out.print("," + getOccurrence(j, i));
					}

					System.out.println();
				}

				break;
			}

			case MARF.ENgramModels.TRIGRAM:
			{
				for(int i = 0; i < this.oColumnIndex.size(); i++)
				{
					for(int n = 0; n < this.oColumnIndex.size(); n++)
					{
						System.out.print("," + this.oColumnIndex.elementAt(n));
					}

					System.out.println();

					for(int j = 0; j < this.oColumnIndex.size(); j++)
					{
						System.out.print(this.oColumnIndex.elementAt(j));

						for(int k = 0; k < this.oColumnIndex.size(); k++)
						{
							System.out.print("," + getOccurrence(k, j, i));
						}

						System.out.println();
					}

					System.out.println();
				}

				break;
			}
		}
	}

	/**
	 * Allows setting a natural language associated with this table.
	 * @param pstrLang the language
	 */
	public final synchronized void setLang(final String pstrLang)
	{
		this.strLang = pstrLang;
	}

	/**
	 * Retrieves current language.
	 * @return the associated natural language for this table
	 */
	public final synchronized String getLang()
	{
		return this.strLang;
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.42 $";
	}
}

// EOF
