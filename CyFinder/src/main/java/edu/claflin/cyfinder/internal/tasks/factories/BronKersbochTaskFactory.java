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
import edu.claflin.cyfinder.internal.tasks.BronKersbochTask;
import edu.claflin.cyfinder.internal.tasks.MakeUndirectedTask;
import edu.claflin.cyfinder.internal.tasks.config.ConfigurationTask;
import edu.claflin.cyfinder.internal.tasks.utils.GraphTaskUtils;
import edu.claflin.cyfinder.internal.ui.configdialog.ConfigDialog;
import edu.claflin.cyfinder.internal.ui.utils.FeatureConfig;
import edu.claflin.finder.algo.BronKerbosch;

public class BronKersbochTaskFactory extends AbstractNetworkTaskFactory
{
	private final boolean bipartite;

	/**
	 * Represents the configuration to apply to executions of this factory.
	 */
	private ConfigurationBundle config;

	/**
	 * Constructs a factory with a specified configuration bundle. Useful for
	 * mandating a configuration file if accessing from outside the normal Cytoscape
	 * scope.
	 * 
	 * @param config    the ConfigurationBundle object representing the subgraph
	 *                  finder configuration.
	 * @param bipartite looking for bicliques?
	 */
	public BronKersbochTaskFactory(ConfigurationBundle config, boolean bipartite)
	{
		this.config = config;
		this.bipartite = bipartite;
	}

	/**
	 * Constructs a factory with no configuration bundle.
	 * 
	 * @param bipartite looking for bicliques?
	 */
	public BronKersbochTaskFactory(boolean bipartite)
	{
		this(null, bipartite);
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
		if (this.config == null) // task not configured
		{
			Frame parent = getDesktopService().getJFrame();
			ExecuteBronK eAction = new ExecuteBronK(cn);

			// configure features in window
			FeatureConfig fc = new FeatureConfig();
			fc.setMinNodeCount(true);			
			fc.setTiedNodeCount(true);

			// start configuration window
			return new TaskIterator(new ConfigurationTask(parent,
					new ConfigDialog<BronKerbosch>(parent, "Configure Bron-Kersboch", eAction, BronKerbosch.class, fc, GraphTaskUtils.getNumericColumnsArray(cn)),
					"Bron Kersboch"));
		}
		else // configured, go for Bron Kersboch
		{
			HashMap<String, String> messages = new HashMap<String, String>();
			messages.put("Title", bipartite ? "Maximum Biclique Graph" : "Maximum Clique");
			messages.put("Execute", "Executing Bron-Kersboch Search...");
			messages.put("Search", "Searching for " + (bipartite ? "Maximum Biclique Graph" : "Maximum Clique"));
			messages.put("Error", "Error during Bron Kersboch Search");

			TaskIterator t = new TaskIterator();
			t.append(new MakeUndirectedTask(cn, false, messages.get("Title")));
			t.append(new BronKersbochTask(cn, config, messages, bipartite));
			return t;
		}
	}

	/**
	 * Private class for executing a BronKersboch after configuring.
	 */
	private final class ExecuteBronK extends AbstractAction
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
		public ExecuteBronK(CyNetwork cn)
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

			// Create a new factory to allow an non-configured factory to remain
			NetworkTaskFactory factory = new BronKersbochTaskFactory(config, bipartite);
			getTaskManagerService().execute(factory.createTaskIterator(cn));
		}
	}
}
