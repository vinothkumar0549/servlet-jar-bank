package com.example.pojo;

//import java.sql.Date;
import java.sql.Timestamp;

import com.example.util.*;
import com.fasterxml.jackson.annotation.JsonFormat;

public class Activity {

    private String activityid;
    private int userid;
    private int accountfrom;
    private int accountto;
    private double amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp date;
    private ActivityType activity;

    public Activity(String activityid, int userid, int accountfrom, int accountto, double amount, Timestamp date,
            ActivityType activity) {
        this.activityid = activityid;
        this.userid = userid;
        this.accountfrom = accountfrom;
        this.accountto = accountto;
        this.amount = amount;
        this.date = date;
        this.activity = activity;
    }

    public Activity(String activityid, int accountfrom, int accountto, double amount, Timestamp date,
            ActivityType activity) {
        this.activityid = activityid;
        this.accountfrom = accountfrom;
        this.accountto = accountto;
        this.amount = amount;
        this.date = date;
        this.activity = activity;
    }

    public String getActivityid() {
        return activityid;
    }

    public void setActivityid(String activityid) {
        this.activityid = activityid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getAccountfrom() {
        return accountfrom;
    }

    public void setAccountfrom(int accountfrom) {
        this.accountfrom = accountfrom;
    }

    public int getAccountto() {
        return accountto;
    }

    public void setAccountto(int accountto) {
        this.accountto = accountto;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public ActivityType getActivity() {
        return activity;
    }
    
    public void setActivity(ActivityType activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-45s%-20s%-20s%-20s%-20s%-30s%-20s%n", activityid, userid, accountfrom, accountto, amount, date, activity));
        return sb.toString(); 
    }

    public static ActivityType valueOf(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'valueOf'");
    }

}


