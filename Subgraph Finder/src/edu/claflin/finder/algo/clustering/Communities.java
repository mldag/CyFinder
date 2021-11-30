/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.claflin.finder.algo.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;

/**
 *
 * @author Cesar Martin
 */
public class Communities
{
	private HashMap<Integer, List<Node>> list;
	private Graph g;
	private boolean weighted;
	private double m2; // number of 1s in adjacency matrix

	public Communities(List<Node> nodes, Graph g, boolean weighted)
	{
		this.list = new HashMap<>();
		this.g = g;
		this.weighted = weighted;

		Collections.sort(nodes);

		for (int index = 0; index < nodes.size(); index++)
		{
			List<Node> emptyList = new ArrayList<>();
			emptyList.add(nodes.get(index));
			list.put(index, emptyList);
		}

		if (weighted)
		{
			m2 = 2 * this.g.getGraphWeight();
		}
		else
		{
			m2 = 2 * this.g.getEdgeCount();
		}
	}
	
	public Communities(HashMap<Integer, List<Node>> list, Graph g, boolean weighted) 
	{
		this.list = list;
		this.g = g;
		this.weighted = weighted;
		
		if (weighted)
		{
			m2 = 2 * this.g.getGraphWeight();
		}
		else
		{
			m2 = 2 * this.g.getEdgeCount();
		}
	}

	private Communities(HashMap<Integer, List<Node>> list, Graph g, boolean weighted, double m2)
	{
		this.list = list;
		this.g = g;
		this.weighted = weighted;
		this.m2 = m2;
	}

	public Communities copy()
	{
		HashMap<Integer, List<Node>> l = new HashMap<>();

		for (Integer i : this.list.keySet())
		{
			List<Node> c = this.list.get(i);
			List<Node> nl = new ArrayList<>(c);
			l.put(i, nl);
		}

		return new Communities(l, g, weighted, m2);
	}

	public int size()
	{
		return list.size();
	}

	public boolean isWeighted()
	{
		return weighted;
	}

	public double getM2()
	{
		return m2;
	}

	public List<Node> get(Integer key)
	{
		return list.get(key);
	}

	public List<Integer> keys()
	{
		return new ArrayList<>(list.keySet());
	}

	public boolean containsKey(Integer key)
	{
		return list.containsKey(key);
	}

	public List<List<Node>> getList()
	{
		List<List<Node>> copy = new ArrayList<>();

		for (Integer i : list.keySet())
		{
			copy.add(this.get(i));
		}
		return copy;
	}

	public int indexOfNode(Node n)
	{
		for (Integer i : this.list.keySet())
		{
			List<Node> community = this.get(i);
			if (community.contains(n))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Determine if 2 Nodes are in the same Community.
	 *
	 * @param n1 NodeStats 1
	 * @param n2 NodeStats 2
	 *
	 * @return 1 if Nodes in same Community, 0 otherwise
	 */
	public int NodesInSameCommunity(Node n1, Node n2)
	{
		for (Integer i : this.list.keySet())
		{
			List<Node> community = this.get(i);
			if (community.contains(n1) && community.contains(n2))
			{
				return 1;
			}
		}

		return 0;
	}

	public boolean connectedCommunities(List<Node> c1, List<Node> c2)
	{
		HashSet<Node> adjacent1 = new HashSet<>(); // stores all Nodes that can be reached from c1

		for (Node n : c1)
		{
			adjacent1.addAll(n.getNeighbors());
		}

		// remove destinations of c1 that are not members of c2
		adjacent1.retainAll(c2);
		
		return !adjacent1.isEmpty(); // any destinations of c1 were members of c2?
	}

	public double a(List<Node> c)
	{
		if (m2 <= 0) 
		{
			return Double.NEGATIVE_INFINITY;
		}
		
		double sum = 0.0;

		for (Node v : c)
		{
			sum += weighted ? v.getWeight() : v.getDegree();
		}

		return sum / m2;
	}

	public double e(List<Node> c1, List<Node> c2)
	{
		if (m2 <= 0) 
		{
			return Double.NEGATIVE_INFINITY;
		}
		
		double sum = 0.0;

		Set<Edge> edge_collection = new HashSet<>();
		c1.stream().forEach(n ->
		{
			edge_collection.addAll(n.getEdges());
		});
		c2.stream().forEach(n ->
		{
			edge_collection.addAll(n.getEdges());
		});
		Set<Edge> edges = edge_collection.stream().filter(e ->
		{
			Node source = e.getSource();
			Node target = e.getTarget();

			return (c1.contains(source) && c2.contains(target)) || (c1.contains(target) && c2.contains(source));
		}).collect(Collectors.toSet());		
		
		for (Edge e : edges)
		{
			// count twice when the communities are the same
			sum += (weighted ? e.getData() : 1) * (c1 == c2 ? 2 : 1);
		}

		return sum / m2;
	}

	public double modularity()
	{
		double sum = 0.0;

		for (Integer i : this.list.keySet())
		{
			List<Node> community = list.get(i);
			if (!community.isEmpty())
			{
				sum += e(community, community) - Math.pow(a(community), 2);
			}
		}

		return sum;
	}

	/**
	 * Merge community j and i into j
	 *
	 * @param j the community to store the result
	 * @param i the community the offers its Nodes
	 */
	public void mergeCommunities(int j, int i)
	{
		if (this.list.containsKey(j) && this.list.containsKey(i))
		{
			this.list.get(j).addAll(this.list.get(i));
			this.list.remove(i);
		}

	}

	public String toStringSorted()
	{
		String result = "";

		List<String> coms = new ArrayList<>();

		for (Integer i : list.keySet())
		{
			Collections.sort(list.get(i));
			coms.add(list.get(i).toString());
		}

		Collections.sort(coms);

		for (int i = 0; i < coms.size(); i++)
		{
			String s = coms.get(i);
			result += "[" + i + "] " + s + "\n";
		}
		return result + "Modularity: " + this.modularity();
	}

	@Override
	public String toString()
	{
		String result = "";

		for (Integer i : this.list.keySet())
		{
			List<Node> community = new ArrayList<>(this.get(i));
			Collections.sort(community);
			if (!community.isEmpty())
			{
				result += i + ": " + community + "\n";
			}
		}
		return result + "Modularity: " + this.modularity();
	}

}
