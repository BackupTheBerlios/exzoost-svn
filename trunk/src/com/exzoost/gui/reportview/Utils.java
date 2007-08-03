package com.exzoost.gui.reportview;

import java.io.File;


/*
 * just to help the file chooser dialog
 */
public class Utils {
        public final static String pdf = "pdf";
        public final static String xml = "xml";
        public final static String html = "html";
//        public final static String tiff = "tiff";
//        public final static String tif = "tif";
        public final static String png = "png";

        /*
         * Get the extension of a file.
         */
        public static String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 &&  i < s.length() - 1) {
                ext = s.substring(i+1).toLowerCase();
            }
            return ext;
        }
}