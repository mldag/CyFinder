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
package edu.claflin.cyfinder.internal;

import static org.cytoscape.work.ServiceProperties.ENABLE_FOR;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;

import edu.claflin.cyfinder.internal.tasks.factories.BipartiteLayoutTaskFactory;
import edu.claflin.cyfinder.internal.tasks.factories.BronKersbochTaskFactory;
import edu.claflin.cyfinder.internal.tasks.factories.ConnectedComponentsTaskFactory;
import edu.claflin.cyfinder.internal.tasks.factories.DisplayGraphTaskFactory;
import edu.claflin.cyfinder.internal.tasks.factories.EdgeBetweennessTaskFactory;
import edu.claflin.cyfinder.internal.tasks.factories.FastGreedyTaskFactory;
import edu.claflin.cyfinder.internal.tasks.factories.IntersectCollectionsTaskFactory;
import edu.claflin.cyfinder.internal.tasks.factories.IntersectNetworksTaskFactory;
import edu.claflin.cyfinder.internal.tasks.factories.KruskalTaskFactory;
import edu.claflin.cyfinder.internal.tasks.factories.MakeUndirectedTaskFactory;
import edu.claflin.cyfinder.internal.tasks.factories.PrimTaskFactory;
import edu.claflin.cyfinder.internal.tasks.factories.ShortestPathTaskFactory;
import edu.claflin.cyfinder.internal.tasks.factories.SubgraphFinderTaskFactory;
import edu.claflin.cyfinder.internal.tasks.factories.TreeLayoutTaskFactory;
import edu.claflin.cyfinder.internal.tasks.factories.WalktrapTaskFactory;

/**
 * Activates the plugin in Cytoscape.
 *
 * @author Charles Allen Schultz II
 * @version 1.4.1 June 19, 2015
 */
public class CyFinderActivator extends AbstractCyActivator
{

	private int gravity = 0;

	/**
	 * {@inheritDoc }
	 * 
	 * @param context the BundleContext of the currently running Cytoscape.
	 * @throws Exception any and all exceptions. :P
	 */
	@Override
	public void start(BundleContext bc) throws Exception
	{
		Global.cyApplicationManagerService = getService(bc, CyApplicationManager.class);
		Global.networkManagerService = getService(bc, CyNetworkManager.class);
		Global.rootNetworkService = getService(bc, CyRootNetworkManager.class);
		Global.desktopService = getService(bc, CySwingApplication.class);
		Global.taskManagerService = getService(bc, TaskManager.class);

		gravity = 0;

		addAnalysisServices(bc);
		addCommunityDetectionServices(bc);
		addMakeUndirectedServices(bc);
		addMaximalSubgraphsServices(bc);
		addSpanningForestServices(bc);
		addSubgraphFinderServices(bc);
		addShortestPathServices(bc);
		addLayoutServices(bc);
//		addDummyServices(bc);

	}

	private void addAnalysisServices(BundleContext bc)
	{
		///////////////////////////////////////////////////////////////////////////////
		// Analysis Services

		// Intersect Collections
		/////////////////////////////////////////////////////////////////////////////
		Properties interCProps = new Properties();
		interCProps.setProperty(TITLE, "Intersect Collections");
		interCProps.setProperty(PREFERRED_MENU, "Apps.CyFinder.Analysis");
		interCProps.setProperty(MENU_GRAVITY, gravity + ".1");
		interCProps.setProperty("preferredTaskManager", "menu");

		IntersectCollectionsTaskFactory interC = new IntersectCollectionsTaskFactory();
		registerAllServices(bc, interC, interCProps);

		// Intersect Networks
		/////////////////////////////////////////////////////////////////////////////
		Properties interNProps = new Properties();
		interNProps.setProperty(TITLE, "Intersect Networks");
		interNProps.setProperty(PREFERRED_MENU, "Apps.CyFinder.Analysis");
		interNProps.setProperty(MENU_GRAVITY, gravity + ".2");
		interNProps.setProperty("preferredTaskManager", "menu");

		IntersectNetworksTaskFactory interN = new IntersectNetworksTaskFactory();
		registerAllServices(bc, interN, interNProps);

		gravity++;
	}

	private void addCommunityDetectionServices(BundleContext bc)
	{
		// Edge Betweenness
		/////////////////////////////////////////////////////////////////////////////
		Properties edgebProps = new Properties();
		edgebProps.put(TITLE, "Edge Betweenness");
		edgebProps.put(PREFERRED_MENU, "Apps.CyFinder.Community Detection");
		edgebProps.put(MENU_GRAVITY, gravity + ".1");
		edgebProps.put(ENABLE_FOR, "network"); // enable the task for the loaded Cytoscape graph.

		EdgeBetweennessTaskFactory edgebFactory = new EdgeBetweennessTaskFactory();
		registerService(bc, edgebFactory, NetworkTaskFactory.class, edgebProps);

		// FastGreedy
		/////////////////////////////////////////////////////////////////////////////
		Properties fastgProps = new Properties();
		fastgProps.put(TITLE, "FastGreedy");
		fastgProps.put(PREFERRED_MENU, "Apps.CyFinder.Community Detection");
		fastgProps.put(MENU_GRAVITY, gravity + ".2");
		fastgProps.put(ENABLE_FOR, "network"); // enable the task for the loaded Cytoscape graph.

		FastGreedyTaskFactory fastgFactory = new FastGreedyTaskFactory();
		registerService(bc, fastgFactory, NetworkTaskFactory.class, fastgProps);

		// Walktrap
		/////////////////////////////////////////////////////////////////////////////
		Properties wtProps = new Properties();
		wtProps.put(TITLE, "Walktrap");
		wtProps.put(PREFERRED_MENU, "Apps.CyFinder.Community Detection");
		wtProps.put(MENU_GRAVITY, gravity + ".3");
		wtProps.put(ENABLE_FOR, "network"); // enable the task for the loaded Cytoscape graph.

		WalktrapTaskFactory wtFactory = new WalktrapTaskFactory();
		registerService(bc, wtFactory, NetworkTaskFactory.class, wtProps);

		gravity++;
	}

	private void addMakeUndirectedServices(BundleContext bc)
	{
		// Make Undirected Service - Additive
		////////////////////////////////////////////////////////////////
		Properties undirectedPropsAdditive = new Properties();
		undirectedPropsAdditive.put(TITLE, "Additive Method");
		undirectedPropsAdditive.put(PREFERRED_MENU, "Apps.CyFinder.Make Undirected");
		undirectedPropsAdditive.put(MENU_GRAVITY, gravity + ".1");
		undirectedPropsAdditive.put(ENABLE_FOR, "network");

		MakeUndirectedTaskFactory muaTaskFactory = new MakeUndirectedTaskFactory(true);
		registerService(bc, muaTaskFactory, NetworkTaskFactory.class, undirectedPropsAdditive);

		// Make Undirected Service - Transformative
		////////////////////////////////////////////////////////////////
		Properties undirectedPropsTransformative = new Properties();
		undirectedPropsTransformative.put(TITLE, "Transformative Method");
		undirectedPropsTransformative.put(PREFERRED_MENU, "Apps.CyFinder.Make Undirected");
		undirectedPropsTransformative.put(MENU_GRAVITY, gravity + ".2");
		undirectedPropsTransformative.put(ENABLE_FOR, "network");

		MakeUndirectedTaskFactory mutTaskFactory = new MakeUndirectedTaskFactory(false);
		registerService(bc, mutTaskFactory, NetworkTaskFactory.class, undirectedPropsTransformative);

		gravity++;
	}

	private void addMaximalSubgraphsServices(BundleContext bc)
	{
		// Connected Components Service
		/////////////////////////////////////////////////////////////////////////////
		Properties concProps = new Properties();
		concProps.put(TITLE, "Connected Components");
		concProps.put(PREFERRED_MENU, "Apps.CyFinder.Maximal Subgraphs");
		concProps.put(MENU_GRAVITY, gravity + ".1");
		concProps.put(ENABLE_FOR, "network"); // enable the task for the loaded Cytoscape graph.

		ConnectedComponentsTaskFactory concFactory = new ConnectedComponentsTaskFactory();
		registerService(bc, concFactory, NetworkTaskFactory.class, concProps);

		// Maximum Clique Service
		/////////////////////////////////////////////////////////////////////////////
		Properties maxCliqueProps = new Properties();
		maxCliqueProps.put(TITLE, "Maximum Clique Subgraph"); // task title
		maxCliqueProps.put(PREFERRED_MENU, "Apps.CyFinder.Maximal Subgraphs"); // put it at the top of the CyFinder menu
		maxCliqueProps.put(MENU_GRAVITY, gravity + ".2");
		maxCliqueProps.put(ENABLE_FOR, "network"); // enable the task for the loaded Cytoscape graph.

		BronKersbochTaskFactory lc = new BronKersbochTaskFactory(false); // creates tasks
		registerService(bc, lc, NetworkTaskFactory.class, maxCliqueProps); // registers the task

		// Maximum BiClique Service
		/////////////////////////////////////////////////////////////////////////////
		Properties maxBiCliqueProps = new Properties();
		maxBiCliqueProps.put(TITLE, "Maximum Biclique Subgraph"); // task title
		maxBiCliqueProps.put(PREFERRED_MENU, "Apps.CyFinder.Maximal Subgraphs"); // put it at the top of the CyFinder
																					// menu
		maxBiCliqueProps.put(MENU_GRAVITY, gravity + ".3");
		maxBiCliqueProps.put(ENABLE_FOR, "network"); // enable the task for the loaded Cytoscape graph.

		BronKersbochTaskFactory lcb = new BronKersbochTaskFactory(true); // creates tasks
		registerService(bc, lcb, NetworkTaskFactory.class, maxBiCliqueProps); // registers the task

		gravity++;
	}

	private void addSpanningForestServices(BundleContext bc)
	{
		// Kruskal Service
		/////////////////////////////////////////////////////////////////////////////
		Properties KruskalProps = new Properties();
		KruskalProps.put(TITLE, "Kruskal"); // task title
		KruskalProps.put(PREFERRED_MENU, "Apps.CyFinder.Spanning Forest"); // put it at the top of the CyFinder
																			// menu
		KruskalProps.put(MENU_GRAVITY, gravity + ".1");
		KruskalProps.put(ENABLE_FOR, "network"); // enable the task for the loaded Cytoscape graph.

		KruskalTaskFactory ktf = new KruskalTaskFactory(); // creates tasks
		registerService(bc, ktf, NetworkTaskFactory.class, KruskalProps); // registers the task

		// Prim Service
		/////////////////////////////////////////////////////////////////////////////
		Properties PrimProps = new Properties();
		PrimProps.put(TITLE, "Prim"); // task title
		PrimProps.put(PREFERRED_MENU, "Apps.CyFinder.Spanning Forest"); // put it at the top of the CyFinder menu
		PrimProps.put(MENU_GRAVITY, gravity + ".2");
		PrimProps.put(ENABLE_FOR, "network"); // enable the task for the loaded Cytoscape graph.

		PrimTaskFactory ptf = new PrimTaskFactory(); // creates tasks
		registerService(bc, ptf, NetworkTaskFactory.class, PrimProps); // registers the task

		gravity++;
	}

	private void addSubgraphFinderServices(BundleContext bc)
	{
		// Subgraph Finder Service
		////////////////////////////////////////////////////////////////
		Properties finderProps = new Properties();
		finderProps.put(TITLE, "Subgraph Finder");
		finderProps.put(PREFERRED_MENU, "Apps.CyFinder");
		finderProps.put(MENU_GRAVITY, gravity);
		finderProps.put(ENABLE_FOR, "network");

		SubgraphFinderTaskFactory sfTaskFactory = new SubgraphFinderTaskFactory();
		registerService(bc, sfTaskFactory, NetworkTaskFactory.class, finderProps);

		gravity++;
	}

	private void addShortestPathServices(BundleContext bc)
	{
		// Shortest Path Service
		////////////////////////////////////////////////////////////////
		Properties shortestPathProps = new Properties();
		shortestPathProps.put(TITLE, "Shortest Path");
		shortestPathProps.put(PREFERRED_MENU, "Apps.CyFinder");
		shortestPathProps.put(MENU_GRAVITY, gravity);
		shortestPathProps.put(ENABLE_FOR, "network");

		ShortestPathTaskFactory shortestPathTaskFactory = new ShortestPathTaskFactory();
		registerService(bc, shortestPathTaskFactory, NetworkTaskFactory.class, shortestPathProps);

		gravity++;
	}

	private void addLayoutServices(BundleContext bc)
	{
		// Bipartite Layout Service
		/////////////////////////////////////////////////////////////////////////////
		Properties BLayoutProps = new Properties();
		BLayoutProps.put(TITLE, "Bipartite Layout");
		BLayoutProps.put(PREFERRED_MENU, "Layout");

		BipartiteLayoutTaskFactory BLayoutFactory = new BipartiteLayoutTaskFactory();
		registerService(bc, BLayoutFactory, TaskFactory.class, BLayoutProps);
		
		Properties TLayoutProps = new Properties();
		TLayoutProps.put(TITLE, "Tree Layout");
		TLayoutProps.put(PREFERRED_MENU, "Layout");

		TreeLayoutTaskFactory TLayoutFactory = new TreeLayoutTaskFactory();
		registerService(bc, TLayoutFactory, TaskFactory.class, TLayoutProps);
	}

	private void addDummyServices(BundleContext bc)
	{
		// Display Graph
		/////////////////////////////////////////////////////////////////////////////
		Properties displayProps = new Properties();
		displayProps.put(TITLE, "Display Graph");
		displayProps.put(PREFERRED_MENU, "Apps.CyFinder");
		displayProps.put(MENU_GRAVITY, gravity);
		displayProps.put(ENABLE_FOR, "network"); // enable the task for the loaded Cytoscape graph.

		DisplayGraphTaskFactory displayFactory = new DisplayGraphTaskFactory();
		registerService(bc, displayFactory, NetworkTaskFactory.class, displayProps);

		gravity++;
	}
}
