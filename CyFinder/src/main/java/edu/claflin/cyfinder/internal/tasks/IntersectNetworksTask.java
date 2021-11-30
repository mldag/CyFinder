package edu.claflin.cyfinder.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import edu.claflin.cyfinder.internal.Global;
import edu.claflin.cyfinder.internal.logic.ConfigurationBundle;
import edu.claflin.cyfinder.internal.tasks.utils.GraphTaskUtils;
import edu.claflin.cyfinder.internal.ui.ErrorPanel;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;

public class IntersectNetworksTask extends AbstractTask
{
	private final ConfigurationBundle config;
	private final CyNetwork network1;
	private final CyNetwork network2;
	private final int minNodeCount;

	public IntersectNetworksTask(ConfigurationBundle config, CyNetwork graph1, CyNetwork graph2)
	{
		super();
		this.config = config;
		this.network1 = graph1;
		this.network2 = graph2;
		this.minNodeCount = 1;
	}

	public IntersectNetworksTask(ConfigurationBundle config, CyNetwork graph1, CyNetwork graph2, int minNodeCount)
	{
		super();
		this.config = config;
		this.network1 = graph1;
		this.network2 = graph2;
		this.minNodeCount = minNodeCount;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception
	{
		try
		{
			if (network1 == null || network2 == null)
			{
				cancel();
			}

			Graph graph1 = null;
			Graph graph2 = null;

			if (!cancelled)
			{
				graph1 = GraphTaskUtils.convertCyNetwork(network1);
				graph2 = GraphTaskUtils.convertCyNetwork(network2);

				if (graph1 == null || graph2 == null)
				{
					cancel();
				}
			}

			List<Node> outputNodes = new ArrayList<>();
			List<Edge> outputEdges = new ArrayList<>();

			if (!cancelled)
			{
				List<Node> sourceNodes = graph1.getNodeList();
				List<Node> targetNodes = graph2.getNodeList();

				sourceNodes.retainAll(targetNodes);

				if (sourceNodes.size() < minNodeCount)
				{
					cancel();
				}
				else
				{
					List<Edge> sourceEdges = graph1.getEdgeList();
					List<Edge> targetEdges = graph2.getEdgeList();

					sourceEdges.retainAll(targetEdges);

					outputNodes.addAll(sourceNodes);
					outputEdges.addAll(sourceEdges);
				}
			}

			if (!cancelled)
			{
				Graph intersection = graph1.intersect(graph2);

				ArrayList<Graph> result = new ArrayList<>();
				result.add(intersection);

				GraphTaskUtils.saveSubGraphs(taskMonitor, result, network1, config);
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
}
