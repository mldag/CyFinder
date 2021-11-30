/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.claflin.finder.logic;

import static edu.claflin.finder.Global.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.claflin.finder.log.LogLevel;

/**
 * Represents a node in memory. A simple implementation requiring only an
 * identifier.
 * 
 * @author Charles Allen Schultz II
 * @version 1.1.1 June 16, 2015
 */
public class Node implements Comparable<Node>
{
	/**
	 * The string representing the node's unique name.
	 */
	private final String identifier;
	private List<Edge> edges;
	private List<Node> neighbors;
	private int degree;
	private double weight;

	/**
	 * Initializes the Node object.
	 *
	 * @param identifier the String representing the Node's identifier.
	 */
	public Node(String identifier)
	{
		this.identifier = identifier;
		edges = new ArrayList<>();
		neighbors = new ArrayList<>();
		degree = 0;
		weight = 0.0;

		if (getLogger() != null)
		{
			getLogger().logInfo(LogLevel.DEBUG, "Node: Created Node with " + "identifier: " + identifier);
		}
	}

	/**
	 * Access method for the Node's identifier.
	 *
	 * @return the String identifying the Node.
	 */
	public String getIdentifier()
	{
		return identifier;
	}
	
	/**
	 * Get the Node's edges
	 * 
	 * @return the node's edges
	 */
	public List<Edge> getEdges()
	{
		return new ArrayList<Edge>(edges);
	}

	/**
	 * Get the Node's neighbors
	 * 
	 * @return the node's neighbors
	 */
	public List<Node> getNeighbors()
	{
		return new ArrayList<Node>(neighbors);
	}

	/**
	 * Get the Node's in-degree.
	 * 
	 * @return the node's degree
	 */
	public int getDegree()
	{
		return degree;
	}

	/**
	 * Get the Node's weight
	 * 
	 * @return the node's weight
	 */
	public double getWeight()
	{
		return weight;
	}

	/**
	 * Add an Edge to the Node and increase the other statistics accordingly.
	 * 
	 * @param e the Edge to add
	 */
	public void addEdge(Edge e)
	{
		if (e.includes(this) && !edges.contains(e))
		{
			edges.add(e);
			Node s = e.getSource();
			Node t = e.getTarget();

			if (this.equals(s))
			{
				neighbors.add(t);
			}
			else
			{
				neighbors.add(s);
			}

			degree++;
			weight += e.getData();
		}
	}

	public void removeEdge(Edge e)
	{
		if (e.includes(this) && edges.contains(e))
		{
			edges.remove(e);
			Node s = e.getSource();
			Node t = e.getTarget();

			if (this.equals(s))
			{
				neighbors.remove(t);
			}
			else
			{
				neighbors.remove(s);
			}

			degree--;
			weight -= e.getData();
		}
	}

	/**
	 * Returns a copy of this Node that is a new object in memory.
	 * 
	 * @return the copy of the Node
	 */
	public Node duplicate()
	{
		return new Node(identifier);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Node)
		{
			Node node = (Node) o;
			return node.getIdentifier().equals(this.getIdentifier());
		}
		else 
		{
			return false;	
		}
		
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 71 * hash + Objects.hashCode(this.identifier);
		return hash;
	}

	@Override
	public String toString()
	{
		return getIdentifier();
	}
	
	@Override
    public int compareTo ( Node that )
    {
        return this.getIdentifier ().compareTo ( that.getIdentifier () );
    }

}
