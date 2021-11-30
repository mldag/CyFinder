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
import edu.claflin.cyfinder.internal.ui.configdialog.SpanningTreeConfigDialog;
import edu.claflin.finder.algo.spanningtree.Prim;

public class PrimTaskFactory extends AbstractNetworkTaskFactory
{
	/**
	 * Represents the configuration to apply to executions of this factory.
	 */
	private ConfigurationBundle config;

	public PrimTaskFactory()
	{
		this(null);
	}

	public PrimTaskFactory(ConfigurationBundle config)
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
			ExecutePrim eAction = new ExecutePrim(cn);

			// start configuration window
			return new TaskIterator(
					new ConfigurationTask(parent,
							new SpanningTreeConfigDialog<Prim>(parent, "Configure Prim", eAction, Prim.class,
									GraphTaskUtils.getNodesArray(cn), GraphTaskUtils.getNumericColumnsArray(cn)),
							"Prim"));
		}
		else
		{
			HashMap<String, String> messages = new HashMap<String, String>();
			messages.put("Title", "Minimum Spanning Tree with Prim Algorithm.");
			messages.put("Execute", "Executing Prim Algorithm...");
			messages.put("Search", "Searching for MST with Prim Algorithm");
			messages.put("Error", "Erro during Prim Algorithm");

			TaskIterator t = new TaskIterator();
			t.append(new MakeUndirectedTask(cn, false, messages.get("Title")));
			t.append(new GeneralAlgorithmTask(cn, config, messages));
			return t;
		}

	}

	private final class ExecutePrim extends AbstractAction
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
		public ExecutePrim(CyNetwork cn)
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
			NetworkTaskFactory factory = new PrimTaskFactory(config);
			getTaskManagerService().execute(factory.createTaskIterator(cn));
		}
	}
}
