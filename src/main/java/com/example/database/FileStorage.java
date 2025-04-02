package com.example.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import com.example.pojo.*;
import com.example.util.*;

public class FileStorage implements Storage {
    private final static String userpath = "C:/Users/Administrator/Desktop/servlet/ServletApp/src/main/java/com/example/files/user.txt";
    private final static String activitypath = "C:/Users/Administrator/Desktop/servlet/servletApp/src/main/java/com/example/files/activity.txt";

    @Override
    public boolean writeUser(User user) {
        try {
            try (BufferedWriter userwriter = new BufferedWriter(new FileWriter(userpath, true))) {
                userwriter.write(user.toString());
                userwriter.flush();
                return true;
            }
        } catch (IOException e) {
            System.out.println("User File Not Found...");
        }
        return false;
    }

    @Override
    public boolean writeActivity(Activity activity) {
        try {
            try (BufferedWriter activitywriter = new BufferedWriter(new FileWriter(activitypath, true))) {
                activitywriter.write(activity.toString());
                activitywriter.flush();
                return true;
            }
        } catch (IOException e) {
            System.out.println("Activity File Not Found...");
        }
        return false;
    }

    @Override
    public User getUser(int userid) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(userpath))){
            bufferedReader.readLine();
            String line;
            while ((line = bufferedReader.readLine()) != null) { 
                if(line.startsWith(String.valueOf(userid))){
                    String[] userdata = line.split("\\s+");
                    User user = new User(Integer.valueOf(userdata[0]), userdata[1], userdata[2], RoleType.valueOf(userdata[3]), Integer.valueOf(userdata[4]), Double.parseDouble(userdata[5]), userdata[6],userdata[7]);
                    return user;
                }
            }
        } catch (Exception e) {
            System.out.println("User File Not Found...");
        }
        return null;
    }

    @Override
    public boolean updateUser(User user) {
        try (RandomAccessFile raf = new RandomAccessFile(userpath, "rw")) {
            long position = 0;
            String line;    
            while ((line = raf.readLine()) != null) {
                if (line.startsWith(String.valueOf(user.getUserid()))) {
                    raf.seek(position);
                    raf.writeBytes(user.toString()); 
                    System.out.println("Customer ID " + user.getUserid() + " updated.");                    
                    return true;
                }
                position = raf.getFilePointer(); 
            }
            System.out.println("Customer ID not found.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Activity> getActivity(User user) {

        List<Activity> activities = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss");

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(activitypath))) {
            bufferedReader.readLine();
            String line;
            
            if(user.getRole() == RoleType.ADMIN){
                while ((line = bufferedReader.readLine()) != null) { 
                    String[] activitydata = line.split("\\s+");
                    if(Integer.valueOf(activitydata[1]) == user.getUserid()){
                        try {
                            activities.add(new Activity(activitydata[0], Integer.valueOf(activitydata[1]), Integer.valueOf(activitydata[2]), Integer.valueOf(activitydata[3]), Double.parseDouble(activitydata[4]), sdf.parse(String.join(" ", java.util.Arrays.copyOfRange(activitydata, 5, 10))), ActivityType.valueOf(activitydata[activitydata.length-1])));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else if(user.getRole() == RoleType.CUSTOMER) {
                while ((line = bufferedReader.readLine()) != null) { 
                    String[] activitydata = line.split("\\s+");
                    if(Integer.parseInt(activitydata[1]) == user.getUserid() || Integer.valueOf(activitydata[2]) == user.getAccountno() || Integer.valueOf(activitydata[3]) == user.getAccountno()){
                        try {
                            activities.add(new Activity(activitydata[0], Integer.valueOf(activitydata[1]), Integer.valueOf(activitydata[2]), Integer.valueOf(activitydata[3]), Double.parseDouble(activitydata[4]), sdf.parse(String.join(" ", java.util.Arrays.copyOfRange(activitydata, 5, 10))), ActivityType.valueOf(activitydata[activitydata.length-1])));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Activity File Not Found");
        }
        return activities;
    }

    @Override
    public List<User> TopNBalance(int n) {

        PriorityQueue<User> pq = new PriorityQueue<>((u1, u2) -> Double.compare(u1.getBalance(), u2.getBalance()));
    
        try (BufferedReader bufferedreader = new BufferedReader(new FileReader(userpath))) {
            bufferedreader.readLine();
            String line;
            while ((line = bufferedreader.readLine()) != null) {
                String[] userdata = line.split("\\s+");
                User user = new User(Integer.valueOf(userdata[0]), userdata[1], userdata[2], RoleType.valueOf(userdata[3]), Integer.valueOf(userdata[4]), Double.parseDouble(userdata[5]), userdata[6], userdata[7]);
    
                if (user.getRole() == RoleType.ADMIN) {
                    continue;
                }
    
                pq.offer(user);
    
                if (pq.size() > n) {
                    pq.poll(); 
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(pq);
        
        List<User> topnbalance = new ArrayList<>(pq);
        topnbalance.sort((u1, u2) -> Double.compare(u2.getBalance(), u1.getBalance()));
        
        return topnbalance;

    }

    @Override
    public List<Activity> getTransaction(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTransaction'");
    }
    
}
