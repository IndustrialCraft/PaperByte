package com.github.industrialcraft.paperbyte.common.net;

import com.github.industrialcraft.netx.MessageRegistry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientInputPacket {
    public ClientInputPacket(DataInputStream stream) throws IOException {

    }
    public void toStream(DataOutputStream stream) throws IOException {

    }
    public static MessageRegistry.MessageDescriptor<ClientInputPacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(ClientInputPacket.class, stream -> new ClientInputPacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
}
