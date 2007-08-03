/*
 * WarehouseDB.java
 *
 * Created on March 2, 2005, 2:40 AM
 */

package com.exzoost.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author knight
 */
public class WarehouseDB {
    private Connection conn;
    private ResultSet uprs;
    private Statement stmt;
    /** Creates a new instance of WarehouseDB */
    public WarehouseDB( Connection conn ) {
        //we have to get the reference of this connection
        this.conn = conn;
    }
    
    public void initializeDataWarehouse( JTable e ) {
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT nama, alamat, index, " +
                    "komentar FROM data_gudang " );
            
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
                e.setValueAt( 
                        firstLetterCaps(uprs.getString("alamat")), i, 1 );
                                
                //insert comment data too
                e.getModel().setValueAt(uprs.getString("komentar"), i, 2 );
                
                //insert index data too
                e.getModel().setValueAt(uprs.getInt("index"), i, 3 );
                
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
    
    public boolean isEnoughSupplyCancelPurchase( int purchaseindex, String warehouse ) {
        //format the string
        warehouse = warehouse.toLowerCase();
        
        try {
            
            int warehouse_index = getIndexWarehouse(warehouse);
            
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "select t.index_barang, sudah, sum(jumlah) as " +
                    "jumlah from terima_barang_beli t inner join stok_gudang sg " +
                    "using(index_barang) where index_pembelian = " + purchaseindex + 
                    " and index_gudang = " + warehouse_index + " group by t.index_barang, " +
                    "t.sudah" );
            
            while(uprs.next()) {
                if(uprs.getInt("sudah")>uprs.getInt("jumlah")) {
                    JOptionPane.showMessageDialog( null, "<html>The supply in your warehouse " +
                            "is not enough<br> for canceling the purchase transaction.", "Warning", 
                            JOptionPane.WARNING_MESSAGE );
                    return false;
                }
            }
            
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
    
    public int getIndexWarehouse( String name ) {
        //format the string
        name = "'" + name.toLowerCase() + "'";
        
        try {
            stmt = conn.createStatement();
            
            //get the data from db
            uprs = stmt.executeQuery( "SELECT index FROM data_gudang WHERE nama = " + name );
            
            int warehouseindex = 0;
            
            if(uprs.next()) 
                warehouseindex = uprs.getInt("index");
            
            uprs.close();
            stmt.close();
            
            return warehouseindex;
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        return -1;
    }
    
    public boolean deleteWarehouse( JTable e ) {
        //the row that we want to delete
        int row = e.getSelectedRow();
        
        try {
            stmt = conn.createStatement();
            
            String index = ((Integer)e.getModel().getValueAt(row,3)).toString();
            
            //must use the operation
            stmt.executeUpdate( "BEGIN WORK" );
            
            //delete from supply table 
            stmt.executeUpdate( "DELETE FROM stok_gudang WHERE index_gudang = " + index );
            
            //delete from warehouse data table ( master )
            stmt.executeUpdate( "DELETE FROM data_gudang WHERE index = " + index );
            
            //finish the operation
            stmt.executeUpdate( "COMMIT WORK" );
            
            stmt.close();
            
            //delete row from jtable
            DefaultTableModel model = (DefaultTableModel)e.getModel();
            model.removeRow(row);
        }
        catch( SQLException ex ) {
            try {
                if(stmt!=null)
                    stmt.executeUpdate( "ROLLBACK" );
            }
            catch( SQLException x ) {
                x.printStackTrace();
            }
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        
        return true;
        
    }
    
    public boolean insertWarehouse( JTable e, String name, String address, 
            String comment ) { 
        //format the string
        name = "'" + name + "'";
        
        //comment, address and phone can be null
        if(comment.trim().equals("")) 
            comment = "null";
        else
            comment = "'" + comment + "'";
        if(address.trim().equals(""))
            address = "null";
        else
            address = "'" + address + "'";
        
        try {
            //insert information to database
                       
            stmt = conn.createStatement();
            
            //warehouse could not exceed more than 6
            uprs = stmt.executeQuery( "SELECT count(index) AS jumlah FROM data_gudang");
            
            uprs.next();
            
            if(uprs.getInt("jumlah")>=6)
                return false;
            
            stmt.executeUpdate("INSERT INTO data_gudang " +
                    "( nama, alamat, komentar ) VALUES (" + 
                    name + ", " + address + ", " + comment + " )" );
            
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
                        
            uprs = stmt.executeQuery( "SELECT nama, alamat, komentar, " +
                    "index FROM data_gudang WHERE index = " +
                    "currval('kd_gudang_seq')");
            
            //fill the empty row
            uprs.next();
            
            //fill the table

            //capitalize the name
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("nama")), i, 0 );

            //capitalize the address
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("alamat")), i, 1 );

            //insert comment data too
            e.getModel().setValueAt(uprs.getString("komentar"), i, 2 );

            //insert index data too
            e.getModel().setValueAt(uprs.getInt("index"), i, 3 );
                
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
    
    public boolean updateWarehouse( JTable e, String name, String address, String comment ) {
        //the row that to be updated
        int row = e.getSelectedRow();
                
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
            String index = ((Integer)e.getModel().getValueAt(row,3)).toString();
            
            //update the database with new information
            stmt.executeUpdate( "UPDATE data_gudang SET nama = " +
                    name + ", alamat = " + address + ", " +
                    "komentar = " + comment +
                    " WHERE index = " + index );            
            
            //after that, we retrieve the information from database that has
            //been updated to update the row of the jtable            
            uprs = stmt.executeQuery( "SELECT nama, alamat, index, " +
                    "komentar FROM data_gudang WHERE index = " + index );
            uprs.next();
            
            //fill the table
            
            //capitalize the name
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("nama")), row, 0 );

            //capitalize the address
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("alamat")), row, 1 );

            //insert comment data too
            e.getModel().setValueAt(uprs.getString("komentar"), row, 2 );

            //insert index data too
            e.getModel().setValueAt(uprs.getInt("index"), row, 3 );
        
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
