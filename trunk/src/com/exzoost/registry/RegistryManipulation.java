/*
 * RegistryManipulation.java
 *
 * Created on March 19, 2005, 1:22 PM
 */

package com.exzoost.registry;

import ca.beq.util.win32.registry.RegistryKey;
import ca.beq.util.win32.registry.RegistryValue;
import ca.beq.util.win32.registry.RootKey;
import java.util.Iterator;


/**
 *
 * @author torvald
 */
public class RegistryManipulation {
    
    /** Creates a new instance of RegistryManipulation */
    public RegistryManipulation() {
    }
    
    public static String PostgreSQLApplicationDirectory() {
        RegistryKey r = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE, 
                "SOFTWARE\\PostgreSQL\\Installations");
//        if(r.hasValue("Installations")) {
//           RegistryValue v = r.getValue("Installations");
//           return v.toString();
//        }
        
        if(r.hasSubkeys()) {
            Iterator i = r.subkeys();
            while(i.hasNext()) {
                RegistryKey x = (RegistryKey)i.next();
                if(x.hasValue("Base Directory")) {
                    RegistryValue v = x.getValue("Base Directory");
                    return (String)v.getData() + "\\bin\\";
                }
            } // while
        }
        
        return "";
    }
    
    public static void main( String[] args ) {
        System.out.println(RegistryManipulation.PostgreSQLApplicationDirectory());
    }
}
