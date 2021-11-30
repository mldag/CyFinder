package edu.claflin.cyfinder.internal.tasks.factories;

import static edu.claflin.cyfinder.internal.Global.getDesktopService;
import static edu.claflin.cyfinder.internal.Global.getTaskManagerService;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.claflin.cyfinder.internal.logic.ConfigurationBundle;
import edu.claflin.cyfinder.internal.tasks.GeneralAlgorithmTask;
import edu.claflin.cyfinder.internal.tasks.MakeUndirectedTask;
import edu.claflin.cyfinder.internal.tasks.config.ConfigurationTask;
import edu.claflin.cyfinder.internal.tasks.utils.GraphTaskUtils;
import edu.claflin.cyfinder.internal.ui.configdialog.ConfigDialog;
import edu.claflin.cyfinder.internal.ui.utils.FeatureConfig;
import edu.claflin.finder.algo.ConnectedComponentsDFS;

public class ConnectedComponentsTaskFactory extends AbstractNetworkTaskFactory
{
	private ConfigurationBundle config;

	public ConnectedComponentsTaskFactory()
	{
		this(null);
	}

	public ConnectedComponentsTaskFactory(ConfigurationBundle config)
	{
		this.config = config;
	}
	
	@Override
	public boolean isReady(CyNetwork cn) 
	{
		return cn.getNodeCount() >= 1;
	}

	@Override
	public TaskIterator createTaskIterator(CyNetwork cn)
	{
		if (config == null)
		{
			Frame parent = getDesktopService().getJFrame();
			ExecuteConnectedC eAction = new ExecuteConnectedC(cn);

			// configure features in window
			FeatureConfig fc = new FeatureConfig();			
			fc.setMinNodeCount(true);						

			// start configuration window
			return new TaskIterator(new ConfigurationTask(parent,
					new ConfigDialog<ConnectedComponentsDFS>(parent, "Configure Connected Components", eAction, ConnectedComponentsDFS.class, fc, GraphTaskUtils.getNumericColumnsArray(cn)),
					"Connected Components"));
		}
		else
		{
			HashMap<String, String> messages = new HashMap<String, String>();
			messages.put("Title", "Looking for Connected Components.");
			messages.put("Execute", "Executing Depth First Search...");
			messages.put("Search", "Searching for Connected Components");
			messages.put("Error", "Error during Connected Components Search");

			TaskIterator t = new TaskIterator();
			t.append(new MakeUndirectedTask(cn, false, messages.get("Title")));
			t.append(new GeneralAlgorithmTask(cn, config, messages));
			return t;
		}
	}

	private final class ExecuteConnectedC extends AbstractAction
	{

		/**
		 * The target CyNetwork.
		 */
		private final CyNetwork cn;

		/**
		 * Constructs the ExecuteAction.
		 * 
		 * @param cn the target CyNetwork.
		 */
		public ExecuteConnectedC(CyNetwork cn)
		{
			this.cn = cn;
		}

		/**
		 * {@inheritDoc }
		 * 
		 * @param e the ActionEvent in which the source is the Configuration Bundle.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			ConfigurationBundle config = (ConfigurationBundle) e.getSource();

			// Create a new factory to allow an unconfigured factory to remain
			NetworkTaskFactory factory = new ConnectedComponentsTaskFactory(config);
			getTaskManagerService().execute(factory.createTaskIterator(cn));
		}
	}

}
