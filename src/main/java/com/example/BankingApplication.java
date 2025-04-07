package com.example;
import java.util.List;
import java.util.Scanner;

import com.example.database.FileStorage;
import com.example.pojo.*;

import com.example.service.UserService;

public class BankingApplication {
    public static void main(String[] args) {

        UserService userservice = new UserService(new FileStorage());

        try (Scanner scanner = new Scanner(System.in)) {

            while(true) {

                System.out.println("----------------Main Menu----------------");
                System.out.println(" 1. Register \n 2. Login \n 3. Exit");
                System.out.println("Enter your Choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {

                    case 1:
                        try{
                            int id = register(scanner);
                            if(id != 0){
                                System.out.println("User Id: "+id);
                            }
                        }catch(IllegalArgumentException e){
                            System.out.println(e.getMessage());
                        }catch(RuntimeException e){
                            System.out.println(e.getMessage());
                        }catch(Exception e){
                            System.out.println(e.getMessage());
                        }
                        // else{
                        //     System.out.println("Not Register Successfully...");
                        // }
                        break;

                    case 2:
                        User session = null;
                        try{
                            session = login(scanner);
                        }catch(IllegalArgumentException e){
                            System.out.println(e.getMessage());
                        }catch(SecurityException e){
                            System.out.println(e.getMessage());
                        }catch(RuntimeException e){
                            System.out.println(e.getMessage());
                        }
                        

                        while(session != null && session.getRole() == RoleType.CUSTOMER) {

                            System.out.println(" 1. Withdraw \n 2. Deposit \n 3. Money Transfer \n 4. Print User Activity \n 5. Logout");
                            System.out.println("Enter the Choice: ");
                            choice = scanner.nextInt();

                            switch (choice) {

                                case 1:

                                    System.out.println("Enter the Amount to be Withdraw: ");
                                    int withdrawmoney = scanner.nextInt();

                                    try{
                                        if(userservice.withdraw(session, withdrawmoney)) {
                                            System.out.println("Amount Withdraw...");
                                        }
                                    }catch(IllegalArgumentException e){
                                        System.out.println(e.getMessage());
                                    }catch(IllegalStateException e){
                                        System.out.println(e.getMessage());
                                    }catch(RuntimeException e){
                                        System.out.println(e.getMessage());
                                    }catch(Exception e){
                                        System.out.println(e.getMessage());
                                    }
                                    break;

                                case 2:

                                    System.out.println("Enter the Amount to be Deposit: ");
                                    int depositmoney = scanner.nextInt();

                                    try{
                                        if(userservice.deposit(session, depositmoney)) {
                                            System.out.println("Amount Desposit");
                                        }
                                    }catch(IllegalArgumentException e){
                                        System.out.println(e.getMessage());
                                    }catch(RuntimeException e){
                                        System.out.println(e.getMessage());
                                    }catch(Exception e){
                                        System.out.println(e.getMessage());
                                    }

                                    break;
                                case 3:

                                    System.out.println("Enter the Receiver Account No: ");
                                    int receiveruser = scanner.nextInt();

                                    System.out.println("Enter the Amount");
                                    int transferamount = scanner.nextInt();

                                    try{
                                        if(userservice.moneytransfer(session, receiveruser, transferamount)) {
                                            System.out.println("Money Transferred...");
                                        }
                                    }catch(IllegalArgumentException e){
                                        System.out.println(e.getMessage());
                                    }catch(IllegalStateException e){
                                        System.out.println(e.getMessage());
                                    }catch(RuntimeException e){
                                        System.out.println(e.getMessage());
                                    }catch(Exception e){
                                        System.out.println(e.getMessage());
                                    }

                                    break;

                                case 4:

                                    System.out.println("-----------------Activity Log------------------------");
                                    System.out.printf("%-45s%-20s%-20s%-20s%-20s%-30s%-20s%n", "ActivityId", "userid", "accountfrom", "accountto", "amount", "date", "activity");

                                    try{
                                        List<Activity> activities = userservice.printActivity(session);
                                        for (Activity activity : activities) {
                                            System.out.println(activity.toString());
                                        }
                                    }catch(IllegalArgumentException e){
                                        System.out.println(e.getMessage());
                                    }catch(RuntimeException e){
                                        System.out.println(e.getMessage());
                                    }catch(Exception e){
                                        System.out.println(e.getMessage());
                                    }
                                    break;  

                                case 5:

                                    try{
                                        userservice.logout(session);
                                        session = null;
                                    }catch(IllegalArgumentException e){
                                        System.out.println(e.getMessage());
                                    }catch(Exception e){
                                        System.out.println(e.getMessage());
                                    }
                                    break;
                            
                                default:

                                    System.out.println("Invalid Option...");
                            }
                        }

                        while(session != null && session.getRole() == RoleType.ADMIN) {

                            System.out.println(" 1. Get Top N Customer \n 2. Print Activity \n 3. Logout");
                            System.out.println("Enter your Choice: ");
                            choice = scanner.nextInt();

                            switch (choice) {

                                case 1:
                                    System.out.println("Enter the NO. of N Customer: ");
                                    int n = scanner.nextInt();
                                    try{
                                        List<User> topusers = userservice.getTopNCustomer(session, n);
                                        for (User user : topusers) {
                                            System.out.println(user.toString());
                                        }
                                    }catch(IllegalArgumentException e){
                                        System.out.println(e.getMessage());
                                    }catch(RuntimeException e){
                                        System.out.println(e.getMessage());
                                    }catch(Exception e){
                                        System.out.println(e.getMessage());
                                    }
                                    break;

                                case 2:

                                    System.out.println("-----------------Activity Log------------------------");
                                    System.out.printf("%-45s%-20s%-20s%-20s%-20s%-30s%-20s%n", "Activity", "userid", "accountfrom", "accountto", "amount", "date", "activity");

                                    try{
                                        List<Activity> activities = userservice.printActivity(session);
                                        for (Activity activity : activities) {
                                            System.out.println(activity.toString());
                                        }
                                    }catch(IllegalArgumentException e){
                                        System.out.println(e.getMessage());
                                    }catch(RuntimeException e){
                                        System.out.println(e.getMessage());
                                    }catch(Exception e){
                                        System.out.println(e.getMessage());
                                    }
                                    break;

                                case 3:
                                    userservice.logout(session);
                                    session = null;
                                    break;
                            
                                default:
                                    System.out.println("Invalid Option...");
                                    break;
                            }
                        }
                        
                        break;

                    case 3:

                        System.exit(0);

                    default:
                    
                        System.out.println("Invalid Option...");
                        break;
                }
            }
        }
    }

    public static int register(Scanner scanner) {

        UserService userservice = new UserService(new FileStorage());

        System.out.println("Enter the Name: ");
        String name = scanner.nextLine();

        System.out.println("Enter the Password: ");
        String password = scanner.nextLine();

        System.out.println("Enter the Re-Type Password: ");
        String retypepassword = scanner.nextLine();

        System.out.println("Enter the Mobile Number:");
        long mobilenumber = Long.parseLong(scanner.nextLine());  // Safe way

        System.out.println("Enter the Aadhaar Number: ");
        long aadhaar = Long.parseLong(scanner.nextLine());  // Safe way

        if(! password.equals(retypepassword)) {
            System.out.println("Enter the Same Password....");
            return 0;
        }

        System.out.println("Enter the Role: ");
        String role = scanner.nextLine();

        try{
            RoleType roletype = RoleType.valueOf(role.toUpperCase());

            if ((roletype == RoleType.ADMIN) || (roletype == RoleType.CUSTOMER)) {
                return userservice.register(new User(name, encryptpassword(password, 1), roletype, mobilenumber, aadhaar));                    
                
            }

        }catch(IllegalArgumentException e) {
            System.out.println("Not Valid Role...");
        }
        return 0;
        
    }

    public static User login(Scanner scanner) {

        UserService userservice = new UserService(new FileStorage());

        System.out.println("Enter Your User Id: ");
        int userid = scanner.nextInt();

        scanner.nextLine();

        System.out.println("Enter Your Password: ");
        String password = scanner.nextLine();

        return userservice.login(userid, encryptpassword(password, 1));

    }

    public static String encryptpassword(String password, int shift) {

        StringBuilder builder = new StringBuilder();

        for (char c : password.toCharArray()) {

            if (Character.isLetterOrDigit(c)) {

                char base = Character.isUpperCase(c) ? 'A' : (Character.isLowerCase(c) ? 'a' : '0');
                int range = Character.isDigit(c) ? 10 : 26; 
                builder.append((char) (base + (c - base + shift) % range));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
