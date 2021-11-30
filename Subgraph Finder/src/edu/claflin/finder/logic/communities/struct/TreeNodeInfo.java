/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.claflin.finder.logic.communities.struct;

/**
 *
 * @author Cesar Martin
 */
public abstract class TreeNodeInfo implements Comparable<TreeNodeInfo>
{
	private int index;

	public TreeNodeInfo(int ind)
	{
		index = ind;
	}

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int ind)
	{
		index = ind;
	}

	public double getValue()
	{
		return index;
	}

	public abstract TreeNodeInfo duplicate();

	public abstract void handleEqualIndex(TreeNodeInfo o);

	@Override
	public String toString()
	{
		return "[ index: " + this.getIndex() + " ]";
	}
}
