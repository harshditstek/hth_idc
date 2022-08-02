package com.hth.crmlog;


import com.hth.crmlog.beans.CRMLOGS;
import com.hth.crmlog.crmLogsView.CrmLogFrame;
import com.hth.crmlog.crmLogsView.InsureDataSingleton;
import com.hth.crmlog.util.Insure;


public class CRMLogsFiles {
    public static String user = "" ;
    public static String secondFlag = "";

    public static void main(String[] args) {
        CrmLogFrame f = new CrmLogFrame();
        Insure[] insureData = CRMLOGS.searchByName();
        System.out.println("main:"+insureData.length);
        InsureDataSingleton ids = InsureDataSingleton.singleton();
        ids.setInsureList(insureData);


        if (args.length == 2){
            user = args[0];
            secondFlag = args[1] ;

        }
    }
}
