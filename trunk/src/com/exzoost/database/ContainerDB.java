/*
 * ContainerDB.java
 *
 * Created on March 2, 2005, 2:40 AM
 */

package com.exzoost.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author knight
 */
public class ContainerDB {
    private Connection conn;
    private ResultSet uprs;
    private Statement stmt;
    private NumberFormat nf;
    /** Creates a new instance of ContainerDB */
    public ContainerDB( Connection conn ) {
        //we have to get the reference of this connection
        this.conn = conn;
        //format the currency
        nf = NumberFormat.getCurrencyInstance( new Locale("id","id"));
    }
    
    public void initializeDataContainer( JTable e ) {
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT nama, volume, index_kontainer, " +
                    "harga, komentar FROM data_kontainer " );
            
            int i = 0;
            while(uprs.next()) {
                
                //if the information from db, add the empty row
                if(i==e.getRowCount()) {
                    DefaultTableModel model = (DefaultTableModel)e.getModel();
                    model.addRow( new Object[] { null, null, null, null, null } );
                }
                
                //fill the table
                
                //capitalize the name
                e.setValueAt( 
                        firstLetterCaps(uprs.getString("nama")), i, 0 );
                
                //volume, baby!!!
                e.setValueAt( 
                        uprs.getInt("volume"), i, 1 );
                            
                e.getModel().setValueAt( nf.format(uprs.getInt("harga")), i, 2 );
                
                //insert comment data too
                e.getModel().setValueAt(uprs.getString("komentar"), i, 3 );
                
                //insert index_kontainer data too
                e.getModel().setValueAt(uprs.getInt("index_kontainer"), i, 4 );
                
                i++;
                
            }
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    public boolean deleteContainer( JTable e ) {
        //the row that we want to delete
        int row = e.getSelectedRow();
        
        try {
            stmt = conn.createStatement();
            
            String index = ((Integer)e.getModel().getValueAt(row,4)).toString();
            
            //delete from warehouse data table ( master )
            stmt.executeUpdate( "DELETE FROM data_kontainer WHERE index_kontainer = " + index );
            
            stmt.close();
            
            //delete row from jtable
            DefaultTableModel model = (DefaultTableModel)e.getModel();
            model.removeRow(row);
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        
        return true;
        
    }
    
    public boolean insertContainer( JTable e, String name, String volume, String price,
            String comment ) { 
        //format the string
        name = "'" + name + "'";
        
        //comment, address and phone can be null
        if(comment.trim().equals("")) 
            comment = "null";
        else
            comment = "'" + comment + "'";
        
        try {
            //insert information to database
                       
            stmt = conn.createStatement();
            
            //container could not exceed more than 6
            uprs = stmt.executeQuery( "SELECT count(index_kontainer) AS jumlah FROM data_kontainer");
            
            uprs.next();
            
            if(uprs.getInt("jumlah")>=6)
                return false;
                        
            stmt.executeUpdate("INSERT INTO data_kontainer " +
                    "( nama, volume, harga, komentar ) VALUES (" + 
                    name + ", " + volume + ", " + price + ", " + comment + " )" );
            
            int i = 0;
            while(e.getValueAt(i,0)!=null) {
                i++;
                //if information is "overloaded", add the empty row
                if(i==e.getRowCount()) {
                    DefaultTableModel model = (DefaultTableModel)e.getModel();
                    model.addRow( new Object[] { null, null, null, null } );
                }
            }
            
            //get the information from database to fill the empty row
                        
            uprs = stmt.executeQuery( "SELECT nama, volume, komentar, harga, " +
                    "index_kontainer FROM data_kontainer WHERE index_kontainer = " +
                    "currval('index_kontainer')");
            
            //fill the empty row
            uprs.next();
            
            //fill the table

            //capitalize the name
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("nama")), i, 0 );

            //volume
            e.setValueAt( uprs.getInt("volume"), i, 1 );

            //price
            e.getModel().setValueAt( nf.format(uprs.getInt("harga")), i, 2 );
            
            //insert comment data too
            e.getModel().setValueAt(uprs.getString("komentar"), i, 3 );

            //insert index data too
            e.getModel().setValueAt(uprs.getInt("index_kontainer"), i, 4 );
                
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        
        return true;
        
    }
    
    public boolean updateContainer( JTable e, String name, String volume, 
            String price, String comment ) {
        //the row that to be updated
        int row = e.getSelectedRow();
                
        //comment field could be null or empty
        if(comment==null||comment.trim().equals("")) {
            comment = new String("null");
        }
        else {
            comment = "'" + comment + "'";
        }
                
        //the other field must not be null
        name = "'" + name + "'";
        
        try {
            stmt = conn.createStatement();
            
            //nomorinduk from the row to be updated
            String index = ((Integer)e.getModel().getValueAt(row,4)).toString();
            
            //update the database with new information
            stmt.executeUpdate( "UPDATE data_kontainer SET nama = " +
                    name + ", volume = " + volume + ", " +
                    "komentar = " + comment + ", harga = " + price + 
                    " WHERE index_kontainer = " + index );            
            
            //after that, we retrieve the information from database that has
            //been updated to update the row of the jtable            
            uprs = stmt.executeQuery( "SELECT nama, volume, index_kontainer, harga, " +
                    "komentar FROM data_kontainer WHERE index_kontainer = " + index );
            uprs.next();
            
            //fill the table
            
            //capitalize the name
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("nama")), row, 0 );

            //volume, baby!!!
            e.setValueAt( uprs.getInt("volume"), row, 1 );
            
            //price
            e.getModel().setValueAt( nf.format(uprs.getInt("harga")), row, 2 );

            //insert comment data too
            e.getModel().setValueAt(uprs.getString("komentar"), row, 3 );

            //insert index data too
            e.getModel().setValueAt(uprs.getInt("index_kontainer"), row, 4 );
        
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        
        return true;
    }
    
    private static String firstLetterCaps(String str) {
        char[] c = str.toLowerCase().toCharArray();
 
        boolean precededByNonLetter = true;
        for (int j = 0; j < c.length; j++) {
            if (Character.isLetter(c[j]) && precededByNonLetter) {
                c[j] = Character.toUpperCase(c[j]);
                precededByNonLetter = false;
            }
            if (!Character.isLetter(c[j])) {
                precededByNonLetter = true;
            }
        }
 
        return new String(c);
    }
    
}
