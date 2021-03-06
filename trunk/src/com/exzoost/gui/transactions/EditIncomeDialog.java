package com.exzoost.gui.transactions;
/*
 * TaxDialog.java
 *
 * Created on December 15, 2004, 1:43 PM
 */

import com.exzoost.database.Transaction;
import com.exzoost.gui.helper.GuiHelper;
import com.exzoost.gui.htmleditor.HTMLEditorManipulation;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JTable;
/**
 *
 * @author  knight
 */
public class EditIncomeDialog extends javax.swing.JDialog {
    private Transaction tr;
    private Connection conn;
    private JTable table;
    /** Creates new form TaxDialog */
    public EditIncomeDialog(java.awt.Frame parent, boolean modal,  
            JTable table, Connection conn ) {
        super(parent, modal);
        this.conn = conn;
        this.table = table;
        tr = new Transaction( conn );
        initComponents();
        
        GuiHelper.setOnCenter((Window)this);
        
        //initialize the dialog
        //the selected row
        int row = table.getSelectedRow();
        
        //parse beautiful string to date
        DateFormat df = DateFormat.getDateInstance(
                        DateFormat.LONG, Locale.ENGLISH );
        
        //format the currency
        NumberFormat nf = NumberFormat.getCurrencyInstance( new Locale("id","id"));
        
        try {
            OtherDateChooser.setDate( df.parse( (String)table.getModel().getValueAt(row,1) ) );
        }
        catch( java.text.ParseException e ) {
            e.printStackTrace();
        }
        InvoiceTF.setText( (String)table.getModel().getValueAt(row,0) );
        try {
            AmountTF.setText( (nf.parse((String)table.getModel().getValueAt(row,3))).toString() );
        }
        catch( java.text.ParseException ex ) {
            System.out.println(ex.getMessage());
        }
        DescriptionArea.setText( (String)table.getModel().getValueAt(row,2) );
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        TopPanel = new javax.swing.JPanel();
        TopRightPanel = new javax.swing.JPanel();
        OtherDateChooser = new com.toedter.calendar.JDateChooser();
        TopLeftPanel = new javax.swing.JPanel();
        InvoiceLB = new javax.swing.JLabel();
        InvoiceTF = new javax.swing.JTextField();
        BottomPanel = new javax.swing.JPanel();
        Cancel = new javax.swing.JButton();
        OK = new javax.swing.JButton();
        DummyLeftLabel = new javax.swing.JLabel();
        RightDummyLabel = new javax.swing.JLabel();
        TaxPanel = new javax.swing.JPanel();
        AmountLabel = new javax.swing.JLabel();
        AmountTF = new javax.swing.JTextField();
        DescriptionLabel = new javax.swing.JLabel();
        DescriptionArea = new javax.swing.JTextArea();
        TypeOtherLabel = new javax.swing.JLabel();

        jMenu1.setText("Menu");
        jMenuBar1.add(jMenu1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit Income Transaction Dialog");
        TopPanel.setLayout(new java.awt.BorderLayout());

        TopRightPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));

        TopRightPanel.add(OtherDateChooser);

        TopPanel.add(TopRightPanel, java.awt.BorderLayout.EAST);

        TopLeftPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));

        InvoiceLB.setText("Invoice : ");
        TopLeftPanel.add(InvoiceLB);

        InvoiceTF.setColumns(12);
        TopLeftPanel.add(InvoiceTF);

        TopPanel.add(TopLeftPanel, java.awt.BorderLayout.WEST);

        getContentPane().add(TopPanel, java.awt.BorderLayout.NORTH);

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

        DummyLeftLabel.setText("     ");
        getContentPane().add(DummyLeftLabel, java.awt.BorderLayout.WEST);

        RightDummyLabel.setText("     ");
        getContentPane().add(RightDummyLabel, java.awt.BorderLayout.EAST);

        TaxPanel.setLayout(new java.awt.GridBagLayout());

        AmountLabel.setText("Amount of Transactions :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        TaxPanel.add(AmountLabel, gridBagConstraints);

        AmountTF.setColumns(24);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        TaxPanel.add(AmountTF, gridBagConstraints);

        DescriptionLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        DescriptionLabel.setText("Description :");
        DescriptionLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        TaxPanel.add(DescriptionLabel, gridBagConstraints);

        DescriptionArea.setColumns(24);
        DescriptionArea.setLineWrap(true);
        DescriptionArea.setRows(5);
        DescriptionArea.setWrapStyleWord(true);
        DescriptionArea.setBorder(new javax.swing.border.EtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        TaxPanel.add(DescriptionArea, gridBagConstraints);

        TypeOtherLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TypeOtherLabel.setText("Other Income Transaction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        TaxPanel.add(TypeOtherLabel, gridBagConstraints);

        getContentPane().add(TaxPanel, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    private void OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKActionPerformed
        String invoice =  InvoiceTF.getText().trim();
        String description = DescriptionArea.getText();
        int amount = Integer.parseInt(AmountTF.getText());
        
        //edit other income transaction
        boolean result = tr.EditIncomeTransaction( table, 
                new java.sql.Date(OtherDateChooser.getDate().getTime()),
                invoice, description, amount );
        
        //error editing income transaction
        if(!result)
            JOptionPane.showMessageDialog(null,"Error editing income transaction data! See log file " +
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
                new IncomeTransactionDialog(new javax.swing.JFrame(), true, null, null).setVisible(true);
            }
        });
    }
    
    private javax.swing.JEditorPane transactionsEP;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AmountLabel;
    private javax.swing.JTextField AmountTF;
    private javax.swing.JPanel BottomPanel;
    private javax.swing.JButton Cancel;
    private javax.swing.JTextArea DescriptionArea;
    private javax.swing.JLabel DescriptionLabel;
    private javax.swing.JLabel DummyLeftLabel;
    private javax.swing.JLabel InvoiceLB;
    private javax.swing.JTextField InvoiceTF;
    private javax.swing.JButton OK;
    private com.toedter.calendar.JDateChooser OtherDateChooser;
    private javax.swing.JLabel RightDummyLabel;
    private javax.swing.JPanel TaxPanel;
    private javax.swing.JPanel TopLeftPanel;
    private javax.swing.JPanel TopPanel;
    private javax.swing.JPanel TopRightPanel;
    private javax.swing.JLabel TypeOtherLabel;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    // End of variables declaration//GEN-END:variables
    
}
