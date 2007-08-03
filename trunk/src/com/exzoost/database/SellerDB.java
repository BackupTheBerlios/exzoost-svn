/*
 * SellerDB.java
 *
 * Created on January 18, 2005, 10:53 PM
 */

package com.exzoost.database;

import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
/**
 *
 * @author knight
 */
public class SellerDB {
    private Connection conn;
    private ResultSet uprs;
    private Statement stmt;
    /** Creates a new instance of SellerDB */
    public SellerDB( Connection conn ) {
        //we have to get the references for the connection
        this.conn = conn;
    }
    
    public void initializeDataSeller( JTable e ) {
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT kd_suplier, nama_suplier, alamat_suplier, " +
                    "telepon_suplier, komentar FROM data_suplier" );
            
            int i = 0;
            while(uprs.next()) {
                
                //if the information from db, add the empty row
                if(i==e.getRowCount()) {
                    DefaultTableModel model = (DefaultTableModel)e.getModel();
                    model.addRow( new Object[] { null, null, null, null, null } );
                }
                
                //fill the table
                e.setValueAt( 
                        firstLetterCaps(uprs.getString("nama_suplier")), i, 0 );
                if(uprs.getString("alamat_suplier")!=null)
                    e.setValueAt( firstLetterCaps(uprs.getString("alamat_suplier")), i, 1 );
                e.setValueAt( uprs.getString("telepon_suplier"), i, 2 );
                e.getModel().setValueAt( uprs.getString("komentar"), i, 3 );
                e.getModel().setValueAt( uprs.getInt("kd_suplier"), i, 4 );
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
    
    public boolean insertSeller( JTable e, String name, String address, String phone,
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
            stmt.executeUpdate( "INSERT INTO data_suplier( nama_suplier, komentar, " +
                "alamat_suplier, telepon_suplier ) VALUES (  " + name + ", " + comment +
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
            uprs = stmt.executeQuery( "SELECT kd_suplier, nama_suplier, alamat_suplier, " +
                    "telepon_suplier, komentar FROM data_suplier " +
                    "WHERE kd_suplier = currval('kd_suplier_seq')" );
            uprs.next();
            
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("nama_suplier")), i, 0 );
            if(uprs.getString("alamat_suplier")!=null)
                e.setValueAt( firstLetterCaps(uprs.getString("alamat_suplier")), i, 1 );
            e.setValueAt( uprs.getString("telepon_suplier"), i, 2 );
            e.getModel().setValueAt( uprs.getString("komentar"), i, 3 );
            e.getModel().setValueAt( uprs.getInt("kd_suplier"), i, 4 );
        
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
    
    public boolean updateSeller( JTable e, String name, String address, String phone,
            String comment ) {
        //the row to be updated
        int row = e.getSelectedRow();
                
        int supliercode = ((Integer)e.getModel().getValueAt(row,4)).intValue();
        
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
            stmt.executeUpdate( "UPDATE data_suplier SET nama_suplier = " +
                    name + ", alamat_suplier = " + address + ", telepon_suplier = " +
                    phone + ", komentar = " + comment + " WHERE kd_suplier = " + 
                    supliercode );            
            
            //get the information back to check the integrity
            uprs = stmt.executeQuery( "SELECT kd_suplier, nama_suplier, alamat_suplier, " +
                    "telepon_suplier, komentar FROM data_suplier WHERE kd_suplier = " + 
                    supliercode );
            uprs.next();           

            e.setValueAt( 
                    firstLetterCaps(uprs.getString("nama_suplier")), row, 0 );
            if(uprs.getString("alamat_suplier")!=null)
                e.setValueAt( firstLetterCaps(uprs.getString("alamat_suplier")), row, 1 );
            e.setValueAt( uprs.getString("telepon_suplier"), row, 2 );
            e.getModel().setValueAt( uprs.getString("komentar"), row, 3 );
            e.getModel().setValueAt( uprs.getInt("kd_suplier"), row, 4 );
        
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
    
    public void initializeSellerItem( DefaultListModel listModel ) {
        try {
            //ok, the first step
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT nama_suplier FROM data_suplier ORDER BY " +
                    "nama_suplier DESC" );
            
            while(uprs.next()) {
                listModel.insertElementAt( 
                        firstLetterCaps( uprs.getString( "nama_suplier" ) ), 0 );
            }
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    public void initializeSuplierForItem( DefaultListModel listModel, String itemname ) {
        //format the string
        itemname = "'" + itemname.toLowerCase() + "'";
        
        try {
            //ok, the first step
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT t.nama_suplier FROM suplier_barang s " +
                    "INNER JOIN data_barang d ON d.index = s.index_barang INNER JOIN " +
                    "data_suplier t ON t.kd_suplier = s.index_suplier WHERE d.nama = " + itemname +
                    " GROUP BY d.nama, t.nama_suplier" );
            
            while(uprs.next()) {
                listModel.insertElementAt( 
                        firstLetterCaps( uprs.getString( "nama_suplier" ) ), 0 );
            }
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    public void harmonizeSellerItem( DefaultListModel sourceModel, DefaultListModel targetModel ) {
        //we want to remove items from sourceModel
        for( int i=0; i<targetModel.getSize(); i++ ) {
            sourceModel.removeElement( targetModel.getElementAt(i) );
        }
    }
    
    public boolean insertItemSeller( DefaultListModel list, String name ) {
        List<Integer> itemindex = new ArrayList<Integer>();
        
        //format the string
        name = "'" + name.toLowerCase() + "'";
        
        try {
            //as usual
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT index FROM data_barang WHERE nama = " + name );
            
            while(uprs.next()) {
                itemindex.add( uprs.getInt( "index" ) );
            }
            
            //must use the operation
            stmt.executeUpdate( "BEGIN WORK" );
            
            //Let's clear the data, ok....
            for( int i=0; i<itemindex.size(); i++ ) {
                stmt.executeUpdate( "DELETE FROM suplier_barang WHERE index_barang = " + 
                        itemindex.get(i) );
            }
            
            int sellerindex = 0;
            String sellername = "";
            for( int i=0; i<list.getSize(); i++) {
                sellername = "'" + ((String)list.get(i)).toLowerCase() + "'"; 
                uprs = stmt.executeQuery( "SELECT kd_suplier FROM data_suplier WHERE nama_suplier = " +
                         sellername );
                uprs.next();
                sellerindex = uprs.getInt("kd_suplier");
                for( int j=0; j<itemindex.size(); j++ ) {
                    stmt.executeUpdate( "INSERT INTO suplier_barang ( index_barang, index_suplier ) " +
                            "VALUES ( " + itemindex.get(j) + ", " + sellerindex + " ) " );
                }
            }
            
            //finish it
            stmt.executeUpdate( "COMMIT WORK" );
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        return true;
    }
    
    
    public void moveSellerItem( JList sourceList, JList targetList ) {
        DefaultListModel sourceModel = (DefaultListModel)sourceList.getModel();
        DefaultListModel targetModel = (DefaultListModel)targetList.getModel();
        
        //if user don't choose any row
        if(sourceList.isSelectionEmpty())
            return;
        
        //add to the target list
        targetModel.addElement(sourceList.getSelectedValue());
        
        //remove from the source list
        sourceModel.removeElement(sourceList.getSelectedValue());
    }
    
    public boolean deleteSeller( JTable e ) {
        //the row to be deleted
        int row = e.getSelectedRow();
        
        //to make sure the primary key is not null
        if(e.getValueAt(row,0)==null)
            return false;
        
        int code = ((Integer)e.getModel().getValueAt(row,4)).intValue();
        
        try {
            //to do operation on db
            stmt = conn.createStatement();
            
            stmt.executeUpdate( "DELETE FROM data_suplier WHERE kd_suplier = " + code);
            
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
    
    public class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {
        public MyComboBoxRenderer(Object[] items) {
            super(items);
        }
    
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
    
            // Select the current value
            setSelectedItem(value);
            return this;
        }
    }
    
}
