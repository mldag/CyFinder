package edu.claflin.cyfinder.internal.tasks.utils;

import static edu.claflin.cyfinder.internal.Global.getNetworkManagerService;
import static edu.claflin.cyfinder.internal.Global.getRootNetworkService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.work.TaskMonitor;

import edu.claflin.cyfinder.internal.logic.ConfigurationBundle;
import edu.claflin.cyfinder.internal.logic.CyEdgeAdapter;
import edu.claflin.cyfinder.internal.logic.CyEdgeViewAdapter;
import edu.claflin.cyfinder.internal.logic.CyNodeAdapter;
import edu.claflin.cyfinder.internal.logic.CyNodeViewAdapter;
import edu.claflin.finder.io.graph.SimpleGraphIO;
import edu.claflin.finder.io.graph.sub.GraphWriter;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;

public class GraphTaskUtils
{

	public static void showInfo(final String title, final String message) 
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
	
	/**
	 * Shows an Error message.
	 * 
	 * @param message the error message
	 * @param title   the error title
	 */
	public static void showError(final String title, final String message)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			}
		});

	}

	public static Graph convertCyNetwork(CyNetwork network)
	{
		return convertCyNetwork(network, null);
	}	

	/**
	 * Converts a CyNetwork to a finder Graph
	 * 
	 * @param network the cynetwork to convert
	 * @param weightColumnName the name of the numeric attribute to read weights from
	 * @return the Graph containing the new network.
	 */
	public static Graph convertCyNetwork(CyNetwork network, String weightColumnName)
	{
		Graph returnGraph = new Graph(network.toString());
		network.getNodeList().stream().forEach(cynode -> {
			String name = network.getDefaultNodeTable().getRow(cynode.getSUID()).get("shared name", String.class);
			CyNodeAdapter node = new CyNodeAdapter(cynode, name);
			returnGraph.addNode(node);
		});

		network.getEdgeList().stream().forEach(cyedge -> {
			String nodeName1 = network.getDefaultNodeTable().getRow(cyedge.getSource().getSUID()).get("shared name",
					String.class);
			CyNodeAdapter node1 = (CyNodeAdapter) returnGraph.getNodeList().stream()
					.filter(anode -> anode.getIdentifier().equals(nodeName1)).toArray()[0];

			String nodeName2 = network.getDefaultNodeTable().getRow(cyedge.getTarget().getSUID()).get("shared name",
					String.class);
			CyNodeAdapter node2 = (CyNodeAdapter) returnGraph.getNodeList().stream()
					.filter(anode -> anode.getIdentifier().equals(nodeName2)).toArray()[0];

			Double data = getEdgeData(network, cyedge, weightColumnName);

			CyEdgeAdapter edge = new CyEdgeAdapter(node1, node2, data, cyedge);
			returnGraph.addEdge(edge);

			// Add second edge if undirected.
//			if (!cyedge.isDirected())
//			{
//				edge = new CyEdgeAdapter(node2, node1, data, cyedge);
//				returnGraph.addEdge(edge);
//			}
		});
		return returnGraph;
	}

	/**
	 * Converts a CyNetworkView to a finder Graph.
	 * 
	 * @param network the cynetwork to convert
	 * @return the Graph containing the new network.
	 */
	public static Graph convertCyNetworkView(CyNetworkView networkview)
	{
		//CyNetwork network = networkview.getModel();
		Graph returnGraph = new Graph(networkview.toString());
		networkview.getNodeViews().stream().forEach(cynode -> {
			String name = networkview.getModel().getDefaultNodeTable().getRow(cynode.getModel().getSUID()).get("shared name", String.class);
			CyNodeViewAdapter node = new CyNodeViewAdapter(cynode, name);
			returnGraph.addNode(node);
		});

		networkview.getEdgeViews().stream().forEach(cyedge -> {
			String nodeName1 = networkview.getModel().getDefaultNodeTable().getRow(cyedge.getModel().getSource().getSUID()).get("shared name",
					String.class);
			CyNodeViewAdapter node1 = (CyNodeViewAdapter) returnGraph.getNodeList().stream()
					.filter(anode -> anode.getIdentifier().equals(nodeName1)).toArray()[0];

			String nodeName2 = networkview.getModel().getDefaultNodeTable().getRow(cyedge.getModel().getTarget().getSUID()).get("shared name",
					String.class);
			CyNodeViewAdapter node2 = (CyNodeViewAdapter) returnGraph.getNodeList().stream()
					.filter(anode -> anode.getIdentifier().equals(nodeName2)).toArray()[0];

			Double data = getEdgeData(networkview.getModel(), cyedge.getModel(), null);

			CyEdgeViewAdapter edge = new CyEdgeViewAdapter(node1, node2, data, cyedge);
			returnGraph.addEdge(edge);

			// Add second edge if undirected.
//			if (!cyedge.isDirected())
//			{
//				edge = new CyEdgeAdapter(node2, node1, data, cyedge);
//				returnGraph.addEdge(edge);
//			}
		});
		return returnGraph;
	}
	/**
	 * Saves the results into the specified output modes.
	 * 
	 * @param taskMonitor the task's monitor
	 * @param subgraphs   the graphs to save
	 */
	public static void saveSubGraphs(final TaskMonitor taskMonitor, List<Graph> subgraphs, CyNetwork network,
			ConfigurationBundle config)
	{

		taskMonitor.setStatusMessage("Saving subgraphs...");
		// taskMonitor.setStatusMessage(LogUtil.);
		taskMonitor.setProgress(0D);
		int operationCount = 0;
		if (config.isInPlace())
			operationCount++;
		if (config.isNewChild())
			operationCount++;
		if (config.isSaveToFile())
			operationCount++;
		int completedOperations = 0;

		// Add inplace annotations.
		if (config.isInPlace())
		{
			int count = 0;
			for (Graph graph : subgraphs)
			{
				String name = graph.getName();

				network.getDefaultNodeTable().createColumn(name, Boolean.class, false, false);
				network.getDefaultEdgeTable().createColumn(name, Boolean.class, false, false);

				for (Node node : graph.getNodeList())
				{
					CyNodeAdapter anode = (CyNodeAdapter) node;
					network.getDefaultNodeTable().getRow(anode.getCyNode().getSUID()).set(name, true);
				}
				for (Edge edge : graph.getEdgeList())
				{
					CyEdgeAdapter aedge = (CyEdgeAdapter) edge;
					network.getDefaultEdgeTable().getRow(aedge.getCyEdge().getSUID()).set(name, true);
				}
				taskMonitor.setProgress(1D * count * completedOperations / subgraphs.size() / operationCount);
			}
			taskMonitor.setProgress(1D * ++completedOperations / operationCount);
		}

		// Add new graphs to collection
		if (config.isNewChild())
		{
			CyRootNetworkManager rootManager = getRootNetworkService();
			CyRootNetwork root = rootManager.getRootNetwork(network);
			int count = 0;

			for (Graph graph : subgraphs)
			{
				String name = graph.getName();
				CySubNetwork sub = root.addSubNetwork();
				sub.getRow(sub).set(CySubNetwork.NAME, name);

				/*
				 * Evyatar & Ariel- adding a new column "Groups" and mapping UI Node to each
				 * Node in the Communicator.getSingleton().groups to give it the correct group,
				 * A or B
				 */
//				sub.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS).createColumn("group", String.class, false);
//				sub.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS).createColumn("partition number", String.class, false);				
//				Map<String, CygrouperNode> m;
//
//				try
//				{
//					m = Communicator.getSingleton().groups.get(count - 1);
//				}
//				catch (IndexOutOfBoundsException e)
//				{
//					m = null;
//				}

				ArrayList<Node> addedNodes = new ArrayList<>();

				for (Node node : graph.getNodeList())
				{
					CyNodeAdapter anode = (CyNodeAdapter) node;
					sub.addNode(anode.getCyNode());
					addedNodes.add(node);

					// partition number (Spring 2019)
//					try 
//					{
//						CyRow row = sub.getDefaultNodeTable().getRow(anode.getCyNode().getSUID());
//						CygrouperNode grp = m.get(row.get("name", String.class));
//						row.set("group", grp.group);
//						row.set("partition number", grp.kPartiteGroupNumber + "");
//
//					}
//					catch (NullPointerException e)
//					{
//
//					}
//					catch (IndexOutOfBoundsException e)
//					{
//
//					}
//					catch (Exception e)
//					{
//
//					}

				}
				graph.getEdgeList().stream().forEach(edge -> {
					CyEdgeAdapter aedge = (CyEdgeAdapter) edge;
					sub.addEdge(aedge.getCyEdge());
				});
				getNetworkManagerService().addNetwork(sub);

				taskMonitor.setProgress(1D * count * completedOperations / subgraphs.size() / operationCount);
			}
			taskMonitor.setProgress(1D * ++completedOperations / operationCount);
		}

		if (config.isSaveToFile())
		{
			GraphWriter gW = new SimpleGraphIO();
			edu.claflin.finder.Global.setOutput(config.getSaveDirectory());

			int count = 0;
			for (Graph graph : subgraphs)
			{
				gW.writeGraph(graph);
				taskMonitor.setProgress(1D * ++count * completedOperations / subgraphs.size() / operationCount);
			}
			taskMonitor.setProgress(1D * ++completedOperations / operationCount);
		}

		taskMonitor.setProgress(1D);
	}

	public static String getNetworkName(CyNetwork net)
	{
		String name = net.getRow(net).get(CyNetwork.NAME, String.class);
		return name;
	}

	public static Node[] getNodesArray(CyNetwork cn) 
	{
		Graph graph = GraphTaskUtils.convertCyNetwork(cn);
		List<Node> lNodes = graph.getNodeList();
		Collections.sort(lNodes);
		Node[] names = new Node[lNodes.size()];
		
		for (int index = 0; index < names.length; index++) 
		{
			names[index] = lNodes.get(index);
		}		
		
		return names;
	}
	
	public static HashSet<String> getNodeNames(CyNetwork cn)
	{
		Graph graph = GraphTaskUtils.convertCyNetwork(cn);
		HashSet<String> nodeNames = new HashSet<>();
		for (Node node : graph.getNodeList())
		{
			nodeNames.add(node.getIdentifier());
		}
		return nodeNames;
	}

	public static CyColumn[] getNumericColumnsArray(CyNetwork network)
	{
		List<CyColumn> colList = getNumericColumns(network);
		CyColumn[] cols = new CyColumn[colList.size()];
		return colList.toArray(cols);
	}
	
	private static List<CyColumn> getNumericColumns(CyNetwork network)
	{

		if (network == null)
			return null;

		List<CyColumn> columns = new ArrayList<>();

		network.getDefaultEdgeTable().getColumns().stream().forEach(col -> {
			if (Number.class.isAssignableFrom(col.getType()))
			{
				columns.add(col);
			}
		});

		List<String> blackListedColumns = new ArrayList<>();
		blackListedColumns.add("SUID");
		blackListedColumns.add("shared name");
		blackListedColumns.add("name");
		blackListedColumns.add("interaction");
		blackListedColumns.add("source");
		blackListedColumns.add("target");

		List<CyColumn> numberColumns = columns.stream().filter(col -> !blackListedColumns.contains(col.getName()))
				.collect(Collectors.toList());
		Collections.sort(numberColumns, (a,b) -> a.toString().compareToIgnoreCase(b.toString()));

		return numberColumns;
	}

	private static Double getEdgeData(CyNetwork network, CyEdge cyedge, String columnName)
	{
		if (columnName == null)
		{
			for (CyColumn col : getNumericColumns(network))
			{
				return extractEdgeData(network, cyedge, col);
			}
			return 0.0;
		}
		else
		{
			for (CyColumn col : getNumericColumns(network))
			{
				if (col.getName().equals(columnName))
				{
					return extractEdgeData(network, cyedge, col);
				}
			}
			return 0.0;
		}
	}

	private static Double extractEdgeData(CyNetwork network, CyEdge cyedge, CyColumn info)
	{
		try
		{
			if (info.getType() == Integer.class)
			{
				Object o = network.getDefaultEdgeTable().getRow(cyedge.getSUID()).get(info.getName(), Integer.class);
				return o == null ? 0.0 : ((Integer) o).doubleValue();

			}
			else if (info.getType() == Double.class)
			{
				Object o = network.getDefaultEdgeTable().getRow(cyedge.getSUID()).get(info.getName(), Double.class);
				return o == null ? 0.0 : (Double) o;
			}
			else
			{
				return 0.0;
			}
		}
		catch (Exception e)
		{
			return 0.0;
		}
	}
	
	public static double getNodeDoubleVisualProperty(View<CyNode> node, VisualProperty<Double> vp) 
	{
		Double d;
		try 
		{
			d = node.getVisualProperty(vp);
		}
		catch (ClassCastException e) 
		{
			String ds = node.getVisualProperty(vp) + "";
			d = Double.parseDouble(ds);
		}
		
		return d;
	}

	public static boolean isInteger(String s)
	{
		try
		{
			Integer.parseInt(s);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	public static boolean isDouble(String s)
	{
		try
		{
			Double.parseDouble(s);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
}
