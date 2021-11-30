/* 
 * Copyright 2015 Charles Allen Schultz II.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package edu.claflin.cyfinder.internal.tasks.factories;

import static edu.claflin.cyfinder.internal.Global.getDesktopService;
import static edu.claflin.cyfinder.internal.Global.getTaskManagerService;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.claflin.cyfinder.internal.logic.ConfigurationBundle;
import edu.claflin.cyfinder.internal.tasks.SubgraphFinderTask;
import edu.claflin.cyfinder.internal.tasks.config.ConfigurationTask;
import edu.claflin.cyfinder.internal.ui.configdialog.FinderConfigDialog;

/**
 * Produces a configuration task.
 *
 * @author Charles Allen Schultz II
 * @version 1.1.1 June 15, 2015
 */
public class SubgraphFinderTaskFactory extends AbstractNetworkTaskFactory
{

	/**
	 * Represents the configuration to apply to executions of this factory.
	 */
	private ConfigurationBundle config;

	/**
	 * Constructs an un-configured factory. Configuration will be handled when
	 * obtaining the task iterator.
	 */
	public SubgraphFinderTaskFactory()
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
	public SubgraphFinderTaskFactory(ConfigurationBundle config)
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
		if (this.config == null)
		{ // Configure First
			Frame parent = getDesktopService().getJFrame();
			ExecuteAction eAction = new ExecuteAction(cn);

			List<CyColumn> numberColumns = new ArrayList<>();

			cn.getDefaultEdgeTable().getColumns().parallelStream().forEach(col -> {
				if (Number.class.isAssignableFrom(col.getType()))
				{
					numberColumns.add(col);
				}
			});
			return new TaskIterator(new ConfigurationTask(parent,
					new FinderConfigDialog(parent, eAction, numberColumns), "Subgraph Finder"));
		}
		else
		{
			return new TaskIterator(new SubgraphFinderTask(cn, config));
		}
	}

	/**
	 * Private class for executing a SubgraphFinderTask after configuring.
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

			// Create a new factory to allow an unconfigured factory to remain
			// so.
			NetworkTaskFactory factory = new SubgraphFinderTaskFactory(config);
			getTaskManagerService().execute(factory.createTaskIterator(cn));
		}
	}
}
