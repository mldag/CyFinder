package edu.claflin.finder.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Logging utility for documenting the state of the program.  This simple custom
 * built logging utility outputs to a file the steps involved in the program.  
 * It may also be configured to output to the terminal as well.
 * 
 * @author Charles Allen Schultz II
 * @version 3.1 May 28, 2015
 */
public class LogUtil {
    
	/**
	 * TEST
	 */
	public static String path;
	
    /**
     * The BufferedWriter used to log to a file.
     */
    private final BufferedWriter bW;
    
    /**
     * The value representing if logging to a file is enabled.  Defaults to true
     * and is only set to false due to a malfunction in the BufferedWriter.
     */
    private boolean logToFile = true;
    /**
     * The value representing if logging to the terminal is enabled.  Defaults 
     * to true.
     */
    private boolean logToTerminal = true;
    /**
     * The maximum granularity of the logging utility.  Anything greater will 
     * be ignored.
     */
    private final LogLevel maxGranularity;
    
    /**
     * The HashMap used for determining which of the logging outputs are logged 
     * to the terminal.
     */
    private final HashMap<LogType, Boolean> terminalLogs = new HashMap<>();
    /**
     * The HashMap used for determining which of the logging outputs are logged 
     * to a file.
     */
    private final HashMap<LogType, Boolean> fileLogs = new HashMap<>();
    
    /**
     * Constructs the logging utility.
     * 
     * @param maxGranularity the maximum granularity of the log messages.
     * @param fileLogs the boolean array containing four values.
     * @param terminalLogs the boolean array containing four values.
     */
    public LogUtil(LogLevel maxGranularity, boolean[] fileLogs, 
            boolean[] terminalLogs) {
        this.maxGranularity = maxGranularity;
        FileWriter fW = null;
        
        try {
            Date today = Calendar.getInstance().getTime();
            File output = new File(today.toString().replace(':', '-') + ".log");
            fW = new FileWriter(output);
        } catch (IOException ioe) {
            reportProblem(ioe.getMessage(), "Disabling Logging to File...");
            logToFile = false;
        } finally {
            if (logToFile)
                bW = new BufferedWriter(fW);
            else
                bW = null;
        }
        
        setFileLogs(fileLogs);
        setTerminalLogs(terminalLogs);
    }
    /**
     * Constructs the logging utility.  Allows the user to specify which 
     * outputs are logged to the terminal and which are logged to the file.
     * 
     * @param maxGranularity the maximum granularity of the log messages.
     * @param logToTerminal the boolean value indicating if logging to terminal 
     * should be enabled.
     */
    public LogUtil(LogLevel maxGranularity, boolean logToTerminal) {
        this(maxGranularity, new boolean[] {true, true, true, true}, 
                new boolean[] {true, true, true, true});
    }
    
    /**
     * Sets the FileLogs HashMap for selective logging to the file.
     * 
     * @param fileLogs the boolean array containing four values.
     */
    private void setFileLogs(boolean[] fileLogs) {
        if (fileLogs.length == 4) {
            this.fileLogs.put(LogType.ALGO, fileLogs[0]);
            this.fileLogs.put(LogType.ERRR, fileLogs[1]);
            this.fileLogs.put(LogType.GRPH, fileLogs[2]);
            this.fileLogs.put(LogType.INFO, fileLogs[3]);
        } else {
            reportProblem("fileLogs array is not the proper length.",
                    "Using default logging.");
            setFileLogs(new boolean[] {true, true, true, true});
        }
    }
    /**
     * Sets the TerminalLogs HashMap for selective terminal logging.
     * 
     * @param terminalLogs the boolean array containing four values.
     */
    private void setTerminalLogs(boolean[] terminalLogs) {
        if (terminalLogs.length == 4) {
            this.terminalLogs.put(LogType.ALGO, terminalLogs[0]);
            this.terminalLogs.put(LogType.ERRR, terminalLogs[1]);
            this.terminalLogs.put(LogType.GRPH, terminalLogs[2]);
            this.terminalLogs.put(LogType.INFO, terminalLogs[3]);
        } else {
            reportProblem("terminalLogs array is not the proper length.",
                    "Using default logging.");
            setTerminalLogs(new boolean[] {true, true, true, true});
        }
    }
    
    /**
     * Destroys the Logging to File functionality of the log engine.  Sets to
     * false the boolean value indicating  that logging to a file is enabled.  
     * It also closes the BufferedWriter to free the resource.
     */
    public void destroyLogToFile() {
        if (logToFile) {
            try {
                bW.close();
            } catch (IOException ioe) {
                reportProblem(ioe.getMessage(), "Unimportant.  "
                        + "Destroying engine anyways.");
            } finally {
                logToFile = false;
            }
        }
    }
    /**
     * Destroys the Logging to Terminal functionality of the log engine.  Sets
     * to false the boolean value indicating that logging to the terminal is
     * enabled.
     */
    public void destroyLogToTerminal() {
        if (logToTerminal)
            logToTerminal = false;
    }
    /**
     * Destroys all logging functionality of the log engine.  A convenience
     * method that calls both destroyLogToTerminal() and destroyLogToFile().
     */
    public void destroy() {
        destroyLogToFile();
        destroyLogToTerminal();
    }
    
    /**
     * Used to report problems with the logging engine to the terminal.
     * Reporting is done to the terminal since most likely the BufferedWriter
     * is experiencing an error.
     * 
     * @param problem the String representing the problem that occurred.
     * @param solution the String representing the solution the program 
     * utilized.
     */
    private void reportProblem(String problem, String solution) {
        System.out.println("~~~ Problem with the Logging Engine! ~~~");
        System.out.println(problem);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(solution);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
    
    /**
     * Used to log data.  Contains the logic for logging both to the terminal
     * and to a file.  This method is private so that a user may not pass 
     * incorrect {@link LogType} objects to the method.  Instead, there exist
     * public methods that call this method appropriately.
     * 
     * @param type the LogType object representing the level of detail for the
     *  log.
     * @param granularity the LogLevel object representing how granular this 
     * message is.
     * @param detail the String representing the data to be logged.
     */
    private void log(LogType type, LogLevel granularity, String detail) {
        if (granularity.compareTo(maxGranularity) <= 0) {
            if (logToFile && fileLogs.get(type)) {
                try {
                    bW.write(type + detail);
                    bW.newLine();
                } catch (IOException ioe) {
                    reportProblem(ioe.getMessage(), "Disabling Logging Engine...");
                    destroyLogToTerminal();
                }
            }
        
            if (logToTerminal && terminalLogs.get(type)) {
                System.out.println(type + detail);
            }
        }
    }
    /**
     * Logs an ERROR message.  Used by the user to log error messages.  Calls 
     * log(LogType, LogLevel, String) for the user.
     * 
     * @param level the level of granularity of this message.
     * @param detail the String representing the data to be logged.
     */
    public void logError(LogLevel level, String detail) {
        log(LogType.ERRR, level, detail);
    }
    /**
     * Logs an INFO message.  Used by the user to log info messages.  Calls 
     * log(LogLevel, String) for the user.
     * 
     * @param level the level of granularity of this message.
     * @param detail the String representing the data to be logged.
     */
    public void logInfo(LogLevel level, String detail) {
        log(LogType.INFO, level, detail);
    }
    /**
     * Logs an ALGO message.  Used by the user to log algorithm messages. Calls 
     * log(LogLevel, String) for the user.
     * 
     * @param level the level of granularity of this message.
     * @param detail the String representing the data to be logged.
     */
    public void logAlgo(LogLevel level, String detail) {
        log(LogType.ALGO, level, detail);
    }
    /**
     * Logs a GRPH message.  Used by the user to log graph messages.  Calls 
     * log(LogLevel, String) for the user.
     * 
     * @param level the level of granularity of this message.
     * @param detail the String representing the data to be logged.
     */
    public void logGraph(LogLevel level, String detail) {
        log(LogType.GRPH, level, detail);
    }
}
