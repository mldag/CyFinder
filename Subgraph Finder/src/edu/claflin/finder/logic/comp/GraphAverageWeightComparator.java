package edu.claflin.finder.logic.comp;

import java.util.Comparator;

import edu.claflin.finder.logic.Graph;

public class GraphAverageWeightComparator implements Comparator<Graph>
{
	@Override
	public int compare(Graph g1, Graph g2)
	{
		double weight1 = g1.getGraphWeight()/g1.getEdgeCount();
		double weight2 = g2.getGraphWeight()/g2.getEdgeCount();

		if (weight1 > weight2)
		{
			return 1;
		}
		else if (weight1 == weight2)
		{
			return 0;
		}
		else
		{
			return -1;
		}
	}
}
