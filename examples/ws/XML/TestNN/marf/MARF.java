/**
 * The MARF System.
 */
package marf;

import java.util.HashMap;
import java.util.Map;

import marf.Classification.ClassificationException;
import marf.Classification.ClassificationFactory;
import marf.Classification.IClassification;
import marf.FeatureExtraction.FeatureExtractionFactory;
import marf.FeatureExtraction.IFeatureExtraction;
import marf.Preprocessing.IPreprocessing;
import marf.Preprocessing.PreprocessingFactory;
import marf.Storage.ISampleLoader;
import marf.Storage.MARFAudioFileFormat;
import marf.Storage.ModuleParams;
import marf.Storage.Result;
import marf.Storage.ResultSet;
import marf.Storage.Sample;
import marf.Storage.SampleLoaderFactory;
import marf.Storage.TrainingSet;
import marf.gui.WaveGrapher;
import marf.nlp.NLPException;
import marf.util.Debug;
import marf.util.MARFException;
import marf.util.NotImplementedException;


/**
 * Provides basic recognition pipeline and its configuration.
 *
 * @author Serguei Mokhov
 * @author Stephen Sinclair
 * @author The MARF Research and Development Group
 *
 * @version $Id: MARF.java,v 1.120 2012/07/18 16:00:20 mokhov Exp $
 * @since 0.0.1
 */
public class MARF
{
	/*
	 * --------------------------------------------------------
	 * General
	 * --------------------------------------------------------
	 */

	/**
	 * Value indicating that some configuration parameter is not set.
	 */
	public static final int UNSET = -1;

	/*
	 * NOTE: when maintaining enumeration, make sure none of them
	 *       overlap, to avoid any possible confusions.
	 */

	/*
	 * --------------------------------------------------------
	 * Preprocessing Modules Enumeration
	 * --------------------------------------------------------
	 */

	/**
	 * Indicates to use Dummy preprocessing module (just normalization).
	 */
	public static final int DUMMY                           = 100;

	/**
	 * Indicates to use filter boosting high frequencies.
	 */
	public static final int HIGH_FREQUENCY_BOOST_FFT_FILTER = 101;

	/**
	 * Indicates to use band-pass filter.
	 */
	public static final int BANDPASS_FFT_FILTER             = 102;

	/**
	 * Indicates to use endpointing.
	 */
	public static final int ENDPOINT                        = 103;

	/**
	 * Indicates to use low-pass FFT filter.
	 */
	public static final int LOW_PASS_FFT_FILTER             = 104;

	/**
	 * Indicates to use high-pass FFT filter.
	 */
	public static final int HIGH_PASS_FFT_FILTER            = 105;

	/**
	 * Indicates to use high-pass high-frequency boost FFT filter.
	 * @since 0.3.0.1
	 */
	public static final int HIGH_PASS_BOOST_FILTER          = 106;

	/**
	 * Indicates to use raw preprocessing, which means no preprocessing.
	 * @since 0.3.0.2
	 */
	public static final int RAW                             = 107;

	/**
	 * Indicates to employ user-defined preprocessing plug-in.
	 * @since 0.3.0.3
	 */
	public static final int PREPROCESSING_PLUGIN            = 108;

	/**
	 * Indicates to use low-pass CFE filter.
	 * @since 0.3.0.6
	 */
	public static final int LOW_PASS_CFE_FILTER             = 109;

	/**
	 * Indicates to use high-pass CFE filter.
	 * @since 0.3.0.6
	 */
	public static final int HIGH_PASS_CFE_FILTER            = 110;

	/**
	 * Indicates to use band-pass CFE filter.
	 * @since 0.3.0.6
	 */
	public static final int BAND_PASS_CFE_FILTER            = 111;

	/**
	 * Indicates to use band-stop CFE filter.
	 * @since 0.3.0.6
	 */
	public static final int BAND_STOP_CFE_FILTER            = 112;

	/**
	 * Indicates to use band-stop FFT filter.
	 * @since 0.3.0.6
	 */
	public static final int BAND_STOP_FFT_FILTER            = 113;

	/**
	 * Indicates to use separable discreet wavelet transform filter.
	 * @since 0.3.0.6, November 2011
	 */
	public static final int SEPARABLE_DWT_FILTER            = 114;

	/**
	 * Indicates to use dual-tree discreet wavelet transform filter.
	 * @since 0.3.0.6, November 2011
	 */
	public static final int DUAL_DTREE_DWT_FILTER           = 115;
	
	/**
	 * Indicates to use dyadic discreet wavelet transform filter.
	 * @since 0.3.0.6, November 2011
	 */
	public static final int DYADIC_DWT_FILTER               = 116;
	
	/**
	 * Upper boundary for preprocessing methods enumeration.
	 * Used in error checks.
	 *
	 * *Update it when add more algorithm constants.*
	 *
	 * @since 0.3.0.1
	 */
	public static final int MAX_PREPROCESSING_METHOD = DYADIC_DWT_FILTER;

	/**
	 * Lower boundary for preprocessing methods enumeration.
	 * Used in error checks.
	 * @since 0.3.0.1
	 */
	public static final int MIN_PREPROCESSING_METHOD = DUMMY;

	/*
	 * --------------------------------------------------------
	 * Feature Extraction Modules Enumeration
	 * --------------------------------------------------------
	 */

	/**
	 * Indicates to use LPC.
	 */
	public static final int LPC                           = 300;

	/**
	 * Indicates to use FFT.
	 */
	public static final int FFT                           = 301;

	/**
	 * Indicates to use F0.
	 */
	public static final int F0                            = 302;

	/**
	 * Indicates to use segmentation.
	 */
	public static final int SEGMENTATION                  = 303;

	/**
	 * Indicates to use cepstral analysis.
	 */
	public static final int CEPSTRAL                      = 304;

	/**
	 * Indicates to use random feature extraction.
	 * @since 0.2.0
	 */
	public static final int RANDOM_FEATURE_EXTRACTION     = 305;

	/**
	 * Indicates to use min/max amplitude extraction.
	 * @since 0.3.0
	 */
	public static final int MIN_MAX_AMPLITUDES            = 306;

	/**
	 * Indicates to employ user-defined feature extraction plug-in.
	 * @since 0.3.0.3
	 */
	public static final int FEATURE_EXTRACTION_PLUGIN     = 307;

	/**
	 * Indicates to use an aggregation of several feature extraction
	 * modules. The modules to aggregate must be specified in the
	 * MARF's <code>ModuleParams</code>.
	 *
	 * @since 0.3.0.5
	 * @see marf.FeatureExtraction.FeatureExtractionAggregator
	 * @see #setModuleParams(ModuleParams)
	 */
	public static final int FEATURE_EXTRACTION_AGGREGATOR = 308;

	/**
	 * Upper boundary for feature extraction methods enumeration.
	 * Used in error checks. *Update it when add more methods.*
	 * @since 0.3.0.1
	 */
	public static final int MAX_FEATUREEXTRACTION_METHOD = FEATURE_EXTRACTION_AGGREGATOR;

	/**
	 * Lower boundary for feature extraction methods enumeration.
	 * Used in error checks.
	 * @since 0.3.0.1
	 */
	public static final int MIN_FEATUREEXTRACTION_METHOD = LPC;


	/*
	 * --------------------------------------------------------
	 * Classification Modules Enumeration
	 * --------------------------------------------------------
	 */

	/**
	 * Indicates to use Neural Network for classification.
	 */
	public static final int NEURAL_NETWORK            = 500;

	/**
	 * Indicates to use stochastic models for classification.
	 */
	public static final int STOCHASTIC                = 501;

	/**
	 *  Indicates to use Hidden Markov Models for classification.
	 */
	public static final int MARKOV                    = 502;

	/**
	 * Indicates to use Euclidean distance for classification.
	 */
	public static final int EUCLIDEAN_DISTANCE        = 503;

	/**
	 * Indicates to use Chebyshev distance for classification.
	 */
	public static final int CHEBYSHEV_DISTANCE        = 504;

	/**
	 * A synonym to Chebyshev distance.
	 * @since 0.2.0
	 */
	public static final int MANHATTAN_DISTANCE        = 504;

	/**
	 * A synonym to Chebyshev distance.
	 * @since 0.3.0.1
	 */
	public static final int CITYBLOCK_DISTANCE        = 504;

	/**
	 * Indicates to use Minkowski distance for classification.
	 * @since 0.2.0
	 */
	public static final int MINKOWSKI_DISTANCE        = 505;

	/**
	 * Indicates to use Mahalanobis distance for classification.
	 * @since 0.2.0
	 */
	public static final int MAHALANOBIS_DISTANCE      = 506;

	/**
	 * Indicates to use random classification.
	 * @since 0.2.0
	 */
	public static final int RANDOM_CLASSIFICATION     = 507;

	/**
	 * Indicates to use diff-distance classification.
	 * @since 0.3.0.2
	 */
	public static final int DIFF_DISTANCE             = 508;

	/**
	 * Indicates to employ user-defined classification plug-in.
	 * @since 0.3.0.3
	 */
	public static final int CLASSIFICATION_PLUGIN     = 509;

	/**
	 * Indicates to employ Zipf's Law-based classifier.
	 * @since 0.3.0.6
	 */
	public static final int ZIPFS_LAW                 = 510;

	/**
	 * Indicates to use the Hamming distance classifier.
	 * @since 0.3.0.6
	 */
	public static final int HAMMING_DISTANCE          = 511;

	/**
	 * Indicates to use the cosine similarity measure classifier.
	 * @since 0.3.0.6
	 */
	public static final int COSINE_SIMILARITY_MEASURE = 512;

	/**
	 * Upper boundary for classification methods enumeration.
	 * Used in error checks. *Update it when add more methods.*
	 * @since 0.3.0.1
	 */
	public static final int MAX_CLASSIFICATION_METHOD = COSINE_SIMILARITY_MEASURE;

	/**
	 * Lower boundary for classification methods enumeration.
	 * Used in error checks.
	 * @since 0.3.0.1
	 */
	public static final int MIN_CLASSIFICATION_METHOD = NEURAL_NETWORK;


	/*
	 * --------------------------------------------------------
	 * Supported Sample File Formats Enumeration
	 * --------------------------------------------------------
	 */

	/**
	 * Indicates WAVE incoming sample file format.
	 */
	public static final int WAV    = MARFAudioFileFormat.WAV;

	/**
	 * Indicates ULAW incoming sample file format.
	 */
	public static final int ULAW   = MARFAudioFileFormat.ULAW;

	/**
	 * Indicates MP3 incoming sample file format.
	 */
	public static final int MP3    = MARFAudioFileFormat.MP3;

	/**
	 * Sine sample format.
	 * @since 0.3.0.2
	 */
	public static final int SINE   = MARFAudioFileFormat.SINE;

	/**
	 * AIFF sample format.
	 * @since 0.3.0.2
	 */
	public static final int AIFF   = MARFAudioFileFormat.AIFF;

	/**
	 * AIFF-C sample format.
	 * @since 0.3.0.2
	 */
	public static final int AIFFC  = MARFAudioFileFormat.AIFFC;

	/**
	 * AU sample format.
	 * @since 0.3.0.2
	 */
	public static final int AU     = MARFAudioFileFormat.AU;

	/**
	 * SND sample format.
	 * @since 0.3.0.2
	 */
	public static final int SND    = MARFAudioFileFormat.SND;

	/**
	 * MIDI sample format.
	 * @since 0.3.0.2
	 */
	public static final int MIDI   = MARFAudioFileFormat.MIDI;

	/**
	 * Custom (plug-in) sample format.
	 * @since 0.3.0.5
	 */
	public static final int CUSTOM = MARFAudioFileFormat.CUSTOM;

	/**
	 * Textual sample format.
	 * @since 0.3.0.6
	 */
	public static final int TEXT   = MARFAudioFileFormat.TEXT;


	/*
	 * --------------------------------------------------------
	 * Mapping of Constants to Human-Readable Descriptions
	 * --------------------------------------------------------
	 */

	/**
	 * Provides human-readable description by mapping module constants
	 * to their names.
	 * @since 0.3.0.6
	 */
	public static final Map<Integer, String> MODULE_NAMES_MAPPING = new HashMap<Integer, String>();

	static
	{
		// Misc
		MODULE_NAMES_MAPPING.put(new Integer(UNSET), "UNSET (" + UNSET + ")");

		// Preprocessing modules
		MODULE_NAMES_MAPPING.put(new Integer(DUMMY), "NORMALIZATION (" + DUMMY + ")");
		MODULE_NAMES_MAPPING.put(new Integer(HIGH_FREQUENCY_BOOST_FFT_FILTER), "HIGH_FREQUENCY_BOOST_FFT_FILTER (" + HIGH_FREQUENCY_BOOST_FFT_FILTER + ")");
		MODULE_NAMES_MAPPING.put(new Integer(BANDPASS_FFT_FILTER), "BANDPASS_FFT_FILTER (" + BANDPASS_FFT_FILTER + ")");
		MODULE_NAMES_MAPPING.put(new Integer(ENDPOINT), "ENDPOINT (" + ENDPOINT + ")");
		MODULE_NAMES_MAPPING.put(new Integer(LOW_PASS_FFT_FILTER), "LOW_PASS_FFT_FILTER (" + LOW_PASS_FFT_FILTER + ")");
		MODULE_NAMES_MAPPING.put(new Integer(HIGH_PASS_FFT_FILTER), "HIGH_PASS_FFT_FILTER (" + HIGH_PASS_FFT_FILTER + ")");
		MODULE_NAMES_MAPPING.put(new Integer(HIGH_PASS_BOOST_FILTER), "HIGH_PASS_BOOST_FILTER (" + HIGH_PASS_BOOST_FILTER + ")");
		MODULE_NAMES_MAPPING.put(new Integer(BAND_STOP_FFT_FILTER), "BAND_STOP_FFT_FILTER (" + BAND_STOP_FFT_FILTER + ")");
		MODULE_NAMES_MAPPING.put(new Integer(RAW), "RAW (" + RAW + ")");
		MODULE_NAMES_MAPPING.put(new Integer(PREPROCESSING_PLUGIN), "PREPROCESSING_PLUGIN (" + PREPROCESSING_PLUGIN + ")");
		MODULE_NAMES_MAPPING.put(new Integer(LOW_PASS_CFE_FILTER), "LOW_PASS_CFE_FILTER (" + LOW_PASS_CFE_FILTER + ")");
		MODULE_NAMES_MAPPING.put(new Integer(HIGH_PASS_CFE_FILTER), "HIGH_PASS_CFE_FILTER (" + HIGH_PASS_CFE_FILTER + ")");
		MODULE_NAMES_MAPPING.put(new Integer(BAND_PASS_CFE_FILTER), "BAND_PASS_CFE_FILTER (" + BAND_PASS_CFE_FILTER + ")");
		MODULE_NAMES_MAPPING.put(new Integer(BAND_STOP_CFE_FILTER), "BAND_STOP_CFE_FILTER (" + BAND_STOP_CFE_FILTER + ")");

		// Feature extraction modules
		MODULE_NAMES_MAPPING.put(new Integer(LPC), "LPC (" + LPC + ")");
		MODULE_NAMES_MAPPING.put(new Integer(FFT), "FFT (" + FFT + ")");
		MODULE_NAMES_MAPPING.put(new Integer(F0), "F0 (" + F0 + ")");
		MODULE_NAMES_MAPPING.put(new Integer(SEGMENTATION), "SEGMENTATION (" + SEGMENTATION + ")");
		MODULE_NAMES_MAPPING.put(new Integer(CEPSTRAL), "CEPSTRAL (" + CEPSTRAL + ")");
		MODULE_NAMES_MAPPING.put(new Integer(RANDOM_FEATURE_EXTRACTION), "RANDOM_FEATURE_EXTRACTION (" + RANDOM_FEATURE_EXTRACTION + ")");
		MODULE_NAMES_MAPPING.put(new Integer(MIN_MAX_AMPLITUDES), "MIN_MAX_AMPLITUDES (" + MIN_MAX_AMPLITUDES + ")");
		MODULE_NAMES_MAPPING.put(new Integer(FEATURE_EXTRACTION_PLUGIN), "MIN_MAX_AMPLITUDES (" + MIN_MAX_AMPLITUDES + ")");
		MODULE_NAMES_MAPPING.put(new Integer(FEATURE_EXTRACTION_AGGREGATOR), "FEATURE_EXTRACTION_AGGREGATOR (" + FEATURE_EXTRACTION_AGGREGATOR + ")");

		// Classification modules
		MODULE_NAMES_MAPPING.put(new Integer(NEURAL_NETWORK), "NEURAL_NETWORK (" + NEURAL_NETWORK + ")");
		MODULE_NAMES_MAPPING.put(new Integer(STOCHASTIC), "STOCHASTIC (" + STOCHASTIC + ")");
		MODULE_NAMES_MAPPING.put(new Integer(MARKOV), "MARKOV (" + MARKOV + ")");
		MODULE_NAMES_MAPPING.put(new Integer(EUCLIDEAN_DISTANCE), "EUCLIDEAN_DISTANCE (" + EUCLIDEAN_DISTANCE + ")");
		MODULE_NAMES_MAPPING.put(new Integer(CHEBYSHEV_DISTANCE), "CHEBYSHEV_DISTANCE (" + CHEBYSHEV_DISTANCE + ")");
		MODULE_NAMES_MAPPING.put(new Integer(MANHATTAN_DISTANCE), "MANHATTAN_DISTANCE (" + MANHATTAN_DISTANCE + ")");
		MODULE_NAMES_MAPPING.put(new Integer(CITYBLOCK_DISTANCE), "CITYBLOCK_DISTANCE (" + CITYBLOCK_DISTANCE + ")");
		MODULE_NAMES_MAPPING.put(new Integer(MINKOWSKI_DISTANCE), "MINKOWSKI_DISTANCE (" + MINKOWSKI_DISTANCE + ")");
		MODULE_NAMES_MAPPING.put(new Integer(MAHALANOBIS_DISTANCE), "MAHALANOBIS_DISTANCE (" + MAHALANOBIS_DISTANCE + ")");
		MODULE_NAMES_MAPPING.put(new Integer(RANDOM_CLASSIFICATION), "RANDOM_CLASSIFICATION (" + RANDOM_CLASSIFICATION + ")");
		MODULE_NAMES_MAPPING.put(new Integer(DIFF_DISTANCE), "DIFF_DISTANCE (" + DIFF_DISTANCE + ")");
		MODULE_NAMES_MAPPING.put(new Integer(CLASSIFICATION_PLUGIN), "CLASSIFICATION_PLUGIN (" + CLASSIFICATION_PLUGIN + ")");
		MODULE_NAMES_MAPPING.put(new Integer(ZIPFS_LAW), "ZIPFS_LAW (" + ZIPFS_LAW + ")");
		MODULE_NAMES_MAPPING.put(new Integer(HAMMING_DISTANCE), "HAMMING_DISTANCE (" + HAMMING_DISTANCE + ")");
		MODULE_NAMES_MAPPING.put(new Integer(COSINE_SIMILARITY_MEASURE), "COSINE_SIMILARITY_MEASURE (" + COSINE_SIMILARITY_MEASURE + ")");

		// Audio sample formats
		MODULE_NAMES_MAPPING.put(new Integer(WAV), MARFAudioFileFormat.Type.WAVE.toString());
		MODULE_NAMES_MAPPING.put(new Integer(ULAW), MARFAudioFileFormat.Type.ULAW.toString());
		MODULE_NAMES_MAPPING.put(new Integer(MP3), MARFAudioFileFormat.Type.MP3.toString());
		MODULE_NAMES_MAPPING.put(new Integer(SINE), MARFAudioFileFormat.Type.SINE.toString());
		MODULE_NAMES_MAPPING.put(new Integer(AIFF), MARFAudioFileFormat.Type.AIFF.toString());
		MODULE_NAMES_MAPPING.put(new Integer(AIFFC), MARFAudioFileFormat.Type.AIFC.toString());
		MODULE_NAMES_MAPPING.put(new Integer(AU), MARFAudioFileFormat.Type.AU.toString());
		MODULE_NAMES_MAPPING.put(new Integer(SND), MARFAudioFileFormat.Type.SND.toString());
		MODULE_NAMES_MAPPING.put(new Integer(MIDI), MARFAudioFileFormat.Type.MIDI.toString());
		MODULE_NAMES_MAPPING.put(new Integer(CUSTOM), MARFAudioFileFormat.Type.CUSTOM.toString());
	}


	/*
	 * --------------------------------------------------------
	 * Module Instance References
	 * --------------------------------------------------------
	 */

	/**
	 * Internal <code>Sample</code> reference.
	 * @since 0.2.0
	 */
	private static Sample             soSample            = null;

	/**
	 * Internal <code>SampleLoader</code> reference.
	 * @since 0.2.0
	 */
	private static ISampleLoader      soSampleLoader      = null;

	/**
	 * Internal <code>Preprocessing</code> reference.
	 * @since 0.2.0
	 */
	private static IPreprocessing     soPreprocessing     = null;

	/**
	 * Internal <code>FeatureExtraction</code> reference.
	 * @since 0.2.0
	 */
	private static IFeatureExtraction soFeatureExtraction = null;

	/**
	 * Internal <code>Classification</code> reference.
	 * @since 0.2.0
	 */
	private static IClassification    soClassification    = null;


	/*
	 * --------------------------------------------------------
	 * Versioning
	 * --------------------------------------------------------
	 */

	/**
	 * Indicates major MARF version, like <b>1</b>.x.x.
	 * As of 0.3.0.3 made public.
	 * As of 0.3.0.5 is always equals to <code>Version.MAJOR_VERSION</code>.
	 * @see Version#MAJOR_VERSION
	 */
	public static final int MAJOR_VERSION  = Version.MAJOR_VERSION;

	/**
	 * Indicates minor MARF version, like 1.<b>1</b>.x.
	 * As of 0.3.0.3 made public.
	 * As of 0.3.0.5 is always equals to <code>Version.MINOR_VERSION</code>.
	 * @see Version#MINOR_VERSION
	 */
	public static final int MINOR_VERSION  = Version.MINOR_VERSION;

	/**
	 * Indicates MARF revision, like 1.1.<b>1</b>.
	 * As of 0.3.0.3 made public.
	 * As of 0.3.0.5 is always equals to <code>Version.REVISION</code>.
	 * @see Version#REVISION
	 */
	public static final int REVISION       = Version.REVISION;

	/**
	 * Indicates MARF minor development revision, like 1.1.1.<b>1</b>.
	 * This is primarily for development releases. On the final release
	 * the counting stops and is reset to 0 every minor version.
	 * As of 0.3.0.3 made public.
	 * As of 0.3.0.5 is always equals to <code>Version.MINOR_REVISION</code>.
	 * @since 0.3.0.2
	 * @see #MINOR_VERSION
	 * @see Version#MINOR_REVISION
	 */
	public static final int MINOR_REVISION = Version.MINOR_REVISION;


	/*
	 * --------------------------------------------------------
	 * Current state of MARF
	 * --------------------------------------------------------
	 */

	/**
	 * Indicates what preprocessing method to use in the pipeline.
	 */
	private static int          siPreprocessingMethod     = UNSET;

	/**
	 * Indicates what feature extraction method to use in the pipeline.
	 */
	private static int          siFeatureExtractionMethod = UNSET;

	/**
	 * Indicates what classification method to use in the pipeline.
	 */
	private static int          siClassificationMethod        = UNSET;

	/**
	 * Indicates what sample format is in use.
	 */
	private static int          siSampleFormat                = UNSET;

	/**
	 * ID of the currently trained speaker.
	 */
	private static int          siCurrentSubject              = UNSET;

	/**
	 * Indicates current incoming sample filename.
	 */
	private static String       sstrFileName                  = "";

	/**
	 * Indicates directory name with training samples.
	 */
	private static String       sstrSamplesDir                = "";

	/**
	 * Indicates to prefix the value contain to the training set's filename.
	 * @since 0.3.0.6
	 * @see TrainingSet
	 */
	private static String       sstrTrainingSetFilenamePrefix = "";

	/**
	 * Stores module-specific parameters in an independent way.
	 */
	private static ModuleParams soModuleParams                = null;

	/**
	 * Indicates whether or not to dump a spectrogram at the end of feature extraction.
	 */
	private static boolean      sbDumpSpectrogram             = false;

	/**
	 * Indicates whether or not to dump a wave graph.
	 */
	private static boolean      sbDumpWaveGraph               = false;

	/**
	 * Class of a sample loader plug-in.
	 * @since 0.3.0.5
	 */
	private static Class<?>       soSampleLoaderPluginClass      = null;

	/**
	 * Class of a preprocessing plug-in.
	 * @since 0.3.0.4
	 */
	private static Class<?>       soPreprocessingPluginClass     = null;

	/**
	 * Class of a feature extraction plug-in.
	 * @since 0.3.0.4
	 */
	private static Class<?>       soFeatureExtractionPluginClass = null;

	/**
	 * Class of a classification plug-in.
	 * @since 0.3.0.4
	 */
	private static Class<?>       soClassificationPluginClass    = null;


	/*
	 * --------------------------------------------------------
	 * Methods
	 * --------------------------------------------------------
	 */

	/**
	 * Must never be instantiated or inherited from...
	 * Or should it be allowed?
	 */
	private MARF()
	{
	}


	/*
	 * --------------------------------------------------------
	 * Setting/Getting MARF Configuration Parameters
	 * --------------------------------------------------------
	 */

	/**
	 * Allows setting a complete MARF configuration parameters.
	 * @param poConfiguration the configuration parameters
	 * @return previous configuration
	 * @throws MARFException in case of any exception
	 * @since 0.3.0.6
	 */
	public static synchronized final Configuration setConfiguration(Configuration poConfiguration)
	throws MARFException
	{
		Configuration oOldConfig = getConfiguration();

		siPreprocessingMethod = poConfiguration.iPreprocessingMethod;
		siFeatureExtractionMethod = poConfiguration.iFeatureExtractionMethod;
		siClassificationMethod = poConfiguration.iClassificationMethod;
		siSampleFormat = poConfiguration.iSampleFormat;
		siCurrentSubject = poConfiguration.iCurrentSubject;
		sstrFileName = poConfiguration.strFileName;
		sstrSamplesDir = poConfiguration.strSamplesDir;
		soModuleParams = poConfiguration.oModuleParams;
		sbDumpSpectrogram = poConfiguration.bDumpSpectrogram;
		sbDumpWaveGraph = poConfiguration.bDumpWaveGraph;

		if
		(
			poConfiguration.strSampleLoaderPluginClass != null
			&& poConfiguration.strSampleLoaderPluginClass.equals("") == false
		)
		{
			setSampleLoaderPluginClass(poConfiguration.strSampleLoaderPluginClass);
		}

		if
		(
			poConfiguration.strPreprocessingPluginClass != null
			&& poConfiguration.strPreprocessingPluginClass.equals("") == false
		)
		{
			setPreprocessingPluginClass(poConfiguration.strPreprocessingPluginClass);
		}

		if
		(
			poConfiguration.strFeatureExtractionPluginClass != null
			&& poConfiguration.strFeatureExtractionPluginClass.equals("") == false
		)
		{
			setFeatureExtractionPluginClass(poConfiguration.strFeatureExtractionPluginClass);
		}

		if
		(
			poConfiguration.strClassificationPluginClass != null
			&& poConfiguration.strClassificationPluginClass.equals("") == false
		)
		{
			setClassificationPluginClass(poConfiguration.strClassificationPluginClass);
		}

		Debug.enableDebug(poConfiguration.bDebug);

		return oOldConfig;
	}

	/**
	 * Allows querying for the current MARF configuration.
	 * @return encapsulated configuration parameters object
	 * @since 0.3.0.6
	 */
	public static synchronized final Configuration getConfiguration()
	{
		return new Configuration
		(
			siPreprocessingMethod,
			siFeatureExtractionMethod,
			siClassificationMethod,
			siSampleFormat,
			siCurrentSubject,
			sstrFileName,
			sstrSamplesDir,
			soModuleParams,
			sbDumpSpectrogram,
			sbDumpWaveGraph,
			soSampleLoaderPluginClass == null ? "" : soSampleLoaderPluginClass.getName(),
			soPreprocessingPluginClass == null ? "" : soPreprocessingPluginClass.getName(),
			soFeatureExtractionPluginClass == null ? "" : soFeatureExtractionPluginClass.getName(),
			soClassificationPluginClass == null ? "" : soClassificationPluginClass.getName(),
			Debug.isDebugOn()
		);
	}

	/**
	 * Sets preprocessing method to be used.
	 * @param piPreprocessingMethod one of the allowed preprocessing methods
	 * @throws MARFException if the parameter outside of the valid range
	 */
	public static synchronized final void setPreprocessingMethod(final int piPreprocessingMethod)
	throws MARFException
	{
		if(piPreprocessingMethod < MIN_PREPROCESSING_METHOD || piPreprocessingMethod > MAX_PREPROCESSING_METHOD)
		{
			throw new MARFException
			(
				"Preprocessing method (" + piPreprocessingMethod +
				") is out of range [" + MIN_PREPROCESSING_METHOD + "," + MAX_PREPROCESSING_METHOD + "]."
			);
		}

		siPreprocessingMethod = piPreprocessingMethod;
	}

	/**
	 * Gets currently selected preprocessing method.
	 * @return one of the preprocessing methods
	 */
	public static synchronized final int getPreprocessingMethod()
	{
		return siPreprocessingMethod;
	}

	/**
	 * Sets feature extraction method to be used.
	 * @param piFeatureExtractionMethod one of the allowed feature extraction methods
	 * @throws MARFException if the parameter outside of the valid range
	 */
	public static synchronized final void setFeatureExtractionMethod(final int piFeatureExtractionMethod)
	throws MARFException
	{
		if(piFeatureExtractionMethod < MIN_FEATUREEXTRACTION_METHOD || piFeatureExtractionMethod > MAX_FEATUREEXTRACTION_METHOD)
		{
			throw new MARFException
			(
				"Feature extraction method (" + piFeatureExtractionMethod +
				") is out of range [" + MIN_FEATUREEXTRACTION_METHOD + "," + MAX_FEATUREEXTRACTION_METHOD + "]."
			);
		}

		siFeatureExtractionMethod = piFeatureExtractionMethod;
	}

	/**
	 * Gets currently selected feature extraction method.
	 * @return current feature extraction method
	 */
	public static synchronized final int getFeatureExtractionMethod()
	{
		return siFeatureExtractionMethod;
	}

	/**
	 * Sets classification method to be used.
	 * @param piClassificationMethod one of the allowed classification methods
	 * @throws MARFException if the parameter outside of the valid range
	 */
	public static synchronized final void setClassificationMethod(final int piClassificationMethod)
	throws MARFException
	{
		if(piClassificationMethod < MIN_CLASSIFICATION_METHOD || piClassificationMethod > MAX_CLASSIFICATION_METHOD)
		{
			throw new MARFException
			(
				"Classification method (" + piClassificationMethod +
				") is out of range [" + MIN_CLASSIFICATION_METHOD + "," + MAX_CLASSIFICATION_METHOD + "]."
			);
		}

		siClassificationMethod = piClassificationMethod;
	}

	/**
	 * Gets classification method to be used.
	 * @return current classification method
	 */
	public static synchronized final int getClassificationMethod()
	{
		return siClassificationMethod;
	}

	/**
	 * Sets input sample file format.
	 * @param piSampleFormat one of the allowed sample formats
	 */
	public static synchronized final void setSampleFormat(final int piSampleFormat)
	{
		siSampleFormat = piSampleFormat;
	}

	/**
	 * Gets input sample file format.
	 * @return current sample format
	 */
	public static synchronized final int getSampleFormat()
	{
		return siSampleFormat;
	}

	/**
	 * Sets input sample file name.
	 * @param pstrFileName string representing sample file to be read
	 */
	public static synchronized final void setSampleFile(final String pstrFileName)
	{
		sstrFileName = pstrFileName;
	}

	/**
	 * Obtains filename of a sample currently being processed.
	 * @return file name of a string representing sample file
	 */
	public static synchronized final String getSampleFile()
	{
		return sstrFileName;
	}

	/**
	 * Sets directory with sample files to be read from.
	 * @param pstrSamplesDir string representing directory name
	 */
	public static synchronized final void setSamplesDir(final String pstrSamplesDir)
	{
		sstrSamplesDir = pstrSamplesDir;
	}

	/**
	 * Sets module-specific parameters an application programmer wishes to pass on to the module.
	 * @param poModuleParams parameters' instance
	 */
	public static synchronized final void setModuleParams(final ModuleParams poModuleParams)
	{
		soModuleParams = poModuleParams;
	}

	/**
	 * Sets the prefix to the training sets's filenames for concurrent training
	 * and testing on multiple categories and models.
	 * @param pstrPrefix string representing sample file to be read
	 * @since 0.3.0.6
	 */
	public static synchronized final void setTrainingSetFilenamePrefix(final String pstrPrefix)
	{
		sstrTrainingSetFilenamePrefix = pstrPrefix;
	}

	/**
	 * Obtains the current prefix used by the training set file names.
	 * @return file name of a string representing sample file
	 * @since 0.3.0.6
	 */
	public static synchronized final String getTrainingSetFilenamePrefix()
	{
		return sstrTrainingSetFilenamePrefix;
	}

	/**
	 * Gets module-specific parameters an application programmer passed on to the module.
	 * @return ModuleParams object reference
	 */
	public static synchronized final ModuleParams getModuleParams()
	{
		return soModuleParams;
	}

	/**
	 * Indicates whether spectrogram is wanted as an output of a FeatureExtraction module.
	 * @param pbDump <code>true</code> if wanted, <code>false</code> if not
	 */
	public static synchronized final void setDumpSpectrogram(final boolean pbDump)
	{
		sbDumpSpectrogram = pbDump;
	}

	/**
	 * Whether spectrogram wanted or not.
	 * @return <code>true</code> if spectrogram being dumped, <code>false</code> otherwise
	 */
	public static synchronized final boolean getDumpSpectrogram()
	{
		return sbDumpSpectrogram;
	}

	/**
	 * Indicates whether wave graph is wanted as an output.
	 * @param pbDump <code>true</code> if wanted, <code>false</code> if not
	 */
	public static synchronized final void setDumpWaveGraph(final boolean pbDump)
	{
		sbDumpWaveGraph = pbDump;
	}

	/**
	 * Whether wave graph wanted or not.
	 * @return <code>true</code> if graph wanted being dumped, <code>false</code> otherwise
	 */
	public static synchronized final boolean getDumpWaveGraph()
	{
		return sbDumpWaveGraph;
	}

	/**
	 * Sets ID of a subject currently being trained on.
	 * @param piSubjectID integer ID of the subject
	 */
	public static synchronized final void setCurrentSubject(final int piSubjectID)
	{
		siCurrentSubject = piSubjectID;
	}

	/**
	 * Gets ID of a subject currently being trained on.
	 * @return integer ID of the subject
	 * @since 0.2.0
	 */
	public static synchronized final int getCurrentSubject()
	{
		return siCurrentSubject;
	}

	/**
	 * Allows loading a sample loader plug-in by its name.
	 * @param pstrClassName class name of the plug-in sample loader module
	 * @throws MARFException if class cannot be loaded for any reason
	 * @since 0.3.0.5
	 */
	public static synchronized final void setSampleLoaderPluginClass(String pstrClassName)
	throws MARFException
	{
		try
		{
			soSampleLoaderPluginClass = Class.forName(pstrClassName);
		}
		catch(Exception e)
		{
			throw new MARFException(e.getMessage(), e);
		}
	}

	/**
	 * Allows setting a loaded sample loader plug-in class.
	 * @param poClass class representing a sample loader plug-in object
	 * @throws MARFException if the parameter is <code>null</code>
	 * @since 0.3.0.5
	 */
	public static synchronized final void setSampleLoaderPluginClass(Class<?> poClass)
	throws MARFException
	{
		if(poClass == null)
		{
			throw new MARFException("Plugin class cannot be null.");
		}

		soSampleLoaderPluginClass = poClass;
	}

	/**
	 * Allows querying for the current preprocessing plug-in class.
	 * @return the internal plug-in class
	 * @since 0.3.0.5
	 */
	public static synchronized final Class<?> getSampleLoaderPluginClass()
	{
		return soSampleLoaderPluginClass;
	}

	/**
	 * Allows loading a preprocessing plug-in by its name.
	 * @param pstrClassName class name of the plug-in preprocessing module
	 * @throws MARFException if class cannot be loaded for any reason
	 * @since 0.3.0.4
	 */
	public static synchronized final void setPreprocessingPluginClass(String pstrClassName)
	throws MARFException
	{
		try
		{
			soPreprocessingPluginClass = Class.forName(pstrClassName);
		}
		catch(Exception e)
		{
			throw new MARFException(e.getMessage(), e);
		}
	}

	/**
	 * Allows setting a loaded preprocessing plug-in class.
	 * @param poClass class representing a preprocessing plug-in object
	 * @throws MARFException if the parameter is <code>null</code>
	 * @since 0.3.0.4
	 */
	public static synchronized final void setPreprocessingPluginClass(Class<?> poClass)
	throws MARFException
	{
		if(poClass == null)
		{
			throw new MARFException("Plugin class cannot be null.");
		}

		soPreprocessingPluginClass = poClass;
	}

	/**
	 * Allows querying for the current preprocessing plug-in class.
	 * @return the internal plug-in class
	 * @since 0.3.0.4
	 */
	public static synchronized final Class<?> getPreprocessingPluginClass()
	{
		return soPreprocessingPluginClass;
	}

	/**
	 * Allows loading a feature extraction plug-in by its name.
	 * @param pstrClassName class name of the plug-in feature extraction module
	 * @throws MARFException if class cannot be loaded for any reason
	 * @since 0.3.0.4
	 */
	public static synchronized final void setFeatureExtractionPluginClass(String pstrClassName)
	throws MARFException
	{
		try
		{
			soFeatureExtractionPluginClass = Class.forName(pstrClassName);
		}
		catch(Exception e)
		{
			throw new MARFException(e.getMessage(), e);
		}
	}

	/**
	 * Allows setting a loaded feature extraction class plug-in class.
	 * @param poClass class representing a feature extraction plug-in object
	 * @throws MARFException if the parameter is <code>null</code>
	 * @since 0.3.0.4
	 */
	public static synchronized final void setFeatureExtractionPluginClass(Class<?> poClass)
	throws MARFException
	{
		if(poClass == null)
		{
			throw new MARFException("Plugin class cannot be null.");
		}

		soFeatureExtractionPluginClass = poClass;
	}

	/**
	 * Allows querying for the current feature extraction plug-in class.
	 * @return the internal plug-in class
	 * @since 0.3.0.4
	 */
	public static synchronized final Class<?> getFeatureExtractionPluginClass()
	{
		return soFeatureExtractionPluginClass;
	}

	/**
	 * Allows loading a classification plug-in by its name.
	 * @param pstrClassName class name of the plug-in classification module
	 * @throws MARFException if class cannot be loaded for any reason
	 * @since 0.3.0.4
	 */
	public static synchronized final void setClassificationPluginClass(String pstrClassName)
	throws MARFException
	{
		try
		{
			soClassificationPluginClass = Class.forName(pstrClassName);
		}
		catch(Exception e)
		{
			throw new MARFException(e.getMessage(), e);
		}
	}

	/**
	 * Allows setting a loaded classification plug-in class.
	 * @param poClass class representing a classification plug-in object
	 * @throws MARFException if the parameter is <code>null</code>
	 * @since 0.3.0.4
	 */
	public static synchronized final void setClassificationPluginClass(Class<?> poClass)
	throws MARFException
	{
		if(poClass == null)
		{
			throw new MARFException("Plugin class cannot be null.");
		}

		soClassificationPluginClass = poClass;
	}

	/**
	 * Allows querying for the current classification plug-in class.
	 * @return the internal plug-in class
	 * @since 0.3.0.4
	 */
	public static synchronized final Class<?> getClassificationPluginClass()
	{
		return soClassificationPluginClass;
	}

	/**
	 * Returns a string representation of the MARF version.
	 * As of 0.3.0.3 MINOR_REVISION is also returned.
	 *
	 * @return version String
	 * @see #MINOR_REVISION
	 */
	public static final String getVersion()
	{
		return Version.getStringVersion();
	}

	/**
	 * Returns an integer representation of the MARF version.
	 * As of 0.3.0.3, MINOR_REVISION is included into calculations
	 * and the formula changed to begin with 1000 as a MAJOR_VERSION
	 * coefficient.
	 *
	 * @return integer version as <code>MAJOR_VERSION * 1000 + MINOR_VERSION * 100 + REVISION * 10 + MINOR_REVISION</code>
	 *
	 * @see #MAJOR_VERSION
	 * @see #MINOR_VERSION
	 * @see #REVISION
	 * @see #MINOR_REVISION
	 */
	public static final int getIntVersion()
	{
		return Version.getIntVersion();
	}

	/**
	 * Retrieves double version of MARF. Unlike the integer version, the double
	 * one begins with 100 and the minor revision is returned after the point,
	 * e.g. 123.4 for 1.2.3.4.
	 *
	 * @return double version as <code>MAJOR_VERSION * 100 + MINOR_VERSION * 10 + REVISION + MINOR_REVISION / 10</code>
	 *
	 * @see #MAJOR_VERSION
	 * @see #MINOR_VERSION
	 * @see #REVISION
	 * @see #MINOR_REVISION
	 *
	 * @since 0.3.0.3
	 */
	public static final double getDoubleVersion()
	{
		return Version.getDoubleVersion();
	}

	/**
	 * Returns a string representation of the current MARF configuration.
	 * @return configuration string
	 */
	public static synchronized final String getConfig()
	{
		return new StringBuffer()
			.append("[")
			.append("SL: ").append(MODULE_NAMES_MAPPING.get(new Integer(siSampleFormat))).append(", ")
			.append("PR: ").append(MODULE_NAMES_MAPPING.get(new Integer(siPreprocessingMethod))).append(", ")
			.append("FE: ").append(MODULE_NAMES_MAPPING.get(new Integer(siFeatureExtractionMethod))).append(", ")
			.append("CL: ").append(MODULE_NAMES_MAPPING.get(new Integer(siClassificationMethod))).append(", ")
			.append("ID: ").append(siCurrentSubject)
			.append("]")
			.toString();
	}

	/**
	 * Retrieves current <code>Sample</code> reference.
	 * @return Sample object
	 * @since 0.2.0
	 */
	public static synchronized final Sample getSample()
	{
		return soSample;
	}

	/**
	 * Retrieves current <code>SampleLoader</code> reference.
	 * @return SampleLoader object
	 * @since 0.2.0
	 */
	public static synchronized final ISampleLoader getSampleLoader()
	{
		return soSampleLoader;
	}

	/**
	 * Retrieves current <code>Preprocessing</code> reference.
	 * @return Preprocessing object
	 * @since 0.2.0
	 */
	public static synchronized final IPreprocessing getPreprocessing()
	{
		return soPreprocessing;
	}

	/**
	 * Retrieves current <code>FeatureExtraction</code> reference.
	 * @return FeatureExtraction object
	 * @since 0.2.0
	 */
	public static synchronized final IFeatureExtraction getFeatureExtraction()
	{
		return soFeatureExtraction;
	}

	/**
	 * Retrieves current <code>Classification</code> reference.
	 * @return Classification object
	 * @since 0.2.0
	 */
	public static synchronized final IClassification getClassification()
	{
		return soClassification;
	}

	/**
	 * Retrieves current <code>Classification</code> reference.
	 * @return Classification object
	 * @since 0.3.0.6; June 17, 2012
	 */
	public static synchronized final Class<?> getClassificationClass()
	throws ClassificationException
	{
		return ClassificationFactory.getClassificationClassByID(siClassificationMethod);
	}

	/**
	 * Queries for the final classification result.
	 * @return integer ID of the identified subject
	 */
	public static synchronized final int queryResultID()
	{
		return soClassification.getResult().getID();
	}

	/**
	 * Gets the entire Result object of the likely outcome.
	 * @return Result ID and all the statistics of the classification
	 */
	public static synchronized final Result getResult()
	{
		return soClassification.getResult();
	}

	/**
	 * Gets the entire collection of results.
	 * @return ResultSet object with one or more results.
	 * @since 0.3.0.2
	 */
	public static synchronized final ResultSet getResultSet()
	{
		return soClassification.getResultSet();
	}

	/* API */

	/**
	 * Recognition/Identification mode.
	 * @since 0.2.0
	 * @throws MARFException if there was an error in the pipeline
	 * or classification
	 */
	public static final void recognize()
	throws MARFException
	{
		startRecognitionPipeline();

		Debug.debug("MARF: Classifying...");

		synchronized(soClassification)
		{
			if(soClassification.classify() == false)
			{
				throw new ClassificationException("Classification returned false.");
			}
		}
	}

	public static final void recognize(Sample poSample)
	throws MARFException
	{
		checkSettings();
		startRecognitionPipeline(poSample);

		Debug.debug("MARF: Classifying...");

		synchronized(soClassification)
		{
			if(soClassification.classify() == false)
			{
				throw new ClassificationException("Classification returned false.");
			}
		}
	}

	/**
	 * Training mode.
	 * @since 0.2.0
	 * @throws MARFException if the subject is unset or there was
	 * an error in training in the underlying classification module
	 */
	public static final void train()
	throws MARFException
	{
		//if(siCurrentSubject == UNSET)
		if(getCurrentSubject() == UNSET)
		{
			throw new MARFException("Unset subject ID for training.");
		}

		startRecognitionPipeline();

		Debug.debug("MARF: Training...");

		synchronized(soClassification)
		{
			//XXX batch: soClassification.restore();

			if(soClassification.train() == false)
			{
				throw new ClassificationException("Training returned false.");
			}

			//XXX batch: soClassification.dump();
		}
	}

	public static final void train(Sample poSample)
	throws MARFException
	{
		if(getCurrentSubject() == UNSET)
		{
			throw new MARFException("Unset subject ID for training.");
		}

		checkSettings();
		startRecognitionPipeline(poSample);

		Debug.debug("MARF: Training...");

		synchronized(soClassification)
		{
			if(soClassification.train() == false)
			{
				throw new ClassificationException("Training returned false.");
			}
		}
	}

	/**
	 * The core processing pipeline. The pipeline work
	 * through almost entire process up until creation
	 * of the classification module. Then the application's
	 * choice depicts whether to call train() or classify()
	 * on this module via MARF's train() or recognize() methods.
	 * The pipeline is granularly synchronized on each object
	 * that gets instantiated, so no indirect blocking occurs
	 * on MARF itself (e.g. between the main thread and the inner
	 * threads of the framework that maybe accessing the MARF
	 * class concurrently).
	 *
	 * @throws MARFException in case any underlying error happens
	 * @see #train()
	 * @see #recognize()
	 */
	private static final void startRecognitionPipeline()
	throws MARFException
	{
		/*
		 * Checking minimal required settings
		 */
		checkSettings();

		/*
		 * Sample Loading Stage
		 */
		synchronized(sstrFileName)
		{
			Debug.debug("MARF: Loading sample \"" + getSampleFile() + "\"");
			soSampleLoader = SampleLoaderFactory.create(siSampleFormat);

			synchronized(soSampleLoader)
			{
				//soSample = soSampleLoader.loadSample(sstrFileName);
				startRecognitionPipeline(soSampleLoader.loadSample(sstrFileName));
			}//sampleloader
		}//samplefilename
	}

	/**
	 * @param poSample
	 * @throws MARFException
	 */
	private static final void startRecognitionPipeline(Sample poSample)
	throws MARFException
	{
		soSample = poSample;
		
		/*
		 * Preprocessing Stage
		 */
		synchronized(soSample)
		{
			Debug.debug("MARF: Preprocessing...");
			soPreprocessing = PreprocessingFactory.create(siPreprocessingMethod, soSample);

			synchronized(soPreprocessing)
			{
				// TODO: [SM]: Should this be in the preprocessing itself somewhere?

				if(sbDumpWaveGraph)
				{
					Debug.debug("MARF: Dumping initial wave graph...");

					new WaveGrapher
					(
						soSample.getSampleArray(),
						0,
						soSample.getSampleArray().length,
						getSampleFile(),
						"initial"
					).dump();
				}

				Debug.debug("MARF: Invoking preprocess() of " + soPreprocessing.getClass().getName());
				soPreprocessing.preprocess();
				Debug.debug("MARF: Done preprocess() of " + soPreprocessing.getClass().getName());

				if(sbDumpWaveGraph)
				{
					Debug.debug("MARF: Dumping preprocessed wave graph...");

					new WaveGrapher
					(
						soSample.getSampleArray(),
						0,
						soSample.getSampleArray().length,
						getSampleFile(),
						"preprocessed"
					).dump();
				}

				/*
				 * Feature Extraction Stage
				 */
				Debug.debug("MARF: Feature extraction...");
				soFeatureExtraction = FeatureExtractionFactory.create(siFeatureExtractionMethod, soPreprocessing);

				synchronized(soFeatureExtraction)
				{
					soFeatureExtraction.extractFeatures();

					/*
					 * Classification Stage
					 */
					Debug.debug("MARF: Classification...");
					soClassification = ClassificationFactory.create(siClassificationMethod, soFeatureExtraction);

					/*
					 * Classification ends in here, as it is continue in one
					 * way or the other in train() or recognize() depending on
					 * the current run-time mode.
					 */
				}// feat
			}// prep
		}//sample itself
	}

	/**
	 * Checks for all necessary settings to be present.
	 * Specifically, checks whether preprocessing, feature
	 * extraction, classification methods are set as well as
	 * the audio sample format. The filename of the sample
	 * or a directory must also be present.
	 *
	 * @throws MARFException if any of the settings are unset
	 * @since 0.3.0.5
	 */
//	private static synchronized void checkSettings()
	public static synchronized void checkSettings()
	throws MARFException
	{
		if
		(
			siPreprocessingMethod     == UNSET ||
			siFeatureExtractionMethod == UNSET ||
			siClassificationMethod    == UNSET ||
			siSampleFormat            == UNSET ||

			// TODO: either filename or dir must present
			(sstrFileName.equals("") && sstrSamplesDir.equals(""))
		)
		{
			// TODO: Enhance error reporting here
			String strSetupErrMsg =
				"MARF.startRecognitionPipeline() - Some configuration parameters were unset.\n" +
				getConfig();

			throw new MARFException(strSetupErrMsg);
		}
	}

	/**
	 * Meant to provide implementation of the buffered sample processing for large samples.
	 * Not implemented.
	 * @throws NotImplementedException
	 */
	public static synchronized final void streamedRecognition()
	{
		throw new NotImplementedException("MARF.streamedRecognition()");
	}

	/**
	 * <p>Enumeration of Statistical Estimators.
	 * In 0.3.0.5 renamed from <code>IStatisticalEstimators</code> to
	 * <code>EStatisticalEstimators</code>.
	 * </p>
	 *
	 * @author Serguei Mokhov
	 * @since 0.3.0.2
	 */
	public interface EStatisticalEstimators
	{
		/**
		 * Indicates to use Maximum Likelihood Estimate estimator/smoothing.
		 */
		public static final int MLE                    = 800;

		/**
		 * Indicates to use Add One estimator/smoothing.
		 */
		public static final int ADD_ONE                = 801;

		/**
		 * Indicates to use Add Delta estimator/smoothing.
		 */
		public static final int ADD_DELTA              = 802;

		/**
		 * Indicates to use Witten-Bell estimator/smoothing.
		 */
		public static final int WITTEN_BELL            = 803;

		/**
		 * Indicates to use Good-Turing estimator/smoothing.
		 */
		public static final int GOOD_TURING            = 804;

		/**
		 * Indicates to use SLI estimator/smoothing.
		 */
		public static final int SLI                    = 805;

		/**
		 * Indicates to use GLI estimator/smoothing.
		 */
		public static final int GLI                    = 806;

		/**
		 * Indicates to use Katz Backoff estimator/smoothing.
		 */
		public static final int KATZ_BACKOFF           = 807;

		/**
		 * Indicates to use ELE estimator/smoothing.
		 * @since 0.3.0.5
		 */
		public static final int ELE                    = 808;

		/**
		 * Lower boundary for statistical estimators enumeration.
		 * Used in error checks.
		 *
		 * *Update it when add more methods.*
		 *
		 * @since 0.3.0.5
		 */
		public static final int MIN_STATS_ESTIMATOR = MLE;

		/**
		 * Upper boundary for statistical estimators enumeration.
		 * Used in error checks.
		 *
		 * *Update it when add more methods.*
		 *
		 * @since 0.3.0.5
		 */
		public static final int MAX_STATS_ESTIMATOR = KATZ_BACKOFF;
	}

	/**
	 * <p>Enumeration of N-gram Models.
	 * In 0.3.0.5 renamed from <code>INgramModels</code> to
	 * <code>ENgramModels</code>.
	 * </p>
	 *
	 * @author Serguei Mokhov
	 * @since 0.3.0.2
	 */
	public interface ENgramModels
	{
		/**
		 * Indicates to use unigram language model.
		 */
		public static final int UNIGRAM                = 900;

		/**
		 * Indicates to use bigram language model.
		 */
		public static final int BIGRAM                 = 901;

		/**
		 * Indicates to use trigram language model.
		 */
		public static final int TRIGRAM                = 902;

		/**
		 * Indicates to use n-gram language model.
		 */
		public static final int NGRAM                  = 903;

		/**
		 * Lower boundary for n-gram models enumeration.
		 * Used in error checks.
		 *
		 * *Update it when add more methods.*
		 *
		 * @since 0.3.0.5
		 */
		public static final int MIN_NGRAM_MODEL = UNIGRAM;

		/**
		 * Upper boundary for n-gram models enumeration.
		 * Used in error checks.
		 *
		 * *Update it when add more methods.*
		 *
		 * @since 0.3.0.5
		 */
		public static final int MAX_NGRAM_MODEL = NGRAM;
	}

	/**
	 * <p>Class NLP is more related to the Natural Language Processing
	 * part of MARF.</p>
	 *
	 * @author Serguei Mokhov
	 * @since 0.3.0.2
	 */
	public static class NLP
	{
		/**
		 * Indicates to use stemming module.
		 */
		public static final int STEMMING               = 1000;

		/**
		 * Indicates to use case-sensitive processing of text.
		 */
		public static final int CASE_SENSITIVE         = 1001;

		/**
		 * When parsing text, also parse numbers as tokens.
		 */
		public static final int PARSE_NUMBERS          = 1002;

		/**
		 * When parsing text, also parse quoted literals.
		 */
		public static final int PARSE_QUOTED_STRINGS   = 1003;

		/**
		 * When parsing text, also parse typical ends of sentences.
		 */
		public static final int PARSE_ENDS_OF_SENTENCE = 1004;

		/**
		 * Perform a raw dump of Zipf's Law data.
		 */
		public static final int RAW_ZIPFS_LAW_DUMP     = 1005;

		/**
		 * Work in character n-gram mode.
		 */
		public static final int CHARACTER_MODE         = 1006;

		/**
		 * Action to train a classifier.
		 */
		public static final int TRAIN                  = 1007;

		/**
		 * Action to perform a classification task.
		 */
		public static final int CLASSIFY               = 1008;

		/**
		 * Use interactive mode.
		 */
		public static final int INTERACTIVE            = 1009;

		/**
		 * When classifying, cheat with Zipf's Law implementation.
		 */
		public static final int ZIPFS_LAW_CHEAT        = 1010;

		/**
		 * Similarly to <code>CHARACTER_MODE</code> work in word mode
		 * for n-grams.
		 * @since 0.3.0.5
		 * @see #CHARACTER_MODE
		 */
		public static final int WORD_MODE              = 1011;

		/**
		 * Current smoothing method.
		 * Default is MLE.
		 * @see EStatisticalEstimators#MLE
		 */
		private static int siSmoothingMethod = EStatisticalEstimators.MLE;

		/**
		 * Current N-gram model.
		 * Default is BIGRAM.
		 * @see ENgramModels#BIGRAM
		 */
		private static int siNgramModel = ENgramModels.BIGRAM;

		/**
		 * Current natural language.
		 * Default is "en".
		 */
		private static String sstrLanguage = "en";

		/**
		 * Retrieves current smoothing method.
		 * @return inner smoothing method
		 */
		public static synchronized final int getSmoothingMethod()
		{
			return siSmoothingMethod;
		}

		/**
		 * Sets current smoothing method.
		 * @param piSmoothingMethod new smoothing method to use
		 * @throws NLPException if the parameter is outside the valid range
		 * @see EStatisticalEstimators#MIN_STATS_ESTIMATOR
		 * @see EStatisticalEstimators#MAX_STATS_ESTIMATOR
		 */
		public static synchronized void setSmoothingMethod(final int piSmoothingMethod)
		throws NLPException
		{
			if
			(
				piSmoothingMethod < EStatisticalEstimators.MIN_STATS_ESTIMATOR
				|| piSmoothingMethod > EStatisticalEstimators.MAX_STATS_ESTIMATOR
			)
			{
				throw new NLPException
				(
					"Statistical smoothing estimartor (" + piSmoothingMethod +
					") is out of range [" + EStatisticalEstimators.MIN_STATS_ESTIMATOR +
					"," + EStatisticalEstimators.MAX_STATS_ESTIMATOR + "]."
				);
			}

			siSmoothingMethod = piSmoothingMethod;
		}

		/**
		 * Retrieves current n-gram model.
		 * @return inner n-gram model
		 */
		public static synchronized final int getNgramModel()
		{
			return siNgramModel;
		}

		/**
		 * Sets current n-gram model.
		 * @param piNgramModel new n-gram model to use
		 * @throws NLPException if the parameter is outside the valid range
		 * @see ENgramModels#MIN_NGRAM_MODEL
		 * @see ENgramModels#MAX_NGRAM_MODEL
		 */
		public static synchronized final void setNgramModel(final int piNgramModel)
		throws NLPException
		{
			if(piNgramModel < ENgramModels.MIN_NGRAM_MODEL || piNgramModel > ENgramModels.MAX_NGRAM_MODEL)
			{
				throw new NLPException
				(
					"N-gram model (" + piNgramModel +
					") is out of range [" + ENgramModels.MIN_NGRAM_MODEL + "," + ENgramModels.MAX_NGRAM_MODEL + "]."
				);
			}

			siNgramModel = piNgramModel;
		}

		/**
		 * Retrieves current language being processed.
		 * @return inner smoothing method
		 */
		public static synchronized String getLanguage()
		{
			return sstrLanguage;
		}

		/**
		 * Sets current processed language.
		 * @param pstrLanguages new language value
		 * @throws NLPException if the parameter is null or empty
		 */
		public static synchronized void setLanguage(String pstrLanguages)
		throws NLPException
		{
			if(pstrLanguages == null || pstrLanguages.length() == 0)
			{
				throw new NLPException("Null or empty language value specified.");
			}

			sstrLanguage = pstrLanguages;
		}
	} // class NLP

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.120 $";
	}
}

// EOF
