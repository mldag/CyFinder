package edu.claflin.finder.io;

import static edu.claflin.finder.Global.getLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import edu.claflin.finder.log.LogLevel;

/**
 * Reads manifest files.  Assumes that the provided manifest file is correct.  
 * This is a static class and requires no instantiation.
 * 
 * @author Charles Allen Schultz II
 * @version 3.1 May 28, 2015
 */
public final class ManifestParser {
    
    /**
     * Private constructor for preventing instantiation of the class.
     */
    private ManifestParser() {}
    
    /**
     * Parses the provided manifest file.
     * 
     * @param manifest the File object pointing to the manifest file on disk.
     * @return the File[] array holding the multiple files to be processed.
     */
    public static File[] parseManifest(File manifest) {
        ArrayList<String> fileStrings = readManifest(manifest);
        if (fileStrings == null) {
            
            if (getLogger() != null) {
                getLogger().logError(LogLevel.NORMAL, 
                        "Manifest improperly formatted.");
            }
            
            return null;
        }
        return processManifest(fileStrings);
    }
    /**
     * Parses the provided manifest file.  This is a convenience method that 
     * allows the programmer to parse a manifest file based on a String 
     * representing the location of the file on disk.  Assumes the file is 
     * correct.
     * 
     * @param fileString the String representing the manifest file on disk.
     * @return the File[] array holding multiple files to be processed.
     */
    public static File[] parseManifest(String fileString) {
        return parseManifest(new File(fileString));
    }
    
    /**
     * Reads the manifest file.  This is a helper method that reads the 
     * manifest file line by line assuming that each line represents a file 
     * to be processed.
     * 
     * @param manifest the File object pointing to the manifest file on disk.
     * @return the ArrayList containing String objects pointing to graph files. 
     */
    private static ArrayList<String> readManifest(File manifest) {
        ArrayList<String> fileStrings = new ArrayList<>();
        
        try(BufferedReader bR = new BufferedReader(new FileReader(manifest))) {
            String line;
            do {
                line = bR.readLine();
                if (line != null) {
                    fileStrings.add(line);
                    if (getLogger() != null) {
                        getLogger().logInfo(LogLevel.DEBUG, 
                                "ManifestParser: Read file from manifest: "
                                + line);
                    }
                }
            } while (line != null);
        } catch (IOException ioe) {
            if (getLogger() != null) {
                getLogger().logError(LogLevel.NORMAL, 
                        "ManifestParser: Error processing manifest file at "
                        + "line: " + fileStrings.size());
            }
            return null;
        }
        
        return fileStrings;
    }
    /**
     * Processes the ArrayList of Strings collected by the readManifest() 
     * method.  
     * 
     * @param fileStrings the ArrayList of Strings pointing to graphs on the 
     * disk.
     * @return the File[] array holding multiple files to be processed.
     */
    private static File[] processManifest(ArrayList<String> fileStrings) {
        File[] graphs = new File[fileStrings.size()];
        
        int index = 0;
        for (String graph : fileStrings) {
            graphs[index++] = new File(graph);
            if (getLogger() != null) {
                getLogger().logInfo(LogLevel.VERBOSE, 
                        "ManifestParser: Created File Object Successfully: " 
                        + graphs[index - 1].getAbsolutePath());
            }
        }
        
        return graphs;
    }
}
