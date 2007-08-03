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
public class CommisionerDB {
    private Connection conn;
    private ResultSet uprs;
    private Statement stmt;
    /** Creates a new instance of CustomerDB */
    public CommisionerDB( Connection conn ) {
        //we have to get the references for the connection
        this.conn = conn;
    }
    
    public void initializeDataCommisioner( JTable e ) {
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT kd_komisioner, nama_komisioner, " +
                    "alamat_komisioner, telepon_komisioner, " +
                    "komentar FROM data_komisioner " );
            
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
                        firstLetterCaps(uprs.getString("nama_komisioner")), i, 0 );
                
                //capitalize the address
                e.setValueAt( 
                        firstLetterCaps(uprs.getString("alamat_komisioner")), i, 1 );
                
                e.setValueAt( uprs.getString("telepon_komisioner"), i, 2 );
                
                //insert comment data too
                e.getModel().setValueAt(uprs.getString("komentar"), i, 3 );
                
                //insert index data too
                e.getModel().setValueAt(uprs.getInt("kd_komisioner"), i, 4 );
                
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
    
    public String getName( int index ) {
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT nama_komisioner FROM data_komisioner " +
                    "WHERE kd_komisioner = " + index );
            
            uprs.next();
            
            String name = uprs.getString("nama_komisioner"); 
            
            uprs.close();
            stmt.close();
            
            return name;
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        return "";
    }
    
    public int getIndex( String name ) {
        name = "'" + name + "'";
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT kd_komisioner FROM data_komisioner " +
                    "WHERE nama_komisioner = " + name );
            
            uprs.next();
            
            int index = uprs.getInt("kd_komisioner"); 
            
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
    
    public boolean insertCommisioner( JTable e, String name, String address, 
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
            stmt.executeUpdate("INSERT INTO data_komisioner " +
                    "( nama_komisioner, alamat_komisioner, telepon_komisioner, komentar) " +
                    "VALUES(" + name + ", "+ address + ", " + phone + ", "+ comment + ")" );
                        
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
            uprs = stmt.executeQuery( "SELECT nama_komisioner, alamat_komisioner, " +
                    "telepon_komisioner, komentar, kd_komisioner " +
                    "FROM data_komisioner WHERE kd_komisioner = " +
                    "currval('kd_komisioner_seq')");
            
            //fill the empty row
            uprs.next();
            
            //fill the table

            //capitalize the name
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("nama_komisioner")), i, 0 );

            //capitalize the address
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("alamat_komisioner")), i, 1 );

            e.setValueAt( uprs.getString("telepon_komisioner"), i, 2 );

            //insert comment data too
            e.getModel().setValueAt(uprs.getString("komentar"), i, 3 );

            //insert index data too
            e.getModel().setValueAt(uprs.getInt("kd_komisioner"), i, 4 );
                
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
    
    public boolean updateCommisioner( JTable e, String name, String address, String phone,  
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
            stmt.executeUpdate( "UPDATE data_komisioner SET nama_komisioner = " +
                    name + ", alamat_komisioner = " + address + ", telepon_komisioner = " + 
                    phone + ", komentar = " + comment + " WHERE kd_komisioner = " + index );
            
            //after that, we retrieve the information from database that has
            //been updated to update the row of the jtable            
            uprs = stmt.executeQuery( "SELECT nama_komisioner, alamat_komisioner, " +
                    "telepon_komisioner, kd_komisioner, " +
                    "komentar FROM data_komisioner WHERE kd_komisioner = " + index );
            uprs.next();
            
            //fill the table
            
            //capitalize the name
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("nama_komisioner")), row, 0 );

            //capitalize the address
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("alamat_komisioner")), row, 1 );

            e.setValueAt( uprs.getString("telepon_komisioner"), row, 2 );

            //insert comment data too
            e.getModel().setValueAt(uprs.getString("komentar"), row, 3 );

            //insert index data too
            e.getModel().setValueAt(uprs.getInt("kd_komisioner"), row, 4 );
        
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
    
    public boolean deleteCommisioner( JTable e ) {
        //the row that we want to delete
        int row = e.getSelectedRow();
        
        try {
            stmt = conn.createStatement();
            String index = ((Integer)e.getModel().getValueAt(row,4)).toString();
                
            stmt.executeUpdate( "DELETE FROM data_komisioner WHERE kd_komisioner = " + 
                    index );
            
            stmt.close();
            
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
