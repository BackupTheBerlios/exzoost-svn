/*
 * CustomerDB.java
 *
 * Created on February 11, 2005, 8:00 PM
 */

package com.exzoost.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author knight
 */
public class CustomerDB {
    private Connection conn;
    private ResultSet uprs;
    private Statement stmt;
    /** Creates a new instance of CustomerDB */
    public CustomerDB( Connection conn ) {
        //we have to get the references for the connection
        this.conn = conn;
    }
    
    public void initializeDataCustomer( JTable e ) {
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT index_pembeli, nama, alamat, telepon, " +
                    "komentar FROM data_pembeli " );
            
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
                
                //capitalize the address
                if(uprs.getString("alamat")!=null)
                    e.setValueAt( 
                            firstLetterCaps(uprs.getString("alamat")), i, 1 );
                
                e.setValueAt( uprs.getString("telepon"), i, 2 );
                
                //insert comment data too
                e.getModel().setValueAt(uprs.getString("komentar"), i, 3 );
                
                //insert index data too
                e.getModel().setValueAt(uprs.getInt("index_pembeli"), i, 4 );
                
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
    
    public int getIndex( String name ) {
        name = "'" + name + "'";
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT index_pembeli FROM data_pembeli " +
                    "WHERE nama = " + name );
            
            uprs.next();
            
            int index = uprs.getInt("index_pembeli"); 
            
            uprs.close();
            stmt.close();
            
            return index;
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        return -1;
    }
    
    public String getName( int index ) {
        String name = "";
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT nama FROM data_pembeli " +
                    "WHERE index_pembeli = " + index );
            
            if(uprs.next())
                name = uprs.getString("nama");
            else
                name = "";
            
            uprs.close();
            stmt.close();
            
            if(name.equals(""))
                return "-";
            else
                return name;
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        return "";
    }
    
    public boolean insertCustomer( String name, String address, 
            String phone, String comment ) { 
        //format the string
        name = "'" + name + "'";
        
        //comment, address and phone can be null
        if(comment.trim().equals("")) 
            comment = "null";
        else
            comment = "'" + comment + "'";
        if(phone.trim().equals(""))
            phone = "null";
        else
            phone = "'" + phone + "'";
        if(address.trim().equals(""))
            address = "null";
        else
            address = "'" + address + "'";
        
        try {
            //insert information to database
                       
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO data_pembeli " +
                    "( nama, alamat, telepon, komentar ) VALUES (" + 
                    name + ", " + address + ", " + phone + ", " + comment + " )" );
            
            stmt.close();
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        
        return true;
    }
    
    public boolean insertCustomer( JTable e, String name, String address, 
            String phone, String comment ) { 
        //format the string
        name = "'" + name + "'";
        
        //comment, address and phone can be null
        if(comment.trim().equals("")) 
            comment = "null";
        else
            comment = "'" + comment + "'";
        if(phone.trim().equals(""))
            phone = "null";
        else
            phone = "'" + phone + "'";
        if(address.trim().equals(""))
            address = "null";
        else
            address = "'" + address + "'";
        
        try {
            //insert information to database
                       
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO data_pembeli " +
                    "( nama, alamat, telepon, komentar ) VALUES (" + 
                    name + ", " + address + ", " + phone + ", " + comment + " )" );
                        
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
            stmt = conn.createStatement();
            uprs = stmt.executeQuery( "SELECT nama, alamat, telepon, komentar, " +
                    "index_pembeli FROM data_pembeli WHERE index_pembeli = " +
                    "currval('index_pembeli')");
            
            //fill the empty row
            uprs.next();
            
            //fill the table

            //capitalize the name
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("nama")), i, 0 );

            //capitalize the address
            if(uprs.getString("alamat")!=null)
                e.setValueAt( 
                        firstLetterCaps(uprs.getString("alamat")), i, 1 );

            e.setValueAt( uprs.getString("telepon"), i, 2 );

            //insert comment data too
            e.getModel().setValueAt(uprs.getString("komentar"), i, 3 );

            //insert index data too
            e.getModel().setValueAt(uprs.getInt("index_pembeli"), i, 4 );
                
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
    
    public boolean updateCustomer( JTable e, String name, String address, String phone,  
            String comment ) {
        //the row that to be updated
        int row = e.getSelectedRow();
        
        //phone field could be null or empty
        if(phone==null||phone.trim().equals("")) {
            phone = new String("null");
        }
        else {
            phone = "'" + phone + "'";
        }
        
        //comment field could be null or empty
        if(comment==null||comment.trim().equals("")) {
            comment = new String("null");
        }
        else {
            comment = "'" + comment + "'";
        }
        
        //address field could be null or empty
        if(address==null||address.trim().equals("")) 
            address = "null";
        else
            address = "'" + address + "'";
        
        //the other field must not be null
        name = "'" + name + "'";
        
        try {
            stmt = conn.createStatement();
            
            //nomorinduk from the row to be updated
            String index = ((Integer)e.getModel().getValueAt(row,4)).toString();
            
            //update the database with new information
            stmt.executeUpdate( "UPDATE data_pembeli SET nama = " +
                    name + ", alamat = " + address + ", telepon = " + phone +
                    ", komentar = " + comment +
                    " WHERE index_pembeli = " + index );            
            
            //after that, we retrieve the information from database that has
            //been updated to update the row of the jtable            
            uprs = stmt.executeQuery( "SELECT nama, alamat, telepon, index_pembeli, " +
                    "komentar FROM data_pembeli WHERE index_pembeli = " + index );
            uprs.next();
            
            //fill the table
            
            //capitalize the name
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("nama")), row, 0 );

            //capitalize the address
            if(uprs.getString("alamat")!=null)
                e.setValueAt( 
                        firstLetterCaps(uprs.getString("alamat")), row, 1 );

            e.setValueAt( uprs.getString("telepon"), row, 2 );

            //insert comment data too
            e.getModel().setValueAt(uprs.getString("komentar"), row, 3 );

            //insert index data too
            e.getModel().setValueAt(uprs.getInt("index_pembeli"), row, 4 );
        
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
    
    public boolean deleteCustomer( JTable e ) {
        //the row that we want to delete
        int row = e.getSelectedRow();
        
        try {
            stmt = conn.createStatement();
            String index = ((Integer)e.getModel().getValueAt(row,4)).toString();
                
            stmt.executeUpdate( "DELETE FROM data_pembeli WHERE index_pembeli = " + index );
            
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
    
    public static String firstLetterCaps(String str) {
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
