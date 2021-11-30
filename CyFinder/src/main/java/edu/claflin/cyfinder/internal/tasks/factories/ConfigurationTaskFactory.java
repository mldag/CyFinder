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
package edu.claflin.cyfinder.internal.tasks.factories;

import edu.claflin.cyfinder.internal.tasks.ConfigurationTask;
import java.awt.Frame;
import javax.swing.Action;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

/**
 * Produces a configuration task.
 *
 * @author Charles Allen Schultz II
 * @version 1.1 June 15, 2015
 */
public class ConfigurationTaskFactory extends AbstractNetworkTaskFactory {

    /**
     * The parent of the configuration JDialog.
     */
    private final Frame parent;
    /**
     * The action to execute when the configuration dialog is completed.
     */
    private final Action successAction;
    
    /**
     * Constructor to create the factory.
     * 
     * @param parent the parent of the Configuration JDialog.
     * @param successAction the action to execute when configuration completes.
     */
    public ConfigurationTaskFactory(Frame parent, Action successAction) {
        this.parent = parent;
        this.successAction = successAction;
    }
    
    /**
     * {@inheritDoc }
     *
     * @param network a CyNetwork that has already been set.
     * @return the TaskIterator containing the new Task.
     */
    @Override
    public TaskIterator createTaskIterator(CyNetwork cn) {
        return new TaskIterator(new ConfigurationTask(cn, parent, successAction));
    }
}
