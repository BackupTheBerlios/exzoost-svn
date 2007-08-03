/*
 * ReportManipulation.java
 *
 * Created on December 16, 2004, 3:13 AM
 */

package com.exzoost.gui.reportview;

import com.exzoost.database.CustomerDB;
import com.exzoost.database.ReportDB;
import com.exzoost.database.SalesmanDB;
import com.exzoost.database.WriteLogFile;
import java.awt.Color;
import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineRenderer3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Day;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.plot.PlotOrientation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.exzoost.database.Transaction;
import org.jfree.ui.TextAnchor;
/**
 *
 * @author knight
 */
public class ReportManipulation {
    private Connection conn;
    private Transaction tr;
    private Statement stmt;
    private ResultSet uprs;
    private NumberFormat nf;
    private DateFormat df;
    /** Creates a new instance of ReportManipulation */
    public ReportManipulation( Connection conn ) {
        this.conn = conn;
        
        //format the number
        nf = NumberFormat.getCurrencyInstance( new Locale("id", "id") );
        df = DateFormat.getDateInstance( DateFormat.LONG, Locale.ENGLISH );
    }
    
    static String[] month = { "", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December" };
    
    public String EditorPaneGeneralReport( Date datefrom, Date dateto ) {
                
        String insert = "";
        
        //must use reportdb class
        ReportDB reportdb = new ReportDB( conn );
        
        try {
            stmt = conn.createStatement();
            
            insert += "<html>";
            insert += "<h2>General Report for date between " + df.format(datefrom) + " and " +
                    df.format(dateto) + "</h2><p>"; 
                                    
            //sale 
            insert += "<b>Total Value of Sale Transaction : " +
                    nf.format( reportdb.TotalSaleValue( datefrom.toString(), dateto.toString() ) )
                    + "</b><p>";
            
            //purchase
            insert += "<b>Total Value of Purchase Transaction : " +
                    nf.format( reportdb.TotalPurchaseValue( datefrom.toString(), dateto.toString() ) )
                    + "</b><p>";
            
            //salesman commision
            insert += "<b>Total Value of Salesman Commision Transaction : " +
                    nf.format( reportdb.TotalSalesmanCommisionValue( datefrom.toString(), 
                    dateto.toString() ) ) + "</b><p>";
            
            //salary payment
            insert += "<b>Total Value of Salary Payment Transaction : " + 
                    nf.format( reportdb.TotalSalaryPaymentValue( datefrom.toString(), 
                    dateto.toString() ) )+ "</b><p>";    
            
            //charges payment
            insert += "<b>Total Outcome Transaction : " +
                    nf.format( reportdb.TotalOutcomeValue( datefrom.toString(),
                    dateto.toString() ) ) + "</b><p>";
                                    
            insert += "</html>";
            
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
        return insert;
    }
            
    public void SaveDialog( JFreeChart chart ) {
        
        //create the file chooser
        final JFileChooser fc = new JFileChooser();
        
        //Add a custom file filter and disable the default
        //(Accept All) file filter.
        fc.addChoosableFileFilter(new ReportFilter());
        fc.setAcceptAllFileFilterUsed(false);
        
        //in response to button click
        int returnVal = fc.showSaveDialog(null);
        
        try {
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File getFile = fc.getSelectedFile();
                
                String path = getFile.getPath();
                
                //without extension
                if(path.indexOf(".png")==-1) {
                    path += ".png";
                }
                
                getFile = new File(path);
                
                ChartUtilities.saveChartAsPNG( getFile, chart, 800, 600 );
            }
        }
        catch( java.io.IOException e ) {
            e.printStackTrace();
        }
    }
    
    public ChartPanel SaleChartReport( String datefrom, String dateto, 
            int id, String typeChart, String typeReport ) {
        
        JFreeChart freechart = null;
        SalesmanDB salesman;
        CustomerDB customer;
        
        //format the date
        String datefromst = "'" + datefrom + "'";
        String datetost = "'" + dateto + "'";
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        //get the salesman name later
        salesman = new SalesmanDB( conn );
        
        //get the customer name later
        customer = new CustomerDB( conn );
        
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
                    ResultSet.CONCUR_UPDATABLE);
            
            //salesman
            if(typeReport.equals("salesman")) {
                
                uprs = stmt.executeQuery( "select sum((harga_jual - harga_modal) * jumlah) as " +
                        "total, extract(month from tanggal::timestamp) as month, " +
                        "extract(year from tanggal::timestamp) as year " +
                        "from transaksi_barang_penjualan t " +
                        "inner join data_transaksi_penjualan d " +
                        "on d.index = t.index_penjualan " +
                        "where tanggal between " + datefromst + " and " + datetost +
                        " and nomor_salesman = " + id +
                        "group by month, year order by year, month" );

                if(uprs.next()) {
                    uprs.beforeFirst();
                    nullStartDataChart(uprs, "Transaction Profit", dataset, datefrom );
                    uprs.beforeFirst();
                    fillDataChart(uprs, "Transaction Profit", dataset );
                    uprs.beforeFirst();
                    nullEndDataChart(uprs, "Transaction Profit", dataset, dateto );
                }
//                while(uprs.next()) {
//                    dataset.setValue( uprs.getInt("value"), "Transaction Profit", 
//                            new String( month[ uprs.getInt("month") ] + " " + uprs.getString("year") ) );    
//                }
                
                uprs = stmt.executeQuery( "select sum(harga_jual * jumlah) as total, " +
                        "extract(month from tanggal::timestamp) as month, " +
                        "extract(year from tanggal::timestamp) as year " +
                        "from transaksi_barang_penjualan t " +
                        "inner join data_transaksi_penjualan d " +
                        "on d.index = t.index_penjualan " +
                        "where tanggal between " + datefromst + " and " + datetost +
                        " and nomor_salesman = " + id +
                        "group by month, year order by year, month" );
 
                if(uprs.next()) {
                    uprs.beforeFirst();
                    nullStartDataChart(uprs, "Transaction Value", dataset, datefrom );
                    uprs.beforeFirst();
                    fillDataChart(uprs, "Transaction Value", dataset );
                    uprs.beforeFirst();
                    nullEndDataChart(uprs, "Transaction Value", dataset, dateto );
                }
//                while(uprs.next()) {
//                    dataset.setValue( uprs.getInt("value"), "Transaction Value",
//                            new String( month[ uprs.getInt("month") ] + " " + uprs.getString("year") ) );
//                }
            }
            //customer
            else if(typeReport.equals("customer")){
                
                uprs = stmt.executeQuery("select sum(harga_jual * jumlah) as total, " +
                        "extract(month from tanggal::timestamp) as month, " +
                        "extract(year from tanggal::timestamp) as year " +
                        "from transaksi_barang_penjualan t " +
                        "inner join data_transaksi_penjualan d " +
                        "on d.index = t.index_penjualan " +
                        "where tanggal between " + datefromst + " and " + datetost +
                        " and nomor_pembeli = " + id +
                        "group by month, year order by year, month");
                if(uprs.next()) {
                    uprs.beforeFirst();
                    nullStartDataChart(uprs, "Transaction Value", dataset, datefrom );
                    uprs.beforeFirst();
                    fillDataChart(uprs, "Transaction Value", dataset );
                    uprs.beforeFirst();
                    nullEndDataChart(uprs, "Transaction Value", dataset, dateto );
                }
//                while(uprs.next()) {
//                    dataset.setValue( uprs.getInt("value"), "Transaction Value", 
//                            new String( month[ uprs.getInt("month") ] + " " + uprs.getString("year") ) );    
//                }
                                
            }
            
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
        String name = "";
        //salesman
        if(typeReport.equals("salesman")) {
            name = firstLetterCaps(salesman.getName(id));
        }
        //customer
        else if(typeReport.equals("customer")) {
            name = firstLetterCaps(customer.getName(id));
        }
        
        if(typeChart.equals("3D BarChart")) {
            freechart = ChartFactory.createBarChart3D( "Progress of " + firstLetterCaps(typeReport) + 
                    " : " + name, "Month", "Achievement", dataset, PlotOrientation.VERTICAL, 
                    true, true, false );
        }
        else if(typeChart.equals("2D BarChart")) {
            freechart = ChartFactory.createBarChart( "Progress of " + firstLetterCaps(typeReport) + 
                    " : " + name, "Month", "Achievement", dataset, PlotOrientation.VERTICAL, 
                    true, true, false );
        }
        else if(typeChart.equals("3D LineChart")) {
            freechart = ChartFactory.createLineChart3D( "Progress of " + firstLetterCaps(typeReport) + 
                    " : " + name, "Month", "Achievement", dataset, PlotOrientation.VERTICAL, 
                    true, true, false );
        } 
        else if(typeChart.equals("2D LineChart")) {
            freechart = ChartFactory.createLineChart( "Progress of " + firstLetterCaps(typeReport) + 
                    " : " + name, "Month", "Achievement", dataset, PlotOrientation.VERTICAL, 
                    true, true, false );
        }
        
        //set background of the chart ( default is red )
        freechart.setBackgroundPaint( new Color(64, 173, 111) );
        
        //plot to "beautify" the chart
        CategoryPlot plot = freechart.getCategoryPlot();
        plot.setForegroundAlpha(1);

        //renderer of the freechart
        CategoryItemRenderer ren = plot.getRenderer();
        ren.setLabelGenerator(new StandardCategoryLabelGenerator()); 
        ren.setPositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
        ren.setItemLabelsVisible(true);
         
        if(typeChart.equals("3D BarChart")) {
            BarRenderer3D barren = (BarRenderer3D)plot.getRenderer();
            barren.setItemMargin(0); // Distance between each bar in a group.
            barren.setMaxBarWidth(1); // The max width of the bar 
            barren.setItemLabelAnchorOffset(8);
        }
        else if(typeChart.equals("2D BarChart")) {
            BarRenderer barren = (BarRenderer)plot.getRenderer();
            barren.setItemMargin(0); // Distance between each bar in a group.
            barren.setMaxBarWidth(1); // The max width of the bar 
        }
        
        ChartPanel chartpanel = new ChartPanel( freechart );
        
        return chartpanel;
    }
    
    public ChartPanel ProfitProgressChartReport( String datefrom, String dateto,
            String typeChart ) {
        
        JFreeChart freechart = null;
        
        ResultSet uprs2 = null;
        
        String label = "Progress of Profit";
        
        //Array of profit
        List<Integer> salearray = new ArrayList<Integer>();
        List<String> saletime = new ArrayList<String>();
        List<Integer> purchasearray = new ArrayList<Integer>();
        
        //format the date
        String datefromst = "'" + datefrom + "'";
        String datetost = "'" + dateto + "'";
                
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
                    ResultSet.CONCUR_UPDATABLE );
            
            //sale value
            
            uprs = stmt.executeQuery( "select sum(harga_jual * jumlah) as total, " +
                    "extract(month from tanggal::timestamp) as month, " +
                    "extract(year from tanggal::timestamp) as year " +
                    "from transaksi_barang_penjualan t " +
                    "inner join data_transaksi_penjualan d " +
                    "on d.index = t.index_penjualan " +
                    "where tanggal between " + datefromst + " and " + datetost +
                    " group by month, year order by year, month" );
                        
            //fill data into db
            if(uprs.next()) {
                uprs.beforeFirst();
                nullStartDataChart(uprs, salearray, saletime, datefrom );
                uprs.beforeFirst();
                fillDataChart(uprs, salearray, saletime );
                uprs.beforeFirst();
                nullEndDataChart(uprs, salearray, saletime, dateto );
            }
            
            //purchase value
            uprs = stmt.executeQuery( "select sum(harga_beli * jumlah) as total, " +
                    "extract(month from tanggal::timestamp) as month, " +
                    "extract(year from tanggal::timestamp) as year " +
                    "from transaksi_barang_pembelian t " +
                    "inner join data_transaksi_pembelian d " +
                    "on d.index = t.index_pembelian " +
                    "where tanggal between " + datefromst + " and " + datetost +
                    " group by month, year order by year, month" );
            
            //fill data into db
            if(uprs.next()) {
                uprs.beforeFirst();
                nullStartDataChart(uprs, purchasearray, null, datefrom );
                uprs.beforeFirst();
                fillDataChart(uprs, purchasearray, null );
                uprs.beforeFirst();
                nullEndDataChart(uprs, purchasearray, null, dateto );
            }
            
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
        
        for( int i=0; i<salearray.size(); i++ ) {
            dataset.setValue( (Integer)salearray.get(i) - (Integer)purchasearray.get(i), label,
                    (String)saletime.get(i) );
//            dataset.setValue( (Integer)purchasearray.get(i), label,
//                    (String)saletime.get(i) );
        }
        
        if(typeChart.equals("3D BarChart")) {
            freechart = ChartFactory.createBarChart3D( "Progress of Profit", "Month", "Total", 
                    dataset, PlotOrientation.VERTICAL, true, true, false );
        }
        else if(typeChart.equals("2D BarChart")) {
            freechart = ChartFactory.createBarChart( "Progress of Profit", "Month", "Total", 
                    dataset, PlotOrientation.VERTICAL, true, true, false );
        }
        else if(typeChart.equals("3D LineChart")) {
            freechart = ChartFactory.createLineChart3D( "Progress of Profit", "Month", "Total", 
                    dataset, PlotOrientation.VERTICAL, true, true, false );
        } 
        else if(typeChart.equals("2D LineChart")) {
            freechart = ChartFactory.createLineChart( "Progress of Profit", "Month", "Total", 
                    dataset, PlotOrientation.VERTICAL, true, true, false );
        }
        
        //plot to "beautify" the chart
        CategoryPlot plot = freechart.getCategoryPlot();
        plot.setForegroundAlpha(1);
        
        //renderer of the freechart
        CategoryItemRenderer ren = plot.getRenderer();
        ren.setLabelGenerator(new StandardCategoryLabelGenerator()); 
        ren.setPositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
        ren.setItemLabelsVisible(true);
         
        if(typeChart.equals("3D BarChart")) {
            BarRenderer3D barren = (BarRenderer3D)plot.getRenderer();
            barren.setItemMargin(0); // Distance between each bar in a group.
            barren.setMaxBarWidth(1); // The max width of the bar 
            barren.setItemLabelAnchorOffset(8);
        }
        else if(typeChart.equals("2D BarChart")) {
            BarRenderer barren = (BarRenderer)plot.getRenderer();
            barren.setItemMargin(0); // Distance between each bar in a group.
            barren.setMaxBarWidth(1); // The max width of the bar 
        }
        else if(typeChart.equals("3D LineChart")) {
            LineRenderer3D larren = (LineRenderer3D)plot.getRenderer();
            larren.setItemLabelAnchorOffset(8);
        }
        
        ChartPanel chartpanel = new ChartPanel( freechart );
        
        return chartpanel;
        
    }
            
    public ChartPanel ItemProgressChartReport( String datefrom, String dateto, 
            String name, String typeChart, String typeReport ) {
        
        JFreeChart freechart = null;
        
        //format the date
        String datefromst = "'" + datefrom + "'";
        String datetost = "'" + dateto + "'";
        
        String oriname = name;
        name = "'" + name.toLowerCase() + "'";
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
                    ResultSet.CONCUR_UPDATABLE );
            
            //progress of item in sale transaction
            if(typeReport.equals("sale")) {
                uprs = stmt.executeQuery( "SELECT sum(jumlah) AS total, nama, " +
                        "extract(month FROM tanggal::timestamp) AS month, " +
                        "extract(year FROM tanggal::timestamp) AS year " +
                        "FROM data_barang b, transaksi_barang_penjualan t, data_transaksi_penjualan d " +
                        "WHERE t.index_penjualan = d.index AND b.index = t.index_barang AND " +
                        "nama = " + name + " AND tanggal BETWEEN " + datefromst + " AND " + datetost +
                        " GROUP BY nama, month, year ORDER BY year, month" );
                
                //fill data into db
                if(uprs.next()) {
                    uprs.beforeFirst();
                    nullStartDataChart(uprs, "Sold Amount", dataset, datefrom );
                    uprs.beforeFirst();
                    fillDataChart(uprs, "Sold Amount", dataset );
                    uprs.beforeFirst();
                    nullEndDataChart(uprs, "Sold Amount", dataset, dateto );
                }
                
            }
            //progress of item in purchase transaction
            else if(typeReport.equals("buy")) {
                uprs = stmt.executeQuery( "SELECT sum(jumlah) AS total, nama, " +
                        "extract(month FROM tanggal::timestamp) AS month, " +
                        "extract(year FROM tanggal::timestamp) AS year " +
                        "FROM data_barang b, transaksi_barang_pembelian t, data_transaksi_pembelian d " +
                        "WHERE t.index_pembelian = d.index AND b.index = t.index_barang AND " +
                        "nama = " + name + " AND tanggal BETWEEN " + datefromst + " AND " + datetost +
                        "GROUP BY nama, month, year ORDER BY year, month" );
                
                //fill data into db
                if(uprs.next()) {
                    uprs.beforeFirst();
                    nullStartDataChart(uprs, "Purchased Amount", dataset, datefrom );
                    uprs.beforeFirst();
                    fillDataChart(uprs, "Purchased Amount", dataset );
                    uprs.beforeFirst();
                    nullEndDataChart(uprs, "Purchased Amount", dataset, dateto );
                }
            }
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
        if(typeReport.equals("sale")) {
            typeReport = "Sale";
        }
        else if(typeReport.equals("buy")) {
            typeReport = "Purchase";
        }
        
        if(typeChart.equals("3D BarChart")) {
            freechart = ChartFactory.createBarChart3D( "Progress of " + typeReport + 
                    " : " + oriname, "Month", "Total", dataset, PlotOrientation.VERTICAL, 
                    true, true, false );
        }
        else if(typeChart.equals("2D BarChart")) {
            freechart = ChartFactory.createBarChart( "Progress of " + typeReport + 
                    " : " + oriname, "Month", "Total", dataset, PlotOrientation.VERTICAL, 
                    true, true, false );
        }
        else if(typeChart.equals("3D LineChart")) {
            freechart = ChartFactory.createLineChart3D( "Progress of " + typeReport + 
                    " : " + oriname, "Month", "Total", dataset, PlotOrientation.VERTICAL, 
                    true, true, false );
        } 
        else if(typeChart.equals("2D LineChart")) {
            freechart = ChartFactory.createLineChart( "Progress of " + typeReport + 
                    " : " + oriname, "Month", "Total", dataset, PlotOrientation.VERTICAL, 
                    true, true, false );
        }
        
        //plot to "beautify" the chart
        CategoryPlot plot = freechart.getCategoryPlot();
        plot.setForegroundAlpha(1);

        //renderer of the freechart
        CategoryItemRenderer ren = plot.getRenderer();
        ren.setLabelGenerator(new StandardCategoryLabelGenerator()); 
        ren.setPositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
        ren.setItemLabelsVisible(true);
         
        if(typeChart.equals("3D BarChart")) {
            BarRenderer3D barren = (BarRenderer3D)plot.getRenderer();
            barren.setItemMargin(0); // Distance between each bar in a group.
            barren.setMaxBarWidth(1); // The max width of the bar 
            barren.setItemLabelAnchorOffset(8);
        }
        else if(typeChart.equals("2D BarChart")) {
            BarRenderer barren = (BarRenderer)plot.getRenderer();
            barren.setItemMargin(0); // Distance between each bar in a group.
            barren.setMaxBarWidth(1); // The max width of the bar 
        }
        else if(typeChart.equals("3D LineChart")) {
            LineRenderer3D larren = (LineRenderer3D)plot.getRenderer();
            larren.setItemLabelAnchorOffset(8);
        }
        
        ChartPanel chartpanel = new ChartPanel( freechart );
        
        return chartpanel;
    }
    
    public JTable ItemsDetailReport( String oridatefromst, String oridatetost, String typeTransaction ) {
        
        //format the string
        String datefromst = "'" + oridatefromst + "'";
        String datetost = "'" + oridatetost + "'";
        
        //title of the report
        String title = "";
        
        //the table
        JTable table = new JTable();
            
        try {
            stmt = conn.createStatement();
            
            if(typeTransaction.equals("sale")) {
                title = "The Sale Transaction Between " + 
                        df.format( new java.sql.Date(1).valueOf(oridatefromst) ) + " and " + 
                        df.format( new java.sql.Date(1).valueOf(oridatetost) );
                uprs = stmt.executeQuery("SELECT sum(jumlah) AS total, nama " +
                        "FROM data_barang b, transaksi_barang_penjualan t, data_transaksi_penjualan d " +
                        "WHERE t.index_penjualan = d.index AND " +
                        "tanggal BETWEEN " + datefromst + " AND " + datetost + " AND " +
                        " b.index = t.index_barang GROUP BY " +
                        "b.nama ORDER BY total DESC" );
            }
            else if(typeTransaction.equals("buy")) {
                title = "The Purchase Transaction Between " + 
                        df.format( new java.sql.Date(1).valueOf(oridatefromst)) + " and " + 
                        df.format( new java.sql.Date(1).valueOf(oridatetost));
                uprs = stmt.executeQuery("SELECT sum(jumlah) AS total, nama " +
                        "FROM data_barang b, transaksi_barang_pembelian t, data_transaksi_pembelian d " +
                        "WHERE t.index_pembelian = d.index AND " +
                        "tanggal BETWEEN " + datefromst + " AND " + datetost + " AND " +
                        " b.index = t.index_barang GROUP BY " +
                        "b.nama ORDER BY total DESC" );
            }
            
            table.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                    {null, null, null},
                    {null, null, null},
                    {null, null, null},
                    {null, null, null}
                },
                new String [] {
                    "No", "Item Name", "Total"
                }
            ) {
                Class[] types = new Class [] {
                    java.lang.Integer.class, java.lang.String.class, java.lang.String.class
                };
                boolean[] canEdit = new boolean [] {
                    false, false, false
                };

                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });
            
            int i = 0;
            
            while(uprs.next()) {
                //if the information from db, add the empty row
                if(i==table.getRowCount()) {
                    DefaultTableModel model = (DefaultTableModel)table.getModel();
                    model.addRow( new Object[] { null, null, null } );
                }
                
                if(uprs.getInt("total")!=0) {
                    table.getModel().setValueAt( i+1, i, 0 );
                    table.getModel().setValueAt( firstLetterCaps(uprs.getString("nama")), i, 1 );
                    table.getModel().setValueAt( uprs.getString("total"), i, 2 );
                }
                
                i++;
            }
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
        return table;
        
    }
    
    public ChartPanel ItemsTransPieReport( String oridatefromst, String oridatetost, 
            String typePieChart, String typeTransaction ) {
        
        //chart class
        JFreeChart freechart;
        
        //format the string
        String datefromst = "'" + oridatefromst + "'";
        String datetost = "'" + oridatetost + "'";
        
        //title of the report
        String title = "";
        
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        try {
            stmt = conn.createStatement();
            
            if(typeTransaction.equals("sale")) {
                title = "The Sale Transaction Between " + 
                        df.format( new java.sql.Date(1).valueOf(oridatefromst) ) + " and " + 
                        df.format( new java.sql.Date(1).valueOf(oridatetost) );
                uprs = stmt.executeQuery("SELECT sum(jumlah) AS total, nama " +
                        "FROM data_barang b, transaksi_barang_penjualan t, data_transaksi_penjualan d " +
                        "WHERE t.index_penjualan = d.index AND " +
                        "tanggal BETWEEN " + datefromst + " AND " + datetost + " AND " +
                        " b.index = t.index_barang GROUP BY " +
                        "b.nama ORDER BY total" );
            }
            else if(typeTransaction.equals("buy")) {
                title = "The Purchase Transaction Between " + 
                        df.format( new java.sql.Date(1).valueOf(oridatefromst)) + " and " + 
                        df.format( new java.sql.Date(1).valueOf(oridatetost));
                uprs = stmt.executeQuery("SELECT sum(jumlah) AS total, nama " +
                        "FROM data_barang b, transaksi_barang_pembelian t, data_transaksi_pembelian d " +
                        "WHERE t.index_pembelian = d.index AND " +
                        "tanggal BETWEEN " + datefromst + " AND " + datetost + " AND " +
                        " b.index = t.index_barang GROUP BY " +
                        "b.nama ORDER BY total" );
            }
            
            List<Integer>  itemarray = new ArrayList<Integer>();
            List<String> stringarray = new ArrayList<String>();
            
            //save the uprs to arraylist
            while(uprs.next()) {
                itemarray.add( uprs.getInt("total"));
                stringarray.add(  firstLetterCaps(uprs.getString("nama")) );
            }
            
            //find the sum
            int itemsize = itemarray.size();
            int itemsum = 0;
            for( int i=0; i<itemsize; i++ ) {
                itemsum += (Integer)itemarray.get(i);
            }
            
            //find the limit ( we don't want to list item under 2% )
            int itemlimit = itemsum / 50;
            
            //sum of other item under 2%
            int otheritem = 0;
            
            for( int i=0; i<itemsize; i++ ) {
                if((Integer)itemarray.get(i)<itemlimit) {
                    otheritem += (Integer)itemarray.get(i);
                }
                else {
                    dataset.setValue( (String)stringarray.get(i), new Double((Integer)itemarray.get(i)) );
                }
            }
            
            //if there is item under 2%
            if(otheritem!=0) {
                dataset.setValue( "Other", new Double(otheritem) );
            }
            
//            while(uprs.next()) {
//                dataset.setValue( firstLetterCaps(uprs.getString("nama")), 
//                        new Double(uprs.getInt("total")) );
//            }
            
            uprs.close();
            stmt.close();
        }
        catch( SQLException ex ) {
            ex.printStackTrace();
            new WriteLogFile(ex).writeLogFile();
        }
        
        if(typePieChart.equals("2D PieChart")) {
            freechart = ChartFactory.createPieChart( title,
                    dataset,
                    true,
                    true,
                    false);    
            PiePlot plot = (PiePlot)freechart.getPlot();
            if(dataset.getIndex("Other")>=0)
                plot.setSectionPaint( dataset.getIndex("Other"), java.awt.Color.GRAY );
        }
        else {
            freechart = ChartFactory.createPieChart3D(
                    title,
                    dataset,
                    true,
                    true,
                    false);
            if(typePieChart.equals("Transparent 3D PieChart")) {
                PiePlot3D plot = (PiePlot3D)freechart.getPlot();
                plot.setForegroundAlpha( 0.6f );
                if(dataset.getIndex("Other")>=0)
                    plot.setSectionPaint( dataset.getIndex("Other"), java.awt.Color.GRAY );
            }
            else {
                PiePlot plot = (PiePlot)freechart.getPlot();
                if(dataset.getIndex("Other")>=0)
                    plot.setSectionPaint( dataset.getIndex("Other"), java.awt.Color.GRAY );
            }
        }
        
        return new ChartPanel( freechart );
        
    }
    
    private void nullStartDataChart( ResultSet uprs, 
            List<Integer> intarray, List<String> stringarray, String datefrom ) throws SQLException {
        //fill data into db
        int startyear = Integer.parseInt(datefrom.substring(0,datefrom.indexOf('-')));
        int startmonth = Integer.parseInt(datefrom.substring(datefrom.indexOf('-')+1,
                datefrom.lastIndexOf('-')));
        int currentmonth = 0;
        int currentyear = 0;
        
        if(uprs.next()) {
            currentmonth = uprs.getInt("month");
            currentyear = uprs.getInt("year");
        }
        else {
            return;
        }
            
        for(int i=startyear; i<=currentyear; i++) {
            if(i==startyear&&i!=currentyear) {
                for(int j=startmonth; j<=12; j++) {
                    intarray.add( 0 );
                    if(stringarray!=null)
                        stringarray.add( new String( month[j] + " " + i) );
//                    dataset.setValue( 0, label, 
//                                new String( month[j] + " " + i) );
                }
            }
            else if(i==currentyear) {
                if(startyear==currentyear) {
                    for(int j=startmonth; j<currentmonth; j++) {
                        intarray.add( 0 );
                        if(stringarray!=null)
                            stringarray.add( new String( month[j] + " " + i) );
//                        dataset.setValue( 0, label, 
//                                new String( month[j] + " " + i) );
                    }
                }
                else {
                    for(int j=1; j<currentmonth; j++) {
                        intarray.add( 0 );
                        if(stringarray!=null)
                            stringarray.add( new String( month[j] + " " + i) );
//                        dataset.setValue( 0, label, 
//                                    new String( month[j] + " " + i) );
                    }    
                }
            }
            else {
                for(int j=1; j<=12; j++) {
                    intarray.add( 0 );
                    if(stringarray!=null)
                        stringarray.add( new String( month[j] + " " + i) );
//                    dataset.setValue( 0, label, 
//                                new String( month[j] + " " + i) );
                }
            }
        }
        
    }
    
    private void nullStartDataChart( ResultSet uprs, String label, 
            DefaultCategoryDataset dataset, String datefrom ) throws SQLException {
        //fill data into db
        int startyear = Integer.parseInt(datefrom.substring(0,datefrom.indexOf('-')));
        int startmonth = Integer.parseInt(datefrom.substring(datefrom.indexOf('-')+1,
                datefrom.lastIndexOf('-')));
        int currentmonth = 0;
        int currentyear = 0;
        
        if(uprs.next()) {
            currentmonth = uprs.getInt("month");
            currentyear = uprs.getInt("year");
        }
        else {
            return;
        }
            
        for(int i=startyear; i<=currentyear; i++) {
            if(i==startyear&&i!=currentyear) {
                for(int j=startmonth; j<=12; j++) {
                    dataset.setValue( 0, label, 
                                new String( month[j] + " " + i) );
                }
            }
            else if(i==currentyear) {
                if(startyear==currentyear) {
                    for(int j=startmonth; j<currentmonth; j++) {
                        dataset.setValue( 0, label, 
                                new String( month[j] + " " + i) );
                    }
                }
                else {
                    for(int j=1; j<currentmonth; j++) {
                        dataset.setValue( 0, label, 
                                    new String( month[j] + " " + i) );
                    }    
                }
            }
            else {
                for(int j=1; j<=12; j++) {
                    dataset.setValue( 0, label, 
                                new String( month[j] + " " + i) );
                }
            }
        }
        
    }
    
    private void nullEndDataChart( ResultSet uprs, 
            List<Integer> intarray, List<String>stringarray, String dateto ) throws SQLException {
        //fill data into db
        int endyear = Integer.parseInt(dateto.substring(0,dateto.indexOf('-')));
        int endmonth = Integer.parseInt(dateto.substring(dateto.indexOf('-')+1,
                dateto.lastIndexOf('-')));
        int currentmonth = 0;
        int currentyear = 0;
        
        while(uprs.next()) {
            currentmonth = uprs.getInt("month");
            currentyear = uprs.getInt("year");    
        }
        
        for(int i=currentyear; i<=endyear; i++) {
            if(i==currentyear&&i!=endyear) {
                for(int j=currentmonth+1; j<=12; j++) {
                    intarray.add( 0 );
                    if(stringarray!=null)
                        stringarray.add( new String( month[j] + " " + i) );
//                    dataset.setValue( 0, label, 
//                                new String( month[j] + " " + i) );
                }
            }
            else if(i==endyear) {
                if(currentyear==endyear) {
                    for(int j=currentmonth+1; j<=endmonth; j++) {
                        intarray.add( 0 );
                        if(stringarray!=null)
                            stringarray.add( new String( month[j] + " " + i) );
//                        dataset.setValue( 0, label, 
//                                    new String( month[j] + " " + i) );
                    }    
                }
                else {
                    for(int j=1; j<=endmonth; j++) {
                        intarray.add( 0 );
                        if(stringarray!=null)
                            stringarray.add( new String( month[j] + " " + i) );
//                        dataset.setValue( 0, label, 
//                                    new String( month[j] + " " + i) );
                    }    
                }
            }
            else {
                for(int j=1; j<=12; j++) {
                    intarray.add( 0 );
                    if(stringarray!=null)
                        stringarray.add( new String( month[j] + " " + i) );
//                    dataset.setValue( 0, label, 
//                                new String( month[j] + " " + i) );
                }
            }
        }
    }
    
    private void nullEndDataChart( ResultSet uprs, String label,
            DefaultCategoryDataset dataset, String dateto ) throws SQLException {
        //fill data into db
        int endyear = Integer.parseInt(dateto.substring(0,dateto.indexOf('-')));
        int endmonth = Integer.parseInt(dateto.substring(dateto.indexOf('-')+1,
                dateto.lastIndexOf('-')));
        int currentmonth = 0;
        int currentyear = 0;
        
        while(uprs.next()) {
            currentmonth = uprs.getInt("month");
            currentyear = uprs.getInt("year");    
        }
        
        for(int i=currentyear; i<=endyear; i++) {
            if(i==currentyear&&i!=endyear) {
                for(int j=currentmonth+1; j<=12; j++) {
                    dataset.setValue( 0, label, 
                                new String( month[j] + " " + i) );
                }
            }
            else if(i==endyear) {
                if(currentyear==endyear) {
                    for(int j=currentmonth+1; j<=endmonth; j++) {
                        dataset.setValue( 0, label, 
                                    new String( month[j] + " " + i) );
                    }    
                }
                else {
                    for(int j=1; j<=endmonth; j++) {
                        dataset.setValue( 0, label, 
                                    new String( month[j] + " " + i) );
                    }    
                }
            }
            else {
                for(int j=1; j<=12; j++) {
                    dataset.setValue( 0, label, 
                                new String( month[j] + " " + i) );
                }
            }
        }
    }
    
    private void fillDataChart( ResultSet uprs, List<Integer> intarray,
            List<String> stringarray ) throws SQLException {
        //fill data into db
        int previousmonth = -1;
        int currentmonth = 0;
        int previousyear = -1;
        int currentyear = 0;
        while(uprs.next()) {
            currentmonth = uprs.getInt("month");
            currentyear = uprs.getInt("year");
            //set it zero value for "notworking" month
            if(previousyear!=-1&&currentyear==previousyear) {
                if(currentmonth-previousmonth>1) {
                    for(int i=previousmonth+1; i<currentmonth; i++) {
                        intarray.add( 0 );
                        if(stringarray!=null)
                            stringarray.add( new String( month[ i ] + " " + currentyear ) );
//                        dataset.setValue( 0, label, 
//                            new String( month[ i ] + " " + currentyear ) );    
                    }
                }
            }
            else if(previousyear!=-1&&currentyear!=previousyear) {
                for(int i=previousyear; i<=currentyear; i++) {
                    //set null for previous year until december
                    if(i==previousyear) {
                        for( int j=previousmonth+1; j<=12; j++) {
                            intarray.add( 0 );
                            if(stringarray!=null)
                                stringarray.add( new String( month[j] + " " + i) );
//                            dataset.setValue( 0, label, 
//                                    new String( month[j] + " " + i) );
                        }
                    }
                    else if(i==currentyear) {
                        //set null from january until current month
                        for( int j=1; j<currentmonth; j++) {
                            intarray.add( 0 );
                            if(stringarray!=null)
                                stringarray.add( new String( month[j] + " " + i) );
//                            dataset.setValue( 0, label, 
//                                    new String( month[j] + " " + i) );
                        }
                    }
                    else {
                        //set null for all mont in "between" year
                        for( int j=1; j<=12; j++) {
                            intarray.add( 0 );
                            if(stringarray!=null)
                                stringarray.add( new String( month[j] + " " + i) );
//                            dataset.setValue( 0, label, 
//                                    new String( month[j] + " " + i) );
                        }
                    }
                }
            }
            intarray.add( uprs.getInt("total") );
            if(stringarray!=null)
                stringarray.add( new String( month[ currentmonth ] + " " + currentyear ) );
//            dataset.setValue( uprs.getInt("total"), label, 
//                    new String( month[ currentmonth ] + " " + currentyear ) );    
            previousmonth = currentmonth;
            previousyear = currentyear;
        }
    }
    
    private void fillDataChart( ResultSet uprs, String label, DefaultCategoryDataset dataset )
        throws SQLException {
        //fill data into db
        int previousmonth = -1;
        int currentmonth = 0;
        int previousyear = -1;
        int currentyear = 0;
        while(uprs.next()) {
            currentmonth = uprs.getInt("month");
            currentyear = uprs.getInt("year");
            //set it zero value for "notworking" month
            if(previousyear!=-1&&currentyear==previousyear) {
                if(currentmonth-previousmonth>1) {
                    for(int i=previousmonth+1; i<currentmonth; i++) {
                        dataset.setValue( 0, label, 
                            new String( month[ i ] + " " + currentyear ) );    
                    }
                }
            }
            else if(previousyear!=-1&&currentyear!=previousyear) {
                for(int i=previousyear; i<=currentyear; i++) {
                    //set null for previous year until december
                    if(i==previousyear) {
                        for( int j=previousmonth+1; j<=12; j++) {
                            dataset.setValue( 0, label, 
                                    new String( month[j] + " " + i) );
                        }
                    }
                    else if(i==currentyear) {
                        //set null from january until current month
                        for( int j=1; j<currentmonth; j++) {
                            dataset.setValue( 0, label, 
                                    new String( month[j] + " " + i) );
                        }
                    }
                    else {
                        //set null for all mont in "between" year
                        for( int j=1; j<=12; j++) {
                            dataset.setValue( 0, label, 
                                    new String( month[j] + " " + i) );
                        }
                    }
                }
            }
            dataset.setValue( uprs.getInt("total"), label, 
                    new String( month[ currentmonth ] + " " + currentyear ) );    
            previousmonth = currentmonth;
            previousyear = currentyear;
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
    
    public class ReportFilter extends FileFilter {

        String extension = "";
        
        //Accept all directories and all pdf, xml, html.
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            extension = Utils.getExtension(f);
            if (extension != null) {
                if (  extension.equals(Utils.png) ) 
                {
                        return true;
                } else {
                    return false;
                }
            }

            return false;
        }
        
        public String getDescription() {
            return "*.png";
        }
    }
    
}
