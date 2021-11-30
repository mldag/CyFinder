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
public class HeapNodeFG implements Comparable<HeapNodeFG>
{
	private int j;
	private double Q;
	public static final double EPSILON = 1E-14;

	public HeapNodeFG(int j, double q)
	{
		this.j = j;
		this.Q = q;
	}

	/**
	 * @return the j
	 */
	public int getJ()
	{
		return j;
	}

	public void setJ(int j)
	{
		this.j = j;
	}

	/**
	 * @return the maxQ
	 */
	public double getQ()
	{
		return Q;
	}

	public void setQ(double q)
	{
		this.Q = q;
	}

	public void setInfo(int j, double q)
	{
		this.setJ(j);
		this.setQ(q);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof HeapNodeFG)
		{
			HeapNodeFG that = (HeapNodeFG) o;
			return Math.abs(this.Q - that.Q) < EPSILON;
		}
		return false;
	}

	@Override
	public String toString()
	{
		return "[ j: " + this.j + " Q: " + this.Q + " ] ";
	}

	/**
	 * Compare this HeapNode to that HeapNode. We want before means higher Q, then
	 * smaller j
	 *
	 * @param that the HeapNode to compare this to
	 * @return -1 if this goes before, 0 if they are equal, 1 if that goes before
	 */
	@Override
	public int compareTo(HeapNodeFG that)
	{
		if (Math.abs(this.getQ() - that.getQ()) < EPSILON)
		{
			if (this.j == that.j)
			{
				return 0;
			}
			else if (this.j < that.j)
			{
				return -1;
			}
			else
			{
				return 1;
			}
		}
		else if (this.getQ() < that.getQ())
		{
			return 1;
		}
		else
		{
			return -1;
		}
	}

	public int compareToQ(HeapNodeFG that)
	{
		if (Math.abs(this.getQ() - that.getQ()) < EPSILON)
		{
			return 0;
		}
		else if (this.getQ() < that.getQ())
		{
			return 1;
		}
		else
		{
			return -1;
		}
	}
}
