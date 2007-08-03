/*
 * LowItemsList.java
 *
 * Created on April 15, 2005, 8:37 PM
 */

package com.exzoost.gui.inventory;

import com.exzoost.database.InventoryDB;
import com.exzoost.gui.helper.GuiHelper;
import com.exzoost.xmlhandler.XMLHandler;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.sql.Connection;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author  knight
 */
public class LowItemsList extends javax.swing.JDialog {
    private InventoryDB inventorydb;
    private int limit;
    private XMLHandler xmlhandler;
    /** Creates new form LowItemsList */
    public LowItemsList(java.awt.Frame parent, boolean modal, Connection conn) {
        super(parent, modal);
        initComponents();
        
        xmlhandler = new XMLHandler();
        inventorydb = new InventoryDB( conn );
        inventorydb.initializeComboBox( WarehouseCoB, "warehouse" );
        
        //reading limit from xml file
        limit = xmlhandler.getLimitLowQuantityItemList();
        
        //initialize directly
        WarehouseCoBActionPerformed(null);
        
        //setup the size of column --> look beautiful
        final int quantitycolumn = 1;
        final int prefsizequantitycolumn = 100;
        MainTable.getColumnModel().getColumn(quantitycolumn).
                setMaxWidth(prefsizequantitycolumn);
        
        GuiHelper.setOnCenter((Window)this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        DummyTopLabel = new javax.swing.JLabel();
        LeftDummyLabel = new javax.swing.JLabel();
        BottomPanel = new javax.swing.JPanel();
        BottomLeftPanel = new javax.swing.JPanel();
        DisplayMessageChB = new javax.swing.JCheckBox();
        BottomRightPanel = new javax.swing.JPanel();
        Close = new javax.swing.JButton();
        RightDummyLabel = new javax.swing.JLabel();
        MainPanel = new javax.swing.JPanel();
        MainScrollPane = new javax.swing.JScrollPane();
        MainTable = new javax.swing.JTable();
        TopPanel = new javax.swing.JPanel();
        WarehouseLB = new javax.swing.JLabel();
        WarehouseCoB = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("List of Item With Low Quantity");
        DummyTopLabel.setText("                ");
        getContentPane().add(DummyTopLabel, java.awt.BorderLayout.NORTH);

        LeftDummyLabel.setText("     ");
        getContentPane().add(LeftDummyLabel, java.awt.BorderLayout.WEST);

        BottomPanel.setLayout(new java.awt.BorderLayout());

        BottomLeftPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 20));

        DisplayMessageChB.setSelected(true);
        DisplayMessageChB.setText("Display This Dialog on Startup of Program");
        BottomLeftPanel.add(DisplayMessageChB);

        BottomPanel.add(BottomLeftPanel, java.awt.BorderLayout.WEST);

        BottomRightPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 20));

        Close.setText("Close");
        Close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseActionPerformed(evt);
            }
        });

        BottomRightPanel.add(Close);

        BottomPanel.add(BottomRightPanel, java.awt.BorderLayout.EAST);

        getContentPane().add(BottomPanel, java.awt.BorderLayout.SOUTH);

        RightDummyLabel.setText("     ");
        getContentPane().add(RightDummyLabel, java.awt.BorderLayout.EAST);

        MainPanel.setLayout(new java.awt.BorderLayout());

        MainTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Name", "Quantity"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        MainScrollPane.setViewportView(MainTable);

        MainPanel.add(MainScrollPane, java.awt.BorderLayout.CENTER);

        TopPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 20, 10));

        WarehouseLB.setText("Warehouse : ");
        TopPanel.add(WarehouseLB);

        WarehouseCoB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WarehouseCoBActionPerformed(evt);
            }
        });

        TopPanel.add(WarehouseCoB);

        MainPanel.add(TopPanel, java.awt.BorderLayout.NORTH);

        getContentPane().add(MainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    private void CloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseActionPerformed
        //should we display on next startup
        if(!DisplayMessageChB.isSelected()) {
            xmlhandler.setDisplayStartupDialog(false);
        }
            
        dispose();
    }//GEN-LAST:event_CloseActionPerformed

    private void WarehouseCoBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WarehouseCoBActionPerformed
        ClearAllTable(MainTable);
        inventorydb.initializeLowItemsList( MainTable, (String)WarehouseCoB.getSelectedItem(),
                limit );
    }//GEN-LAST:event_WarehouseCoBActionPerformed
    
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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new LowItemsList(new javax.swing.JFrame(), true).setVisible(true);
//            }
//        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BottomLeftPanel;
    private javax.swing.JPanel BottomPanel;
    private javax.swing.JPanel BottomRightPanel;
    private javax.swing.JButton Close;
    private javax.swing.JCheckBox DisplayMessageChB;
    private javax.swing.JLabel DummyTopLabel;
    private javax.swing.JLabel LeftDummyLabel;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JScrollPane MainScrollPane;
    private javax.swing.JTable MainTable;
    private javax.swing.JLabel RightDummyLabel;
    private javax.swing.JPanel TopPanel;
    private javax.swing.JComboBox WarehouseCoB;
    private javax.swing.JLabel WarehouseLB;
    // End of variables declaration//GEN-END:variables
    
}