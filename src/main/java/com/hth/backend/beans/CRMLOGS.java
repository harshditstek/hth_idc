package com.hth.backend.beans;

import com.hth.backend.iSeries;
import com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord;
import com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2;
import com.hth.util.Division;

import java.util.List;

public class CRMLOGS {
    public static List<String[]> getCrmLogs(String maxId) {
        String alias = "QTEMP.CRMLOG";
        String file = "PDLIB.CRMLOG(TRT)";
        String sql = "SELECT * FROM QTEMP.CRMLOG where CREF# > '" + maxId + "'";
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

    public static List<String[]> getCrmLogsByReference(String reference) {
        String alias = "QTEMP.CRMLOG";
        String file = "PDLIB.CRMLOG(TRT)";
        String sql = "SELECT * FROM QTEMP.CRMLOG where CREF# > '" + reference + "'";
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

    public static CrmLogRecord2[] getCrmLogs2() {
        String alias = "QTEMP.CRMLOG";
        String file = "PDLIB.CRMLOG(TRT)";
        String sql = "SELECT * FROM QTEMP.CRMLOG";
        List<String[]> resultList = iSeries.executeSQLByAlias(sql, alias, file);

        CrmLogRecord2[] crmLogList = new CrmLogRecord2[resultList.size()];
        String[] result;
        String refNum, claimType, phoneNum, fName, lName, companyName, customerGroup, ssn, claimNum, note;

        for (int idx = 0; idx < crmLogList.length; idx++) {
            result = resultList.get(idx);
            refNum = result[0].trim();
            claimType = result[1].trim();
            phoneNum = result[2].trim();
            fName = result[3].trim();
            lName = result[4].trim();
            companyName = result[5].trim();
            customerGroup = result[6].trim();
            ssn = result[7].trim();
            claimNum = result[8].trim();
            note = result[12].trim();

            crmLogList[idx] = new CrmLogRecord2(refNum, claimType, phoneNum, fName, lName, companyName, customerGroup, ssn, claimNum, note);
        }
        return crmLogList;
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
        String alias = "qtemp.insure";
        String file = "testdata.insure(trt)";
        String sql = "SELECT ILNAM,IFNAM,ISSN,IHPHN FROM qtemp.insure " +
                "WHERE IFNAM like '" + firstChars + "%' or ILNAM like '" + firstChars + "%' ";
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
        String alias = "qtemp.insure";
        String file = "testdata.insure(trt)";
        String sql = "SELECT IFNAM,ILNAM,ISSN FROM QTEMP.INSURE WHERE IHPHN = '" + phoneNumber + "'";
        //String sql  = "Select * from QTEMP.INSURE limit 1";
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

    public static List<String[]> searchByReferenceNumber(String reference) {
        String alias = "QTEMP.CRMLOG";
        String file = "PDLIB.CRMLOG(TRT)";
        String sql = "SELECT * FROM QTEMP.CRMLOG WHERE CREF# = '" + reference + "'";
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

    public static List<String[]> insureData() {
        String alias = "qtemp.insure";
        String file = "testdata.insure(trt)";
        String sql = "SELECT IFNAM,ILNAM,ISSN,IHPHN FROM QTEMP.INSURE";
        List<String[]> resultList = iSeries.executeSQLByAlias(sql, alias, file);

        String[] result;

        for (int idx = 0; idx < resultList.size(); idx++) {
            result = resultList.get(idx);
            for (int i = 0; i < result.length; i++) {
                System.out.println(result[i].toString());
            }
        }
        return resultList;
    }

}
