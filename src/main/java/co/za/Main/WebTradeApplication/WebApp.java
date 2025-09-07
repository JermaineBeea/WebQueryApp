package  co.za.Main.WebTradeApplication ;

import java.io.IOException;

public class WebApp {
    public static void main(String[] args) {
        try {
            WebServerApplication app = new WebServerApplication();
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down trade server...");
                app.stop();
            }));
            
            System.out.println("Trade Web Server is running. Access at: http://localhost:8080");
            System.out.println("Press Ctrl+C to stop the server.");
            
        } catch (IOException e) {
            System.err.println("Failed to start trade server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}