package org.syemon;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class SimpleHttpServerTest {

    public static final int CONNECT_TIMEOUT = 5000;
    public static final int READ_TIMEOUT = 5000;
    private final HttpServerFactory httpServerFactory = new HttpServerFactory();

    private SimpleHttpServer simpleHttpServer;

    HttpClient client;
    HttpRequest request;

    @BeforeEach
    public void setUp() throws IOException {
        ConcurrentCollectionHttpHandler.CONCURRENT_MAP.clear();


        HttpServerConfig httpServerConfig = new HttpServerConfig()
                .setHost("localhost")
                .setPort(8080)
                .setThreadPoolSize(10);
        simpleHttpServer = new SimpleHttpServer(httpServerFactory.create(httpServerConfig));
        simpleHttpServer.start();
        client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/multi");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .timeout(Duration.of(5, ChronoUnit.SECONDS))
                .build();
    }

    @AfterEach
    public void tearDown() {
        simpleHttpServer.stop();
        client.close();
    }

    @RepeatedTest(10)
    public void testParallelRequests() throws InterruptedException {
        assertThat(ConcurrentCollectionHttpHandler.CONCURRENT_MAP).isEmpty();

        final int numberOfRequests = 10000;
        final int threadPoolNumber = 20;

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolNumber);

        try {
            for (int i = 0; i < numberOfRequests; i++) {
                executor.submit(() -> {
                    try {
                        HttpResponse<String> response = client.send(
                                request, HttpResponse.BodyHandlers.ofString());

                        assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
                    } catch (Exception e) {
                        log.error("Request failed", e);
                        assert false : "Request failed";
                    }
                });
                log.info("Finished request: {} out of {}  requests.", i + 1, numberOfRequests);
            }

            executor.shutdown();
            boolean finished = executor.awaitTermination(5, TimeUnit.MINUTES);
            assert finished : "Not all requests finished in time";
        } finally {
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        }

        assertThat(ConcurrentCollectionHttpHandler.CONCURRENT_MAP.size()).isEqualTo(numberOfRequests);
    }
}