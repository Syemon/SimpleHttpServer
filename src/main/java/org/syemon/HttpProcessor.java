package org.syemon;

import lombok.AllArgsConstructor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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
            System.out.println("Processing request");
            InputStream inputStream = clientSocket.getInputStream();
            Optional<HttpRequest> httpRequest = requestReader.decode(inputStream);
            System.out.println("Result request:" + httpRequest);

            HttpResponse httpResponse;
            final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            if (httpRequest.isPresent()) {
                HttpPath httpPath = HttpPath.builder()
                        .path(httpRequest.get().getPath())
                        .method(httpRequest.get().getMethod().name())
                        .build();
                RequestRunner requestRunner = routes.get(httpPath);
                httpResponse = requestRunner.run(httpRequest.get());
                System.out.println("Response: " + httpResponse);
                responseWriter.writeResponse(httpResponse, bufferedWriter);
                System.out.println("Response sent");
                inputStream.close();
                System.out.println("Client socket closed");


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
                e.printStackTrace();
            }
        }

    }
}
