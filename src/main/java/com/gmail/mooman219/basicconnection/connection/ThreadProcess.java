package com.gmail.mooman219.basicconnection.connection;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class ThreadProcess extends Thread {

    private final PacketManager packetHandler;
    private final ArrayBlockingQueue<Packet> processingQueue;

    public ThreadProcess(PacketManager packetHandler, ArrayBlockingQueue<Packet> processingQueue) {
        super("BasicConnection:Internal Process");
        this.packetHandler = packetHandler;
        this.processingQueue = processingQueue;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                Packet packet = processingQueue.take();
                packetHandler.handle(packet);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
