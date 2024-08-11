package org.syemon;

import com.sun.net.httpserver.HttpServer;

public class SimpleHttpServer {
    private final HttpServer httpServer;

    public SimpleHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    public void start() {
        long startTime =  System.currentTimeMillis();
        System.out.println("Server start at: " + startTime);
        httpServer.start();
        long endTime = System.currentTimeMillis();
        System.out.println("Server executed at: " + System.currentTimeMillis() + ". It took " + (endTime - startTime) + " miliseconds");

    }

    public void stop() {
        httpServer.stop(0);
    }
}
