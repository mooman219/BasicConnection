package com.gmail.mooman219.framework.central;

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

    public Server(int port) {
        this.port = port;
    }

    public boolean init() {
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
}
