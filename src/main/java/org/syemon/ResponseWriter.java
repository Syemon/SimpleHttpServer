package org.syemon;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.util.List;

@Slf4j
public class ResponseWriter {

    private static final String HTTP_1_1 = "HTTP/1.1";

    public void writeResponse(HttpResponse httpResponse, BufferedWriter outputStream) {
        String responseId = httpResponse.getBody().toString();
        log.debug("Response ID: {}", responseId);

        List<String> headers = httpResponse.getHeaders();
        log.debug("{} Headers: {}", responseId, headers);
        try {
            outputStream.write(HTTP_1_1 + " " + httpResponse.getStatusCode() + "\r\n");
            for (String header : headers) {
                outputStream.write(header + "\r\n");
            }
            outputStream.write("\r\n");
            outputStream.write(httpResponse.getBody().toString());
            outputStream.close();
        } catch (Exception e) {
            log.error("{} Error while writing response: {}", responseId, e.getMessage());
        }
    }
}
