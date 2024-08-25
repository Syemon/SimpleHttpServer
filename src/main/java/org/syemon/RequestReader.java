package org.syemon;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class RequestReader {

    public Optional<HttpRequest> decode(final InputStream inputStream) {
        List<String> lines = inputStreamToRequestLines(inputStream);
        return buildRequest(lines);
    }

    private static List<String> inputStreamToRequestLines(InputStream inputStream) {
        try {
            int availableBytes = inputStream.available();
            while (availableBytes <= 0) {//TODO: add some timeout?
                availableBytes = inputStream.available();
            }

            final char[] inBuffer = new char[inputStream.available()];
            final InputStreamReader inReader = new InputStreamReader(inputStream);
            final int read = inReader.read(inBuffer);

            List<String> message = new ArrayList<>();

            try (Scanner sc = new Scanner(new String(inBuffer))) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    message.add(line);
                }
            }

            return message;
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    private static Optional<HttpRequest> buildRequest(List<String> message) {
        if (message.isEmpty()) {
            return Optional.empty();
        }

        String firstLine = message.get(0);
        String[] httpInfo = firstLine.split(" ");

        if (httpInfo.length != 3) {
            return Optional.empty();
        }

        String protocolVersion = httpInfo[2];
        if (!protocolVersion.equals("HTTP/1.1")) {
            return Optional.empty();
        }

        try {
            return Optional.of(
                        HttpRequest.builder()
                            .method(HttpMethod.valueOf(httpInfo[0]))
                            .path(new URI(httpInfo[1]).getPath())
                            .build());
        } catch (URISyntaxException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
