package edu.claflin.finder.log;

/**
 * Enumeration of the logging levels for the user.
 * @author Charles Allen Schultz II
 * @version 1.0 May 28, 2015
 */
public enum LogLevel {
    
    NORMAL("NORMAL"),
    VERBOSE("VERBOSE"),
    DEBUG("DEBUG");
    
    /**
     * A simple string description of the log level for output.
     */
    private final String description;
    
    /**
     * Constructs the Log Level with the specified description.
     * @param description the String representing the description of the log 
     * level.
     */
    LogLevel(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return description;
    }
}
