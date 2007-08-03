/*
 * ProducerDB.java
 *
 * Created on March 10, 2005, 7:54 PM
 */

package com.exzoost.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author knight
 */
public class ProducerDB {
    private Connection conn;
    private ResultSet uprs;
    private Statement stmt;
    
    /** Creates a new instance of ProducerDB */
    public ProducerDB( Connection conn ) {
        //we have to get the references for the connection
        this.conn = conn;
    }
    
    public void initializeDataProducer( JTable e ) {
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT index, nama, alamat, " +
                    "telepon, komentar FROM data_produsen" );
            
            int i = 0;
            while(uprs.next()) {
                
                //if the information from db, add the empty row
                if(i==e.getRowCount()) {
                    DefaultTableModel model = (DefaultTableModel)e.getModel();
                    model.addRow( new Object[] { null, null, null, null, null } );
                }
                
                //fill the table
                e.setValueAt( 
                        firstLetterCaps(uprs.getString("nama")), i, 0 );
                if(uprs.getString("alamat")!=null)
                    e.setValueAt( firstLetterCaps(uprs.getString("alamat")), i, 1 );
                e.setValueAt( uprs.getString("telepon"), i, 2 );
                e.getModel().setValueAt( uprs.getString("komentar"), i, 3 );
                e.getModel().setValueAt( uprs.getInt("index"), i, 4 );
                
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
    
    public boolean insertProducer( JTable e, String name, String address, String phone,
            String comment ) {
        
        //address field may null
        if(address==null||address.trim().equals("")) {
            address = new String("null");
        }
        else {
            address = "'" + address + "'";
        }
        
        //phone field may null
        if(phone==null||phone.trim().equals("")) {
            phone = new String("null");
        }
        else {
            phone = "'" + phone + "'";
        }
        
        //comment field may null
        if(comment==null||comment.trim().equals("")) {
            comment = new String("null");
        }
        else {
            comment = "'" + comment + "'";
        }
        
        //name field may null
        name = "'" + name + "'";
        
        try {
            
            //insert information to database
            stmt = conn.createStatement();
            stmt.executeUpdate( "INSERT INTO data_produsen( nama, komentar, " +
                "alamat, telepon ) VALUES (  " + name + ", " + comment +
                ", " + address + ", " + phone + " )" );
            int i = 0;
            
            while(e.getValueAt(i,0)!=null) {
                i++;
                //if information overloaded, add the row
                if(i==e.getRowCount()) {
                    DefaultTableModel model = (DefaultTableModel)e.getModel();
                    model.addRow( new Object[] { null, null, null, null } );
                }
            }
            
            //get the information back so that you can check the intergrity of db
            uprs = stmt.executeQuery( "SELECT index, nama, alamat, " +
                    "telepon, komentar FROM data_produsen " +
                    "WHERE index = currval('data_produsen_index_seq')" );
            uprs.next();
            
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("nama")), i, 0 );
            if(uprs.getString("alamat")!=null)
                e.setValueAt( firstLetterCaps(uprs.getString("alamat")), i, 1 );
            e.setValueAt( uprs.getString("telepon"), i, 2 );
            e.getModel().setValueAt( uprs.getString("komentar"), i, 3 );
            e.getModel().setValueAt( uprs.getInt("index"), i, 4 );
        
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
    
    public void initializeComboBoxProducer( JComboBox box ) {
        try {
            //first step
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT nama FROM data_produsen" );
            
            while(uprs.next()) {
                box.addItem( firstLetterCaps( uprs.getString("nama") ) );
            }
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    public boolean updateProducer( JTable e, String name, String address, String phone,
            String comment ) {
        //the row to be updated
        int row = e.getSelectedRow();
                
        int producerindex = ((Integer)e.getModel().getValueAt(row,4)).intValue();
        
        //the address field may be null
        if(address==null||address.trim().equals("")) {
            address = new String("null");
        }
        else {
            address = "'" + address + "'";
        }
        
        //the phone field may be null
        if(phone==null||phone.trim().equals("")) {
            phone = new String("null");
        }
        else {
            phone = "'" + phone + "'";
        }
        
        //comment field may null
        if(comment==null||comment.trim().equals("")) {
            comment = new String("null");
        }
        else {
            comment = "'" + comment + "'";
        }
        
        //name may not be null
        name = "'" + name + "'";
        
        try {
            //we want to get the kd_suplier from db
            stmt = conn.createStatement();
            
            
            //update the information in db
            stmt.executeUpdate( "UPDATE data_produsen SET nama = " +
                    name + ", alamat = " + address + ", telepon = " +
                    phone + ", komentar = " + comment + " WHERE index = " + 
                    producerindex );            
            
            //get the information back to check the integrity
            uprs = stmt.executeQuery( "SELECT index, nama, alamat, " +
                    "telepon, komentar FROM data_produsen WHERE index = " + 
                    producerindex );
            uprs.next();           

            e.setValueAt( 
                    firstLetterCaps(uprs.getString("nama")), row, 0 );
            if(uprs.getString("alamat")!=null)
                e.setValueAt( firstLetterCaps(uprs.getString("alamat")), row, 1 );
            e.setValueAt( uprs.getString("telepon"), row, 2 );
            e.getModel().setValueAt( uprs.getString("komentar"), row, 3 );
            e.getModel().setValueAt( uprs.getInt("index"), row, 4 );
        
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
    
    public boolean deleteProducer( JTable e ) {
        //the row to be deleted
        int row = e.getSelectedRow();
        
        //to make sure the primary key is not null
        if(e.getValueAt(row,0)==null)
            return false;
        
        int index = ((Integer)e.getModel().getValueAt(row,4)).intValue();
        
        try {
            //to do operation on db
            stmt = conn.createStatement();
            
            stmt.executeUpdate( "DELETE FROM data_produsen WHERE index = " + index);
            
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
