/*
 * Connection.java
 *
 * Created on December 25, 2004, 8:10 AM
 */

package com.exzoost.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
/**
 *
 * @author knight
 */
public class NewConnection {
    /** Creates a new instance of Connection */
    private NewConnection( ) {
        System.out.println("Using NewConnection class...");
    }
    
    public static Connection returnConnection( Properties props, String url ) 
        throws SQLException {
        try { 
            Class.forName("org.postgresql.Driver");
        }
        catch( java.lang.ClassNotFoundException e ) {
            System.err.println(e.getMessage());
        }
        
        return DriverManager.getConnection(url, props);
        
    }
    
    static void printSQLError( SQLException e ) {
        while(e!=null) {
            System.out.println(e.toString());
            e = e.getNextException();
        }
    }
    
    public static void closeConnection( Connection conn ) {
        try {
            conn.close();
        }
        catch( Throwable e ) {
            System.out.println("exception thrown:");
            
            if(e instanceof SQLException ) {
                printSQLError( (SQLException) e );
            }
            else {
                e.printStackTrace();
            }
        }
    }
    
}
