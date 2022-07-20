package com.hth.newCustomerServiceFeature.crmLogsView;

import com.hth.util.Insure;

import java.util.LinkedList;
import java.util.List;

public class InsureDataSingleton {

    private static InsureDataSingleton insure_list = null;

    //public CrmLogRecord2[] crmlogList;
    //public List<String[]> insureList = new LinkedList<String[]>();
    public List<Insure[]> insureList = new LinkedList<Insure[]>();

    public List<Insure[]> getInsureList() {
        return insureList;
    }

    public void setInsureList(Insure[] insureList) {
        //this.insureList = insureList;
        this.insureList.add(insureList);
    }

    //    public List<String[]> getInsureList() {
//        return insureList;
//    }
//
//    public void setInsureList(List<String[]> insureList) {
//        this.insureList.addAll(insureList);
//    }

    private InsureDataSingleton(){
    }

    public static InsureDataSingleton singleton()
    {
        if (insure_list == null) {
            insure_list = new InsureDataSingleton();
        }
        return insure_list;
    }

    public static void main(String[] args) {
        //List<String[]> data = InsureDataSingleton.singleton().getInsureList();
        //System.out.println("ss:"+data.size());
    }
}
