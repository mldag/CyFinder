package edu.claflin.finder.logic.comp;

import static edu.claflin.finder.Global.getLogger;

import java.math.BigDecimal;
import java.util.Comparator;

import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Edge;

/**
 * A simple edge weight comparator in which the edge data is considered a
 * numeric value and used as the comparison benchmark.
 * 
 * @author Charles Allen Schultz II
 * @version 3.0 February 4, 2016
 */
public class EdgeWeightComparator implements Comparator<Edge>
{

	/**
	 * Indicates if the comparator should sort elements in ascending order.
	 */
	private final boolean ascending;

	/**
	 * Constructs an EdgeWeightComparator.
	 * 
	 * @param ascending boolean indicating if edges should be sorted in ascending
	 *                  order.
	 */
	public EdgeWeightComparator(boolean ascending)
	{
		this.ascending = ascending;
	}

	/**
	 * Compares the edges based on the D data held by the edge. Assumes the data is
	 * a numeric value.
	 * 
	 * @param o1 the first edge to compare.
	 * @param o2 the second edge to compare.
	 * @return -1, 0, or 1 if the first edge is ordered before, the same as, or
	 *         after the second.
	 */
	@Override
	public int compare(Edge o1, Edge o2)
	{
		BigDecimal val1 = null, val2 = null;
		try
		{
			val1 = new BigDecimal(o1.getData());
			val2 = new BigDecimal(o2.getData());
		}
		catch (NumberFormatException nfe)
		{
			if (getLogger() != null)
			{
				getLogger().logError(LogLevel.NORMAL,
						"Comparator: Edge data " + "must be of number foramt to use the " + "EdgeWeightComparator!");
				getLogger().destroy();
			}
			throw new RuntimeException("Bad Edge Data");
		}

		if (ascending)
		{
			return val2.compareTo(val1);
		}
		else
		{
			return val1.compareTo(val2);
		}
	}

	/**
	 * Converts an unknown object into a BigDecimal if possible.
	 * 
	 * @param object the Object to convert.
	 * @return the converted BigDecimal object.
	 */
	private BigDecimal toNumber(Object object)
	{
		if (object instanceof Double || object instanceof Float)
			return new BigDecimal((Double) object);
		else if (object instanceof Integer || object instanceof Short)
			return new BigDecimal((Integer) object);
		else if (object instanceof Long)
			return new BigDecimal((Long) object);
		else
			throw new NumberFormatException();
	}
}
