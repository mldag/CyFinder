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

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.claflin.cyfinder.internal.tasks.MakeUndirectedTask;

/**
 * Constructs a MakeUndirectedTask.
 * 
 * @author Charles Allen Schultz II
 * @version 1.1 June 19, 2015
 */
public class MakeUndirectedTaskFactory extends AbstractNetworkTaskFactory
{

	/**
	 * Boolean indicating if new edges should be added to make the network
	 * undirected (true) or if old edges should be converted (false).
	 */
	private final boolean additive;

	/**
	 * Constructs the Task Factory.
	 * 
	 * @param additive the boolean indicating the method of conversion.
	 */
	public MakeUndirectedTaskFactory(boolean additive)
	{
		this.additive = additive;
	}
	
	@Override
	public boolean isReady(CyNetwork cn) 
	{
		return cn.getNodeCount() >= 1;
	}

	@Override
	public TaskIterator createTaskIterator(CyNetwork cn)
	{
		return new TaskIterator(new MakeUndirectedTask(cn, additive));
	}
}
