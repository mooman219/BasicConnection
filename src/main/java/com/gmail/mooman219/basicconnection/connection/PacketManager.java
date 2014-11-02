package com.gmail.mooman219.basicconnection.connection;

import java.util.HashMap;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class PacketManager {

    private final HashMap<Class<? extends Packet>, PacketHandler> handlers = new HashMap<>();

    public void register(Class<? extends Packet> packet, PacketHandler handle) {
        if (handlers.containsKey(packet)) {
            throw new IllegalStateException("Handler already registered for '" + packet.getSimpleName() + "'.");
        }
        handlers.put(packet, handle);
    }

    public void handle(Packet packet) {
        handlers.get(packet.getClass()).handle(packet);
    }
}
