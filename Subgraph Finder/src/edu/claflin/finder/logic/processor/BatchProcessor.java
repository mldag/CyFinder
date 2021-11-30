package edu.claflin.finder.logic.processor;

import static edu.claflin.finder.Global.getLogger;

import java.util.ArrayList;

import edu.claflin.finder.log.LogLevel;

/**
 * Used to process one or multiple objects.  Processing is accomplished via a 
 * level of abstraction.  This is the primary control structure of the SNBA 
 * code base.
 * 
 * @author Charles Allen Schultz II
 * @version 3.1 May 28, 2015
 * @param <T> the object Type to be processed.
 * @param <S> the expected output Type of the processing.
 */
public final class BatchProcessor<T, S> {
        
    /**
     * Constructs a BatchProcessor.  Since a BatchProcessor object only relies 
     * on restrictions set using angle bracket operators, no other information 
     * is needed for proper instantiation.
     */
    public BatchProcessor() {}
    
    /**
     * Processes a singular object.  Utilizes a system of abstraction to allow 
     * multiple parts of the program to rely on the same infrastructure for 
     * performing mass-processing.
     * 
     * @param toProcess the T object to process.
     * @param processor the Processable object to process with.
     * @return the S object array containing the results of the processing.
     */
    public ArrayList<S> processSingular(T toProcess, Processable<T, S> processor) {
        
        if (getLogger() != null) {
            getLogger().logInfo(LogLevel.NORMAL, 
                    String.format("Processing %s object.", 
                    toProcess.getClass().getCanonicalName()));
        }
        
        ArrayList<S> processedData = processor.process(toProcess);
        
        if (getLogger() != null) {
            getLogger().logInfo(LogLevel.NORMAL, 
                String.format("Processed %s object.",
                toProcess.getClass().getCanonicalName()));
        }
        
        return processedData;
    }
    
    /**
     * Processes multiple files.  This method does so by calling the 
     * processSingular(T, {@link Processable}) method.
     * 
     * @param toProcess the T object array containing the objects to be processed.
     * @param processor the Processable object to process the objects with.
     * @return the S object two dimensional array containing the collective 
     * results of the processing.
     */
    public ArrayList<ArrayList<S>> processMultiple(T[] toProcess, Processable<T, S> processor) {
        ArrayList<ArrayList<S>> processedData = new ArrayList<ArrayList<S>>();
        
        if (getLogger() != null) {
            getLogger().logInfo(LogLevel.NORMAL, 
                    String.format("Processing %d %s objects.", 
                    toProcess.length, toProcess.getClass().getCanonicalName()));
        }
        
        for (T item : toProcess) {
            processedData.add(processSingular(item, processor));
        }
     
        if (getLogger() != null) {
            getLogger().logInfo(LogLevel.NORMAL, 
                    String.format("Processed %d %s objects.", 
                    toProcess.length, toProcess.getClass().getCanonicalName()));
        }
        
        return processedData;
    }
}