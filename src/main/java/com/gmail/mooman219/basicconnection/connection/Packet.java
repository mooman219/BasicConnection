package com.gmail.mooman219.basicconnection.connection;

import gnu.trove.map.hash.TByteObjectHashMap;
import gnu.trove.map.hash.TObjectByteHashMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Joseph Cumbo (mooman219)
 */
public abstract class Packet {

    private static final AtomicInteger packetCount = new AtomicInteger(1);
    private static final TObjectByteHashMap<Class<? extends Packet>> ids = new TObjectByteHashMap(16, .50f, (byte) 0xFF);
    private static final TByteObjectHashMap<PacketDecoder> decoders = new TByteObjectHashMap(16, .50f, (byte) 0xFF);

    private Connection sender = null;

    public static byte getPacketId(Class<? extends Packet> packet) {
        byte ret = ids.get(packet);
        if ((ret & 0xFF) == 0xFF) {
            System.out.println("Packet.getPacketId: Packet ID for (" + packet.getSimpleName() + ") does not exist.");
        }
        return ret;
    }

    public static Packet read(byte id, ObjectInputStream in) {
        PacketDecoder decoder = decoders.get(id);
        if (decoder != null) {
            try {
                return decoder.decode(in);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Packet.read: Decoder for ID (" + id + ") does not exist.");
        }
        return null;
    }

    public static boolean send(ObjectOutputStream out, Packet p) {
        byte id = ids.get(p.getClass());
        if ((id & 0xFF) == 0xFF) {
            throw new IllegalStateException("Packet.send: Packet ID (" + id + ") does not exist.");
        } else {
            try {
                out.writeByte(id);
                p.write(out);
                out.flush();
            } catch (SocketException ex) {
                ex.printStackTrace();
                return false;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    public static void registerPacket(Class<? extends Packet> packet, PacketDecoder decoder) {
        if (ids.containsKey(packet)) {
            throw new IllegalStateException("Packet (" + packet.getSimpleName() + ") is already registered.");
        }
        byte id = (byte) (packetCount.getAndIncrement() & 0xFF);
        if (decoders.containsKey(id)) {
            throw new IllegalStateException("Too many packets have been registered. Unable to register (" + packet.getSimpleName() + ").");
        } else {
            decoders.put(id, decoder);
            ids.put(packet, id);
        }
    }

    public Connection getSender() {
        return sender;
    }

    protected void setSender(Connection sender) {
        this.sender = sender;
    }

    protected abstract void write(ObjectOutputStream out) throws IOException;

    protected abstract void read(ObjectInputStream in) throws IOException;

}
