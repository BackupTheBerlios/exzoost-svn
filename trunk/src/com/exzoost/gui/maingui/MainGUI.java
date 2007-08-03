/*
 * GUIInventoriBarang.java
 *
 * Created on October 26, 2004, 7:36 PM
 */

package com.exzoost.gui.maingui;


import com.exzoost.database.CommisionerDB;
import com.exzoost.database.ContainerDB;
import com.exzoost.database.CustomerDB;
import com.exzoost.database.DebtCreditDB;
import com.exzoost.database.EmployeeDB;
import com.exzoost.database.ManipulateDB;
import com.exzoost.database.NewConnection;
import com.exzoost.database.ProducerDB;
import com.exzoost.database.ReportDB;
import com.exzoost.database.SalesmanDB;
import com.exzoost.database.SellerDB;
import com.exzoost.database.WarehouseDB;
import com.exzoost.gui.commisioner.AddCommisionerDialog;
import com.exzoost.gui.commisioner.EditCommisionerDialog;
import com.exzoost.gui.container.AddContainerDialog;
import com.exzoost.gui.container.EditContainerDialog;
import com.exzoost.gui.customer.AddCustomerDialog;
import com.exzoost.gui.customer.EditCustomerDialog;
import com.exzoost.gui.employee.AddEmployeeDialog;
import com.exzoost.gui.employee.EditEmployeeDialog;
import com.exzoost.gui.htmleditor.HTMLEditorManipulation;
import com.exzoost.gui.inventory.LowItemsList;
import com.exzoost.gui.inventory.SearchInventoryDialog;
import com.exzoost.gui.producer.AddProducerDialog;
import com.exzoost.gui.producer.EditProducerDialog;
import com.exzoost.gui.reportview.BuyReport;
import com.exzoost.gui.reportview.CustomerReport;
import com.exzoost.gui.reportview.GeneralReport;
import com.exzoost.gui.reportview.JasperTableReport;
import com.exzoost.gui.reportview.ReportManipulation;
import com.exzoost.gui.reportview.SaleReport;
import com.exzoost.gui.reportview.SalesmanReport;
import com.exzoost.gui.transactions.OutcomeTransactionDialog;
import com.exzoost.gui.transactions.ChooseWarehouse;
import com.exzoost.gui.transactions.EditDebtCreditDialog;
import com.exzoost.gui.transactions.EditIncomeDialog;
import com.exzoost.gui.transactions.EditOutcomeDialog;
import com.exzoost.gui.transactions.EditPurchaseTransactionDialog;
import com.exzoost.gui.transactions.EditSalaryTransactionDialog;
import com.exzoost.gui.transactions.EditSaleTransactionDialog;
import com.exzoost.gui.transactions.IncomeTransactionDialog;
import com.exzoost.gui.transactions.PurchaseTransactionDialog;
import com.exzoost.gui.transactions.SalaryTransactionDialog;
import com.exzoost.gui.transactions.SaleTransactionDialog;
import com.exzoost.gui.transactions.SearchTransactionDialog;
import com.exzoost.gui.transactions.ViewTransactionDialog;
import com.exzoost.gui.warehouse.EditWarehouseDialog;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.io.File;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.JTree;
import javax.swing.Icon;
import javax.swing.ListSelectionModel;
import javax.swing.JOptionPane;
import java.awt.Component;
import javax.swing.ImageIcon;
import java.awt.Frame;
import javax.swing.JFrame;
import java.awt.CardLayout;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.plaf.ColorUIResource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import org.jfree.chart.ChartPanel;
import com.exzoost.database.Transaction;
import com.exzoost.database.InventoryDB;
import com.exzoost.gui.inventory.EditItemDialog;
import com.exzoost.gui.inventory.AddItemDialog;
import com.exzoost.gui.salesman.EditSalesmanDialog;
import com.exzoost.gui.salesman.AddSalesmanDialog;
import com.exzoost.gui.seller.EditSellerDialog;
import com.exzoost.gui.seller.AddSellerDialog;
import com.exzoost.xmlhandler.XMLHandler;
/**
 *
 * @author  knight
 */
public class MainGUI extends javax.swing.JFrame {
    private Connection conn;
    private Transaction tr;
    private ResultSet uprs, uprs2;
    private Statement stmt, stmt2;
    private HTMLEditorManipulation htmlem;
    private StringBuffer warehouse;
    
    //main database classes
    private InventoryDB inventory;
    private SalesmanDB salesman;
    private EmployeeDB employee;
    private SellerDB seller;
    private CustomerDB customer;
    private DebtCreditDB debtcredit;
    private CommisionerDB commisioner;
    private JasperTableReport jasper;
    private WarehouseDB warehousedb;
    private ProducerDB producer;
    private ContainerDB containerdb;
    private ReportDB reportdb;
    private ManipulateDB manipulatedb;
    private ReportManipulation reportman;
    private XMLHandler xmlhandler;
    
    //item row
    private int itemrow;
    //the offset
    private int offset;
    //item display limit
    private int displaylimit;
    /** Creates new form GUIInventoriBarang */
    public MainGUI( Connection conn ) {
        //must use the conn reference to do operation
        this.conn = conn;
        
        warehouse = new StringBuffer("Root");
        
        //setup the "main database classes" to do the operation
        inventory = new InventoryDB( conn );
        salesman = new SalesmanDB( conn );
        seller = new SellerDB( conn );
        employee = new EmployeeDB( conn );
        customer = new CustomerDB( conn );
        commisioner = new CommisionerDB( conn );
        debtcredit = new DebtCreditDB( conn );
        jasper = new JasperTableReport( conn );
        warehousedb = new WarehouseDB( conn );
        producer = new ProducerDB( conn );
        containerdb = new ContainerDB( conn );
        reportdb = new ReportDB( conn );
        manipulatedb = new ManipulateDB( conn );
        tr = new Transaction( conn );
        xmlhandler = new XMLHandler();
        reportman = new ReportManipulation( conn );
        
        //setup the gui
        initComponents();
        
        //misc hacking
        inventory.initializeComboBox( warehouseComboBox, "warehouse" );
                
        AdditionalSetUpTable();
                                
        //set table header not moveable
        setTableHeaderNotMoveable();
        
        //set table so it is "single" selected
        setTableSingleSelection();
        
        //show home profile
        HomeShowProfile();
        
        //setup the summary
        SummaryProfile();
        
        //maximize the gui
        setExtendedState(Frame.MAXIMIZED_BOTH);
        
        showLowItem();
        
        setUpPageItem();
    }
    
    //class for tree of maingui
    private class treeInfo {
        public String view;
        
        public treeInfo( String view ) {
            this.view = view;
        }
                
        public String toString() {
            return view;
        }
    }
    
    //to render the tree
    private class MyRenderer extends DefaultTreeCellRenderer {
        Icon[] treeIcon;
        //treeIcon[0] --> company
        //treeIcon[2] --> transaction
        //treeIcon[3] --> item
        //treeIcon[4] --> report
        //treeIcon[5] --> workers data
        //treeIcon[6] --> salesman
        //treeIcon[7] --> customer
        //treeIcon[8] --> commisioner
        
        public MyRenderer( Icon[] icon ) {
            treeIcon = icon;
        }
        
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
            super.getTreeCellRendererComponent(tree,value,sel,expanded,leaf,row,hasFocus);
            if(isComponent(value,"Root")) {
                setIcon(treeIcon[0]);
            }
            else if(isComponent(value,"Insert Transaction")) {
                setIcon(treeIcon[1]);
            }
            else if(isComponent(value,"Item")) {
                setIcon(treeIcon[2]);
            }
            else if(isComponent(value,"Report")) {
                setIcon(treeIcon[3]);
            }
            else if(isComponent(value,"Employee")) {
                setIcon(treeIcon[4]);
            }
            else if(isComponent(value,"Suplier")) {
                setIcon(treeIcon[5]);
            }
            else if(isComponent(value,"Salesman")) {
                setIcon(treeIcon[6]);
            }
            else if(isComponent(value,"Customer")) {
                setIcon(treeIcon[7]);
            }
            else if(isComponent(value,"Commisioner")) {
                setIcon(treeIcon[8]);
            }
            else if(isComponent(value,"Edit Transaction")) {
                setIcon(treeIcon[9]);
            }
            else if(isComponent(value,"Producer")) {
                setIcon(treeIcon[10]);
            }
            
            return this;
        }
                
        //For JTree, to check whether the leaf is root
        protected boolean isComponent( Object value, String component ) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            treeInfo nodeInfo = (treeInfo)(node.getUserObject());

            if(component=="Root") {
                if((nodeInfo.view).equals(xmlhandler.elementCompanyProfile("name"))) {
                    return true;
                }
                return false;
            }
            else {
                if((nodeInfo.view).equals(component)) {
                    return true;
                }
                return false;
            }
        }
    }
    
    //it is still damn tree
    private void createNodes( DefaultMutableTreeNode top ) {
        DefaultMutableTreeNode leaf = null;
        
        leaf = new DefaultMutableTreeNode( new treeInfo("Insert Transaction") );
        top.add(leaf);
        leaf = new DefaultMutableTreeNode( new treeInfo("Item") );
        top.add(leaf);
        leaf = new DefaultMutableTreeNode( new treeInfo("Report") );
        top.add(leaf);
        leaf = new DefaultMutableTreeNode( new treeInfo("Employee") );
        top.add(leaf);
        leaf = new DefaultMutableTreeNode( new treeInfo("Suplier"));
        top.add(leaf);
        leaf = new DefaultMutableTreeNode( new treeInfo("Salesman"));
        top.add(leaf);
        leaf = new DefaultMutableTreeNode( new treeInfo("Customer"));
        top.add(leaf);
        leaf = new DefaultMutableTreeNode( new treeInfo("Commisioner"));
        top.add(leaf);
        leaf = new DefaultMutableTreeNode( new treeInfo("Edit Transaction"));
        top.add(leaf);
        leaf = new DefaultMutableTreeNode( new treeInfo("Producer"));
        top.add(leaf);
    }
    
    private DefaultTableModel defaultmodel;
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        PanelMainGUI = new javax.swing.JPanel();
        transactionsScrollPane = new javax.swing.JScrollPane();
        transactionsEP = new javax.swing.JEditorPane();
        reportPanel = new javax.swing.JPanel();
        MainReportPanel = new javax.swing.JPanel();
        stockPanel = new javax.swing.JPanel();
        stockScrollPane = new javax.swing.JScrollPane();
        stuffTable = new javax.swing.JTable();
        topStockPanel = new javax.swing.JPanel();
        Begin = new javax.swing.JButton();
        Back = new javax.swing.JButton();
        PageItemTF = new javax.swing.JTextField();
        CountPage = new javax.swing.JLabel();
        Forward = new javax.swing.JButton();
        End = new javax.swing.JButton();
        ViewOrderByLB = new javax.swing.JLabel();
        OrderByCoB = new javax.swing.JComboBox();
        propertiesComboBox = new javax.swing.JComboBox();
        warehouseComboBox = new javax.swing.JComboBox();
        displayComment = new javax.swing.JCheckBox();
        employeePanel = new javax.swing.JPanel();
        topEmployeePanel = new javax.swing.JPanel();
        ReactiveEmployee = new javax.swing.JButton();
        employeeTypeComboBox = new javax.swing.JComboBox();
        employeeStatusComboBox = new javax.swing.JComboBox();
        displayCommentEmployee = new javax.swing.JCheckBox();
        employeeScrollPane = new javax.swing.JScrollPane();
        employeeTable = new javax.swing.JTable();
        suplierPanel = new javax.swing.JPanel();
        topSellerPanel = new javax.swing.JPanel();
        displayCommentSeller = new javax.swing.JCheckBox();
        sellerScrollPane = new javax.swing.JScrollPane();
        sellerTable = new javax.swing.JTable();
        salesmanPanel = new javax.swing.JPanel();
        topSalesmanPanel = new javax.swing.JPanel();
        ReactiveSalesman = new javax.swing.JButton();
        salesmanStatusComboBox = new javax.swing.JComboBox();
        displayCommentSalesman = new javax.swing.JCheckBox();
        salesmanScrollPane = new javax.swing.JScrollPane();
        salesmanTable = new javax.swing.JTable();
        CustomerPanel = new javax.swing.JPanel();
        topCustomerPanel = new javax.swing.JPanel();
        displayCommentCustomer = new javax.swing.JCheckBox();
        customerScrollPane = new javax.swing.JScrollPane();
        customerTable = new javax.swing.JTable();
        CommisionerPanel = new javax.swing.JPanel();
        topCommisionerPanel = new javax.swing.JPanel();
        displayCommentCommisioner = new javax.swing.JCheckBox();
        commisionerScrollPane = new javax.swing.JScrollPane();
        commisionerTable = new javax.swing.JTable();
        EditTransPanel = new javax.swing.JPanel();
        DebtCreditTopPanel = new javax.swing.JPanel();
        SortedLabel = new javax.swing.JLabel();
        SortingEditTransCoB = new javax.swing.JComboBox();
        EditTransCoB = new javax.swing.JComboBox();
        TransPanel = new javax.swing.JPanel();
        SaleScP = new javax.swing.JScrollPane();
        SaleTable = new javax.swing.JTable();
        PurchaseScP = new javax.swing.JScrollPane();
        PurchaseTable = new javax.swing.JTable();
        SalaryScP = new javax.swing.JScrollPane();
        SalaryTable = new javax.swing.JTable();
        OutcomeScP = new javax.swing.JScrollPane();
        OutcomeTable = new javax.swing.JTable();
        IncomeScP = new javax.swing.JScrollPane();
        IncomeTable = new javax.swing.JTable();
        CreditScP = new javax.swing.JScrollPane();
        CreditTable = new javax.swing.JTable();
        DebtScP = new javax.swing.JScrollPane();
        DebtTable = new javax.swing.JTable();
        ProducerPanel = new javax.swing.JPanel();
        TopProducerPane = new javax.swing.JPanel();
        DisplayCommentProducer = new javax.swing.JCheckBox();
        ProducerScrollPane = new javax.swing.JScrollPane();
        ProducerTable = new javax.swing.JTable();
        HomeScp = new javax.swing.JScrollPane();
        HomePanel = new javax.swing.JPanel();
        HomeMainPanel = new javax.swing.JPanel();
        HomeIcon = new javax.swing.JLabel();
        CompanyLB = new javax.swing.JLabel();
        OwnerLB = new javax.swing.JLabel();
        AddressLB = new javax.swing.JLabel();
        PhoneLB = new javax.swing.JLabel();
        EmailLB = new javax.swing.JLabel();
        CompanyID = new javax.swing.JLabel();
        OwnerID = new javax.swing.JLabel();
        AddressID = new javax.swing.JLabel();
        PhoneID = new javax.swing.JLabel();
        EmailID = new javax.swing.JLabel();
        WarehousePanel = new javax.swing.JPanel();
        WarehouseScP = new javax.swing.JScrollPane();
        WarehouseTB = new javax.swing.JTable();
        WarehouseDummyLeftLB = new javax.swing.JLabel();
        WarehouseDummyRightLB = new javax.swing.JLabel();
        WarehouseDummySoutLB = new javax.swing.JLabel();
        WarehouseLB = new javax.swing.JLabel();
        ContainerPanel = new javax.swing.JPanel();
        ContainerScP = new javax.swing.JScrollPane();
        ContainerTB = new javax.swing.JTable();
        ContainerDummyLeftLB = new javax.swing.JLabel();
        ContainerDummyRightLB = new javax.swing.JLabel();
        ContainerDummySoutLB = new javax.swing.JLabel();
        ContainerLB = new javax.swing.JLabel();
        SummaryPanel = new javax.swing.JPanel();
        DateLB = new javax.swing.JLabel();
        SummarySaleLB = new javax.swing.JLabel();
        SummaryPurchaseLB = new javax.swing.JLabel();
        SummarySalesmanCommisionLB = new javax.swing.JLabel();
        SummarySalaryPaymentLB = new javax.swing.JLabel();
        SummaryChargesLB = new javax.swing.JLabel();
        iconBar = new javax.swing.JPanel();
        transactionIconBar = new javax.swing.JPanel();
        Sale = new javax.swing.JButton();
        Purchase = new javax.swing.JButton();
        Salary = new javax.swing.JButton();
        Outcome = new javax.swing.JButton();
        Other = new javax.swing.JButton();
        View = new javax.swing.JButton();
        Clear = new javax.swing.JButton();
        Search = new javax.swing.JButton();
        inventoryIconBar = new javax.swing.JPanel();
        AddItem = new javax.swing.JButton();
        EditItem = new javax.swing.JButton();
        DeleteItem = new javax.swing.JButton();
        SearchItem = new javax.swing.JButton();
        reportIconBar = new javax.swing.JPanel();
        GeneralReport = new javax.swing.JButton();
        PurchaseReport = new javax.swing.JButton();
        SaleReport = new javax.swing.JButton();
        CustomersReport = new javax.swing.JButton();
        SalesmanReport = new javax.swing.JButton();
        employeeDataIconBar = new javax.swing.JPanel();
        AddEmployee = new javax.swing.JButton();
        EditEmployee = new javax.swing.JButton();
        DeleteEmployee = new javax.swing.JButton();
        SuplierIconBar = new javax.swing.JPanel();
        AddSuplier = new javax.swing.JButton();
        EditSuplier = new javax.swing.JButton();
        DeleteSuplier = new javax.swing.JButton();
        salesmanIconBar = new javax.swing.JPanel();
        AddSalesman = new javax.swing.JButton();
        EditSalesman = new javax.swing.JButton();
        DeleteSalesman = new javax.swing.JButton();
        HomeIconBar = new javax.swing.JPanel();
        AddWarehouse = new javax.swing.JButton();
        EditWarehouse = new javax.swing.JButton();
        DeleteWarehouse = new javax.swing.JButton();
        AddContainer = new javax.swing.JButton();
        EditContainer = new javax.swing.JButton();
        DeleteContainer = new javax.swing.JButton();
        EditHome = new javax.swing.JButton();
        CustomerIconBar = new javax.swing.JPanel();
        AddCustomer = new javax.swing.JButton();
        EditCustomer = new javax.swing.JButton();
        DeleteCustomer = new javax.swing.JButton();
        CommisionerIconBar = new javax.swing.JPanel();
        AddCommisioner = new javax.swing.JButton();
        EditCommisioner = new javax.swing.JButton();
        DeleteCommisioner = new javax.swing.JButton();
        EditTransIconBar = new javax.swing.JPanel();
        EditTransactionButton = new javax.swing.JButton();
        DeleteTransaction = new javax.swing.JButton();
        ProducerIconBar = new javax.swing.JPanel();
        AddProducer = new javax.swing.JButton();
        EditProducer = new javax.swing.JButton();
        DeleteProducer = new javax.swing.JButton();
        treeScrollPane = new javax.swing.JScrollPane();
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(new treeInfo(xmlhandler.elementCompanyProfile("name")) );
        createNodes(top);
        treeGUI = new JTree(top);
        ImageIcon[] compIcon = new ImageIcon[11];
        compIcon[0] = new ImageIcon(getClass().getResource("/com/exzoost/images/home32.png"));
        compIcon[1] = new ImageIcon(getClass().getResource("/com/exzoost/images/inserttrans32.png"));
        compIcon[2] = new ImageIcon(getClass().getResource("/com/exzoost/images/stock32.png"));
        compIcon[3] = new ImageIcon(getClass().getResource("/com/exzoost/images/report32.png"));
        compIcon[4] = new ImageIcon(getClass().getResource("/com/exzoost/images/workers32.png"));
        compIcon[5] = new ImageIcon(getClass().getResource("/com/exzoost/images/seller32.png"));
        compIcon[6] = new ImageIcon(getClass().getResource("/com/exzoost/images/salesman32.png"));
        compIcon[7] = new ImageIcon(getClass().getResource("/com/exzoost/images/customer32.png"));
        compIcon[8] = new ImageIcon(getClass().getResource("/com/exzoost/images/commisioner32.png"));
        compIcon[9] = new ImageIcon(getClass().getResource("/com/exzoost/images/edittrans32.png"));
        compIcon[10] = new ImageIcon(getClass().getResource("/com/exzoost/images/producer32.png"));
        treeGUI.setCellRenderer( new MyRenderer(compIcon) );
        mainMenuBar = new javax.swing.JMenuBar();
        mainFile = new javax.swing.JMenu();
        mainSaveMenuItem = new javax.swing.JMenuItem();
        mainPrintMenuItem = new javax.swing.JMenuItem();
        mainExportMenuItem = new javax.swing.JMenuItem();
        mainExitMenuItem = new javax.swing.JMenuItem();
        mainTools = new javax.swing.JMenu();
        Preferences = new javax.swing.JMenuItem();
        mainHelp = new javax.swing.JMenu();
        About = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Exzoost");
        setFont(new java.awt.Font("SansSerif", 0, 10));
        ImageIcon frameicon = new ImageIcon(getClass().getResource("/com/exzoost/images/exzoost.gif"));
        setIconImage(frameicon.getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        PanelMainGUI.setLayout(new java.awt.CardLayout());

        transactionsScrollPane.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 204, 204), 1, true));
        transactionsScrollPane.setName("transaction");
        transactionsEP.setEditable(false);
        transactionsEP.setContentType("text/html");
        transactionsScrollPane.setViewportView(transactionsEP);

        PanelMainGUI.add(transactionsScrollPane, "inserttrans");

        reportPanel.setLayout(new java.awt.BorderLayout());

        reportPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 204)));
        reportPanel.setName("report");
        MainReportPanel.setLayout(new java.awt.BorderLayout());

        reportPanel.add(MainReportPanel, java.awt.BorderLayout.CENTER);

        PanelMainGUI.add(reportPanel, "report");

        stockPanel.setLayout(new java.awt.BorderLayout());

        stockPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 204)));
        stockPanel.setName("item");
        stuffTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Item Code", "Category", "Name", "Modal Price", "Sale Price", "Quantity", "Comment", "Index", "Length ( cm )", "Width ( cm )", "Height ( cm )", "Volume cm3", "Primary M", "P = S", "Secondary M", "Producer", "Suplier", "S = T", "Third T"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        stockScrollPane.setViewportView(stuffTable);

        stockPanel.add(stockScrollPane, java.awt.BorderLayout.CENTER);

        topStockPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        Begin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/Begin.gif")));
        Begin.setBorderPainted(false);
        Begin.setContentAreaFilled(false);
        Begin.setPreferredSize(new java.awt.Dimension(28, 34));
        Begin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BeginActionPerformed(evt);
            }
        });
        Begin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        topStockPanel.add(Begin);

        Back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/Back.gif")));
        Back.setBorderPainted(false);
        Back.setContentAreaFilled(false);
        Back.setPreferredSize(new java.awt.Dimension(28, 34));
        Back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackActionPerformed(evt);
            }
        });
        Back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        topStockPanel.add(Back);

        PageItemTF.setColumns(3);
        PageItemTF.setText("1");
        PageItemTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PageItemTFActionPerformed(evt);
            }
        });

        topStockPanel.add(PageItemTF);

        CountPage.setText("jLabel1");
        topStockPanel.add(CountPage);

        Forward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/Forward.gif")));
        Forward.setBorderPainted(false);
        Forward.setContentAreaFilled(false);
        Forward.setPreferredSize(new java.awt.Dimension(28, 34));
        Forward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ForwardActionPerformed(evt);
            }
        });
        Forward.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        topStockPanel.add(Forward);

        End.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/End.gif")));
        End.setBorderPainted(false);
        End.setContentAreaFilled(false);
        End.setPreferredSize(new java.awt.Dimension(28, 34));
        End.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EndActionPerformed(evt);
            }
        });
        End.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        topStockPanel.add(End);

        ViewOrderByLB.setText("Order by : ");
        topStockPanel.add(ViewOrderByLB);

        OrderByCoB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Name", "Category", "Sale Price", "Modal Price" }));
        OrderByCoB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OrderByCoBActionPerformed(evt);
            }
        });

        topStockPanel.add(OrderByCoB);

        propertiesComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Quantity & Price", "Additional Properties", "Producers & Sellers" }));
        propertiesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                propertiesComboBoxActionPerformed(evt);
            }
        });

        topStockPanel.add(propertiesComboBox);

        warehouseComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warehouseComboBoxActionPerformed(evt);
            }
        });

        topStockPanel.add(warehouseComboBox);

        displayComment.setText("Comment");
        displayComment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayCommentActionPerformed(evt);
            }
        });

        topStockPanel.add(displayComment);

        stockPanel.add(topStockPanel, java.awt.BorderLayout.NORTH);

        PanelMainGUI.add(stockPanel, "item");

        employeePanel.setLayout(new java.awt.BorderLayout());

        employeePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 204)));
        employeePanel.setName("employee");
        topEmployeePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        ReactiveEmployee.setText("Reactive");
        ReactiveEmployee.setEnabled(false);
        ReactiveEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReactiveEmployeeActionPerformed(evt);
            }
        });

        topEmployeePanel.add(ReactiveEmployee);

        employeeTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Daily", "Monthly" }));
        employeeTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                employeeComboBoxActionPerformed(evt);
            }
        });

        topEmployeePanel.add(employeeTypeComboBox);

        employeeStatusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Active", "Not Active" }));
        employeeStatusComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                employeeComboBoxActionPerformed(evt);
            }
        });

        topEmployeePanel.add(employeeStatusComboBox);

        displayCommentEmployee.setText("Display Comment");
        displayCommentEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayCommentEmployeeActionPerformed(evt);
            }
        });

        topEmployeePanel.add(displayCommentEmployee);

        employeePanel.add(topEmployeePanel, java.awt.BorderLayout.NORTH);

        employeeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Number Identity", "Employee Name", "Address", "Position", "Salary", "Phone", "Birth date", "Birth-place", "Comment", "Index"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        employeeScrollPane.setViewportView(employeeTable);

        employeePanel.add(employeeScrollPane, java.awt.BorderLayout.CENTER);

        PanelMainGUI.add(employeePanel, "employee");

        suplierPanel.setLayout(new java.awt.BorderLayout());

        suplierPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 204)));
        suplierPanel.setName("suplier");
        topSellerPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        displayCommentSeller.setText("Display Comment");
        displayCommentSeller.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayCommentSellerActionPerformed(evt);
            }
        });

        topSellerPanel.add(displayCommentSeller);

        suplierPanel.add(topSellerPanel, java.awt.BorderLayout.NORTH);

        sellerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Suplier", "Address", "Phone", "Comment", "Code"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        sellerScrollPane.setViewportView(sellerTable);

        suplierPanel.add(sellerScrollPane, java.awt.BorderLayout.CENTER);

        PanelMainGUI.add(suplierPanel, "suplier");

        salesmanPanel.setLayout(new java.awt.BorderLayout());

        salesmanPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 204)));
        salesmanPanel.setName("salesman");
        topSalesmanPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        ReactiveSalesman.setText("Reactive");
        ReactiveSalesman.setEnabled(false);
        ReactiveSalesman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReactiveSalesmanActionPerformed(evt);
            }
        });

        topSalesmanPanel.add(ReactiveSalesman);

        salesmanStatusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Active", "Not Active" }));
        salesmanStatusComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salesmanStatusComboBoxActionPerformed(evt);
            }
        });

        topSalesmanPanel.add(salesmanStatusComboBox);

        displayCommentSalesman.setText("Display Comment");
        displayCommentSalesman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayCommentSalesmanActionPerformed(evt);
            }
        });

        topSalesmanPanel.add(displayCommentSalesman);

        salesmanPanel.add(topSalesmanPanel, java.awt.BorderLayout.NORTH);

        salesmanTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Number Identity", "Salesman Name", "Address", "Phone", "Salary", "Birth date", "Birth-place", "Comment", "Index"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        salesmanScrollPane.setViewportView(salesmanTable);

        salesmanPanel.add(salesmanScrollPane, java.awt.BorderLayout.CENTER);

        PanelMainGUI.add(salesmanPanel, "salesman");

        CustomerPanel.setLayout(new java.awt.BorderLayout());

        CustomerPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 204)));
        CustomerPanel.setName("customer");
        topCustomerPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        displayCommentCustomer.setText("Display Comment");
        displayCommentCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayCommentCustomerActionPerformed(evt);
            }
        });

        topCustomerPanel.add(displayCommentCustomer);

        CustomerPanel.add(topCustomerPanel, java.awt.BorderLayout.NORTH);

        customerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Customer Name", "Address", "Phone", "Comment", "Index"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        customerScrollPane.setViewportView(customerTable);

        CustomerPanel.add(customerScrollPane, java.awt.BorderLayout.CENTER);

        PanelMainGUI.add(CustomerPanel, "customer");

        CommisionerPanel.setLayout(new java.awt.BorderLayout());

        CommisionerPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 204)));
        CommisionerPanel.setName("commisioner");
        topCommisionerPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        displayCommentCommisioner.setText("Display Comment");
        displayCommentCommisioner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayCommentCommisionerActionPerformed(evt);
            }
        });

        topCommisionerPanel.add(displayCommentCommisioner);

        CommisionerPanel.add(topCommisionerPanel, java.awt.BorderLayout.NORTH);

        commisionerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Commisioner Name", "Address", "Phone", "Comment", "Index"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        commisionerScrollPane.setViewportView(commisionerTable);

        CommisionerPanel.add(commisionerScrollPane, java.awt.BorderLayout.CENTER);

        PanelMainGUI.add(CommisionerPanel, "commisioner");

        EditTransPanel.setLayout(new java.awt.BorderLayout());

        EditTransPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 204)));
        EditTransPanel.setName("debtcredit");
        DebtCreditTopPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        SortedLabel.setText("Sorted by : ");
        DebtCreditTopPanel.add(SortedLabel);

        SortingEditTransCoB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Invoice", "Date" }));
        SortingEditTransCoB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditTransCoBActionPerformed(evt);
            }
        });

        DebtCreditTopPanel.add(SortingEditTransCoB);

        EditTransCoB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sale Transaction", "Purchase Transaction", "Salary Payment Transaction", "Income Transaction", "Outcome Transaction", "Debt", "Credit" }));
        EditTransCoB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditTransCoBActionPerformed(evt);
            }
        });

        DebtCreditTopPanel.add(EditTransCoB);

        EditTransPanel.add(DebtCreditTopPanel, java.awt.BorderLayout.NORTH);

        TransPanel.setLayout(new java.awt.CardLayout());

        SaleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Invoice", "Date", "Salesman", "Customer", "Salesman Commision", "Total Transaction", "Index"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        SaleScP.setViewportView(SaleTable);

        TransPanel.add(SaleScP, "sale");

        PurchaseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Invoice", "Date", "Commisioner", "Commision", "Container", "Total Transaction", "Index"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        PurchaseTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        PurchaseScP.setViewportView(PurchaseTable);

        TransPanel.add(PurchaseScP, "purchase");

        SalaryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Invoice", "Date", "Comment"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        SalaryScP.setViewportView(SalaryTable);

        TransPanel.add(SalaryScP, "salary");

        OutcomeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Invoice", "Date", "Type", "Comment", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        OutcomeScP.setViewportView(OutcomeTable);

        TransPanel.add(OutcomeScP, "outcome");

        IncomeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Invoice", "Date", "Description", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        IncomeScP.setViewportView(IncomeTable);

        TransPanel.add(IncomeScP, "income");

        CreditTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Invoice", "Date", "Customer Must Pay", "Customer Have Paid", "Total Transaction", "Index"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        CreditScP.setViewportView(CreditTable);

        TransPanel.add(CreditScP, "credit");

        DebtTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Invoice", "Date", "We Must Pay", "We Have Paid", "Total Transaction", "Index"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        PurchaseTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        DebtScP.setViewportView(DebtTable);

        TransPanel.add(DebtScP, "debt");

        EditTransPanel.add(TransPanel, java.awt.BorderLayout.CENTER);

        PanelMainGUI.add(EditTransPanel, "edittrans");

        ProducerPanel.setLayout(new java.awt.BorderLayout());

        ProducerPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 204)));
        ProducerPanel.setName("producer");
        TopProducerPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        DisplayCommentProducer.setText("Display Comment");
        DisplayCommentProducer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisplayCommentProducerActionPerformed(evt);
            }
        });

        TopProducerPane.add(DisplayCommentProducer);

        ProducerPanel.add(TopProducerPane, java.awt.BorderLayout.NORTH);

        ProducerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Producer", "Address", "Phone", "Comment", "Index"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ProducerTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        ProducerScrollPane.setViewportView(ProducerTable);

        ProducerPanel.add(ProducerScrollPane, java.awt.BorderLayout.CENTER);

        PanelMainGUI.add(ProducerPanel, "producer");

        HomeScp.setName("home");
        HomePanel.setLayout(new java.awt.GridBagLayout());

        HomePanel.setName("home");
        HomeMainPanel.setLayout(new java.awt.GridBagLayout());

        HomeMainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        HomeIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/home128.png")));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        HomeMainPanel.add(HomeIcon, gridBagConstraints);

        CompanyLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        CompanyLB.setText("Company Name : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 15, 0);
        HomeMainPanel.add(CompanyLB, gridBagConstraints);

        OwnerLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        OwnerLB.setText("Owner Name : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 15, 0);
        HomeMainPanel.add(OwnerLB, gridBagConstraints);

        AddressLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        AddressLB.setText("Address : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 15, 0);
        HomeMainPanel.add(AddressLB, gridBagConstraints);

        PhoneLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        PhoneLB.setText("Phone : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 15, 0);
        HomeMainPanel.add(PhoneLB, gridBagConstraints);

        EmailLB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        EmailLB.setText("Email : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 15, 0);
        HomeMainPanel.add(EmailLB, gridBagConstraints);

        CompanyID.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 15, 0);
        HomeMainPanel.add(CompanyID, gridBagConstraints);

        OwnerID.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 15, 0);
        HomeMainPanel.add(OwnerID, gridBagConstraints);

        AddressID.setText("jLabel3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 15, 0);
        HomeMainPanel.add(AddressID, gridBagConstraints);

        PhoneID.setText("jLabel4");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 15, 0);
        HomeMainPanel.add(PhoneID, gridBagConstraints);

        EmailID.setText("jLabel5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 15, 0);
        HomeMainPanel.add(EmailID, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(30, 0, 0, 0);
        HomePanel.add(HomeMainPanel, gridBagConstraints);

        WarehousePanel.setLayout(new java.awt.BorderLayout());

        WarehouseScP.setPreferredSize(new java.awt.Dimension(453, 200));
        WarehouseTB.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Warehouse Name", "Warehouse Address", "Comment", "Index"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        WarehouseTB.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        WarehouseScP.setViewportView(WarehouseTB);

        WarehousePanel.add(WarehouseScP, java.awt.BorderLayout.CENTER);

        WarehouseDummyLeftLB.setText("          ");
        WarehousePanel.add(WarehouseDummyLeftLB, java.awt.BorderLayout.WEST);

        WarehouseDummyRightLB.setText("          ");
        WarehousePanel.add(WarehouseDummyRightLB, java.awt.BorderLayout.EAST);

        WarehouseDummySoutLB.setText(" ");
        WarehouseDummySoutLB.setPreferredSize(new java.awt.Dimension(4, 30));
        WarehousePanel.add(WarehouseDummySoutLB, java.awt.BorderLayout.SOUTH);

        WarehouseLB.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        WarehouseLB.setText("Warehouse");
        WarehousePanel.add(WarehouseLB, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(45, 0, 0, 0);
        HomePanel.add(WarehousePanel, gridBagConstraints);

        ContainerPanel.setLayout(new java.awt.BorderLayout());

        ContainerScP.setPreferredSize(new java.awt.Dimension(453, 200));
        ContainerTB.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Container Name", "Volume ( m3 )", "Price", "Comment", "Index"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ContainerTB.setDragEnabled(true);
        ContainerTB.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        ContainerScP.setViewportView(ContainerTB);

        ContainerPanel.add(ContainerScP, java.awt.BorderLayout.CENTER);

        ContainerDummyLeftLB.setText("          ");
        ContainerPanel.add(ContainerDummyLeftLB, java.awt.BorderLayout.WEST);

        ContainerDummyRightLB.setText("          ");
        ContainerPanel.add(ContainerDummyRightLB, java.awt.BorderLayout.EAST);

        ContainerDummySoutLB.setText(" ");
        ContainerDummySoutLB.setPreferredSize(new java.awt.Dimension(4, 30));
        ContainerPanel.add(ContainerDummySoutLB, java.awt.BorderLayout.SOUTH);

        ContainerLB.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ContainerLB.setText("Container");
        ContainerPanel.add(ContainerLB, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(45, 0, 0, 0);
        HomePanel.add(ContainerPanel, gridBagConstraints);

        SummaryPanel.setLayout(new java.awt.GridBagLayout());

        DateLB.setText("Summary of the Company : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        SummaryPanel.add(DateLB, gridBagConstraints);

        SummarySaleLB.setText("Total Value of Sale Transaction : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        SummaryPanel.add(SummarySaleLB, gridBagConstraints);

        SummaryPurchaseLB.setText("Total Value of Purchase Transaction : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        SummaryPanel.add(SummaryPurchaseLB, gridBagConstraints);

        SummarySalesmanCommisionLB.setText("Total Value of Salesman Commision Transaction : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        SummaryPanel.add(SummarySalesmanCommisionLB, gridBagConstraints);

        SummarySalaryPaymentLB.setText("Total Value of Salary Payment Transaction : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        SummaryPanel.add(SummarySalaryPaymentLB, gridBagConstraints);

        SummaryChargesLB.setText("Total Value of Charges Payment Transaction : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        SummaryPanel.add(SummaryChargesLB, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        HomePanel.add(SummaryPanel, gridBagConstraints);

        HomeScp.setViewportView(HomePanel);

        PanelMainGUI.add(HomeScp, "home");

        getContentPane().add(PanelMainGUI, java.awt.BorderLayout.CENTER);

        iconBar.setLayout(new java.awt.CardLayout());

        transactionIconBar.setLayout(new javax.swing.BoxLayout(transactionIconBar, javax.swing.BoxLayout.X_AXIS));

        Sale.setFont(new java.awt.Font("Dialog", 0, 12));
        Sale.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/sale32.png")));
        Sale.setText("Sale");
        Sale.setToolTipText("Sell items to customer");
        Sale.setBorderPainted(false);
        Sale.setContentAreaFilled(false);
        Sale.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Sale.setMargin(new java.awt.Insets(0, 0, 0, 0));
        Sale.setMaximumSize(new java.awt.Dimension(79, 61));
        Sale.setMinimumSize(new java.awt.Dimension(79, 61));
        Sale.setPreferredSize(new java.awt.Dimension(79, 61));
        Sale.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Sale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaleActionPerformed(evt);
            }
        });
        Sale.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        transactionIconBar.add(Sale);

        Purchase.setFont(new java.awt.Font("Dialog", 0, 12));
        Purchase.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/buy32.png")));
        Purchase.setText("Purchase");
        Purchase.setToolTipText("Buy stuff from seller");
        Purchase.setBorderPainted(false);
        Purchase.setContentAreaFilled(false);
        Purchase.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Purchase.setMargin(new java.awt.Insets(0, 0, 0, 0));
        Purchase.setMaximumSize(new java.awt.Dimension(79, 61));
        Purchase.setMinimumSize(new java.awt.Dimension(79, 61));
        Purchase.setPreferredSize(new java.awt.Dimension(79, 61));
        Purchase.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Purchase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PurchaseActionPerformed(evt);
            }
        });
        Purchase.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        transactionIconBar.add(Purchase);

        Salary.setFont(new java.awt.Font("Dialog", 0, 12));
        Salary.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/salary32.png")));
        Salary.setText("Salary");
        Salary.setToolTipText("Pay the salary to workers");
        Salary.setBorderPainted(false);
        Salary.setContentAreaFilled(false);
        Salary.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Salary.setMargin(new java.awt.Insets(0, 0, 0, 0));
        Salary.setMaximumSize(new java.awt.Dimension(79, 61));
        Salary.setMinimumSize(new java.awt.Dimension(79, 61));
        Salary.setPreferredSize(new java.awt.Dimension(79, 61));
        Salary.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Salary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SalaryActionPerformed(evt);
            }
        });
        Salary.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        transactionIconBar.add(Salary);

        Outcome.setFont(new java.awt.Font("Dialog", 0, 12));
        Outcome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/charges32.png")));
        Outcome.setText("Outcome");
        Outcome.setToolTipText("Other outcome");
        Outcome.setBorderPainted(false);
        Outcome.setContentAreaFilled(false);
        Outcome.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Outcome.setMargin(new java.awt.Insets(0, 0, 0, 0));
        Outcome.setMaximumSize(new java.awt.Dimension(79, 61));
        Outcome.setMinimumSize(new java.awt.Dimension(79, 61));
        Outcome.setPreferredSize(new java.awt.Dimension(79, 61));
        Outcome.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Outcome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OutcomeActionPerformed(evt);
            }
        });
        Outcome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        transactionIconBar.add(Outcome);

        Other.setFont(new java.awt.Font("Dialog", 0, 12));
        Other.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/other32.png")));
        Other.setText("Income");
        Other.setToolTipText("Other income");
        Other.setBorderPainted(false);
        Other.setContentAreaFilled(false);
        Other.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Other.setMargin(new java.awt.Insets(0, 0, 0, 0));
        Other.setMaximumSize(new java.awt.Dimension(79, 61));
        Other.setMinimumSize(new java.awt.Dimension(79, 61));
        Other.setPreferredSize(new java.awt.Dimension(79, 61));
        Other.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Other.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OtherActionPerformed(evt);
            }
        });
        Other.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        transactionIconBar.add(Other);

        View.setFont(new java.awt.Font("Dialog", 0, 12));
        View.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/view32.png")));
        View.setText("View");
        View.setToolTipText("View The Transaction");
        View.setBorderPainted(false);
        View.setContentAreaFilled(false);
        View.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        View.setMargin(new java.awt.Insets(0, 0, 0, 0));
        View.setMaximumSize(new java.awt.Dimension(79, 61));
        View.setMinimumSize(new java.awt.Dimension(79, 61));
        View.setPreferredSize(new java.awt.Dimension(79, 61));
        View.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        View.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewActionPerformed(evt);
            }
        });
        View.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        transactionIconBar.add(View);

        Clear.setFont(new java.awt.Font("Dialog", 0, 12));
        Clear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/clear32.png")));
        Clear.setText("Clear");
        Clear.setToolTipText("Clear the view");
        Clear.setBorderPainted(false);
        Clear.setContentAreaFilled(false);
        Clear.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Clear.setMargin(new java.awt.Insets(0, 0, 0, 0));
        Clear.setMaximumSize(new java.awt.Dimension(79, 61));
        Clear.setMinimumSize(new java.awt.Dimension(79, 61));
        Clear.setPreferredSize(new java.awt.Dimension(79, 61));
        Clear.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearActionPerformed(evt);
            }
        });
        Clear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        transactionIconBar.add(Clear);

        Search.setFont(new java.awt.Font("Dialog", 0, 12));
        Search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/searchtrans32.png")));
        Search.setText("Search");
        Search.setToolTipText("Search the transaction");
        Search.setBorderPainted(false);
        Search.setContentAreaFilled(false);
        Search.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Search.setMargin(new java.awt.Insets(0, 0, 0, 0));
        Search.setMaximumSize(new java.awt.Dimension(79, 61));
        Search.setMinimumSize(new java.awt.Dimension(79, 61));
        Search.setPreferredSize(new java.awt.Dimension(79, 61));
        Search.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchActionPerformed(evt);
            }
        });
        Search.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        transactionIconBar.add(Search);

        iconBar.add(transactionIconBar, "inserttrans");

        inventoryIconBar.setLayout(new javax.swing.BoxLayout(inventoryIconBar, javax.swing.BoxLayout.X_AXIS));

        AddItem.setFont(new java.awt.Font("Dialog", 0, 12));
        AddItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/addInventory32.png")));
        AddItem.setText("Add Item");
        AddItem.setToolTipText("Add new inventory");
        AddItem.setBorderPainted(false);
        AddItem.setContentAreaFilled(false);
        AddItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        AddItem.setMargin(new java.awt.Insets(0, 0, 0, 0));
        AddItem.setMaximumSize(new java.awt.Dimension(100, 61));
        AddItem.setMinimumSize(new java.awt.Dimension(100, 61));
        AddItem.setPreferredSize(new java.awt.Dimension(100, 61));
        AddItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        AddItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddItemActionPerformed(evt);
            }
        });
        AddItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        inventoryIconBar.add(AddItem);

        EditItem.setFont(new java.awt.Font("Dialog", 0, 12));
        EditItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/editInventory32.png")));
        EditItem.setText("Edit Item");
        EditItem.setToolTipText("Edit the properties of inventory");
        EditItem.setBorderPainted(false);
        EditItem.setContentAreaFilled(false);
        EditItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        EditItem.setMargin(new java.awt.Insets(0, 0, 0, 0));
        EditItem.setMaximumSize(new java.awt.Dimension(100, 61));
        EditItem.setMinimumSize(new java.awt.Dimension(100, 61));
        EditItem.setPreferredSize(new java.awt.Dimension(100, 61));
        EditItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        EditItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditItemActionPerformed(evt);
            }
        });
        EditItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        inventoryIconBar.add(EditItem);

        DeleteItem.setFont(new java.awt.Font("Dialog", 0, 12));
        DeleteItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/deleteInventory32.png")));
        DeleteItem.setText("Delete Item");
        DeleteItem.setToolTipText("Delete the properties of inventory");
        DeleteItem.setBorderPainted(false);
        DeleteItem.setContentAreaFilled(false);
        DeleteItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DeleteItem.setMargin(new java.awt.Insets(0, 0, 0, 0));
        DeleteItem.setMaximumSize(new java.awt.Dimension(100, 61));
        DeleteItem.setMinimumSize(new java.awt.Dimension(100, 61));
        DeleteItem.setPreferredSize(new java.awt.Dimension(100, 61));
        DeleteItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        DeleteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteItemActionPerformed(evt);
            }
        });
        DeleteItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        inventoryIconBar.add(DeleteItem);

        SearchItem.setFont(new java.awt.Font("Dialog", 0, 12));
        SearchItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/searchInventory32.png")));
        SearchItem.setText("Search Item");
        SearchItem.setToolTipText("Search your inventory");
        SearchItem.setBorderPainted(false);
        SearchItem.setContentAreaFilled(false);
        SearchItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        SearchItem.setMargin(new java.awt.Insets(0, 0, 0, 0));
        SearchItem.setMaximumSize(new java.awt.Dimension(100, 61));
        SearchItem.setMinimumSize(new java.awt.Dimension(100, 61));
        SearchItem.setPreferredSize(new java.awt.Dimension(100, 61));
        SearchItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        SearchItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchItemActionPerformed(evt);
            }
        });
        SearchItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        inventoryIconBar.add(SearchItem);

        iconBar.add(inventoryIconBar, "item");

        reportIconBar.setLayout(new javax.swing.BoxLayout(reportIconBar, javax.swing.BoxLayout.X_AXIS));

        GeneralReport.setFont(new java.awt.Font("Dialog", 0, 12));
        GeneralReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/profitReport32.png")));
        GeneralReport.setText("General");
        GeneralReport.setToolTipText("Read the profit report");
        GeneralReport.setBorderPainted(false);
        GeneralReport.setContentAreaFilled(false);
        GeneralReport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        GeneralReport.setMargin(new java.awt.Insets(0, 0, 0, 0));
        GeneralReport.setMaximumSize(new java.awt.Dimension(67, 61));
        GeneralReport.setMinimumSize(new java.awt.Dimension(67, 61));
        GeneralReport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        GeneralReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GeneralReportActionPerformed(evt);
            }
        });
        GeneralReport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        reportIconBar.add(GeneralReport);

        PurchaseReport.setFont(new java.awt.Font("Dialog", 0, 12));
        PurchaseReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/purchaseReport32.png")));
        PurchaseReport.setText("Purchase");
        PurchaseReport.setToolTipText("Read the purchase report");
        PurchaseReport.setBorderPainted(false);
        PurchaseReport.setContentAreaFilled(false);
        PurchaseReport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        PurchaseReport.setMargin(new java.awt.Insets(0, 0, 0, 0));
        PurchaseReport.setMaximumSize(new java.awt.Dimension(67, 61));
        PurchaseReport.setMinimumSize(new java.awt.Dimension(67, 61));
        PurchaseReport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        PurchaseReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PurchaseReportActionPerformed(evt);
            }
        });
        PurchaseReport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        reportIconBar.add(PurchaseReport);

        SaleReport.setFont(new java.awt.Font("Dialog", 0, 12));
        SaleReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/saleReport32.png")));
        SaleReport.setText("Sale");
        SaleReport.setToolTipText("Read the sale report");
        SaleReport.setBorderPainted(false);
        SaleReport.setContentAreaFilled(false);
        SaleReport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        SaleReport.setMargin(new java.awt.Insets(0, 0, 0, 0));
        SaleReport.setMaximumSize(new java.awt.Dimension(67, 61));
        SaleReport.setMinimumSize(new java.awt.Dimension(67, 61));
        SaleReport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        SaleReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaleReportActionPerformed(evt);
            }
        });
        SaleReport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        reportIconBar.add(SaleReport);

        CustomersReport.setFont(new java.awt.Font("Dialog", 0, 12));
        CustomersReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/customersReport32.png")));
        CustomersReport.setText("Customers");
        CustomersReport.setToolTipText("Read the customers report");
        CustomersReport.setBorderPainted(false);
        CustomersReport.setContentAreaFilled(false);
        CustomersReport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        CustomersReport.setMargin(new java.awt.Insets(0, 0, 0, 0));
        CustomersReport.setMaximumSize(new java.awt.Dimension(67, 61));
        CustomersReport.setMinimumSize(new java.awt.Dimension(67, 61));
        CustomersReport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        CustomersReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CustomersReportActionPerformed(evt);
            }
        });
        CustomersReport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        reportIconBar.add(CustomersReport);

        SalesmanReport.setFont(new java.awt.Font("Dialog", 0, 12));
        SalesmanReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/salesmanReport32.png")));
        SalesmanReport.setText("Salesman");
        SalesmanReport.setToolTipText("Read the salesman report");
        SalesmanReport.setBorderPainted(false);
        SalesmanReport.setContentAreaFilled(false);
        SalesmanReport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        SalesmanReport.setMargin(new java.awt.Insets(0, 0, 0, 0));
        SalesmanReport.setMaximumSize(new java.awt.Dimension(67, 61));
        SalesmanReport.setMinimumSize(new java.awt.Dimension(67, 61));
        SalesmanReport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        SalesmanReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SalesmanReportActionPerformed(evt);
            }
        });
        SalesmanReport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        reportIconBar.add(SalesmanReport);

        iconBar.add(reportIconBar, "report");

        employeeDataIconBar.setLayout(new javax.swing.BoxLayout(employeeDataIconBar, javax.swing.BoxLayout.X_AXIS));

        AddEmployee.setFont(new java.awt.Font("Dialog", 0, 12));
        AddEmployee.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/addWorker32.png")));
        AddEmployee.setText("Add Employee");
        AddEmployee.setToolTipText("Add employee to your place");
        AddEmployee.setBorderPainted(false);
        AddEmployee.setContentAreaFilled(false);
        AddEmployee.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        AddEmployee.setMargin(new java.awt.Insets(2, 0, 2, 0));
        AddEmployee.setMaximumSize(new java.awt.Dimension(108, 61));
        AddEmployee.setMinimumSize(new java.awt.Dimension(103, 61));
        AddEmployee.setPreferredSize(new java.awt.Dimension(108, 61));
        AddEmployee.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        AddEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddEmployeeActionPerformed(evt);
            }
        });
        AddEmployee.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        employeeDataIconBar.add(AddEmployee);

        EditEmployee.setFont(new java.awt.Font("Dialog", 0, 12));
        EditEmployee.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/editWorker32.png")));
        EditEmployee.setText("Edit Employee");
        EditEmployee.setToolTipText("Edit the properties of your employee");
        EditEmployee.setBorderPainted(false);
        EditEmployee.setContentAreaFilled(false);
        EditEmployee.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        EditEmployee.setMargin(new java.awt.Insets(2, 0, 2, 0));
        EditEmployee.setMaximumSize(new java.awt.Dimension(108, 61));
        EditEmployee.setMinimumSize(new java.awt.Dimension(103, 61));
        EditEmployee.setPreferredSize(new java.awt.Dimension(108, 61));
        EditEmployee.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        EditEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditEmployeeActionPerformed(evt);
            }
        });
        EditEmployee.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        employeeDataIconBar.add(EditEmployee);

        DeleteEmployee.setFont(new java.awt.Font("Dialog", 0, 12));
        DeleteEmployee.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/deleteWorker32.png")));
        DeleteEmployee.setText("Delete Employee");
        DeleteEmployee.setToolTipText("Delete the properties of your employee");
        DeleteEmployee.setBorderPainted(false);
        DeleteEmployee.setContentAreaFilled(false);
        DeleteEmployee.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DeleteEmployee.setMargin(new java.awt.Insets(2, 0, 2, 0));
        DeleteEmployee.setMaximumSize(new java.awt.Dimension(108, 61));
        DeleteEmployee.setPreferredSize(new java.awt.Dimension(108, 61));
        DeleteEmployee.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        DeleteEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteEmployeeActionPerformed(evt);
            }
        });
        DeleteEmployee.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        employeeDataIconBar.add(DeleteEmployee);

        iconBar.add(employeeDataIconBar, "employee");

        SuplierIconBar.setLayout(new javax.swing.BoxLayout(SuplierIconBar, javax.swing.BoxLayout.X_AXIS));

        AddSuplier.setFont(new java.awt.Font("Dialog", 0, 12));
        AddSuplier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/addWorker32.png")));
        AddSuplier.setText("Add Suplier");
        AddSuplier.setToolTipText("Add suplier");
        AddSuplier.setBorderPainted(false);
        AddSuplier.setContentAreaFilled(false);
        AddSuplier.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        AddSuplier.setMargin(new java.awt.Insets(2, 0, 2, 0));
        AddSuplier.setMaximumSize(new java.awt.Dimension(100, 61));
        AddSuplier.setMinimumSize(new java.awt.Dimension(63, 61));
        AddSuplier.setPreferredSize(new java.awt.Dimension(80, 61));
        AddSuplier.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        AddSuplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddSuplierActionPerformed(evt);
            }
        });
        AddSuplier.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        SuplierIconBar.add(AddSuplier);

        EditSuplier.setFont(new java.awt.Font("Dialog", 0, 12));
        EditSuplier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/editWorker32.png")));
        EditSuplier.setText("Edit Suplier");
        EditSuplier.setToolTipText("Edit the properties of suplier");
        EditSuplier.setBorderPainted(false);
        EditSuplier.setContentAreaFilled(false);
        EditSuplier.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        EditSuplier.setMargin(new java.awt.Insets(2, 0, 2, 0));
        EditSuplier.setMaximumSize(new java.awt.Dimension(100, 61));
        EditSuplier.setMinimumSize(new java.awt.Dimension(63, 61));
        EditSuplier.setPreferredSize(new java.awt.Dimension(80, 61));
        EditSuplier.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        EditSuplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditSuplierActionPerformed(evt);
            }
        });
        EditSuplier.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        SuplierIconBar.add(EditSuplier);

        DeleteSuplier.setFont(new java.awt.Font("Dialog", 0, 12));
        DeleteSuplier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/deleteWorker32.png")));
        DeleteSuplier.setText("Delete Suplier");
        DeleteSuplier.setToolTipText("Delete suplier");
        DeleteSuplier.setBorderPainted(false);
        DeleteSuplier.setContentAreaFilled(false);
        DeleteSuplier.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DeleteSuplier.setMargin(new java.awt.Insets(2, 0, 2, 0));
        DeleteSuplier.setMaximumSize(new java.awt.Dimension(100, 61));
        DeleteSuplier.setMinimumSize(new java.awt.Dimension(63, 61));
        DeleteSuplier.setPreferredSize(new java.awt.Dimension(80, 61));
        DeleteSuplier.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        DeleteSuplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteSuplierActionPerformed(evt);
            }
        });
        DeleteSuplier.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        SuplierIconBar.add(DeleteSuplier);

        iconBar.add(SuplierIconBar, "suplier");

        salesmanIconBar.setLayout(new javax.swing.BoxLayout(salesmanIconBar, javax.swing.BoxLayout.X_AXIS));

        AddSalesman.setFont(new java.awt.Font("Dialog", 0, 12));
        AddSalesman.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/addWorker32.png")));
        AddSalesman.setText("Add Salesman");
        AddSalesman.setToolTipText("Add employee to your place");
        AddSalesman.setBorderPainted(false);
        AddSalesman.setContentAreaFilled(false);
        AddSalesman.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        AddSalesman.setMargin(new java.awt.Insets(2, 0, 2, 0));
        AddSalesman.setMaximumSize(new java.awt.Dimension(103, 61));
        AddSalesman.setMinimumSize(new java.awt.Dimension(103, 61));
        AddSalesman.setPreferredSize(new java.awt.Dimension(101, 61));
        AddSalesman.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        AddSalesman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddSalesmanActionPerformed(evt);
            }
        });
        AddSalesman.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        salesmanIconBar.add(AddSalesman);

        EditSalesman.setFont(new java.awt.Font("Dialog", 0, 12));
        EditSalesman.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/editWorker32.png")));
        EditSalesman.setText("Edit Salesman");
        EditSalesman.setToolTipText("Edit the properties of your employee");
        EditSalesman.setBorderPainted(false);
        EditSalesman.setContentAreaFilled(false);
        EditSalesman.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        EditSalesman.setMargin(new java.awt.Insets(2, 0, 2, 0));
        EditSalesman.setMaximumSize(new java.awt.Dimension(103, 61));
        EditSalesman.setMinimumSize(new java.awt.Dimension(103, 61));
        EditSalesman.setPreferredSize(new java.awt.Dimension(101, 61));
        EditSalesman.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        EditSalesman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditSalesmanActionPerformed(evt);
            }
        });
        EditSalesman.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        salesmanIconBar.add(EditSalesman);

        DeleteSalesman.setFont(new java.awt.Font("Dialog", 0, 12));
        DeleteSalesman.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/deleteWorker32.png")));
        DeleteSalesman.setText("Delete Salesman");
        DeleteSalesman.setToolTipText("Delete the properties of your employee");
        DeleteSalesman.setBorderPainted(false);
        DeleteSalesman.setContentAreaFilled(false);
        DeleteSalesman.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DeleteSalesman.setMargin(new java.awt.Insets(2, 0, 2, 0));
        DeleteSalesman.setMaximumSize(new java.awt.Dimension(103, 61));
        DeleteSalesman.setMinimumSize(new java.awt.Dimension(103, 61));
        DeleteSalesman.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        DeleteSalesman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteSalesmanActionPerformed(evt);
            }
        });
        DeleteSalesman.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        salesmanIconBar.add(DeleteSalesman);

        iconBar.add(salesmanIconBar, "salesman");

        HomeIconBar.setLayout(new javax.swing.BoxLayout(HomeIconBar, javax.swing.BoxLayout.X_AXIS));

        AddWarehouse.setFont(new java.awt.Font("Dialog", 0, 12));
        AddWarehouse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/addWorker32.png")));
        AddWarehouse.setText("Add Warehouse");
        AddWarehouse.setToolTipText("Add warehouse");
        AddWarehouse.setBorderPainted(false);
        AddWarehouse.setContentAreaFilled(false);
        AddWarehouse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        AddWarehouse.setMargin(new java.awt.Insets(2, 0, 2, 0));
        AddWarehouse.setMaximumSize(new java.awt.Dimension(115, 61));
        AddWarehouse.setMinimumSize(new java.awt.Dimension(100, 61));
        AddWarehouse.setPreferredSize(new java.awt.Dimension(104, 61));
        AddWarehouse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        AddWarehouse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddWarehouseActionPerformed(evt);
            }
        });
        AddWarehouse.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        HomeIconBar.add(AddWarehouse);

        EditWarehouse.setFont(new java.awt.Font("Dialog", 0, 12));
        EditWarehouse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/editWorker32.png")));
        EditWarehouse.setText("Edit Warehouse");
        EditWarehouse.setToolTipText("Edit the warehouse data");
        EditWarehouse.setBorderPainted(false);
        EditWarehouse.setContentAreaFilled(false);
        EditWarehouse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        EditWarehouse.setMargin(new java.awt.Insets(2, 0, 2, 0));
        EditWarehouse.setMaximumSize(new java.awt.Dimension(115, 61));
        EditWarehouse.setMinimumSize(new java.awt.Dimension(100, 61));
        EditWarehouse.setPreferredSize(new java.awt.Dimension(104, 61));
        EditWarehouse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        EditWarehouse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditWarehouseActionPerformed(evt);
            }
        });
        EditWarehouse.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        HomeIconBar.add(EditWarehouse);

        DeleteWarehouse.setFont(new java.awt.Font("Dialog", 0, 12));
        DeleteWarehouse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/deleteWorker32.png")));
        DeleteWarehouse.setText("Delete Warehouse");
        DeleteWarehouse.setToolTipText("Delete the warehouse data");
        DeleteWarehouse.setBorderPainted(false);
        DeleteWarehouse.setContentAreaFilled(false);
        DeleteWarehouse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DeleteWarehouse.setMargin(new java.awt.Insets(2, 0, 2, 0));
        DeleteWarehouse.setMaximumSize(new java.awt.Dimension(115, 61));
        DeleteWarehouse.setMinimumSize(new java.awt.Dimension(100, 61));
        DeleteWarehouse.setPreferredSize(new java.awt.Dimension(104, 61));
        DeleteWarehouse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        DeleteWarehouse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteWarehouseActionPerformed(evt);
            }
        });
        DeleteWarehouse.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        HomeIconBar.add(DeleteWarehouse);

        AddContainer.setFont(new java.awt.Font("Dialog", 0, 12));
        AddContainer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/addContainer32.png")));
        AddContainer.setText("Add Container");
        AddContainer.setToolTipText("Add container data");
        AddContainer.setBorderPainted(false);
        AddContainer.setContentAreaFilled(false);
        AddContainer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        AddContainer.setMargin(new java.awt.Insets(2, 0, 2, 0));
        AddContainer.setMaximumSize(new java.awt.Dimension(115, 61));
        AddContainer.setMinimumSize(new java.awt.Dimension(100, 61));
        AddContainer.setPreferredSize(new java.awt.Dimension(104, 61));
        AddContainer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        AddContainer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddContainerActionPerformed(evt);
            }
        });
        AddContainer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        HomeIconBar.add(AddContainer);

        EditContainer.setFont(new java.awt.Font("Dialog", 0, 12));
        EditContainer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/editContainer32.png")));
        EditContainer.setText("Edit Container");
        EditContainer.setToolTipText("Edit container data");
        EditContainer.setBorderPainted(false);
        EditContainer.setContentAreaFilled(false);
        EditContainer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        EditContainer.setMargin(new java.awt.Insets(2, 0, 2, 0));
        EditContainer.setMaximumSize(new java.awt.Dimension(115, 61));
        EditContainer.setMinimumSize(new java.awt.Dimension(100, 61));
        EditContainer.setPreferredSize(new java.awt.Dimension(104, 61));
        EditContainer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        EditContainer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditContainerActionPerformed(evt);
            }
        });
        EditContainer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        HomeIconBar.add(EditContainer);

        DeleteContainer.setFont(new java.awt.Font("Dialog", 0, 12));
        DeleteContainer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/deleteContainer32.png")));
        DeleteContainer.setText("Delete Container");
        DeleteContainer.setToolTipText("Delete the container data");
        DeleteContainer.setBorderPainted(false);
        DeleteContainer.setContentAreaFilled(false);
        DeleteContainer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DeleteContainer.setMargin(new java.awt.Insets(2, 0, 2, 0));
        DeleteContainer.setMaximumSize(new java.awt.Dimension(115, 61));
        DeleteContainer.setMinimumSize(new java.awt.Dimension(100, 61));
        DeleteContainer.setPreferredSize(new java.awt.Dimension(104, 61));
        DeleteContainer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        DeleteContainer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteContainerActionPerformed(evt);
            }
        });
        DeleteContainer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        HomeIconBar.add(DeleteContainer);

        EditHome.setFont(new java.awt.Font("Dialog", 0, 12));
        EditHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/editHome32.png")));
        EditHome.setText("Edit Home");
        EditHome.setToolTipText("Edit home data");
        EditHome.setBorderPainted(false);
        EditHome.setContentAreaFilled(false);
        EditHome.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        EditHome.setMargin(new java.awt.Insets(2, 0, 2, 0));
        EditHome.setMaximumSize(new java.awt.Dimension(115, 61));
        EditHome.setMinimumSize(new java.awt.Dimension(100, 61));
        EditHome.setPreferredSize(new java.awt.Dimension(104, 61));
        EditHome.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        EditHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditHomeActionPerformed(evt);
            }
        });
        EditHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        HomeIconBar.add(EditHome);

        iconBar.add(HomeIconBar, "home");

        CustomerIconBar.setLayout(new javax.swing.BoxLayout(CustomerIconBar, javax.swing.BoxLayout.X_AXIS));

        AddCustomer.setFont(new java.awt.Font("Dialog", 0, 12));
        AddCustomer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/addWorker32.png")));
        AddCustomer.setText("Add Customer");
        AddCustomer.setToolTipText("Add customer data");
        AddCustomer.setBorderPainted(false);
        AddCustomer.setContentAreaFilled(false);
        AddCustomer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        AddCustomer.setMargin(new java.awt.Insets(2, 0, 2, 0));
        AddCustomer.setMaximumSize(new java.awt.Dimension(108, 61));
        AddCustomer.setMinimumSize(new java.awt.Dimension(101, 61));
        AddCustomer.setPreferredSize(new java.awt.Dimension(108, 61));
        AddCustomer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        AddCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddCustomerActionPerformed(evt);
            }
        });
        AddCustomer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        CustomerIconBar.add(AddCustomer);

        EditCustomer.setFont(new java.awt.Font("Dialog", 0, 12));
        EditCustomer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/editWorker32.png")));
        EditCustomer.setText("Edit Customer");
        EditCustomer.setToolTipText("Edit the customer data");
        EditCustomer.setBorderPainted(false);
        EditCustomer.setContentAreaFilled(false);
        EditCustomer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        EditCustomer.setMargin(new java.awt.Insets(2, 0, 2, 0));
        EditCustomer.setMaximumSize(new java.awt.Dimension(108, 61));
        EditCustomer.setMinimumSize(new java.awt.Dimension(101, 61));
        EditCustomer.setPreferredSize(new java.awt.Dimension(108, 61));
        EditCustomer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        EditCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditCustomerActionPerformed(evt);
            }
        });
        EditCustomer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        CustomerIconBar.add(EditCustomer);

        DeleteCustomer.setFont(new java.awt.Font("Dialog", 0, 12));
        DeleteCustomer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/deleteWorker32.png")));
        DeleteCustomer.setText("Delete Customer");
        DeleteCustomer.setToolTipText("Delete the customer data");
        DeleteCustomer.setBorderPainted(false);
        DeleteCustomer.setContentAreaFilled(false);
        DeleteCustomer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DeleteCustomer.setMargin(new java.awt.Insets(2, 0, 2, 0));
        DeleteCustomer.setMaximumSize(new java.awt.Dimension(108, 61));
        DeleteCustomer.setMinimumSize(new java.awt.Dimension(101, 61));
        DeleteCustomer.setPreferredSize(new java.awt.Dimension(108, 61));
        DeleteCustomer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        DeleteCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteCustomerActionPerformed(evt);
            }
        });
        DeleteCustomer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        CustomerIconBar.add(DeleteCustomer);

        iconBar.add(CustomerIconBar, "customer");

        CommisionerIconBar.setLayout(new javax.swing.BoxLayout(CommisionerIconBar, javax.swing.BoxLayout.X_AXIS));

        AddCommisioner.setFont(new java.awt.Font("Dialog", 0, 12));
        AddCommisioner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/addWorker32.png")));
        AddCommisioner.setText("Add Commisioner");
        AddCommisioner.setToolTipText("Add commisioner data");
        AddCommisioner.setBorderPainted(false);
        AddCommisioner.setContentAreaFilled(false);
        AddCommisioner.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        AddCommisioner.setMargin(new java.awt.Insets(2, 0, 2, 0));
        AddCommisioner.setMaximumSize(new java.awt.Dimension(128, 61));
        AddCommisioner.setMinimumSize(new java.awt.Dimension(122, 61));
        AddCommisioner.setPreferredSize(new java.awt.Dimension(122, 61));
        AddCommisioner.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        AddCommisioner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddCommisionerActionPerformed(evt);
            }
        });
        AddCommisioner.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        CommisionerIconBar.add(AddCommisioner);

        EditCommisioner.setFont(new java.awt.Font("Dialog", 0, 12));
        EditCommisioner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/editWorker32.png")));
        EditCommisioner.setText("Edit Commisioner");
        EditCommisioner.setToolTipText("Edit the commisioner data");
        EditCommisioner.setBorderPainted(false);
        EditCommisioner.setContentAreaFilled(false);
        EditCommisioner.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        EditCommisioner.setMargin(new java.awt.Insets(2, 0, 2, 0));
        EditCommisioner.setMaximumSize(new java.awt.Dimension(128, 61));
        EditCommisioner.setMinimumSize(new java.awt.Dimension(122, 61));
        EditCommisioner.setPreferredSize(new java.awt.Dimension(122, 61));
        EditCommisioner.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        EditCommisioner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditCommisionerActionPerformed(evt);
            }
        });
        EditCommisioner.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        CommisionerIconBar.add(EditCommisioner);

        DeleteCommisioner.setFont(new java.awt.Font("Dialog", 0, 12));
        DeleteCommisioner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/deleteWorker32.png")));
        DeleteCommisioner.setText("Delete Commisioner");
        DeleteCommisioner.setToolTipText("Delete the commisioner data");
        DeleteCommisioner.setBorderPainted(false);
        DeleteCommisioner.setContentAreaFilled(false);
        DeleteCommisioner.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DeleteCommisioner.setMargin(new java.awt.Insets(2, 0, 2, 0));
        DeleteCommisioner.setMaximumSize(new java.awt.Dimension(128, 61));
        DeleteCommisioner.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        DeleteCommisioner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteCommisionerActionPerformed(evt);
            }
        });
        DeleteCommisioner.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        CommisionerIconBar.add(DeleteCommisioner);

        iconBar.add(CommisionerIconBar, "commisioner");

        EditTransIconBar.setLayout(new javax.swing.BoxLayout(EditTransIconBar, javax.swing.BoxLayout.X_AXIS));

        EditTransactionButton.setFont(new java.awt.Font("Dialog", 0, 12));
        EditTransactionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/editTransaction32.png")));
        EditTransactionButton.setText("Edit Transaction");
        EditTransactionButton.setToolTipText("Edit the transaction");
        EditTransactionButton.setBorderPainted(false);
        EditTransactionButton.setContentAreaFilled(false);
        EditTransactionButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        EditTransactionButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        EditTransactionButton.setMaximumSize(new java.awt.Dimension(114, 61));
        EditTransactionButton.setMinimumSize(new java.awt.Dimension(103, 61));
        EditTransactionButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        EditTransactionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditTransactionButtonActionPerformed(evt);
            }
        });
        EditTransactionButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        EditTransIconBar.add(EditTransactionButton);

        DeleteTransaction.setFont(new java.awt.Font("Dialog", 0, 12));
        DeleteTransaction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/deleteTransaction32.png")));
        DeleteTransaction.setText("Delete Transaction");
        DeleteTransaction.setToolTipText("Delete the transaction");
        DeleteTransaction.setBorderPainted(false);
        DeleteTransaction.setContentAreaFilled(false);
        DeleteTransaction.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DeleteTransaction.setMargin(new java.awt.Insets(2, 0, 2, 0));
        DeleteTransaction.setMaximumSize(new java.awt.Dimension(114, 61));
        DeleteTransaction.setMinimumSize(new java.awt.Dimension(103, 61));
        DeleteTransaction.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        DeleteTransaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteTransactionActionPerformed(evt);
            }
        });
        DeleteTransaction.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        EditTransIconBar.add(DeleteTransaction);

        iconBar.add(EditTransIconBar, "edittrans");

        ProducerIconBar.setLayout(new javax.swing.BoxLayout(ProducerIconBar, javax.swing.BoxLayout.X_AXIS));

        AddProducer.setFont(new java.awt.Font("Dialog", 0, 12));
        AddProducer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/addWorker32.png")));
        AddProducer.setText("Add Producer");
        AddProducer.setToolTipText("Add customer data");
        AddProducer.setBorderPainted(false);
        AddProducer.setContentAreaFilled(false);
        AddProducer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        AddProducer.setMargin(new java.awt.Insets(2, 0, 2, 0));
        AddProducer.setMaximumSize(new java.awt.Dimension(108, 61));
        AddProducer.setMinimumSize(new java.awt.Dimension(101, 61));
        AddProducer.setPreferredSize(new java.awt.Dimension(105, 61));
        AddProducer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        AddProducer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddProducerActionPerformed(evt);
            }
        });
        AddProducer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        ProducerIconBar.add(AddProducer);

        EditProducer.setFont(new java.awt.Font("Dialog", 0, 12));
        EditProducer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/editWorker32.png")));
        EditProducer.setText("Edit Producer");
        EditProducer.setToolTipText("Edit the customer data");
        EditProducer.setBorderPainted(false);
        EditProducer.setContentAreaFilled(false);
        EditProducer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        EditProducer.setMargin(new java.awt.Insets(2, 0, 2, 0));
        EditProducer.setMaximumSize(new java.awt.Dimension(108, 61));
        EditProducer.setMinimumSize(new java.awt.Dimension(101, 61));
        EditProducer.setPreferredSize(new java.awt.Dimension(105, 61));
        EditProducer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        EditProducer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditProducerActionPerformed(evt);
            }
        });
        EditProducer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        ProducerIconBar.add(EditProducer);

        DeleteProducer.setFont(new java.awt.Font("Dialog", 0, 12));
        DeleteProducer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/exzoost/images/deleteWorker32.png")));
        DeleteProducer.setText("Delete Producer");
        DeleteProducer.setToolTipText("Delete the customer data");
        DeleteProducer.setBorderPainted(false);
        DeleteProducer.setContentAreaFilled(false);
        DeleteProducer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        DeleteProducer.setMargin(new java.awt.Insets(2, 0, 2, 0));
        DeleteProducer.setMaximumSize(new java.awt.Dimension(108, 61));
        DeleteProducer.setMinimumSize(new java.awt.Dimension(101, 61));
        DeleteProducer.setPreferredSize(new java.awt.Dimension(105, 61));
        DeleteProducer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        DeleteProducer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteProducerActionPerformed(evt);
            }
        });
        DeleteProducer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                IconBarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                IconBarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                IconBarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                IconBarMouseReleased(evt);
            }
        });

        ProducerIconBar.add(DeleteProducer);

        iconBar.add(ProducerIconBar, "producer");

        getContentPane().add(iconBar, java.awt.BorderLayout.NORTH);

        treeScrollPane.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 204, 204), 1, true));
        treeGUI.setRowHeight(32);
        treeGUI.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeGUIValueChanged(evt);
            }
        });

        treeScrollPane.setViewportView(treeGUI);

        getContentPane().add(treeScrollPane, java.awt.BorderLayout.WEST);

        mainFile.setMnemonic('f');
        mainFile.setText("File");
        mainSaveMenuItem.setText("Save");
        mainSaveMenuItem.setToolTipText("Save");
        mainSaveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainSaveMenuItemActionPerformed(evt);
            }
        });

        mainFile.add(mainSaveMenuItem);

        mainPrintMenuItem.setText("Print");
        mainPrintMenuItem.setToolTipText("Print");
        mainPrintMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainPrintMenuItemActionPerformed(evt);
            }
        });

        mainFile.add(mainPrintMenuItem);

        mainExportMenuItem.setText("Export...");
        mainExportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainExportMenuItemActionPerformed(evt);
            }
        });

        mainFile.add(mainExportMenuItem);

        mainExitMenuItem.setMnemonic('x');
        mainExitMenuItem.setText("Exit");
        mainExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainExitMenuItemActionPerformed(evt);
            }
        });

        mainFile.add(mainExitMenuItem);

        mainMenuBar.add(mainFile);

        mainTools.setMnemonic('t');
        mainTools.setText("Tools");
        Preferences.setMnemonic('p');
        Preferences.setText("Preferences");
        Preferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PreferencesActionPerformed(evt);
            }
        });

        mainTools.add(Preferences);

        mainMenuBar.add(mainTools);

        mainHelp.setMnemonic('h');
        mainHelp.setText("Help");
        About.setMnemonic('a');
        About.setText("About");
        About.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutActionPerformed(evt);
            }
        });

        mainHelp.add(About);

        mainMenuBar.add(mainHelp);

        setJMenuBar(mainMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ReactiveSalesmanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReactiveSalesmanActionPerformed
        int row = salesmanTable.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(salesmanTable.getValueAt(row,0)==null)
                return;
        }
        
        int n = JOptionPane.showConfirmDialog( this, "Do you really want to reactive this " +
                    "salesman data?", "Question",
                    JOptionPane.YES_NO_OPTION );
        
        if(n==JOptionPane.YES_OPTION) {
            if( salesman.reactiveSalesman(
                    ((Integer)salesmanTable.getModel().getValueAt(row,8)).intValue() ) ) {
                
                    javax.swing.table.DefaultTableModel model = 
                            (javax.swing.table.DefaultTableModel)salesmanTable.getModel();
                    model.removeRow(salesmanTable.getSelectedRow());
            }
            else {
                    JOptionPane.showMessageDialog(this,"Error reactivating salesman data! " +
                            "See log file for detail!", "Warning", JOptionPane.WARNING_MESSAGE );    
            }
        }
    }//GEN-LAST:event_ReactiveSalesmanActionPerformed

    private void ReactiveEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReactiveEmployeeActionPerformed
        int row = employeeTable.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(employeeTable.getValueAt(row,0)==null)
                return;
        }
        
        int n = JOptionPane.showConfirmDialog( this, "Do you really want to reactive this " +
                    "employee data?", "Question",
                    JOptionPane.YES_NO_OPTION );
        
        if(n==JOptionPane.YES_OPTION) {
            if( employee.reactiveEmployee(
                    ((Integer)employeeTable.getModel().getValueAt(row,9)).intValue() ) ) {
                
                    javax.swing.table.DefaultTableModel model = 
                            (javax.swing.table.DefaultTableModel)employeeTable.getModel();
                    model.removeRow(employeeTable.getSelectedRow());
            }
            else {
                    JOptionPane.showMessageDialog(this,"Error reactivating employee data! " +
                            "See log file for detail!", "Warning", JOptionPane.WARNING_MESSAGE );
            }
        }
    }//GEN-LAST:event_ReactiveEmployeeActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        mainExitMenuItemActionPerformed(null);
    }//GEN-LAST:event_formWindowClosing

    private void PageItemTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PageItemTFActionPerformed
        int offsettemp = offset;
        try {
            offset = Integer.parseInt(PageItemTF.getText());
            if(offset>getPageEnd()||offset<1) {
                throw new NumberFormatException();
            }
            ClearAllTable(stuffTable);
            inventory.initializeDataItems( stuffTable, 
                ((String)warehouseComboBox.getSelectedItem()).toLowerCase(), 
                viewOrderBy( (String)OrderByCoB.getSelectedItem() ),
                (offset-1) * displaylimit,
                displaylimit );
        }
        catch( NumberFormatException e ) {
            offset = offsettemp;
        }
        PageItemTF.setText("" + offset);
    }//GEN-LAST:event_PageItemTFActionPerformed

    private void BeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BeginActionPerformed
        try {
            offset = 1;
            PageItemTF.setText("" + offset);
        }
        catch( NumberFormatException e ) {
        }
        Forward.setEnabled(true);
        End.setEnabled(true);
        Back.setEnabled(false);
        Begin.setEnabled(false);
        PageItemTFActionPerformed(null);
    }//GEN-LAST:event_BeginActionPerformed

    private void BackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackActionPerformed
        try {
            offset = Integer.parseInt(PageItemTF.getText());
            offset--;
            PageItemTF.setText("" + offset);
        }
        catch( NumberFormatException e ) {
        }
        Forward.setEnabled(true);
        End.setEnabled(true);
        if(offset==1) {
            Back.setEnabled(false);
            Begin.setEnabled(false);
        }
        PageItemTFActionPerformed(null);
    }//GEN-LAST:event_BackActionPerformed

    private void EndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EndActionPerformed
        try {
            offset = getPageEnd();
            PageItemTF.setText("" + offset);
        }
        catch( NumberFormatException e ) {
        }
        Begin.setEnabled(true);
        Back.setEnabled(true);
        Forward.setEnabled(false);
        End.setEnabled(false);
        PageItemTFActionPerformed(null);
    }//GEN-LAST:event_EndActionPerformed

    private void ForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ForwardActionPerformed
        try {
            offset = Integer.parseInt(PageItemTF.getText());
            offset++;
            PageItemTF.setText("" + offset);
        }
        catch( NumberFormatException e ) {
        }
        if(offset>=getPageEnd()) {
            Forward.setEnabled(false);
            End.setEnabled(false);
        }
        Begin.setEnabled(true);
        Back.setEnabled(true);
        PageItemTFActionPerformed(null);
    }//GEN-LAST:event_ForwardActionPerformed

    private void PreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreferencesActionPerformed
//        String item1 = "Ocean";
//        String item2 = "Liquid";
//        String item3 = "Metouia";
//        String item4 = "Plastic";
//        String item5 = "Plastic3D";
//        String item6 = "PlasticXP";
//        String item7 = "Windows";
//        String item8 = "Compiere";
//        Object[] possibilities = {item1, item2, item3, item4, item5, item6, item7, item8};
//        String s = (String)JOptionPane.showInputDialog(
//                            this,
//                            "Choose what theme do you want to use!",
//                            "Choose Look and Feel!",
//                            JOptionPane.PLAIN_MESSAGE,
//                            null,
//                            possibilities,
//                            "ham");
//
//        //If a string was returned, say so.
//        if ((s != null) && (s.length() > 0)) {
//            xmlhandler.setUpLookAndFeel(s);
//        }
        new PreferencesDialog( this, true ).setVisible(true);
    }//GEN-LAST:event_PreferencesActionPerformed

    private void SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchActionPerformed
        new SearchTransactionDialog( this, true, transactionsEP, conn ).setVisible(true);
    }//GEN-LAST:event_SearchActionPerformed

    private void EditTransactionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditTransactionButtonActionPerformed
        String edittrans = "";
        
        edittrans = (String)EditTransCoB.getSelectedItem();
        
        int row = 0;
        
        if(edittrans.equals("Sale Transaction")) {
            row = SaleTable.getSelectedRow();
            if(SaleTable.getValueAt(row,0)==null)
                return;
            new EditSaleTransactionDialog( this, true, (String)SaleTable.getModel().getValueAt(row,0),
                    conn ).setVisible(true);
            ClearAllTable(SaleTable);
        }
        else if(edittrans.equals("Purchase Transaction")) {
            row = PurchaseTable.getSelectedRow();
            if(PurchaseTable.getValueAt(row,0)==null)
                return;
            new EditPurchaseTransactionDialog( this, true, 
                    (String)PurchaseTable.getModel().getValueAt(row,0), conn ).setVisible(true);
            ClearAllTable(PurchaseTable);
        }
        else if(edittrans.equals("Salary Payment Transaction")) {
            row = SalaryTable.getSelectedRow();
            if(SalaryTable.getValueAt(row,0)==null)
                return;
            new EditSalaryTransactionDialog( this, true, SalaryTable, conn ).setVisible(true);
            ClearAllTable(SalaryTable);
        }
        else if(edittrans.equals("Income Transaction")) {
            row = IncomeTable.getSelectedRow();
            if(IncomeTable.getValueAt(row,0)==null)
                return;
            new EditIncomeDialog( this, true, IncomeTable, conn ).setVisible(true);
            ClearAllTable(IncomeTable);
        }
        else if(edittrans.equals("Outcome Transaction")) {
            row = OutcomeTable.getSelectedRow();
            if(OutcomeTable.getValueAt(row,0)==null)
                return;
            new EditOutcomeDialog( this, true, OutcomeTable, conn ).setVisible(true);
            ClearAllTable(OutcomeTable);
        }
        else if(edittrans.equals("Debt")) {
            row = DebtTable.getSelectedRow();
            if(DebtTable.getValueAt(row,0)==null)
                return;
            new EditDebtCreditDialog( this, true, DebtTable, "buy", conn ).setVisible(true);
            ClearAllTable(DebtTable);
        }
        else if(edittrans.equals("Credit")) {
            row = CreditTable.getSelectedRow();
            if(CreditTable.getValueAt(row,0)==null)
                return;
            new EditDebtCreditDialog( this, true, CreditTable, "sale", conn ).setVisible(true);
            ClearAllTable(CreditTable);
        }
        
        EditTransCoBActionPerformed(null);
    }//GEN-LAST:event_EditTransactionButtonActionPerformed

    private void mainExportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainExportMenuItemActionPerformed
        new AskSuperUserRight( this, true, conn ).setVisible(true);
    }//GEN-LAST:event_mainExportMenuItemActionPerformed

    private void EditHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditHomeActionPerformed
        new EditHomeDialog( this, true ).setVisible(true);
        HomeShowProfile();
        TreePath path = treeGUI.getPathForRow(0); //get the root node
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
        
        if(node.isRoot()) {
            node.setUserObject( new treeInfo(xmlhandler.elementCompanyProfile("name")) );
            treeGUI.updateUI();
        }
    }//GEN-LAST:event_EditHomeActionPerformed

    private void DeleteContainerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteContainerActionPerformed
        int row = ContainerTB.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(ContainerTB.getValueAt(row,0)==null)
                return;
        }
        
        //what is the answer from user
        int n = JOptionPane.showConfirmDialog( this, "Do you really want to delete this " +
                "container data?", "Question",
                JOptionPane.YES_NO_OPTION );
        
        //he really wants to delete the data from db
        if(n==JOptionPane.YES_OPTION) {
            //delete data from db
            if(!containerdb.deleteContainer(ContainerTB)) {
                JOptionPane.showMessageDialog(null,"Error deleting container! See log file for " +
                        "detail!", "Warning", JOptionPane.WARNING_MESSAGE );
            }
        }
        //he does not want to delete
        else if(n==JOptionPane.NO_OPTION) {
            return;
        }
    }//GEN-LAST:event_DeleteContainerActionPerformed

    private void EditContainerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditContainerActionPerformed
        int row = ContainerTB.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(ContainerTB.getValueAt(row,0)==null)
                return;
        }
        
        new EditContainerDialog( this, true, ContainerTB, conn ).setVisible(true);
    }//GEN-LAST:event_EditContainerActionPerformed

    private void AddContainerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddContainerActionPerformed
        if(ContainerTB.getRowCount()==6) {
            JOptionPane.showMessageDialog(null,"You can not add container more than 6!",
                    "Warning", JOptionPane.WARNING_MESSAGE );
        }
        new AddContainerDialog( this, true, ContainerTB, conn ).setVisible(true);
    }//GEN-LAST:event_AddContainerActionPerformed

    private void OrderByCoBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OrderByCoBActionPerformed
//        inventory.initializeDataItems( stuffTable, 
//                ((String)warehouseComboBox.getSelectedItem()).toLowerCase(), 
//                viewOrderBy( (String)OrderByCoB.getSelectedItem() ),
//                (offset-1) * 32 );
        PageItemTFActionPerformed(null);
    }//GEN-LAST:event_OrderByCoBActionPerformed

    private void DeleteProducerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteProducerActionPerformed
        int row = ProducerTable.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(ProducerTable.getValueAt(row,0)==null)
                return;
        }
        
        //what is the answer from user
        int n = JOptionPane.showConfirmDialog( this, "Do you really want to delete the " +
                "producer data?", "Question",
                JOptionPane.YES_NO_OPTION );
        
        //he really wants to delete the data from db
        if(n==JOptionPane.YES_OPTION) {
            //delete data from db
            if(producer.deleteProducer(ProducerTable)==true) {
                
                //delete row from jtable
                javax.swing.table.DefaultTableModel model = 
                        (javax.swing.table.DefaultTableModel)ProducerTable.getModel();
                model.removeRow(ProducerTable.getSelectedRow());
            }
            else {
                JOptionPane.showMessageDialog(null,"Error deleting producer data! See log file " +
                    "for detail!", "Warning", JOptionPane.WARNING_MESSAGE );
            }
        }
        //he does not want to delete
        else if(n==JOptionPane.NO_OPTION) {
            return;
        }
    }//GEN-LAST:event_DeleteProducerActionPerformed

    private void EditProducerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditProducerActionPerformed
        new EditProducerDialog( this, true, ProducerTable, conn ).setVisible(true);
    }//GEN-LAST:event_EditProducerActionPerformed

    private void AddProducerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddProducerActionPerformed
        new AddProducerDialog( this, true, ProducerTable, conn ).setVisible(true);
    }//GEN-LAST:event_AddProducerActionPerformed

    private void DisplayCommentProducerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisplayCommentProducerActionPerformed
        //display comment if it is selected
        if(DisplayCommentProducer.isSelected()) {
            ProducerTable.addColumn(producertc);
        }
        //don't display comment if it is not selected
        else {
            ProducerTable.removeColumn(producertc);
        }
    }//GEN-LAST:event_DisplayCommentProducerActionPerformed

    private void DeleteWarehouseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteWarehouseActionPerformed
        int row = WarehouseTB.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(WarehouseTB.getValueAt(row,0)==null)
                return;
        }
        
        int n;
        
        //what is the answer????
        n = JOptionPane.showConfirmDialog( this, "Do you really want to delete " +
                "this warehouse?\n It will destroy all items in this warehouse.", "Question",
                JOptionPane.YES_NO_OPTION );

        //damn, he really want to delete the data from database
        if(n==JOptionPane.YES_OPTION) {
            //delete from db
            if(!warehousedb.deleteWarehouse(WarehouseTB)) {
                JOptionPane.showMessageDialog(null,"Error deleting warehouse! See log file for " +
                        "detail!", "Warning", JOptionPane.WARNING_MESSAGE );
            }
        }
        //no he doesn't want to
        else if(n==JOptionPane.NO_OPTION) {
            return;
        }
    }//GEN-LAST:event_DeleteWarehouseActionPerformed

    private void EditWarehouseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditWarehouseActionPerformed
        int row = WarehouseTB.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(WarehouseTB.getValueAt(row,0)==null)
                return;
        }
        
        new EditWarehouseDialog( this, true, WarehouseTB, conn ).setVisible(true);
    }//GEN-LAST:event_EditWarehouseActionPerformed

    private void AddWarehouseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddWarehouseActionPerformed
        if(WarehouseTB.getRowCount()==6) {
            JOptionPane.showMessageDialog(null,"You can not add warehouse more than 6!",
                    "Warning", JOptionPane.WARNING_MESSAGE );
        }
        new AddWarehouseDialog( this, true, WarehouseTB, conn ).setVisible(true);
    }//GEN-LAST:event_AddWarehouseActionPerformed

    private void mainPrintMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainPrintMenuItemActionPerformed
        String componentname = getVisibleComponent(PanelMainGUI).getName(); 
        if( componentname.equals("employee") ) {
            jasper.TableReport("employee");
        }
        else if( componentname.equals("customer") ) { 
            jasper.TableReport("customer");
        }
        else if( componentname.equals("suplier") ) {
            jasper.TableReport("suplier");
        }
        else if(componentname.equals("salesman")) {
            jasper.TableReport("salesman");
        }
        else if(componentname.equals("commisioner")) {
            jasper.TableReport("commisioner");
        }
        else if(componentname.equals("producer")) {
            jasper.TableReport("producer");
        }
        else if(componentname.equals("transaction")) {
            DocumentRenderer renderer = new DocumentRenderer();
            renderer.print( transactionsEP );
        }
        else if(componentname.equals("item")) {
            String item1 = "Category, Code, Name, Purchase Price, & Sale Price";
            String item2 = "Category, Code, Name, & Quantity";
            String item3 = "Category, Code, Name, Producer, & Comment";
            Object[] possibilities = {item1, item2, item3};
            String s = (String)JOptionPane.showInputDialog(
                                this,
                                "Choose what report do you want to use!",
                                "Choose item data",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                possibilities,
                                "ham");

            //If a string was returned, say so.
            if ((s != null) && (s.length() > 0)) {
                if(s.equals(item1)) {
                    jasper.TableReport("item1");
                }
                if(s.equals(item2)) {
                    jasper.TableReport("item2");
                }
                if(s.equals(item3)) {
                    jasper.TableReport("item3");
                }
            }
        }
        else if(componentname.equals("report")) {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setPrintable((ChartPanel)MainReportPanel.getComponent(0));
            if (printJob.printDialog()) {
                try {
                    printJob.print();
                }
                catch( java.awt.print.PrinterException e ) {
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_mainPrintMenuItemActionPerformed

    private void GeneralReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GeneralReportActionPerformed
        new GeneralReport( this, true, MainReportPanel, conn ).setVisible(true);
    }//GEN-LAST:event_GeneralReportActionPerformed

    private void PurchaseReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PurchaseReportActionPerformed
        new BuyReport( this, true, MainReportPanel, conn ).setVisible(true);
    }//GEN-LAST:event_PurchaseReportActionPerformed

    private void DeleteTransactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteTransactionActionPerformed
        String deltrans = "";
        
        deltrans = (String)EditTransCoB.getSelectedItem();
        
        int row = 0;
        
        if(deltrans.equals("Sale Transaction")) {
            row = SaleTable.getSelectedRow();
            if(SaleTable.getValueAt(row,0)==null)
                return;
        }
        else if(deltrans.equals("Purchase Transaction")) {
            row = PurchaseTable.getSelectedRow();
            if(PurchaseTable.getValueAt(row,0)==null)
                return;
        }
        else if(deltrans.equals("Salary Payment Transaction")) {
            row = SalaryTable.getSelectedRow();
            if(SalaryTable.getValueAt(row,0)==null)
                return;
        }
        else if(deltrans.equals("Income Transaction")) {
            row = IncomeTable.getSelectedRow();
            if(IncomeTable.getValueAt(row,0)==null)
                return;
        }
        else if(deltrans.equals("Outcome Transaction")) {
            row = OutcomeTable.getSelectedRow();
            if(OutcomeTable.getValueAt(row,0)==null)
                return;
        }
        else if(deltrans.equals("Debt")) {
            row = DebtTable.getSelectedRow();
            if(DebtTable.getValueAt(row,0)==null)
                return;
        }
        else if(deltrans.equals("Credit")) {
            row = CreditTable.getSelectedRow();
            if(CreditTable.getValueAt(row,0)==null)
                return;
        }
                
        //make sure user choose valid row
        if(row==-1)
            return;
        
        int n;
        
        //what is the answer????
        n = JOptionPane.showConfirmDialog( this, "Do you really want to delete " +
                "this transaction?", "Question", JOptionPane.YES_NO_OPTION );

        //damn, he really want to delete the data from database
        if(n==JOptionPane.YES_OPTION) {
            //delete from db
            
            if(deltrans.equals("Sale Transaction")) {
                int saleindex = ((Integer)SaleTable.getModel().getValueAt(row,6)).intValue();
                new ChooseWarehouse( this, true, conn, "delete sale", warehouse ).setVisible(true);
                if(!warehouse.toString().equals("Root")) {
                    if(!tr.DeleteSaleTransaction( saleindex, warehouse.toString() )) {
                        JOptionPane.showMessageDialog(null,
                            "Error deleting sale transaction data! See log file " +
                            "for detail!", "Warning", JOptionPane.WARNING_MESSAGE );    
                    }
                    ClearAllTable(SaleTable);
                }
            }
            else if(deltrans.equals("Purchase Transaction")) {
                int purchaseindex = ((Integer)PurchaseTable.getModel().getValueAt(row,6)).intValue();
                new ChooseWarehouse( this, true, conn, "delete purchase", warehouse ).setVisible(true);
                if(!warehouse.toString().equals("Root")
                &&warehousedb.isEnoughSupplyCancelPurchase(
                        purchaseindex,warehouse.toString()) ) {
                    if(!tr.DeletePurchaseTransaction( purchaseindex, warehouse.toString() ) ){
                        JOptionPane.showMessageDialog(null,
                            "Error deleting purchase transaction data! See log file " +
                            "for detail!", "Warning", JOptionPane.WARNING_MESSAGE );    
                    }
                    ClearAllTable(PurchaseTable);
                }
            }
            else if(deltrans.equals("Salary Payment Transaction")) {
                if(!tr.DeleteSalaryTransaction((String)SalaryTable.getValueAt(row,0))) {
                    JOptionPane.showMessageDialog(null,
                            "Error deleting salary payment transaction data! See log file " +
                            "for detail!", "Warning", JOptionPane.WARNING_MESSAGE );    
                }
                ClearAllTable(SalaryTable);
            }
            else if(deltrans.equals("Income Transaction")) {
                if(!tr.DeleteOtherIncome((String)IncomeTable.getValueAt(row,0)) ) {
                    JOptionPane.showMessageDialog(null,
                            "Error deleting income transaction data! See log file " +
                            "for detail!", "Warning", JOptionPane.WARNING_MESSAGE );    
                }
                ClearAllTable(IncomeTable);
            }
            else if(deltrans.equals("Outcome Transaction")) {
                if(!tr.DeleteOutcomeTransaction((String)OutcomeTable.getValueAt(row,0)) ) {
                    JOptionPane.showMessageDialog(null,
                            "Error deleting outcome transaction data! See log file " +
                            "for detail!", "Warning", JOptionPane.WARNING_MESSAGE );    
                }
                ClearAllTable(OutcomeTable);
            }
            else if(deltrans.equals("Debt")||deltrans.equals("Credit")) {
                JOptionPane.showMessageDialog(this, "You can not delete this transaction!", 
                        "Warning", JOptionPane.WARNING_MESSAGE );
            }
        }
        //no he doesn't want to
        else if(n==JOptionPane.NO_OPTION) {
            return;
        }
        
        EditTransCoBActionPerformed(null);
    }//GEN-LAST:event_DeleteTransactionActionPerformed

    private void EditTransCoBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditTransCoBActionPerformed
        //change the jtable according to card layout
        CardLayout cl = (CardLayout)(TransPanel.getLayout());
        
        //sorting by what
        String sorting = (String)SortingEditTransCoB.getSelectedItem();
        
        //what transaction to view
        String edittrans = (String)EditTransCoB.getSelectedItem();
        
        if(edittrans.equals("Sale Transaction")) {
            cl.show(TransPanel,"sale");   
            tr.InitializeSaleEditTransaction(SaleTable, sorting);
        }
        else if(edittrans.equals("Purchase Transaction")) {
            cl.show(TransPanel,"purchase");
            tr.InitializePurchaseEditTransaction(PurchaseTable, sorting);
        }
        else if(edittrans.equals("Salary Payment Transaction")) {
            cl.show(TransPanel,"salary");
            tr.InitializeSalaryEditTransaction(SalaryTable, sorting);
        }
        else if(edittrans.equals("Income Transaction")) {
            cl.show(TransPanel,"income");
            tr.InitializeIncomeEditTransaction(IncomeTable, sorting);
        }
        else if(edittrans.equals("Outcome Transaction")) {
            cl.show(TransPanel,"outcome");
            tr.InitializeOutcomeEditTransaction(OutcomeTable, sorting);
        }
        else if(edittrans.equals("Debt")) {
            cl.show(TransPanel,"debt");
            debtcredit.initializeDataDebit(DebtTable, sorting);
        }
        else if(edittrans.equals("Credit")) {
            cl.show(TransPanel,"credit");
            debtcredit.initializeDataCredit(CreditTable, sorting);
        }
    }//GEN-LAST:event_EditTransCoBActionPerformed

    private void ClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearActionPerformed
        transactionsEP.setText("");
    }//GEN-LAST:event_ClearActionPerformed

    private void mainSaveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainSaveMenuItemActionPerformed
        String componentname = getVisibleComponent(PanelMainGUI).getName(); 
        if( componentname.equals("employee") ) {
            jasper.SaveDialog("employee");
        }
        else if( componentname.equals("customer") ) { 
            jasper.SaveDialog("customer");
        }
        else if( componentname.equals("suplier") ) {
            jasper.SaveDialog("suplier");
        }
        else if(componentname.equals("salesman")) {
            jasper.SaveDialog("salesman");
        }
        else if(componentname.equals("commisioner")) {
            jasper.SaveDialog("commisioner");
        }
        else if(componentname.equals("producer")) {
            jasper.SaveDialog("producer");
        }
        else if(componentname.equals("item")) {
            String item1 = "Category, Code, Name, Purchase Price, & Sale Price";
            String item2 = "Category, Code, Name, & Quantity";
            String item3 = "Category, Code, Name, Producer, & Comment";
            Object[] possibilities = {item1, item2, item3};
            String s = (String)JOptionPane.showInputDialog(
                                this,
                                "Choose what report do you want to use!",
                                "Choose item data",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                possibilities,
                                "ham");

            //If a string was returned, say so.
            if ((s != null) && (s.length() > 0)) {
                if(s.equals(item1)) {
                    jasper.SaveDialog("item1");
                }
                if(s.equals(item2)) {
                    jasper.SaveDialog("item2");
                }
                if(s.equals(item3)) {
                    jasper.SaveDialog("item3");
                }
            }
        }
        else if(componentname.equals("transaction")) {
            HTMLEditorManipulation htmlem = new HTMLEditorManipulation( transactionsEP, conn );
            htmlem.HTMLWrite();
        }
        else if(componentname.equals("report")) {
            reportman.SaveDialog( ((ChartPanel)MainReportPanel.getComponent(0)).getChart() );
        }
    }//GEN-LAST:event_mainSaveMenuItemActionPerformed

    private void propertiesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_propertiesComboBoxActionPerformed
        javax.swing.JComboBox cb;
        
        if(evt!=null)
            cb = (javax.swing.JComboBox)evt.getSource();
        else
            cb = propertiesComboBox;
        
        //if user want to see the quantity & price
        if( ((String)cb.getSelectedItem()).equals("Quantity & Price") ) {
            displayComment.setEnabled(true);
            warehouseComboBox.setEnabled(true);
            //remove the producer and seller column
            stuffTable.removeColumn(produceritem);
            stuffTable.removeColumn(selleritem);
            //remove the length, width, height, volume, primary measurement,
            //secondary measurement
            stuffTable.removeColumn(length);
            stuffTable.removeColumn(height);
            stuffTable.removeColumn(width);
            stuffTable.removeColumn(volume);
            stuffTable.removeColumn(primary);
            stuffTable.removeColumn(secondary);
            stuffTable.removeColumn(measurement);
            
            //update: 3 july 2006, remove s = t, and third measurement columns
            stuffTable.removeColumn(s_t);
            stuffTable.removeColumn(third);
            
            //update: 22 juni 2006, because we have removed code and category column
            //when we click "Additional Properties" we have to add them here
            stuffTable.removeColumn(code);
            stuffTable.removeColumn(category);
            stuffTable.addColumn(code);
            stuffTable.addColumn(category);
            
            //update: 22 juni 2006, move the name column so it is in third position
            stuffTable.moveColumn( 0, 2 );

            //add modal, sale, quantity, comment ( index already removed )
            stuffTable.addColumn(modal);
            stuffTable.addColumn(sale);
            stuffTable.addColumn(quantity);
            stuffTable.addColumn(stufftc);
            
            displayComment.setSelected(true);
        }
        //if user want to see additional properties of item
        else if( ((String)cb.getSelectedItem()).equals("Additional Properties") ) {
            //remove the modal, sale, quantity, comment column
            //update: 22 juni 2006, and code, category column
            if(displayComment.isSelected()) {
                stuffTable.removeColumn(stufftc);
                displayComment.setSelected(false);
            }
            displayComment.setEnabled(false);
            warehouseComboBox.setEnabled(false);
            
            //update: 22 juni 2006, ok, here we remove them
            stuffTable.removeColumn(category);
            stuffTable.removeColumn(code);
            
            stuffTable.removeColumn(modal);
            stuffTable.removeColumn(sale);
            stuffTable.removeColumn(quantity);
            //remove the producer and seller column
            stuffTable.removeColumn(produceritem);
            stuffTable.removeColumn(selleritem);
            //add the length, width, height, volume, primary measurement,
            //secondary measurement
            stuffTable.addColumn(length);
            stuffTable.addColumn(width);
            stuffTable.addColumn(height);
            stuffTable.addColumn(volume);
            stuffTable.addColumn(primary);
            stuffTable.addColumn(measurement);
            stuffTable.addColumn(secondary);
            stuffTable.addColumn(s_t);
            stuffTable.addColumn(third);
         
        }
        //if user want to see the producer and seller
        else if( ((String)cb.getSelectedItem()).equals("Producers & Sellers") ) {
            //remove the modal, sale, quantity, comment column
            if(displayComment.isSelected()) {
                stuffTable.removeColumn(stufftc);
                displayComment.setSelected(false);
            }
            displayComment.setEnabled(false);
            warehouseComboBox.setEnabled(false);
            stuffTable.removeColumn(modal);
            stuffTable.removeColumn(sale);
            stuffTable.removeColumn(quantity);
            //remove the length, width, height, volume, primary measurement,
            //secondary measurement
            stuffTable.removeColumn(length);
            stuffTable.removeColumn(height);
            stuffTable.removeColumn(width);
            stuffTable.removeColumn(volume);
            stuffTable.removeColumn(primary);
            stuffTable.removeColumn(secondary);
            stuffTable.removeColumn(measurement);
            
            //update: 3 july 2006, remove s = t, and third measurement columns
            stuffTable.removeColumn(s_t);
            stuffTable.removeColumn(third);
            
            //update: 22 juni 2006, because we have removed code and category column
            //when we click "Additional Properties" we have to add them here
            stuffTable.removeColumn(code);
            stuffTable.removeColumn(category);
            stuffTable.addColumn(code);
            stuffTable.addColumn(category);
            
            //update: 22 juni 2006, move the name column so it is in third position
            stuffTable.moveColumn(0, 2);
            
            //add the producer and seller of items
            stuffTable.addColumn(produceritem);
            stuffTable.addColumn(selleritem);
            
        }
    }//GEN-LAST:event_propertiesComboBoxActionPerformed

    private void displayCommentCommisionerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayCommentCommisionerActionPerformed
        //display comment if it is selected
        if(displayCommentCommisioner.isSelected()) {
            commisionerTable.addColumn(commisionertc);
        }
        //don't display comment if it is not selected
        else {
            commisionerTable.removeColumn(commisionertc);
        }
    }//GEN-LAST:event_displayCommentCommisionerActionPerformed

    private void DeleteCommisionerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteCommisionerActionPerformed
        int row = commisionerTable.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(commisionerTable.getValueAt(row,0)==null)
                return;
        }
        
        int n;
        
        //what is the answer????
        n = JOptionPane.showConfirmDialog( this, "Do you really want to delete " +
                "this commisioner data?", "Question",
                JOptionPane.YES_NO_OPTION );

        //damn, he really want to delete the data from database
        if(n==JOptionPane.YES_OPTION) {
            //delete from db
            if(commisioner.deleteCommisioner(commisionerTable)) {
                //delete row from jtable
                javax.swing.table.DefaultTableModel model = 
                        (javax.swing.table.DefaultTableModel)commisionerTable.getModel();
                model.removeRow(commisionerTable.getSelectedRow());
            }
            else {
                JOptionPane.showMessageDialog(null,"Error deleting commisioner data! See log file " +
                    "for detail!", "Warning", JOptionPane.WARNING_MESSAGE );
            }
        }
        //no he doesn't want to
        else if(n==JOptionPane.NO_OPTION) {
            return;
        }
    }//GEN-LAST:event_DeleteCommisionerActionPerformed

    private void EditCommisionerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditCommisionerActionPerformed
        int row = commisionerTable.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(commisionerTable.getValueAt(row,0)==null)
                return;
        }
        
        new EditCommisionerDialog(this,true, commisionerTable, conn).setVisible(true);
    }//GEN-LAST:event_EditCommisionerActionPerformed

    private void AddCommisionerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddCommisionerActionPerformed
        new AddCommisionerDialog(this,true, commisionerTable, conn).setVisible(true);
    }//GEN-LAST:event_AddCommisionerActionPerformed

    private void DeleteCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteCustomerActionPerformed
        int row = customerTable.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(customerTable.getValueAt(row,0)==null)
                return;
        }
        
        int n;
        
        //what is the answer????
        n = JOptionPane.showConfirmDialog( this, "Do you really want to delete " +
                "this customer data?", "Question",
                JOptionPane.YES_NO_OPTION );

        //damn, he really want to delete the data from database
        if(n==JOptionPane.YES_OPTION) {
            //delete from db
            if(customer.deleteCustomer(customerTable)) {
                //delete row from jtable
                javax.swing.table.DefaultTableModel model = 
                        (javax.swing.table.DefaultTableModel)customerTable.getModel();
                model.removeRow(customerTable.getSelectedRow());
            }
            else {
                JOptionPane.showMessageDialog(null,"Error deleting customer data! See log file " +
                    "for detail!", "Warning", JOptionPane.WARNING_MESSAGE );
            }
        }
        //no he doesn't want to
        else if(n==JOptionPane.NO_OPTION) {
            return;
        }
    }//GEN-LAST:event_DeleteCustomerActionPerformed

    private void EditCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditCustomerActionPerformed
        int row = customerTable.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(customerTable.getValueAt(row,0)==null)
                return;
        }
        
        new EditCustomerDialog(this,true, customerTable, conn).setVisible(true);
    }//GEN-LAST:event_EditCustomerActionPerformed

    private void AddCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddCustomerActionPerformed
        new AddCustomerDialog(this,true, customerTable, conn).setVisible(true);
    }//GEN-LAST:event_AddCustomerActionPerformed

    private void displayCommentCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayCommentCustomerActionPerformed
        //display comment if it is selected
        if(displayCommentCustomer.isSelected()) {
            customerTable.addColumn(customertc);
        }
        //don't display comment if it is not selected
        else {
            customerTable.removeColumn(customertc);
        }
    }//GEN-LAST:event_displayCommentCustomerActionPerformed

    private void salesmanStatusComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salesmanStatusComboBoxActionPerformed
        //first clear the table
        while(salesmanTable.getRowCount()>0)
            ((DefaultTableModel)salesmanTable.getModel()).removeRow(0);
        //then make 4 new empty row
        for(int i=0; i<4; i++)
            ((DefaultTableModel)salesmanTable.getModel()).addRow(
                    new Object[] { null, null, null, null, null, 
                            null, null, null, null, null } );
        //then initialize again
        if( ((String)salesmanStatusComboBox.getSelectedItem()).equals("Active") ) {
            salesman.initializeDataSalesman(salesmanTable, "TRUE");
            ReactiveSalesman.setEnabled(false);
        }
        else if( ((String)salesmanStatusComboBox.getSelectedItem()).equals("Not Active") ) {
            salesman.initializeDataSalesman(salesmanTable, "FALSE");
            ReactiveSalesman.setEnabled(true);
        }
    }//GEN-LAST:event_salesmanStatusComboBoxActionPerformed

    private void displayCommentSalesmanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayCommentSalesmanActionPerformed
        //display comment if it is selected
        if(displayCommentSalesman.isSelected()) {
            salesmanTable.addColumn(salesmantc);
        }
        //don't display comment if it is not selected
        else {
            salesmanTable.removeColumn(salesmantc);
        }
    }//GEN-LAST:event_displayCommentSalesmanActionPerformed

    private void displayCommentSellerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayCommentSellerActionPerformed
        //display comment if it is selected
        if(displayCommentSeller.isSelected()) {
            sellerTable.addColumn(sellertc);
        }
        //don't display comment if it is not selected
        else {
            sellerTable.removeColumn(sellertc);
        }
    }//GEN-LAST:event_displayCommentSellerActionPerformed

    private void employeeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_employeeComboBoxActionPerformed
        ClearAllTable(employeeTable);
        
        String status = "";
        String type = "";
        //then initialize again
        if( ((String)employeeStatusComboBox.getSelectedItem()).equals("Active") ) {
            status = "TRUE";
            ReactiveEmployee.setEnabled(false);
        }
        else if( ((String)employeeStatusComboBox.getSelectedItem()).equals("Not Active") ) {
            status = "FALSE";
            ReactiveEmployee.setEnabled(true);
        }
        
        if( ((String)employeeTypeComboBox.getSelectedItem()).equals("Daily") )
            type = "harian";
        else if( ((String)employeeTypeComboBox.getSelectedItem()).equals("Monthly") )
            type = "bulanan";
        
        employee.initializeDataEmployee(employeeTable, status, type );
    }//GEN-LAST:event_employeeComboBoxActionPerformed

    private void displayCommentEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayCommentEmployeeActionPerformed
        //display comment if it is selected
        if(displayCommentEmployee.isSelected()) {
            employeeTable.addColumn(employeetc);
        }
        //don't display comment if it is not selected
        else {
            employeeTable.removeColumn(employeetc);
        }
    }//GEN-LAST:event_displayCommentEmployeeActionPerformed

    private void displayCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayCommentActionPerformed
        //display comment if it is selected
        if(displayComment.isSelected()) {
            stuffTable.addColumn(stufftc);
        }
        //don't display comment if it is not selected
        else {
            stuffTable.removeColumn(stufftc);
        }
    }//GEN-LAST:event_displayCommentActionPerformed

    private void mainExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainExitMenuItemActionPerformed
        if(xmlhandler.isExitConfirmation()) {
            int answer = JOptionPane.showConfirmDialog(this, "Do you want to exit?",
                    "Exit Confirmation", JOptionPane.YES_NO_OPTION);
            if(answer==JOptionPane.YES_OPTION) {
                NewConnection.closeConnection(conn);
                System.exit(0);        
            }
            else {
                return;
            }
        }
        NewConnection.closeConnection(conn);
        System.exit(0);
    }//GEN-LAST:event_mainExitMenuItemActionPerformed

    private void DeleteSalesmanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteSalesmanActionPerformed
        int row = salesmanTable.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(salesmanTable.getValueAt(row,0)==null)
                return;
        }
        
        int n;
        if(((String)salesmanStatusComboBox.getSelectedItem()).equals("Active")) {
            //what is the answer????
            n = JOptionPane.showConfirmDialog( this, "Do you really want to make this " +
                    "salesman not active?", "Question",
                    JOptionPane.YES_NO_OPTION );
            
            //damn, he really want to delete the data from database
            if(n==JOptionPane.YES_OPTION) {
                //delete from db
                salesman.makeNotActiveSalesman(salesmanTable);
            }
            //no he doesn't want to
            else if(n==JOptionPane.NO_OPTION) {
                return;
            }
        }
        else if(((String)salesmanStatusComboBox.getSelectedItem()).equals("Not Active")) {
            n = JOptionPane.showConfirmDialog( this, "Do you really want to delete this " +
                    "salesman data?", "Question",
                    JOptionPane.YES_NO_OPTION );
            
            //damn, he really want to delete the data from database
            if(n==JOptionPane.YES_OPTION) {
                //delete from db
                if(salesman.deleteSalesman(salesmanTable)) {
                    //delete row from jtable
                    javax.swing.table.DefaultTableModel model = 
                            (javax.swing.table.DefaultTableModel)salesmanTable.getModel();
                    model.removeRow(salesmanTable.getSelectedRow());
                }
                else {
                    JOptionPane.showMessageDialog(null,"Error deleting salesman data! See log file " +
                        "for detail!", "Warning", JOptionPane.WARNING_MESSAGE );
                }
            }
            //no he doesn't want to
            else if(n==JOptionPane.NO_OPTION) {
                return;
            }
        }
    }//GEN-LAST:event_DeleteSalesmanActionPerformed

    private void EditSalesmanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditSalesmanActionPerformed
        int row = salesmanTable.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(salesmanTable.getValueAt(row,0)==null)
                return;
        }
        
        //call the edit salesman dialog
        new EditSalesmanDialog( this, true, salesmanTable, conn ).setVisible(true);
    }//GEN-LAST:event_EditSalesmanActionPerformed

    private void AddSalesmanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddSalesmanActionPerformed
        //call the new salesman dialog
        new AddSalesmanDialog( this, true, salesmanTable, conn ).setVisible(true);
    }//GEN-LAST:event_AddSalesmanActionPerformed

    private void DeleteEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteEmployeeActionPerformed
        int row = employeeTable.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(employeeTable.getValueAt(row,0)==null)
                return;
        }
        
        int n;
        if(((String)employeeStatusComboBox.getSelectedItem()).equals("Active")) {
            //what is the answer????
            n = JOptionPane.showConfirmDialog( this, "Do you really want to make this " +
                    "employee not active?", "Question",
                    JOptionPane.YES_NO_OPTION );
            
            //damn, he really want to delete the data from database
            if(n==JOptionPane.YES_OPTION) {
                //delete from db
                employee.makeNotActiveEmployee(employeeTable);
            }
            //no he doesn't want to
            else if(n==JOptionPane.NO_OPTION) {
                return;
            }
        }
        else if(((String)employeeStatusComboBox.getSelectedItem()).equals("Not Active")) {
            n = JOptionPane.showConfirmDialog( this, "Do you really want to delete this " +
                    "employee data?", "Question",
                    JOptionPane.YES_NO_OPTION );
            
            //damn, he really want to delete the data from database
            if(n==JOptionPane.YES_OPTION) {
                //delete from db
                if(employee.deleteEmployee(employeeTable)) {
                    //delete row from jtable
                    javax.swing.table.DefaultTableModel model = 
                            (javax.swing.table.DefaultTableModel)employeeTable.getModel();
                    model.removeRow(employeeTable.getSelectedRow());
                }
                else {
                    JOptionPane.showMessageDialog(this,"Error deleting employee data! See log file " +
                        "for detail!", "Warning", JOptionPane.WARNING_MESSAGE );
                }
            }
            //no he doesn't want to
            else if(n==JOptionPane.NO_OPTION) {
                return;
            }
        }
        
    }//GEN-LAST:event_DeleteEmployeeActionPerformed

    private void EditEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditEmployeeActionPerformed
        int row = employeeTable.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(employeeTable.getValueAt(row,0)==null)
                return;
        }
        
        String status = "";
        String type = "";
        //then initialize again
        if( ((String)employeeStatusComboBox.getSelectedItem()).equals("Active") )
            status = "TRUE";
        else if( ((String)employeeStatusComboBox.getSelectedItem()).equals("Not Active") )
            status = "FALSE";

        if( ((String)employeeTypeComboBox.getSelectedItem()).equals("Daily") )
            type = "harian";
        else if( ((String)employeeTypeComboBox.getSelectedItem()).equals("Monthly") )
            type = "bulanan";
        
        //call the dialog to do operation
        new EditEmployeeDialog( this, true, employeeTable, 
                (String)employeeTypeComboBox.getSelectedItem(), conn ).setVisible(true);
        
        //reset the table
        ClearAllTable(employeeTable);
        
        employee.initializeDataEmployee(employeeTable, status, type );
    }//GEN-LAST:event_EditEmployeeActionPerformed

    private void SearchItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchItemActionPerformed
        //call the search dialog
        new SearchInventoryDialog( this, true, stuffTable, 
                ((String)warehouseComboBox.getSelectedItem()).toLowerCase(), 
                conn ).setVisible(true);
    }//GEN-LAST:event_SearchItemActionPerformed

    private void DeleteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteItemActionPerformed
        int row = stuffTable.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(stuffTable.getValueAt(row,0)==null)
                return;
        }
        
        int n = JOptionPane.showConfirmDialog( this, "Do you really want to delete " +
                "this item data?", "Confirmation Dialog", JOptionPane.YES_NO_OPTION );
        
        int item_index = inventory.getItemIndex((String)stuffTable.getValueAt(row,1));
        
        if(n==JOptionPane.YES_OPTION) {
            if(inventory.deleteItem(item_index)) {
                //how many item are in database
                itemrow = inventory.getItemRow();
                //set the label
                CountPage.setText("of " + getPageEnd() + " pages");
                PageItemTFActionPerformed(null);
            }
            else {
                //error deleting item data
                JOptionPane.showMessageDialog(null,"Error deleting item data! " +
                        "See log file for detail!", "Warning", JOptionPane.WARNING_MESSAGE );
            }
        }
        //no..... I don't want to delete it
        else if(n==JOptionPane.NO_OPTION) {
            return;
        }
    }//GEN-LAST:event_DeleteItemActionPerformed

    private void EditItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditItemActionPerformed
        int row = stuffTable.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(stuffTable.getValueAt(row,0)==null)
                return;
        }
        
        //warehouse
        String whouse = (String)warehouseComboBox.getSelectedItem();
        
        //call the dialog to do the job
        new EditItemDialog( this, true, stuffTable, conn ).setVisible(true);
          
        PageItemTFActionPerformed(null);
    }//GEN-LAST:event_EditItemActionPerformed

    private void AddItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddItemActionPerformed
        new AddItemDialog( this, true, 
                ((String)warehouseComboBox.getSelectedItem()).toLowerCase(), 
                conn ).setVisible(true);
        //how many item are in database
        itemrow = inventory.getItemRow();
        //set the label
        CountPage.setText("of " + getPageEnd() + " pages");
        PageItemTFActionPerformed(null);
    }//GEN-LAST:event_AddItemActionPerformed

    private void warehouseComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warehouseComboBoxActionPerformed
        javax.swing.JComboBox cb = (javax.swing.JComboBox)evt.getSource();
//        inventory.initializeDataItems( stuffTable, 
//                ((String)cb.getSelectedItem()).toLowerCase(),
//                viewOrderBy( (String)OrderByCoB.getSelectedItem() ),
//                offset * 30 );
        PageItemTFActionPerformed(null);
    }//GEN-LAST:event_warehouseComboBoxActionPerformed

    private void ViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewActionPerformed
        new ViewTransactionDialog(this, true, transactionsEP, conn).setVisible(true);
    }//GEN-LAST:event_ViewActionPerformed

    private void AddEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddEmployeeActionPerformed
        new AddEmployeeDialog(this,true, employeeTable, conn).setVisible(true);
       
        String status = "";
        String type = "";
        //then initialize again
        if( ((String)employeeStatusComboBox.getSelectedItem()).equals("Active") )
            status = "TRUE";
        else if( ((String)employeeStatusComboBox.getSelectedItem()).equals("Not Active") )
            status = "FALSE";

        if( ((String)employeeTypeComboBox.getSelectedItem()).equals("Daily") )
            type = "harian";
        else if( ((String)employeeTypeComboBox.getSelectedItem()).equals("Monthly") )
            type = "bulanan";

        employee.initializeDataEmployee(employeeTable, status, type );
    }//GEN-LAST:event_AddEmployeeActionPerformed

    private void DeleteSuplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteSuplierActionPerformed
        int row = sellerTable.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(sellerTable.getValueAt(row,0)==null)
                return;
        }
        
        //what is the answer from user
        int n = JOptionPane.showConfirmDialog( this, "Do you really want to delete the " +
                "seller data?", "Question",
                JOptionPane.YES_NO_OPTION );
        
        //he really wants to delete the data from db
        if(n==JOptionPane.YES_OPTION) {
            //delete data from db
            if(seller.deleteSeller(sellerTable)==true) {
                //delete row from jtable
                javax.swing.table.DefaultTableModel model = 
                        (javax.swing.table.DefaultTableModel)sellerTable.getModel();
                model.removeRow(sellerTable.getSelectedRow());
            }
            else {
                JOptionPane.showMessageDialog(null,"Error deleting suplier data! See log file " +
                    "for detail!", "Warning", JOptionPane.WARNING_MESSAGE );
            }
        }
        //he does not want to delete
        else if(n==JOptionPane.NO_OPTION) {
            return;
        }
    }//GEN-LAST:event_DeleteSuplierActionPerformed

    private void EditSuplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditSuplierActionPerformed
        int row = sellerTable.getSelectedRow();
        
        //make sure user choose valid row
        if(row==-1)
            return;
        else {    
            if(sellerTable.getValueAt(row,0)==null)
                return;
        }
        
        //call the dialog to do "operation"
        new EditSellerDialog(this,true, sellerTable, conn).setVisible(true);
    }//GEN-LAST:event_EditSuplierActionPerformed

    private void AddSuplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddSuplierActionPerformed
        //call the dialog to do "operation"
        new AddSellerDialog(this,true, sellerTable, conn).setVisible(true);
    }//GEN-LAST:event_AddSuplierActionPerformed

    private void SalesmanReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SalesmanReportActionPerformed
        new SalesmanReport( this, true, MainReportPanel, conn ).setVisible(true);
    }//GEN-LAST:event_SalesmanReportActionPerformed

    private void SaleReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaleReportActionPerformed
        new SaleReport( this, true, MainReportPanel, conn ).setVisible(true);
    }//GEN-LAST:event_SaleReportActionPerformed

    private void CustomersReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustomersReportActionPerformed
        new CustomerReport( this, true, MainReportPanel, conn ).setVisible(true);
    }//GEN-LAST:event_CustomersReportActionPerformed

    private void OtherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OtherActionPerformed
        new IncomeTransactionDialog( this, true, transactionsEP, conn ).setVisible(true);
    }//GEN-LAST:event_OtherActionPerformed

    private void OutcomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OutcomeActionPerformed
        new OutcomeTransactionDialog(this, true, transactionsEP, conn ).setVisible(true);
    }//GEN-LAST:event_OutcomeActionPerformed

    private void SalaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SalaryActionPerformed
        new SalaryTransactionDialog( this, true, transactionsEP, conn ).setVisible(true);
    }//GEN-LAST:event_SalaryActionPerformed

    private void PurchaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PurchaseActionPerformed
        new PurchaseTransactionDialog(this, true, transactionsEP, stuffTable, 
                ((String)warehouseComboBox.getSelectedItem()).toLowerCase(), conn ).setVisible(true);
    }//GEN-LAST:event_PurchaseActionPerformed

    private void IconBarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_IconBarMousePressed
        JButton e = (JButton)evt.getSource();
        e.setBackground( new Color(102,204,255));
        e.setContentAreaFilled(true);
    }//GEN-LAST:event_IconBarMousePressed

    private void IconBarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_IconBarMouseReleased
        JButton e = (JButton)evt.getSource();
        e.setBackground( new ColorUIResource(238,238,238));
        e.setContentAreaFilled(false);
    }//GEN-LAST:event_IconBarMouseReleased

    private void IconBarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_IconBarMouseExited
        JButton e = (JButton)evt.getSource();
        e.setContentAreaFilled(false);
    }//GEN-LAST:event_IconBarMouseExited

    private void IconBarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_IconBarMouseEntered
        JButton e = (JButton)evt.getSource();                 
        e.setBackground( new ColorUIResource(238,238,238));
        e.setContentAreaFilled(true);
    }//GEN-LAST:event_IconBarMouseEntered

    private void SaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaleActionPerformed
        new SaleTransactionDialog(this, true, transactionsEP, conn).setVisible(true);
    }//GEN-LAST:event_SaleActionPerformed

    private void treeGUIValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeGUIValueChanged
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeGUI.getLastSelectedPathComponent();
        if(node==null)
            return;
        
        CardLayout cl = (CardLayout)(PanelMainGUI.getLayout());
        CardLayout cl2 = (CardLayout)(iconBar.getLayout());
        
        Object nodeInfo = node.getUserObject();
        if(node.isLeaf()) {
            treeInfo treeComp = (treeInfo)nodeInfo;
            //If user selects Transactions
            if(treeComp.view=="Insert Transaction") {
                cl2.show(iconBar,"inserttrans");
                cl.show(PanelMainGUI,"inserttrans");
            }
            //If user selects Items
            else if(treeComp.view=="Item") {
                cl.show(PanelMainGUI,"item");
                cl2.show(iconBar,"item");
                //initialize data from db
                inventory.initializeComboBox( warehouseComboBox, "warehouse" );
                //how many item are in database
                itemrow = inventory.getItemRow();
                //set the labelinitiali
                CountPage.setText("of " + getPageEnd() + " pages");
                //find the offset
                PageItemTFActionPerformed(null);
            }
            else if(treeComp.view=="Report") {
                cl.show(PanelMainGUI,"report");
                cl2.show(iconBar,"report");
            }
            else if(treeComp.view=="Employee") {
                cl.show(PanelMainGUI,"employee");
                cl2.show(iconBar,"employee");
                //initialize data froom db
                String status = "";
                String type = "";
                //then initialize again
                if( ((String)employeeStatusComboBox.getSelectedItem()).equals("Active") )
                    status = "TRUE";
                else if( ((String)employeeStatusComboBox.getSelectedItem()).equals("Not Active") )
                    status = "FALSE";

                if( ((String)employeeTypeComboBox.getSelectedItem()).equals("Daily") )
                    type = "harian";
                else if( ((String)employeeTypeComboBox.getSelectedItem()).equals("Monthly") )
                    type = "bulanan";

                employee.initializeDataEmployee(employeeTable, status, type );
            }
            else if(treeComp.view=="Suplier") {
                cl.show(PanelMainGUI,"suplier");
                cl2.show(iconBar,"suplier");
                //initialize data from db
                seller.initializeDataSeller(sellerTable);
            }
            else if(treeComp.view=="Salesman") {
                cl.show(PanelMainGUI,"salesman");
                cl2.show(iconBar,"salesman");
                //initialize data from db
                if( ((String)salesmanStatusComboBox.getSelectedItem()).equals("Active") )
                    salesman.initializeDataSalesman(salesmanTable, "TRUE");
                else
                    salesman.initializeDataSalesman(salesmanTable, "FALSE");
            }
            else if(treeComp.view=="Customer") {
                cl.show(PanelMainGUI,"customer");
                cl2.show(iconBar,"customer");
                //initialize data from db
                customer.initializeDataCustomer(customerTable);
            }
            else if(treeComp.view=="Commisioner") {
                cl.show(PanelMainGUI,"commisioner");
                cl2.show(iconBar,"commisioner");
                //initialize database
                commisioner.initializeDataCommisioner(commisionerTable);
            }
            else if(treeComp.view=="Edit Transaction") {
                cl.show(PanelMainGUI,"edittrans");
                cl2.show(iconBar,"edittrans");
                //initialize data from db
//                if( ((String)DebtCreditCoB.getSelectedItem()).equals("Debit") )
//                    debtcredit.initializeDataDebt(DebtCreditTable);
//                else
//                    debtcredit.initializeDataCredit(DebtCreditTable);
                EditTransCoBActionPerformed(null);
            }
            else if(treeComp.view=="Producer") {
                cl.show(PanelMainGUI,"producer");
                cl2.show(iconBar,"producer");
                //initialize data from db
                producer.initializeDataProducer( ProducerTable );
            }
        }
        else {
            cl.show(PanelMainGUI,"home");
            cl2.show(iconBar,"home");
            //initialize data from db
            warehousedb.initializeDataWarehouse(WarehouseTB);
            containerdb.initializeDataContainer(ContainerTB);
        }
    }//GEN-LAST:event_treeGUIValueChanged

    private void updateAmountItemRow( int amount ) {
        CountPage.setText("of " + amount );
    }
    
    protected Component getVisibleComponent( Container container ) {
        Component [] array = container.getComponents();
        
        for( int i=0; i< array.length; i++ ) {
            if(array[i].isShowing() ) 
                return array[i];
        }
        
        return null;
    }
    
    private void AboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AboutActionPerformed
        new About( this, true ).setVisible( true );
    }//GEN-LAST:event_AboutActionPerformed
    
    public static void ClearAllTable( javax.swing.JTable table ) {
        //first clear the table
        while(table.getRowCount()>0)
            ((DefaultTableModel)table.getModel()).removeRow(0);
        //then make 4 new empty row
        for(int i=0; i<4; i++)
            ((DefaultTableModel)table.getModel()).addRow(
                    new Object[] { null, null, null, null, null, 
                            null, null, null, null, null } );
    }
    
    private String viewOrderBy( String orderby ) {
        //the query has three nama column, db is data_barang table
        if(orderby.equals("Name")) {
            orderby = "db.nama";
        }
        else if(orderby.equals("Category")) {
            orderby = "kategori";
        }
        else if(orderby.equals("Sale Price")) {
            orderby = "harga_jual";
        }
        else if(orderby.equals("Modal Price")) {
            orderby = "harga_modal";
        }
        return orderby;
    }
    
    private java.util.Date GetPastOneMonthDate() {
        java.util.Date today = new java.util.Date();
        long toDayTime = today.getTime();
        long days = 30*(24*60*60000L); //(today + 2 days), if 3 days 3*(24*60*60000L);
        long newDay = (long)toDayTime-days;
        return new java.util.Date(newDay);
    }
            
    //get the summary profile a month before
    private void SummaryProfile() {
        //format the date & number
        DateFormat df = DateFormat.getDateInstance( DateFormat.LONG, Locale.ENGLISH );
        NumberFormat nf = NumberFormat.getCurrencyInstance( new Locale("id","id"));
        
        GregorianCalendar gregorian = new GregorianCalendar( );
        
        String[] displayMonths = new DateFormatSymbols().getMonths();
        
        //month and year variable ( month start from 0, so 0 is january, 11 is december )
        int gregorianmonth = gregorian.get(Calendar.MONTH);
        int gregorianyear = gregorian.get(Calendar.YEAR);
        
        
        //the title of the summary
        //ok, we must use temporary variable because of missmatch with db standard date
        int displaygregorianmonth = gregorianmonth;
        int displaygregorianyear = gregorianyear;
        
        //december watchout
        //if the current month is january ( 0 ) then we must display summary for 
        //dec ( 11 ) last year ( current year - 1 )
        if(displaygregorianmonth==0) {
            displaygregorianmonth = 11;
            displaygregorianyear = gregorianyear - 1;
        }
        //if the current month is not january, then just minus it with one, current year is 
        //same
        else {
            displaygregorianmonth -= 1;
        }
        DateLB.setText("Summary of the Company in " + 
                 displayMonths[displaygregorianmonth] + " " + displaygregorianyear );
        
        //the summary report ( month start from 1  so no need minus anymore )
        SummarySaleLB.setText( "Total Value of Sale Transaction : " +
                nf.format( reportdb.TotalSaleValue( gregorianmonth,
                gregorianyear ) ) );
        SummaryPurchaseLB.setText( "Total Value of Purchase Transaction : " +
                nf.format( reportdb.TotalPurchaseValue( gregorianmonth,
                gregorianyear ) ) );
        SummarySalesmanCommisionLB.setText( "Total Value of Salesman Commision Transaction : " +
                nf.format( reportdb.TotalSalesmanCommisionValue( gregorianmonth,
                gregorianyear ) ) );
        SummarySalaryPaymentLB.setText( "Total Value of Salary Payment Transaction : " + 
                nf.format( reportdb.TotalSalaryPaymentValue( gregorianmonth,
                gregorianyear ) ) );
        SummaryChargesLB.setText( "Total Value of Outcome Payment Transaction : " +
                nf.format( reportdb.TotalOutcomeValue( gregorianmonth,
                gregorianyear ) ) );
        
    }
    
        
    private void AdditionalSetUpTable() {
        //additional setup for "index" and "comment" column on stuffTable
        //special case for stuffTable
        TableColumnModel stuffcm = stuffTable.getColumnModel();
        
        //update: 22 juni 2006 add column code and modal so we can remove them in propertiesComboBox
        //ActionPerformed when we click "Additonal Properties"
        //we have to reference name column so we can put category and code column
        //in the left of the name column
        category = stuffcm.getColumn(0);
        code = stuffcm.getColumn(1);
        
        modal = stuffcm.getColumn(3);
        sale = stuffcm.getColumn(4);
        quantity = stuffcm.getColumn(5);
        
        //update: 2 july 2006, add two column; s = t, Third T
        s_t = stuffcm.getColumn(17);
        third = stuffcm.getColumn(18);
        
        //make it middle
        class MiddleCellEditor extends DefaultTableCellRenderer {
            MiddleCellEditor() {
                super();
                setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            }
        }
        quantity.setCellRenderer( new MiddleCellEditor() );
        
        length = stuffcm.getColumn(8);
        length.setCellRenderer( new MiddleCellEditor() );
        width = stuffcm.getColumn(9);
        width.setCellRenderer( new MiddleCellEditor() );
        height = stuffcm.getColumn(10);
        height.setCellRenderer( new MiddleCellEditor() );
        volume = stuffcm.getColumn(11);
        volume.setCellRenderer( new MiddleCellEditor() );
        primary = stuffcm.getColumn(12);
        measurement = stuffcm.getColumn(13);
        measurement.setCellRenderer( new MiddleCellEditor() );
        secondary = stuffcm.getColumn(14);
        produceritem = stuffcm.getColumn(15);
        selleritem = stuffcm.getColumn(16);
        //index column
        TableColumn stuffcol = stuffcm.getColumn(7);
        stuffTable.removeColumn(stuffcol);
        //comment column
        stufftc = stuffTable.getColumnModel().getColumn(6);
        if(!displayComment.isSelected())
            stuffTable.removeColumn(stufftc);
        
        //remove all column first for the first time
        stuffTable.removeColumn(modal);
        stuffTable.removeColumn(sale);
        stuffTable.removeColumn(quantity);
        stuffTable.removeColumn(code);
        stuffTable.removeColumn(category);
        stuffTable.removeColumn(s_t);
        stuffTable.removeColumn(third);
        
        propertiesComboBoxActionPerformed(null);
        
        //additional setup for "index" and "comment" column on employeeTable
        TableColumnModel employeecm = employeeTable.getColumnModel();
        //index column
        TableColumn employeecol = employeecm.getColumn(9);
        employeeTable.removeColumn(employeecol);
        //comment column
        employeetc = employeecm.getColumn(8);
        if(!displayCommentEmployee.isSelected())
            employeeTable.removeColumn(employeetc);
        
        //additional setup for sellerTable
        TableColumnModel sellercm = sellerTable.getColumnModel();
        //index column
        TableColumn sellercol = sellercm.getColumn(4);
        sellerTable.removeColumn(sellercol);
        //comment column
        sellertc = sellercm.getColumn(3);
        if(!displayCommentSeller.isSelected())
            sellerTable.removeColumn(sellertc);
        
        //additional setup for salesmanTable
        TableColumnModel salesmancm = salesmanTable.getColumnModel();
        //index column
        TableColumn salesmancol = salesmancm.getColumn(8);
        salesmanTable.removeColumn(salesmancol);
        //comment column
        salesmantc = salesmancm.getColumn(7);
        if(!displayCommentSeller.isSelected())
            salesmanTable.removeColumn(salesmantc);
        
        //additional setup for ProducerTable
        TableColumnModel producercm = ProducerTable.getColumnModel();
        //index column
        TableColumn producercol = producercm.getColumn(4);
        ProducerTable.removeColumn(producercol);
        //comment column
        producertc = producercm.getColumn(3);
        if(!DisplayCommentProducer.isSelected())
            ProducerTable.removeColumn(producertc);
        
        //additional setup for customerTable
        TableColumnModel customercm = customerTable.getColumnModel();
        //index column
        TableColumn customercol = customercm.getColumn(4);
        customerTable.removeColumn(customercol);
        //comment column
        customertc = customercm.getColumn(3);
        if(!displayCommentCustomer.isSelected())
            customerTable.removeColumn(customertc);
        
        //additional setup for commisionerTable
        TableColumnModel commisionercm = commisionerTable.getColumnModel();
        //index column
        TableColumn commisionercol = commisionercm.getColumn(4);
        commisionerTable.removeColumn(commisionercol);
        //comment columnlist
        commisionertc = commisionercm.getColumn(3);
        if(!displayCommentCommisioner.isSelected())
            commisionerTable.removeColumn(commisionertc);
        
        //additional setup for debtcreditTable
//        TableColumn debtcreditcol = DebtCreditTable.getColumnModel().getColumn(5);
//        DebtCreditTable.removeColumn(debtcreditcol);
        
        //additional setup for debt table
        TableColumn debtcol = DebtTable.getColumnModel().getColumn(5);
        DebtTable.removeColumn(debtcol);
        
        //additional setup for sale ( edit trans ) table
        TableColumn salecol = SaleTable.getColumnModel().getColumn(6);
        SaleTable.removeColumn(salecol);
        
        //additional setup for purchase ( edit trans ) table
        TableColumn purchasecol = PurchaseTable.getColumnModel().getColumn(6);
        PurchaseTable.removeColumn(purchasecol);
        
        //additional setup for credit table
        TableColumn creditcol = CreditTable.getColumnModel().getColumn(5);
        CreditTable.removeColumn(creditcol);
        
        //additional setup for warehouseTB
        TableColumn warehouseindexcol = WarehouseTB.getColumnModel().getColumn(3);
        WarehouseTB.removeColumn(warehouseindexcol);
        
        //additional setup for containerTB
        TableColumn containerindexcol = ContainerTB.getColumnModel().getColumn(4);
        ContainerTB.removeColumn(containerindexcol);
    }
    
    private void setTableHeaderNotMoveable() {
        //set the all table header cannot be reordered
        stuffTable.getTableHeader().setReorderingAllowed(false);
        employeeTable.getTableHeader().setReorderingAllowed(false);
        sellerTable.getTableHeader().setReorderingAllowed(false);
        salesmanTable.getTableHeader().setReorderingAllowed(false);
        customerTable.getTableHeader().setReorderingAllowed(false);
        commisionerTable.getTableHeader().setReorderingAllowed(false);
        WarehouseTB.getTableHeader().setReorderingAllowed(false);
        ContainerTB.getTableHeader().setReorderingAllowed(false);
        ProducerTable.getTableHeader().setReorderingAllowed(false);
        SaleTable.getTableHeader().setReorderingAllowed(false);
        PurchaseTable.getTableHeader().setReorderingAllowed(false);
        SalaryTable.getTableHeader().setReorderingAllowed(false);
        IncomeTable.getTableHeader().setReorderingAllowed(false);
        OutcomeTable.getTableHeader().setReorderingAllowed(false);
        DebtTable.getTableHeader().setReorderingAllowed(false);
        CreditTable.getTableHeader().setReorderingAllowed(false);
    }
    
    private void setTableSingleSelection() {
        //set the all table so it is single selected
        stuffTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        employeeTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        sellerTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        salesmanTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        customerTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        commisionerTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        WarehouseTB.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        ContainerTB.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        ProducerTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        SaleTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        PurchaseTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        SalaryTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        IncomeTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        OutcomeTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        DebtTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        CreditTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    }
    
    private void HomeShowProfile() {
        CompanyID.setText( xmlhandler.elementCompanyProfile("name") );
        OwnerID.setText( xmlhandler.elementCompanyProfile("owner") );
        AddressID.setText( xmlhandler.elementCompanyProfile("address") );
        PhoneID.setText( xmlhandler.elementCompanyProfile("phone") );
        EmailID.setText( xmlhandler.elementCompanyProfile("email") );
    }
    
    private void showLowItem() {
        boolean show = xmlhandler.isDisplayStartupDialog();
        final javax.swing.JFrame frame = this;
        
        if(show) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new LowItemsList( frame, true, conn ).setVisible(true);
                }
            });
        }
    }
    
    private void setUpPageItem() {
        //how many item are in database
        displaylimit = xmlhandler.getItemDisplayLimit();
        itemrow = inventory.getItemRow();
        Back.setEnabled(false);
        Begin.setEnabled(false);
        if(getPageEnd()==1) {
            Forward.setEnabled(false);
            End.setEnabled(false);
        }
    }
    
    private int getPageEnd() {
        // temporary fix for annoying java.lang.ArithmeticException
        if(itemrow==0)
            return 1;
        if(itemrow%displaylimit!=0)
            return itemrow / displaylimit + 1;
        else
            return itemrow / displaylimit;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main( String[] args ) {
        java.awt.EventQueue.invokeLater( new Runnable() {
            public void run() {
                new MainGUI(null).setVisible(true);
            }
        });      
    }
    
    //update: 22 juni 2006, add column category, name and code so we can remove
    //them in propertiesComboBoxActionPerformed when we click "Addtional Properties"
    private TableColumn category, code, modal, sale, quantity, length, width, height, volume, primary, 
            measurement, secondary, produceritem, selleritem, s_t, third;
    private TableColumn stufftc, employeetc, sellertc, salesmantc, customertc, commisionertc,
            producertc;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem About;
    private javax.swing.JButton AddCommisioner;
    private javax.swing.JButton AddContainer;
    private javax.swing.JButton AddCustomer;
    private javax.swing.JButton AddEmployee;
    private javax.swing.JButton AddItem;
    private javax.swing.JButton AddProducer;
    private javax.swing.JButton AddSalesman;
    private javax.swing.JButton AddSuplier;
    private javax.swing.JButton AddWarehouse;
    private javax.swing.JLabel AddressID;
    private javax.swing.JLabel AddressLB;
    private javax.swing.JButton Back;
    private javax.swing.JButton Begin;
    private javax.swing.JButton Clear;
    private javax.swing.JPanel CommisionerIconBar;
    private javax.swing.JPanel CommisionerPanel;
    private javax.swing.JLabel CompanyID;
    private javax.swing.JLabel CompanyLB;
    private javax.swing.JLabel ContainerDummyLeftLB;
    private javax.swing.JLabel ContainerDummyRightLB;
    private javax.swing.JLabel ContainerDummySoutLB;
    private javax.swing.JLabel ContainerLB;
    private javax.swing.JPanel ContainerPanel;
    private javax.swing.JScrollPane ContainerScP;
    private javax.swing.JTable ContainerTB;
    private javax.swing.JLabel CountPage;
    private javax.swing.JScrollPane CreditScP;
    private javax.swing.JTable CreditTable;
    private javax.swing.JPanel CustomerIconBar;
    private javax.swing.JPanel CustomerPanel;
    private javax.swing.JButton CustomersReport;
    private javax.swing.JLabel DateLB;
    private javax.swing.JPanel DebtCreditTopPanel;
    private javax.swing.JScrollPane DebtScP;
    private javax.swing.JTable DebtTable;
    private javax.swing.JButton DeleteCommisioner;
    private javax.swing.JButton DeleteContainer;
    private javax.swing.JButton DeleteCustomer;
    private javax.swing.JButton DeleteEmployee;
    private javax.swing.JButton DeleteItem;
    private javax.swing.JButton DeleteProducer;
    private javax.swing.JButton DeleteSalesman;
    private javax.swing.JButton DeleteSuplier;
    private javax.swing.JButton DeleteTransaction;
    private javax.swing.JButton DeleteWarehouse;
    private javax.swing.JCheckBox DisplayCommentProducer;
    private javax.swing.JButton EditCommisioner;
    private javax.swing.JButton EditContainer;
    private javax.swing.JButton EditCustomer;
    private javax.swing.JButton EditEmployee;
    private javax.swing.JButton EditHome;
    private javax.swing.JButton EditItem;
    private javax.swing.JButton EditProducer;
    private javax.swing.JButton EditSalesman;
    private javax.swing.JButton EditSuplier;
    private javax.swing.JComboBox EditTransCoB;
    private javax.swing.JPanel EditTransIconBar;
    private javax.swing.JPanel EditTransPanel;
    private javax.swing.JButton EditTransactionButton;
    private javax.swing.JButton EditWarehouse;
    private javax.swing.JLabel EmailID;
    private javax.swing.JLabel EmailLB;
    private javax.swing.JButton End;
    private javax.swing.JButton Forward;
    private javax.swing.JButton GeneralReport;
    private javax.swing.JLabel HomeIcon;
    private javax.swing.JPanel HomeIconBar;
    private javax.swing.JPanel HomeMainPanel;
    private javax.swing.JPanel HomePanel;
    private javax.swing.JScrollPane HomeScp;
    private javax.swing.JScrollPane IncomeScP;
    private javax.swing.JTable IncomeTable;
    private javax.swing.JPanel MainReportPanel;
    private javax.swing.JComboBox OrderByCoB;
    private javax.swing.JButton Other;
    private javax.swing.JButton Outcome;
    private javax.swing.JScrollPane OutcomeScP;
    private javax.swing.JTable OutcomeTable;
    private javax.swing.JLabel OwnerID;
    private javax.swing.JLabel OwnerLB;
    private javax.swing.JTextField PageItemTF;
    private javax.swing.JPanel PanelMainGUI;
    private javax.swing.JLabel PhoneID;
    private javax.swing.JLabel PhoneLB;
    private javax.swing.JMenuItem Preferences;
    private javax.swing.JPanel ProducerIconBar;
    private javax.swing.JPanel ProducerPanel;
    private javax.swing.JScrollPane ProducerScrollPane;
    private javax.swing.JTable ProducerTable;
    private javax.swing.JButton Purchase;
    private javax.swing.JButton PurchaseReport;
    private javax.swing.JScrollPane PurchaseScP;
    private javax.swing.JTable PurchaseTable;
    private javax.swing.JButton ReactiveEmployee;
    private javax.swing.JButton ReactiveSalesman;
    private javax.swing.JButton Salary;
    private javax.swing.JScrollPane SalaryScP;
    private javax.swing.JTable SalaryTable;
    private javax.swing.JButton Sale;
    private javax.swing.JButton SaleReport;
    private javax.swing.JScrollPane SaleScP;
    private javax.swing.JTable SaleTable;
    private javax.swing.JButton SalesmanReport;
    private javax.swing.JButton Search;
    private javax.swing.JButton SearchItem;
    private javax.swing.JLabel SortedLabel;
    private javax.swing.JComboBox SortingEditTransCoB;
    private javax.swing.JLabel SummaryChargesLB;
    private javax.swing.JPanel SummaryPanel;
    private javax.swing.JLabel SummaryPurchaseLB;
    private javax.swing.JLabel SummarySalaryPaymentLB;
    private javax.swing.JLabel SummarySaleLB;
    private javax.swing.JLabel SummarySalesmanCommisionLB;
    private javax.swing.JPanel SuplierIconBar;
    private javax.swing.JPanel TopProducerPane;
    private javax.swing.JPanel TransPanel;
    private javax.swing.JButton View;
    private javax.swing.JLabel ViewOrderByLB;
    private javax.swing.JLabel WarehouseDummyLeftLB;
    private javax.swing.JLabel WarehouseDummyRightLB;
    private javax.swing.JLabel WarehouseDummySoutLB;
    private javax.swing.JLabel WarehouseLB;
    private javax.swing.JPanel WarehousePanel;
    private javax.swing.JScrollPane WarehouseScP;
    private javax.swing.JTable WarehouseTB;
    private javax.swing.JScrollPane commisionerScrollPane;
    private javax.swing.JTable commisionerTable;
    private javax.swing.JScrollPane customerScrollPane;
    private javax.swing.JTable customerTable;
    private javax.swing.JCheckBox displayComment;
    private javax.swing.JCheckBox displayCommentCommisioner;
    private javax.swing.JCheckBox displayCommentCustomer;
    private javax.swing.JCheckBox displayCommentEmployee;
    private javax.swing.JCheckBox displayCommentSalesman;
    private javax.swing.JCheckBox displayCommentSeller;
    private javax.swing.JPanel employeeDataIconBar;
    private javax.swing.JPanel employeePanel;
    private javax.swing.JScrollPane employeeScrollPane;
    private javax.swing.JComboBox employeeStatusComboBox;
    private javax.swing.JTable employeeTable;
    private javax.swing.JComboBox employeeTypeComboBox;
    private javax.swing.JPanel iconBar;
    private javax.swing.JPanel inventoryIconBar;
    private javax.swing.JMenuItem mainExitMenuItem;
    private javax.swing.JMenuItem mainExportMenuItem;
    private javax.swing.JMenu mainFile;
    private javax.swing.JMenu mainHelp;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JMenuItem mainPrintMenuItem;
    private javax.swing.JMenuItem mainSaveMenuItem;
    private javax.swing.JMenu mainTools;
    private javax.swing.JComboBox propertiesComboBox;
    private javax.swing.JPanel reportIconBar;
    private javax.swing.JPanel reportPanel;
    private javax.swing.JPanel salesmanIconBar;
    private javax.swing.JPanel salesmanPanel;
    private javax.swing.JScrollPane salesmanScrollPane;
    private javax.swing.JComboBox salesmanStatusComboBox;
    private javax.swing.JTable salesmanTable;
    private javax.swing.JScrollPane sellerScrollPane;
    private javax.swing.JTable sellerTable;
    private javax.swing.JPanel stockPanel;
    private javax.swing.JScrollPane stockScrollPane;
    private javax.swing.JTable stuffTable;
    private javax.swing.JPanel suplierPanel;
    private javax.swing.JPanel topCommisionerPanel;
    private javax.swing.JPanel topCustomerPanel;
    private javax.swing.JPanel topEmployeePanel;
    private javax.swing.JPanel topSalesmanPanel;
    private javax.swing.JPanel topSellerPanel;
    private javax.swing.JPanel topStockPanel;
    private javax.swing.JPanel transactionIconBar;
    private javax.swing.JEditorPane transactionsEP;
    private javax.swing.JScrollPane transactionsScrollPane;
    javax.swing.JTree treeGUI;
    private javax.swing.JScrollPane treeScrollPane;
    private javax.swing.JComboBox warehouseComboBox;
    // End of variables declaration//GEN-END:variables
    
}
