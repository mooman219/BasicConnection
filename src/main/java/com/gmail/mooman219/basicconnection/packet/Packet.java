package com.gmail.mooman219.framework.central.packet;

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

    public static Packet next(byte id, FastInput in) {
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

    protected static void registerPacket(Class<? extends Packet> packet, PacketDecoder decoder) {
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
