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

import java.util.Collections;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

/**
 * Represents a task to make a network undirected. The means in which the
 * network is converted is dependent upon a boolean set upon construction of the
 * instance.
 * 
 * @author Charles Allen Schultz II
 * @version 1.1 June 19, 2015
 */
public class MakeUndirectedTask extends AbstractNetworkTask
{

	/**
	 * Boolean indicating if new edges should be added to make the network
	 * undirected (true) or if old edges should be converted (false).
	 */
	private final boolean additive;
	private String title; // title for the task monitor if the task is the first step in a broader process

	/**
	 * Constructs the task.
	 * 
	 * @param network  the CyNetwork to operate on.
	 * @param additive the boolean indicating the method of conversion.
	 */
	public MakeUndirectedTask(CyNetwork network, boolean additive)
	{
		super(network);
		this.additive = additive;
		this.title = null;
	}
	
	/**
	 * Constructs the task when it is the first step in a broader process.
	 * 
	 * @param network  the CyNetwork to operate on.
	 * @param additive the boolean indicating the method of conversion.
	 * @param title the title to set to the task monitor in the broader process
	 */
	public MakeUndirectedTask(CyNetwork network, boolean additive, String title)
	{
		super(network);
		this.additive = additive;
		this.title = title;
	}

	@Override
	public void run(TaskMonitor taskMonitor)
	{
		if (title == null) 
		{
			taskMonitor.setTitle("Coercing network to undirected state...");	
		}
		else 
		{
			taskMonitor.setTitle(title);
		}
		
		taskMonitor.setProgress(0D);

		if (additive)
			addNewEdges(taskMonitor);
		else
			convertOldEdges(taskMonitor);		
		
	}

	/**
	 * The old network has edges added to it to force it into an undirected state.
	 * If undirected-like edges already exist between two nodes no action is taken,
	 * even if the edge attributes are different.
	 *
	 * @param taskMonitor the TaskMonitor object to use to report progress.
	 */
	private void addNewEdges(TaskMonitor taskMonitor)
	{
		List<CyEdge> oldEdges = network.getEdgeList();
		int count = 0;

		for (CyEdge edge : oldEdges)
		{
			if (edge.isDirected())
			{
				boolean backEdgeExists = false;
				for (CyEdge backEdge : oldEdges)
				{
					if (edge.getSource() == backEdge.getTarget() && edge.getTarget() == backEdge.getSource())
					{
						backEdgeExists = true;
						break;
					}
				}

				if (!backEdgeExists)
				{
					CyEdge newEdge = network.addEdge(edge.getTarget(), edge.getSource(), true);
					network.getDefaultEdgeTable().getColumns().stream().forEach(col ->
					{
						Object value = network.getDefaultEdgeTable().getRow(edge.getSUID()).get(col.getName(),
								col.getType());
						network.getDefaultEdgeTable().getRow(newEdge.getSUID()).set(col.getName(), value);
					});
				}
			}

			taskMonitor.setProgress(1D * ++count / oldEdges.size());
		}
	}

	/**
	 * Transforms old edges that are not undirected into directed versions.
	 * 
	 * @param taskMonitor the TaskMonitor object to use to report progress.
	 */
	private void convertOldEdges(TaskMonitor taskMonitor)
	{
		List<CyEdge> oldEdges = network.getEdgeList();
		int count = 0;

		for (CyEdge edge : oldEdges)
		{
			if (edge.isDirected())
			{
				CyEdge newEdge = network.addEdge(edge.getSource(), edge.getTarget(), false);
				network.getDefaultEdgeTable().getColumns().stream().forEach(col ->
				{
					Object value = network.getDefaultEdgeTable().getRow(edge.getSUID()).get(col.getName(),
							col.getType());
					network.getDefaultEdgeTable().getRow(newEdge.getSUID()).set(col.getName(), value);
				});
				network.removeEdges(Collections.singletonList(edge));
			}

			taskMonitor.setProgress(1D * ++count / oldEdges.size());
		}
	}
}
