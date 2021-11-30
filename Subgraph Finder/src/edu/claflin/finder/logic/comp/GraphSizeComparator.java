package edu.claflin.finder.logic.comp;

import java.util.Comparator;

import edu.claflin.finder.logic.Graph;

public class GraphSizeComparator implements Comparator<Graph>
{
	@Override
	public int compare(Graph g1, Graph g2)
	{

		int count1 = g1.getNodeCount();
		int count2 = g2.getNodeCount();

		if (count1 > count2)
		{
			return 1;
		}
		else if (count1 == count2)
		{
			int eCount1 = g1.getEdgeCount();
			int eCount2 = g2.getEdgeCount();
			
			if (eCount1 > eCount2) 
			{
				return 1;
			}
			else if (eCount1 == eCount2) 
			{
				return 0;	
			}
			else 
			{
				return -1;
			}
			
		}
		else
		{
			return -1;
		}

	}
}
