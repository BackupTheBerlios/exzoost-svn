package com.exzoost.gui.salesman;
/*
 * AddSellerDialog.java
 *
 * Created on December 17, 2004, 1:59 PM
 */

import com.exzoost.gui.helper.GuiHelper;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.sql.Date;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import com.exzoost.database.SalesmanDB;
import java.sql.Connection;
/**
 *
 * @author  knight
 */
public class EditSalesmanDialog extends javax.swing.JDialog {
    private Connection conn;
    private NumberFormat nf;
    /** Creates new form AddSellerDialog */
    public EditSalesmanDialog(java.awt.Frame parent, boolean modal, 
            JTable table, Connection conn ) {
        super(parent, modal);
        
        //must get the conn reference to do the operation
        this.conn = conn;
        
        //setup the gui
        initComponents();
        
        GuiHelper.setOnCenter((Window)this);
        
        //format the currency
        nf = NumberFormat.getCurrencyInstance( new Locale("id","id"));
        
        //must get the table reference
        this.table = table;
        
        //the row to be udpated
        int row = table.getSelectedRow();
        
        //set the dialog text field
        numberTF.setText((String)table.getValueAt(row,0));
        nameTF.setText((String)table.getValueAt(row,1));
        addressTA.setText((String)table.getValueAt(row,2));
        phoneTF.setText((String)table.getValueAt(row,3));
        
        try {
            salaryTF.setText( (nf.parse((String)table.getValueAt(row,4))).toString() );
        }
        catch( java.text.ParseException ex ) {
            System.out.println(ex.getMessage());
        }
        
        DateFormat df = DateFormat.getDateInstance(
                        DateFormat.LONG, Locale.ENGLISH );
        try {
            birthDateChooser.setDate( df.parse( (String)table.getValueAt(row,5) ) );
        }
        catch( java.text.ParseException e ) {
            e.printStackTrace();
        }
        
        birthPlaceTF.setText((String)table.getValueAt(row,6));
        commentTA.setText((String)table.getModel().getValueAt(row,7));
        
        //setup default button
        getRootPane().setDefaultButton(OK);
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
        DummyTopLabel = new javax.swing.JLabel();
        LeftDummyLabel = new javax.swing.JLabel();
        RightDummyLabel = new javax.swing.JLabel();
        SellerPanel = new javax.swing.JPanel();
        NumberLabel = new javax.swing.JLabel();
        numberTF = new javax.swing.JTextField();
        NameLabel = new javax.swing.JLabel();
        nameTF = new javax.swing.JTextField();
        AddressLabel = new javax.swing.JLabel();
        PhoneLabel = new javax.swing.JLabel();
        phoneTF = new javax.swing.JTextField();
        BirthDateLabel = new javax.swing.JLabel();
        birthDateChooser = new com.toedter.calendar.JDateChooser();
        BirthPlaceLabel = new javax.swing.JLabel();
        birthPlaceTF = new javax.swing.JTextField();
        addressTA = new javax.swing.JTextArea();
        SalaryLabel = new javax.swing.JLabel();
        salaryTF = new javax.swing.JTextField();
        CommentLabel = new javax.swing.JLabel();
        commentTA = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit Salesman");
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

        SellerPanel.setLayout(new java.awt.GridBagLayout());

        SellerPanel.setBorder(new javax.swing.border.TitledBorder(null, "Salesman", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11)));
        NumberLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        NumberLabel.setText("* Code Number : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        SellerPanel.add(NumberLabel, gridBagConstraints);

        numberTF.setColumns(12);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        SellerPanel.add(numberTF, gridBagConstraints);

        NameLabel.setText("* Name : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        SellerPanel.add(NameLabel, gridBagConstraints);

        nameTF.setColumns(12);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        SellerPanel.add(nameTF, gridBagConstraints);

        AddressLabel.setText("* Address : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        SellerPanel.add(AddressLabel, gridBagConstraints);

        PhoneLabel.setText("Phone Number : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        SellerPanel.add(PhoneLabel, gridBagConstraints);

        phoneTF.setColumns(12);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        SellerPanel.add(phoneTF, gridBagConstraints);

        BirthDateLabel.setText("* Birth Date : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        SellerPanel.add(BirthDateLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        SellerPanel.add(birthDateChooser, gridBagConstraints);

        BirthPlaceLabel.setText("* Birth Place : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        SellerPanel.add(BirthPlaceLabel, gridBagConstraints);

        birthPlaceTF.setColumns(12);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        SellerPanel.add(birthPlaceTF, gridBagConstraints);

        addressTA.setColumns(12);
        addressTA.setLineWrap(true);
        addressTA.setRows(4);
        addressTA.setTabSize(4);
        addressTA.setWrapStyleWord(true);
        addressTA.setBorder(new javax.swing.border.EtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        SellerPanel.add(addressTA, gridBagConstraints);

        SalaryLabel.setText("Salary : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        SellerPanel.add(SalaryLabel, gridBagConstraints);

        salaryTF.setColumns(12);
        salaryTF.setFont(new java.awt.Font("Dialog", 1, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        SellerPanel.add(salaryTF, gridBagConstraints);

        CommentLabel.setText("Comment : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        SellerPanel.add(CommentLabel, gridBagConstraints);

        commentTA.setColumns(16);
        commentTA.setLineWrap(true);
        commentTA.setRows(8);
        commentTA.setTabSize(4);
        commentTA.setWrapStyleWord(true);
        commentTA.setBorder(new javax.swing.border.EtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        SellerPanel.add(commentTA, gridBagConstraints);

        getContentPane().add(SellerPanel, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    private void OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKActionPerformed
        //get the information from dialog text field
        String name = nameTF.getText().trim();
        String address = addressTA.getText().trim();
        String birthplace = birthPlaceTF.getText().trim();
        String phone = phoneTF.getText().trim();
        String salary = salaryTF.getText().trim();
        String number = numberTF.getText().trim();
        String comment = commentTA.getText();
        
        
        //the number field must not be null
        if(number.equals("")) {
            JOptionPane.showMessageDialog(null,"Please insert the Number Text Field!",
                    "Warning", JOptionPane.WARNING_MESSAGE );
            numberTF.requestFocusInWindow();
            return;
        }
        
        //the name field must not be null
        if(name.equals("")) {
            JOptionPane.showMessageDialog(null,"Please insert the Name Text Field!", 
                    "Warning", JOptionPane.WARNING_MESSAGE );
            nameTF.requestFocusInWindow();
            return;
        }
        
        //the address must not be null
        if(address.equals("")) {
            JOptionPane.showMessageDialog(null,"Please insert the Address Text Field!", 
                    "Warning", JOptionPane.WARNING_MESSAGE );
            addressTA.requestFocusInWindow();
            return;
        }
        
        //the birth place must not be null
        if(birthplace.equals("")) {
            JOptionPane.showMessageDialog(null,"Please insert the Birth Place Text Field!",
                    "Warning", JOptionPane.WARNING_MESSAGE );
            birthPlaceTF.requestFocusInWindow();
            return;
        }
                
        //salary text field can be null
        if(salary.equals("")) 
            salary = "null";
        
        //must use the salesman class
        SalesmanDB st = new SalesmanDB( conn );
        boolean result = st.updateSalesman( table, number, name, address, phone, 
                new Date(birthDateChooser.getDate().getTime()), birthplace, comment, salary );
        
        //error editing salesman
        if(!result)
            JOptionPane.showMessageDialog(null,"Error editing salesman data! See log file " +
                    "for detail!", "Warning", JOptionPane.WARNING_MESSAGE );
        
        //free the resource
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
                new EditSalesmanDialog(new javax.swing.JFrame(), true, null, 
                        null).setVisible(true);
            }
        });
    }
    
    private javax.swing.JTable table;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AddressLabel;
    private javax.swing.JLabel BirthDateLabel;
    private javax.swing.JLabel BirthPlaceLabel;
    private javax.swing.JPanel BottomPanel;
    private javax.swing.JButton Cancel;
    private javax.swing.JLabel CommentLabel;
    private javax.swing.JLabel DummyTopLabel;
    private javax.swing.JLabel LeftDummyLabel;
    private javax.swing.JLabel NameLabel;
    private javax.swing.JLabel NumberLabel;
    private javax.swing.JButton OK;
    private javax.swing.JLabel PhoneLabel;
    private javax.swing.JLabel RightDummyLabel;
    private javax.swing.JLabel SalaryLabel;
    private javax.swing.JPanel SellerPanel;
    private javax.swing.JTextArea addressTA;
    private com.toedter.calendar.JDateChooser birthDateChooser;
    private javax.swing.JTextField birthPlaceTF;
    private javax.swing.JTextArea commentTA;
    private javax.swing.JTextField nameTF;
    private javax.swing.JTextField numberTF;
    private javax.swing.JTextField phoneTF;
    private javax.swing.JTextField salaryTF;
    // End of variables declaration//GEN-END:variables
    
}