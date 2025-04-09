package com.example.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import com.example.pojo.Activity;
import com.example.pojo.PasswordHistory;
import com.example.pojo.User;
import com.example.util.ActivityType;
import com.example.util.DatabaseConnection;
import com.example.pojo.RoleType;

public class DatabaseStorage implements Storage {

    @Override
    public boolean writeUser(User user) {
        //String query = "INSERT INTO users (userid, name, encryptedpassword, role, accountno, balance, mobilenumber, aadhaar) VALUES (?,?,?,?,?,?,?,?)";

        String insertuser = "INSERT INTO userdetails (userid, encryptedpassword, name, role, mobilenumber, aadhaar) VALUES (?,?,?,?,?,?)";
        String insertcustomer = "INSERT INTO accountdetails (accountno, userid, balance) VALUES (?,?,?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatementuser = connection.prepareStatement(insertuser);
             PreparedStatement preparedStatementcustomer = connection.prepareStatement(insertcustomer)) {

            preparedStatementuser.setInt(1, user.getUserid());
            preparedStatementuser.setString(2, user.getEncryptedpassword());
            preparedStatementuser.setString(3, user.getName());
            preparedStatementuser.setString(4, user.getRole().name()); // for "ADMIN"
            preparedStatementuser.setLong(5, user.getMobilenumber()); 
            preparedStatementuser.setLong(6, user.getAadhaar());

            int val = preparedStatementuser.executeUpdate();

            if(user.getRole() == RoleType.CUSTOMER){
                preparedStatementcustomer.setInt(1, user.getAccountno());
                preparedStatementcustomer.setInt(2, user.getUserid());
                preparedStatementcustomer.setInt(3, (int) user.getBalance());

                preparedStatementcustomer.executeUpdate();
            }

            return val != 0;

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return false;
    }


    @Override
    public boolean writeActivity(Activity activity) {
        System.out.println("\n\n\n\n\n\n\n\n\n"+activity.toString());
        String activityquery = "INSERT INTO activitydetails (activityid, userid, date, activity) VALUES (?,?,?,?)";
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatementactivity = connection.prepareStatement(activityquery);) {
            

            if(activity.getActivity() == ActivityType.ACCOUNTOPEN || 
              activity.getActivity() == ActivityType.LOGIN || 
              activity.getActivity() == ActivityType.LOGOUT ||
              activity.getActivity() == ActivityType.GETNCUSTOMERS ||
              activity.getActivity() == ActivityType.UPDATEPROFILE ||
              activity.getActivity() == ActivityType.CHANGEPASSWORD){

                preparedStatementactivity.setString(1, activity.getActivityid());
                preparedStatementactivity.setInt(2, activity.getUserid());

            
                preparedStatementactivity.setTimestamp(3, activity.getDate()); 
                preparedStatementactivity.setString(4, activity.getActivity().name());


                int val = preparedStatementactivity.executeUpdate();
                return val != 0;

            }

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return false;
    }

    @Override
    public boolean writeTransaction(Activity activity) {
        String activityquery = "INSERT INTO transactiondetails (transactionid, accountfrom, accountto, amount, date, transaction) VALUES (?,?,?,?,?,?)";
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatementtransaction = connection.prepareStatement(activityquery);) {
            System.out.println("\n\n\n\n\n\n"+activity.toString());

            if(activity.getActivity() == ActivityType.WITHDRAW || 
              activity.getActivity() == ActivityType.DEPOSIT || 
              activity.getActivity() == ActivityType.MONEYTRANSFER ||
              activity.getActivity() == ActivityType.BANKCHARGES){

                preparedStatementtransaction.setString(1, activity.getActivityid());

                preparedStatementtransaction.setInt(2, activity.getAccountfrom());

                if (activity.getAccountto() == 0) {
                    preparedStatementtransaction.setNull(3, java.sql.Types.INTEGER);
                } else{
                    preparedStatementtransaction.setInt(3, activity.getAccountto());
                }

                preparedStatementtransaction.setInt(4, (int)activity.getAmount());
            
                preparedStatementtransaction.setTimestamp(5, activity.getDate()); 

                preparedStatementtransaction.setString(6, activity.getActivity().name());

                int val = preparedStatementtransaction.executeUpdate();
                return val != 0;

            }

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return false;
    }

    @Override
    public User getUser(int userid) {
        //String query = "SELECT * FROM users WHERE userid = ?";

        String user = "SELECT u.userid, u.encryptedpassword, u.name, u.role, u.mobilenumber, u.aadhaar, a.accountno, a.balance, a.transactioncount FROM userdetails u LEFT JOIN accountdetails a ON u.userid = a.userid where u.userid = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(user)) {

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
                result.getLong("mobilenumber"),
                result.getLong("aadhaar"),
                result.getInt("transactioncount"));
            }

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return null;
    }

    @Override
    public boolean updateUser(User user) {
        String query = "UPDATE accountdetails SET balance = ?, transactioncount = ? WHERE accountno = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, (int) user.getBalance());
            preparedStatement.setInt(2, user.getTransactioncount());
            preparedStatement.setInt(3, user.getAccountno());

            int n = preparedStatement.executeUpdate();

            return n== 1;

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return false;
    }

    @Override
    public boolean updateProfile(User user) {
        String query = "UPDATE userdetails SET mobilenumber = ?, aadhaar = ? WHERE userid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, user.getMobilenumber());
            preparedStatement.setLong(2, user.getAadhaar());
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

        String query = "SELECT * FROM transactiondetails WHERE accountfrom = ? OR accountto = ? ORDER BY date DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                System.out.println("\n\n\n\n\n\n\n\n\n"+user.toString());

            preparedStatement.setInt(1, user.getAccountno());
            preparedStatement.setInt(2, user.getAccountno());
            ResultSet result = preparedStatement.executeQuery();

            while(result.next()) {
                Activity activity = new Activity(
                result.getString("transactionid"), 
                result.getInt("accountfrom"),
                result.getInt("accountto"),
                result.getInt("amount"),
                result.getTimestamp("date"),
                ActivityType.valueOf(result.getString("transaction")));
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

        String query = "SELECT * FROM activitydetails WHERE userid = ? ORDER BY date DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, user.getUserid());
            ResultSet result = preparedStatement.executeQuery();

            while(result.next()) {
                Activity activity = new Activity(
                result.getString("activityid"), 
                result.getInt("userid"), 
                0,0,0,
                result.getTimestamp("date"),
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

        //String query = "SELECT * FROM users WHERE role = 'CUSTOMER'  ORDER BY balance DESC LIMIT ?";

        String query = "SELECT u.userid, u.name, a.accountno, a.balance FROM userdetails u JOIN accountdetails a ON u.userid = a.userid ORDER BY a.balance desc LIMIT ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, n);

            ResultSet result = preparedStatement.executeQuery();

            while(result.next()) {
                User user = new User(
                    result.getInt("userid"), 
                    result.getString("name"), 
                    null, null,
                    result.getInt("accountno"),
                    result.getInt("balance"),
                    0,0,0);

                    users.add(user);
                
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return users;
    }


    @Override
    public boolean changepassword(User user) {
        
        String query = "UPDATE userdetails SET encryptedpassword = ? WHERE userid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getEncryptedpassword());
            preparedStatement.setInt(2, user.getUserid());

            int n = preparedStatement.executeUpdate();

            return n== 1;

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return false;
    }


    @Override
    public List<String> getPasswordHistory(User user) {
        String query = "SELECT oldpassword FROM passwordhistorydetails WHERE userid = ? ORDER BY id DESC LIMIT ?";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query)
        ) {
            stmt.setInt(1, user.getUserid());
            stmt.setInt(2, 3);

            ResultSet rs = stmt.executeQuery();

            List<String> oldpasswords = new ArrayList<>();
            while (rs.next()) {
                oldpasswords.add(rs.getString("oldpassword"));
            }
            return oldpasswords;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // private static String getTimeStamp(Date date){
    //     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //     String mysqlDateTime = sdf.format(date); // Convert to MySQL format
    //     return mysqlDateTime; // Example: 2025-03-19 18:07:18
    // }

    // private static Date convertDate(String dateTime) {
        
    //     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
    //     try {
    //         Date date = sdf.parse(dateTime); // Convert back to Date
    //         return date; // Output: Wed Mar 19 18:07:18 IST 2025
    //     } catch (ParseException e) {
    //         e.printStackTrace();
    //     }
    //     return null;
    // }
    
}
