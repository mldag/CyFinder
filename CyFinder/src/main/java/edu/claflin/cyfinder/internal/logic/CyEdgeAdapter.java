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
package edu.claflin.cyfinder.internal.logic;

import org.cytoscape.model.CyEdge;

import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Node;

/**
 * Represents an adaptation of the CyEdge to the finder Edge
 * 
 * @author Charles Allen Schultz II
 * @version 1.1 February 23, 2016
 * @param <D>
 */
public class CyEdgeAdapter extends Edge
{

	/**
	 * The CyNode this Finder Node represents.
	 */
	private final CyEdge cyedge;

	/**
	 * Constructs the Adapter
	 * 
	 * @param cyedge the CyNode to point the Finder Node to.
	 */
	public CyEdgeAdapter(CyNodeAdapter node1, CyNodeAdapter node2, Double data, CyEdge cyedge)
	{
		super(node1, node2, data, !cyedge.isDirected());
		this.cyedge = cyedge;
	}

	/**
	 * Returns the CyEdge this Edge represents.
	 * 
	 * @return the CyEdge this Edge represents.
	 */
	public CyEdge getCyEdge()
	{
		return cyedge;
	}

	@Override
	public CyEdgeAdapter duplicate(Node source, Node destination)
	{
		if (!source.equals(this.getSource()) || !destination.equals(this.getTarget()))
		{
			throw new IllegalArgumentException(
					"The new source and " + "destination nodes must be equivalent to the old " + "ones!");
		}

		CyNodeAdapter node1 = (CyNodeAdapter) source;
		CyNodeAdapter node2 = (CyNodeAdapter) destination;
		return new CyEdgeAdapter(node1, node2, this.getData(), cyedge);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CyEdgeAdapter)
		{
			CyEdgeAdapter edge = (CyEdgeAdapter) o;
			boolean equivalent = (edge.getSource().equals(this.getSource()) && edge.getTarget().equals(this.getTarget())
					|| (edge.getSource().equals(this.getTarget()) && edge.getTarget().equals(this.getSource())
							&& edge.isUndirected()));
			return equivalent;
		}
		else if (o instanceof Edge)
		{
			return super.equals(o);
		}
		else
		{
			return false;
		}
	}
}
