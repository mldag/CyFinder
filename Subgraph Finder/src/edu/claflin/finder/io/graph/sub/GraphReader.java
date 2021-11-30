package edu.claflin.finder.io.graph.sub;

import edu.claflin.finder.logic.Graph;
import java.io.File;

/**
 * Interface used for classes designed to read graphs from memory.
 * 
 * @author Charles Allen Schultz II
 * @version 3.0 May 20, 2015
 */
public interface GraphReader {

    /**
     * Reads a graph from memory.
     * 
     * @param source the File object pointing to the graph in memory to read.
     * @param undirected a boolean indicating if the graph should be 
     * interpreted as undirected.
     * @return the {@link Graph} object read from memory.
     */
    Graph parseGraph(File source, boolean undirected);
}
