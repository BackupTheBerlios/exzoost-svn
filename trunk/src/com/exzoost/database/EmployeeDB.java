/*
 * employeeDB.java
 *
 * Created on January 18, 2005, 10:05 PM
 */

package com.exzoost.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author knight
 */
public class EmployeeDB {
    private Connection conn;
    private ResultSet uprs;
    private Statement stmt;
    private DateFormat df;
    private NumberFormat nf;
    /** Creates a new instance of employeeDB */
    public EmployeeDB( Connection conn ) {
        //we have to get the references for the connection
        this.conn = conn;

        //format the currency
        nf = NumberFormat.getCurrencyInstance( new Locale("id","id"));
        
        //format the date to beautiful string
        df = DateFormat.getDateInstance( DateFormat.LONG, Locale.ENGLISH );
    }
    
    public int getMonthSalaryDailyEmployee( int dailysalary, java.util.Date date ) {
        GregorianCalendar gregorian = new GregorianCalendar();
        gregorian.setTime(date);
        return gregorian.getActualMaximum(Calendar.DAY_OF_MONTH) * dailysalary;
        
    }
    
    public void initializeDataEmployee( JTable e, String status, String type ) {
        //format the string
        type = "'" + type.toLowerCase() + "'";
        
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT nomor_induk, nama_karyawan, posisi, gaji," +
                    "telepon, alamat, tempat_lahir, tanggal_lahir, komentar, index, " +
                    "status FROM data_karyawan WHERE status = " + status + " AND tipe = " + type );
            
            int i = 0;
            while(uprs.next()) {
                
                //if the information from db, add the empty row
                if(i==e.getRowCount()) {
                    DefaultTableModel model = (DefaultTableModel)e.getModel();
                    model.addRow( new Object[] { null, null, null, null, null, null, 
                            null, null, null, null } );
                }
                
                //fill the table
                e.setValueAt( uprs.getString("nomor_induk"), i, 0 );
                
                //capitalize the name
                e.setValueAt( 
                        firstLetterCaps(uprs.getString("nama_karyawan")), i, 1 );
                
                e.setValueAt( 
                        firstLetterCaps(uprs.getString("alamat")), i, 2 );
                
                //capitalize the position
                e.setValueAt( 
                        firstLetterCaps(uprs.getString("posisi")), i, 3 );
                
                e.setValueAt( nf.format(uprs.getInt("gaji")), i, 4 );
                e.setValueAt( uprs.getString("telepon"), i, 5 );
                
                e.setValueAt( df.format( uprs.getDate("tanggal_lahir") ), i, 6 );
                
                //capitalize the birth place
                e.setValueAt( 
                        firstLetterCaps(uprs.getString("tempat_lahir")), i, 7 );
                
                //insert comment data too
                e.getModel().setValueAt(uprs.getString("komentar"), i, 8 );
                
                //insert index data too
                e.getModel().setValueAt(uprs.getInt("index"), i, 9 );
                
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
    
    public boolean insertEmployee( JTable e, String name, java.sql.Date date, String address, 
            String phone, String number, int salary, String position, String birthplace,
            String comment, String type ) { 
        //format the string
        name = "'" + name + "'";
        String sdate = "'" + date + "'";
        address = "'" + address + "'";
        number = "'" + number + "'";
        position = "'" + position + "'";
        birthplace = "'" + birthplace + "'";
        type = "'" + type + "'";
        
        //comment and phone can be null
        if(comment.trim().equals("")) 
            comment = "null";
        else
            comment = "'" + comment + "'";
        if(phone.trim().equals(""))
            phone = "null";
        else
            phone = "'" + phone + "'";
        
        try {
            //insert information to database
                       
            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO data_karyawan " +
                    "( nomor_induk, nama_karyawan, posisi, gaji, telepon, alamat," +
                    "tempat_lahir, tanggal_lahir, komentar, status, tipe ) " +
                    "VALUES (" + number + ", " + name + ", " + position + ", " + 
                    salary + ", " + phone + ", " + address + ", " + birthplace + ", " +
                    sdate + ", " + comment + ", TRUE, " + type + " )" );
                  
            stmt.close();
        }
        catch( Throwable ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        
        return true;
        
    }
    
    public boolean updateEmployee( JTable e, String number, String name, String address, 
            String position, int salary, String phone, java.sql.Date date, 
            String birthplace, String comment, String type ) {
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
        position = "'" + position + "'";
        String dateString = "'" + date.toString() + "'";
        birthplace = "'" + birthplace + "'";
        type = "'" + type + "'";
        
        try {
            stmt = conn.createStatement();
            
            //nomorinduk from the row to be updated
            String nomorinduk = (String)e.getValueAt(row,0);
            
            //update the database with new information
            stmt.executeUpdate( "UPDATE data_karyawan SET nama_karyawan = " +
                    name + ", alamat = " + address + ", posisi = " +
                    position + ", gaji = " + salary + ", telepon = " + phone +
                    ", tempat_lahir = " + birthplace + ", tanggal_lahir = " + dateString +
                    ", nomor_induk = " + number + ", komentar = " + comment + ", tipe = " + type +
                    " WHERE nomor_induk = '" + nomorinduk + "'");            
           
            stmt.close();
        }
        catch( Throwable ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        
        return true;
    }
    
    public int getIndex( String code ) {
        code = "'" + code + "'";
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT index FROM data_karyawan " +
                    "WHERE nomor_induk = " + code );
            
            uprs.next();
            
            int index = uprs.getInt("index"); 
            
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
    
    public boolean deleteEmployee( JTable e ) {
        //the row that we want to delete
        int row = e.getSelectedRow();
        
        try {
            stmt = conn.createStatement();
            String index = ((Integer)e.getModel().getValueAt(row,9)).toString();
                
            stmt.executeUpdate( "DELETE FROM data_karyawan WHERE index = " + index );
            
            stmt.close();
            
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        
        return true;
        
    }
    
    public boolean makeNotActiveEmployee( JTable e ) {
        //the row that we want to delete
        int row = e.getSelectedRow();
        
        try {
            stmt = conn.createStatement();
            String index = "'" + (Integer)e.getModel().getValueAt(row,9) + "'";
            
            stmt.executeUpdate("UPDATE data_karyawan SET status = FALSE " +
                    "WHERE index = " + index );
            
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
    
    public boolean reactiveEmployee( int index ) {
        
        try {
            stmt = conn.createStatement();
            
            stmt.executeUpdate("UPDATE data_karyawan SET status = TRUE " +
                    "WHERE index = " + index );
            
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
