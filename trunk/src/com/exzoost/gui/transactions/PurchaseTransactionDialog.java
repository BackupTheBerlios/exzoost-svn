package com.exzoost.gui.transactions;
/*
 * SaleStuffDialog.java
 *
 * Created on November 11, 2004, 6:10 PM
 */

import com.exzoost.database.CommisionerDB;
import com.exzoost.database.InventoryDB;
import com.exzoost.gui.htmleditor.HTMLEditorManipulation;
import com.exzoost.gui.inventory.AddItemDialog;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import javax.swing.Box;
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
public class PurchaseTransactionDialog extends javax.swing.JDialog {
    private Connection conn;
    private Transaction tr;
    private InventoryDB inventory;
    private CommisionerDB commisionerdb;
    private ResultSet uprs;
    private Statement stmt;
    private String warehouse;
    public String invoiceOut;
    private JTable table;
    private String place;
    private DefaultTableModel model;
    TableColumn[] columnarray;
    
    /** Creates new form SaleStuffDialog */
    public PurchaseTransactionDialog(java.awt.Frame parent, boolean modal,
            javax.swing.JEditorPane transactionsEP, JTable table, String place, Connection conn ) {
        super(parent, modal);
        
        //this is the jeditor pane that we will pass to htmleditor
        this.transactionsEP = transactionsEP;
        
        //reference to the connection
        this.conn = conn;
        
        //for calling add item dialog later
        this.table = table;
        this.place = place;
        
        Locale IndonesianLocale = new Locale("id","id");
        IndonesianFormat = NumberFormat.getCurrencyInstance(IndonesianLocale);
        
        //main database class
        inventory = new InventoryDB( conn );
        commisionerdb = new CommisionerDB( conn );
        tr = new Transaction( conn );
        
        //setup the gui
        initComponents();
        
        //setup some of the jtextfields
        DiscountTF.setText( IndonesianFormat.format(0) );
        CommisionTF.setText( IndonesianFormat.format(0) );
        PaymentTF.setText( IndonesianFormat.format(0) );
        
        //setup another thing
        model = (DefaultTableModel)BuyTB.getModel();
        BuyTB.removeColumn( BuyTB.getColumnModel().getColumn(5) );
        columnarray = new TableColumn[12];
        for(int i=0; i<12; i++) {
            columnarray[i] = QuantityTable.getColumnModel().getColumn(i+2);
        }
        for(int i=0; i<12; i++) {
            QuantityTable.removeColumn(columnarray[i]);
        }
        
        //initialize the combo box
        inventory.initializeComboBox(CommisionerCoB, "commisioner");
        inventory.initializeComboBox(WarehouseCoB, "supply");
        inventory.initializeComboBox(ContainerCoB, "container");
        inventory.initializeComboBox(SuplierCoB, "suplier");
        
        //maximize it
        //set the dialog in center of the screen and set the size
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize( new java.awt.Dimension( dim.width, dim.height - 100) );
        
        //setup the default button
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
            int row = BuyTB.getSelectedRow();
            int column = BuyTB.getSelectedColumn();
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
                        int n = JOptionPane.showConfirmDialog(
                                null,
                                "Do you want to add new item to database?",
                                "Adding item",
                                JOptionPane.YES_NO_OPTION );
                        if(n==JOptionPane.YES_OPTION) {
                            new AddItemDialog( null, true, place, conn ).setVisible(true);
                        }
                        return false;
                    }
                    //that name exist in inventory database
                    else {
                        //row that is edited
                        int rowCount = BuyTB.getRowCount();
                        
                        //check every row that "it has been there???"
                        for(int i=0; i<rowCount; i++) {
                            
                            //check every row that has the name and not it self
                            if(BuyTB.getModel().getValueAt(i,0)!=null&&i!=row) {
                                
                                //if it "has been" there
                                if( ( (String)BuyTB.getModel().getValueAt(i,0 ) ).toLowerCase().trim().equals(inventoryname) ) {
                                    
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
                } catch( SQLException e ) {
                    e.printStackTrace();
                    return false;
                }
            } catch(ClassCastException exception) {
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
        PurchaseTransactionDateChooser = new com.toedter.calendar.JDateChooser();
        rightDummyLabel = new javax.swing.JLabel();
        leftDummyLabel = new javax.swing.JLabel();
        bottomJP = new javax.swing.JPanel();
        bottomButtonJP = new javax.swing.JPanel();
        Cancel = new javax.swing.JButton();
        OK = new javax.swing.JButton();
        discountTransactionJP = new javax.swing.JPanel();
        CenterPanel = new javax.swing.JPanel();
        discountTransactionLB = new javax.swing.JLabel();
        DiscountTF = new javax.swing.JTextField();
        CommisionerChB = new javax.swing.JCheckBox();
        CommisionerCoB = new javax.swing.JComboBox();
        CommisionLB = new javax.swing.JLabel();
        CommisionTF = new javax.swing.JTextField();
        MethodPaymentLB = new javax.swing.JLabel();
        MethodPaymentCoB = new javax.swing.JComboBox();
        PaymentLB = new javax.swing.JLabel();
        PaymentTF = new javax.swing.JTextField();
        SupplyLB = new javax.swing.JLabel();
        WarehouseCoB = new javax.swing.JComboBox();
        SenderTF = new javax.swing.JTextField();
        ContainerLB = new javax.swing.JLabel();
        CommentLB = new javax.swing.JLabel();
        CommentTA = new javax.swing.JTextArea();
        ContainerCoB = new javax.swing.JComboBox();
        SenderChB = new javax.swing.JCheckBox();
        SuplierCoB = new javax.swing.JComboBox();
        SuplierChB = new javax.swing.JCheckBox();
        NorthPanel = new javax.swing.JPanel();
        TotalTransactionLB = new javax.swing.JLabel();
        MainTabbedPane = new javax.swing.JTabbedPane();
        mainSP = new javax.swing.JScrollPane();
        BuyTB = new javax.swing.JTable();
        distributionSP = new javax.swing.JScrollPane();
        QuantityTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Purchase Dialog");
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

        PurchaseTransactionDateChooser.setPreferredSize(new java.awt.Dimension(150, 20));
        TopRightPanel.add(PurchaseTransactionDateChooser);

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

        discountTransactionJP.setLayout(new java.awt.BorderLayout());

        CenterPanel.setLayout(new java.awt.GridBagLayout());

        discountTransactionLB.setText("Discount for Transaction : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 0, 0);
        CenterPanel.add(discountTransactionLB, gridBagConstraints);

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
        CenterPanel.add(DiscountTF, gridBagConstraints);

        CommisionerChB.setText("Commisioner : ");
        CommisionerChB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        CommisionerChB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CommisionerChBActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        CenterPanel.add(CommisionerChB, gridBagConstraints);

        CommisionerCoB.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        CenterPanel.add(CommisionerCoB, gridBagConstraints);

        CommisionLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        CommisionLB.setText("Commision : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        CenterPanel.add(CommisionLB, gridBagConstraints);

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
        CenterPanel.add(CommisionTF, gridBagConstraints);

        MethodPaymentLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        MethodPaymentLB.setText("Method Of Payment :  ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        CenterPanel.add(MethodPaymentLB, gridBagConstraints);

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
        CenterPanel.add(MethodPaymentCoB, gridBagConstraints);

        PaymentLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        PaymentLB.setText("Payment : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        CenterPanel.add(PaymentLB, gridBagConstraints);

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
        CenterPanel.add(PaymentTF, gridBagConstraints);

        SupplyLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        SupplyLB.setText("Supply :  ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        CenterPanel.add(SupplyLB, gridBagConstraints);

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
        CenterPanel.add(WarehouseCoB, gridBagConstraints);

        SenderTF.setColumns(12);
        SenderTF.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        CenterPanel.add(SenderTF, gridBagConstraints);

        ContainerLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ContainerLB.setText("Container : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        CenterPanel.add(ContainerLB, gridBagConstraints);

        CommentLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        CommentLB.setText("Comment : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        CenterPanel.add(CommentLB, gridBagConstraints);

        CommentTA.setColumns(12);
        CommentTA.setLineWrap(true);
        CommentTA.setRows(6);
        CommentTA.setTabSize(4);
        CommentTA.setWrapStyleWord(true);
        CommentTA.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        CenterPanel.add(CommentTA, gridBagConstraints);

        ContainerCoB.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        CenterPanel.add(ContainerCoB, gridBagConstraints);

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
        CenterPanel.add(SenderChB, gridBagConstraints);

        SuplierCoB.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        CenterPanel.add(SuplierCoB, gridBagConstraints);

        SuplierChB.setText("Suplier : ");
        SuplierChB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        SuplierChB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SuplierChBActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        CenterPanel.add(SuplierChB, gridBagConstraints);

        discountTransactionJP.add(CenterPanel, java.awt.BorderLayout.CENTER);

        NorthPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 100, 5));

        TotalTransactionLB.setText("Total Transaction Price : ");
        NorthPanel.add(TotalTransactionLB);

        discountTransactionJP.add(NorthPanel, java.awt.BorderLayout.NORTH);

        bottomJP.add(discountTransactionJP, java.awt.BorderLayout.CENTER);

        getContentPane().add(bottomJP, java.awt.BorderLayout.SOUTH);

        BuyTB.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, new Integer(0), new Integer(0), new Integer(0), new Integer(0), null},
                {null, new Integer(0), new Integer(0), new Integer(0), new Integer(0), null},
                {null, new Integer(0), new Integer(0), new Integer(0), new Integer(0), null},
                {null, new Integer(0), new Integer(0), new Integer(0), new Integer(0), null}
            },
            new String [] {
                "Item Name", "Buying Price", "Item Discount", "Quantity", "Total Price", "Valid"
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
        BuyTB.setCellSelectionEnabled(true);
        BuyTB.setMinimumSize(null);
        BuyTB.setPreferredSize(null);
        BuyTB.setSelectionBackground(new java.awt.Color(0, 204, 153));
        //item name column
        TableColumn tc = BuyTB.getColumnModel().getColumn(0);
        tc.setPreferredWidth(170);
        tc.setCellEditor( new FirstColumnSaleEditor() );

        //buying price column
        tc = BuyTB.getColumnModel().getColumn(1);
        tc.setPreferredWidth(250);
        tc.setCellRenderer( new CurrencyRenderer() );

        //item discount column
        tc = BuyTB.getColumnModel().getColumn(2);
        tc.setPreferredWidth(250);
        tc.setCellRenderer( new CurrencyRenderer() );

        //quantity column
        tc = BuyTB.getColumnModel().getColumn(3);
        tc.setPreferredWidth(130);

        //total price column
        tc = BuyTB.getColumnModel().getColumn(4);
        tc.setPreferredWidth(250);
        tc.setCellRenderer( new CurrencyRenderer() );

        //set the listener
        BuyTB.getModel().addTableModelListener( new TableModelListener() {

            //if user is changing inside jtable
            public void tableChanged(TableModelEvent tme) {
                final int column = tme.getColumn();
                final int row = tme.getFirstRow();
                DefaultTableModel model = (DefaultTableModel)BuyTB.getModel();
                int val = 0, totalprice = 0;

                //user change from column name
                if(column==0) {
                    String inventoryname = ( (String)model.getValueAt(row,0)).toLowerCase().trim();

                    inventoryname = "'" + inventoryname + "'";
                    try {
                        stmt = conn.createStatement();

                        //get the price of item from db
                        uprs = stmt.executeQuery("SELECT harga_modal FROM data_barang  " +
                            " WHERE index = ( SELECT index FROM data_barang " +
                            " WHERE nama = " + inventoryname + " ) " );
                        uprs.next();

                        //set the value of related cell
                        model.setValueAt( uprs.getInt("harga_modal"),row,1);

                        String warehousetemp = "";
                        int warehouseamount = WarehouseCoB.getItemCount()-1;
                        int warehousecolumn = 6;

                        for(int i=0; i<warehouseamount; i++) {
                            warehousetemp = "'" + ((String)WarehouseCoB.getItemAt(i)).toLowerCase().trim() + "'";

                            uprs = stmt.executeQuery( "SELECT sum(jumlah) as jumlah " +
                                "FROM stok_gudang s inner join data_gudang d on d.index = s.index_gudang " +
                                "WHERE d.nama = " + warehousetemp + " AND s.index_barang = (select index from data_barang " +
                                "where nama = " + inventoryname + ")");

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
                    model.setValueAt( totalprice, row, 4 );
                    QuantityTable.getModel().setValueAt(
                        (String)model.getValueAt(row,0), row, 0 );

                    //validate the row
                    tr.Validate(BuyTB);

                    updateTotalTransactionLabel();

                    //amount of row of the table
                    int rowCount = BuyTB.getRowCount();

                    //count the valid row by its status
                    for(int i=0; i<rowCount; i++) {
                        if(model.getValueAt(i,5)!=null&&model.getValueAt(i,0)!=null)
                        if(((Boolean)model.getValueAt(i,5)).booleanValue()==true)
                        val++;
                    }

                    //we need to add the row
                    if(val>=(rowCount-1)) {
                        model.addRow( new Object[] { null, 0, 0, 0, 0, false } );
                        ((DefaultTableModel)QuantityTable.getModel()).addRow( new Object[] { null, null, null,
                            null, null,null, null, null, null, null, null, null, null, null } );
                    QuantityTable.validate();
                    QuantityTable.repaint();
                    BuyTB.validate();
                    BuyTB.repaint();
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
                model.setValueAt( totalprice, row, 4 );

                if(column==3)
                QuantityTable.getModel().setValueAt( amount, row, 1 );

                //validate the row
                tr.Validate(BuyTB);

                updateTotalTransactionLabel();

                //the amount of row in jtable
                int rowCount = BuyTB.getRowCount();

                //count the valid row by its status
                for(int i=0; i<rowCount; i++) {
                    if(model.getValueAt(i,5)!=null&&model.getValueAt(i,0)!=null)
                    if(((Boolean)model.getValueAt(i,5)).booleanValue()==true)
                    val++;
                }

                //we need to add the row
                if(val>=(rowCount-1)) {
                    model.addRow( new Object[] { null, 0, 0, 0, 0, false } );
                    ((DefaultTableModel)QuantityTable.getModel()).addRow( new Object[] { null, null, null, null, null,
                        null, null, null, null, null, null, null, null, null } );
                QuantityTable.validate();
                QuantityTable.repaint();
                BuyTB.validate();
                BuyTB.repaint();
            }
        }
    }
    });
    mainSP.setViewportView(BuyTB);

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
    
    private void SuplierChBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SuplierChBActionPerformed
        if(SuplierChB.isSelected()) {
            SuplierCoB.setEnabled(true);
        } else {
            SuplierCoB.setEnabled(false);
        }
    }//GEN-LAST:event_SuplierChBActionPerformed
    
    private void SenderChBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SenderChBActionPerformed
        if(SenderChB.isSelected()) {
            ContainerCoB.setEnabled(true);
            SenderTF.setEnabled(true);
        } else {
            ContainerCoB.setEnabled(false);
            SenderTF.setEnabled(false);
        }
    }//GEN-LAST:event_SenderChBActionPerformed
    
    private void WarehouseCoBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WarehouseCoBActionPerformed
        //we use quantity column as our template
        TableColumn newcolumn = BuyTB.getColumnModel().getColumn(3);
        String warehouse = "";
        
        if( ((String)WarehouseCoB.getSelectedItem()).equals("Custom") ) {
            if(QuantityTable.getColumnCount()<=2) {
                //add warehouse column
                for(int i=0; i<WarehouseCoB.getItemCount()-1; i++) {
                    QuantityTable.addColumn( columnarray[i] );
                    warehouse = (String)WarehouseCoB.getItemAt(i);
                    columnarray[i].setHeaderValue( "Add to " + warehouse );
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
        } else {
            //check if the warehouse column has already been there
            if(QuantityTable.getColumnCount()>2) {
                for(int i=0; i<WarehouseCoB.getItemCount()-1; i++) {
                    QuantityTable.removeColumn( columnarray[i] );
                    QuantityTable.removeColumn( columnarray[i+6] );
                }
            }
        }
    }//GEN-LAST:event_WarehouseCoBActionPerformed
    
    private void DiscountTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_DiscountTFFocusLost
        JTextField textfield = (JTextField)evt.getSource();
        
        try {
            //default is zero
            if(textfield.getText().trim().equals(""))
                textfield.setText( IndonesianFormat.format(0) );
            else
                textfield.setText(
                        IndonesianFormat.format(
                        Integer.parseInt(textfield.getText()) ) ) ;
        } catch( java.lang.NumberFormatException cep ) {
            System.out.println(cep.getMessage());
        }
        
        int rowValid = 0;
        
        //Must be at least one valid row
        for(int i=0; i<BuyTB.getRowCount(); i++) {
            if(BuyTB.getModel().getValueAt(i,5)!=null)
                if( ( (Boolean) BuyTB.getModel().getValueAt(i,5) ).booleanValue()==true)
                    rowValid++;
        }
        
        if(rowValid!=0) {
            updateTotalTransactionLabel();
        }
        
    }//GEN-LAST:event_DiscountTFFocusLost
    
    private void NumberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_NumberFocusLost
        JTextField textfield = (JTextField)evt.getSource();
        
        try {
            //default is zero
            if(textfield.getText().trim().equals(""))
                textfield.setText( IndonesianFormat.format( 0 ) );
            else if(textfield.getText().indexOf("IDR")!=-1) {
                
            }
            else
                textfield.setText( IndonesianFormat.format(Integer.parseInt(textfield.getText()) ) );
        } catch( java.lang.NumberFormatException cep ) {
            System.out.println(cep.getMessage());
        }
    }//GEN-LAST:event_NumberFocusLost
    
    private void NumberFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_NumberFocusGained
        JTextField textfield = (JTextField)evt.getSource();
        textfield.selectAll();
    }//GEN-LAST:event_NumberFocusGained
    
    private void InvoiceTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_InvoiceTFFocusLost
        String tempinvoice = InvoiceTF.getText().trim();
        InvoiceTF.setText( tempinvoice );
    }//GEN-LAST:event_InvoiceTFFocusLost
    
    private void MethodPaymentCoBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MethodPaymentCoBActionPerformed
        if( ((String)MethodPaymentCoB.getSelectedItem()).equals("Credit") ) {
            PaymentTF.setEnabled(true);
        } else {
            PaymentTF.setEnabled(false);
        }
    }//GEN-LAST:event_MethodPaymentCoBActionPerformed
    
    private void CommisionerChBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CommisionerChBActionPerformed
        if(CommisionerChB.isSelected()) {
            CommisionerCoB.setEnabled(true);
            CommisionTF.setEnabled(true);
        } else {
            CommisionerCoB.setEnabled(false);
            CommisionTF.setEnabled(false);
        }
    }//GEN-LAST:event_CommisionerChBActionPerformed
    
    private void OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKActionPerformed
        int rowValid = 0;
        
        //invoice
        String invoice = InvoiceTF.getText().trim();
        
        //date
        java.sql.Date trdate = new java.sql.Date(PurchaseTransactionDateChooser.getDate().getTime());
        
        //transaction discount
        int trdiscount = 0;
        
        try {
            if(!DiscountTF.getText().trim().equals(""))
                trdiscount = ((Long)IndonesianFormat.parse( DiscountTF.getText().trim() )).intValue();
        } catch( java.text.ParseException exc ) {
            System.out.println( exc.getMessage() );
        }
        
        //commisioner and stuff
        String commisioner = "";
        int commision = 0;
        String commisionerid = "";
        if(CommisionerChB.isSelected()) {
            commisioner = ((String)CommisionerCoB.getSelectedItem()).toLowerCase();
            commisionerid = "" + commisionerdb.getIndex(commisioner);
            try {
                if(!DiscountTF.getText().trim().equals(""))
                    commision = ((Long)IndonesianFormat.parse( CommisionTF.getText().trim() )).intValue();
            } catch( java.text.ParseException exc ) {
                System.out.println( exc.getMessage() );
            }
        } else {
            commisionerid = "null";
        }
        
        //payment
        int payment = 0, totalprice = 0;
        if( ((String)MethodPaymentCoB.getSelectedItem()).equals("Cash") ) {
            String totaltr = TotalTransactionLB.getText();
            try {
                payment =
                        ((Long)IndonesianFormat.parse(totaltr.substring( totaltr.indexOf(":") + 2 ))).intValue();
                totalprice = payment;
            } catch( java.text.ParseException exc ) {
                System.out.println( exc.getMessage() );
            }
        } else if( ((String)MethodPaymentCoB.getSelectedItem()).equals("Credit") ) {
            String totaltr = PaymentTF.getText();
            try {
                if(!totaltr.equals(""))
                    payment = ((Long)IndonesianFormat.parse(totaltr)).intValue();
                totalprice =
                        ((Long)IndonesianFormat.parse(
                        TotalTransactionLB.getText().substring(
                        TotalTransactionLB.getText().indexOf(":") + 2 ))).intValue();
            } catch( java.text.ParseException exc ) {
                System.out.println( exc.getMessage() );
            }
        }
        
        //sender
        String sender = "";
        String container = "";
        if(SenderChB.isSelected()) {
            sender = SenderTF.getText().trim();
            container = (String)ContainerCoB.getSelectedItem();
            container = container.substring(0,container.indexOf("/")-1);
        } else {
            sender = null;
            container = null;
        }
        
        //comment
        String comment = "";
        if(CommentTA.getText().trim().equals(""))
            comment = null;
        else
            comment = CommentTA.getText();
        
        //suplier
        String suplier = "";
        if(SuplierChB.isSelected())
            suplier = (String)SuplierCoB.getSelectedItem();
        else
            suplier = null;
        
        //supply
        String supply = (String)WarehouseCoB.getSelectedItem();
        
        //check if the necessary component are valued or not null
        if(invoice==null||invoice.trim().equals("")) {
            JOptionPane.showMessageDialog( null, "Please insert the Invoice Text Field!",
                    "Warning", JOptionPane.WARNING_MESSAGE );
            InvoiceTF.requestFocusInWindow();
            return;
        }
        
        //Must be at least one valid row
        for(int i=0; i<BuyTB.getRowCount(); i++) {
            if(model.getValueAt(i,5)!=null)
                if( ( (Boolean) model.getValueAt(i,5) ).booleanValue()==true)
                    rowValid++;
        }
        
        if(rowValid==0) {
            JOptionPane.showMessageDialog( null,
                    "Please insert at least one valid row in the transaction table!",
                    "Warning", JOptionPane.WARNING_MESSAGE );
            return;
        }
        
        //Invoice Number must be unique
        if(tr.InvoiceNumberNotUnique(invoice, "purchase") ) {
            JOptionPane.showMessageDialog( null, "Invoice must be unique.\n The invoice " +
                    "you have filled is already there in database.\n Please change it!", "Warning", JOptionPane.WARNING_MESSAGE );
            InvoiceTF.requestFocusInWindow();
            InvoiceTF.selectAll();
            return;
        }
        
        if( supply.equals("Custom") )
            if(tr.StockOverLoaded(QuantityTable, "purchase"))
                return;
        
        boolean result = tr.InsertBuyTransaction( sender, trdate,
                trdiscount, invoice, comment, commision, commisionerid,
                container, supply, suplier, totalprice, payment,
                BuyTB, QuantityTable );
        
        if(!result) {
            JOptionPane.showMessageDialog(null,"Error inserting purchase transaction data! See log " +
                    "file for detail!", "Warning", JOptionPane.WARNING_MESSAGE );
            dispose();
        }
        
        if(transactionsEP!=null) {
            HTMLEditorManipulation htmlem = new HTMLEditorManipulation( transactionsEP, conn );
            htmlem.HTMLBuyTransactionDialog( invoice );
        }
        
        dispose();
    }//GEN-LAST:event_OKActionPerformed
    
    private void CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelActionPerformed
        
        dispose();
    }//GEN-LAST:event_CancelActionPerformed
    
    public void updateTotalTransactionLabel() {
        int newprice = 0;
        
        for(int i=0; i<BuyTB.getRowCount(); i++ ) {
            if(model.getValueAt(i,5)!=null) {
                if( ((Boolean)model.getValueAt(i,5)).booleanValue() == true ) {
                    newprice += ((Integer)model.getValueAt(i,4)).intValue();
                }
            }
        }
        
        //cut the total transaction price from total discount too
        if(!DiscountTF.getText().equals("")) {
            try {
                newprice = newprice - ((Long)IndonesianFormat
                        .parse(DiscountTF.getText())).intValue();
            } catch( java.text.ParseException cep ) {
                System.out.println( cep.getMessage() );
            }
        }
        
        TotalTransactionLB.setText("Total Transaction Price : " + IndonesianFormat.format(newprice) );
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    }
    
    private javax.swing.JEditorPane transactionsEP;
    private NumberFormat IndonesianFormat;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable BuyTB;
    private javax.swing.JButton Cancel;
    private javax.swing.JPanel CenterPanel;
    private javax.swing.JLabel CommentLB;
    private javax.swing.JTextArea CommentTA;
    private javax.swing.JLabel CommisionLB;
    private javax.swing.JTextField CommisionTF;
    private javax.swing.JCheckBox CommisionerChB;
    private javax.swing.JComboBox CommisionerCoB;
    private javax.swing.JComboBox ContainerCoB;
    private javax.swing.JLabel ContainerLB;
    private javax.swing.JTextField DiscountTF;
    private javax.swing.JLabel InvoiceLB;
    private javax.swing.JTextField InvoiceTF;
    private javax.swing.JTabbedPane MainTabbedPane;
    private javax.swing.JComboBox MethodPaymentCoB;
    private javax.swing.JLabel MethodPaymentLB;
    private javax.swing.JPanel NorthPanel;
    private javax.swing.JButton OK;
    private javax.swing.JLabel PaymentLB;
    private javax.swing.JTextField PaymentTF;
    private com.toedter.calendar.JDateChooser PurchaseTransactionDateChooser;
    private javax.swing.JTable QuantityTable;
    private javax.swing.JCheckBox SenderChB;
    private javax.swing.JTextField SenderTF;
    private javax.swing.JCheckBox SuplierChB;
    private javax.swing.JComboBox SuplierCoB;
    private javax.swing.JLabel SupplyLB;
    private javax.swing.JPanel TopLeftPanel;
    private javax.swing.JPanel TopRightPanel;
    private javax.swing.JLabel TotalTransactionLB;
    private javax.swing.JComboBox WarehouseCoB;
    private javax.swing.JPanel bottomButtonJP;
    private javax.swing.JPanel bottomJP;
    private javax.swing.JPanel discountTransactionJP;
    private javax.swing.JLabel discountTransactionLB;
    private javax.swing.JScrollPane distributionSP;
    private javax.swing.JLabel leftDummyLabel;
    private javax.swing.JScrollPane mainSP;
    private javax.swing.JLabel rightDummyLabel;
    private javax.swing.JPanel topJP;
    // End of variables declaration//GEN-END:variables
    
}
