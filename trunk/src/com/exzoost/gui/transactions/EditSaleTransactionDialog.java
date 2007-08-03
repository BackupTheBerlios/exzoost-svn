/*
 * SaleStuffDialog.java
 *
 * Created on November 11, 2004, 6:10 PM
 */

package com.exzoost.gui.transactions;

import com.exzoost.database.CustomerDB;
import com.exzoost.database.InventoryDB;
import com.exzoost.database.SalesmanDB;
import com.exzoost.database.WarehouseDB;
import com.exzoost.gui.htmleditor.HTMLEditorManipulation;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JFormattedTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.util.Properties;
import java.util.Locale;
import java.text.NumberFormat;
import com.exzoost.database.Transaction;

/**
 *
 * @author  knight
 */
public class EditSaleTransactionDialog extends javax.swing.JDialog {
    private Connection conn;
    private Transaction tr;
    private ResultSet uprs;
    private Statement stmt;
    private InventoryDB inventory;
    private SalesmanDB salesmandb;
    private CustomerDB customerdb;
    private DefaultTableModel model;
    private String invoice;
    private JTextField celleditor = new JTextField();
    TableColumn[] columnarray;
        
    /** Creates new form SaleStuffDialog */
    public EditSaleTransactionDialog(java.awt.Frame parent, boolean modal, String invoice,
            Connection conn ) {
        super(parent, modal);
        this.transactionsEP = transactionsEP;
        this.conn = conn;
        this.invoice = invoice;
        celleditor.setHorizontalAlignment(JTextField.RIGHT);
        tr = new Transaction( conn );
        Locale IndonesianLocale = new Locale("id","id");
        IndonesianFormat = NumberFormat.getCurrencyInstance(IndonesianLocale);
        
        //setup the gui
        initComponents();
        
        inventory = new InventoryDB( conn );
        salesmandb = new SalesmanDB( conn );
        customerdb = new CustomerDB( conn );
        
        //initialize the combobox
        inventory.initializeComboBox(CustomerCoB, "customer");
        inventory.initializeComboBox(SalesmanCoB, "salesman");
        inventory.initializeComboBox(WarehouseCoB, "warehouse_only");
        
        //maximize it
        //set the dialog in center of the screen and set the size
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize( new java.awt.Dimension( dim.width, dim.height - 100) );
        
        //setup the dialog
        InvoiceTF.setText(invoice);
        tr.SetUpEditSaleTransactionDialog(invoice, SaleTransactionDateChooser, DiscountTF, 
                CustomerCoB, CustomerChB, SalesmanCoB, SalesmanChB, SendingPaidCoB, CommisionTF, 
                SenderTF, ShipmentCostTF, CommentTA );
        tr.initializeTransferTable(TransferTB, "sale", invoice,
                (String)WarehouseCoB.getSelectedItem() );
        
        //setup editable column
        TableColumn tc = TransferTB.getColumnModel().getColumn(4);
        tc.setCellEditor( new WeGave() );
        tc = TransferTB.getColumnModel().getColumn(5);
        tc.setCellEditor( new CustomerReturn() );
        
    }
    
    class WeGave extends DefaultCellEditor {
        WeGave() {
            super( celleditor );
        }
        
        public boolean stopCellEditing() {
            int row = TransferTB.getSelectedRow();
            
            //empty the CustomerReturn column
            TransferTB.getModel().setValueAt( 0, row, 5 );
            
            //if the cell is blank, make it 0
            if( ((JTextField)getComponent()).getText().trim().equals("") ) {
                ((JTextField)getComponent()).setText("0");
                return super.stopCellEditing();
            }
            
            //that name does not exist in inventory database
            if( Integer.parseInt(((JTextField)getComponent()).getText()) < 0 ) {
                ((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
                getComponent().requestFocusInWindow();
                ((JTextField)getComponent()).selectAll();
                JOptionPane.showMessageDialog(
                        null,
                        "Don't insert negative value!",
                        "Warning", JOptionPane.WARNING_MESSAGE );
                return false;
            }
                        
            return super.stopCellEditing();
        }
        
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column ) {
            Component c = super.getTableCellEditorComponent( table, value, isSelected, row, column );
            ((JComponent)c).setBorder( new LineBorder(Color.blue));
            return c;
        }
    }
    
    class CustomerReturn extends DefaultCellEditor {
        CustomerReturn() {
            super( celleditor );
        }
        
        public boolean stopCellEditing() {
            int row = TransferTB.getSelectedRow();
            
            //empty the WeGave column
            TransferTB.getModel().setValueAt( 0, row, 4 );
            
            //if the cell is blank, make it 0
            if( ((JTextField)getComponent()).getText().trim().equals("") ) {
                ((JTextField)getComponent()).setText("0");
                return super.stopCellEditing();
            }
            
            //that name does not exist in inventory database
            if( Integer.parseInt( (String)getCellEditorValue() ) < 0 ) {
                ((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
                getComponent().requestFocusInWindow();
                ((JTextField)getComponent()).selectAll();
                JOptionPane.showMessageDialog(
                        null,
                        "Don't insert negative value!",
                        "Warning", JOptionPane.WARNING_MESSAGE );
                return false;
            }
            
            return super.stopCellEditing();
        }
        
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column ) {
            Component c = super.getTableCellEditorComponent( table, value, isSelected, row, column );
            ((JComponent)c).setBorder( new LineBorder(Color.blue));
            return c;
        }
    }
        
    private String removeZeroFront( String arg ) {
        int index = 0;
        char[] c = arg.toLowerCase().toCharArray();
        
        for( int i=0; i<c.length; i++ ) {
            if(c[i]!='0') {
                System.out.println(arg.substring( i, arg.length() ));
                return arg.substring( i, arg.length() );
            }
        }
        return "";
    }
    
    private boolean CheckingTable() {
        
        for( int i=0; i<TransferTB.getRowCount()&&TransferTB.getModel().getValueAt(i,0)!=null; i++ ) {
            
            try {
                //if we gave more than we must give
                if( ((Integer)TransferTB.getModel().getValueAt(i,4)).intValue() >
                       ((Integer)TransferTB.getModel().getValueAt(i,2)).intValue() ) {
                    JOptionPane.showMessageDialog( null, "You gave too much to customer!", 
                            "Warning", JOptionPane.WARNING_MESSAGE );
                    return false;
                }
            }
            catch( java.lang.ClassCastException e ) {
                if( Integer.parseInt((String)TransferTB.getModel().getValueAt(i,4)) >
                       ((Integer)TransferTB.getModel().getValueAt(i,2)).intValue() ) {
                    JOptionPane.showMessageDialog( null, "You gave too much to customer!", 
                            "Warning", JOptionPane.WARNING_MESSAGE );
                    return false;
                }
            }
            
            try {
                //if customer return more than he has
                if( ((Integer)TransferTB.getModel().getValueAt(i,5)).intValue() >
                       ((Integer)TransferTB.getModel().getValueAt(i,3)).intValue() ) {
                    JOptionPane.showMessageDialog( null, "The customer returned too much to you!", 
                            "Warning", JOptionPane.WARNING_MESSAGE );
                    return false;
                }
            }
            catch( java.lang.ClassCastException e ) {
                if( Integer.parseInt((String)TransferTB.getModel().getValueAt(i,5)) >
                       ((Integer)TransferTB.getModel().getValueAt(i,3)).intValue() ) {
                    JOptionPane.showMessageDialog( null, "The customer returned too much to you!", 
                            "Warning", JOptionPane.WARNING_MESSAGE );
                    return false;
                }
            }
            
            try {
                //if we gave more than supply in warehouse
                if( ((Integer)TransferTB.getModel().getValueAt(i,4)).intValue() >
                       ((Integer)TransferTB.getModel().getValueAt(i,6)).intValue() ) {
                    JOptionPane.showMessageDialog( null, "The supply in warehouse is not enough!", 
                            "Warning", JOptionPane.WARNING_MESSAGE );
                    return false;
                }
            }
            catch( java.lang.ClassCastException e ) {
                if( Integer.parseInt((String)TransferTB.getModel().getValueAt(i,4)) >
                       ((Integer)TransferTB.getModel().getValueAt(i,6)).intValue() ) {
                    JOptionPane.showMessageDialog( null, "The supply in warehouse is not enough!!", 
                            "Warning", JOptionPane.WARNING_MESSAGE );
                    return false;
                }
            }
        }
        return true;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        topJP = new javax.swing.JPanel();
        TopLeftPanel = new javax.swing.JPanel();
        InvoiceLB = new javax.swing.JLabel();
        InvoiceTF = new javax.swing.JTextField();
        TopRightPanel = new javax.swing.JPanel();
        SaleTransactionDateChooser = new com.toedter.calendar.JDateChooser();
        rightDummyLabel = new javax.swing.JLabel();
        leftDummyLabel = new javax.swing.JLabel();
        bottomJP = new javax.swing.JPanel();
        bottomButtonJP = new javax.swing.JPanel();
        Cancel = new javax.swing.JButton();
        OK = new javax.swing.JButton();
        DetailTransactionJP = new javax.swing.JPanel();
        MainDetailTransactionJP = new javax.swing.JPanel();
        DiscountTransactionLB = new javax.swing.JLabel();
        CustomerChB = new javax.swing.JCheckBox();
        CustomerCoB = new javax.swing.JComboBox();
        SalesmanChB = new javax.swing.JCheckBox();
        SalesmanCoB = new javax.swing.JComboBox();
        CommisionTF = new javax.swing.JTextField();
        CommisionLB = new javax.swing.JLabel();
        SupplyLB = new javax.swing.JLabel();
        WarehouseCoB = new javax.swing.JComboBox();
        DiscountTF = new javax.swing.JTextField();
        CommentLB = new javax.swing.JLabel();
        CommentTA = new javax.swing.JTextArea();
        SenderTF = new javax.swing.JTextField();
        ShipmentLB = new javax.swing.JLabel();
        ShipmentCostTF = new javax.swing.JTextField();
        SendingPaidCoB = new javax.swing.JComboBox();
        PaidByLabel = new javax.swing.JLabel();
        SenderChB = new javax.swing.JCheckBox();
        TotalTransactionJP = new javax.swing.JPanel();
        mainSP = new javax.swing.JScrollPane();
        TransferTB = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit Sale Dialog");
        setModal(true);
        topJP.setLayout(new java.awt.BorderLayout());

        TopLeftPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));

        InvoiceLB.setText("Invoice Number : ");
        TopLeftPanel.add(InvoiceLB);

        InvoiceTF.setColumns(12);
        InvoiceTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                InvoiceTFFocusLost(evt);
            }
        });

        TopLeftPanel.add(InvoiceTF);

        topJP.add(TopLeftPanel, java.awt.BorderLayout.WEST);

        TopRightPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));

        SaleTransactionDateChooser.setPreferredSize(new java.awt.Dimension(160, 20));
        TopRightPanel.add(SaleTransactionDateChooser);

        topJP.add(TopRightPanel, java.awt.BorderLayout.EAST);

        getContentPane().add(topJP, java.awt.BorderLayout.NORTH);

        rightDummyLabel.setText("          ");
        getContentPane().add(rightDummyLabel, java.awt.BorderLayout.EAST);

        leftDummyLabel.setText("          ");
        getContentPane().add(leftDummyLabel, java.awt.BorderLayout.WEST);

        bottomJP.setLayout(new java.awt.BorderLayout());

        bottomButtonJP.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        Cancel.setText("Cancel");
        Cancel.setMaximumSize(new java.awt.Dimension(84, 25));
        Cancel.setMinimumSize(new java.awt.Dimension(84, 25));
        Cancel.setPreferredSize(new java.awt.Dimension(84, 25));
        Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelActionPerformed(evt);
            }
        });

        bottomButtonJP.add(Cancel);

        OK.setText("OK");
        OK.setMaximumSize(new java.awt.Dimension(84, 25));
        OK.setMinimumSize(new java.awt.Dimension(84, 25));
        OK.setPreferredSize(new java.awt.Dimension(84, 25));
        OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKActionPerformed(evt);
            }
        });

        bottomButtonJP.add(OK);

        bottomJP.add(bottomButtonJP, java.awt.BorderLayout.SOUTH);

        DetailTransactionJP.setLayout(new java.awt.BorderLayout());

        MainDetailTransactionJP.setLayout(new java.awt.GridBagLayout());

        DiscountTransactionLB.setText("Discount for Transaction :  ");
        DiscountTransactionLB.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainDetailTransactionJP.add(DiscountTransactionLB, gridBagConstraints);

        CustomerChB.setText("Customer : ");
        CustomerChB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        CustomerChB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CustomerChBActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainDetailTransactionJP.add(CustomerChB, gridBagConstraints);

        CustomerCoB.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainDetailTransactionJP.add(CustomerCoB, gridBagConstraints);

        SalesmanChB.setText("Salesman : ");
        SalesmanChB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        SalesmanChB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SalesmanChBActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainDetailTransactionJP.add(SalesmanChB, gridBagConstraints);

        SalesmanCoB.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainDetailTransactionJP.add(SalesmanCoB, gridBagConstraints);

        CommisionTF.setColumns(12);
        CommisionTF.setEnabled(false);
        CommisionTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                NumberFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                NumberFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        MainDetailTransactionJP.add(CommisionTF, gridBagConstraints);

        CommisionLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        CommisionLB.setText("Commision : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        MainDetailTransactionJP.add(CommisionLB, gridBagConstraints);

        SupplyLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        SupplyLB.setText("Using Warehouse : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainDetailTransactionJP.add(SupplyLB, gridBagConstraints);

        WarehouseCoB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WarehouseCoBActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainDetailTransactionJP.add(WarehouseCoB, gridBagConstraints);

        DiscountTF.setColumns(12);
        DiscountTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                NumberFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                DiscountTFFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainDetailTransactionJP.add(DiscountTF, gridBagConstraints);

        CommentLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        CommentLB.setText("Comment : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainDetailTransactionJP.add(CommentLB, gridBagConstraints);

        CommentTA.setColumns(12);
        CommentTA.setLineWrap(true);
        CommentTA.setRows(6);
        CommentTA.setTabSize(4);
        CommentTA.setWrapStyleWord(true);
        CommentTA.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainDetailTransactionJP.add(CommentTA, gridBagConstraints);

        SenderTF.setColumns(12);
        SenderTF.setEnabled(false);
        SenderTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                SenderTFFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainDetailTransactionJP.add(SenderTF, gridBagConstraints);

        ShipmentLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ShipmentLB.setText("Shipment Cost : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        MainDetailTransactionJP.add(ShipmentLB, gridBagConstraints);

        ShipmentCostTF.setColumns(12);
        ShipmentCostTF.setEnabled(false);
        ShipmentCostTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                NumberFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                NumberFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        MainDetailTransactionJP.add(ShipmentCostTF, gridBagConstraints);

        SendingPaidCoB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Customer", "Company" }));
        SendingPaidCoB.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        MainDetailTransactionJP.add(SendingPaidCoB, gridBagConstraints);

        PaidByLabel.setText("Paid by : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        MainDetailTransactionJP.add(PaidByLabel, gridBagConstraints);

        SenderChB.setText("Sender : ");
        SenderChB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        SenderChB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SenderChBActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainDetailTransactionJP.add(SenderChB, gridBagConstraints);

        DetailTransactionJP.add(MainDetailTransactionJP, java.awt.BorderLayout.CENTER);

        TotalTransactionJP.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 100, 5));

        DetailTransactionJP.add(TotalTransactionJP, java.awt.BorderLayout.NORTH);

        bottomJP.add(DetailTransactionJP, java.awt.BorderLayout.CENTER);

        getContentPane().add(bottomJP, java.awt.BorderLayout.SOUTH);

        mainSP.setPreferredSize(new java.awt.Dimension(468, 303));
        TransferTB.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Item Name", "We Have Given", "We Must Give", "Total Items", "We Give", "Customer Return", "Supply In Warehouse"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TransferTB.setColumnSelectionAllowed(true);
        TransferTB.setSelectionBackground(new java.awt.Color(0, 204, 153));
        TransferTB.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        TransferTB.getTableHeader().setReorderingAllowed(false);

        //set the listener
        TransferTB.getModel().addTableModelListener(
            new TableModelListener() {

                //if user is changing inside jtable
                public void tableChanged(TableModelEvent tme) {

                    final int column = tme.getColumn();
                    final int row = tme.getFirstRow();

                    //total items in transaction
                    int totaltrans = 0;

                    //the new value
                    int changed = 0;

                    //have paid or have accepted
                    if(column==1) {
                        //manipulate the other table
                        if(TransferTB.getModel().getValueAt(row,3)!=null)
                        totaltrans = ((Integer)TransferTB.getModel().getValueAt(row,3)).intValue();
                        if(TransferTB.getModel().getValueAt(row,column)!=null)
                        changed = ((Integer)TransferTB.getModel().getValueAt(row,1)).intValue();
                        TransferTB.getModel().setValueAt( totaltrans-changed, row, column+1 );
                    }

                }
            });

            mainSP.setViewportView(TransferTB);

            getContentPane().add(mainSP, java.awt.BorderLayout.CENTER);

            pack();
        }// </editor-fold>//GEN-END:initComponents

    private void SenderChBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SenderChBActionPerformed
        if(SenderChB.isSelected()) {
            SenderTF.setEnabled(true);
            ShipmentCostTF.setEnabled(true);
            SendingPaidCoB.setEnabled(true);
        }
        else {
            SenderTF.setEnabled(false);
            ShipmentCostTF.setEnabled(false);
            SendingPaidCoB.setEnabled(false);
        }
    }//GEN-LAST:event_SenderChBActionPerformed

    private void SenderTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_SenderTFFocusLost
        if(SenderTF.getText().trim().equals("")) {
            SendingPaidCoB.setEnabled(false);
        }
        else {
            SendingPaidCoB.setEnabled(true);
        }
    }//GEN-LAST:event_SenderTFFocusLost

    private void InvoiceTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_InvoiceTFFocusLost
        String tempinvoice = InvoiceTF.getText().trim();
        InvoiceTF.setText( tempinvoice );
    }//GEN-LAST:event_InvoiceTFFocusLost

    private void DiscountTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_DiscountTFFocusLost
        JTextField textfield = (JTextField)evt.getSource();
        
        try {
            //default is zero
            if(textfield.getText().trim().equals(""))
                textfield.setText( IndonesianFormat.format(0) );
            else 
                textfield.setText( 
                        IndonesianFormat.format(
                        Integer.parseInt(textfield.getText()) ) );
        }
        catch( java.lang.NumberFormatException cep ) {
            System.out.println(cep.getMessage());
        }
                
    }//GEN-LAST:event_DiscountTFFocusLost

    private void WarehouseCoBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WarehouseCoBActionPerformed
        tr.initializeTransferTable(TransferTB, "sale", invoice,
                (String)WarehouseCoB.getSelectedItem() );
    }//GEN-LAST:event_WarehouseCoBActionPerformed

    private void NumberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_NumberFocusLost
        JTextField textfield = (JTextField)evt.getSource();
        
        try {
            //default is zero
            if(textfield.getText().trim().equals(""))
                textfield.setText( IndonesianFormat.format( 0 ) );
            else
                textfield.setText( 
                        IndonesianFormat.format(
                        Integer.parseInt(textfield.getText()) ) );
        }
        catch( java.lang.NumberFormatException cep ) {
            System.out.println(cep.getMessage());
        }
    }//GEN-LAST:event_NumberFocusLost

    private void NumberFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_NumberFocusGained
        JTextField textfield = (JTextField)evt.getSource();
        textfield.selectAll();
    }//GEN-LAST:event_NumberFocusGained

    private void SalesmanChBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SalesmanChBActionPerformed
        if(SalesmanChB.isSelected()) {
            SalesmanCoB.setEnabled(true);
            CommisionTF.setEnabled(true);
        }
        else {
            SalesmanCoB.setEnabled(false);
            CommisionTF.setEnabled(false);
        }
    }//GEN-LAST:event_SalesmanChBActionPerformed

    private void CustomerChBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustomerChBActionPerformed
        if(CustomerChB.isSelected()) {
            CustomerCoB.setEnabled(true);
        }
        else {
            CustomerCoB.setEnabled(false);
        }
    }//GEN-LAST:event_CustomerChBActionPerformed

    private void OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKActionPerformed
        //invoice
        String newinvoice = InvoiceTF.getText().trim();
        
        //date
        java.sql.Date trdate = new java.sql.Date(SaleTransactionDateChooser.getDate().getTime());
        
        //transaction discount
        int trdiscount = 0;
        
        try {
            if(!DiscountTF.getText().trim().equals(""))
                trdiscount = ((Long)IndonesianFormat.parse( DiscountTF.getText().trim() )).intValue();
        }
        catch( java.text.ParseException exc ) {
            System.out.println( exc.getMessage() );
        }
        
        //customer
        String customer = "";
        String customerid = "";
        if(CustomerChB.isSelected()) {
            customer = ((String)CustomerCoB.getSelectedItem()).toLowerCase();
            customerid = new Integer( customerdb.getIndex(customer) ).toString();
        }
        else {
            customerid = "null";
        }
        
        //who gonna paid the sending price????
        String sending_price = "";
        if( ((String)SendingPaidCoB.getSelectedItem()).equals("Customer") )
            sending_price = "pembeli";
        else
            sending_price = "penjual";
        
        //salesman and stuff
        String salesman = "";
        String salesmanid = "";
        int commision = 0;
        if(SalesmanChB.isSelected()) {
            salesman = ((String)SalesmanCoB.getSelectedItem()).toLowerCase();
            salesman = salesman.substring(2, salesman.indexOf(")") - 1 );
            salesmanid = new Integer( salesmandb.getIndex(salesman) ).toString();
            try {
                if(!CommisionTF.getText().trim().equals(""))
                    commision = ((Long)IndonesianFormat.parse( CommisionTF.getText().trim() )).intValue();
            }
            catch( java.text.ParseException exc ) {
                System.out.println( exc.getMessage() );
            }   
        }
        else {
            salesmanid = "null";
        }
        
        //sender
        String sender = "";
        int shipmentcost = 0;
        if(SenderTF.getText().trim().equals("")) {
            sender = null;
        }
        else {
            sender = SenderTF.getText().trim().toLowerCase();
            try {
                if(!ShipmentCostTF.getText().equals(""))
                    shipmentcost = 
                            ((Long)IndonesianFormat.parse(ShipmentCostTF.getText())).intValue();
            }
            catch( java.text.ParseException exc ) {
                System.out.println( exc.getMessage() );
            }
        }
        
        //comment
        String comment = "";
        if(CommentTA.getText().trim().equals(""))
            comment = null;
        else
            comment = CommentTA.getText();
        
        //supply
        String supply = (String)WarehouseCoB.getSelectedItem();
        
        int rowValid = 0;
                
        //check if the necessary component are valued or not null
        if(newinvoice==null||newinvoice.equals("")) {
            JOptionPane.showMessageDialog( null, "Please insert the Invoice Text Field!", "Warning", 
                    JOptionPane.WARNING_MESSAGE );
            InvoiceTF.requestFocusInWindow();
            return;
        }
        
        if(!CheckingTable())
            return;
        
        boolean result = tr.EditSaleTransaction( trdate, trdiscount, newinvoice, invoice, 
                comment, commision, shipmentcost, sender, salesmanid, customerid, sending_price );
        
        //error editing sale transaction
        if(!result)
            JOptionPane.showMessageDialog(null,"Error editing sale transaction data! See log file " +
                    "for detail!", "Warning", JOptionPane.WARNING_MESSAGE );
        
        //testing updating transfer table
        int warehouseindex = new WarehouseDB( conn ).getIndexWarehouse(
                (String)WarehouseCoB.getSelectedItem() );
        int saleindex = tr.getIndexSaleTransaction(invoice);
        
        tr.editTransferSale(TransferTB, warehouseindex, saleindex);
                                                           
        dispose();
    }//GEN-LAST:event_OKActionPerformed

    private void CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelActionPerformed
        dispose();
    }//GEN-LAST:event_CancelActionPerformed
            
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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    }
    
    private javax.swing.JEditorPane transactionsEP;
    private NumberFormat IndonesianFormat;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Cancel;
    private javax.swing.JLabel CommentLB;
    private javax.swing.JTextArea CommentTA;
    private javax.swing.JLabel CommisionLB;
    private javax.swing.JTextField CommisionTF;
    private javax.swing.JCheckBox CustomerChB;
    private javax.swing.JComboBox CustomerCoB;
    private javax.swing.JPanel DetailTransactionJP;
    private javax.swing.JTextField DiscountTF;
    private javax.swing.JLabel DiscountTransactionLB;
    private javax.swing.JLabel InvoiceLB;
    private javax.swing.JTextField InvoiceTF;
    private javax.swing.JPanel MainDetailTransactionJP;
    private javax.swing.JButton OK;
    private javax.swing.JLabel PaidByLabel;
    private com.toedter.calendar.JDateChooser SaleTransactionDateChooser;
    private javax.swing.JCheckBox SalesmanChB;
    private javax.swing.JComboBox SalesmanCoB;
    private javax.swing.JCheckBox SenderChB;
    private javax.swing.JTextField SenderTF;
    private javax.swing.JComboBox SendingPaidCoB;
    private javax.swing.JTextField ShipmentCostTF;
    private javax.swing.JLabel ShipmentLB;
    private javax.swing.JLabel SupplyLB;
    private javax.swing.JPanel TopLeftPanel;
    private javax.swing.JPanel TopRightPanel;
    private javax.swing.JPanel TotalTransactionJP;
    private javax.swing.JTable TransferTB;
    private javax.swing.JComboBox WarehouseCoB;
    private javax.swing.JPanel bottomButtonJP;
    private javax.swing.JPanel bottomJP;
    private javax.swing.JLabel leftDummyLabel;
    private javax.swing.JScrollPane mainSP;
    private javax.swing.JLabel rightDummyLabel;
    private javax.swing.JPanel topJP;
    // End of variables declaration//GEN-END:variables
    
}
