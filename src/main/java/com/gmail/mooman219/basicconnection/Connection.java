package com.gmail.mooman219.framework.central;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class Connection {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Socket socket;
    private final ThreadConnection connectionThread;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.connectionThread = new ThreadConnection(socket);
    }

    public void shutdown() {
        running.set(false);
    }
}
