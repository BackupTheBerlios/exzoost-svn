/*
 * EditDebtCreditDialog.java
 *
 * Created on February 25, 2005, 10:41 PM
 */

package com.exzoost.gui.transactions;

import com.exzoost.database.Transaction;
import com.exzoost.gui.helper.GuiHelper;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author  knight
 */
public class EditDebtCreditDialog extends javax.swing.JDialog {
    private JTable table;
    private String operation;
    private Transaction tr;
    private NumberFormat nf;
    private int row;
    private Connection conn;
    /** Creates new form EditDebtCreditDialog */
    public EditDebtCreditDialog(java.awt.Frame parent, boolean modal, 
            JTable table, String operation, Connection conn) {
        super(parent, modal);
        initComponents();
        this.table = table;
        this.operation = operation;
        this.conn = conn;
        
        //use the transaction class
        tr = new Transaction( conn );
        
        GuiHelper.setOnCenter((Window)this);
        
        //format the currency
        nf = NumberFormat.getCurrencyInstance( new Locale("id","id"));
        
        //where is the row to be edited
        row = table.getSelectedRow();
        
        //setup the text field
        InvoiceTF.setText((String)table.getValueAt(row,0));
        DateTF.setText( (String)table.getValueAt(row,1) );
        HavePaidTF.setText( (String)table.getValueAt(row,3) );
        MustPayTF.setText( (String)table.getValueAt(row,2) );
        TotalTransactionTF.setText( (String)table.getValueAt(row,4) );
        
        //difference between sale(credit) and buy(debt)
        if(operation.equals("sale")) {
            setTitle("Edit Credit Dialog");
            HavePaidLabel.setText("The Customer Have Paid : ");
            MustPayLabel.setText("The Customer Must Pay : ");
            PayLabel.setText("The Customer Pay : ");
            ReturnLabel.setText("We Return : ");
        }
        else if(operation.equals("buy")) {
            setTitle("Edit Debt Dialog");
            HavePaidLabel.setText("We Have Paid : ");
            MustPayLabel.setText("We Must Pay : ");
            PayLabel.setText("We Pay : ");
            ReturnLabel.setText("The Suplier Return : ");
        }
        
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
        TopDummyLabel = new javax.swing.JPanel();
        LeftDummyLabel = new javax.swing.JPanel();
        RightDummyLabel = new javax.swing.JPanel();
        MainPanel = new javax.swing.JPanel();
        InvoiceLabel = new javax.swing.JLabel();
        InvoiceTF = new javax.swing.JTextField();
        DateLabel = new javax.swing.JLabel();
        DateTF = new javax.swing.JTextField();
        TotalTransactionLabel = new javax.swing.JLabel();
        TotalTransactionTF = new javax.swing.JTextField();
        HavePaidLabel = new javax.swing.JLabel();
        HavePaidTF = new javax.swing.JTextField();
        MustPayLabel = new javax.swing.JLabel();
        MustPayTF = new javax.swing.JTextField();
        PayLabel = new javax.swing.JLabel();
        PayTF = new javax.swing.JTextField();
        ReturnLabel = new javax.swing.JLabel();
        ReturnTF = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit Credit Dialog");
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

        getContentPane().add(TopDummyLabel, java.awt.BorderLayout.NORTH);

        getContentPane().add(LeftDummyLabel, java.awt.BorderLayout.WEST);

        getContentPane().add(RightDummyLabel, java.awt.BorderLayout.EAST);

        MainPanel.setLayout(new java.awt.GridBagLayout());

        InvoiceLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        InvoiceLabel.setText("Invoice : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainPanel.add(InvoiceLabel, gridBagConstraints);

        InvoiceTF.setColumns(12);
        InvoiceTF.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainPanel.add(InvoiceTF, gridBagConstraints);

        DateLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        DateLabel.setText("Date : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainPanel.add(DateLabel, gridBagConstraints);

        DateTF.setColumns(12);
        DateTF.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainPanel.add(DateTF, gridBagConstraints);

        TotalTransactionLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        TotalTransactionLabel.setText("Total Transaction : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainPanel.add(TotalTransactionLabel, gridBagConstraints);

        TotalTransactionTF.setColumns(12);
        TotalTransactionTF.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainPanel.add(TotalTransactionTF, gridBagConstraints);

        HavePaidLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        HavePaidLabel.setText("The Customer Have Paid : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainPanel.add(HavePaidLabel, gridBagConstraints);

        HavePaidTF.setColumns(12);
        HavePaidTF.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainPanel.add(HavePaidTF, gridBagConstraints);

        MustPayLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        MustPayLabel.setText("The Customer Must Pay : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainPanel.add(MustPayLabel, gridBagConstraints);

        MustPayTF.setColumns(12);
        MustPayTF.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainPanel.add(MustPayTF, gridBagConstraints);

        PayLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        PayLabel.setText("The Customer Pay : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainPanel.add(PayLabel, gridBagConstraints);

        PayTF.setColumns(12);
        PayTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                PayTFFocusGained(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainPanel.add(PayTF, gridBagConstraints);

        ReturnLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ReturnLabel.setText("The Customer Return : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainPanel.add(ReturnLabel, gridBagConstraints);

        ReturnTF.setColumns(12);
        ReturnTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ReturnTFFocusGained(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainPanel.add(ReturnTF, gridBagConstraints);

        getContentPane().add(MainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    private void ReturnTFFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ReturnTFFocusGained
        PayTF.setText("");
    }//GEN-LAST:event_ReturnTFFocusGained

    private void PayTFFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_PayTFFocusGained
        ReturnTF.setText("");
    }//GEN-LAST:event_PayTFFocusGained

    private void OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKActionPerformed
        //check if the input is under 0 and upper total transaction
        int totaltrans = 0;
        int havepaid = 0;
        
        try {
            totaltrans = ((Long)nf.parse(TotalTransactionTF.getText())).intValue();
            havepaid = ((Long)nf.parse( HavePaidTF.getText())).intValue(); 
        }
        catch( java.text.ParseException e ) {
            System.out.println(e.getMessage());
        }
        int index = ((Integer)table.getModel().getValueAt(row,5)).intValue();
        int pay = 0;
        int returnpay = 0;
        
        
        //if pay be done
        if(!PayTF.getText().trim().equals("")) {
            pay = Integer.parseInt( PayTF.getText() );
            if(pay<0) {
                JOptionPane.showMessageDialog(null,"Please don't insert negative value!", 
                        "Warning", JOptionPane.WARNING_MESSAGE );
                return;
            }
            else if(pay+havepaid>totaltrans) {
                if(operation.equals("buy"))
                    JOptionPane.showMessageDialog(null,"You pay more than total transaction value!", 
                            "Warning", JOptionPane.WARNING_MESSAGE );
                else if(operation.equals("sale")) 
                    JOptionPane.showMessageDialog(null,"The customer pay more than total transaction " +
                            "value!", "Warning", JOptionPane.WARNING_MESSAGE );
                return;
            }
            havepaid += pay;
        }
        //if return be done
        if(!ReturnTF.getText().trim().equals("")) {
            returnpay = Integer.parseInt( ReturnTF.getText() );
            if(returnpay<0) {
                JOptionPane.showMessageDialog(null,"Please don't insert negative value!",
                        "Warning", JOptionPane.WARNING_MESSAGE );
                return;
            }
            else if(returnpay>havepaid) {
                if(operation.equals("buy"))
                    JOptionPane.showMessageDialog(null, "The seller return more than total " +
                            "transaction value!", "Warning", JOptionPane.WARNING_MESSAGE );
                else if(operation.equals("sale"))
                    JOptionPane.showMessageDialog(null, "You return more than total transaction " +
                            "value!", "Warning", JOptionPane.WARNING_MESSAGE );
                return;
            }
            havepaid -= returnpay;
        }
        
        //debt
        if(operation.equals("buy")) {
            tr.EditDebtTransaction(index, havepaid, totaltrans );
        }
        //sale
        else if(operation.equals("sale")) {
            tr.EditCreditTransaction(index, havepaid, totaltrans );
        }
        
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
                new EditDebtCreditDialog(new javax.swing.JFrame(), true, 
                        null, null, null).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BottomPanel;
    private javax.swing.JButton Cancel;
    private javax.swing.JLabel DateLabel;
    private javax.swing.JTextField DateTF;
    private javax.swing.JLabel HavePaidLabel;
    private javax.swing.JTextField HavePaidTF;
    private javax.swing.JLabel InvoiceLabel;
    private javax.swing.JTextField InvoiceTF;
    private javax.swing.JPanel LeftDummyLabel;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JLabel MustPayLabel;
    private javax.swing.JTextField MustPayTF;
    private javax.swing.JButton OK;
    private javax.swing.JLabel PayLabel;
    private javax.swing.JTextField PayTF;
    private javax.swing.JLabel ReturnLabel;
    private javax.swing.JTextField ReturnTF;
    private javax.swing.JPanel RightDummyLabel;
    private javax.swing.JPanel TopDummyLabel;
    private javax.swing.JLabel TotalTransactionLabel;
    private javax.swing.JTextField TotalTransactionTF;
    // End of variables declaration//GEN-END:variables
    
}
