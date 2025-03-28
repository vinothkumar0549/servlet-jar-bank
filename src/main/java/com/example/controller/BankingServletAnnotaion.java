package com.example.controller;

import java.io.*;
import java.util.*;

import com.example.security.SecurityUtil;

import org.json.*;

import com.example.database.DatabaseStorage;
import com.example.database.Storage;
import com.example.pojo.Activity;
import com.example.pojo.User;
import com.example.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/annotation")
public class BankingServletAnnotaion {

    Storage storage = new DatabaseStorage();

    UserService userservice = new UserService(storage);

    @GET
    @Path("/hello")
    @Produces(MediaType.APPLICATION_JSON)
    public Response hello(){
        return Response.status(Response.Status.OK).entity("Hello World").build();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(User user){

        user.setEncryptedpassword(SecurityUtil.encrypt(user.getEncryptedpassword(), 1));

        try{
            int userid = userservice.register(user);
            return Response.status(Response.Status.CREATED).entity("{ \"userId\": \"" + userid + "\", \"message\": \"Register success\" }").build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"" + e.getMessage() + "\"}").build();
                
        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT).entity("{\"error\": \"" + e.getMessage() + "\"}").build();
                
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        }
        
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(User user) {

        try{
            user.setEncryptedpassword(SecurityUtil.encrypt(user.getEncryptedpassword(), 1));

            User resultuser = userservice.login(user.getUserid(), user.getEncryptedpassword());

            return Response.status(Response.Status.OK).entity(resultuser).build();

        }catch(IllegalArgumentException e){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        }catch(SecurityException e){
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        }catch(RuntimeException e){
            return Response.status(Response.Status.CONFLICT).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        }catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        }

    }

    @POST
    @Path("/withdraw")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response withdraw(String  reqeustBody, @Context HttpServletRequest request) {

        JSONObject jsonObject = new JSONObject(reqeustBody);

        int userid = Integer.parseInt(jsonObject.getString("userid"));
        String password = SecurityUtil.encrypt(jsonObject.getString("password"), 1);

        int amount = Integer.parseInt(jsonObject.getString("amount"));

        User user;

        try{
            user = validate(userid, password);
        }catch(Exception e){
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }

        try{
            userservice.withdraw(user, amount);
            return Response.status(Response.Status.OK).entity("{ message : withdraw Successfully }").build();

        } catch(IllegalArgumentException e){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        } catch(IllegalStateException e){
            return Response.status(Response.Status.CONFLICT).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        } catch(RuntimeException e){
            return Response.status(Response.Status.CONFLICT).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        } catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        }
    }
    
    @POST
    @Path("/deposit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deposit(String requestBody, @Context HttpServletRequest request) {

        JSONObject responsejson = new JSONObject(requestBody);


        int userid = Integer.parseInt(responsejson.getString("userid"));
        String password = SecurityUtil.encrypt(responsejson.getString("password"), 1);

        int amount = Integer.parseInt(responsejson.getString("amount"));

        User user;
        
        try{
            user = validate(userid, password);
        }catch(Exception e){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }


        try{
            userservice.deposit(user, amount);
            return Response.status(Response.Status.OK).entity("{ message : Deposit Successfully }").build();

        } catch(IllegalArgumentException e){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        } catch(RuntimeException e){
            return Response.status(Response.Status.CONFLICT).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        } catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        }

    }
    
    
    @POST
    @Path("/moneytransfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response moneytransfer(String requestBody, @Context HttpServletRequest request) throws IOException {
    
        JSONObject requestjson = new JSONObject(requestBody);

        int userid = Integer.parseInt(requestjson.getString("userid"));
        String password = SecurityUtil.encrypt(requestjson.getString("password"), 1);

        int receiverid = Integer.parseInt(requestjson.getString("receiverid"));
        int amount = Integer.parseInt(requestjson.getString("amount"));

        User user;
        try{
            user = validate(userid, password);
        }catch(Exception e){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        }

        try{
            userservice.moneytransfer(user, receiverid, amount);
            return Response.status(Response.Status.OK).entity("{ message : Money Transfered Successfully }").build();

        } catch(IllegalArgumentException e){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"" + e.getMessage() + "\"}").build();


        } catch(IllegalStateException e){
            return Response.status(Response.Status.CONFLICT).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        } catch(RuntimeException e){
            return Response.status(Response.Status.CONFLICT).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        } catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        }

    }
    
    @POST
    @Path("/getactivity")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getactivity(User user) {

        try{
            user.setEncryptedpassword(SecurityUtil.encrypt(user.getEncryptedpassword(), 1));
            user = validate(user.getUserid(), user.getEncryptedpassword());
        }catch(Exception e){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        }

        try{
            List<Activity> activities = userservice.printActivity(user);
            return Response.status(Response.Status.OK).entity("{ \"Activity\": \"" + activities + "\", \"message\": \"success\" }").build();

        } catch(IllegalArgumentException e){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        } catch(RuntimeException e){
            return Response.status(Response.Status.CONFLICT).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        } catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        }

    }

    @POST
    @Path("/topnucustomer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response topncustomer(String requestBody, @Context HttpServletRequest request) {

        JSONObject requestjson = new JSONObject(requestBody);

        int userid = Integer.parseInt(requestjson.getString("userid"));
        String password = SecurityUtil.encrypt(requestjson.getString("password"),1);
        int n = Integer.parseInt(requestjson.getString("no"));

        User user;
        try{
            user = validate(userid, password);
        }catch(Exception e){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        }

        try{
            List<User> users = userservice.getTopNCustomer(user, n);
            return Response.status(Response.Status.OK).entity("{ \"Users\": \"" + users + "\", \"message\": \"success\" }").build();

        } catch(IllegalArgumentException e){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        } catch(RuntimeException e){
            return Response.status(Response.Status.CONFLICT).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        } catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        }

    }

    // private String logout(JSONObject requestBody, HttpServletResponse response) throws IOException{
    //     int userid = Integer.parseInt(requestBody.getString("userid"));
    //     String password = encrypt(requestBody.getString("password"), 1);
    //     JSONObject responsejson = new JSONObject();
    // }


    // public static String encrypt(String password, int shift) {

    //     StringBuilder builder = new StringBuilder();

    //     for (char c : password.toCharArray()) {

    //         if (Character.isLetterOrDigit(c)) {

    //             char base = Character.isUpperCase(c) ? 'A' : (Character.isLowerCase(c) ? 'a' : '0');
    //             int range = Character.isDigit(c) ? 10 : 26; 
    //             builder.append((char) (base + (c - base + shift) % range));
    //         } else {
    //             builder.append(c);
    //         }
    //     }
    //     return builder.toString();
    // }

    private User validate(int userid, String password) {

        User user = storage.getUser(userid);

        

        SecurityUtil.validation(user, password);
        
        // if(user == null){
        //     throw new IllegalArgumentException("User Not Found");
        // }

        // if(! user.getEncryptedpassword().equals(password)){
        //     throw new SecurityException("Invalid Password");
        // }

        return user;
    }

}

