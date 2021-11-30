package edu.claflin.cyfinder.internal.logic;

import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.View;

import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Node;

/**
 * Represents an adaptation of the CyEdge view to the finder Edge.
 * @author Cesar Martin
 *
 */
public class CyEdgeViewAdapter extends CyEdgeAdapter
{
	private final View<CyEdge> cyedgeview;
	
	public CyEdgeViewAdapter(CyNodeViewAdapter node1, CyNodeViewAdapter node2, Double data, View<CyEdge> cyedge) 
	{
		super(node1, node2, data, cyedge.getModel());
		this.cyedgeview = cyedge;
	}
	
	public View<CyEdge> getCyEdgeView() 
	{
		return cyedgeview;
	}
	
	@Override
	public CyEdgeViewAdapter duplicate(Node source, Node destination)
	{
		if (!source.equals(this.getSource()) || !destination.equals(this.getTarget()))
		{
			throw new IllegalArgumentException(
					"The new source and " + "destination nodes must be equivalent to the old " + "ones!");
		}

		CyNodeViewAdapter node1 = (CyNodeViewAdapter) source;
		CyNodeViewAdapter node2 = (CyNodeViewAdapter) destination;
		return new CyEdgeViewAdapter(node1, node2, this.getData(), cyedgeview);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CyEdgeViewAdapter)
		{
			CyEdgeViewAdapter edge = (CyEdgeViewAdapter) o;
			boolean equivalent = (edge.getSource().equals(this.getSource()) && edge.getTarget().equals(this.getTarget())
					|| (edge.getSource().equals(this.getTarget()) && edge.getTarget().equals(this.getSource())
							&& edge.isUndirected()));
			return equivalent;
		}
		else if (o instanceof CyEdgeAdapter || o instanceof Edge) 
		{
			return super.equals(o);
		}
		else 
		{
			return false;	
		}		
	}
}
