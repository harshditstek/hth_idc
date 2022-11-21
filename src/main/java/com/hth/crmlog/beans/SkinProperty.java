package com.hth.crmlog.beans;

import java.io.FileInputStream;
import java.util.Properties;

public class SkinProperty {

    private static Properties props = new Properties();

    static {
        try {
//            //String filePath = IDRecord.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//            //String decodedPath = URLDecoder.decode(filePath, "UTF-8");
//            //System.out.println("file:path:" + decodedPath);
//            props.load(new FileInputStream(jarDir.toString() + "\\skin.properties"));
            //System.out.println(jarDir.toString());

            props.load(new FileInputStream("/HTHv2.5/@ht/lib/skin.properties"));
        } catch (Exception e) {
        }
    }

    public static final String image = props.getProperty("logo");
    public static final String headerBackground = props.getProperty("headerBackground");
    public static final String leftSidebarBackground = props.getProperty("leftSidebarBackground");
    public static final String stripBackground = props.getProperty("stripBackground");
}
