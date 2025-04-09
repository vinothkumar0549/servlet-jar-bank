package com.example.pojo;

public class PasswordHistory {

    private int userid;
    private String password1;
    private String password2;
    private String password3;

    public PasswordHistory(){
        
    }

    public PasswordHistory(int userid, String password1, String password2, String password3) {
        this.userid = userid;
        this.password1 = password1;
        this.password2 = password2;
        this.password3 = password3;
    }
    public int getUserid() {
        return userid;
    }
    public void setUserid(int userid) {
        this.userid = userid;
    }
    public String getPassword1() {
        return password1;
    }
    public void setPassword1(String password1) {
        this.password1 = password1;
    }
    public String getPassword2() {
        return password2;
    }
    public void setPassword2(String password2) {
        this.password2 = password2;
    }
    public String getPassword3() {
        return password3;
    }
    public void setPassword3(String password3) {
        this.password3 = password3;
    }

    

}
