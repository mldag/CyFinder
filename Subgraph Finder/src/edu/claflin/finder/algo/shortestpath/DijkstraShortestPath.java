package edu.claflin.finder.algo.shortestpath;

import static edu.claflin.finder.Global.getLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.claflin.finder.algo.Algorithm;
import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;



public class DijkstraShortestPath extends Algorithm {

    /**
     * Public Constructor for creating an Algorithm.
     *
     * @param bundle the ArgumentsBundle containing the arguments for the
     *             Algorithm object.
     */
    public DijkstraShortestPath(ArgumentsBundle bundle) {
        super(bundle);
        if (getLogger() != null)
        {
            getLogger().logInfo(LogLevel.DEBUG, "Shortest Path search algorithm instantiated.");
        }
    }
    
    @Override
	public String toString() 
	{
		return "Dijkstra Algorithm";
	}

    @Override
    public ArrayList<Graph> process(Graph graph) {
        //return new ArrayList<Graph>(Arrays.asList(graph));
    	
        Set<String> visited = new HashSet<String>();
        Node   from = graph.getNode(this.args.getObject("fromNode").toString());
        Node   to = graph.getNode(this.args.getObject("toNode").toString());
        Edge   currentEdge;
        Graph subgraph = new Graph("");
        ArrayList<Graph> results = new ArrayList<>();
        Node parent;
        
        boolean undirected = false;
        
        List<Node> nodes; // where subgraph node list will be stored
		List<Edge> edges = graph.getEdgeList(); //graph edges

		

		for (Edge e : graph.getEdgeList())
		{
			
			if (e.isUndirected())
			{
				undirected = true;
				
			}
			
		}
		
		
        // Return if origin Node is equals to destination Node.
        if (from.getIdentifier().equals(to.getIdentifier())){
            subgraph.addNode(from);
            return new ArrayList<Graph>(Arrays.asList(subgraph));
        }

       
        HashMap<Node, Node> changedAt = new HashMap<>();
        changedAt.put(from, null);

        // Keeps track of the shortest path we've found so far for every node
        HashMap<Node, Double> shortestPathMap = new HashMap<>();

        // Setting every node's shortest path weight to positive infinity to start
        // except the starting node, whose shortest path weight is 0
        for (Node node : graph.getNodeList()) {
            if (node == from)
                shortestPathMap.put(from, 0.0);
            else shortestPathMap.put(node, Double.POSITIVE_INFINITY);
        }

        // go through all neighbors
        List<Node> neighbors = graph.getAdjacencyList(from);
        for (Node node : neighbors) {
        	currentEdge = graph.getEdge(from, node);
            shortestPathMap.put(node,currentEdge.getData() );
            changedAt.put(node, from);
        }
        
        visited.add(from.getIdentifier());
       
        while (true) {
            Node currentNode = closestReachableUnvisited(shortestPathMap,graph,visited);
            // If we haven't reached the to node yet, and there isn't another
            // reachable node the path between start and to doesn't exist
            // (they aren't connected)
            if (currentNode == null) {
            	if (getLogger() != null)
                    getLogger().logAlgo(LogLevel.VERBOSE, "ShortestPath: Shortest path not found, there is not path");
            
                return results;
            }

           
            if (currentNode == to) {
                Node child = to;

        
                parent = changedAt.get(child);
                
                while (parent != null) {
                    parent = changedAt.get(child);
                    

                    // Since our changedAt map keeps track of child -> parent relations
                   
                    
                    subgraph.addNode(child);
                    
                    
                    
                    
                    
                    child = parent;
                }
                
                nodes = subgraph.getNodeList();
                
                for(int i =1;i<subgraph.getNodeCount();i++)
                {
                	for(Edge e: edges) 
                    {
                		if( e.getSource() == nodes.get(i) && e.getTarget() == currentNode )
                			subgraph.addEdge(e);
                		if(undirected && e.getSource() == currentNode && e.getTarget() == nodes.get(i))
                			subgraph.addEdge(e);
                    }
                	currentNode = nodes.get(i);
                }
              
                
                
                if (getLogger() != null)
                        getLogger().logAlgo(LogLevel.VERBOSE, "ShortestPath: Shortest path found");
                
                if(!subgraph.getEdgeList().isEmpty() && !subgraph.getNodeList().isEmpty()) 
                {
                	graph = new Graph("Shortest Path from " + from.getIdentifier() + " to " + to.getIdentifier() +" W(T) = " 
                			+ getWeight(subgraph.getEdgeList()), subgraph.getNodeList(), subgraph.getEdgeList());                	
                	results.add(graph);
                	
                }
                
                return results;
            }
            
            visited.add(currentNode.getIdentifier());

            // Now we go through all the unvisited nodes our current node has an edge to
            // and check whether its shortest path value is better when going through our
            // current node than whatever we had before
            
            
            for (Node node : graph.getAdjacencyList(currentNode)) {
            	currentEdge = graph.getEdge(currentNode,node );
                if (visited.contains(node.getIdentifier()))
                    continue;
                
                if (shortestPathMap.get(currentNode)
                   + currentEdge.getData()
                   < shortestPathMap.get(node)) {
                    shortestPathMap.put(node,
                                       shortestPathMap.get(currentNode) + currentEdge.getData());
                    changedAt.put(node, currentNode);
                }
            }
        }
        
    }
    private Node closestReachableUnvisited(HashMap<Node, Double> shortestPathMap,Graph graph,Set<String> visited) {

        double shortestDistance = Double.POSITIVE_INFINITY;
        Node closestReachableNode = null;
        for (Node node : graph.getNodeList()) {
            if (visited.contains(node.getIdentifier()))
                continue;

            double currentDistance = shortestPathMap.get(node);
            if (currentDistance == Double.POSITIVE_INFINITY)
                continue;

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                closestReachableNode = node;
            }
        }
        return closestReachableNode;
    }
    
    private double getWeight(List<Edge> E)
    {
    	double total = 0;
    	for (Edge e : E)
    	{
    		try
    		{
    			double data = e.getData();
    			total += data;
    		}
    		catch (NullPointerException exception)
    		{
    			// return -1.0;
    		}
    		catch (ClassCastException exception)
    		{
    			// return -1.0;
    		}
    	}
    	return total;

    }
}
