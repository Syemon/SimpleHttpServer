package org.syemon;

import java.io.IOException;

public class App {

    public static void main( String[] args ) throws IOException {

        HttpServerConfig httpServerConfig = HttpServerConfig.builder()
                .host("localhost")
                .port(8080)
                .threadPoolSize(10)
                .build();
        HttpServerFactory httpServerFactory = new HttpServerFactory();

        SimpleHttpServer simpleHttpServer = httpServerFactory.create(httpServerConfig);
        simpleHttpServer.start();
    }
}
