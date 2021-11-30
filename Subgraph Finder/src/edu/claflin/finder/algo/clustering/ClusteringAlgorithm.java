package edu.claflin.finder.algo.clustering;

import java.util.ArrayList;
import java.util.List;

import edu.claflin.finder.algo.Algorithm;
import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;

public abstract class ClusteringAlgorithm extends Algorithm
{	
	private boolean weighted;
	
	public ClusteringAlgorithm(ArgumentsBundle args)
	{
		super(args);
		weighted = args.getBoolean("weighted") == null ? false : args.getBoolean("weighted");		
	}

	public boolean isWeighted() 
	{
		return weighted;
	}
	
	protected ArrayList<Graph> buildCommunityGraphs(List<List<Node>> cs, Graph graph, String algo_name)
	{	
		ArrayList<Graph> communities = new ArrayList<>();

		int count = 0;
		for (List<Node> V : cs)
		{
			if (!V.isEmpty())
			{
				List<Edge> E = graph.getEdgesBack(V);
				String communityName = graph.getName() + " " + algo_name + " Community " + ++count + (weighted ? (" on attribute " + getWeightName()) : "");
				Graph h = new Graph(communityName, V, E);
				communities.add(h);
			}
		}

		return communities;
	}
}
