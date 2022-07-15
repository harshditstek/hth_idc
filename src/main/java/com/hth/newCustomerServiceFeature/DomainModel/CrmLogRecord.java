package com.hth.newCustomerServiceFeature.DomainModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.lang.String;

public class CrmLogRecord {
    private String refNum ;
    private String claimType ;
    private String phoneNum ;
    private String fName ;
    private String lName ;
    private String companyName ;
    private String ssn ;
    private String claimNum ;
    private String time ;
    private String date ;
    private String user ;
    private String note ;
    private String filler ;

    public String getRefNum() {
        return refNum;
    }

    public String getUser() {
        return user;
    }

    public String getClaimType() {
        return claimType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getSsn() {
        return ssn;
    }

    public String getClaimNum() {
        return claimNum;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    public String getFiller() {
        return filler;
    }

    public CrmLogRecord(Builder builder){
        this.refNum = builder.refNum ;
        this.claimType = builder.claimType ;
        this.phoneNum = builder.phoneNum ;
        this.fName = builder.fName ;
        this.lName = builder.lName ;
        this.companyName = builder.companyName;
        this.ssn = builder.ssn ;
        this.claimNum = builder.claimNum ;
        this.time = builder.time ;
        this.date = builder.date ;
        this.note = builder.note ;
        this.filler = builder.filler ;
        this.user = builder.user ;
    }

    public static class Builder {
        private String refNum ;
        private String claimType ;
        private String phoneNum ;
        private String fName ;
        private String lName ;
        private String companyName;
        private String ssn ;
        private String claimNum ;
        private String user ;
        private String time =getCurrentTime() ;
        private String date = getCurrentDate() ;
        private String note = "" ;
        private String filler  = "";

        private String  getCurrentTime(){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat timeformat = new SimpleDateFormat("HHmmss");
            String  time = timeformat.format(c.getTime());
            return time ;
        }

        private String  getCurrentDate(){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("MMddyy");
            String date = dateformat.format(c.getTime());
            return date ;
        }
        public Builder withRefNum(String refNum){
            this.refNum = refNum;
            return this ;
        }
        public Builder withClaimType(String claimType){
            this.claimType = claimType;
            return this ;
        }
        public Builder withPhoneNum(String phoneNum){
            this.phoneNum = phoneNum;
            return this ;
        }
        public Builder withFName(String fName){
            this.fName = fName;
            return this ;
        }
        public Builder withLName(String lName){
            this.lName = lName;
            return this ;
        }
        public Builder withCompanyName(String companyName){
            this.companyName = companyName;
            return this ;
        }
        public Builder withSSN(String ssn){
            this.ssn = ssn;
            return this ;
        }
        public Builder withClaimNum(String claimNum){
            this.claimNum = claimNum;
            return this ;
        }
        public Builder withUser(String user){
            this.user = user;
            return this ;
        }
        public Builder withTime(String time){
            this.time = time;
            return this ;
        }
        public Builder withDate(String date){
            this.date = date;
            return this ;
        }
        public Builder withNote(String note){
            this.note = note;
            return this ;
        }
        public Builder withFiller(String filler){
            this.filler = filler;
            return this ;
        }

        public CrmLogRecord build(){
            return new CrmLogRecord(this);
        }


    }



}
