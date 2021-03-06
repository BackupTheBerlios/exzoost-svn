/*
 * PreferencesDialog.java
 *
 * Created on April 16, 2005, 1:05 AM
 */

package com.exzoost.gui.maingui;

import com.exzoost.gui.helper.GuiHelper;
import com.exzoost.xmlhandler.XMLHandler;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author  knight
 */
public class PreferencesDialog extends javax.swing.JDialog {
    
    /** Creates new form PreferencesDialog */
    public PreferencesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        GuiHelper.setOnCenter((Window)this);
        
        setUpSpinner();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        DummyTopLabel = new javax.swing.JLabel();
        LeftDummyLabel = new javax.swing.JLabel();
        RightDummyLabel = new javax.swing.JLabel();
        MainPanel = new javax.swing.JPanel();
        StartUpDialogPanel = new javax.swing.JPanel();
        DisplayItem = new javax.swing.JLabel();
        QuantitySpn = new javax.swing.JSpinner();
        DisplayChB = new javax.swing.JCheckBox();
        ThemePanel = new javax.swing.JPanel();
        ThemeCoB = new javax.swing.JComboBox();
        ExitConfirmationPanel = new javax.swing.JPanel();
        ExitConfirmationChB = new javax.swing.JCheckBox();
        InventoryListPanel = new javax.swing.JPanel();
        ItemDisplayLabel = new javax.swing.JLabel();
        LimitSpn = new javax.swing.JSpinner();
        BottomPanel = new javax.swing.JPanel();
        Cancel = new javax.swing.JButton();
        OK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Preferences Dialog");
        DummyTopLabel.setText("                ");
        getContentPane().add(DummyTopLabel, java.awt.BorderLayout.NORTH);

        LeftDummyLabel.setText("     ");
        getContentPane().add(LeftDummyLabel, java.awt.BorderLayout.WEST);

        RightDummyLabel.setText("     ");
        getContentPane().add(RightDummyLabel, java.awt.BorderLayout.EAST);

        MainPanel.setLayout(new javax.swing.BoxLayout(MainPanel, javax.swing.BoxLayout.Y_AXIS));

        StartUpDialogPanel.setLayout(new java.awt.GridBagLayout());

        StartUpDialogPanel.setBorder(new javax.swing.border.TitledBorder("Start Up Dialog"));
        DisplayItem.setText("Display Items With Quantity Below ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 0, 10);
        StartUpDialogPanel.add(DisplayItem, gridBagConstraints);

        QuantitySpn.setPreferredSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 20);
        StartUpDialogPanel.add(QuantitySpn, gridBagConstraints);

        DisplayChB.setSelected(true);
        DisplayChB.setText("Display Low Quantity Items List Dialog on Start Up");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 10);
        StartUpDialogPanel.add(DisplayChB, gridBagConstraints);

        MainPanel.add(StartUpDialogPanel);

        ThemePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 5));

        ThemePanel.setBorder(new javax.swing.border.TitledBorder("Theme"));
        ThemeCoB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Plastic", "Plastic3D", "PlasticXP", "Liquid", "Ocean", "Metouia", "Compiere", "Windows" }));
        ThemePanel.add(ThemeCoB);

        MainPanel.add(ThemePanel);

        ExitConfirmationPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 5));

        ExitConfirmationPanel.setBorder(new javax.swing.border.TitledBorder("Exit Confirmation"));
        ExitConfirmationChB.setSelected(true);
        ExitConfirmationChB.setText("Show Exit Confirmation Dialog");
        ExitConfirmationPanel.add(ExitConfirmationChB);

        MainPanel.add(ExitConfirmationPanel);

        InventoryListPanel.setLayout(new java.awt.GridBagLayout());

        InventoryListPanel.setBorder(new javax.swing.border.TitledBorder("Inventory List"));
        ItemDisplayLabel.setText("How Many Items Should I Display Per Page ? ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 10, 0);
        InventoryListPanel.add(ItemDisplayLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 20);
        InventoryListPanel.add(LimitSpn, gridBagConstraints);

        MainPanel.add(InventoryListPanel);

        getContentPane().add(MainPanel, java.awt.BorderLayout.CENTER);

        BottomPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        Cancel.setText("Cancel");
        Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelActionPerformed(evt);
            }
        });

        BottomPanel.add(Cancel);

        OK.setText("OK");
        OK.setMaximumSize(new java.awt.Dimension(75, 25));
        OK.setPreferredSize(new java.awt.Dimension(75, 25));
        OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKActionPerformed(evt);
            }
        });

        BottomPanel.add(OK);

        getContentPane().add(BottomPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }//GEN-END:initComponents

    private void OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKActionPerformed
        int limit = ((Integer)QuantitySpn.getValue()).intValue();
        boolean display = DisplayChB.isSelected();
        String theme = (String)ThemeCoB.getSelectedItem();
        boolean exitconf = ExitConfirmationChB.isSelected();
        int displaylimit = ((Integer)LimitSpn.getValue()).intValue();
        
        XMLHandler xmlhandler = new XMLHandler();
        
        xmlhandler.setDisplayStartupDialog(display);
        xmlhandler.setLimitQuantityItemsList(limit);
        xmlhandler.setItemDisplayLimit(displaylimit);
        xmlhandler.setUpLookAndFeel(theme);
        xmlhandler.setExitConfirmation(exitconf);
        
        dispose();
    }//GEN-LAST:event_OKActionPerformed

    private void CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelActionPerformed
        dispose();
    }//GEN-LAST:event_CancelActionPerformed
    
    private void setUpSpinner() {
        SpinnerModel model =
        new SpinnerNumberModel(10, //initial value
                               1, //min
                               1000, //max
                               1);
        
        QuantitySpn.setModel(model);
        
        model = 
        new SpinnerNumberModel(32, //initial value
                       5, //min
                       999, //max
                       1);
        
        LimitSpn.setModel(model);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PreferencesDialog(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BottomPanel;
    private javax.swing.JButton Cancel;
    private javax.swing.JCheckBox DisplayChB;
    private javax.swing.JLabel DisplayItem;
    private javax.swing.JLabel DummyTopLabel;
    private javax.swing.JCheckBox ExitConfirmationChB;
    private javax.swing.JPanel ExitConfirmationPanel;
    private javax.swing.JPanel InventoryListPanel;
    private javax.swing.JLabel ItemDisplayLabel;
    private javax.swing.JLabel LeftDummyLabel;
    private javax.swing.JSpinner LimitSpn;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JButton OK;
    private javax.swing.JSpinner QuantitySpn;
    private javax.swing.JLabel RightDummyLabel;
    private javax.swing.JPanel StartUpDialogPanel;
    private javax.swing.JComboBox ThemeCoB;
    private javax.swing.JPanel ThemePanel;
    // End of variables declaration//GEN-END:variables
    
}
