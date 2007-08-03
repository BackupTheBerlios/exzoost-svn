/*
 * WriteLogFile.java
 *
 * Created on April 13, 2005, 6:09 PM
 */

package com.exzoost.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Exception;

/**
 *
 * @author knight
 */
public class WriteLogFile {
    private Throwable exception;
    final String logfile;
    final String operatingsystem;
    final String homeuser;
    final String WINDOWS_XP = "Windows XP";
    final String LINUX = "Linux";
    /** Creates a new instance of WriteLogFile */
    public WriteLogFile( Throwable e ) {
        this.exception = e;
        operatingsystem = System.getProperty("os.name");
        homeuser = System.getProperty("user.home");
        
        logfile = homeuser + "/exzoost/logfile.txt";
    }
    
    public WriteLogFile( ) {
        operatingsystem = System.getProperty("os.name");
        homeuser = System.getProperty("user.home");
        
        logfile = homeuser + "/exzoost/logfile.txt";
    }
    
    public void writeLogFile( ) {
        FileOutputStream out = null;
        PrintStream p;

        try {
            out = new FileOutputStream( logfile, true );
        }
        catch( java.io.FileNotFoundException e ) {
            e.printStackTrace();
        }

        p = new PrintStream( out );

        p.append(getStackTrace(exception));

        p.flush();
        p.close();
    }
    
    public void removeLogFile() {
        File file = new File(logfile);
        
        file.delete();
    }
    
    private static String getStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }
    
}
