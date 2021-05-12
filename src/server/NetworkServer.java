package networkExercise.server;

import common.AddressBookDataSource;
import common.Person;
import networkExercise.NetworkDataSource;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkServer {
    private static final int PORT = 10000;

    /**
     * this is the timeout inbetween accepting clients, not reading from the socket itself.
     */
    private static final int SOCKET_ACCEPT_TIMEOUT = 100;

    /**
     * This timeout is used for the actual clients.
     */
    private static final int SOCKET_READ_TIMEOUT = 5000;

    private AtomicBoolean running = new AtomicBoolean(true);

    /**
     * The connection to the database where everything is stored.
     */
    private AddressBookDataSource database;

    /**
     * Handles the connection received from ServerSocket.
     * Does not return until the client disconnects.
     * @param socket The socket used to communicate with the currently connected client
     */
    private void handleConnection(Socket socket) {
        try {
            /**
             * We create the streams once at connection time, and re-use them until the client disconnects.
             * This **must** be in the opposite order to the client, because creating an ObjectInputStream
             * reads data, and an ObjectOutputStream writes data.
             */
            final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            /**
             * while(true) here might seem a bit confusing - why do we have an infinite loop?
             * That's because we don't want to exit until the client disconnects, and when they do, readObject()
             * will throw an IOException, which will cause this method to exit. Another option could be to have
             * a "close" message/command sent by the client, but if the client closes improperly, or they lose
             * their network connection, it's not going to get sent anyway.
             */
            while (true) {
                try {
                    /**
                     * Read the command, this tells us what to send the client back.
                     * If the client has disconnected, this will throw an exception.
                     */
                    final NetworkDataSource.Command command = (NetworkDataSource.Command) inputStream.readObject();
                    handleCommand(socket, inputStream, outputStream, command);
                } catch (SocketTimeoutException e) {
                    /**
                     * We catch SocketTimeoutExceptions, because that just means the client hasn't sent
                     * any new requests. We don't want to disconnect them otherwise. Another way to
                     * check if they're "still there would be with ping/pong commands.
                     */
                    continue;
                }
            }
        } catch (IOException | ClassCastException | ClassNotFoundException e) {
            System.out.println(String.format("Connection %s closed", socket.toString()));
        }
    }

    /**
     * Handles a request from the client.
     * @param socket socket for the client
     * @param inputStream input stream to read objects from
     * @param outputStream output stream to write objects to
     * @param command command we're handling
     * @throws IOException if the client has disconnected
     * @throws ClassNotFoundException if the client sends an invalid object
     */
    private void handleCommand(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream,
                               NetworkDataSource.Command command) throws IOException, ClassNotFoundException {
        /**
         * Remember this is happening on separate threads for each client. Therefore access to the database
         * must be thread-safe in some way. The easiest way to achieve thread safety is to just put a giant
         * lock around all database operations, in this case with a synchronized block on the database object.
         */
        switch (command) {
            case ADD_PERSON: {
                // client is sending us a new person
                final Person p = (Person) inputStream.readObject();
                synchronized (database) {
                    database.addPerson(p);
                }
                System.out.println(String.format("Added person '%s' to database from client %s",
                        p.getName(), socket.toString()));
            }
            break;

            case GET_PERSON: {
                // client sends us the name of the person to retrieve
                final String personName = (String) inputStream.readObject();
                synchronized (database) {
                    // synchronize both the get as well as the send, that way
                    // we don't send a half updated person
                    final Person person = database.getPerson(personName);

                    // send the client back the person's details, or null
                    outputStream.writeObject(person);

                    if (person != null)
                        System.out.println(String.format("Sent person '%s' to client %s",
                                person.getName(), socket.toString()));
                }
                outputStream.flush();
            }
            break;

            case GET_SIZE: {
                // no parameters sent by client

                // send the client back the size of the database
                synchronized (database) {
                    outputStream.writeInt(database.getSize());
                }
                outputStream.flush();

                System.out.println(String.format("Sent size of %d to client %s",
                        database.getSize(), socket.toString()));
            }
            break;

            case DELETE_PERSON: {
                // one parameter - the person's name
                final String personName = (String) inputStream.readObject();
                synchronized (database) {
                    database.deletePerson(personName);
                }

                System.out.println(String.format("Deleted person '%s' on behalf of client %s",
                        personName, socket.toString()));
            }
            break;

            case GET_NAME_SET: {
                // no parameters sent by client

                // send the client back the name set
                synchronized (database) {
                    outputStream.writeObject(database.nameSet());
                }
                outputStream.flush();

                System.out.println(String.format("Sent name set to client %s",
                        socket.toString()));
            }
            break;
        }
    }

    /**
     * Returns the port the server is configured to use
     *
     * @return The port number
     */
    public static int getPort() {
        return PORT;
    }

    /**
     * Starts the server running on the default port
     */
    public void start() throws IOException {
        // Connect to the database.
        database = new JDBCAddressBookDataSource();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverSocket.setSoTimeout(SOCKET_ACCEPT_TIMEOUT);
            for (;;) {
                if (!running.get()) {
                    // The server is no longer running
                    break;
                }
                try {
                    final Socket socket = serverSocket.accept();
                    socket.setSoTimeout(SOCKET_READ_TIMEOUT);

                    // We have a new connection from a client. Use a runnable and thread to handle
                    // the client. The lambda here wraps the functional interface (Runnable).
                    final Thread clientThread = new Thread(() -> handleConnection(socket));
                    clientThread.start();
                } catch (SocketTimeoutException ignored) {
                    // Do nothing. A timeout is normal- we just want the socket to
                    // occasionally timeout so we can check if the server is still running
                } catch (Exception e) {
                    // We will report other exceptions by printing the stack trace, but we
                    // will not shut down the server. A exception can happen due to a
                    // client malfunction (or malicious client)
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // If we get an error starting up, show an error dialog then exit
            e.printStackTrace();
            System.exit(1);
        }

        // Close down the server
        System.exit(0);
    }

    /**
     * Requests the server to shut down
     */
    public void shutdown() {
        // Shut the server down
        running.set(false);
    }
}
