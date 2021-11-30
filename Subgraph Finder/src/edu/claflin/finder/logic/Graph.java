package edu.claflin.finder.logic;

import static edu.claflin.finder.Global.getLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.claflin.finder.log.LogLevel;

/**
 * Used to represent a graph in memory. Migrated from matrix form, the graph is
 * now kept track of via a list of edges and nodes. All the previous methods
 * remain in existence but the underlying implementation has changed.
 *
 * @author Charles Allen Schultz II
 * @version 3.4 February 2, 2016
 */
public class Graph
{
	/**
	 * The name of the Graph provided by the user. This value is used by the program
	 * to name output files unless specified otherwise.
	 */
	private final String graphName;

	/**
	 * The list of the nodes added to the graph. Used by the program to keep track
	 * of the associations within the matrix. It may be re-ordered dynamically at
	 * runtime.
	 */
	private final ArrayList<Node> nodeList;

	/**
	 * The list of edges added to the graph. The graph is, by default, a directed
	 * graph. As such, two edges must be added to force the graph to be
	 * bidirectional.
	 */
	private final ArrayList<Edge> edgeList;

	/*
	 * The total weight of the graph Graph 
	 */
	private double weight;

	/**
	 * Used to suppress logging.
	 */
	protected boolean suppressLog = false;

	/**
	 * Initializes the graph. Merely initializes fields. It does not set any data.
	 * Also sets the Logging Utility object. If null, the class will not log data.
	 *
	 * @param graphName the String which represents the name of the Graph.
	 */
	public Graph(String graphName)
	{
		this.graphName = graphName;
		this.nodeList = new ArrayList<>();
		this.edgeList = new ArrayList<>();
	}

	/**
	 * Private constructor used by subGraph method.
	 *
	 * @param graphName the String which represents the name of the Graph.
	 * @param nodeList  the ArrayList of Node objects.
	 * @param edgeList  the ArrayList of Edge objects.
	 */
	public Graph(String graphName, List<Node> nodeList, List<Edge> edgeList)
	{
		this(graphName);
		this.addPartialGraph(nodeList, edgeList);
	}

	// ================================================================================
	// Graph Section

	/**
	 * Gets the name of the graph.
	 *
	 * @return The graph name.
	 */
	public String getName()
	{
		return graphName;
	}

	/**
	 * Adds a partial graph to this graph. Specifically adds a set of nodes and a
	 * set of edges to the graph. It should be noted that nodes or edges in the
	 * graph already that attempt to be added again may cause exceptions to occur.
	 * Additionally, Nodes are added before Edges so any Edges that depend on Nodes
	 * in the add list may be safely added simultaneously. This method is the
	 * backbone structure for adding any component to the graph as all other
	 * addition methods filter through here.
	 *
	 * @param nodes the List&lt;Node&gt; containing the nodes to add.
	 * @param edges the List&lt;Edge&gt; containing the edges to add.
	 * @return true, always. Allows overriding in subclasses which wish to change
	 *         base implementation.
	 */
	public boolean addPartialGraph(List<Node> nodes, List<Edge> edges)
	{
		if (nodes != null)
			nodes.stream().forEach(node ->
			{
				checkNode(node);
				nodeList.add(node.duplicate());
				if (!suppressLog && getLogger() != null)
				{
					getLogger().logGraph(LogLevel.VERBOSE, getName() + ": Added Node: \"" + node + "\"");
				}
			});

		if (edges != null)
			edges.stream().forEach(edge ->
			{
				if (nodeList.containsAll(Arrays.asList(edge.getSource(), edge.getTarget())) && !edgeList.contains(edge))
				{
					Node s = this.getNode(edge.getSource().getIdentifier());
					Node t = this.getNode(edge.getTarget().getIdentifier());
					Edge e = edge.duplicate(s, t); // we have to store the edge that has the node with the stats
					s.addEdge(e); // update stats of node s
					t.addEdge(e); // update stats of node t
					edgeList.add(e); // add edge that has nodes with the stats to the list					
					weight += edge.getData(); // update graph weight

					if (!suppressLog && getLogger() != null)
					{
						getLogger().logGraph(LogLevel.VERBOSE, getName() + ": Added Edge: " + edge);
					}
				}
			});

		return true;
	}

	/**
	 * Returns a subGraph of this graph. Isolates and extracts a subgraph based on
	 * user supplied values. The new Graph is a separate Graph object.
	 *
	 * @param startNode     the integer pointing to the start node.
	 * @param stopNode      the integer pointing to the stop node.
	 * @param nameQualifier the String representing an additional qualifier to
	 *                      prepend the name of the new Graph object with.
	 * @deprecated Outdated since v3. Replaced by
	 *             {@link #getSubGraph(java.util.List, java.lang.String)}
	 * @return the Graph object representing a subgraph of the original.
	 */
	@Deprecated
	public Graph getSubGraph(int startNode, int stopNode, String nameQualifier)
	{
		List<Node> subList = nodeList.subList(startNode, stopNode);

		String name = nameQualifier + "S[" + startNode + "," + stopNode + "]" + "-";

		return getSubGraph(subList, name);
	}

	/**
	 * Returns a subGraph of this graph. Isolates and extracts a subgraph based on
	 * user supplied values. The new Graph is a separate Graph object.
	 *
	 * @param subStringSet  an ArrayList containing the String names of the desired
	 *                      nodes.
	 * @param nameQualifier the String representing an additional qualifier to
	 *                      prepend the name of the new Graph object with.
	 * @deprecated Outdated since v3. Replaced by
	 *             {@link #getSubGraph(java.util.List, java.lang.String)}
	 * @return the Graph object representing a subgraph of the original.
	 */
	@Deprecated
	public Graph getSubGraph(ArrayList<String> subStringSet, String nameQualifier)
	{

		List<Node> mainList = getNodeList();
		List<Node> subList = new ArrayList<>();
		subStringSet.stream().forEach(nodeString -> mainList.stream()
				.filter(node -> node.getIdentifier().equals(nodeString)).forEach(node -> subList.add(node))); // Should
		// only
		// add
		// one.

		return getSubGraph(subList, nameQualifier);
	}

	/**
	 * Returns a subGraph of this graph. Isolates and extracts a subgraph based on a
	 * user supplied node list. The new Graph is a quasi-separate Graph. Each node
	 * utilized is directly related between the two graphs and, as such, changes to
	 * the properties of one node in the subgraph will be reflected in the super
	 * graph. To obtain an independent graph
	 *
	 *
	 * @param nodes         the List&lt;Node&gt; containing the Node references.
	 * @param nameQualifier the String to differentiate the Graph names.
	 * @return the Graph representing the subgraph.
	 */
	public Graph getSubGraph(List<Node> nodes, String nameQualifier)
	{
		ArrayList<Node> nodeSubList = new ArrayList<>();
		ArrayList<Edge> edgeSubList = new ArrayList<>();
		nodeSubList.addAll(nodes);

		nodes.stream().forEach((Node node) ->
		{
			List<Node> adjacency = node.getNeighbors();
			adjacency.retainAll(nodes);
			adjacency.stream().forEach((Node dest) ->
			{
				edgeSubList.add(getEdge(node, dest));
			});
		});

		String name = nameQualifier + graphName;

		return new Graph(name, nodeSubList, edgeSubList);
	}

	/**
	 * Transposes the specified nodes. Swaps the location of the nodes in memory.
	 * Specifically swaps the nodes in the nodeList as the Edge data has been
	 * uncoupled from the Node data. Newer implementation is dependent upon the int
	 * based method for performance.
	 *
	 * @param node1Index the integer index of the first node.
	 * @param node2Index the integer index of the second node.
	 */
	public void transpose(int node1Index, int node2Index)
	{
		checkNodeIndex(node1Index);
		checkNodeIndex(node2Index);

		Node a = nodeList.get(node1Index);
		Node b = nodeList.get(node2Index);
		nodeList.set(node2Index, a);
		nodeList.set(node1Index, b);

		if (!suppressLog && getLogger() != null)
		{
			getLogger().logGraph(LogLevel.VERBOSE, getName() + ": Transposed Nodes \"" + nodeList.get(node1Index)
					+ "\" & \"" + nodeList.get(node2Index) + "\".");
		}
	}

	/**
	 * Transposes the specified nodes. Swaps the location of the nodes in memory.
	 * This is a convenience method for transposing the nodes based on the Sting
	 * representation of the nodes.
	 *
	 * @param node1 the String name of the first node.
	 * @param node2 the String name of the second node.
	 * @deprecated Outdated since v3. Replaced by {}
	 */
	@Deprecated
	public void transpose(String node1, String node2)
	{
		int node1Index = getNodeIndex(node1);
		int node2Index = getNodeIndex(node2);

		transpose(node1Index, node2Index);
	}

	/**
	 * Transposes the specified nodes. Relies on the integer based method for
	 * carrying out the operation.
	 *
	 * @param node1 the Node object in the first position.
	 * @param node2 the Node object in the second position.
	 */
	public void transpose(Node node1, Node node2)
	{
		transpose(nodeList.indexOf(node1), nodeList.indexOf(node2));
	}

	/**
	 * Get the Graph's total weight (sum of all edge weights)
	 * 
	 * @return the Graph's total weight
	 */
	public double getGraphWeight()
	{
		return weight;
		//return this.getEdgeList().stream().mapToDouble((Edge e) -> e.getData()).sum();
	}

	/**
	 * Copies this Graph object using the getSubGraph() method.
	 *
	 * @return a Graph object identical to the original.
	 */
	public Graph copy()
	{
		return getSubGraph(nodeList, "");
	}

	/**
	 * Copies this Graph producing a unique copy in which there is no entanglement
	 * between the two Graph objects. I.e. there are no shared nodes or edges
	 * between the two graphs. Mathematically speaking:
	 * <code>x.getNodeList().get(0) != y.getNodeList().get(0)</code> under any
	 * circumstances. The Nodes and Edges may remain equivalent via the equals()
	 * implementation. Further, Edge data shall remain entangled due to
	 * implementation details.
	 *
	 * @return a unique copy of the graph object.
	 */
	public Graph uniqueCopy()
	{
		return uniqueCopy(graphName);
	}

	public Graph uniqueCopy(String gName)
	{
		ArrayList<Node> nodeList = new ArrayList<>();
		ArrayList<Edge> edgeList = new ArrayList<>();

		this.nodeList.stream().forEach(node ->
		{
			nodeList.add(node.duplicate());
		});
		this.edgeList.stream()
				/*
				 * This is gonna look crazy, but network components may be equivalent even if
				 * they are not the same object in memory. This means we can obtain the new Node
				 * from the new list by utilizing its equivalency with the old Node in the old
				 * list.
				 */
				.forEach(edge ->
				{
					Node newSource = nodeList.get(nodeList.indexOf(edge.getSource()));
					Node newDest = nodeList.get(nodeList.indexOf(edge.getTarget()));
					edgeList.add(edge.duplicate(newSource, newDest));
				});

		return new Graph(gName, nodeList, edgeList);
	}

	/**
	 * Union this Graph with a provided Graph.
	 *
	 * @param that the Graph to union with
	 * @return the Graph that is the union of the two Graphs
	 */
	public Graph union(Graph that)
	{
		Set<Node> nList1 = new HashSet<>(this.getNodeList());
		Set<Node> nList2 = new HashSet<>(that.getNodeList());
		Set<Edge> eList1 = new HashSet<>(this.getEdgeList());
		Set<Edge> eList2 = new HashSet<>(that.getEdgeList());

		eList1.addAll(eList2);
		nList1.addAll(nList2);

		return new Graph(this.getName() + " union " + that.getName(), new ArrayList<>(nList1), new ArrayList<>(eList1));
	}

	/**
	 * Intersects this Graph with a provided Graph.
	 *
	 * @param that the Graph to intersect with
	 * @return the Graph that is the intersection of the two Graphs
	 */
	public Graph intersect(Graph that)
	{
		Set<Node> nList1 = new HashSet<>(this.getNodeList());
		Set<Node> nList2 = new HashSet<>(that.getNodeList());
		Set<Edge> eList1 = new HashSet<>(this.getEdgeList());
		Set<Edge> eList2 = new HashSet<>(that.getEdgeList());

		eList1.retainAll(eList2);
		nList1.retainAll(nList2);

		eList1 = eList1.stream().filter(e -> nList1.contains(e.getSource()) && nList1.contains(e.getTarget()))
				.collect(Collectors.toSet());

		return new Graph(this.getName() + " intersection " + that.getName(), new ArrayList<>(nList1),
				new ArrayList<>(eList1));
	}

	/**
	 * Difference of this Graph with a provided Graph.
	 *
	 * @param that the Graph to difference with
	 * @return the Graph that is the difference of the two Graphs
	 */
	public Graph difference(Graph that)
	{
		Set<Node> nList1 = new HashSet<>(this.getNodeList());
		Set<Node> nList2 = new HashSet<>(that.getNodeList());
		Set<Edge> eList1 = new HashSet<>(this.getEdgeList());
		Set<Edge> eList2 = new HashSet<>(that.getEdgeList());

		eList1.removeAll(eList2);
		nList1.removeAll(nList2);

		eList1 = eList1.stream().filter(e -> nList1.contains(e.getSource()) && nList1.contains(e.getTarget()))
				.collect(Collectors.toSet());

		return new Graph(this.getName() + " difference " + that.getName(), new ArrayList<>(nList1),
				new ArrayList<>(eList1));
	}

	/**
	 * Returns a String representation of the Graph.
	 *
	 * @return the String representation of the Graph.
	 */
	@Override
	public String toString()
	{
		List<Node> V = this.getNodeList();
		List<Edge> E = this.getEdgeList();
		Collections.sort(V);
		Collections.sort(E, (a, b) -> a.toString().compareTo(b.toString()));

		return graphName + "\n" + "|V|: " + V.size() + " |E|: " + E.size() + "\nV: " + V + "\nE: " + E;
		//return graphName + "\n" + "|V|: " + V.size() + " |E|: " + E.size() + "\nV: " + V;
	}

	// ================================================================================
	// Node Section

	/**
	 * Gets the number of nodes in the graph.
	 *
	 * @return the integer representing the total number of nodes in the graph.
	 */
	public int getNodeCount()
	{
		return nodeList.size();
	}

	/**
	 * Returns the list of nodes in the graph.
	 *
	 * @return the List&lt;Node&gt; containing the list of nodes in the graph.
	 */
	public List<Node> getNodeList()
	{
		return new ArrayList<>(nodeList);
	}

	/**
	 * Checks if the graph contains the given node.
	 *
	 * @param node the node to check for
	 * @return whether or not node is in this graph
	 */
	public boolean containsNode(Node node)
	{
		return nodeList.contains(node);
	}

	/**
	 * Adds a node to the graph. Adds the specified node name to the nodeList by
	 * creating and adding a new node and then calling the addNode(Node) method.
	 *
	 * @param node the String which denotes the name of the node to add.
	 * @deprecated Outdated since v3. Replaced by {}
	 * @return true if the addition was successful.
	 */
	@Deprecated
	public boolean addNewNode(String node)
	{
		return addNode(new Node(node));
	}

	/**
	 * Adds a node to the graph. Adds a Node object to the graph.
	 *
	 * @param node the Node object to add.
	 * @return true if the addition was successful.
	 */
	public boolean addNode(Node node)
	{
		return addNodes(Arrays.asList(node));
	}

	/**
	 * Removes a node from the set. Only used with the Bron-Kerbosch algorithm right
	 * now, hopefully doesn't break anything Does NOT affect edges!!!!!!! At time of
	 * writing may cause issues if you rely on it to do so
	 *
	 * @param node the node to remove
	 */
	public void removeNode(Node node)
	{
		removeEdgesInvolving(node); // remove the Edges involving the Node
		nodeList.remove(node);
	}

	/**
	 * Adds a list of nodes to the graph.
	 *
	 * @param nodes the List&lt;Node&gt; containing the nodes to add.
	 * @return true if the addition was successful.
	 */
	public boolean addNodes(List<Node> nodes)
	{
		return addPartialGraph(nodes, null);
	}

	/**
	 * Gets the index of a node based on its name. Maintained with the new
	 * implementation to preserve existing uses. Will not match nodes which may have
	 * additional properties attached in future implementations.
	 *
	 * @param node the String representing the node to search for.
	 * @deprecated Outdated since v3. Replaced by {}
	 * @return the index of the node or -1 if the node was not found.
	 */
	@Deprecated
	public int getNodeIndex(String node)
	{
		return getNodeIndex(new Node(node));
	}

	/**
	 * Gets the index of a Node object in memory. Retained for older API
	 * compatibility. The ordering of the nodes is not guaranteed and may change
	 * during the program's runtime.
	 *
	 * @param node the Node object to search for.
	 * @return the index of the node or -1 if the node was not found.
	 */
	public int getNodeIndex(Node node)
	{
		return nodeList.indexOf(node);
	}

	/**
	 * Gets the name of the node at the specified nodeIndex. Ensures that the
	 * provided integer is within the range of available indices.
	 *
	 * @param nodeIndex the integer representing the index to get from the
	 *                  headerList.
	 * @deprecated Outdated since v3. No replacement. Obtain the Node data via the
	 *             {@link Node} object.
	 * @return the String representing the name of the node.
	 */
	@Deprecated
	public String getNodeName(int nodeIndex)
	{
		checkNodeIndex(nodeIndex);
		return nodeList.get(nodeIndex).getIdentifier();
	}

	/**
	 * Gets a node based on its name.
	 *
	 * @param NodeName the String representing the node to search for.
	 * @return the node or null if the node was not found.
	 */
	public Node getNode(String NodeName)
	{
		Node node = null;
		for (Node n : nodeList)
		{
			if (n.getIdentifier().equalsIgnoreCase(NodeName))
			{
				node = n;
				break;
			}
		}
		return node;
	}

	/**
	 * Obtains an adjacency list for the supplied node based on the Graph. 
	 * Very inefficient, try to use the neighbors variable in the Node class instead.
	 *
	 * @param node the Node to obtain the adjacency list for.
	 * @return the List of Nodes containing the adjacent Nodes.
	 */
	public List<Node> getAdjacencyList(Node node)
	{
		List<Node> adjacent = new ArrayList<>();

		Iterator<Edge> edgeIterator = edgeList.iterator();
		while (edgeIterator.hasNext())
		{
			Edge edge = edgeIterator.next();
			if (edge.getSource().equals(node))
			{
				adjacent.add(edge.getTarget());
			}
			else if (edge.isUndirected() && edge.getTarget().equals(node))
			{
				adjacent.add(edge.getSource());
			}
		}

		return adjacent;
	}

	/**
	 * Verifies that the node is NOT in the graph. Checks to see if the provided
	 * node name is in the headerList and, hence, the dataMatrix. This is an
	 * internal method used to ensure that the algorithm is operating correctly.
	 *
	 * @param node the Node to check for.
	 */
	private void checkNode(Node node)
	{
		if (getNodeIndex(node) != -1)
		{
			String errorString = node + " is already in the graph!";

			if (!suppressLog && getLogger() != null)
			{
				getLogger().logError(LogLevel.NORMAL, getName() + ": " + errorString);
			}

			throw new IllegalArgumentException(errorString);
		}
	}

	/**
	 * Verifies that the nodeIndex is valid. Ensures that the node does not fall out
	 * of bounds. This is an internal method used to ensure that the algorithm is
	 * operating correctly.
	 *
	 * @param nodeIndex the integer of the node to check for.
	 * @deprecated No replacement. Further operations shall utilize {)}
	 */
	@Deprecated
	private void checkNodeIndex(int nodeIndex)
	{
		if (nodeList.size() >= nodeIndex && nodeIndex < 0)
		{
			String errorString = nodeIndex + " is not a valid Header index!";

			if (!suppressLog && getLogger() != null)
			{
				getLogger().logError(LogLevel.NORMAL, getName() + ": " + errorString);
			}

			throw new ArrayIndexOutOfBoundsException(errorString);
		}
	}
	// ================================================================================
	// Edge Section

	/**
	 * Get the number of Edges in the Graph
	 * 
	 * @return the number of Edges in the Graph
	 */
	public int getEdgeCount()
	{
		return edgeList.size();
	}

	/**
	 * Returns the list of edges in the graph.
	 *
	 * @return the List&lt;Edge&gt; containing the list of edges in the graph.
	 */
	public List<Edge> getEdgeList()
	{
		return new ArrayList<>(edgeList);
	}

	/**
	 * Adds an edge connecting the specified nodes in the Graph. Sets the object
	 * data for the edge dependent upon the supplied parameter.
	 *
	 * @param node1Index the integer value indicating the first node.
	 * @param node2Index the integer value indicating the second node.
	 * @param edgeData   the Object that represents unique edge data (i.e. weight or
	 *                   edge relationship).
	 * @deprecated Outdated since v3. Replaced by {}
	 * @return true if the addition was successful.
	 */
	@Deprecated
	public boolean addEdge(int node1Index, int node2Index, Double edgeData)
	{
		checkNodeIndex(node1Index);
		checkNodeIndex(node2Index);

		// Deprecated command ONLY creates directed edges.
		Edge edge = new Edge(nodeList.get(node1Index), nodeList.get(node2Index), edgeData, false);
		return addEdge(edge);
	}

	/**
	 * Adds an edge connecting the specified nodes to the Graph. This is a
	 * convenience method for adding an edge using node names. Graphs are undirected
	 * so the order of the parameters does not matter.
	 *
	 * @param node1    the String indicating the first node.
	 * @param node2    the String indicating the second node.
	 * @param edgeData the Object that represents unique edge data (i.e. weight or
	 *                 edge relationship).
	 * @deprecated Outdated since v3. Replaced by {
	 * @return true if the addition was successful.
	 */
	@Deprecated
	public boolean addEdge(String node1, String node2, Double edgeData)
	{
		int node1Index = getNodeIndex(node1);
		int node2Index = getNodeIndex(node2);

		return addEdge(node1Index, node2Index, edgeData);
	}

	/**
	 * Adds an edge to the Graph. If any edge already exists within the Graph
	 * connecting the two supplied nodes or either of the two nodes is not in the
	 * nodeList, then the edge is not added.
	 *
	 * @param edge the Edge object to add to the graph.
	 * @return true if the addition was successful.
	 */
	public boolean addEdge(Edge edge)
	{
		return addEdges(Arrays.asList(edge));
	}

	/**
	 * Adds a list of Edges to the graph.
	 *
	 * @param edges the List&lt;Edge&gt; containing the edges to add.
	 * @return true if the addition was successful.
	 */
	public boolean addEdges(List<Edge> edges)
	{
		return addPartialGraph(null, edges);
	}

	/**
	 * Gets the value of the edge between the specified nodes.
	 *
	 * @param node1Index the integer value of the first node.
	 * @param node2Index the integer value of the second node.
	 * @deprecated Outdated since v3. No replacement. Obtain the edge first, then
	 *             obtain the data from the Edge object.
	 * @return the Object representing the edge relationship.
	 */
	@Deprecated
	public Object getEdge(int node1Index, int node2Index)
	{
		checkNodeIndex(node1Index);
		checkNodeIndex(node2Index);

		Edge fakeEdge = getEdge(nodeList.get(node1Index), nodeList.get(node2Index));

		return fakeEdge.getData();
	}

	/**
	 * Gets the value of the edge between the specified nodes. This is a convenience
	 * method for getting the edge using the node's String name.
	 *
	 * @param node1 the String of the first node.
	 * @param node2 the String of the second node.
	 * @deprecated Outdated since v3. No replacement. Obtain the edge first, then
	 *             obtain the data from the Edge object.
	 * @return the Object representing the edge relationship.
	 */
	@Deprecated
	public Object getEdge(String node1, String node2)
	{
		int node1Index = getNodeIndex(node1);
		int node2Index = getNodeIndex(node2);

		Edge fakeEdge = getEdge(nodeList.get(node1Index), nodeList.get(node2Index));

		return fakeEdge.getData();
	}

	/**
	 * Locates an Edge object in the edgeList and returns it. Note that it will also
	 * return an edge with the provided source and destination parameters swapped IF
	 * AN ONLY IF said edge is undirected.
	 *
	 * @param source      the source Node of the edge.
	 * @param destination the destination Node of the edge.
	 * @return the Edge object or null if not found.
	 */
	public Edge getEdge(Node source, Node destination)
	{
		// "fake" access is accessing an edge utilizing only the source and
		// destination. the equals method on edges do not check for data nor
		// undirectedness since edges with the same source and destination are
		// considered equal.

		// Locate un/directed edge via "fake" access.
		int index = edgeList.indexOf(new Edge(source, destination, 0.0, false));
		if (index != -1)
			return edgeList.get(index);

		// Locate undirected reversed node edges also via "fake" access.
		index = edgeList.indexOf(new Edge(destination, source, 0.0, false));
		if (index != -1 && edgeList.get(index).isUndirected())
			return edgeList.get(index);

		return null;
	}

	/**
	 * Get the list of Edges in which the given Node participates.
	 * 
	 * @param n the Node to find edges
	 * @return the edges the node participates in
	 */
	public List<Edge> getNodeEdges(Node n)
	{
		return this.getEdgeList().stream().filter((Edge e) -> n.equals(e.getSource()) || n.equals(e.getTarget()))
				.collect(Collectors.toList());
	}

	public void removeEdge(Edge e)
	{
		Node s = e.getSource();
		Node t = e.getTarget();
		s.removeEdge(e);
		t.removeEdge(e);
		weight -= e.getData();
		edgeList.remove(e);
	}

	/**
	 * Remove all the edges in which the given Node participates.
	 *
	 * @param n the participating Node
	 */
	private void removeEdgesInvolving(Node n)
	{
		for (Edge e : n.getEdges())
		{
			this.removeEdge(e);
		}
	}	

	/**
	 * Gets the list edges of this Graph that involve the nodes in the given list
	 *
	 * @param nodes the list of nodes to found the edges from
	 * @return the list edges of this Graph that involve the nodes in the given list
	 */
	public List<Edge> getEdgesBack(List<Node> nodes)
	{
		return this.getEdgeList().stream().filter(e -> nodes.contains(e.getSource()) && nodes.contains(e.getTarget()))
				.collect(Collectors.toList());

	}

	// ================================================================================
	// Clique Utils

	/**
	 * Determines whether the Graph is a Clique.
	 * @return true if the Graph is a Clique
	 */
	public boolean isClique()
	{
		for (Node node : this.getNodeList())
		{
			List<Node> nodesMinusOne = new ArrayList<>();
			nodesMinusOne.addAll(this.getNodeList());
			nodesMinusOne.remove(node);

			List<Node> adjacency = node.getNeighbors();
			boolean containsAll = adjacency.containsAll(nodesMinusOne);
			if (!containsAll)
				return false;
		}

		return true;
	}

	// ================================================================================
	// Bipartite Utils	

	/**
	 * Determines whether the Graph is Bipartite.
	 * @return true if the Graph is Bipartite
	 */
	public boolean isBipartite()
	{
		ArrayList<ArrayList<Node>> ps = getPartiteSets();

		if (ps.isEmpty())
			return false;

		ArrayList<Node> s1 = ps.get(0);
		ArrayList<Node> s2 = ps.get(1);

		return !(s1.isEmpty() || s2.isEmpty());
	}

	/**
	 * Divides the given graph into two bipartite sets.
	 * 
	 * @return two lists of nodes, each representing a bipartite set. Both lists
	 *         are empty if the graph is not a valid bipartite graph.
	 */
	public ArrayList<ArrayList<Node>> getPartiteSets()
	{
		ArrayList<ArrayList<Node>> result = new ArrayList<ArrayList<Node>>();

		Set<Node> groupA = new HashSet<>();
		Set<Node> groupB = new HashSet<>();

		for (Node node : this.getNodeList())
		{
			if (!isBipartite(node, groupA, groupB))
			{
				result.add(new ArrayList<Node>());
				result.add(new ArrayList<Node>());
				// Return empty sets if the graph is not bipartite.
				return result;
			}
		}

		ArrayList<Node> arrA = new ArrayList<Node>();
		for (Node a : groupA)
		{
			arrA.add(a);
		}

		ArrayList<Node> arrB = new ArrayList<Node>();
		for (Node b : groupB)
		{
			arrB.add(b);
		}

		result.add(arrA);
		result.add(arrB);
		return result;
	}

	/**
	 * Private method for stepping through the adjacency lists and determining if
	 * the graph is bipartite. Specifically, it uses a Depth-First model for
	 * exploring the node tree and sorting the nodes into two groups. If it
	 * successfully sorts the nodes into two groups with no invalidating edges, it
	 * returns true.
	 * 
	 * @param addSet        the current set to add a node to based on the previous.
	 * @param compareSet    the current set to add adjacent nodes to.
	 * @param current       the current Node being compared.
	 * @return true if the structure could be bipartite.
	 */
	private boolean isBipartite(Node current, Set<Node> addSet, Set<Node> compareSet)
	{
		if (!addSet.contains(current) && !compareSet.contains(current))
		{
			addSet.add(current);
			List<Node> adjacent = current.getNeighbors();
			for (Node neighbor : adjacent)
			{
				if (addSet.contains(neighbor) || !this.isBipartite(neighbor, compareSet, addSet))
					return false;
			}
		}

		return true;
	}

}
