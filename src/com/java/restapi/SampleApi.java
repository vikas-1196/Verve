package com.java.restapi;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SampleApi {

    private static final Set<String> uniqueRequests = ConcurrentHashMap.newKeySet();
    private static final Set<String> currentMinuteRequests = ConcurrentHashMap.newKeySet();
    private static final Logger logger = Logger.getLogger(SampleApi.class.getName());

    static {
        try {
            // Ensure the log directory exists
            File logDirectory = new File("log");
            if (!logDirectory.exists()) {
                logDirectory.mkdir();  // Create the directory if it doesn't exist
            }

            // Log to file
            FileHandler fileHandler = new FileHandler("log/unique_requests.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // Set up the HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/trackRequest", new TrackRequestHandler());
        server.setExecutor(Executors.newCachedThreadPool()); // thread pool for concurrent handling
        server.start();
        System.out.println("Server is running on port 8080...");

        // Schedule the logging task every minute
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            logUniqueRequestsCount();
        }, 1, 1, TimeUnit.MINUTES);
    }

    // HTTP Handler for tracking requests
    static class TrackRequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.contains("id=")) {
                String id = query.split("id=")[1];
                uniqueRequests.add(id);
                currentMinuteRequests.add(id);
                String response = "Request tracked for ID: " + id;
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                String response = "ID not found in request";
                exchange.sendResponseHeaders(400, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    // Method to log unique request counts and clear the current minute set
    private static void logUniqueRequestsCount() {
        logger.info("Unique requests in this minute: " + currentMinuteRequests.size());
        currentMinuteRequests.clear(); // Clear for the next minute
    }
}
