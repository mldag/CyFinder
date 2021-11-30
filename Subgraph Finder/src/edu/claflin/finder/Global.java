package edu.claflin.finder;

import java.io.File;

import edu.claflin.finder.log.LogLevel;
import edu.claflin.finder.log.LogUtil;

/**
 * Holds "Global" data that needs to be accessed by multiple parts of the
 * program.  Should a piece of data, like the LogUtil reference or the output
 * directory, need to be shared by multiple parts of the program it will be 
 * stored here.  This is a static class and requires no instantiation.
 * 
 * @author Charles Allen Schultz II
 * @version 3.1 May 28, 2015
 */
public final class Global
{

	/**
	 * Private constructor for preventing instantiation of the class.
	 */
	private Global()
	{
	}

	/**
	 * The LogUtil reference used for logging data in the program.
	 */
	private static LogUtil logger = null;

	/**
	 * Instantiates the logger field.
	 * 
	 * @param maxGranularity the maximum granularity of the log messages.
	 * @param fileLogs the boolean array indicating file log settings.
	 * @param terminalLogs the boolean array indicating terminal output 
	 * settings.
	 */
	public static void makeLogger(LogLevel maxGranularity, boolean[] fileLogs, boolean[] terminalLogs)
	{
		logger = new LogUtil(maxGranularity, fileLogs, terminalLogs);

		if (logger != null)
		{
			logger.logInfo(LogLevel.DEBUG, "Logger instantiated correctly.");
		}
	}

	/**
	 * Destroys the LogUtil reference.  Calls the destroy() method on the logger
	 * field if the logger field is not null.
	 */
	public static void destroyLogger()
	{
		if (logger != null)
		{
			logger.logInfo(LogLevel.DEBUG, "Logger destroyed.");
			logger.destroy();
			logger = null;
		}
	}

	/**
	 * Gets the logger.
	 * @return the LogUtil reference used for logging in the program.
	 */
	public static LogUtil getLogger()
	{
		return logger;
	}

	/**
	 * The output directory for storing results.  Defaults to the working 
	 * directory.
	 */
	private static File output = new File(System.getProperty("user.dir"));

	/**
	 * Sets the output directory for storing results.
	 * 
	 * @param file the File representing the new output directory.
	 * @return a boolean indicating if the output directory was successfully 
	 * set.
	 */
	public static boolean setOutput(File file)
	{
		if (file.isDirectory())
		{
			output = file;
			if (logger != null)
				logger.logInfo(LogLevel.VERBOSE, "Global: Set output directory to: " + file.getAbsolutePath());
			return true;
		}
		else
		{
			if (logger != null)
				logger.logError(LogLevel.NORMAL, "Global: New output directory is NOT a directory!");
			return false;
		}
	}

	/**
	 * Gets the output directory for storing results.
	 * 
	 * @return the File representing the output directory.
	 */
	public static File getOutput()
	{
		return output;
	}
}
