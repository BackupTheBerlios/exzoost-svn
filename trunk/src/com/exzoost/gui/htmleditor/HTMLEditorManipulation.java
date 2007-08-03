/*
 * HTMLEditorManipulation.java
 *
 * Created on December 11, 2004, 4:07 AM
 */

package com.exzoost.gui.htmleditor;

import com.exzoost.database.CommisionerDB;
import com.exzoost.database.CustomerDB;
import com.exzoost.database.SalesmanDB;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLDocument;
import java.sql.Date;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import com.exzoost.database.Transaction;
/**
 *
 * @author knight
 */
public class HTMLEditorManipulation {
    private HTMLEditorKit editorkit;
    private HTMLDocument doc;
    private JEditorPane transactionsEP;
    private Connection conn;
    private CustomerDB customerdb;
    private SalesmanDB salesmandb;
    private ResultSet uprs;
    private Statement stmt;
    private CommisionerDB commisionerdb;
    private NumberFormat nf;
    private DateFormat df;
    /** Creates a new instance of HTMLEditorManipulation */
    public HTMLEditorManipulation( JEditorPane transactionsEP, Connection conn ) {
        editorkit = (HTMLEditorKit)transactionsEP.getEditorKit();
        doc = (HTMLDocument)transactionsEP.getDocument();
        
        //the editor pane to be manipulated
        this.transactionsEP = transactionsEP;
        
        //reference to the connection
        this.conn = conn;
        
        //commisioner database class
        commisionerdb = new CommisionerDB( conn );
        customerdb = new CustomerDB( conn );
        salesmandb = new SalesmanDB( conn );
        
        //number formatting tool
        nf = NumberFormat.getCurrencyInstance( new Locale("id","id"));
        
        //parse beautiful string to date
        df = DateFormat.getDateInstance( DateFormat.LONG, Locale.ENGLISH );
    }
    
    public void HTMLWrite( ) {
        try {
            //create the file chooser
            final JFileChooser fc = new JFileChooser();
            
            //in response to button click
            int returnVal = fc.showSaveDialog(null);
            
            //if user want to save the transaction file
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                
                //get the file
                File getFile = fc.getSelectedFile();		
                
                OutputStream out = new FileOutputStream(getFile);
                
                //write the file
                editorkit.write(out, doc, 0, doc.getLength() );
            }
        }
        catch( javax.swing.text.BadLocationException e ) {
            e.printStackTrace();
        }
        catch( java.io.IOException e ) {
            e.printStackTrace();
        }
    }
            
    public void HTMLIncomeDialog( String invoice ) {
        
        String insert = "";
        String invoicest = "'" + invoice + "'";
        
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT tanggal, " +
                    "deskripsi, jumlah FROM transaksi_pemasukan_lain " +
                    "WHERE invoice = " + invoicest );
            
            uprs.next();
            
            Date datest = uprs.getDate("tanggal");
            int amount = uprs.getInt("jumlah");
            String description = "";
            if(uprs.getString("deskripsi")==null)
                description = "-";
            else 
                description = uprs.getString("deskripsi");
            
            insert = "<html><font face='arial'><u>Income Transaction</u><br>" +
                     "Invoice : <b>" + invoice + "</b>" +
                     "Date : <b>" + df.format(datest) + "</b>" + 
                     "<font face='arial'>Amount of Transaction : <b>" + nf.format(amount) + "</b><br>" +
                     "Description : <b>" + description + "</b></font><hr><br></html>";
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
        }
        
        try {
            editorkit.insertHTML( doc, doc.getLength(), insert, 0, 0, null );
        }
        catch( javax.swing.text.BadLocationException e ) {
            e.printStackTrace();
        }
        catch( java.io.IOException e ) {
            e.printStackTrace();
        }
    }
    
    public void HTMLOutcomeDialog( String invoice ) {
        
        String insert = "";
        String invoicest = "'" + invoice + "'";
        
        try {
            stmt = conn.createStatement();
            
            uprs = stmt.executeQuery( "SELECT nama, tanggal, " +
                    "komentar, jumlah FROM transaksi_pengeluaran " +
                    "WHERE invoice = " + invoicest );
            
            uprs.next();
            
            Date datest = uprs.getDate("tanggal");
            int amount = uprs.getInt("jumlah");
            String comment = "";
            if(uprs.getString("komentar")!=null)
                comment = uprs.getString("komentar");
            else
                comment = "-";
            String type = "";
            type = uprs.getString("nama");
            if(type.equals("Listrik"))
                type = "Electric Payment";
            else if(type.equals("Air"))
                type = "Water Payment";
            else if(type.equals("Telepon"))
                type = "Phone Payment";
            else if(type.equals("Lain-lain"))
                type = "Other Payment";

            insert = "<html><font face='arial'><u>Outcome Payment Transaction</u><br>" + 
                 "Invoice : <b>" + invoice + "</b>" +
                 "Date : <b>" + df.format(datest) + "</b>" + 
                 "Payment : <b>" + nf.format(amount) + "</b><br>" +
                 "Type : <b>" + type + "</b><br>" +
                 "Comment : <b>" + comment + "</b></font><hr><br></html>";

            
            uprs.close();
            stmt.close();
        }
        catch( SQLException e ) {
            e.printStackTrace();
        }
        
        try {
            editorkit.insertHTML( doc, doc.getLength(), insert, 0, 0, null );
        }
        catch( javax.swing.text.BadLocationException e ) {
            e.printStackTrace();
        }
        catch( java.io.IOException e ) {
            e.printStackTrace();
        }
    }
    
    public void HTMLSalaryDialog( String invoice ) {
        
        int index = 0;
        String insert = "";
        String comment = "";
        String invoicest = "'" + invoice + "'";
        String tableheader = "";
        String employeetableDetail = "";
        String salesmantableDetail = "";
        String bottom = "";
        
        try {
            stmt = conn.createStatement();
            
            //get data from the master of transaction
            uprs = stmt.executeQuery( "SELECT index, invoice_gaji, tanggal_gaji, komentar FROM " +
                    "data_transaksi_gaji WHERE invoice_gaji = " + invoicest  );
            
            uprs.next();
            
            //this is the index of salary transaction
            index = uprs.getInt("index");
            
            //comment
            if(uprs.getString("komentar")==null)
                comment = "-";
            else
                comment = uprs.getString("komentar");
            
            //date
            Date datest = uprs.getDate("tanggal_gaji");
            
            int totalSalaryPayment = 0;
            
            tableheader =
                    "<tr><td bgcolor='#ffffe6'><center><font face='arial'>Name</font></center></td>" +
                    "<td bgcolor='#ffffe6'><center><font face='arial'>Official Salary</font></center></td>" +
                    "<td bgcolor='#ffffe6'><center><font face='arial'>Salary Cut<font></center></td>" +
                    "<td bgcolor='#ffffe6'><center><font face='arial'>Total Salary<font></center></td></tr>";
            
            //get the list of employee salary transaction
            uprs = stmt.executeQuery( "SELECT t.potongan, k.nama_karyawan, t.gaji FROM " +
                    "( SELECT * FROM transaksi_gaji WHERE index_transaksi_gaji = " + index + 
                    " AND tipe <> 'salesman' ) t INNER JOIN data_karyawan k ON( t.index_karyawan = " +
                    "k.index ) " );
                                    
            while(uprs.next()) {
                String name = uprs.getString("nama_karyawan");
                int salary = uprs.getInt("gaji");
                int cut = uprs.getInt("potongan");
                int total_salary = salary - cut; 
                employeetableDetail += "<tr><td align='right'><font face='arial'>" + 
                        firstLetterCaps(name) + "</font></td>" +
                        "<td align='right'><font face='arial'>" + nf.format(salary) + "</font></td>" +
                        "<td align='right'><font face='arial'>" + nf.format(cut) + "</font></td>" +
                        "<td align='right'><font face='arial'>" + nf.format(total_salary) + "</font></td></tr>"; 
                totalSalaryPayment += (total_salary-cut);
            }
            
            
            //get the list of salesman salary transaction
            uprs = stmt.executeQuery( "SELECT t.potongan, k.nama_salesman, t.gaji FROM " +
                    "( SELECT * FROM transaksi_gaji_salesman WHERE index_transaksi_gaji = " + 
                    index + " ) t INNER JOIN data_salesman k " +
                    "ON( t.index_salesman = k.index_salesman ) " );
            
            while(uprs.next()) {
                String name = uprs.getString("nama_salesman");
                int salary = uprs.getInt("gaji");
                int cut = uprs.getInt("potongan");
                int total_salary = salary - cut; 
                salesmantableDetail += "<tr><td align='right'><font face='arial'>" + 
                        firstLetterCaps(name) + "</font></td>" +
                        "<td align='right'><font face='arial'>" + nf.format(salary) + "</font></td>" +
                        "<td align='right'><font face='arial'>" + nf.format(cut) + "</font></td>" +
                        "<td align='right'><font face='arial'>" + nf.format(total_salary) + "</font></td></tr>"; 
                totalSalaryPayment += (total_salary-cut);
            }
            
            bottom = "<div align='right'>Total Salary Payment : <b>" + 
                     nf.format(totalSalaryPayment) + "</b><br></font></div><hr><br></html>";
            
            insert = "<html><font face='arial'><u>Salary Payment Transaction</u><br>" + 
                     "Invoice : <b>" + invoice + "</b><br>" +
                     "Date : <b>" + df.format(datest) + "</b><br>" +
                     "Comment : <b>" + comment + "</b><br>" + 
                     "<b>Employee : </b><br>" +
                     "<table border='1' width='100%'>" +
                     tableheader +
                     employeetableDetail +
                     "</table>" +
                     "<b>Salesman : </b><br>" +
                     "<table border='1' width='100%'>" +
                     tableheader +
                     salesmantableDetail +
                     "</table>" +
                     bottom;
            
            uprs.close();
            stmt.close();
            
        }
        catch( SQLException e ) {
            e.printStackTrace();
        }
        
        try {
            editorkit.insertHTML( doc, doc.getLength(), insert, 0, 0, null );
        }
        catch( javax.swing.text.BadLocationException e ) {
            e.printStackTrace();
        }
        catch( java.io.IOException e ) {
            e.printStackTrace();
        }
    }
    
    public void HTMLBuyTransactionDialog( String invoice ) {
        
        String insert = "";
        String datest = "";
        String commisionerst = "";
        String tableheader = "";
        String discounttransactionst = "";
        int discounttransaction = 0;
        String comment = "";
        String sender = "";
        String invoicest = "";
        String suplier = "";
        int shipmentcost = 0;
        int commision = 0;
        
        //save the original state of invoice
        invoicest = invoice;
        
        //we search the transaction from the invoice
        invoice = "'" + invoice + "'";
        
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
        
            //get the info of the master of the purchase transaction
            uprs = stmt.executeQuery("SELECT potongan, tanggal, suplier, " +
                    "komentar, kd_komisioner, komisi_komisioner, pengirim, index_kontainer " +
                    "FROM data_transaksi_pembelian " +
                    "WHERE invoice = " + invoice );
            
            uprs.next();
                        
            //sender label
            if(uprs.getString("pengirim")==null)
                sender = "-";
            else
                sender = firstLetterCaps( uprs.getString("pengirim") );
            
            //suplier label
            if(uprs.getString("suplier")==null)
                suplier = "-";
            else
                suplier = firstLetterCaps( uprs.getString("suplier") );
            
            //commision of commisioner
            commision = uprs.getInt("komisi_komisioner");
            
            //date label
            datest = df.format( uprs.getDate( "tanggal" ) );
            
            //comment label
            if(uprs.getString("komentar")==null)
                comment = "-";
            else
                comment = uprs.getString("komentar");
            
            //commisioner label
            if(uprs.getString("kd_komisioner")!=null)
                commisionerst = firstLetterCaps( 
                        commisionerdb.getName( uprs.getInt( "kd_komisioner" ) ) );
            
            //discount transaction label
            discounttransaction = uprs.getInt( "potongan" );
            discounttransactionst = nf.format( discounttransaction );
            
            //get container price
            int container_index = uprs.getInt("index_kontainer");
            double container_price = -1;
            
            if(!sender.equals("-")) {
                uprs = stmt.executeQuery("select harga from data_kontainer where " +
                        "index_kontainer = " + container_index);
            
                if(uprs.next())
                    container_price = uprs.getDouble("harga");
            }
            
            //get the table detail ( list of items that bought )
            uprs = stmt.executeQuery( "SELECT index_barang, kd_barang, nama, harga_beli, " +
                    "diskon_per_barang, volume, jumlah FROM " +
                    "transaksi_barang_pembelian t inner join data_barang d on d.index = " +
                    " t.index_barang " +
                    "WHERE index_pembelian = ( SELECT index FROM data_transaksi_pembelian " +
                    "WHERE invoice = " + invoice + " ) " );
            
            //table header label
            tableheader = 
                "<tr><td bgcolor='#ffffe6'><center><font face='arial'>Name</font></center></td>" +
                "<td bgcolor='#ffffe6'><center><font face='arial'>Purchase Price</font></center></td>" +
                "<td bgcolor='#ffffe6'><center><font face='arial'>Quantity</font></center></td>" +
                "<td bgcolor='#ffffe6'><center><font face='arial'>Discount</font></center></td>" +
                "<td bgcolor='#ffffe6'><center><font face='arial'>Sending Price</font></center></td>" +
                "<td bgcolor='#ffffe6'><center><font face='arial'>Total Price</font></center></td></tr>";
            
            String tableDetail = "";
            int totalTransactionPrice = 0;
            
            //how do we get the sending price
            //assume this is the purchase transaction:
            //item        quantity         volume         total_volume(quantity * volume)
            //sugus         5                100                 500
            //durex         10                60                 600
            //the price of container is 100000
            //the sending price of item is total_volume of item per total_volume all item * price of container.
            //After that, divide it by quantity of item
            //so sending price of sugus is ( ( 500 / ( 500 + 600 ) )* 1000000 ) / 5
            
            //looping for finding sending price
            LinkedList<Double> quantity_list = new LinkedList<Double>();
            LinkedList<Double> total_volume_list = new LinkedList<Double>();
            double big_total_volume = 0;
            double temp_total_volume = 0;
            if(!sender.equals("-")) {
                while(uprs.next()) {
                    quantity_list.add(uprs.getDouble("jumlah"));
                    temp_total_volume = uprs.getInt("volume") * uprs.getInt("jumlah");
                    total_volume_list.add(temp_total_volume);
                    big_total_volume += temp_total_volume;
                }
                
                uprs.beforeFirst();
            }
            
            //looping for generating table
            while(uprs.next()) {
                String name = uprs.getString("nama");
                int price = uprs.getInt("harga_beli");
                int unit = uprs.getInt("jumlah");
                int discount = uprs.getInt("diskon_per_barang");
                double sending_price = 0;
                if(!sender.equals("-")) {
                    sending_price = ( ( total_volume_list.poll() / big_total_volume ) 
                                          * container_price ) / quantity_list.poll();
                }
                int total_price = unit * ( price - discount );
                tableDetail += "<tr><td align='right'><font face='arial'>" + 
                        firstLetterCaps(name) + "</font></td>" +
                        "<td align='right'><font face='arial'>" + nf.format(price) + "</font></td>" +
                        "<td align='right'><font face='arial'>" + unit + "</font></td>" + 
                        "<td align='right'><font face='arial'>" + nf.format(discount) + "</font></td>" +
                        "<td align='right'><font face='arial'>" + nf.format(sending_price) + "</font></td>" +
                        "<td align='right'><font face='arial'>" + nf.format(total_price) + "</font></td></tr>"; 
                totalTransactionPrice += total_price;
            }
            
            //minus with transaction discount
            totalTransactionPrice -= discounttransaction;
            
            String bottom = "<div align='right'><font face='arial'>Transaction Discount : <b>" + 
                    discounttransactionst + "</b><br>" +
                    "Total Payment : <b>" + nf.format(totalTransactionPrice) + "</b><br></font></div><hr></html>";
               
            insert = "<html><font face='arial'><u>Purchase Transaction</u><br>" +
                     "Invoice : <b>" + invoicest + "</b><br>" +
                     "Date : <b>" + datest + "</b><br>" +  
                     "Commisioner : <b>" + commisionerst + "</b><br>" +
                     "Commisioner Commision : <b>" + nf.format(commision) + "</b><br>" +
                     "Sender : <b>" + sender + "</b><br>";
            
           if(!sender.equals("-")) {
               insert += "Sending Price : <b>" + nf.format(container_price) + "</b><br>";
           }
            
           insert += "Suplier : <b>" + suplier + "</b><br>" +
                     "Comment : <b>" + comment + "</b><br>" +
                     "<table border='1' width='100%'>" +
                     tableheader +
                     tableDetail +
                     "</table>" +
                     bottom;
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
        }
        
        try {
            editorkit.insertHTML( doc, doc.getLength(), insert, 0, 0, null );
        }
        catch( javax.swing.text.BadLocationException e ) {
            e.printStackTrace();
        }
        catch( java.io.IOException e ) {
            e.printStackTrace();
        }
    }
    
    public void HTMLSaleTransactionDialog( String invoice ) {
        
        String insert = "";
        String invoicest = "";
        String datest = "";
        String customerst = "";
        String salesmanst = "";
        String tableheader = "";
        int discounttransaction = 0;
        String discounttransactionst = "";
        int shipmentcost = 0;
        int commision = 0;
        String comment = "";
        String sender = "";
        String senderpaid = "";
        
        //save the original state of invoice
        invoicest = invoice;
        
        //we search the transaction from the invoice
        invoice = "'" + invoice + "'";
        
        try {
            stmt = conn.createStatement();
                        
            //get the info of the master of the sale transaction
            uprs = stmt.executeQuery("SELECT potongan, tanggal, nomor_pembeli, " +
                    "komentar, nomor_salesman, komisi_salesman, pengirim, biaya_kirim, " +
                    "tertanggung_biaya_kirim FROM data_transaksi_penjualan " +
                    "WHERE invoice = " + invoice );
            
            uprs.next();
                                    
            //sending price paid by....
            if(uprs.getString("tertanggung_biaya_kirim")==null)
                senderpaid = "-";
            else if( uprs.getString("tertanggung_biaya_kirim").equals("penjual") )
                senderpaid = "Company";
            else
                senderpaid = "Customer";
            
            //commision of salesman
            commision = uprs.getInt("komisi_salesman");
            
            //shipment cost
            shipmentcost = uprs.getInt("biaya_kirim");
            
            //sender label
            if(uprs.getString("pengirim")==null)
                sender = "-";
            else
                sender = firstLetterCaps( uprs.getString("pengirim") );
            
            //comment label
            if(uprs.getString("komentar")==null)
                comment = "-";
            else
                comment = uprs.getString("komentar");
            
            //date label
            datest = df.format( uprs.getDate( "tanggal" ) );
            
            //customer label
            customerst = firstLetterCaps( customerdb.getName( uprs.getInt( "nomor_pembeli" ) ) );
            
            //salesman label
            salesmanst = firstLetterCaps( salesmandb.getName( uprs.getInt("nomor_salesman") ) );
            
            //discount transaction label
            discounttransaction = uprs.getInt( "potongan" );
            discounttransactionst = nf.format( discounttransaction );
            
            //get the table detail ( list of items that sold )
            uprs = stmt.executeQuery( "SELECT d.nama, t.harga_jual, t.diskon_per_barang, t.harga_modal, " +
                    "t.jumlah, ((t.harga_jual - t.diskon_per_barang) * t.jumlah) as sum_trans " +
                    "FROM transaksi_barang_penjualan t inner join data_barang d " +
                    "on index_barang = index " +
                    "WHERE index_penjualan = ( SELECT index FROM data_transaksi_penjualan " +
                    "WHERE invoice = " + invoice + " ) " );
            
            //table header label
            tableheader = 
                "<tr><td bgcolor='#ffffe6'><center><font face='arial'>Name</font></center></td>" +
                "<td bgcolor='#ffffe6'><center><font face='arial'>Selling Price</font></center></td>" +
                "<td bgcolor='#ffffe6'><center><font face='arial'>Quantity<font></center></td>" +
                "<td bgcolor='#ffffe6'><center><font face='arial'>Discount<font></center></td>" +
                "<td bgcolor='#ffffe6'><center><font face='arial'>Total Price<font></center></td></tr>";
            
            String tableDetail = "";
            int totalTransactionPrice = 0;
            
            while(uprs.next()) {
                String name = uprs.getString("nama");
                int price = uprs.getInt("harga_jual");
                int unit = uprs.getInt("jumlah");
                int discount = uprs.getInt("diskon_per_barang");
                int total_price = uprs.getInt("sum_trans");
                tableDetail += "<tr><td align='right'><font face='arial'>" + 
                        firstLetterCaps(name) + "</font></td>" +
                        "<td align='right'><font face='arial'>" + nf.format(price) + "</font></td>" +
                        "<td align='right'><font face='arial'>" + unit + "</font></td>" + 
                        "<td align='right'><font face='arial'>" + nf.format(discount) + "</font></td>" +
                        "<td align='right'><font face='arial'>" + nf.format(total_price) + "</font></td></tr>"; 
                totalTransactionPrice += total_price;
            }
            
            //minus with transaction discount
            totalTransactionPrice -= discounttransaction;
            
            String bottom = "<div align='right'><font face='arial'>Transaction Discount : <b>" + 
                    discounttransactionst + "</b><br>" + "Total Payment : <b>" + 
                    nf.format(totalTransactionPrice) + "</b><br></font></div><hr><br></html>";
            
            insert = "<html><font face='arial'><u>Sale Transaction</u><br>" +
                     "Invoice : <b>" + invoicest + "</b><br>" +
                     "Date : <b>" + datest + "</b><br>" +  
                     "Customer : <b>" + customerst + "</b><br>" +
                     "Salesman : <b>" + salesmanst + "</b><br>" +
                     "Salesman Commision : <b>" + nf.format(commision) + "</b><br>" +
                     "Sender : <b>" + sender + "</b><br>" +
                     "Sending Price : <b>" + nf.format(shipmentcost) + "</b>" +
                     " Paid by : <b>" + senderpaid + "</b><br>" + 
                     "Comment : <b>" + comment + "</b><br>" + 
                     "<table border='1' width='100%'>" +
                     tableheader +
                     tableDetail +
                     "</table>" +
                     bottom;
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
        }
        
        try {
            editorkit.insertHTML( doc, doc.getLength(), insert, 0, 0, null );
        }
        catch( javax.swing.text.BadLocationException e ) {
            e.printStackTrace();
        }
        catch( java.io.IOException e ) {
            e.printStackTrace();
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
    
    public static String nonBreakingSpace( int count ) {
        String nbsp = "";
        
        for( int i=0; i<count; i++) {
            nbsp += "&nbsp";
        }
        
        return nbsp;
                
    }
}
