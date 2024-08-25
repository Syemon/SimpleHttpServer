package org.syemon;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class SimpleHttpServerTest {

    public static final String HOST = "localhost";
    public static final int PORT = 8080;
    public static final int THREAD_POOL_SIZE = 10;
    private static final String URL = "http://%s:%s/multi".formatted(HOST, PORT);

    private final HttpServerFactory httpServerFactory = new HttpServerFactory();

    private SimpleHttpServer simpleHttpServer;

    HttpClient client;
    HttpRequest request;

    @BeforeEach
    public void setUp() throws IOException {


//        ConcurrentCollectionHttpHandler.CONCURRENT_MAP.clear();

        HttpServerConfig httpServerConfig = new HttpServerConfig()
                .setHost(HOST)
                .setPort(PORT)
                .setThreadPoolSize(THREAD_POOL_SIZE);
        simpleHttpServer = httpServerFactory.create(httpServerConfig);
        //invoke simpleHttpServer.start() in a separate thread, but not in executor
        new Thread(simpleHttpServer::start).start();

        client = HttpClient.newHttpClient();
        URI uri = URI.create(URL);
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
//        assertThat(ConcurrentCollectionHttpHandler.CONCURRENT_MAP).isEmpty();

        final int numberOfRequests = 1000;
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

//        assertThat(ConcurrentCollectionHttpHandler.CONCURRENT_MAP.size()).isEqualTo(numberOfRequests);
    }
}