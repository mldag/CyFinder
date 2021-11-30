package edu.claflin.finder.logic.cygrouper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.claflin.finder.logic.Edge;

 /* This class was written by Evyatar Saias & Ariel Sari on Nov 19, 2019. */
public class CygrouperAssembler {
    List<CygrouperEdge> edges = new ArrayList<CygrouperEdge>(); // the list that will contain the edge source target pair when reading from csv input
    Map map = new HashMap<String, ArrayList<String>>();


    public CygrouperAssembler(List<Edge> regularEdges){

        //Takes in a list of Edges and converts it into a list of CygrouperEdges
        for(Edge e: regularEdges){
            convertCyfinderEdge(e.getSource().getIdentifier(),e.getTarget().getIdentifier());
        }
        //creates a a string tree from the edge list , basically maps a string node to connection string nodes
        assembleMap(this.edges,this.map);
        //creates nodes out of the string keys in the map, then sets this.map equal to map<String,ArrayList<Node>>
        assmbleNodes(this.edges,this.map);

    }
    public void convertCyfinderEdge(String s, String t){
        this.edges.add(new CygrouperEdge(s,t));
    }

    // takes in a list of edges (source and target) and creates a map with the uniqe sources mapped to all thier connections
    //also ensures that targets are treated as source and vis versa, this way all nodes have connections from both directions
    public void assembleMap(List<CygrouperEdge> edgeList, Map map){

        for(int i = 0; i < edgeList.size(); i++){
            CygrouperEdge currentEdge = edgeList.get(i);
            if(!map.containsKey(currentEdge.source)){
                ArrayList<String> connections = new ArrayList<String>();
                connections.add(currentEdge.target);
                map.put(currentEdge.source,connections);
            }
            else{
                ArrayList<String> connections = (ArrayList<String>)map.get(currentEdge.source);
                connections.add(currentEdge.target);
            }
            if(!map.containsKey(currentEdge.target)){
                ArrayList<String> connections = new ArrayList<String>();
                connections.add(currentEdge.source);
                map.put(currentEdge.target,connections);
            }else{
                ArrayList<String> connections = (ArrayList<String>)map.get(currentEdge.target);
                connections.add(currentEdge.source);
            }

        }
        printNodeMap(map);
    }


    private void assmbleNodes(List<CygrouperEdge> edgeList, Map map){
        Set<String> nodeNames = new HashSet<>();
        for(int i = 0; i < edgeList.size(); i++){
            nodeNames.add(edgeList.get(i).source);
            nodeNames.add(edgeList.get(i).target);
        }

        Map nameToObjectMap = new HashMap<String,CygrouperNode>();
        nodeNames.forEach((name)->{
            // for every key in the map
            CygrouperNode node = new CygrouperNode(name);
            //System.out.print(node);
            nameToObjectMap.put(name,node);
        });

        map.forEach((key,value)->{
            CygrouperNode currentNode = (CygrouperNode) nameToObjectMap.get(key);
            System.out.println("-----------------");
            System.out.println("Current:"+currentNode);
            System.out.println("connections:"+currentNode.getConnectionsList());

            //gives the node object based on the name
            ArrayList<String> connections = (ArrayList<String>)value;
            System.out.println("String connections:"+value);
            System.out.println("String connections size:"+connections.size());

            for(int i = 0; i <connections.size();i++){
                System.out.println("in loop"+i+": String connection:"+connections.get(i));
                currentNode.getConnectionsList().add((CygrouperNode)nameToObjectMap.get(connections.get(i)));
                //System.out.println( currentNode.getConnectionsList());
            }
            System.out.println("AFTER: connections:"+currentNode.getConnectionsList());

        });
        this.map = nameToObjectMap;
        printNodeMap(this.map);


    }
    public Map getTree(){
        return this.map;
    }

    private void printNodeMap(Map map){
        map.forEach((k,v) -> System.out.println("key: " + k + ", value: " + v));
    }
}

