package marf.Preprocessing.CFEFilters;

import marf.Preprocessing.Filter;
import marf.Preprocessing.PreprocessingException;
import marf.Storage.Sample;
import marf.math.Algorithms;
import marf.math.ComplexMatrix;
import marf.math.Matrix;
import marf.math.Vector;
import marf.util.Arrays;


/**
 * <p>General Continuous-Fraction Expansion (CFE) filter types.
 * Based on the Masters Thesis work of Shivani Bhat in
 * research of the 2D digital CFE filters.
 * </p>
 *
 * <p>This implementation in MARF is used for 2D version in
 * the <code>TestFilters</code> application and the 1D version
 * in <code>SpeakerIdentApp</code>. Look at the apps for the
 * example usage. The exact meaning of the filter parameters is
 * defined in Shivani's thesis.
 * </p>
 * 
 * $Id: CFEFilter.java,v 1.12 2011/11/21 17:45:47 mokhov Exp $
 * 
 * @author Serguei Mokhov
 * @author Shivani Haridas Bhat
 */
public abstract class CFEFilter
extends Filter
{
	protected double a00 = 1; 
	protected double a01 = 1;
	protected double a10 = 1;
	protected double a11 = 1;
	protected double b00 = 1;
	protected double b01 = 1;
	protected double b10 = 1;
	protected double b11 = 1;
	protected double a1 = 1;
	protected double a2 = 1; 
	protected double b1 = 1;
	protected double b2 = 1; 
	protected double k1 = 1;
	protected double k2 = 1;


	protected String strConfig = "";
	
	protected ComplexMatrix oZ1 = null;
	protected ComplexMatrix oZ2 = null;
	
	public static final int DEFAULT_STEP_SIZE = 50;
	public static final int DEFAULT_CHUNK_SIZE = 256;
//	public static final int DEFAULT_CHUNK_SIZE = 512;

	protected int iChunkSize = DEFAULT_CHUNK_SIZE; 

	protected ComplexMatrix H = null;
	
	/**
	 * LowPassFilter Constructor.
	 * @param poSample incoming sample
	 * @throws PreprocessingException
	 */
	public CFEFilter(Sample poSample)
	throws PreprocessingException
	{
		super(poSample);

		this.strConfig = new StringBuffer(getClass().getName()).append("-")
			.append(a00).append("-").append(a01).append("-").append(a10).append("-").append(a11).append("-")
			.append(b00).append("-").append(b01).append("-").append(b10).append("-").append(b11).append("-")
			.append(a1).append("-").append(a2).append("-")
			.append(b1).append("-").append(b2).append("-")
			.append(k1).append("-").append(k2).append("-")
			.append(iChunkSize).toString();
		
		this.oObjectToSerialize = H;
		this.strFilename = this.strConfig + "." + getDefaultExtension();
	}
	
	/**
	 * @see marf.Preprocessing.IFilter#filter(double[], double[])
	 */
	public boolean filter(final double[] padSample, double[] padFiltered)
	throws PreprocessingException
	{
		if(padSample == null)
		{
			throw new PreprocessingException("Source sample is null");
		}

		if(padFiltered == null)
		{
			throw new PreprocessingException("Destination array for filter output is null");
		}

		if(padFiltered.length != padSample.length)
		{
			throw new PreprocessingException("Lengths of the source sample and the destination vectors do not agree.");
		}

		boolean bChanges = false;

		for(int j = 0; j < padSample.length; j += this.iChunkSize)
		{
//			double[][] adSample = new double[padSample.length][padSample.length];
//			double[][] adFiltered = new double[padSample.length][padSample.length];
//			double[][] addSample = new double[this.iChunkSize][this.iChunkSize];
//			double[][] addFiltered = new double[this.iChunkSize][this.iChunkSize];
			double[][] addSample = new double[1][this.iChunkSize];
			double[][] addFiltered = new double[1][this.iChunkSize];

			// The last chunk of the array may not necessarily be aligned
			// with the chunk size, so account for it.
			int iLengthToCopy =
				padFiltered.length - (j + 1) < this.iChunkSize ?
					padFiltered.length - (j + 1) : this.iChunkSize;

//			System.out.println("Length to copy: " + iLengthToCopy);
//			System.out.println("array length: " + padSample.length + ", j: " + j);
//			System.out.println("len - (j + 1): " + (padFiltered.length - (j + 1)));

//			for(int i = 0; i < padSample.length; i++)
			// Take the next chunk
//			for(int i = 0; i < this.iChunkSize; i++)
			for(int i = 0; i < 1; i++)
			{
				Arrays.copy(addSample[i], 0, padSample, j, iLengthToCopy);
			}

			// Apply filtering
//			System.out.println("Filter start: " + new Date());
			bChanges |= filter(addSample, addFiltered);
//			System.out.println("Filter end  : " + new Date() + "\n\n");

			// Copy filtered output
			Arrays.copy(padFiltered, j, addFiltered[0], 0, iLengthToCopy);
		}

		return bChanges;
	}

	/**
	 * @see marf.Preprocessing.IFilter#filter(double[][], double[][])
	 */
	public boolean filter(final double[][] padSample, double[][] padFiltered)
	throws PreprocessingException
	{
		try
		{
			// Re-load the H matrix from disk
			restore();

			if(H == null)
			{
				
				/*
				w1=-pi:pi/50:pi;
				w2=-pi:pi/50:pi;
				z1=exp(-j*w1);
				z2=exp(-j*w2);
				[Z1,Z2]=meshgrid(z1,z2);
				*/
		/*
				double dLowerBound = -Math.PI;
				double dUpperBound = Math.PI;
				double dStep = Math.PI / DEFAULT_STEP_SIZE;
		*/
				// Assumption is always that the incoming sample is a square matrix;
				// Must also be a power of 2.
				double dLowerBound = 0;
				double dUpperBound = (2 * Math.PI) - (2 * Math.PI / padSample.length);
				double dStep = (2 * Math.PI) / padSample.length;
		
				//int iLength = (int)((dUpperBound - dLowerBound) / dStep);
				//int iLength = (int)((dUpperBound - dLowerBound) / dStep) + 1;
				int iLength = this.iChunkSize;
				
				double[] w = new double[iLength];
				double[] z = new double[iLength];
		
				for(int i = 0; i < iLength; i++)
				{
					//w[i] = -Math.PI + i * (Math.PI / dStep);
					w[i] = -Math.PI + i * (Math.PI / DEFAULT_STEP_SIZE);
					z[i] = Math.exp(Math.tan(w[i]));
					//z[i] = Math.tan(w[i]);
				}
				
				Vector oZVector = new Vector(z);
				Vector oWVector = new Vector(w);
				
				//this.oZ1 = new Matrix();
				this.oZ1 = new ComplexMatrix(z.length, z.length);
		//		this.oZ1.setCols(z.length);
		//		this.oZ1.setRows(z.length);
		
				this.oZ2 = new ComplexMatrix(z.length, z.length);
		//		this.oZ2 = new Matrix();
		//		this.oZ2.setCols(z.length);
		//		this.oZ2.setRows(z.length);
		
				for(int i = 0; i < w.length; i++)
				{
					this.oZ1.loadRow(i, oZVector);
					this.oZ2.loadColumn(i, oZVector);
				}
		
//				System.out.println("H start: " + new Date());
				H = h();
//				System.out.println("H end  : " + new Date() + "\n\n");

				// Taking inverse FFT of the digital filter
				//imgLPF_S=real(ifft2(Hlpf,L,W));
				//double[] adOutputReal = new double[H.size()];
				//double[] adOutputImag = new double[H.size()];
//				System.out.println("Taking inverse FFT of the digital filter... " + new Date());			
				ComplexMatrix oOutputMatrix = new ComplexMatrix(H.getRows(), H.getCols());
				
				//Algorithms.FFT.doFFT(H.getRealMatrixArray(), H.getImaginaryMatrixArray(), adOutputReal, adOutputImag, -1);
				Algorithms.FFT.doFFT2(H, oOutputMatrix, -1);
//				System.out.println("Inverse FFT Done:                           " + new Date());			

				H = oOutputMatrix;
				
//				System.out.println("Dumping...");			
				this.oObjectToSerialize = H;
				
				dump();
			}

			Matrix oSampleMatrix = new Matrix(padSample);

//			System.out.println("H is a matrix of size: rows=" + H.getRows() + ", cols=" + H.getCols());			
//			System.out.println("S is a matrix of size: rows=" + oSampleMatrix.getRows() + ", cols=" + oSampleMatrix.getCols());			
	
	
			// Convoluting degraded sample and the filter in spatial domain
			//img_out=conv2(imgdeg,imgLPF_S);
	
//			ComplexMatrix oFilteredSample = ComplexMatrix.multiply(oSampleMatrix, oOutputMatrix);
//			System.out.println("Convolution start: " + new Date());
			ComplexMatrix oFilteredSample = ComplexMatrix.multiply(oSampleMatrix, H);
//			System.out.println("Convolution end  : " + new Date() + "\n\n");
			
			// Copy only real resultant part
			Arrays.copy(padFiltered, 0, oFilteredSample.getMatrix2D());
			
//			adOutputReal = null;
//			adOutputImag = null;

//			oFilteredSample = null;
//			H = null;
//			oSampleMatrix = null;
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			throw new PreprocessingException(e);
		}
	}

	/**
	 * @see marf.Preprocessing.IFilter#filter(double[][][], double[][][])
	 */
	public boolean filter(final double[][][] padSample, double[][][] padFiltered)
	throws PreprocessingException
	{
		try
		{
			boolean bChanges = false;

			// Treat every row as a two-dimensional array for filtering
			for(int i = 0; i < padSample.length; i++)
			{
				bChanges |= filter(padSample[i], padFiltered[i]);
			}

			return bChanges;
		}
		catch(NullPointerException e)
		{
			throw new PreprocessingException("One of the source or destination arrays is null.");
		}
	}

	/**
	 * Transfer function; must be implemented by concrete filters.
	 * @return
	 */
	public abstract ComplexMatrix h();

	/**
	 * Overrides that of Preprocessing to invoke the CFE low-pass
	 * filter instead of the FFT one.
	 * @return boolean that sample has changed (noise removed)
	 * @throws PreprocessingException declared but never thrown
	 * @see LowPassFilter
	 * @see marf.Preprocessing.Preprocessing#removeNoise()
	 * @since 0.3.0.6
	 */
	public boolean removeNoise()
	throws PreprocessingException
	{
		LowPassFilter oNoiseRemover = new LowPassFilter(this.oSample);
		boolean bChanges = oNoiseRemover.preprocess();
		
		this.oSample.setSampleArray(oNoiseRemover.getSample().getSampleArray());
		oNoiseRemover = null;
		
		return bChanges;
	}

	public synchronized void backSynchronizeObject()
	{
		this.H = (ComplexMatrix)this.oObjectToSerialize;
	}
}

// EOF
