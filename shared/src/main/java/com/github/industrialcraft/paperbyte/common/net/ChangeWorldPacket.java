package com.github.industrialcraft.paperbyte.common.net;

import com.github.industrialcraft.netx.MessageRegistry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChangeWorldPacket {
    public ChangeWorldPacket() {

    }
    public ChangeWorldPacket(DataInputStream stream) throws IOException {

    }
    public void toStream(DataOutputStream stream) throws IOException {

    }
    public static MessageRegistry.MessageDescriptor<ChangeWorldPacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(ChangeWorldPacket.class, stream -> new ChangeWorldPacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
}
