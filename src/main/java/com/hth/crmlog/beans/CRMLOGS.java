package com.hth.crmlog.beans;

import com.hth.backend.iSeries;
import com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2;
import com.hth.crmlog.util.Insure;

import java.util.List;

public class CRMLOGS {
    public static List<String[]> searchAllClaim() {
        String alias = "QTEMP.CLMHDR";
        String file = "testdata.CLMHDR(TRT)";
        String sql = "SELECT HCLMNO FROM QTEMP.CLMHDR";
        List<String[]> resultList = iSeries.executeSQLByAlias(sql, alias, file);
        System.out.println(resultList.size());
        String[] result;
        for (int idx = 0; idx < resultList.size(); idx++) {
            result = resultList.get(idx);
            for (int i = 0; i < result.length; i++) {
                System.out.println(result[i].toString());
            }
        }

        return resultList;
    }

    public static List<String[]> searchClaim(String claim) {
        String alias = "QTEMP.CLMHDR";
        String file = "testdata.CLMHDR(TRT)";
        String sql = "SELECT HCLMNO FROM QTEMP.CLMHDR where HCLMNO = '" + claim + "'";
        List<String[]> resultList = iSeries.executeSQLByAlias(sql, alias, file);
        System.out.println(resultList.size());

        return resultList;
    }

    public static List<String[]> getPDLIBData() {
        String alias = "QTEMP.LDAWRKF";
        String file = "PDLIB.LDAWRKF(TRT)";
        String sql = "SELECT * FROM QTEMP.LDAWRKF";
        List<String[]> resultList = iSeries.executeSQLByAlias(sql, alias, file);
        System.out.println(resultList.size());

        return resultList;
    }

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

//        for (int idx = 0; idx < resultList.size(); idx++) {
//            result = resultList.get(idx);
//            for (int i = 0; i < result.length; i++) {
//                System.out.println(result[i].toString());
//            }
//        }
        return resultList;
    }

    public static Insure[] searchByName() {
        String alias = "qtemp.insure";
        String file = "testdata.insure(trt)";
        String sql = "SELECT ILNAM,IFNAM,ISSN,IHPHN FROM qtemp.insure";
        List<String[]> resultList = iSeries.executeSQLByAlias(sql, alias, file);

        Insure[] insureList = new Insure[resultList.size()];
        String[] result;
        String fName, lName, ssn, phone;

        for (int idx = 0; idx < insureList.length; idx++) {
            result = resultList.get(idx);
            fName = result[0].trim();
            lName = result[1].trim();
            ssn = result[2].trim();
            phone = result[3].trim();

            insureList[idx] = new Insure(fName, lName, ssn, phone);
        }

        return insureList;
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

    public static List<String[]> reportData(String startDate, String endDate, String cptType, String provider, String serviceType, String exclusionCode, String providerQuery, String serviceQuery, String exclusionQuery){
        StringBuilder sb = new StringBuilder();
        String[] alias = {"qtemp.clmdet", "qtemp.clmhdr", "qtemp.clmnot", "qtemp.codfil", "qtemp.insur3", "qtemp.insure", "qtemp.insdep", "qtemp.provdr"};
        String[] file = {"testdata.clmdet(TRT)", "testdata.clmhdr(TRT)", "testdata.clmnot(TRT)", "testdata.codfil(TRT)", "testdata.insur3(TRT)", "testdata.insure(TRT)", "testdata.insdep(TRT)", "testdata.provdr(TRT)"};

        sb.append("select c.dprc,a.HCLMNO as CLAIM_NUMBER ,c.DLINE as LINE_NO," +
                "concat(concat(Substring(digits((c.DDOS)),5,2),Substring(digits((c.DDOS)),3,2)),substring(digits((c.DDOS)),1,2)) as DATE_OF_SERVICE," +
                "a.HDIV as DIVISION,b.IPOLCY as POLICY_ID," +
                "concat(b.IFNAM , b.ILNAM) as EMPLOYEE, " +
                "c.DDEP as DEPENDENT_CODE , a.HCOVCD as COVERAGE,c.DAMTAL AS AMOUNT_CLAIMED,c.DAMTEX,c.DEXCD,c.DEXCD2,a.htotck as TOTAL_PAID," +
                "HICD1,HICD2,HICD3,HICD4,HICD5,HICD6,HICD7,HICD8,HICD9,HICD10," +
                "e.DESC1 as TYPE_OF_SERVICE, PPROVN as PROVIDER_ID,PNAME as PROVIDER_NAME " +
                "from qtemp.clmhdr a " +
                "join qtemp.clmdet c on dclmno=hclmno " +
                "right join qtemp.insure b on (hgrpno=igrpno and hssn=issn) " +
                "join qtemp.codfil e on (e.cdcod5=substring(c.DPRC,6,2) and cdcod1='T') " +
                //"left join  qtemp.insdep d on(a.hgrpno=d.DGRPNO and a.hssn=d.dessn and a.hdep=d.dcode) "+
                "join qtemp.provdr f on f.PPROVN = a.HPROVD " +
                "where " +
                "(concat(concat(Substring(digits((c.DDOS)),5,2),Substring(digits((c.DDOS)),3,2)),substring(digits((c.DDOS)),1,2)) between " + startDate + " and " + endDate + ") ");

        String dQuery = "";

            if (!cptType.equals("")) {
                dQuery += "( substring(c.DPRC,6,2)= '" + cptType + "' ";
            }
            if(!provider.equals("")){
                if(dQuery.isEmpty()){
                    dQuery += "( PNAME like '%" + provider + "%') ";
                }else{
                    dQuery += providerQuery+" PNAME like '%" + provider + "%') ";
                }
            }
            else {
                if (!dQuery.isEmpty()) {
                    dQuery += ")";
                }
            }
            if(!serviceType.equals("")){
                if(dQuery.isEmpty()){
                    dQuery += "(e.DESC1 like '%"+ serviceType+"%' ";
                }else{
                    dQuery += serviceQuery+" ( e.DESC1 like '%"+ serviceType+"%' ";
                }
            }
            if(!exclusionCode.equals("")){
                if(dQuery.isEmpty()){
                    dQuery += "(c.DEXCD like '%"+exclusionCode+"%' or c.DEXCD2 like '%"+exclusionCode+"%') ";
                }else{
                    dQuery += exclusionQuery+" (c.DEXCD like '%"+exclusionCode+"%' or c.DEXCD2 like '%"+exclusionCode+"%'))";
                }
            }
            else{
                if(!dQuery.isEmpty() && (dQuery.lastIndexOf("(")>dQuery.lastIndexOf(")"))){
                    dQuery += ")";
                }
            }

        if(!dQuery.isEmpty())
        {sb.append( "and "+dQuery);}
        sb.append(" order by a.hclmno desc");

        System.out.println("sb:" + sb.toString());

        List<String[]> resultList = iSeries.executeSQLByAlias(sb.toString(), alias, file);
        System.out.println("size of report:" + resultList.size());

        return resultList;
    }

}
