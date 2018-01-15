package marf;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import marf.Storage.ModuleParams;
import marf.util.Arrays;
import marf.util.NotImplementedException;


/**
 * <p>Encapsulates MARF configuration parameters for serialization
 * to disk or network.
 * </p>
 * 
 * @author Serguei Mokhov
 * @since 0.3.0.6
 * @version $Id: Configuration.java,v 1.7 2012/07/09 03:53:32 mokhov Exp $
 */
public class Configuration
implements Serializable, Cloneable
{
	/**
	 * For serialization versioning.
	 */
	private static final long serialVersionUID = -5710879938672379718L;

	/**
	 * Indicates what preprocessing method to use in the pipeline.
	 */
	protected int           iPreprocessingMethod     = MARF.UNSET;

	/**
	 * Indicates what feature extraction method to use in the pipeline.
	 */
	protected int           iFeatureExtractionMethod = MARF.UNSET;

	/**
	 * Indicates what classification method to use in the pipeline.
	 */
	protected  int          iClassificationMethod    = MARF.UNSET;

	/**
	 * Indicates what sample format is in use.
	 */
	protected  int          iSampleFormat            = MARF.UNSET;

	/**
	 * ID of the currently trained speaker.
	 */
	protected  int          iCurrentSubject          = MARF.UNSET;

	/**
	 * Indicates current incoming sample filename.
	 */
	protected  String       strFileName              = "";

	/**
	 * Indicates directory name with training samples.
	 */
	protected  String       strSamplesDir            = "";

	/**
	 * Stores module-specific parameters in an independent way.
	 */
	protected  ModuleParams oModuleParams            = null;

	/**
	 * Indicates whether or not to dump a spectrogram at the end of feature extraction.
	 */
	protected  boolean      bDumpSpectrogram         = false;

	/**
	 * Indicates whether or not to dump a wave graph.
	 */
	protected  boolean      bDumpWaveGraph           = false;

	/**
	 * Fully-qualified class name of a sample loader plug-in.
	 */
	protected  String       strSampleLoaderPluginClass      = null;

	/**
	 * Fully-qualified class name of a preprocessing plug-in.
	 */
	protected  String       strPreprocessingPluginClass     = null;

	/**
	 * Fully-qualified class name of a feature extraction plug-in.
	 */
	protected  String       strFeatureExtractionPluginClass = null;

	/**
	 * Fully-qualified class name of a classification plug-in.
	 */
	protected  String       strClassificationPluginClass    = null;

	/**
	 * Debug flag.
	 */
	protected boolean bDebug = false;


	/**
	 * 
	 */
	public Configuration()
	{
		super();
	}

	/**
	 * All-fields constructor.
	 * @param piPreprocessingMethod
	 * @param piFeatureExtractionMethod
	 * @param piClassificationMethod
	 * @param piSampleFormat
	 * @param piCurrentSubject
	 * @param pstrFileName
	 * @param pstrSamplesDir
	 * @param poModuleParams
	 * @param pbDumpSpectrogram
	 * @param pbDumpWaveGraph
	 * @param pstrSampleLoaderPluginClass
	 * @param pstrPreprocessingPluginClass
	 * @param pstrFeatureExtractionPluginClass
	 * @param pstrClassificationPluginClass
	 * @param pbDebug
	 */
	public Configuration
	(
		int piPreprocessingMethod,
		int piFeatureExtractionMethod,
		int piClassificationMethod,
		int piSampleFormat,
		int piCurrentSubject,
		String pstrFileName,
		String pstrSamplesDir,
		ModuleParams poModuleParams,
		boolean pbDumpSpectrogram,
		boolean pbDumpWaveGraph,
		String pstrSampleLoaderPluginClass,
		String pstrPreprocessingPluginClass,
		String pstrFeatureExtractionPluginClass,
		String pstrClassificationPluginClass,
		boolean pbDebug
	)
	{
		this.iPreprocessingMethod = piPreprocessingMethod;
		this.iFeatureExtractionMethod = piFeatureExtractionMethod;
		this.iClassificationMethod = piClassificationMethod;
		this.iSampleFormat = piSampleFormat;
		this.iCurrentSubject = piCurrentSubject;
		this.strFileName = pstrFileName;
		this.strSamplesDir = pstrSamplesDir;
		this.oModuleParams = poModuleParams;
		this.bDumpSpectrogram = pbDumpSpectrogram;
		this.bDumpWaveGraph = pbDumpWaveGraph;
		this.strSampleLoaderPluginClass = pstrSampleLoaderPluginClass;
		this.strPreprocessingPluginClass = pstrPreprocessingPluginClass;
		this.strFeatureExtractionPluginClass = pstrFeatureExtractionPluginClass;
		this.strClassificationPluginClass = pstrClassificationPluginClass;
		this.bDebug = pbDebug;
	}

	/**
	 * Copy-constructor.
	 * @param poConfiguration
	 */
	public Configuration(Configuration poConfiguration)
	{
		Configuration oCopy = (Configuration)poConfiguration.clone();

		this.iPreprocessingMethod = oCopy.iPreprocessingMethod;
		this.iFeatureExtractionMethod = oCopy.iFeatureExtractionMethod;
		this.iClassificationMethod = oCopy.iClassificationMethod;
		this.iSampleFormat = oCopy.iSampleFormat;
		this.iCurrentSubject = oCopy.iCurrentSubject;
		this.strFileName = oCopy.strFileName;
		this.strSamplesDir = oCopy.strSamplesDir;
		this.oModuleParams = oCopy.oModuleParams;
		this.bDumpSpectrogram = oCopy.bDumpSpectrogram;
		this.bDumpWaveGraph = oCopy.bDumpWaveGraph;
		this.strSampleLoaderPluginClass = oCopy.strSampleLoaderPluginClass;
		this.strPreprocessingPluginClass = oCopy.strPreprocessingPluginClass;
		this.strFeatureExtractionPluginClass = oCopy.strFeatureExtractionPluginClass;
		this.strClassificationPluginClass = oCopy.strClassificationPluginClass;
		this.bDebug = oCopy.bDebug;
	}

	/**
	 * Converts the encapsulated configuration to the
	 * Properties-style format. Primarily used for debugging
	 * at this point.
	 * 
	 * TODO: document/standardize/constify property keys.
	 *
	 * @return instance of java.util.Properties with MARF configuration.
	 * @see java.util.Properties
	 * @see #toString()
	 */
	public Properties toProperties()
	{
		Properties oProperties = new Properties();
		
		oProperties.setProperty("marf.module.preprocessing.method", Integer.toString(this.iPreprocessingMethod));
		oProperties.setProperty("marf.module.preprocessing.plugin", this.strPreprocessingPluginClass == null ? "" : this.strPreprocessingPluginClass);

		oProperties.setProperty("marf.module.featureextraction.method", Integer.toString(this.iFeatureExtractionMethod));
		oProperties.setProperty("marf.module.featureextraction.plugin", this.strFeatureExtractionPluginClass == null ? "" : this.strFeatureExtractionPluginClass);

		oProperties.setProperty("marf.module.classification.method", Integer.toString(this.iClassificationMethod));
		oProperties.setProperty("marf.module.classification.plugin", this.strClassificationPluginClass == null ? "" : this.strClassificationPluginClass);
		oProperties.setProperty("marf.module.classification.currentsubject", Integer.toString(this.iCurrentSubject));
		
		oProperties.put("marf.module.params", this.oModuleParams == null ?  new ModuleParams() : this.oModuleParams);

		oProperties.setProperty("marf.samples.format", Integer.toString(this.iSampleFormat));
		oProperties.setProperty("marf.samples.filename", this.strFileName == null ? "" : this.strFileName);
		oProperties.setProperty("marf.samples.dir", this.strSamplesDir == null ? "" : this.strSamplesDir);
		oProperties.setProperty("marf.samples.loader.plugin", this.strSampleLoaderPluginClass == null ? "" : this.strSampleLoaderPluginClass);

		oProperties.setProperty("marf.flags.dump.spectrogram", Boolean.toString(this.bDumpSpectrogram));
		oProperties.setProperty("marf.flags.dump.wavegraph", Boolean.toString(this.bDumpWaveGraph));
		oProperties.setProperty("marf.flags.debug", Boolean.toString(this.bDebug));

		return oProperties;
	}
	
	
	/**
	 * @return
	 * @throws NotImplementedException
	 */
	public ModuleParams toModuleParams()
	{
		throw new NotImplementedException();
	}
	
	/**
	 * Converts configuration to argv-type of array
	 * using Properties with name=value pairs for each
	 * configuration element.
	 *
	 * @return array of strings representing
	 * @see #toProperties()
	 */
	public String[] toArgumentVector()
	{
		Properties oProperties = toProperties();
		String[] astrArgv = new String[oProperties.size()];

		Enumeration<?> oPropNames = oProperties.propertyNames();
		
		int i = 0;
		
		while(oPropNames.hasMoreElements())
		{
			Object oPropertyName = oPropNames.nextElement();
			Object oPropertyValue = oProperties.get(oPropertyName);
			
			if(oPropertyValue.toString().equals("") == false)
			{
				if(oPropertyName.equals("marf.module.params") == false)
				{
					astrArgv[i++] = new StringBuffer()
						.append(oPropertyName).append("=").append(oPropertyValue)
						.toString();
				}
				else
				{
					StringBuffer oValue = new StringBuffer('"').append(oPropertyValue.toString().replaceAll("\"","\\\"").replaceAll("\n","\\n")).append('"');

					astrArgv[i++] = new StringBuffer() 
						.append(oPropertyName).append("=").append(oValue)
						.toString();
				}
			}
			
		}

		String[] argv = new String[i];
		Arrays.copy(argv, 0, astrArgv, 0, i);
		
		return argv;
	}

	/**
	 * @return
	 */
	public Vector<?> toVector()
	{
		return Arrays.arrayToVector(toArgumentVector());
	}
	
	/**
	 * @return Returns the bDebug.
	 */
	public boolean isDebugOn()
	{
		return this.bDebug;
	}

	/**
	 * @param pbDebug The bDebug to set.
	 */
	public void setDebug(boolean pbDebug)
	{
		this.bDebug = pbDebug;
	}

	/**
	 * @return Returns the bDumpSpectrogram.
	 */
	public boolean isDumpSpectrogramOn()
	{
		return this.bDumpSpectrogram;
	}

	/**
	 * @param pbDumpSpectrogram The bDumpSpectrogram to set.
	 */
	public void setDumpSpectrogram(boolean pbDumpSpectrogram)
	{
		this.bDumpSpectrogram = pbDumpSpectrogram;
	}

	/**
	 * @return Returns the bDumpWaveGraph.
	 */
	public boolean isDumpWaveGraphOn()
	{
		return this.bDumpWaveGraph;
	}

	/**
	 * @param pbDumpWaveGraph The bDumpWaveGraph to set.
	 */
	public void setDumpWaveGraph(boolean pbDumpWaveGraph)
	{
		this.bDumpWaveGraph = pbDumpWaveGraph;
	}

	/**
	 * @return Returns the iClassificationMethod.
	 */
	public int getClassificationMethod()
	{
		return this.iClassificationMethod;
	}

	/**
	 * @param piClassificationMethod The iClassificationMethod to set.
	 */
	public void setClassificationMethod(int piClassificationMethod)
	{
		this.iClassificationMethod = piClassificationMethod;
	}

	/**
	 * @return Returns the iCurrentSubject.
	 */
	public int getCurrentSubject()
	{
		return this.iCurrentSubject;
	}

	/**
	 * @param piCurrentSubject The iCurrentSubject to set.
	 */
	public void setCurrentSubject(int piCurrentSubject)
	{
		this.iCurrentSubject = piCurrentSubject;
	}

	/**
	 * @return Returns the iFeatureExtractionMethod.
	 */
	public int getFeatureExtractionMethod()
	{
		return this.iFeatureExtractionMethod;
	}

	/**
	 * @param piFeatureExtractionMethod The iFeatureExtractionMethod to set.
	 */
	public void setFeatureExtractionMethod(int piFeatureExtractionMethod)
	{
		this.iFeatureExtractionMethod = piFeatureExtractionMethod;
	}

	/**
	 * @return Returns the iPreprocessingMethod.
	 */
	public int getPreprocessingMethod()
	{
		return this.iPreprocessingMethod;
	}

	/**
	 * @param piPreprocessingMethod The iPreprocessingMethod to set.
	 */
	public void setPreprocessingMethod(int piPreprocessingMethod)
	{
		this.iPreprocessingMethod = piPreprocessingMethod;
	}

	/**
	 * @return Returns the iSampleFormat.
	 */
	public int getSampleFormat()
	{
		return this.iSampleFormat;
	}

	/**
	 * @param piSampleFormat The iSampleFormat to set.
	 */
	public void setSampleFormat(int piSampleFormat)
	{
		this.iSampleFormat = piSampleFormat;
	}

	/**
	 * @return Returns the oClassificationPluginClass.
	 */
	public String getClassificationPluginClass()
	{
		return this.strClassificationPluginClass;
	}

	/**
	 * @param pstrClassificationPluginClass The oClassificationPluginClass to set.
	 */
	public void setClassificationPluginClass(String pstrClassificationPluginClass)
	{
		this.strClassificationPluginClass = pstrClassificationPluginClass;
	}

	/**
	 * @return Returns the oFeatureExtractionPluginClass.
	 */
	public String getFeatureExtractionPluginClass()
	{
		return this.strFeatureExtractionPluginClass;
	}

	/**
	 * @param pstrFeatureExtractionPluginClass The oFeatureExtractionPluginClass to set.
	 */
	public void setFeatureExtractionPluginClass(String pstrFeatureExtractionPluginClass)
	{
		this.strFeatureExtractionPluginClass = pstrFeatureExtractionPluginClass;
	}

	/**
	 * @return Returns the oModuleParams.
	 */
	public ModuleParams getModuleParams()
	{
		return this.oModuleParams;
	}

	/**
	 * @param poModuleParams The oModuleParams to set.
	 */
	public void setModuleParams(ModuleParams poModuleParams)
	{
		this.oModuleParams = poModuleParams;
	}

	/**
	 * @return Returns the oPreprocessingPluginClass.
	 */
	public String getPreprocessingPluginClass()
	{
		return this.strPreprocessingPluginClass;
	}

	/**
	 * @param pstrPreprocessingPluginClass The oPreprocessingPluginClass to set.
	 */
	public void setPreprocessingPluginClass(String pstrPreprocessingPluginClass)
	{
		this.strPreprocessingPluginClass = pstrPreprocessingPluginClass;
	}

	/**
	 * @return Returns the oSampleLoaderPluginClass.
	 */
	public String getSampleLoaderPluginClass()
	{
		return this.strSampleLoaderPluginClass;
	}

	/**
	 * @param pstrSampleLoaderPluginClass The oSampleLoaderPluginClass to set.
	 */
	public void setSampleLoaderPluginClass(String pstrSampleLoaderPluginClass)
	{
		this.strSampleLoaderPluginClass = pstrSampleLoaderPluginClass;
	}

	/**
	 * @return Returns the strFileName.
	 */
	public String getFileName()
	{
		return this.strFileName;
	}

	/**
	 * @param pstrFileName The strFileName to set.
	 */
	public void setFileName(String pstrFileName)
	{
		this.strFileName = pstrFileName;
	}

	/**
	 * @return Returns the strSamplesDir.
	 */
	public String getSamplesDir()
	{
		return this.strSamplesDir;
	}

	/**
	 * @param pstrSamplesDir The strSamplesDir to set.
	 */
	public void setSamplesDir(String pstrSamplesDir)
	{
		this.strSamplesDir = pstrSamplesDir;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		try
		{
			Configuration oCopy = (Configuration)super.clone();
			oCopy.oModuleParams = (ModuleParams)this.oModuleParams.clone();
			return oCopy;
		}
		catch(CloneNotSupportedException e)
		{
			e.printStackTrace(System.err);
			throw new InternalError();
		}
	}

	/**
	 * Returns String representation of Configuration by converting
	 * Properties to the String format. Primarily for debugging.
	 * @see java.lang.Object#toString()
	 * @see #toProperties()
	 */
	public String toString()
	{
		return toProperties().toString();
	}
}

// EOF
