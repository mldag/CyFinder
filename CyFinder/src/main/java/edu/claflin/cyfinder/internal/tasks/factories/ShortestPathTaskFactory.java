package edu.claflin.cyfinder.internal.tasks.factories;

import static edu.claflin.cyfinder.internal.Global.getDesktopService;
import static edu.claflin.cyfinder.internal.Global.getTaskManagerService;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.HashSet;

import javax.swing.AbstractAction;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.claflin.cyfinder.internal.logic.ConfigurationBundle;
import edu.claflin.cyfinder.internal.tasks.ShortestPathTask;
import edu.claflin.cyfinder.internal.tasks.config.ConfigurationTask;
import edu.claflin.cyfinder.internal.tasks.utils.GraphTaskUtils;
import edu.claflin.cyfinder.internal.ui.configdialog.ShortestPathConfigDialog;

/**
 * Produces a configuration task.
 *
 * @author Juan C Ibarra
 * @version 1.0.0 February 20, 2021
 */
public class ShortestPathTaskFactory extends AbstractNetworkTaskFactory
{

	/**
	 * Represents the configuration to apply to executions of this factory.
	 */
	private ConfigurationBundle config;

	/**
	 * Constructs an un-configured factory. Configuration will be handled when
	 * obtaining the task iterator.
	 */
	public ShortestPathTaskFactory()
	{
		this(null);
	}

	/**
	 * Constructs a factory with a specified configuration bundle. Useful for
	 * mandating a configuration file if accessing from outside the normal Cytoscape
	 * scope.
	 *
	 * @param config the ConfigurationBundle object representing the subgraph finder
	 *               configuration.
	 */
	public ShortestPathTaskFactory(ConfigurationBundle config)
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
//		if (!ConditionChecker.checkConnected(cn) )
//		{
//			return new TaskIterator(new DummyTask(cn));
//		}
		if (this.config == null)
		{ // Configure First
			HashSet<String> nodeNames = GraphTaskUtils.getNodeNames(cn);
			Frame parent = getDesktopService().getJFrame();
			ExecuteAction eAction = new ExecuteAction(cn);
			ShortestPathConfigDialog cDialog = new ShortestPathConfigDialog(parent, eAction,nodeNames);
			return new TaskIterator(new ConfigurationTask(parent, cDialog, "Shortest Path"));
		}
		else
		{
			return new TaskIterator(new ShortestPathTask(cn, config));
		}
	}

	

	/**
	 * Private class for executing a ShortestPathTask after configuring.
	 */
	private final class ExecuteAction extends AbstractAction
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
		public ExecuteAction(CyNetwork cn)
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

			// Create a new factory to allow an un-configured factory to remain
			// so.
			NetworkTaskFactory factory = new ShortestPathTaskFactory(config);
			getTaskManagerService().execute(factory.createTaskIterator(cn));
		}
	}
}