package edu.claflin.finder;

import java.io.File;
import java.util.List;

import edu.claflin.finder.algo.Algorithm.GraphSortOrder;
import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.algo.BreadthFirstTraversalSearch;
import edu.claflin.finder.algo.DepthFirstTraversalSearch;
import edu.claflin.finder.io.graph.SimpleGraphIO;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.cond.BipartiteCondition;
import edu.claflin.finder.logic.cond.CliqueCondition;

public class FinderDriver
{

	public static void main(String[] args)
	{
		String fnames[] = { 
				"BRCA.txt", "COAD.txt", "GBM.txt"
//				"S.txt"
		};
		
		boolean bipartite = false;
		for (String fn : fnames)
		{
			SimpleGraphIO sgio = new SimpleGraphIO();
			Graph g = sgio.parseGraph(new File("./Test Graphs/" + fn), true);
			System.out.println(g);
			System.out.println();
			System.out.println();

			ArgumentsBundle ab = new ArgumentsBundle();
			ab.putBoolean(ArgumentsBundle.COMMON_ARGS.EDGE_PRESERVATION.toString(), true);
			
			if (bipartite) 
			{
				ab.addCondition(new BipartiteCondition());
			}
			else 
			{
				ab.addCondition(new CliqueCondition());
			}
			
			BreadthFirstTraversalSearch bfs = new BreadthFirstTraversalSearch(ab);
			DepthFirstTraversalSearch dfs = new DepthFirstTraversalSearch(ab);
			
			bfs.setMinNodeCount(2);
			bfs.setGraphSortOrder(GraphSortOrder.DESCENDING);
			dfs.setMinNodeCount(2);
			dfs.setGraphSortOrder(GraphSortOrder.DESCENDING);

			double start = 0.0;
			double end = 0.0;
			
			start = System.currentTimeMillis();
			List<Graph> bfsres = bfs.process(g);
			end = System.currentTimeMillis();
			double bfstime = (end - start) / 1000;
			
			System.out.println("BFS");
			for (Graph res : bfsres)
			{
				System.out.println(res);
				System.out.println();
			}
			System.out.println(bfstime);
			System.out.println();
			System.out.println();
			
			start = System.currentTimeMillis();
			List<Graph> dfsres = dfs.process(g);
			end = System.currentTimeMillis();
			double dfstime = (end - start) / 1000;
			
			System.out.println("DFS");
			for (Graph res : dfsres) 
			{
				System.out.println(res);
				System.out.println();
			}
			System.out.println(dfstime);			
			System.out.println("===========================================\n");
		}
		System.out.println("END");

	}

}

