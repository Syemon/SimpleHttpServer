package org.syemon;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@AllArgsConstructor
public class HttpProcessor {

    private final RequestReader requestReader;
    private final ResponseWriter responseWriter;

    private final ConcurrentHashMap<HttpPath, RequestRunner> routes;

    public void addRoute(HttpPath path, RequestRunner requestRunner) {
        routes.put(path, requestRunner);
    }

    public void process(Socket clientSocket) {
        try {
            log.debug("Processing request");
            InputStream inputStream = clientSocket.getInputStream();
            Optional<HttpRequest> httpRequest = requestReader.decode(inputStream);
            log.debug("Result request: {}", httpRequest);

            HttpResponse httpResponse;
            final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            if (httpRequest.isPresent()) {
                HttpPath httpPath = HttpPath.builder()
                        .path(httpRequest.get().getPath())
                        .method(httpRequest.get().getMethod().name())
                        .build();
                RequestRunner requestRunner = routes.get(httpPath);
                httpResponse = requestRunner.run(httpRequest.get());
                log.debug("Response: {}", httpResponse);
                responseWriter.writeResponse(httpResponse, bufferedWriter);
                log.debug("Response sent");
                inputStream.close();
                log.info("Client socket closed");
            } else {
                httpResponse = HttpResponse.builder().statusCode(400).body("Bad request").build();
                responseWriter.writeResponse(httpResponse, bufferedWriter);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log.error("Error while closing client socket: {}", e.getMessage(), e);
            }
        }

    }
}
