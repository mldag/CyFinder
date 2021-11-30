package edu.claflin.finder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.claflin.finder.algo.Algorithm.GraphSortOrder;
import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.algo.BronKerbosch;
import edu.claflin.finder.io.graph.SimpleGraphIO;
import edu.claflin.finder.logic.Graph;

public class BronKersbochDriver
{
	public static void main(String[] args) throws IOException 
	{
		String fnames[] = {
				"S.txt", "M.txt", "B.txt", "D.txt", "Clique_Test.txt", "BRCA.txt", "COAD.txt", "GBM.txt"
//				"Bipartite_Test_3.txt"
		};		
		boolean bipartite = false;
		
		for (String fn : fnames)
		{
			SimpleGraphIO sgio = new SimpleGraphIO();
			Graph g = sgio.parseGraph(new File("./Test Graphs/" + fn), true);
			System.out.println(g);
			System.out.println();

			ArgumentsBundle ab = new ArgumentsBundle();			
			ab.putBoolean("bipartite", bipartite);
			
			BronKerbosch bk = new BronKerbosch(ab);			
			bk.setMinNodeCount(2);
			bk.setGraphSortOrder(GraphSortOrder.AVERAGE_WEIGHT);
			
			double start = 0.0;
			double end = 0.0;
			
			start = System.currentTimeMillis();
			List<Graph> results = bk.process(g);
			end = System.currentTimeMillis();
			double bktime = (end - start) / 1000;
			
			for (Graph res : results)
			{
				System.out.println(res);
				System.out.println();
			}
			
			System.out.println(bktime);
			System.out.println("===========================================\n");
		}
		System.out.println("END");
	}
}
