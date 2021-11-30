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
package edu.claflin.cyfinder.internal.tasks;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

import edu.claflin.cyfinder.internal.Global;
import edu.claflin.cyfinder.internal.logic.ConfigurationBundle;
import edu.claflin.cyfinder.internal.tasks.utils.GraphTaskUtils;
import edu.claflin.cyfinder.internal.ui.ErrorPanel;
import edu.claflin.finder.algo.Algorithm;
import edu.claflin.finder.logic.Graph;

/**
 * Represents a Subgraph Finder Task. Integrates with the stand-alone library
 * with the same name to find subgraphs and return them in Cytoscape Format.
 * 
 * @author Charles Allen Schultz II
 * @version 1.4 June 19, 2015
 */
public class SubgraphFinderTask extends AbstractNetworkTask implements PropertyChangeListener
{
	private final ConfigurationBundle config;
	private TaskMonitor taskMonitor;

	/**
	 * Constructs the Task.
	 * 
	 * @param network the Network to analyze.
	 * @param config  the Configuration to use.
	 */
	public SubgraphFinderTask(CyNetwork network, ConfigurationBundle config)
	{
		super(network);
		this.config = config;
	}

	@Override
	public void run(final TaskMonitor taskMonitor)
	{
		try
		{
			this.taskMonitor = taskMonitor;
			taskMonitor.setTitle("Subgraph Finder");
			taskMonitor.setStatusMessage("Reading Network.");

			// Read CyNetwork into a Subgraph Finder Network
			Graph graph = null;
			if (!cancelled)
				graph = GraphTaskUtils.convertCyNetwork(network);
			else
				taskMonitor.setStatusMessage("Cancelled");

			if (graph == null)
			{
				taskMonitor.setStatusMessage("Cancelled");
				cancel();
				return;
			}

			// Search for Subgraphs Here
			ArrayList<Graph> subgraphs = null;
			if (!cancelled)
				subgraphs = findSubGraphs(taskMonitor, graph);
			else
				taskMonitor.setStatusMessage("Cancelled");

			// Save Found Subgraphs To Current Network, child network, file...
			if (!cancelled)
				GraphTaskUtils.saveSubGraphs(taskMonitor, subgraphs, network, config);
			else
				taskMonitor.setStatusMessage("Cancelled");

		}
		catch (Throwable error)
		{
//			GraphTaskUtils.showError("Error during Subgraph Search", error.getMessage());
			SwingUtilities.invokeLater(() -> {
				String message = "An error occurred during execution!";
				ErrorPanel errorPanel = new ErrorPanel(message, error);
				errorPanel.display(Global.getDesktopService().getJFrame(), message);
			});
			cancel();
		}
	}

	/**
	 * Processes the graph and returns the subgraphs.
	 * 
	 * @param taskMonitor the TaskMonitor to use to report progress.
	 * @param target      the Graph object to analyze.
	 * @return the ArrayList containing the subgraphs.
	 */
	private ArrayList<Graph> findSubGraphs(final TaskMonitor taskMonitor, Graph target)
	{
		taskMonitor.setStatusMessage("Searching for subgraphs...");
		taskMonitor.setProgress(0D);

		// added updated status messages
		taskMonitor.setStatusMessage("Checking selected agorithm...");
		Algorithm algo = config.getAlgo();
		algo.addPropertyChangeListener(this);
		taskMonitor.setStatusMessage("Processing graphs based on " + config.getAlgo().toString());
		ArrayList<Graph> graphs = algo.process(target);

		return graphs;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (!cancelled && evt.getPropertyName().equals(Algorithm.PROP_PROGRESS))
		{
			taskMonitor.setProgress((Double) evt.getNewValue());
		}
	}

	/**
	 * Cancels the Task.
	 */
	@Override
	public void cancel()
	{
		super.cancel();
		if (taskMonitor != null)
			taskMonitor.setProgress(-1D);
	}

}
