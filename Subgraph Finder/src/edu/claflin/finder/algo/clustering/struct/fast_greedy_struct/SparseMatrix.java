package edu.claflin.finder.algo.clustering.struct.fast_greedy_struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import edu.claflin.finder.algo.clustering.struct.BinarySearchTree;

/**
 * SparseMatrix is a sparse matrix with row format.
 */
public class SparseMatrix
{
	public HashMap<Integer, PriorityQueue<HeapNodeFG>> heaps;
	public HashMap<Integer, BinarySearchTree<TreeNodeInfoFG>> trees;
	private int rows;

	/**
	 * empty sparse matrix with allocated number of rows
	 *
	 * @param rows the number of rows
	 */
	public SparseMatrix(int rows)
	{
		trees = new HashMap<>(rows);
		heaps = new HashMap<>(rows);
		this.rows = rows;
	}

	/**
	 * get the rows of the matrix
	 *
	 * @return the number of rows in the matrix
	 */
	public int rows()
	{
		return rows;
	}

	/**
	 * Add the given j index, q value pair to the ith tree and heap
	 *
	 * @param i the row to add
	 * @param j the j index
	 * @param q the q value
	 */
	public void add(int i, int j, double q)
	{
		addTree(i, j, q);
		addHeap(i, j, q);
	}

	/**
	 * Add the given j index, q value pair to the ith tree.
	 *
	 * @param i the row to add
	 * @param j the j index
	 * @param q the q value
	 */
	public void addTree(int i, int j, double q)
	{

		if (trees.containsKey(i))
		{
			trees.get(i).add(new TreeNodeInfoFG(j, q));
		}
		else
		{
			BinarySearchTree<TreeNodeInfoFG> bst = new BinarySearchTree<>();
			bst.add(new TreeNodeInfoFG(j, q));
			trees.put(i, bst);
		}
	}

	/**
	 * Add the given j index, q value pair to the ith heap.
	 *
	 * @param i the row to add
	 * @param j the j index
	 * @param q the q value
	 */
	public void addHeap(int i, int j, double q)
	{

		if (heaps.containsKey(i))
		{
			heaps.get(i).add(new HeapNodeFG(j, q));
		}
		else
		{
			PriorityQueue<HeapNodeFG> mh = new PriorityQueue<>();
			mh.add(new HeapNodeFG(j, q));
			heaps.put(i, mh);
		}

	}

	/**
	 * get number at index or null if not set.
	 *
	 * @param i the row to search
	 * @param j the colum to search
	 * @return the q value if it exists or null
	 */
	public Double get(int i, int j)
	{
		if (!trees.containsKey(i))
		{
			return null;
		}
		return trees.get(i).getVal(new TreeNodeInfoFG(j, -1.0));
	}

	/**
	 * Set value q and the ith tree's j column. add it if it's not there.
	 *
	 * @param i the row to set in
	 * @param j the column to set in
	 * @param q the new q value
	 *
	 */
	public void setTree(int i, int j, double q)
	{

		Double b = get(i, j);

		if (b != null)
		{
			trees.get(i).set(new TreeNodeInfoFG(j, q));
		}
		else
		{
			addTree(i, j, q);
		}

	}

	/**
	 * Set the value q and the ith heap's j column. add it if it's not there.
	 *
	 * @param i the row to set in
	 * @param j the column to set in
	 * @param q the new q value
	 *
	 */
	public void setHeap(int i, int j, double q)
	{
		if (heaps.containsKey(i))
		{
			PriorityQueue<HeapNodeFG> qh = heaps.get(i);
			ArrayList<HeapNodeFG> hl = new ArrayList<>();

			boolean found = false;
			while (!qh.isEmpty() && !found)
			{
				HeapNodeFG hn = qh.poll();

				if (hn.getJ() == j)
				{
					hn.setQ(q);
					qh.add(hn);
					found = true;
				}
				else
				{
					hl.add(hn);
				}
			}

			for (HeapNodeFG hn : hl)
			{
				qh.add(hn);
			}

			if (!found)
			{
				this.addHeap(i, j, q);
			}
		}
		else
		{
			this.addHeap(i, j, q);
		}

	}

	/**
	 * Set the q value at the ith tree and heap's j column.
	 *
	 * @param i the row to set in
	 * @param j the column to set in
	 * @param q the new q value
	 */
	public void set(int i, int j, double q)
	{
		this.setTree(i, j, q);
		this.setHeap(i, j, q);
	}

	/*
	 * Remove column j from the tree i
	 *
	 * @param i the tree to remove in
	 * 
	 * @param j the column to remove
	 */
	private void removeFromTree(int i, int j)
	{
		Double b = get(i, j);

		if (b != null)
		{
			trees.get(i).remove(new TreeNodeInfoFG(j, -1.0));
		}
	}

	/**
	 * Get the maximum q value at row i
	 *
	 * @param i the row to search in
	 * @return the maximum q value
	 */
	public HeapNodeFG getMax(int i)
	{
		if (heaps.containsKey(i))
		{
			return heaps.get(i).peek();
		}
		return null;
	}

	/**
	 * Clear the ith tree and heap
	 *
	 * @param i the row to clear in
	 */
	public void clearRow(int i)
	{
		if (heaps.containsKey(i) && trees.containsKey(i))
		{
			this.heaps.remove(i);
			this.trees.remove(i);
			rows--;
		}
	}

	/**
	 * Merge Trees j and i into j, summing values if the same column occurs.
	 *
	 * @param j the tree to expand
	 * @param i the tree that offers its Nodes
	 */
	public void mergeTrees(int j, int i)
	{
		if (trees.containsKey(i) && trees.containsKey(j))
		{
			this.removeFromTree(i, j); // remove col j from tree i to not add j,j
			trees.get(j).mergeTrees(trees.get(i)); // merge trees j and i into j
		}
	}

	/**
	 * Rebuild heap j with the tree j
	 *
	 * @param j the heap to rebuild
	 */
	public void rebuildHeap(int j)
	{
		if (heaps.containsKey(j) && trees.containsKey(j))
		{
			heaps.get(j).clear();

			for (TreeNodeInfoFG t : trees.get(j).asList())
			{
				TreeNodeInfoFG inf = t;
				HeapNodeFG h = new HeapNodeFG(inf.getIndex(), inf.getValue());
				heaps.get(j).add(h);
			}
		}

	}

	@Override
	public String toString()
	{
		String result = "";

		for (Integer i : trees.keySet())
		{
			if (!trees.get(i).isEmpty())
			{
				result += i + "\n";
				result += "Heap: " + heaps.get(i) + "\n";
				result += "Tree: " + trees.get(i) + "\n";
			}
		}
		return result;
	}
}
