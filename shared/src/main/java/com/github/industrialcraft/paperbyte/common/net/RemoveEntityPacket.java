package com.github.industrialcraft.paperbyte.common.net;

import com.github.industrialcraft.netx.MessageRegistry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RemoveEntityPacket {
    public final int entityId;
    public RemoveEntityPacket(int entityId) {
        this.entityId = entityId;
    }
    public RemoveEntityPacket(DataInputStream stream) throws IOException {
        this.entityId = stream.readInt();
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeInt(entityId);
    }
    public static MessageRegistry.MessageDescriptor<RemoveEntityPacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(RemoveEntityPacket.class, stream -> new RemoveEntityPacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
}
