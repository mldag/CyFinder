package edu.claflin.finder.io.graph;

import static edu.claflin.finder.Global.getLogger;
import static edu.claflin.finder.Global.getOutput;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.claflin.finder.io.graph.sub.GraphWriter;
import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;

/**
 * A class for writing graphs in the NOA batch format.
 * 
 * NOA batch format is similar to the SIF format except that multiple graphs 
 * are contained within a single file for batch processing by the NOA plugin 
 * for Cytoscape.  An example batch graph is here: 
 *
 * 
 * @author Charles Allen Schultz II
 * @version 3.1 May 28, 2015
 */
public final class NOABatchIO implements GraphWriter {
    
    /**
     * Contains the String representation of the default relationship to use.  
     * The default relationship value is used when writing a graph and the graph 
     * being read does not use String objects to represent edges.  Currently, 
     * String objects are being used for simplicity.
     */
    private final String defaultRelationship;
    
    /**
     * Constructs the NOABatchIO object.
     * 
     * @param defaultRelationship the String representing the default edge 
     * relationship.
     */
    public NOABatchIO(String defaultRelationship) {
        this.defaultRelationship = defaultRelationship;
    }
    
    /**
     * {@inheritDoc } Writes NOA batch files.
     * 
     * @param toWrite the Graph object to write to memory.
     */
    @Override
    public void writeGraph(Graph toWrite) {
        String[] partsOfName = toWrite.getName().split("-");
        File output = new File(getOutput(), partsOfName[partsOfName.length - 1]);
        boolean error = false;
        boolean append = output.exists();
        
        if (getLogger() != null) {
            getLogger().logInfo(LogLevel.NORMAL, "GraphIO: Attempting to log "
                    + "graph to file (NOA Batch Format"
                    + (append ? " | Appending to existing file." : "") + ")");
        }
        
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(output, append))) {
            bW.write(">" + toWrite.getName().replace(" ", "_"));
            bW.newLine();
            
            for (Edge edge : toWrite.getEdgeList()) {
                String type = verifyRelationship(edge.getData());
                String line = String.format("%s %s %s",
                        edge.getSource().getIdentifier(), type,
                        edge.getTarget().getIdentifier());
                bW.write(line);
                bW.newLine();
                
                if (getLogger() != null) {
                    getLogger().logInfo(LogLevel.VERBOSE, 
                            "GraphIO: Wrote line to graph file: " + line);
                }
            }
        } catch (IOException ioe) {
            error = true;
            if (getLogger() != null) {
                getLogger().logInfo(LogLevel.NORMAL, 
                        "GraphIO: Error writing graph to file: "
                        + toWrite.getName());
            }
        } finally {
            String success = error ? "Failed to write" : "Successfully wrote";
            if (getLogger() != null) {
                getLogger().logInfo(LogLevel.NORMAL, "GraphIO: " + success +
                        " NOA Batch graph to file: "
                        + output.getAbsolutePath());
            }
        }
    }
    
    /**
     * Verifies the relationship between edges.  Checks to see that the Object 
     * used in the {@link Graph} for representing an edge is a String.  If it 
     * not, the default relationship is used instead.
     * 
     * @param edgeData the Edge Data
     * @return the relationship in the edge
     */
    private String verifyRelationship(Object edgeData) {
        if (edgeData instanceof String)
            return (String) edgeData;
        else
            return defaultRelationship;
    }
}
