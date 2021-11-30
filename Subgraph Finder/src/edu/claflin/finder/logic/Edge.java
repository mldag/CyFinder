package edu.claflin.finder.logic;

import static edu.claflin.finder.Global.getLogger;

import java.util.Objects;

import edu.claflin.finder.log.LogLevel;

/**
 * Represents an Edge in memory. A simple implementation requiring only two
 * nodes and a single piece of edge data.
 * 
 * @author Charles Allen Schultz II
 * @version 3.1 February 2, 2016
 */
public class Edge
{
	/**
	 * The node representing the source.
	 */
	private Node source;
	/**
	 * The node representing the target.
	 */
	private Node target;
	/**
	 * The data attached to the edge.
	 */
	private double data;
	/**
	 * Indicates if this edge should be treated as being undirected.
	 */
	private boolean undirected;

	/**
	 * Initializes the Edge object.
	 *
	 * @param source      the Node representing the interaction source.
	 * @param target the Node representing the interaction target.
	 * @param data        the Object representing the Edge data.
	 * @param undirected  the boolean indicating if this edge is undirected.
	 */
	public Edge(Node source, Node target, double data, boolean undirected)
	{
		this.source = source;
		this.target = target;
		this.data = data;
		this.undirected = undirected;

		if (getLogger() != null)
		{
			getLogger().logInfo(LogLevel.DEBUG, "Edge: Created Edge: " + toString());
		}
	}

	/**
	 * Access method for the Edge's source.
	 * 
	 * @return the Node representing the source.
	 */
	public Node getSource()
	{
		return source;
	}

	/**
	 * Access method for the Edge's target.
	 * 
	 * @return the Node representing the target.
	 */
	public Node getTarget()
	{
		return target;
	}

	/**
	 * Access method for the Edge's data (such as weight).
	 * 
	 * @return the Object representing the data.
	 */
	public double getData()
	{
		return data;
	}

	/**
	 * Access method for the Edge's data (such as weight).
	 * 
	 * @param data the new data for the edge.
	 */
	public void setData(Double data)
	{
		this.data = data;
	}

	/**
	 * Access method for the Edge's undirectedness parameter.
	 * 
	 * @return the boolean indicating if this edge is undirected.
	 */
	public boolean isUndirected()
	{
		return undirected;
	}

	/**
	 * Access method for the Edge's undirectedness parameter.
	 * 
	 * @param undirected a boolean indicating if this edge should be undirected.
	 */
	public void setUndirected(boolean undirected)
	{
		this.undirected = undirected;
	}

	/**
	 * Determines whether the given Node is involved in the Edge.
	 * @param n the Node to verify is in the Edge
	 * @return whether the given Node is involved in the Edge
	 */
	public boolean includes(Node n)
	{
		return n.equals(this.getSource()) || (this.isUndirected() && n.equals(this.getTarget()));
	}

	/**
	 * Attempts to duplicate an Edge based on the supplied parameters. The data
	 * attached to the edge REMAINS THE SAME OBJECT as the original graph. As such,
	 * non-immutable objects will reflect changes across both edges. The supplied
	 * Node objects must be equivalent to the old ones.
	 *
	 * @param source      the new Source node reference to utilize.
	 * @param destination the new Destination node reference to utilize.
	 * @return a duplicate Edge decoupled from the original Graph.
	 */
	public Edge duplicate(Node source, Node destination)
	{
		if (!source.equals(this.getSource()) || !destination.equals(this.getTarget()))
		{
			throw new IllegalArgumentException(
					"The new source and " + "destination nodes must be equivalent to the old " + "ones!");
		}
		return new Edge(source, destination, data, undirected);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Edge)
		{
			Edge edge = (Edge) o;
			boolean equivalent = (edge.getSource().equals(getSource()) && edge.getTarget().equals(getTarget())
					|| (edge.getSource().equals(getTarget()) && edge.getTarget().equals(getSource())
							&& edge.isUndirected()));
			return equivalent;
		}
		else
		{
			return false;
		}

	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 53 * hash + Objects.hashCode(this.source);
		hash = 53 * hash + Objects.hashCode(this.target);
		return hash;
	}

	@Override
	public String toString()
	{
		return String.format("[%s, %s, %f]:%s", source, target, data, undirected ? "U" : "D");
	}

}
