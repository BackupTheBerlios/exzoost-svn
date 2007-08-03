/*
 * Main.java
 *
 * Created on October 26, 2004, 7:35 PM
 */

package com.exzoost.gui.maingui;

import com.exzoost.gui.login.FirstTime;

/**
 *
 * @author  knight
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Check if it is the first time
        new FirstTime();
    }
    
}
