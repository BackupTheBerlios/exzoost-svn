/*
 * GuiHelper.java
 *
 * Created on January 18, 2007, 2:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.exzoost.gui.helper;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author knight
 */
public class GuiHelper {
    
    /** Creates a new instance of GuiHelper */
    public GuiHelper() {
    }
    
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
    
    public static void setOnCenter(Window window) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int vpos = dim.height / 2 - window.getHeight() / 2;
        int hpos = dim.width / 2 - window.getWidth() / 2;
        window.setLocation( hpos, vpos );
    }
    
}
