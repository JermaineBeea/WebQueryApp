package co.za.Main;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
        try {
            WebServerApp app = new WebServerApp();
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                app.stop();
            }));
            
            System.out.println("Server is running. Access the application at: http://localhost:8080");
            System.out.println("Press Ctrl+C to stop the server.");
            
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}