package com.gmail.mooman219.basicconnection;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class Server {

    private final int port;
    private final int maxConnections;

    private ExecutorService clientPool;
    private ServerSocket serverSocket;
    private ThreadAccept acceptThread;

    /**
     * Create a new Server instance.
     *
     * @param port the port the server will be running on.
     * @param maxConnections the maximum client connections this server can have
     * at one time.
     */
    public Server(int port, int maxConnections) {
        this.maxConnections = maxConnections;
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
            clientPool = Executors.newFixedThreadPool(maxConnections, new ThreadFactoryBuilder().setNameFormat("BasicConnection Client #%d").build());
            serverSocket = new ServerSocket();
            serverSocket.setPerformancePreferences(0, 2, 1);
            serverSocket.bind(new InetSocketAddress(port), 64);
            acceptThread = new ThreadAccept(serverSocket, clientPool);
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

    /**
     * Gets the maximum number of client connections allowed at one time for the
     * given server.
     *
     * @return the max connections.
     */
    public int getMaxConnections() {
        return maxConnections;
    }
}
