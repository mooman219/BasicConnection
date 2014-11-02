package com.gmail.mooman219.basicconnection.client;

import com.gmail.mooman219.basicconnection.connection.Connection;
import com.gmail.mooman219.basicconnection.connection.EndPoint;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class Client extends EndPoint {

    private final String hostname;

    private Socket socket;
    private Connection connection;
    private Thread serverThread;

    public Client(int port, String hostname) {
        super(port, 128);
        this.hostname = hostname;
    }

    @Override
    protected void bind() throws IOException {
        socket = new Socket();
        socket.setPerformancePreferences(0, 2, 1);
        socket.setReceiveBufferSize(8192);
        socket.setSendBufferSize(8192);
        socket.connect(new InetSocketAddress(hostname, getPort()));
        connection = new Connection(socket, getProcessingQueue());
        connection.start();
    }

    @Override
    public void shutdown() {
        super.shutdown();
        connection.interrupt();
    }

    public Connection getConnection() {
        return connection;
    }

}
