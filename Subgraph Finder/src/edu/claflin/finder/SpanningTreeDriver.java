package edu.claflin.finder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.claflin.finder.algo.Algorithm.GraphSortOrder;
import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.algo.spanningtree.Kruskal;
import edu.claflin.finder.algo.spanningtree.Prim;
import edu.claflin.finder.io.graph.SimpleGraphIO;
import edu.claflin.finder.logic.Graph;



public class SpanningTreeDriver
{
	public static void main(String[] args) throws IOException 
	{
		String fnames[] = { 
				"MST_Test_1.txt", "BRCA.txt", "COAD.txt", "GBM.txt"
		};
		double threshold = Double.POSITIVE_INFINITY;
//		double threshold = Double.NEGATIVE_INFINITY;
//		double threshold = 0;
		
		boolean max = false;
		String startNode = "A";
		
		for (String fn : fnames)
		{
			SimpleGraphIO sgio = new SimpleGraphIO();
			Graph g = sgio.parseGraph(new File("./Test Graphs/" + fn), true);
			System.out.println(g);
			System.out.println();
			System.out.println();

			ArgumentsBundle ab = new ArgumentsBundle();
			
			ab.putBoolean("max", max);
			ab.putDouble("threshold", threshold);
			ab.putObject("startNode", startNode);
			
			Kruskal k = new Kruskal(ab);
			Prim p = new Prim(ab);
			
			k.setMinNodeCount(2);
			k.setGraphSortOrder(GraphSortOrder.DESCENDING);
			p.setMinNodeCount(2);
			p.setGraphSortOrder(GraphSortOrder.DESCENDING);

			double start = 0.0;
			double end = 0.0;
			
			start = System.currentTimeMillis();
			List<Graph> kres = k.process(g);
			end = System.currentTimeMillis();
			double ktime = (end - start) / 1000;
			
			System.out.println("Kruskal");
			for (Graph res : kres)
			{
				System.out.println(res);
				System.out.println();
			}
			System.out.println(ktime);
			System.out.println();
			System.out.println();
			
			start = System.currentTimeMillis();
			List<Graph> pres = p.process(g);
			end = System.currentTimeMillis();
			double ptime = (end - start) / 1000;
			
			System.out.println("Prim");
			for (Graph res : pres) 
			{
				System.out.println(res);
				System.out.println();
			}
			System.out.println(ptime);			
			System.out.println("===========================================\n");
		}
		System.out.println("END");
	}
}
