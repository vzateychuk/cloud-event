package ru.vez.cloudevent.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class JettyServer {
    public static void main(String[] args) throws Exception {
        // Create a basic Jetty server object that will listen on port 8080.
        Server server = new Server(8080);

        // Create a ServletContextHandler and add the servlet.
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add the CloudEventsHttpServlet servlet to the context.
        context.addServlet(new ServletHolder(CloudEventsHttpServlet.class), "/receiver");

        // Start the server.
        server.start();
        server.join();
    }
}