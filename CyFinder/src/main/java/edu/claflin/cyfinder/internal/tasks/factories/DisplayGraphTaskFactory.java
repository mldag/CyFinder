package edu.claflin.cyfinder.internal.tasks.factories;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.claflin.cyfinder.internal.tasks.DisplayGraphTask;
import edu.claflin.cyfinder.internal.tasks.MakeUndirectedTask;

public class DisplayGraphTaskFactory extends AbstractNetworkTaskFactory
{

	@Override
	public TaskIterator createTaskIterator(CyNetwork cn)
	{
		TaskIterator t = new TaskIterator();
		t.append(new MakeUndirectedTask(cn, false, "Displaying Graph."));
		t.append(new DisplayGraphTask(cn));
		return t;		
	}

}
