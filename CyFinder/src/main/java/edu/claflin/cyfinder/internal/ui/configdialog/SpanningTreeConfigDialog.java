package edu.claflin.cyfinder.internal.ui.configdialog;

import static edu.claflin.cyfinder.internal.ui.GridBagBuilder.getConstraints;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.cytoscape.model.CyColumn;

import edu.claflin.cyfinder.internal.logic.ConfigurationBundle;
import edu.claflin.cyfinder.internal.tasks.utils.GraphTaskUtils;
import edu.claflin.cyfinder.internal.ui.ErrorPanel;
import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.algo.spanningtree.ExtremumSpanningTree;
import edu.claflin.finder.logic.Node;

public class SpanningTreeConfigDialog<T extends ExtremumSpanningTree> extends ConfigDialog
{
	protected Node[] nodes;
	protected boolean prim;
	
	/**
	 * Constructor for subclasses to call the JDialog constructor
	 * 
	 * @param parent the window's parent
	 */
	public SpanningTreeConfigDialog(Frame parent, String name, boolean modal)
	{
		super(parent, name, modal);
		nodes = new Node[0];
		this.vertical_index = 0;
	}

	public SpanningTreeConfigDialog(Frame parent, String name, Action successAction, Class<T> algoType, Node[] nodes,
			CyColumn[] edgeColumns)
	{
		super(parent, name, true);

		this.vertical_index = 0;
		this.algoType = algoType;
		this.successAction = successAction;
		this.nodes = nodes;
		this.edgeColumns = edgeColumns;
		prim = name.contains("Prim");

		// listen to buttons
		nCheckBox.addActionListener(this);
		sCheckBox.addActionListener(this);
		tCheckBox.addActionListener(this);
		doneButton.addActionListener(this);

		weightAttributeSelection = new JComboBox<>(edgeColumns);
		startNodeSelection = new JComboBox<>(nodes);

		jthreshold.setEnabled(false);

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

		if (edgeColumns.length >= 1)
		{
			addWeightSelector(insets);
		}
		addMinNodeCount(insets);
		addMinMax(insets);
		addThreshold(insets);

		if (prim && nodes.length >= 1)
		{
			addStartNode(insets);
		}

		add(new JSeparator(JSeparator.HORIZONTAL), getConstraints(0, vertical_index++, 4, 1, 1, 0,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, insets));

		addSaveOptions(insets);

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
	@Override
	public ConfigurationBundle getConfigurationBundle() throws Exception
	{
		ConfigurationBundle configBundle = new ConfigurationBundle();

		if (algoType != null)
		{
			ArgumentsBundle argsBundle = new ArgumentsBundle();

			boolean max = jmax.isSelected();
			argsBundle.putBoolean("max", max);

			if (prim && nodes.length >= 1)
			{
				argsBundle.putObject("startNode", startNodeSelection.getSelectedItem().toString());
			}

			if (tCheckBox.isSelected())
			{
				argsBundle.putDouble("threshold", Double.parseDouble(jthreshold.getText()));
			}

			Class[] args = { ArgumentsBundle.class };
			Constructor<T> constructor = algoType.getDeclaredConstructor(args);
			T algo = constructor.newInstance(argsBundle);

			int minimumNodeCount = Integer.parseInt(minNodeCountField.getText());
			algo.setMinNodeCount(minimumNodeCount);

			if (edgeColumns.length >= 1)
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
			// error if threshold value is not a double
			else if (!GraphTaskUtils.isDouble(jthreshold.getText()))
			{
				JOptionPane.showMessageDialog(this, "Threshold must be a Double!", errorTitle,
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
		else if (e.getSource() == tCheckBox)
		{
			jthreshold.setEnabled(tCheckBox.isSelected());
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
	// GUI
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Weight

	@Override
	protected void addWeightSelector(Insets insets)
	{
		weightAttributeLabel.setToolTipText(weightAttributeInfo);
		
		add(weightAttributeLabel, getConstraints(0, vertical_index, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(weightAttributeSelection, getConstraints(1, vertical_index++, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.LINE_END, 0, 0, insets));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Maximum or Minimum Spanning Tree
	//////////////////////////////////////////////////////////////////////////////////////////////

	protected JLabel minmaxLabel = new JLabel("Find Minimum or Maximum:");
	ButtonGroup maxmin = new ButtonGroup();
	JRadioButton jmin = new JRadioButton("Minimum Spanning Tree", true);
	JRadioButton jmax = new JRadioButton("Maximum Spanning Tree");

	protected void addMinMax(Insets insets)
	{
		maxmin.add(jmin);
		maxmin.add(jmax);
		add(minmaxLabel, getConstraints(0, vertical_index, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(jmin, getConstraints(1, vertical_index, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0,
				0, insets));
		add(jmax, getConstraints(2, vertical_index++, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.LINE_END,
				0, 0, insets));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Threshold
	//////////////////////////////////////////////////////////////////////////////////////////////

	protected JCheckBox tCheckBox = new JCheckBox("Use a Threshold?*");
	protected String thresholdInfo = "<html>Set an Edge weight threshold: <br/> Minimum Edge weight when finding Maximum Spanning Tree <br/> Maximum Edge weight when finding Minimum Spanning Tree</html>";
	protected JTextField jthreshold = new JTextField("0");

	protected void addThreshold(Insets insets)
	{
		tCheckBox.setToolTipText(thresholdInfo);

		add(tCheckBox, getConstraints(0, vertical_index, 1, 1, 1, 1, GridBagConstraints.NONE,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(jthreshold, getConstraints(1, vertical_index++, 2, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.LINE_END, 0, 0, insets));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Start Node
	//////////////////////////////////////////////////////////////////////////////////////////////
	protected JLabel startNodeLabel = new JLabel("Select the starting Node for the search *");
	protected String startNodeInfo = "<html>Sets the starting Node for the Prim search</html>";
	protected JComboBox<Node> startNodeSelection = new JComboBox<Node>();

	protected void addStartNode(Insets insets)
	{
		startNodeLabel.setToolTipText(startNodeInfo);
		add(startNodeLabel, getConstraints(0, vertical_index, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.LINE_START, 0, 0, insets));
		add(startNodeSelection, getConstraints(1, vertical_index++, 1, 1, 1, 1, GridBagConstraints.BOTH,
				GridBagConstraints.LINE_END, 0, 0, insets));
	}
}
