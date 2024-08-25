package org.syemon;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class HttpServerFactory {

    public static final int BACKLOG = 0;

    public SimpleHttpServer create(HttpServerConfig httpServerConfig) throws IOException {
        Optional.ofNullable(httpServerConfig.getPort())
                .orElseThrow(() -> new IllegalArgumentException("Port is required"));
        Optional.ofNullable(httpServerConfig.getHost())
                .orElseThrow(() -> new IllegalArgumentException("Host is required"));
        Optional.ofNullable(httpServerConfig.getThreadPoolSize())
                .orElseThrow(() -> new IllegalArgumentException("ThreadPoolSize is required"));

        ServerSocket serverSocket = new ServerSocket(httpServerConfig.getPort());
        final RequestReader requestReader = new RequestReader();
        final ResponseWriter responseWriter = new ResponseWriter();
        SimpleHttpServer simpleHttpServer = new SimpleHttpServer(serverSocket, Executors.newFixedThreadPool(httpServerConfig.getThreadPoolSize()), new HttpProcessor(requestReader, responseWriter, new ConcurrentHashMap<>()));

        simpleHttpServer.addRoute(
                "/multi",
                "GET",
                request ->
                        HttpResponse.builder()
                                .statusCode(200)
                                .headers(List.of("Content-Type: text/plain"))
                                .body(UUID.randomUUID().toString()).build());

        return simpleHttpServer;
    }
}
