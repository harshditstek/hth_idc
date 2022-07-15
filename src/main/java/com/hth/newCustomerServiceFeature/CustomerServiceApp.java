package com.hth.newCustomerServiceFeature;


//import com.hth.id_card.user_interface.decorator.ID_Decorator;
import com.hth.id_card.user_interface.printer.ID_PrinterSelection;
import com.hth.newCustomerServiceFeature.View.MyFrame;
import com.hth.util.GroupMaster;

import javax.swing.*;

public class CustomerServiceApp {
    public static String user = "" ;
    public static String secondFlag = "";

    public static void main(String[] args) {
        MyFrame f = new MyFrame();

        if (args.length == 2){
            user = args[0];
            secondFlag = args[1] ;

        }



    }
}
