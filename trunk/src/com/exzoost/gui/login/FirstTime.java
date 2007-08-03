/*
 * FirstTime.java
 *
 * Created on November 2, 2004, 2:03 PM
 */

package com.exzoost.gui.login;
import com.exzoost.database.NewConnection;
import com.exzoost.database.WriteLogFile;
import com.exzoost.gui.maingui.MainGUI;
import com.exzoost.xmlhandler.XMLHandler;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.*;
import java.lang.System;
import java.io.File;
import com.exzoost.gui.login.FirstTimeWizard;
import java.sql.Connection;
import java.util.Properties;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.sourceforge.mlf.metouia.MetouiaLookAndFeel;

/**
 *
 * @author  knight
 */
public class FirstTime {
    private String theme;
    private XMLHandler xmlhandler;
    /** Creates a new instance of FirstTime */
    public FirstTime() {
        //setup xmlhandler
        xmlhandler = new XMLHandler();
        
        //remove log file
        new WriteLogFile().removeLogFile();
        
        loadConfigurationFile();
        
        setLookAndFeel();
        
        loadCompanyProfile();
                
    }
    
    private void loadConfigurationFile() {
        File configurationexzoost = new File( System.getProperty("user.home") + 
                "/exzoost/configuration.xml" );
        
        if( configurationexzoost.exists() ) {
            return;
        }
        else {
            File directoryexzoost = new File( System.getProperty("user.home") + "/exzoost"  );
            directoryexzoost.mkdir();
            xmlhandler.createConfigurationFile();
        }
    }
    
    private void loadExzoostDirectory() {
        File directoryexzoost = new File( System.getProperty("user.home") + 
                "/exzoost" );

        //check if the directory exists...
        if(!directoryexzoost.exists()) {
            directoryexzoost.mkdir();    
        }
    }
    
    private void loadCompanyProfile() {
        //If not the first time
        File newbie = new File( System.getProperty("user.home") + 
                "/exzoost/companyProfile.xml" );
        
        if( newbie.exists() ) {
            new LogOnWindow().setVisible(true);
        }
        //If it is the first time
        else {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new FirstTimeWizard(new javax.swing.JFrame(), true).setVisible(true);
                }
            });
        }
    }
    
    private void setLookAndFeel() {
        try {
            theme = xmlhandler.getLookAndFeel();
            //select the theme
            if(theme.equals("liquid")) {
                UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
            }
            else if(theme.equals("metouia")) {
                UIManager.setLookAndFeel(new MetouiaLookAndFeel());
            }
            else if(theme.equals("plastic3d")) {
                PlasticLookAndFeel.setMyCurrentTheme(new SkyBluer());
                UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
            }
            else if(theme.equals("plasticxp")) {
                PlasticLookAndFeel.setMyCurrentTheme(new SkyBluer());
                UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
            }
            else if(theme.equals("plastic")) {   
                PlasticLookAndFeel.setMyCurrentTheme(new SkyBluer());
                UIManager.setLookAndFeel( new PlasticLookAndFeel());
            }
            else if(theme.equals("windows")) {
                UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
            }
            else if(theme.equals("compiere")) {
                UIManager.setLookAndFeel( "org.compiere.plaf.CompiereLookAndFeel" );
            }
        }
        catch( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }
        catch( java.lang.ClassNotFoundException e ) {
            e.printStackTrace();
        }
        catch( java.lang.InstantiationException e ) {
            e.printStackTrace();
        }
        catch( java.lang.IllegalAccessException e ) {
            e.printStackTrace();
        }
    }
}
