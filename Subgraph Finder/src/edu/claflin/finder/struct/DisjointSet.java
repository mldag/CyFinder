package edu.claflin.finder.struct;

import java.util.LinkedList;

/**
 * A LinkedList-based Disjoint Set data structure. Three operations combined O(m
 * + n* log(n)).
 */
public class DisjointSet<T extends Comparable<T>>
{
	LinkedList<T>[] list;
	private int size;
	private int length;

	public DisjointSet(int size)
	{
		this.size = size;
		list = new LinkedList[this.size];
		length = 0;

		for (int i = 0; i < size; i++)
		{
			list[i] = new LinkedList<T>();
		}
	}

	public int size()
	{
		return length;
	}

	public boolean contains(T a)
	{
		return find(a) != null;
	}

	public void makeSet(T n)
	{
		if (find(n) == null)
		{
			list[length].add(n);
			length++;
		}
	}

	public T find(T x)
	{
		T representative = null;

		for (int i = 0; i < length; i++)
		{
			if (list[i].contains(x))
			{
				representative = list[i].get(0);
			}
		}
		return representative;
	}

	public boolean disjointElements(T a, T b)
	{
		return !find(a).equals(find(b));
	}

	public void merge(T a, T b)
	{
		T ra = find(a);
		T rb = find(b);

		if (ra != null && rb != null && !ra.equals(rb))
		{
			int rai = -1;
			int rbi = -1;

			for (int i = 0; i < length; i++)
			{
				if (list[i].get(0).equals(ra))
				{
					rai = i;
				}
				else if (list[i].get(0).equals(rb))
				{
					rbi = i;
				}
			}

			if (rai != -1 && rbi != -1 && rai != rbi)
			{
				list[rai].addAll(list[rbi]);
				list[rbi] = list[length - 1];
				length--;
			}
		}
	}

	@Override
	public String toString()
	{
		String result = "";
		for (int i = 0; i < length; i++)
		{
			result += list[i].toString() + "\n";
		}
		return result;
	}

}
