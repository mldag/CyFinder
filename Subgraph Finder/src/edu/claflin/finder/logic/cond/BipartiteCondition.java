package edu.claflin.finder.logic.cond;

import edu.claflin.finder.logic.Condition;
import edu.claflin.finder.logic.Graph;

/**
 * Represents a bipartite graph as a Condition object.  Used to test for 
 * bipartiteness.
 * 
 * @author Charles Allen Schultz II
 * @version 1.1.1 May 26, 2015
 */
public class BipartiteCondition extends Condition
{

	/**
	 * {@inheritDoc }
	 * <p>
	 * Checks to determine if the supplied graph is Bipartite.
	 * @return true if the graph is bipartite.
	 */
	@Override
	public boolean satisfies(Graph existingGraph)
	{
		if (existingGraph.getNodeCount() == 1)
			return true;
		else
			return existingGraph.isBipartite();
	}	

	@Override
	public String toString()
	{
		return "Bipartite Condition";
	}
}
