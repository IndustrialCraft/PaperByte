package com.github.industrialcraft.paperbyte.common.net;

import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.netx.MessageRegistry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GameDataPacket {
    public final Map<Integer, Identifier> entityRegistry;
    public GameDataPacket(Map<Integer, Identifier> entityRegistry) {
        this.entityRegistry = entityRegistry;
    }
    public GameDataPacket(DataInputStream stream) throws IOException {
        HashMap<Integer,Identifier> modEntityRegistry = new HashMap<>();
        int size = stream.readInt();
        for(int i = 0;i < size;i++){
            modEntityRegistry.put(stream.readInt(), Identifier.parse(stream.readUTF()));
        }
        this.entityRegistry = Collections.unmodifiableMap(modEntityRegistry);
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeInt(entityRegistry.size());
        for(var entry : entityRegistry.entrySet()){
            stream.writeInt(entry.getKey());
            stream.writeUTF(entry.getValue().toString());
        }
    }
    public static MessageRegistry.MessageDescriptor<GameDataPacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(GameDataPacket.class, stream -> new GameDataPacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
}
