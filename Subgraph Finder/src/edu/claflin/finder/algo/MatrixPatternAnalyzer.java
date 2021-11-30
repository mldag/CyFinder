package edu.claflin.finder.algo;

import java.util.ArrayList;

import edu.claflin.finder.logic.Graph;

/**
 * Processes a {@link Graph} searching for bipartite subgraphs by analyzing the 
 * matrices of said Graph.
 * 
 * @author Charles Allen Schultz II
 * @version 3.0.2 February 4, 2016
 * @deprecated No replacement yet.  Inefficient and utilizes old algorithm 
 * strategy.
 */
@Deprecated
public class MatrixPatternAnalyzer extends Algorithm {

    /**
     * Public constructor for initializing the analyzer.  Currently ignores 
     * state variables given it doesn't operate on an expansion basis.
     */
    public MatrixPatternAnalyzer() {
        super(new ArgumentsBundle());
    }
    
    /**
     * {@inheritDoc }
     * <br><br>
     * Processes the {@link Graph} object in four steps. <br>
     * 1) Check for valid 0 Sub-matrices.<br>
     * 2) Iterate on Step 1 through all contiguous node groupings.<br>
     * 3) Repeat Step 2 for each "Cornered" Sub-matrix of the original Graph.
     * <br>
     * 4) Repeat Step 3 for each transposition of the Graph.
     * Each step is carried out in its own method. This method controls Step 4 
     * by looping through all the permutations of the supplied Graph object.  
     * The permutation code is based on the "Counting QuickPerm Algorithm" 
     * found here:
     * 
     * @param graph the Graph object to search for Bipartite subgraphs.
     * @return the ArrayList of Graph objects containing found bipartite 
     * subgraph objects.
     */
    @Override
    public ArrayList<Graph> process(Graph graph) {
//        getLogger().logInfo("MPA: Searching Graph: " + graph.getName());
        ArrayList<Graph> bipartiteSubGraphs = new ArrayList<>();
        
        // Check the original because the perm algorithm skips the first.
        checkSubGraphs(graph, 0);
        
        int nodeCount = graph.getNodeCount();
        int[] controlArray = new int[nodeCount]; // For tracking permutations.
        int index = 1;
        int transpositions = 0;
        
        while (index < nodeCount) {
            if (controlArray[index] < index) {
                int swapIndex = ((index % 2) == 0) ? 0 : controlArray[index];
                graph.transpose(index, swapIndex);
                transpositions++;
                Graph foundGraph = checkSubGraphs(graph, transpositions);
                
                if (foundGraph != null)
                    bipartiteSubGraphs.add(foundGraph);
                
                controlArray[index]++;
                index = 1;
            } else {
                controlArray[index] = 0;
                index++;
            }
        }
        
//        getLogger().logInfo("MPA: Finished Searching Graph.  BSGs found: " + 
//                bipartiteSubGraphs.size());
        
        return cull(bipartiteSubGraphs);
    }
    /**
     * Step 3: Repeat Step 2 for each "Cornered" Sub-matrix of the original 
     * Graph.
     * <br>
     * A "Cornered" sub-matrix is a sub-matrix whose upper left node lies in 
     * the upper left corner of the main matrix.  I.e. A(0,0) = B(0,0) if A is 
     * the main matrix and B is the sub-matrix.
     * <br>
     * This method processes Step 2 with each of the n - 1 cornered matrices 
     * contained in the original matrix.  The smallest cornered matrix 
     * containing only one node is not processed (due to it being impossible to 
     * sort one node into two disjoint sets.
     * 
     * @param inGraph the graph
     * @param transpositions the number of transpositions
     * @return result of step 3
     */
    private Graph checkSubGraphs(Graph inGraph, int transpositions) {
        for (int length = inGraph.getNodeCount(); length > 2; length--) {
            String nameQualifier = "[MPA] T-" + transpositions + "-";
            Graph subGraph = inGraph.getSubGraph(0, length, nameQualifier);
            
//            getLogger().logAlgo("MPA: Checking subGraph: " + 
//                    subGraph.getName());
            
            if(checkNodeGroupings(subGraph))
                return subGraph;
        }
        
        return null;
    }
    /**
     * Step 2: Iterate on Step 1 through all contiguous node groupings.
     * 
     * Bipartite graphs may have a ratio of m:n nodes in each disjoint set. 
     * Since both node counts are independent of each other, all possible 
     * options (except cases where m or n = 0) must be checked.<br>
     * This section of the algorithm instructs the previous to examine a very 
     * specific section of the subgraphs for contiguous zeroes.  It iterates 
     * through all valid contiguous groupings.
     * 
     * @param inGraph the (sub) {@link Graph} object to check for bipartiteness.
     * @return the boolean primitive indicating if the Graph was bipartite.
     */
    private boolean checkNodeGroupings(Graph inGraph) {
        for (int row = inGraph.getNodeCount() - 1; row >= 0; row--) {
            
//            getLogger().logAlgo("MPA: Checking Node Groupings: " + row);
            
            if (checkZeroMatrices(inGraph, row))
                return true;
        }
        
        return false;
    }
    /**
     * Step 1: Check for valid 0 Sub-matrices.
     * <br>
     * Undirected bipartite graphs, when ordered correctly, have two noticeable 
     * regions containing no edges, or, if the graph is represented by ones and 
     * zeroes, two regions of zeros.  The following graph demonstrates this.<br>
     * <br><pre>
     * [ 0 0 0 0 1 ]<br>
     * [ 0 0 0 1 1 ]<br>
     * [ 0 0 0 1 0 ]<br>
     * [ 0 1 1 0 0 ]<br>
     * [ 1 1 0 0 0 ]<br></pre>
     * This section of the code verifies that these contiguous regions exist. 
     * If they don't, the provided Graph object (or subgraph object in this 
     * case) is not Bipartite.  This is the core comparison to be performed.
     * 
     * @param inGraph the {@link Graph} object to check for valid 0 
     * sub-matrices.
     * @param nodeCap the furthest node inward which the upperLeft Matrix should
     *  extend to.
     * @return the boolean primitive indicating if the sub-matrix is bipartite.
     */
    private boolean checkZeroMatrices(Graph inGraph, int nodeCap) {
//        getLogger().logAlgo("MPA: Checking Upper 0 SubMatrix.");
        for (int upperX = 0; upperX < nodeCap; upperX++) {
            for (int upperY = 0; upperY < nodeCap; upperY++) {
                if (inGraph.getEdge(upperX, upperY) != null)
                    return false;
            }
        }
        
//        getLogger().logAlgo("MPA: Checking Lower 0 SubMatrix.");
        for (int lowerX = nodeCap; lowerX < inGraph.getNodeCount(); lowerX++) {
            for (int lowerY = nodeCap; lowerY < inGraph.getNodeCount(); lowerY++) {
                if (inGraph.getEdge(lowerX, lowerY) != null)
                    return false;
            }
        }
        
        return true;
    }
}
