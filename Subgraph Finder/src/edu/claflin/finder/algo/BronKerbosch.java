package edu.claflin.finder.algo;

import static edu.claflin.finder.Global.getLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;

/**
 * Bron-Kersboch Algorithm to find Maximum Cliques and Bicliques.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Bron%E2%80%93Kerbosch_algorithm">Wikipedia Summary</a>
 * @see <a href="https://dl.acm.org/doi/10.1145/362342.362367">Original Paper</a>
 * @see <a href="https://link.springer.com/article/10.1007%2FBF00991836">Pivoting Variant</a>
 */
public class BronKerbosch extends Algorithm
{
	private boolean bipartite;

	/**
	 * Public constructor for initializing the Bron Kersbboch with default
	 * conditions.
	 * 
	 * @param bundle the ArgumentsBundle containing the instantiation arguments.
	 */
	public BronKerbosch(ArgumentsBundle bundle)
	{
		super(bundle);
		bipartite = false;
	}

	@Override
	public String toString()
	{
		this.bipartite = this.args.getBoolean("bipartite") == null ? false : this.args.getBoolean("bipartite");
		return "Bron-Kersboch Algorithm " + (bipartite ? "for Bicliques" : "for Cliques");
	}

	/**
	 * {@inheritDoc } <br>
	 * Finds the Maximum Cliques of a Graph or the Maximum Biclique of a Bipartite
	 * Graph if the bipartite argument is set to true.
	 * 
	 * @param graph the {@link Graph} object to search through.
	 * @return the ArrayList of Graph objects holding all found subgraphs.
	 */
	@Override
	public ArrayList<Graph> process(Graph graph)
	{
		this.bipartite = this.args.getBoolean("bipartite") == null ? false : this.args.getBoolean("bipartite");

		if (getLogger() != null)
		{
			getLogger().logInfo(LogLevel.DEBUG,
					"BronKerbosch algorithm initialized for " + (bipartite ? "for Bipartite" : "for Cliques") + " .");
		}

		ArrayList<Graph> results = new ArrayList<>();

		if (graph.getNodeCount() <= 1)
		{
			return results;
		}

		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL, "BronKerbosch: Searching Graph: " + graph.getName());
		}

		if (bipartite) // look for Bipartites?
		{
			results = (ArrayList<Graph>) maximumBicliques(graph);
		}
		else // look for Cliques
		{
			results = (ArrayList<Graph>) maximumCliques(graph);
		}

		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL,
					"BronKerbosch: Finished Searching Graph. SGs found: " + results.size());
		}

		return cull(results);
	}

	/**
	 * Given a graph, finds all complete subgraphs, and then returns the largest
	 * one.
	 *
	 * @param graph the graph
	 * @return the largest complete subgraph (clique)
	 */
	private List<Graph> maximumCliques(Graph graph)
	{
		List<Graph> cliques = bronKerboschClique(graph);
		return nameGraphs(getMaximum(cliques), graph.getName(), false);
	}

	/**
	 * Gets the Maximum Complete Bipartite Graphs by Node count from the input Graph
	 * using Bron-Kersboch.
	 * 
	 * @param graph the input Graph
	 * @return the Maximum Complete Bipartite Graphs
	 */
	private List<Graph> maximumBicliques(Graph graph)
	{
		List<Graph> bicliques = bronKerboschBiclique(graph); // get candidates
		return nameGraphs(getMaximum(bicliques), graph.getName(), true);
	}

	/**
	 * Given a graph, finds all complete subgraphs.
	 *
	 * @param graph the graph
	 * @return a List of complete subgraphs, unordered
	 */
	private List<Graph> bronKerboschClique(Graph graph)
	{
		List<Graph> results = new ArrayList<>();
		List<List<Node>> cliques = new ArrayList<>();
		List<Node> candidates = graph.getNodeList();

		bronKerbosch(cliques, new ArrayList<Node>(), candidates, new ArrayList<Node>());

		for (List<Node> clique : cliques)
		{

			if (!clique.isEmpty())
			{
				Graph g = new Graph("Clique", clique, graph.getEdgesBack(clique));
				results.add(g);
			}
		}

		return results;
	}

	/**
	 * Finds all bicliques within a bipartite graph.
	 *
	 * This is done by adding edges between every nodes in a partite set in both
	 * partite sets, using the above algorithm, and then removing all the added
	 * edges from the results.
	 *
	 * THIS ONLY WORKS ON GRAPHS THAT ARE BIPARTITE!
	 *
	 * @param graph the bipartite graph
	 * @return array list of bicliques
	 */
	private List<Graph> bronKerboschBiclique(Graph graph)
	{
		// Need copy of the graph as we are going to modify this as part of the
		// algorithm
		Graph graphCopy = graph.uniqueCopy();
		List<Graph> results = new ArrayList<>();

		if (!graphCopy.isBipartite())
		{
			return results;
		}

		ArrayList<ArrayList<Node>> bSets = graphCopy.getPartiteSets();

		// add artificial Edges between Nodes in the same partite Sets
		addEdgesInParititeSet(graphCopy, bSets.get(0));
		addEdgesInParititeSet(graphCopy, bSets.get(1));

		results = bronKerboschClique(graphCopy); // call recursive method

		for (int i = 0; i < results.size(); i++)
		{
			Graph g = results.get(i);

			// remove artificial Edges
			removeEdgesInPartiteSet(g, bSets.get(0));
			removeEdgesInPartiteSet(g, bSets.get(1));

			// remove cliques that consisted solely of artificial Edges
			if (g.getNodeCount() > 1 && g.getEdgeCount() < 1)
			{
				results.remove(i);
				i--;
			}
		}
		return results;
	}

	/**
	 * Recursive helper method to do the above.
	 *
	 * @param results    result list. Passed between recursive calls to create
	 *                   output
	 * @param clique     the current Clique being constructed; Graph r in Wikipedia
	 *                   link
	 * @param candidates the candidate Nodes for the Clique; Graph p in Wikipedia
	 *                   link
	 * @param excluded   the Nodes to be excluded from the Clique;
	 *                   Graph x in Wikipedia link
	 */
	private void bronKerbosch(List<List<Node>> results, List<Node> clique, List<Node> candidates, List<Node> excluded)
	{
		// no candidates or excluded Nodes, found a clique
		if (candidates.isEmpty() && excluded.isEmpty())
		{
			results.add(new ArrayList<Node>(clique));
		}
		else
		{
			// we pivot to a Node in P (candidates) union X (excluded)
			List<Node> pux = new ArrayList<>();
			pux.addAll(candidates);
			pux.addAll(excluded);

			// we choose a Node with maximum neighbors in P (candidates)
			Node u = pux.stream().max((a, b) ->
			{
				List<Node> an = a.getNeighbors();
				List<Node> bn = b.getNeighbors();

				an.retainAll(candidates);
				bn.retainAll(candidates);

				return an.size() - bn.size();
			}).get();

			// Clone lists to not modify original because otherwise we are modifying the
			// original lists
			// remove the neighbors of the pivot from the candidates to minimize the number
			// of recursive calls.
			// this can improve performance significantly.
			List<Node> candidates2 = new ArrayList<>(candidates);
			candidates2.removeAll(u.getNeighbors());
			Collections.sort(candidates2);

			// Iterate through each candidate vertex
			for (Node n : candidates2)
			{
				// Clone lists to not modify original because otherwise we are modifying the
				// original lists
				List<Node> clique2 = new ArrayList<>(clique);
				List<Node> candidates3 = new ArrayList<>(candidates);
				List<Node> excluded2 = new ArrayList<>(excluded);

				// Call algorithm on union(r2, n), intersection(p2, neighbors(n)),
				// intersection(x2, neighbors(n)).
				clique2.add(n);

				List<Node> nnb = n.getNeighbors();
				excluded2.retainAll(nnb);
				candidates3.retainAll(nnb);

				bronKerbosch(results, clique2, candidates3, excluded2);

				// Modify the original list for the next iteration
				// Remove node n from the candidates P, and then add n to the excluded X
				candidates.remove(n);
				excluded.add(n);
			}
		}
	}

	/**
	 * Adds edges to a Graph between all Nodes in a partite set. Remove these edges later 
	 * with {@link removeEdgesInPartiteSet}.
	 *
	 * @param graph the Graph to add the Edges to
	 * @param list the partite set
	 */
	private void addEdgesInParititeSet(Graph graph, List<Node> list)
	{
		List<Node> nodeList = graph.getNodeList();

		for (int outer = 0; outer < list.size(); outer++)
		{
			for (int inner = outer + 1; inner < list.size(); inner++)
			{
				Node first = list.get(outer);
				Node second = list.get(inner);

				if (nodeList.contains(first) && nodeList.contains(second))
				{
					Node s = nodeList.get(nodeList.indexOf(first));
					Node t = nodeList.get(nodeList.indexOf(second));
					graph.addEdge(new Edge(s, t, 0.0, true));
				}
			}
		}
	}

	/**
	 * Removes edges in a Graph between all Nodes in a partite set.
	 *
	 * @param graph the Graph to remove the Edges from
	 * @param list the partite set
	 */
	private void removeEdgesInPartiteSet(Graph graph, List<Node> list)
	{
		List<Edge> edgeList = graph.getEdgeList();

		for (int outer = 0; outer < list.size(); outer++)
		{
			for (int inner = outer + 1; inner < list.size(); inner++)
			{
				Edge toRemove = null;
				for (Edge e : edgeList)
				{
					Node src = list.get(outer);
					Node dest = list.get(inner);
					if (((e.getSource().equals(src) && e.getTarget().equals(dest)
							|| (e.getSource().equals(dest) && e.getTarget().equals(src)))))
					{
						toRemove = e;
						break;
					}
				}

				// Removal outside of the loop to prevent ConcurrentModificationException
				if (toRemove != null)
				{
					graph.removeEdge(toRemove);
				}
			}
		}
	}

	private List<Graph> getMaximum(List<Graph> graphs)
	{
		List<Graph> maximalGraphs = new ArrayList<>();

		final int maximumSize = graphs.stream().mapToInt(g -> g.getNodeCount()).max().orElse(-1); // get max size
		// filter out lower sizes
		maximalGraphs = graphs.stream().filter(g -> g.getNodeCount() == maximumSize).collect(Collectors.toList());

		return maximalGraphs;
	}

	private List<Graph> nameGraphs(List<Graph> graphs, String originalName, boolean bipartite)
	{
		for (int index = 1; index <= graphs.size(); index++)
		{
			Graph g = graphs.get(index - 1);
			graphs.set(index - 1,
					g.uniqueCopy(originalName + " " + (bipartite ? "Maximum Biclique " : "Maximum Clique ") + index));
		}

		return graphs;
	}

}
