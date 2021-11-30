package edu.claflin.finder.algo;

import static edu.claflin.finder.Global.getLogger;

import java.util.ArrayList;
import java.util.List;

import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;

/**
 * Processes a {@link Graph} searching for connected components by performing a
 * depth first search.
 */
public class ConnectedComponentsDFS extends Algorithm
{

	/**
	 * Public constructor for initializing the DepthFirstTraversalSearch with
	 * default conditions.
	 * 
	 * @param bundle the ArgumentsBundle containing the instantiation arguments.
	 */
	public ConnectedComponentsDFS(ArgumentsBundle bundle)
	{
		super(bundle);

		if (getLogger() != null)
		{
			getLogger().logInfo(LogLevel.DEBUG, "Connected Components with Depth First Search Algorithm .");
		}
	}

	@Override
	public String toString()
	{
		return "Connected Components with Depth First Search Algorithm";
	}

	/**
	 * {@inheritDoc } <br>
	 * Finds most Subgraphs by performing a depth first search on the node tree. It
	 * is unknown if it finds all or only most of the subgraphs due to the nature of
	 * the algorithm. It is expected, however, that it would miss certain node
	 * groupings.
	 * 
	 * @param graph the {@link Graph} object to search through.
	 * @return the ArrayList of Graph objects holding all found subgraphs.
	 */
	@Override
	public ArrayList<Graph> process(Graph graph)
	{
		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL, "Connected Components: Searching Graph: " + graph.getName());
		}

		if (graph.getNodeCount() < 1)
		{
			return new ArrayList<Graph>();
		}

		ArrayList<Graph> results = getConnectedComponents(graph);

		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL,
					"Connected Components: Finished Searching Graph. SGs found: " + results.size());
		}

		return cull(results);
	}

	/**
	 * Gets a list of this Graph's Connected Components. Uses Depth First Search
	 * Technique.
	 * 
	 * @param graph the Graph to get Connected Components from.
	 * 
	 * @return the list of this Graph's Connected Components
	 */
	private ArrayList<Graph> getConnectedComponents(Graph graph)
	{
		ArrayList<Graph> results = new ArrayList<>(); // stores connected components

		List<Node> nList = graph.getNodeList();
		
		/*
		 * perform dfs on a node to get a connected component and add its edges back
		 * continue until all edges have been added to a connected component
		 */
		setProgress(0D);
		int i = 1;
		while (!nList.isEmpty())
		{
			Node n = nList.remove(0); // get a node
			List<Node> componentNodes = new ArrayList<>(); // nodes of this iteration's connected component
			
			dfs(n, nList, componentNodes); // get nodes of this iteration's connected component
			
			List<Edge> componentEdges = graph.getEdgesBack(componentNodes);
			
			Graph component = new Graph(graph.getName() + " Component " + i, componentNodes, componentEdges);
			results.add(component);

			i++;
			setProgress(1D * (((double) graph.getNodeCount() - nList.size()) / graph.getNodeCount()));
		}

		return results;
	}

	/**
	 * Recursive dfs that stores the nodes of a connected component.
	 * 
	 * @param n      the starting node
	 * @param inodes the nodes awaiting consideration
	 * @param onodes the list of nodes already visited
	 */
	private void dfs(Node n, List<Node> inodes, List<Node> onodes)
	{	
		onodes.add(n); // add starting nodes
		
		for (Node neighbor : n.getNeighbors()) // for all neighbors in undirected graph
		{
			if (!onodes.contains(neighbor)) // neighbor not added yet?
			{
				inodes.remove(neighbor); // remove neighbor from consideration
				dfs(neighbor, inodes, onodes); // call for neighbor
			}
		}
	}
}
