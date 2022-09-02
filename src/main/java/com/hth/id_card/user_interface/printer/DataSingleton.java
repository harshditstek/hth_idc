package com.hth.id_card.user_interface.printer;

import java.util.LinkedList;
import java.util.List;

public class DataSingleton {
    private static DataSingleton download_list = null;

    public List<String[]> insureList = new LinkedList<String[]>();

    public List<String[]> getInsureList() {
        return insureList;
    }

    public void setInsureList(List<String[]> insureList) {
        //this.insureList = insureList;
        this.insureList.addAll(insureList);
    }

    private DataSingleton(){
    }

    public static DataSingleton singleton()
    {
        if (download_list == null) {
            download_list = new DataSingleton();
        }
        return download_list;
    }

    public static void destroy(){
        download_list = null;
    }
}
