package com.gmail.mooman219.framework.central;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class ThreadAccept extends Thread {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ServerSocket serverSocket;

    public ThreadAccept(ServerSocket serverSocket) {
        super("FrameworkCentral - Accept");
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        running.set(true);
        while (running.get()) {
            try {
                Socket acceptedConnection = serverSocket.accept();
            } catch (IOException ex) {
                Logger.getLogger(ThreadAccept.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void shutdown() {
        running.set(false);
    }
}
