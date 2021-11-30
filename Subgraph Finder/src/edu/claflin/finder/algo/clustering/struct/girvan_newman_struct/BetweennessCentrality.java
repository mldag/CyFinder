/**
 * Copyright (c) 2008, The JUNG Authors 
 *
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * https://github.com/jrtom/jung/blob/master/LICENSE for a description.
 * Created on Sep 16, 2008
 * 
 */
package edu.claflin.finder.algo.clustering.struct.girvan_newman_struct;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;

/**
 * Computes betweenness centrality for each vertex and edge in the graph.
 * 
 * @see "Ulrik Brandes: A Faster Algorithm for Betweenness Centrality. Journal of Mathematical Sociology 25(2):163-177, 2001."
 */
public class BetweennessCentrality
{
	protected Graph graph;
	protected boolean weighted;
	protected Map<Node, Double> vertex_scores;
	protected Map<Edge, Double> edge_scores;
	protected Map<Node, BetweennessData> vertex_data;

	/**
	 * Calculates betweenness scores based on the all-pairs unweighted shortest
	 * paths in the graph.
	 * 
	 * @param graph the the graph to calculate betweenness centrality
	 * @param weighted whether to consider edge weights
	 */
	public BetweennessCentrality(Graph graph, boolean weighted)
	{
		this.graph = graph;
		this.weighted = weighted;
		initialize();
		
		if (weighted)
		{
			computeBetweenness(new MapBinaryHeap<Node>(new BetweennessComparator()));			
		}
		else
		{
			computeBetweenness(new LinkedList<Node>());
		}

	}

	protected void initialize()
	{
		this.vertex_scores = new HashMap<Node, Double>();
		this.edge_scores = new HashMap<Edge, Double>();
		this.vertex_data = new HashMap<Node, BetweennessData>();

		for (Node v : graph.getNodeList())
			this.vertex_scores.put(v, 0.0);

		for (Edge e : graph.getEdgeList())
			this.edge_scores.put(e, 0.0);
	}

	protected void computeBetweenness(Queue<Node> queue)
	{
		for (Node v : graph.getNodeList())
		{
			// initialize the betweenness data for this new vertex
			for (Node s : graph.getNodeList())
				this.vertex_data.put(s, new BetweennessData());

			vertex_data.get(v).numSPs = 1;
			vertex_data.get(v).distance = 0;

			Stack<Node> stack = new Stack<Node>();
			queue.offer(v);

			while (!queue.isEmpty())
			{
				Node w = queue.poll();
				stack.push(w);
				BetweennessData w_data = vertex_data.get(w);

				for (Edge e : w.getEdges())
				{
					Node x = this.getOpposite(w, e);
					if (x.equals(w))
						continue;
					double wx_weight = weighted ? e.getData() : 1;

					BetweennessData x_data = vertex_data.get(x);
					double x_potential_dist = w_data.distance + wx_weight;

					if (x_data.distance < 0)
					{
						x_data.distance = x_potential_dist;
						queue.offer(x);
					}

					// note:
					// (1) this can only happen with weighted edges
					// (2) x's SP count and incoming edges are updated below
					if (x_data.distance > x_potential_dist)
					{
						x_data.distance = x_potential_dist;
						// invalidate previously identified incoming edges
						// (we have a new shortest path distance to x)
						x_data.incomingEdges.clear();
						// update x's position in queue
						((MapBinaryHeap<Node>) queue).update(x);
					}

				}
				for (Edge e : w.getEdges())
				{
					Node x = this.getOpposite(w, e);
					if (x.equals(w))
						continue;
					double e_weight = weighted ? e.getData() : 1;
					BetweennessData x_data = vertex_data.get(x);
					double x_potential_dist = w_data.distance + e_weight;
					if (x_data.distance == x_potential_dist)
					{
						x_data.numSPs += w_data.numSPs;
						x_data.incomingEdges.add(e);
					}
				}
			}
			while (!stack.isEmpty())
			{
				Node x = stack.pop();

				for (Edge e : vertex_data.get(x).incomingEdges)
				{
					Node w = this.getOpposite(x, e);
					double partialDependency = vertex_data.get(w).numSPs / vertex_data.get(x).numSPs
							* (1.0 + vertex_data.get(x).dependency);
					vertex_data.get(w).dependency += partialDependency;
					double e_score = edge_scores.get(e).doubleValue();
					edge_scores.put(e, e_score + partialDependency);
				}
				if (!x.equals(v))
				{
					double x_score = vertex_scores.get(x).doubleValue();
					x_score += vertex_data.get(x).dependency;
					vertex_scores.put(x, x_score);
				}
			}
		}

		for (Node v : graph.getNodeList())
		{
			double v_score = vertex_scores.get(v).doubleValue();
			v_score /= 2.0;
			vertex_scores.put(v, v_score);
		}
		for (Edge e : graph.getEdgeList())
		{
			double e_score = edge_scores.get(e).doubleValue();
			e_score /= 2.0;
			edge_scores.put(e, e_score);
		}

		vertex_data.clear();
	}

	public Node getOpposite(Node v, Edge e)
	{
		Node source = e.getSource();
		Node target = e.getTarget();

		if (v.equals(source))
		{
			return target;
		}
		else if (v.equals(target))
		{
			return source;
		}
		else
		{
			return null;
		}
	}

	public Double getVertexScore(Node v)
	{
		return vertex_scores.get(v);
	}

	public Double getEdgeScore(Edge e)
	{
		return edge_scores.get(e);
	}

	private class BetweennessData
	{
		double distance;
		double numSPs;
		List<Edge> incomingEdges;
		double dependency;

		BetweennessData()
		{
			distance = -1;
			numSPs = 0;
			incomingEdges = new ArrayList<Edge>();
			dependency = 0;
		}

		@Override
		public String toString()
		{
			return "[d:" + distance + ", sp:" + numSPs + ", p:" + incomingEdges + ", d:" + dependency + "]\n";
		}
	}

	private class BetweennessComparator implements Comparator<Node>
	{
		@Override
		public int compare(Node v1, Node v2)
		{
			return vertex_data.get(v1).distance > vertex_data.get(v2).distance ? 1 : -1;
		}
	}
}
