package edu.claflin.cyfinder.internal.tasks.config;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * Represents a Global Configuration Task for configuring all CyFinder
 * libraries. Configures all CyFinder libraries.
 *
 * @author Juan C Ibarra Cuza
 * @version 1.0.0 March 12, 2021
 */
public class ConfigurationTask extends AbstractTask
{

	/**
	 * The parent window to exhibit modality over.
	 */
	private final Frame parent;

	/**
	 * The name of the feature.
	 */
	private final String name;
	/**
	 * The configuration dialog reference.
	 */
	private JDialog cDialog;

	/**
	 * Constructs the ConfigurationTask.
	 *
	 * @param network      the CyNetwork to analyze.
	 * @param parent       the parent window to exhibit modality over.
	 * @param configDialog the configuration dialog reference.
	 * @param name         the name of the feature.
	 */
	public ConfigurationTask(Frame parent, JDialog configDialog, String name)
	{
		super();
		this.parent = parent;
		this.cDialog = configDialog;
		this.name = name;
	}

	/**
	 * {@inheritDoc }
	 *
	 * @param taskMonitor the TaskMonitor for reporting progress. Unused.
	 */
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception
	{
		taskMonitor.setTitle(name + " Configuration Dialog.");
		taskMonitor.setStatusMessage("Initializing...");
		taskMonitor.setProgress(0D);

		SwingUtilities.invokeLater(() -> {
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
	public void cancel()
	{
		if (cDialog != null)
		{
			cDialog.setVisible(false);
			cDialog.dispose();
		}
	}
}