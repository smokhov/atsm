package marf.Preprocessing.WaveletFilters;

import marf.Preprocessing.IPreprocessing;
import marf.Preprocessing.PreprocessingException;
import marf.Storage.Sample;

/**
 * Stub.
 * @author serguei
 */
public class DyadicWaveletFilter extends WaveletFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3729208239407325876L;

	/**
	 * 
	 */
	public DyadicWaveletFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param poPreprocessing
	 * @throws PreprocessingException
	 */
	public DyadicWaveletFilter(IPreprocessing poPreprocessing)
			throws PreprocessingException {
		super(poPreprocessing);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param poSample
	 * @throws PreprocessingException
	 */
	public DyadicWaveletFilter(Sample poSample) throws PreprocessingException {
		super(poSample);
		// TODO Auto-generated constructor stub
	}

}
