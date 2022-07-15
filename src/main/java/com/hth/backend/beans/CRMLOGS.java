package com.hth.backend.beans;

import com.hth.backend.iSeries;
import com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord;
import com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2;

import java.util.List;

public class CRMLOGS {
    public static List<String[]> getCrmLogs() {
        String alias = "QTEMP.CRMLOG";
        String file = "PDLIB.CRMLOG(TRT)";
        String sql = "SELECT * FROM QTEMP.CRMLOG";
        List<String[]> resultList = iSeries.executeSQLByAlias(sql, alias, file);
//        String[] result;
//        for (int idx = 0; idx < resultList.size(); idx++) {
//           result = resultList.get(idx);
//           for(int i=0;i<result.length;i++){
//               System.out.println(result[i].toString());
//           }
//      }
        return resultList;
    }

    public static void insertCrmLogs(CrmLogRecord2 crmLogRecord) {
        String claimType = crmLogRecord.getClaimType();
        String claimPhone = crmLogRecord.getPhoneNum();
        String fName = crmLogRecord.getfName();
        String lName = crmLogRecord.getlName();
        String companyName = crmLogRecord.getCompanyName();
        String customerGroup = crmLogRecord.getCustomerGroup();
        String ssn = crmLogRecord.getSsn();
        String claimNumber = crmLogRecord.getClaimNum();

        String user = crmLogRecord.getUser();
        String time = crmLogRecord.getTime();
        String date = crmLogRecord.getDate();
        String note = crmLogRecord.getNote();
        String filler = crmLogRecord.getFiller();

        String alias = "QTEMP.CRMLOG";
        String file = "PDLIB.CRMLOG(TRT)";

        String sql = "INSERT INTO " + alias + " (CREF#,CTYPE,CPHN,CFNAM,CLNAM,CCPYNM,CGRPNO,CSSN,CCLMNO,CTIME," +
                "CDATE,CUSER,CNOTE,filler) values " +
                "((select max(cref#)+1 from qtemp.crmlog), '" + claimType + "', '" + claimPhone + "', '" + fName + "', '" + lName + "'," +
                " '" + companyName + "','" + customerGroup + "', '" + ssn + "', '" + claimNumber + "', '" + time + "'," +
                " '" + date + "', '" + user + "', '" + note + "', '" + filler + "' )";

        //iSeries.executeSQL(sql);
        //String values = "'1000000006','M','6093391560','JANE','DOE','ABC COMPANY','ABC','123456789','0000007','71222','104500','DABREP','TEST ENTRY',''";
        //String sql = "INSERT INTO "+ alias + " (CREF#,CTYPE,CPHN,CFNAM,CLNAM,CCPYNM,CGRPNO,CSSN,CCLMNO,CTIME,CDATE,CUSER,CNOTE,filler) values ("+values+")";
        iSeries.executeSQLByAlias(sql, alias, file);
        System.out.println("The Data is Inserted successfully");
    }

    public static List<String[]> searchByName(String firstChars) {
        String alias = "QTEMP.CRMLOG";
        String file = "PDLIB.CRMLOG(TRT)";
        String sql = "SELECT CFNAM,CLNAM,CSSN FROM QTEMP.CRMLOG " +
                "WHERE CFNAM like '" + firstChars + "%' or CLNAM like '" + firstChars + "%' ";
        List<String[]> resultList = iSeries.executeSQLByAlias(sql, alias, file);
        String[] result;
        String reference, provider;

        for (int idx = 0; idx < resultList.size(); idx++) {
            result = resultList.get(idx);
            for (int i = 0; i < result.length; i++) {
                System.out.println(result[i].toString());
            }
        }
        return resultList;
    }

    public static List<String[]> searchByPhone(String phoneNumber) {
        String alias = "QTEMP.CRMLOG";
        String file = "PDLIB.CRMLOG(TRT)";
        String sql = "SELECT CREF#,CFNAM,CLNAM,CCPYNM,CGRPNO,CSSN,CCLMNO,CNOTE FROM QTEMP.CRMLOG WHERE CPHN = '" + phoneNumber + "'";
        List<String[]> resultList = iSeries.executeSQLByAlias(sql, alias, file);

        String[] result;

        for (int idx = 0; idx < resultList.size(); idx++) {
            result = resultList.get(idx);
            for (int i = 0; i < result.length; i++) {
                //System.out.println(result[i].toString());
            }
        }
        return resultList;
    }
}
