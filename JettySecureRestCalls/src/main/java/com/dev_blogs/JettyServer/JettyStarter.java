package com.dev_blogs.JettyServer;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URL;
import java.security.ProtectionDomain;

public class JettyStarter {
    public static void main(String[] args) {
        Server server = new Server();

        // HTTP connector
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);

        // HTTPS configuration
        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());

        // Configuring SSL
        SslContextFactory sslContextFactory = new SslContextFactory();

        // Defining keystore path and passwords
        sslContextFactory.setKeyStorePath("/home/zheka/practice/ssl/2/keystore");
        sslContextFactory.setKeyStorePassword("gromit");
        sslContextFactory.setKeyManagerPassword("gromit");

        // Configuring the connector
        ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
        sslConnector.setPort(8443);

        // Setting HTTP and HTTPS connectors
        server.setConnectors(new Connector[]{connector, sslConnector});
        // --

        // add handler
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{"index.html"});
        resource_handler.setResourceBase(".");

        ProtectionDomain domain = JettyStarter.class.getProtectionDomain();
        URL location = domain.getCodeSource().getLocation();

        // add context
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar(location.toExternalForm());

        HandlerList handlers = new HandlerList();
        // first element  is webSocket handler
        // second element is first handler,
        // third element is webContext
        handlers.setHandlers(new Handler[]{resource_handler, webapp});

        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

}