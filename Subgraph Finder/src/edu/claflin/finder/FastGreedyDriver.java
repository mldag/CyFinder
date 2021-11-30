package edu.claflin.finder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.claflin.finder.algo.Algorithm.GraphSortOrder;
import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.algo.clustering.FastGreedy;
import edu.claflin.finder.io.graph.SimpleGraphIO;
import edu.claflin.finder.logic.Graph;

public class FastGreedyDriver
{
	public static void main(String[] args) throws IOException
	{
		String fnames[] =
		{
//				"S.txt", "M.txt", "B.txt", "D.txt", "BRCA.txt", "COAD.txt", "GBM.txt"
				"BRCA.txt", "COAD.txt", "GBM.txt"
		};
		boolean weighted = false;

		for (String fn : fnames)
		{			
			SimpleGraphIO sgio = new SimpleGraphIO();
			Graph g = sgio.parseGraph(new File("./Test Graphs/" + fn), true);
			System.out.println(g.toString());
			System.out.println();

			ArgumentsBundle ab = new ArgumentsBundle();
			ab.putBoolean("weighted", weighted);
			FastGreedy fg = new FastGreedy(ab);
			fg.setMinNodeCount(g.getNodeCount() <= 10 ? 1 : 10);
			fg.setGraphSortOrder(GraphSortOrder.DESCENDING);

			double start = 0.0;
			double end = 0.0;

			start = System.currentTimeMillis();
			List<Graph> results = fg.process(g);
			end = System.currentTimeMillis();
			double fgtime = (end - start) / 1000;

			for (Graph res : results)
			{
				System.out.println(res.toString());
				System.out.println();
			}

			System.out.println(fgtime);
			System.out.println("===========================================\n");
		}
		System.out.println("END");
	}
}
