package com.gmail.mooman219.framework.central.packet;

import com.esotericsoftware.kryo.io.FastInput;
import java.io.IOException;

/**
 * @author Joseph Cumbo (mooman219)
 */
public interface PacketDecoder {

    public Packet decode(FastInput in) throws IOException;
}
