/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.claflin.finder.logic.communities.struct.fast_greedy_struct;

/**
 *
 * @author César Martín Pavón
 */
public class IndexedHeapNode extends HeapNodeFG
{
	private int i;

	public IndexedHeapNode(int i, int j, double q)
	{
		super(j, q);
		this.i = i;
	}

	/**
	 * @return the i
	 */
	public int getI()
	{
		return i;
	}

	public void setI(int i)
	{
		this.i = i;
	}

	public void setInfo(int i, int j, double q)
	{
		this.i = i;
		super.setInfo(j, q);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof IndexedHeapNode)
		{
			IndexedHeapNode that = (IndexedHeapNode) o;
			return Math.abs(this.getQ() - that.getQ()) < EPSILON;
		}
		return false;
	}

	@Override
	public int compareTo(HeapNodeFG that)
	{
		if (that instanceof IndexedHeapNode)
		{
			IndexedHeapNode ithat = (IndexedHeapNode) that;

			int qresult = super.compareToQ(that);
			if (qresult == 0)
			{
				if (this.getI() == ithat.getI())
				{
					return 0;
				}
				else if (this.getI() < ithat.getI())
				{
					return -1;
				}
				else
				{
					return 1;
				}
			}
			else
			{
				return qresult;
			}
		}
		return 1;
	}

	@Override
	public String toString()
	{
		return "[ i: " + i + " " + super.toString() + " ] ";
	}

}
