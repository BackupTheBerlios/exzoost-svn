/*
 * ReportDB.java
 *
 * Created on March 17, 2005, 2:13 AM
 */

package com.exzoost.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author knight
 */
public class ReportDB {
    private Connection conn;
    private Statement stmt;
    private ResultSet uprs;
    /** Creates a new instance of ReportDB */
    public ReportDB( Connection conn ) {
        this.conn = conn;
    }
    
    public int TotalOutcomeValue( String datefrom, String dateto ) {
        int total = 0;
        
        //format the string
        datefrom = "'" + datefrom + "'";
        dateto = "'" + dateto + "'";
        
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT sum(jumlah) AS total_beban FROM transaksi_pengeluaran " +
                    "WHERE tanggal BETWEEN " + datefrom + " AND " + dateto );
            
            uprs.next();
            
            total = uprs.getInt("total_beban");
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
        return total;
    }
    
    public int TotalOutcomeValue( int month, int year ) {
        int total = 0;
        
        if(month==0) {
            month = 12;
            year -= 1;
        }
        
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT sum(jumlah) AS total_beban FROM transaksi_pengeluaran " +
                    "WHERE extract(month FROM tanggal::timestamp) = " + month + 
                    " AND extract(year FROM tanggal::timestamp) = " + year );
            
            uprs.next();
            
            total = uprs.getInt("total_beban");
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
        return total;
    }
    
    public int TotalSalaryPaymentValue( String datefrom, String dateto ) {
        int total = 0;
        
        //format the string
        datefrom = "'" + datefrom + "'";
        dateto = "'" + dateto + "'";
        
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT sum(gaji) AS total_gaji FROM " +
                    "data_transaksi_gaji d INNER JOIN transaksi_gaji t ON " +
                    "( index = t.index_transaksi_gaji ) WHERE tanggal_gaji " +
                    "BETWEEN " + datefrom + " AND " + dateto );
            
            uprs.next();
            
            total += uprs.getInt( "total_gaji" );
            
            uprs = stmt.executeQuery("SELECT sum(gaji) AS total_gaji FROM " +
                    "data_transaksi_gaji d INNER JOIN transaksi_gaji_salesman t ON " +
                    "( index = t.index_transaksi_gaji ) WHERE tanggal_gaji " +
                    "BETWEEN " + datefrom + " AND " + dateto );
            
            uprs.next();
            
            total += uprs.getInt("total_gaji");
            
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
        
        return total;
    }
    
    public int TotalSalaryPaymentValue( int month, int year ) {
        int total = 0;
        
        if(month==0) {
            month = 12;
            year -= 1;
        }
        
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT sum(gaji) AS total_gaji FROM " +
                    "data_transaksi_gaji d " +
                    "INNER JOIN transaksi_gaji t ON( index = index_transaksi_gaji ) " +
                    "WHERE extract(month FROM tanggal_gaji::timestamp) = " + month + 
                    " AND extract(year FROM tanggal_gaji::timestamp) = " + year );
            
            uprs.next();
            
            total += uprs.getInt( "total_gaji" );
            
            uprs = stmt.executeQuery( "SELECT sum(gaji) AS total_gaji FROM " +
                    "data_transaksi_gaji d " +
                    "INNER JOIN transaksi_gaji_salesman t ON( index = index_transaksi_gaji ) " +
                    "WHERE extract(month FROM tanggal_gaji::timestamp) = " + month + 
                    " AND extract(year FROM tanggal_gaji::timestamp) = " + year );
            
            uprs.next();
            
            total += uprs.getInt( "total_gaji" );
            
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
        
        return total;
    }
    
    public int TotalSaleValue( String datefrom, String dateto ) {
        int total = 0;
        
        //format the string
        datefrom = "'" + datefrom + "'";
        dateto = "'" + dateto + "'";
        
        try {
            //always begin with this
            stmt = conn.createStatement();
                        
            uprs = stmt.executeQuery("select sum(harga_jual * jumlah) as total_jual " +
                    "from transaksi_barang_penjualan t " +
                    "inner join data_transaksi_penjualan d " +
                    "on d.index = t.index_penjualan " +
                    "where tanggal between " + datefrom + " and " + dateto);
            
            uprs.next();
            
            total = uprs.getInt( "total_jual" );
            
            //free the resources
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
        
        return total;
    }
    
    public int TotalSaleValue( int month, int year ) {
        int total = 0;
        if(month==0) {
            month = 12;
            year -= 1;
        }
                
        try {
            //always begin with this
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery("select sum(harga_jual * jumlah) as total_jual " +
                    "from transaksi_barang_penjualan t " +
                    "inner join data_transaksi_penjualan d " +
                    "on d.index = t.index_penjualan " +
                    "where extract(month FROM tanggal::timestamp) = " + month + 
                    " AND extract(year FROM tanggal::timestamp) = " + year);
                        
            uprs.next();
            
            total = uprs.getInt( "total_jual" );
            
            //free the resources
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
        
        return total;
    }
    
    public int TotalPurchaseValue( String datefrom, String dateto ) {
        int total = 0;
        
        //format the string
        datefrom = "'" + datefrom + "'";
        dateto = "'" + dateto + "'";
        
        try {
            //always begin with this
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "select sum(harga_beli * jumlah) as total_beli " +
                    "from transaksi_barang_pembelian t " +
                    "inner join data_transaksi_pembelian d " +
                    "on d.index = t.index_pembelian " +
                    "where tanggal BETWEEN " + datefrom + " AND " + dateto );
            
            uprs.next();
            
            total = uprs.getInt( "total_beli" );
            
            //free the resources
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
        
        return total;
    }
    
    public int TotalPurchaseValue( int month, int year ) {
        int total = 0;
        if(month==0) {
            month = 12;
            year -= 1;
        }
        
        try {
            //always begin with this
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "select sum(harga_beli * jumlah) as total_beli " +
                    "from transaksi_barang_pembelian t " +
                    "inner join data_transaksi_pembelian d " +
                    "on d.index = t.index_pembelian " +
                    "where extract(month FROM tanggal::timestamp) = " + 
                    month + " AND extract(year FROM tanggal::timestamp) = " + year);
            
            uprs.next();
            
            total = uprs.getInt( "total_beli" );
            
            //free the resources
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
        
        return total;
    }
    
    public int TotalSalesmanCommisionValue( String datefrom, String dateto ) {
        int total = 0;
        
        //format the string
        datefrom = "'" + datefrom + "'";
        dateto = "'" + dateto + "'";
        
        try {
            //always begin with this
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT sum(komisi_salesman) AS total_komisi_salesman " +
                    "FROM data_transaksi_penjualan WHERE nomor_salesman IS NOT null " +
                    "AND tanggal BETWEEN " + datefrom + " AND " + dateto );
            
            uprs.next();
            
            total = uprs.getInt( "total_komisi_salesman" );
            
            //free the resources
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
        
        return total;
    }
    
    public int TotalSalesmanCommisionValue( int month, int year ) {
        int total = 0;
        if(month==0) {
            month = 12;
            year -= 1;
        }
        
        try {
            //always begin with this
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT sum(komisi_salesman) AS total_komisi_salesman " +
                    "FROM data_transaksi_penjualan WHERE nomor_salesman IS NOT null " +
                    "AND extract(month FROM tanggal::timestamp) = " + month  + 
                    " AND extract(year FROM tanggal::timestamp) = " + year );
            
            uprs.next();
            
            total = uprs.getInt( "total_komisi_salesman" );
            
            //free the resources
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
        
        return total;
    }
    
}
