package org.example;

import java.io.*;
import java.net.*;

public class KVClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            System.out.println("Connected to the server at " + SERVER_ADDRESS + ":" + SERVER_PORT);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            String command;

            while (true) {
                System.out.print("Enter command (SET key value / GET key / EXIT): ");
                command = consoleInput.readLine();

                if (command.equalsIgnoreCase("EXIT")) {
                    break;  // Exit the loop if the user types EXIT
                }
                if (command == null || command.trim().isEmpty()) {
                    System.out.println("Command cannot be empty. Please enter a valid command.");
                    continue;  // Skip the loop iteration if the command is empty
                }
                output.println(command);  // Send the command to the server
                String response = input.readLine();  // Get the response from the server
                System.out.println("Server: " + response);  // Print server response
            }

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}