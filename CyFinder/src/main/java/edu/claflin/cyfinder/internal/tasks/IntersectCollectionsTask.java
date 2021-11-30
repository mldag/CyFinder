package edu.claflin.cyfinder.internal.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import edu.claflin.cyfinder.internal.Global;
import edu.claflin.cyfinder.internal.logic.ConfigurationBundle;
import edu.claflin.cyfinder.internal.tasks.utils.GraphTaskUtils;
import edu.claflin.cyfinder.internal.ui.ErrorPanel;

public class IntersectCollectionsTask extends AbstractTask
{
	private final ConfigurationBundle config;
	private final CyRootNetwork root1;
	private final CyRootNetwork root2;
	private final int minNodeCount;

	public IntersectCollectionsTask(ConfigurationBundle config, CyRootNetwork root1, CyRootNetwork root2,
			int minNodeCount)
	{
		super();
		this.config = config;
		this.root1 = root1;
		this.root2 = root2;
		this.minNodeCount = minNodeCount;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception
	{
		try
		{
			if (root1 != root2)
			{
				List<CyNetwork> subNetworks1 = new ArrayList<>();
				List<CyNetwork> subNetworks2 = new ArrayList<>();

				subNetworks1.addAll(root1.getSubNetworkList());
				subNetworks2.addAll(root2.getSubNetworkList());

				subNetworks1 = subNetworks1.stream().filter(net -> net.toString() != null).collect(Collectors.toList());
				subNetworks2 = subNetworks2.stream().filter(net -> net.toString() != null).collect(Collectors.toList());

				// sort and reverse because task iterator behaves like a LinkedList
				// this wasy, the crossing will happen in the correct order
				Collections.sort(subNetworks1, new NetworkComparator());
				Collections.reverse(subNetworks1);
				Collections.sort(subNetworks2, new NetworkComparator());
				Collections.reverse(subNetworks2);

				// remove empty networks
				subNetworks1 = subNetworks1.stream()
						.filter(subnetwork -> GraphTaskUtils.getNetworkName(subnetwork).length() >= 1)
						.collect(Collectors.toList());
				subNetworks2 = subNetworks2.stream()
						.filter(subnetwork -> GraphTaskUtils.getNetworkName(subnetwork).length() >= 1)
						.collect(Collectors.toList());

				for (CyNetwork cn1 : subNetworks1)
				{
					if (cancelled)
						break;

					for (CyNetwork cn2 : subNetworks2)
					{
						if (cancelled)
							break;
						this.insertTasksAfterCurrentTask(new IntersectNetworksTask(config, cn1, cn2, minNodeCount));
					}
				}

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

class NetworkComparator implements Comparator<CyNetwork>
{

	@Override
	public int compare(CyNetwork n1, CyNetwork n2)
	{
		String name1 = GraphTaskUtils.getNetworkName(n1);
		String name2 = GraphTaskUtils.getNetworkName(n2);
		return name1.compareTo(name2);
	}

}
