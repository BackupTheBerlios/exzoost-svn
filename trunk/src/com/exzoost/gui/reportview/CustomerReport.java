/*
 * CustomersReport.java
 *
 * Created on December 15, 2004, 9:43 PM
 */
/**
 *
 * @author  knight
 */
package com.exzoost.gui.reportview;

import com.exzoost.database.InventoryDB;
import com.exzoost.database.CustomerDB;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;

import org.jfree.chart.ChartPanel;

public class CustomerReport extends javax.swing.JDialog {
    private Connection conn;
    private InventoryDB inventory;
    private CustomerDB customerdb;
    /** Creates new form CustomersReport */
    public CustomerReport(java.awt.Frame parent, boolean modal, 
            javax.swing.JPanel panel, Connection conn ) {
        super(parent, modal);
        
        //this is the connection
        this.conn = conn;
        
        //setup the gui
        initComponents();
        
        inventory = new InventoryDB( conn );
        customerdb = new CustomerDB( conn );
        inventory.initializeComboBox(CustomerCoB, "customer");
        
        //must manipulate this panel to show the report
        this.panel = panel;
        
        //set the dialog in center of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int vpos = dim.height / 2 - getHeight() / 2;
        int hpos = dim.width / 2 - getWidth() / 2;
        setLocation( hpos, vpos );
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        BottomPanel = new javax.swing.JPanel();
        Cancel = new javax.swing.JButton();
        OK = new javax.swing.JButton();
        LeftDummyLabel = new javax.swing.JLabel();
        RightDummyLabel = new javax.swing.JLabel();
        BasicPanel = new javax.swing.JPanel();
        FromMonthLB = new javax.swing.JLabel();
        FromMonthChooser = new com.toedter.calendar.JMonthChooser();
        FromYearLB = new javax.swing.JLabel();
        FromYearChooser = new com.toedter.calendar.JYearChooser();
        FromLB = new javax.swing.JLabel();
        ToLB = new javax.swing.JLabel();
        ToMonthLB = new javax.swing.JLabel();
        ToYearLB = new javax.swing.JLabel();
        ToMonthChooser = new com.toedter.calendar.JMonthChooser();
        ToYearChooser = new com.toedter.calendar.JYearChooser();
        CustomerCoB = new javax.swing.JComboBox();
        ReportCoB = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Customer Progress Report");
        BottomPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        Cancel.setText("Cancel");
        Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelActionPerformed(evt);
            }
        });

        BottomPanel.add(Cancel);

        OK.setText("OK");
        OK.setPreferredSize(new java.awt.Dimension(75, 25));
        OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKActionPerformed(evt);
            }
        });

        BottomPanel.add(OK);

        getContentPane().add(BottomPanel, java.awt.BorderLayout.SOUTH);

        LeftDummyLabel.setText("     ");
        getContentPane().add(LeftDummyLabel, java.awt.BorderLayout.WEST);

        RightDummyLabel.setText("     ");
        getContentPane().add(RightDummyLabel, java.awt.BorderLayout.EAST);

        BasicPanel.setLayout(new java.awt.GridBagLayout());

        FromMonthLB.setText("Month : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        BasicPanel.add(FromMonthLB, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        BasicPanel.add(FromMonthChooser, gridBagConstraints);

        FromYearLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        FromYearLB.setText("Year : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 30, 0);
        BasicPanel.add(FromYearLB, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 30, 0);
        BasicPanel.add(FromYearChooser, gridBagConstraints);

        FromLB.setText("From : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(30, 10, 0, 0);
        BasicPanel.add(FromLB, gridBagConstraints);

        ToLB.setText("To : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(30, 30, 0, 0);
        BasicPanel.add(ToLB, gridBagConstraints);

        ToMonthLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ToMonthLB.setText("Month : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 0, 0);
        BasicPanel.add(ToMonthLB, gridBagConstraints);

        ToYearLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ToYearLB.setText("Year : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 30, 0);
        BasicPanel.add(ToYearLB, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 30);
        BasicPanel.add(ToMonthChooser, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 30, 30);
        BasicPanel.add(ToYearChooser, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        BasicPanel.add(CustomerCoB, gridBagConstraints);

        ReportCoB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "3D BarChart", "2D BarChart", "3D LineChart", "2D LineChart" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 0);
        BasicPanel.add(ReportCoB, gridBagConstraints);

        getContentPane().add(BasicPanel, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    private void OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKActionPerformed
        //this is the dummy date
        
        GregorianCalendar gregorian = new GregorianCalendar( ToYearChooser.getYear(),
                ToMonthChooser.getMonth(), 1 );
        
        //if year not compatible
        if(FromYearChooser.getYear()>ToYearChooser.getYear()) {
            JOptionPane.showMessageDialog(null,"The year in To field is earlier than year in " +
                    "From field!", "Warning", JOptionPane.WARNING_MESSAGE );
            return;
        }
        else if(FromYearChooser.getYear()==ToYearChooser.getYear()){
            //if month is not compatible
            if(FromMonthChooser.getMonth()>ToMonthChooser.getMonth()) {
                JOptionPane.showMessageDialog(null,"The month in To field is earlier than month " +
                        "in From field!", "Warning", JOptionPane.WARNING_MESSAGE );
                return;
            }
        }
        
        String datefrom = FromYearChooser.getYear() + "-" + (FromMonthChooser.getMonth()+1) + 
                "-01";
        String dateto = ToYearChooser.getYear() + "-" + (ToMonthChooser.getMonth()+1) + "-" +
                gregorian.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        //get the customer id
        String customer = ((String)CustomerCoB.getSelectedItem()).toLowerCase();
        int customerid = new Integer( customerdb.getIndex(customer) ).intValue();
        
        //bar report type ( 2d or 3d )
        String bartype = (String)ReportCoB.getSelectedItem();
        
        //report class
        ReportManipulation rm = new ReportManipulation( conn );
        
        //the chart panel
        ChartPanel chartpanel = rm.SaleChartReport( datefrom, dateto, customerid, bartype, "customer" );
        panel.removeAll();
        panel.add(chartpanel); 
        panel.revalidate();
        panel.repaint();
        
        dispose();
    }//GEN-LAST:event_OKActionPerformed

    private void CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelActionPerformed
        dispose();
    }//GEN-LAST:event_CancelActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SalesmanReport(new javax.swing.JFrame(), true, 
                        new javax.swing.JPanel(), null).setVisible(true);
            }
        });
    }
    
    private javax.swing.JPanel panel;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BasicPanel;
    private javax.swing.JPanel BottomPanel;
    private javax.swing.JButton Cancel;
    private javax.swing.JComboBox CustomerCoB;
    private javax.swing.JLabel FromLB;
    private com.toedter.calendar.JMonthChooser FromMonthChooser;
    private javax.swing.JLabel FromMonthLB;
    private com.toedter.calendar.JYearChooser FromYearChooser;
    private javax.swing.JLabel FromYearLB;
    private javax.swing.JLabel LeftDummyLabel;
    private javax.swing.JButton OK;
    private javax.swing.JComboBox ReportCoB;
    private javax.swing.JLabel RightDummyLabel;
    private javax.swing.JLabel ToLB;
    private com.toedter.calendar.JMonthChooser ToMonthChooser;
    private javax.swing.JLabel ToMonthLB;
    private com.toedter.calendar.JYearChooser ToYearChooser;
    private javax.swing.JLabel ToYearLB;
    // End of variables declaration//GEN-END:variables
    
}
