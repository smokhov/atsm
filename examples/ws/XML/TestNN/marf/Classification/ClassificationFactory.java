package marf.Classification;

import marf.MARF;
import marf.Classification.Distance.ChebyshevDistance;
import marf.Classification.Distance.DiffDistance;
import marf.Classification.Distance.EuclideanDistance;
import marf.Classification.Distance.HammingDistance;
import marf.Classification.Distance.MahalanobisDistance;
import marf.Classification.Distance.MinkowskiDistance;
import marf.Classification.Markov.Markov;
import marf.Classification.NeuralNetwork.NeuralNetwork;
import marf.Classification.RandomClassification.RandomClassification;
import marf.Classification.Similarity.CosineSimilarityMeasure;
import marf.Classification.Stochastic.Stochastic;
import marf.Classification.Stochastic.ZipfLaw;
import marf.FeatureExtraction.IFeatureExtraction;


/**
 * Provides a factory to instantiate requested classification module(s).
 *
 * @author Serguei Mokhov
 * @version $Id: ClassificationFactory.java,v 1.6 2012/06/17 19:12:29 mokhov Exp $
 * @since 0.3.0.5
 */
public final class ClassificationFactory
{
	/**
	 * Disallow instances of this factory as deemed useless.
	 */
	private ClassificationFactory()
	{
	}

	/**
	 * Instantiates a Classification module indicated by
	 * the first parameter with the 2nd parameter as an argument.
	 *
	 * @param poClassificationMethod the integer value corresponding to the
	 * desired classification module
	 * @param poFeatureExtraction passed as an argument to the classifier per framework requirement
	 * @return a reference to the instance of the created feature extraction module
	 * @throws ClassificationException if the indicated module is
	 * unknown or could not be loaded
	 *
	 * @see MARF#NEURAL_NETWORK
	 * @see MARF#STOCHASTIC
	 * @see MARF#MARKOV
	 * @see MARF#EUCLIDEAN_DISTANCE
	 * @see MARF#CHEBYSHEV_DISTANCE
	 * @see MARF#MINKOWSKI_DISTANCE
	 * @see MARF#MAHALANOBIS_DISTANCE
	 * @see MARF#RANDOM_CLASSIFICATION
	 * @see MARF#DIFF_DISTANCE
	 * @see MARF#HAMMING_DISTANCE
	 * @see MARF#COSINE_SIMILARITY_MEASURE
	 * @see MARF#CLASSIFICATION_PLUGIN
	 * @see MARF#ZIPFS_LAW
	 *
	 * @see NeuralNetwork
	 * @see Stochastic
	 * @see Markov
	 * @see ChebyshevDistance
	 * @see EuclideanDistance
	 * @see MinkowskiDistance
	 * @see MahalanobisDistance
	 * @see RandomClassification
	 * @see DiffDistance
	 * @see HammingDistance
	 * @see CosineSimilarityMeasure
	 * @see ZipfLaw
	 */
	public static final IClassification create(final Integer poClassificationMethod, IFeatureExtraction poFeatureExtraction)
	throws ClassificationException
	{
		return create(poClassificationMethod.intValue(), poFeatureExtraction);
	}

	/**
	 * Instantiates a Classification module indicated by
	 * the first parameter with the 2nd parameter as an argument.
	 *
	 * @param piClassificationMethod the integer value corresponding to the
	 * desired classification module
	 * @param poFeatureExtraction passed as an argument to the classifier per framework requirement
	 * @return a reference to the instance of the created feature extraction module
	 * @throws ClassificationException if the indicated module is
	 * unknown or could not be loaded
	 *
	 * @see MARF#NEURAL_NETWORK
	 * @see MARF#STOCHASTIC
	 * @see MARF#MARKOV
	 * @see MARF#EUCLIDEAN_DISTANCE
	 * @see MARF#CHEBYSHEV_DISTANCE
	 * @see MARF#MINKOWSKI_DISTANCE
	 * @see MARF#MAHALANOBIS_DISTANCE
	 * @see MARF#RANDOM_CLASSIFICATION
	 * @see MARF#DIFF_DISTANCE
	 * @see MARF#HAMMING_DISTANCE
	 * @see MARF#COSINE_SIMILARITY_MEASURE
	 * @see MARF#CLASSIFICATION_PLUGIN
	 * @see MARF#ZIPFS_LAW
	 *
	 * @see NeuralNetwork
	 * @see Stochastic
	 * @see Markov
	 * @see ChebyshevDistance
	 * @see EuclideanDistance
	 * @see MinkowskiDistance
	 * @see MahalanobisDistance
	 * @see RandomClassification
	 * @see DiffDistance
	 * @see HammingDistance
	 * @see CosineSimilarityMeasure
	 * @see ZipfLaw
	 */
	public static final IClassification create(final int piClassificationMethod, IFeatureExtraction poFeatureExtraction)
	throws ClassificationException
	{
		IClassification oClassification = null;

		switch(piClassificationMethod)
		{
			case MARF.NEURAL_NETWORK:
				oClassification = new NeuralNetwork(poFeatureExtraction);
				break;

			case MARF.STOCHASTIC:
				oClassification = new Stochastic(poFeatureExtraction);
				break;

			case MARF.MARKOV:
				oClassification = new Markov(poFeatureExtraction);
				break;

			case MARF.EUCLIDEAN_DISTANCE:
				oClassification = new EuclideanDistance(poFeatureExtraction);
				break;

			case MARF.CHEBYSHEV_DISTANCE:
				oClassification = new ChebyshevDistance(poFeatureExtraction);
				break;

			case MARF.MINKOWSKI_DISTANCE:
				oClassification = new MinkowskiDistance(poFeatureExtraction);
				break;

			case MARF.MAHALANOBIS_DISTANCE:
				oClassification = new MahalanobisDistance(poFeatureExtraction);
				break;

			case MARF.RANDOM_CLASSIFICATION:
				oClassification = new RandomClassification(poFeatureExtraction);
				break;

			case MARF.DIFF_DISTANCE:
				oClassification = new DiffDistance(poFeatureExtraction);
				break;

			case MARF.HAMMING_DISTANCE:
				oClassification = new HammingDistance(poFeatureExtraction);
				break;

			case MARF.COSINE_SIMILARITY_MEASURE:
				oClassification = new CosineSimilarityMeasure(poFeatureExtraction);
				break;

			case MARF.CLASSIFICATION_PLUGIN:
			{
				try
				{
					oClassification = (IClassification)MARF.getClassificationPluginClass().newInstance();
					oClassification.setFeatureExtraction(poFeatureExtraction);
				}
				catch(Exception e)
				{
					throw new ClassificationException(e.getMessage(), e);
				}

				break;
			}

			case MARF.ZIPFS_LAW:
				oClassification = new ZipfLaw(poFeatureExtraction);
				break;

			default:
			{
				throw new ClassificationException
				(
					"Unknown classification method: " + piClassificationMethod
				);
			}
		}
	
		return oClassification;
	}

	/**
	 * Allows to query for Class objects of the classifiers based on ID
	 * for reflection and other purposes.
	 * @param piClassificationMethod numerical classifier ID
	 * @return the Class object corresponding to the numerical classifier
	 * @throws ClassificationException if the numerical ID not known
	 * @since 0.3.0.6; June 17, 2012
	 */
	public static final Class<?> getClassificationClassByID(final int piClassificationMethod)
	throws ClassificationException
	{
		switch(piClassificationMethod)
		{
			case MARF.NEURAL_NETWORK:
				return NeuralNetwork.class;

			case MARF.STOCHASTIC:
				return Stochastic.class;

			case MARF.MARKOV:
				return Markov.class;

			case MARF.EUCLIDEAN_DISTANCE:
				return EuclideanDistance.class;

			case MARF.CHEBYSHEV_DISTANCE:
				return ChebyshevDistance.class;

			case MARF.MINKOWSKI_DISTANCE:
				return MinkowskiDistance.class;

			case MARF.MAHALANOBIS_DISTANCE:
				return MahalanobisDistance.class;

			case MARF.RANDOM_CLASSIFICATION:
				return RandomClassification.class;

			case MARF.DIFF_DISTANCE:
				return DiffDistance.class;

			case MARF.HAMMING_DISTANCE:
				return HammingDistance.class;

			case MARF.COSINE_SIMILARITY_MEASURE:
				return CosineSimilarityMeasure.class;

			case MARF.CLASSIFICATION_PLUGIN:
				return MARF.getClassificationPluginClass();

			case MARF.ZIPFS_LAW:
				return ZipfLaw.class;

			default:
			{
				throw new ClassificationException
				(
					"Unknown classification method: " + piClassificationMethod
				);
			}
		}
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.6 $";
	}
}

// EOF
