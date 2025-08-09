package co.za.Main.WebApplication;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class WebServerApp {
    
    private HttpServer server;
    
    public WebServerApp() throws IOException {
        setupServer();
    }
    
    private void setupServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Only 2 endpoints needed
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/query", new QueryHandler());
        
        server.start();
        System.out.println("Server running at: http://localhost:8080");
    }
    
    // Serve HTML page
    class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String html = getHtmlContent();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, html.getBytes().length);
                exchange.getResponseBody().write(html.getBytes());
                exchange.getResponseBody().close();
            }
        }
    }
    
    // Process query calculations
    class QueryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().close();
                return;
            }
            
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String body = readBody(exchange);
                    
                    // Parse values
                    Long a = parseValue(body, "a");
                    Long b = parseValue(body, "b"); 
                    Long c = parseValue(body, "c");
                    
                    // Calculate using QueryFunction
                    QueryFunction qf = new QueryFunction(a, b, c);
                    
                    // Send results
                    String response = String.format(
                        "{\"success\":true,\"results\":{\"a\":%d,\"b\":%d,\"c\":%d}}",
                        qf.returnA(), qf.returnB(), qf.returnC()
                    );
                    
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    exchange.getResponseBody().write(response.getBytes());
                    exchange.getResponseBody().close();
                    
                } catch (Exception e) {
                    String error = "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
                    exchange.sendResponseHeaders(500, error.getBytes().length);
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.getResponseBody().close();
                }
            }
        }
    }
    
    // Helper methods
    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json");
    }
    
    private String readBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            return reader.lines().reduce("", (a, b) -> a + b);
        }
    }
    
    private Long parseValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+)");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? Long.parseLong(matcher.group(1)) : 0L;
    }
    
    private String getHtmlContent() {
        try {
            String content = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get("index.html")));
            System.out.println("Loaded HTML file successfully, length: " + content.length());
            return content;
        } catch (Exception e) {
            System.out.println("Could not load index.html: " + e.getMessage());
            return "<!DOCTYPE html><html><head><title>Test</title></head><body><h1>Server is working!</h1><p>But index.html file not found</p></body></html>";
        }
    }
    
    public void stop() {
        if (server != null) server.stop(0);
    }
}