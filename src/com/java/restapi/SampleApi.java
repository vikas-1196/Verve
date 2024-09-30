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
            File logDirectory = new File("log");
            if (!logDirectory.exists()) {
                logDirectory.mkdir();  
            }

            FileHandler fileHandler = new FileHandler("log/unique_requests.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/verve/accept", new TrackRequestHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server Stared in port 8080");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            logUniqueRequestsCount();
        }, 1, 1, TimeUnit.MINUTES);
    }

    static class TrackRequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.contains("id=")) {
                String id = query.split("id=")[1];
                uniqueRequests.add(id);
                currentMinuteRequests.add(id);
                String response = "ok";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                String response = "failed";
                exchange.sendResponseHeaders(400, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    private static void logUniqueRequestsCount() {
        logger.info("Unique requests in this minute: " + currentMinuteRequests.size());
        currentMinuteRequests.clear();
    }
}
