/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.claflin.finder.algo.clustering;

import static edu.claflin.finder.Global.getLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.algo.clustering.struct.fast_greedy_struct.HeapNodeFG;
import edu.claflin.finder.algo.clustering.struct.fast_greedy_struct.IndexedHeapNode;
import edu.claflin.finder.algo.clustering.struct.fast_greedy_struct.IndexedHeapQueue;
import edu.claflin.finder.algo.clustering.struct.fast_greedy_struct.SparseMatrix;
import edu.claflin.finder.algo.clustering.struct.fast_greedy_struct.VectorValue;
import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;

/**
 * FastGreedy Algorithm for Community Detection. Based on python's igraph library.
 * 
 * @see <a href="https://ece-research.unm.edu/ifis/papers/community-moore.pdf">Original Paper</a> 
 * @see <a href="https://www.uvm.edu/pdodds/files/papers/others/2004/newman2004d.pdf">For Weighted Networks</a>  
 * @see <a href="https://github.com/igraph/igraph/blob/master/src/community/fast_modularity.c">Original igraph implementation</a>
 *
 * @author Cesar Martin
 */
public class FastGreedy extends ClusteringAlgorithm
{
	public FastGreedy(ArgumentsBundle args)
	{
		super(args);

		if (getLogger() != null)
		{
			getLogger().logInfo(LogLevel.DEBUG, "FastGreedy algorithm initialized.");
		}

	}

	@Override
	public String toString()
	{
		return "Fast Greedy Algorithm";
	}

	/**
	 * Performs FastGreedy community detection on a given Graph, returning the
	 * clustering with the maximum modularity.
	 *
	 * @param graph the Graph to perform FastGreedy on
	 * @return the list of Graphs representing the clustering with the maximum
	 *         modularity
	 */
	@Override
	public ArrayList<Graph> process(Graph graph)
	{
		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL,
					"FastGreedy: Searching Graph: " + graph.getName() + "\nInitializing Data Structures");
		}

		if (graph.getNodeCount() < 1)
		{
			return new ArrayList<Graph>();
		}

		List<Node> nList = graph.getNodeList();
		List<Edge> eList = graph.getEdgeList(); // original edges

		Collections.sort(nList);

		Communities cms = new Communities(nList, graph, isWeighted());
		double m2 = cms.getM2(); // size of adjacency matrix (not sparse matrix)

		SparseMatrix matrix = new SparseMatrix(nList.size()); // matrix with rows as trees and maxheaps
		IndexedHeapQueue H = new IndexedHeapQueue(); // maxheap that stores maximums of the row maxheaps
		HashMap<Integer, VectorValue> ai = new HashMap<>(); // vector that stores the communities a' values

		// intialize matrix delta Qij
		for (Edge e : eList)
		{
			Node n1 = e.getSource();
			Node n2 = e.getTarget();

			int i = cms.indexOfNode(n1);
			int j = cms.indexOfNode(n2);
			List<Node> c1 = cms.get(i);
			List<Node> c2 = cms.get(j);
			
			Node ns1 = c1.get(0);
			Node ns2 = c2.get(0);
			
			double deg1 = isWeighted() ? ns1.getWeight() : ns1.getDegree();
			double deg2 = isWeighted() ? ns2.getWeight() : ns2.getDegree();
			
			double value = cms.e(c1, c2) - deg1 * deg2 / (Math.pow(m2, 2));

			matrix.add(i, j, value);
			matrix.add(j, i, value);
		}

		// fill the MaxHeap (reversed Priority Queue) with the largest element of each
		// row of the matrix
		for (Integer row : cms.keys())
		{
			if (matrix.getMax(row) != null)
			{
				HeapNodeFG h = matrix.getMax(row);
				H.add(row, h.getJ(), h.getQ());
			}

		}

		// initialize vector ai with a values of the 1 Node communities
		for (Integer i : cms.keys())
		{
			List<Node> cns = cms.get(i);
			ai.put(i, new VectorValue(cns, cms.a(cns)));
		}

		// stop when modularity first decreases, need old communities to compare
		Communities oldcs = cms.copy();
		boolean compare = true; // ignore modularity comparison in the first iteration

		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL, "FastGreedy: Running Greedy Algorithm");
		}

		setProgress(0D);
		// while don't end up with 1 community and queue not empty (more merges to do)
		// and modularity does not decrease
		while (cms.size() > 1 && !H.isEmpty() && (compare || (oldcs.modularity() < cms.modularity())))
		{
			compare = false; // no longer first iteration
			oldcs = cms.copy(); // store old communities for the next comparison

			IndexedHeapNode max = H.poll(); // get max delta Q
			int i = max.getI();
			int j = max.getJ();
			//double q = max.getQ();

			// check the Heapn Node is not bogus
			if (!cms.keys().contains(i) || !cms.keys().contains(j))
			{
				compare = true;
				continue;
			}

			List<Node> ci = cms.get(i); // community to "delete"
			List<Node> cj = cms.get(j); // community that absorbs the "deleted" one

			// "delete" column i (to be deleted) from row j by making it -1
			Double ji = matrix.get(j, i);
			if (ji != null) // delete column i in row j
			{
				matrix.set(j, i, -1);
			}

			// merge trees of j and i into j (Qjk = Qik + Qjk)
			matrix.mergeTrees(j, i);

			// updates trees of j to reflect its increased size
			for (Integer k : cms.keys())
			{
				List<Node> c = cms.get(k);

				if (k != i && k != j && !c.isEmpty())
				{
					if (cms.connectedCommunities(c, ci) && !cms.connectedCommunities(c, cj))
					{
						matrix.setTree(j, k, matrix.get(i, k) - 2 * ai.get(j).getA() * ai.get(k).getA());
					}
					else if (!cms.connectedCommunities(c, ci) && cms.connectedCommunities(c, cj))
					{
						matrix.setTree(j, k, matrix.get(j, k) - 2 * ai.get(i).getA() * ai.get(k).getA());
					}
				}
			}

			// update trees of remaining communities k
			for (Integer k : cms.keys())
			{
				if (k != i && k != j && !cms.get(k).isEmpty())
				{
					Double jk = matrix.get(j, k);
					if (jk != null) // make kj into jk
					{
						matrix.set(k, j, jk);
					}

					// delete column i (to be deleted) of k by making it -1
					Double ki = matrix.get(k, i);
					if (ki != null)
					{
						matrix.set(k, i, -1);
					}
				}
			}

			matrix.rebuildHeap(j); // rebuild j's heap from its tree

			H.set(j, matrix.getMax(j)); // update max of row j in H

			// update max values of ks in H
			for (Integer k : cms.keys())
			{
				List<Node> c = cms.get(k);

				if (k != i && k != j && !c.isEmpty())
				{
					if ((cms.connectedCommunities(c, ci) || cms.connectedCommunities(c, cj)))
					{
						H.set(k, matrix.getMax(k));
					}
				}
			}

			ai.get(j).setA(ai.get(j).getA() + ai.get(i).getA()); // aj = aj + ai
			ai.remove(i);

			matrix.clearRow(i); // clear row i (does not actually change the matrix dimensions)

			cms.mergeCommunities(j, i); // merge communities j and i into j
			setProgress(1D * (((double)graph.getNodeCount() - cms.size()) / graph.getNodeCount()));
		}		

		ArrayList<Graph> communities;

		String algo_name = "FastGreedy";

		if (oldcs.modularity() > cms.modularity())
		{
			communities = this.buildCommunityGraphs(oldcs.getList(), graph, algo_name);
		}

		else
		{
			communities = this.buildCommunityGraphs(cms.getList(), graph, algo_name);
		}

		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL,
					"FastGreedy: Finished Searching. Found: " + communities.size() + " Communities");
		}

		return cull(communities);
	}

}
