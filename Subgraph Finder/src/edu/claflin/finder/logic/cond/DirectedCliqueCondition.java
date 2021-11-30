package edu.claflin.finder.logic.cond;

import edu.claflin.finder.logic.Condition;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;

/**
 * Represents a Directed Clique condition.  Used to enforce a Clique 
 * relationship on a ConditionedGraph.  Unlike the standard CliqueCondition, 
 * this condition doesn't require there to be an edge from every node to every 
 * other node.  Instead, the stipulation is that there exist an edge between 
 * every two nodes (i.e. there may exist an edge such that Node A points to 
 * Node B, but no edge from Node B to Node A exists).  The following is a 
 * simple example in adjacency list format:
 * <br>
 * A : { B, C }
 * B : { C }
 * 
 * @author Charles Allen Schultz II
 * @version 1.0 June 19, 2015
 */
public class DirectedCliqueCondition extends Condition
{

	/**
	 * {@inheritDoc }
	 * <p>
	 * Checks to determine if the supplied graph is a Directed Clique.
	 * @param existingGraph the Graph object to test against.
	 * @return true if the graph is a directed clique.
	 */
	@Override
	public boolean satisfies(Graph existingGraph)
	{
		for (Node source : existingGraph.getNodeList())
		{
			boolean directedEdgeExists = true;
			for (Node target : existingGraph.getNodeList())
			{
				if (source == target)
					continue;
				if (existingGraph.getEdge(source, target) == null && existingGraph.getEdge(target, source) == null)
				{
					directedEdgeExists = false;
					break;
				}
			}

			if (!directedEdgeExists)
				return false;
		}

		return true;
	}

	@Override
	public String toString()
	{
		return "Directed Clique Condition";
	}
}
