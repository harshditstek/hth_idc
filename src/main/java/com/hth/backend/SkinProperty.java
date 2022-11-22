package com.hth.backend;

import java.io.FileInputStream;
import java.net.URLDecoder;
import java.util.Properties;

public class SkinProperty {

    private static Properties props = new Properties();

    static {
        try {
            String filePath = SkinProperty.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String decodedPath = URLDecoder.decode(filePath, "UTF-8");
            if (decodedPath.startsWith("/")) {
                decodedPath = decodedPath.replaceFirst("/", "");
            }
            if (decodedPath.endsWith(".jar")) {
                int index = decodedPath.lastIndexOf('/');
                //System.out.println(decodedPath.substring(0, index) + "/skin.properties");
                decodedPath = decodedPath.substring(0, index);
            }
            System.out.println(decodedPath+"/skin.properties");
            props.load(new FileInputStream(decodedPath + "/skin.properties"));
        } catch (Exception e) {

        }
    }

    public static final String image = props.getProperty("logo");
    public static final String headerBackground = props.getProperty("headerBackground");
    public static final String leftSidebarBackground = props.getProperty("leftSidebarBackground");
    public static final String stripBackground = props.getProperty("stripBackground");
}


