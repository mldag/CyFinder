/*
* Copyright (c) 2003, The JUNG Authors 
*
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* https://github.com/jrtom/jung/blob/master/LICENSE for a description.
*/
package edu.claflin.finder.algo.clustering.struct.girvan_newman_struct;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import edu.claflin.finder.logic.Node;

/**
 * Finds all weak components in a graph as sets of vertex sets. A weak component
 * is defined as a maximal subgraph in which all pairs of vertices in the
 * subgraph are reachable from one another in the underlying undirected
 * subgraph.
 * <p>
 * This implementation identifies components as sets of vertex sets. To create
 * the induced graphs from any or all of these vertex sets, see
 * <code>algorithms.filters.FilterUtils</code>.
 * <p>
 * Running time: O(|Node| + |E|) where |Node| is the number of vertices and |E| is the
 * number of edges.
 * 
 * @author Scott White
 */
public class WeakComponentClusterer
{
	/**
	 * Extracts the weak components from a graph.
	 * 
	 * @param nList the list of Nodes of the Graph
	 * @return the list of weak components
	 */
	public Set<Set<Node>> apply(List<Node> nList)
	{

		Set<Set<Node>> clusterSet = new HashSet<Set<Node>>();

		HashSet<Node> unvisitedVertices = new HashSet<Node>(nList);

		while (!unvisitedVertices.isEmpty())
		{
			Set<Node> cluster = new HashSet<Node>();
			Node root = unvisitedVertices.iterator().next();
			unvisitedVertices.remove(root);
			cluster.add(root);

			Queue<Node> queue = new LinkedList<Node>();
			queue.add(root);

			while (!queue.isEmpty())
			{
				Node currentVertex = queue.remove();
				List<Node> neighbors = currentVertex.getNeighbors();

				for (Node neighbor : neighbors)
				{
					if (unvisitedVertices.contains(neighbor))
					{
						queue.add(neighbor);
						unvisitedVertices.remove(neighbor);
						cluster.add(neighbor);
					}
				}
			}
			clusterSet.add(cluster);
		}
		return clusterSet;
	}
}
