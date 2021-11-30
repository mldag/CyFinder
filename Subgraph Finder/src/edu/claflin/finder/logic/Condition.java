package edu.claflin.finder.logic;

/**
 * Represents a graph condition in memory.  Subgraphs are described based on 
 * Condition objects.  Simple graphs can be represented by a single condition 
 * or several condition objects wrapped within a large condition.
 * 
 * @author Charles Allen Schultz II
 * @version 1.0.1 May 22, 2015
 */
public abstract class Condition {
    
    /**
     * Used to test if the supplied graph is within the bounds of the 
     * condition's implementation.
     * 
     * @param existingGraph the Graph to test if the Condition applies.
     * @return a boolean indicating if the requirements satisfy the condition.
     */
    public abstract boolean satisfies(Graph existingGraph);
}
