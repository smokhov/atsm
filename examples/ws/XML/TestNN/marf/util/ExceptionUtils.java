package marf.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;


/**
 * <p>Common exception utility methods used by all the branches
 * of the MARF Exceptions hierarchy.
 * </p>
 * 
 * $Id: ExceptionUtils.java,v 1.2 2007/12/15 17:46:07 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @since 0.3.0.6
 * @version $Revision: 1.2 $
 */
public class ExceptionUtils
{
	/**
	 * Allow inheritance, but other than that, no need
	 * to instantiate as all methods in here are presumed
	 * to be static.
	 */
	protected ExceptionUtils()
	{
	}

	/**
	 * Writes out the stack trace into a string and returns it
	 * along with the supplied message.
	 * @param pstrMessage custom exception message to prepend to the output
	 * @param poException
	 * @return exception message string with a stack trace
	 */
	public static String getStackTraceAsString(String pstrMessage, Exception poException)
	{
		String strMessage = "";

	 	// Based on PostgreSQL JDBC driver's PGSQLException.
		try
		{
			ByteArrayOutputStream oByteArrayOutputStream = new ByteArrayOutputStream();
			PrintWriter oPrintWriter = new PrintWriter(oByteArrayOutputStream);
	
			oPrintWriter.println("Exception: " + poException + "\nStack Trace:\n");
			poException.printStackTrace(oPrintWriter);
			oPrintWriter.println("End of Stack Trace");
	
			strMessage += pstrMessage + " " + oByteArrayOutputStream;
	
			oPrintWriter.println(strMessage);
	
			oPrintWriter.flush();
			oPrintWriter.close();
	
			oByteArrayOutputStream.close();
		}
		catch(Exception e)
		{
			strMessage +=
				pstrMessage + " " +
				poException +
				"\nError on stack trace generation: " +
				e;
		}
		
		return strMessage;
	}
}

// EOF
