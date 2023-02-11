package com.hackydesk.thesaviour;

public class usermodelstore
{
    private String fname,number,email,bodyguards;

    public usermodelstore(String fname, String number, String email,String bodyguards) {
        this.fname = fname;
        this.number = number;
        this.email = email;
        this.bodyguards = bodyguards;

    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBodyguards() {
        return bodyguards;
    }

    public void setBodyguards(String bodyguards) {
        this.bodyguards = bodyguards;
    }


}

