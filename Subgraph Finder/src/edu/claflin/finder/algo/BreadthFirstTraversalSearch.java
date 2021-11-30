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
 * Processes a {@link Graph} searching for subgraphs by performing a breadth
 * first search on each node of the graph.
 * 
 * @author Charles Allen Schultz II
 * @version 3.4 February 4, 2016
 */
public class BreadthFirstTraversalSearch extends Algorithm
{
	private Comparator<Edge> comparator;

	/**
	 * Public constructor for initializing the BreadthFirstTraversalSearch with
	 * default conditions.
	 * 
	 * @param bundle the ArgumentsBundle containing the instantiation arguments.
	 */
	public BreadthFirstTraversalSearch(ArgumentsBundle bundle)
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
			getLogger().logInfo(LogLevel.DEBUG, "Breadth First Traversal " + "Search algorithm instantiated.");
		}
	}

	@Override
	public String toString()
	{
		return "Breadth First Search Algorithm";
	}

	/**
	 * {@inheritDoc } <br>
	 * Finds most Subgraphs by performing a breadth first search on the node tree.
	 * It is unknown if it finds all or only most of the subgraphs due to the nature
	 * of the algorithm. It is expected, however, that it would miss certain node
	 * groupings.
	 * 
	 * @param graph the {@link Graph} object to search through.
	 * @return the Graph array holding all found subgraphs.
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
			getLogger().logAlgo(LogLevel.NORMAL, "BFTS: Searching Graph: " + graph.getName());
		}

		for (Node node : graph.getNodeList())
		{

			if (getLogger() != null)
			{
				getLogger().logAlgo(LogLevel.VERBOSE, "BFTS: Setting Node as root: " + node.toString());
			}

			subGraphs.add(searchNode(graph, node));
			setProgress(graph.getNodeIndex(node) * 1D / graph.getNodeCount());
		}

		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL, "BFTS: Finished Searching Graph. SGs found: " + subGraphs.size());
		}

		return cull(subGraphs);
	}

	/**
	 * Helper method to search for the SubGraphs in Breadth First Search. Creates and manages a queue of
	 * nodes to search through. Is called on each node in the tree.
	 * 
	 * @param graph the Graph object to search through.
	 * @param node  the node to use as the root.
	 * @return the Graph object representing the found subgraph.
	 */
	private Graph searchNode(Graph graph, Node node)
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

		// set subgraph name
		ConditionedGraph subGraph = new ConditionedGraph(graph.getName() + " BFS " + node,
				args.getConditionsList());
		// add parameter node to subgraph
		subGraph.addNode(node);

		Queue<Edge> queue;

		if (comparator != null)
			queue = new PrioritySet<>(comparator, true);
		else
			queue = new LinkedList<>();

		// add edges to the queue
		LinkedList<Node> visited = new LinkedList<>();

		if (true)// for testing
		{
			// entry point in to the search, an edge that starts from nothing and leads to
			// the root node
			queue.add(new Edge(null, node, 0.0, false));
		}

		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.DEBUG, "BFTS: Initialized queue.");
		}

		while (!queue.isEmpty())
		{
			Edge currentEdge = queue.remove();
			Node current;

			// since the queue is for edges, need to get the node that we are currently on
			// from the edge
			if (currentEdge.isUndirected())
			{
				if (!visited.contains(currentEdge.getSource()))
				{
					current = currentEdge.getSource();
				}
				else if (!visited.contains(currentEdge.getTarget()))
				{
					current = currentEdge.getTarget();
				}
				else
				{
					continue;
				}

			}
			else
			{
				current = currentEdge.getTarget();
			}

			// mark current node as visited
			visited.add(current);

			if (getLogger() != null)
			{
				getLogger().logAlgo(LogLevel.DEBUG, "BFTS: Scanning Node: " + current.toString());
			}

			// Check to see if the current node has any edges back into the
			// graph and attempt add them in sequence.
			// Not unnecessary if the method is NOT preservative.

			// Adds elements to subgraph to be returned
			List<Node> cAdjacency = current.getNeighbors();
			List<Edge> cEdges = new ArrayList<>();

			cAdjacency.retainAll(subGraph.getNodeList());
			cAdjacency.stream().forEach(n ->
			{
				Edge e1 = graph.getEdge(current, n);
				if (!cEdges.contains(e1) && e1 != null)
				{
					cEdges.add(e1);

					Edge e2 = graph.getEdge(n, current);

					if (e2 != null & finalUndirectedClique && !e1.isUndirected())
					{
						cEdges.add(e2);
					}
				}

			});
			cEdges.removeAll(subGraph.getEdgeList());
			cEdges.stream().forEach(e -> subGraph.addEdge(e));

			// checks for adjacent nodes
			for (Node neighbor : current.getNeighbors())
			{
				List<Node> nList = new ArrayList<>();
				List<Edge> eList = new ArrayList<>();
				if (!visited.contains(neighbor))
				{
					// adds each adjacent node to the node list and adding an edge
					// between the two nodes
					nList.add(neighbor);
					Edge e1 = graph.getEdge(current, neighbor);

					if (e1 != null)
					{
						eList.add(e1);

						Edge e2 = graph.getEdge(neighbor, current);
						if (e2 != null & finalUndirectedClique && !e1.isUndirected())
						{
							eList.add(e2);
						}
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
						Edge e1 = graph.getEdge(neighbor, n);
						if (!eList.contains(e1) && e1 != null)
						{
							eList.add(e1);

							Edge e2 = graph.getEdge(n, neighbor);
							if (e2 != null && finalUndirectedClique && !e1.isUndirected())
							{
								eList.add(e2);
							}
						}
					});
				}
				// Cull already present elements from the lists.
				nList.removeAll(subGraph.getNodeList());
				eList.removeAll(subGraph.getEdgeList());

				// Do the addition, and, if successful, add the node to the
				// queue.
				if (!visited.contains(neighbor))
				{
					if (subGraph.addPartialGraph(nList, eList))
					{
						Edge e1 = graph.getEdge(current, neighbor);

						if (e1 != null)
						{
							queue.add(e1);

							Edge e2 = graph.getEdge(neighbor, current);
							if (e2 != null && finalUndirectedClique && !e1.isUndirected())
							{
								eList.add(e2);
							}
						}

					}
				}
			}

		}

		return subGraph;
	}
}
