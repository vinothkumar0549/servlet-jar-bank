package com.example.service;

import java.util.*;

import java.time.LocalDateTime;
import java.sql.Timestamp;

import com.example.database.*;
import com.example.pojo.*;
import com.example.security.SecurityUtil;
import com.example.util.*;

public class UserService {

    private Storage storage;

    public UserService(Storage storage){
        this.storage = storage;
    }
    
    public int register(User user) {

        if(user == null){
            throw new IllegalArgumentException("User object cannot be null");
        }
        int userid = new Random().nextInt(9999 - 1001 + 1) + 1001;
        //int userid = new Random().nextInt(9000) + 1000;
        user.setUserid(userid);
        if(user.getRole() == RoleType.CUSTOMER){
            user.setAccountno(Integer.parseInt(userid + "0" + userid));
            user.setBalance(10000.00);
        }
        if(storage.writeUser(user)){
            Activity activity = new Activity(UUID.randomUUID().toString().replace("-", ""), user.getUserid(), 0, 0, user.getBalance(), Timestamp.valueOf(LocalDateTime.now()), ActivityType.ACCOUNTOPEN);
            if(storage.writeActivity(activity)){
                return user.getUserid();
            } else {
                throw new RuntimeException("Failed to log account creation activity");
            }
        } else {
            throw new RuntimeException("User registration failed");
        }
    }

    public User login(int userid, String encryptpassword) {
        User user = storage.getUser(userid);
        if(user == null){
            throw new IllegalArgumentException("User not found");
        }

        if(!user.getEncryptedpassword().equals(encryptpassword)){
            throw new SecurityException("Incorrect password");
        }
        if(storage.writeActivity(new Activity(UUID.randomUUID().toString().replace("-", ""), user.getUserid(), 0, 0, 0, Timestamp.valueOf(LocalDateTime.now()), ActivityType.LOGIN))){
            return user;
        }
        throw new RuntimeException("Failed to log login activity");
    }

    public boolean withdraw(User user, int amount) {
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if(user.getRole()!=RoleType.CUSTOMER){
            throw new IllegalArgumentException("Access Denied");
        }
        

        if(amount <= 0){
            throw new IllegalArgumentException("Invalid withdrawal amount");
        }
        
        if(user.getBalance() < amount) {
            throw new IllegalStateException("Insufficient balance");
        }
        user.setBalance(user.getBalance() - amount);
        user.setTransactioncount(user.getTransactioncount()+1);
        if(checkbankcharges(user)){  // check the bank charges...
            storage.writeTransaction(new Activity(UUID.randomUUID().toString().replace("-", ""), user.getUserid(), user.getAccountno(), 0, amount, Timestamp.valueOf(LocalDateTime.now()), ActivityType.BANKCHARGES));
        } 
        if(storage.updateUser(user)) {
            if(storage.writeTransaction(new Activity(UUID.randomUUID().toString().replace("-", ""), user.getUserid(), user.getAccountno(), 0, amount, Timestamp.valueOf(LocalDateTime.now()), ActivityType.WITHDRAW))){
                System.out.println("\n\n\n\n\n\n\n\n\n\n\n  Transaction count "+user.getTransactioncount());
                return true;
            }
            throw new RuntimeException("Failed to log withdrawal activity");
        }
        throw new RuntimeException("Failed to update user balance");

    }

    public boolean deposit(User user, int amount) {

        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if(user.getRole() != RoleType.CUSTOMER){
            throw new IllegalArgumentException("Access Denied");
        }
        if(amount <= 0){
            throw new IllegalArgumentException("Invalid deposit amount");
        }
        
        user.setBalance(user.getBalance() + amount);
        user.setTransactioncount(user.getTransactioncount()+1);
        if(checkbankcharges(user)){  // check the bank charges...
            storage.writeTransaction(new Activity(UUID.randomUUID().toString().replace("-", ""), user.getUserid(), user.getAccountno(), 0, amount, Timestamp.valueOf(LocalDateTime.now()), ActivityType.BANKCHARGES));
        } 
        if(storage.updateUser(user)) {

            if(storage.writeTransaction(new Activity(UUID.randomUUID().toString().replace("-", ""), user.getUserid(), user.getAccountno(), 0, amount, Timestamp.valueOf(LocalDateTime.now()), ActivityType.DEPOSIT))){
                System.out.println("\n\n\n\n\n\n\n\n\n\n\n  Transaction count "+user.getTransactioncount());
                return true;
            }
            throw new RuntimeException("Failed to Update Activity");
        }
        throw new RuntimeException("Failed to update user balance");

    }

    public boolean moneytransfer(User user, int receiverid, int amount) {
        
        if(user == null){
            throw new IllegalArgumentException("Sender user not found");
        }
        if(user.getRole() != RoleType.CUSTOMER){
            throw new IllegalArgumentException("Access Denied");
        }
        if(amount <= 0){
            throw new IllegalArgumentException("Invalid transfer amount");
        }
        if(user.getBalance() < amount) {
            throw new IllegalStateException("Insufficient balance");
        }
        User receiver = storage.getUser(receiverid);
        if(receiver == null || receiver.getRole() == RoleType.ADMIN){
            throw new IllegalArgumentException("Receiver account not valid");
        }
        System.out.println("\n\n\n\n\n useraccount"+user.toString() +" "+user.getAccountno());
        System.out.println("\n\n\n\n\n receiver account"+receiver.toString()+" "+receiver.getAccountno());
        user.setBalance(user.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);
        user.setTransactioncount(user.getTransactioncount()+1);
        if(checkbankcharges(user)){  // check the bank charges...
            storage.writeTransaction(new Activity(UUID.randomUUID().toString().replace("-", ""), user.getUserid(), user.getAccountno(), 0, amount, Timestamp.valueOf(LocalDateTime.now()), ActivityType.BANKCHARGES));
        }
        if(storage.updateUser(user) && storage.updateUser(receiver)){
            if(storage.writeTransaction(new Activity(UUID.randomUUID().toString().replace("-", ""), user.getUserid(), user.getAccountno(), receiver.getAccountno(), amount, Timestamp.valueOf(LocalDateTime.now()), ActivityType.MONEYTRANSFER))){
                System.out.println("\n\n\n\n\n\n\n\n\n\n\n  Transaction count "+user.getTransactioncount());
                return true;
            }
            throw new RuntimeException("Failed to Update Activity");
        }
        throw new RuntimeException("Failed to update user balances");
        
    }

    public boolean logout(User user) {
        if(user != null){
            storage.writeActivity(new Activity(UUID.randomUUID().toString().replace("-", ""), user.getUserid(), 0, 0, 0, Timestamp.valueOf(LocalDateTime.now()), ActivityType.LOGOUT));
            return true;
        }
        throw new IllegalArgumentException("Invalid user for logout");
    }

    public List<Activity> printTransaction(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
    
        List<Activity> transactions = storage.getTransaction(user);

        if (transactions == null || transactions.isEmpty()) {
            throw new RuntimeException("No Transaction data available for the user");
        }
    
        // for (Activity activity : activities) {
        //     System.out.println(activity.toString());
        // }
    
        return transactions;
    }

    public List<Activity> printActivity(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
    
        List<Activity> activities = storage.getActivity(user);

        if (activities == null || activities.isEmpty()) {
            throw new RuntimeException("No activity data available for the user");
        }
    
        // for (Activity activity : activities) {
        //     System.out.println(activity.toString());
        // }
    
        return activities;
    }

    public boolean updateProfile(User user, User updateuser) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be Found");
        }

        // Update only the changed fields
        if (updateuser.getMobilenumber() != 0) {
            user.setMobilenumber(updateuser.getMobilenumber());
        }
        if (updateuser.getAadhaar() != 0) {
            user.setAadhaar(updateuser.getAadhaar());
        }

        // Save updated user back to storage
        if(storage.updateProfile(user)){
            storage.writeActivity(new Activity(UUID.randomUUID().toString().replace("-", ""), user.getUserid(), 0, 0, 0, Timestamp.valueOf(LocalDateTime.now()), ActivityType.UPDATEPROFILE));
            return true;
        }
        throw new RuntimeException("Failed to update user balances");

        
    }
    

    public List<User> getTopNCustomer(User user, int n) {
        if( user.getRole() != RoleType.ADMIN){
            throw new IllegalArgumentException("Access Denied");
        }
        if (n <= 0) {
            throw new IllegalArgumentException("The number of customers must be greater than zero");
        }
    
        List<User> topNBalance = storage.TopNBalance(n);
    
        if (topNBalance == null || topNBalance.isEmpty()) {
            throw new RuntimeException("No customer data available");
        }

        if(! storage.writeActivity(new Activity(UUID.randomUUID().toString().replace("-", ""), user.getUserid(), 0, 0, 0, Timestamp.valueOf(LocalDateTime.now()), ActivityType.GETNCUSTOMERS))){
            throw new RuntimeException("Activity Not Strored");
        }

    
        //System.out.println("Top " + n + " Customers by Balance:");
        // for (User u : topNBalance) {
        //     System.out.println(u.toString());
        // }
    
        return topNBalance;
    }

    public boolean checkchangepassword(User user){

        if(user.getTransactioncount() % 5 == 0){
            return true;
        }
        return false;

    }

    public boolean checkbankcharges(User user){
        if(user.getTransactioncount() % 3 == 0){
            user.setBalance(user.getBalance() - 10);
            return true;
        }
        return false;
    }

    public boolean changepassword(User user, String oldpassword, String newpassword){
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }
        user.setEncryptedpassword(SecurityUtil.encrypt(newpassword, 1));
        if(storage.changepassword(user)) {
            if(storage.writeActivity(new Activity(UUID.randomUUID().toString().replace("-", ""), user.getUserid(), user.getAccountno(), 0, 0, Timestamp.valueOf(LocalDateTime.now()), ActivityType.CHANGEPASSWORD))){
                return true;
            }
            throw new RuntimeException("Failed to log the activity");
        }
        throw new RuntimeException("Failed to update the user");
    }
    
}
