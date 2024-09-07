package org.syemon;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;

@Slf4j
@AllArgsConstructor
public class SimpleHttpServer {
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final HttpProcessor httpProcessor;
    
    public void start() {
        OffsetDateTime serverStartTime = OffsetDateTime.now();

        log.info("Server start at: {}", serverStartTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                log.info("Server accepted connection {}", clientSocket);
                Runnable runner = () -> httpProcessor.process(clientSocket);
                executorService.execute(runner);
            }
        } catch (Exception e) {
            log.error("Error while running the server: {}", e.getMessage(), e);
        } finally {
            executorService.shutdown();
            stop();
            OffsetDateTime serverEndTime = OffsetDateTime.now();
            long durationInMillis = Duration.between(serverStartTime, serverEndTime).toMillis();
            log.info("Server shutdown at: {}. It lasted {} milliseconds", serverEndTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), durationInMillis);
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
            log.error("Error while stopping the server", e);
        }
    }
}
