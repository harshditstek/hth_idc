package com.hth.newCustomerServiceFeature.crmLogsView;

import com.hth.backend.beans.CRMLOGS;
import com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2;

import java.util.LinkedList;
import java.util.List;


public class CRMLogsSingleton {

    private static CRMLogsSingleton list_instance = null;

    //public CrmLogRecord2[] crmlogList;
    public List<String[]> crmlogList = new LinkedList<String[]>();

   private CRMLogsSingleton(){
    }

    public List<String[]> getCrmlogList() {
        return crmlogList;
    }

    public void setCrmlogList(List<String[]> crmlogList) {
       if(crmlogList.size()==0)
        this.crmlogList = crmlogList;
       else
           this.crmlogList.addAll(crmlogList);
    }

    public String getMaxId(){
       String result;
       if(crmlogList.size()==0){
           result="0";
       }
       else{
           //result = crmlogList.stream().max(crmlogList.get(crmlogList.size()));
           result = crmlogList.get(crmlogList.size()-1)[0];
       }
       return result;
    }

    public static CRMLogsSingleton singleton()
    {
        if (list_instance == null) {
            list_instance = new CRMLogsSingleton();
        }
        return list_instance;
    }

}
