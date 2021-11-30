package edu.claflin.finder.logic.cygrouper;

import edu.claflin.finder.logic.Graph;
import java.util.ArrayList;
import java.util.Map;

/*
  This interface was written by Evyatar Saias & Ariel Sari on Nov 19, 2019
  It is implemented by the singleton class Communicator, which are used to provide an easy
  way of communicating between the modules Subgraph-Finder and Cyfinder.
 */
public interface CommunicationListener {
    public void setUniqueSubGraphs(ArrayList<Graph> subgraphs);
    public void setGroups(ArrayList<Map<String, CygrouperNode>> results);
    public void setPartitionNumbers(ArrayList<Map<String, CygrouperNode>> results);
}
