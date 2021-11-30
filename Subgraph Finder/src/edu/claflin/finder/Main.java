package edu.claflin.finder;

import static edu.claflin.finder.Global.getLogger;
import static edu.claflin.finder.Global.getOutput;
import static edu.claflin.finder.Global.makeLogger;
import static edu.claflin.finder.Global.setOutput;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import edu.claflin.finder.algo.Algorithm;
import edu.claflin.finder.algo.ArgumentsBundle;
import edu.claflin.finder.algo.BreadthFirstTraversalSearch;
import edu.claflin.finder.algo.Bundle;
import edu.claflin.finder.algo.DepthFirstTraversalSearch;
import edu.claflin.finder.io.ManifestParser;
import edu.claflin.finder.io.graph.NOABatchIO;
import edu.claflin.finder.io.graph.SIFGraphIO;
import edu.claflin.finder.io.graph.SimpleGraphIO;
import edu.claflin.finder.io.graph.sub.GraphReader;
import edu.claflin.finder.io.graph.sub.GraphWriter;
import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.logic.Condition;
import edu.claflin.finder.logic.Edge;
import edu.claflin.finder.logic.Graph;
import edu.claflin.finder.logic.comp.EdgeWeightComparator;
import edu.claflin.finder.logic.cond.BipartiteCondition;
import edu.claflin.finder.logic.cond.CliqueCondition;
import edu.claflin.finder.logic.cond.DirectedCliqueCondition;
import edu.claflin.finder.logic.processor.BatchProcessor;

/**
 * Interprets the command line arguments provided to the program.
 * 
 * @author Charles Allen Schultz II
 * @version 3.2 February 4, 2016
 */
public class Main {
    
    /**
     * Command line argument processing occurs here.  Program usage:
     * {@code     java -jar SubgraphFinder.jar -v -out <booleans> -log <booleans> [ -f <FILE> | -m <MANIFEST> ] -input <TYPE> <OPTION> -output <TYPE> <OPTION> -algo <ALGO>}
     * 
     * {@code        -f <FILE> : Supply a single file to be parsed.}
     * {@code        -m <MANIFEST> : Supply a manifest listing several files to be parsed.}
     * 
     * {@code        -level <OPTION>}
     * 
     * {@code            <OPTION> : A logging level.}
     * {@code                NORMAL : Shallowest logging level.  Least amount of messages.}
     * {@code                VERBOSE : Moderate logging; includes intermittent operations.}
     * {@code                DEBUG : Highest logging granularity; logs every message.}
     * 
     * {@code        -out <booleans> :: -log <booleans>}
     * {@code            out : Logging to command prompt options.}
     * {@code            log : Logging to file options.}
     * 
     * {@code            <booleans> : Use 't' for true and 'f' for false.  The supplied arguments should}
     * {@code                    be concatenated as a single string.  There are currently four (4) needed:}
     * {@code                ALGO : Logging of Algorithm operations.}
     * {@code                ERRR : Logging of Error informations.}
     * {@code                GRPH : Logging of Graph operations.}
     * {@code                INFO : Logging of General informations.}
     * 
     * {@code        -input <TYPE> :: -output <TYPE>}
     * {@code            input : How to process the source graphs.}
     * {@code            output : How to output the found graphs.}
     * 
     * {@code            <TYPE> : Select from one of the following options:}
     * {@code                SIMPLE : Simple tab delimited format. (DEFUALT)}
     * {@code                SIF : Simple Interaction Format as defined in the Cytoscape Manual.}
     * {@code                NOA : NOA Batch Format as defined in the NOA Plugin for Cytoscape. (OUTPUT ONLY)}
     * 
     * {@code            <OPTION> : Some formats require an additional parameter described below:}
     * {@code                SIF : Default Relationship Type (i.e. \"pp\" or \"pd\")}
     * {@code                NOA : Same as SIF.}
     * 
     * {@code        -cfg <OPTION> : Some algorithms receive special configuration parameters, all of which default to false.}
     * 
     * {@code            <OPTIONS> : Select from one of the following options:}
     * {@code                PRESERVATIVE : Preserves edges on expansion.}
     * 
     * {@code        -type <OPTION> : A subgraph type to look for.}
     * 
     * {@code            <OPTIONS> : Select from one of the following options:}
     * {@code                BIPARTITE : Bipartite Subgraphs.}
     * {@code                CLIQUE : Clique Subgraphs.}
     * {@code                DCLIQUE : Directed Clique Subgraphs.}
     * 
     * {@code        -order <OPTION> : An ordering to impose}
     * 
     * {@code            <OPTIONS> : Select from one of the following options:}
     * {@code                EDGEWEIGHT : Order expansion based on the data attached to the edge interpretted as a number.}
     * {@code                    (Higher values are taken first.)}
     * 
     * {@code        -undirected : Interprets a graph as being undirected.}
     * 
     * {@code        -algo <ALGO> : The algorithm code indicating the method of finding BSGs to use.}
     * 
     * {@code            <ALGO> : Select from one of the following options:}
     * {@code                BFTS : Finds bipartite graphs based on a breadth first traversal search of the network.}
     * {@code                DFTS : Finds bipartite graphs based on a depth first traversal search of the network.}
     * {@code                BNDL : See below for instruction. (Do not self-reference.  Program will exit with error.)}
     * 
     * {@code        -algo BNDL <ALGO>( <ALGO> ...) : Load a BUNDLE Algorithm.  Processes the graph using each }
     * {@code                of the provided Algorithms.  Algorithm codes should be the same as above and delimited }
     * {@code                using spaces.  NOTE: this must be the last argument as the program assumes all following }
     * {@code                arguments are Algorithm codes.}
     * 
     * @param args the String array containing the program's startup parameters.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("~~~ SubGraph Finder ~~~");
            System.out.println("Usage:");
            System.out.println("    java -jar SubgraphFinder.jar -v -out <booleans> -log <booleans> [ -f <FILE> | -m <MANIFEST> ] -input <TYPE> <OPTION> -output <TYPE> <OPTION> -algo <ALGO>");
            System.out.println();
            System.out.println("       -f <FILE> : Supply a single file to be parsed.");
            System.out.println("       -m <MANIFEST> : Supply a manifest listing several files to be parsed.");
            System.out.println();
            System.out.println("       -level <OPTION>");
            System.out.println();
            System.out.println("           <OPTION> : A logging level.");
            System.out.println("               NORMAL : Shallowest logging level.  Least amount of messages.");
            System.out.println("               VERBOSE : Moderate logging; includes intermittent operations.");
            System.out.println("               DEBUG : Highest logging granularity; logs every message.");
            System.out.println();
            System.out.println("       -out <booleans> :: -log <booleans>");
            System.out.println("           out : Logging to command prompt options.");
            System.out.println("           log : Logging to file options.");
            System.out.println();
            System.out.println("           <booleans> : Use 't' for true and 'f' for false.  The supplied arguments should");
            System.out.println("                   be concatenated as a single string.  There are currently four (4) needed:");
            System.out.println("               ALGO : Logging of Algorithm operations.");
            System.out.println("               ERRR : Logging of Error informations.");
            System.out.println("               GRPH : Logging of Graph operations.");
            System.out.println("               INFO : Logging of General informations.");
            System.out.println();
            System.out.println("       -input <TYPE> :: -output <TYPE>");
            System.out.println("           input : How to process the source graphs.");
            System.out.println("           output : How to output the found graphs.");
            System.out.println();
            System.out.println("           <TYPE> : Select from one of the following options:");
            System.out.println("               SIMPLE : Simple tab delimited format. (DEFAULT)");
            System.out.println("               SIF : Simple Interaction Format as defined in the Cytoscape Manual.");
            System.out.println("               NOA : NOA Batch Format as defined in the NOA Plugin for Cytoscape. (OUTPUT ONLY)");
            System.out.println();
            System.out.println("           <OPTION> : Some formats require an additional parameter described below:");
            System.out.println("               SIF : Default Relationship Type (i.e. \"pp\" or \"pd\")");
            System.out.println("               NOA : Same as SIF.");
            System.out.println();
            System.out.println("       -algo <ALGO> : The algorithm code indicating the method of finding SGs to use.");
            System.out.println();
            System.out.println("           <ALGO> : Select from one of the following options:");
            System.out.println("               BFTS : Finds subgraphs based on a breadth first traversal search of the network.");
            System.out.println("               DFTS : Finds subgraphs based on a depth first traversal search of the network.");
            System.out.println("               BNDL : See below for instruction. (Do not self-reference.  Program will exit with error.)");
            System.out.println();
            System.out.println("       -cfg <OPTION> : Some algorithms receive special configuration parameters, all of which default to false.");
            System.out.println();
            System.out.println("           <OPTIONS> : Select from one of the following options:");
            System.out.println("               PRESERVATIVE : Preserves edges on expansion.");
            System.out.println();
            System.out.println("       -type <OPTION> : A subgraph type to look for.");
            System.out.println();
            System.out.println("           <OPTIONS> : Select from one of the following options:");
            System.out.println("               BIPARTITE : Bipartite Subgraphs.");
            System.out.println("               CLIQUE : Clique Subgraphs.");
            System.out.println("               DCLIQUE : Directed Clique Subgraphs.");
            System.out.println();
            System.out.println("       -order <OPTION> : An ordering to impose");
            System.out.println();
            System.out.println("           <OPTIONS> : Select from one of the following options:");
            System.out.println("               EDGEWEIGHT-ASCENDING : Order expansion based on the data attached to the edge interpretted as a number.");
            System.out.println("                            (Higher values are taken first.)");
            System.out.println("               EDGEWEIGHT-DESCENDING : Order expansion based on the data attached to the edge interpretted as a number.");
            System.out.println("                            (Lower values are taken first.)");
            System.out.println();
            System.out.println("       -undirected : Interprets a graph as being undirected.");
            System.out.println();
            System.out.println("       -algo BNDL <ALGO>( <ALGO> ...) : Load a BUNDLE Algorithm.  Processes the graph using each ");
            System.out.println("               of the provided Algorithms.  Algorithm codes should be the same as above and delimited ");
            System.out.println("               using spaces.  NOTE: this must be the last argument as the program assumes all following ");
            System.out.println("               arguments are Algorithm codes.");
        } else {
            boolean manifest = false;
            boolean[] terminalLogs = new boolean[] {true, true, true, true};
            boolean[] fileLogs = new boolean[] {true, true, true, true};
            boolean undirected = false;
            String file = null;
            Algorithm algo = null;
            GraphReader reader = new SimpleGraphIO();
            GraphWriter writer = new SimpleGraphIO();
            
            HashMap<String, Boolean> config = new HashMap<>();
            //FIXME - sets default to not preserve edges, can modify to either forcer true or set true as default
            config.put("PRESERVATIVE", true);
            
            ArrayList<Condition> conditions = new ArrayList<>();
            Comparator<Edge> ordering = null;
            
            LogLevel level = LogLevel.NORMAL;
            
            for (int arg = 0; arg < args.length; arg++) {
                switch(args[arg].toLowerCase()) {
                    case "-level":
                        switch(args[++arg]) {
                            case "NORMAL":
                                break;
                            case "VERBOSE":
                                level = LogLevel.VERBOSE;
                                break;
                            case "DEBUG":
                                level = LogLevel.DEBUG;
                                break;
                        }
                        break;  
                    case "-m":
                        manifest = true;
                    case "-f":
                        file = args[++arg];
                        break;
                    case "-log":
                        fileLogs = parseBoolean(args[++arg]);
                        break;
                    case "-out":
                        terminalLogs = parseBoolean(args[++arg]);
                        break;
                    case "-input":
                        String type = args[++arg];
                        if (type.equals("SIF"))
                            reader = new SIFGraphIO(args[++arg]);
                        else if (type.equals("NOA"))
                            error();
                        break;
                    case "-output":
                        type = args[++arg];
                        if (type.equals("SIF"))
                            writer = new SIFGraphIO(args[++arg]);
                        else if (type.equals("NOA"))
                            writer = new NOABatchIO(args[++arg]);
                        break;
                    case "-algo":
                        String algorithmCode = args[++arg];
                        if (algorithmCode.equals("BNDL")) {
                            Algorithm[] algos = 
                                    new Algorithm[args.length - ++arg];
                            for (; arg < args.length; arg++) {
                                algos[args.length - arg - 1] = 
                                        parseAlgorithm(args[arg], config, conditions, ordering);
                            }
                            algo = new Bundle(algos);
                        } else {
                            algo = parseAlgorithm(algorithmCode, config, conditions, ordering);
                        }
                        break;
                    case "-cfg":
                        switch(args[++arg]) {
                            case "PRESERVATIVE":
                                config.put("PRESERVATIVE", true);
                                break;
                            default:
                                error();
                        }
                        break;
                    case "-type":
                        switch(args[++arg]) {
                            case "BIPARTITE":
                                conditions.add(new BipartiteCondition());
                                break;
                            case "CLIQUE":
                                conditions.add(new CliqueCondition());
                                break;
                            case "DCLIQUE":
                                conditions.add(new DirectedCliqueCondition());
                                break;
                            default:
                                error();
                        }
                        break;
                    case "-order":
                        switch(args[++arg]) {
                            case "EDGEWEIGHT-ASCENDING":
                                ordering = new EdgeWeightComparator(true);
                                break;
                            case "EDGEWEIGHT-DESCENDING":
                                ordering = new EdgeWeightComparator(false);
                        }
                        break;
                    case "-undirected":
                        undirected = true;
                        break;
                    default:
                        error();
                }
            }
            
            makeLogger(level, fileLogs, terminalLogs);
            BatchProcessor<Graph, Graph> bP = new BatchProcessor<>();
            //Evyatar & Ariel Test

            if (manifest) {
                File[] files = ManifestParser.parseManifest(file);
                Graph[] graphs = new Graph[files.length];
                for (int index = 0; index < files.length; index++)
                    graphs[index] = reader.parseGraph(files[index], undirected);
                
                // Inherent difficulty in this method: memory overflow possible.
//                ArrayList<ArrayList<Graph>> foundGraphs = 
//                        bP.processMultiple(graphs, algo);
//                for (int index = 0; index < foundGraphs.size(); index++) {
//                    String output = graphs[index].getName();
//                    writeGraphs(foundGraphs.get(index), output, writer);
//                }
                
//                This solution reduces the memory overflow chance.
                for (Graph current : graphs) {
                    String output = current.getName();
                    ArrayList<Graph> foundGraphs = bP.processSingular(current, algo);
                    writeGraphs(foundGraphs, output, writer);
                }
            } else {
                Graph graph = reader.parseGraph(new File(file), undirected);
                ArrayList<Graph> foundGraphs = bP.processSingular(graph, algo);
                writeGraphs(foundGraphs, graph.getName(), writer);
            }

            getLogger().destroy();
        }
    }
    
    /**
     * Writes Graph objects to memory.  Does so in a manner so as to promote 
     * extensibility.
     * 
     * @param graphs the ArrayList object containing the graphs to write.
     * @param output the String representing the folder in the memory to store 
     * the graphs in.
     * @param writer the {@link GraphWriter} object to use for writing the 
     * graphs.
     */
    private static void writeGraphs(ArrayList<Graph> graphs, String output, GraphWriter writer) {
        File oldOutput = getOutput();
        File newOutput = new File(oldOutput + File.separator, output);
        newOutput.mkdir();
        setOutput(newOutput);
        
        for (Graph graph : graphs) {
            writer.writeGraph(graph);
        }
        
        setOutput(oldOutput);
    }
    
    /**
     * Parses a boolean string for configuring the {}.  Converts
     * the characters in the string into a boolean array depending on if they 
     * are 't' for true or 'f' for false.
     * 
     * @param booleans the String containing the boolean pattern.
     * @return the boolean array initialized from the String.
     */
    private static boolean[] parseBoolean(String booleans) {
        boolean[] returnBoolean = new boolean[booleans.length()];
        char[] values = booleans.toCharArray();
        
        for (int index = 0; index < values.length; index++) {
            switch (values[index]) {
                case 't':
                    returnBoolean[index] = true;
                    break;
                case 'f':
                    returnBoolean[index] = false;
                    break;
                default:
                    error();
            }
        }
        
        return returnBoolean;
    }
    
    /**
     * Parses an algorithm code string for returning the appropriate Algorithm 
     * object.
     * 
     * @param algorithmCode the String containing the correct Algorithm code.
     * @param config the configurations
     * @param conditions the conditions
     * @param comparator the Edge comparator
     * @return the {@link Algorithm} object instantiated from the code.
     */
    private static Algorithm parseAlgorithm(String algorithmCode, HashMap<String, Boolean> config, ArrayList<Condition> conditions, Comparator<Edge> comparator) {
        // MOVE THIS IN THE FUTURE.. This whole code is a mess to do it now..
        ArgumentsBundle bundle = new ArgumentsBundle();
        
        // checks for the "Edge Preservation" check box
        // can try to 
        bundle.putBoolean(ArgumentsBundle.COMMON_ARGS.EDGE_PRESERVATION.toString(), config.get("PRESERVATIVE"));
        conditions.stream().forEach(cond -> bundle.addCondition(cond));
        bundle.putObject(ArgumentsBundle.COMMON_ARGS.EDGE_WEIGHT_COMPARATOR.toString(), comparator);
        
        switch (algorithmCode) {
            case "BFTS":
                return new BreadthFirstTraversalSearch(bundle);
            case "DFTS":
                return new DepthFirstTraversalSearch(bundle);
            default:
                error();
        }
        
        return null; // Will never get here.
    }
    
    /**
     * Used to process a fatal error. Kills the program with a warning to the 
     * user.
     */
    private static void error() {
        System.out.println("ERROR - Please use appropriate arguments.");
        System.out.println("(Run: 'java -jar SubgraphFinder.jar' with no arguments.)");
        System.exit(1);
    }
}
