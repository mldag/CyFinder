/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.claflin.finder.logic.communities.struct.walk_trap_struct;

/**
 *
 * @author Cesar Martin
 */
public class HeapNodeWT implements Comparable<HeapNodeWT>
{
	private int index1;
	private int index2;
	private double sigma;
	public static final double EPSILON = 1E-14;

	public HeapNodeWT(int ind1, int ind2, double sig)
	{
		this.index1 = ind1;
		this.index2 = ind2;
		this.sigma = sig;
	}

	public int getIndex1()
	{
		return index1;
	}

	public int getIndex2()
	{
		return index2;
	}

	public void setIndex1(int ind)
	{
		index1 = ind;
	}

	public void setIndex2(int ind)
	{
		index2 = ind;
	}

	public double getValue()
	{
		return sigma;
	}

	public void setSigma(double nwsig)
	{
		sigma = nwsig;
	}

	public HeapNodeWT duplicate()
	{
		return new HeapNodeWT(this.getIndex1(), this.getIndex2(), this.getValue());
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof HeapNodeWT)
		{
			return this.compareTo((HeapNodeWT) o) == 0;
		}
		return false;
	}

	@Override
	public int compareTo(HeapNodeWT that)
	{
		Double i = this.getValue();
		Double j = that.getValue();

		if (Math.abs(i - j) < EPSILON)
		{
			return 0;
		}
		else if (i < j)
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}

	@Override
	public String toString()
	{
		return "[ i: " + this.getIndex1() + " j: " + this.getIndex2() + " Sigma: " + getValue() + " ] ";
	}
}
