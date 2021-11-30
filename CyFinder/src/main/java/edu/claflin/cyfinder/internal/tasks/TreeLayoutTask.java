package edu.claflin.cyfinder.internal.tasks;

import static edu.claflin.cyfinder.internal.Global.getApplicationManagerService;
import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NODE_HEIGHT;
import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NODE_WIDTH;
import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NODE_Y_LOCATION;

import java.util.Collections;
import java.util.List;

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
 * Arranges a Network like a tree with a random node at the root.
 * Disconnected Networks are arranged into forests.
 */
public class TreeLayoutTask extends AbstractTask
{
	private static final String displayName = "Tree Layout";

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

			double componentX = 0;
			double componentHorizontalSpacing = 50;
			int count = 1;
			for (CyNetworkView view : views) // apply tree layout to all selected networks
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

				// each connected component is a tree

				ArgumentsBundle ab = new ArgumentsBundle();
				ConnectedComponentsDFS dfs = new ConnectedComponentsDFS(ab);
				dfs.setGraphSortOrder(GraphSortOrder.DESCENDING);
				dfs.setMinNodeCount(1);

				for (Graph component : dfs.process(g))
				{
					if (cancelled)
						return;

					View<CyNode> aview = ((CyNodeViewAdapter) component.getNodeList().get(0)).getCyNodeView();
					double xspacing = GraphTaskUtils.getNodeDoubleVisualProperty(aview, NODE_WIDTH) + 30;
					double yspacing = GraphTaskUtils.getNodeDoubleVisualProperty(aview, NODE_HEIGHT) + 30;
					double componentgap = positionComponent(component, componentX, 0, xspacing, yspacing); // position the tree and get the component gap
					componentX += componentHorizontalSpacing + componentgap; // start x of next component

				}

				if (cancelled)
					return;

				view.fitContent(); // fit result to screen

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
	 * Position a connected component and return the width of the widest level of the component.
	 * @param component the connected component to position
	 * @param rootX the x coordinate of the component's root
	 * @param rootY the y coordinate of the component's root
	 * @param nodeHorizontalSpacing horizontal space between nodes
	 * @param nodeVerticalSpacing vertical space between nodes
	 * @return the width of the widest level of the component
	 */
	private double positionComponent(Graph component, double rootX, double rootY, double nodeHorizontalSpacing,
			double nodeVerticalSpacing)
	{
		if (cancelled)
			return 0;

		if (component.getNodeCount() <= 0)
			return 0;

		List<Node> nList = component.getNodeList();
		Collections.sort(nList);

		Node root = nList.remove(0);

		if (cancelled)
			return 0;

		positionNode(root, rootX, rootY);

		return bfs(root, rootX, rootY + nodeVerticalSpacing, nodeHorizontalSpacing, nodeVerticalSpacing, nList,
				component);
	}

	/**
	 * Breadth First Search that positions an already positioned node's descendants and returns the width of the widest level of the descendants.
	 * @param n the node being considered
	 * @param intialX the level's intiail x
	 * @param levelY the level's y
	 * @param nodeHorizontalSpacing horizontal space between nodes
	 * @param nodeVerticalSpacing vertical space between nodes
	 * @param remaining descendants pending positioning
	 * @param component the component being positioned
	 * @return the width of the widest level of the descendants
	 */
	private double bfs(Node n, double intialX, double levelY, double nodeHorizontalSpacing, double nodeVerticalSpacing,
			List<Node> remaining, Graph component)
	{
		if (cancelled)
			return 0;

		List<Node> level = component.getAdjacencyList(n); // children
		level.retainAll(remaining); // remove already positioned
		remaining.removeAll(level); // mark current level as positioned

		Collections.sort(level);

		// width of this level
		double width = level.size() * nodeHorizontalSpacing + level.size()
				* GraphTaskUtils.getNodeDoubleVisualProperty(((CyNodeViewAdapter) n).getCyNodeView(), NODE_WIDTH);

		if (cancelled)
			return 0;

		int index = 0;
		for (Node v : level) // position level
		{
			if (cancelled)
				return 0;
			positionNode(v, intialX + index * nodeHorizontalSpacing, levelY);
			index++;
		}

		for (Node v : level) // position the node's descendants
		{
			if (cancelled)
				return 0;
			width = Math.max(width, bfs(v, intialX, levelY + nodeVerticalSpacing, nodeHorizontalSpacing,
					nodeVerticalSpacing, remaining, component));
		}

		if (cancelled)
			return 0;

		return width;
	}

	/**
	 * Positions a Node
	 * @param n the node
	 * @param x coordinate
	 * @param y coordinate
	 */
	private void positionNode(Node n, double x, double y)
	{
		View<CyNode> nview = ((CyNodeViewAdapter) n).getCyNodeView();
		nview.setVisualProperty(NODE_X_LOCATION, x);
		nview.setVisualProperty(NODE_Y_LOCATION, y);
	}
}
