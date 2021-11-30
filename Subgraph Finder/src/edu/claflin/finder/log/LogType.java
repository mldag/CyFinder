package edu.claflin.finder.log;

/**
 * Enumeration of the logging types used by the logging utility.
 * 
 * @author Charles Allen Schultz II
 * @version 3.1 May 28, 2015
 */
public enum LogType {
    
    INFO("[INFO]"), 
    ERRR("[ERRR]"),
    ALGO("[ALGO]"),
    GRPH("[GRPH]");
    
    /**
     * Identification string for the type of the log.
     */
    private final String identifier;
    
    /**
     * Constructs the LogLevel object with the specified identification String.
     * 
     * @param identifier the String to differentiate log types.
     */
    LogType(String identifier) {
        this.identifier = identifier;
    }
    
    /**
     * Used to get a user friendly version of the LogType.
     * 
     * @return the String consisting of the identifier and a trailing space.
     */
    @Override
    public String toString() {
        return identifier + " ";
    }
}
