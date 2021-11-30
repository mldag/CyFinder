package edu.claflin.finder.algo.clustering;

import static edu.claflin.finder.Global.getLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.algo.clustering.struct.walk_trap_struct.HeapNodeWT;
import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;

/**
 * Walktrap Algorithm for Community Detection. Random walks from a Node in
 * a community tend to stay in the same Node. Based on python's igraph library.
 * 
 * @see <a href="https://homepage.cs.uiowa.edu/~ghosh/communities.pdf">Original Paper</a>
 * @see <a href="https://github.com/igraph/igraph/tree/master/src/community/walktrap">Original igraph implementation</a>
 * 
 * @author Cesar Martin and Adolfo Perera
 *
 */
public class Walktrap extends ClusteringAlgorithm
{
	private int steps; // length of the walk
	/*
	 * Probability vectors. The Integer index corresponds to the community with
	 * index in the list instance variable. The vector stores the probability of
	 * going from the community to a given Node in a walk of length steps instance
	 * variable.
	 */
	private HashMap<Integer, HashMap<Node, Double>> Pv; // store

	public Walktrap(ArgumentsBundle args)
	{
		super(args);
		steps = args.getInteger("steps") == null ? 4 : args.getInteger("steps"); // length of the walk
		Pv = new HashMap<>();

		if (getLogger() != null)
		{
			getLogger().logInfo(LogLevel.DEBUG, "Walktrap algorithm initialized.");
		}

	}

	@Override
	public String toString()
	{
		return "Walktrap Algorithm";
	}

	@Override
	public ArrayList<Graph> process(Graph graph)
	{
		if (graph.getNodeCount() < 1)
		{
			return new ArrayList<Graph>();
		}

		ArrayList<Graph> communities = new ArrayList<>(); // stores the communities

		List<Node> nList = graph.getNodeList();
		Collections.sort(nList);

		// initialize 1-node communities
		Communities cms = new Communities(nList, graph, isWeighted());

		// store initial probabilities for 1-node communities
		for (int i = 0; i < nList.size(); i++)
		{
			Pv.put(i, cprobs(cms.get(i), graph, nList));
		}

		PriorityQueue<HeapNodeWT> q = new PriorityQueue<>(); // stores delta sigmas

		/*
		 * Store initial delta sigmas for 1-node communities. "j > i" means we store as
		 * an adjacency list format (rather than matrix) because we don't want loops to
		 * self and reverse duplicates
		 */

		List<Integer> keys = cms.keys();

		for (int outer = 0; outer < keys.size(); outer++)
		{
			int outerKey = keys.get(outer);

			for (int inner = outer + 1; inner < keys.size(); inner++)
			{
				int innerKey = keys.get(inner);

				if (cms.connectedCommunities(cms.get(outerKey), cms.get(innerKey)))
				{
					q.add(new HeapNodeWT(outerKey, innerKey, delta_sigma(outerKey, innerKey, nList, cms)));
				}
			}
		}

		Communities maxCommunities = cms.copy(); // stores the community with the max modularity
		double maxModularity = cms.modularity(); // stores max modularity to avoid computations

		setProgress(0D);
		// while not reached 1 community with all Nodes && queue not empty (more merges
		// to do)
		while (cms.size() > 1 && !q.isEmpty())
		{
			HeapNodeWT min = q.poll(); // get min delta sigma

			if (min == null)
				continue;

			int index1 = min.getIndex1();
			int index2 = min.getIndex2();
			double sigma = min.getValue();

			List<Node> C1 = new ArrayList<>(cms.get(index1));
			List<Node> C2 = new ArrayList<>(cms.get(index2));

			// only merge adjacent communities
			if (index1 != index2 && cms.connectedCommunities(C1, C2))
			{
				/*
				 * merge probabilities of index1 and index2 communities into index1 delete
				 * index2 community probabilities. communities 1 and 2 must have not been merged
				 * by this point.
				 */
				Pv.put(index1, merge_probs(index1, index2, cms));
				Pv.remove(index2);

				/*
				 * Find all communities C adjacent to the merged community C1 union C2. For
				 * example: 
				 * C1 -> C|              | C3 -> C   
				 * .......|              | .......
				 * .......| This becomes | .......
				 * C -> C2|              | (removed cause don't need duplicates (C -> C3 is a duplicate)
				 * .......|              | .......
				 */
				List<HeapNodeWT> tree_nodes = new ArrayList<>(q);
				HashMap<Integer, MutualConnectionsRecord> mcs = new HashMap<>();

				for (HeapNodeWT info : tree_nodes)
				{
					int k = -1;

					// C1 to C
					if (info.getIndex1() == index1)
					{
						k = info.getIndex2();

						if (mcs.containsKey(k)) // already got C in the list?
						{
							/*
							 * this means C was added because of C2, so set the C1 that is currently null.
							 */
							mcs.get(k).setc1(info.getValue());
						}
						else // C is new detected neighbour of the merge
						{
							MutualConnectionsRecord r = new MutualConnectionsRecord(k);
							r.setc1(info.getValue());
							mcs.put(k, r);
						}

					}
					// C2 to C
					else if (info.getIndex1() == index2)
					{
						k = info.getIndex2();

						if (mcs.containsKey(k)) // already got C in the list?
						{
							/*
							 * this means C was added because of C1, so set the C2 that is currently null.
							 */
							mcs.get(k).setc2(info.getValue());
						}
						else // C is new detected neighbour of the merge
						{
							MutualConnectionsRecord r = new MutualConnectionsRecord(k);
							r.setc2(info.getValue());
							mcs.put(k, r);
						}
					}
					// C to C1
					else if (info.getIndex2() == index1 && cms.containsKey(info.getIndex1()))
					{
						k = info.getIndex1();

						if (mcs.containsKey(k)) // already got C in the list?
						{
							/*
							 * this means C was added because of C2, so set the C1 that is currently null.
							 */
							mcs.get(k).setc1(info.getValue());
						}
						else // C is new detected neighbor of the merge
						{
							MutualConnectionsRecord r = new MutualConnectionsRecord(k);
							r.setc1(info.getValue());
							mcs.put(k, r);
						}
					}
					// C to C2
					else if (info.getIndex2() == index2 && cms.containsKey(info.getIndex1()))
					{
						k = info.getIndex1();

						if (mcs.containsKey(k)) // already got C in the list?
						{
							/*
							 * this means C was added because of C1, so set the C2 that is currently null.
							 */
							mcs.get(k).setc2(info.getValue());
						}
						else // C is new detected neighbour of the merge
						{
							MutualConnectionsRecord r = new MutualConnectionsRecord(k);
							r.setc2(info.getValue());
							mcs.put(k, r);
						}
					}

					// current C was detected as a neighbour?
					if (k != -1)
					{
						// remove its sigma from the Queue since it needs to be updated
						remove_node(info, q);
					}

				}

				// merge C1 and C2 into the C1
				cms.mergeCommunities(index1, index2);

				// add new delta sigmas of adjacent communities to the heap
				for (Integer k : mcs.keySet())
				{
					MutualConnectionsRecord m = mcs.get(k);
					List<Node> C = cms.get(k);

					// community was adjacent to both C1 and C2, use constant time calculation
					if (m.hasc1() && m.hasc2())
					{
						// LanceWilliamsJambu
						q.add(new HeapNodeWT(Math.min(index1, k), Math.max(index1, k),
								((C1.size() + C.size()) * m.get_c1sigma() + (C2.size() + C.size()) * m.get_c2sigma()
										- C.size() * sigma) / (C1.size() + C2.size() + C.size())));
					}
					else if (m.hasc1()) // only adjacent to C1, need to calculate from scratch
					{
						q.add(new HeapNodeWT(Math.min(index1, k), Math.max(index1, k),
								delta_sigma(index1, k, nList, cms)));//
					}
					else if (m.hasc2()) // only adjacent to C2, need to calculate from scratch
					{
						q.add(new HeapNodeWT(Math.min(index1, k), Math.max(index1, k),
								delta_sigma(index1, k, nList, cms)));//
					}
				}
			}

			/*
			 * Record the current clustering as the one with the maximum modularity
			 */
			double modularity = cms.modularity();

			if (modularity > maxModularity)
			{
				maxCommunities = cms.copy();
				maxModularity = modularity;
			}

			setProgress(1D * (((double) graph.getNodeCount() - cms.size()) / graph.getNodeCount()));
		}

		communities = buildCommunityGraphs(maxCommunities.getList(), graph, "Walktrap"); // get Graphs from communities
		return cull(communities);
	}

	/**
	 * Remove a HeapNode from the Queue.
	 * 
	 * @param info the node to remove
	 * @param q    the priority queue to remove from
	 */
	private void remove_node(HeapNodeWT info, PriorityQueue<HeapNodeWT> q)
	{
		boolean found = false;
		List<HeapNodeWT> auxiliary = new ArrayList<>();

		while (!q.isEmpty() && !found)
		{
			HeapNodeWT n = q.poll();

			if (n.getIndex1() == info.getIndex1() && n.getIndex2() == info.getIndex2())
			{
				found = true;
			}
			else
			{
				auxiliary.add(n);
			}
		}

		for (HeapNodeWT n : auxiliary)
		{
			q.add(n);
		}
	}

	/**
	 * Calculate delta sigma from communities C1 and C2 according to Section 4.2
	 * Theorem 5 in the paper.
	 * 
	 * @param C1index the index of community C1 in the list
	 * @param C2index the index of community C2 in the list
	 * @param nList   the sorted Node list
	 * @param cms     the Communities
	 * @return the delta sigma between C1 and C2
	 */
	private double delta_sigma(int C1index, int C2index, List<Node> nList, Communities cms)
	{
		return distance(C1index, C2index, nList) * ((double) cms.get(C1index).size() * cms.get(C2index).size())
				/ (cms.get(C1index).size() + cms.get(C2index).size());
	}

	/**
	 * Get the Node degree. The algorithm counts the node itself as a degree.
	 * 
	 * @param n the Node to get the degree of
	 * @return the degree of the Node counting itself
	 */
	private int getDegree(Node n)
	{
		return n.getDegree() + 1;
	}

	/**
	 * Get the Node weight. The algorithm counts the node itself as having a weight
	 * of weight/degree.
	 * 
	 * @param n the Node to get the weight of
	 * @return the weight of the Node counting its own
	 */
	private double getWeight(Node n)
	{
		return n.getWeight() + n.getWeight() / n.getDegree();
	}

	/**
	 * Get a probability vector for a community. The probabilities are of going from
	 * the community to all Nodes in a random walk of length steps instance
	 * variable. This is an adaptation of the int constructor of the Probabilities
	 * class in igraph master's walktrap_communities.cpp
	 * 
	 * @param C     the community
	 * @param graph the original graph
	 * @param nList the sorted list of Nodes
	 * @return the probability vector
	 */
	private HashMap<Node, Double> cprobs(List<Node> C, Graph graph, List<Node> nList)
	{
		HashMap<Node, Double> P = new HashMap<>();

		double[] tmp_vector1 = new double[nList.size()];
		double[] tmp_vector2 = new double[nList.size()];

		/*
		 * initialize vector with probabilities for in community Nodes. 1 if 1-Node
		 * community.
		 */
		for (Node n : C)
		{
			tmp_vector1[nList.indexOf(n)] = 1.0 / C.size();
		}

		// "perform" the random walk of length this.steps
		for (int step = 0; step < this.steps; step++)
		{
			// reset vector2
			for (int i = 0; i < nList.size(); i++)
			{
				tmp_vector2[i] = 0.0;
			}

			/*
			 * use degrees for unweighted, weights for weighted
			 */

			for (Node i : nList)
			{
				double prob = tmp_vector1[nList.indexOf(i)] / (isWeighted() ? this.getWeight(i) : this.getDegree(i));

				/*
				 * update the probability from Node i to itself. if weighted multiply by self
				 * edge weight defined as weight (not counting self edge) / degree (not counting
				 * self)
				 */
				tmp_vector2[nList.indexOf(i)] += prob * (isWeighted() ? i.getWeight() / i.getDegree() : 1);

				for (Node j : i.getNeighbors())
				{
					Edge e = i.getEdges().stream().filter(edge -> edge.includes(j)).collect(Collectors.toList()).get(0);

					/*
					 * update the probability from Node i to j. if weighted multiply by edge weight.
					 */
					tmp_vector2[nList.indexOf(j)] += prob * (isWeighted() ? e.getData() : 1.0);
				}

			}

			// swap vectors so vector1 has the results of this iteration for the next one
			double[] swap = tmp_vector1;
			tmp_vector1 = tmp_vector2;
			tmp_vector2 = swap;
		}

		// store the probability vector
		// use weight if weighted, degrees otherwise
		for (Node n : nList)
		{
			P.put(n,
					tmp_vector1[nList.indexOf(n)] / Math.sqrt(isWeighted() ? this.getWeight(n) : this.getDegree(n)));
		}

		return P;
	}

	/**
	 * Calculate the probability vectors of joining 2 communities. Equation in
	 * section 4.4 of the paper (first bullet point). This is an adaptation of the 2
	 * ints constructor of the Probabilities class in igraph master's
	 * walktrap_communities.cpp
	 * 
	 * @param i   index of community 1
	 * @param j   index of community 2
	 * @param cms the Communities
	 * @return the new probability vector
	 */
	private HashMap<Node, Double> merge_probs(int i, int j, Communities cms)
	{
		List<Node> C1 = cms.get(i);
		List<Node> C2 = cms.get(j);
		HashMap<Node, Double> P1 = Pv.get(i);
		HashMap<Node, Double> P2 = Pv.get(j);

		double w1 = (double) C1.size() / ((double) (C1.size() + C2.size()));
		double w2 = (double) C2.size() / ((double) (C1.size() + C2.size()));

		HashMap<Node, Double> P = new HashMap<>();

		for (Node n : P1.keySet())
		{
			P.put(n, P1.get(n) * w1 + P2.get(n) * w2);
		}

		return P;
	}

	/**
	 * Compute the distance between community i and community j. This is an
	 * adaptation of the 2 ints constructor of the compute_distance of the
	 * Probabilities class in igraph master's walktrap_communities.cpp
	 * 
	 * @param i     index of community 1
	 * @param j     index of community 2
	 * @param nList the sorted list of nodes
	 * @return the distance between the communities
	 */
	private double distance(int i, int j, List<Node> nList)
	{
		double sum = 0.0;
		for (Node n : nList)
		{
			sum += Math.pow(Pv.get(i).get(n) - Pv.get(j).get(n), 2);
		}
		return sum;
	}

	/**
	 * Stores the delta sigmas between a Community C connected to community 1 or
	 * community 2. "or" because C may only be connected to 1 or both. C c1sigma / \
	 * c2sigma C1 C2
	 * 
	 * @author Adolfo Perera
	 */
	private class MutualConnectionsRecord
	{
		private int mutual_index; // index of C

		private double c1sigma;
		private double c2sigma;

		public MutualConnectionsRecord(int m)
		{
			mutual_index = m;

			c1sigma = -1;
			c2sigma = -1;
		}

		public int get_mutual_index()
		{
			return mutual_index;
		}

		public double get_c1sigma()
		{
			return c1sigma;
		}

		public double get_c2sigma()
		{
			return c2sigma;
		}

		public void setc1(double sigma)
		{
			c1sigma = sigma;
		}

		public void setc2(double sigma)
		{
			c2sigma = sigma;
		}

		public boolean hasc1()
		{
			return c1sigma != -1;
		}

		public boolean hasc2()
		{
			return c2sigma != -1;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof MutualConnectionsRecord)
			{
				return this.get_mutual_index() == ((MutualConnectionsRecord) o).get_mutual_index();
			}
			else
			{
				return false;
			}
		}

		@Override
		public int hashCode()
		{
			return mutual_index;
		}
	}
}
