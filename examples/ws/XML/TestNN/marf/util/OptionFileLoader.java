package marf.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;


/**
 * <p>Loads a configuration file.</p>
 * 
 * <p>This class is based upon <code>OptionsFileLoaderSingleton</code>
 * originally implemented by Marc-Andre Laverdiere in the Crytolysis
 * and Ftklipse projects. It was later adapted to be an options provider
 * and brought to MARF by Serguei Mokhov.
 * </p>
 * 
 * <p>The class loads a configuration file with the format:
 * 
 * <pre>key=value #comment</pre>
 * 
 * WARNING: this is not a great option to keep passwords in memory, because
 * the data is kept as strings.</p>
 *  
 * @author Marc-Andre Laverdiere
 * @author Serguei Mokhov
 * 
 * @since 0.3.0.6
 * @version $Id: OptionFileLoader.java,v 1.3 2012/07/18 02:45:45 mokhov Exp $
 */
public class OptionFileLoader
implements IOptionProvider
{
	/**
	 * Singleton Instance.
	 */
	protected static OptionFileLoader soOptionsLoaderInstance = null;
	
	/**
	 * Options tracking in the form (key, value), both kept as strings.
	 */
	protected Hashtable<String, String> oHashOptionValuePairTracker;
	
	/**
	 * Indicates that the config was initialized.
	 */
	protected boolean bIsInitialized;
	
	/**
	 * Default config file name.
	 */
	protected static final String DEFAULT_CONFIG_FILE_NAME = ".config";
	
	/**
	 * Comment symbol.
	 */
	protected static final char COMMENT_SYMBOL = '#';
	
	/**
	 * Separator symbol.
	 */
	protected static final char SEPARATOR_SYMBOL = '=';
	
	/**
	 * Constructs an uninitialized options loader. 
	 */
	protected OptionFileLoader()
	{
		this.oHashOptionValuePairTracker = new Hashtable<String, String>();
		this.bIsInitialized = false;
	}
	
	/**
	 * Obtains singleton instance.
	 * @return a unique instance of the options loader
	 */
	public static synchronized OptionFileLoader getInstance()
	{
		if(soOptionsLoaderInstance == null)
		{
			soOptionsLoaderInstance = new OptionFileLoader();
		}
		
		return soOptionsLoaderInstance;
	}
	
	/**
	 * Loads default configuration file.
	 * @throws IOException on error reading file
	 */
	public void loadConfiguration()
	throws IOException
	{
		loadConfiguration(DEFAULT_CONFIG_FILE_NAME);
	}
	
	/**
	 * Loads specified configuration file.
	 * @param pstrFileName name of the configuration file
	 * @throws IOException on error reading file
	 */	
	public void loadConfiguration(final String pstrFileName)
	throws IOException
	{
		BufferedReader oReader = new BufferedReader(new FileReader(pstrFileName));

		while(oReader.ready())
		{
			String strLine = oReader.readLine();
			
			// For each line, skipping end of file, empty lines
			if(strLine != null && !strLine.equals(""))
			{
				strLine = strLine.trim(); //remove whitespace
				String strUncommented = this.removeComment(strLine); //remove comments
				
				// if not a comment, extract the key and the associated value
				if(strUncommented != null)
				{
					String strKey = this.getKey(strUncommented);
					String strValue = this.getValue(strUncommented);
//					System.out.println(strKey + "->" + strValue);
					this.oHashOptionValuePairTracker.put(strKey,strValue);
				}
			}
		}
		
		this.bIsInitialized = true;
	}
	
	/**
	 * Allows retrieving setting by a key.
	 * @param pstrKey the key
	 * @return the setting
	 */
	public String getConfigurationSetting(String pstrKey)
	{
		return this.oHashOptionValuePairTracker.get(pstrKey);
	}
	
	/**
	 * Removes the comment from the line
	 * @param pstrLine line of data
	 * @return null if only a comment, a string otherwise
	 */
	protected String removeComment(String pstrLine)
	{
		int iCommentPosition = pstrLine.indexOf(COMMENT_SYMBOL);

		if(iCommentPosition == -1) //no comment in line
		{
			return pstrLine;
		}
		else if(iCommentPosition == 0)
		{
			return null; //line is a comment
		}
		else
		{
			return pstrLine.substring(0, iCommentPosition);
		}
	}
	
	/**
	 * Obtains the key of the line.
	 * @param pstrLine line of data
	 * @return the string to the left of the separator
	 */	
	protected String getKey(String pstrLine)
	{
		int iCommentPosition = pstrLine.indexOf(SEPARATOR_SYMBOL);

		if(iCommentPosition == -1 || iCommentPosition == 0) //no key
		{
			return "";
		}
		else
		{
			return (pstrLine.substring(0, iCommentPosition)).trim();
		}
	}
	
	/**
	 * Obtains the value of the line.
	 * @param pstrLine line of data
	 * @return the string to the right of the separator
	 */	
	protected String getValue(String pstrLine)
	{
		int iCommentPosition = pstrLine.indexOf(SEPARATOR_SYMBOL);

		if(iCommentPosition == -1) //no value
		{
			return "";
		}
		else if(pstrLine.length() == iCommentPosition + 1)
		{
			// at the end of line
			return "";
		}
		else
		{
			return (pstrLine.substring(iCommentPosition + 1, pstrLine.length())).trim(); //the rest of the line
		}
	}	
	
	/**
	 * @return true if a configuration file was loaded, false otherwise.
	 */
	public boolean isInitialized()
	{
		return this.bIsInitialized;
	}


	public void addValidOption(final String pstrOptionString)
	{
		this.oHashOptionValuePairTracker.put(pstrOptionString, pstrOptionString);
	}
	
	public void addValidOption(final int piOptionCode, final String pstrOptionString)
	{
		addValidOption(pstrOptionString);
	}

	public void addValidOption(final int piOptionCode, final String pstrOptionString, boolean pbRequiresArgument)
	{
		throw new NotImplementedException();
	}
	
	public void addActiveOption(final String pstrOptionString)
	{
		throw new NotImplementedException();
	}
	
	public void addActiveOption(final int piOptionCode, final String pstrOptionString)
	{
		throw new NotImplementedException();
	}
	
	public void addActiveOption(final String pstrOptionString, final String pstrOptionValue)
	{
		throw new NotImplementedException();
	}
	
	/**
	 * @see marf.util.IOptionProvider#size()
	 */
	public int size()
	{
		return this.oHashOptionValuePairTracker.size();
	}

	public String[] getArgumentVector()
	{
		throw new NotImplementedException();
	}

	public String getArgumentString()
	{
		throw new NotImplementedException();
	}
	
	public int getOption(final String pstrOption)
	{
		throw new NotImplementedException();
	}
	public int getOption(final String pstrOption, boolean pbReturnOnError)
	{
		throw new NotImplementedException();
	}

	public String getOptionArgument(final String pstrOption, boolean pbReturnOnError)
	{
		throw new NotImplementedException();
	}
	
	public String getOption(int piOption)
	{
		throw new NotImplementedException();
	}
	public String getOption(int piOption, boolean pbReturnOnError)
	{
		throw new NotImplementedException();
	}
	
	public String getOptionArgument(final String pstrOption)
	{
		throw new NotImplementedException();
	}

	public String getOptionArgument(final int piOption)
	{
		throw new NotImplementedException();
	}
	public String getOptionArgument(final int piOption, boolean pbReturnOnError)
	{
		throw new NotImplementedException();
	}

	@Override
	public Vector<String> getInvalidOptions()
	{
		throw new NotImplementedException();
	}

	@Override
	public Hashtable<?, ?> getActiveOptions()
	{
		throw new NotImplementedException();
	}
	
	@Override
	public Hashtable<?, ?> getValidOptions()
	{
		throw new NotImplementedException();
	}

	public boolean isActiveOption(final String pstrOption)
	{
		throw new NotImplementedException();
	}
	public boolean isActiveOption(int piOption)
	{
		throw new NotImplementedException();
	}
	
	public boolean isValidOption(String pstrOption)
	{
		throw new NotImplementedException();
	}
	public boolean isValidOption(int piOption)
	{
		throw new NotImplementedException();
	}
	
	public boolean isInvalidOption(String pstrOption)
	{
		throw new NotImplementedException();
	}
}

// EOF