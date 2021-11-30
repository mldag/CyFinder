package edu.claflin.cyfinder.internal.ui.configdialog;

import static edu.claflin.cyfinder.internal.ui.GridBagBuilder.getConstraints;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import edu.claflin.cyfinder.internal.tasks.utils.GraphTaskUtils;
import edu.claflin.cyfinder.internal.ui.ErrorPanel;

public class IntersectConfigDialog extends ConfigDialog
{

	private boolean collections;

	/**
	 * The action to execute upon completing the configuration.
	 */
	private Action successAction;

	public IntersectConfigDialog(Frame parent, Action successAction, String[] columnNames, boolean collections)
	{
		super(parent, "Select " + (collections ? "Collections " : "Networks ") + "to Intersect", true);
		this.successAction = successAction;
		this.collections = collections;

		Arrays.sort(columnNames);

		cb1 = new JComboBox<>(columnNames);
		cb2 = new JComboBox<>(columnNames);

		// listen to buttons
		nCheckBox.addActionListener(this);
		sCheckBox.addActionListener(this);
		doneButton.addActionListener(this);

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
		Insets insets = getInsets();

		addNetworksSelector(insets);
		add(new JSeparator(JSeparator.HORIZONTAL), getConstraints(0, vertical_index++, 4, 1, 1, 0,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));

		if (collections)
		{
			addMinNodeCount(insets);
			add(new JSeparator(JSeparator.HORIZONTAL), getConstraints(0, vertical_index++, 4, 1, 1, 0,
					GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));
		}

		addSaveOptions(insets);
		add(new JSeparator(JSeparator.HORIZONTAL), getConstraints(0, vertical_index++, 4, 1, 1, 0,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));
		addDoneButton(insets);

		pack();
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

			// error if no selection
			if (cb1.getSelectedItem() == null || cb2.getSelectedItem() == null)
			{
				JOptionPane.showMessageDialog(this, "You must select 2 Networks to compare!", errorTitle,
						JOptionPane.ERROR_MESSAGE);
			}
			// error if intersecting with self
			else if (cb1.getSelectedItem().equals(cb2.getSelectedItem()))
			{
				JOptionPane.showMessageDialog(this,
						"Should not intersect " + (collections ? "Collection " : "Network ") + "with itself!",
						errorTitle, JOptionPane.ERROR_MESSAGE);
			}
			// error if minimum Node Count is not positive integer
			else if (!GraphTaskUtils.isInteger(minNodeCountField.getText())
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

				try
				{
					String[] info = { (String) cb1.getSelectedItem(), (String) cb2.getSelectedItem(),
							minNodeCountField.getText() };
					Object[] tuple = { getConfigurationBundle(), info };
					newEvent = new ActionEvent(tuple, 0, "NETWORK_TUPLE"); // event emitted by {ConfigBundle,
																			// intersection config}
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

	// UI components

	private JLabel SourceNetworkL = new JLabel("Network 1");
	private JLabel TargetNetworkL = new JLabel("Network 2");

	private JComboBox<String> cb1;
	private JComboBox<String> cb2;

	protected void addNetworksSelector(Insets insets)
	{
		add(SourceNetworkL, getConstraints(0, vertical_index, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(cb1, getConstraints(1, vertical_index++, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.LINE_END,
				0, 0, insets));
		add(TargetNetworkL, getConstraints(0, vertical_index, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(cb2, getConstraints(1, vertical_index++, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.LINE_END,
				0, 0, insets));
	}
}
