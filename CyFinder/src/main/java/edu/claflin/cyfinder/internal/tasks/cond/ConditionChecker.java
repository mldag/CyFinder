package edu.claflin.cyfinder.internal.tasks.cond;

import org.cytoscape.model.CyNetwork;

import edu.claflin.cyfinder.internal.tasks.utils.GraphTaskUtils;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;

public class ConditionChecker
{

	public static boolean checkUndirected(CyNetwork network)
	{
		// Read CyNetwork into a Subgraph Finder Network
		Graph graph = null;
		graph = GraphTaskUtils.convertCyNetwork(network); // cynetwork to Graph

		// undirected no longer gives warnings but starts a make unidirected task

		if (graph == null)
		{
			// showUndirectedError();
			return false;
		}

		for (Edge e : graph.getEdgeList())
		{
			if (!e.isUndirected())
			{
				// showUndirectedError();
				return false;
			}
		}

		return true;
	}

	public static boolean checkDirected(CyNetwork network)
	{
		// Read CyNetwork into a Subgraph Finder Network
		Graph graph = null;
		graph = GraphTaskUtils.convertCyNetwork(network); // cynetwork to Graph

		if (graph == null)
		{
			showDirectedError();
			return false;
		}

		for (Edge e : graph.getEdgeList())
		{
			if (e.isUndirected())
			{
				showDirectedError();
				return false;
			}
		}

		return true;
	}
	
	public static boolean checkPseudo(CyNetwork network)
	{
		// Read CyNetwork into a Subgraph Finder Network
		Graph graph = null;

		graph = GraphTaskUtils.convertCyNetwork(network); // cynetwork to Graph

		if (graph == null)
		{
			showPseudoError();
			return false;
		}

		for (Edge e : graph.getEdgeList())
		{
			if (e.getSource().equals(e.getTarget()))
			{
				showPseudoError();
				return false;
			}
		}

		return true;
	}

	private static void showDirectedError()
	{
		GraphTaskUtils.showError("Error: Input Network not Directed",
				"This Network is not Directed. To use the shortest path function the graph must be directed");
	}

	private static void showPseudoError()
	{
		GraphTaskUtils.showError("Error: Input Network has loops", "This Network contains loops." );
	}
}
