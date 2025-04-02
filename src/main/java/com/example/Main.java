package com.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.eclipse.jetty.servlet.DefaultServlet;

public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // Serve static files (React build) from /public
        ServletHolder staticHolder = new ServletHolder("static", DefaultServlet.class);
        staticHolder.setInitParameter("resourceBase", Main.class.getClassLoader().getResource("public").toExternalForm());
        staticHolder.setInitParameter("dirAllowed", "true");
        context.addServlet(staticHolder, "/*");

        // Register Jersey's AppConfig manually
        ResourceConfig config = new AppConfig();
        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(config));

        // Set Jersey API URL pattern
        context.addServlet(jerseyServlet, "/ServletApp/jersey/*");

        server.setHandler(context);
        server.start();
        System.out.println("Server started at http://localhost:8080/");
        server.join();
    }
}

//public class Main {
    //     public static void main(String[] args) throws Exception {
    //         Server server = new Server(8080);
    //         ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    //         context.setContextPath("/ServletApp");
    
    //         // Register Jersey's AppConfig manually
    //         ResourceConfig config = new AppConfig();
    //         ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(config));
    
    //         // Set Jersey URL pattern
    //         context.addServlet(jerseyServlet, "/jersey/*");
    
    
    //         server.setHandler(context);
    //         server.start();
    //         System.out.println("Server started at http://localhost:8080/");
    //         server.join();
    //     }
    // }
    
