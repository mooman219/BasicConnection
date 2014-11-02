package com.gmail.mooman219.basicconnection.server;

import com.gmail.mooman219.basicconnection.connection.Connection;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class ThreadAccept extends Thread {

    private final AtomicInteger connectionsCreated = new AtomicInteger(0);
    private final ServerSocket serverSocket;
    private final Server server;

    public ThreadAccept(ServerSocket serverSocket, Server server) {
        super("BasicConnection:Server Accept");
        this.serverSocket = serverSocket;
        this.server = server;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                socket.setReceiveBufferSize(8192);
                socket.setSendBufferSize(8192);
                Connection connection = new Connection(socket, server.getProcessingQueue());
                server.getClientPool().submit(connection);
            } catch (IOException ex) {
                System.out.println("Error accepting connection. Skipping.");
                ex.printStackTrace();
            }
        }
    }
}
