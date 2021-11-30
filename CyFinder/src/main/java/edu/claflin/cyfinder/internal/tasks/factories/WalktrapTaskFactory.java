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
import edu.claflin.cyfinder.internal.ui.configdialog.WalktrapConfigDialog;
import edu.claflin.cyfinder.internal.ui.utils.FeatureConfig;
import edu.claflin.finder.algo.clustering.Walktrap;

public class WalktrapTaskFactory extends AbstractNetworkTaskFactory
{
	/**
	 * Represents the configuration to apply to executions of this factory.
	 */
	private ConfigurationBundle config;

	public WalktrapTaskFactory()
	{
		this(null);
	}

	public WalktrapTaskFactory(ConfigurationBundle config)
	{
		this.config = config;
	}
	
	@Override
	public boolean isReady(CyNetwork cn) 
	{
		return cn.getNodeCount() >= 1;
	}

	/**
	 * {@inheritDoc }
	 * 
	 * @param cn the CyNetwork object to analyze.
	 * @return the TaskIterator containing the task to execute.
	 */
	@Override
	public TaskIterator createTaskIterator(CyNetwork cn)
	{
		if (config == null)
		{
			Frame parent = getDesktopService().getJFrame();
			ExecuteWalkT eAction = new ExecuteWalkT(cn);

			// configure features in window
			FeatureConfig fc = new FeatureConfig();
			fc.setMinNodeCount(true);
			fc.setWeightSelect(true);

			// start configuration window
			return new TaskIterator(new ConfigurationTask(parent, new WalktrapConfigDialog<Walktrap>(parent, eAction,
					Walktrap.class, fc, GraphTaskUtils.getNumericColumnsArray(cn)), "Walktrap"));
		}
		else
		{
			HashMap<String, String> messages = new HashMap<String, String>();
			messages.put("Title", "Community Detection with Walktrap Algorithm.");
			messages.put("Execute", "Executing Walktrap Search...");
			messages.put("Search", "Searching for Clustering with maximum modularity");
			messages.put("Error", "Error during Community Detection");

			TaskIterator t = new TaskIterator();
			t.append(new MakeUndirectedTask(cn, false, messages.get("Title")));
			t.append(new GeneralAlgorithmTask(cn, config, messages));
			return t;
		}

	}

	private final class ExecuteWalkT extends AbstractAction
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
		public ExecuteWalkT(CyNetwork cn)
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
			NetworkTaskFactory factory = new WalktrapTaskFactory(config);
			getTaskManagerService().execute(factory.createTaskIterator(cn));
		}
	}
}
