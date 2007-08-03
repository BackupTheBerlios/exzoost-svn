/*
 * JasperTableReport.java
 *
 * Created on February 18, 2005, 7:59 PM
 */

package com.exzoost.gui.reportview;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author knight
 */
public class JasperTableReport {
    private Connection conn;
    
    /** Creates a new instance of JasperTableReport */
    public JasperTableReport( Connection conn ) {
        this.conn = conn;
    }
    
    /**
     * Show the save dialog for employee
     *
     */
    public void SaveDialog( String type ) {
        //create the file chooser
        final JFileChooser fc = new JFileChooser();
        
        //Add a custom file filter and disable the default
        //(Accept All) file filter.
        fc.addChoosableFileFilter(new ReportFilter());
        fc.setAcceptAllFileFilterUsed(false);
        
        //in response to button click
        int returnVal = fc.showSaveDialog(null);
                
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            //we need this binary file
            File reportFile = null;
            String pathjasper = "jasper/";
            
            if(type.equals("employee")) {
                pathjasper += "employees.jasper";
            }
            else if(type.equals("suplier")) {
                pathjasper += "supliers.jasper";
            }
            else if(type.equals("customer")) {
                pathjasper += "customers.jasper";
            }
            else if(type.equals("commisioner")) {
                pathjasper += "commisioners.jasper";
            }
            else if(type.equals("salesman")) {
                pathjasper += "salesmen.jasper";
            }
            else if(type.equals("producer")) {
                pathjasper += "producers.jasper";
            }
            //category, code, name, purchase price, & sale price
            else if(type.equals("item1")) {
                pathjasper += "items1.jasper";
            }
            //category, code, name, quantity
            else if(type.equals("item2")) {
                pathjasper += "items2.jasper";
            }
            //category, code, name, producer, & comment
            else if(type.equals("item3")) {
                pathjasper += "items3.jasper";
            }
//            reportFile = new File( getClass().getResource(pathjasper).toURI() );
                
            try {
                //get the report instance
                JasperReport jasperReport = 
                        (JasperReport) JRLoader.loadObject( pathjasper );

                //get the print object
                JasperPrint jasperPrint = 
                        JasperFillManager.fillReport( jasperReport, null, conn );

                File getFile = fc.getSelectedFile();
                
                //check if the user add .pdf extension
                String path = getFile.getPath();
                
                //without extension
                if(path.indexOf(".pdf")==-1) {
                    path += ".pdf";
                }
                
                JasperExportManager.exportReportToPdfFile(jasperPrint, path );
            }
            catch( JRException e ) {
                e.printStackTrace();
            }
        }
        else {
        }
    }
    
    /**
     * 
     * Create and display report
     *
     */
    public void TableReport( String type ) {
        //we need this binary file
        //FileInputStream reportFile = null;
        String pathjasper = "jasper/";
        
        if(type.equals("employee")) {
            pathjasper += "employees.jasper";
        }
        else if(type.equals("suplier")) {
            pathjasper += "supliers.jasper";
        }
        else if(type.equals("customer")) {
            pathjasper += "customers.jasper";
        }
        else if(type.equals("commisioner")) {
            pathjasper += "commisioners.jasper";
        }
        else if(type.equals("salesman")) {
            pathjasper += "salesmen.jasper";
        }
        else if(type.equals("producer")) {
            pathjasper += "producers.jasper";
        }
        //category, code, name, purchase price, & sale price
        else if(type.equals("item1")) {
            pathjasper += "items1.jasper";
        }
        //category, code, name, quantity
        else if(type.equals("item2")) {
            pathjasper += "items2.jasper";
        }
        //category, code, name, producer, & comment
        else if(type.equals("item3")) {
            pathjasper += "items3.jasper";
        }
        //reportFile = getClass().getResourceAsStream(pathjasper);
        
        try {
            //get the report instance
            JasperReport jasperReport = 
                    (JasperReport) JRLoader.loadObject( pathjasper );

            //get the print object
            JasperPrint jasperPrint = 
                    JasperFillManager.fillReport( jasperReport, null, conn );

            //show it...
            JasperViewer.viewReport( jasperPrint, false );
        }
        catch( JRException e ) {
            e.printStackTrace();
        }
        
    }
        
    /*
     *file filter for jchooserdialog
     *
     */
    public class ReportFilter extends FileFilter {

        String extension = "";
        
        //Accept all directories and all pdf, xml, html.
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            extension = Utils.getExtension(f);
            if (extension != null) {
                if ( extension.equals(Utils.pdf) )
//                    ||
//                    extension.equals(Utils.jpeg) ||
//                    extension.equals(Utils.jpg) ||
//                    extension.equals(Utils.png)) 
                {
                        return true;
                } else {
                    return false;
                }
            }

            return false;
        }
        
        public String getDescription() {
            return "*.pdf";
        }
    }
    
}
