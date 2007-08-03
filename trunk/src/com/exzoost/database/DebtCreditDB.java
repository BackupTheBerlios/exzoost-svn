/*
 * DebtCreditDB.java
 *
 * Created on February 24, 2005, 11:49 PM
 */

package com.exzoost.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author knight
 */
public class DebtCreditDB {
    private Connection conn;
    private ResultSet uprs;
    private Statement stmt;
    private NumberFormat nf;
    private DateFormat df;
    
    /** Creates a new instance of DebtCreditDB */
    public DebtCreditDB( Connection conn ) {
        this.conn = conn;
        
        //format the currency
        nf = NumberFormat.getCurrencyInstance( new Locale("id","id"));
        
        //format the date to beautiful string
        df = DateFormat.getDateInstance( DateFormat.LONG, Locale.ENGLISH );
    }
    
    //piutang or in credit table
    public void initializeDataCredit( JTable e, String sorting ) {
        if(sorting.equals("Invoice")) {
            sorting = "invoice";
        }
        else if(sorting.equals("Date")) {
            sorting = "tanggal DESC";
        }
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT invoice, tanggal, index_penjualan, belum_bayar, " +
                    "sudah_bayar, belum_bayar + sudah_bayar AS total_transaksi " +
                    "FROM piutang p INNER JOIN data_transaksi_penjualan d " +
                    "ON( index = index_penjualan ) ORDER BY " + sorting );
            
            int i = 0;
            while(uprs.next()) {
                
                //if the information from db, add the empty row
                if(i==e.getRowCount()) {
                    DefaultTableModel model = (DefaultTableModel)e.getModel();
                    model.addRow( new Object[] { null, null, null, null, null, null } );
                }
                
                //fill the table
                
                //the invoice of the transaction
                e.getModel().setValueAt( 
                        uprs.getString("invoice"), i, 0 );
                
                //date
                e.getModel().setValueAt( 
                        df.format(uprs.getDate("tanggal")), i, 1 );
                
                //must paid....
                e.getModel().setValueAt( 
                        nf.format(uprs.getInt("belum_bayar")), i, 2 );
                
                //have paid....
                e.getModel().setValueAt(
                        nf.format(uprs.getInt("sudah_bayar")), i, 3 );
                
                //total transaction
                e.getModel().setValueAt(
                        nf.format(uprs.getInt("total_transaksi")), i, 4 );
                
                //index of sale transaction
                e.getModel().setValueAt(
                        uprs.getInt("index_penjualan"), i, 5 );
                
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
    
    //utang or in debt table
    public void initializeDataDebit( JTable e, String sorting ) {
        if(sorting.equals("Invoice")) {
            sorting = "invoice";
        }
        else if(sorting.equals("Date")) {
            sorting = "tanggal DESC";
        }
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            uprs = stmt.executeQuery( "SELECT invoice, tanggal, index_pembelian, belum_bayar, " +
                    "sudah_bayar, belum_bayar + sudah_bayar AS total_transaksi " +
                    "FROM utang p INNER JOIN data_transaksi_pembelian d " +
                    "ON( index = index_pembelian ) ORDER BY " + sorting );
            
            int i = 0;
            while(uprs.next()) {
                
                //if the information from db, add the empty row
                if(i==e.getRowCount()) {
                    DefaultTableModel model = (DefaultTableModel)e.getModel();
                    model.addRow( new Object[] { null, null, null, null, null, null } );
                }
                
                //fill the table
                
                //the invoice of the transaction
                e.getModel().setValueAt( 
                        uprs.getString("invoice"), i, 0 );
                
                //date
                e.getModel().setValueAt( 
                        df.format(uprs.getDate("tanggal")), i, 1 );
                
                //must paid....
                e.getModel().setValueAt( 
                        nf.format(uprs.getInt("belum_bayar")), i, 2 );
                
                //have paid....
                e.getModel().setValueAt(
                        nf.format(uprs.getInt("sudah_bayar")), i, 3 );
                
                //total transaction
                e.getModel().setValueAt(
                        nf.format(uprs.getInt("total_transaksi")), i, 4 );
                
                //index of sale transaction
                e.getModel().setValueAt(
                        uprs.getInt("index_pembelian"), i, 5 );
                
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
