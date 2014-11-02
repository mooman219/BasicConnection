package com.gmail.mooman219.basicconnection.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class Connection extends Thread {

    private final ArrayBlockingQueue<Packet> pendingPackets = new ArrayBlockingQueue<>(128);
    private final ArrayBlockingQueue<Packet> processingQueue;
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public Connection(Socket socket, ArrayBlockingQueue<Packet> processingQueue) throws IOException {
        super("BasicConnection:Internal Connection");
        this.socket = socket;
        this.processingQueue = processingQueue;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Sending is not instant. It is queued to be sent.
     *
     * @param packet Packet to send.
     */
    public void send(Packet packet) {
        try {
            pendingPackets.put(packet);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public String getHostname() {
        return socket.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            attemptReads();
            attemptWrites();
        }
        try {
            this.socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("Connection closed for (" + this.getHostname() + ").");
    }

    private void attemptReads() {
        try {
            while (in.available() > 0) {
                Packet packet = Packet.read(in.readByte(), in);
                if (packet != null) {
                    packet.setSender(this);
                    processingQueue.offer(packet);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void attemptWrites() {
        if (!pendingPackets.isEmpty()) {
            Packet packet;
            while ((packet = pendingPackets.poll()) != null) {
                if (!Packet.send(out, packet)) {
                    this.interrupt();
                }
            }
        }
    }
}
