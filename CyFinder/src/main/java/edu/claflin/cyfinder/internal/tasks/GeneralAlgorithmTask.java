package edu.claflin.cyfinder.internal.tasks;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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

public class GeneralAlgorithmTask extends AbstractNetworkTask implements PropertyChangeListener
{
	protected final ConfigurationBundle config;
	protected TaskMonitor taskMonitor;
	protected HashMap<String, String> messages;

	protected List<Graph> algoResults;

	public GeneralAlgorithmTask(CyNetwork network, ConfigurationBundle config, HashMap<String, String> messages)
	{
		super(network);
		this.config = config;
		this.messages = messages;
		algoResults = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(final TaskMonitor taskMonitor)
	{
		try
		{
			this.taskMonitor = taskMonitor;

			initialize(taskMonitor);

			Graph graph = readNetworkToGraph(taskMonitor);

			List<Graph> subgraphs = execute(taskMonitor, graph);

			save(subgraphs);
		}
		catch (Throwable error)
		{
			SwingUtilities.invokeLater(() -> {
				String message = "An error occurred during execution!";
				ErrorPanel errorPanel = new ErrorPanel(message, error);
				errorPanel.display(Global.getDesktopService().getJFrame(), message);
			});
			cancel();
		}
	}

	protected void initialize(final TaskMonitor taskMonitor)
	{
		taskMonitor.setTitle(messages.get("Title"));
	}

	protected Graph readNetworkToGraph(final TaskMonitor taskMonitor)
	{
		taskMonitor.setStatusMessage("Reading Network.");

		if (!cancelled)
			return GraphTaskUtils.convertCyNetwork(network, config.getWeightAttribute());
		else
			return null;
	}

	/**
	 * For additionally configure the ArgumentsBundle
	 */
	protected void preconfigure()
	{

	}

	/**
	 * Subgraph post processing. e.g. naming
	 * 
	 * @param source the original Graph
	 * @param graphs the graphs to process
	 * @return the processed graphs
	 */
	protected List<Graph> postconfigure(Graph original, List<Graph> subgraphs)
	{
		return subgraphs;
	}

	protected List<Graph> execute(final TaskMonitor taskMonitor, Graph original)
	{
		taskMonitor.setStatusMessage(messages.get("Execute"));

		if (!cancelled)
		{
			taskMonitor.setStatusMessage(messages.get("Search"));
			taskMonitor.setProgress(0D);

			preconfigure(); // maybe set up an algorithm bundle variable only in this algorithm

			Algorithm algo = config.getAlgo();
			algo.addPropertyChangeListener(this);

			taskMonitor.setStatusMessage("Processing graphs based on " + config.getAlgo().toString());

			/*
			 * We have to execute the algorithms here. Because they are computationally
			 * expensive and we also have a cancellable progress dialogue we need 2 Threads:
			 * one for executing and another for listening to cancelling. Whichever finishes
			 * first cancels the other. To do this we use a CountDownLatch that permits the
			 * flow to continue when either thread finishes.
			 */

			CountDownLatch latch = new CountDownLatch(1);
			AlgoExecutor ae = new AlgoExecutor(algo, original, latch);
			CancelSentinel cs = new CancelSentinel(latch);
			ae.start();
			cs.start();

			try
			{
				latch.await();
				
				/** 
				 * stop is deprecetaed because it is unsafe but in this
				 * case it should be good because the threads have no
				 * variables to corrupt. AlgoExecutor can corrupt
				 * algoResults but if AlgoExecutor is stopped then
				 * algoResults won't be used because the task
				 * was cancelled.
				 */
				ae.stop();
				cs.stop();
			}
			catch (InterruptedException e)
			{

			}
			catch (Exception e)
			{

			}

			taskMonitor.setProgress(1D);

			return postconfigure(original, algoResults); // maybe name graphs
		}
		else
			return null;
	}

	protected void save(List<Graph> subgraphs)
	{
		if (!cancelled)
			GraphTaskUtils.saveSubGraphs(taskMonitor, subgraphs, network, config);
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

	protected class AlgoExecutor extends Thread
	{
		private Algorithm algo;
		private Graph graph;
		private CountDownLatch latch;

		public AlgoExecutor(Algorithm algo, Graph graph, CountDownLatch latch)
		{
			super("Thread for: " + algo.toString());
			this.algo = algo;
			this.graph = graph;
			this.latch = latch;
		}

		@Override
		public void run()
		{
			algoResults = algo.process(graph);
			latch.countDown();
		}
	}

	protected class CancelSentinel extends Thread
	{
		private CountDownLatch latch;

		public CancelSentinel(CountDownLatch latch)
		{
			super("Thread for Cancel Sentinel");
			this.latch = latch;
		}

		@Override
		public void run()
		{
			while (!cancelled)
			{

			}
			latch.countDown();
		}
	}
}
