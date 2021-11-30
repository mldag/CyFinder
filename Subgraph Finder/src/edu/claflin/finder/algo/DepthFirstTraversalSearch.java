package edu.claflin.finder.algo;

import static edu.claflin.finder.Global.getLogger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Condition;
import edu.claflin.finder.logic.ConditionedGraph;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;
import edu.claflin.finder.logic.cond.CliqueCondition;
import edu.claflin.finder.struct.PrioritySet;

/**
 * Processes a {@link Graph} searching for bipartite subgraphs by performing a
 * depth first search on each node.
 * 
 * @author Charles Allen Schultz II
 * @version 3.4 February 4, 2016
 */
public class DepthFirstTraversalSearch extends Algorithm
{
	private Comparator<Edge> comparator;

	/**
	 * Public constructor for initializing the DepthFirstTraversalSearch with
	 * default conditions.
	 * 
	 * @param bundle the ArgumentsBundle containing the instantiation arguments.
	 */
	public DepthFirstTraversalSearch(ArgumentsBundle bundle)
	{
		super(bundle);
		comparator = null;

		try
		{
			Object obj = args.getObject(ArgumentsBundle.COMMON_ARGS.EDGE_WEIGHT_COMPARATOR.toString());
			if (obj != null)
			{
				comparator = (Comparator<Edge>) obj;
			}
		}
		catch (ClassCastException e)
		{ // In future, maybe change ArgumentsBundle to auto-cast things appropriately and
			// restrict elements.
			if (getLogger() != null)
			{
				getLogger().logAlgo(LogLevel.NORMAL, "BFTS: Error casting EDGE_WEIGHT_COMPARATOR");
			}
		}

		if (getLogger() != null)
		{
			getLogger().logInfo(LogLevel.DEBUG, "Depth First Traversal Search " + "algorithm initialized.");
		}
	}

	@Override
	public String toString()
	{
		return "Depth First Search Algorithm";
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
		try
		{
			Object obj = args.getObject(ArgumentsBundle.COMMON_ARGS.EDGE_WEIGHT_COMPARATOR.toString());
			if (obj != null)
			{
				comparator = (Comparator<Edge>) obj;
			}
		}
		catch (ClassCastException e)
		{ // In future, maybe change ArgumentsBundle to auto-cast things appropriately and
			// restrict elements.
			if (getLogger() != null)
			{
				getLogger().logAlgo(LogLevel.NORMAL, "BFTS: Error casting EDGE_WEIGHT_COMPARATOR");
			}
		}

		ArrayList<Graph> subGraphs = new ArrayList<>();

		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL, "DFTS: Searching Graph: " + graph.getName());
		}

		for (Node current : graph.getNodeList())
		{

			if (getLogger() != null)
			{
				getLogger().logAlgo(LogLevel.VERBOSE, "DFTS: Setting Node as root: " + current.toString());
			}

			Graph subGraph = new ConditionedGraph(graph.getName() + " DFS " + current,
					args.getConditionsList());
			ArrayList<Node> visited = new ArrayList<>();
			visited.add(current);
			subGraphs.add(searchNode(graph, subGraph, current, visited));
			setProgress(graph.getNodeIndex(current) * 1D / graph.getNodeCount());
		}

		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL, "DFTS: Finished Searching Graph. SGs found: " + subGraphs.size());
		}

		return cull(subGraphs);
	}

	/**
	 * Helper method to search for the SubGraphs in Depth First search. Loops recursively to search for
	 * subgraphs. Is called on each node in the tree.
	 * 
	 * @param graph    the Graph object to search through.
	 * @param subGraph the Graph object indicating the subgraph to store found nodes
	 *                 and edges in. (Used due to the recursive nature of the
	 *                 algorithm.)
	 * @param node     the current node to search.
	 * @param visited  the List containing the visited nodes.
	 * @return the Graph object representing the found subgraph.
	 */
	private Graph searchNode(Graph graph, Graph subGraph, Node node, List<Node> visited)
	{
		Boolean preservative = args.getBoolean(ArgumentsBundle.COMMON_ARGS.EDGE_PRESERVATION.toString());
		boolean undirectedClique = false;

		for (Condition c : this.args.getConditionsList())
		{

			if (c instanceof CliqueCondition)
			{
				undirectedClique = true;
			}
		}
		final boolean finalUndirectedClique = undirectedClique;

		if (!subGraph.getNodeList().contains(node))
			subGraph.addNode(node);

		// Check to see if the current node has any edges back into the
		// graph and attempt add them in sequence.
		// NOT unnecessary if the algorithm is NOT preservative.
		List<Node> cAdjacency = node.getNeighbors();
		List<Edge> cEdges = new ArrayList<>();
		cAdjacency.retainAll(subGraph.getNodeList());
		cAdjacency.stream().forEach(n ->
		{
			Edge e1 = graph.getEdge(node, n);

			if (e1 != null)
			{
				cEdges.add(e1);

				Edge e2 = graph.getEdge(n, node);
				if (e2 != null && finalUndirectedClique && !e1.isUndirected())
				{

					cEdges.add(e2);
				}
			}

		});
		cEdges.removeAll(subGraph.getEdgeList());
		cEdges.stream().forEach(e -> subGraph.addEdge(e));

		// Crazy queue mechanism for setting the ordering of explored nodes.
		Queue<Edge> queue;

		if (comparator != null)
			queue = new PrioritySet<>(comparator, true);
		else
			queue = new LinkedList<>();
		
		node.getNeighbors().stream().forEach(n ->
		{
			Edge e1 = graph.getEdge(node, n);
			if (!visited.contains(n) && e1 != null)
			{
				queue.add(e1);

				Edge e2 = graph.getEdge(n, node);
				if (e2 != null && finalUndirectedClique && !e1.isUndirected())
				{
					cEdges.add(e2);
				}
			}

		});

		while (!queue.isEmpty())
		{
			Edge currentEdge = queue.remove();
			Node neighbor;

			if (currentEdge.isUndirected())
			{
				if (!visited.contains(currentEdge.getSource()))
				{
					neighbor = currentEdge.getSource();
				}
				else if (!visited.contains(currentEdge.getTarget()))
				{
					neighbor = currentEdge.getTarget();
				}
				else
				{
					continue; // eat the edge since it's already been "explored"
				}
			}
			else
			{
				neighbor = currentEdge.getTarget();
			}

			List<Node> nList = new ArrayList<>();
			List<Edge> eList = new ArrayList<>();
			nList.add(neighbor);
			Edge e1 = graph.getEdge(node, neighbor);

			if (e1 != null)
			{
				eList.add(e1);

				Edge e2 = graph.getEdge(neighbor, node);
				if (e2 != null && finalUndirectedClique && !e1.isUndirected())
				{

					eList.add(e2);
				}

			}

			// If preservative, add a node and all it's edges back into the
			// graph all at once.
			if (preservative != null && preservative)
			{
				List<Node> nAdjacency = neighbor.getNeighbors();
				nAdjacency.retainAll(subGraph.getNodeList());
				nAdjacency.stream().forEach(n ->
				{
					Edge e3 = graph.getEdge(neighbor, n);

					if (e3 != null)
					{
						eList.add(e3);

						Edge e4 = graph.getEdge(n, neighbor);
						if (e4 != null & finalUndirectedClique && !e3.isUndirected())
						{

							cEdges.add(e4);
						}
					}
				});
			}

			// Remove any allowable additions that are already present.
			nList.removeAll(subGraph.getNodeList());
			eList.removeAll(subGraph.getEdgeList());

			// Do the addition, and, if successful, recurse on the node.
			if (subGraph.addPartialGraph(nList, eList))
			{
				if (!visited.contains(neighbor))
				{
					visited.add(neighbor);
					searchNode(graph, subGraph, neighbor, visited);
				}
			}
		}
		return subGraph;
	}
}
