package marf.Storage.Loaders;

import java.io.File;

import marf.Storage.Sample;
import marf.Storage.StorageException;
import marf.util.NotImplementedException;


/**
 * TODO: Not Implemented.
 * @author Serguei Mokhov
 * @version $Revision: 1.5 $
 * @since 0.3.0.2
 */
public class AIFFCLoader
extends AudioSampleLoader
{
	/**
	 * AIFFC Loader Constructor.
	 */
	public AIFFCLoader()
	{
	}

	/**
	 * Not Implemented.
	 * @param padSample unused
	 * @return nothing
	 * @throws NotImplementedException
	 * @throws StorageException never thrown
	 */
	public final int readSampleData(double[] padSample)
	throws StorageException
	{
		throw new NotImplementedException("readAudioData()");
	}

	/**
	 * Not Implemented.
	 * @param padSample unused
	 * @param piNbrData unused
	 * @return nothing
	 * @throws NotImplementedException
	 * @throws StorageException never thrown
	 */
	public final int writeSampleData(final double[] padSample, final int piNbrData)
	throws StorageException
	{
		throw new NotImplementedException("writeAudioData()");
	}

	/**
	 * Not Implemented.
	 * @param poFile unused
	 * @return nothing
	 * @throws NotImplementedException
	 * @throws StorageException never thrown
	 */
	public Sample loadSample(File poFile)
	throws StorageException
	{
		throw new NotImplementedException("loadSample()");
	}

	/**
	 * Not Implemented.
	 * @param poFile unused
	 * @throws NotImplementedException
	 * @throws StorageException never thrown
	 */
	public void saveSample(File poFile)
	throws StorageException
	{
		throw new NotImplementedException("saveSample()");
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.5 $";
	}
}

// EOF
