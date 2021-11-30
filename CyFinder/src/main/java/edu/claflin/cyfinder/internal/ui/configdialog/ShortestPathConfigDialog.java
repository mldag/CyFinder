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
import java.util.HashSet;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import edu.claflin.cyfinder.internal.logic.ConfigurationBundle;
import edu.claflin.cyfinder.internal.ui.ErrorPanel;
import edu.claflin.cyfinder.internal.ui.utils.ComboItem;
import edu.claflin.finder.algo.Algorithm;
import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.algo.shortestpath.DijkstraShortestPath;
import edu.claflin.finder.algo.shortestpath.ShortestPath;

/**
 * Represents a configuration dialog for the Maximum Clique and Maximum Biclique
 * Tasks. It is a subset of ConfigDialog in that it only includes save options
 * and always uses the BronKersboch Algorithm.
 */
public class ShortestPathConfigDialog extends ConfigDialog
{
	/**
	 * Whether to look for Bipartite Graphs or not.
	 */
	private final HashSet<String> nodeList;

	// UI components

	/**
	 * GUI: JComboBox for selecting a sort method for the graph outputs by size
	 */
	private JLabel algorithmSelectionLabel = new JLabel("Select Search Algorithm ?*");
	private String algorithmSelectionInfo = "<html><p></p>Select which algorithm will be used to find the shortest path</html>";
	private JComboBox algorithmSelection = new JComboBox();

	/**
	 * GUI: Label for the origin node section of Gui
	 *
	 */
	private JTextField fromNode;
	private JLabel fromLabel = new JLabel("From Node: ");
	private String fromLabelInfo = "<html>" + "<p>Enter the name of the origin node.<p>" + "</html>";

	/**
	 * GUI: Label for the destination node section of Gui
	 *
	 *
	 */
	private JTextField toNode;
	private JLabel toLabel = new JLabel("To Node: ");
	private String toLabelInfo = "<html>" + "<p>Enter the name of the destination node.<p>" + "</html>";

	// ----------------------------------------------------------------------------------------------------

	/**
	 * Constructor for initializing the Panel.
	 */
	public ShortestPathConfigDialog(Frame parent, Action successAction, HashSet<String> nodeList)
	{
		super(parent, "Configure Shortest Path", true);
		this.successAction = successAction;
		this.nodeList = nodeList;

		sCheckBox.addActionListener(this);

		doneButton.addActionListener(this);

		
		algorithmSelection.addItem(new ComboItem("Dijkstra's", 0));
		algorithmSelection.addItem(new ComboItem("Shortest Path v1.0.0", 1));

		fromNode = new JTextField();
		toNode = new JTextField();

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

		//
		saveGraphOption.setToolTipText(saveGraphOptionInfo);
		algorithmSelectionLabel.setToolTipText(algorithmSelectionInfo);
		helpLabel.setToolTipText(helpInfo);

		setLayout(new GridBagLayout());
		Insets insets = new Insets(4, 4, 4, 4);
		add(algorithmSelectionLabel, getConstraints(0, 10, 4, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(algorithmSelection, getConstraints(0, 12, 4, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(fromLabel, getConstraints(0, 14, 2, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.LINE_START, 0, 0,
				insets));
		add(fromNode,
				getConstraints(1, 14, 2, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.LINE_END, 0, 0, insets));
		add(toLabel, getConstraints(0, 16, 2, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.LINE_START, 0, 0,
				insets));
		add(toNode,
				getConstraints(1, 16, 2, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.LINE_END, 0, 0, insets));
		add(new JSeparator(JSeparator.HORIZONTAL),
				getConstraints(0, 18, 4, 1, 1, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));
		add(saveGraphOption, getConstraints(0, 20, 4, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.LINE_START,
				0, 0, insets));
		add(iCheckBox,
				getConstraints(0, 22, 4, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));
		add(nCheckBox,
				getConstraints(0, 24, 4, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));
		add(sCheckBox,
				getConstraints(0, 26, 4, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));
		add(new JSeparator(JSeparator.HORIZONTAL),
				getConstraints(0, 28, 4, 1, 1, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));
		add(doneButton,
				getConstraints(2, 30, 2, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.LINE_END, 0, 0, insets));
		add(helpLabel, getConstraints(0, 30, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.LINE_START, 0, 0,
				insets));
		pack();
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
		argsBundle.putObject("fromNode", fromNode.getText());
		argsBundle.putObject("toNode", toNode.getText());
		Algorithm algo;

		int orderIndex = algorithmSelection.getSelectedIndex();
		if (orderIndex == 0)
		{
			// Shortest Path v1.0.0
			algo = new DijkstraShortestPath(argsBundle);
			
		}
		else if (orderIndex == 1)
		{
			algo = new ShortestPath(argsBundle);
		}
		else
		{
			throw new IllegalArgumentException("Algorithms index wasn't valid for some reason.");
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
	 * checks to see if a node name string is in the graph nodelist
	 *
	 * @return true if the graph nodelist contains that node name
	 *         false if not
	 * 
	 */
	private boolean checkNode(String nodeName) 
	{
		return nodeList.contains(nodeName);
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
			if (!iCheckBox.isSelected() && !nCheckBox.isSelected() && !sCheckBox.isSelected())
			{
				JOptionPane.showMessageDialog(this, "You must select a means of saving results!", errorTitle,
						JOptionPane.ERROR_MESSAGE);
			}
			else if (fromNode.getText().equals("") || toNode.getText().equals(""))
			{
				JOptionPane.showMessageDialog(this, "You must select a origin node and a destination node.", errorTitle,
						JOptionPane.ERROR_MESSAGE);
			}
			else if(!checkNode(fromNode.getText()))
			{
				JOptionPane.showMessageDialog(this, "Your source node does not exist in the selected graph.", errorTitle,
						JOptionPane.ERROR_MESSAGE);
			}
			else if(!checkNode(toNode.getText()))
			{
				JOptionPane.showMessageDialog(this, "Your destination node does not exist in the selected graph.", errorTitle,
						JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				ActionEvent newEvent = null;
				try
				{
					newEvent = new ActionEvent(getConfigurationBundle(), 0, "CONFIG_BUNDLE");
				}
				catch (Exception ex)
				{
					String description = "An error occurred trying to create " + "the ConfigurationBundle.";
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
}
