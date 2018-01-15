package marf.util.comparators;

import marf.Stats.StatisticalObject;
import marf.util.SortComparator;


/**
 * <p>Compares StatisticalObjects by their frequency when sorting.</p>
 *
 * $Id: FrequencyComparator.java,v 1.15 2007/12/01 00:26:51 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.15 $
 * @since 0.3.0.2
 *
 * @see StatisticalObject
 * @see marf.Stats.WordStats
 */
public class FrequencyComparator
extends SortComparator
{
    /**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = 5923172975252427681L;

	/**
	 * Constructs a frequency comparator with the specified sort mode.
	 * @param piSortMode ASCENDING or DESCENDING
	 */
	public FrequencyComparator(int piSortMode)
	{
		super(piSortMode);
	}

	/**
	 * Implementation of the Comparator interface for the StatisticalObjects.
	 * To decide on inequality of the <code>StatisticalObject</code> objects we
	 * compare their frequencies only.
	 *
	 * @param poStatsObject1 first stats object to compare
	 * @param poStatsObject2 second stats object to compare
	 * @return 0 of the frequencies are equal. Depending on the sort mode; a negative
	 * value may mean poStatsObject1 &lt; poStatsObject2 if ASCENDING; or otherwise if DESCENDING
	 *
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * @see StatisticalObject
	 */
	public int compare(Object poStatsObject1, Object poStatsObject2)
	{
		StatisticalObject oStats1 = (StatisticalObject)poStatsObject1;
		StatisticalObject oStats2 = (StatisticalObject)poStatsObject2;

		switch(this.iSortMode)
		{
			case DESCENDING:
				return (oStats2.getFrequency() - oStats1.getFrequency());

			case ASCENDING:
			default:
				return (oStats1.getFrequency() - oStats2.getFrequency());
		}
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.15 $";
	}
}

// EOF
