package marf.util;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;


/**
 * <p>MARF Runnable Logging Facility.</p>
 * 
 * <p>All methods are properly synchronized should multiple threads
 * access the same logger.</p>
 *
 * $Id: Logger.java,v 1.10 2007/12/23 06:29:47 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.10 $
 * @since 0.3.0.2
 */
public class Logger
extends BaseThread
{
	/**
	 * Indicates to emit messages to three destinations: 
	 * a file, STDOUT, and STDERR.
	 */
	public final static int LOG_TO_FILE_STDOUT_STDERR = 0;

	/**
	 * Indicates to redirect STDOUT to a file.
	 */
	public final static int LOG_STDOUT_TO_FILE = 1;

	/**
	 * Indicates to redirect STDERR to a file.
	 */
	public final static int LOG_STDERR_TO_FILE = 2;

	/**
	 * Indicates to redirect STDOUT and STDERR to a file.
	 */
	public final static int LOG_STDOUT_STDERR_TO_FILE = 3;

	/**
	 * If set to <code>true</code>, no timestamp is issued with a message.
	 */
	protected boolean bNoTimestamp = false;

	/**
	 * Redirection filename.
	 */
	protected String strFilename;

	/**
	 * Indicates if logging is available.
	 */
	protected boolean bAvailable = true;

	/**
	 * Underlying LogPrintStream for messages and files. 
	 */
	protected LogPrintStream oLog;

	/**
	 * Log message to log.
	 */
	protected String strLogMessage = "";

	/**
	 * Indicates where the log should go.
	 * See the <code>LOG_*</code> constants for possible directions.
	 * @see #LOG_TO_FILE_STDOUT_STDERR
	 * @see #LOG_STDOUT_TO_FILE
	 * @see #LOG_STDERR_TO_FILE
	 * @see #LOG_STDOUT_STDERR_TO_FILE
	 */
	protected int iLogDirection;

	/**
	 * Takes the log filename and sets the direction
	 * to log to the file, STDERR, and STDOUT.
	 *  
	 * @param pstrFilename desired log filename
	 * @throws Exception
	 */
	public Logger(String pstrFilename)
	throws Exception
	{
		this(pstrFilename, LOG_TO_FILE_STDOUT_STDERR);
	}

	/**
	 * Creates a logger with output filename and desired direction.
	 * Timestamps are always added.
	 * 
	 * @param pstrFilename desired log filename
	 * @param piLogDirection desired logging direction
	 * @throws Exception
	 * 
	 * @see #LOG_TO_FILE_STDOUT_STDERR
	 * @see #LOG_STDOUT_TO_FILE
	 * @see #LOG_STDERR_TO_FILE
	 * @see #LOG_STDOUT_STDERR_TO_FILE
	 */
	public Logger(String pstrFilename, int piLogDirection)
	throws Exception
	{
		this(pstrFilename, piLogDirection, true);
	}

	/**
	 * Creates a logger with output filename, desired direction,
	 * and possibly a timestamp.
	 * 
	 * @param pstrFilename desired log filename
	 * @param piLogDirection desired logging direction
	 * @param pbTimestampRequired <code>true</code> if a timestamp desired in logs
	 * @throws Exception
	 * 
	 * @see #LOG_TO_FILE_STDOUT_STDERR
	 * @see #LOG_STDOUT_TO_FILE
	 * @see #LOG_STDERR_TO_FILE
	 * @see #LOG_STDOUT_STDERR_TO_FILE
	 */
	public Logger(String pstrFilename, int piLogDirection, boolean pbTimestampRequired)
	throws Exception
	{
		Debug.debug("Logger type " + piLogDirection + " with file " + pstrFilename + " requested.");

		setLogDirection(piLogDirection);
		this.strFilename = pstrFilename;
		enableTimestamp(pbTimestampRequired);

		this.oLog = new LogPrintStream(pstrFilename);

		switch(piLogDirection)
		{
			case LOG_TO_FILE_STDOUT_STDERR:
			{
				System.setOut(new LogPrintStream(System.out, oLog));
				System.setErr(new LogPrintStream(System.err, oLog));
				break;
			}

			case LOG_STDOUT_STDERR_TO_FILE:
			{
				System.setOut(this.oLog);
				System.setErr(this.oLog);
				break;
			}

			case LOG_STDOUT_TO_FILE:
			{
				System.setOut(this.oLog);
				break;
			}

			case LOG_STDERR_TO_FILE:
			{
				System.setErr(this.oLog);
				break;
			}

			default:
			{
				throw new NotImplementedException("Logger type " + piLogDirection + " not implemented.");
			}
		}

		Debug.debug("Logger type " + piLogDirection + " with file " + pstrFilename + " created.");
	}

	/**
	 * Our own LogPrintStream for log delivery.
	 * @author Serguei Mokhov
	 */
	public class LogPrintStream
	extends PrintStream
	{
		/**
		 * Internal instance of log stream that might point to a file,
		 * STDOUT or STDERR.
		 */
		protected LogPrintStream oLPS;

		/**
		 * Default output is to a file.
		 * @param pstrFilename filename of a file to log to
		 * @throws Exception if the are problems with file operations
		 */
		public LogPrintStream(String pstrFilename)
		throws Exception
		{
			// java 1.5: super(pstrFilename);

			// workaround for java 1.4
			// since 0.3.0.6 appends
			super(new FileOutputStream(pstrFilename, true), false);

			this.oLPS = null;
		}

		/**
		 * Defaults output to the stream specified in the parameter.
		 * @param poOutputStream stream to log to
		 */
		public LogPrintStream(OutputStream poOutputStream)
		{
			super(poOutputStream);
			this.oLPS = null;
		}

		/**
		 * Sets the output to go to the two destinations of the first
		 * and second parameters.
		 * @param poOutputStream possibly a file or STDOUT/STDERR
		 * @param poLogPrintStream possibly a file or STDOUT/STDERR
		 */
		public LogPrintStream(OutputStream poOutputStream, LogPrintStream poLogPrintStream)
		{
			super(poOutputStream);
			this.oLPS = poLogPrintStream;
		}

		/**
		 * Issues a log message to the parent and possibly contained instance of itself.
		 * @param pstrMessage log message to log
		 * @see java.io.PrintStream#println(java.lang.String)
		 */
		public synchronized void println(String pstrMessage)
		{
			StringBuffer strMsg = new StringBuffer();
			
			if(bNoTimestamp == false)
			{
				strMsg.append("[").append(new Date()).append("]: ");
			}
			
			strMsg.append(pstrMessage);

			super.println(strMsg);

			if(this.oLPS != null)
			{
				// Using pstrMessage instead of strMsg to avoid double timestamps
				this.oLPS.println(pstrMessage);
			}
		}

		/**
		 * Object to log.
		 * @see java.io.PrintStream#println(java.lang.Object)
		 */
		public synchronized void println(Object poMessage)
		{
			println(poMessage.toString());
		}
	} // LogPrintStream

	/**
	 * Verifies if the timestamps are enabled.
	 * @return Returns the bNoTimestamp.
	 */
	public synchronized boolean isTimestampEnabled()
	{
		return (this.bNoTimestamp == false);
	}

	/**
	 * Enables or disables timestamps.
	 * @param pbEnable <code>true</code> to enable timestamping
	 */
	public synchronized void enableTimestamp(boolean pbEnable)
	{
		this.bNoTimestamp = !pbEnable;
	}
	
	/**
	 * @return returns the log direction.
	 */
	public synchronized int getLogDirection()
	{
		return this.iLogDirection;
	}

	/**
	 * Sets new log direction.
	 * @param piLogDirection The iLogDirection to set.
	 * @throws IllegalArgumentException if the direction parameter is out of range
	 */
	public synchronized void setLogDirection(int piLogDirection)
	{
		if(piLogDirection < LOG_TO_FILE_STDOUT_STDERR || piLogDirection > LOG_STDOUT_STDERR_TO_FILE)
		{
			throw new IllegalArgumentException
			(
				"Log direction should be between [" +
				LOG_TO_FILE_STDOUT_STDERR + "," +
				LOG_STDOUT_STDERR_TO_FILE +"]"
			);
		}
			
		this.iLogDirection = piLogDirection;
	}

	/**
	 * Returns references to the internal log stream.
	 * @return LogPrintStream reference
	 */
	public synchronized LogPrintStream getLogPrintStream()
	{
		return this.oLog;
	}

	/**
	 * Allows to set new log stream.
	 * @param poLogPrintStream the new stream to set.
	 */
	public synchronized void setLogPrintStream(LogPrintStream poLogPrintStream)
	{
		this.oLog = poLogPrintStream;
	}

	/**
	 * @return returns the log filename.
	 */
	public synchronized String getLogFilename()
	{
		return this.strFilename;
	}

	/**
	 * @param pstrFilename the log filename to set.
	 */
	public synchronized void setLogFilename(String pstrFilename)
	{
		this.strFilename = pstrFilename;
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.10 $";
	}
}

// EOF
