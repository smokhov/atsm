package marf.Preprocessing.CFEFilters;

import marf.Preprocessing.PreprocessingException;
import marf.Storage.Sample;
import marf.math.ComplexMatrix;



public class LowPassFilter
extends CFEFilter
{
	/**
	 * LowPassFilter Constructor.
	 * @param poSample incoming sample
	 * @throws PreprocessingException
	 */
	public LowPassFilter(Sample poSample)
	throws PreprocessingException
	{
		super(poSample);
	}

	public ComplexMatrix h()
	{
		ComplexMatrix a = new ComplexMatrix(oZ1);
		a.minus(this.a1);

		ComplexMatrix b = new ComplexMatrix(oZ2);
		b.minus(this.a2);

		ComplexMatrix c = new ComplexMatrix(oZ1);
		c.add(1);

		ComplexMatrix d = new ComplexMatrix(oZ2);
		d.add(1);

		return computeTranferFunction(a, b, c, d);
	}

	/**
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	protected ComplexMatrix computeTranferFunction(ComplexMatrix a, ComplexMatrix b, ComplexMatrix c, ComplexMatrix d)
	{
		//H1=( (k1^2).*(a.^2) ) .* (((a11*b11*k2^2) .* (b.^2)) + (((a10*b11)*k2).*b.*d) + ((a10*b10).*d.^2)));

		ComplexMatrix aSquare = a.multiply(a);
		ComplexMatrix bSquare = b.multiply(b);
		ComplexMatrix cSquare = c.multiply(c);
		ComplexMatrix dSquare = d.multiply(d);
		
		ComplexMatrix bd = b.multiply(d);
		ComplexMatrix bcd = b.multiply(c).multiply(d);

		ComplexMatrix H1 = ComplexMatrix.multiply
		(
			ComplexMatrix.multiply(aSquare, k1 * k1),

			ComplexMatrix.add
			(
				ComplexMatrix.multiply(bSquare, a11 * b11 * k2 * k2),
				ComplexMatrix.add
				(
					ComplexMatrix.multiply(bd, a10 * b11 * k2),
					ComplexMatrix.multiply(dSquare, a10 * b10)
				)
			)
		);
		
		//H2=(k1.*a) .* ((((a01*b11)*k2^2).*(b.^2).*c)+(((a00*b11+a10*b01+a01*b10+a11*b00)*k2).*b.*d.*c)+((a10*b00).*c.*d.^2));

		ComplexMatrix H2 = ComplexMatrix.multiply
		(
			ComplexMatrix.multiply(k1, a),
			
			ComplexMatrix.add
			(
				ComplexMatrix.multiply(ComplexMatrix.multiply(a01*b11*k2*k2, bSquare), c),
				
				ComplexMatrix.add
				(
					ComplexMatrix.multiply
					(
						ComplexMatrix.multiply
						(
							ComplexMatrix.multiply((a00*b11+a10*b01+a01*b10+a11*b00)*k2, b),
							d
						),
						c
					),
					
					ComplexMatrix.multiply
					(
						ComplexMatrix.multiply(a10*b00, c), dSquare
					)
				)
			)
		);
		
		//H3=((a01*b01*k2^2).*(b.^2).*(c.^2))+(((a01*b00)*k2).*b.*d.*(c.^2))+((a00*b00).*(c.^2).*(d.^2));
		
		ComplexMatrix H3 = ComplexMatrix.add
		(
			ComplexMatrix.multiply(ComplexMatrix.multiply(a01 * b01 * k2 * k2, bSquare), cSquare),
			ComplexMatrix.add
			(
				ComplexMatrix.multiply(ComplexMatrix.multiply(a01 * b00 * k2, bd), cSquare),
				ComplexMatrix.multiply(ComplexMatrix.multiply(a00 * b00, cSquare), dSquare)
			)
		);

		//H=(a00.*b00.*(c.^2).*(d.^2))./(H1+H2+H3);

		ComplexMatrix H = ComplexMatrix.divide
		(
			ComplexMatrix.multiply(ComplexMatrix.multiply(a00 * b00, cSquare), dSquare),
			ComplexMatrix.add(H1, ComplexMatrix.add(H2, H3))
		);
		
		return H;
	}
}

// EOF
