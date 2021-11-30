package edu.claflin.cyfinder.internal.ui.configdialog;

import static edu.claflin.cyfinder.internal.ui.GridBagBuilder.getConstraints;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;

import javax.swing.Action;
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

public class WalktrapConfigDialog<T extends Algorithm> extends ConfigDialog<T>
{
	/**
	 * {@inheritDoc}
	 */
	public WalktrapConfigDialog(Frame parent, String name, boolean modal)
	{
		super(parent, name, modal);
		this.vertical_index = 0;
	}
	
	public WalktrapConfigDialog(Frame parent, Action successAction, Class<T> algoType, FeatureConfig fc,
			CyColumn[] edgeColumns)
	{
		super(parent, "Configure Walktrap", true);

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
	 * {@inheritDoc}
	 */
	@Override
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

		addWalkLength(insets);
		add(new JSeparator(JSeparator.HORIZONTAL), getConstraints(0, vertical_index++, 4, 1, 1, 0,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));

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
	 * {@inheritDoc}
	 */
	@Override
	public ConfigurationBundle getConfigurationBundle() throws Exception
	{
		ConfigurationBundle configBundle = new ConfigurationBundle();

		if (algoType != null)
		{
			ArgumentsBundle argsBundle = new ArgumentsBundle();
			argsBundle.putBoolean("weighted", wCheckBox.isSelected());
			argsBundle.putInteger("steps", Integer.parseInt(walkLengthField.getText()));

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
			// error if walk length is not positive integer
			else if (!GraphTaskUtils.isInteger(walkLengthField.getText())
					|| Integer.parseInt(walkLengthField.getText()) < 1)
			{
				JOptionPane.showMessageDialog(this, "Walk Length must be a positive integer!", errorTitle,
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

	// Minimum Node count for output
	protected JLabel walkLengthLabel = new JLabel("Walk Length *");
	protected String walkLengthInfo = "<html>Length of the Walk</html>";
	protected JTextField walkLengthField = new JTextField("4");

	protected void addWalkLength(Insets insets)
	{
		walkLengthLabel.setToolTipText(walkLengthInfo);

		add(walkLengthLabel, getConstraints(0, vertical_index, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(walkLengthField, getConstraints(1, vertical_index++, 2, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.LINE_END, 0, 0, insets));
	}
}
