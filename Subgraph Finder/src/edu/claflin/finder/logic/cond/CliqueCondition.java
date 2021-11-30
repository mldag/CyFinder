package edu.claflin.finder.logic.cond;

import edu.claflin.finder.logic.Condition;
import edu.claflin.finder.logic.Graph;

/**
 * Represents a Clique condition.  Used to enforce a Clique relationship on a 
 * ConditionedGraph.  Requires that every node have an edge flowing from it to 
 * every other node.  The following is a simple example in adjacency list 
 * format:
 * <br>
 * A : { B, C }
 * B : { A, C }
 * C : { A, B }
 * 
 * @author Charles Allen Schultz II
 * @version 1.0 June 19, 2015
 */
public class CliqueCondition extends Condition
{

	/**
	 * {@inheritDoc }
	 * <p>
	 * Checks to determine if the supplied graph is a Clique.
	 * @param existingGraph the Graph object to test against.
	 * @return true if the graph is a clique.
	 */
	@Override
	public boolean satisfies(Graph existingGraph)
	{
		return existingGraph.isClique();
	}

	@Override
	public String toString()
	{
		return "Clique Condition";
	}
}
