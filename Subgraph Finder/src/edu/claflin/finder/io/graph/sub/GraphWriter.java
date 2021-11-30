package edu.claflin.finder.io.graph.sub;

import edu.claflin.finder.logic.Graph;

/**
 * Interface used by classes that write graphs to memory.
 * 
 * @author Charles Allen Schultz II
 * @version 3.0 May 20, 2015
 */
public interface GraphWriter {

    /**
     * Writes graphs to memory.
     * 
     * @param toWrite the {@link Graph} object to write to memory.
     */
    void writeGraph(Graph toWrite);
}
