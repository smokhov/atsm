package marf.math;

import marf.util.Arrays;

/**
 * <p>Collection of algorithms to be used by the modules.
 * Decouples algorithms from the modules and allows to be
 * used by different types of modules.</p>
 *
 * @author Serguei Mokhov
 * @version $Id: Algorithms.java,v 1.19 2012/06/17 22:33:34 mokhov Exp $
 * @since 0.3.0.2
 */
public final class Algorithms
{
	/**
	 * There shall no be derivatives or instances.
	 */
	private Algorithms()
	{
	}

	/**
	 * A collection of Hamming Window-related algorithms.
	 * @author Stephen Sinclair
	 * @author Serguei Mokhov
	 */
	public static final class Hamming
	{
		/* Hamming Window */

		/**
		 * Retrieves a single value of hamming window based on
		 * length and index within the window.
		 *
		 * @param piN index into the window
		 * @param piLength the total length of the window
		 * @return the hamming value within the window length; 0 if outside of the window
		 */
		public static final double hamming(final int piN, final int piLength)
		{
			if(piN <= (piLength - 1) && piN >= 0)
			{
				return 0.54 - (0.46 * Math.cos((2 * Math.PI * piN) / (piLength - 1)));
			}

			return 0;
		}

		/**
		 * Applies hamming window to an array of doubles.
		 * @param padWindow array of doubles to apply windowing to
		 * @since 0.2.0
		 */
		public static final void hamming(double[] padWindow)
		{
			for(int i = 0; i < padWindow.length; i++)
			{
				padWindow[i] *=
					(0.54 - (0.46 * Math.cos((2 * Math.PI * i) /
					(padWindow.length - 1))));
			}
		}

		/**
		 * Calculates square root Hamming window value given index and size.
		 * @param piIndex window index
		 * @param piSize window size
		 * @return resulting squared hamming window
		 * @since 0.3.0.2
		 */
		public static final double sqrtHamming(int piIndex, int piSize)
		{
			return Math.sqrt(1 - 0.85185 * Math.cos((2 * piIndex - 1) * Math.PI / piSize));
		}
	}

	/**
	 * <p>A collection of FFT-related math.</p>
	 * @author Stephen Sinclair
	 * @author Serguei Mokhov
	 */
	public static final class FFT
	{
		/* FFT Methods */

		/**
		 * FFT algorithm, translated from "Numerical Recipes in C++" that
		 * implements the Fast Fourier Transform, which performs a discrete Fourier transform
		 * in O(n*log(n)).
		 *
		 * @param padInputReal InputReal is real part of input array
		 * @param padInputImag InputImag is imaginary part of input array
		 * @param padOutputReal OutputReal is real part of output array
		 * @param padOutputImag OutputImag is imaginary part of output array
		 * @param piDirection Direction is 1 for normal FFT, -1 for inverse FFT
		 * @throws MathException if the sizes or direction are wrong
		 */
		public static final void doFFT
		(
			final double[] padInputReal,
			final double[] padInputImag,
			double[] padOutputReal,
			double[] padOutputImag,
			int piDirection
		)
		throws MathException
		{
			// Ensure input length is a power of two
			int iLength = padInputReal.length;

			if((iLength < 1) | ((iLength & (iLength - 1)) != 0))
			{
				throw new MathException("Length of input (" + iLength + ") is not a power of 2.");
			}

			if((piDirection != 1) && (piDirection != -1))
			{
				throw new MathException("Bad direction specified. Should be 1 or -1.");
			}

			if(padOutputReal.length < padInputReal.length)
			{
				throw new MathException("Output length (" + padOutputReal.length + ") < Input length (" + padInputReal.length + ")");
			}

			// Determine max number of bits
			int iMaxBits, n = iLength;

			for(iMaxBits = 0; iMaxBits < 16; iMaxBits++)
			{
				if(n == 0) break;
				n /= 2;
			}

			iMaxBits -= 1;

			// Binary reversion & interlace result real/imaginary
			int i, t, bit;

			for(i = 0; i < iLength; i++)
			{
				t = 0;
				n = i;

				for(bit = 0; bit < iMaxBits; bit++)
				{
					t = (t * 2) | (n & 1);
					n /= 2;
				}

				padOutputReal[t] = padInputReal[i];
				padOutputImag[t] = padInputImag[i];
			}

			// put it all back together (Danielson-Lanczos butterfly)
			int mmax = 2, istep, j, m;								// counters
			double theta, wtemp, wpr, wr, wpi, wi, tempr, tempi;	// trigonometric recurrences

			n = iLength * 2;

			while(mmax < n)
			{
				istep = mmax * 2;
				theta = (piDirection * 2 * Math.PI) / mmax;
				wtemp = Math.sin(0.5 * theta);
				wpr   = -2.0 * wtemp * wtemp;
				wpi   = Math.sin(theta);
				wr    = 1.0;
				wi    = 0.0;

				for(m = 0; m < mmax; m += 2)
				{
					for(i = m; i < n; i += istep)
					{
						j = i + mmax;
						tempr = wr * padOutputReal[j / 2] - wi * padOutputImag[j / 2];
						tempi = wr * padOutputImag[j / 2] + wi * padOutputReal[j / 2];

						padOutputReal[j / 2] = padOutputReal[i / 2] - tempr;
						padOutputImag[j / 2] = padOutputImag[i / 2] - tempi;

						padOutputReal[i / 2] += tempr;
						padOutputImag[i / 2] += tempi;
					}

					wr = (wtemp = wr) * wpr - wi * wpi + wr;
					wi = wi * wpr + wtemp * wpi + wi;
				}

				mmax = istep;
			}
		}

/*		
		public static final void doFFT2
		(
			final double[][] padInputReal,
			double[][] padInputImag,
			double[][] padOutputReal,
			double[][] padOutputImag,
			int piDirection
		)
		throws MathException
*/

		/**
		 * Performs 2D FFT; which is merely 1D FFT for every column, and then,
		 * 1D FFT of every row of the result.
		 * 
		 * @param poInputMatrix
		 * @param poOutputMatrix
		 * @param piDirection
		 * @throws MathException
		 * @since 0.3.0.6
		 */
		public static final void doFFT2
		(
			final ComplexMatrix poInputMatrix,
			ComplexMatrix poOutputMatrix,
			int piDirection
		)
		throws MathException
		{
			assert
				poInputMatrix.iCols == poOutputMatrix.iCols && poInputMatrix.iRows == poOutputMatrix.iRows
				: "Matrix dimensions (" + poInputMatrix.iRows + "x" + poInputMatrix.iCols
				+ ") vs (" + poOutputMatrix.iRows + "x" + poOutputMatrix.iCols + ") do not match.";

			// Do 1D FFT for every column first
			for(int i = 0; i < poInputMatrix.iCols; i++)
			{
				ComplexVector oInputColumn = poInputMatrix.getComplexColumn(i);
				ComplexVector oOutputColumn = new ComplexVector(oInputColumn.size());
				
				doFFT(oInputColumn.adMatrix, oInputColumn.adImaginaryMatrix, oOutputColumn.adMatrix, oOutputColumn.adImaginaryMatrix, piDirection);
				
				poOutputMatrix.loadColumn(i, oOutputColumn);
			}

			// Then do 1D FFT of the result of the above
			for(int i = 0; i < poInputMatrix.iRows; i++)
			{
				ComplexVector oInputRow = poOutputMatrix.getComplexRow(i);
				ComplexVector oOutputRow = new ComplexVector(oInputRow.size());

				doFFT(oInputRow.adMatrix, oInputRow.adImaginaryMatrix, oOutputRow.adMatrix, oOutputRow.adImaginaryMatrix, piDirection);

				poOutputMatrix.loadRow(i, oOutputRow);
			}
		}

		/**
		 * Performs a normal FFT, taking a real input (e.g. an audio sample) and returns
		 * the frequency analysis in terms of "magnitude" and "phase angle".
		 *
		 * @param padSample must be an array of size (2^k)
		 * @param padMagnitude must be half the size of "sample"
		 * @param padPhaseAngle must be half the size of "sample"; may be null
		 * @throws MathException
		 */
		public static final void normalFFT(final double[] padSample, double[] padMagnitude, double[] padPhaseAngle)
		throws MathException
		{
			double[] adSampleImag = new double[padSample.length];
			double[] adOutputReal = new double[padSample.length];
			double[] adOutputImag = new double[padSample.length];

			doFFT(padSample, adSampleImag, adOutputReal, adOutputImag, 1);

			// convert complex output to magnitude and phase angle
			int iLen = padMagnitude.length;

			if(padMagnitude.length > (padSample.length / 2))
			{
				iLen = padSample.length / 2;
			}

			for(int i = 0; i < iLen; i++)
			{
				padMagnitude[i] = Math.sqrt(adOutputReal[i] * adOutputReal[i] + adOutputImag[i] * adOutputImag[i]);

				if(padPhaseAngle != null)
				{
					// Bug ID 3432300 fix (phase angle was always assumed in the atan(y / x) range).
					padPhaseAngle[i] = Math.atan2(adOutputImag[i], adOutputReal[i]);
				}
			}
		}

		/**
		 * Performs a normal FFT, taking a real input (e.g. an audio sample) and returns
		 * the frequency analysis in terms of "magnitude".
		 *
		 * @param padSample must be an array of size (2^k)
		 * @param padMagnitude must be half the size of "sample"
		 * @throws MathException
		 */
		public static final void normalFFT(final double[] padSample, double[] padMagnitude)
		throws MathException
		{
			normalFFT(padSample, padMagnitude, null);
		}
	}

	/**
	 * <p>A collection of LPC-related algorithms.</p>
	 * @author Ian Clement
	 */
	public static final class LPC
	{
		/* LPC methods */

		/**
		 * Does the LPC algorithm.
		 * <b>NOTE:</b> input is assumed to be windowed, ie: input.length = N.
		 *
		 * @param padInput windowed part of incoming sample
		 * @param padOutput resulting LPC coefficients
		 * @param padError output LPC error
		 * @param piPoles number of poles
		 * @throws MathException
		 */
		public static final void doLPC(final double[] padInput, double[] padOutput, double[] padError, int piPoles)
		throws MathException
		{
			if(piPoles <= 0)
			{
				throw new MathException("Number of poles should be > 0; supplied: " + piPoles);
			}

			if(padOutput.length != piPoles)
			{
				throw new MathException("Output array should be of length p (" + piPoles + ")!");
			}

			if(padError.length != piPoles)
			{
				throw new MathException("Error array should be of length p (" + piPoles + ")!");
			}

			double[]   k = new double[piPoles];
			double[][] A = new double[piPoles][piPoles];

			padError[0] = applyAutoCorrelation(padInput, 0);

			A[0][0] = k[0] = 0.0;

			for(int m = 1; m < piPoles; m++)
			{
				// calculate k[m]
				double dTmp = applyAutoCorrelation(padInput, m);

				for(int i = 1; i < m; i++)
				{
					dTmp -= A[m - 1][i] * applyAutoCorrelation(padInput, m - i);
				}

				k[m] = dTmp / padError[m - 1];

				// update A[m][*]
				for(int i = 0; i < m; i++)
				{
					A[m][i] = A[m - 1][i] - k[m] * A[m - 1][m - i];
				}

				A[m][m] = k[m];

				// update error[m]
				padError[m] = (1 - (k[m] * k[m])) * padError[m - 1];
			}

			// [SM]: kludge?
			for(int i = 0; i < piPoles; i++)
			{
				if(Double.isNaN(A[piPoles - 1][i]))
				{
					padOutput[i] = 0.0;
				}
				else
				{
					padOutput[i] = A[piPoles - 1][i];
				}
			}
		}

		/**
		 * Implements the least-square autocorrelation method.
		 * @param padInput windowed input signal
		 * @param piX coefficient number
		 * @return double - correlation number
		 */
		public static final double applyAutoCorrelation(final double[] padInput, int piX)
		{
			double dRet = 0.0;

			for(int i = piX; i < padInput.length; i++)
			{
				dRet += padInput[i] * padInput[i - piX];
			}

			return dRet;
		}
	}

	/**
	 * <p>A collection of Wavelet-transform-related algorithms.</p>
	 * Code translated into Java from http://eeweb.poly.edu/iselesni/WaveletSoftware.
	 * @author Serguei Mokhov
	 * @since 0.3.0.6, November 2011
	 */
	public static final class Wavelet
	{
		/**
		 * Analysis filters.
		 * 2-rows 1-column, as opposed to 2-columns 1-row in MATLAB. 
		 * http://eeweb.poly.edu/iselesni/WaveletSoftware/allcode/farras.m
		 */
		public static double[][] af = 
		{
			{
                 0,
                 0,
				-0.08838834764832,
				 0.08838834764832,
				 0.69587998903400,
				 0.69587998903400,
				 0.08838834764832,
				-0.08838834764832,
				 0.01122679215254,
				 0.01122679215254
			},
			{
				-0.01122679215254,
				 0.01122679215254,
				 0.08838834764832,
				 0.08838834764832,
				-0.69587998903400,
				 0.69587998903400,
				-0.08838834764832,
				-0.08838834764832,
				 0,
				 0
			}
		};
		
		/**
		 * Synthesis filters.
		 * http://eeweb.poly.edu/iselesni/WaveletSoftware/allcode/farras.m 
		 */
		public static double[][] sf =
		{
			{
				 0.01122679215254,
				 0.01122679215254,
				-0.08838834764832,
				 0.08838834764832,
				 0.69587998903400,
				 0.69587998903400,
				 0.08838834764832,
				-0.08838834764832,
				 0,
				 0
			},
			{
				 0,
				 0,
				-0.08838834764832,
				-0.08838834764832,
				 0.69587998903400,
				-0.69587998903400,
				 0.08838834764832,
				 0.08838834764832,
				 0.01122679215254,
				-0.01122679215254
			}
		};
		
		public static final void applyWaveletTransform
		(
			final double[] padInputReal,
			final double[] padInputImag,
			double[] padOutputReal,
			double[] padOutputImag,
			int piDirection
		)
		throws MathException
		{
			throw new MathException("Not implemented.");
		}
		
		/**
		 * Analysis filter bank.
		 * http://eeweb.poly.edu/iselesni/WaveletSoftware/allcode/afb.m
		 * @param x
		 * @param af
		 * @return
		 * @throws MathException
		 */
		public static double[][] afb(double[] x, double[][] af)
		throws MathException
		{
			int N = x.length;
			int L = af.length / 2;
			x = cshift(x, -L);

			// lowpass filter
			double[] lo = upfirdn(x, af[0], 1, 2);
			//System.err.println("lo[.0.] " + lo.length + ": " + Arrays.arrayToCSV(lo));
			// lo(1:L) = lo(N/2+[1:L]) + lo(1:L);
			for(int l = 0; l < L; l++)
			{
				lo[l] = lo[N/2 + l] + lo[l];
			}

			//System.err.println("lo[.1.] " + lo.length + ": " + Arrays.arrayToCSV(lo));
			
			//lo = lo(1:N/2);
			double[] loHalf = new double[N/2];
			Arrays.copy(loHalf, lo, N/2);
			
			//System.err.println("lo[.2.] " + loHalf.length + ": " + Arrays.arrayToCSV(loHalf));

			// highpass filter
			double[] hi = upfirdn(x, af[1], 1, 2);
			//hi(1:L) = hi(N/2+[1:L]) + hi(1:L);
			for(int h = 0; h < L; h++)
			{
				hi[h] = hi[N/2 + h] + hi[h];
			}

			//hi = hi(1:N/2);
			double[] hiHalf = new double[N/2];
			Arrays.copy(hiHalf, hi, N/2);

			return new double[][] {loHalf, hiHalf};
		}

		// http://eeweb.poly.edu/iselesni/WaveletSoftware/allcode/sfb.m
		public static final double[] sfb(double[] lo, double[] hi, double[][] sf)
		throws MathException
		{
			int N = 2 * lo.length;
			int L = sf[0].length;
			
			lo = upfirdn(lo, sf[0], 2, 1);
			hi = upfirdn(hi, sf[1], 2, 1);
			
			// Recombine the signal
			double[] y = add(lo, hi);
			//y(1:L-2) = y(1:L-2) + y(N+[1:L-2]);
			for(int l = 0; l < L-2; l++)
			{
				y[l] += y[N+l];
			}
			//y = y(1:N);
			//XXX
			
			//y = cshift(y, 1-L/2);
			y = cshift(y, 1-L/2);
			return y;
		}
		
		public static final double[][] farras()
		{
			return new double[][]
			{
				af[0],
				af[1],
				sf[0],
				sf[1]
			};
		}
		
		// XXX: use our Vector?
		public static final double[] add(double[] x, double[] y)
		{
			//System.err.println("x.l"+x.length+",y.l="+y.length);

			//double[] z = new double[x.length];
			double[] z = new double[x.length >= y.length ? x.length : y.length];
			
			for(int i = 0; i < x.length; i++)
			{
				if(i >= x.length)
				{
					z[i] = y[i];
				}
				else if(i >= y.length)
				{
					z[i] = x[i];
				}
				else
				{
					z[i] = x[i] + y[i];
				}
			}
			
			return z;
		}
		
		/**
		 * Shifts x by m.
		 * @param x
		 * @param m > 0 shifts to the right; < 0 shifts to the left
		 * @return
		 * http://eeweb.poly.edu/iselesni/WaveletSoftware/allcode/cshift.m
		 */
		public static final double[] cshift(double[] x, int m)
		{
			int N = x.length;
			double[] y = new double[N];
			
			//System.err.println("cshift: N="+N);
			//System.err.println("cshift: m="+m);
			
			for(int n = 0; n < N; n++)
			{
				// Compute mod() and take care of the negative
				int i = (n - m) % N;
				//System.err.println("cshift: i(1)="+i);
				if(i < 0)
				{
					i += N;
				}
				//System.err.println("cshift: i(2)="+i);
				//System.err.println("cshift: n( )="+n);
			
				y[n] = x[i];
			}
			
			return y;
		}
		
		// http://eeweb.poly.edu/iselesni/WaveletSoftware/allcode/soft.m
		// function y = soft(x,T)
		public static final double[] soft(double[] x, int T)
		{
			double[] y = new double[x.length];
			
			//y = max(abs(x) - T, 0);
			//y = y./(y+T) .* x;
			for(int i = 0; i < y.length; i++)
			{
				// Filter out coefficients below the threshold
				y[i] = Math.max(Math.abs(x[i]) - T, 0);
				
				// Restore the values and signs especially
				y[i] = (y[i] / (y[i] + T)) * x[i];
			}
			
			return y;
		}
		
		// http://eeweb.poly.edu/iselesni/WaveletSoftware/allcode/dwt.m
		public static final double[][] dwt(double[] x, int J, double[][] af)
		throws MathException
		{
			double w[][] = new double[J+1][];
			
			for(int k = 0; k < J; k++)
			{
				double[][] a = afb(x, af);
				x = a[0];
				w[k] = a[1];
			}
			
			w[J] = x;
			
			return w;
		}
		
		// http://eeweb.poly.edu/iselesni/WaveletSoftware/allcode/idwt.m
		public static final double[] idwt(double[][] w, int J, double[][] sf)
		throws MathException
		{
			double y[] = w[J];
			
			for(int k = J-1; k >= 0; k--)
			{
				y = sfb(y, w[k], sf);
			}
			
			return y;
		}
		
		// http://code.google.com/p/upfirdn/source/browse/upfirdn/Resampler.h
		public static final class Resampler
		{
			private int _upRate;
			private int _downRate;
			
			private double[] _transposedCoefs;
			private double[] _state;
			//private    double[]  _stateEnd;
			private int _stateEnd;
			    
			private int _paddedCoefCount;  // ceil(len(coefs)/upRate)*upRate
			private int _coefsPerPhase;    // _paddedCoefCount / upRate
			    
			private int _t;                // "time" (modulo upRate)
			private int _xOffset;

			//Resampler(int upRate, int downRate, double[] coefs, int coefCount)
			public Resampler(int upRate, int downRate, double[] coefs)
			{
				this._upRate = upRate;
				this._downRate = downRate;
				_t = 0;
				_xOffset = 0;
					
				_paddedCoefCount = coefs.length;
				
				while(_paddedCoefCount % _upRate != 0)
				{
					_paddedCoefCount++;
				}

				_coefsPerPhase = _paddedCoefCount / _upRate;
			    _transposedCoefs = new double[_paddedCoefCount];
			    
				//fill(_transposedCoefs, _transposedCoefs.length + _paddedCoefCount, 0.);
				Arrays.fill(_transposedCoefs, 0);

				_state = new double[_coefsPerPhase - 1];
				//_stateEnd = _state + _coefsPerPhase - 1;
				_stateEnd = _coefsPerPhase - 1;
				//fill(_state, _stateEnd, 0.);
				Arrays.fill(_state, 0);

				/* This both transposes, and "flips" each phase, while
				 * copying the defined coefficients into local storage.
				 * There is probably a faster way to do this
				 */
				for(int i = 0; i < _upRate; ++i)
				{
					for(int j = 0; j < _coefsPerPhase; ++j)
					{
						if(j * _upRate + i < coefs.length)
						{
							_transposedCoefs[(_coefsPerPhase - 1 - j) + i * _coefsPerPhase] = coefs[j * _upRate + i];
						}
					}
				}
			} // Resampler

			//int apply(double[] in, int inCount, double[] out, int outCount)
			int apply(double[] in, double[] out)
			throws MathException
			{
				if(out.length < neededOutCount(in.length))
				{
					throw new MathException("Not enough output samples");
				}

				// x points to the latest processed input sample
				//inputType *x = in + _xOffset;
				int x = _xOffset;
				//outputType *y = out;
				int y = 0;
				   
				// inputType *end = in + inCount;
				int end = in.length;

				while(x < end)
				{
					//outputType acc = 0.;
					double acc = 0.0;
					
					//coefType *h = _transposedCoefs + _t*_coefsPerPhase;
					int h = _t * _coefsPerPhase;
					
					//inputType *xPtr = x - _coefsPerPhase + 1;
					int xPtr = x - _coefsPerPhase + 1;
					
					//int offset = in - xPtr;
					int offset = -xPtr;
					
					if(offset > 0)
					{
						// need to draw from the _state buffer
						//inputType *statePtr = _stateEnd - offset;
						int statePtr = _stateEnd - offset;
						
						while(statePtr < _stateEnd)
						{
							//acc += *statePtr++ * *h++;
							//System.err.println("statePtr="+statePtr+",h="+h+",_state.l="+_state.length+",in.l="+in.length);
							// XXX: Kludge and hack
							/*
							if(h >= in.length)
							{
								System.err.println("XXX: Kludge and hack activated.");
								break;
							}
							*/
							
							//acc += _state[statePtr++] * in[h++];
							acc += _state[statePtr++] * _transposedCoefs[h++];
						}
						
						xPtr += offset;
					}
					
					while(xPtr <= x)
					{
						//acc += *xPtr++ * *h++;
						// XXX: Kludge and hack
						//if(h >= in.length||xPtr>= in.length) break;
						//acc += in[xPtr++] * in[h++];
						acc += in[xPtr++] * _transposedCoefs[h++];
					}
					
					//*y++ = acc;
					out[y++] = acc;
					_t += _downRate;
					
					int advanceAmount = _t / _upRate;
					
					x += advanceAmount;
					
					// which phase of the filter to use
					_t %= _upRate;
				}
				_xOffset = x - end;

				// manage _state buffer
				// find number of samples retained in buffer:
				int retain = (_coefsPerPhase - 1) - in.length;

				if(retain > 0)
				{
					// for inCount smaller than state buffer, copy end of buffer
					// to beginning:
					//copy(_stateEnd - retain, _stateEnd, _state);
					//Arrays.copy(_state, _stateEnd - retain, _state, _stateEnd, retain);
					Arrays.copy(_state, 0, _state, _stateEnd - retain, retain);
				
					// Then, copy the entire (short) input to end of buffer
					//copy(in, end, _stateEnd - inCount);
					//Arrays.copy(_state, end, in, _stateEnd - in.length);
					Arrays.copy(_state, _stateEnd - in.length, in, in.length);
				}
				else
				{
					// just copy last input samples into state buffer
					//copy(end - (_coefsPerPhase - 1), end, _state);
					//System.err.println("_state.l="+_state.length+",_coefsPerPhase="+_coefsPerPhase+",end="+end+",in.l="+in.length);
					//Arrays.copy(_state, end - (_coefsPerPhase - 1), in, end);
					Arrays.copy(_state, 0, in, end - (_coefsPerPhase - 1), (_coefsPerPhase - 1));
					//Arrays.copy(_state, 0, in, end - (_coefsPerPhase));
				}
				
				// number of samples computed
				//return y - out;
				//return y - out.length;
				return y;
			}
			
			int neededOutCount(int inCount)
			{
				int np = inCount * _upRate;
				int need = np / _downRate;
				
				if((_t + _upRate * _xOffset) < (np % _downRate))
				{
					need++;
				}
				
				return need;
			}

			int coefsPerPhase()
			{
				return _coefsPerPhase;
			}
		}
		
		public static double[] upfirdn
		(
			int upRate,
			int downRate, 
			double[] input,
//			int inLength,
			double[] filter
//			int filterLength
	    )
		throws MathException
		{
			// Create the Resampler
			//Resampler<S1, S2, C> theResampler(upRate, downRate, filter, filterLength);
			//Resampler theResampler = new Resampler(upRate, downRate, filter, filterLength);
			Resampler theResampler = new Resampler(upRate, downRate, filter);
			
			// pad input by length of one polyphase of filter to flush all values out
			int padding = theResampler.coefsPerPhase() - 1;
			double[] inputPadded = new double[input.length + padding];
			
			//for(int i = 0; i < input.length + padding; i++)
			for(int i = 0; i < inputPadded.length; i++)
			{
				if(i < input.length)
				{
					inputPadded[i] = input[i];
				}
				else
				{
					inputPadded[i] = 0;
				}
			}

			// calc size of output
			//int resultsCount = theResampler.neededOutCount(input.length + padding); 
			int resultsCount = theResampler.neededOutCount(inputPadded.length); 

			// results.resize(resultsCount);
			double[] results = new double[resultsCount];
	
			// run filtering
			int numSamplesComputed = theResampler.apply
			(
				inputPadded, 
				//input.length + padding,
				results
				//resultsCount
			);
			
			if(numSamplesComputed != resultsCount)
			{
				System.err.println("upfirdn: numSamplesComputed != resultsCount: " + numSamplesComputed + " != " + resultsCount);
			}
			
		    //delete[] inputPadded;
		    inputPadded = null;
		    
		    return results;
		}

//		public static double[] upfirdn(int upRate, int downRate, double[] input, double[] filter)
//		throws MathException
//		{
//		    return upfirdn(upRate, downRate, input, filter);
//		}
	
		public static double[] upfirdn(double[] input, double[] filter, int upRate, int downRate)
		throws MathException
		{
		   return upfirdn(upRate, downRate, input, filter);
		}

	} // Wavelet

	/**
	 * Returns source code revision information.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.19 $";
	}
}

// EOF
