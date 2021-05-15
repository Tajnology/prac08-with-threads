package networkExercise;

import networkExercise.common.AddressBookDataSource;
import networkExercise.common.Person;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class NetworkDataSource implements AddressBookDataSource {
    private static final String HOSTNAME = "127.0.0.1";
    private static final int PORT = 10000;

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    /**
     * These are the commands which will be sent across the network connection.
     */
    public enum Command {
        ADD_PERSON,
        GET_PERSON,
        DELETE_PERSON,
        GET_SIZE,
        GET_NAME_SET
    }

    public NetworkDataSource() {
        try {
            // Persist a single connection through the whole lifetime of the application.
            // We will re-use this same connection/socket, rather than repeatedly opening
            // and closing connections.
            socket = new Socket(HOSTNAME, PORT);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            // If the networkExercise.server connection fails, we're going to throw exceptions
            // whenever the application actually tries to query anything.
            // But it wasn't written to handle this, so make sure your
            // networkExercise.server is running beforehand!
            System.out.println("Failed to connect to networkExercise.server");
        }
    }

    @Override
    public void addPerson(Person p) {
        if (p == null)
            throw new IllegalArgumentException("Person cannot be null");

        try {
            // tell the networkExercise.server to expect a person's details
            outputStream.writeObject(Command.ADD_PERSON);

            // send the actual data
            outputStream.writeObject(p);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Person getPerson(String name) {
        try {
            // tell the networkExercise.server to expect a person's name, and send us back their details
            outputStream.writeObject(Command.GET_PERSON);
            outputStream.writeObject(name);

            // flush because if we don't, the request might not get sent yet, and we're waiting for a response
            outputStream.flush();

            // read the person's details back from the networkExercise.server
            return (Person)inputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getSize() {
        /**
         * Protocol documentation might look like:
         * GET_SIZE:
         *  - No parameters
         *
         * Server responds with:
         *   - (int) number of entries
         */
        try {
            outputStream.writeObject(Command.GET_SIZE);
            outputStream.flush();

            // read the person's details back from the networkExercise.server
            return inputStream.readInt();
        } catch (IOException | ClassCastException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void deletePerson(String name) {
        /**
         * Protocol documentation might look like:
         * DELETE_PERSON:
         *  - (String) the person to remove
         *
         * Server does not respond.
         */

        try {
            outputStream.writeObject(Command.DELETE_PERSON);
            outputStream.writeObject(name);
            outputStream.flush();
        } catch (IOException | ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
    }

    @Override
    public Set<String> nameSet() {
        try {
            outputStream.writeObject(Command.GET_NAME_SET);
            outputStream.flush();
            return (Set<String>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }
}
