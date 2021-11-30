package edu.claflin.finder.logic;

import static edu.claflin.finder.Global.getLogger;
import edu.claflin.finder.log.LogLevel;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Represents a PrioritySet.  Operates as an extension of the 
 * java.util.PriorityQueue in that it does not allow duplicate elements.
 * Furthermore, it may be configured so that on addition of similar elements
 * (i.e. elements that are equivalent via .equals() but have different weight
 * values) the higher of the two values is kept via comparator analysis.
 * 
 * That is to say, if a Comparator indicates that a newly added object should 
 * come BEFORE the other object in a descending order, then the old object is 
 * replaced.  This can be leveraged to perform a descending-based replacement 
 * if the comparator is configured properly.  (i.e. a 4 replacing a 5.)
 * 
 * @author Charles Allen Schultz II
 * @version 1.1 May 28, 2015
 * @param <T> the type of object to manage in the PriorityQueue.
 */
public class PrioritySet<T> extends PriorityQueue<T> {
    
    /**
     * Boolean indicating if newer elements with a greater compare value 
     * should replace others.
     */
    private final boolean addOverride;
    
    /**
     * Public constructor for initializing a PrioritySet.  Utilizes the 
     * natural ordering of the elements.
     * 
     * @param addOverride a boolean indicating if new elements may override 
     * older ones based on the comparator's value.
     */
    public PrioritySet(boolean addOverride) {
        this.addOverride = addOverride;
    }
    /**
     * Public constructor for initializing a PrioritySet.  Requires both a 
     * comparator for comparing elements and a boolean indicating if 
     * addOverride should be enabled.
     * 
     * @param compare the Comparator to use for queue ordering.
     * @param addOverride a boolean indicating if new elements may override 
     * older ones based on the comparator's value.
     */
    public PrioritySet(Comparator<T> compare, boolean addOverride) {
        super(compare);
        this.addOverride = addOverride;
    }
    
    /**
     * Allows additions of items that already has a copy within the set 
     * (determined by obj.equals(obj2) ) and their subsequent replacement if 
     * they are of lesser value than the new object as determined by the 
     * queue's comparator.
     * <p>
     * {@inheritDoc }
     * 
     * @param t the T type object to add.
     * @return true if added, false if not.
     */
    @Override
    public boolean add(T t) {
        if (!contains(t)) {
            if (getLogger() != null) {
                getLogger().logInfo(LogLevel.DEBUG, "PrioritySet: Adding new "
                        + "item to PrioritySet instance: " + t.toString());
            }
            return super.add(t);
        } else if (addOverride) {
            boolean removed = removeIf(tInSet -> tInSet.equals(t) && 
                    comparator().compare(t, tInSet) < 0);
            if (removed) {
                if (getLogger() != null) {
                    getLogger().logInfo(LogLevel.DEBUG, "PrioritySet: "
                            + "Replacing item in PrioritySet instance: "
                            + t.toString());
                }
                return super.add(t);
            }
        }
        
        if (getLogger() != null) {
            getLogger().logInfo(LogLevel.DEBUG, "PrioritySet: Not adding item "
                    + "to PrioritySet instance: " + t.toString());
        }
        
        return false;
    }
}
