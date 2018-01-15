package marf.Preprocessing.CFEFilters;

import marf.Preprocessing.PreprocessingException;
import marf.Storage.Sample;
import marf.math.ComplexMatrix;


public class HighPassFilter
extends LowPassFilter
{
	/**
	 * LowPassFilter Constructor.
	 * @param poSample incoming sample
	 * @throws PreprocessingException
	 */
	public HighPassFilter(Sample poSample)
	throws PreprocessingException
	{
		super(poSample);
	}

	public ComplexMatrix h()
	{
		//a=Z1+a1; b=Z2+a2; (note:a,b,c,d are different from lowpass filter)
		//c=Z1-1; d=Z2-1;

		ComplexMatrix a = new ComplexMatrix(oZ1);
		a.add(this.a1);

		ComplexMatrix b = new ComplexMatrix(oZ2);
		b.add(this.a2);

		ComplexMatrix c = new ComplexMatrix(oZ1);
		c.minus(1);

		ComplexMatrix d = new ComplexMatrix(oZ2);
		d.minus(1);

		return computeTranferFunction(a, b, c, d);
	}
}

// EOF
