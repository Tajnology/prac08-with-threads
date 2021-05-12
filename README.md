CAB302 Software Development
===========================

# Supplementary Demo - Combining Databases, Sockets and Threads

This demo program is an extension of Practial 8, using a database for persistent storage, and sockets for client/server communication. The goal is to demonstrate how to transform an interface, such as the address book, into a network protocol.

Each client has its own persistent connection, rather than opening and closing a connection every time they wish to make a request. Because we are using blocking sockets, a single thread would only allow a single client to connect at any point in time. To work around this, we create a thread for each client which connects, which loops until the client disconnects.

Another option would be to use nonblocking sockets and a multiplexer such as Java's NIO Selector. But this requires more asynchronous-like programming, which adds a non-trivial amount of complexity.

By default, it is configured to use SQLite, so you may need to add this as a library if it is not already registered in IntelliJ. Refer to practical 7 and 8 for how to do so.
