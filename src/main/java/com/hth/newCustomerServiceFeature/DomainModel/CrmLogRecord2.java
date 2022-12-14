package com.hth.newCustomerServiceFeature.DomainModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CrmLogRecord2 {

    private String refNum;
    private String claimType;
    private String phoneNum;
    private String fName;
    private String lName;
    private String companyName;

    private String customerGroup;
    private String ssn;
    private String claimNum;
    private String time;
    private String date;
    private String user;
    private String note;
    private String filler;

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

    public String getCustomerGroup(){ return customerGroup; }

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

    public CrmLogRecord2(String refNum, String claimType, String phoneNum, String fName, String lName, String companyName, String customerGroup, String ssn, String claimNum, String note) {
        this.refNum = refNum;
        this.claimType = claimType;
        this.phoneNum = phoneNum;
        this.fName = fName;
        this.lName = lName;
        this.companyName = companyName;
        this.customerGroup = customerGroup;
        this.ssn = ssn;
        this.claimNum = claimNum;
        this.note = note;
    }

    public CrmLogRecord2(com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2.Builder builder) {
        this.refNum = builder.refNum;
        this.claimType = builder.claimType;
        this.phoneNum = builder.phoneNum;
        this.fName = builder.fName;
        this.lName = builder.lName;
        this.companyName = builder.companyName;
        this.customerGroup = builder.customerGroup;
        this.ssn = builder.ssn;
        this.claimNum = builder.claimNum;
        this.time = builder.time;
        this.date = builder.date;
        this.note = builder.note;
        this.filler = builder.filler;
        this.user = builder.user;
    }

    public static class Builder {
        private String refNum;
        private String claimType;
        private String phoneNum;
        private String fName;
        private String lName;
        private String companyName;

        private String customerGroup;
        private String ssn;
        private String claimNum;
        private String user;
        private String time = getCurrentTime();
        private String date = getCurrentDate();
        private String note = "";
        private String filler = "";

        private String getCurrentTime() {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat timeformat = new SimpleDateFormat("HHmmss");
            String time = timeformat.format(c.getTime());
            return time;
        }

        private String getCurrentDate() {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("MMddyy");
            String date = dateformat.format(c.getTime());
            return date;
        }

        public com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2.Builder withRefNum(String refNum) {
            this.refNum = refNum;
            return this;
        }

        public com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2.Builder withClaimType(String claimType) {
            this.claimType = claimType;
            return this;
        }

        public com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2.Builder withPhoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
            return this;
        }

        public com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2.Builder withFName(String fName) {
            this.fName = fName;
            return this;
        }

        public com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2.Builder withLName(String lName) {
            this.lName = lName;
            return this;
        }

        public com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2.Builder withCompanyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2.Builder withCustomerGroup(String customerGroup) {
            this.customerGroup = customerGroup;
            return this;
        }

        public com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2.Builder withSSN(String ssn) {
            this.ssn = ssn;
            return this;
        }

        public com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2.Builder withClaimNum(String claimNum) {
            this.claimNum = claimNum;
            return this;
        }

        public com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2.Builder withUser(String user) {
            this.user = user;
            return this;
        }

        public com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2.Builder withTime(String time) {
            this.time = time;
            return this;
        }

        public com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2.Builder withDate(String date) {
            this.date = date;
            return this;
        }

        public com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2.Builder withNote(String note) {
            this.note = note;
            return this;
        }

        public com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2.Builder withFiller(String filler) {
            this.filler = filler;
            return this;
        }

        public com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2 build() {
            return new com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2(this);
        }


    }


}

