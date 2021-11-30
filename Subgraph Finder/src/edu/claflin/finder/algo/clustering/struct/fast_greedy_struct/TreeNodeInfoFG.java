/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.claflin.finder.algo.clustering.struct.fast_greedy_struct;

import edu.claflin.finder.algo.clustering.struct.TreeNodeInfo;

/**
 *
 * @author Cesar Martin
 */
public class TreeNodeInfoFG extends TreeNodeInfo
{
	private double Q;

	public TreeNodeInfoFG(int ind, double q)
	{
		super(ind);
		Q = q;
	}

	@Override
	public double getValue()
	{
		return Q;
	}

	public void setQ(double nwq)
	{
		Q = nwq;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof TreeNodeInfoFG)
		{
			return this.getIndex() == ((TreeNodeInfoFG) o).getIndex();
		}
		return false;
	}

	@Override
	public TreeNodeInfoFG duplicate()
	{
		return new TreeNodeInfoFG(this.getIndex(), this.getValue());
	}

	@Override
	public int compareTo(TreeNodeInfo that)
	{
		Integer i = this.getIndex();
		Integer j = that.getIndex();

		return i.compareTo(j);
	}

	@Override
	public String toString()
	{
		return "[ j: " + this.getIndex() + " Q: " + getValue() + " ] ";
	}

	@Override
	public void handleEqualIndex(TreeNodeInfo o)
	{
		if (o instanceof TreeNodeInfoFG)
		{
			TreeNodeInfoFG that = (TreeNodeInfoFG) o;
			this.setQ(this.getValue() + that.getValue());
		}
	}
}
