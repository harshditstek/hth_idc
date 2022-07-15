package com.hth.newCustomerServiceFeature;


import com.hth.newCustomerServiceFeature.View.MyFrame;
import com.hth.newCustomerServiceFeature.crmLogsView.CrmLogFrame;


public class CRMLogsFiles {
    public static String user = "" ;
    public static String secondFlag = "";

    public static void main(String[] args) {
        CrmLogFrame f = new CrmLogFrame();

        if (args.length == 2){
            user = args[0];
            secondFlag = args[1] ;

        }
    }
}

