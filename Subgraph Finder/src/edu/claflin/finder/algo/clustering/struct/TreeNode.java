package edu.claflin.finder.algo.clustering.struct;

/*
 * PID: 6062311
 */
/**
 * The basic unit of the BinarySearchTree.
 */
public class TreeNode<T extends TreeNodeInfo> implements Comparable<TreeNode>
{
	private T info;
	private TreeNode left;
	private TreeNode right;

	public TreeNode()
	{
		info = null;
		left = null;
		right = null;
	}

	/**
	 * Create an empty TreeNode.
	 * 
	 * @param info the information
	 */
	public TreeNode(T info)
	{
		this.info = info;
		left = null;
		right = null;
	}

	public TreeNode(T info, TreeNode<T> l, TreeNode<T> r)
	{
		this.info = info;
		this.left = l;
		this.right = r;
	}

	public T getInfo()
	{
		return info;
	}

	/**
	 * Get the left child TreeNode.
	 *
	 * @return the left child TreeNode
	 */
	public TreeNode getLeftChild()
	{
		return left;
	}

	/**
	 * Get the right child TreeNode.
	 *
	 * @return the right child TreeNode
	 */
	public TreeNode getRightChild()
	{
		return right;
	}

	/**
	 * Set all the information of this TreeNode.
	 *
	 * @param info the information
	 * @param l    the left child TreeNode
	 * @param r    the right child TreeNode
	 */
	public void setNode(T info, TreeNode<T> l, TreeNode<T> r)
	{
		this.info = info;
		this.left = l;
		this.right = r;
	}

	/**
	 * Set the information of this TreeNode.
	 *
	 * @param info the info to set
	 */
	public void setInfo(T info)
	{
		this.info = info;
	}

	/**
	 * Set the left child TreeNode.
	 *
	 * @param left the left child TreeNode
	 */
	public void setLeftChild(TreeNode left)
	{
		this.left = left;
	}

	/**
	 * Set the right child TreeNode.
	 *
	 * @param right the right child TreeNode
	 */
	public void setRightChild(TreeNode right)
	{
		this.right = right;
	}

	public TreeNode<T> duplicate()
	{
		return new TreeNode<T>(this.getInfo(), this.getLeftChild(), this.getRightChild());
	}

	@Override
	public int compareTo(TreeNode that)
	{
		return this.getInfo().compareTo(that.getInfo());
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof TreeNode)
		{
			TreeNode that = (TreeNode) o;
			return this.info.equals(that.info);
		}
		return false;
	}

	@Override
	public String toString()
	{
		return info.toString();
	}

}
