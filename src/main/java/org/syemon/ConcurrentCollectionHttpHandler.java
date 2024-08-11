package org.syemon;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentCollectionHttpHandler implements HttpHandler {

    public static final Map<String, String> CONCURRENT_MAP = new ConcurrentHashMap<>();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
//        try(InputStream inputStream = exchange.getRequestBody()) {
//            inputStream.
//        }
        CONCURRENT_MAP.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        String plainTextResponse = CONCURRENT_MAP.toString();
        exchange.sendResponseHeaders(200, plainTextResponse.length());

        try(OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(plainTextResponse.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }

    }
}
