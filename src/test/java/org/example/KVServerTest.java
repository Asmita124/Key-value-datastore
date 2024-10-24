package org.example;

import org.junit.jupiter.api.*;

import java.io.*;
import java.net.*;

public class KVServerTest {
    private static Thread serverThread;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    @BeforeAll
    public static void startServer() {
        serverThread = new Thread(() -> {
            try {
                KVServer.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        // Wait for the server to start
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void stopServer() {
        serverThread.interrupt();
    }

    @BeforeEach
    public void setUp() throws IOException {
        socket = new Socket("127.0.0.1", 8080);
        output = new PrintWriter(socket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (input != null) input.close();
        if (output != null) output.close();
        if (socket != null) socket.close();
    }

    @Test
    public void testSetAndGet() throws IOException {
        output.println("SET key1 value1");
        Assertions.assertEquals("OK", input.readLine());

        output.println("GET key1");
        Assertions.assertEquals("value1", input.readLine());
    }

    @Test
    public void testGetNonExistentKey() throws IOException {
        output.println("GET key2");
        Assertions.assertEquals("NOT_FOUND", input.readLine());
    }

    @Test
    public void testInvalidCommand() throws IOException {
        output.println("INVALID");
        Assertions.assertEquals("INVALID_COMMAND", input.readLine());
    }
}