package edu.claflin.finder.logic.processor;

import java.util.ArrayList;

/**
 * Represents an operation that can be carried out on a specific type of data.
 * 
 * @author Charles Allen Schultz II
 * @version 3.0 May 20, 2015
 * @param <T> the T type object to process.
 * @param <S> the S type object of the data produced by processing.
 */
public interface Processable<T, S> {
    
    /**
     * Processes data.
     * 
     * @param t the T type object to process.
     * @return the S type object array produced by the processing.
     */
    ArrayList<S> process(T t);
}
