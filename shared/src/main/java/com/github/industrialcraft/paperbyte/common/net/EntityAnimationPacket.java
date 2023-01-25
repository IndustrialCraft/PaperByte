package com.github.industrialcraft.paperbyte.common.net;

import com.github.industrialcraft.netx.MessageRegistry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EntityAnimationPacket {
    public final int entityId;
    public final String animationId;//todo: make animation string
    public EntityAnimationPacket(int entityId, String animationId) {
        this.entityId = entityId;
        this.animationId = animationId;
    }
    public EntityAnimationPacket(DataInputStream stream) throws IOException {
        this.entityId = stream.readInt();
        this.animationId = stream.readUTF();
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeInt(entityId);
        stream.writeUTF(animationId);
    }
    public static MessageRegistry.MessageDescriptor<EntityAnimationPacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(EntityAnimationPacket.class, stream -> new EntityAnimationPacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
}
