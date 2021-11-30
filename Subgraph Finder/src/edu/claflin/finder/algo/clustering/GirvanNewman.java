package edu.claflin.finder.algo.clustering;

import static edu.claflin.finder.Global.getLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.algo.clustering.struct.girvan_newman_struct.BetweennessCentrality;
import edu.claflin.finder.algo.clustering.struct.girvan_newman_struct.WeakComponentClusterer;
import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;

/**
 * Girvan-Newman Algorithm for Community Detection. Based on python's igraph
 * library and JUNG's graph library.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Girvan%E2%80%93Newman_algorithm">Wikipedia Summary</a>
 * @see <a href="https://www.pnas.org/content/99/12/7821">Original Paper</a>
 * @see <a href="https://github.com/igraph/igraph/blob/master/src/community/edge_betweenness.c">Original igraph implementation</a>
 * @see <a href="https://github.com/jrtom/jung/blob/master/jung-algorithms/src/main/java/edu/uci/ics/jung/algorithms/cluster/EdgeBetweennessClusterer.java">Original JUNG implementation</a>
 * @author Cesar Martin
 *
 */
public class GirvanNewman extends ClusteringAlgorithm
{

	public GirvanNewman(ArgumentsBundle args)
	{
		super(args);

		if (getLogger() != null)
		{
			getLogger().logInfo(LogLevel.DEBUG, "Edge Betweenness with Girvan Newman algorithm initialized.");
		}

	}

	@Override
	public String toString()
	{
		return "Girvan Newman Algorithm";
	}

	/**
	 * Performs Edge Betweenness community detection with Girvan-Newman algorithm a
	 * given Graph, returning the clustering with the maximum modularity.
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
					"EdgeBetweenness: Searching Graph: " + graph.getName() + "\nRunning Algorithm");
		}

		if (graph.getNodeCount() < 1)
		{
			return new ArrayList<Graph>();
		}

		Graph communityGraph = graph.uniqueCopy();
		Graph algoGraph = graph.uniqueCopy();
		
		List<Node> nList = algoGraph.getNodeList();
//		List<NodeStats> communitiesStats = graph.getNodeStats(); // communities stats (unchanged)
//		// graph stats (changes)
//		List<NodeStats> graphStats = communitiesStats.stream().map(ns -> ns.copy()).collect(Collectors.toList());
//		HashMap<Node, NodeStats> nodeToGraphStats = new HashMap<>(); // get graph node stats from nodes
//		HashMap<NodeStats, NodeStats> graphToCommunitiesStats = new HashMap<>(); // get communities stats from graph
//																					// node stats

		Collections.sort(nList);
//		Collections.sort(communitiesStats);
//		Collections.sort(graphStats);

//		for (NodeStats ns : graphStats)
//		{
//			nodeToGraphStats.put(ns.getNode(), ns);
//		}

//		for (int index = 0; index < graphStats.size(); index++)
//		{
//			NodeStats gst = graphStats.get(index);
//			NodeStats cst = communitiesStats.get(index);
//			graphToCommunitiesStats.put(gst, cst);

		Communities maxCommunities = null;
		double maxModularity = Double.NEGATIVE_INFINITY;
		int clusterCount = -1;

		setProgress(0D);
		while (algoGraph.getEdgeCount() > 0)
		{
			BetweennessCentrality bc = new BetweennessCentrality(algoGraph, isWeighted());

			Edge to_remove = null;
			double score = 0;
			for (Edge e : algoGraph.getEdgeList())
			{
				if (bc.getEdgeScore(e) > score)
				{
					to_remove = e;
					score = bc.getEdgeScore(e);
				}
			}

			algoGraph.removeEdge(to_remove);

			WeakComponentClusterer wcSearch = new WeakComponentClusterer();
			Set<Set<Node>> clusterSet = wcSearch.apply(algoGraph.getNodeList());

			if (clusterSet.size() != clusterCount)
			{
				HashMap<Integer, List<Node>> map = new HashMap<>();

				int index = 0;
				for (Set<Node> cNodesAlgo : clusterSet)
				{
					Set<Node> cNodesCom = cNodesAlgo.stream().map(nAlgo -> communityGraph.getNode(nAlgo.getIdentifier()))
							.collect(Collectors.toSet());
					map.put(index, new ArrayList<Node>(cNodesCom));
					index++;
				}

				Communities cms = new Communities(map, communityGraph, isWeighted());

				double modularity = cms.modularity();
				if (modularity > maxModularity)
				{
					maxModularity = modularity;
					maxCommunities = cms;
				}

				clusterCount = clusterSet.size();
			}

			setProgress(1D * (((double) graph.getEdgeCount() - algoGraph.getEdgeCount()) / graph.getEdgeCount()));
		}

		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL,
					"EdgeBetweenness: Algorithm done. Getting Clustering with Maximum Modularity");
		}

		ArrayList<Graph> communities = new ArrayList<>();

		if (maxCommunities != null)
		{
			communities = this.buildCommunityGraphs(maxCommunities.getList(), graph, "Edge Betweenness");
		}

		return cull(communities);
	}

}
