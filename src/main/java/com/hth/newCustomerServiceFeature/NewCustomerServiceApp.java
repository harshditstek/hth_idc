package com.hth.newCustomerServiceFeature;

import com.hth.backend.beans.CRMLOGS;
import com.hth.newCustomerServiceFeature.newCustomerView.NewFrame;

public class NewCustomerServiceApp {
    public static String user = "" ;
    public static String secondFlag = "";

    public static void main(String[] args) {
        NewFrame f = new NewFrame();
        CRMLOGS.insureData();
        if (args.length == 2){
            user = args[0];
            secondFlag = args[1] ;

        }



    }
}
