package marf.Storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import marf.util.BaseThread;


/**
 * <p>Reads data from the input channel and writes to the output stream.</p>
 *
 * $Id: SampleRecorder.java,v 1.6 2006/03/13 15:11:28 mokhov Exp $
 *
 * @author Jimmy Nicolacopoulos
 * @since 0.3.0.6
 */
public class SampleRecorder
extends BaseThread
{
	private TargetDataLine line = null;
	private boolean stopped = false;
	private Sample oSample = null;
	static int threadCount = 0;

	public SampleRecorder(Sample pSample)
	{
		oSample = pSample;
		this.setName("Recorder" + threadCount++);
	}

	public void stopRecording()
	{
		stopped = true;
	}

	public void run()
	{
		// define the required attributes for our line,
		// and make sure a compatible line is supported.
		stopped = false;
		AudioFormat format = oSample.getAudioFormat();

		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

		if (!AudioSystem.isLineSupported(info))
		{
			System.err.println("Line matching " + info + " not supported.");
			return;
		}

		// get and open the target data line for capture.

		try
		{
			line = (TargetDataLine)AudioSystem.getLine(info);
			line.open(format, line.getBufferSize());
		}
		catch(LineUnavailableException ex)
		{
			System.err.println(ex.toString());
			return;
		}
		catch(SecurityException ex)
		{
			System.err.println(ex.toString());
			return;
		}
		catch(Exception ex)
		{
			System.err.println(ex.toString());
			return;
		}

		// play back the captured audio data
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int frameSizeInBytes = format.getFrameSize();
		int bufferLengthInFrames = line.getBufferSize() / 8;
		int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
		byte[] data = new byte[bufferLengthInBytes];
		int numBytesRead;

		line.start();

		while(!stopped)
		{
			if((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1)
			{
				break;
			}

			out.write(data, 0, numBytesRead);
		}

		// we reached the end of the stream.  stop and close the line.
		line.stop();
		line.close();
		line = null;

		// stop and close the output stream
		try
		{
			out.flush();
			out.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}

		// load bytes into the audio input stream for playback

		byte audioBytes[] = out.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
		
		/* TODO in the recorder itself
		AudioInputStream oAudioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);
		oSample.setAudioInputStream(oAudioInputStream);
		*/

		try
		{
			//oSample.reset();
			oSample.resetArrayMark();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return;
		}

	}
} // End class SampleRecorder

// EOF
