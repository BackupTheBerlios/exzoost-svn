/*
 * FileConfigurationManipulate.java
 *
 * Created on March 18, 2005, 4:31 AM
 */

package com.exzoost.gui.login;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author knight
 */
public class FileConfigurationManipulate {
    private String operatingsystem;
    private String homeuser;
    
    final String WINDOWS_XP = "Windows XP";
    final String LINUX = "Linux";
    /** Creates a new instance of FileConfigurationManipulate */
    public FileConfigurationManipulate() {
        //get the operating system of the user and it's homeuser
        operatingsystem = System.getProperty("os.name");
        homeuser = System.getProperty("user.home");
    }
    
    private void copyFile( File source, File dest ) throws FileNotFoundException, IOException {
        FileReader in = new FileReader(source);
        FileWriter out = new FileWriter(dest);
        int c;
        
        while( (c=in.read()) != -1 ) 
            out.write(c);
        
        out.close();
        in.close();
    }
    
    public void dummyPasswordFile( String db, String user, String password, String port ) {
        File pgpassfile = null;
        File backuppgpassfile = null;
        
        if(operatingsystem.equals(LINUX)) {
            pgpassfile = new File( homeuser + "/.pgpass" );
            backuppgpassfile = new File( homeuser + "/.pgpassbak" );
        }
        //if user is using Windows XP
        else {
            //This is where postgresql setting data kept in Windows XP
            File directoryappdata = new File( homeuser + "\\Application Data" + "\\postgresql" );

            //check if the directory exists...
            if(!directoryappdata.exists()) {
                directoryappdata.mkdir();    
            }

            pgpassfile = new File( directoryappdata.toString() + "\\pgpass.conf" );
            backuppgpassfile = new File( directoryappdata.toString() + "\\pgpassbak.conf" );

        }
         
        //check if the pgpass.conf exists
        if(pgpassfile.exists()) {

            try {
                //copy the file ( backup the file )
                copyFile( pgpassfile, backuppgpassfile );
            }
            catch( FileNotFoundException e ) {
                e.printStackTrace();
            }
            catch( IOException e ) {
                e.printStackTrace();
            }

            //delete the source
            pgpassfile.delete();
        }

        FileOutputStream out = null;
        PrintStream p;

        try {
            out = new FileOutputStream( pgpassfile.toString() );
        }
        catch( java.io.FileNotFoundException e ) {
            e.printStackTrace();
        }

        p = new PrintStream( out );

        String pass = "localhost:" + port + ":" + db + ":" + user + ":" +
                password;

        p.println(pass);

        p.close();


        if(operatingsystem.equals(LINUX)) {
            changePermissionUNIX();
        }
    }
    
    private void changePermissionUNIX() {
        //process pqsql
        String command = "chmod 0600 " + homeuser + "/.pgpass";

        try {
            Process proc = Runtime.getRuntime().exec(command);

        }
        catch( java.io.IOException e ) {
            e.printStackTrace();
        }
        //end of process
    }
    
    public void returnOriginalFile( ) {
        File pgpassfile = null;
        File backuppgpassfile = null;
        
        
        if(operatingsystem.equals(LINUX)) {
            
            pgpassfile = new File( homeuser + "/.pgpass" );
            backuppgpassfile = new File( homeuser + "/.pgpassbak" );
            
            pgpassfile.delete();
            backuppgpassfile.renameTo( new File( homeuser + 
                    "/.pgpass" ) );
            
            changePermissionUNIX();
        }
        //if user is using Windows XP
        else {
            //This is where postgresql setting data kept in Windows XP
            File directoryappdata = new File( homeuser + "\\Application Data" + "\\postgresql" );

            //check if the directory exists...
            if(!directoryappdata.exists()) {
                directoryappdata.mkdir();    
            }

            pgpassfile = new File( directoryappdata.toString() + "\\pgpass.conf" );
            backuppgpassfile = new File( directoryappdata.toString() + "\\pgpassbak.conf" );
        }
    }
    
}
