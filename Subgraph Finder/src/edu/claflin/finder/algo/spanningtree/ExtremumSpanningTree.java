package edu.claflin.finder.algo.spanningtree;

import java.util.List;

import edu.claflin.finder.algo.Algorithm;
import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;

public abstract class ExtremumSpanningTree extends Algorithm
{	
	protected boolean max; // maximum spanning tree? (otherwise minimum)
	protected Double threshold; // threshold value for edge weights
	
	/**
	 * Public constructor for initializing the Extremum Spanning Tree finder with default conditions.
	 * 
	 * @param bundle the ArgumentsBundle containing the instantiation arguments.
	 */
	public ExtremumSpanningTree(ArgumentsBundle bundle)
	{
		super(bundle);
		
		max = args.getBoolean("max") == null ? false : args.getBoolean("max");
		threshold = args.getDouble("threshold");
		
		// change default threshold depending on max/min
		if (threshold == null) 
		{
			if (max) // max means there is a low threshold, since none was selected, then all edges should pass with minimum double
			{
				threshold = Double.NEGATIVE_INFINITY;
			}
			else // not max means there is a high threshold, since none was selected, then all edges should pass with maximum double
			{
				threshold = Double.POSITIVE_INFINITY;
			}
		}
	}
	
	protected String getName(Graph g, String algo, List<Edge> edges) 
	{
		double weight = edges.stream().mapToDouble(e -> e.getData()).sum();
		return g.getName() + " " + algo + " " + (max ? "Maximum Spanning Tree " : "Minimum Spanning Tree ") + ("for attribute " + getWeightName() + " ") + "W(T) = "
				+ weight;
	}
	
	protected boolean edgeMeetsThreshold(Edge e) 
	{		
		if (max && e.getData() >= threshold) // max means I have a threshold to filter out low weight Nodes
		{
			return true;
		}
		else if (!max && e.getData() <= threshold) // not max means I have a threshold to filter out high weight Nodes
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
	
}
