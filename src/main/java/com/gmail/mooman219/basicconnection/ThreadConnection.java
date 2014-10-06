package com.gmail.mooman219.framework.central;

import com.esotericsoftware.kryo.io.FastInput;
import com.esotericsoftware.kryo.io.FastOutput;
import com.gmail.mooman219.framework.central.packet.Packet;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class ThreadConnection extends Thread {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ArrayBlockingQueue<Packet> pendingPackets = new ArrayBlockingQueue<>(64);
    private final String address;
    private final FastOutput out;
    private final FastInput in;

    public ThreadConnection(Socket socket) throws IOException {
        super("FrameworkCentral - Receive (" + socket.getInetAddress().getHostAddress() + ")");
        this.address = socket.getInetAddress().getHostAddress();
        this.out = new FastOutput(socket.getOutputStream());
        this.in = new FastInput(socket.getInputStream());
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
            Logger.getLogger(ThreadConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        running.set(true);
        while (running.get()) {
            byte input = in.readByte();
            if (input != -1) {
                Packet packet = Packet.next(input, in);
                if (packet != null) {
                    // Process packet on main thread
                } else {
                    Logger.getLogger(ThreadConnection.class.getName()).log(Level.WARNING, "Unable to read packet ID ({0}) for {1}", new Object[]{input, address});
                }
            }
            while (!pendingPackets.isEmpty()) {
                Packet packet = pendingPackets.poll();
                if (packet != null) {
                    Packet.send(out, packet);
                } else {
                    break;
                }
            }
        }
    }

    public void shutdown() {
        running.set(false);
    }
}
