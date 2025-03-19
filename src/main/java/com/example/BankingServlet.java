package com.example;

import java.io.*;
import java.util.*;

import org.json.*;

import com.example.database.FileStorage;
import com.example.database.Storage;
import com.example.pojo.Activity;
import com.example.pojo.User;
import com.example.service.UserService;
import com.example.util.RoleType;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@jakarta.servlet.annotation.WebServlet("/banking")
public class BankingServlet extends HttpServlet {

    Storage storage = new FileStorage();

    UserService userservice = new UserService(storage);

    //private static final Map<String, String> sessionMap = new HashMap<>();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String path = request.getPathInfo();
        JSONObject requestBody = new JSONObject(readRequestBody(request));
        
        if ("/register".equals(path)) {
            out.print(registerUser(requestBody, response));
        } else if ("/login".equals(path)) {
            out.print(loginUser(requestBody, response));
        } else if ("/deposit".equals(path)) {
            out.print(deposit(requestBody, response));
        } else if ("/withdraw".equals(path)) {
            out.print(withdraw(requestBody, response));
        } else if ("/moneytransfer".equals(path)) {
            out.print(moneytransfer(requestBody, response));
        } else if ("/getactivity".equals(path)){
            out.print(getactivity(requestBody, response));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print(new JSONObject().put("message", "Invalid endpoint"));
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String path = request.getPathInfo();
        
        if (path != null && path.startsWith("/topncustomer/")) {
            JSONObject responsejson = new JSONObject();

            try {
                // Extract 'n' after "/topncustomer/"
                String nStr = path.substring("/topncustomer/".length()); // Get only 'n'
                int n = Integer.parseInt(nStr); // Convert 'n' to integer
                
                // Call method to get top N customers
                List<User> topnusersbalance = userservice.getTopNCustomer(n);
                responsejson.put("Users", topnusersbalance);
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (NumberFormatException e) {
                responsejson.put("error", "Invalid Number Format");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch (IllegalArgumentException e){
                responsejson.put("error", "The Number Of Customers greate than Zero "+ e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch(RuntimeException e){
                responsejson.put("error","Customer Data Not Available "+e.getMessage());
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } catch(Exception e){
                responsejson.put("Error", "Unexpected error "+ e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            out.print(responsejson.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print(new JSONObject().put("message", "Invalid endpoint"));
        }
    }

    private String registerUser(JSONObject requestBody, HttpServletResponse response) throws IOException {
        String name = requestBody.getString("name");
        String password = encrypt(requestBody.getString("password"), 1);
        RoleType usertype = null;
        //String retypepassword = requestBody.getString("retypepassword");
        
        JSONObject responsejson = new JSONObject();

        try{
            usertype = RoleType.valueOf(requestBody.getString("usertype").toUpperCase());
            
        }catch(Exception e){
            responsejson.put("error", " Invalid User Type: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return response.toString();
        }
        
        try{
            int userid = userservice.register(new User(name, password, usertype));
            responsejson.put("UserId", userid);
            responsejson.put("message", "User registered successfully");
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (IllegalArgumentException e) {
            responsejson.put("error", "Invalid input: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
                
        } catch (RuntimeException e) {
            responsejson.put("error", "Registration failed: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict
                
        } catch (Exception e) {
            responsejson.put("error", "Unexpected error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }

        
        return responsejson.toString();
    }

    private String loginUser(JSONObject requestBody, HttpServletResponse response) throws IOException {

        int userid = Integer.parseInt(requestBody.getString("userid"));
        String password = encrypt(requestBody.getString("password"), 1);
        JSONObject responsejson = new JSONObject();


        try{
            User user = userservice.login(userid, password);

            JSONObject userjson = new JSONObject();

            userjson.put("userid ", user.getUserid());
            userjson.put("name", user.getName());
            userjson.put("encryptedpassword", user.getEncryptedpassword());
            userjson.put("roletype", user.getRole());
            userjson.put("accountno", user.getAccountno());
            userjson.put("balance", user.getBalance());

            responsejson.put("user", userjson);
            responsejson.put("message: ", " login successfully");

            response.setStatus(HttpServletResponse.SC_OK);

        }catch(IllegalArgumentException e){
            responsejson.put("error ", "Invalid Input: "+e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }catch(SecurityException e){
            responsejson.put("error ", "Incorrect Password: "+e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }catch(RuntimeException e){
            responsejson.put("error", "Login failed: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict
        }catch(Exception e){
            responsejson.put("error", "Unexpected error: "+e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return responsejson.toString();
    }

    private String withdraw(JSONObject requestBody, HttpServletResponse response) throws IOException {


        JSONObject responsejson = new JSONObject();

        JSONObject userjson = requestBody.getJSONObject("user");

        User user = new User(
            Integer.parseInt(userjson.getString("userid")),
            userjson.getString("name"),
            encrypt(userjson.getString("password"), 1),
            RoleType.valueOf(userjson.getString("roletype").toUpperCase()),
            Integer.parseInt(userjson.getString("accountno")),
            Double.parseDouble(userjson.getString("balance"))
        );

        try{
            validate(user);
        }catch(Exception e){
            responsejson.put("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return responsejson.toString();
        }

        int amount = Integer.parseInt(requestBody.getString("amount"));


        try{
            userservice.withdraw(user, amount);
            responsejson.put("message", "withdraw successfully");
            response.setStatus(HttpServletResponse.SC_OK);
        } catch(IllegalArgumentException e){
            responsejson.put("error ", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch(IllegalStateException e){
            responsejson.put("error ", e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        } catch(RuntimeException e){
            responsejson.put("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        } catch(Exception e){
            responsejson.put("error", "UnExpected Error: "+ e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        // String customerId = requestBody.getString("customerId");
        // double amount = requestBody.getDouble("amount");
        // Check balance, update balance, and log transaction (to be implemented)
        return responsejson.toString();
    }
    
    private String deposit(JSONObject requestBody, HttpServletResponse response) throws IOException {

        JSONObject responsejson = new JSONObject();

        JSONObject userjson = requestBody.getJSONObject("user");

        User user = new User(
            Integer.parseInt(userjson.getString("userid")),
            userjson.getString("name"),
            encrypt(userjson.getString("password"), 1),
            RoleType.valueOf(userjson.getString("roletype")),
            Integer.parseInt(userjson.getString("accountno")),
            Double.parseDouble(userjson.getString("balance"))
        );

        int amount = Integer.parseInt(requestBody.getString("amount"));

        try{
            validate(user);
        }catch(Exception e){
            responsejson.put("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return responsejson.toString();
        }


        try{
            userservice.deposit(user, amount);
            responsejson.put("message", "deposit successfully");
            response.setStatus(HttpServletResponse.SC_OK);
        } catch(IllegalArgumentException e){
            responsejson.put("error ", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch(RuntimeException e){
            responsejson.put("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        } catch(Exception e){
            responsejson.put("error", "UnExpected Error: "+ e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return responsejson.toString();
    }
    
    

    private String moneytransfer(JSONObject requestBody, HttpServletResponse response) throws IOException {
    
        JSONObject responsejson = new JSONObject();

        JSONObject userjson = requestBody.getJSONObject("user");

        User user = new User(
            Integer.parseInt(userjson.getString("userid")),
            userjson.getString("name"),
            encrypt(userjson.getString("password"), 1),
            RoleType.valueOf(userjson.getString("roletype")),
            Integer.parseInt(userjson.getString("accountno")),
            Double.parseDouble(userjson.getString("balance"))
        );


        int receiverid = Integer.parseInt(requestBody.getString("receiverid"));
        int amount = Integer.parseInt(requestBody.getString("amount"));

        try{
            validate(user);
        }catch(Exception e){
            responsejson.put("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return responsejson.toString();
        }

        try{
            userservice.moneytransfer(user, receiverid, amount);
            responsejson.put("message", "money transferred");
            response.setStatus(HttpServletResponse.SC_OK);
        } catch(IllegalArgumentException e){
            responsejson.put("error ", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch(IllegalStateException e){
            responsejson.put("error ", e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        } catch(RuntimeException e){
            responsejson.put("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        } catch(Exception e){
            responsejson.put("error", "UnExpected Error: "+ e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        // Perform transfer logic (to be implemented)
        return responsejson.toString();
    }
    
    private String getactivity(JSONObject requestBody, HttpServletResponse response) throws IOException {

        JSONObject responsejson = new JSONObject();

        JSONObject userjson = requestBody.getJSONObject("user");

        User user = new User(
            Integer.parseInt(userjson.getString("userid")),
            userjson.getString("name"),
            encrypt(userjson.getString("password"), 1),
            RoleType.valueOf(userjson.getString("roletype")),
            Integer.parseInt(userjson.getString("accountno")),
            Double.parseDouble(userjson.getString("balance"))
        );

        try{
            validate(user);
        }catch(Exception e){
            responsejson.put("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return responsejson.toString();
        }

        try{
            List<Activity> activities = userservice.printActivity(user);
            responsejson.put("Activity", activities);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch(IllegalArgumentException e){
            responsejson.put("error ", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch(RuntimeException e){
            responsejson.put("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        } catch(Exception e){
            responsejson.put("error", "UnExpected Error: "+ e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return responsejson.toString();
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

    public static String encrypt(String password, int shift) {

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

    private void validate(User user) {

        User userverify = storage.getUser(user.getUserid());

        if(userverify == null){
            throw new IllegalArgumentException("User Not Found");
        }

        if(! userverify.getEncryptedpassword().equals(user.getEncryptedpassword())){
            throw new SecurityException("Invalid Password");
        }

        if(!(user.getAccountno() == userverify.getAccountno() && 
            user.getBalance() == userverify.getBalance() && 
            user.getRole() == userverify.getRole() && 
            user.getName().equals(userverify.getName()))){
                throw new IllegalArgumentException();
            }
        return;
    }

}

