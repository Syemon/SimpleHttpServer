package org.syemon;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class App
{
    /*
### Requirements for Implementing a Simple Multithreaded HTTP Server

1. **Server Initialization**
   - Initialize a server socket to listen on a specific port.
   - Configure the server to accept connections in an infinite loop.

2. **Thread Pool Management**
   - Utilize `ExecutorService` to create a fixed thread pool for managing client connections.
   - Ensure the thread pool size is sufficient to handle the expected concurrency level.

3. **Handling Client Connections**
   - For each incoming client connection, the server should accept the connection and hand it off to a worker thread from the thread pool.
   - Each worker thread is responsible for reading the client's request, processing it, and sending back a response.

4. **HTTP Request Processing**
   - Parse the HTTP request to understand the requested resource or action.
   - Support basic HTTP methods like GET (additional methods like POST can be optional).

5. **HTTP Response Generation**
   - Generate appropriate HTTP responses based on the request.
   - Include at least the status line, content-type header, and body in the response.

6. **Concurrency Handling**
   - Ensure thread safety in accessing shared resources, if any (e.g., shared data structures or files).
   - Handle exceptions gracefully to prevent a single client from affecting the server's stability.

7. **Resource Management**
   - Properly close client sockets and free resources after each request is processed.
   - Ensure the server can shut down gracefully, terminating existing connections and releasing all resources.

8. **Logging and Monitoring (Optional)**
   - Implement logging to track requests, responses, and any errors or exceptions.
   - Monitor thread pool usage and performance metrics for tuning and debugging purposes.

9. **Testing**
   - Write unit and integration tests to verify the server's functionality and concurrency handling.
   - Test the server with multiple simultaneous clients to ensure it behaves as expected under load.
     */
    public static void main( String[] args ) throws IOException {

        HttpServerConfig httpServerConfig = new HttpServerConfig()
                .setHost("localhost")
                .setPort(8080)
                .setThreadPoolSize(10);
        HttpServerFactory httpServerFactory = new HttpServerFactory();

        SimpleHttpServer simpleHttpServer = httpServerFactory.create(httpServerConfig);
        simpleHttpServer.start();
    }
}
