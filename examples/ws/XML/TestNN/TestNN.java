import java.util.NoSuchElementException;
import java.util.Random;

import marf.Classification.NeuralNetwork.NeuralNetwork;
import marf.util.Arrays;
import marf.util.Debug;
import marf.util.OptionProcessor;


/**
 * <p>Tests Neural Network MARF Module.</p>
 *
 * $Id: TestNN.java,v 1.9 2006/02/25 07:05:44 mokhov Exp $
 *
 * @version 0.3.0, $Revision: 1.9 $
 * @since 0.2.0 of MARF
 *
 * @author Ian Clement
 * @author Serguei Mokhov
 */
public class TestNN
{
	/*
	 * ----------------
	 * App's Versioning
	 * ----------------
	 */

	/**
	 * Current major version of the application.
	 * @since 0.3.0.5
	 */
	public static final int MAJOR_VERSION = 0;

	/**
	 * Current minor version of the application.
	 * @since 0.3.0.5
	 */
	public static final int MINOR_VERSION = 3;
	
	/**
	 * Current revision of the application.
	 * @since 0.3.0.5
	 */
	public static final int REVISION      = 0;

	/*
	 * -------------------
	 * Options Enumeration
	 * -------------------
	 */

	/**
	 * Option number for "--help".
	 * @since 0.3.0.5
	 */
	public static final int OPT_HELP_LONG        = 10;

	/**
	 * Option number for "-h".
	 * @since 0.3.0.5
	 */
	public static final int OPT_HELP_SHORT       = 11;

	/**
	 * Option number for "--version".
	 * @since 0.3.0.5
	 */
	public static final int OPT_VERSION          = 12;

	/**
	 * Option number for "--debug".
	 * @since 0.3.0.5
	 */
	public static final int OPT_DEBUG            = 13;

	/**
	 * Option number for "-dtd".
	 * @since 0.3.0.5
	 */
	public static final int OPT_VALIDATE_DTD     = 14;

	/**
	 * Option number for "-error".
	 * @since 0.3.0.5
	 */
	public static final int OPT_MIN_ERROR        = 15;

	/**
	 * Option number for "-epochs".
	 * @since 0.3.0.5
	 */
	public static final int OPT_EPOCHS           = 16;

	/**
	 * Option number for "-train".
	 * @since 0.3.0.5
	 */
	public static final int OPT_TRAIN_ITERATIONS = 17;

	/**
	 * Option number for "-test".
	 * @since 0.3.0.5
	 */
	public static final int OPT_TEST_ITERATIONS  = 18;


	/**
	 * The main thread of the application.
	 * @param argv command-line arguments
	 */
	public static final void main(String[] argv)
	{
		try
		{
			/*
			 * Get our options right
			 */
			OptionProcessor oGetOpt = new OptionProcessor();

			oGetOpt.addValidOption(OPT_HELP_LONG, "--help");
			oGetOpt.addValidOption(OPT_HELP_SHORT, "-h");
			oGetOpt.addValidOption(OPT_VERSION, "--version");
			oGetOpt.addValidOption(OPT_DEBUG, "--debug");

			oGetOpt.addValidOption(OPT_VALIDATE_DTD, "-dtd");

			// These options require an argument
			oGetOpt.addValidOption(OPT_MIN_ERROR, "-error", true);
			oGetOpt.addValidOption(OPT_EPOCHS, "-epochs", true);
			oGetOpt.addValidOption(OPT_TRAIN_ITERATIONS, "-train", true);
			oGetOpt.addValidOption(OPT_TEST_ITERATIONS, "-test", true);

			oGetOpt.parse(argv);

			// Enable debugging if requested
			if(oGetOpt.isActiveOption(OPT_DEBUG))
			{
				Debug.enableDebug();
			}

			// Generalities
			if(oGetOpt.getInvalidOptions().size() > 1)
			{
				System.err.println("Invalid options found: " + oGetOpt.getInvalidOptions());
				usage();
				System.exit(1);
			}
			else if(oGetOpt.isActiveOption(OPT_HELP_LONG) || oGetOpt.isActiveOption(OPT_HELP_SHORT))
			{
				usage();
				System.exit(0);
			}
			else if(oGetOpt.isActiveOption(OPT_VERSION))
			{
				System.out.println("TestNN Application, v." + getVersion());
				System.out.println("Using MARF, v." + marf.Version.getStringVersion());
				System.exit(0);
			}

			/*
			 * Validate version compatibility of the app and MARF
			 */
			marf.Version.validateVersions(0 * 100 + 3 * 10 + 0 + .5);

			// Set initial parameters
			String strFilename = oGetOpt.getInvalidOptions().firstElement().toString();

			NeuralNetwork oNNet = new NeuralNetwork(null);

			// Defaults
			boolean bDTDValidate = false;

			int iEpochNum = 1;
			int iTrainNum = 1;
			int iTestNum = 1;

			double dMinErr = 1.0;
	
			// Customizations based on options
			if(oGetOpt.isActiveOption(OPT_VALIDATE_DTD))
			{
				bDTDValidate = true;
			}

			if(oGetOpt.isActiveOption(OPT_MIN_ERROR))
			{
				dMinErr = Double.parseDouble(oGetOpt.getOptionArgument(OPT_MIN_ERROR));
			}

			if(oGetOpt.isActiveOption(OPT_EPOCHS))
			{
				iEpochNum = Integer.parseInt(oGetOpt.getOptionArgument(OPT_EPOCHS));
			}

			if(oGetOpt.isActiveOption(OPT_TRAIN_ITERATIONS))
			{
				iTrainNum = Integer.parseInt(oGetOpt.getOptionArgument(OPT_TRAIN_ITERATIONS));
			}

			if(oGetOpt.isActiveOption(OPT_TEST_ITERATIONS))
			{
				iTestNum = Integer.parseInt(oGetOpt.getOptionArgument(OPT_TEST_ITERATIONS));
			}

			// Actual processing
			System.out.println("==============================");
			System.out.println("Loading " + strFilename + " NNet...");
			System.out.println("==============================");

			oNNet.initialize(strFilename, bDTDValidate);

			System.out.println("==============================");
			System.out.println("Printing Resultant XML NNet...");
			System.out.println("==============================");

			// Default is in XML format
			oNNet.dumpXML(strFilename + ".1");
	
			System.out.println("==============================");
			System.out.println("Training NNet...");
			System.out.println("==============================");
	
			Random oRand = new Random();
	
			double[] adTrainIn = {0.0, 0.0, 0.0, 0.0, 0.0};
			double[] adExpected = {0.0};
	
			// Testing
			double[] adToSend = {0.0, 0.0, 0.0, 0.0, 0.0};
			
			double dExp = 0.0;
			double dError = 1.0;
	
			int iLimit = 0;
	
			while(dError > dMinErr && iLimit < iEpochNum)
			{
				Debug.debug("Epoch: " + iLimit);
	
				for(int i = 0; i < iTrainNum; i++)
				{
					Debug.debug("------ " + i + " ------");
	
					for(int j = 0; j < 5; j++)
					{
						if(oRand.nextDouble() >= 0.5)
						{
							adTrainIn[j] = 1.0;
						}
						else
						{
							adTrainIn[j] = 0.0;
						}
					}

					Debug.debug("Input vector: [" + Arrays.arrayToCSV(adTrainIn) + "]");
	
					if
					(
						((adTrainIn[1] > 0.5) && (adTrainIn[2] > 0.5)) ||
						((adTrainIn[2] > 0.5) && (adTrainIn[3] > 0.5))
					)
					{
						adExpected[0] = 1.0;
					}
					else
					{
						adExpected[0] = 0.0;
					}
	
					Debug.debug("Expected vector: [" + Arrays.arrayToCSV(adExpected) + "]");
					
					oNNet.train(adTrainIn, adExpected.length, 0.2);
				}
	
				oNNet.commit();

				int iCount = 0;
	
				for(iCount = 0; iCount < iTestNum; iCount++)
				{
					for(int j = 0; j < 5; j++)
					{
						if(oRand.nextDouble() >= 0.5)
						{
							adToSend[j] = 1.0;
						}
						else
						{
							adToSend[j] = 0.0;
						}
					}

					Debug.debug("Vector to send: [" + Arrays.arrayToCSV(adTrainIn) + "]");

					if
					(
						((adToSend[1] > 0.5) && (adToSend[2] > 0.5)) ||
						((adToSend[2] > 0.5) && (adToSend[3] > 0.5))
					)
					{
						dExp = 1.0;
					}
					else
					{
						dExp = 0.0;
					}

					Debug.debug("Expected error: [" + dExp + "]");

					oNNet.setInputs(adToSend);
					oNNet.eval();

					double[] adResults = oNNet.getOutputResults();
	
					Debug.debug("Results: [" + Arrays.arrayToCSV(adResults) + "]");

					if(adResults[0] >= 0.5)
					{
						Debug.debug("Got: >= 0.5");
						dError += dMinErr * Math.abs(dExp - 1.0);
					}
					else
					{
						Debug.debug("Got: <= 0.5");
						dError += dMinErr * dExp;
					}
				}
	
				dError /= iCount;
				iLimit++;
			}
	
			System.out.println("==============================");
			System.out.println("Dumping Resultant XML NNet...");
			System.out.println("==============================");
	
			oNNet.dumpXML(strFilename + ".2");

			System.out.println("==============================");
			System.out.println("Summary");
			System.out.println("==============================");
	
			System.out.println("Epochs: " + iLimit + " of " + iEpochNum);
			System.out.println("Train: " + iTrainNum + ", Test: " + iTestNum);
	
			dError *= 100;
			dMinErr *= 100;

			System.out.println("The error is: " + dError + "%, where desired minimum was " + dMinErr + "%");
		}
		catch(NoSuchElementException e)
		{
			System.err.println("Missing required argument --- filename of an .xml document.");
			usage();
			System.exit(1);
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	/**
	 * Displays usage information into STDOUT. 
	 * @since 0.3.0.5
	 */
	private static final void usage()
	{
		System.out.println
		(
			"Usage:\n" +
			"    java TestNN [ OPTIONS ] <sample-xml-file>\n" +
			"    java TestNN --help | -h | --version\n" +
			"    java -jar TestNN.jar [ OPTIONS ] <sample-xml-file>\n\n" +
			"    java -jar TestNN.jar --help | -h | --version\n\n" +

			"Options (one or more of the following):\n" +
			"    --debug       - enable debugging\n" +
			"    -dtd          - validate the DTD of the corresponding XML document\n" +
			"    -error=VALUE  - specify minimum desired error\n" +
			"    -epochs=VALUE - specify numer of epochs for training\n" +
			"    -train=NUMBER - number of training interations\n" +
			"    -test=NUMBER  - number of testing iterations\n"
		);
	}

	/**
	 * Retrieves String representation of the application's version.
	 * @since 0.3.0.5
	 * @return version String
	 */
	public static final String getVersion()
	{
		return MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION;
	}
}

// EOF
