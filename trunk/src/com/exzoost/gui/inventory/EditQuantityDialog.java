/*
 * AddQuantityDialog.java
 *
 * Created on January 31, 2007, 12:02 PM
 */

package com.exzoost.gui.inventory;

import com.exzoost.database.InventoryDB;
import com.exzoost.gui.helper.GuiHelper;
import java.awt.Window;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import javax.swing.JTable;

/**
 *
 * @author  knight
 */
public class EditQuantityDialog extends javax.swing.JDialog {
    private int itemindex;
    private int warehouseindex;
    private InventoryDB st;
    private String date;
    private String quantity;
    /** Creates new form AddQuantityDialog */
    public EditQuantityDialog(java.awt.Frame parent, boolean modal, InventoryDB st, 
            int itemindex, int warehouseindex, String date) {
        super(parent, modal);
        this.itemindex = itemindex;
        this.warehouseindex = warehouseindex;
        this.date = date;
        this.st = st;
        initComponents();
        if(date!=null) {
            DateC.setText("<html><b>" + date + "</b></html>");
            ExpiredCheckBox.setVisible(false);
        }
        else {
            DateC.setVisible(false);
            DateL.setVisible(false);
            ExpiredCheckBox.setSelected(true);
            ExpiredCheckBox.setEnabled(false);
        }
        GuiHelper.setOnCenter((Window)this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        OKB = new javax.swing.JButton();
        CancelB = new javax.swing.JButton();
        DateL = new javax.swing.JLabel();
        QuantityL = new javax.swing.JLabel();
        QuantityTF = new javax.swing.JTextField();
        ExpiredCheckBox = new javax.swing.JCheckBox();
        DateC = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Quantity and Expired Date");
        setAlwaysOnTop(true);
        setModal(true);
        setName("AddQuantityD");
        setResizable(false);
        OKB.setText("OK");
        OKB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKBActionPerformed(evt);
            }
        });

        CancelB.setText("Cancel");
        CancelB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelBActionPerformed(evt);
            }
        });

        DateL.setText("Expired Date:");

        QuantityL.setText("Quantity:");

        QuantityTF.setColumns(5);

        ExpiredCheckBox.setText("Not Expired");
        ExpiredCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ExpiredCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        DateC.setText("jLabel1");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(107, Short.MAX_VALUE)
                .add(CancelB)
                .add(23, 23, 23)
                .add(OKB)
                .add(35, 35, 35))
            .add(layout.createSequentialGroup()
                .add(57, 57, 57)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(DateC)
                    .add(ExpiredCheckBox)
                    .add(QuantityL)
                    .add(QuantityTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(DateL))
                .addContainerGap(147, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(46, 46, 46)
                .add(DateL)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(DateC)
                .add(19, 19, 19)
                .add(ExpiredCheckBox)
                .add(18, 18, 18)
                .add(QuantityL)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(QuantityTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 26, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(OKB)
                    .add(CancelB))
                .add(22, 22, 22))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OKBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKBActionPerformed
        quantity = QuantityTF.getText();
        if(!st.editQuantityAndExpiredDate(warehouseindex, itemindex, date, quantity))
          quantity = "-1"; 
        dispose();
    }//GEN-LAST:event_OKBActionPerformed

    private void CancelBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelBActionPerformed
        dispose();
        quantity = "-2";
    }//GEN-LAST:event_CancelBActionPerformed
    
    public String getReturnValue() {
        return quantity;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CancelB;
    private javax.swing.JLabel DateC;
    private javax.swing.JLabel DateL;
    private javax.swing.JCheckBox ExpiredCheckBox;
    private javax.swing.JButton OKB;
    private javax.swing.JLabel QuantityL;
    private javax.swing.JTextField QuantityTF;
    // End of variables declaration//GEN-END:variables
    
}
