/* 
 * Copyright 2015 Charles Allen Schultz II.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package edu.claflin.cyfinder.internal.logic;

import java.io.File;
import java.util.Comparator;

import org.cytoscape.model.CyColumn;

import edu.claflin.finder.algo.Algorithm;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Node;

/**
 * Represents a configuration of the Subgraph Finder external utility.  
 * Currently uses an inefficient method of storing each data as an individual 
 * field instead of collections.  Will be reworked at some other date if 
 * necessary.
 * 
 * @author Charles Allen Schultz II
 * @version 1.2 June 17, 2015
 */
public class ConfigurationBundle {
    
//    /**
//     * List of Conditions to search for.
//     */
//    private List<Condition> appliedConditions;
    /**
     * The search method.
     */
    private Algorithm algo;
    
    private String weightAttribute = null;
    
    /**
     * Boolean indicating if the search should preserve back edges.
     */
    private boolean preservative = false;
    /**
     * An edge based comparator for ordering the search. May be null in which 
     * case the static ordering is used.
     */
    private Comparator<Edge> ordering = null;
    /**
     * The CyColumn to use to order the edges;
     */
    private CyColumn orderingColumn = null;
    
    // Saving Methodologies
    
    /**
     * Boolean indicating that the found subgraphs should be marked as 
     * annotations on the source graph.
     */
    private boolean inPlace = false;
    /**
     * Boolean indicating that a child graph should be created for each found 
     * subgraph.
     */
    private boolean newChild = false;
    /**
     * Boolean indicating that the found subgraph should be saved to file.  
     * Currently uses the simple output built into the finder utility.
     */
    private boolean saveToFile = false;
    
    /**
     * The File object indicating in what directory to save found subgraphs.
     */
    private File saveDirectory = null;

    /**
     * The origin node from shortest path algorithm.
     */
    private Node fromNode = null;

    /**
     * The destination node from shortest path algorithm.
     */
    private Node toNode = null;
    
    /**
     * Constructor to create the un-configured bundle.
     */
    public ConfigurationBundle() {
//        appliedConditions = new ArrayList<>();
    }
    
//    /**
//     * Adds a condition to the bundle.
//     * @param cond the Condition to add.
//     */
//    public void addCondition(Condition cond) {
//        appliedConditions.add(cond);
//    }
//    /**
//     * Returns the configuration list.
//     * @return the List containing the Condition objects.
//     */
//    public List<Condition> getConditions() {
//        return appliedConditions;
//    }

    /**
     * Sets the search algorithm to use.
     * @param algo the Algorithm object representing the search type.
     */
    public void setAlgo(Algorithm algo) {
        this.algo = algo;
    }
    /**
     * Gets the search algorithm.
     * @return the search Algorithm object.
     */
    public Algorithm getAlgo() {
        return algo;
    }

//    /**
//     * Sets the preservative boolean.
//     * @param preservative the boolean indicating if the search should be 
//     * preservative.
//     */
//    public void setPreservative(boolean preservative) {
//        this.preservative = preservative;
//    }
//    /**
//     * Gets the preservative boolean.
//     * @return the boolean indicating if the search should be preservative.
//     */
//    public boolean isPreservative() {
//        return preservative;
//    }

/**
	 * @return the weightAttribute
	 */
	public String getWeightAttribute()
	{
		return weightAttribute;
	}

	/**
	 * @param weightAttribute the weightAttribute to set
	 */
	public void setWeightAttribute(String weightAttribute)
	{
		this.weightAttribute = weightAttribute;
	}

	//    /**
//     * Sets the ordering comparator.
//     * @param ordering the Edge object Comparator used to order the edges 
//     * in expansion searches.
//     */
//    public void setOrdering(Comparator<Edge> ordering) {
//        this.ordering = ordering;
//    }
//    /**
//     * Gets the ordering comparator.
//     * @return the Edge object Comparator used to order the edges in expansion 
//     * searches.
//     */
//    public Comparator<Edge> getOrdering() {
//        return ordering;
//    }
    /**
     * Sets the ordering column.
     * @param orderingColumn the CyColumn to use for ordering the edges.
     */
    public void setOrderingColumn(CyColumn orderingColumn) {
        this.orderingColumn = orderingColumn;
    }
    /**
     * Gets the ordering column.
     * @return the CyColumn to use for ordering the edges;
     */
    public CyColumn getOrderingColumn() {
        return orderingColumn;
    }

    /**
     * Sets the inPlace boolean.
     * @param inPlace the boolean indicating if found subgraphs should be saved 
     * in place.
     */
    public void setInPlace(boolean inPlace) {
        this.inPlace = inPlace;
    }
    /**
     * Gets the inPlace boolean.
     * @return the boolean indicating if found subgraphs should be saved in 
     * place.
     */
    public boolean isInPlace() {
        return inPlace;
    }

    /**
     * Sets the newChild boolean.
     * @param newChild the boolean indicating if found subgraphs should be 
     * saved as a new child.
     */
    public void setNewChild(boolean newChild) {
        this.newChild = newChild;
    }
    /**
     * Gets the newChild boolean.
     * @return the boolean indicating if found subgraphs should be saved as a 
     * new child.
     */
    public boolean isNewChild() {
        return newChild;
    }

    /**
     * Sets the saveToFile boolean.
     * @param saveToFile the boolean indicating if found subgraphs should be 
     * saved to file.
     */
    public void setSaveToFile(boolean saveToFile) {
        this.saveToFile = saveToFile;
    }
    /**
     * Gets the saveToFile boolean.
     * @return the boolean indicating if found subgraphs should be saved to 
     * file.
     */
    public boolean isSaveToFile() {
        return saveToFile;
    }
    
    /**
     * Sets the save directory for saving subgraphs to file.
     * @param file the File object indicating the directory to save to.
     */
    public void setSaveDirectory(File file) {
        if (file.isDirectory())
            saveDirectory = file;
    }
    /**
     * Returns the target directory for saving subgraphs.
     * @return the File object pointing to the target directory.
     */
    public File getSaveDirectory() {
        return saveDirectory;
    }

    /**
     * Gets the origin node from shortest path algorithm.
     * @return a Node in the CyNetwork
     */
    public Node getFromNode() {
        return fromNode;
    }
    /**
     * Sets the origin node from shortest path algorithm.
     * @param fromNode a node in the CyNetwork
     */
    public void setFromNode(Node fromNode) {
        this.fromNode = fromNode;
    }
    /**
     * Gets the destination node from shortest path algorithm.
     * @return a Node in the CyNetwork
     */
    public Node getToNode() {
        return toNode;
    }
    /**
     * Sets the destination node from shortest path algorithm.
     * @param toNode a node in the CyNetwork
     */
    public void setToNode(Node toNode) {
        this.toNode = toNode;
    }
}
