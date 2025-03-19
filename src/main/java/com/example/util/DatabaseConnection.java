package com.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/mysql"; // Replace with your DB name
    private static final String USER = "root"; // Your DB username
    private static final String PASSWORD = "mysql"; // Your DB password

    private static DatabaseConnection instance; // Singleton instance
    private Connection connection;

    // Private constructor to prevent instantiation
    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

}

// package com.example.util;

// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;

// public class DatabaseConnection {
//     private static final String URL = "jdbc:mysql://localhost:3306/your_database"; // Replace with your DB name
//     private static final String USER = "root"; // Your DB username
//     private static final String PASSWORD = "mysql"; // Your DB password

//     public static Connection getConnection() {
//         Connection conn = null;
//         try {
//             conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             System.out.println("Database connected successfully!");
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return conn;
//     }
// }
