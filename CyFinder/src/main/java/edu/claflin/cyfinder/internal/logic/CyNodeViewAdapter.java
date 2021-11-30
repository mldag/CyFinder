package edu.claflin.cyfinder.internal.logic;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;

import edu.claflin.finder.logic.Node;

/**
 * Represents an adaptation of a CyNode's view to the finder Node.
 * @author Cesar Martin
 *
 */
public class CyNodeViewAdapter extends CyNodeAdapter
{
	private final View<CyNode> cynodeview;

	public CyNodeViewAdapter(View<CyNode> cynodeview, String name)
	{
		super(cynodeview.getModel(), name);
		this.cynodeview = cynodeview;
	}
	
	public View<CyNode> getCyNodeView() 
	{
		return cynodeview;
	}

	@Override
	public CyNodeViewAdapter duplicate()
	{
		return new CyNodeViewAdapter(cynodeview, this.getIdentifier());
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CyNodeViewAdapter)
		{
			CyNodeViewAdapter node = (CyNodeViewAdapter) o;
			return node.getIdentifier().equals(getIdentifier());
		}
		else if (o instanceof CyNodeAdapter || o instanceof Node) 
		{
			return super.equals(o);
		}
		else
		{
			return false;
		}
	}
}
