package com.gmail.mooman219.basicconnection.packet;

import com.esotericsoftware.kryo.io.FastInput;
import com.esotericsoftware.kryo.io.FastOutput;
import gnu.trove.map.hash.TByteObjectHashMap;
import gnu.trove.map.hash.TObjectByteHashMap;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Joseph Cumbo (mooman219)
 */
public abstract class Packet {

    private static final AtomicInteger packetCount = new AtomicInteger(1);

    private static final TObjectByteHashMap<Class<? extends Packet>> ids = new TObjectByteHashMap<>(16, .75f, (byte) 0); // Takes Class<Packet>, returns id
    private static final TByteObjectHashMap<PacketDecoder> decoders = new TByteObjectHashMap<>(); // Takes id, returns decoder

    /**
     * Reads the incoming data on 'in'. The data is read by the PacketDecoder
     * associated with the given 'id'.
     *
     * @param id the id of the packet that will decode the input.
     * @param in the input being read.
     * @return the Packet representing the given id, null if there's an error
     * while decoding or no PacketDecoder exists for the given 'id'.
     */
    public static Packet read(byte id, FastInput in) {
        PacketDecoder decoder = decoders.get(id);
        if (decoder != null) {
            try {
                return decoder.decode(in);
            } catch (IOException ex) {
                Logger.getLogger(Packet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     * Writes Packet 'p' to the output 'out'.
     *
     * @param out the output being written to.
     * @param p the packet being written.
     * @throws IllegalStateException if the packet being written has not been
     * registered.
     */
    public static void send(FastOutput out, Packet p) {
        byte id = ids.get(p.getClass());
        if (id != 0) {
            try {
                out.writeByte(id);
                p.write(out);
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(Packet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            throw new IllegalStateException("Packet.send: Packet ID (" + id + ") does not exist.");
        }
    }

    /**
     * Registers a packet. A packet needs to be registered in order to read and
     * write packets of that type.
     *
     * @param packet the packet type being registered.
     * @param decoder the associated decoder that will be used when reading
     * packets of given type 'packet'.
     */
    public static void registerPacket(Class<? extends Packet> packet, PacketDecoder decoder) {
        byte id = (byte) (packetCount.getAndIncrement() & 0xFF);
        if (decoders.contains(id)) {
            throw new IllegalStateException("Too many packets have been registered. Tried registering " + id);
        } else {
            decoders.put(id, decoder);
            ids.put(packet, id);
        }
    }

    protected abstract void write(FastOutput out) throws IOException;

    protected abstract Packet read(FastInput in) throws IOException;
}
