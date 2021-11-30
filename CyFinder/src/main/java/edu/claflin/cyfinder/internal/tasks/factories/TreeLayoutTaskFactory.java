package edu.claflin.cyfinder.internal.tasks.factories;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.claflin.cyfinder.internal.tasks.TreeLayoutTask;

public class TreeLayoutTaskFactory  extends AbstractTaskFactory
{
	/**
	 * Constructs a Task Factory
	 * @param appManager plugin manager
	 */
	public TreeLayoutTaskFactory()
	{
		super();		
	}

	/**
	 * Returns Task Iterator with Bipartite Layout Task
	 */
	@Override
	public TaskIterator createTaskIterator()
	{
		return new TaskIterator(new TreeLayoutTask());
	}
}
