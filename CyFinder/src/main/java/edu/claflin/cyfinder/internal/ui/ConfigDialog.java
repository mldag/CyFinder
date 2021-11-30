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
package edu.claflin.cyfinder.internal.ui;

import edu.claflin.cyfinder.internal.logic.ConfigurationBundle;
import static edu.claflin.cyfinder.internal.ui.GridBagBuilder.getConstraints;
import edu.claflin.finder.algo.Algorithm;
import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.algo.BreadthFirstTraversalSearch;
import edu.claflin.finder.algo.Bundle;
import edu.claflin.finder.algo.DepthFirstTraversalSearch;
import edu.claflin.finder.logic.Condition;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.comp.EdgeWeightComparator;
import edu.claflin.finder.logic.cond.BipartiteCondition;
import edu.claflin.finder.logic.cond.CliqueCondition;
import edu.claflin.finder.logic.cond.DirectedCliqueCondition;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import org.cytoscape.model.CyColumn;

/**
 * Represents a configuration dialog for the Subgraph Finder external utility. 
 * 
 * @author Charles Allen Schultz II
 * @version 1.7 June 19, 2015
 */
public class ConfigDialog extends JDialog implements ActionListener,
        ItemListener {
    
    /**
     * Used to populate the Conditions List.
     */
    private static final Class[] conditions = 
            new Class[] {
                BipartiteCondition.class, 
                CliqueCondition.class,
                DirectedCliqueCondition.class
            };
    /**
     * Used to populate the algorithms list.
     */
    private static final Class[] algorithms = new Class[] {
        BreadthFirstTraversalSearch.class,
        DepthFirstTraversalSearch.class
    };
    /**
     * Used to populate the orderings list.
     */
    private static final Class[] orderings = 
            new Class[] {Void.class, EdgeWeightComparator.class};
    
    private final List<CyColumn> columns;
    private CyColumn selectedColumn = null;
    
    /**
     * The action to execute upon completing the configuration.
     */
    private final Action successAction;
    
    // UI components
    
    /**
     * GUI: Conditions Label.
     */
    private JLabel cLabel = new JLabel("Search Conditions");
    /**
     * GUI: Algorithms Label.
     */
    private JLabel aLabel = new JLabel("Search Algorithm(s)");
    /**
     * GUI: Orderings Label.
     */
    private JLabel oLabel = new JLabel("Ordering:");
    
    /**
     * GUI: Scroll pane for the conditions list.
     */
    private JScrollPane cPane;
    /**
     * GUI: Conditions list object.
     */
    private JList conditionsList;
    /**
     * GUI: Scroll pane for the algorithms list.
     */
    private JScrollPane aPane;
    /**
     * GUI: Algorithms list object.
     */
    private JList algorithmsList;
    
    /**
     * GUI: Combo box for selecting an ordering.
     */
    private JComboBox orderingSelection;
    
    /**
     * GUI: Checkbox for setting a comparison algorithm to sort in ascending
     * order.
     */
    private JCheckBox aCheckBox = new JCheckBox("Ascending Order");
    
    /**
     * GUI: Checkbox for enabling "preservative" searching.
     */
    private JCheckBox pCheckBox = new JCheckBox("Edge Preservative");
    
    /**
     * GUI: Checkbox for enabling in-place annotation.
     */
    private JCheckBox iCheckBox = new JCheckBox("In-Place annotation of source graph.");
    /**
     * GUI: Checkbox for enabling new child creation.
     */
    private JCheckBox nCheckBox = new JCheckBox("New Child Graph beneath source graph.");
    /**
     * GUI: Checkbox for enabling saving to file.
     */
    private JCheckBox sCheckBox = new JCheckBox("Save found subgraph to file.");
    
    /**
     * GUI: Button to complete configuration.
     */
    private JButton doneButton = new JButton("Done");
    
    /**
     * The File object indicating the directory to save subgraphs in.
     */
    private File saveDirectory = null;
    
    /**
     * Constructor for initializing the Panel.
     */
    public ConfigDialog(Frame parent, Action successAction,
            List<CyColumn> columns) {
        super(parent, "Configure Subgraph Finder", true);
        this.successAction = successAction;
        this.columns = columns;
        
        ClassModel cModel = new ClassModel(conditions);
        conditionsList = new JList(cModel);
        conditionsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        conditionsList.setCellRenderer(cModel);
        cPane = new JScrollPane(conditionsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        ClassModel aModel = new ClassModel(algorithms);
        algorithmsList = new JList(aModel);
        algorithmsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        algorithmsList.setCellRenderer(aModel);
        aPane = new JScrollPane(algorithmsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        ClassModel orderingsModel = new ClassModel(orderings);
        orderingSelection = new JComboBox(orderingsModel);
        orderingSelection.setSelectedIndex(0);
        orderingSelection.setRenderer(orderingsModel);
        if (columns.isEmpty())
            orderingSelection.setEnabled(false);
        
        orderingSelection.addItemListener(this);
        sCheckBox.addActionListener(this);
        aCheckBox.setSelected(true);
        aCheckBox.setEnabled(false);
        
        doneButton.addActionListener(this);
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        init();
    }
    /**
     * Initializes and constructs the GUI.
     */
    private void init() {
        setLayout(new GridBagLayout());
        Insets insets = new Insets(2, 2, 2, 2);
        
        add(cLabel, getConstraints(0, 0, 2, 1, 1, 1, 
                GridBagConstraints.NONE, GridBagConstraints.PAGE_END, 
                0, 0, insets));
        add(aLabel, getConstraints(2, 0, 2, 1, 1, 1, 
                GridBagConstraints.NONE, GridBagConstraints.PAGE_END, 
                0, 0, insets));
        add(cPane, getConstraints(0, 1, 2, 4, 1, 1, 
                GridBagConstraints.BOTH, GridBagConstraints.CENTER, 
                0, 0, insets));
        add(aPane, getConstraints(2, 1, 2, 4, 1, 1, 
                GridBagConstraints.BOTH, GridBagConstraints.CENTER, 
                0, 0, insets));
        add(new JSeparator(JSeparator.HORIZONTAL), 
                getConstraints(0, 5, 4, 1, 1, 0, 
                        GridBagConstraints.BOTH, GridBagConstraints.CENTER, 
                        0, 0, insets));
        add(oLabel, getConstraints(0, 6, 1, 1, 1, 1, 
                GridBagConstraints.NONE, GridBagConstraints.LINE_END, 
                0, 0, insets));
        add(orderingSelection, getConstraints(1, 6, 1, 1, 1, 1, 
                GridBagConstraints.NONE, GridBagConstraints.CENTER, 
                0, 0, insets));
        add(aCheckBox, getConstraints(1, 7, 1, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.CENTER,
                0, 0, insets));
        add(pCheckBox, getConstraints(2, 6, 2, 1, 1, 1, 
                GridBagConstraints.BOTH, GridBagConstraints.CENTER, 
                0, 0, insets));
        add(new JSeparator(JSeparator.HORIZONTAL), 
                getConstraints(0, 8, 4, 1, 1, 0, 
                        GridBagConstraints.BOTH, GridBagConstraints.CENTER, 
                        0, 0, insets));
        add(iCheckBox, getConstraints(0, 9, 4, 1, 1, 1, 
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0, 0, insets));
        add(nCheckBox, getConstraints(0, 10, 4, 1, 1, 1, 
                GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                0, 0, insets));
        add(sCheckBox, getConstraints(0, 11, 4, 1, 1, 1, 
                GridBagConstraints.BOTH, GridBagConstraints.CENTER, 
                0, 0, insets));
        add(new JSeparator(JSeparator.HORIZONTAL), 
                getConstraints(0, 12, 4, 1, 1, 0, 
                        GridBagConstraints.BOTH, GridBagConstraints.CENTER,
                        0, 0, insets));
        add(doneButton, getConstraints(2, 13, 2, 1, 1, 1,
                GridBagConstraints.NONE, GridBagConstraints.LINE_END, 
                0, 0, insets));
        pack();
    }

    /**
     * Creates a ConfigurationBundle object holding the configuration as 
     * defined by the user.
     * 
     * @return the ConfigurationBundle.
     * @throws Exception should a problem with reflection occur.
     */
    public ConfigurationBundle getConfigurationBundle() throws Exception {
        ConfigurationBundle configBundle = new ConfigurationBundle();
        ArgumentsBundle argsBundle = new ArgumentsBundle();
        
        
        // Add Conditions
        List selectedConditions = conditionsList.getSelectedValuesList();
        for (Object obj : selectedConditions) {
            Class condClass = (Class) obj;
            argsBundle.addCondition((Condition) condClass.newInstance());
        }
        
        // Add Ordering
        Class selectedOrdering = (Class) orderingSelection.getSelectedItem();
        if (selectedOrdering != Void.class) {
            Constructor declaredConstructor = selectedOrdering.getDeclaredConstructor(boolean.class);
            Comparator<Edge> edgeWeightComparator = (Comparator<Edge>) declaredConstructor.newInstance(aCheckBox.isSelected());
            argsBundle.putObject(ArgumentsBundle.COMMON_ARGS.EDGE_WEIGHT_COMPARATOR.toString(), edgeWeightComparator);
            configBundle.setOrderingColumn(selectedColumn);
        }
        
        // Set Preservative
        argsBundle.putBoolean(ArgumentsBundle.COMMON_ARGS.EDGE_PRESERVATION.toString(), pCheckBox.isSelected());
        
        // Select Algorithm
        List selectedAlgorithms = algorithmsList.getSelectedValuesList();
        
        Algorithm algo;
        if (selectedAlgorithms.size() == 1) {
            Class algoClass = (Class) selectedAlgorithms.get(0);
            Constructor constructor = algoClass.getConstructor(ArgumentsBundle.class);
            algo = (Algorithm) constructor.newInstance(argsBundle);            
        } else {
            ArrayList<Algorithm> bundledAlgos = new ArrayList<>();
            for (Object obj : selectedAlgorithms) {
                Class algoClass = (Class) obj;
                Constructor constructor = algoClass.getConstructor(ArgumentsBundle.class);
                bundledAlgos.add((Algorithm) constructor.newInstance(argsBundle));
            }
            algo = new Bundle(bundledAlgos.toArray(new Algorithm[0]));
        }
        configBundle.setAlgo(algo);
        
        configBundle.setInPlace(iCheckBox.isSelected());
        configBundle.setNewChild(nCheckBox.isSelected());
        configBundle.setSaveToFile(sCheckBox.isSelected());
        
        if (configBundle.isSaveToFile())
            configBundle.setSaveDirectory(saveDirectory);
        
        return configBundle;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == doneButton) {
            String errorTitle = "Configuration Error";
            if (conditionsList.getSelectedValuesList().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "You must select a condition to search for!", errorTitle,
                        JOptionPane.ERROR_MESSAGE);
            } else if (algorithmsList.getSelectedValuesList().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "You must select an algorithm to use in the search!",
                        errorTitle, JOptionPane.ERROR_MESSAGE);
            } else if (!iCheckBox.isSelected() && !nCheckBox.isSelected()
                    && !sCheckBox.isSelected()) {
                JOptionPane.showMessageDialog(this,
                        "You must select a means of saving results!", errorTitle,
                        JOptionPane.ERROR_MESSAGE);
            } else {
                ActionEvent newEvent = null;
                try {
                    newEvent = new ActionEvent(getConfigurationBundle(), 0,
                            "CONFIG_BUNDLE");
                } catch (Exception ex) {
                    String description = "An error occurred trying to create "
                            + "the ConfigurationBundle.";
                    ErrorPanel errorPanel = new ErrorPanel(description, ex);
                    errorPanel.display(this, errorTitle);
                } finally {
                    setVisible(false);
                    if (newEvent != null) {
                        successAction.actionPerformed(newEvent);
                    }
                    dispose();
                }
            }
        } else if (e.getSource() == sCheckBox && sCheckBox.isSelected()) {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnval = fileChooser.showOpenDialog(this);
            
            if (returnval != JFileChooser.APPROVE_OPTION) {
                sCheckBox.setSelected(false);
            } else {
                saveDirectory = fileChooser.getSelectedFile();
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItemSelectable() == orderingSelection && 
                e.getStateChange() == ItemEvent.SELECTED && 
                orderingSelection.getSelectedIndex() != 0) {
            CyColumn response = (CyColumn) JOptionPane.showInputDialog(this, 
                    "Please select a column to use for ordering.",
                    "Ordering Configuration",
                    JOptionPane.QUESTION_MESSAGE,
                    null, 
                    columns.toArray(new CyColumn[0]),
                    columns.get(0));
            
            if (response == null) {
                orderingSelection.setSelectedIndex(0);
                aCheckBox.setEnabled(false);
            } else {
                selectedColumn = response;
                aCheckBox.setEnabled(true);
            }
        }
    }
    
    /**
     * Represents a ListModel, a ComboBoxModel, and a ListCellRenderer all in 
     * one.
     */
    private final class ClassModel extends DefaultComboBoxModel<Class> 
            implements ListCellRenderer {

        /**
         * The Class data to use.
         */
        private final Class[] data;
        /**
         * A default renderer for making the majority of the component.
         */
        private DefaultListCellRenderer dLCR = new DefaultListCellRenderer();
        
        /**
         * Initialize the object.
         * @param data the Class array containing the options.
         */
        public ClassModel(Class[] data) {
            this.data = data;
        }
        
        /**
         * {@inheritDoc }
         * @return an integer indicating the size of the data.
         */
        @Override
        public int getSize() {
            return data.length;
        }

        /**
         * {@inheritDoc }
         * @param index the integer index of the object queried.
         * @return the Class object at that index.
         */
        @Override
        public Class getElementAt(int index) {
            return data[index];
        }

        /**
         * {@inheritDoc } Overwrites the label information to be the simple 
         * class name.
         * 
         * @param list the JList being rendered.
         * @param value the value of the object.
         * @param index the index of the object.
         * @param isSelected a boolean indicating if the object is selected.
         * @param cellHasFocus a boolean indicating if the cell has focus.
         * @return a JComponent rendering of the cell (JLabel).
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel listLabel = (JLabel) 
                    dLCR.getListCellRendererComponent(list, value, index, 
                            isSelected, cellHasFocus);
            
            Class selectedClass = (Class) value;
            String original = selectedClass.getSimpleName();
            String spaced = Pattern.compile("([a-z])([A-Z])").matcher(original)
                    .replaceAll("$1 $2");
            listLabel.setText(spaced);
            
            return listLabel;
        }
    }
}
