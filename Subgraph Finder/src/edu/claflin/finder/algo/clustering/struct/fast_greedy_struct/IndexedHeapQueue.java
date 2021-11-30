/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.claflin.finder.algo.clustering.struct.fast_greedy_struct;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 *
 * @author Cesar Martin
 */
public class IndexedHeapQueue
{
	private PriorityQueue<IndexedHeapNode> q;

	public IndexedHeapQueue()
	{
		q = new PriorityQueue<>();
	}

	public int size()
	{
		return q.size();
	}

	public boolean isEmpty()
	{
		return q.isEmpty();
	}

	public void add(int i, int j, double a)
	{
		q.add(new IndexedHeapNode(i, j, a));
	}

	public IndexedHeapNode peek()
	{
		return q.peek();
	}

	public IndexedHeapNode poll()
	{
		return q.poll();
	}

	public void set(int i, HeapNodeFG hnn)
	{
		ArrayList<IndexedHeapNode> hl = new ArrayList<>();
		boolean found = false;

		while (!q.isEmpty() && !found)
		{
			IndexedHeapNode hn = q.poll();

			if (hn.getI() == i)
			{
				hn.setInfo(hnn.getJ(), hnn.getQ());
				q.add(hn);
				found = true;
			}
			else
			{
				hl.add(hn);
			}
		}

		for (IndexedHeapNode hn : hl)
		{
			q.add(hn);
		}
	}

	@Override
	public String toString()
	{
		return q.toString();
	}
}
