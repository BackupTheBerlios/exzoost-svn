/*
 * AddWarehouseDialog.java
 *
 * Created on March 2, 2005, 3:42 PM
 */

package com.exzoost.gui.maingui;

import com.exzoost.database.WarehouseDB;
import com.exzoost.gui.helper.GuiHelper;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.sql.Connection;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author  knight
 */
public class AddWarehouseDialog extends javax.swing.JDialog {
    private WarehouseDB warehousedb;
    private Connection conn;
    private JTable table;
    /** Creates new form AddWarehouseDialog */
    public AddWarehouseDialog(java.awt.Frame parent, boolean modal, 
            JTable table, Connection conn ) {
        super(parent, modal);
        
        //setup the gui
        initComponents();
        
        GuiHelper.setOnCenter((Window)this);
        
        this.conn = conn;
        this.table = table;
        
        //must use this database class
        warehousedb = new WarehouseDB( conn );
        
        getRootPane().setDefaultButton(OK);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        BottomPanel = new javax.swing.JPanel();
        Cancel = new javax.swing.JButton();
        OK = new javax.swing.JButton();
        DummyTopLabel = new javax.swing.JLabel();
        LeftDummyLabel = new javax.swing.JLabel();
        RightDummyLabel = new javax.swing.JLabel();
        WarehousePanel = new javax.swing.JPanel();
        NameLabel = new javax.swing.JLabel();
        nameTF = new javax.swing.JTextField();
        AddressLabel = new javax.swing.JLabel();
        addressTA = new javax.swing.JTextArea();
        CommentLabel = new javax.swing.JLabel();
        commentTA = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Warehouse Dialog");
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

        DummyTopLabel.setText("                ");
        getContentPane().add(DummyTopLabel, java.awt.BorderLayout.NORTH);

        LeftDummyLabel.setText("     ");
        getContentPane().add(LeftDummyLabel, java.awt.BorderLayout.WEST);

        RightDummyLabel.setText("     ");
        getContentPane().add(RightDummyLabel, java.awt.BorderLayout.EAST);

        WarehousePanel.setLayout(new java.awt.GridBagLayout());

        NameLabel.setText("* Name : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        WarehousePanel.add(NameLabel, gridBagConstraints);

        nameTF.setColumns(12);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        WarehousePanel.add(nameTF, gridBagConstraints);

        AddressLabel.setText("* Address : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        WarehousePanel.add(AddressLabel, gridBagConstraints);

        addressTA.setColumns(12);
        addressTA.setLineWrap(true);
        addressTA.setRows(4);
        addressTA.setTabSize(4);
        addressTA.setWrapStyleWord(true);
        addressTA.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        WarehousePanel.add(addressTA, gridBagConstraints);

        CommentLabel.setText("Comment : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        WarehousePanel.add(CommentLabel, gridBagConstraints);

        commentTA.setColumns(16);
        commentTA.setLineWrap(true);
        commentTA.setRows(8);
        commentTA.setTabSize(4);
        commentTA.setWrapStyleWord(true);
        commentTA.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        WarehousePanel.add(commentTA, gridBagConstraints);

        jLabel1.setText("Fields marked with an asterisk * are required.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        WarehousePanel.add(jLabel1, gridBagConstraints);

        getContentPane().add(WarehousePanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKActionPerformed
        String name = nameTF.getText().trim();
        String address = addressTA.getText().trim();
        String comment = commentTA.getText();
        
        if(name.equals("")) {
            JOptionPane.showMessageDialog(null,"Please insert the Name Text Field!", 
                    "Warning", JOptionPane.WARNING_MESSAGE );
            nameTF.requestFocusInWindow();
            return;
        }
        else if(name.toLowerCase().equals("total")) {
            JOptionPane.showMessageDialog(null,"Name of warehouse could not be 'total'!", 
                    "Warning", JOptionPane.WARNING_MESSAGE );
            nameTF.requestFocusInWindow();
            return;
        }
        
        boolean result = warehousedb.insertWarehouse(table, name, address, comment ); 
        
        //error inserting warehouse
        if(!result)
            JOptionPane.showMessageDialog(null,"Error adding warehouse! See log file " +
                    "for detail!", "Warning", JOptionPane.WARNING_MESSAGE );
        
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
                new AddWarehouseDialog(new javax.swing.JFrame(), true, 
                        null, null).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AddressLabel;
    private javax.swing.JPanel BottomPanel;
    private javax.swing.JButton Cancel;
    private javax.swing.JLabel CommentLabel;
    private javax.swing.JLabel DummyTopLabel;
    private javax.swing.JLabel LeftDummyLabel;
    private javax.swing.JLabel NameLabel;
    private javax.swing.JButton OK;
    private javax.swing.JLabel RightDummyLabel;
    private javax.swing.JPanel WarehousePanel;
    private javax.swing.JTextArea addressTA;
    private javax.swing.JTextArea commentTA;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField nameTF;
    // End of variables declaration//GEN-END:variables
    
}
