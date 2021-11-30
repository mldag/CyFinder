package edu.claflin.finder.algo.spanningtree;

import static edu.claflin.finder.Global.getLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;
import edu.claflin.finder.logic.comp.EdgeWeightComparator;
import edu.claflin.finder.struct.PrioritySet;

public class Prim extends ExtremumSpanningTree
{
	private String startNode;

	/**
	 * Public constructor for initializing the Prim with default conditions.
	 * 
	 * @param bundle the ArgumentsBundle containing the instantiation arguments.
	 */
	public Prim(ArgumentsBundle bundle)
	{
		super(bundle);

		try
		{
			startNode = (String) args.getObject("startNode");
		}
		catch (ClassCastException e)
		{
			startNode = null;
		}

		if (getLogger() != null)
		{
			getLogger().logInfo(LogLevel.DEBUG, "Prim Algorithm.");
		}
	}

	@Override
	public String toString()
	{
		return "Prim Algorithm";
	}

	/**
	 * {@inheritDoc } <br>
	 * Finds the Minimum Spanning Tree MST with Prim's algorithm.
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
			getLogger().logAlgo(LogLevel.NORMAL, "Prim: Searching Graph: " + graph.getName());
		}

		List<Node> nList = graph.getNodeList(); // original nodes

		List<Node> nodes = new ArrayList<>(); // MST nodes
		List<Edge> edges = new ArrayList<>(); // MST edges

		PrioritySet<DistanceRecord> ps = new PrioritySet<>(new DistanceRecordComparator(), true);

		startSearch(ps, nodes, nList, nList, graph);

		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL, "Prim: Finished setting initial Edge.");
		}
		
		setProgress(0D);

		// while not added all the nodes to the MST
		while (nList.size() != nodes.size() && !ps.isEmpty())
		{
			DistanceRecord dr = ps.poll(); // get next distance record
			Edge e = dr.getEdge();

			if (!edgeMeetsThreshold(e))
			{
				break;
			}

			int index = dr.getIndex(); // get the record's Node index in nList

			Node vi = nList.get(index); // get the destination node
			nodes.add(vi); // add it to the MST
			edges.add(dr.getEdge()); // add the Edge to the MST

			// get neighbors of vi not in MST
			List<Node> CutNeighbours = vi.getNeighbors();
			CutNeighbours.removeAll(nodes);
			Collections.sort(CutNeighbours);
			/*
			 * Add the neighbor distance records. If new distance record has less weight
			 * than an existing record (including infinity) replace the old record with the
			 * lower weight one
			 */
			for (Node n : CutNeighbours)
			{
				ps.add(new DistanceRecord(nList.indexOf(n), graph.getEdge(vi, n)));
			}

			// disconnected Graph
			if (nList.size() != nodes.size() && ps.isEmpty())
			{
				// get the remaining Nodes
				List<Node> remaniningNodes = new ArrayList<>(nList); // get all nodes
				remaniningNodes.removeAll(nodes); // remove tree nodes from all nodes to get remaining

				// start new search
				startSearch(ps, nodes, remaniningNodes, nList, graph);
			}
			
			setProgress((double)nodes.size() / nList.size());
		}
		
		Graph T = new Graph(getName(graph, "Prim", edges), nList, edges);
		results.add(T);		

		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL, "Prim: Finished Searching for Minimum Spanning Tree.");
		}

		return cull(results);
	}

	private void startSearch(PrioritySet<DistanceRecord> ps, List<Node> treeList, List<Node> remainingNodes,
			List<Node> nList, Graph graph)
	{
		if (!remainingNodes.isEmpty())
		{
			Collections.sort(remainingNodes);

			Node start;

			if (startNode != null)
			{
				start = graph.getNode(startNode);

				if (start == null || !remainingNodes.contains(start)) // no start Node, use first
				{
					start = remainingNodes.get(0);
				}
			}
			else
			{
				start = remainingNodes.get(0);
			}

			treeList.add(start); // add initial Node

			List<Node> v0n = start.getNeighbors(); // get its neighbors
			Collections.sort(v0n);
			for (Node n : v0n) // add Edges to the consideration
			{
				Edge e = graph.getEdge(start, n);
				ps.add(new DistanceRecord(nList.indexOf(n), e));
			}
		}

	}

	private class DistanceRecord
	{
		private int nodeIndex;
		private Edge e;

		public DistanceRecord(int n, Edge ed)
		{
			nodeIndex = n;
			e = ed;
		}

		public int getIndex()
		{
			return nodeIndex;
		}

		public Edge getEdge()
		{
			return e;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof DistanceRecord)
			{
				DistanceRecord that = (DistanceRecord) o;

				return this.getIndex() == that.getIndex();
			}
			return false;
		}
	}

	private class DistanceRecordComparator implements Comparator<DistanceRecord>
	{
		@Override
		public int compare(DistanceRecord arg0, DistanceRecord arg1)
		{
			EdgeWeightComparator comp = new EdgeWeightComparator(max);
			return comp.compare(arg0.getEdge(), arg1.getEdge());
		}

	}
}
