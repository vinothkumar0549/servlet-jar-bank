
package com.example.pojo;

import com.example.util.*;

public class User {
    
    private int userid;
    private String name;
    private String encryptedpassword;
    private RoleType role;
    private int accountno;
    private double balance;

    public User(String name, String encryptedpassword, RoleType role) {
        this.name = name;
        this.encryptedpassword = encryptedpassword;
        this.role = role;
    }

    public User(int userid, String name, String encryptedpassword, RoleType role, int accountno, double balance) {
        this.userid = userid;
        this.name = name;
        this.encryptedpassword = encryptedpassword;
        this.role = role;
        this.accountno = accountno;
        this.balance = balance;
    }

    public User() {
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEncryptedpassword() {
        return encryptedpassword;
    }

    public void setEncryptedpassword(String encryptedpassword) {
        this.encryptedpassword = encryptedpassword;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }
    
    public int getAccountno() {
        return accountno;
    }

    public void setAccountno(int accountno) {
        this.accountno = accountno;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s%-20s%-20s%-20s%-20s%-20s%n", userid, name, encryptedpassword, role, accountno, balance));
        return sb.toString();    
    }

}

