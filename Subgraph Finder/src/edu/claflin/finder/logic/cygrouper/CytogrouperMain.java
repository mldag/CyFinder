package edu.claflin.finder.logic.cygrouper;

import edu.claflin.finder.logic.Graph;
import java.util.*;
/*
* Everything in the cygrouper package was written By Evyatar Saias & Ariel Sari on Nov 19, 2019.
* The purpose of the classes in this package is to assign each node a set attribute (Group A or Group B).
* With this information Cytoscape can group the nodes into the different sets that make up the bipartite condition.
* */
public class CytogrouperMain {
    /*
     ArrayList<Map<String,CygrouperNode>> results is a list of Maps, where each map is a map with a String as key and a CygrouperNode as value. (Map<String,CygrouperNode>)
     The Key (String) is the name of the Gene (EX:CLCA2) while the value CygrouperNode is the CyGrouperNode object representing that same gene. This CygrouperNode object contains a list of all the connections to it as a field.
    */
    ArrayList<Map<String,CygrouperNode>> results = new ArrayList<>();
    /* CommunicationListener listener is the listener used to store the results field on the Communicator object, and allow access to it on the Cyfinder Module. */
    CommunicationListener listener;
    /*
      This map (Map tree here below) is a map with a String as key and a CygrouperNode as value. (Map<String,CygrouperNode>)
      The Key (String) is the name of the Gene (EX:CLCA2) while the value CygrouperNode is the CyGrouperNode object representing that same gene.
    */
    Map tree;

    /*
      PARAM: List<Graph> subgraphs:
             The the end product of the original Cyfinder logic, that is the final correct subgraphs found given all of the conditions provided.
      PARAM: CommunicationListener listener:
             a Communicator object (Singleton) which implements the CommunicationListener interface
     */
    public CytogrouperMain(List<Graph> subgraphs, CommunicationListener listener, int partiteNumber) {
        this.listener = listener;       
                
        for(int i = 0; i < subgraphs.size(); i++) {
        
        	//if the passed subgraph has no edges then continue to prevent an error
          	//FIXME - will error if passed subgraph has no edges
        	if(subgraphs.get(i).getEdgeList().isEmpty())
        		continue;
            CygrouperAssembler assmble = new CygrouperAssembler(subgraphs.get(i).getEdgeList());
            tree = assmble.getTree();
            BFS(tree);
            
            //Currently disabled
            /*
            if(partiteNumber > 2) {
            	kPartite(tree, partiteNumber); 
            } */           
            
            results.add(tree);
        }
        listener.setGroups(this.results);
    }
    /*
        Breadth First Search like algorithm to traverse the tree and assign a group to each level.
     */
    public void BFS (Map tree){
        Queue<CygrouperNode> q = new LinkedList<>();
        CygrouperNode firstNode = (CygrouperNode)tree.get(tree.keySet().toArray()[0]);
        firstNode.setGroup("A");
        firstNode.isVisited = true;
        firstNode.setPartiteNumber(0);
        q.add(firstNode);

        while(!q.isEmpty()){
            //char parentGroup = q.peek().getOppositeGroup();
            CygrouperNode current = q.peek();
            //current.setGroup(parentGroup);
            ArrayList<CygrouperNode> connections = current.getConnectionsList();
            for(int i = 0; i < connections.size();i++){
                CygrouperNode connection = connections.get(i);
                if(connection.isVisited != true){
                    connection.setGroup(current.getOppositeGroup());
                    connection.setPartiteNumber(current.getPartiteNumber() + 1);
                    q.add(connection);
                }
            }
            current.isVisited = true;
            q.remove();

        }
    }
    
    
    /** 
     * Algorithm to convert tree with groups A and B to K-Partite.
     * Adds a new attribute, assuming that A and B attributes are already created as per above algorithm
     * 
     * @param tree the graph, assumed to already have been assigned group names
     * @param k the number of partitions to create
     * 
     * Written by kpuli007
     */
    /*
    public void kPartite(Map tree, int k) {    	
    	//1-partite graph isn't a thing, and 2-partite is just binary partite which is the input to this method.
    	//So we disallow using them as parameters
    	if(k <= 2) {    		
    		System.out.println("k must be greater than or equal to 3!");
        	//Alert error here I guess. Probably quit execution
        	return;
    	}
    	
        //Hopefully tree.size is the number of nodes lol        
        //1. Transfer all nodes into queues. We don't care about order too much so we use Linked List which implements Queue
        Queue<CygrouperNode> groupA = new LinkedList<CygrouperNode>();
        Queue<CygrouperNode> groupB = new LinkedList<CygrouperNode>();
        
        for(Object o: tree.values()) {
            CygrouperNode node = (CygrouperNode)o;
            if(node.isGroup("A")) {
            	groupA.add(node);
            }
            else if(node.isGroup("B")) {
            	groupB.add(node);
            }
            else {
            	System.out.println("This should never be reached.");
            	//Alert error here I guess. Probably quit execution
            	return;
            }
        }
        
        
        //2. Pick the group that has more
        Queue<CygrouperNode> greaterGroup;
        Queue<CygrouperNode> lesserGroup;
        
        if(groupA.size() >= groupB.size()) {
        	greaterGroup = groupA;
        	lesserGroup = groupB;
        }
        else {
        	greaterGroup = groupB;
        	lesserGroup = groupA;
        }
        
        //3.
        //If k is even, both groups need minimum k/2 nodes.
        //If k is odd, larger group needs (k + 1) / 2, and smaller needs ((k + 1) / 2) - 1
        //In integer division, it is safe to do k / 2 and (k / 2) + 1
        
        if(k % 2 == 0) {
        	if(greaterGroup.size() < (k / 2) || lesserGroup.size() < (k / 2)) {
        		return;
        	}
        }
        else {
        	if(greaterGroup.size() < (k / 2 + 1) || lesserGroup.size() < (k / 2)) {
        		return;
        	}
        }
        
        //4. Alternate groups. For each integer group from 1 to k (inclusive):
        //if it's odd give it a node from the greater group
        //if it's even give it a node from the lesser group
        //The above distinction is irrelevant if k is even, but it is necessary for odd cases and works in both odd and even cases.
      
        while(!greaterGroup.isEmpty() && !lesserGroup.isEmpty()) {
        	for(int part = 1; part <= k; part++) {
        		if(greaterGroup.isEmpty() || lesserGroup.isEmpty()) {
        			break;
        		}        		
        		else if(part % 2 == 1) {
            		CygrouperNode current = greaterGroup.remove();
            		current.setPartiteNumber(part);
            	}
            	else {
            		CygrouperNode current = lesserGroup.remove();
            		current.setPartiteNumber(part);
            	}
            }
        }
        
        //5. at this point, one of the sets is empty.
        //remove all nodes from the greater group and toss them into partite 1
        //remove all nodes from the lesser group and toss them into partite 2
        //this is done for simplicity for now, in the future could make it more balanced
        //there's no guarantee that the lesser group isn't empty when the above loop is done by the way
        while(!greaterGroup.isEmpty()) {
            CygrouperNode current = greaterGroup.remove();
            current.setPartiteNumber(1);
        }
        while(!lesserGroup.isEmpty()) {
            CygrouperNode current = lesserGroup.remove();
            current.setPartiteNumber(2);
        }
    }
    */
}
