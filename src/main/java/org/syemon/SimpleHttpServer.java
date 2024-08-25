package org.syemon;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;

@AllArgsConstructor
public class SimpleHttpServer {
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final HttpProcessor httpProcessor;
    
    public void start() {
        OffsetDateTime serverStartTime = OffsetDateTime.now();

        System.out.println("Server start at: " + serverStartTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        try {
            while (true) {

                Socket clientSocket = serverSocket.accept();
                System.out.println("Server accepted connection" + clientSocket);
                Runnable runner = () -> httpProcessor.process(clientSocket);
                executorService.execute(runner);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
            stop();
            OffsetDateTime serverEndTime = OffsetDateTime.now();
            long durationInMillis = Duration.between(serverStartTime, serverEndTime).toMillis();
            System.out.printf("Server shutdown at: %s. It lasted %d milliseconds", serverEndTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), durationInMillis);
        }

    }

    public SimpleHttpServer addRoute(String path, String method, RequestRunner requestRunner) {
        HttpPath httpPath = HttpPath.builder().path(path).method(method).build();
        httpProcessor.addRoute(httpPath, requestRunner);
        return this;
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error while stopping the server: " + e.getMessage());
        }
    }
}
