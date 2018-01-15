package marf.Preprocessing.CFEFilters;

import marf.Preprocessing.PreprocessingException;
import marf.Storage.Sample;
import marf.math.ComplexMatrix;


public class BandStopFilter
extends CFEFilter
{
	/**
	 * LowPassFilter Constructor.
	 * @param poSample incoming sample
	 * @throws PreprocessingException
	 */
	public BandStopFilter(Sample poSample)
	throws PreprocessingException
	{
		super(poSample);
	}

	public ComplexMatrix h()
	{
		//a=Z1-a1; b=Z2-a2;
		//c=Z1+b1; d=Z2+b2;
		ComplexMatrix a = new ComplexMatrix(oZ1);
		a.minus(this.a1);

		ComplexMatrix b = new ComplexMatrix(oZ2);
		b.minus(this.a2);

		ComplexMatrix c = new ComplexMatrix(oZ1);
		c.add(b1);

		ComplexMatrix d = new ComplexMatrix(oZ2);
		d.add(b2);

		ComplexMatrix aSquare = a.multiply(a);
		ComplexMatrix bSquare = b.multiply(b);
		ComplexMatrix cSquare = c.multiply(c);
		ComplexMatrix dSquare = d.multiply(d);
		
		ComplexMatrix ac = ComplexMatrix.divide(aSquare, cSquare);
		ComplexMatrix bd = ComplexMatrix.divide(bSquare, dSquare);

		//H1=( a00 .* b00 .* (((k1^2 .* (a.^2 ./ c.^2)) + 1) .^2 ) .* (((k2^2.*(b.^2./d.^2))+1).^2) );
		// a00 .* b00
		// (((k1^2 .* (a.^2 ./ c.^2)) + 1) .^2)
		// (((k2^2 .* (b.^2 ./ d.^2)) + 1) .^2)
		
		ComplexMatrix H1 = ComplexMatrix.multiply
		(
			a00 * b00,
			
			ComplexMatrix.multiply
			(
				ComplexMatrix.add(ComplexMatrix.multiply(ac, k1 * k1), 1).powComplex(2),
				ComplexMatrix.add(ComplexMatrix.multiply(bd, k2 * k2), 1).powComplex(2)
			)
		);
		
		//H2=((k1^2.*(a.^2./c.^2)) .* ( (a11*b11.*(k2^2.*(b.^2./d.^2))) + (a10*b11.*(k2.*(b./d)).*((k2^2.*(b.^2./d.^2))+1)) + (a10*b10.*((k2^2.*(b.^2./d.^2))+1).^2)));

		ComplexMatrix H2 = ComplexMatrix.multiply
		(
			ComplexMatrix.multiply(k1 * k1, ac),
			
			ComplexMatrix.add
			(
				ComplexMatrix.add
				(
					//(a11*b11.*k2^2.*(b.^2./d.^2))
					ComplexMatrix.multiply(a11 * b11 * k2 * k2, bd),
					
					//a10*b11.*k2.*(b./d) .* ((k2^2.*(b.^2./d.^2))+1)
					ComplexMatrix.multiply
					(
						ComplexMatrix.multiply(a10 * b11 * k2, bd),
						ComplexMatrix.add(ComplexMatrix.multiply(k2 * k2, bd), 1)
					)
				),
				
				//a10*b10.*((k2^2.*(b.^2./d.^2))+1).^2)
				ComplexMatrix.multiply
				(
					a10 * b10,
					ComplexMatrix.add(ComplexMatrix.multiply(k2 * k2, bd), 1).powComplex(2)
				)
			)
		);
		
		//H3=( (k1.*(a./c).*((k1^2.*(a.^2./c.^2))+1)) .* ( (a01*b11.*(k2^2.*(b.^2./d.^2))) + ( (a11*b00+a00*b11+a10*b01+a01*b10) .* (k2.*(b./d)) .* ((k2^2.*(b.^2./d.^2))+1) ) + (a10*b00.*((k2^2.*(b.^2./d.^2))+1).^2) ));
		
		ComplexMatrix H3 = ComplexMatrix.multiply
		(
			//(k1.*(a./c) .* ((k1^2.*(a.^2./c.^2))+1))
			ComplexMatrix.multiply
			(
				ComplexMatrix.multiply(k1, ac),
				ComplexMatrix.add(ComplexMatrix.multiply(k1 * k1, ac), 1)
			),
			
			ComplexMatrix.add
			(
				ComplexMatrix.add
				(
					//a01*b11.*k2^2.*(b.^2./d.^2)
					ComplexMatrix.multiply(a01 * b11 * k2 * k2, bd),
			
					//(a11*b00+a00*b11+a10*b01+a01*b10) .* k2.*(b./d) .* ((k2^2.*(b.^2./d.^2))+1)
					ComplexMatrix.multiply
					(
						ComplexMatrix.multiply((a11*b00+a00*b11+a10*b01+a01*b10) * k2, bd),
						ComplexMatrix.add(ComplexMatrix.multiply(k2 * k2, bd), 1)
					)
				),
				
				//(a10*b00.*((k2^2.*(b.^2./d.^2))+1).^2)
				ComplexMatrix.multiply
				(
					a10 * b00,
					ComplexMatrix.add(ComplexMatrix.multiply(k2 * k2, bd), 1).powComplex(2)
				)
			)
		);
				
		//H4=( (((k1^2.*(a.^2./c.^2))+1).^2) .* ( (a01*b01.*(k2^2.*(b.^2./d.^2))) + (a01*b00.*(k2.*(b./d)).*((k2^2.*(b.^2./d.^2))+1)) + (a00*b00.*((k2^2.*(b.^2./d.^2))+1).^2) ) );

		ComplexMatrix H4 = ComplexMatrix.multiply
		(
			//((k1^2.*(a.^2./c.^2))+1).^2
			ComplexMatrix.add(ComplexMatrix.multiply(k1 * k1, ac), 1).powComplex(2),
			
			ComplexMatrix.add
			(
				ComplexMatrix.add
				(
					//a01*b01.*k2^2.*(b.^2./d.^2)
					ComplexMatrix.multiply(a01 * b01 * k2 * k2, bd),

					//(a01*b00.*k2.*(b./d) .* ((k2^2.*(b.^2./d.^2))+1))
					ComplexMatrix.multiply
					(
						ComplexMatrix.multiply(a01 * b00 * k2, bd),
						ComplexMatrix.add(ComplexMatrix.multiply(k2 * k2, bd), 1)
					)
				),

				//(a00*b00.*((k2^2.*(b.^2./d.^2))+1).^2)
				ComplexMatrix.multiply(a00 * b00, ComplexMatrix.add(ComplexMatrix.multiply(k2 * k2, bd), 1).powComplex(2))
			)
		);
		
		//Hbsf=H1./(H2+H3+H4);
		ComplexMatrix H = ComplexMatrix.divide
		(
			H1,
			ComplexMatrix.add(H2, ComplexMatrix.add(H3, H4))
		);
		
		return H;
	}
}

// EOF
