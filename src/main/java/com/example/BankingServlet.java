package com.example;

import java.io.*;
import java.util.*;
import org.json.*;

import com.example.database.FileStorage;
import com.example.pojo.User;
import com.example.service.UserService;
import com.example.util.RoleType;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@jakarta.servlet.annotation.WebServlet("/banking")
public class BankingServlet extends HttpServlet {

    UserService userservice = new UserService(new FileStorage());

    //private static final Map<String, String> sessionMap = new HashMap<>();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String path = request.getPathInfo();
        JSONObject requestBody = new JSONObject(readRequestBody(request));
        
        if ("/register".equals(path)) {
            out.print(registerUser(requestBody));
        } else if ("/login".equals(path)) {
            out.print(loginUser(requestBody));
        } else if ("/deposit".equals(path)) {
            out.print(deposit(requestBody));
        } else if ("/withdraw".equals(path)) {
            out.print(withdraw(requestBody));
        } else if ("/transfer".equals(path)) {
            out.print(transfer(requestBody));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print(new JSONObject().put("message", "Invalid endpoint"));
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String path = request.getPathInfo();
        
        if ("/transactions".equals(path)) {
            String customerId = request.getParameter("customerId");
            out.print(getTransactionHistory(customerId));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print(new JSONObject().put("message", "Invalid endpoint"));
        }
    }

    private String registerUser(JSONObject requestBody) throws IOException {
        String name = requestBody.getString("name");
        String password = requestBody.getString("password");
        RoleType usertype = RoleType.valueOf(requestBody.getString("usertype").toUpperCase());
        //String retypepassword = requestBody.getString("retypepassword");
        
        int userid = userservice.register(new User(name, password, usertype));

        JSONObject obj = new JSONObject();
        obj.put("UserId", userid);
        obj.put("message", "User registered successfully");
        return obj.toString();
    }

    private String loginUser(JSONObject requestBody) throws IOException {
        String userid = requestBody.getString("customerId");
        String password = requestBody.getString("password");

        //userservice.login(userid, password);
        
        // try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMER_FILE))) {
        //     String line;
        //     while ((line = br.readLine()) != null) {
        //         String[] parts = line.split(",");
        //         if (parts[0].equals(customerId) && parts[3].equals(password)) {
        //             String sessionId = UUID.randomUUID().toString();
        //             sessionMap.put(sessionId, customerId);
        //             return new JSONObject().put("sessionId", sessionId).toString();
        //         }
        //     }
        // }
        return new JSONObject().put("message", "Invalid credentials").toString();
    }
    
    private String deposit(JSONObject requestBody) throws IOException {
        String customerId = requestBody.getString("customerId");
        double amount = requestBody.getDouble("amount");

        // Update balance and transaction file (to be implemented)
        return new JSONObject().put("message", "Amount deposited successfully").toString();
    }
    
    private String withdraw(JSONObject requestBody) throws IOException {
        String customerId = requestBody.getString("customerId");
        double amount = requestBody.getDouble("amount");
        // Check balance, update balance, and log transaction (to be implemented)
        return new JSONObject().put("message", "Amount withdrawn successfully").toString();
    }

    private String transfer(JSONObject requestBody) throws IOException {
        String senderId = requestBody.getString("senderId");
        String receiverId = requestBody.getString("receiverId");
        double amount = requestBody.getDouble("amount");
        // Perform transfer logic (to be implemented)
        return new JSONObject().put("message", "Amount transferred successfully").toString();
    }
    
    private String getTransactionHistory(String customerId) throws IOException {
        // Retrieve and return transaction history from transaction.txt (to be implemented)
        return new JSONObject().put("message", "Transaction history retrieved").toString();
    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
