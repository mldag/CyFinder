package edu.claflin.finder.io.graph;

import static edu.claflin.finder.Global.getLogger;
import static edu.claflin.finder.Global.getOutput;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import edu.claflin.finder.io.graph.sub.GraphReader;
import edu.claflin.finder.io.graph.sub.GraphWriter;
import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.Node;

/**
 * A class for reading and writing graphs in the SIF format.
 * 
 * The SIF, or Simple Interaction File, format is outlined in detail here:
 *
 * 
 * @author Charles Allen Schultz II
 * @version 3.1.1 February 2, 2016
 */
public final class SIFGraphIO implements GraphReader, GraphWriter
{

	/**
	 * Contains the String representation of the default relationship to use. The
	 * default relationship value is used when writing a graph and the graph being
	 * read does not use String objects to represent edges. Currently, String
	 * objects are being used for simplicity.
	 */
	private final String defaultRelationship;

	/**
	 * Constructs the SIFGraphIO object.
	 * 
	 * @param defaultRelationship the String representing the default edge
	 *                            relationship.
	 */
	public SIFGraphIO(String defaultRelationship)
	{
		this.defaultRelationship = defaultRelationship;
	}

	/**
	 * {@inheritDoc } Reads SIF Style graphs.
	 * 
	 * @param source     the File object representing the graph in memory.
	 * @param undirected This boolean is IGNORED as SIF graphs are NOT undirected.
	 * @return the read Graph
	 */
	@Override
	public Graph parseGraph(File source, boolean undirected)
	{

		// Make sure file is actually a file.
		if (!testFile(source))
			return null;

		Graph graph = new Graph(source.getName().split("\\.")[0]);

		if (getLogger() != null)
		{
			getLogger().logInfo(LogLevel.VERBOSE, "GraphIO: Attempting to read SIF graph: " + source.getAbsolutePath());
		}

		// Read graph here. Assume File is a valid SIF Graph file.
		try (BufferedReader bR = new BufferedReader(new FileReader(source)))
		{
			String line;

			do
			{
				line = bR.readLine();
				if (line != null)
				{
					String[] nodeString = line.split(" ");

					// Add nodes to Graph.
					Node[] nodes = new Node[nodeString.length - 1];
					if (nodes.length < 2 && getLogger() != null)
					{
						getLogger().logError(LogLevel.NORMAL, "GraphIO: Error: malformed SIF line: " + line);
						throw new IOException();
					}

					for (int nodeIndex = 0; nodeIndex < nodes.length; nodeIndex++)
					{
						if (nodeIndex == 1)
							continue;
						nodes[nodeIndex] = new Node(nodeString[nodeIndex]);
					}
					for (Node node : nodes)
					{
						if (!graph.getNodeList().contains(node))
							graph.addNode(node);
					}

					Double data = 0.0;

					try
					{
						data = Double.valueOf(nodeString[1]);
					}
					catch (NumberFormatException e)
					{

					}

					for (int nodeIndex = 1; nodeIndex < nodes.length; nodeIndex++)
					{
						graph.addEdge(new Edge(nodes[0], nodes[nodeIndex], data, false));
					}
				}
			}
			while (line != null);
		}
		catch (IOException ioe)
		{
			if (getLogger() != null)
			{
				getLogger().logError(LogLevel.NORMAL,
						"GraphIO: Error processing SIF Graph from file: " + source.getAbsolutePath());
			}
			graph = null;
		}
		finally
		{
			if (graph != null && getLogger() != null)
			{
				getLogger().logInfo(LogLevel.NORMAL, "GraphIO: Successfully constructed SIF Graph from " + "file.");
			}
			
		}
		return graph;
	}

	/**
	 * {@inheritDoc } Writes SIF Style graphs.
	 * 
	 * @param toWrite the Graph object to write to memory.
	 */
	@Override
	public void writeGraph(Graph toWrite)
	{
		File output = new File(getOutput(), toWrite.getName());
		boolean error = false;

		if (getLogger() != null)
		{
			getLogger().logInfo(LogLevel.NORMAL,
					"GraphIO: Attempt to log graph to file (SIF FORMAT): " + toWrite.getName());
		}

		try (BufferedWriter bW = new BufferedWriter(new FileWriter(output)))
		{
			for (Edge edge : toWrite.getEdgeList())
			{
				String type = edge.getData() + "";
				String line = String.format("%s %s %s", edge.getSource().getIdentifier(), type,
						edge.getTarget().getIdentifier());
				bW.write(line);
				bW.newLine();
				if (getLogger() != null)
				{
					getLogger().logInfo(LogLevel.VERBOSE, "GraphIO: Wrote line to graph file: " + line);
				}
			}
		}
		catch (IOException ioe)
		{
			error = true;
			if (getLogger() != null)
			{
				getLogger().logInfo(LogLevel.NORMAL, "GraphIO: Error writing graph to file: " + toWrite.getName());
			}
		}
		finally
		{
			String success = error ? "Failed to write" : "Successfully wrote";
			if (getLogger() != null)
			{
				getLogger().logInfo(LogLevel.NORMAL,
						"GraphIO: " + success + " SIF graph to file: " + output.getAbsolutePath());
			}
		}
	}

	/**
	 * Tests to see if a File is a valid graph file. Currently only tests by
	 * checking to ensure the provided file is not a directory.
	 * 
	 * @param file the File to test.
	 * @return the boolean value representing if the file is valid.
	 */
	private static boolean testFile(File file)
	{
		if (file.isDirectory())
		{
			if (getLogger() != null)
			{
				getLogger().logError(LogLevel.NORMAL, "GraphIO: File is a directory: " + file.getAbsolutePath());
			}
			return false;
		}
		return true;
	}

	/**
	 * Verifies the relationship between edges. Checks to see that the Object used
	 * in the {@link Graph} for representing an edge is a String. If it not, the
	 * default relationship is used instead.
	 * 
	 * @param edgeData the edge Data
	 * @return the Edge relationship
	 */
	private String verifyRelationship(Object edgeData)
	{
		if (edgeData instanceof String)
			return (String) edgeData;
		else
			return defaultRelationship;
	}
}
