package com.hth.report;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ReportData {

    private static ReportData list_instance = null;
    public List<String[][]> reportData = new LinkedList<String[][]>();
    private ReportData(){
    }
    public List<String[][]> getReportData() {
        return reportData;
    }
    public void setReportData(String[][] reportData) {
        this.reportData.addAll(Collections.singleton(reportData));
    }
    public static ReportData singleton()
    {
        if (list_instance == null) {
            list_instance = new ReportData();
        }
        return list_instance;
    }
}
