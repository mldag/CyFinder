package edu.claflin.finder.logic.cygrouper;

import edu.claflin.finder.logic.Graph;
import java.util.ArrayList;
import java.util.Map;

/*
  This class was written by Evyatar Saias & Ariel Sari on Nov 19, 2019.
  This is a Singleton class which implements an Interface also written by Evyatar & Ariel called CommunicationListener.
  This class is instantiated in the Cyfinder Module and provides an easy mean of communication between the Subgraph-Finder Module and the Cyfinder Module.
 */
public class Communicator implements CommunicationListener {
    private static Communicator communicator;
    public static ArrayList<Map<String, CygrouperNode>> groups = new ArrayList<>();
    public static ArrayList<Map<String, CygrouperNode>> partitionNumbers = new ArrayList<>();
    public static ArrayList<Graph> uniqueSubgraphs = new ArrayList<>();
    private Communicator(){

    }
    public static Communicator getSingleton(){
        if(Communicator.communicator == null){
            Communicator.communicator = new Communicator();
            return Communicator.communicator;
        }else{
            return Communicator.communicator;
        }
    }

    @Override
    public void setGroups(ArrayList<Map<String, CygrouperNode>> results) {
        Communicator.groups = results;
    }
    
    @Override
    public void setPartitionNumbers(ArrayList<Map<String, CygrouperNode>> results) {
        Communicator.partitionNumbers = results;
    }

    @Override
    public void setUniqueSubGraphs(ArrayList<Graph> subgraphs) {
        Communicator.uniqueSubgraphs = subgraphs;
    }
}
