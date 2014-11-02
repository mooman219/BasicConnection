package com.gmail.mooman219.basicconnection.connection;

import java.io.IOException;
import java.net.BindException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Joseph Cumbo (mooman219)
 */
public abstract class EndPoint {

    private final int port;
    private final PacketManager handler;
    private final ArrayBlockingQueue<Packet> processingQueue;

    private ThreadProcess processThread;

    public EndPoint(int port, int processingSize) {
        this.port = port;
        this.handler = new PacketManager();
        this.processingQueue = new ArrayBlockingQueue<>(processingSize);
    }

    protected abstract void bind() throws IOException;

    public boolean start() {
        try {
            bind();
            processThread = new ThreadProcess(handler, processingQueue);
            processThread.start();
            return true;
        } catch (BindException ex) {
            System.out.println("Port " + port + " already binded. Unable to start server.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void shutdown() {
        processThread.interrupt();
    }

    public int getPort() {
        return port;
    }

    public PacketManager getPacketHandler() {
        return handler;
    }

    public ArrayBlockingQueue<Packet> getProcessingQueue() {
        return processingQueue;
    }
}
