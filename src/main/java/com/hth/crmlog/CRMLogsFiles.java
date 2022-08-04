package com.hth.crmlog;


import com.hth.crmlog.beans.CRMLOGS;
import com.hth.crmlog.crmLogsView.CrmLogFrame;
import com.hth.crmlog.crmLogsView.InsureDataSingleton;
import com.hth.crmlog.util.Insure;
import com.hth.images.HTH_Image;

import javax.swing.*;


public class CRMLogsFiles {
    public static String user = "" ;
    public static String secondFlag = "";

    public static void main(String[] args) {

        if (args.length == 2){
            user = args[0];
            secondFlag = args[1] ;

        }
        CrmLogFrame f = new CrmLogFrame();
       // loader();
        Insure[] insureData = CRMLOGS.searchByName();
        System.out.println("main:"+insureData.length);
        InsureDataSingleton ids = InsureDataSingleton.singleton();
        ids.setInsureList(insureData);
    }
    public static JWindow loader(){
        JWindow window = new JWindow();
        window.getContentPane().add(new JLabel(new ImageIcon(HTH_Image.getImageURL("download_HTH.png"))));
        window.setBounds(850, 500, 200, 200);
        window.setVisible(true);
        return window;
    }
}

