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
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SampleApi {

    private static final Set<String> uniqueRequests = ConcurrentHashMap.newKeySet();
    private static final Logger logger = Logger.getLogger(SampleApi.class.getName());

    static {
        try {
            File logDirectory = new File("../log");
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
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");
    }

    static class TrackRequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;
            int statusCode;

            String query = exchange.getRequestURI().getQuery();
            String id = null;

            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");

                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];

                        if (key.equals("id")) {
                            try {
                                Integer.parseInt(value);
                                id = value;
                                uniqueRequests.add(id); 
                            } catch (NumberFormatException e) {
                                response = "failed";
                                statusCode = 400;
                                sendResponse(exchange, statusCode, response);
                                return;
                            }
                        }
                    }
                }
            }

            
            if (id == null) {
                response = "failed"; 
                statusCode = 400;
            } else {
                response = "ok";
                statusCode = 200;
            }

            sendResponse(exchange, statusCode, response);
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String responseMessage) throws IOException {
            exchange.sendResponseHeaders(statusCode, responseMessage.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseMessage.getBytes());
            os.close();
        }
    }
}
