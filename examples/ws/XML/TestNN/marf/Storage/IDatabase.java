package marf.Storage;

import java.io.Serializable;


/**
 * <p>Subject database interface.</p>
 * 
 * $Id: IDatabase.java,v 1.8 2006/08/11 19:19:59 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.8 $
 * @since 0.3.0.2
 */
public interface IDatabase
extends Serializable
{
	/**
	 * Interface source code revision.
	 */
	String MARF_INTERFACE_CODE_REVISION = "$Revision: 1.8 $";

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.5
	 */
	long serialVersionUID = 3425923890218987256L;

	/**
	 * Given ID, fetches the corresponding filename.
	 * Retrieves Speaker's ID by a sample filename.
	 * 
	 * @param pstrFileName Name of a .wav file for which ID must be returned
	 * @param pbTraining indicates whether the filename is a training (<code>true</code>) sample or testing (<code>false</code>)
	 * 
	 * @return int ID, -1 if not found
	 * @throws StorageException
	 */
	int getIDByFilename(String pstrFileName, boolean pbTraining)
	throws StorageException;

	/**
	 * Retrieves subject's name by their ID.
	 * @param piID ID of a subject in the DB to return a name for
	 * @return name string
	 * @throws StorageException
	 */
	String getName(int piID)
	throws StorageException;

	/**
	 * Connects to the database of subjects.
	 * @throws StorageException
	 */
	void connect()
	throws StorageException;

	/**
	 * Retrieves subject's data from the database and populates
	 * internal data structures.
	 * @throws StorageException
	 */
	void query()
	throws StorageException;

	/**
	 * Closes (file) database connection.
	 * @throws StorageException
	 */
	void close()
	throws StorageException;
}

// EOF
