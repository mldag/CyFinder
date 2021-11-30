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
package edu.claflin.cyfinder.internal.ui.configdialog;

import static edu.claflin.cyfinder.internal.ui.GridBagBuilder.getConstraints;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.cytoscape.model.CyColumn;

import edu.claflin.cyfinder.internal.logic.ConfigurationBundle;
import edu.claflin.cyfinder.internal.tasks.utils.GraphTaskUtils;
import edu.claflin.cyfinder.internal.ui.ErrorPanel;
import edu.claflin.cyfinder.internal.ui.utils.ClassModel;
import edu.claflin.cyfinder.internal.ui.utils.ComboItem;
import edu.claflin.finder.algo.Algorithm;
import edu.claflin.finder.algo.Algorithm.GraphSortOrder;
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
import edu.claflin.finder.logic.cygrouper.Communicator;
import edu.claflin.finder.logic.cygrouper.CygrouperNode;

/**
 * Represents a configuration dialog for the Subgraph Finder external utility.
 * 
 * @author Charles Allen Schultz II
 * @version 1.7 June 19, 2015
 */
public class FinderConfigDialog extends ConfigDialog implements ItemListener
{
	/**
	 * Used to populate the Conditions List.
	 */
	private static final Class[] conditions = new Class[] { BipartiteCondition.class, CliqueCondition.class,
			DirectedCliqueCondition.class };
	/**
	 * Used to populate the algorithms list.
	 */
	private static final Class[] algorithms = new Class[] { BreadthFirstTraversalSearch.class,
			DepthFirstTraversalSearch.class };
	/**
	 * Used to populate the orderings list.
	 */
	private static final Class[] orderings = new Class[] { Void.class, EdgeWeightComparator.class };

	private final List<CyColumn> columns;
	private CyColumn selectedColumn = null;

	// ----------------------------------------------------------------------------------------------------

	/**
	 * Constructor for initializing the Panel.
	 */
	public FinderConfigDialog(Frame parent, Action successAction, List<CyColumn> columns)
	{
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
		nCheckBox.addActionListener(this);
		sCheckBox.addActionListener(this);
		aCheckBox.setSelected(true);
		aCheckBox.setEnabled(false);

		doneButton.addActionListener(this);

		sortGraphSelection.setEnabled(false); // no sorting option if no new child saving
		sortGraphSelection.addItem(new ComboItem("None", 0));
		sortGraphSelection.addItem(new ComboItem("Ascending", 1));
		sortGraphSelection.addItem(new ComboItem("Descending", 2));
		sortGraphSelection.addItem(new ComboItem("Average Weight", 3));

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(true);
		init();
	}

	/**
	 * Initializes and constructs the GUI.
	 */
	@Override
	protected void init()
	{
		setLayout(new GridBagLayout());
		Insets insets = get_insets();

		addSearchConfig(insets);
		add(new JSeparator(JSeparator.HORIZONTAL), getConstraints(0, vertical_index++, 4, 1, 1, 0,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));
		addMiscConfig(insets);
		add(new JSeparator(JSeparator.HORIZONTAL), getConstraints(0, vertical_index++, 4, 1, 1, 0,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));
		addMinNodeCount(insets);
		add(new JSeparator(JSeparator.HORIZONTAL), getConstraints(0, vertical_index++, 4, 1, 1, 0,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));
		addOrderedSaveOptions(insets);
		add(new JSeparator(JSeparator.HORIZONTAL), getConstraints(0, vertical_index++, 4, 1, 1, 0,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));

		// partite feature does not currently work
//		addPartiteConfig(insets);
//		add(new JSeparator(JSeparator.HORIZONTAL), getConstraints(0, vertical_index++, 4, 1, 1, 0,
//				GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));

		addDoneButton(insets);
		// testing();
		pack();
	}

	/* Evyatar & Ariel, not used. Kept for testing purposes */
	public void testing()
	{
		Communicator communicator = Communicator.getSingleton();
		JTextArea txt = new JTextArea();
		for (int i = 0; i < communicator.groups.size(); i++)
		{
			txt.append("SG" + (i + 1) + " :{\n");
			for (Map.Entry<String, CygrouperNode> x : communicator.groups.get(i).entrySet())
			{
				txt.append("\t" + x.getKey() + " " + x.getValue().group + "\n");
			}
			txt.append("}\n");
		}
		JFrame newFrame = new JFrame();
		JPanel panel = new JPanel();
		JScrollPane scroll = new JScrollPane(panel);
		panel.add(txt);
		newFrame.add(scroll);
		newFrame.setSize(500, 500);
		newFrame.setVisible(true);
	}

	/**
	 * Creates a ConfigurationBundle object holding the configuration as defined by
	 * the user.
	 * 
	 * @return the ConfigurationBundle.
	 * @throws Exception should a problem with reflection occur.
	 */
	@Override
	public ConfigurationBundle getConfigurationBundle() throws Exception
	{
		ConfigurationBundle configBundle = new ConfigurationBundle();
		ArgumentsBundle argsBundle = new ArgumentsBundle();

		// Add Conditions
		List selectedConditions = conditionsList.getSelectedValuesList();
		for (Object obj : selectedConditions)
		{
			Class condClass = (Class) obj;
			argsBundle.addCondition((Condition) condClass.newInstance());
		}

		// Add Ordering
		Class selectedOrdering = (Class) orderingSelection.getSelectedItem();
		if (selectedOrdering != Void.class)
		{
			Constructor declaredConstructor = selectedOrdering.getDeclaredConstructor(boolean.class);
			Comparator<Edge> edgeWeightComparator = (Comparator<Edge>) declaredConstructor
					.newInstance(aCheckBox.isSelected());
			argsBundle.putObject(ArgumentsBundle.COMMON_ARGS.EDGE_WEIGHT_COMPARATOR.toString(), edgeWeightComparator);
			configBundle.setOrderingColumn(selectedColumn);
		}

		// Set Preservative
		argsBundle.putBoolean(ArgumentsBundle.COMMON_ARGS.EDGE_PRESERVATION.toString(), pCheckBox.isSelected());

		// Select Algorithm
		List selectedAlgorithms = algorithmsList.getSelectedValuesList();

		Algorithm algo;
		if (selectedAlgorithms.size() == 1)
		{
			Class algoClass = (Class) selectedAlgorithms.get(0);
			Constructor constructor = algoClass.getConstructor(ArgumentsBundle.class);
			algo = (Algorithm) constructor.newInstance(argsBundle);

			// FIXME - add check for algorithm selected
		}
		else
		{
			ArrayList<Algorithm> bundledAlgos = new ArrayList<>();
			for (Object obj : selectedAlgorithms)
			{
				Class algoClass = (Class) obj;
				Constructor constructor = algoClass.getConstructor(ArgumentsBundle.class);
				bundledAlgos.add((Algorithm) constructor.newInstance(argsBundle));
			}
			algo = new Bundle(bundledAlgos.toArray(new Algorithm[0]));
		}

		int partiteNumber = 0;
		try
		{
			partiteNumber = Integer.parseInt(partiteField.getText());
		}
		catch (Exception e)
		{
			// do nothing
		}
		algo.setPartiteNumber(partiteNumber);

		int minimumNodeCount = Integer.parseInt(minNodeCountField.getText());
		algo.setMinNodeCount(minimumNodeCount);

		int orderIndex = sortGraphSelection.getSelectedIndex();
		if (orderIndex == 0)
		{
			// NONE
			algo.setGraphSortOrder(GraphSortOrder.NONE);
		}
		else if (orderIndex == 1)
		{
			// ASCENDING
			algo.setGraphSortOrder(GraphSortOrder.ASCENDING);
		}
		else if (orderIndex == 2)
		{
			// DESCENDING
			algo.setGraphSortOrder(GraphSortOrder.DESCENDING);
		}
		else if (orderIndex == 3)
		{
			algo.setGraphSortOrder(GraphSortOrder.AVERAGE_WEIGHT);
		}
		else
		{
			throw new IllegalArgumentException("Order index wasn't valid for some reason.");
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
	public void actionPerformed(ActionEvent e)
	{

		if (e.getSource() == doneButton)
		{
			String errorTitle = "Configuration Error";

			if (conditionsList.getSelectedValuesList().isEmpty())
			{
				JOptionPane.showMessageDialog(this, "You must select a condition to search for!", errorTitle,
						JOptionPane.ERROR_MESSAGE);
			}
			else if (algorithmsList.getSelectedValuesList().isEmpty())
			{
				JOptionPane.showMessageDialog(this, "You must select an algorithm to use in the search!", errorTitle,
						JOptionPane.ERROR_MESSAGE);
			}
			// error if minimum Node Count is not positive integer
			else if (!GraphTaskUtils.isInteger(minNodeCountField.getText())
					|| Integer.parseInt(minNodeCountField.getText()) < 1)
			{
				JOptionPane.showMessageDialog(this, "Minimum Node Count must be a positive integer!", errorTitle,
						JOptionPane.ERROR_MESSAGE);
			}
			else if (!iCheckBox.isSelected() && !nCheckBox.isSelected() && !sCheckBox.isSelected())
			{
				JOptionPane.showMessageDialog(this, "You must select a means of saving results!", errorTitle,
						JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				ActionEvent newEvent = null;

				try // try to get the configuration bundle
				{
					newEvent = new ActionEvent(getConfigurationBundle(), 0, "CONFIG_BUNDLE");
				}
				catch (Exception ex)
				{
					String description = "An error occurred trying to configure the feature.";
					ErrorPanel errorPanel = new ErrorPanel(description, ex);
					errorPanel.display(this, errorTitle);
				}
				finally
				{
					setVisible(false);
					if (newEvent != null)
					{
						successAction.actionPerformed(newEvent);
					}
					dispose();
				}
			}
		}
		else if (e.getSource() == nCheckBox) // enable/disable ordering for new child saving
		{
			sortGraphSelection.setEnabled(nCheckBox.isSelected());
		}
		else if (e.getSource() == sCheckBox && sCheckBox.isSelected())
		{
			JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fileChooser.showOpenDialog(this);

			if (returnVal != JFileChooser.APPROVE_OPTION)
			{
				sCheckBox.setSelected(false);
			}
			else
			{
				saveDirectory = fileChooser.getSelectedFile();
			}
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void itemStateChanged(ItemEvent e)
	{
		if (e.getItemSelectable() == orderingSelection && e.getStateChange() == ItemEvent.SELECTED
				&& orderingSelection.getSelectedIndex() != 0)
		{
			CyColumn response = (CyColumn) JOptionPane.showInputDialog(this,
					"Please select a column to use for ordering.", "Ordering Configuration",
					JOptionPane.QUESTION_MESSAGE, null, columns.toArray(new CyColumn[0]), columns.get(0));

			if (response == null)
			{
				orderingSelection.setSelectedIndex(0);
				aCheckBox.setEnabled(false);
			}
			else
			{
				selectedColumn = response;
				aCheckBox.setEnabled(true);
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// UI components
	//////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * GUI: Conditions Label. And toolTip text
	 */
	private JLabel cLabel = new JLabel("Search Condition ?*");
	private String cLabelInfo = "<html>Select One of the Given Search conditions:<p><br>"
			+ "1) Bi-Partite Condition 			-> When a node that belongs to one group has no direct\n<p>"
			+ "													connection to any other  node of the same group\n<p><br>"
			+ "2) Clique Condition	  			-> When every node is connected to every other node directly\n<p><br>"
			+ "3) Directed Clique Condtion	-> Similar to Clique, but this condition doesn't require there to be<p>"
			+ " 												 an edge from every node to every other node<p></html>";

	/**
	 * GUI: Algorithms Label. And toolTip text
	 */
	private JLabel aLabel = new JLabel("Search Algorithm ?*");
	private String aLabelInfo = "<html>Select One of the Given Search Algorithms: <p><br>"
			+ "1) Breath First Traversal Search ->  It starts at the tree root and explores<p>"
			+ " all of the neighbor nodes at the present depth prior to moving on to the nodes at the next depth level.<p><br>"
			+ "2) Depth First Traversal Search -> The algorithm starts at the root node and <p>"
			+ "explores as far as possible along each branch before backtracking</html>";

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

	protected void addSearchConfig(Insets insets)
	{
		cLabel.setToolTipText(cLabelInfo);
		aLabel.setToolTipText(aLabelInfo);

		add(cLabel, getConstraints(0, vertical_index, 2, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.PAGE_END,
				0, 0, insets));
		add(aLabel, getConstraints(2, vertical_index++, 2, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.PAGE_END, 0, 0, insets));
		add(cPane, getConstraints(0, vertical_index, 2, 4, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0,
				0, insets));
		add(aPane, getConstraints(2, vertical_index++, 2, 4, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER,
				0, 0, insets));
		vertical_index += 3;
	}

	/**
	 * GUI: Orderings Label.
	 * 
	 * And toolTip text
	 */
	private JLabel oLabel = new JLabel("Ordering ?*:");
	private String oLabelInfo = "<html>Select One of the Given Search Algorithms: <p><br>"
			+ "1) Void (Default Setting) -> No specific ordering<p><br>"
			+ "2) Edge Weight Comparator -> Compares the subgraphs based on:<p>"
			+ "2.1. Weight -> based on the average weight of edges in the specific subgraph<p>"
			+ "2.2. SUID -> based on order of nodes ";

	/**
	 * GUI: Combo box for selecting an ordering.
	 */
	private JComboBox orderingSelection;

	/**
	 * GUI: Checkbox for setting a comparison algorithm to sort in ascending order.
	 */
	private JCheckBox aCheckBox = new JCheckBox("Ascending Order");

	/**
	 * GUI: Checkbox for enabling "preservative" searching.
	 * 
	 * rapin001 on 2/10/20 Set the default value as true, so it's checked
	 * 
	 * tried parameter - if the user has made a choice already in case the
	 */
	private JLabel pCheckBoxLabel = new JLabel("Edge Preservative ?*");
	private JCheckBox pCheckBox = new JCheckBox("", true);
	private String pCheckBoxInfo = "<html>Select if the edge preservation is enabed :<p><br>"
			+ "Enabled -> Will abandon a node if any of its edges violates the condition<p><br>"
			+ "Disabled -> Will only abandon the edge that violates the condtion, not the whole node<p><br>"
			+ "Warning!<p>" + "1) If DISABLED, the results might be false or might display nothing<p>"
			+ "2) Most use comes when using with the Bi-Partite condition</html>";

	protected void addMiscConfig(Insets insets)
	{
		oLabel.setToolTipText(oLabelInfo);
		pCheckBoxLabel.setToolTipText(pCheckBoxInfo);

		add(oLabel, getConstraints(0, vertical_index++, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(orderingSelection, getConstraints(0, vertical_index++, 2, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.CENTER, 0, 0, insets));
		add(aCheckBox, getConstraints(0, vertical_index, 2, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.CENTER,
				0, 0, insets));
		add(new JSeparator(JSeparator.VERTICAL), getConstraints(2, vertical_index - 2, 1, 3, 1, 0,
				GridBagConstraints.BOTH, GridBagConstraints.LINE_START, 0, 0, insets));
		add(pCheckBoxLabel, getConstraints(3, vertical_index - 2, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(pCheckBox, getConstraints(3, vertical_index - 1, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.CENTER, 0, 0, insets));
		vertical_index++;
	}

	/**
	 * JTextField for selecting a k-partite number
	 */
	private JLabel partiteFieldLabel = new JLabel("K-partite number (optional, minimum 3) ?*");
	private JTextField partiteField = new JTextField();
	private String partiteFieldInfo = "<html>Specify the k-partie number: <p><br>"
			+ "1) Leave Blank -> No ordering is applied<p><br>"
			+ "2) Type the number which specifies the range of the partition number (0 to k)<p>"
			+ "-- Min number is 3</html>";

	protected void addPartiteConfig(Insets insets)
	{
		partiteFieldLabel.setToolTipText(partiteFieldInfo);

		add(partiteFieldLabel, getConstraints(0, vertical_index++, 2, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.CENTER, 0, 0, insets));
		add(partiteField, getConstraints(1, vertical_index++, 2, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.LINE_START, 0, 0, insets));
	}
}
