/*
 * ManipulateDB.java
 *
 * Created on March 17, 2005, 5:43 PM
 */

package com.exzoost.database;

import com.exzoost.gui.login.FileConfigurationManipulate;
import com.exzoost.registry.RegistryManipulation;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author knight
 */
public class ManipulateDB {
    private Connection conn;
    private Statement stmt;
    private ResultSet uprs;
    private String operatingsystem;
    private String homeuser;
    private Runtime rt;
    private String database;
    private String user;
    
    //the operating system
    final String WINDOWS_XP = "Windows XP";
    final String LINUX = "Linux";
    final String initializedb = "initializedb.sql";
    
    /** Creates a new instance of ManipulateDB */
    public ManipulateDB( Connection conn ) {
        this.conn = conn;
        
        //get the operating system of the user and it's homeuser
        operatingsystem = System.getProperty("os.name");
        homeuser = System.getProperty("user.home");
        
        //runtime
        rt = Runtime.getRuntime();
        
    }
    
    public String getCurrentDatabase() {
        String database = "";
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT current_database() AS db" );
            
            if(uprs.next())
                database = uprs.getString( "db" );
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
        }
        return database;
    }
    
    public boolean createDatabase( String db ) {
        try {
            stmt = conn.createStatement();
            
            stmt.executeUpdate( "CREATE DATABASE " + db + " TEMPLATE template0");
            
            stmt.close();
            
            return true;
        }
        catch( SQLException e ) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean initializeEmptyDatabase( String db, String port ) {
        File initializeFile = null;
//        InitializeDatabaseDialog dialog = new InitializeDatabaseDialog( null, true );
        
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                dialog = 
//                        ;
//            }
//        });
        
//        try {
//            initializeFile = new File( getClass().getResourceAsStream(initializedb) );
//        }
//        catch( java.net.URISyntaxException e ) {
//            e.printStackTrace();
//        }
        
        String command = "";
        
        if(operatingsystem.equals(LINUX)) {
            
            //process pqsql
//            command = "psql " + db + " -f " + initializeFile.getAbsolutePath() + 
//                    " -U " + getCurrentUser();
            command = "psql " + db + " -f " + initializedb + 
                    " -U " + getCurrentUser() + " -p " + port;            
        }
        else {
//            command = "\"" + RegistryManipulation.PostgreSQLApplicationDirectory() +
//                    "\\psql.exe\" " + db + " -f \"" + initializeFile.getAbsolutePath() + 
//                    "\" -U " + getCurrentUser();
            command = "\"" + RegistryManipulation.PostgreSQLApplicationDirectory() +
                    "\\psql.exe\" " + db + " -f \"" + initializedb + 
                    "\" -U " + getCurrentUser() + " -p " + port;
        }
        
        try {
            Process proc = rt.exec(command);

            //stream class
            InputStreamReader isr = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null) {
                System.out.println(line);
            }
//                dialog.outputEditorPane(proc.getInputStream());

            int wait = proc.waitFor();

            return true;
        }
        catch( java.io.IOException e ) {
            e.printStackTrace();
        }
        catch( java.lang.InterruptedException e ) {
            e.printStackTrace();
        }
        //end of process
        
        return false;
    }
    
    public void dropDatabase( String db ) throws SQLException {
        stmt = conn.createStatement();

        stmt.executeUpdate( "DROP DATABASE " + db );

        stmt.close();
    }
    
    private String getCurrentUser() {
        String user = "";
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT current_user AS user" );
            
            if(uprs.next())
                user = uprs.getString( "user" );
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
        }
        return user;
    }
    
    public void importDatabase( File file, String db, String port ) {
        
        String command = "";
        
        if(operatingsystem.equals(LINUX)) {
            //process pqsql
            command = "psql " + db + " -f " + file.getAbsolutePath() + 
                    " -U " + getCurrentUser() + " -p " + port;
        }
        else {
            command = "\"" + RegistryManipulation.PostgreSQLApplicationDirectory() +
                    "\\psql.exe\" " + db + " -f \"" + file.getAbsolutePath() + 
                    "\" -U " + getCurrentUser() + " -p " + port;
        }
        
        try {
            Process proc = rt.exec(command);

            //stream class
            InputStreamReader isr = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null) {
                System.out.println(line);
            }
//                dialog.outputEditorPane(proc.getInputStream());

            int wait = proc.waitFor();

        }
        catch( java.io.IOException e ) {
            e.printStackTrace();
        }
        catch( java.lang.InterruptedException e ) {
            e.printStackTrace();
        }
        //end of process
    }
    
    public void exportDatabase( File file, String port ) {
        String command = "";
        
        if(operatingsystem.equals(LINUX)) {
            //process pqsql
            command = "pg_dump -i " + getCurrentDatabase() + " -f \"" + file.getAbsolutePath() + 
                    "\" -F p -D -x -O  -U " + getCurrentUser() + " -p " + port;
        }
        else {
            command = "\"" + RegistryManipulation.PostgreSQLApplicationDirectory() +
                    "\\pg_dump.exe\" -i " + getCurrentDatabase() + " -f \"" + file.getAbsolutePath() + 
                    "\" -F p -D -x -O  -U " + getCurrentUser() + " -p " + port;
        }
        try {
            Process proc = rt.exec(command);

            //stream class
            InputStreamReader isr = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null) {
                System.out.println(line);
            }
//                dialog.outputEditorPane(proc.getInputStream());

            int wait = proc.waitFor();

        }
        catch( java.io.IOException e ) {
            e.printStackTrace();
        }
        catch( java.lang.InterruptedException e ) {
            e.printStackTrace();
        }
        //end of process
    }
    
}
