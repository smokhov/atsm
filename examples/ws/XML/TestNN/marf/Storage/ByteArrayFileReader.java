package marf.Storage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

import marf.util.Arrays;
import marf.util.MARFRuntimeException;
import marf.util.NotImplementedException;


/**
 * <p>Reads in a file contents into a byte array buffers.</p>
 *
 * @author Serguei Mokhov
 * @version $Id: ByteArrayFileReader.java,v 1.7 2012/07/09 03:27:29 mokhov Exp $
 * @since 0.3.0.6
 */
public class ByteArrayFileReader
{
	/**
	 * Default number of reserved buffers.
	 */
	public static final int DEFAULT_NUMBER_OF_BUFFERS = 100;

	/**
	 * Default size of each buffer in bytes.
	 */
	public static final int DEFAULT_BUFFER_SIZE = 8192;


	/**
	 * Buffer of buffers to fill in.
	 */
	protected byte[][] attBuffers = new byte[DEFAULT_NUMBER_OF_BUFFERS][];

	/**
	 * Contained file size in bytes.
	 */
	protected int iFileSize = 0;

	/**
	 * Contained used buffers count.
	 */
	protected int iBufferCount = 0;

	/**
	 * Current buffer size.
	 */
	protected int iBufferSize = DEFAULT_BUFFER_SIZE;

	/**
	 * Constructs an empty reader.
	 */
	public ByteArrayFileReader()
	{
		super();
	}

	/**
	 * Constructs the reader with the contents of the file by specifying the filename.
	 * @param pstrFilename
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public ByteArrayFileReader(String pstrFilename)
	throws FileNotFoundException, IOException
	{
		this(new File(pstrFilename));
	}

	/**
	 * Constructs the reader with the contents of the file by specifying the File object.
	 * @param poFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public ByteArrayFileReader(File poFile)
	throws FileNotFoundException, IOException
	{
		read(poFile);
	}

	/**
	 * Constructs the reader with the contents of the file by specifying the URI.
	 * @param poURI
	 */
	public ByteArrayFileReader(URI poURI)
	{
		read(poURI);
	}

	/**
	 * Constructs the reader with the contents of the file by specifying the InputStream object.
	 * @param poInputStream
	 * @throws IOException
	 */
	public ByteArrayFileReader(InputStream poInputStream)
	throws IOException
	{
		read(poInputStream);
	}

	/**
	 * Constructs the reader with the contents of the file by specifying the Reader object.
	 * @param poReader
	 */
	public ByteArrayFileReader(Reader poReader)
	{
		read(poReader);
	}

	/**
	 * @param poFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public byte[] read(File poFile)
	throws FileNotFoundException, IOException
	{
		return read(new BufferedInputStream(new FileInputStream(poFile)));
	}

	/**
	 * @param poURI unused
	 * @return
	 * @throws NotImplementedException
	 */
	public byte[] read(URI poURI)
	{
		throw new NotImplementedException();
	}

	/**
	 * @param poInputStream
	 * @return
	 * @throws IOException
	 */
	public byte[] read(InputStream poInputStream)
	throws IOException
	{
		boolean bEOF = false;

		do
		{
			// Increment the capacity upon reaching the current one
			if(this.iBufferCount >= this.attBuffers.length)
			{
				byte[][] attNewBuffers = new byte[this.attBuffers.length + DEFAULT_NUMBER_OF_BUFFERS][];
				Arrays.copy(attNewBuffers, 0, this.attBuffers, 0, this.attBuffers.length);
				this.attBuffers = attNewBuffers;
			}

			this.attBuffers[this.iBufferCount] = new byte[this.iBufferSize];
			int iBytesRead = poInputStream.read(this.attBuffers[this.iBufferCount++]);

			if(iBytesRead < 0)
			{
				bEOF = true;
				break;
			}

			if(iBytesRead < this.iBufferSize)
			{
				this.iFileSize += this.iBufferSize - iBytesRead;
				bEOF = true;
				break;
			}

			this.iFileSize += this.iBufferSize;
		}
		while(bEOF == false);

		poInputStream.close();

		return toByteArray();
	}

	/**
	 * @param poReader
	 * @return
	 * @throws NotImplementedException
	 */
	public byte[] read(Reader poReader)
	{
		throw new NotImplementedException();
	}

	/**
	 * @return
	 */
	public byte[] toByteArray()
	{
		if(this.iFileSize == 0)
		{
			throw new MARFRuntimeException("No file has been read yet.");
		}

		byte[] atByteFile = new byte[this.iFileSize];

		for(int i = 0; i < this.iBufferCount; i++)
		{
			Arrays.copy
			(
				atByteFile,
				i * this.iBufferSize,
				this.attBuffers[i],
				0,

				i == this.iBufferCount - 1
					? this.iFileSize - i * this.iBufferSize
					: this.attBuffers[i].length
			);
		}

		return atByteFile;
	}

	public int getFileSize()
	{
		return this.iFileSize;
	}
}

// EOF
