package edu.claflin.cyfinder.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

public class DummyTask extends AbstractNetworkTask
{

	public DummyTask(CyNetwork network)
	{
		super(network);
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception
	{
		
	}

}
