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

import org.cytoscape.model.CyNode;

import edu.claflin.finder.logic.Node;

/**
 * Represents an adaptation of the CyNode to the finder Node.
 * @author Charles Allen Schultz II
 * @version 1.0 June 16, 2015
 */
public class CyNodeAdapter extends Node
{

	/**
	 * The CyNode this Finder Node represents.
	 */
	private final CyNode cynode;

	/**
	 * Constructs the Adapter
	 * @param cynode the CyNode to point the Finder Node to.
	 * @param name the Name to use for exporting.
	 */
	public CyNodeAdapter(CyNode cynode, String name)
	{
		super(name);
		this.cynode = cynode;
	}

	/**
	 * Returns the CyNode this Node represents.
	 * @return the CyNode this Node represents.
	 */
	public CyNode getCyNode()
	{
		return cynode;
	}

	@Override
	public CyNodeAdapter duplicate()
	{
		return new CyNodeAdapter(cynode, this.getIdentifier());
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CyNodeAdapter)
		{
			CyNodeAdapter node = (CyNodeAdapter) o;
			return node.getIdentifier().equals(this.getIdentifier());
		}
		else if (o instanceof Node) 
		{
			return super.equals(o);
		}
		else
		{
			return false;
		}
	}
}
