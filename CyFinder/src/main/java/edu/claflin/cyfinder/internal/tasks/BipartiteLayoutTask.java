package edu.claflin.cyfinder.internal.tasks;

import static edu.claflin.cyfinder.internal.Global.getApplicationManagerService;
import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NODE_HEIGHT;
import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NODE_WIDTH;
import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NODE_Y_LOCATION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import edu.claflin.cyfinder.internal.Global;
import edu.claflin.cyfinder.internal.logic.CyNodeViewAdapter;
import edu.claflin.cyfinder.internal.tasks.utils.GraphTaskUtils;
import edu.claflin.cyfinder.internal.ui.ErrorPanel;
import edu.claflin.finder.algo.Algorithm.GraphSortOrder;
import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.algo.ConnectedComponentsDFS;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;

/**
 * Arranges Bipartite Networks in 2 aligned columns that are the partite sets.
 */
public class BipartiteLayoutTask extends AbstractTask
{
	private static final String displayName = "Bipartite Layout";

	/**
	 * Start the task.
	 */
	@Override
	public void run(TaskMonitor taskMonitor)
	{
		try
		{
			taskMonitor.setTitle(displayName);

			List<CyNetworkView> views = getApplicationManagerService().getSelectedNetworkViews();

			taskMonitor.setProgress(0D);

			int count = 1;
			for (CyNetworkView view : views) // apply bipartite layout to all selected networks
			{
				if (cancelled)
					return;

				if (view == null)
					continue;

				CyNetwork cn = view.getModel();

				if (cn == null || cn.getNodeCount() <= 0)
					continue;

				if (cancelled)
					return;

				//read network view into graph
				Graph g = GraphTaskUtils.convertCyNetworkView(view);

				// we work with undirected graphs
				for (Edge e : g.getEdgeList())
				{
					if (cancelled)
						return;
					
					e.setUndirected(true);
				}

				if (cancelled)
					return;

				// apply bipartite layout on each connected component because some may be bipartite and we don't want to miss them because the graph could be disconnected
				
				ArgumentsBundle ab = new ArgumentsBundle();
				ConnectedComponentsDFS dfs = new ConnectedComponentsDFS(ab);
				dfs.setGraphSortOrder(GraphSortOrder.DESCENDING);
				dfs.setMinNodeCount(1);
				
				for (Graph component : dfs.process(g)) 
				{
					if (!component.isBipartite())
					{
						continue; // not biparite, skip.
					}
					
					// get partite sets.
					ArrayList<ArrayList<Node>> bSets = component.getPartiteSets();

					Collections.sort(bSets, (a, b) -> b.size() - a.size());
					Collections.sort(bSets.get(0));
					Collections.sort(bSets.get(1));

					if (cancelled)
						return;

					List<View<CyNode>> s1 = bSets.get(0).stream().map(node -> ((CyNodeViewAdapter) node).getCyNodeView())
							.collect(Collectors.toList());
					List<View<CyNode>> s2 = bSets.get(1).stream().map(node -> ((CyNodeViewAdapter) node).getCyNodeView())
							.collect(Collectors.toList());

					if (cancelled)
						return;

					View<CyNode> aview = s1.get(0);
					double xspacing = GraphTaskUtils.getNodeDoubleVisualProperty(aview, NODE_WIDTH) + 110;
					double yspacing = GraphTaskUtils.getNodeDoubleVisualProperty(aview, NODE_HEIGHT) + 30;

					position(s1, s2, xspacing, yspacing); // poistion the nodes
					
					if (cancelled)
						return;
					
					view.fitContent(); // fit result to screen
				}
				

				taskMonitor.setProgress(1D * (count++ / views.size()));
			}
		}
		catch (Throwable error)
		{
			SwingUtilities.invokeLater(() ->
			{
				String message = "An error occurred during execution!";
				ErrorPanel errorPanel = new ErrorPanel(message, error);
				errorPanel.display(Global.getDesktopService().getJFrame(), message);
			});
			cancel();
		}
	}

	/**
	 * Positions the Nodes in the partite sets
	 * 
	 * @param set1
	 * @param set2
	 * @param nodeHorizontalSpacing
	 * @param nodeVerticalSpacing
	 */
	private void position(List<View<CyNode>> set1, List<View<CyNode>> set2, double nodeHorizontalSpacing,
			double nodeVerticalSpacing)
	{
		double currX = 0.0d;
		double currY = 0.0d;
		double initialX = 0.0d;
		double initialY = 0.0d;

		double nodeCount = set1.size() + set2.size();

		HashSet<View<CyNode>> all = new HashSet<>(); // used to determine geo center
		all.addAll(set1);
		all.addAll(set2);

		// Calculate our starting point as the geographical center of the nodes.
		for (final View<CyNode> nView : all)
		{
			if (cancelled)
				return;			
			
			initialX += GraphTaskUtils.getNodeDoubleVisualProperty(nView, NODE_X_LOCATION) / nodeCount;
			initialY += GraphTaskUtils.getNodeDoubleVisualProperty(nView, NODE_Y_LOCATION) / nodeCount;
		}

		// initialX and initialY reflect the center of our grid, so we
		// need to offset by distance/2 in each direction
		initialX = initialX - (nodeHorizontalSpacing / 2);
		initialY = initialY - (nodeVerticalSpacing / 2);
		currX = initialX;
		currY = initialY;

		for (final View<CyNode> nView : set1) // poistion nodes in set 1
		{
			if (cancelled)
				return;
			nView.setVisualProperty(NODE_X_LOCATION, currX);
			nView.setVisualProperty(NODE_Y_LOCATION, currY);

			currY += nodeVerticalSpacing;
		}

		currX += nodeHorizontalSpacing; // new column to the right
		currY = initialY; // start from the top

		for (final View<CyNode> nView : set2) // poistion nodes in set 1
		{
			if (cancelled)
				return;
			nView.setVisualProperty(NODE_X_LOCATION, currX);
			nView.setVisualProperty(NODE_Y_LOCATION, currY);

			currY += nodeVerticalSpacing;
		}
	}
}
