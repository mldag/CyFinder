package edu.claflin.finder.logic;

import static edu.claflin.finder.Global.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.claflin.finder.log.LogLevel;

/**
 * Represents a "conditioned" graph.  A conditioned graph is restrained and 
 * limited by the conditions applied to the graph upon creation.
 * 
 * @author Charles Allen Schultz II
 * @version 1.1 May 28, 2015
 */
public class ConditionedGraph extends Graph {


    /**
     * The List of Conditions applied to this Graph.
     */
    private final List<Condition> conditions;
    /**
     * A boolean indicating whether or not conditions are forced when 
     * components are added to the network.  If true, a simulation is run 
     * before adding Nodes or Edges to determine if the conditions will hold 
     * afterwards.
     */
    private boolean forceSatisfactionOnAdd = true;

    /**
     * Public constructor for creating a simple ConditionedGraph.  Has no 
     * conditions applied to it.
     * @param graphName the String representing the name of the graph.
     */
    public ConditionedGraph(String graphName) {
        super(graphName);
        conditions = new ArrayList<>();
    }
    /**
     * Public constructor for creating a ConditionedGraph.  Applies some 
     * conditions.
     * @param graphName the String representing the name of the graph.
     * @param conditions the Collection&lt;Condition&gt; containing the 
     * Condition objects.
     */
    public ConditionedGraph(String graphName, Collection<Condition> conditions) {
        this(graphName);
        if (conditions != null)
            this.conditions.addAll(conditions);
    }
    /**
     * Public constructor for creating a ConditionedGraph.  Applies some 
     * conditions as well as configuring if conditions should be forced.
     * @param graphName the String representing the name of the graph.
     * @param conditions the Collection &lt;Condition&gt; containing the 
     * Condition objects.
     * @param forceOnAdd the boolean indicating if conditions should be forced.
     */
    public ConditionedGraph(String graphName, Collection<Condition> conditions,
            boolean forceOnAdd) {
        this(graphName, conditions);
        forceSatisfactionOnAdd = forceOnAdd;
    }
    /**
     * Private constructor used to create copies of ConditionedGraph objects.
     * @param graph a normal Graph object without conditions.
     * @param conditions a collection of conditions to apply.
     */
    private ConditionedGraph(Graph graph, Collection<Condition> conditions) {
        this(graph.getName(), conditions, false);
        addPartialGraph(graph.getNodeList(), graph.getEdgeList());
    }
    
    /**
     * Adds a condition to the graph.
     * @param condition the Condition to add.
     */
    public void addCondition(Condition condition) {
        conditions.add(condition);
    }
    /**
     * Removes a condition from the graph.
     * @param condition the Condition to remove.
     */
    public void removeCondition(Condition condition) {
        conditions.remove(condition);
    }
    /**
     * Returns an accessible list of conditions.  It CAN be manipulated outside 
     * the graph's control, in which case it can be updated externally, however 
     * it is NOT thread safe.
     * @return a List of Conditions.
     */
    public List<Condition> getConditionsList() {
        return conditions;
    }
    
    /**
     * Returns true if conditions are forced on addition of new network 
     * components.
     * @return true if conditions are forced; false otherwise.
     */
    public boolean forcesOnAdd() {
        return forceSatisfactionOnAdd;
    }
    /**
     * Accessor method for configuring if conditions are forced on add of 
     * network components.
     * @param forceSatisfactionOnAdd a boolean indicating if conditions should 
     * be forced.
     */
    public void setForceOnAdd(boolean forceSatisfactionOnAdd) {
        this.forceSatisfactionOnAdd = forceSatisfactionOnAdd;
    }
    
    /**
     * Used to query if the ConditionedGraph fits its applied conditions.
     * @return true if the graph satisfies its conditions.
     */
    public boolean querySatisfaction() {
        for (Condition condition : conditions) {
            if (!condition.satisfies(this))
                return false;
        }
        
        return true;
    }
    
    /**
     * {@inheritDoc }
     * <p>
     * Runs a simulation if the ConditionedGraph is configured to check first 
     * that additions will be adhered to.  Allows a user to incrementally build 
     * a graph and determine at which point the graph no longer abides by its 
     * conditions.
     * 
     * @param nodes the List&lt;Node&gt; containing the nodes to add.
     * @param edges the List&lt;Edge&gt; containing the edges to add.
     * @return true if the addition abides by the conditions of the graph.
     */
    @Override
    public boolean addPartialGraph(List<Node> nodes, List<Edge> edges) {
        if (forceSatisfactionOnAdd) {
            ConditionedGraph copy = copy();
            copy.suppressLog = true;
            copy.superAddPartialGraph(nodes, edges);
            if (!copy.querySatisfaction())
                return false;
        }
        
        superAddPartialGraph(nodes, edges);
        if (forceSatisfactionOnAdd && getLogger() != null) {
            getLogger().logGraph(LogLevel.VERBOSE, 
                    getName() + ": Successful addition to conditioned graph.");
        }
        
        return true;
    }
    /**
     * Private method for circumventing the condition restrictions.  Used by 
     * {@link #addPartialGraph(java.util.List, java.util.List)} for simulating 
     * a graph expansion.
     * 
     * @param nodes the List&lt;Node&gt; containing the nodes to add.
     * @param edges the List&lt;Edge&gt; containing the edges to add.
     */
    private void superAddPartialGraph(List<Node> nodes, List<Edge> edges) {
        super.addPartialGraph(nodes, edges);
    }
    
    /**
     * Private method used for copying the ConditionedGraph.  Applies 
     * the current conditions and satisfaction variables to the supplied Graph 
     * to create a ConditionedGraph.
     * 
     * @param graph an unconditioned Graph object to apply conditions to.
     * @return a ConditionedGraph object with the same conditions as this one.
     */
    private ConditionedGraph conditionOther(Graph graph) {
        ConditionedGraph conditionedCopy = new ConditionedGraph(graph, conditions);
        conditionedCopy.setForceOnAdd(forceSatisfactionOnAdd);
        return conditionedCopy;
    }
    /**
     * {@inheritDoc }
     * @return a ConditionedGraph that is a copy of this one.
     */
    @Override
    public ConditionedGraph copy() {
        Graph copy = super.copy();
        return conditionOther(copy);
    }
    /**
     * {@inheritDoc }
     * @return a ConditionedGraph that is a copy of this one, but with 
     * different references for its components.
     */
    @Override
    public ConditionedGraph uniqueCopy() {
        Graph uniqueCopy = super.uniqueCopy();
        return conditionOther(uniqueCopy);
    }
    
    @Override
	public String toString() 
    {
    	return super.toString() + "\nConditions: " + conditions;
    	
    }
}
