package com.github.industrialcraft.paperbyte.common.net;

import com.github.industrialcraft.netx.MessageRegistry;
import com.github.industrialcraft.paperbyte.common.util.Position;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AddEntityPacket {
    public final int entityId;
    public final int entityType;
    public final Position position;
    public AddEntityPacket(int entityId, int entityType, Position position) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.position = position;
    }
    public AddEntityPacket(DataInputStream stream) throws IOException {
        this.entityId = stream.readInt();
        this.entityType = stream.readInt();
        this.position = Position.fromStream(stream);
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeInt(entityId);
        stream.writeInt(entityType);
        this.position.toStream(stream);
    }
    public static MessageRegistry.MessageDescriptor<AddEntityPacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(AddEntityPacket.class, stream -> new AddEntityPacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
}
