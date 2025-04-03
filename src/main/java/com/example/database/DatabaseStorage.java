package com.example.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import com.example.pojo.Activity;
import com.example.pojo.User;
import com.example.util.ActivityType;
import com.example.util.DatabaseConnection;
import com.example.pojo.RoleType;

public class DatabaseStorage implements Storage {

    @Override
    public boolean writeUser(User user) {
        String query = "INSERT INTO users (userid, name, encryptedpassword, role, accountno, balance, mobilenumber, aadhaar) VALUES (?,?,?,?,?,?,?,?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, user.getUserid());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getEncryptedpassword());
            preparedStatement.setString(4, String.valueOf(user.getRole())); 
            preparedStatement.setInt(5, user.getAccountno());
            preparedStatement.setInt(6, (int) user.getBalance());
            preparedStatement.setLong(7, Long.parseLong(user.getMobilenumber())); 
            preparedStatement.setLong(8, Long.parseLong(user.getAadhaar()));

            int val = preparedStatement.executeUpdate();
            return val != 0; 

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return false;
    }


    @Override
    public boolean writeActivity(Activity activity) {
        String query = "INSERT INTO activity (activityid, userid, accountfrom, accountto, amount, date, activity) VALUES (?,?,?,?,?,?,?)";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, activity.getActivityid());
            preparedStatement.setInt(2, activity.getUserid());

            preparedStatement.setInt(3, activity.getAccountfrom());
            

            preparedStatement.setInt(4, activity.getAccountto());

            preparedStatement.setInt(5, (int) activity.getAmount());
            preparedStatement.setString(6, getTimeStamp(activity.getDate())); 

            preparedStatement.setString(7, String.valueOf(activity.getActivity()));


            int val = preparedStatement.executeUpdate();
            return val != 0; 

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return false;
    }

    @Override
    public User getUser(int userid) {
        String query = "SELECT * FROM users WHERE userid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userid);
            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                return new User(
                result.getInt("userid"), 
                result.getString("name"), 
                result.getString("encryptedpassword"),
                RoleType.valueOf(result.getString("role")),
                result.getInt("accountno"),
                result.getInt("balance"),
                String.valueOf(result.getLong("mobilenumber")),
                String.valueOf(result.getLong("Aadhaar")));
            }

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return null;
    }

    @Override
    public boolean updateUser(User user) {
        String query = "UPDATE users SET balance = ? WHERE userid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, (int) user.getBalance());
            preparedStatement.setInt(2, user.getUserid());

            int n = preparedStatement.executeUpdate();

            return n== 1;

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return false;
    }

    @Override
    public boolean updateProfile(User user) {
        String query = "UPDATE users SET mobilenumber = ?, aadhaar = ? WHERE userid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, Long.parseLong(user.getMobilenumber()));
            preparedStatement.setLong(2, Long.parseLong(user.getAadhaar()));
            preparedStatement.setInt(3, user.getUserid());

            int n = preparedStatement.executeUpdate();

            return n== 1;

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return false;
    }

    @Override
    public List<Activity> getTransaction(User user) {

        List<Activity> activities = new ArrayList<>();

        String query = "SELECT * FROM activity WHERE (userid = ? OR accountto = ?) AND activity IN (?, ?, ?) ";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, user.getUserid());
            preparedStatement.setInt(2, Integer.parseInt(user.getUserid()+"0"+user.getUserid()));
            preparedStatement.setString(3, "WITHDRAW");
            preparedStatement.setString(4, "DEPOSIT");
            preparedStatement.setString(5, "MONEYTRANSFER");
            ResultSet result = preparedStatement.executeQuery();

            while(result.next()) {
                Activity activity = new Activity(
                result.getString("activityid"), 
                result.getInt("userid"), 
                result.getInt("accountfrom"),
                result.getInt("accountto"),
                result.getInt("amount"),
                convertDate(result.getString("date")),
                ActivityType.valueOf(result.getString("activity")));
                activities.add(activity);
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return activities;
    }

    @Override
    public List<Activity> getActivity(User user) {

        List<Activity> activities = new ArrayList<>();

        String query = "SELECT * FROM activity WHERE (userid = ? OR accountto = ?) AND activity IN (?, ?, ?, ?, ?) ";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, user.getUserid());
            preparedStatement.setInt(2, Integer.parseInt(user.getUserid()+"0"+user.getUserid()));
            preparedStatement.setString(3, "ACCOUNTOPEN");
            preparedStatement.setString(4, "LOGIN");
            preparedStatement.setString(5, "LOGOUT");
            preparedStatement.setString(6, "GETNCUSTOMERS");
            preparedStatement.setString(7, "UPDATEPROFILE");
            ResultSet result = preparedStatement.executeQuery();

            while(result.next()) {
                Activity activity = new Activity(
                result.getString("activityid"), 
                result.getInt("userid"), 
                result.getInt("accountfrom"),
                result.getInt("accountto"),
                result.getInt("amount"),
                convertDate(result.getString("date")),
                ActivityType.valueOf(result.getString("activity")));
                activities.add(activity);
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return activities;
    }

    @Override
    public List<User> TopNBalance(int n) {

        List<User> users = new ArrayList<>();

        String query = "SELECT * FROM users WHERE role = 'CUSTOMER'  ORDER BY balance DESC LIMIT ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, n);

            ResultSet result = preparedStatement.executeQuery();

            while(result.next()) {
                User user = new User(
                    result.getInt("userid"), 
                    result.getString("name"), 
                    result.getString("encryptedpassword"),
                    RoleType.valueOf(result.getString("role")),
                    result.getInt("accountno"),
                    result.getInt("balance"),
                    String.valueOf(result.getLong("mobilenumber")),
                    String.valueOf(result.getLong("aadhaar")));

                    users.add(user);
                
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return users;
    }

    private static String getTimeStamp(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String mysqlDateTime = sdf.format(date); // Convert to MySQL format
        return mysqlDateTime; // Example: 2025-03-19 18:07:18
    }

    private static Date convertDate(String dateTime) {
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        try {
            Date date = sdf.parse(dateTime); // Convert back to Date
            return date; // Output: Wed Mar 19 18:07:18 IST 2025
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
