/*
 * XMLHandler.java
 *
 * Created on November 6, 2004, 2:42 AM
 */

package com.exzoost.xmlhandler;

import java.io.FileOutputStream;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.jdom.Element;
import org.jdom.Comment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;

/**
 *
 * @author knight
 */
public class XMLHandler {
    private Namespace ns;
       
    /** Creates a new instance of XMLHandler */
    public XMLHandler( ) {
        ns = Namespace.getNamespace( "www.akbarhome.com" );
    }
    
    public void companyProfile(String companyName, String ownerName, String addressCompany, 
            String phoneNumber, String emailCompany ) {
        
        Element companyElement = new Element("company", ns);
        Document companyProfile = new Document(companyElement);
        companyElement.addContent( new Comment("User Company Profile"));
        companyElement.addContent( new Element("name", ns).addContent(companyName) );
        companyElement.addContent( new Element("owner", ns).addContent(ownerName) );
        companyElement.addContent( new Element("address", ns).addContent(addressCompany) );
        companyElement.addContent( new Element("phone", ns).addContent(phoneNumber) );
        companyElement.addContent( new Element("email", ns).addContent(emailCompany) );
        
        //save to this file
        String fname = System.getProperty("user.home") + "/exzoost/companyProfile.xml";
        writeToFile(fname, companyProfile);
    }
    
    public String elementCompanyProfile( String value ) {
        try {
            SAXBuilder sb = new SAXBuilder();
            String filename = System.getProperty("user.home") + 
                    "/exzoost/companyProfile.xml"; 
            Document doc = sb.build( new File(filename) );
            Element root = doc.getRootElement();
            if(value=="name") {
                return root.getChildText("name", ns);
            } else if(value=="owner") {
                return root.getChildText("owner", ns);
            } else if(value=="address") {
                return root.getChildText("address", ns);
            } else if(value=="phone") {
                return root.getChildText("phone", ns);
            } else if(value=="email") {
                return root.getChildText("email", ns);
            }
        }
        catch( JDOMException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return null;
    }
    
    //create configuration file if it does not exist
    public void createConfigurationFile() {
        String filename = System.getProperty("user.home") + 
                    "/exzoost/configuration.xml"; 
        
        Element root = new Element( "configurationExzoost", ns );
        DocType dt = new DocType( "configurationExzoost" );
        Document doc = new Document( root, dt );
        
        //look and feel element
        String theme = "plasticxp";
        Element el = new Element( "theme", ns );
        el.setText(theme);
        root.addContent(el);
        
        //low quantity item warning dialog element
        String warning = "true";
        String limit = "10";
        Element loel = new Element( "startup-dialog", ns );
        //the child element of startup-dialog
        Element child_loel = new Element( "show", ns );
        child_loel.setText(warning);
        Element child_loel2 = new Element( "limit", ns );
        child_loel2.setText(limit);
        loel.addContent(child_loel);
        loel.addContent(child_loel2);
        
        //add to root element
        root.addContent(loel);
        
        //exit confirmation element
        String exit = "true";
        Element exitel = new Element( "exit-confirmation", ns );
        exitel.setText(exit);
        
        //add to root element
        root.addContent(exitel);
        
        //list of inventory
        String displaylimit = "32";
        Element dili = new Element( "item-display-limit", ns );
        dili.setText(displaylimit);
        
        //add to root element
        root.addContent(dili);
        
        writeToFile( filename, doc );
        
    }
    
    //get the exit confirmation
    public boolean isExitConfirmation() {
        try {
            SAXBuilder sb = new SAXBuilder();
            String filename = System.getProperty("user.home") + 
                    "/exzoost/configuration.xml"; 
            Document doc = sb.build( new File(filename) );
            Element root = doc.getRootElement();

            return new Boolean(true).parseBoolean(
                    root.getChildText("exit-confirmation", ns) );
        }
        catch( JDOMException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return true;
    }
    
    //get the item display limit
    public int getItemDisplayLimit() {
        try {
            SAXBuilder sb = new SAXBuilder();
            String filename = System.getProperty("user.home") + 
                    "/exzoost/configuration.xml"; 
            Document doc = sb.build( new File(filename) );
            Element root = doc.getRootElement();

            return Integer.parseInt(root.getChildText("item-display-limit", ns));
        }
        catch( JDOMException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return 32;
    }
    
    //get the limit
    public int getLimitLowQuantityItemList() {
        try {
            SAXBuilder sb = new SAXBuilder();
            String filename = System.getProperty("user.home") + 
                    "/exzoost/configuration.xml"; 
            Document doc = sb.build( new File(filename) );
            Element root = doc.getRootElement();

            return Integer.parseInt(root.getChild("startup-dialog", ns).getChildText("limit", ns));
        }
        catch( JDOMException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return 10;
    }
    
    //get the boolean ( should we display startup dialog )
    public boolean isDisplayStartupDialog() {
        try {
            SAXBuilder sb = new SAXBuilder();
            String filename = System.getProperty("user.home") + 
                    "/exzoost/configuration.xml"; 
            Document doc = sb.build( new File(filename) );
            Element root = doc.getRootElement();
            
            return new Boolean(true).parseBoolean(
                    root.getChild("startup-dialog", ns).getChildText("show", ns));
        }
        catch( JDOMException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return true;
    }
    
    //get the look and feel
    public String getLookAndFeel() {
        try {
            SAXBuilder sb = new SAXBuilder();
            String filename = System.getProperty("user.home") + 
                    "/exzoost/configuration.xml"; 
            Document doc = sb.build( new File(filename) );
            Element root = doc.getRootElement();

            return root.getChildText("theme", ns);
        }
        catch( JDOMException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return "ocean";
    }
    
    //set the exit confirmation
    public void setExitConfirmation( boolean display ) {
        try {
            SAXBuilder sb = new SAXBuilder();
            String filename = System.getProperty("user.home") + 
                    "/exzoost/configuration.xml"; 
            Document doc = sb.build( new File(filename) );
            Element root = doc.getRootElement();

            Element eltheme = root.getChild("exit-confirmation", ns);
            eltheme.setText("" + display);
            writeToFile( filename, doc );
        }
        catch( JDOMException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
    //set the item display limit
    public void setItemDisplayLimit( int limit ) {
        try {
            SAXBuilder sb = new SAXBuilder();
            String filename = System.getProperty("user.home") + 
                    "/exzoost/configuration.xml"; 
            Document doc = sb.build( new File(filename) );
            Element root = doc.getRootElement();

            Element eltheme = root.getChild("item-display-limit", ns);
            eltheme.setText("" + limit);
            writeToFile( filename, doc );
        }
        catch( JDOMException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
    //set the boolean display startup dialog
    public void setDisplayStartupDialog( boolean display ) {
        try {
            SAXBuilder sb = new SAXBuilder();
            String filename = System.getProperty("user.home") + 
                    "/exzoost/configuration.xml"; 
            Document doc = sb.build( new File(filename) );
            Element root = doc.getRootElement();

            Element eltheme = root.getChild("startup-dialog", ns).getChild("show", ns);
            eltheme.setText("" + display);
            writeToFile( filename, doc );
        }
        catch( JDOMException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
    //set the limit quantity of item to be displayed
    public void setLimitQuantityItemsList( int limit ) {
        try {
            SAXBuilder sb = new SAXBuilder();
            String filename = System.getProperty("user.home") + 
                    "/exzoost/configuration.xml"; 
            Document doc = sb.build( new File(filename) );
            Element root = doc.getRootElement();

            Element eltheme = root.getChild("startup-dialog", ns).getChild("limit", ns);
            eltheme.setText("" + limit);
            writeToFile( filename, doc );
        }
        catch( JDOMException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
    //set the look and feel
    public void setUpLookAndFeel( String theme ) {
        try {
            SAXBuilder sb = new SAXBuilder();
            String filename = System.getProperty("user.home") + 
                    "/exzoost/configuration.xml"; 
            Document doc = sb.build( new File(filename) );
            Element root = doc.getRootElement();

            Element eltheme = root.getChild("theme", ns);
            eltheme.setText(theme.toLowerCase());
            writeToFile( filename, doc );
        }
        catch( JDOMException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
    //write to file
    private static void writeToFile( String fname, Document doc ) {
        try {
            FileOutputStream out = new FileOutputStream(fname);
            
            XMLOutputter serializer = new XMLOutputter( Format.getPrettyFormat() );
            serializer.output( doc, out );
            out.flush();
            out.close();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
}
