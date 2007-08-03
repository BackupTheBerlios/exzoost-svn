/*
 * salesmanDB.java
 *
 * Created on January 18, 2005, 8:34 PM
 */

package com.exzoost.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author knight
 */
public class SalesmanDB {
    private Connection conn;
    private ResultSet uprs;
    private Statement stmt;
    private DateFormat df;
    private NumberFormat nf;
    
    /** Creates a new instance of salesmanDB */
    public SalesmanDB( Connection conn ) {
        //we have to get the references for the connection
        this.conn = conn;
        
        //format the currency
        nf = NumberFormat.getCurrencyInstance( new Locale("id","id"));

        //format the date to beautiful string
        df = DateFormat.getDateInstance( DateFormat.LONG, Locale.ENGLISH );
    }
    
    public String getName( int index ) {
        String name = "";
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT nama_salesman FROM data_salesman " +
                    "WHERE index_salesman = " + index );
            
            if(uprs.next())
                name = uprs.getString("nama_salesman");
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
    
    public int getIndex( String code ) {
        code = "'" + code + "'";
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT index_salesman FROM data_salesman " +
                    "WHERE nomor_induk = " + code );
            
            uprs.next();
            
            int index = uprs.getInt("index_salesman"); 
            
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
    
    public int getMonthCommision( int month, int year, String name ) {
        //format the string
        name = "'" + name.toLowerCase() + "'";
        
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT sum(komisi_salesman) AS total_com " +
                    "FROM data_transaksi_penjualan " +
                    "WHERE nomor_salesman = ( SELECT index_salesman FROM data_salesman WHERE " +
                    "nama_salesman = " + name + " ) AND extract( month FROM tanggal::timestamp ) = " + 
                    month + " AND extract( year FROM tanggal::timestamp ) = " + year );
            
            int commision = 0;
            
            if(uprs.next())
                commision = uprs.getInt( "total_com" );
            
            uprs.close();
            stmt.close();
            
            return commision;
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
        
        return 0;
    }
    
    public void initializeDataSalesman( JTable e, String status ) {
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT nomor_induk, nama_salesman, gaji," +
                    "telepon, alamat, tempat_lahir, tanggal_lahir, komentar, " +
                    "index_salesman FROM data_salesman WHERE status = " + status );
            
            int i = 0;
            while(uprs.next()) {
                
                //if the information from db, add the empty row
                if(i==e.getRowCount()) {
                    DefaultTableModel model = (DefaultTableModel)e.getModel();
                    model.addRow( new Object[] { null, null, null, null, null, null,
                                            null, null, null } );
                }
                
                //fill the table
                e.setValueAt( uprs.getString("nomor_induk"), i, 0 );
                
                //capitalize the name
                e.setValueAt( 
                        firstLetterCaps(uprs.getString("nama_salesman")) , i, 1 );
                
                //capitalize the address field 
                e.setValueAt( firstLetterCaps(uprs.getString("alamat")), i, 2 );
                
                e.setValueAt( uprs.getString("telepon"), i, 3 );
                
                //beautifulize the salary field
                e.setValueAt( nf.format(uprs.getInt("gaji")), i, 4 );
                
                //beautifulize the date birth field
                e.setValueAt( df.format( uprs.getDate("tanggal_lahir") ), i, 5 );
                
                //capitalize the birth place
                e.setValueAt( 
                        firstLetterCaps(uprs.getString("tempat_lahir")), i, 6 );
                
                //insert the comment data too
                e.getModel().setValueAt( uprs.getString("komentar"), i, 7 );
                
                //insert the index data too
                e.getModel().setValueAt( uprs.getInt("index_salesman"), i, 8 );
                
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
    
    public boolean insertSalesman( JTable e, String name, java.sql.Date date, String address, 
            String phone, String code, String birthplace, String comment, String salary ) { 
        //format the parameter
        name = "'" + name + "'";
        String sdate = "'" + date + "'";
        address = "'" + address + "'";
        if(phone==null||phone.trim().equals(""))
            phone = "null";
        else
            phone = "'" + phone + "'";
        code = "'" + code + "'";
        birthplace = "'" + birthplace + "'";
        if(comment==null||comment.trim().equals(""))
            comment = "null";
        else
            comment = "'" + comment + "'";
        if(salary==null||salary=="0")
            salary = "null";
             
        try {
            //insert information to database            
            stmt = conn.createStatement();
            
            stmt.executeUpdate("INSERT INTO data_salesman ( " +
                    "nomor_induk, nama_salesman, telepon, alamat, tempat_lahir," +
                    "tanggal_lahir, komentar, gaji, status ) VALUES ( " + code + 
                    ", " + name + ", " + phone + ", " + address + ", " + birthplace +
                    ", " + sdate + ", " + comment + ", " + salary + ", " + "TRUE ) ");
            
            int i = 0;
            while(e.getValueAt(i,0)!=null) {
                i++;
                //if information is "overloaded", add the empty row
                if(i==e.getRowCount()) {
                    DefaultTableModel model = (DefaultTableModel)e.getModel();
                    model.addRow( new Object[] { null, null, null, null, null, null,
                                                null, null, null } );
                }
            }
            
            //get the information from database to fill the empty row
            stmt = conn.createStatement();
            uprs = stmt.executeQuery( "SELECT nomor_induk, index_salesman, komentar," +
                "nama_salesman,telepon,alamat,tempat_lahir,tanggal_lahir, gaji FROM " +
                "data_salesman WHERE index_salesman = currval('index_salesman') " +
                "AND status = TRUE" );
            uprs.next();
            
            //fill the table
            e.setValueAt( uprs.getString("nomor_induk"), i, 0 );

            //capitalize the name
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("nama_salesman")) , i, 1 );

            //capitalize the address field 
            e.setValueAt( firstLetterCaps(uprs.getString("alamat")), i, 2 );

            e.setValueAt( uprs.getString("telepon"), i, 3 );

            //beautifulize the salary field
            e.setValueAt( nf.format(uprs.getInt("gaji")), i, 4 );

            //beautifulize the date birth field
            e.setValueAt( df.format( uprs.getDate("tanggal_lahir") ), i, 5 );

            //capitalize the birth place
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("tempat_lahir")), i, 6 );

            //insert the comment data too
            e.getModel().setValueAt( uprs.getString("komentar"), i, 7 );

            //insert the index data too
            e.getModel().setValueAt( uprs.getInt("index_salesman"), i, 8 );
            
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
    
    public boolean updateSalesman( JTable e, String number, String name, String address, 
            String phone, java.sql.Date date, String birthplace, String comment,
            String salary) {
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
        
        //the other field must not be null
        number = "'" + number + "'";
        name = "'" + name + "'";
        address = "'" + address + "'";
        String dateString = "'" + date.toString() + "'";
        birthplace = "'" + birthplace + "'";

        //index from the row to be updated
        String index = ((Integer)e.getModel().getValueAt(row,8)).toString();
        
        try {
            stmt = conn.createStatement();
            
            //update the database with new information
            stmt.executeUpdate( "UPDATE data_salesman SET nama_salesman = " +
                    name + ", alamat = " + address + ", telepon = " + phone +
                    ", tempat_lahir = " + birthplace + ", tanggal_lahir = " + dateString +
                    ", nomor_induk = " + number + ", komentar = " + comment +
                    ", gaji = " + salary + " WHERE index_salesman = " + index  );            
            
            //after that, we retrieve the information from database that has
            //been updated to update the row of the jtable
            uprs = stmt.executeQuery( "SELECT nama_salesman, alamat, telepon, " +
                    "tempat_lahir, tanggal_lahir, nomor_induk, komentar, gaji, " +
                    "index_salesman FROM data_salesman WHERE index_salesman = " + index );
            uprs.next();
            //fill the table
            e.setValueAt( uprs.getString("nomor_induk"), row, 0 );

            //capitalize the name
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("nama_salesman")) , row, 1 );

            //capitalize the address field 
            e.setValueAt( firstLetterCaps(uprs.getString("alamat")), row, 2 );

            e.setValueAt( uprs.getString("telepon"), row, 3 );

            //beautifulize the salary field
            e.setValueAt( nf.format(uprs.getInt("gaji")), row, 4 );

            //beautifulize the date birth field
            e.setValueAt( df.format( uprs.getDate("tanggal_lahir") ), row, 5 );

            //capitalize the birth place
            e.setValueAt( 
                    firstLetterCaps(uprs.getString("tempat_lahir")), row, 6 );

            //insert the comment data too
            e.getModel().setValueAt( uprs.getString("komentar"), row, 7 );

            //insert the index data too
            e.getModel().setValueAt( uprs.getInt("index_salesman"), row, 8 );
        
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
    
    public boolean reactiveSalesman( int index ) {
        try {
            stmt = conn.createStatement();
            
            stmt.executeUpdate( "UPDATE data_salesman SET status = TRUE WHERE index_salesman = " +
                    + index );
            
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
            return false;
        }
        return true;
    }
    
    public boolean deleteSalesman( JTable e ) {
        //the row that we want to delete
        int row = e.getSelectedRow();
        
        //index from the row to be updated
        String index = ((Integer)e.getModel().getValueAt(row,8)).toString();
        
        try {
            stmt = conn.createStatement();
            
            stmt.executeUpdate( "DELETE FROM data_salesman WHERE index_salesman = " +
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
    
    public void makeNotActiveSalesman( JTable e ) {
        //the row that we want to delete
        int row = e.getSelectedRow();
        
        try {
            stmt = conn.createStatement();
            String index = ((Integer)e.getModel().getValueAt(row,8)).toString();
            
            stmt.executeUpdate("UPDATE data_salesman SET status = FALSE " +
                    "WHERE index_salesman = " + index );
            
            stmt.close();
            
            //delete row from jtable
            DefaultTableModel model = (DefaultTableModel)e.getModel();
            model.removeRow(row);
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    public void makeActiveSalesman( JTable e ) {
        //the row that we want to delete
        int row = e.getSelectedRow();
        
        try {
            stmt = conn.createStatement();
            String index = ((Integer)e.getModel().getValueAt(row,8)).toString();
            
            stmt.executeUpdate("UPDATE data_salesman SET status = FALSE " +
                    "WHERE index_salesman = " + index );
            
            stmt.close();
            
            //delete row from jtable
            DefaultTableModel model = (DefaultTableModel)e.getModel();
            model.removeRow(row);
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
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
