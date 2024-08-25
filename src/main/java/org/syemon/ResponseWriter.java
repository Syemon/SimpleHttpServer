package org.syemon;

import java.io.BufferedWriter;
import java.util.List;

public class ResponseWriter {

    public void writeResponse(HttpResponse httpResponse, BufferedWriter outputStream) {
        String responseId = httpResponse.getBody().toString();
        System.out.println("Response ID: " + responseId);

        List<String> headers = httpResponse.getHeaders();
        System.out.println(responseId + " Headers: " + headers);
        try {
            outputStream.write("HTTP/1.1 " + httpResponse.getStatusCode() + "\r\n");
            for (String header : headers) {
                outputStream.write(header + "\r\n");
            }
            outputStream.write("\r\n");
            outputStream.write(httpResponse.getBody().toString());
            outputStream.close();
        } catch (Exception e) {
            System.out.println(responseId + " Error while writing response: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
