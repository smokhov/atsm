package marf.Classification.NeuralNetwork;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import marf.MARF;
import marf.Classification.Classification;
import marf.Classification.ClassificationException;
import marf.FeatureExtraction.IFeatureExtraction;
import marf.Storage.ITrainingSample;
import marf.Storage.Result;
import marf.Storage.StorageException;
import marf.util.Debug;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * <p>Artificial Neural Network-based Classifier.</p>
 *
 * @author Ian Clement
 * @author Serguei Mokhov
 *
 * @version $Id: NeuralNetwork.java,v 1.64 2012/07/09 03:53:32 mokhov Exp $
 * @since 0.0.1
 */
public class NeuralNetwork
extends Classification
{
	/*
	 * ----------------------
	 * Enumeration
	 * ----------------------
	 */

	/**
	 * How many binary Neurons in the output layer.
	 * Presumably int (our ID) is 4 bytes, hence 4 * 8 = 32 bits, and so many outputs.
	 * @since 0.2.0
	 */
	public static final int DEFAULT_OUTPUT_NEURON_BITS = 32;

	/**
	 * Default training constant of <code>1</code> if none supplied.
	 * @since 0.2.0
	 */
	public static final double DEFAULT_TRAINING_CONSTANT = 1;

	/**
	 * Default number of epoch iterations of <code>64</code> if none supplied.
	 * @since 0.2.0
	 */
	public static final int DEFAULT_EPOCH_NUMBER = 64;

	/**
	 * Default minimum training error of <code>0.1</code> if none supplied.
	 * @since 0.2.0
	 */
	public static final double DEFAULT_MIN_ERROR = 0.1;


	/*
	 * ----------------------
	 * Data Members
	 * ----------------------
	 */

	/**
	 * Collection of layers.
	 */
	private ArrayList<Layer> oLayers = new ArrayList<Layer>();

	/**
	 * Current layer.
	 */
	private transient Layer oCurrentLayer;

	/**
	 * Current layer's #.
	 */
	private transient int iCurrenLayer = 0;

	/**
	 * Number of the buffered layer.
	 */
	private transient int iCurrLayerBuf = 0;

	/**
	 * Current Neuron.
	 */
	private transient Neuron oCurrNeuron;

	/**
	 * Neuron Type.
	 */
	private transient int iNeuronType = Neuron.UNDEF;

	/**
	 * Input layer.
	 */
	private Layer oInputs = new Layer();

	/**
	 * Output layer.
	 */
	private Layer oOutputs = new Layer();

	/* Constants used for JAXP 1.2 */

	/**
	 * All output will use this encoding.
	 */
	public static final String OUTPUT_ENCODING = "UTF-8";

	/**
	 * JAXP 1.2 Schema.
	 */
	public static final String JAXP_SCHEMA_LANGUAGE =
		"http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	/**
	 * XML 2001 Schema.
	 */
	public static final String W3C_XML_SCHEMA =
		"http://www.w3.org/2001/XMLSchema";

	/**
	 * JAXP 1.2 Schema URL.
	 */
	public static final String JAXP_SCHEMA_SOURCE =
		"http://java.sun.com/xml/jaxp/properties/schemaSource";

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = 6116721242820120028L;


	/*
	 * ----------------------
	 * Methods
	 * ----------------------
	 */

	/**
	 * NeuralNetwork Constructor.
	 * @param poFeatureExtraction FeatureExtraction module reference
	 */
	public NeuralNetwork(IFeatureExtraction poFeatureExtraction)
	{
		super(poFeatureExtraction);
		//this.oObjectToSerialize = this;
//		this.iCurrentDumpMode = DUMP_XML;
		this.iCurrentDumpMode = DUMP_GZIP_BINARY;
//		this.iCurrentDumpMode = DUMP_BINARY;
		this.strFilename = getDefaultFilename();
	}

	/* Classification API */

	/**
	 * Implements training of Neural Net.
	 * @return <code>true</code> if training was successful
	 * @throws ClassificationException if there are any errors
	 * @throws NullPointerException if module parameters are incorrectly set
	 */
	public final boolean train()
	throws ClassificationException
	{
		return trainImplementation(null);
	}

	/**
	 * Implements training of Neural Net given the feature vector.
	 * @param padFeatureVector the feature vector to train on
	 * @return <code>true</code> if training was successful
	 * @throws ClassificationException if there are any errors
	 * @throws NullPointerException if module parameters are incorrectly set
	 * @since 0.3.0.6
	 */
	public final boolean train(double[] padFeatureVector)
	throws ClassificationException
	{
		return trainImplementation(padFeatureVector);
	}

	/**
	 * Implements training of Neural Net.
	 * @param padFeatureVector the feature vector to train on; if null pipelining mode is used
	 * @return <code>true</code> if training was successful
	 * @throws ClassificationException if there are any errors
	 * @throws NullPointerException if module parameters are incorrectly set
	 * @since 0.3.0.6
	 */
	private final boolean trainImplementation(double[] padFeatureVector)
	throws ClassificationException
	{
		Vector<ITrainingSample> oTrainingSamples = null;
		Vector<Serializable> oParams = null;

		try
		{
			/*
			 * Get newly coming feature vector into the TrainingSet cluster
			 * in case it's not there.
			 */
			int iBackupDumpMode = this.iCurrentDumpMode;
//			this.iCurrentDumpMode = DUMP_BINARY;
			this.iCurrentDumpMode = DUMP_GZIP_BINARY;

			if(padFeatureVector == null)
			{
				super.train();
			}
			else
			{
				super.train(padFeatureVector);
			}

			this.iCurrentDumpMode = iBackupDumpMode;

			// Defaults
			double dTrainConst = DEFAULT_TRAINING_CONSTANT;
			int    iEpochNum   = DEFAULT_EPOCH_NUMBER;
			double dMinErr     = DEFAULT_MIN_ERROR;

			// Defaults can be overridden by an app
			if(MARF.getModuleParams() != null)
			{
				oParams = MARF.getModuleParams().getClassificationParams();

				if(oParams.size() > 1)
				{
					// May throw NullPointerException
					dTrainConst = ((Double)oParams.elementAt(1)).doubleValue();
					iEpochNum   = ((Integer)oParams.elementAt(2)).intValue();
					dMinErr     = ((Double)oParams.elementAt(3)).doubleValue();
				}
			}

			// Reload training data from the disk if there was any
			restore();

			// Get the Training set...
			oTrainingSamples = this.oTrainingSet.getClusters();

			// Set initial values to always enter the epoch training loop
			int    iLimit = 0;
			double dError = dMinErr + 1;

			// Epoch training
			while(dError > dMinErr && iLimit < iEpochNum)
			{
				// Execute the training for each training cluster of utterances
				for(int i = 0; i < oTrainingSamples.size(); i++)
				{
//					Cluster oCluster = (Cluster)oTrainingSamples.get(i);
					ITrainingSample oTrainingSample = (ITrainingSample)oTrainingSamples.get(i);
					train(oTrainingSample.getMeanVector(), oTrainingSample.getSubjectID(), dTrainConst);

					// Commit modified weight
					commit();
				}

				// Test new values and calc error...
				// TODO: Testing is done with the same training samples :-(
				int iCount = 0;
				dError = 0.0;

				for(iCount = 0; iCount < oTrainingSamples.size(); iCount++)
				{
//					Cluster oCluster = (Cluster)oTrainingSamples.get(iCount);
					ITrainingSample oTrainingSample = (ITrainingSample)oTrainingSamples.get(iCount);

					// XXX: can be median and feature vectors
					setInputs(oTrainingSample.getMeanVector());
					runNNet();

					int iID = interpretAsBinary();

//					dError += Math.abs(oCluster.getSubjectID() - iID);
					dError += dMinErr * Math.abs(oTrainingSample.getSubjectID() - iID);

					Debug.debug("Expected: " + oTrainingSample.getSubjectID() + ", Got: " + iID + ", Error: " + dError);
				}

				if(iCount == 0)
				{
					throw new ClassificationException("NeuralNetwork.train() --- There are no training samples!");
				}

				dError /= iCount;
				iLimit++;

				Debug.debug("Epoch: error = " + dError + ", limit = " + iLimit);
			}

			dump();

			return true;
		}
		catch(StorageException e)
		{
			e.printStackTrace(System.err);

			throw new ClassificationException
			(
				"StorageException while dumping/restoring neural net: " +
				e.getMessage(), e
			);
		}
		catch(NullPointerException e)
		{
			e.printStackTrace(System.err);

			throw new ClassificationException
			(
				"NeuralNetwork.train(): Missing required ModuleParam (" + oParams +
				") or TrainingSample (" + oTrainingSamples + ")"
			);
		}
	}

	
	/**
	 * Neural Network implementation of classification routine.
	 * In 0.3.0.6 the generic pipelined version of this API
	 * <code>classify()</code> was refactored into the
	 * <code>Classification</code>.
	 *
	 * @return <code>true</code> upon successful classification
	 *
	 * @throws ClassificationException when input feature vector
	 * length does not match the size of the input neuron layer or
	 * if there was a StorageException during dump/restore.
	 * 
	 * @since 0.3.0.6
	 */
	public final boolean classify(double[] padFeatureVector)
	throws ClassificationException
	{
		try
		{
//			double[] adFeatures = this.oFeatureExtraction.getFeaturesArray();
			double[] adFeatures = padFeatureVector;

			// Reload trained net
			restore();

			if(adFeatures.length != this.oInputs.size())
			{
				throw new ClassificationException
				(
					"Input array size (" + adFeatures.length +
					") not consistent with input layer (" + this.oInputs.size() + ")"
				);
			}

			// Set the incoming features to the net's inputs
			for(int i = 0; i < adFeatures.length; i++)
			{
				this.oInputs.get(i).dResult = adFeatures[i];
			}

			// Execute the algorithm
			runNNet();

			// Make result...
			// TODO: fix second best kludge of adding the same thing twice
			this.oResultSet.addResult(new Result(interpretAsBinary()));
			this.oResultSet.addResult(new Result(interpretAsBinary() + 1));

			return true;
		}
		catch(StorageException e)
		{
			e.printStackTrace(System.err);
			throw new ClassificationException(e);
		}
	}

	/**
	 * Evaluates the entire neural network.
	 * @since 0.3.0.5
	 */
	public final void eval()
	{
		runNNet();
	}

	/**
	 * Evaluates the entire neural network.
	 */
	private final void runNNet()
	{
		for(int i = 0; i < this.oLayers.size(); i++)
		{
			Layer oTmpLayer = (Layer)this.oLayers.get(i);
			oTmpLayer.eval();
		}
	}

	//----------- Methods for Creating the NNet -----------------

	/**
	 * Parses XML and produces a neural network data structure.
	 * @param pstrFilename net's XML filename
	 * @param pbValidateDTD if true DTD will be validated
	 * @throws StorageException if there was an I/O or otherwise error
	 * during initialization of the net
	 */
	public final void initialize(final String pstrFilename, final boolean pbValidateDTD)
	throws StorageException
	{
		try
		{
			Debug.debug("Initializing XML parser...");

			DocumentBuilderFactory oDBF = DocumentBuilderFactory.newInstance();

			oDBF.setNamespaceAware(true);
			oDBF.setValidating(pbValidateDTD);

			DocumentBuilder oBuilder = oDBF.newDocumentBuilder();
			OutputStreamWriter oErrorWriter = new OutputStreamWriter(System.err, OUTPUT_ENCODING);

			oBuilder.setErrorHandler(new NeuralNetworkErrorHandler(new PrintWriter(oErrorWriter, true)));

			Debug.debug("Parsing XML file...");
			Document oDocument = oBuilder.parse(new File(pstrFilename));

			// Add input layer
			this.oLayers.add(this.oInputs);

			// Build NNet structure
			Debug.debug("Making the NNet structure...");
			buildNetwork(oDocument);

			// Add output layer
			this.oLayers.add(this.oOutputs);

			// Fix inputs/outputs
			Debug.debug("Setting the inputs and outputs for each Neuron...");
			this.iCurrenLayer = 0;
			createLinks(oDocument);
		}
		catch(FileNotFoundException e)
		{
			try
			{
				generate();
				dump();
			}
			catch(ClassificationException oClassificationException)
			{
				oClassificationException.printStackTrace(System.err);
				throw new StorageException(oClassificationException);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			throw new StorageException(e);
		}
    }

	/**
	 * Generates the initial network at random with the default parameters.
	 * The defaults include the number of input neurons is the same as number of
	 * features <it>f</it>, the number of output layer neurons is the same as the
	 * number of bits in an integer <it>n</it>, and one middle (hidden) layer that has
	 * <it>h = |f - n| / 2</i>. If  <it>f = n</it>, then <it>h = f / 2</it>.
	 * @throws ClassificationException
	 * @since 0.3.0.6
	 * @see #DEFAULT_OUTPUT_NEURON_BITS
	 */
	public void generate()
	throws ClassificationException
	{
		Debug.debug("Generating new net...");

		int iFeaturesNum = this.oFeatureExtraction.getFeaturesArray().length;

		int iLastHiddenNeurons = Math.abs(iFeaturesNum - DEFAULT_OUTPUT_NEURON_BITS) / 2;

		if(iLastHiddenNeurons == 0)
		{
			iLastHiddenNeurons = iFeaturesNum / 2;
		}

		// Generate fully linked 3-layer neural net with random weights
		generate
		(
			// As many inputs as features
			iFeaturesNum,

			// "Middleware", from air
			new int[]
			{
				iFeaturesNum * 2,
				iFeaturesNum,
				iLastHiddenNeurons
			},

			// Output layer
			DEFAULT_OUTPUT_NEURON_BITS
		);

		Debug.debug("Dumping newly generated net...");
	}

	/**
	 * Performs DOM tree traversal to build neural network structure.
	 * @param poNode current root Node
	 */
	private final void buildNetwork(Node poNode)
	{
		String strName;
		int iType = poNode.getNodeType();

		if(iType == Node.ELEMENT_NODE)
		{
			strName = poNode.getNodeName();

			// TODO: not yet ;-)
			if(strName.equals("input") || strName.equals("output"))
			{
				return;
			}

			//Debug.debug("Making " + name + "...");

			NamedNodeMap oAtts = poNode.getAttributes();

			if(strName.equals("layer"))
			{
				for(int i = 0; i < oAtts.getLength(); i++)
				{
					Node oAttribute = oAtts.item(i);

					String strAttName = oAttribute.getNodeName();
					String strAttValue = oAttribute.getNodeValue();

					if(strAttName.equals("type"))
					{
						if(strAttValue.equals("input"))
						{
							this.oCurrentLayer = this.oInputs;
							this.iNeuronType = Neuron.INPUT;
						}
						else if(strAttValue.equals("output"))
						{
							this.oCurrentLayer = this.oOutputs;
							this.iNeuronType = Neuron.OUTPUT;
						}
						else
						{
							this.oCurrentLayer = new Layer();
							this.oLayers.add(this.oCurrentLayer);
							this.iNeuronType = Neuron.HIDDEN;
						}
					}
					else if(strAttName.equals("index"))
					{
						Debug.debug("Indexing layers currently not supported... Assuming written order.");
					}
					else
					{
						System.err.println("Unknown layer attribute: " + strAttName);
					}
				}
			}
			else if(strName.equals("neuron"))
			{
				String strNeuronName = new String();
				double dThreshold = 0.0;

				for(int i = 0; i < oAtts.getLength(); i++)
				{
					Node oAttribute = oAtts.item(i);

					String strAttName = oAttribute.getNodeName();
					String strAttValue = oAttribute.getNodeValue();

					if(strAttName.equals("index"))
					{
						//Debug.debug("Setting neuron name to " + strAttValue);
						strNeuronName = new String(strAttValue);
					}
					else if(strAttName.equals("thresh"))
					{
						try
						{
							dThreshold = Double.valueOf(strAttValue.trim()).doubleValue();
							//Debug.debug("Setting threshold to " + dThreshold + ".");
						}
						catch(NumberFormatException nfe)
						{
							// TODO: throw an exception maybe?
							System.err.println("NumberFormatException: " + nfe.getMessage());
							nfe.printStackTrace(System.err);
						}
					}
					else
					{
						System.err.println("Unknown layer attribute: " + strAttName);
					}
				}

				//Debug.debug("Making new neuron " + strNeuronName + " of type " + iType);

				Neuron oTmpNeuron = new Neuron(strNeuronName, this.iNeuronType);
				oTmpNeuron.dThreshold = dThreshold;
				this.oCurrentLayer.add(oTmpNeuron);
			}
		}

		// Recurse for children if any
		for
		(
			Node oChild = poNode.getFirstChild();
			oChild != null;
			oChild = oChild.getNextSibling()
		)
		{
			buildNetwork(oChild);
		}
	}

	/**
	 * DOM tree traversal -- create input and output links.
	 * @param poNode Node to create links to and from
	 * @throws ClassificationException if net's configuration is out order
	 */
	private final void createLinks(Node poNode)
	throws ClassificationException
	{
		int iType = poNode.getNodeType();

		String strName;

		if(iType == Node.ELEMENT_NODE)
		{
			strName = poNode.getNodeName();

			NamedNodeMap oAtts = poNode.getAttributes();

			if(strName.equals("layer"))
			{
				for(int i = 0; i < oAtts.getLength(); i++)
				{
					Node oAttribute = oAtts.item(i);

					String strAttName = oAttribute.getNodeName();
					String strAttValue = oAttribute.getNodeValue();

					if(strAttName.equals("type"))
					{
						if(strAttValue.equals("input"))
						{
							this.oCurrentLayer = this.oInputs;
							this.iCurrenLayer = 0;
						}
						else if(strAttValue.equals("output"))
						{
							this.oCurrentLayer = this.oOutputs;
							this.iCurrenLayer = this.oLayers.size() - 1;
						}
						else
						{
							this.iCurrenLayer = ++this.iCurrLayerBuf;
							this.oCurrentLayer = (Layer)this.oLayers.get(this.iCurrenLayer);
						}

						//Debug.debug("Moving to layer " + currLayer + " [currLayerBuf is " + currLayerBuf + "]");
					}
				}
			}

			else if(strName.equals("neuron"))
			{
				String strIndex = new String();

				for(int i = 0; i < oAtts.getLength(); i++)
				{
					Node oAttribute = oAtts.item(i);

					String strAttName = oAttribute.getNodeName();
					String strAttValue = oAttribute.getNodeValue();

					if(strAttName.equals("index"))
					{
						strIndex = new String(strAttValue);
					}
				}

				this.oCurrNeuron = this.oCurrentLayer.getNeuron(strIndex);
			}

			else if(strName.equals("input"))
			{
				String strIndex = null;
				double dWeight = -1.0;

				for(int i = 0; i < oAtts.getLength(); i++)
				{
					Node oAttribute = oAtts.item(i);

					String strAttName = oAttribute.getNodeName();
					String strAttValue = oAttribute.getNodeValue();

					if(strAttName.equals("ref"))
					{
						strIndex = new String(strAttValue);
					}
					else if(strAttName.equals("weight"))
					{
						try
						{
							dWeight = Double.valueOf(strAttValue.trim()).doubleValue();
						}
						catch(NumberFormatException nfe)
						{
							// TODO: should classification exception be thrown here?? 
							System.err.println("NumberFormatException: " + nfe.getMessage());
							nfe.printStackTrace(System.err);
						}
					}
				}

				//if(dWeight < 0.0) {
				//    throw new ClassificationException("Bad \'weight\' defined for neuron " + this.oCurrNeuron.name + " in layer " + this.oCurrentLayer);
				//}

				if(strIndex == null || strIndex.equals(""))
				{
					throw new ClassificationException
					(
						"No 'ref' value assigned for neuron " +
						this.oCurrNeuron.strName +
						" in layer " + this.iCurrenLayer
					);
				}

				//Debug.debug("Adding input " + strIndex + " with weight " + dWeight);

				if(this.iCurrenLayer > 0)
				{
					Neuron oNeuronToAdd = ((Layer)this.oLayers.get(this.iCurrenLayer - 1)).getNeuron(strIndex);

					if(oNeuronToAdd == null)
					{
						throw new ClassificationException
						(
							"Cannot find neuron " + strIndex
							+ " in layer " + (this.iCurrenLayer - 1)
						);
					}

					this.oCurrNeuron.addInput(oNeuronToAdd, dWeight);
				}
				else
				{
					throw new ClassificationException("Input element not allowed in input layer");
				}
			}

			else if(strName.equals("output"))
			{
				String strIndex = null;

				for(int i = 0; i < oAtts.getLength(); i++)
				{
					Node oAttribute = oAtts.item(i);

					String strAttName = oAttribute.getNodeName();
					String strAttValue = oAttribute.getNodeValue();

					if(strAttName.equals("ref"))
					{
						strIndex = new String(strAttValue);
					}
				}

				if(strIndex == null || strIndex.equals(""))
				{
					throw new ClassificationException
					(
						"No 'ref' value assigned for neuron " + this.oCurrNeuron.strName +
						" in layer " + this.iCurrenLayer
					);
				}

				//Debug.debug("Adding output " + strIndex);

				if(this.iCurrenLayer >= 0)
				{
					Neuron oNeuronToAdd = this.oLayers.get(this.iCurrenLayer + 1).getNeuron(strIndex);

					if(oNeuronToAdd == null)
					{
						throw new ClassificationException
						(
							"Cannot find neuron " + strIndex
							+ " in layer " + (this.iCurrenLayer + 1)
						);
					}

					this.oCurrNeuron.addOutput(oNeuronToAdd);
				}
			}
		}

		// Recurse for children if any
		for
		(
			Node oChild = poNode.getFirstChild();
			oChild != null;
			oChild = oChild.getNextSibling()
		)
		{
			createLinks(oChild);
		}
    }

    //----------- Methods for Running the NNet -----------------

	/**
	 * Sets inputs.
	 * @param padInputs double array of input features
	 * @throws ClassificationException if the input array's length isn't
	 * equal to the size of the input layer
	 */
	public final void setInputs(final double[] padInputs)
	throws ClassificationException
	{
		if(padInputs.length != this.oInputs.size())
		{
			throw new ClassificationException
			(
				"Input array size not consistent with input layer."
			);
		}

		for(int i = 0; i < padInputs.length; i++)
		{
			this.oInputs.get(i).dResult = padInputs[i];
		}
	}

	/**
	 * Gets outputs of a neural network run.
	 * @return array of doubles read off the output layer's neurons
	 */
	public double[] getOutputResults()
	{
		double[] adRet = new double[this.oOutputs.size()];

		for(int i = 0; i < this.oOutputs.size(); i++)
		{
			adRet[i] = this.oOutputs.get(i).dResult;
		}

		return adRet;
	}

    //----------- Methods for Outputting the NNet -----------------

	/**
	 * Indents the output according to the requested tabulation
	 * for pretty indentation. TODO: move elsewhere to some 
	 * utility module.
	 * @param poWriter Writer object to write tabs to.
	 * @param piTabsNum number of tabs
	 * @throws IOException if there is an error writing out the tabs
	 */
	public static final void indent(BufferedWriter poWriter, final int piTabsNum)
	throws IOException
	{
		for(int i = 0; i < piTabsNum; i++)
		{
			poWriter.write("\t");
		}
	}

	/**
	 * Dumps Neural Network as XML file.
	 * @param pstrFilename XML file name to write to
	 * @throws StorageException in case of an I/O error
	 */
	public final void dumpXML(final String pstrFilename)
	throws StorageException
	{
		try
		{
			BufferedWriter oWriter = new BufferedWriter(new FileWriter(pstrFilename));

			oWriter.write("<?xml version=\"1.0\"?>");
			oWriter.newLine();
			oWriter.write("<net>");
			oWriter.newLine();

			for(int i = 0; i < this.oLayers.size(); i++)
			{
				Layer oTmpLayer = (Layer)this.oLayers.get(i);

				indent(oWriter, 1);
				oWriter.write("<layer type=\"");

				if(i == 0)
				{
					oWriter.write("input");
				}
				else if(i == this.oLayers.size() - 1)
				{
					oWriter.write("output");
				}
				else
				{
					oWriter.write("hidden");
				}

				oWriter.write(new StringBuffer("\" index=\"").append(i).append("\">").toString());
				oWriter.newLine();

				for(int j = 0; j < oTmpLayer.size(); j++)
				{
					oTmpLayer.get(j).printXML(oWriter, 2);
				}

				indent(oWriter, 1);
				oWriter.write("</layer>");
				oWriter.newLine();
			}

			oWriter.write("</net>");
			oWriter.newLine();

			oWriter.close();
		}
        catch(Exception e)
        {
            throw new StorageException(e);
        }
	}

	/**
	 * Generates a virgin net at random.
	 * @param piNumOfInputs number of input Neurons in the input layer
	 * @param paiHiddenLayers arrays of numbers of Neurons in the hidden layers
	 * @param piNumOfOutputs number of output Neurons in the output layer
	 * @since 0.2.0, Serguei
	 * @throws ClassificationException if the hidden layers array has nothing or is null
	 */
	public final void generate(int piNumOfInputs, int[] paiHiddenLayers, int piNumOfOutputs)
	throws ClassificationException
	{
		if(paiHiddenLayers == null || paiHiddenLayers.length == 0)
		{
			throw new ClassificationException
			(
				"Number of hidden layers may not be null or of 0 length."
			);
		}

		for(int i = 1; i <= 1 + paiHiddenLayers.length + 1; i++)
		{
			// Add input layer
			if(i == 1)
			{
  				for(int j = 1; j <= piNumOfInputs; j++)
  				{
					Neuron oFreshAndJuicyNeuron = new Neuron("" + j, Neuron.INPUT);
					oFreshAndJuicyNeuron.dThreshold = 1.0;
					this.oInputs.add(oFreshAndJuicyNeuron);
				}

				this.oLayers.add(this.oInputs);

				continue;
			}

			// Add output layer
			if(i == 1 + paiHiddenLayers.length + 1)
			{
  				for(int j = 1; j <= piNumOfOutputs; j++)
  				{
					Neuron oFreshAndJuicyNeuron = new Neuron("" + j, Neuron.OUTPUT);
					oFreshAndJuicyNeuron.dThreshold = 1.0;
					this.oOutputs.add(oFreshAndJuicyNeuron);
				}

  				this.oLayers.add(this.oOutputs);

				continue;
			}

			Layer oHiddenLayer = new Layer();

			// Add hidden layers
			for(int j = 1; j <= paiHiddenLayers[i - 2]; j++)
			{
				Neuron oFreshAndJuicyNeuron = new Neuron("" + j, Neuron.HIDDEN);
				oFreshAndJuicyNeuron.dThreshold = 1.0;
				oHiddenLayer.add(oFreshAndJuicyNeuron);
			}

			this.oLayers.add(oHiddenLayer);
		}

		// Fix inputs / outputs
		Debug.debug("Setting the inputs and outputs for each Neuron...");

		for(int iCurrentLayer = 0; iCurrentLayer < this.oLayers.size() - 1; iCurrentLayer++)
		{
			Layer oTmpLayer = (Layer)this.oLayers.get(iCurrentLayer);

			for(int n = 0; n < oTmpLayer.size(); n++)
			{
				Neuron oCurrentNeuron = oTmpLayer.get(n);

				Layer oNextLayer = (Layer)this.oLayers.get(iCurrentLayer + 1);

				for(int k = 0; k < oNextLayer.size(); k++)
				{
					Neuron oNextLayerNeuron = oNextLayer.get(k);
					oCurrentNeuron.addOutput(oNextLayerNeuron);
					oNextLayerNeuron.addInput(oCurrentNeuron, new Random().nextDouble() * 2.0 - 1.0);
				}
			}
		}
	}

    //----------- Method for Training the NNet -----------------

	/**
	 * Performs Actual training of the net.
	 * @param padInput the input feature vector
	 * @param piExpectedLength expected length of the output layer
	 * @param pdTrainConst training constant to use for neurons
	 * @throws ClassificationException if the training constant less than zero or sizes
	 * of the inputs do not match or there is a problem evaluating the network
	 */
	public final void train(final double[] padInput, int piExpectedLength, final double pdTrainConst)
	throws ClassificationException
	{
		if(pdTrainConst <= 0.0)
		{
			throw new ClassificationException
			(
				"NeuralNetwork.train(): Training constant must be > 0.0, supplied: " +
				pdTrainConst
			);
		}

		if(padInput.length != this.oInputs.size())
		{
			throw new ClassificationException
			(
				"NeuralNetwork.train(): Input array size (" + padInput.length +
				") not consistent with input layer (" + this.oInputs.size() + ")"
			);
		}

		/*
		 * Setup NNet with training data.
		 */

		// Must setup the input data...
		setInputs(padInput);

		//if(piExpectedLength/*.length*/ != this.oOutputs.size())
		//    throw new ClassificationException("Expected array size not consistent with output layer.");

		// Run on training data. TODO: Must fix...
		runNNet();

		for(int k = this.oOutputs.size() - 1; k >= 0; k--)
		{
			int iCurrExpectedLength = piExpectedLength % 2;
			piExpectedLength /= 2;

			this.oOutputs.get(k).train(iCurrExpectedLength, pdTrainConst, 1.0);
		}

		for(int i = this.oLayers.size() - 2; i >= 0; i--)
		{
			Layer oLayer = (Layer)this.oLayers.get(i);
			oLayer.train(pdTrainConst);
		}
	}

	/**
	 * Applies changes made to neurons on every net's layer.
	 */
	public final void commit()
	{
		for(int i = 0; i < this.oLayers.size(); i++)
		{
			Layer oLayer = (Layer)this.oLayers.get(i);
			oLayer.commit();
		}
	}

	/**
	 * Interprets net's binary output as an ID for the final classification result.
	 * @return ID, integer
	 */
	private final int interpretAsBinary()
	{
		int iID = 0;

		for(int i = 0; i < this.oOutputs.size(); i++)
		{
			// Binary displacement happens to not have any
			// effect in the first iteration :-P
			iID *= 2;

			// Add 1 if the resulting weight is more than 0.5
			if(this.oOutputs.get(i).dResult > 0.5)
			{
				iID += 1;
			}

			Debug.debug(this.oOutputs.get(i).dResult + ",");
		}

		Debug.debug("Interpreted binary result (ID) = " + iID);

		return iID;
	}

    /* From Storage Manager */

	/**
	 * Dumps Neural Net to an XML or serialized file.
	 * @throws StorageException
	 */
	public void dump()
	throws StorageException
	{
		try
		{
			switch(this.iCurrentDumpMode)
			{
				case DUMP_GZIP_BINARY:
				case DUMP_BINARY:
				{
					// No initialization was done for binary mode
					if(this.oInputs.size() == 0)
					{
						generate();
					}
					
					Vector<Serializable> oNeuralNetLayersData = new Vector<Serializable>(3);
					oNeuralNetLayersData.add(this.oInputs);
					oNeuralNetLayersData.add(this.oLayers);
					oNeuralNetLayersData.add(this.oOutputs);
					
					this.oObjectToSerialize = oNeuralNetLayersData; 
				}
			}
		}
		catch(ClassificationException e)
		{
			e.printStackTrace(System.err);
			throw new StorageException(e);
		}

		switch(this.iCurrentDumpMode)
		{
			case DUMP_GZIP_BINARY:
				dumpGzipBinary();
				break;

			case DUMP_BINARY:
				dumpBinary();
				break;

			default:
				super.dump();
		}
		//dumpXML();
	}

	/**
	 * Restores Neural Net from an XML or serialized file.
	 * @throws StorageException
	 */
	public void restore()
	throws StorageException
	{
		switch(this.iCurrentDumpMode)
		{
			case DUMP_GZIP_BINARY:
				restoreGzipBinary();
				break;

			case DUMP_BINARY:
				restoreBinary();
				break;

			default:
				super.restore();
		}
		//restoreXML();
	}

	/**
	 * Overrides the default implementation of <code>dumpXML()</code>.
	 * Merely calls <code>dumpXML(String)</code>.
	 * @see marf.Storage.IStorageManager#dumpXML()
	 * @see #dumpXML(String)
	 * @since 0.3.0.6
	 */
	public void dumpXML()
	throws StorageException
	{
		dumpXML(getDefaultFilename());
	}
	
	/**
	 * Overrides the default implementation of <code>restoreXML()</code>.
	 * Merely calls <code>initialize()</code>.
	 * @see marf.Storage.IStorageManager#restoreXML()
	 * @see #initialize(String, boolean)
	 * @since 0.3.0.6
	 */
	public void restoreXML()
	throws StorageException
	{
		initialize(getDefaultFilename(), false);
	}

	/**
	 * @see marf.Storage.StorageManager#backSynchronizeObject()
	 * @since 0.3.0.6
	 */
	@SuppressWarnings("unchecked")
	public void backSynchronizeObject()
	{
		Vector<Serializable> oLoadedCopy = (Vector<Serializable>)this.oObjectToSerialize;

		this.oInputs = (Layer)oLoadedCopy.firstElement();
		this.oLayers = (ArrayList<Layer>)oLoadedCopy.elementAt(1);
		this.oOutputs = (Layer)oLoadedCopy.lastElement();
	}
	
	/**
	 * Generates typical filename for dump/restore.
	 * @return canonical filename for dump/restore based on the current dump mode.
	 * @since 0.3.0.6 
	 */
	protected String getDefaultFilename()
	{
		return new StringBuffer()
			.append(getClass().getName()).append(".")
			.append(MARF.getPreprocessingMethod()).append(".")
			.append(MARF.getFeatureExtractionMethod()).append(".")
			.append(getDefaultExtension())
			.toString();
	}

	/**
	 * Retrieves the minimum-error classification result.
	 * @return Result object
	 * @since 0.3.0.2
	 */
	public Result getResult()
	{
		return this.oResultSet.getMinimumResult();
	}

	/**
	 * Error handler to report errors and warnings.
	 * TODO: iclement: this may need revision.
	 * @author Ian Clement
	 */
	private static class NeuralNetworkErrorHandler
	implements ErrorHandler
	{
		/**
		 * Error handler output goes here.
		 */
		private PrintWriter oOut;

		/**
		 * Constructs our error handler with the given writer.
		 * @param poOut writer to write errors to
		 */
		NeuralNetworkErrorHandler(PrintWriter poOut)
		{
			this.oOut = poOut;
		}

		/**
		 * Returns a string describing parse exception details.
		 * @param poParseException exception to get info from
		 * @return string representation of the info
		 */
		private String getParseExceptionInfo(SAXParseException poParseException)
		{
			String strSystemId = poParseException.getSystemId();

			if(strSystemId == null)
			{
				strSystemId = "null";
			}

			String strErrInfo =
				"URI=" + strSystemId +
				" Line=" + poParseException.getLineNumber() +
				": " + poParseException.getMessage();

			return strErrInfo;
		}

		// The following methods are standard SAX ErrorHandler methods.
		// See SAX documentation for more info.

		/**
		 * Issues a SAX warning.
		 * @param poParseException the parse exception to warn about
		 * @throws SAXException
		 */
		public void warning(SAXParseException poParseException)
		throws SAXException
		{
			this.oOut.println
			(
				"WARNING: "
				+ getParseExceptionInfo(poParseException)
			);
		}

		/**
		 * Issues a SAX error.
		 * @param poParseException the source of the error information
		 * @throws SAXException the error based on the parse exception
		 */
		public void error(SAXParseException poParseException)
		throws SAXException
		{
			String strMessage = "ERROR: " + getParseExceptionInfo(poParseException);
			throw new SAXException(strMessage);
		}

		/**
		 * Issues a SAX fatal error.
		 * @param poParseException the source of the error information
		 * @throws SAXException the error based on the parse exception
		 */
		public void fatalError(SAXParseException poParseException)
		throws SAXException
		{
			String strMessage = "FATAL: " + getParseExceptionInfo(poParseException);
			throw new SAXException(strMessage);
		}
	}

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.64 $";
	}
}

// EOF
