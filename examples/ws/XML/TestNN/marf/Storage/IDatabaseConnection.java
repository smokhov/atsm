package marf.Storage;

import marf.Configuration;


/**
 * Abstracts connections to the database.
 * Can be Readers, custom classes, relational database connectors
 * or whatever represents the data source.
 * 
 * @author Serguei Mokhov
 * @since 0.3.0.6; May 30, 2010
 * @version $Id: IDatabaseConnection.java,v 1.2 2010/05/31 08:17:10 mokhov Exp $
 */
public interface IDatabaseConnection
{
	void open()
	throws StorageException;
	
	void initialize(String pstrFilename)
	throws StorageException;
	
	void close()
	throws StorageException;
	
	void setConfiguration(Configuration poConfiguration);
	
	// legacy? get the data and present it in a CSV form
	// no matter what the source is?
	String readLine()
	throws StorageException;
}

// EOF
