/*
 * Transaction.java
 *
 * Created on November 18, 2004, 10:45 AM
 */

package com.exzoost.database;

import com.toedter.calendar.JDateChooser;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author knight
 */
public class Transaction {
    private Properties props;
    private Connection conn;
    private Statement stmt;
    private ResultSet uprs;
    private NumberFormat nf;
    private DateFormat df;
    /** Creates a new instance of Transaction */
    
    //Transaction class job is to do transaction related db operation
    public Transaction( Connection conn ) {
        this.conn = conn;
        
        //format the currency
        nf = NumberFormat.getCurrencyInstance( new Locale("id","id"));
        
        //format the date to beautiful string
        df = DateFormat.getDateInstance( DateFormat.LONG, Locale.ENGLISH );
    }
    
    //for edit transaction, initialize the sale transaction
    public void InitializeSaleEditTransaction( JTable table, String sorting ) {
        if(sorting.equals("Invoice")) {
            sorting = "invoice";
        } else if(sorting.equals("Date")) {
            sorting = "tanggal DESC";
        }
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT invoice, tanggal, nama_salesman, b.nama AS " +
                    "nama_pembeli, komisi_salesman, index, belum_bayar + sudah_bayar AS " +
                    "total_transaksi FROM data_transaksi_penjualan LEFT JOIN " +
                    "data_salesman ON ( nomor_salesman = index_salesman ) LEFT JOIN data_pembeli b " +
                    "ON( index_pembeli = nomor_pembeli ) LEFT JOIN piutang p ON ( p.index_penjualan = " +
                    " index ) ORDER BY " + sorting );
            
            int i = 0;
            while(uprs.next()) {
                
                //if jtable is full, add empty row
                if(i==table.getRowCount()) {
                    ((DefaultTableModel)table.getModel()).addRow( new Object[] { null, null ,null ,
                            null, null, null, null } );
                }
                
                //invoice
                table.getModel().setValueAt( uprs.getString("invoice"), i, 0 );
                
                //date
                table.getModel().setValueAt( df.format(uprs.getDate("tanggal") ), i, 1  );
                
                //salesman
                if(uprs.getString("nama_salesman")!=null)
                    table.getModel().setValueAt( firstLetterCaps(uprs.getString("nama_salesman")), i, 2 );
                
                //customer
                if(uprs.getString("nama_pembeli")!=null)
                    table.getModel().setValueAt( firstLetterCaps(uprs.getString("nama_pembeli")), i, 3 );
                
                //salesman commision
                table.getModel().setValueAt( nf.format(uprs.getInt("komisi_salesman")), i, 4 );
                
                //total value of transaction
                table.getModel().setValueAt( nf.format(uprs.getInt("total_transaksi")), i, 5 );
                
                //index of transaction ( invisible )
                table.getModel().setValueAt( uprs.getInt("index"), i, 6 );
                
                i++;
                
            }
            
            uprs.close();
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    //for edit transaction, initialize the purchase transaction
    public void InitializePurchaseEditTransaction( JTable table, String sorting ) {
        if(sorting.equals("Invoice")) {
            sorting = "invoice";
        } else if(sorting.equals("Date")) {
            sorting = "tanggal DESC";
        }
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT invoice, tanggal, nama_komisioner, index_kontainer, " +
                    "komisi_komisioner, k.nama AS nama_kontainer, index, belum_bayar + sudah_bayar AS " +
                    "total_transaksi FROM data_transaksi_pembelian LEFT JOIN " +
                    "data_komisioner USING ( kd_komisioner ) LEFT JOIN utang u ON " +
                    "( u.index_pembelian = index ) LEFT JOIN data_kontainer k USING " +
                    "( index_kontainer ) ORDER BY " + sorting );
            
            int i = 0;
            while(uprs.next()) {
                
                //if jtable is full, add empty row
                if(i==table.getRowCount()) {
                    ((DefaultTableModel)table.getModel()).addRow( new Object[] { null, null ,null ,
                            null, null, null, null } );
                }
                
                //invoice
                table.getModel().setValueAt( uprs.getString("invoice"), i, 0 );
                
                //date
                table.getModel().setValueAt( df.format(uprs.getDate("tanggal") ), i, 1  );
                
                //commisioner
                if(uprs.getString("nama_komisioner")!=null)
                    table.getModel().setValueAt( firstLetterCaps(uprs.getString("nama_komisioner")), i, 2 );
                
                //commision
                table.getModel().setValueAt( nf.format(uprs.getInt("komisi_komisioner")), i, 3 );
                
                //container name
                table.getModel().setValueAt( uprs.getString("nama_kontainer"), i, 4 );
                
                //total value of transaction
                table.getModel().setValueAt( nf.format(uprs.getInt("total_transaksi")), i, 5 );
                
                //index of transaction ( invisible )
                table.getModel().setValueAt( uprs.getInt("index"), i, 6 );
                
                i++;
                
            }
            
            uprs.close();
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    //the official edit transaction
    public void InitializeSalaryEditTransactionDialog( String invoice, JTable table ) {
        //format the string
        invoice = "'" + invoice + "'";
        
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT t.potongan, k.nomor_induk, k.nama_karyawan, k.tipe, " +
                    "t.gaji FROM ( SELECT * FROM transaksi_gaji WHERE index_transaksi_gaji = " +
                    "( SELECT index FROM data_transaksi_gaji WHERE invoice_gaji = " + invoice +
                    " ) AND tipe <> 'salesman' ) t INNER JOIN data_karyawan k ON " +
                    "( k.index = t.index_karyawan ) " );
            
            int i = 0;
            String type = "";
            String cut = "";
            while(uprs.next()) {
                
                //if jtable is full, add empty row
                if(i==table.getRowCount()) {
                    ((DefaultTableModel)table.getModel()).addRow( new Object[] { null, null, null,
                            null, null } );
                }
                
                //code number
                table.getModel().setValueAt( uprs.getString("nomor_induk"), i, 0 );
                
                //name
                table.getModel().setValueAt(
                        firstLetterCaps(uprs.getString("nama_karyawan")), i, 1 );
                
                //salary
                table.getModel().setValueAt(
                        nf.format(uprs.getInt("gaji")), i, 2 );
                
                //cut or bonus
                cut = nf.format(uprs.getInt("potongan"));
                //netral
                if(nf.parse(cut).intValue()==0) {
                    table.getModel().setValueAt( cut, i, 3 );
                    table.getModel().setValueAt( cut, i, 4 );
                }
                //cut
                else if(nf.parse(cut).intValue()>0) {
                    table.getModel().setValueAt( cut, i, 3 );
                    table.getModel().setValueAt( 0, i, 4 );
                }
                //bonus
                else if(nf.parse(cut).intValue()<0) {
                    table.getModel().setValueAt( 0, i, 3 );
                    table.getModel().setValueAt( cut, i, 4 );
                }
                
                //type ( daily or monthly )
                type = uprs.getString("tipe");
                if(type.equals("harian"))
                    type = "Daily Employee";
                else if(type.equals("bulanan"))
                    type = "Monthly Employee";
                table.getModel().setValueAt( type, i, 5 );
                
                i++;
            }
            
            uprs = stmt.executeQuery( "SELECT t.potongan, k.nomor_induk, k.nama_salesman, " +
                    "t.gaji FROM ( SELECT * FROM transaksi_gaji_salesman WHERE " +
                    "index_transaksi_gaji = ( SELECT index FROM data_transaksi_gaji " +
                    "WHERE invoice_gaji = " + invoice + " )  ) t " +
                    "INNER JOIN data_salesman k ON ( k.index_salesman = t.index_salesman ) " );
            
            while(uprs.next()) {
                
                //if jtable is full, add empty row
                if(i==table.getRowCount()) {
                    ((DefaultTableModel)table.getModel()).addRow( new Object[] { null, null, null,
                            null, null } );
                }
                
                //code number
                table.getModel().setValueAt( uprs.getString("nomor_induk"), i, 0 );
                
                //name
                table.getModel().setValueAt(
                        firstLetterCaps(uprs.getString("nama_salesman")), i, 1 );
                
                //salary
                table.getModel().setValueAt(
                        nf.format(uprs.getInt("gaji")), i, 2 );
                
                //cut or bonus
                cut = nf.format(uprs.getInt("potongan"));
                //netral
                if(nf.parse(cut).intValue()==0) {
                    table.getModel().setValueAt( cut, i, 3 );
                    table.getModel().setValueAt( cut, i, 4 );
                }
                //cut
                else if(nf.parse(cut).intValue()>0) {
                    table.getModel().setValueAt( cut, i, 3 );
                    table.getModel().setValueAt( 0, i, 4 );
                }
                //bonus
                else if(nf.parse(cut).intValue()<0) {
                    table.getModel().setValueAt( 0, i, 3 );
                    table.getModel().setValueAt( cut, i, 4 );
                }
                
                //type ( daily or monthly )
                type = "Salesman";
                table.getModel().setValueAt( type, i, 5 );
                
                i++;
            }
            
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        } catch( java.text.ParseException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
    }
    
    public boolean EditSalaryTransaction( String originalinvoice, String invoice,
            java.sql.Date date, String comment ) {
        //format the string
        invoice = "'" + invoice + "'";
        originalinvoice = "'" + originalinvoice + "'";
        comment = "'" + comment + "'";
        String datest = "'" + date + "'";
        
        try {
            stmt = conn.createStatement();
            
            stmt.executeUpdate("UPDATE data_transaksi_gaji SET invoice_gaji = " + invoice +
                    ", komentar = " + comment + ", tanggal_gaji = " + datest +
                    " WHERE invoice_gaji = " + originalinvoice );
            
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        return true;
    }
    
    //for edit transaction, initialize the salary payment transaction, for maingui
    public void InitializeSalaryEditTransaction( JTable table, String sorting ) {
        if(sorting.equals("Invoice")) {
            sorting = "invoice_gaji";
        } else if(sorting.equals("Date")) {
            sorting = "tanggal_gaji DESC";
        }
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT invoice_gaji, tanggal_gaji, komentar " +
                    "FROM data_transaksi_gaji ORDER BY " + sorting );
            
            int i = 0;
            while(uprs.next()) {
                
                //if jtable is full, add empty row
                if(i==table.getRowCount()) {
                    ((DefaultTableModel)table.getModel()).addRow( new Object[] { null, null, null,
                            null, null } );
                }
                
                //invoice
                table.getModel().setValueAt( uprs.getString("invoice_gaji"), i, 0 );
                
                //date
                table.getModel().setValueAt( df.format(uprs.getDate("tanggal_gaji")), i, 1 );
                
                //comment
                table.getModel().setValueAt( uprs.getString("komentar"), i, 2 );
                
                i++;
            }
            
            uprs.close();
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    //for edit transaction, initialize the income transaction
    public void InitializeIncomeEditTransaction( JTable table, String sorting ) {
        if(sorting.equals("Invoice")) {
            sorting = "invoice";
        } else if(sorting.equals("Date")) {
            sorting = "tanggal DESC";
        }
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT invoice, tanggal, deskripsi, jumlah FROM " +
                    "transaksi_pemasukan_lain ORDER BY " + sorting );
            
            int i = 0;
            while(uprs.next()) {
                
                //if jtable is full, add empty row
                if(i==table.getRowCount()) {
                    ((DefaultTableModel)table.getModel()).addRow(
                            new Object[] { null, null, null, null, null, null } );
                }
                
                //invoice
                table.getModel().setValueAt( uprs.getString("invoice"), i, 0 );
                
                //date
                table.getModel().setValueAt( df.format(uprs.getDate("tanggal")), i, 1 );
                
                //description
                table.getModel().setValueAt( uprs.getString("deskripsi"), i, 2 );
                
                //value
                table.getModel().setValueAt( nf.format(uprs.getInt("jumlah")), i, 3 );
                
                i++;
            }
            
            uprs.close();
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    //for edit transaction, initialize the outcome transaction
    public void InitializeOutcomeEditTransaction( JTable table, String sorting ) {
        if(sorting.equals("Invoice")) {
            sorting = "invoice";
        } else if(sorting.equals("Date")) {
            sorting = "tanggal DESC";
        }
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT invoice, tanggal, nama, komentar, jumlah FROM " +
                    "transaksi_pengeluaran ORDER BY " + sorting );
            
            int i = 0;
            String type = "";
            while(uprs.next()) {
                
                //if jtable is full, add empty row
                if(i==table.getRowCount()) {
                    ((DefaultTableModel)table.getModel()).addRow(
                            new Object[] { null, null, null, null, null, null } );
                }
                
                //invoice
                table.getModel().setValueAt( uprs.getString("invoice"), i, 0 );
                
                //date
                table.getModel().setValueAt( df.format( uprs.getDate("tanggal") ), i, 1 );
                
                //type
                type = uprs.getString("nama");
                if(type.equals("Listrik")) {
                    type = "Electric";
                } else if(type.equals("Telepon")) {
                    type = "Telephone";
                } else if(type.equals("Air")) {
                    type = "Water";
                } else if(type.equals("Lain-lain")) {
                    type = "Other";
                }
                
                table.getModel().setValueAt( type, i, 2 );
                
                //comment
                table.getModel().setValueAt( uprs.getString("komentar"), i, 3 );
                
                //total
                table.getModel().setValueAt( nf.format( uprs.getInt("jumlah") ), i, 4 );
                
                i++;
            }
            
            uprs.close();
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    //edit credit table ( piutang )
    public boolean EditCreditTransaction( int saleindex, int havepaid, int totaltrans  ) {
        try {
            stmt = conn.createStatement();
            
            stmt.executeUpdate( "UPDATE piutang SET sudah_bayar = " + havepaid +
                    ", belum_bayar = " + ( totaltrans - havepaid ) +
                    " WHERE index_penjualan = " + saleindex );
            
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        return true;
    }
    
    public int getIndexSaleTransaction( String invoice ) {
        //format the string
        invoice = "'" + invoice.toLowerCase() + "'";
        
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT index FROM data_transaksi_penjualan WHERE " +
                    "invoice = " + invoice );
            
            int index = 0;
            
            if(uprs.next()) {
                index = uprs.getInt("index");
            }
            
            uprs.close();
            stmt.close();
            
            return index;
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
        return -1;
    }
    
    public int getIndexPurchaseTransaction( String invoice ) {
        //format the string
        invoice = "'" + invoice + "'";
        
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT index FROM data_transaksi_pembelian WHERE " +
                    "invoice = " + invoice );
            
            int index = 0;
            
            if(uprs.next()) {
                index = uprs.getInt("index");
            }
            
            uprs.close();
            stmt.close();
            
            return index;
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
        return -1;
    }
    
    //delete transaction with transaction index
    public boolean DeleteSaleTransaction( int index, String warehouse ) {
        WarehouseDB warehousedb = new WarehouseDB( conn );
        int warehouse_index = warehousedb.getIndexWarehouse(warehouse);
        
        try {
            stmt = conn.createStatement();
            
            //must use transaction
            stmt.executeUpdate("BEGIN WORK");
            
            //delete from piutang table
            stmt.executeUpdate( "DELETE FROM piutang WHERE index_penjualan = " + index );
            
            //update item stock
            uprs = stmt.executeQuery("select * from kasih_barang_jual where index_penjualan = " +
                    index);
            
            
            ArrayList<Integer> list_row_amount = new ArrayList<Integer>();
            ArrayList<Integer> list_row_item = new ArrayList<Integer>();
            while(uprs.next()) {
                list_row_amount.add(uprs.getInt("sudah"));
                list_row_item.add(uprs.getInt("index_barang"));
            }
            
            for(int i=0; i<list_row_amount.size(); i++) {
                boolean is_null = false;
                uprs = stmt.executeQuery("select * from stok_gudang where index_gudang = " +
                        warehouse_index + " and index_barang = " + list_row_item.get(i) + 
                        " and tanggal_kadaluarsa is null");
                if(uprs.next())
                    is_null = true;
                if(is_null) {
                    stmt.executeUpdate("update stok_gudang set jumlah = jumlah + " +
                                list_row_amount.get(i) + " where index_barang = " + 
                                list_row_item.get(i) + " and index_gudang = " + 
                                warehouse_index + " and tanggal_kadaluarsa is null");
                }
                else {
                    stmt.executeUpdate("insert into stok_gudang ( jumlah, index_barang, " +
                            "index_gudang, tanggal_kadaluarsa ) values ( " +
                            list_row_amount.get(i) + ", " + list_row_item.get(i) + ", " +
                            warehouse_index + ", null )");
                }
            }
            
            //delete from kasih_barang_jual table
            stmt.executeUpdate( "DELETE FROM kasih_barang_jual WHERE index_penjualan = " + index );
                        
            //delete from transaksi_barang_penjualan
            stmt.executeUpdate( "DELETE FROM transaksi_barang_penjualan WHERE index_penjualan = " +
                    index );
            
            //end of adjustment
            
            //ok, here is the final, delete from master of transaction
            stmt.executeUpdate( "DELETE FROM data_transaksi_penjualan WHERE index = " + index );
            
            //finish the transaction
            stmt.executeUpdate("COMMIT WORK");
            
            uprs.close();
            stmt.close();
        } catch( SQLException e ) {
            try {
                if(stmt!=null) {
                    stmt.executeUpdate("ROLLBACK");
                    stmt.close();
                }
            } catch( SQLException ex ) {
                ex.printStackTrace();
                new WriteLogFile(ex).writeLogFile();
            }
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
            return false;
        }
        return true;
    }
    
    public boolean DeleteOutcomeTransaction( String invoice ) {
        //format the string
        invoice = "'" + invoice + "'";
        
        try {
            stmt = conn.createStatement();
            
            stmt.executeUpdate( "DELETE FROM transaksi_pengeluaran WHERE invoice = " + invoice );
            
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        return true;
    }
    
    public boolean DeleteOtherIncome( String invoice ) {
        //format the string
        invoice = "'" + invoice + "'";
        
        try {
            stmt = conn.createStatement();
            
            stmt.executeUpdate( "DELETE FROM transaksi_pemasukan_lain WHERE invoice = " + invoice );
            
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        return true;
    }
    
    //delete transaction with invoice
    public boolean DeleteSaleTransaction( String invoice, String warehouse ) {
        int sale_index = getIndexSaleTransaction(invoice);
        return DeleteSaleTransaction(sale_index,warehouse);
    }
    
    //delete purchase transaction by invoice
    public boolean DeletePurchaseTransaction( String invoice, String warehouse ) {
        int purchase_index = getIndexPurchaseTransaction(invoice);
        return DeletePurchaseTransaction(purchase_index,warehouse);
    }
    
    //delete purchase transaction by index
    public boolean DeletePurchaseTransaction( int index, String warehouse ) {
        WarehouseDB warehousedb = new WarehouseDB( conn );
        int warehouseindex = warehousedb.getIndexWarehouse(warehouse);
        
        try {
            stmt = conn.createStatement();
            
            //must use transaction
            stmt.executeUpdate("BEGIN WORK");
            
            //delete from piutang table
            stmt.executeUpdate( "DELETE FROM utang WHERE index_pembelian = " + index );
            
            //make adjustment for stok_gudang
            //update item stock
            uprs = stmt.executeQuery("select * from terima_barang_beli where index_pembelian = " +
                    index);
            
            ArrayList<Integer> list_deleted_amount = new ArrayList<Integer>();
            ArrayList<Integer> list_deleted_item = new ArrayList<Integer>();
            while(uprs.next()) {
                list_deleted_amount.add(uprs.getInt("sudah"));
                list_deleted_item.add(uprs.getInt("index_barang"));
            }
            
            for(int i=0; i<list_deleted_amount.size(); i++) {
                
                uprs = stmt.executeQuery("select * from stok_gudang where index_barang = " +
                        list_deleted_item.get(i) + " and index_gudang = " + warehouseindex + 
                        " order by tanggal_kadaluarsa");
                
                ArrayList<Integer> list_row_amount = new ArrayList<Integer>();
                ArrayList<String> list_row_date = new ArrayList<String>();
                while(uprs.next()) {
                    list_row_amount.add(uprs.getInt("jumlah"));
                    list_row_date.add(uprs.getString("tanggal_kadaluarsa"));
                }
                
                int deleted_amount = list_deleted_amount.get(i);
                for(int j=0; j<list_row_amount.size(); j++) {
                    
                    String row_date = "'" + list_row_date.get(j) + "'";
                    String date_condition = "";
                    if(row_date.equals("'null'")) {
                        date_condition = "tanggal_kadaluarsa is null";
                    }
                    else {
                        date_condition = "tanggal_kadaluarsa = " + row_date;
                    }
                    
                    if(deleted_amount >= list_row_amount.get(j)) {
                        stmt.executeUpdate("delete from stok_gudang where index_barang = " +
                                    list_deleted_item.get(i) + " and index_gudang = " + 
                                    warehouseindex + " and " + date_condition);
                        deleted_amount -= list_row_amount.get(j); 
                    }
                    else {
                        stmt.executeUpdate("update stok_gudang set jumlah = jumlah - " + 
                                deleted_amount + " where index_barang = " +
                                list_deleted_item.get(i) + " and index_gudang = " + 
                                warehouseindex + " and " + date_condition );
                        deleted_amount = 0;
                    }
                    if(deleted_amount<=0)
                        break;
                }
            }
            //end of adjustment
            
            //delete from kasih_barang_jual table
            stmt.executeUpdate( "DELETE FROM terima_barang_beli WHERE index_pembelian = " + index );
            
            //delete from trans_barang_pembelian
            stmt.executeUpdate( "DELETE FROM transaksi_barang_pembelian " +
                    "WHERE index_pembelian = " + index );
            
            //ok, here is the final, delete from master of transaction
            stmt.executeUpdate( "DELETE FROM data_transaksi_pembelian WHERE index = " + index );
            
            //finish the transaction
            stmt.executeUpdate("COMMIT WORK");
            
            uprs.close();
            stmt.close();
        } catch( SQLException e ) {
            try {
                if(stmt!=null) {
                    stmt.executeUpdate("ROLLBACK");
                    stmt.close();
                }
            } catch( SQLException ex ) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
            return false;
        }
        return true;
    }
    
    public boolean DeleteSalaryTransaction( String invoice ) {
        //format the string
        invoice = "'" + invoice + "'";
        
        try {
            stmt = conn.createStatement();
            
            stmt.executeUpdate( "BEGIN WORK" );
            
            //delete from transaksi_gaji table ( all the employee & salesman name )
            stmt.executeUpdate( "DELETE FROM transaksi_gaji WHERE index_transaksi_gaji = " +
                    "( SELECT index FROM data_transaksi_gaji WHERE invoice_gaji = " + invoice +
                    " ) " );
            
            //at last, delete the master of salary transaction
            stmt.executeUpdate( "DELETE FROM data_transaksi_gaji WHERE invoice_gaji = " + invoice );
            
            stmt.executeUpdate( "COMMIT WORK" );
            
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        return true;
    }
    
    public boolean EditOutcomeTransaction( JTable table, java.sql.Date date, String invoice,
            String type, String comment, int amount ) {
        //format the string
        String datest = "'" + date + "'";
        invoice = "'" + invoice + "'";
        comment = "'" + comment + "'";
        type = "'" + type + "'";
        
        try {
            stmt = conn.createStatement();
            
            String originalinvoice = "'" +
                    (String)table.getModel().getValueAt( table.getSelectedRow(), 0 ) + "'";
            
            stmt.executeUpdate( "UPDATE transaksi_pengeluaran SET invoice = " + invoice +
                    ", komentar = " + comment + ", jumlah = " + amount + ", tanggal = " +
                    datest + ", nama = " + type  + " WHERE invoice = " + originalinvoice );
            
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        return true;
    }
    
    public boolean EditIncomeTransaction( JTable table, java.sql.Date date, String invoice,
            String description, int amount ) {
        
        //format the string
        String datest = "'" + date + "'";
        invoice = "'" + invoice + "'";
        description = "'" + description + "'";
        
        try {
            stmt = conn.createStatement();
            
            String originalinvoice = "'" +
                    (String)table.getModel().getValueAt( table.getSelectedRow(), 0 ) + "'";
            
            stmt.executeUpdate( "UPDATE transaksi_pemasukan_lain SET invoice = " + invoice +
                    ", deskripsi = " + description + ", jumlah = " + amount + ", tanggal = " +
                    datest + " WHERE invoice = " + originalinvoice );
            
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        
        return true;
        
    }
    
    public void SetUpEditPurchaseTransactionDialog( String invoice, JDateChooser Datechooser,
            JTextField Discount, JComboBox Commisioner, JCheckBox CommisionerChB,
            JComboBox Suplier, JCheckBox SuplierChB, JTextField Commision, JTextArea Comment ) {
        //format the string
        invoice = "'" + invoice + "'";
        
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT tanggal, potongan, komisi_komisioner, nama_komisioner, " +
                    "b.komentar, suplier FROM data_transaksi_pembelian b LEFT JOIN data_komisioner " +
                    "USING( kd_komisioner ) WHERE invoice = " + invoice );
            
            if(uprs.next()) {
                //commisioner
                if(uprs.getString("nama_komisioner")!=null) {
                    CommisionerChB.setSelected(true);
                    Commisioner.setEnabled(true);
                    Commisioner.setSelectedItem( firstLetterCaps(uprs.getString("nama_komisioner")) );
                    Commision.setEnabled(true);
                    Commision.setText( nf.format(uprs.getInt("komisi_komisioner")) );
                }
                
                //suplier
                if(uprs.getString("suplier")!=null) {
                    SuplierChB.setSelected(true);
                    Suplier.setEnabled(true);
                    Suplier.setSelectedItem( firstLetterCaps(uprs.getString("suplier")) );
                    Suplier.setEnabled(true);
                }
                
                //date
                Datechooser.setDate( uprs.getDate("tanggal") );
                
                //discount
                Discount.setText( nf.format(uprs.getInt("potongan")) );
                
                //comment
                Comment.setText( uprs.getString("komentar") );
                
            }
            
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    public void SetUpEditSaleTransactionDialog( String invoice, JDateChooser Datechooser,
            JTextField Discount, JComboBox Customer, JCheckBox CustomerChB, JComboBox Salesman,
            JCheckBox SalesmanChB, JComboBox SendingPaid,
            JTextField Commision, JTextField Sender, JTextField Shipmentcost, JTextArea Comment ) {
        //format the string
        invoice = "'" + invoice + "'";
        
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery("SELECT tanggal, potongan, komisi_salesman, biaya_kirim, " +
                    "nama_salesman, p.nama, j.komentar, s.nomor_induk, " +
                    "pengirim, tertanggung_biaya_kirim FROM data_transaksi_penjualan j LEFT JOIN " +
                    "data_salesman s ON( index_salesman = nomor_salesman ) LEFT JOIN data_pembeli p " +
                    "ON( p.index_pembeli = nomor_pembeli ) WHERE invoice = " + invoice );
            
            if(uprs.next()) {
                //customer
                if(uprs.getString("nama")!=null) {
                    CustomerChB.setSelected(true);
                    Customer.setEnabled(true);
                    Customer.setSelectedItem( firstLetterCaps(uprs.getString("nama")) );
                }
                
                //salesman
                if(uprs.getString("nama_salesman")!=null) {
                    SalesmanChB.setSelected(true);
                    Salesman.setEnabled(true);
                    Salesman.setSelectedItem( "( " + uprs.getString("nomor_induk") + " ) " +
                            firstLetterCaps(uprs.getString("nama_salesman")) );
                    Commision.setEnabled(true);
                    Commision.setText( nf.format(uprs.getInt("komisi_salesman")) );
                }
                
                //shipment cost
                Shipmentcost.setText( nf.format(uprs.getInt("biaya_kirim")) );
                
                //tertanggung_biaya_kirim
                if(uprs.getString("tertanggung_biaya_kirim")==null) {
                    SendingPaid.setEnabled(false);
                } else if(uprs.getString("tertanggung_biaya_kirim").equals("pembeli")) {
                    SendingPaid.setSelectedItem( "Customer" );
                } else if(uprs.getString("tertanggung_biaya_kirim").equals("penjual")) {
                    SendingPaid.setSelectedItem( "Company" );
                }
                
                //date
                Datechooser.setDate( uprs.getDate("tanggal") );
                
                //discount
                Discount.setText( nf.format(uprs.getInt("potongan")) );
                
                //sender
                Sender.setText(uprs.getString("pengirim"));
                
                //comment
                Comment.setText(uprs.getString("komentar"));
            }
            
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    public boolean EditSaleTransaction( java.sql.Date date, int discount, String invoice,
            String originalinvoice, String comment, int salesmancommision, int sendingprice,
            String sender, String salesmanid, String customerid, String whopaysending ) {
        //format the string
        String datest = "'" + date + "'";
        invoice = "'" + invoice + "'";
        originalinvoice = "'" + originalinvoice + "'";
        if(comment==null) {
            comment = "null";
        } else {
            comment = "'" + comment + "'";
        }
        if(sender==null) {
            sender = "null";
        } else {
            sender = "'" + sender.toLowerCase() + "'";
        }
        if(whopaysending==null) {
            whopaysending = "null";
        } else {
            whopaysending = "'" + whopaysending + "'";
        }
        
        try {
            stmt = conn.createStatement();
            
            stmt.executeUpdate("UPDATE data_transaksi_penjualan SET tanggal = " + datest + ", " +
                    "potongan = " + discount + ", invoice = " + invoice + ", komentar = " + comment +
                    ", komisi_salesman = " + salesmancommision + ", biaya_kirim = " + sendingprice +
                    ", pengirim = " + sender + ", nomor_salesman = " + salesmanid + ", nomor_pembeli = " +
                    customerid + ", tertanggung_biaya_kirim = " + whopaysending + " WHERE " +
                    "invoice = " + originalinvoice );
            
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        
        return true;
        
    }
    
    public boolean EditPurchaseTransaction( java.sql.Date date, int discount, String invoice,
            String originalinvoice, String comment, int commisionercommision, String commisionerid,
            String suplier ) {
        //format the string
        String datest = "'" + date + "'";
        invoice = "'" + invoice + "'";
        originalinvoice = "'" + originalinvoice + "'";
        if(comment==null) {
            comment = "null";
        } else {
            comment = "'" + comment + "'";
        }
        if(commisionerid==null) {
            commisionerid = "null";
        } else {
            commisionerid = "'" + commisionerid + "'";
        }
        if(suplier==null) {
            suplier = "null";
        } else {
            suplier = "'" + suplier.toLowerCase() + "'";
        }
        
        try {
            stmt = conn.createStatement();
            
            stmt.executeUpdate( "UPDATE data_transaksi_pembelian SET tanggal = " + datest + ", " +
                    "potongan = " + discount + ", invoice = " + invoice + ", komentar = " + comment +
                    ", komisi_komisioner = " + commisionercommision +  ", kd_komisioner = " +
                    commisionerid + ", suplier = " + suplier + " WHERE invoice = " + originalinvoice );
            
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        return true;
    }
    
    //sudah +, belum -, beli
    private void transferPurchaseLessServer( int warehouseindex, int change, String itemname,
            int purchaseindex ) {
        //format the string
        itemname = "'" + itemname.toLowerCase() + "'";
        
        try {
            stmt = conn.createStatement();
            
            int itemindex = -1;
            
            stmt.executeUpdate( "BEGIN WORK" );
            
            uprs = stmt.executeQuery( "SELECT index from data_barang " +
                    "WHERE nama = " + itemname );
            
            if(uprs.next()) {
                itemindex = uprs.getInt("index");
            }
            
            boolean non_expired_here = false;
            
            uprs = stmt.executeQuery("select index_barang " +
                    "FROM stok_gudang INNER JOIN data_barang ON( index = index_barang ) " +
                    "WHERE nama = " + itemname + " AND index_gudang = " + warehouseindex +
                    " and tanggal_kadaluarsa is null");
            
            if(uprs.next())
                non_expired_here = true;
            
            //manipulate terima_barang_beli
            stmt.executeUpdate( "UPDATE terima_barang_beli SET sudah = sudah + " + change + ", " +
                    "belum = belum - " + change + " WHERE index_pembelian = " + purchaseindex + " AND " +
                    "index_barang = " + itemindex );
            
            if(!non_expired_here) {
                //manipulate stok_gudang table
                stmt.executeUpdate( "insert into stok_gudang ( jumlah, index_barang, " +
                        "index_gudang ) values ( " + change + ", " + itemindex +
                        ", " + warehouseindex + ")" );
            }
            else
                //manipulate stok_gudang table
                stmt.executeUpdate( "UPDATE stok_gudang SET jumlah = jumlah + " + change +
                        " WHERE index_barang = " + itemindex + " AND index_gudang = " +
                        warehouseindex + " and tanggal_kadaluarsa is null");
            
            stmt.executeUpdate( "COMMIT WORK" );
            
            stmt.close();
        } catch( SQLException e ) {
            try {
                stmt.executeUpdate("ROLLBACK");
                stmt.close();
            } catch( SQLException ex ) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
    }
    
    //sudah -, belum +, beli
    private void transferPurchaseMoreServer( int warehouseindex, int change, String itemname,
            int purchaseindex ) {
        //format the string
        itemname = "'" + itemname.toLowerCase() + "'";
        
        try {
            stmt = conn.createStatement();
            
            int itemindex = -1;
            int itemamount = -1;
            
            stmt.executeUpdate( "BEGIN WORK" );
            
            uprs = stmt.executeQuery( "SELECT index_barang, sum(jumlah) as jumlah " +
                    "FROM stok_gudang INNER JOIN data_barang ON( index = index_barang ) " +
                    "WHERE nama = " + itemname + " AND index_gudang = " + warehouseindex + 
                    " group by index_barang ORDER BY index_barang" );
            
            if(uprs.next()) {
                itemindex = uprs.getInt("index_barang");
                itemamount = uprs.getInt("jumlah");
            }
            
            if(itemamount>=change) {
                int cur_change = change;
                uprs = stmt.executeQuery("select * from stok_gudang where index_barang = " +
                        itemindex + " and index_gudang = " + warehouseindex + " order by " +
                        "tanggal_kadaluarsa");

                LinkedList<Integer> list_row_amount = new LinkedList<Integer>();
                LinkedList<String> list_row_date = new LinkedList<String>();
                while(uprs.next()) {
                    list_row_amount.add(uprs.getInt("jumlah"));
                    list_row_date.add(uprs.getString("tanggal_kadaluarsa"));
                }
    
                while(cur_change>0) {
                    
                    int row_amount = list_row_amount.poll();
                    String row_date = "'" + list_row_date.poll() + "'";
                    String date_condition = "";
                    if(row_date.equals("'null'")) {
                        date_condition = "tanggal_kadaluarsa is null";
                    }
                    else {
                        date_condition = "tanggal_kadaluarsa = " + row_date;
                    }
                    
                    if(cur_change>=row_amount) {
                        stmt.executeUpdate("delete from stok_gudang where index_barang = " +
                                itemindex + " and index_gudang = " + warehouseindex + " and " +
                                date_condition );
                    }
                    else {
                        //manipulate stok_gudang table
                        stmt.executeUpdate( "UPDATE stok_gudang SET jumlah = jumlah - " + 
                                cur_change + " WHERE index_barang = " + itemindex + 
                                " AND index_gudang = " + warehouseindex + " and " + 
                                date_condition );    
                    }
                    
                    cur_change -= row_amount;
                }
            }
            else {
                stmt.executeUpdate( "delete from stok_gudang WHERE index_barang = " +
                        itemindex + " AND index_gudang = " + warehouseindex  );

                change -= itemamount;
            }
            
            //manipulate terima_barang_beli
            stmt.executeUpdate( "UPDATE terima_barang_beli SET sudah = sudah - " + change + ", " +
                    "belum = belum + " + change + " WHERE index_pembelian = " + purchaseindex +
                    " AND index_barang = " + itemindex );
            
            stmt.executeUpdate( "COMMIT WORK" );
            
            stmt.close();
        } catch( SQLException e ) {
            try {
                stmt.executeUpdate("ROLLBACK");
                stmt.close();
            } catch( SQLException ex ) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
    }
    
    //sudah -, belum +, jual
    private void transferSaleLessClient( int warehouseindex, int change, String itemname,
            int saleindex ) {
        //format the string
        itemname = "'" + itemname.toLowerCase() + "'";
        
        try {
            stmt = conn.createStatement();
            
            int itemindex = -1;
            
            stmt.executeUpdate( "BEGIN WORK" );
            
            uprs = stmt.executeQuery( "SELECT index_barang, jumlah " +
                    "FROM stok_gudang INNER JOIN data_barang ON( index = index_barang ) " +
                    "WHERE nama = " + itemname + " AND index_gudang = " + warehouseindex );
            
            if(uprs.next()) {
                itemindex = uprs.getInt("index_barang");
            }
            
            boolean non_expired_here = false;
            
            uprs = stmt.executeQuery("select index_barang " +
                    "FROM stok_gudang INNER JOIN data_barang ON( index = index_barang ) " +
                    "WHERE nama = " + itemname + " AND index_gudang = " + warehouseindex +
                    " and tanggal_kadaluarsa is null");
            
            if(uprs.next())
                non_expired_here = true;
            
            //update kasih_barang_jual table
            stmt.executeUpdate( "UPDATE kasih_barang_jual SET belum = belum + " + change + ", " +
                    "sudah = sudah - " + change + " WHERE index_penjualan = " + saleindex + " AND " +
                    "index_barang = " + itemindex );
                        
            if(!non_expired_here) 
                //manipulate stok_gudang table
                stmt.executeUpdate( "insert into stok_gudang ( jumlah, index_barang, " +
                        "index_gudang ) values ( " + change + ", " + itemindex +
                        ", " + warehouseindex + ")" );
            else
                //manipulate stok_gudang table
                stmt.executeUpdate( "UPDATE stok_gudang SET jumlah = jumlah + " + change +
                        " WHERE index_barang = " + itemindex + " AND index_gudang = " +
                        warehouseindex + " and tanggal_kadaluarsa is null");
            
            stmt.executeUpdate( "COMMIT WORK" );
            
            stmt.close();
        } catch( SQLException e ) {
            try {
                stmt.executeUpdate("ROLLBACK");
                stmt.close();
            } catch( SQLException ex ) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
        
    }
    
    private void transferSaleMoreClient( int warehouseindex, int change, String itemname,
            int saleindex ) {
        //format the string
        itemname = "'" + itemname.toLowerCase() + "'";
        
        try {
            stmt = conn.createStatement();
            
            int itemindex = -1;
            int itemamount = -1;
            
            stmt.executeUpdate( "BEGIN WORK" );
            
            uprs = stmt.executeQuery( "SELECT index_barang, sum(jumlah) as jumlah " +
                    "FROM stok_gudang INNER JOIN data_barang ON( index = index_barang ) " +
                    "WHERE nama = " + itemname + " AND index_gudang = " + warehouseindex + 
                    " group by index_barang ORDER BY index_barang" );
            
            if(uprs.next()) {
                itemindex = uprs.getInt("index_barang");
                itemamount = uprs.getInt("jumlah");
            }
                                    
            if(itemamount>=change) {
             
                int cur_change = change;
                uprs = stmt.executeQuery("select * from stok_gudang where index_barang = " +
                        itemindex + " and index_gudang = " + warehouseindex + " order by " +
                        "tanggal_kadaluarsa");
                
                LinkedList<Integer> list_row_amount = new LinkedList<Integer>();
                LinkedList<String> list_row_date = new LinkedList<String>();
                while(uprs.next()) {
                    list_row_amount.add(uprs.getInt("jumlah"));
                    list_row_date.add(uprs.getString("tanggal_kadaluarsa"));
                }
                    
                while(cur_change>0) {
                    
                    int row_amount = list_row_amount.poll();
                    String row_date = "'" + list_row_date.poll() + "'";
                    String date_condition = "";
                    if(row_date.equals("'null'")) {
                        date_condition = "tanggal_kadaluarsa is null";
                    }
                    else {
                        date_condition = "tanggal_kadaluarsa = " + row_date;
                    }
                    
                    if(cur_change>=row_amount) {
                        stmt.executeUpdate("delete from stok_gudang where index_barang = " +
                                itemindex + " and index_gudang = " + warehouseindex + " and " +
                                date_condition );
                    }
                    else {
                        //manipulate stok_gudang table
                        stmt.executeUpdate( "UPDATE stok_gudang SET jumlah = jumlah - " + 
                                cur_change + " WHERE index_barang = " + itemindex + 
                                " AND index_gudang = " + warehouseindex + " and " + 
                                date_condition );    
                    }
                    
                    cur_change -= row_amount;
                }

            } 
            else if(itemamount<change) {
                stmt.executeUpdate( "delete from stok_gudang WHERE index_barang = " +
                        itemindex + " AND index_gudang = " + warehouseindex  );

                change -= itemamount;
            }
            
            //update kasih_barang_jual table
            stmt.executeUpdate( "UPDATE kasih_barang_jual SET belum = belum - " + change + ", " +
                    "sudah = sudah + " + change + " WHERE index_penjualan = " + saleindex + " AND " +
                    "index_barang = " + itemindex );
            
            stmt.executeUpdate( "COMMIT WORK" );
        } catch( SQLException e ) {
            try {
                stmt.executeUpdate("ROLLBACK");
                stmt.close();
            } catch( SQLException ex ) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
    }
    
    //editing item transfer table for purchase transaction
    public void editTransferPurchase( JTable table, int warehouseindex, int purchaseindex ) {
        //column for easy remembering
        final int namecolumn = 0;
        final int suplierhavegivencolumn = 1;
        final int supliermustgivecolumn = 2;
        final int totalitemscolumn = 3;
        final int supliergavecolumn = 4;
        final int wereturnedcolumn = 5;
        final int suplyinwarehousecolumn = 6;
        
        //looping the row of the table
        for( int row=0; row<table.getRowCount(); row++ ) {
            //if the row is empty, pass on
            if(table.getModel().getValueAt(row,namecolumn)==null)
                continue;
            
            String name = (String)table.getModel().getValueAt(row,namecolumn);
            
            int suplierhavegiven = ((Integer)table.getModel().
                    getValueAt(row,suplierhavegivencolumn)).intValue();
            int supliermustgive = ((Integer)table.getModel().
                    getValueAt(row,supliermustgivecolumn)).intValue();
            int supliergave = 0;
            int wereturned = 0;
            try {
                supliergave = ((Integer)table.getModel().
                        getValueAt(row,supliergavecolumn)).intValue();
            } catch( java.lang.ClassCastException cce ) {
                supliergave = Integer.parseInt((String)table.getModel().
                        getValueAt(row,supliergavecolumn));
            } finally {
                if(supliergave>supliermustgive)
                    return;
            }
            try {
                wereturned = ((Integer)table.getModel().
                        getValueAt(row,wereturnedcolumn)).intValue();
            } catch( java.lang.ClassCastException cce ) {
                wereturned = Integer.parseInt( (String)table.getModel().
                        getValueAt(row,wereturnedcolumn));
            } finally {
                if(wereturned>suplierhavegiven)
                    return;
            }
            
            //if suplier add the "give
            if(supliergave>0) {
                transferPurchaseLessServer(warehouseindex, supliergave, name, purchaseindex);
            }
            //if suplier decrease the "give", we "returned"
            else if(wereturned>0) {
                transferPurchaseMoreServer(warehouseindex, wereturned, name, purchaseindex);
            }
        }
    }
    
    //editing item transfer table for sale transaction
    public void editTransferSale( JTable table, int warehouseindex, int saleindex ) {
        //column for easy remembering
        final int namecolumn = 0;
        final int wehavegivencolumn = 1;
        final int wemustgivecolumn = 2;
        final int totalitemscolumn = 3;
        final int wegavecolumn = 4;
        final int customerreturnedcolumn = 5;
        final int suplyinwarehousecolumn = 6;
        
        //looping the row of the table
        for( int row=0; row<table.getRowCount(); row++ ) {
            //if the row is empty, pass on
            if(table.getModel().getValueAt(row,namecolumn)==null)
                continue;
            
            String name = (String)table.getModel().getValueAt(row,namecolumn);
            
            int wehavegiven = ((Integer)table.getModel().
                    getValueAt(row,wehavegivencolumn)).intValue();
            int wemustgive = ((Integer)table.getModel().
                    getValueAt(row,wemustgivecolumn)).intValue();
            int wegave = 0;
            int customerreturned = 0;
            try {
                wegave = ((Integer)table.getModel().
                        getValueAt(row,wegavecolumn)).intValue();
            } catch( java.lang.ClassCastException cce ) {
                System.out.println(cce.getMessage());
                wegave = Integer.parseInt((String)table.getModel().getValueAt(row,wegavecolumn));
            } finally {
                if(wegave>wemustgive)
                    return;
            }
            try {
                customerreturned = ((Integer)table.getModel().
                        getValueAt(row,customerreturnedcolumn)).intValue();
            } catch( java.lang.ClassCastException cce ) {
                customerreturned = Integer.parseInt((String)table.getModel().getValueAt(row,
                        customerreturnedcolumn));
                System.out.println(cce.getMessage());
            } finally {
                if(customerreturned>wehavegiven)
                    return;
            }
            
            //if we add the "give
            if(wegave>0) {
                transferSaleMoreClient(warehouseindex, wegave, name, saleindex);
            }
            //if we decrease the "give", customer "returned"
            else if(customerreturned>0) {
                transferSaleLessClient(warehouseindex, customerreturned, name, saleindex);
            }
        }
    }
    
    //edit debt table ( utang )
    public boolean EditDebtTransaction( int buyindex, int havepaid, int totaltrans ) {
        try {
            stmt = conn.createStatement();
            
            stmt.executeUpdate( "UPDATE utang SET sudah_bayar = " + havepaid +
                    ", belum_bayar = " + ( totaltrans - havepaid ) +
                    " WHERE index_pembelian = " + buyindex );
            
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        return true;
    }
    
    //transaksi pemasukan lain....
    public boolean OthersIncomeTransaction( String invoice, Date date, int payment,
            String description ) {
        
        try {
            //invoice, date, description must not be null
            invoice = "'" + invoice + "'";
            String datestring = "'" + date + "'";
            description = "'" + description + "'";
            
            //insert into database
            stmt = conn.createStatement();
            
            stmt.executeUpdate("INSERT INTO transaksi_pemasukan_lain( invoice " +
                    ", tanggal, deskripsi, jumlah " +
                    " ) VALUES ( " + invoice + ", " + datestring + ", " +
                    description + ", " + payment + " )" );
            
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        
        return true;
    }
    
    //water, electricity, building tax charges....
    public boolean OutcomeTransaction( String invoice, Date date, int payment, String name,
            String comment ) {
        
        try {
            //invoice, date, description must not be null
            invoice = "'" + invoice + "'";
            String datest = "'" + date + "'";
            name = "'" + name + "'";
            
            //description field could be null or empty
            if(comment==null||comment.trim().equals("")) {
                comment = new String("null");
            } else {
                comment = "'" + comment + "'";
            }
            
            //do the operation on database
            stmt = conn.createStatement();
            
            stmt.executeUpdate("INSERT INTO transaksi_pengeluaran( invoice, " +
                    "tanggal, nama, komentar, " +
                    "jumlah ) VALUES ( " + invoice + ", " + datest + ", " +
                    name + ", " + comment + ", " + payment + ")");
            
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        return true;
    }
    
    public boolean SalaryTransaction( String invoice, Date date, String comment,
            JTable dailyemployeetb, JTable monthlyemployeetb, JTable salesmantb ) {
        
        try {
            //Invoice, date field must not be null
            invoice = "'" + invoice + "'";
            String datestring = "'" + date + "'";
            if(comment.trim().equals(""))
                comment = null;
            else
                comment = "'" + comment + "'";
            
            //do the operation
            stmt = conn.createStatement();
            
            //must set transaction on db
            stmt.executeUpdate("BEGIN WORK");
            
            //master of transaction
            stmt.executeUpdate("INSERT INTO data_transaksi_gaji( invoice_gaji, " +
                    " tanggal_gaji, komentar ) VALUES( " + invoice + ", " + datestring + ", " +
                    comment + " )");
            
            //daily employee part
            
            //insert data per individu on salary transaction
            String employee_name = new String("");
            int employee_index = 0;
            for( int i=0; i<dailyemployeetb.getRowCount(); i++ ) {
                
                //only pay if the boolean is true
                if( dailyemployeetb.getValueAt(i,5)==null)
                    continue;
                
                if( ((Boolean)dailyemployeetb.getValueAt(i,5)).booleanValue()==false )
                    continue;
                
                //we must get the employee number not the name to insert the db
                employee_name = "'" + ((String)dailyemployeetb.getValueAt(i,1)).toLowerCase() + "'";
                employee_index = ((Integer)dailyemployeetb.getModel().getValueAt(i,6)).intValue();
                
                //get the salary cut & bonus from jtable
                String salary = (nf.parse((String)dailyemployeetb.getModel().getValueAt(i,2))).toString();
                int salary_cut = ((Integer)dailyemployeetb.getValueAt(i,3)).intValue();
                int salary_bonus = ((Integer)dailyemployeetb.getValueAt(i,4)).intValue();
                
                //just to make sure the employee name not null
                if(employee_name!=null)
                    //insert the new information to db
                    stmt.executeUpdate("INSERT INTO transaksi_gaji( index_transaksi_gaji, " +
                            " index_karyawan, gaji, potongan, tipe ) VALUES ( " +
                            "currval('index_transaksi_gaji'), " + employee_index + ", " + salary + ", " +
                            (salary_cut - salary_bonus) + ", 'karyawan harian' )" );
            }
            
            //monthly employee part
            for( int i=0; i<monthlyemployeetb.getRowCount(); i++ ) {
                
                //only pay if the boolean is true
                if( monthlyemployeetb.getValueAt(i,5)==null)
                    continue;
                
                if( ((Boolean)monthlyemployeetb.getValueAt(i,5)).booleanValue()==false )
                    continue;
                
                //we must get the employee number not the name to insert the db
                employee_name = "'" + ((String)monthlyemployeetb.getValueAt(i,1)).toLowerCase() + "'";
                employee_index = ((Integer)monthlyemployeetb.getModel().getValueAt(i,6)).intValue();
                
                //get the salary cut & bonus from jtable
                String salary = (nf.parse((String)monthlyemployeetb.getModel().getValueAt(i,2))).toString();
                int salary_cut = ((Integer)monthlyemployeetb.getValueAt(i,3)).intValue();
                int salary_bonus = ((Integer)monthlyemployeetb.getValueAt(i,4)).intValue();
                
                //just to make sure the employee name not null
                if(employee_name!=null)
                    //insert the new information to db
                    stmt.executeUpdate("INSERT INTO transaksi_gaji( index_transaksi_gaji, " +
                            " index_karyawan, gaji, potongan, tipe ) VALUES ( " +
                            "currval('index_transaksi_gaji'), " + employee_index + ", " + salary + ", " +
                            (salary_cut - salary_bonus) + ", 'karyawan bulanan' )" );
            }
            
            //salesman part
            
            //insert data per individu on salary transaction
            String salesman_name = new String("");
            int salesman_index = 0;
            for( int i=0; i<salesmantb.getRowCount(); i++ ) {
                
                //only pay if the boolean is true
                if( salesmantb.getModel().getValueAt(i,6)==null)
                    continue;
                
                if( ((Boolean)salesmantb.getModel().getValueAt(i,6)).booleanValue()==false )
                    continue;
                
                //we must get the salesman number not the name to insert the db
                salesman_name = "'" + ((String)salesmantb.getValueAt(i,1)).toLowerCase() + "'";
                salesman_index = ((Integer)salesmantb.getModel().getValueAt(i,7)).intValue();
                
                //get the salary cut & bonus from jtable
                String salary = (nf.parse((String)salesmantb.getModel().getValueAt(i,2))).toString();
                int salary_cut = ((Integer)salesmantb.getValueAt(i,3)).intValue();
                int salary_bonus = ((Integer)salesmantb.getValueAt(i,4)).intValue();
                
                //just to make sure the employee name not null
                if(salesman_name!=null)
                    //insert the new information to db
                    stmt.executeUpdate("INSERT INTO transaksi_gaji_salesman ( " +
                            "index_transaksi_gaji, index_salesman, gaji, potongan ) " +
                            "VALUES ( currval('index_transaksi_gaji'), " + salesman_index + 
                            ", " + salary + ", " + (salary_cut - salary_bonus) + " )" );
            }
            
            //finish the transaction on db
            stmt.executeUpdate("COMMIT WORK");
            stmt.close();
        } catch( java.text.ParseException parse ) {
            System.out.println(parse.getMessage());
        } catch( SQLException e ) {
            try {
                stmt.executeUpdate("ROLLBACK");
                stmt.close();
            } catch( SQLException cep ) {
                cep.printStackTrace();
            }
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
            return false;
        }
        
        return true;
        
    }
    
    public boolean InsertBuyTransaction( String sender, java.sql.Date date,
            int discounttransaction, String invoice, String comment, int commision, String commisionerid,
            String container, String supply, String suplier, int totalprice, int payment,
            JTable table, JTable listtable ) {
        
        //prepare the parameter
        invoice = "'" + invoice + "'";
        String stdate = "'" + date + "'";
        if(sender==null)
            sender = "null";
        else
            sender = "'" + sender + "'";
        if(comment==null)
            comment = "null";
        else
            comment = "'" + comment + "'";
        if(container==null)
            container = "null";
        else
            container = "'" + container.toLowerCase() + "'";
        if(suplier==null)
            suplier = "null";
        else
            suplier = "'" + suplier.toLowerCase() + "'";
        
        //put the data into database
        try {
            stmt = conn.createStatement();
            
            //start the operation
            stmt.executeUpdate("BEGIN WORK");
            
            uprs = stmt.executeQuery( "SELECT index_kontainer FROM data_kontainer WHERE nama = " +
                    container );
            
            String contindex = "";
            if(uprs.next())
                contindex = uprs.getString("index_kontainer");
            else
                contindex = "null";
            
            //insert into the master of purchase transaction
            stmt.executeUpdate("INSERT INTO data_transaksi_pembelian( " +
                    "tanggal, potongan, invoice, komentar, kd_komisioner, " +
                    "komisi_komisioner, index_kontainer, pengirim, suplier ) " +
                    "VALUES " +
                    "(" + stdate + ", " + discounttransaction + ", " + invoice +
                    ", " + comment + ", " + commisionerid + ", " + commision + ", " + contindex +
                    ", " + sender + ", " + suplier + " ) ");
            
            //insert into list of items that bought
            for(int i=0; i<table.getRowCount(); i++) {
                
                //if the status value is not null
                if(table.getModel().getValueAt(i,5)!=null)
                    
                    //if the row is valid
                    if(((Boolean)table.getModel().getValueAt(i,5)).booleanValue()==true) {
                    
                    //this is the inventory name
                    String inventoryname = "'" +
                            ((String)table.getModel().getValueAt(i,0)).trim().toLowerCase() + "'";
                    
                    uprs = stmt.executeQuery("select index, harga_modal from data_barang " +
                            "where nama = " + inventoryname);
                    
                    uprs.next();
                    
                    int item_index = uprs.getInt("index");
                    int buying_price = uprs.getInt("harga_modal");
                    
                    //this is the discount per inventory
                    Integer discountperinventory = (Integer)table.getModel().getValueAt(i,2);
                    
                    //amount of this inventory
                    Integer amountofinventory = (Integer)table.getModel().getValueAt(i,3);
                    
                    int itemgiven = 0;
                    String warehouse = "";
                    
                    //additional operation such as update supply of inventory in warehouse
                    //and insert data into the terima barang beli table
                    
                    if(supply.equals("Custom")) {
                        //use the list
                        ArrayList<String> list = new ArrayList<String>();
                        
                        //get the list of name of the warehouse
                        uprs = stmt.executeQuery( "SELECT nama FROM data_gudang ORDER BY nama" );
                        
                        //how many the warehouse are we have
                        int count = 0;
                        
                        //put the name of the warehouse into list and count it....
                        while(uprs.next()) {
                            list.add( uprs.getString("nama") );
                            count++;
                        }
                        
                        //for every warehouse we must update the supply of inventory
                        for(int j=0; j<count; j++) {
                            
                            //the name of the warehouse
                            warehouse = "'" + (String)list.get(j) + "'";
                            
                            //the amount of inventory that is to take from seller
                            Integer amountinvent =
                                    (Integer)listtable.getModel().getValueAt(i,2+j);
                            
                            uprs = stmt.executeQuery("select jumlah from stok_gudang where " +
                                    "index_gudang = ( SELECT index FROM data_gudang WHERE nama = " +
                                    warehouse + " ) and index_barang = " + item_index + 
                                    " and tanggal_kadaluarsa is null");
                            
                            if(uprs.next()) 
                                //update
                                stmt.executeUpdate( "UPDATE stok_gudang SET jumlah = jumlah + " +
                                        amountinvent + " WHERE index_gudang = " +
                                        "( SELECT index FROM data_gudang WHERE nama = " +
                                        warehouse + " ) AND index_barang = " + item_index +
                                        " and tanggal_kadaluarsa is null");
                            else
                            //update
                                stmt.executeUpdate( "insert into stok_gudang ( jumlah, " +
                                        "index_gudang, index_barang ) values ( " +
                                        amountinvent + ", " +
                                        "( SELECT index FROM data_gudang WHERE nama = " +
                                        warehouse + " ), " + item_index + ")" );
                            
                            //we purchase 13 items from seller but maybe we just get from him
                            //5 items at the transaction....
                            itemgiven += amountinvent.intValue();
                        }
                        
                        uprs.close();
                    }
                    //only one warehouse
                    else {
                        warehouse = "'" + supply.toLowerCase() + "'";
                        
                        uprs = stmt.executeQuery("select jumlah from stok_gudang where " +
                                    "index_gudang = ( SELECT index FROM data_gudang WHERE nama = " +
                                    warehouse + " ) and index_barang = " + item_index + 
                                    " and tanggal_kadaluarsa is null");
                            
                        if(uprs.next()) 
                            //update
                            stmt.executeUpdate( "UPDATE stok_gudang SET jumlah = jumlah + " +
                                    amountofinventory + " WHERE index_gudang = " +
                                    "( SELECT index FROM data_gudang WHERE nama = " +
                                    warehouse + " ) AND index_barang = " + item_index +
                                    " and tanggal_kadaluarsa is null");
                        else
                            //update
                            stmt.executeUpdate( "insert into stok_gudang ( jumlah, " +
                                    "index_gudang, index_barang ) values ( " +
                                    amountofinventory + ", " +
                                    "( SELECT index FROM data_gudang WHERE nama = " +
                                    warehouse + " ), " + item_index + ")");
                        
                        itemgiven += amountofinventory.intValue();
                    }
                    //item to be bought list
                    stmt.executeUpdate("INSERT INTO transaksi_barang_pembelian( " +
                            "index_barang, index_pembelian, diskon_per_barang, jumlah, " +
                            "harga_beli ) VALUES " +
                            "( " + item_index + ", currval('index_transaksi_pembelian'), " +
                            discountperinventory + ", " + amountofinventory + ", " + buying_price + ")");
                    
                    //accept item list
                    stmt.executeUpdate( "INSERT INTO terima_barang_beli " +
                            "( index_barang, index_pembelian, belum, sudah ) " +
                            "VALUES( " + item_index + ", currval('index_transaksi_pembelian'), " +
                            ( amountofinventory.intValue() - itemgiven ) + ", " + itemgiven + " )" );
                    
                    }
                
            }
            
            //update the piutang list
            stmt.executeUpdate( "INSERT INTO utang( index_pembelian, belum_bayar, sudah_bayar ) " +
                    "VALUES ( currval('index_transaksi_pembelian'), " +
                    (totalprice - payment) + ", " + payment + " ) " );
            
            //finish the operation
            stmt.executeUpdate("COMMIT WORK");
            
            //free the resource
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            try {
                if(stmt!=null) {
                    stmt.executeUpdate("ROLLBACK");
                    stmt.close();
                }
            } catch( SQLException b ) {
                System.out.println(b.getMessage());
            }
            return false;
        }
        
        return true;
        
    }
    
    public boolean InsertSaleTransaction(String sender, java.sql.Date date,
            int discounttransaction, String invoice, String comment, int commision, String salesmanid,
            int sendingprice, String customerid, String supply, int totalprice, int payment,
            String sending_paid, JTable transtable, JTable disttable ) {
        
        //prepare the parameter
        invoice = "'" + invoice + "'";
        String stdate = "'" + date + "'";
        if(sender==null) {
            sender = "null";
            sending_paid = "null";
        } else {
            sender = "'" + sender + "'";
            sending_paid = "'" + sending_paid + "'";
        }
        if(comment==null)
            comment = "null";
        else
            comment = "'" + comment + "'";
        
        //put the data into database
        try {
            stmt = conn.createStatement();
            
            //start the operation
            stmt.executeUpdate("BEGIN WORK");
            
            //insert into the master of sale transaction
            stmt.executeUpdate("INSERT INTO data_transaksi_penjualan( " +
                    "tanggal, potongan, invoice, komentar, nomor_salesman, " +
                    "komisi_salesman, biaya_kirim, pengirim, nomor_pembeli, tertanggung_biaya_kirim ) " +
                    "VALUES " +
                    "(" + stdate + ", " + discounttransaction + ", " + invoice +
                    ", " + comment + ", " + salesmanid + ", " + commision + ", " + sendingprice +
                    ", " + sender + ", " + customerid + ", " + sending_paid  + ")");
            
            //insert into list of items that to be sold
            //looping around the table
            for(int i=0; i<transtable.getRowCount(); i++) {
                
                //if the status value is not null
                if(transtable.getModel().getValueAt(i,5)!=null)
                    
                    //if the row is valid then do the operation
                    if(((Boolean)transtable.getModel().getValueAt(i,5)).booleanValue()==true) {
                    
                    //this is the inventory name
                    String inventoryname = "'" +
                            ((String)transtable.getModel().getValueAt(i,0)).trim().toLowerCase() + "'";
                    
                    //discount per item
                    Integer discountperitem = (Integer)transtable.getModel().getValueAt(i,2);
                    
                    //amount of item
                    Integer amountofitem = (Integer)transtable.getModel().getValueAt(i,3);
                    
                    //amount of item for the operation : will decrease slowly
                    int opt_amountofitem = amountofitem.intValue();
                    
                    uprs = stmt.executeQuery( "SELECT index, harga_modal, harga_jual FROM " +
                            "data_barang WHERE nama = " + inventoryname + " ORDER BY index");
                    
                    int itemindex = -1;
                    int sale_price = -1;
                    int buying_price = -1;
                    if(uprs.next()) {
                        itemindex = uprs.getInt("index");
                        sale_price = uprs.getInt("harga_jual");
                        buying_price = uprs.getInt("harga_modal");
                    }
                    
                    int trans_amountinventoperation = 0;
                    
                    //if more than one warehouse involved
                    if(supply.equals("Custom")) {
                        
                        //use the list
                        ArrayList<String> warehouselist = new ArrayList<String>();

                        //get the list of name of the warehouse
                        uprs = stmt.executeQuery( "SELECT nama FROM data_gudang ORDER BY nama" );

                        int warehouse_int = 0;
                        //put the name of the warehouse into list and count it....
                        while(uprs.next()) {
                            warehouselist.add( uprs.getString("nama") );
                            warehouse_int++;
                        }
                        
                        int []amountinventtake = new int[warehouse_int];
                        
                        //how many of this item we give to customer
                        int totalgiven = 0;
                        
                        for( int j = 0; j < warehouse_int; j++ ) {
                            //the amount of item that is give to customer
                            amountinventtake[j] =
                                    ((Integer)disttable.getModel().getValueAt(i,2+j)).intValue();
                            totalgiven += amountinventtake[j];
                        }
                        
                        //for every warehouse we must update the supply of inventory
                        for(int j=0; j<warehouse_int; j++) {

                            //we have take all the item for this transaction
                            if(amountinventtake[j]==0)
                                continue;

                            //the name of the warehouse
                            String warehouse = "'" + (String)warehouselist.get(j) + "'";
                            
                            uprs = stmt.executeQuery("select index from data_gudang where nama = " +
                                warehouse );
                        
                            uprs.next();
                        
                            int warehouse_index = uprs.getInt("index");

                            //for every warehouse, get the list of the spesific item
                            uprs = stmt.executeQuery( "select jumlah, tanggal_kadaluarsa from " +
                                    "stok_gudang where index_barang = " + itemindex + 
                                    " and index_gudang = " + warehouse_index + " order by " +
                                    "tanggal_kadaluarsa" );

                            LinkedList<Integer> amount_warehouse = new LinkedList<Integer>();
                            LinkedList<String> date_warehouse = new LinkedList<String>();
                            while(uprs.next()) {
                                amount_warehouse.add( uprs.getInt("jumlah") );
                                date_warehouse.add( uprs.getString("tanggal_kadaluarsa") );
                            }
                            
                            int temp_amountinventtake = amountinventtake[j];

                            while(temp_amountinventtake>0) {
                                
                                int amountinventwarehouse = amount_warehouse.poll();
                                String dateinventwarehouse = date_warehouse.poll();
                                if(dateinventwarehouse==null) {
                                    dateinventwarehouse = " is null";
                                }
                                else {
                                    dateinventwarehouse = " = '" + dateinventwarehouse + "'";
                                }

                                //if amount of items to be taken from warehouse is less than
                                //supply for that item ( maybe the "oldest" item, or "newest" item )
                                if(amountinventwarehouse>temp_amountinventtake) {
                                    stmt.executeUpdate( "UPDATE stok_gudang SET jumlah = " +
                                            "jumlah - " +
                                            temp_amountinventtake + " WHERE index_gudang = " +
                                            warehouse_index + "  AND index_barang = " + 
                                            itemindex + " and tanggal_kadaluarsa " + 
                                            dateinventwarehouse);
                                    temp_amountinventtake = 0;
                                }
                                //if amount of items to be taken from warehouse is more than
                                //supply for that item
                                else {
                                    stmt.executeUpdate( "delete from stok_gudang where " +
                                        "index_gudang = " + warehouse_index + 
                                        " and index_barang = " + itemindex + " and " +
                                        "tanggal_kadaluarsa " + dateinventwarehouse);
                                    temp_amountinventtake -= amountinventwarehouse;
                                }
                            }

                        }
                            
                        //item list to be sold list ( child of master of sale transaction )
                        stmt.executeUpdate("INSERT INTO transaksi_barang_penjualan( " +
                                "index_barang, index_penjualan, diskon_per_barang, jumlah, " +
                                "harga_modal, harga_jual ) VALUES ( " +
                                itemindex + ", currval('index_transaksi_penjualan'), " +
                                discountperitem + ", " + amountofitem + ", " + buying_price +
                                ", " + sale_price + ")");
                            
                        
                        //kasih_barang_jual list
                        stmt.executeUpdate( "INSERT INTO kasih_barang_jual " +
                                "( index_barang, index_penjualan, belum, sudah ) " +
                                "VALUES( " + itemindex + ", " + 
                                "currval('index_transaksi_penjualan'), " +
                                ( amountofitem - totalgiven ) + ", " + totalgiven + " )" );
                        
                    }
                    //one warehouse only
                    else {
                        String warehouse = "'" + supply.toLowerCase() + "'";
                        
                        uprs = stmt.executeQuery("select index from data_gudang where nama = " +
                                warehouse );
                        
                        uprs.next();
                        
                        int warehouse_index = uprs.getInt("index");
                        
                        //update the supply of inventory in database
                        //start from the oldest to newest because we want
                        //to sell the oldest items first, then the newer
                        //until the newest item
                        
                        //for every warehouse, get the list of the spesific item
                        uprs = stmt.executeQuery( "select jumlah, tanggal_kadaluarsa from " +
                                "stok_gudang where index_barang = " + itemindex + 
                                " and index_gudang = " + warehouse_index + " order by " +
                                "tanggal_kadaluarsa" );

                        LinkedList<Integer> amount_warehouse = new LinkedList<Integer>();
                        LinkedList<String> date_warehouse = new LinkedList<String>();
                        while(uprs.next()) {
                            amount_warehouse.add( uprs.getInt("jumlah") );
                            date_warehouse.add( uprs.getString("tanggal_kadaluarsa") );
                        }
                        
                        int amountinventtake = amountofitem;
                        
                        while(amountinventtake>0) {
                            
                            int amountinventwarehouse = amount_warehouse.poll();
                            String dateinventwarehouse = date_warehouse.poll();
                            
                            if(dateinventwarehouse==null) {
                                dateinventwarehouse = " is null";
                            }
                            else {
                                dateinventwarehouse = " = '" + dateinventwarehouse + "'"; 
                            }
                            
                            //if amount of items to be taken from warehouse is less than
                            //supply for that item ( maybe the "oldest" item, or "newest" item )
                            if(amountinventwarehouse>amountinventtake) {
                                stmt.executeUpdate( "UPDATE stok_gudang SET jumlah = " +
                                        "jumlah - " + amountinventtake + " WHERE index_gudang = " +
                                        warehouse_index + " AND index_barang = " + itemindex +
                                        " and tanggal_kadaluarsa " + dateinventwarehouse );
                                amountinventtake = 0;
                            }
                            //if amount of items to be taken from warehouse is more than
                            //supply for that item
                            else {
                                stmt.executeUpdate( "delete from stok_gudang where " +
                                        "index_gudang = " + warehouse_index + 
                                        " and index_barang = " + itemindex + " and " +
                                        "tanggal_kadaluarsa " + dateinventwarehouse);
                                amountinventtake -= amountinventwarehouse;
                            }
                        }
                            
                        //item list to be sold list ( child of master of sale transaction )
                        stmt.executeUpdate("INSERT INTO transaksi_barang_penjualan( " +
                                "index_barang, index_penjualan, diskon_per_barang, jumlah, " +
                                "harga_modal, harga_jual ) VALUES ( " +
                                itemindex + ", currval('index_transaksi_penjualan'), " +
                                discountperitem + ", " + amountofitem + ", " + buying_price  +
                                ", " + sale_price + " )");
                            
                        
                        //kasih_barang_jual list
                        stmt.executeUpdate( "INSERT INTO kasih_barang_jual " +
                                "( index_barang, index_penjualan, belum, sudah ) " +
                                "VALUES( " +
                                itemindex + ", currval('index_transaksi_penjualan'), " +
                                "0, " + amountofitem + " )" );
                        
                    }
                    
                    
                    }//looping if the flag is true
                
            }//list of item in table
            
            //update the piutang list
            stmt.executeUpdate( "INSERT INTO piutang( index_penjualan, belum_bayar, sudah_bayar ) VALUES ( " +
                    "currval('index_transaksi_penjualan'), " + (totalprice - payment) + ", " + payment + " ) " );
            
            //finish the operation
            stmt.executeUpdate("COMMIT WORK");
            
            //free the resource
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            try {
                if(stmt!=null) {
                    stmt.executeUpdate("ROLLBACK");
                    stmt.close();
                }
            } catch( SQLException b ) {
                System.out.println(b.getMessage());
            }
            return false;
        }
        
        return true;
        
    }
    
    public void updateTransferTable( JTable e, String type ) {
        try {
            stmt = conn.createStatement();
            
            //get the information from database
            if(type.equals("sale")) {
                int rowCount = e.getRowCount();
                
                int saleindex = ((Integer)e.getModel().getValueAt(0,4)).intValue();
                
                for(int i=0; i<rowCount; i++) {
                    
                    if(e.getValueAt(i,0)!=null) {
                        int notgive = ((Integer)e.getModel().getValueAt(i,2)).intValue();
                        int havegive = ((Integer)e.getModel().getValueAt(i,1)).intValue();
                        int itemindex = ((Integer)e.getModel().getValueAt(i,5)).intValue();
                        
                        stmt.executeUpdate( "UPDATE kasih_barang_jual SET " +
                                "belum = " + notgive + ", sudah =  " + havegive +
                                " WHERE index_penjualan = " + saleindex + " AND " +
                                "index_barang = " + itemindex );
                    }
                    
                }
                
                stmt.close();
                
            } else if(type.equals("buy")) {
                int rowCount = e.getRowCount();
                
                int buyindex = ((Integer)e.getModel().getValueAt(0,4)).intValue();
                
                for(int i=0; i<rowCount; i++) {
                    
                    if(e.getValueAt(i,0)!=null) {
                        int notaccept = ((Integer)e.getModel().getValueAt(i,2)).intValue();
                        int haveaccept = ((Integer)e.getModel().getValueAt(i,1)).intValue();
                        int itemindex = ((Integer)e.getModel().getValueAt(i,5)).intValue();
                        
                        stmt.executeUpdate( "UPDATE terima_barang_beli SET " +
                                "belum = " + notaccept + ", sudah =  " + haveaccept +
                                " WHERE index_pembelian = " + buyindex + " AND " +
                                "index_barang = " + itemindex );
                    }
                    
                }
                
                stmt.close();
                
            }
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    public void initializeTransferTable( JTable e, String type, String invoice, String warehouse ) {
        int index = 0;
        //format the string
        warehouse = "'" + warehouse.toLowerCase() + "'";
        try {
            stmt = conn.createStatement();
            uprs = stmt.executeQuery("select index from data_gudang where nama = " + warehouse);
            
            uprs.next();
            
            int warehouse_index = uprs.getInt("index");
            
            //get the information from database
            if(type.equals("sale")) {
                index = getIndexSaleTransaction(invoice);
                stmt = conn.createStatement();
                uprs = stmt.executeQuery( "SELECT nama, belum, sudah, belum + sudah AS total, " +
                        "sum(jumlah) as jumlah FROM kasih_barang_jual k INNER JOIN data_barang d " +
                        "ON d.index = k.index_barang left JOIN " +
                        "(select * from stok_gudang where index_gudang = " + warehouse_index +
                        " ) w ON k.index_barang = w.index_barang WHERE " +
                        "index_penjualan = " + index + " group by nama, belum, sudah, total");
            } 
            else if(type.equals("buy")||type.equals("purchase")) {
                index = getIndexPurchaseTransaction(invoice);
                stmt = conn.createStatement();
                uprs = stmt.executeQuery( "SELECT nama, belum, sudah, belum + sudah AS total, " +
                        "sum(jumlah) as jumlah FROM terima_barang_beli k INNER JOIN data_barang d " +
                        "ON d.index = k.index_barang left JOIN " +
                        "(select * from stok_gudang where index_gudang = " + warehouse_index +
                        " ) w ON k.index_barang = w.index_barang WHERE " +
                        "index_pembelian = " + index + " group by nama, belum, sudah, total" );
            }
            
            int i = 0;
            while(uprs.next()) {
                
                //fill the table
                
                //the name of item
                e.getModel().setValueAt(
                        firstLetterCaps( uprs.getString("nama") ), i, 0 );
                
                //date
                e.getModel().setValueAt(
                        uprs.getInt("sudah"), i, 1 );
                
                //must paid....
                e.getModel().setValueAt(
                        uprs.getInt("belum"), i, 2 );
                
                //have paid....
                e.getModel().setValueAt(
                        uprs.getInt("total"), i, 3 );
                
                //editable column
                if(e.getModel().getValueAt(i,4)==null)
                    e.getModel().setValueAt(
                            0, i, 4 );
                if(e.getModel().getValueAt(i,5)==null)
                    e.getModel().setValueAt(
                            0, i, 5 );
                
                //suply in warehouse
                e.getModel().setValueAt(
                          uprs.getInt("jumlah"), i, 6 );
                
                i++;
                
                //if the information from db, add the empty row
                if(i==e.getRowCount()) {
                    DefaultTableModel model = (DefaultTableModel)e.getModel();
                    model.addRow( new Object[] { null, null, null, null, null, null } );
                }
            }
            uprs.close();
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    public void initializeSalaryTableTransaction( JTable dailyemployeetb, JTable monthlyemployeetb,
            JTable salesmantb, java.util.Date date ) {
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //start from 0
        int month = cal.get( Calendar.MONTH ) + 1;
        int year = cal.get( Calendar.YEAR );
        
        //get month salary for daily employee
        EmployeeDB employeedb = new EmployeeDB( conn );
        
        try {
            stmt = conn.createStatement();
            Statement stmthelp = conn.createStatement();
            
            //daily employee table
//            uprs = stmt.executeQuery( "SELECT nama_karyawan, nomor_induk, k.index, k.gaji, " +
//                    "max(tanggal_gaji) AS terakhir_bayar FROM data_karyawan k " +
//                    "LEFT JOIN transaksi_gaji g ON( k.index = index_karyawan ) " +
//                    "LEFT JOIN data_transaksi_gaji d ON ( d.index = g.index_transaksi_gaji ) " +
//                    "WHERE g.tipe = 'karyawan harian' GROUP BY nama_karyawan, nomor_induk, " +
//                    "k.index, k.gaji" );
            
            uprs = stmt.executeQuery( "SELECT nama_karyawan, nomor_induk, k.index, k.gaji " +
                    "FROM data_karyawan k WHERE tipe = 'harian' AND status = TRUE" );
            
            int i = 0;
            while(uprs.next()) {
                
                //if the information from db, add the empty row
                if(i==dailyemployeetb.getRowCount()) {
                    DefaultTableModel model = (DefaultTableModel)dailyemployeetb.getModel();
                    model.addRow( new Object[] { null, null, null, 0, 0, null,
                            null, null, null, null } );
                }
                
                //code number column
                dailyemployeetb.getModel().setValueAt( uprs.getString("nomor_induk"), i, 0 );
                
                //name column
                dailyemployeetb.getModel().setValueAt(
                        firstLetterCaps( uprs.getString("nama_karyawan") ), i, 1 );
                
                //salary
                dailyemployeetb.getModel().setValueAt(
                        nf.format(
                        employeedb.getMonthSalaryDailyEmployee(uprs.getInt("gaji"), date) ), i, 2 );
                
                //paid boolean
                dailyemployeetb.getModel().setValueAt( true, i, 5 );
                
                //index for easiness
                dailyemployeetb.getModel().setValueAt( uprs.getInt("index"), i, 6 );
                
                ResultSet uprshelp = stmthelp.executeQuery("SELECT max(tanggal_gaji) " +
                        "AS terakhir_bayar FROM transaksi_gaji g  LEFT JOIN data_transaksi_gaji " +
                        "d ON ( d.index = g.index_transaksi_gaji ) WHERE " +
                        "g.tipe = 'karyawan harian' AND g.index_karyawan = " +
                        uprs.getInt("index") + " GROUP BY g.index_karyawan");
                
                if(uprshelp.next()) {
                    //last payment
                    if(uprshelp.getDate("terakhir_bayar")!=null)
                        dailyemployeetb.getModel().setValueAt(
                                df.format(uprshelp.getDate("terakhir_bayar")), i, 7 );
                }
                
                i++;
                
            }
            
            //monthly employee table
//            uprs = stmt.executeQuery( "SELECT nama_karyawan, nomor_induk, k.index, k.gaji, " +
//                    "max(tanggal_gaji) AS terakhir_bayar FROM transaksi_gaji g " +
//                    "INNER JOIN data_transaksi_gaji ON ( index = index_transaksi_gaji ) " +
//                    "INNER JOIN data_karyawan k ON( k.index = index_karyawan ) " +
//                    "WHERE g.tipe = 'karyawan bulanan' GROUP BY nama_karyawan, nomor_induk, " +
//                    "k.index, k.gaji" );
            
            uprs = stmt.executeQuery( "SELECT nama_karyawan, nomor_induk, index, gaji " +
                    "FROM data_karyawan WHERE tipe = 'bulanan' AND status = TRUE" );
            
            i = 0;
            while(uprs.next()) {
                
                //if the information from db, add the empty row
                if(i==monthlyemployeetb.getRowCount()) {
                    DefaultTableModel model = (DefaultTableModel)monthlyemployeetb.getModel();
                    model.addRow( new Object[] { null, null, null, 0, 0, null,
                            null, null, null, null } );
                }
                
                //code number column
                monthlyemployeetb.getModel().setValueAt( uprs.getString("nomor_induk"), i, 0 );
                
                //name column
                monthlyemployeetb.getModel().setValueAt(
                        firstLetterCaps( uprs.getString("nama_karyawan") ), i, 1 );
                
                //salary
                monthlyemployeetb.getModel().setValueAt(
                        nf.format( uprs.getInt("gaji") ), i, 2 );
                
                //paid boolean
                monthlyemployeetb.getModel().setValueAt( true, i, 5 );
                
                //index for easiness
                monthlyemployeetb.getModel().setValueAt( uprs.getInt("index"), i, 6 );
                
                ResultSet uprshelp = stmthelp.executeQuery("SELECT max(tanggal_gaji) " +
                        "AS terakhir_bayar FROM transaksi_gaji g  LEFT JOIN data_transaksi_gaji " +
                        "d ON ( d.index = g.index_transaksi_gaji ) WHERE " +
                        "g.tipe = 'karyawan bulanan' AND g.index_karyawan = " +
                        uprs.getInt("index") + " GROUP BY g.index_karyawan");
                
                if(uprshelp.next()) {
                    //last payment
                    if(uprshelp.getDate("terakhir_bayar")!=null)
                        monthlyemployeetb.getModel().setValueAt(
                                df.format(uprshelp.getDate("terakhir_bayar")), i, 7 );
                }
                
                i++;
                
                
            }
            
            //salesman table
//            uprs = stmt.executeQuery( "SELECT nama_salesman, nomor_induk, index_salesman, s.gaji, " +
//                    "max(tanggal_gaji) AS terakhir_bayar FROM transaksi_gaji " +
//                    "INNER JOIN data_transaksi_gaji ON ( index = index_transaksi_gaji ) " +
//                    "INNER JOIN data_salesman s ON( index_salesman = index_karyawan ) " +
//                    "WHERE tipe = 'salesman' AND status = true AND s.gaji > 0 " +
//                    "GROUP BY nama_salesman, nomor_induk, index_salesman, s.gaji" );
            
            uprs = stmt.executeQuery( "SELECT nama_salesman, nomor_induk, index_salesman AS " +
                    "index, gaji FROM data_salesman WHERE status = TRUE AND gaji > 0 " );
            
            SalesmanDB salesmandb = new SalesmanDB( conn );
            
            i = 0;
            while(uprs.next()) {
                
                //if the information from db, add the empty row
                if(i==salesmantb.getRowCount()) {
                    DefaultTableModel model = (DefaultTableModel)salesmantb.getModel();
                    model.addRow( new Object[] { null, null, null, 0, 0, null,
                            null, null, null, null } );
                }
                
                //code number column
                salesmantb.getModel().setValueAt( uprs.getString("nomor_induk"), i, 0 );
                
                //name column
                salesmantb.getModel().setValueAt(
                        firstLetterCaps( uprs.getString("nama_salesman") ), i, 1 );
                
                //salary
                salesmantb.getModel().setValueAt( nf.format(uprs.getInt("gaji")), i, 2 );
                
                //his "value"
                salesmantb.getModel().setValueAt(
                        nf.format( salesmandb.getMonthCommision(month, year,
                        uprs.getString("nama_salesman") ) ), i, 5 );
                
                //paid boolean
                salesmantb.getModel().setValueAt( true, i, 6 );
                
                //index for easiness
                salesmantb.getModel().setValueAt( uprs.getInt("index"), i, 7 );
                
                ResultSet uprshelp = stmthelp.executeQuery("SELECT max(tanggal_gaji) " +
                        "AS terakhir_bayar FROM transaksi_gaji g  LEFT JOIN data_transaksi_gaji " +
                        "d ON ( d.index = g.index_transaksi_gaji ) WHERE " +
                        "g.tipe = 'salesman' AND g.index_karyawan = " +
                        uprs.getInt("index") + " GROUP BY g.index_karyawan");
                
                if(uprshelp.next()) {
                    //last payment
                    if(uprshelp.getDate("terakhir_bayar")!=null)
                        salesmantb.getModel().setValueAt(
                                df.format(uprshelp.getDate("terakhir_bayar")), i, 8 );
                }
                
                i++;
                
                
            }
            
            uprs.close();
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    public boolean InvoiceNumberNotUnique( String invoice, String type ) {
        invoice = "'" + invoice + "'";
        try {
            stmt = conn.createStatement();
            
            if(type.equals("sale"))
                uprs = stmt.executeQuery("SELECT invoice FROM data_transaksi_penjualan" +
                        " WHERE invoice = " + invoice );
            else if(type.equals("purchase"))
                uprs = stmt.executeQuery("SELECT invoice FROM data_transaksi_pembelian" +
                        " WHERE invoice = " + invoice );
            
            //if there is.... so not unique
            if(uprs.next()) {
                return true;
            }
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        } finally {
            try {
                uprs.close();
                stmt.close();
            } catch( SQLException e ) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    public boolean StockOverLoaded( JTable table, String trtype ) {
        //how many warehouse that we have
        int warehouseamount = 0;
        
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT count(nama) AS jumlah FROM data_gudang" );
            
            uprs.next();
            
            warehouseamount = uprs.getInt( "jumlah" );
            
            uprs.close();
            stmt.close();
            
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
        //how many row that we have
        int rowCount = table.getRowCount();
        
        //how many ( total ) do we want to take this item from warehouse???
        int totaltake = 0;
        
        //stock that we want to take from this warehouse
        int stock = 0;
        
        //quantity of item waht we want to sell
        int quantity = 0;
        
        //item name
        String inventoryname = "";
        
        //check for every valid row
        for( int row=0; row<rowCount; row++ ) {
            //just check the name, damn it!!!!
            if( table.getValueAt(row,0) != null ) {
                inventoryname = (String)table.getValueAt( row, 0 );
                totaltake = 0;
                quantity = ( (Integer)table.getValueAt( row, 1 ) ).intValue();
                //check for every warehouse
                for( int i=0; i<warehouseamount; i++ ) {
                    stock = ( (Integer)table.getValueAt( row, 2 + i ) ).intValue();
                    totaltake += stock;
                }
                
                if(totaltake>quantity) {
                    if(trtype.equals("sale")) {
                        JOptionPane.showMessageDialog( null, "You want to take " + totaltake +
                                " " + inventoryname + " from the warehouse... but you want " +
                                "to sell only " + quantity + " !" );
                    }
                    else if(trtype.equals("purchase")) {
                        JOptionPane.showMessageDialog( null, "You want to put " + totaltake +
                                " " + inventoryname + " to the warehouse... but you want " +
                                "to purchase only " + quantity + " !" );
                    }
                    return true;
                }
            }
        }
        
        return false;
        
    }
    
    public boolean StockNotEnough( String supply, JTable table ) {
        //how many warehouse that we have
        int warehouseamount = 0;
        
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT count(nama) AS jumlah FROM data_gudang" );
            
            uprs.next();
            
            warehouseamount = uprs.getInt( "jumlah" );
            
            uprs.close();
            stmt.close();
            
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
        //how many row that we have
        int rowCount = table.getRowCount();
        
        //how many items that we want to sell
        int quantity = 0;
        
        //how many items that we want to take from the warehouse
        int take = 0;
        
        //the supply of items in warehouse
        int stock = 0;
        
        //the name of item
        String inventoryname = "";
        
        //the name of warehouse
        String warehousename = "";
        
        if( supply.equals("Custom") ) {
            
            //check for every warehouse
            for( int i=0; i<warehouseamount; i++ ) {
                
                warehousename = (String)table.getColumnModel().getColumn(2+i).getHeaderValue();
                warehousename = warehousename.substring(10);
                
                //check for every valid row
                for( int row=0; row<rowCount; row++ ) {
                    
                    //just check the name, damn it!!!!
                    if( table.getValueAt(row,0) != null ) {
                        
                        inventoryname = (String)table.getValueAt( row, 0 );
                        quantity = ( (Integer)table.getValueAt( row, 1 ) ).intValue();
                        take = ( (Integer)table.getValueAt( row, 2 + i ) ).intValue();
                        stock = ( (Integer)table.getValueAt( row, 2 + warehouseamount + i ) ).intValue();
                        
                        if( take > stock ) {
                            
                            JOptionPane.showMessageDialog( null,
                                    "The amount of " + inventoryname + " that you want to sell from " +
                                    firstLetterCaps(warehousename) + " is " + take + " \nbut " +
                                    "the supply of " + inventoryname + " in warehouse " +
                                    firstLetterCaps(warehousename) + " is " + stock + "!","Warning",
                                    JOptionPane.WARNING_MESSAGE );
                            return true;
                        }
                        
                    }
                    
                }
            }
        } 
        else {
            warehousename = supply;
            supply = "'" + supply.toLowerCase() + "'";
            
            //warehouse temp
            String warehousetemp = "";
            
            //check for every valid row
            for( int row=0; row<rowCount; row++ ) {
                
                //just check the name, damn it!!!!
                if( table.getValueAt(row,0) != null ) {
                    
                    inventoryname = (String)table.getValueAt( row, 0 );
                    quantity = ( (Integer)table.getValueAt( row, 1 ) ).intValue();
                    take = ( (Integer)table.getValueAt( row, 1 ) ).intValue();
                    String tempinventoryname = "'" + inventoryname.toLowerCase() + "'";
                    
                    try {
                        stmt = conn.createStatement();
                        
                        uprs = stmt.executeQuery( "SELECT b.nama AS nama_barang, " +
                                "sum(jumlah) AS jumlahtotal, g.nama AS nama_gudang " +
                                "FROM stok_gudang s INNER JOIN data_barang b ON " +
                                "( s.index_barang = b.index ) " +
                                "INNER JOIN data_gudang g ON ( g.index = s.index_gudang ) " +
                                "WHERE g.nama = " + supply + " AND b.nama = " +
                                tempinventoryname +
                                " GROUP BY b.nama, s.index_gudang, g.nama ORDER BY b.nama");
                        uprs.next();
                        
                        stock = uprs.getInt("jumlahtotal");
                        
                        uprs.close();
                        stmt.close();
                    } catch( SQLException ex ) {
                        System.out.println(tempinventoryname);
                        System.out.println(supply);
                        ex.printStackTrace();
                        new WriteLogFile(ex).writeLogFile();
                    }
                    
                    if( take > stock ) {
                        
                        JOptionPane.showMessageDialog( null,
                                "The amount of " + inventoryname + " that you want to sell from " +
                                warehousename + " is " + take + " \nbut " +
                                "the supply of " + inventoryname + " in warehouse " +
                                warehousename + " is " + stock + "!","Warning",
                                JOptionPane.WARNING_MESSAGE );
                        return true;
                    }
                    
                }
                
            }
        }
        return false;
    }
    
    public List<String> SearchSaleTransaction( String invoice, String salesman, String customer,
            String sender, String discountless, String discountmore, String comment ) {
        
        //there are too many search...... types
        String search = " index > -1 ";
        
        //array list of invoice to be proseced in html
        List<String> invoicelist = null;
        
        //if empty why we're here????
        if(invoice==null&&salesman==null&&customer==null&&sender==null&&discountless==null
                &&discountmore==null&&comment==null) {
            return null;
        }
        
        //customize string search
        if(invoice!=null) {
            invoice = " invoice LIKE '%" + invoice + "%' ";
            search = search + " AND " + invoice;
        }
        if(salesman!=null) {
            salesman = " nama_salesman LIKE '%" + salesman.toLowerCase() + "%' ";
            search = search + " AND " + salesman;
        }
        if(customer!=null) {
            customer = " c.nama LIKE '%" + customer.toLowerCase() + "%' ";
            search = search + " AND " + customer;
        }
        if(sender!=null) {
            sender = " pengirim LIKE '%" + sender.toLowerCase() + "%' ";
            search = search + " AND " + sender;
        }
        if(discountless!=null) {
            discountless = " potongan < " + discountless + " ";
            search = search + " AND " + discountless;
        }
        if(discountmore!=null) {
            discountmore = " potongan > " + discountmore + " ";
            search = search + " AND " + discountmore;
        }
        if(comment!=null) {
            comment = " p.komentar LIKE '%" + comment + "%'";
            search = search + " AND " + comment;
        }
        
        try {
            //get the conn reference
            stmt = conn.createStatement();
            
            //get the resultset
            uprs = stmt.executeQuery( "SELECT invoice FROM data_transaksi_penjualan p LEFT JOIN " +
                    "data_salesman ON( nomor_salesman = index_salesman ) LEFT JOIN " +
                    "data_pembeli c ON( index_pembeli = nomor_pembeli ) WHERE " + search );
            
            invoicelist = new ArrayList<String>();
            //fill data
            while(uprs.next()) {
                invoicelist.add( uprs.getString("invoice") );
            }
            
            //close the resource
            uprs.close();
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
        return invoicelist;
    }
    
    public List<String> SearchPurchaseTransaction( String invoice, String commisioner,
            String suplier, String sender, String discountless, String discountmore, String comment ) {
        
        //there are too many search...... types
        String search = " index > -1 ";
        
        //array list of invoice to be proseced in html
        List<String> invoicelist = null;
        
        //if empty why we're here????
        if(invoice==null&&commisioner==null&&suplier==null&&sender==null&&discountless==null
                &&discountmore==null&&comment==null) {
            return null;
        }
        
        //customize the string search
        if(invoice!=null) {
            invoice = " invoice LIKE '%" + invoice + "%' ";
            search = search + " AND " + invoice;
        }
        if(commisioner!=null) {
            commisioner = " nama_komisioner LIKE '%" + commisioner.toLowerCase() + "%' ";
            search = search + " AND " + commisioner;
        }
        if(suplier!=null) {
            suplier = " suplier LIKE '%" + suplier.toLowerCase() + "%' ";
            search = search + " AND " + suplier;
        }
        if(sender!=null) {
            sender = " pengirim LIKE '%" + sender.toLowerCase() + "%' ";
            search = search + " AND " + sender;
        }
        if(discountless!=null) {
            discountless = " potongan < " + discountless + " ";
            search = search + " AND " + discountless;
        }
        if(discountmore!=null) {
            discountmore = " potongan > " + discountmore + " ";
            search = search + " AND " + discountmore;
        }
        if(comment!=null) {
            comment = " p.komentar LIKE '%" + comment + "%'";
            search = search + " AND " + comment;
        }
        
        try {
            //get the conn reference
            stmt = conn.createStatement();
            
            //get the resultset
            uprs = stmt.executeQuery( "SELECT invoice FROM data_transaksi_pembelian p LEFT JOIN " +
                    "data_komisioner USING ( kd_komisioner ) " +
                    "WHERE " + search );
            
            invoicelist = new ArrayList<String>();
            //fill data
            while(uprs.next()) {
                invoicelist.add( uprs.getString("invoice") );
            }
            
            //close the resource
            uprs.close();
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
        return invoicelist;
    }
    
    public List<String> SearchSalaryTransaction( String invoice, String comment ) {
        
        //there are too many search...... types
        String search = " index > -1 ";
        
        //array list of invoice to be proseced in html
        List<String> invoicelist = null;
        
        //if empty why we're here????
        if(invoice==null&&comment==null) {
            return null;
        }
        
        //customize the string search
        if(invoice!=null) {
            invoice = " invoice_gaji LIKE '%" + invoice + "%' ";
            search = search + " AND " + invoice;
        }
        if(comment!=null) {
            comment = " g.komentar LIKE '%" + comment + "%'";
            search = search + " AND " + comment;
        }
        
        try {
            //get the conn reference
            stmt = conn.createStatement();
            
            //get the resultset
            uprs = stmt.executeQuery( "SELECT invoice_gaji AS invoice FROM data_transaksi_gaji g " +
                    "WHERE " + search );
            
            invoicelist = new ArrayList<String>();
            //fill data
            while(uprs.next()) {
                invoicelist.add( uprs.getString("invoice") );
            }
            
            //close the resource
            uprs.close();
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
        return invoicelist;
    }
    
    public List<String> SearchIncomeTransaction( String invoice, String valueless, String valuemore,
            String comment ) {
        
        //there are too many search...... types
        String search = " jumlah > 0 ";
        
        //array list of invoice to be proseced in html
        List<String> invoicelist = null;
        
        //if empty why we're here????
        if(invoice==null&&valueless==null&&valuemore==null&&comment==null) {
            return null;
        }
        
        //customize the string search
        if(invoice!=null) {
            invoice = " invoice LIKE '%" + invoice + "%' ";
            search = search + " AND " + invoice;
        }
        if(valueless!=null) {
            valueless = " jumlah < " + valueless + " ";
            search = search + " AND " + valueless;
        }
        if(valuemore!=null) {
            valuemore = " jumlah > " + valuemore + " ";
            search = search + " AND " + valuemore;
        }
        if(comment!=null) {
            comment = " deskripsi LIKE '%" + comment + "%'";
            search = search + " AND " + comment;
        }
        
        try {
            //get the conn reference
            stmt = conn.createStatement();
            
            //get the resultset
            uprs = stmt.executeQuery( "SELECT invoice FROM transaksi_pemasukan_lain " +
                    "WHERE " + search );
            
            invoicelist = new ArrayList<String>();
            //fill data
            while(uprs.next()) {
                invoicelist.add( uprs.getString("invoice") );
            }
            
            //close the resource
            uprs.close();
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
        return invoicelist;
    }
    
    public List<String> SearchOutcomeTransaction( String invoice, String type, String valueless,
            String valuemore, String comment ) {
        
        //there are too many search...... types
        String search = " jumlah > 0 ";
        
        //array list of invoice to be proseced in html
        List<String> invoicelist = null;
        
        //if empty why we're here????
        if(invoice==null&&type==null&&valueless==null&&valuemore==null&&comment==null) {
            return null;
        }
        
        //customize the string search
        if(invoice!=null) {
            invoice = " invoice LIKE '%" + invoice + "%' ";
            search = search + " AND " + invoice;
        }
        if(type!=null) {
            type = " nama LIKE '%" + type + "%' ";
            search = search + " AND " + type;
        }
        if(valueless!=null) {
            valueless = " jumlah < " + valueless + " ";
            search = search + " AND " + valueless;
        }
        if(valuemore!=null) {
            valuemore = " jumlah > " + valuemore + " ";
            search = search + " AND " + valuemore;
        }
        if(comment!=null) {
            comment = " komentar LIKE '%" + comment + "%'";
            search = search + " AND " + comment;
        }
        
        try {
            //get the conn reference
            stmt = conn.createStatement();
            
            //get the resultset
            uprs = stmt.executeQuery( "SELECT invoice FROM transaksi_pengeluaran " +
                    "WHERE " + search );
            
            invoicelist = new ArrayList<String>();
            //fill data
            while(uprs.next()) {
                invoicelist.add( uprs.getString("invoice") );
            }
            
            //close the resource
            uprs.close();
            stmt.close();
        } catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
        return invoicelist;
    }
    
    public void Validate( JTable table ) {
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        
        for(int row=0; row<table.getRowCount(); row++ ) {
            if(model.getValueAt(row,0)!=null) {
                //lowercase the item name
                String itemname = ((String)model.getValueAt(row,0)).toLowerCase().trim();
                try {
                    stmt = conn.createStatement();
                    
                    itemname = "'" + itemname + "'";
                    uprs = stmt.executeQuery("SELECT nama FROM data_barang  " +
                            "  WHERE nama = " + itemname );
                    if(uprs.next()) {
                        model.setValueAt(true,row,5);
                        //check if the "row" is valid
                        int discount = ((Integer)model.getValueAt( row, 2 )).intValue();
                        int unit_price = ((Integer)model.getValueAt( row, 1 )).intValue();
                        int amount = ((Integer)model.getValueAt( row, 3)).intValue();
                        if(((Integer)model.getValueAt( row, 4 )).intValue()!=((unit_price - discount) * amount)) {
                            model.setValueAt(false,row,5);
                        } else if(amount==0) {
                            model.setValueAt(false,row,5);
                        }
                    } else {
                        model.setValueAt(false,row,5);
                    }
                    uprs.close();
                    stmt.close();
                } catch( SQLException ex ) {
                    ex.printStackTrace();
                    new WriteLogFile(ex).writeLogFile();
                }
            }
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
    
    public void cleanUp( ) {
        try {
            conn.close();
        } catch( SQLException e ) {
            e.printStackTrace();
        }
    }
    
}
