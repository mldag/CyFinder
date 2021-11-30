package edu.claflin.cyfinder.internal.ui.configdialog;

import static edu.claflin.cyfinder.internal.ui.GridBagBuilder.getConstraints;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Constructor;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.cytoscape.model.CyColumn;

import edu.claflin.cyfinder.internal.logic.ConfigurationBundle;
import edu.claflin.cyfinder.internal.tasks.utils.GraphTaskUtils;
import edu.claflin.cyfinder.internal.ui.ErrorPanel;
import edu.claflin.cyfinder.internal.ui.utils.ComboItem;
import edu.claflin.cyfinder.internal.ui.utils.FeatureConfig;
import edu.claflin.finder.algo.Algorithm;
import edu.claflin.finder.algo.Algorithm.GraphSortOrder;
import edu.claflin.finder.algo.ArgumentsBundle;

public class ConfigDialog<T extends Algorithm> extends JDialog implements ActionListener
{
	/**
	 * vertical index of components as they are added
	 */
	protected int vertical_index;

	/**
	 * The Algorithm Class given to the dialog
	 */
	protected Class<T> algoType;

	/**
	 * The action to execute upon completing the configuration.
	 */
	protected Action successAction;

	protected CyColumn[] edgeColumns;
	protected FeatureConfig fc;

	/**
	 * Constructor for subclasses to call the JDialog constructor
	 * 
	 * @param parent the window's parent
	 */
	public ConfigDialog(Frame parent, String name, boolean modal)
	{
		super(parent, name, modal);
		this.vertical_index = 0;
	}

	public ConfigDialog(Frame parent, String name, Action successAction, Class<T> algoType, FeatureConfig fc,
			CyColumn[] edgeColumns)
	{
		super(parent, name, true);

		this.vertical_index = 0;
		this.algoType = algoType;
		this.successAction = successAction;
		this.edgeColumns = edgeColumns;
		this.fc = fc;

		// listen to buttons
		nCheckBox.addActionListener(this);
		sCheckBox.addActionListener(this);
		wCheckBox.addActionListener(this);
		doneButton.addActionListener(this);

		weightAttributeSelection = new JComboBox<>(edgeColumns);

		sortGraphSelection.addItem(new ComboItem("None", 0));
		if (!fc.isTiedNodeCount())
		{
			sortGraphSelection.addItem(new ComboItem("Ascending", 1));
			sortGraphSelection.addItem(new ComboItem("Descending", 2));
		}
		sortGraphSelection.addItem(new ComboItem("Average Weight", 3));

		weightAttributeSelection.setEnabled(false);
		sortGraphSelection.setEnabled(false); // no sorting option if no new child saving

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(true);

		init();
	}

	/**
	 * Initializes and constructs the GUI.
	 */
	protected void init()
	{
		setLayout(new GridBagLayout());
		Insets insets = get_insets();

		if (fc.isWeightSelect() && edgeColumns.length >= 1 && fc.isMinNodeCount())
		{
			addWeightSelector(insets);
			addMinNodeCount(insets);
			add(new JSeparator(JSeparator.HORIZONTAL), getConstraints(0, vertical_index++, 4, 1, 1, 0,
					GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));
		}
		else if (fc.isWeightSelect() && edgeColumns.length >= 1)
		{
			addWeightSelector(insets);
			add(new JSeparator(JSeparator.HORIZONTAL), getConstraints(0, vertical_index++, 4, 1, 1, 0,
					GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));
		}
		else if (fc.isMinNodeCount())
		{
			addMinNodeCount(insets);
			add(new JSeparator(JSeparator.HORIZONTAL), getConstraints(0, vertical_index++, 4, 1, 1, 0,
					GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));
		}

		if (fc.isOrdered())
		{
			addOrderedSaveOptions(insets);
		}
		else
		{
			addSaveOptions(insets);
		}

		add(new JSeparator(JSeparator.HORIZONTAL), getConstraints(0, vertical_index++, 4, 1, 1, 0,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));
		addDoneButton(insets);

		pack();
	}

	/**
	 * Creates a ConfigurationBundle object holding the configuration as defined by
	 * the user.
	 * 
	 * @return the ConfigurationBundle.
	 * @throws Exception should a problem with reflection occur.
	 */
	public ConfigurationBundle getConfigurationBundle() throws Exception
	{
		ConfigurationBundle configBundle = new ConfigurationBundle();

		if (algoType != null)
		{
			ArgumentsBundle argsBundle = new ArgumentsBundle();
			argsBundle.putBoolean("weighted", wCheckBox.isSelected());

			Class[] args = { ArgumentsBundle.class };
			Constructor<T> constructor = algoType.getDeclaredConstructor(args);
			T algo = constructor.newInstance(argsBundle);

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

			if (wCheckBox.isSelected())
			{
				String weightName = ((CyColumn) weightAttributeSelection.getSelectedItem()).getName();
				algo.setWeightName(weightName);
				configBundle.setWeightAttribute(weightName);
			}

			configBundle.setAlgo(algo);
		}

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

			// error if minimum Node Count is not positive integer
			if (!GraphTaskUtils.isInteger(minNodeCountField.getText())
					|| Integer.parseInt(minNodeCountField.getText()) < 1)
			{
				JOptionPane.showMessageDialog(this, "Minimum Node Count must be a positive integer!", errorTitle,
						JOptionPane.ERROR_MESSAGE);
			}
			// error if no save option was selected
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
		else if (e.getSource() == wCheckBox) // enable/disable ordering for new child saving
		{
			weightAttributeSelection.setEnabled(wCheckBox.isSelected());
		}
		else if (e.getSource() == nCheckBox) // enable/disable ordering for new child saving
		{
			sortGraphSelection.setEnabled(nCheckBox.isSelected());
		}
		else if (e.getSource() == sCheckBox && sCheckBox.isSelected()) // get file path for file saving
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

	//////////////////////////////////////////////////////////////////////////////////////////////
	// GUI general
	//////////////////////////////////////////////////////////////////////////////////////////////

	protected Insets get_insets()
	{
		return new Insets(2, 2, 2, 2);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Save Options
	//////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * GUI: Label for the save subgraph section of Gui
	 * 
	 * rapin001
	 */
	protected JLabel saveGraphOption = new JLabel("Display/storing options ?*");
	protected String saveGraphOptionInfo = "<html>Select display/save method for generated sub-graphs"
			+ "<p>Can select more than one of the following options<p><br>"
			+ "1) In-Place annotation of source graph -> generates a truth table for each subgraph showing which nodes are included<p><br>"
			+ "2) New Child Graph beneath source graph -> generate and display separate subgraphs based on the source graph in Cytoscape<p><br>"
			+ "3) Save found subgraph to file -> save generated subgraphs to file <br> -generates .txt file for each subgraph specifying<p></html>";
	/**
	 * GUI: Checkbox for enabling in-place annotation.
	 */
	protected JCheckBox iCheckBox = new JCheckBox("In-Place annotation of source graph.");
	/**
	 * GUI: Checkbox for enabling new child creation.
	 */
	protected JCheckBox nCheckBox = new JCheckBox("New Child Graph beneath source graph.");
	/**
	 * GUI: Checkbox for enabling saving to file.
	 */
	protected JCheckBox sCheckBox = new JCheckBox("Save found subgraph to file.");

	/**
	 * GUI: Button to complete configuration.
	 */
	protected JButton doneButton = new JButton("Done");

	/**
	 * The File object indicating the directory to save subgraphs in.
	 */
	protected File saveDirectory = null;

	/**
	 * Add label to instruction on how to get more info rapin001
	 */
	protected JLabel helpLabel = new JLabel("Need Help?*");
	protected String helpInfo = "<html>For more information about the available options and features,<p> position your mouse cursor over the  ->\"?*\"<-  symbol</html>";

	protected void addSaveOptions(Insets insets)
	{
		saveGraphOption.setToolTipText(saveGraphOptionInfo);

		add(saveGraphOption, getConstraints(0, vertical_index++, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(iCheckBox, getConstraints(0, vertical_index++, 4, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.CENTER, 0, 0, insets));
		add(nCheckBox, getConstraints(0, vertical_index++, 4, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.CENTER, 0, 0, insets));
		add(sCheckBox, getConstraints(0, vertical_index++, 4, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.CENTER, 0, 0, insets));
	}

	protected void addDoneButton(Insets insets)
	{
		helpLabel.setToolTipText(helpInfo);

		add(doneButton, getConstraints(2, vertical_index, 2, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.LINE_END, 0, 0, insets));
		add(helpLabel, getConstraints(0, vertical_index++, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.LINE_START, 0, 0, insets));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Ordered Save Options
	//////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * GUI: JComboBox for selecting a sort method for the graph outputs by size
	 */
	protected JLabel sortGraphSelectionLabel = new JLabel("Save Order ?*");
	protected String sortGraphSelectionInfo = "<html>Select the order by which to display the subgraphs based on node count/average weight<p><br>"
			+ "1) None ->  no sorting order (Default option)<p>" + "2) Ascending -> low to high node count<p>"
			+ "3) Descending -> high to low node count<p>"
			+ "4) Average Weight -> based on average weight of subgraph</html>";
	protected JComboBox<ComboItem> sortGraphSelection = new JComboBox<>();

	protected void addOrderedSaveOptions(Insets insets)
	{
		saveGraphOption.setToolTipText(saveGraphOptionInfo);
		sortGraphSelectionLabel.setToolTipText(sortGraphSelectionInfo);

		add(saveGraphOption, getConstraints(0, vertical_index++, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(iCheckBox, getConstraints(0, vertical_index++, 4, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.CENTER, 0, 0, insets));
		add(nCheckBox, getConstraints(0, vertical_index++, 4, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.CENTER, 0, 0, insets));
		add(sortGraphSelectionLabel, getConstraints(0, vertical_index, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.CENTER, 0, 0, insets));
		add(sortGraphSelection, getConstraints(1, vertical_index++, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.LINE_END, 0, 0, insets));
		add(sCheckBox, getConstraints(0, vertical_index++, 4, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.CENTER, 0, 0, insets));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// General Configuration
	//////////////////////////////////////////////////////////////////////////////////////////////

	// Weight Attribute
	protected JCheckBox wCheckBox = new JCheckBox("Use Weight?");
	protected JLabel weightAttributeLabel = new JLabel("Select Weight attribute *");
	protected String weightAttributeInfo = "<html>Select an attribute to use as a weight <br/> If unselected one will be chosen or 0 will be used if no numeric attributes exist</html>";
	protected JComboBox<CyColumn> weightAttributeSelection = new JComboBox<>();

	protected void addWeightSelector(Insets insets)
	{
		weightAttributeLabel.setToolTipText(weightAttributeInfo);

		add(wCheckBox, getConstraints(0, vertical_index++, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(weightAttributeLabel, getConstraints(0, vertical_index, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(weightAttributeSelection, getConstraints(1, vertical_index++, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.LINE_END, 0, 0, insets));
	}

	// Minimum Node count for output
	protected JLabel minNodeCountLabel = new JLabel("Minimum Node Count *");
	protected String minNodeCountInfo = "<html>Subnetworks with lower Node count are removed from the output</html>";
	protected JTextField minNodeCountField = new JTextField("2");

	protected void addMinNodeCount(Insets insets)
	{
		minNodeCountLabel.setToolTipText(minNodeCountInfo);

		add(minNodeCountLabel, getConstraints(0, vertical_index, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(minNodeCountField, getConstraints(1, vertical_index++, 2, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.LINE_END, 0, 0, insets));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// End of GUI
	//////////////////////////////////////////////////////////////////////////////////////////////
}
