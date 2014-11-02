package com.gmail.mooman219.basicconnection.server;

import com.gmail.mooman219.basicconnection.connection.EndPoint;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class Server extends EndPoint {

    private ConnectionManager connectionManager;
    private ServerSocket serverSocket;
    private ThreadAccept acceptThread;

    public Server(int port, int maxConnections) {
        super(port, 128);
        this.connectionManager = new ConnectionManager(maxConnections);
    }

    @Override
    protected void bind() throws IOException {
        clientPool = Executors.newFixedThreadPool(maxConnections);
        serverSocket = new ServerSocket();
        serverSocket.setPerformancePreferences(0, 2, 1);
        serverSocket.setReceiveBufferSize(8192);
        serverSocket.bind(new InetSocketAddress(getPort()), 64);
        acceptThread = new ThreadAccept(serverSocket, this);
        acceptThread.start();
    }

    @Override
    public void shutdown() {
        super.shutdown();
        acceptThread.interrupt();
        clientPool.shutdownNow();
    }

    public int getMaxConnections() {
        return connectionManager.getMaxConnections();
    }
}
