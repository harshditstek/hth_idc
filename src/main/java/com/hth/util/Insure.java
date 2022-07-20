package com.hth.util;

public class Insure {

    private String fName;

    private String lName;

    private String ssn;

    private String phone;

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getPhone() {
        return phone;
    }

    public Insure(String fName, String lName, String ssn, String phone) {
        this.fName = fName;
        this.lName = lName;
        this.ssn = ssn;
        this.phone = phone;
    }
}
