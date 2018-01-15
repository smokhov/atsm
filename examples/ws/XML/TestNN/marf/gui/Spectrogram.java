package marf.gui;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.Vector;

import marf.MARF;
import marf.Storage.StorageException;
import marf.Storage.StorageManager;
import marf.util.Debug;
import marf.util.NotImplementedException;


/**
 * <p>Class Spectrogram dumps a spectrogram to a PPM file.</p>
 *
 * $Id: Spectrogram.java,v 1.34 2012/05/30 16:24:18 mokhov Exp $
 *
 * @author Ian Clement
 * @author Serguei Mokhov
 * @version $Revision: 1.34 $
 * @since 0.0.1
 */
public class Spectrogram
extends StorageManager
{
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = -8219029755014757254L;

	/**
	 * The data vector.
	 */
	protected Vector<double[]> oData = null;

	/**
	 * Current minimum.
	 */
	protected double dMin = 0.0;

	/**
	 * Current maximum.
	 */
	protected double dMax = 0.0;

	/**
	 * To differentiate file names based on the feature extraction method name.
	 */
	protected String strMethod = "";

	/**
	 * Constructor.
	 */
	public Spectrogram()
	{
		this.oData = new Vector<double[]>();
	}

	/**
	 * Constructor with a feature extraction method name.
	 * @param pstrMethodName String representing FE module name
	 */
	public Spectrogram(String pstrMethodName)
	{
		this();
		this.strMethod = pstrMethodName;
	}

	/**
	 * Adds LPC spectrum to the data to dump.
	 * @param padLPCCoeffs LPC coefficients to dump
	 * @param piNumCoeffs number of LPC coefficients to dump
	 * @param piSize size of the spectrogram (width)
	 */
	public final void addLPC(final double[] padLPCCoeffs, final int piNumCoeffs, final int piSize)
	{
		double[] adToInsert = new double[piSize];

		for(int i = 0; i < piSize; i++)
		{
			double dAr = 1.0;
			double dAi = 0.0;

			for(int k = 0; k < piNumCoeffs; k++)
			{
				dAr -= padLPCCoeffs[k] * Math.cos(2 * Math.PI * i * -k / 128);
				dAi -= padLPCCoeffs[k] * Math.sin(2 * Math.PI * i * -k / 128);
			}

			double dA = Math.sqrt(dAr * dAr + dAi * dAi);
			double dH = 1.0 / dA;

			if(dH > this.dMax)
			{
				this.dMax = dH;
			}
			else
			{
				if(dH < this.dMin)
				{
					this.dMin = dH;
				}
			}

			adToInsert[i] = dH;
		}

		this.oData.add(adToInsert);
	}

	/**
	 * Adds FFT spectrum to the data to dump.
	 * @param padValues array of doubles (FFT coefficients)
	 */
	public final void addFFT(final double[] padValues)
	{
		double[] adToInsert = new double[padValues.length / 2];

		if(this.oData.size() == 0)
		{
			this.dMin = this.dMax = padValues[0];
		}

		for(int k = 0; k < adToInsert.length; k++)
		{
			if(padValues[k] > dMax)
			{
				this.dMax = padValues[k];
			}

			if(padValues[k] < dMin)
			{
				this.dMin = padValues[k];
			}

			adToInsert[k] = padValues[k];
		}

		this.oData.add(adToInsert);
	}

	/**
	 * Dumps spectrogram.
	 * @throws StorageException
	 */
	public final void dump()
	throws StorageException
	{
		try
		{
			Debug.debug("Dumping spectrogram " + MARF.getSampleFile() + "." + this.strMethod + ".ppm");
			Debug.debug("Spectrogram.dump() - data size in vectors: " + this.oData.size());

			FileOutputStream oFOS = null;
			DataOutputStream oOutFile = null;

			oFOS = new FileOutputStream(MARF.getSampleFile() + "." + this.strMethod + ".ppm");
			oOutFile = new DataOutputStream(oFOS);

		// Output PPM header
/*
		man ppm:

         - A "magic number" for identifying the file type.  A pgm file's magic number is the two characters "P6".

         - Whitespace (blanks, TABs, CRs, LFs).

         - A width, formatted as ASCII characters in decimal.

         - Whitespace.

         - A height, again in ASCII decimal.

         - Whitespace.

         - The maximum color value (Maxval), again in ASCII decimal.  Must be less than 65536.

         - Newline or other single whitespace character.

         - A raster of Width * Height pixels, proceeding through the image in normal English reading order.  Each pixel is  a  triplet  of  red,
           green,  and  blue  samples,  in that order.  Each sample is represented in pure binary by either 1 or 2 bytes.  If the Maxval is less
           than 256, it is 1 byte.  Otherwise, it is 2 bytes.  The most significant byte is first.
*/
			// Output data

			// Make max be at 75%
			this.dMax *= 0.75;

			int iWidth = this.oData.size();
			int iLength = this.oData.elementAt(0).length;

			oOutFile.writeBytes
			(
				"P6\n"
				+ iWidth + "\n"
				+ iLength + "\n"
				+ "255\n"
			);

			for(int j = iLength - 1; j >= 0; j--)
			{
				for(int i = 0; i < iWidth; i++)
				{
					double[] adData = this.oData.elementAt(i);
					
					for(int m = 0; m < 3; m++)
					{
						double dVal;

						if(adData[j] > this.dMax)
						{
							dVal = this.dMax;
						}
						else if(adData[j] < this.dMin)
						{
							dVal = this.dMin;
						}
						else
						{
							dVal = adData[j];
						}

						oOutFile.writeByte((int)(((this.dMax - dVal) / this.dMax) * 256));
					}
				}
			}

			Debug.debug
			(
				"Done dumping spectrogram " +
				MARF.getSampleFile() + "." + this.strMethod +
				".ppm [" + (this.oData.size() * iLength * 3) + " bytes]"
			);
		}
		catch(Exception e)
		{
			throw new StorageException(e);
		}
	}

	/**
	 * Not implemented.
	 * @throws NotImplementedException
	 * @throws StorageException never thrown
	 */
	public void restore()
	throws StorageException
	{
		throw new NotImplementedException("Spectrogram.restore()");
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.34 $";
	}
}

// EOF
