package org.syemon;

import com.sun.net.httpserver.HttpServer;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleHttpServer {
    private final HttpServer httpServer;

    public SimpleHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    public void start() {
        OffsetDateTime serverStartTime = OffsetDateTime.now();

        System.out.println("Server start at: " + serverStartTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));        //print serverStartTime variable to console in iso format (with miliseconds)

        httpServer.start();
        OffsetDateTime serverEndTime = OffsetDateTime.now();
        long durationInMillis = Duration.between(serverStartTime, serverEndTime).toMillis();
        System.out.printf("Server executed at: %s. It took %d milliseconds", serverEndTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), durationInMillis);

    }

    public void stop() {
        httpServer.stop(0);
    }
}
