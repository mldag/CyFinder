package edu.claflin.finder.algo.shortestpath;

import static edu.claflin.finder.Global.getLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.claflin.finder.algo.Algorithm;
import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;
import edu.claflin.finder.logic.comp.EdgeWeightComparator;
import edu.claflin.finder.struct.PrioritySet;

public class ShortestPath extends Algorithm {


    /**
     * Public Constructor for creating Shortest Path Algorithm.
     *
     * @param bundle the ArgumentsBundle containing the arguments for the
     *             Algorithm object.
     */
    public ShortestPath(ArgumentsBundle bundle) {
        super(bundle);
        if (getLogger() != null)
        {
            getLogger().logInfo(LogLevel.DEBUG, "Shortest Path search algorithm instantiated.");
        }
    }

    @Override
    public ArrayList<Graph> process(Graph graph) {
        //return new ArrayList<Graph>(Arrays.asList(graph));

        Set<String> visited = new HashSet<String>();

        Node   from = graph.getNode(this.args.getObject("fromNode").toString());
        Node   to = graph.getNode(this.args.getObject("toNode").toString());

        Graph subgraph = new Graph("Shortest Path from " + from.getIdentifier() + " to " + to.getIdentifier());

        if (from == null || to == null){
            if (getLogger() != null)
                getLogger().logAlgo(LogLevel.VERBOSE, "ShortestPath ERROR: Origin node and/or destination node not found in graph.");
            // returns an empty array
             return new ArrayList<Graph>();
        }

        // Return if origin Node is equals to destination Node.
        if (from.getIdentifier().equals(to.getIdentifier())){
            subgraph.addNode(from);
            return new ArrayList<Graph>(Arrays.asList(subgraph));
        }

        // Create a min heap to store all neighbors of the current Node.
        PrioritySet<Edge> minHeap = new PrioritySet<>(new EdgeWeightComparator(false),false);

        // Add origin Node ID to the visited list.
        visited.add(from.getIdentifier());

        if (getLogger() != null)
        {
            getLogger().logAlgo(LogLevel.VERBOSE, "ShortestPath: Setting Node as root: " + from.toString());
        }
        // Add origin Node to the new subgraph.
        subgraph.addNode(from);

        double accumulatedEdgeWeight = 0;

        boolean found = false;

        do{
            // Get all the current node neighbors
            List<Node> neighbors = graph.getAdjacencyList(from);
            // Add all new neighbors to the minHeap with the accumulated weight.
            for(Node n :neighbors){
                Edge e = graph.getEdge(from, n);
                // Update Edge weight with the accumulated weight before adding it to the heap.
                e.setData(e.getData() + accumulatedEdgeWeight);
                // Add the new Edge to the min Heap.
                minHeap.add(e);
            }
            Edge closestEdge = null;
            Node closestNode = null;

            while(closestNode == null && !minHeap.isEmpty())
            {
                // Get the minimum weight edge and remove it from the heap.
                closestEdge = minHeap.poll();
                // Get closest neighbor and check if it's not visited.
                try{
                    closestNode = closestEdge.getTarget();
                }catch (NullPointerException error)
                {
                    closestNode = null;
                    if (getLogger() != null)
                        getLogger().logAlgo(LogLevel.VERBOSE, "ShortestPath: ERROR:No destination node found for current edge.");
                }
                if (visited.contains(closestNode.getIdentifier()))
                    closestNode = null;
            }
                if(closestEdge != null && closestNode != null) {
                    accumulatedEdgeWeight = closestEdge.getData();
                    subgraph.addNode(closestNode);
                    // Add closest neighbor to visited list and Edge to subgraph
                    visited.add(closestNode.getIdentifier());
                    subgraph.addEdge(closestEdge);
                    // Update origin node for next iteration
                    from = closestNode;
                    // If closest node is destination node stop loop
                    if (from.getIdentifier().equals(to.getIdentifier())) {
                        found = true;
                        break;
                    }
                }

        }while(!minHeap.isEmpty() && from != null);

        // FIXME - Remove visited paths that not reach the destination node.
        subgraph = removePaths(subgraph, from,to);

        if (found) {
            if (getLogger() != null)
                getLogger().logAlgo(LogLevel.VERBOSE, "ShortestPath: Shortest path found");
            return new ArrayList<Graph>(Arrays.asList(subgraph));
        }
        else{
            if (getLogger() != null)
                getLogger().logAlgo(LogLevel.VERBOSE, "ShortestPath: ERROR:Shortest path not found");
            // returns an empty array
            return new ArrayList<Graph>();
        }
    }

    /**
     * Private class for removing visited paths that not reach the destination node.
     *
     * @param graph the graph containing the shortest path with extra paths
     *             Algorithm object.
     * @param from starting Node
     * @param to ending Node
     * 
     * @return the Graph without fake visited paths
     */
    private Graph removePaths(Graph graph, Node from, Node to){
        return graph;
    }

}
