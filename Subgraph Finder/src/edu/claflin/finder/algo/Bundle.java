package edu.claflin.finder.algo;

import static edu.claflin.finder.Global.getLogger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Graph;

/**
 * Processes a {@link Graph} searching for subgraphs.  Will utilize all 
 * provided algorithms.  Effectively bundles more than one algorithm into a 
 * single algorithm.
 * 
 * @author Charles Allen Schultz II
 * @version 3.1.2 February 4, 2015
 */
public class Bundle extends Algorithm implements PropertyChangeListener {
    
    /**
     * The {@link Algorithm} array containing the algorithms to process graphs 
     * with.
     */
    private final Algorithm[] algorithms;
    /**
     * Number of algorithms executed.  Used for tracking progress.
     */
    private int done = 0;
    
    /**
     * Constructs the Algorithm object.
     * 
     * @param algorithms the Algorithm array to process graphs with.
     */
    public Bundle(Algorithm[] algorithms) {
        super(new ArgumentsBundle()); // Supplied bundle is not used for configuration.
        this.algorithms = algorithms;
        
        if (getLogger() != null) {
            getLogger().logInfo(LogLevel.DEBUG, "Bundle Algorithm initialized.");
        }
    }

    /**
     * {@inheritDoc }
     * <br>
     * Finds most Subgraphs by running the graph through all the Algorithm 
     * objects in the algorithms array.  Used to search the same 
     * via different methods.
     * 
     * @param graph the {@link Graph} object to search through.
     * @return the ArrayList of Graph objects holding all found subgraphs.
     */
    @Override
    public ArrayList<Graph> process(Graph graph) {
        ArrayList<Graph> subGraphs = new ArrayList<>();
        
        for (Algorithm algo : algorithms) {
            algo.addPropertyChangeListener(this);
            subGraphs.addAll(algo.process(graph));
            done++;
        }
        
        return cull(subGraphs);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PROP_PROGRESS)) {
            double progress = done * 1D / algorithms.length // Completed Algos
                    + ((Double) evt.getNewValue()) / 2; // Current Algo
            setProgress(progress);
        }
    }
    
    @Override
	public String toString() 
    {
    	String result = "";
    	
    	for (int index = 0; index < algorithms.length-1; index++) 
    	{
    		result += algorithms[index].toString() + " and\n";
    	}
    	result += algorithms[algorithms.length-1];
    	
    	return result;
    }
}
