package edu.claflin.finder.algo.spanningtree;

import static edu.claflin.finder.Global.getLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;
import edu.claflin.finder.logic.comp.EdgeWeightComparator;
import edu.claflin.finder.struct.DisjointSet;
import edu.claflin.finder.struct.PrioritySet;

public class Kruskal extends ExtremumSpanningTree
{
	/**
	 * Public constructor for initializing the Kruskal with default conditions.
	 * 
	 * @param bundle the ArgumentsBundle containing the instantiation arguments.
	 */
	public Kruskal(ArgumentsBundle bundle)
	{
		super(bundle);

		if (getLogger() != null)
		{
			getLogger().logInfo(LogLevel.DEBUG, "Kruskal Algorithm.");
		}
	}

	@Override
	public String toString()
	{
		return "Kruskal Algorithm";
	}

	/**
	 * {@inheritDoc } <br>
	 * Finds the Minimum Spanning Tree MST with Kruskal's algorithm.
	 * 
	 * @param graph the {@link Graph} object to search through.
	 * @return the ArrayList of Graph objects holding the MST.
	 */
	@Override
	public ArrayList<Graph> process(Graph graph)
	{
		ArrayList<Graph> results = new ArrayList<>();

		if (graph.getNodeCount() < 1)
		{
			return results;
		}

		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL, "Kruskal: Searching Graph: " + graph.getName());
		}

		List<Node> nList = graph.getNodeList(); // original nodes
		List<Edge> eList = graph.getEdgeList(); // original edges

		Collections.sort(nList);

		DisjointSet<Node> partitions = new DisjointSet<>(nList.size());

		// initialize partitions
		for (Node n : nList)
		{
			partitions.makeSet(n);
		}

		PrioritySet<Edge> ps = new PrioritySet<>(new EdgeWeightComparator(max), true);

		for (Edge e : eList)
		{
			if (edgeMeetsThreshold(e))
			{
				ps.add(e);
			}
		}

		List<Edge> edges = new ArrayList<>(); // MST edges

		setProgress(0D);
		// while number of edges does not equal node count - 1
		while (partitions.size() > 1 && !ps.isEmpty())
		{
			Edge e = ps.poll();
			Node source = e.getSource();
			Node target = e.getTarget();

			if (partitions.disjointElements(source, target))
			{
				edges.add(e);
				partitions.merge(source, target);
			}
			setProgress(1D * (((double)graph.getNodeCount() - partitions.size()) / graph.getNodeCount()));
		}		

		Graph T = new Graph(getName(graph, "Kruskal", edges), nList, edges);
		results.add(T);

		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL, "Kruskal: Finished Searching for Minimum Spanning Tree.");
		}

		return cull(results);
	}

	

}
