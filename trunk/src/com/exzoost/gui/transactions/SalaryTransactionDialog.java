package com.exzoost.gui.transactions;
/*
 * SaleStuffDialog.java
 *
 * Created on November 11, 2004, 6:10 PM
 */

import com.exzoost.gui.helper.GuiHelper;
import com.exzoost.gui.htmleditor.HTMLEditorManipulation;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Window;
import javax.swing.Box;
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
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Properties;
import com.exzoost.database.Transaction;

/**
 *
 * @author  knight
 */
public class SalaryTransactionDialog extends javax.swing.JDialog {
    private Connection conn;
    private Transaction tr;
    private JTextField celleditor = new JTextField();
    ResultSet uprs;
    private int rowUser = 0;
    /** Creates new form SaleStuffDialog */
    public SalaryTransactionDialog(java.awt.Frame parent, boolean modal,
            javax.swing.JEditorPane transactionsEP, Connection conn ) {
        super(parent, modal);
        this.conn = conn;
        
        //must get help from transaction class
        tr = new Transaction( conn );
        
        celleditor.setHorizontalAlignment(JTextField.RIGHT);
        
        //setup the gui
        initComponents();
                
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize( new java.awt.Dimension( dim.width, dim.height - 100) );
        GuiHelper.setOnCenter((Window)this);
        
        //this the jeditor pane, stupid!!!!
        this.transactionsEP = transactionsEP;
        
        //initialize the two of jtable
        tr.initializeSalaryTableTransaction( DailyEmployeeTB, MonthlyEmployeeTB, SalesmanTB,
                SalaryDateChooser.getDate() );
        
        //remove the index column
        DailyEmployeeTB.removeColumn( DailyEmployeeTB.getColumnModel().getColumn(6) );
        MonthlyEmployeeTB.removeColumn( MonthlyEmployeeTB.getColumnModel().getColumn(6) );
        SalesmanTB.removeColumn( SalesmanTB.getColumnModel().getColumn(7) );
        
        //setup default button
        getRootPane().setDefaultButton(OK);
        
        
        //set the column for defaultzero
        TableColumn tc = DailyEmployeeTB.getColumnModel().getColumn(3);
        tc.setCellEditor( new DefaultZero() );
        tc = DailyEmployeeTB.getColumnModel().getColumn(4);
        tc.setCellEditor( new DefaultZero() );
        tc = MonthlyEmployeeTB.getColumnModel().getColumn(3);
        tc.setCellEditor( new DefaultZero() );
        tc = MonthlyEmployeeTB.getColumnModel().getColumn(4);
        tc.setCellEditor( new DefaultZero() );
        tc = SalesmanTB.getColumnModel().getColumn(3);
        tc.setCellEditor( new DefaultZero() );
        tc = SalesmanTB.getColumnModel().getColumn(4);
        tc.setCellEditor( new DefaultZero() );
    }
    
    class DefaultZero extends DefaultCellEditor {
        DefaultZero() {
            super( celleditor );
        }
        
        public boolean stopCellEditing() {
            
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
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        topJP = new javax.swing.JPanel();
        TopRightPanel = new javax.swing.JPanel();
        SalaryDateChooser = new com.toedter.calendar.JDateChooser();
        TopLeftPanel = new javax.swing.JPanel();
        InvoiceLB = new javax.swing.JLabel();
        InvoiceTF = new javax.swing.JTextField();
        rightDummyLabel = new javax.swing.JLabel();
        leftDummyLabel = new javax.swing.JLabel();
        bottomJP = new javax.swing.JPanel();
        bottomButtonJP = new javax.swing.JPanel();
        Cancel = new javax.swing.JButton();
        OK = new javax.swing.JButton();
        BottomPanel = new javax.swing.JPanel();
        CommentLabel = new javax.swing.JLabel();
        CommentTA = new javax.swing.JTextArea();
        EmployeeTbP = new javax.swing.JTabbedPane();
        MonthlyEmployeeScP = new javax.swing.JScrollPane();
        MonthlyEmployeeTB = new javax.swing.JTable();
        DailyEmployeeScP = new javax.swing.JScrollPane();
        DailyEmployeeTB = new javax.swing.JTable();
        SalesmanScP = new javax.swing.JScrollPane();
        SalesmanTB = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Salary Payment Dialog");
        setModal(true);
        topJP.setLayout(new java.awt.BorderLayout());

        TopRightPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));

        SalaryDateChooser.setPreferredSize(new java.awt.Dimension(160, 20));
        TopRightPanel.add(SalaryDateChooser);

        topJP.add(TopRightPanel, java.awt.BorderLayout.EAST);

        TopLeftPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));

        InvoiceLB.setText("Invoice : ");
        TopLeftPanel.add(InvoiceLB);

        InvoiceTF.setColumns(12);
        TopLeftPanel.add(InvoiceTF);

        topJP.add(TopLeftPanel, java.awt.BorderLayout.WEST);

        getContentPane().add(topJP, java.awt.BorderLayout.NORTH);

        rightDummyLabel.setText("          ");
        getContentPane().add(rightDummyLabel, java.awt.BorderLayout.EAST);

        leftDummyLabel.setText("          ");
        getContentPane().add(leftDummyLabel, java.awt.BorderLayout.WEST);

        bottomJP.setLayout(new java.awt.BorderLayout());

        bottomButtonJP.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        Cancel.setText("Cancel");
        Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelActionPerformed(evt);
            }
        });

        bottomButtonJP.add(Cancel);

        OK.setText("OK");
        OK.setMaximumSize(new java.awt.Dimension(75, 25));
        OK.setMinimumSize(new java.awt.Dimension(75, 25));
        OK.setPreferredSize(new java.awt.Dimension(75, 25));
        OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKActionPerformed(evt);
            }
        });

        bottomButtonJP.add(OK);

        bottomJP.add(bottomButtonJP, java.awt.BorderLayout.SOUTH);

        BottomPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));

        CommentLabel.setText("Comment : ");
        BottomPanel.add(CommentLabel);

        CommentTA.setColumns(16);
        CommentTA.setRows(8);
        CommentTA.setTabSize(4);
        CommentTA.setBorder(new javax.swing.border.EtchedBorder());
        BottomPanel.add(CommentTA);

        bottomJP.add(BottomPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(bottomJP, java.awt.BorderLayout.SOUTH);

        DailyEmployeeTB.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        DailyEmployeeTB.getTableHeader().setReorderingAllowed(false);
        MonthlyEmployeeTB.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        MonthlyEmployeeTB.getTableHeader().setReorderingAllowed(false);
        MonthlyEmployeeTB.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, new Integer(0), new Integer(0), new Integer(0), new Boolean(false), null, null},
                {null, null, new Integer(0), new Integer(0), new Integer(0), null, null, null},
                {null, null, new Integer(0), new Integer(0), new Integer(0), null, null, null},
                {null, null, new Integer(0), new Integer(0), new Integer(0), null, null, null}
            },
            new String [] {
                "Code Number", "Name", "Salary", "Salary Cut", "Salary Bonus", "Paid", "Index", "Last Payment Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Boolean.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        MonthlyEmployeeTB.setColumnSelectionAllowed(true);
        MonthlyEmployeeTB.setSelectionBackground(new java.awt.Color(0, 204, 153));
        //TableColumn tc = EmployeeTB.getColumnModel().getColumn(1);
        //tc.setPreferredWidth(150);
        //tc = SalaryTB.getColumnModel().getColumn(3);
        //tc.setPreferredWidth(200);
        //tc = SalaryTB.getColumnModel().getColumn(0);
        //tc.setPreferredWidth(120);
        MonthlyEmployeeScP.setViewportView(MonthlyEmployeeTB);

        EmployeeTbP.addTab("Employee ( Monthly )", MonthlyEmployeeScP);

        DailyEmployeeTB.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, new Integer(0), new Integer(0), new Integer(0), new Boolean(false), null, null},
                {null, null, new Integer(0), new Integer(0), new Integer(0), null, null, null},
                {null, null, new Integer(0), new Integer(0), new Integer(0), null, null, null},
                {null, null, new Integer(0), new Integer(0), new Integer(0), null, null, null}
            },
            new String [] {
                "Code Number", "Name", "Salary", "Salary Cut", "Salary Bonus", "Paid", "Index", "Last Payment Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Boolean.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        DailyEmployeeTB.setColumnSelectionAllowed(true);
        DailyEmployeeTB.setSelectionBackground(new java.awt.Color(0, 204, 153));
        //TableColumn tc = EmployeeTB.getColumnModel().getColumn(1);
        //tc.setPreferredWidth(150);
        //tc = SalaryTB.getColumnModel().getColumn(3);
        //tc.setPreferredWidth(200);
        //tc = SalaryTB.getColumnModel().getColumn(0);
        //tc.setPreferredWidth(120);
        DailyEmployeeScP.setViewportView(DailyEmployeeTB);

        EmployeeTbP.addTab("Employee ( Daily )", DailyEmployeeScP);

        SalesmanTB.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, new Integer(0), new Integer(0), null, null, null, null},
                {null, null, null, new Integer(0), new Integer(0), null, null, null, null},
                {null, null, null, new Integer(0), new Integer(0), null, null, null, null},
                {null, null, null, new Integer(0), new Integer(0), null, null, null, null}
            },
            new String [] {
                "Code Number", "Salesman Name", "Salary", "Salary Cut", "Salary Bonus", "Commision", "Paid", "Index ", "Last Payment Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true, false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        SalesmanTB.setColumnSelectionAllowed(true);
        SalesmanTB.setSelectionBackground(new java.awt.Color(0, 204, 153));
        SalesmanTB.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        SalesmanTB.getTableHeader().setReorderingAllowed(false);
        SalesmanScP.setViewportView(SalesmanTB);

        EmployeeTbP.addTab("Salesman", SalesmanScP);

        getContentPane().add(EmployeeTbP, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents
    
    private void OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKActionPerformed
        //get the information from the dialog
        String invoice = InvoiceTF.getText().trim();
        String comment = CommentTA.getText();
        
        if(invoice.trim().equals("")) {
            JOptionPane.showMessageDialog( null, "Please insert the invoice text field! ",
                    "Warning", JOptionPane.WARNING_MESSAGE );
            InvoiceTF.requestFocusInWindow();
            return;
        }
        
        boolean result = tr.SalaryTransaction( invoice, new java.sql.Date(SalaryDateChooser.getDate().getTime()),
                comment, DailyEmployeeTB, MonthlyEmployeeTB, SalesmanTB );
        
        if(!result) {
            JOptionPane.showMessageDialog(null,"Error inserting salary payment transaction data! " +
                    "See log file for detail!", "Warning", JOptionPane.WARNING_MESSAGE );
            dispose();
        }
        
        if(transactionsEP!=null) {
            HTMLEditorManipulation htmlem = new HTMLEditorManipulation( transactionsEP, conn );
            htmlem.HTMLSalaryDialog( invoice );
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
                new SalaryTransactionDialog(new javax.swing.JFrame(), true, null, null).setVisible(true);
            }
        });
    }
    
    
    private javax.swing.JEditorPane transactionsEP;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BottomPanel;
    private javax.swing.JButton Cancel;
    private javax.swing.JLabel CommentLabel;
    private javax.swing.JTextArea CommentTA;
    private javax.swing.JScrollPane DailyEmployeeScP;
    private javax.swing.JTable DailyEmployeeTB;
    private javax.swing.JTabbedPane EmployeeTbP;
    private javax.swing.JLabel InvoiceLB;
    private javax.swing.JTextField InvoiceTF;
    private javax.swing.JScrollPane MonthlyEmployeeScP;
    private javax.swing.JTable MonthlyEmployeeTB;
    private javax.swing.JButton OK;
    private com.toedter.calendar.JDateChooser SalaryDateChooser;
    private javax.swing.JScrollPane SalesmanScP;
    private javax.swing.JTable SalesmanTB;
    private javax.swing.JPanel TopLeftPanel;
    private javax.swing.JPanel TopRightPanel;
    private javax.swing.JPanel bottomButtonJP;
    private javax.swing.JPanel bottomJP;
    private javax.swing.JLabel leftDummyLabel;
    private javax.swing.JLabel rightDummyLabel;
    private javax.swing.JPanel topJP;
    // End of variables declaration//GEN-END:variables
    
}
