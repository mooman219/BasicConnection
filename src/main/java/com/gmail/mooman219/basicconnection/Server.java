package com.gmail.mooman219.basicconnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class Server {

    private final int port;

    private ServerSocket serverSocket;
    private ThreadAccept acceptThread;

    /**
     * Create a new Server instance.
     *
     * @param port the port the server will be running on.
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * Starts the server. This will create a new thread to accept connections
     * on.
     *
     * @return true if started without errors.
     */
    public boolean start() {
        try {
            serverSocket = new ServerSocket();
            serverSocket.setPerformancePreferences(0, 2, 1);
            serverSocket.bind(new InetSocketAddress(port), 64);
            acceptThread = new ThreadAccept(serverSocket);
            acceptThread.start();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Gets the port the server is running on.
     *
     * @return the active port.
     */
    public int getPort() {
        return port;
    }
}
