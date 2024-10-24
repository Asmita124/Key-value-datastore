# Simple Distributed Key-Value Store

## Overview

This project implements the simplest distributed key-value store using POSIX.
The implementation includes a server that handles `SET` and `GET` requests from clients.
A blend concepts from sources such as Fulmański's tutorial on key-value stores,
insights from Olric and ScyllaDB, and key discussions about implementing such a system have been used and referenced.

## How to Run the Key-Value Store

1. Prerequisites:

   Ensure you have Java 11 or higher installed.
   Install Gradle for building the project or check existing version.
   > gradle --version
   
2. Build the Project
   > ./gradlew build
3. Run the Server
   > ./gradlew run

   You should see output similar to:
   > Server is listening on port 8080

4. Run the Client
   In another terminal, run the client in debug mode to interact with the server:
    > java -cp build/classes/java/main org.example.KVClient
   
   The client will prompt you to enter commands. You can use the following:
SET key value to store a key-value pair. 
GET key to retrieve the value for a given key. 
EXIT to exit the client.

   Example:
   ```shell
   Enter command (SET key value / GET key / EXIT): SET key1 value1
   Server: OK 
   Enter command (SET key value / GET key / EXIT): GET key1
   Server: value1
   ```


5. Stop the Server
   To stop the server, simply use Ctrl+C in the terminal where it's running.



## Decisions & Design Choices:

1. Use of POSIX Sockets:

Why Sockets? - Sockets are one of the most fundamental POSIX features for inter-process communication across networks 
(or between different machines). They allow for TCP communication, which makes this key-value store distributed. 
I have opted for this over other IPC methods (like pipes or shared memory) because the problem specifically involves a distributed system, and sockets are ideal for network communication.
Shortcuts Taken: This is a TCP-based system, which means it's not implementing more complex distributed protocols. We're also limiting communication to simple SET and GET commands to keep the system lightweight.

2. In-memory Data Storage:

The store will be in-memory, using a ConcurrentHashMap. This offers thread-safe access to the data and speeds up operations.
This avoids complexities related to disk I/O and simplifies implementation. 
The store is a simple array of KeyValuePair to hold keys and values.
Shortcuts Taken: No persistence is offered, meaning the key-value store will lose data if the server crashes. A more robust implementation could write data to disk or use a database.

3. Handling Commands (GET/SET):

The server parses incoming commands (SET key value and GET key) and either stores a key-value pair or retrieves it. This design is sufficient for a basic key-value store.
Shortcuts Taken: There's no error handling for malformed commands beyond a basic check, and no support for complex operations like deleting keys or updating them in specific ways.

4. Concurrency:

Each client connection is handled by a separate thread, allowing multiple clients to interact with the server concurrently.
The use of a ConcurrentHashMap ensures that key-value operations are thread-safe, even if multiple clients perform GET or SET operations simultaneously.

5. No Replication or Fault Tolerance:

Distributed systems often include replication for fault tolerance. However, since the assignment asks for a simple implementation with shortcuts, I didn't implement replication. This version assumes that if a server fails, the data is lost.
Shortcuts Taken: No replication or leader election protocol is used. Fault tolerance is not considered, which could be a critical feature in real-world distributed systems.

## Explanation of Code:

1. KVClient.java (Client Side)

   The client connects to the server running on 127.0.0.1:8080.
   It continuously prompts the user for a command (either SET key value to store a key-value pair or GET key to retrieve the value for a key).
   When the user inputs a command, the client sends it to the server over the network.
   It then reads and displays the server's response.
   If the user types EXIT, the client terminates the loop and closes the connection.


2. KVServer.java (Server Side)

   The server listens on port 8080 for incoming client connections.
   When a client connects, the server accepts the connection and creates a socket to communicate with the client.
   The server reads the command from the client, splits the command into parts, and based on the first part (either SET or GET), it processes the request:
   SET Command: It stores the key-value pair in a thread-safe ConcurrentHashMap.
   GET Command: It retrieves the value for the given key from the map.
   If the command is invalid or incomplete, the server responds with INVALID_COMMAND.
   If the key is not found in the GET command, the server responds with NOT_FOUND.
   After responding to the client, the server closes the client connection but continues listening for other clients.


   Flow Summary:

   Client sends a command (e.g., SET key1 value1) to the server.
   Server receives the command, processes it, and stores or retrieves data accordingly.
   Server sends a response (OK for SET, the value for GET, or error messages) back to the client.
   Client prints the response to the console and continues to accept more commands from the user.
   This structure allows for a simple distributed key-value store where multiple clients can connect to the server and interact with the shared data.

While the server is single-threaded for the actual GET and SET operations, the use of ConcurrentHashMap ensures thread-safe access to the key-value store. Each client connection is handled by a separate thread to allow concurrent clients.

## Trade-offs and Shortcuts

1. No Persistence:
By opting for an in-memory store, the system is fast but non-durable. Once the server shuts down, all data is lost. For a production system, you would want to implement a more durable solution (e.g., writing data to disk or database).
2. No Advanced Error Handling:
The code includes basic error handling but does not cover more complex failure scenarios (e.g., network interruptions, data corruption). This is acceptable given the time constraints and the assignment's focus on rapid development.
3. Concurrency and Scaling:
The current system is single-threaded per client, which works for small-scale applications. However, for a system with many clients, this could lead to resource exhaustion. A more scalable solution could use non-blocking I/O (NIO) or an event-driven architecture, but this would require more time.
4. Consistent Hashing:
While consistent hashing is mentioned in the design, the current implementation does not include it. This is a conscious decision to keep the implementation simple for now. We could add consistent hashing logic as the next step to allow distributed data storage across multiple nodes.

## Summary

   Simplicity: The design focuses on rapid implementation. By using TCP sockets for communication, ConcurrentHashMap for storage, and threading for concurrency, the system is kept minimal yet functional.
   Shortcuts: No persistence, no fault tolerance, and basic error handling. These are intentional to meet the time constraints while delivering a working prototype.

## Future Improvements:

   Persistence mechanisms (file-based storage).
   Proper distribution and replication of data across multiple nodes.
   Fault tolerance and more robust concurrency handling.

## Using LLM (Optional Section)

   If LLMs (like GitHub Copilot or ChatGPT) were used for generating parts of the code, here is how you could integrate that into production:
    Code suggestions: LLMs could generate boilerplate code and provide ideas for implementation patterns.
    Testing and Debugging: LLMs could assist with generating test cases or debugging information by suggesting likely issues.
    Documentation: LLMs could help in automating documentation and reasoning, especially for complex decisions.

## References 

For further inspiration, you can check out how Olric handles distributed caching and messaging in their repository
GitHub - buraksezer/olric: Distributed cache and in-memory key/value data store. It can be used both as an embedded Go library and as a language-independent service.
Fulmański's tutorial provides insights into key-value store mechanisms, which we've leveraged to build this simple structure.
ScyllaDB's blog on distributed systems offers valuable insights into the complexities of building distributed databases.
The discussions on Hacker News and Reddit about implementing key-value stores have been insightful in understanding the challenges and trade-offs involved.


## Issues Faced and Debugging

During my development i faced some issues like:

Null pointer exception - Socket connection was not established properly.
Thread safety - ConcurrentHashMap was not thread safe.
Data loss - Data was lost when server was stopped.

These issues were resolved by proper synchronization and handling of exceptions.

When debugging the code, I used print statements and logging to track the flow of execution and identify any issues. I also used a debugger to step through the code and inspect variables at different points in the program.
I found the issue, when SET command returned server ok, but the GET command returned server null. After executing one process the server started returning null.
This was fixed by - 
Threading: Each client connection is handled in a new thread using new Thread(() -> handleClient(socket)).start();. This will ensure that the server can handle multiple clients concurrently.
Persistent Connection: The while ((command = input.readLine()) != null) ensures that the server keeps processing commands from the client until the connection is explicitly closed by the client.

Another issue i found was during test cases -  java.net.ConnectException where i has to check the port number and the server was not running.
I used the @BeforeAll and @AfterAll annotations from JUnit to start and stop the server in your test class.