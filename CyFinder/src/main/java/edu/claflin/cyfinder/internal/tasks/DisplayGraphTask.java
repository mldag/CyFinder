package edu.claflin.cyfinder.internal.tasks;

import static edu.claflin.cyfinder.internal.Global.getDesktopService;

import javax.swing.SwingUtilities;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

import edu.claflin.cyfinder.internal.tasks.utils.GraphTaskUtils;
import edu.claflin.cyfinder.internal.ui.ErrorPanel;
import edu.claflin.finder.logic.Graph;

public class DisplayGraphTask extends AbstractNetworkTask
{

	public DisplayGraphTask(CyNetwork network)
	{
		super(network);
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception
	{
		try
		{
			Graph graph = null;
			if (!cancelled)
				graph = GraphTaskUtils.convertCyNetwork(network); // cynetwork to Graph
			
			if (!cancelled) 
				GraphTaskUtils.showInfo("Graph", graph.toString());
		}
		catch (Throwable error)
		{
			SwingUtilities.invokeLater(() -> {
				String message = "An error occurred during execution!";
				ErrorPanel errorPanel = new ErrorPanel(message, error);
				errorPanel.display(getDesktopService().getJFrame(), message);
			});
			cancel();
		}
	}

}
