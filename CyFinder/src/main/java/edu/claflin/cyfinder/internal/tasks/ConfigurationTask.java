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
package edu.claflin.cyfinder.internal.tasks;

import edu.claflin.cyfinder.internal.ui.ConfigDialog;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

/**
 * Represents a Configuration Task for configuring the SubGraph Finder.  
 * Configures the Subgraph Finder external library.
 *
 * @author Charles Allen Schultz II
 * @version 1.2.1 June 15, 2015
 */
public class ConfigurationTask extends AbstractNetworkTask {

    /**
     * The parent window to exhibit modality over.
     */
    private final Frame parent;
    /**
     * The Action to execute when configuration is completed.
     */
    private final Action successAction;
    /**
     * The configuration dialog reference.
     */
    private ConfigDialog cDialog;
    
    /**
     * Constructs the ConfigurationTask.
     * 
     * @param network the CyNetwork to analyze.
     * @param parent the parent window to exhibit modality over.
     * @param successAction the Action to execute when configuration finishes.
     */
    public ConfigurationTask(CyNetwork network, Frame parent,
            Action successAction) {
        super(network);
        this.parent = parent;
        this.successAction = successAction;
    }

    /**
     * {@inheritDoc }
     *
     * @param taskMonitor the TaskMonitor for reporting progress. Unused.
     */
    @Override
    public void run(final TaskMonitor taskMonitor) {
        taskMonitor.setTitle("CyFinder Configuration Dialog");
        taskMonitor.setStatusMessage("Initializing...");
        taskMonitor.setProgress(0D);
        
        SwingUtilities.invokeLater(() -> {
            List<CyColumn> numberColumns = new ArrayList<>();
            
            network.getDefaultEdgeTable().getColumns().parallelStream()
                    .forEach(col -> {
                        if (Number.class.isAssignableFrom(col.getType())) {
                            numberColumns.add(col);
                        }
                    });
            
            cDialog = new ConfigDialog(parent, successAction, numberColumns);
            cDialog.setLocationRelativeTo(parent);
            cDialog.setVisible(true);
        });

        taskMonitor.setStatusMessage("Initialized.");
        taskMonitor.setProgress(1D);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void cancel() {
        if (cDialog != null) {
            cDialog.setVisible(false);
            cDialog.dispose();
        }
    }
}
