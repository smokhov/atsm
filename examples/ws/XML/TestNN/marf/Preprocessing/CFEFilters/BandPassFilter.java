package marf.Preprocessing.CFEFilters;

import marf.Preprocessing.PreprocessingException;
import marf.Storage.Sample;
import marf.math.ComplexMatrix;


/**
 * <p>Implements band-pass CFE filter based on Shivani Bhat's
 * thesis work.
 * </p>
 * 
 * $Id: BandPassFilter.java,v 1.4 2007/01/29 00:42:42 mokhov Exp $
 * 
 * @version $Revision: 1.4 $
 * @author Serguei Mokhov
 * @author Shivani Haridas Bhat
 */
public class BandPassFilter
extends CFEFilter
{
	/**
	 * LowPassFilter Constructor.
	 * @param poSample incoming sample
	 * @throws PreprocessingException
	 */
	public BandPassFilter(Sample poSample)
	throws PreprocessingException
	{
		super(poSample);
	}

	public ComplexMatrix h()
	{
		try
		{
			//HB=HL.*HH;
			HighPassFilter oHH = new HighPassFilter(this.oSample);
			LowPassFilter oLH = new LowPassFilter(this.oSample);
			
			oLH.oZ1 = oHH.oZ1 = this.oZ1;
			oLH.oZ2 = oHH.oZ2 = this.oZ2;

			return ComplexMatrix.multiply(oLH.h(), oHH.h());
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}
	}
}

// EOF
