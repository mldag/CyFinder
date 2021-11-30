package edu.claflin.finder.algo.clustering.struct;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class BinarySearchTree<T extends TreeNodeInfo>
{
	private TreeNode<T> root; // root of the bst; implemented as a dummy node.
	private boolean allow_duplicates;

	/**
	 * Create an empty BinarySearchTree.
	 */
	public BinarySearchTree()
	{
		root = new TreeNode<>(); // dummy node as the root
		allow_duplicates = false;
	}

	public BinarySearchTree(boolean dup)
	{
		root = new TreeNode<>(); // dummy node as the root
		allow_duplicates = dup;
	}

	public boolean allows_duplicates()
	{
		return this.allow_duplicates;
	}

	/**
	 * Is the BinarySearchTree empty?
	 *
	 * @return true if empty, false otherwise
	 */
	public boolean isEmpty()
	{
		return root.getLeftChild() == null;
	}

	public void clear()
	{
		root.setLeftChild(null);
	}

	/**
	 * Addd a new Tree Node with the given j index and q value
	 *
	 * @param info the key
	 */
	public void add(T info)
	{
		if (this.isEmpty())
		{
			TreeNode<T> p = new TreeNode<>(info);
			root.setLeftChild(p);
		}
		else
		{
			add(info, root.getLeftChild());
			DSW();
		}
	}

	/**
	 * Recursive implementation of add.
	 *
	 * @param info the key
	 * @param p    the current top Tree Node
	 */
	private void add(T info, TreeNode<T> p) // insert renamed add
	{
		TreeNode<T> x = new TreeNode<>(info);

		if (x.compareTo(p) == 0)
		{
			if (this.allows_duplicates())
			{
				if (p.getLeftChild() == null)
				{
					p.setLeftChild(x);
				}
				else
				{
					add(info, p.getLeftChild());
				}
			}
			else
			{
				p.getInfo().handleEqualIndex(info);
			}

		}
		else if (x.compareTo(p) < 0)
		{
			if (p.getLeftChild() == null)
			{
				p.setLeftChild(x);
			}
			else
			{
				add(info, p.getLeftChild());
			}
		}
		else if (p.getRightChild() == null)
		{
			p.setRightChild(x);
		}
		else
		{
			add(info, p.getRightChild());
		}
	}

	public Double getVal(T info)
	{
		T found = get(info);
		return found == null ? null : found.getValue();
	}

	public T get(T info)
	{
		TreeNode<T> found = get(info, root.getLeftChild());
		return found == null ? null : (T) found.getInfo();
	}

	private TreeNode<T> get(T info, TreeNode<T> p)
	{

		if (p == null)
		{
			return null;
		}
		else if (info.compareTo(p.getInfo()) == 0)
		{
			return p;
		}
		else if (info.compareTo(p.getInfo()) < 0)
		{
			return get(info, p.getLeftChild());
		}
		else
		{
			return get(info, p.getRightChild());
		}

	}

	public void set(T info)
	{
		TreeNode<T> found = get(info, root.getLeftChild());

		if (found != null)
		{
			found.setInfo(info);
		}
	}

	/**
	 * Is the specified ElemntType in the BinarySearchTree?
	 *
	 * @param info the key
	 *
	 * @return true if it is contained, false otherwise
	 */
	public boolean contains(T info)
	{
		return contains(info, root.getLeftChild());
	}

	/*
	 * Recursive implementation of contains.
	 *
	 * @param info the key to search for
	 * 
	 * @param p the TreeNode<T> to check for inclusion and recursive parameter
	 *
	 * @return true if the ElementType is included, false otherwise
	 */
	private boolean contains(T info, TreeNode<T> p) // search renamed contains
	{
		if (p == null)
		{
			return false;
		}
		else if (info.compareTo(p.getInfo()) == 0)
		{
			return true;
		}
		else if (info.compareTo(p.getInfo()) < 0)
		{
			return contains(info, p.getLeftChild());
		}
		else
		{
			return contains(info, p.getRightChild());
		}
	}

	/**
	 * Gets an inorder String representation of the BinarySearchTree.
	 *
	 * @return the BinarySearchTree as a String
	 */
	@Override
	public String toString()
	{
		String s = toString(root.getLeftChild());
		return s.substring(0, s.length() -2);
	}

	/**
	 * Recursive implementation of inorder toString.
	 *
	 * @param p the current TreeNode to print and recursive parameter
	 *
	 * @return the BinarySearchTree as a String
	 */
	private String toString(TreeNode<T> p)
	{
		String result = "";

		if (p != null)
		{
			result += toString(p.getLeftChild());
			result += p.toString() + ", ";
			result += toString(p.getRightChild());
		}
		return result;
	}

	public T getMin()
	{
		return (T) getMin(root.getLeftChild());
	}

	private T getMin(TreeNode<T> p)
	{
		if (p.getLeftChild() == null)
		{
			return p.getInfo();
		}
		else
		{
			return (T) getMin(p.getLeftChild());
		}
	}

	public T getMax()
	{
		return (T) getMax(root.getLeftChild());
	}

	public T getMax(TreeNode<T> p)
	{
		if (p.getRightChild() == null)
		{
			return p.getInfo();
		}
		else
		{
			return (T) getMax(p.getRightChild());
		}
	}

	/*
	 * https://www.geeksforgeeks.org/binary-search-tree-set-2-delete/
	 */
	public void remove(T info)
	{
		if (this.contains(info))
		{
			TreeNode<T> new_root = remove(info, root.getLeftChild());
			root.setLeftChild(new_root);
			DSW();
		}
	}

	private TreeNode<T> remove(T info, TreeNode<T> p)
	{
		/* Base Case: If the tree is empty */
		if (p == null)
		{
			return p;
		}

		/* Otherwise, recur down the tree */
		if (info.compareTo(p.getInfo()) < 0)
		{
			p.setLeftChild(remove(info, p.getLeftChild()));
		}
		else if (info.compareTo(p.getInfo()) > 0)
		{
			p.setRightChild(remove(info, p.getRightChild()));
		}

		// if key is same as p's
		// key, then This is the
		// node to be deleted
		else
		{
			// node with only one child or no child
			if (p.getLeftChild() == null)
			{
				return p.getRightChild();
			}
			else if (p.getRightChild() == null)
			{
				return p.getLeftChild();
			}

			// node with two children: Get the inorder
			// successor (smallest in the right subtree)
			T min = (T) getMin(p.getRightChild());

			p.setInfo(min);

			// Delete the inorder successor
			p.setRightChild(remove(p.getInfo(), p.getRightChild()));
		}

		return p;
	}

	public int getHeight()
	{
		return getHeight(root.getLeftChild());
	}

	private int getHeight(TreeNode<T> p)
	{
		if (p == null)
		{
			return -1;
		}
		else
		{
			return 1 + Math.max(getHeight(p.getLeftChild()), getHeight(p.getRightChild()));
		}
	}

	/*
	 * http://www.geekviewpoint.com/java/bst/dsw_algorithm
	 */
	public void DSW()
	{
		if (!this.isEmpty())
		{
			createBackbone();// effectively: createBackbone( root)
			createPerfectBST();// effectively: createPerfectBST( root)
		}
	}

	/**
	 * Time complexity: O(n)
	 */
	private void createBackbone()
	{
		TreeNode<T> grandParent = null;
		TreeNode<T> parent = root.getLeftChild();
		TreeNode<T> leftChild;

		while (parent != null)
		{
			leftChild = parent.getLeftChild();
			if (leftChild != null)
			{
				grandParent = rotateRight(grandParent, parent, leftChild);
				parent = leftChild;
			}
			else
			{
				grandParent = parent;
				parent = parent.getRightChild();
			}
		}
	}

	private TreeNode<T> rotateRight(TreeNode<T> grandParent, TreeNode<T> parent, TreeNode<T> leftChild)
	{
		if (grandParent != null)
		{
			grandParent.setRightChild(leftChild);
		}
		else
		{
			root.setLeftChild(leftChild);
		}
		parent.setLeftChild(leftChild.getRightChild());
		leftChild.setRightChild(parent);
		return grandParent;
	}

	/**
	 * Time complexity: O(n)
	 */
	private void createPerfectBST()
	{
		int n = 0;
		for (TreeNode<T> tmp = root.getLeftChild(); tmp != null; tmp = tmp.getRightChild())
		{
			n++;
		}
		// m = 2^floor[lg(n+1)]-1, ie the greatest power of 2 less than n: minus 1
		int m = greatestPowerOf2LessThanN(n + 1) - 1;
		makeRotations(n - m);

		while (m > 1)
		{
			makeRotations(m /= 2);
		}
	}

	/**
	 * . Time complexity: log(n)
	 *
	 * @param n the inout number
	 * @return the greatest power of 2 less than N
	 */
	private int greatestPowerOf2LessThanN(int n)
	{
		int x = MSB(n);// MSB
		return (1 << x);// 2^x
	}

	/**
	 * Time complexity: log(n)
	 *
	 * return the index of most significant set bit: index of least significant bit
	 * is 0
	 */
	/**
	 * Return the index of most significant set bit: index of least significant bit
	 * is 0. Time complexity: log(n)
	 *
	 * @param n the input number
	 * @return the index of most significant set bit
	 */
	public int MSB(int n)
	{
		int ndx = 0;
		while (1 < n)
		{
			n = (n >> 1);
			ndx++;
		}
		return ndx;
	}

	private void makeRotations(int bound)
	{
		TreeNode<T> grandParent = null;
		TreeNode<T> parent = root.getLeftChild();
		TreeNode<T> child = root.getLeftChild().getRightChild();

		for (; bound > 0; bound--)
		{
			try
			{
				if (child != null)
				{
					rotateLeft(grandParent, parent, child);
					grandParent = child;
					parent = grandParent.getRightChild();
					child = parent.getRightChild();
				}
				else
				{
					break;
				}
			}
			catch (NullPointerException convenient)
			{
				break;
			}
		}
	}

	private void rotateLeft(TreeNode<T> grandParent, TreeNode<T> parent, TreeNode<T> rightChild)
	{
		if (grandParent != null)
		{
			grandParent.setRightChild(rightChild);
		}
		else
		{
			root.setLeftChild(rightChild);
		}
		parent.setRightChild(rightChild.getLeftChild());
		rightChild.setLeftChild(parent);
	}

	public void mergeTrees(BinarySearchTree that)
	{
		mergeTrees(that, that.root.getLeftChild());
	}

	private void mergeTrees(BinarySearchTree that, TreeNode<T> p)
	{
		if (p != null)
		{
			mergeTrees(that, p.getLeftChild());
			this.add(p.getInfo());
			mergeTrees(that, p.getRightChild());
		}
	}

	public List<T> asList()
	{
		List<T> l = new ArrayList<>();
		asList(l, root.getLeftChild());
		return l;
	}

	private void asList(List<T> l, TreeNode<T> p)
	{
		if (p != null)
		{
			asList(l, p.getLeftChild());
			l.add(p.getInfo());
			asList(l, p.getRightChild());
		}
	}
}
