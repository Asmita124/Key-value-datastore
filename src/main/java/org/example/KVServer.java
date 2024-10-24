package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class KVServer {
    private static final int PORT = 8080;
    private static final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                new Thread(() -> handleClient(socket)).start();
                // This ensures that the server can handle multiple clients concurrently.
                // Handle each client in a separate thread as the connection was being closed after one process.
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

            String command;
            // For each command, parse and store or retrieve values from the store
            while ((command = input.readLine()) != null) { // Keep reading until the client disconnects
                String[] parts = command.split(" ");
                if (parts.length > 0) {
                    switch (parts[0]) {
                        case "SET":
                            if (parts.length == 3) {
                                // Parse and store the value
                                store.put(parts[1], parts[2]);
                                output.println("OK");
                            } else {
                                output.println("INVALID_COMMAND");
                            }
                            break;

                        case "GET":
                            if (parts.length == 2) {
                                // Retrieve and send the value
                                String value = store.get(parts[1]);
                                output.println(value != null ? value : "NOT_FOUND");
                            } else {
                                output.println("INVALID_COMMAND");
                            }
                            break;

                        default:
                            output.println("INVALID_COMMAND");
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Client connection error: " + e.getMessage());
        }
    }
}
