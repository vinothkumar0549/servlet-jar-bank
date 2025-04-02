package com.example;

import org.glassfish.jersey.server.ResourceConfig;

import org.glassfish.jersey.jackson.JacksonFeature;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/jersey")
public class AppConfig extends ResourceConfig {
    static {
        System.out.println("Static block: Jersey is starting...");
    }

    public AppConfig() {
        System.out.println("Constructor: Jersey is running!");
        packages("com.example.controller");
        register(JacksonFeature.class);
    }
}
