package marf.Storage;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

import marf.util.InvalidSampleFormatException;


/**
 * <p>Supported MARF Audio File Formats.</p>
 *
 * NOTE: this code is still experimental.<br />
 *
 * $Id: MARFAudioFileFormat.java,v 1.14 2007/12/23 06:29:46 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.14 $
 * @since 0.3.0.2
 */
public class MARFAudioFileFormat
extends AudioFileFormat
{
	/*
	 * --------------------------------------------------------
	 * Supported Sample File Formats Enumeration
	 * --------------------------------------------------------
	 */

	/**
	 * Unknown sample format.
	 */
	public static final int UNK    = -1;

	/**
	 * Indicates WAVE incoming sample file format.
	 */
	public static final int WAV    = 700;

	/**
	 * Indicates ULAW incoming sample file format.
	 */
	public static final int ULAW   = 701;

	/**
	 * Indicates MP3 incoming sample file format.
	 */
	public static final int MP3    = 702;

	/**
	 * Sine sample format.
	 * @since 0.3.0
	 */
	public static final int SINE   = 703;

	/**
	 * AIFF sample format.
	 * @since 0.3.0
	 */
	public static final int AIFF   = 704;

	/**
	 * AIFF-C sample format.
	 * @since 0.3.0
	 */
	public static final int AIFFC  = 705;

	/**
	 * AU sample format.
	 * @since 0.3.0
	 */
	public static final int AU     = 706;

	/**
	 * SND sample format.
	 * @since 0.3.0
	 */
	public static final int SND    = 707;

	/**
	 * MIDI sample format.
	 * @since 0.3.0
	 */
	public static final int MIDI   = 708;

	/**
	 * Custom (plug-in) sample format.
	 * @since 0.3.0.5
	 */
	public static final int CUSTOM = 709;

	/**
	 * Textual sample format.
	 * @since 0.3.0.6
	 */
	public static final int TEXT   = 710;

	/**
	 * Lowest possible sample format value.
	 * For boundaries check.
	 */
	private static final int LOWEST_FORMAT  = WAV;

	/**
	 * Highest possible sample format value.
	 * For boundaries check.
	 */
	private static final int HIGHEST_FORMAT = TEXT;

	/**
	 * Default sample array's size (1024).
	 * @since 0.3.0
	 */
	public static final int DEFAULT_SAMPLE_SIZE = 1024;

	/**
	 * Default sample chunk's size (128).
	 * @since 0.3.0
	 */
	public static final int DEFAULT_CHUNK_SIZE = 128;


	/*
	 * -------------------
	 * Data members
	 * -------------------
	 */

	/**
	 * Current sample's format.
	 */
	protected int iFormat = UNK;

	/**
	 * File type.
	 * Has to be added as the parent's is private.
	 */
	protected transient MARFAudioFileFormat.Type oType = null;

	/**
	 * Default constructor creates a WAVE-type format by default,
	 * which is PCM-signed, 16 bits, mono, little-endian.
	 * @throws InvalidSampleFormatException since 0.3.0.6 as implementation delegated to MARFAudioFileFormat(int)
	 * @see #MARFAudioFileFormat(int) 
	 */
	public MARFAudioFileFormat()
	throws InvalidSampleFormatException
	{
		this(WAV);
	}

	/**
	 * Constructs format instant given the format enumeration parameter
	 * and default AudioFormat parameters.
	 *
	 * @param piFormat the desired format instance.
	 * @throws InvalidSampleFormatException if the specified format cannot be set
	 * @since 0.3.0.6
	 *
	 * @see #setAudioFormat(int)
	 * @see	AudioFormat.Encoding#PCM_SIGNED
	 * @see	ISampleLoader#DEFAULT_FREQUENCY
	 * @see	ISampleLoader#DEFAULT_SAMPLE_BIT_SIZE
	 * @see	ISampleLoader#DEFAULT_CHANNELS
	 * @see	ISampleLoader#DEFAULT_FREQUENCY
	 */
	public MARFAudioFileFormat(int piFormat)
	throws InvalidSampleFormatException
	{
		this
		(
			Type.forFormatCode(piFormat),
			new AudioFormat
			(
				AudioFormat.Encoding.PCM_SIGNED,
				ISampleLoader.DEFAULT_FREQUENCY,
				ISampleLoader.DEFAULT_SAMPLE_BIT_SIZE,
				ISampleLoader.DEFAULT_CHANNELS,
				(ISampleLoader.DEFAULT_SAMPLE_BIT_SIZE / 8) * ISampleLoader.DEFAULT_CHANNELS,
				ISampleLoader.DEFAULT_FREQUENCY,
				false
			),
			ISampleLoader.DEFAULT_SAMPLE_BIT_SIZE
		);

		setAudioFormat(piFormat);
	}

	/**
	 * Mimics parent's constructor.
	 * @param poType file format
	 * @param poFormat audio format
	 * @param piFrameLength frame length
	 */
	public MARFAudioFileFormat(AudioFileFormat.Type poType, AudioFormat poFormat, int piFrameLength)
	{
		super(poType, poFormat, piFrameLength);
	}

	/**
	 * MARF-related constructor.
	 * @param poType file format
	 * @param poFormat audio format
	 * @param piFrameLength frame length
	 */
	public MARFAudioFileFormat(MARFAudioFileFormat.Type poType, AudioFormat poFormat, int piFrameLength)
	{
		super(poType, poFormat, piFrameLength);
	}

	/**
	 * Retrieves current sample's format.
	 * @return an integer representing the format of the sample
	 */
	public final int getAudioFormat()
	{
		return this.iFormat;
	}

	/**
	 * Sets current format of a sample.
	 * @param piFormat format number from the enumeration
	 * @return since 0.3.0.6 returns the resultant type.
	 * @throws InvalidSampleFormatException if piFormat is out of range
	 */
	public final MARFAudioFileFormat.Type setAudioFormat(final int piFormat)
	throws InvalidSampleFormatException
	{
		this.oType = Type.forFormatCode(piFormat);
		return this.oType;
	}

	/**
	 * Constructs an audio file format object.
	 * Mimics parent's protected constructor.
	 *
	 * @param poType type of the audio file
	 * @param piByteLength length of the file in bytes, or <code>AudioSystem.NOT_SPECIFIED</code>
	 * @param poFormat format of the audio data in the file
	 * @param piFrameLength the audio data length in sample frames, or <code>AudioSystem.NOT_SPECIFIED</code>
	 */
	protected MARFAudioFileFormat(Type poType, int piByteLength, AudioFormat poFormat, int piFrameLength)
	{
		super(poType, piByteLength, poFormat, piFrameLength);
	}

	/**
	 * In addition to the types defined in <code>AudioFileFormat.Type</code>
	 * defines MP3, MIDI, and ULAW formats and their extensions.
	 *
	 * @see javax.sound.sampled.AudioFileFormat.Type
	 */
	public static class Type
	extends AudioFileFormat.Type
	{
		/**
		 * Specifies a WAVE file.
		 */
		public static final Type WAVE = new Type("WAVE", "wav");

		/**
		 * Specifies an AU file.
		 */
		public static final Type AU = new Type("AU", "au");

		/**
		 * Specifies an AIFF file.
		 */
		public static final Type AIFF = new Type("AIFF", "aif");

		/**
		 * Specifies an AIFF-C file.
		 */
		public static final Type AIFC = new Type("AIFF-C", "aifc");

		/**
		 * Specifies a SND file.
		 */
		public static final Type SND = new Type("SND", "snd");

		/**
		 * Specifies MP3 file.
		 */
		public static final Type MP3 = new Type("MP3", "mp3");

		/**
		 * Specifies SINE file.
		 */
		public static final Type SINE = new Type("SINE", "sine");

		/**
		 * Specifies MIDI file.
		 */
		public static final Type MIDI = new Type("MIDI", "mid");

		/**
		 * Specifies ULAW file.
		 */
		public static final Type ULAW = new Type("ULAW", "ulaw");

		/**
		 * Specifies custom plug-in file.
		 * @since 0.3.0.5
		 */
		public static final Type CUSTOM = new Type("CUSTOM", "plugin");

		/**
		 * Specifies text file.
		 * @since 0.3.0.6
		 */
		public static final Type TEXT = new Type("TEXT", "txt");

		/**
		 * Mimics parent's constructor.
		 * @param pstrName format name
		 * @param pstrExtension typical file extension
		 */
		public Type(String pstrName, String pstrExtension)
		{
			super(pstrName, pstrExtension);
		}
		
		/**
		 * Given valid format code returns the corresponding Type instance.
		 *
		 * @param piFormat the desired format code
		 * @return the Type instance instance
		 * @since 0.3.0.6
		 * @throws InvalidSampleFormatException if the format code is out of range
		 * 
		 * @see MARFAudioFileFormat#LOWEST_FORMAT
		 * @see MARFAudioFileFormat#HIGHEST_FORMAT
		 */
		public static final Type forFormatCode(int piFormat)
		throws InvalidSampleFormatException
		{
			if((piFormat < LOWEST_FORMAT || piFormat > HIGHEST_FORMAT) && piFormat != UNK)
			{
				throw new InvalidSampleFormatException("Not a valid audio format: " + piFormat);
			}

			Type oType = null;
			
			switch(piFormat)
			{
				case MARFAudioFileFormat.WAV:
					oType = WAVE;
					break;

				case MARFAudioFileFormat.ULAW:
					oType = ULAW;
					break;

				case MARFAudioFileFormat.MP3:
					oType = MP3;
					break;

				case MARFAudioFileFormat.SINE:
					oType = SINE;
					break;

				case MARFAudioFileFormat.AIFF:
					oType = AIFF;
					break;

				case MARFAudioFileFormat.AIFFC:
					oType = AIFC;
					break;

				case MARFAudioFileFormat.AU:
					oType = AU;
					break;

				case MARFAudioFileFormat.SND:
					oType = SND;
					break;

				case MARFAudioFileFormat.MIDI:
					oType = MARFAudioFileFormat.Type.MIDI;
					break;

				case MARFAudioFileFormat.CUSTOM:
					oType = MARFAudioFileFormat.Type.CUSTOM;
					break;

				case MARFAudioFileFormat.TEXT:
					oType = MARFAudioFileFormat.Type.TEXT;
					break;
					
				default:
					assert false : "Impossible invalid sample format: " + piFormat;
			}
			
			return oType;
		}
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.14 $";
	}
}

// EOF
