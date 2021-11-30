package edu.claflin.cyfinder.internal.tasks.factories;

import static edu.claflin.cyfinder.internal.Global.getDesktopService;
import static edu.claflin.cyfinder.internal.Global.getNetworkManagerService;
import static edu.claflin.cyfinder.internal.Global.getTaskManagerService;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.claflin.cyfinder.internal.logic.ConfigurationBundle;
import edu.claflin.cyfinder.internal.tasks.IntersectNetworksTask;
import edu.claflin.cyfinder.internal.tasks.config.ConfigurationTask;
import edu.claflin.cyfinder.internal.tasks.utils.GraphTaskUtils;
import edu.claflin.cyfinder.internal.ui.configdialog.IntersectConfigDialog;

public class IntersectNetworksTaskFactory extends AbstractTaskFactory
{
	private Object[] config; // {ConfigBundle, {network1, network2, minNodeCount}}

	public IntersectNetworksTaskFactory()
	{
		super();
		config = null;
	}

	public IntersectNetworksTaskFactory(Object[] config)
	{
		super();
		this.config = config;
	}

	@Override
	public boolean isReady()
	{
		return getNetworkManagerService().getNetworkSet().size() >= 2;
	}

	@Override
	public TaskIterator createTaskIterator()
	{
		Set<CyNetwork> networkSet = getNetworkManagerService().getNetworkSet();
	      
		Map<String, CyNetwork> networkMap = new HashMap<String, CyNetwork>();

		String[] columnNames = new String[networkSet.size()];

		int index = 0;
		for (CyNetwork v : networkSet)
		{
			String name = GraphTaskUtils.getNetworkName(v);
			networkMap.put(name, v);
			columnNames[index++] = name;
		}

		if (config == null)
		{
			Frame parent = getDesktopService().getJFrame();
			ExecuteIntersect eAction = new ExecuteIntersect();

			return new TaskIterator(new ConfigurationTask(parent,
					new IntersectConfigDialog(parent, eAction, columnNames, false),
					"Intersect Networks"));
		}
		else
		{
			TaskIterator t = new TaskIterator();
			
			ConfigurationBundle cb = (ConfigurationBundle)config[0];
			CyNetwork n1 = networkMap.get(((String[])config[1] )[0]);
			CyNetwork n2 = networkMap.get(((String[])config[1] )[1]);
			int minNodeCount = Integer.parseInt(((String[])config[1] )[2]);
			
			t.append(new IntersectNetworksTask(cb, n1, n2, minNodeCount));
			return t;
		}
	}

	private final class ExecuteIntersect extends AbstractAction
	{

		/**
		 * {@inheritDoc }
		 * 
		 * @param e the ActionEvent in which the source is the Configuration Bundle.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object[] config = (Object[]) e.getSource();

			AbstractTaskFactory factory = new IntersectNetworksTaskFactory(config);
			getTaskManagerService().execute(factory.createTaskIterator());
		}
	}
}
