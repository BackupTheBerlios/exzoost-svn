/*
 * Stock.java
 *
 * Created on December 5, 2004, 9:12 PM
 */

package com.exzoost.database;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
/**
 *
 * @author knight
 */
public class InventoryDB {
    private Connection conn;
    private ResultSet uprs, uprs2;
    private Transaction tr;
    private Statement stmt, stmt2;
    private NumberFormat nf;
    /** Creates a new instance of Stock */
    //we must use conn reference to do the operation
    public InventoryDB( Connection conn ) {
        this.conn = conn;
        //format the currency
        nf = NumberFormat.getCurrencyInstance( new Locale("id","id"));
    }
    
    public String initializeDefaultItemCode() {
        String itemcode = "";
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery("SELECT max(oid) AS code FROM data_barang");
            
            uprs.next();
            
            itemcode = uprs.getString("code");
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
        return itemcode;
    }
    
    public void addSupplyItem( String name, int addsupply, String warehouse ) {
        try {
            stmt = conn.createStatement();
            
            name = "'" + name + "'";
            warehouse = "'" + warehouse + "'";
            
            //this is the index of item & warehouse
            int itemindex = 0;
            int warehouseindex = 0;
            
            uprs = stmt.executeQuery( "SELECT index FROM data_gudang WHERE nama = " + warehouse );
            
            if(uprs.next()) {
                warehouseindex = uprs.getInt( "index" );
            }
            
            uprs = stmt.executeQuery( "SELECT max(index) AS maxindex FROM data_barang WHERE nama = " +
                    name );
            
            if(uprs.next()) {
                itemindex = uprs.getInt("maxindex");
            }
                        
            stmt.executeUpdate( "UPDATE stok_gudang SET jumlah = jumlah + " + addsupply +
                    " WHERE index_barang = " + itemindex + " AND index_gudang = " + warehouseindex );
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
    }
    
    public void reduceSupplyItem( String name, int reducesupply, String warehouse ) {
        try {
            stmt = conn.createStatement();
            
            name = "'" + name + "'";
            warehouse = "'" + warehouse + "'";
            
            //this is the index of item & warehouse
            List<Integer> itemindex = new ArrayList<Integer>();
            int warehouseindex = 0;
            
            uprs = stmt.executeQuery( "SELECT index FROM data_gudang WHERE nama = " + warehouse );
            
            if(uprs.next()) {
                warehouseindex = uprs.getInt( "index" );
            }
            
            uprs = stmt.executeQuery( "SELECT index FROM data_barang WHERE nama = " +
                    name + " ORDER BY index DESC");
            
            while(uprs.next()) {
                itemindex.add( uprs.getInt("index") );
            }
                        
            stmt.executeUpdate( "BEGIN WORK" );
            
            int amount = 0;
            for( int i = 0; i < itemindex.size() && reducesupply > 0; i++ ) {
                
                uprs = stmt.executeQuery( "SELECT jumlah FROM stok_gudang WHERE index_barang = " +
                        itemindex.get(i) + " AND index_gudang = " + warehouseindex );
                
                uprs.next();
                
                amount = uprs.getInt("jumlah");
                
                if(reducesupply>=amount) {
                    stmt.executeUpdate( "UPDATE stok_gudang SET jumlah = 0 " +
                            " WHERE index_barang = " + itemindex.get(i) + 
                            " AND index_gudang = " + warehouseindex );
                    reducesupply -= amount;
                }
                else if(reducesupply<amount) {
                    stmt.executeUpdate( "UPDATE stok_gudang SET jumlah = jumlah - " + reducesupply +
                            " WHERE index_barang = " + itemindex.get(i) +
                            " AND index_gudang = " + warehouseindex );
                    reducesupply = 0;
                }
            }
            
            stmt.executeUpdate( "COMMIT WORK" );
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            try {
                if(stmt!=null) {
                    stmt.executeUpdate( "ROLLBACK" );
                    stmt.close();
                }
            }
            catch( SQLException ex ) {
                ex.printStackTrace();
            }
            new WriteLogFile(e).writeLogFile();
        }
    }
            
    //when user add item, the name must not be same with previous name
    public boolean isAlreadyThereItem( String itemname ) {
        //format the string
        itemname = "'" + itemname.toLowerCase() + "'";
        
        //return value
        boolean value = false;
        
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT nama FROM data_barang WHERE nama = " +
                    itemname );
            
            if(uprs.next())
                value = true;
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
        
        return value;
    }
    
    public boolean deleteItem( int item_index ) {
        
        try {
            stmt = conn.createStatement();
            
            stmt.executeUpdate( "BEGIN WORK" );
            
            stmt.executeUpdate("DELETE FROM suplier_barang WHERE index_barang = " + 
                    item_index);
            
            stmt.executeUpdate("DELETE FROM stok_gudang WHERE index_barang = " + 
                    item_index);
            
            stmt.executeUpdate("DELETE FROM data_barang WHERE index = " + 
                    item_index);
            
            stmt.executeUpdate( "COMMIT WORK" );
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            try {
                if(stmt!=null) stmt.executeUpdate( "ROLLBACK" );
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
    
    public boolean UpdateValidName( String oldname, String newname ) {
        boolean result = true;
        
        //make it lowercase
        oldname = "'" + oldname.toLowerCase() + "'";
        newname = "'" + newname.toLowerCase() + "'";
        
        if(oldname.equals(newname))
            return result;
                
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT nama FROM data_barang WHERE nama = " + 
                    newname );
            
            if(uprs.next())
                result = false;
            
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            return false;
        }
        return result;
    }

    public boolean updateItem( int item_index, String category, String name, 
            String itemcode, String comment, int buying_price, int sale_price, 
            int length, int width, int height, int volume, String primary, 
            String secondary, String measurement, String producer, String third, 
            String thirdmeasurement, ArrayList seller_list ) {
         
        //format the string
        String originalname = name;
        category = "'" + category + "'";
        name = "'" + name.toLowerCase() + "'";
        itemcode = "'" + itemcode + "'";
        if(comment.trim().equals(""))
            comment = "null";
        else
            comment = "'" + comment + "'";
        primary = "'" + primary + "'";
        secondary = "'" + secondary + "'";
        
        if(third.trim().equals(""))
            third = "null";
        else
            third = "'" + third + "'";
        if(thirdmeasurement.trim().equals(""))
            thirdmeasurement = "null";
        else
            thirdmeasurement = "'" + thirdmeasurement + "'";
        
        if(producer.trim().equals(""))
            producer = "null";
        else
            producer = "'" + producer.toLowerCase() + "'";
        
        SellerDB sellerdb = new SellerDB( conn );
        
        try {
            stmt = conn.createStatement();
        
            stmt.executeUpdate( "BEGIN WORK" );
            
            stmt.executeUpdate( "UPDATE data_barang SET kategori = " + category + 
                    ", nama = " + name + ", kd_barang = " + itemcode + ", komentar = " +
                    comment + ", harga_modal = " + buying_price + ", harga_jual = " +
                    sale_price + ", panjang = " + length + ", lebar = " + width +
                    ", tinggi = " + height + ", volume = " + volume + ", ukuran_primer = " +
                    primary + ", ukuran_sekunder = " + secondary + ", jumlah_ukuran = " +
                    measurement + ", ukuran_ketiga = " + third +
                    ", jumlah_ukuran_dua_ke_tiga = " + thirdmeasurement + 
                    ", index_produsen = ( SELECT index FROM data_produsen WHERE " +
                    "data_produsen.nama = " + producer +
                    " ) WHERE index = " + item_index );
            
            //set up the seller for item list
            //sellerdb.insertItemSeller(listmodel, originalname);
            
            stmt.executeUpdate( "COMMIT WORK" );
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            try {
                if(stmt!=null) {
                    stmt.executeUpdate( "ROLLBACK" );
                    stmt.close();
                }
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
    
    public void initializeLowItemsList( JTable table, String place, int limit ) {
        
        //format the string
        place = "'" + place.toLowerCase() + "'";
        
        try {
            stmt = conn.createStatement();
            
            if(place.equals("'total'")) {
                uprs = stmt.executeQuery( "SELECT d.nama, sum(jumlah) as jumlah " +
                        "FROM data_barang d inner join stok_gudang sg on " +
                        "(sg.index_barang = d.index) group by d.nama " +
                        " ORDER BY d.nama");    
            }
            else {
                uprs = stmt.executeQuery( "SELECT d.nama, sum(jumlah) as jumlah " +
                        "FROM data_barang d inner join stok_gudang sg on " +
                        "(sg.index_barang = d.index) inner join data_gudang dg " +
                        "on (dg.index = sg.index_gudang) where dg.nama = " + place + 
                        " group by d.nama " +
                        " ORDER BY d.nama");
            }
            
            int i = 0;
            while(uprs.next()) {
                
                if(uprs.getInt("jumlah")<limit) {
                    //if jtable is full, add empty row
                    if(i==table.getRowCount()) {
                        ((DefaultTableModel)table.getModel()).addRow( new Object[] { null, null } );
                    }

                    table.getModel().setValueAt( firstLetterCaps(uprs.getString("nama")), i, 0 );
                    table.getModel().setValueAt( uprs.getInt("jumlah"), i, 1 );

                    i++;
                }
            }
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
    }
    
    public boolean insertItem( String name, String itemCode, int salePrice, 
            int buyingPrice, String comment, Integer length, Integer width,
            Integer height, Integer volume, String primary, String secondary, 
            int measurement, String category, String producer, String third, 
            String thirdmeasurement, DefaultListModel listmodel ) {
        //format the string
        String originalname = name;
        name = "'" + name + "'";
        itemCode = "'" + itemCode + "'";
        if(comment.trim().equals("")) 
            comment = "null";
        else
            comment = "'" + comment + "'";
        primary = "'" + primary + "'";
        secondary = "'" + secondary + "'";
        category = "'" + category + "'";
        producer = "'" + producer + "'";
        int indprod = 0;
        
        //update: 3 july 2006, add two other columns
        if(third.trim().equals(""))
            third = "null";
        else
            third = "'" + third + "'";
        if(thirdmeasurement.trim().equals(""))
            thirdmeasurement = "null";
        else
            thirdmeasurement = "'" + thirdmeasurement + "'";
        
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate("BEGIN WORK");
            
            uprs = stmt.executeQuery( "SELECT index FROM data_produsen WHERE nama = " + 
                    producer.toLowerCase() );
            
            if(uprs.next())
                indprod = uprs.getInt("index");
            
            stmt.executeUpdate( "INSERT INTO data_barang ( " +
                " nama, kd_barang, harga_modal, harga_jual, komentar, kategori, " +
                    "index_produsen, " +
                " panjang, lebar, tinggi, volume, jumlah_ukuran, ukuran_primer, " +
                    "ukuran_sekunder, " +
                " ukuran_ketiga, jumlah_ukuran_dua_ke_tiga ) " +
                "VALUES ( " + name + ", " + itemCode + ", " + buyingPrice + ", " + 
                salePrice + ", " + comment + ", " + category + ", " + indprod + 
                ", " + length + ", " + width + ", " + height + ", " + volume +
                ", " + measurement + ", " + primary + ", " + secondary +
                ", " + third + ", " + thirdmeasurement + " ) " );
            
            //insert into seller of item
            int sellerindex = 0;
            String sellername = "";
            for( int i=0; i<listmodel.getSize(); i++) {
                sellername = "'" + ((String)listmodel.get(i)).toLowerCase() + "'"; 
                uprs = stmt.executeQuery( "SELECT kd_suplier FROM data_suplier WHERE " +
                        "nama_suplier = " +
                         sellername );
                uprs.next();
                sellerindex = uprs.getInt("kd_suplier");
                stmt.executeUpdate( "INSERT INTO suplier_barang ( index_barang, " +
                        "index_suplier ) " +
                        "VALUES ( currval('kd_barang_seq'), " + sellerindex + " ) " );
            }
            
            stmt.executeUpdate("COMMIT WORK");
                        
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            try {
                stmt.executeUpdate( "ROLLBACK" );
                stmt.close();
            }
            catch( SQLException cep ) {
                cep.printStackTrace();
            }
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        
        return true;
        
    }
    
    public void updateSalePriceItem( JTable e, String NewSalePrice ) {
        //which row to be clone???
        int row = e.getSelectedRow();
        
        //get the index of the item that we want to edit
        int itemindex = 0;
        
        itemindex = ((Integer)e.getModel().getValueAt(row,7)).intValue();
        
        try {
            stmt = conn.createStatement();
            
            //start the operation
            stmt.executeUpdate( "BEGIN WORK" );
            
            stmt.executeUpdate( "UPDATE data_barang SET harga_jual = " +
                    NewSalePrice + " WHERE index = " + itemindex );
            
            //record the price change
//            stmt.executeUpdate("INSERT INTO perubahan_harga_barang ( " +
//                    "index_barang, harga_jual, tanggal ) " +
//                    "VALUES ( " + itemindex + ", " + NewSalePrice + ", 'TODAY' )" );
            
            //finish the operation
            stmt.executeUpdate( "COMMIT WORK" );
            
            //just to make sure
            uprs = stmt.executeQuery( "SELECT v.harga_jual " +
                        "FROM view_stok_table_single v WHERE index = " +
                        itemindex );
            
            uprs.next();
            
            //update the jtable
            e.getModel().setValueAt( nf.format(uprs.getInt("harga_jual")), row, 4 );
            
            //free the resources
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    public void initializeDataItems( JTable e, String place, String orderby,
            int offset, int limit ) {
        SellerDB sellerdb = new SellerDB( conn );
        try {
            stmt = conn.createStatement();
            stmt2 = conn.createStatement();
            
            String param_place = "";
            if(!place.equals("total")) {
                uprs = stmt.executeQuery("select index from data_gudang where nama = '" + 
                        place + "'");
                uprs.next();
                int warehouse_index = uprs.getInt("index");
                param_place = " where index_gudang = " + warehouse_index;
            }
            
            uprs = stmt.executeQuery("select db.*, dp.nama as nama_produsen, sum(jumlah) " +
                    "as jumlah_barang from data_barang as db inner join data_produsen as dp " +
                    "on dp.index = db.index_produsen left join " +
                    "( select * from stok_gudang " + param_place + " ) sg on " +
                    "sg.index_barang = db.index left join data_gudang dg on " +
                    "dg.index = sg.index_gudang group " +
                    "by db.index, db.nama, db.harga_modal, db.harga_jual, db.kd_barang, " +
                    "db.komentar, db.kategori, db.index_produsen, db.panjang, db.lebar, " +
                    "db.tinggi, db.volume, db.jumlah_ukuran, db.ukuran_primer, " +
                    "db.ukuran_sekunder, db.ukuran_ketiga, db.jumlah_ukuran, " +
                    "db.jumlah_ukuran_dua_ke_tiga, dp.nama order by " + orderby +
                    " limit " + limit + " offset " + offset);
                       
            int i = 0;
            //fill the jtable
            while(uprs.next()) {
                
                //if jtable is full, add empty row
                if(i==e.getRowCount()) {
                    ((DefaultTableModel)e.getModel()).addRow( 
                            new Object[] { null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null, null } );
                }
                
                e.getModel().setValueAt( uprs.getString("kd_barang"), i, 0 );
                e.getModel().setValueAt( uprs.getString("kategori"), i, 1 );
                e.getModel().setValueAt( firstLetterCaps(uprs.getString("nama")), i, 2 );
                e.getModel().setValueAt( nf.format(uprs.getInt("harga_modal")), i, 3 );
                e.getModel().setValueAt( nf.format(uprs.getInt("harga_jual")), i, 4 );
                e.getModel().setValueAt( uprs.getInt("jumlah_barang"), i, 5 );
                e.getModel().setValueAt( uprs.getString("komentar"), i, 6 );
                e.getModel().setValueAt( uprs.getInt("index"), i, 7 );
                e.getModel().setValueAt( uprs.getInt("panjang"), i, 8 );
                e.getModel().setValueAt( uprs.getInt("lebar"), i, 9 );
                e.getModel().setValueAt( uprs.getInt("tinggi"), i, 10 );
                e.getModel().setValueAt( uprs.getInt("volume"), i, 11 );
                e.getModel().setValueAt( uprs.getString("ukuran_primer"), i, 12 );
                e.getModel().setValueAt( uprs.getInt("jumlah_ukuran"), i, 13 );
                e.getModel().setValueAt( uprs.getString("ukuran_sekunder"), i, 14 );
                e.getModel().setValueAt( 
                        firstLetterCaps(uprs.getString("nama_produsen")), i, 15 );
                e.getModel().setValueAt( uprs.getInt("jumlah_ukuran_dua_ke_tiga"), i, 17 );
                e.getModel().setValueAt( uprs.getString("ukuran_ketiga"), i, 18 );
                
                //get the suplier
                uprs2 = stmt2.executeQuery("select nama_suplier from suplier_barang " +
                        "inner join data_suplier on kd_suplier = index_suplier where " +
                        "index_barang = " + uprs.getInt("index"));

                String suplier_for_item = "";
                if(uprs2.next())
                    suplier_for_item = firstLetterCaps(uprs2.getString("nama_suplier"));
                while(uprs2.next()) {
                    suplier_for_item += ", " + firstLetterCaps(uprs2.getString("nama_suplier"));
                }
                e.getModel().setValueAt( suplier_for_item, i, 16 );
                
                i++;
            }
            
            if(uprs2!=null) {
                uprs2.close();
                stmt2.close();
            }
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    //how many item are in database
    public int getItemRow() {
        int row = 0;
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT count(*) AS rowcount FROM data_barang" );
            
            if(uprs.next())
                row = uprs.getInt("rowcount");
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        return row;
    }
    
    //used by addquantitydialog; add quantity and expired date for an item
    public boolean addQuantityAndExpiredDate(int warehouse_index, int item_index,
            String date, String quantity) {
        try {
            stmt = conn.createStatement();
            
            if(date!=null)
                date = "'" + date + "'";
            else
                date = "null";
            
            if(date.equals("null")) {
                uprs = stmt.executeQuery("select * from stok_gudang where index_barang = " +
                        item_index + " and index_gudang = " + warehouse_index + 
                        " and tanggal_kadaluarsa is null");
                
                if(uprs.next()) {
                    throw new SQLException("tanggal_kadaluarsa with null value " +
                            "is already there.");
                }
            }
            stmt.executeUpdate("insert into stok_gudang ( index_barang, " +
                    "index_gudang, jumlah, tanggal_kadaluarsa ) values ( " +
                    item_index + ", " + warehouse_index + ", " + quantity + ", " + 
                    date + ")");
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        return true;
    }
    
    public boolean deleteQuantityAndExpiredDate(int item_index, int warehouse_index,
            String date ) {
        try {
            stmt = conn.createStatement();
            if(date!=null)
                stmt.executeUpdate("delete from stok_gudang where index_gudang = " + 
                        warehouse_index + " and " + "tanggal_kadaluarsa = '" + date + 
                        "' and index_barang = " + item_index );
            else
                stmt.executeUpdate("delete from stok_gudang where index_gudang = " + 
                        warehouse_index + " and " + "tanggal_kadaluarsa is " + date + 
                        " and index_barang = " + item_index );
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        return true;
    }
    
    public boolean editQuantityAndExpiredDate(int warehouse_index, int item_index,
            String date, String quantity) {
        try {
            stmt = conn.createStatement();
            String date_param = "";
            if(date!=null)
                date_param = " = '" + date + "' ";
            else
                date_param = " is null ";
            stmt.executeUpdate("update stok_gudang set jumlah = " + quantity +
                    " where index_gudang = " + warehouse_index + " and " +
                    "tanggal_kadaluarsa " + date_param + " and index_barang = " + item_index );
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
            return false;
        }
        return true;
    }
    
    public int getWarehouseIndex(String warehousename) {
        try {
            stmt = conn.createStatement();
            uprs = stmt.executeQuery("select index from data_gudang where nama = '" +
                    warehousename + "'");
            if(uprs.next()) {
                return uprs.getInt("index");
            }
            else {
                return -1;
            }
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        return -1;
    }
    
    public int getItemIndex(String itemcode) {
        try {
            stmt = conn.createStatement();
            uprs = stmt.executeQuery("select index from data_barang where kd_barang = '" +
                    itemcode + "'");
            if(uprs.next()) {
                return uprs.getInt("index");
            }
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        return -1;
    }
    
    //used by edititemdialog. Update the total quantity in quantity panel
    public int getTotalQuantityByWarehouseAndCode(String warehouse_name, String item_code) {
        //if 'total', then no search by warehouse
        String warehouse_search = "";
        if(!warehouse_name.equals("total")) {
            warehouse_search = "dg.nama = '" + warehouse_name + "' and ";
        }
        try {
            stmt = conn.createStatement();
            uprs = stmt.executeQuery("select sum(jumlah) as jumlah_barang from " +
                    "data_barang as " +
                "db inner join stok_gudang sg on db.index = sg.index_barang inner join " +
                "data_gudang dg on dg.index = sg.index_gudang where " +
                warehouse_search + " db.kd_barang = '" + item_code + "'");
            if(uprs.next())
                return uprs.getInt("jumlah_barang");
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        return -1;
    }
    
    //set up the quantity and expired date table
    public ArrayList<String[]> setQuantityExpiredDateTable(int warehouse_index, 
            int item_index) {
        
        //if -1(means 'total'), then no search by warehouse
        String warehouse_search = "";
        if(warehouse_index!=-1) {
            warehouse_search = " index_gudang = " + warehouse_index + " and ";
        }
        
        ArrayList<String[]> quantity_expired_list = new ArrayList<String[]>();
        try {
            stmt = conn.createStatement();
            uprs = stmt.executeQuery("select sum(jumlah) as jumlah_barang, " +
                    "tanggal_kadaluarsa from stok_gudang where " + warehouse_search +
                    " index_barang = " + item_index + " group by tanggal_kadaluarsa");

            while(uprs.next()) {
                String[] single_list = { uprs.getString("tanggal_kadaluarsa"),
                   uprs.getString("jumlah_barang") };
                quantity_expired_list.add(single_list);
            }
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        return quantity_expired_list;
    }
           
    public void searchItem( JTable e, String place, String category, String name, String itemcode,
            String buyingless, String buyingmore, String saleless, String salemore,
            String producer, String suplier, String quantityless, String quantitymore ) {
                
        try {
            stmt = conn.createStatement();
            stmt2 = conn.createStatement();
            
            String param_place = "";
            if(!place.equals("total")) {
                //format the place string
                uprs = stmt.executeQuery("select index from data_gudang where nama = '" + place + 
                        "'");
                if(uprs.next())
                    param_place = " = " + uprs.getInt("index");
                
            }
            else {
                param_place = " > -1";
            }
            
            //there are too many search...... types
            String search = " db.index > -1 ";
            
            //if empty why we're here????
            if(category==null&&name==null&&itemcode==null&&buyingless==null&&buyingmore==null&&
                    saleless==null&&salemore==null&&quantityless==null&&quantitymore==null&&
                    producer==null&&suplier==null) {
                return;
            }
        
            //clear the table
            ClearAllTable(e);
            
            //customize string search
            if(name!=null) {
                name = " db.nama LIKE '%" + name + "%' "; 
                search = search + " AND " + name;
            }
            if(category!=null) {
                category = " kategori LIKE '%" + category + "%' ";
                search = search + " AND " + category;
            }
            if(itemcode!=null) {
                itemcode = " kd_barang LIKE '%" + itemcode + "%' ";
                search = search + " AND " + itemcode;
            }
            if(buyingless!=null) {
                buyingless = " harga_modal < " + buyingless + " ";
                search = search + " AND " + buyingless;
            }
            if(buyingmore!=null) {
                buyingmore = " harga_modal > " + buyingmore + " ";
                search = search + " AND " + buyingmore;
            }
            if(saleless!=null) {
                saleless = " harga_jual < " + saleless + " ";
                search = search + " AND " + saleless;
            }
            if(salemore!=null) {
                salemore = " harga_jual > " + salemore + " ";
                search = search + " AND " + salemore;
            }
            if(producer!=null) {
                producer = " dp.nama LIKE '%" + producer + "%' ";
                search = search + " AND " + producer;
            }
            
            uprs = stmt.executeQuery("select db.*, dp.nama as nama_produsen, sum(jumlah) " +
                    "as jumlah_barang from data_barang as db inner join data_produsen as dp " +
                    "on dp.index = db.index_produsen left join (select * from stok_gudang " +
                    "where index_gudang " + param_place + " ) sg on sg.index_barang = " +
                    "db.index WHERE " + search + " group by db.index, db.nama, " +
                    "db.harga_modal, db.harga_jual, db.kd_barang, db.komentar, db.kategori, " +
                    "db.index_produsen, db.panjang, db.lebar, db.tinggi, db.volume, " +
                    "db.jumlah_ukuran, db.ukuran_primer, db.ukuran_sekunder, " +
                    "db.ukuran_ketiga, db.jumlah_ukuran, db.jumlah_ukuran_dua_ke_tiga, dp.nama");
            
           
            int i = 0;
            
            String itemname = "";
            SellerDB sellerdb = new SellerDB( conn );
            
            boolean uprs2_null = true;
            //fill the jtable
            while(uprs.next()) {
                
                int item_amount_got = uprs.getInt("jumlah_barang");
                int item_amount_more_asked = -1;
                if(quantitymore!=null)
                    item_amount_more_asked = Integer.parseInt(quantitymore);
                int item_amount_less_asked = Integer.MAX_VALUE;
                if(quantityless!=null)
                    item_amount_less_asked = Integer.parseInt(quantityless);
                
                if(item_amount_got < item_amount_less_asked && 
                        item_amount_got > item_amount_more_asked) {

                    //if jtable is full, add empty row
                    if(i==e.getRowCount()) {
                        DefaultTableModel model = (DefaultTableModel)e.getModel();
                        model.addRow( new Object[] { null, null, null, null, null, null, null,
                            null, null, null, null, null } );
                    }
                    
                    uprs2_null = false;
                    //get the suplier
                    uprs2 = stmt2.executeQuery("select nama_suplier from suplier_barang " +
                            "inner join data_suplier on kd_suplier = index_suplier where " +
                            "index_barang = " + uprs.getInt("index"));
                    
                    String suplier_for_item = "";
                    if(uprs2.next())
                        suplier_for_item = firstLetterCaps(uprs2.getString("nama_suplier"));
                    while(uprs2.next()) {
                        suplier_for_item += ", " + firstLetterCaps(uprs2.getString("nama_suplier"));
                    }
                    if((suplier==null) || (suplier!=null && 
                            suplier_for_item.toLowerCase().indexOf(suplier.toLowerCase())!=-1)) {
                    
                        itemname = uprs.getString("nama");
                        e.getModel().setValueAt( uprs.getString("kd_barang"), i, 0 );
                        e.getModel().setValueAt( uprs.getString("kategori"), i, 1 );
                        e.getModel().setValueAt( firstLetterCaps(itemname), i, 2 );
                        e.getModel().setValueAt( nf.format(uprs.getInt("harga_modal")), i, 3 );
                        e.getModel().setValueAt( nf.format(uprs.getInt("harga_jual")), i, 4 );
                        e.getModel().setValueAt( uprs.getInt("jumlah_barang"), i, 5 );
                        e.getModel().setValueAt( uprs.getString("komentar"), i, 6 );
                        e.getModel().setValueAt( uprs.getInt("index"), i, 7 );
                        e.getModel().setValueAt( uprs.getInt("panjang"), i, 8 );
                        e.getModel().setValueAt( uprs.getInt("lebar"), i, 9 );
                        e.getModel().setValueAt( uprs.getInt("tinggi"), i, 10 );
                        e.getModel().setValueAt( uprs.getInt("volume"), i, 11 );
                        e.getModel().setValueAt( uprs.getString("ukuran_primer"), i, 12 );
                        e.getModel().setValueAt( uprs.getInt("jumlah_ukuran"), i, 13 );
                        e.getModel().setValueAt( uprs.getString("ukuran_sekunder"), i, 14 );
                        e.getModel().setValueAt( firstLetterCaps(uprs.getString("nama_produsen")), i, 15 );
                        e.getModel().setValueAt( uprs.getInt("jumlah_ukuran_dua_ke_tiga"), i, 17 );
                        e.getModel().setValueAt( uprs.getString("ukuran_ketiga"), i, 18 );
                        e.getModel().setValueAt( suplier_for_item, i, 16 );

                        i++;
                    }
                }

            }
            
            if(!uprs2_null) {
                uprs2.close();
                stmt2.close();
            }
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
    }
    
    /*
     * initialize combobox for salesman and customer
     * @param combo jcombobox to be initialized
     * 
     */
    public void initializeComboBox( JComboBox combo, String name ) {
        try {
            
            stmt = conn.createStatement();
            
            //use the list
            List<String> list = new ArrayList<String>();
            
            if(name.equals("customer")) {
                uprs = stmt.executeQuery( "SELECT nama FROM data_pembeli " );
            
                //add customer name to combobox
                while(uprs.next()) {
                    list.add( firstLetterCaps(uprs.getString("nama")) );
                }
            }
            else if(name.equals("warehouse_only")) {
                uprs = stmt.executeQuery("SELECT nama FROM " +
                        "data_gudang ORDER BY nama");
                
                //add warehouse's name to combobox
                while(uprs.next()) {
                    list.add( firstLetterCaps(uprs.getString("nama")) );
                }
            }
            else if(name.equals("warehouse")){
                uprs = stmt.executeQuery( "SELECT nama FROM " +
                        "data_gudang ORDER BY nama" );
            
                //add warehouse's name to combobox
                while(uprs.next()) {
                    list.add( firstLetterCaps(uprs.getString("nama")) );
                }
                list.add( "Total" );
            }
            else if(name.equals("salesman")) {
                uprs = stmt.executeQuery( "SELECT nomor_induk, nama_salesman FROM " +
                        "data_salesman WHERE status = true" );
                
                //add salesman name to combobox
                while(uprs.next()) {
                    list.add( "( " + uprs.getString("nomor_induk") + " ) " + 
                            firstLetterCaps(uprs.getString("nama_salesman")) );
                }
            }
            else if(name.equals("supply")){
                uprs = stmt.executeQuery( "SELECT nama FROM " +
                        "data_gudang ORDER BY nama" );
            
                //add warehouse's name to combobox
                while(uprs.next()) {
                    list.add( firstLetterCaps(uprs.getString("nama")) );
                }
                list.add( "Custom" );
            }
            else if(name.equals("commisioner")) {
                uprs = stmt.executeQuery( "SELECT nama_komisioner FROM " +
                        "data_komisioner" );
            
                //add warehouse's name to combobox
                while(uprs.next()) {
                    list.add( firstLetterCaps(uprs.getString("nama_komisioner")) );
                }
            }
            else if(name.equals("container")) {
                uprs = stmt.executeQuery( "SELECT nama, volume FROM data_kontainer" );
                
                //add container's name to combobox
                char cubic = '\u00b3';
                while(uprs.next()) {
                    list.add( firstLetterCaps(uprs.getString("nama")) + " / " +
                            uprs.getInt("volume") + " m" + cubic );
                }
            }
            else if(name.equals("items")) {
                uprs = stmt.executeQuery( "SELECT DISTINCT nama FROM data_barang" );
                
                while(uprs.next()) {
                    list.add( firstLetterCaps(uprs.getString("nama")) );
                }
            }
            else if(name.equals("suplier")) {
                uprs = stmt.executeQuery( "SELECT nama_suplier FROM data_suplier" );
                
                while(uprs.next()) {
                    list.add( firstLetterCaps(uprs.getString("nama_suplier")) );
                }
            }
            
            combo.setModel( 
                    new javax.swing.DefaultComboBoxModel( list.toArray() ) );
            
            //close the resource
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
            new WriteLogFile(e).writeLogFile();
        }
    }
    
    public void initializeQuantityExpiredDatePanel(String warehouse_name, JLabel total_label, 
            DefaultTableModel quantity_expired_date_tm) {
        
    }
        
    public static void ClearAllTable( javax.swing.JTable table ) {
        //first clear the table
        while(table.getRowCount()>0)
            ((DefaultTableModel)table.getModel()).removeRow(0);
        //then make 4 new empty row
        for(int i=0; i<4; i++)
            ((DefaultTableModel)table.getModel()).addRow(
                    new Object[] { null, null, null, null, null, 
                            null, null, null, null, null } );
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
