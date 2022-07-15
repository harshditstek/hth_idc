package com.hth.newCustomerServiceFeature.Repository;

import com.hth.backend.iSeries;
import com.hth.id_card.HTH_IDC;
import com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord;

import java.util.List;

public class Repository {

    String member = "TR1" ;
    private static Repository repository ;

    private Repository(String userName){
    }

    public static Repository getInstance(String userName){
        if (repository == null){
            repository = new Repository(userName);
        }
        return repository ;
    }

    public String getName(String ssn){
        String name ="" ;
        String alias = "qtemp.insure";
        String file = "hthdatv1.insure("+member+")";
        String sql = "select ifnam, ilnam from qtemp.insure " +
                "where issn = '"+ssn+"' or ipolcy = '"+ssn+"' or ipolcy ='"+ssn+"'" ;
        List<String[]> result = iSeries.executeSQLByAlias(sql,alias,file);

        if ( result.size()!=0) {
            name = result.get(0)[0].trim() + "," + result.get(0)[1].trim();  // fname,lname
        }

        return name ;
    }

    public String[][] getInsureData(String firstChars){
        String name ="" ;
        String alias = "qtemp.insure";
        //---member
        String file = "hthdatv1.insure(tr1)";
        String sql = "select ifnam, ilnam, issn from qtemp.insure " +
                "where ifnam like '"+firstChars+"%' or ilnam like '"+firstChars+"%' " ;

        List<String[]> result = iSeries.executeSQLByAlias(sql,alias,file);

        if ( result.size()!=0) {
            String[][] data = new String[result.size()][3];
            for (int i =0 ;i<data.length; i++){
                data[i] = result.get(i);
            }
            return data ;
        }
        return new String[0][0];
    }

    public boolean validateClaimNumber(String claimNum){
        String alias = "qtemp.clmhdr";
        String file = "hthdatv1.clmhdr("+member+")";
        String sql = "select hclmno from qtemp.clmhdr where  hclmno = '"+claimNum+"'" ;

        List<String[]> result = iSeries.executeSQLByAlias(sql,alias,file);

        if ( result.size()< 1) {
            System.out.println("claimNumber doesn't exist");
            return false ;
        }
        return true ;
    }
    public Integer generateRefNum(){
        String alias = "qtemp.crmlog";
        String file = "pdlib.crmlog";
        String sql ="select max(cref#) from qtemp.crmlog " ;

        List<String[]> result = iSeries.executeSQLByAlias(sql,alias,file);

        //initial variable
        if ( result.size()< 1 ||  result.get(0)[0] == null) {
            return 1111111111 ;
        }
       int  maxRef =Integer.parseInt(result.get(0)[0]);
       return maxRef +1 ;
    }

    public Integer generateRefNumTest(){
        String alias = "QTEMP.CRMLOG";
        String file = "PDLIB.CRMLOG(TRT)";
        String sql ="select max(cref#) from QTEMP.CRMLOG" ;

        List<String[]> result = iSeries.executeSQLByAlias(sql,alias,file);

        //initial variable
        if ( result.size()< 1 ||  result.get(0)[0] == null) {
            return 1111111111 ;
        }
        int  maxRef =Integer.parseInt(result.get(0)[0]);
        return maxRef +1 ;
    }

    public void insertIntoCrmLog(CrmLogRecord crmLogRecord){
        String refNum = crmLogRecord.getRefNum();
        String claimType = crmLogRecord.getClaimType();
        String claimPhone = crmLogRecord.getPhoneNum();
        String fName = crmLogRecord.getfName();
        String lName = crmLogRecord.getlName();
        String companyName = crmLogRecord.getCompanyName();
        String ssn = crmLogRecord.getSsn();
        String claimNumber = crmLogRecord.getClaimNum();

        String user = crmLogRecord.getUser();
        String time = crmLogRecord.getTime();
        String date = crmLogRecord.getDate();
        String  note = crmLogRecord.getNote();
        String filler = crmLogRecord.getFiller();



        String sql = "insert into pdlib.crmlog (CREF#,CTYPE,CPHN,CFNAM,CLNAM,CCPYNM,CSSN,CCLMNO,CTIME," +
                "CDATE,CUSER,CNOTE,filler) values " +
                "('"+refNum+"', '"+claimType+"', '"+claimPhone+"', '"+fName+"', '"+lName+"'," +
                " '"+companyName+"', '"+ssn+"', '"+claimNumber+"', '"+time+"'," +
                " '"+date+ "', '"+user+"', '"+note+"', '"+filler+"' )";

        iSeries.executeSQL(sql);

        System.out.println("the Data is inserted successfully");


    }
}
