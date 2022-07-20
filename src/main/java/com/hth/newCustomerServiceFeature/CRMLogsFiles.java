package com.hth.newCustomerServiceFeature;


import com.hth.backend.beans.CRMLOGS;
import com.hth.newCustomerServiceFeature.View.MyFrame;
import com.hth.newCustomerServiceFeature.crmLogsView.CrmLogFrame;
import com.hth.newCustomerServiceFeature.crmLogsView.InsureDataSingleton;
import com.hth.util.Insure;

import java.util.List;


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

