/*
 * SaleStuffDialog.java
 *
 * Created on November 11, 2004, 6:10 PM
 */

package com.exzoost.gui.transactions;

import com.exzoost.database.CustomerDB;
import com.exzoost.database.InventoryDB;
import com.exzoost.database.SalesmanDB;
import com.exzoost.gui.customer.AddCustomerDialog;
import com.exzoost.gui.employee.AddEmployeeDialog;
import com.exzoost.gui.htmleditor.HTMLEditorManipulation;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Toolkit;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
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
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
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
public class SaleTransactionDialog extends javax.swing.JDialog {
    private Connection conn;
    private Transaction tr;
    private ResultSet uprs;
    private Statement stmt;
    private InventoryDB inventory;
    private SalesmanDB salesmandb;
    private CustomerDB customerdb;
    private DefaultTableModel model;
    private JFormattedTextField jformattf;
    TableColumn[] columnarray;
        
    /** Creates new form SaleStuffDialog */
    public SaleTransactionDialog(java.awt.Frame parent, boolean modal, 
            javax.swing.JEditorPane transactionsEP, Connection conn ) {
        super(parent, modal);
        this.transactionsEP = transactionsEP;
        this.conn = conn;
        tr = new Transaction( conn );
        Locale IndonesianLocale = new Locale("id","id");
        IndonesianFormat = NumberFormat.getCurrencyInstance(IndonesianLocale);
        
        //setup the gui
        initComponents();
        
        inventory = new InventoryDB( conn );
        salesmandb = new SalesmanDB( conn );
        customerdb = new CustomerDB( conn );
        SaleTB.setAutoCreateColumnsFromModel(false);
        DiscountTF.setText( IndonesianFormat.format(0) );
        CommisionTF.setText( IndonesianFormat.format(0) );
        PaymentTF.setText( IndonesianFormat.format(0) );
        columnarray = new TableColumn[12];
        for(int i=0; i<12; i++) {
            columnarray[i] = QuantityTable.getColumnModel().getColumn(i+2);    
        }
        for(int i=0; i<12; i++) {
            QuantityTable.removeColumn(columnarray[i]);
        }
        SaleTB.removeColumn( SaleTB.getColumnModel().getColumn(5) );
        model = (DefaultTableModel)SaleTB.getModel();
        
        //initialize the combobox
        inventory.initializeComboBox(CustomerCoB, "customer");
        inventory.initializeComboBox(SalesmanCoB, "salesman");
        inventory.initializeComboBox(WarehouseCoB, "supply");
        
        //maximize it
        //set the dialog in center of the screen and set the size
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize( new java.awt.Dimension( dim.width, dim.height - 100) );
        
        //setup the spinner
        SetUpSpinner();
        
        //total transaction label
        TotalTransactionLB.setText("Total Transaction Price : " + 
                IndonesianFormat.format(0) );
        
        //setup default button
        getRootPane().setDefaultButton(OK);
    }
    
    class CurrencyRenderer extends DefaultTableCellRenderer {
        public CurrencyRenderer() {
            super();
            setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        }
        public void setValue(Object value) {
            if(value!=null&&(value instanceof Number)) {
                Number number = (Number)value;
                value = IndonesianFormat.format(number.intValue());
            }
            super.setValue(value);
        }
    }
    
    class FirstColumnSaleEditor extends DefaultCellEditor {
        FirstColumnSaleEditor() {
            super( new JTextField() );
        }
        
        public boolean stopCellEditing() {
            int row = SaleTB.getSelectedRow();
            int column = SaleTB.getSelectedColumn();
            try {
                try {
                    stmt = conn.createStatement();
                    
                    //name of the inventory to be checked
                    String inventoryname = ((String)getCellEditorValue()).trim().toLowerCase();
                    
                    //for query purpose
                    String sqlinventoryname = "'" + inventoryname + "'";
                    
                    uprs = stmt.executeQuery(
                        "SELECT nama FROM data_barang " +
                        " WHERE nama = " + sqlinventoryname );
                    
                    //that name does not exist in inventory database
                    if(!uprs.next()) {
                        ((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
                        getComponent().requestFocusInWindow();
                        ((JTextField)getComponent()).selectAll();
                        JOptionPane.showMessageDialog(
                                null,
                                "That item name does not exist in inventory database.",
                                "Warning", JOptionPane.WARNING_MESSAGE );
                        //new ChooseItemDialog( null, true, (JTextField)getComponent(), conn ).setVisible(true);
                        return false;
                    }
                    //that name exist in inventory database
                    else {
                        //row that is edited
                        int rowCount = SaleTB.getRowCount();
                        
                        //check every row that "it has been there???"
                        for(int i=0; i<rowCount; i++) {
                            
                            //check every row that has the name
                            if(SaleTB.getModel().getValueAt(i,0)!=null) {
                                    //if it "has been" there
                                    if(((String)SaleTB.getModel().getValueAt(i,0)).toLowerCase().trim().equals(inventoryname)) {
                                        
                                        //mark it red
                                        ((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
                                        
                                        //force the focus
                                        getComponent().requestFocusInWindow();
                                        
                                        //select all so it can be edited
                                        ((JTextField)getComponent()).selectAll();
                                        
                                        //show them warning dialog
                                        JOptionPane.showMessageDialog(
                                                null,
                                                "That item name has already appear in your transaction table.",
                                                "Warning", JOptionPane.WARNING_MESSAGE );
                                        uprs.close();
                                        stmt.close();
                                        return false;
                                    }
                              }
                        }
                    }
                    uprs.close();
                    stmt.close();
                }
                catch( SQLException e ) {
                    e.printStackTrace();
                    return false;
                }
            }
            catch(ClassCastException exception) {
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
        MethodPaymentLB = new javax.swing.JLabel();
        MethodPaymentCoB = new javax.swing.JComboBox();
        CommisionLB = new javax.swing.JLabel();
        PaymentLB = new javax.swing.JLabel();
        PaymentTF = new javax.swing.JTextField();
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
        CreateCustomer = new javax.swing.JButton();
        CommisionSp = new JSpinner( new SpinnerNumberModel( 0, 0, 1, .01 ) );
        SenderChB = new javax.swing.JCheckBox();
        TotalTransactionJP = new javax.swing.JPanel();
        TotalTransactionLB = new javax.swing.JLabel();
        MainTabbedPane = new javax.swing.JTabbedPane();
        mainSP = new javax.swing.JScrollPane();
        SaleTB = new javax.swing.JTable();
        distributionSP = new javax.swing.JScrollPane();
        QuantityTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sale Dialog");
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
                CommisionCount(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        MainDetailTransactionJP.add(CommisionTF, gridBagConstraints);

        MethodPaymentLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        MethodPaymentLB.setText("Method Of Payment :  ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainDetailTransactionJP.add(MethodPaymentLB, gridBagConstraints);

        MethodPaymentCoB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cash", "Credit" }));
        MethodPaymentCoB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MethodPaymentCoBActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        MainDetailTransactionJP.add(MethodPaymentCoB, gridBagConstraints);

        CommisionLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        CommisionLB.setText("Commision : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        MainDetailTransactionJP.add(CommisionLB, gridBagConstraints);

        PaymentLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        PaymentLB.setText("Payment : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        MainDetailTransactionJP.add(PaymentLB, gridBagConstraints);

        PaymentTF.setColumns(12);
        PaymentTF.setEnabled(false);
        PaymentTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                NumberFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                NumberFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        MainDetailTransactionJP.add(PaymentTF, gridBagConstraints);

        SupplyLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        SupplyLB.setText("Supply :  ");
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

        CreateCustomer.setText("Create Customer");
        CreateCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateCustomerActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        MainDetailTransactionJP.add(CreateCustomer, gridBagConstraints);

        CommisionSp.setEnabled(false);
        CommisionSp.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                CommisionSpStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        MainDetailTransactionJP.add(CommisionSp, gridBagConstraints);

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

        TotalTransactionLB.setText("Total Transaction Price : ");
        TotalTransactionJP.add(TotalTransactionLB);

        DetailTransactionJP.add(TotalTransactionJP, java.awt.BorderLayout.NORTH);

        bottomJP.add(DetailTransactionJP, java.awt.BorderLayout.CENTER);

        getContentPane().add(bottomJP, java.awt.BorderLayout.SOUTH);

        mainSP.setPreferredSize(new java.awt.Dimension(453, 403));
        SaleTB.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, new Integer(0), new Integer(0), new Integer(0), new Integer(0), null},
                {null, new Integer(0), new Integer(0), new Integer(0), new Integer(0), null},
                {null, new Integer(0), new Integer(0), new Integer(0), new Integer(0), null},
                {null, new Integer(0), new Integer(0), new Integer(0), new Integer(0), null}
            },
            new String [] {
                "Item Name", "Selling Price", "Item Discount", "Quantity", "Total Price", "Valid"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        SaleTB.setColumnSelectionAllowed(true);
        SaleTB.setMinimumSize(null);
        SaleTB.setPreferredSize(null);
        SaleTB.setSelectionBackground(new java.awt.Color(0, 204, 153));
        //item name column
        TableColumn tc = SaleTB.getColumnModel().getColumn(0);
        tc.setPreferredWidth(170);
        tc.setCellEditor( new FirstColumnSaleEditor() );

        //selling price column
        tc = SaleTB.getColumnModel().getColumn(1);
        tc.setPreferredWidth(250);
        tc.setCellRenderer( new CurrencyRenderer() );

        //item discount column
        tc = SaleTB.getColumnModel().getColumn(2);
        tc.setPreferredWidth(250);
        tc.setCellRenderer( new CurrencyRenderer() );

        //quantity column
        tc = SaleTB.getColumnModel().getColumn(3);
        tc.setPreferredWidth(130);

        //total price column
        tc = SaleTB.getColumnModel().getColumn(4);
        tc.setPreferredWidth(250);
        tc.setCellRenderer( new CurrencyRenderer() );

        //set the listener
        SaleTB.getModel().addTableModelListener( new TableModelListener() {

            //if user is changing inside jtable
            public void tableChanged(TableModelEvent tme) {
                final int column = tme.getColumn();
                final int row = tme.getFirstRow();
                int val = 0, totalprice = 0;

                //user change from column name
                if(column==0) {
                    String inventoryname = ((String)model.getValueAt(row,0)).toLowerCase().trim();
                    inventoryname = "'" + inventoryname + "'";
                    try {
                        stmt = conn.createStatement();

                        //get the price of item from db
                        uprs = stmt.executeQuery("SELECT harga_jual FROM data_barang  " +
                            "WHERE nama = " + inventoryname);
                        uprs.next();

                        //set the value of related cell
                        model.setValueAt(uprs.getInt("harga_jual"),row,1);

                        String warehousetemp = "";
                        int warehouseamount = WarehouseCoB.getItemCount()-1;
                        int warehousecolumn = 6;

                        for(int i=0; i<warehouseamount; i++) {
                            warehousetemp = "'" + ((String)WarehouseCoB.getItemAt(i)).toLowerCase().trim() + "'";

                            uprs = stmt.executeQuery( "SELECT sum(jumlah) as jumlah " +
                                "FROM stok_gudang s inner join data_gudang g on index_gudang = index " +
                                "WHERE nama = " + warehousetemp + " AND index_barang = ( select " +
                                "index from data_barang where nama = " + inventoryname + " )" );

                            uprs.next();

                            QuantityTable.getModel().setValueAt(
                                uprs.getInt("jumlah"), row, 2 + warehousecolumn + i );
                            QuantityTable.getModel().setValueAt(
                                0, row, 2 + i );
                        }

                        //free the resource
                        uprs.close();
                        stmt.close();
                    }
                    catch( SQLException e ) {
                        e.printStackTrace();
                    }

                    //automatically set the total price have to be paid for this item
                    int discount = ((Integer)model.getValueAt( row, 2 )).intValue();
                    int unit_price = ((Integer)model.getValueAt( row, 1 )).intValue();
                    int amount = ((Integer)model.getValueAt( row, 3)).intValue();
                    totalprice = (unit_price - discount) * amount;
                    model.setValueAt(  totalprice, row, 4 );
                    QuantityTable.getModel().setValueAt(
                        (String)model.getValueAt(row,0), row, 0 );

                    //validate the row
                    tr.Validate(SaleTB);

                    updateTotalTransactionLabel();

                    //amount of row of the table
                    int rowCount = SaleTB.getRowCount();

                    //count the valid row by its status
                    for(int i=0; i<rowCount; i++) {
                        if(model.getValueAt(i,5)!=null&&model.getValueAt(i,0)!=null)
                        if(((Boolean)model.getValueAt(i,5)).booleanValue()==true)
                        val++;
                    }

                    //we need to add the row
                    if(val>=(rowCount-1)) {
                        model.addRow( new Object[] { null, 0, 0, 0, 0, false,
                            0, 0, 0, 0, 0, 0 } );
                    ((DefaultTableModel)QuantityTable.getModel()).addRow( new Object[] { null, null, null,
                        null, null,null, null, null, null, null, null, null, null, null } );
                QuantityTable.validate();
                QuantityTable.repaint();
                SaleTB.validate();
                SaleTB.repaint();
            }

        }
        //user move from item discount ( 2 ) and quantity ( 3 ) column
        else if(column==2||column==3) {
            //we must not put null value in these column
            if(model.getValueAt(row,column)==null)
            model.setValueAt( new Integer(0), row, column );

            //set the total price have to be paid for this item
            int discount = ((Integer)model.getValueAt( row, 2 )).intValue();
            int unit_price = ((Integer)model.getValueAt( row, 1 )).intValue();
            int amount = ((Integer)model.getValueAt( row, 3)).intValue();
            totalprice = (unit_price - discount) * amount;
            model.setValueAt(  totalprice, row, 4 );
            if(column==3)
            QuantityTable.getModel().setValueAt( amount, row, 1 );

            //validate the row
            tr.Validate(SaleTB);

            updateTotalTransactionLabel();

            //the amount of row in jtable
            int rowCount = SaleTB.getRowCount();

            //count the valid row by its status
            for(int i=0; i<rowCount; i++) {
                if(model.getValueAt(i,5)!=null&&model.getValueAt(i,0)!=null)
                if(((Boolean)model.getValueAt(i,5)).booleanValue()==true)
                val++;
            }

            //we need to add the row
            if(val>=(rowCount-1)) {
                model.addRow( new Object[] { null, 0, 0, 0, 0, false,
                    0, 0, 0, 0, 0, 0} );
            ((DefaultTableModel)QuantityTable.getModel()).addRow( new Object[] { null, null, null, null, null,
                null, null, null, null, null, null, null, null, null } );
        QuantityTable.validate();
        QuantityTable.repaint();
        SaleTB.validate();
        SaleTB.repaint();
    }
    }
    }
    });
    mainSP.setViewportView(SaleTB);

    MainTabbedPane.addTab("list of items", mainSP);

    QuantityTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null}
        },
        new String [] {
            "Item Name", "Quantity", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8", "Title 9", "Title 10", "Title 11", "Title 12", "Title 13", "Title 14"
        }
    ) {
        Class[] types = new Class [] {
            java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
        };
        boolean[] canEdit = new boolean [] {
            false, false, true, true, true, true, true, true, false, false, false, false, false, false
        };

        public Class getColumnClass(int columnIndex) {
            return types [columnIndex];
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    QuantityTable.setCellSelectionEnabled(true);
    QuantityTable.setSelectionBackground(new java.awt.Color(0, 204, 153));
    distributionSP.setViewportView(QuantityTable);

    MainTabbedPane.addTab("distribution of items", distributionSP);

    getContentPane().add(MainTabbedPane, java.awt.BorderLayout.CENTER);

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SenderChBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SenderChBActionPerformed
        if(!SenderChB.isSelected()) {
            SendingPaidCoB.setEnabled(false);
            ShipmentCostTF.setEnabled(false);
            SenderTF.setEnabled(false);
        }
        else {
            SendingPaidCoB.setEnabled(true);
            ShipmentCostTF.setEnabled(true);
            SenderTF.setEnabled(true);
        }
    }//GEN-LAST:event_SenderChBActionPerformed

    private void CommisionCount(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CommisionCount
        //get the salesman commision
        Integer commision = 0;
        try {
            if(!CommisionTF.getText().trim().equals(""))
                commision = ((Long)IndonesianFormat.parse( CommisionTF.getText().trim() )).intValue();
        }
        catch( java.text.ParseException exc ) {
            System.out.println( exc.getMessage() );
        }   
        
        // Get the total price from Total Transaction Label
        Integer totalprice = 0;
        
        try {
            totalprice = 
                ((Long)IndonesianFormat.parse(
                        TotalTransactionLB.getText().substring( 
                        TotalTransactionLB.getText().indexOf(":") + 2 ))).intValue();
        }
        catch( java.text.ParseException e ) {
            System.out.println(e.getMessage());
        }
        
        CommisionSp.setValue( commision.floatValue() / totalprice.floatValue() );
    }//GEN-LAST:event_CommisionCount

    private void CommisionSpStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_CommisionSpStateChanged
        JSpinner spinner = (JSpinner)evt.getSource();
    
        // Get the new value
        Object value = spinner.getValue();
        
        // Get the total price from Total Transaction Label
        int totalprice = 0;
        
        try {
            totalprice = 
                ((Long)IndonesianFormat.parse(
                        TotalTransactionLB.getText().substring( 
                        TotalTransactionLB.getText().indexOf(":") + 2 ))).intValue();
        }
        catch( java.text.ParseException e ) {
            System.out.println(e.getMessage());
            return;
        }
        
        //set up the commision percent generator
        try {
            CommisionTF.setText( IndonesianFormat.format( (Float)value * totalprice ) );
        }
        catch( java.lang.ClassCastException cce ) {
            try {
                CommisionTF.setText( IndonesianFormat.format( (Integer)value * totalprice ) );
            }
            catch( java.lang.ClassCastException dcce ) {
                CommisionTF.setText( IndonesianFormat.format( (Double)value * totalprice ) );
            }
        }
    }//GEN-LAST:event_CommisionSpStateChanged

    private void CreateCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateCustomerActionPerformed
        new AddCustomerDialog(null,true, conn).setVisible(true);
        inventory.initializeComboBox(CustomerCoB, "customer");
    }//GEN-LAST:event_CreateCustomerActionPerformed

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
            else if(textfield.getText().indexOf("IDR")!=-1) {
                
            }
            else 
                textfield.setText( 
                        IndonesianFormat.format(
                        Integer.parseInt(textfield.getText()) ) );
        }
        catch( java.lang.NumberFormatException cep ) {
            System.out.println(cep.getMessage());
        }
        
        int rowValid = 0;
        
        //Must be at least one valid row
        for(int i=0; i<SaleTB.getRowCount(); i++) {
            if(SaleTB.getModel().getValueAt(i,5)!=null)
                if( ( (Boolean) SaleTB.getModel().getValueAt(i,5) ).booleanValue()==true) 
                    rowValid++;
        }
        
        if(rowValid!=0) {
            updateTotalTransactionLabel();
        }
        
    }//GEN-LAST:event_DiscountTFFocusLost

    private void WarehouseCoBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WarehouseCoBActionPerformed
        //we use quantity column as our template
        TableColumn newcolumn = SaleTB.getColumnModel().getColumn(3);
        String warehouse = "";
        
        if( ((String)WarehouseCoB.getSelectedItem()).equals("Custom") ) {
            if(QuantityTable.getColumnCount()<=2) {
            //add warehouse column
                for(int i=0; i<WarehouseCoB.getItemCount()-1; i++) {
                    QuantityTable.addColumn( columnarray[i] );
                    warehouse = (String)WarehouseCoB.getItemAt(i);
                    columnarray[i].setHeaderValue( "Take from " + warehouse );
                    columnarray[i].setPreferredWidth(200);
                }
                for(int i=0; i<WarehouseCoB.getItemCount()-1; i++) {
                    QuantityTable.addColumn( columnarray[i+6] );
                    warehouse = (String)WarehouseCoB.getItemAt(i);
                    columnarray[i+6].setHeaderValue( "Supply in " + warehouse );
                    columnarray[i+6].setPreferredWidth(200);
                }
                QuantityTable.getColumnModel().getColumn(0).setPreferredWidth(130);
            }
        }
        else {
            //check if the warehouse column has already been there
            if(QuantityTable.getColumnCount()>2) {
                for(int i=0; i<WarehouseCoB.getItemCount()-1; i++) {
                    QuantityTable.removeColumn( columnarray[i] );
                    QuantityTable.removeColumn( columnarray[i+6] );
                }
            }
        }
    }//GEN-LAST:event_WarehouseCoBActionPerformed

    private void MethodPaymentCoBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MethodPaymentCoBActionPerformed
        if( ((String)MethodPaymentCoB.getSelectedItem()).equals("Credit") ) {
            PaymentTF.setEnabled(true);
        }
        else {
            PaymentTF.setEnabled(false);
        }
    }//GEN-LAST:event_MethodPaymentCoBActionPerformed

    private void NumberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_NumberFocusLost
        JTextField textfield = (JTextField)evt.getSource();
        
        try {
            //default is zero
            if(textfield.getText().trim().equals(""))
                textfield.setText( IndonesianFormat.format( 0 ) );
            else if(textfield.getText().indexOf("IDR")!=-1) {
                
            }
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
            CommisionSp.setEnabled(true);
        }
        else {
            SalesmanCoB.setEnabled(false);
            CommisionTF.setEnabled(false);
            CommisionSp.setEnabled(false);
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
        String invoice = InvoiceTF.getText().trim();
        
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
                    commision = ((Number)IndonesianFormat.parse( CommisionTF.getText().trim() )).intValue();
            }
            catch( java.text.ParseException exc ) {
                System.out.println( exc.getMessage() );
            }   
        }
        else {
            salesmanid = "null";
        }
        
        //payment
        int payment = 0, totalprice = 0;
        if( ((String)MethodPaymentCoB.getSelectedItem()).equals("Cash") ) {
            String totaltr = TotalTransactionLB.getText();
            try {
                payment = 
                ((Long)IndonesianFormat.parse(totaltr.substring( totaltr.indexOf(":") + 2 ))).intValue();
                totalprice = payment;
            }
            catch( java.text.ParseException exc ) {
                System.out.println( exc.getMessage() );
            }
        }
        else if( ((String)MethodPaymentCoB.getSelectedItem()).equals("Credit") ) {
            String totaltr = PaymentTF.getText();
            try {
                if(!totaltr.equals(""))
                    payment = ((Long)IndonesianFormat.parse(totaltr)).intValue();
                totalprice = 
                ((Long)IndonesianFormat.parse(
                        TotalTransactionLB.getText().substring( 
                        TotalTransactionLB.getText().indexOf(":") + 2 ))).intValue();
            }
            catch( java.text.ParseException exc ) {
                System.out.println( exc.getMessage() );
            }
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
        if(invoice==null||invoice.equals("")) {
            JOptionPane.showMessageDialog( null, "Please insert the Invoice Text Field!", "Warning", 
                    JOptionPane.WARNING_MESSAGE );
            InvoiceTF.requestFocusInWindow();
            return;
        }
            
        //Must be at least one valid row
        for(int i=0; i<SaleTB.getRowCount(); i++) {
            if(model.getValueAt(i,5)!=null)
                if( ( (Boolean) model.getValueAt(i,5) ).booleanValue()==true) 
                    rowValid++;
        }
        
        if(rowValid==0) {
            JOptionPane.showMessageDialog( null, "Please insert at least one valid row " +
                    "in the transaction table!", "Warning", JOptionPane.WARNING_MESSAGE );
            return;
        }
        
        //Invoice Number must be unique
        if(tr.InvoiceNumberNotUnique(invoice, "sale") ) {
            JOptionPane.showMessageDialog( null, "Invoice must be unique.\n The invoice " +
                    "you have filled is already there in database.\n Please change it!", "Warning", 
                    JOptionPane.WARNING_MESSAGE );
            InvoiceTF.requestFocusInWindow();
            InvoiceTF.selectAll();
            return;
        }
        
        if(tr.StockNotEnough( (String)WarehouseCoB.getSelectedItem(), QuantityTable ))
            return;
      
        if( supply.equals("Custom") )
            if(tr.StockOverLoaded(QuantityTable, "sale"))
                return;
            
        //insert into database
        boolean result = tr.InsertSaleTransaction( sender, trdate, trdiscount, invoice, comment, commision, salesmanid, 
            shipmentcost, customerid, supply, totalprice, payment, sending_price, SaleTB, QuantityTable );
        
        if(!result) {
            JOptionPane.showMessageDialog(null,"Error inserting sale transaction data! See log " +
                    "file for detail!", "Warning", JOptionPane.WARNING_MESSAGE );
            dispose();
        }
        
        if(transactionsEP!=null) {
            HTMLEditorManipulation htmlem = new HTMLEditorManipulation( transactionsEP, conn );
            htmlem.HTMLSaleTransactionDialog( invoice );
        }
        
        dispose();
    }//GEN-LAST:event_OKActionPerformed

    private void CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelActionPerformed
        dispose();
    }//GEN-LAST:event_CancelActionPerformed
    
    public void updateTotalTransactionLabel() {
        int newprice = 0;
        
        for(int i=0; i<SaleTB.getRowCount(); i++ ) {
            if(model.getValueAt(i,5)!=null) {
                if( ((Boolean)model.getValueAt(i,5)).booleanValue() == true ) {
                    newprice += ((Integer)model.getValueAt(i,4)).intValue();
                }
            }
        }
        
        //cut the total transaction price from total discount too
        if(!DiscountTF.getText().equals("")) {
            try {
                newprice = newprice - ((Long)IndonesianFormat.parse(DiscountTF.getText())).intValue();
            }
            catch( java.text.ParseException cep ) {
                cep.printStackTrace();
            }
        }
        
        TotalTransactionLB.setText("Total Transaction Price : " + IndonesianFormat.format(newprice) );
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
    
    
    private void SetUpSpinner() {
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor( CommisionSp, "0.00%" );
        CommisionSp.setEditor(editor);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SaleTransactionDialog(new javax.swing.JFrame(), true, null, null).setVisible(true);
            }
        });
    }
    
    private javax.swing.JEditorPane transactionsEP;
    private NumberFormat IndonesianFormat;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Cancel;
    private javax.swing.JLabel CommentLB;
    private javax.swing.JTextArea CommentTA;
    private javax.swing.JLabel CommisionLB;
    private javax.swing.JSpinner CommisionSp;
    private javax.swing.JTextField CommisionTF;
    private javax.swing.JButton CreateCustomer;
    private javax.swing.JCheckBox CustomerChB;
    private javax.swing.JComboBox CustomerCoB;
    private javax.swing.JPanel DetailTransactionJP;
    private javax.swing.JTextField DiscountTF;
    private javax.swing.JLabel DiscountTransactionLB;
    private javax.swing.JLabel InvoiceLB;
    private javax.swing.JTextField InvoiceTF;
    private javax.swing.JPanel MainDetailTransactionJP;
    private javax.swing.JTabbedPane MainTabbedPane;
    private javax.swing.JComboBox MethodPaymentCoB;
    private javax.swing.JLabel MethodPaymentLB;
    private javax.swing.JButton OK;
    private javax.swing.JLabel PaidByLabel;
    private javax.swing.JLabel PaymentLB;
    private javax.swing.JTextField PaymentTF;
    private javax.swing.JTable QuantityTable;
    private javax.swing.JTable SaleTB;
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
    private javax.swing.JLabel TotalTransactionLB;
    private javax.swing.JComboBox WarehouseCoB;
    private javax.swing.JPanel bottomButtonJP;
    private javax.swing.JPanel bottomJP;
    private javax.swing.JScrollPane distributionSP;
    private javax.swing.JLabel leftDummyLabel;
    private javax.swing.JScrollPane mainSP;
    private javax.swing.JLabel rightDummyLabel;
    private javax.swing.JPanel topJP;
    // End of variables declaration//GEN-END:variables
    
}
