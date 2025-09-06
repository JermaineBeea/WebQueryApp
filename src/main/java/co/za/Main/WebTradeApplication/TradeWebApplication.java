package  co.za.Main.WebTradeApplication;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.math.BigDecimal;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TradeWebApplication {
    
    private HttpServer server;
    
    public TradeWebApplication() throws IOException {
        setupServer();
    }
    
    private void setupServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Endpoints
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/data", new DataHandler());
        server.createContext("/api/update", new UpdateHandler());
        server.createContext("/api/query", new QueryHandler());
        
        server.start();
        System.out.println("Trade Web Server running at: http://localhost:8080");
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
    
    // Get current data from database
    class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().close();
                return;
            }
            
            if ("GET".equals(exchange.getRequestMethod())) {
                try (TradeVariableDatabase db = new TradeVariableDatabase()) {
                    String json = buildDataJson(db);
                    sendJsonResponse(exchange, json);
                } catch (Exception e) {
                    sendErrorResponse(exchange, "Error fetching data: " + e.getMessage());
                }
            }
        }
    }
    
    // Update variable values
    class UpdateHandler implements HttpHandler {
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
                    
                    String variable = parseStringValue(body, "variable");
                    String column = parseStringValue(body, "column");
                    BigDecimal value = new BigDecimal(parseStringValue(body, "value"));
                    
                    try (TradeVariableDatabase db = new TradeVariableDatabase()) {
                        db.updateValue(variable, column, value);
                        sendJsonResponse(exchange, "{\"success\":true,\"message\":\"Value updated successfully\"}");
                    }
                    
                } catch (Exception e) {
                    sendErrorResponse(exchange, "Error updating value: " + e.getMessage());
                }
            }
        }
    }
    
    // Run trade calculations
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
                    boolean basedOnExecution = parseBooleanValue(body, "basedOnExecution");
                    
                    try (TradeVariableDatabase db = new TradeVariableDatabase()) {
                        TradeQueryImplementation queryImpl = new TradeQueryImplementation(basedOnExecution);
                        queryImpl.populateTable(db);
                        
                        String json = buildDataJson(db);
                        sendJsonResponse(exchange, "{\"success\":true,\"message\":\"Query executed successfully\",\"data\":" + json + "}");
                    }
                    
                } catch (Exception e) {
                    sendErrorResponse(exchange, "Error running query: " + e.getMessage());
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
    
    private String parseStringValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : "";
    }
    
    private boolean parseBooleanValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(true|false)");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() && "true".equals(matcher.group(1));
    }
    
    private void sendJsonResponse(HttpExchange exchange, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, json.getBytes().length);
        exchange.getResponseBody().write(json.getBytes());
        exchange.getResponseBody().close();
    }
    
    private void sendErrorResponse(HttpExchange exchange, String error) throws IOException {
        String json = "{\"success\":false,\"error\":\"" + error + "\"}";
        exchange.sendResponseHeaders(500, json.getBytes().length);
        exchange.getResponseBody().write(json.getBytes());
        exchange.getResponseBody().close();
    }
    
    private String buildDataJson(TradeVariableDatabase db) throws Exception {
        String[] variables = {"tradeprofit", "profitfactor", "tradeamount", "buyvariable", "sellvariable"};
        StringBuilder json = new StringBuilder("{\"data\":[");
        
        for (int i = 0; i < variables.length; i++) {
            String var = variables[i];
            BigDecimal max = db.getValueFromColumn(var, "maximum");
            BigDecimal min = db.getValueFromColumn(var, "minimum");
            BigDecimal factorMin = db.getValueFromColumn(var, "factormin");
            BigDecimal factorMax = db.getValueFromColumn(var, "factormax");
            BigDecimal returnMin = db.getValueFromColumn(var, "returnmin");
            BigDecimal returnMax = db.getValueFromColumn(var, "returnmax");
            
            json.append(String.format(
                "{\"variable\":\"%s\",\"maximum\":\"%s\",\"minimum\":\"%s\"," +
                "\"factormin\":\"%s\",\"factormax\":\"%s\",\"returnmin\":\"%s\",\"returnmax\":\"%s\"}",
                var, max.toPlainString(), min.toPlainString(),
                factorMin.toPlainString(), factorMax.toPlainString(),
                returnMin.toPlainString(), returnMax.toPlainString()));
            
            if (i < variables.length - 1) json.append(",");
        }
        
        json.append("]}");
        return json.toString();
    }
    
    private String getHtmlContent() {
        try {
            return new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get("trade-index.html")));
        } catch (Exception e) {
            return getFallbackHtml();
        }
    }
    
    private String getFallbackHtml() {
        return """
<!DOCTYPE html>
<html>
<head><title>Trade Application</title></head>
<body>
    <h1>Trade Web Application</h1>
    <p>Please create trade-index.html file for the interface</p>
</body>
</html>
        """;
    }
    
    public void stop() {
        if (server != null) server.stop(0);
    }
}