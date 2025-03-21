<web-app xmlns="http://jakarta.ee/xml/ns/jakartaee" version="5.0">

    <!-- Jersey Framework for Java REST API -->
    <servlet>
        <servlet-name>jerseyservlet</servlet-name>
        <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
        
        <init-param>
            <param-name>jersey.config.server.provider.packages</param-name>
            <param-value>com.example</param-value>
        </init-param>

        <init-param>
            <param-name>jakarta.ws.rs.Application</param-name>
            <param-value>com.example.MyApplication</param-value>
        </init-param>

        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>jerseyservlet</servlet-name>
        <url-pattern>/jersey/*</url-pattern>
    </servlet-mapping>

    <!-- Normal Servlet Mapping -->
    <servlet>
        <servlet-name>MyServlet</servlet-name>
        <servlet-class>com.example.MyServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>MyServlet</servlet-name>
        <url-pattern>/hello</url-pattern>
    </servlet-mapping>

    <servlet>
        <servlet-name>BankingServlet</servlet-name>
        <servlet-class>com.example.BankingServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>BankingServlet</servlet-name>
        <url-pattern>/banking/*</url-pattern>
    </servlet-mapping>

</web-app>
