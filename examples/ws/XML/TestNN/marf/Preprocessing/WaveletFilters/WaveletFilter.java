package marf.Preprocessing.WaveletFilters;

import marf.Preprocessing.Filter;
import marf.Preprocessing.IPreprocessing;
import marf.Preprocessing.PreprocessingException;
import marf.Storage.Sample;
import marf.math.Algorithms;
import marf.math.MathException;
import marf.util.Arrays;
import marf.util.Debug;
import marf.util.NotImplementedException;


/**
 * Implements simple separating discrete wavelet transform (SDWT) filter.
 * Derivatives may implement more complex filters by overriding this one.
 * 
 * @author Serguei Mokhov
 * @version $Id: WaveletFilter.java,v 1.7 2012/06/04 02:26:21 mokhov Exp $
 */
public class WaveletFilter
extends Filter
{
	/**
	 * For serialization versioning.
	 */
	private static final long serialVersionUID = -2327003693081462142L;

	/**
	 * 
	 */
	public WaveletFilter()
	{
		super();
	}

	/**
	 * @param poPreprocessing
	 * @throws PreprocessingException
	 */
	public WaveletFilter(IPreprocessing poPreprocessing)
	throws PreprocessingException
	{
		super(poPreprocessing);
	}

	/**
	 * @param poSample
	 * @throws PreprocessingException
	 */
	public WaveletFilter(Sample poSample)
	throws PreprocessingException
	{
		super(poSample);
	}

	/* (non-Javadoc)
	 * @see marf.Preprocessing.IFilter#filter(double[], double[])
	 */
	public boolean filter(double[] padSample, double[] padFiltered)
	throws PreprocessingException
	{
		try
		{
			//int iResponseSize = this.adFreqResponse.length;
/*
			double[] adBuffer = new double[iResponseSize];
			double[] adBufferImag = new double[iResponseSize];
			double[] adOutputReal = new double[iResponseSize];
			double[] adOutputImag = new double[iResponseSize];
*/
			if(padFiltered.length < padSample.length)
			{
				throw new PreprocessingException
				(
					"WaveletFilter: Output buffer not long enough (" +
					padFiltered.length + " < " + padSample.length + ")."
				);
			}

			// http://eeweb.poly.edu/iselesni/WaveletSoftware/allcode/denS2D.m
			// http://eeweb.poly.edu/iselesni/WaveletSoftware/denoise.html
			//int J = 4;
			int J = 3;
			int T = 35;
			
			double[][] adDWTCoeffs = Algorithms.Wavelet.dwt(padSample, J, Algorithms.Wavelet.af);

			// Loop through scales and apply soft thresholding
			for(int j = 0; j < J; j++)
			{
		    	adDWTCoeffs[j] = Algorithms.Wavelet.soft(adDWTCoeffs[j], T); 
			}		
			
			double[] adFiltered = Algorithms.Wavelet.idwt(adDWTCoeffs, J, Algorithms.Wavelet.sf);
			// -- end
			
			System.err.println("adFiltered=" + adFiltered + ",padFiltered="+padFiltered);
			System.err.println("adFiltered.l=" + adFiltered.length + ",padFiltered.l="+padFiltered.length);
			
			//Arrays.copy(padFiltered, 0, adFiltered);
			Arrays.copy
			(
				padFiltered,
				0,
				adFiltered,
				0,
				padFiltered.length <= adFiltered.length ? padFiltered.length : adFiltered.length
			);
				
			Debug.debug(getClass(), "done");

			return true;
		}
		catch(NullPointerException e)
		{
			e.printStackTrace(System.err);
			throw new PreprocessingException("FFTFilter: frequency response hasn't been set.");
		}
		catch(MathException e)
		{
			e.printStackTrace(System.err);
			throw new PreprocessingException("FFTFilter: " + e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see marf.Preprocessing.IFilter#filter(double[][], double[][])
	 */
	public boolean filter(double[][] padSample, double[][] padFiltered)
	throws PreprocessingException
	{
		// TODO Auto-generated method stub
		throw new NotImplementedException();
		//return false;
	}

	/* (non-Javadoc)
	 * @see marf.Preprocessing.IFilter#filter(double[][][], double[][][])
	 */
	public boolean filter(double[][][] padSample, double[][][] padFiltered)
	throws PreprocessingException
	{
		// TODO Auto-generated method stub
		throw new NotImplementedException();
		//return false;
	}
}

// EOF
