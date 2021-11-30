package edu.claflin.finder.algo;

import static edu.claflin.finder.Global.getLogger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.comp.GraphAverageWeightComparator;
import edu.claflin.finder.logic.comp.GraphSizeComparator;
import edu.claflin.finder.logic.processor.Processable;

/**
 * Abstraction of the algorithm classes used for finding subgraphs.
 * 
 * @author Charles Allen Schultz II
 * @version 3.4 February 2. 2015
 */
public abstract class Algorithm implements Processable<Graph, Graph>
{

	public enum GraphSortOrder
	{
		NONE, ASCENDING, DESCENDING, AVERAGE_WEIGHT;
	}

	// CommunicationListener listener;
	// int counter = 1;

	private int minNodeCount = 1;
	private String weightName = "weight";

	/**
	 * The ArgumentsBundle object holding arguments for the algorithm.
	 */
	public final ArgumentsBundle args;

	/**
	 * Adds Property Change support to track the progress of algorithms.
	 */
	private PropertyChangeSupport mPCS;
	/**
	 * The current progress of the algorithm.
	 */
	private double progress = 0D;

	/**
	 * Number passed in using partite number. Somewhat of a bandaid fix because
	 * haven't figured out the arguments bundle yet - kpuli
	 */
	private int partiteNumber = 0;
	/**
	 * Constant for indicating the progress property.
	 */
	public static final String PROP_PROGRESS = "progress";

	private GraphSortOrder sortOrder = GraphSortOrder.NONE;

	/**
	 * Public Constructor for creating an Algorithm.
	 * 
	 * @param args the ArgumentsBundle containing the arguments for the Algorithm
	 *             object.
	 */
	public Algorithm(ArgumentsBundle args)
	{
//		Communicator gimme = Communicator.getSingleton();
//		listener = gimme;		

		if (args == null)
		{
			if (getLogger() != null)
			{
				getLogger().logError(LogLevel.DEBUG, "The arguments bundle " + "must NOT be null!");
				getLogger().logInfo(LogLevel.DEBUG,
						"Creating empty " + "ArgumentsBundle (Algorithm will not function).");
			}

			// Set arguments bundle to empty bundle.. algorithm will simply
			// do nothing.
			args = new ArgumentsBundle();
		}
		this.args = args;
		mPCS = new PropertyChangeSupport(this);

		minNodeCount = 1;
		weightName = "weight";

		if (getLogger() != null)
		{
			getLogger().logInfo(LogLevel.DEBUG, "Algorithm object instantiated.");
		}
	}

	public GraphSortOrder getGraphSortOrder()
	{
		return this.sortOrder;
	}

	public void setGraphSortOrder(GraphSortOrder gso)
	{
		this.sortOrder = gso;
	}

	public int getMinNodeCount()
	{
		return this.minNodeCount;
	}

	public void setMinNodeCount(int minNodeCount)
	{
		this.minNodeCount = minNodeCount;
	}

	public String getWeightName()
	{
		return weightName;
	}

	public void setWeightName(String weightName)
	{
		this.weightName = weightName;
	}

	public int getPartiteNumber()
	{
		return this.partiteNumber;
	}

	public void setPartiteNumber(int partiteNumber)
	{
		this.partiteNumber = partiteNumber;
	}

	/**
	 * Removes duplicate subGraphs from the provided ArrayList. This ensures that
	 * all found Graphs are unique.
	 * 
	 * @param subGraphs the ArrayList containing the subGraphs to remove duplicates
	 *                  from.
	 * @return the ArrayList of Graph objects containing only unique subgraphs.
	 */
	protected final ArrayList<Graph> cull(ArrayList<Graph> subGraphs)
	{
		// filter out low node count nodes
		subGraphs = (ArrayList<Graph>) subGraphs.stream().filter(g -> g.getNodeCount() >= minNodeCount)
				.collect(Collectors.toList());
		
		boolean[] duplicate = new boolean[subGraphs.size()];
//        if(counter == 1){
//            listener.gimmeUniqueSubGraphs(subGraphs);
//            counter++;
//        }
		if (getLogger() != null)
		{
			getLogger().logAlgo(LogLevel.NORMAL, "CULL: " + subGraphs.size() + " queued for culling.");
		}

		for (int index1 = 0; index1 < subGraphs.size(); index1++)
		{

			if (duplicate[index1])
				continue;

			for (int index2 = index1 + 1; index2 < subGraphs.size(); index2++)
			{
				if (duplicate[index2])
					continue;

				if (getLogger() != null)
				{
					getLogger().logAlgo(LogLevel.VERBOSE,
							"CULL: Comparing " + "graphs " + index1 + " and " + index2 + ".");
				}

				Graph graph1 = subGraphs.get(index1);
				Graph graph2 = subGraphs.get(index2);

				if (graph1.getNodeCount() < graph2.getNodeCount())
				{
					duplicate[index1] = compare(graph1, graph2);

					if (duplicate[index1] && getLogger() != null)
					{
						getLogger().logAlgo(LogLevel.VERBOSE,
								"CULL: Marked " + "duplicate graph at index " + index1 + ".");
					}

					break;
				}
				else
				{
					duplicate[index2] = compare(graph2, graph1);

					if (duplicate[index2] && getLogger() != null)
					{
						getLogger().logAlgo(LogLevel.VERBOSE,
								"CULL: Marked " + "duplicate graph at index " + index2 + ".");
					}
				}
			}
		}

		for (int index = duplicate.length - 1; index >= 0; index--)
			if (duplicate[index])
				subGraphs.remove(index);

		if (getLogger() != null)
		{
			getLogger().logInfo(LogLevel.NORMAL, "CULL: Culled " + (duplicate.length - subGraphs.size()) + " graphs.");
		}

		// sorting
		GraphSortOrder gso = this.getGraphSortOrder();

		if (gso == GraphSortOrder.NONE)
		{
			// do nothing
		}
		else if (gso == GraphSortOrder.AVERAGE_WEIGHT)
		{
			Collections.sort(subGraphs, new GraphAverageWeightComparator());
		}
		else
		{
			if (gso == GraphSortOrder.ASCENDING)
			{
				Collections.sort(subGraphs, new GraphSizeComparator());
			}
			else if (gso == GraphSortOrder.DESCENDING)
			{
				Collections.sort(subGraphs, new GraphSizeComparator());
				Collections.reverse(subGraphs);
			}
		}

		

		// Evyatar & Ariel. Takes the completed subgraphs and starts the logic of
		// assigning a group to evey node in the subgraph (A or B) for each subgraph.
//        CytogrouperMain cytoGrouper = new CytogrouperMain(subGraphs,Communicator.getSingleton(), this.partiteNumber);
		/*
		 * Evyatar & Ariel. This listener (listener.gimmeUniqueSubGraphs(subGraphs)) is
		 * not used but may be useful to keep for the future, since it allows access to
		 * the the final subgraphs found in the Cyfinder module of the app.
		 */
//        listener.setUniqueSubGraphs(subGraphs);
		return subGraphs;
	}

	/**
	 * Reorders the supplied graphs so that the matrices match. The larger graph (in
	 * terms of node count) is reordered so that the order of its nodes mirrors that
	 * of the smaller. I.e. if the larger graph shares in common three nodes with
	 * the smaller, those three nodes are put into the same positions in the larger
	 * graph as they are in the smaller graph.
	 * 
	 * @param base  the Graph object (smaller) to base ordering on.
	 * @param match the Graph object (larger) to reorder.
	 * @deprecated No replacement. There is no longer a need to reorder graphs to
	 *             test equality.
	 * @return a boolean primitive indicating if the graphs are comparable.
	 */
	@Deprecated
	private boolean reorder(Graph base, Graph match)
	{
		for (int index = 0; index < base.getNodeCount(); index++)
		{

			int matchIndex = match.getNodeIndex(base.getNodeName(index));
			if (matchIndex == -1)
				return false;

			match.transpose(index, matchIndex);
		}

		return true;
	}

	/**
	 * Compares the two graphs based on edges. Tests to see that the smaller graph
	 * is essentially a subGraph of the larger.
	 * 
	 * @param base  the Graph object (smaller) to match.
	 * @param match the Graph object (larger) to test against.
	 * @return a boolean primitive indicating if the smaller Graph is a subgraph of
	 *         the larger.
	 */
	private boolean compare(Graph base, Graph match)
	{
		return match.getNodeList().containsAll(base.getNodeList())
				&& match.getEdgeList().containsAll(base.getEdgeList());
	}

	protected void setProgress(double progress)
	{
		if (progress < 0D || progress > 1D)
			throw new IllegalArgumentException("Progress must be between 0 and 1!");

		double oldProgress = this.progress;
		this.progress = progress;
		mPCS.firePropertyChange(PROP_PROGRESS, oldProgress, progress);
	}

	/**
	 * Adds a PropertyChangeListener.
	 * 
	 * @param pcl the PropertyChangeListener.
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl)
	{
		mPCS.addPropertyChangeListener(pcl);
	}

	/**
	 * Removes a PropertyChangeListener.
	 * 
	 * @param pcl the PropertyChangeListener.
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl)
	{
		mPCS.removePropertyChangeListener(pcl);
	}
}
