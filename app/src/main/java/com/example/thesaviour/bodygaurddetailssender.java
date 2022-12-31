package com.example.thesaviour;

public class bodygaurddetailssender
{
    private String bname,bnumber;

    public bodygaurddetailssender(String bname, String bnumber) {
        this.bname = bname;
        this.bnumber = bnumber;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getBnumber() {
        return bnumber;
    }

    public void setBnumber(String bnumber) {
        this.bnumber = bnumber;
    }
}

