package org.syemon;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HttpServerFactory {

    public static final int BACKLOG = 0;

    public HttpServer create(HttpServerConfig httpServerConfig) throws IOException {
        Optional.ofNullable(httpServerConfig.getPort())
                .orElseThrow(() -> new IllegalArgumentException("Port is required"));
        Optional.ofNullable(httpServerConfig.getHost())
                .orElseThrow(() -> new IllegalArgumentException("Host is required"));
        Optional.ofNullable(httpServerConfig.getThreadPoolSize())
                .orElseThrow(() -> new IllegalArgumentException("ThreadPoolSize is required"));

        InetSocketAddress inetSocketAddress = new InetSocketAddress(
                httpServerConfig.getHost(),
                httpServerConfig.getPort());

        HttpServer httpServer = HttpServer.create(inetSocketAddress, BACKLOG);

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        httpServer.setExecutor(threadPoolExecutor);

        httpServer.createContext("/multi", new ConcurrentCollectionHttpHandler());
        return httpServer;
    }
}
