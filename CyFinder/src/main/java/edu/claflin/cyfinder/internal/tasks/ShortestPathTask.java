package edu.claflin.cyfinder.internal.tasks;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

import edu.claflin.cyfinder.internal.Global;
import edu.claflin.cyfinder.internal.logic.ConfigurationBundle;
import edu.claflin.cyfinder.internal.tasks.utils.GraphTaskUtils;
import edu.claflin.cyfinder.internal.ui.ErrorPanel;
import edu.claflin.finder.algo.Algorithm;
import edu.claflin.finder.logic.Graph;

/**
 * Represents a Shortest Path Task. Integrates with the stand-alone library
 * with the same name to find shortest path and return them in Cytoscape Format.
 *
 * @author Juan C Ibarra
 * @version 1.0.0 February 20, 2020
 */
public class ShortestPathTask extends AbstractNetworkTask implements PropertyChangeListener {
    private final ConfigurationBundle config;
    private TaskMonitor taskMonitor;

    /**
     * Constructs the Task.
     *
     * @param network the Network to analyze.
     * @param config  the Configuration to use.
     */
    public ShortestPathTask(CyNetwork network, ConfigurationBundle config) {
        super(network);
        this.config = config;
    }

    @Override
    public void run(final TaskMonitor taskMonitor) {
        try {
            this.taskMonitor = taskMonitor;
            taskMonitor.setTitle("Shortest Path");

            // Read CyNetwork into a Subgraph Finder Network
            Graph graph = null;
            if (!cancelled)
                graph = GraphTaskUtils.convertCyNetwork(network);

            if (graph == null) {
                cancel();
                return;
            }

            // Search for Subgraph Here
            ArrayList<Graph> subgraphs = null;
            if (!cancelled) {
                subgraphs = findShortestPath(taskMonitor, graph);
                if (subgraphs == null)
                {
                    taskMonitor.setStatusMessage("Shortest path could not be found");
                    GraphTaskUtils.showError("Shortest path could not be found", "Make sure the source node and destination node are present in the graph");
                    cancel();
                    return;
                }
            }
            // Save Found Subgraphs To Current Network, child network, file...
            if (!cancelled)
                GraphTaskUtils.saveSubGraphs(taskMonitor, subgraphs, network, config);

        } catch (Throwable error) {
//        	GraphTaskUtils.showError("Error during Shortest Path", error.getMessage());
        	
            SwingUtilities.invokeLater(() -> {
                String message = "An error occurred during execution!";
                ErrorPanel errorPanel = new ErrorPanel(message, error);
                errorPanel.display(Global.getDesktopService().getJFrame(), message);
            });
            cancel();
        }
    }

    /**
     * Processes the graph and returns the shortest path.
     *
     * @param taskMonitor the TaskMonitor to use to report progress.
     * @param target      the Graph object to analyze.
     * @return the ArrayList containing the subgraphs.
     */

    private ArrayList<Graph> findShortestPath(final TaskMonitor taskMonitor, Graph target) {
        taskMonitor.setStatusMessage("Searching shortest path...");
        taskMonitor.setProgress(0D);

        // added updated status messages
        taskMonitor.setStatusMessage("Checking selected agorithm...");
        Algorithm algo = config.getAlgo();
        algo.addPropertyChangeListener(this);
        taskMonitor.setStatusMessage("Processing graph based on " + config.getAlgo().toString());
        ArrayList<Graph> graphs = algo.process(target);
        //comment back in when testing shortest path 1.0.0
        //GraphTaskUtils.showError("Testing shortest path", graphs.toString());
//        for (int i = 0; i < graphs.size(); i++) {
//            Graph g = graphs.get(i);
//            if (g.getNodeCount() < 2) {
//                graphs.remove(i);
//                i--;
//            }
//        }
        if(graphs.isEmpty())
        GraphTaskUtils.showError("No path found", "There is no path between the nodes you selected");
        
        return graphs;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!cancelled && evt.getPropertyName().equals(Algorithm.PROP_PROGRESS)) {
            taskMonitor.setProgress((Double) evt.getNewValue());
        }
    }

    /**
     * Cancels the Task.
     */
    @Override
    public void cancel() {
        super.cancel();
        if (taskMonitor != null)
            taskMonitor.setProgress(-1D);
    }
}