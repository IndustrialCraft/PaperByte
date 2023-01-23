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
    public final Map<Integer, Identifier> soundRegistry;
    public final byte[] clientData;
    public GameDataPacket(Map<Integer, Identifier> entityRegistry, Map<Integer, Identifier> soundRegistry, byte[] clientData) {
        this.entityRegistry = entityRegistry;
        this.soundRegistry = soundRegistry;
        this.clientData = clientData;
    }
    public GameDataPacket(DataInputStream stream) throws IOException {
        HashMap<Integer,Identifier> modEntityRegistry = new HashMap<>();
        int size = stream.readInt();
        for(int i = 0;i < size;i++){
            modEntityRegistry.put(stream.readInt(), Identifier.parse(stream.readUTF()));
        }
        this.entityRegistry = Collections.unmodifiableMap(modEntityRegistry);
        HashMap<Integer,Identifier> modSoundRegistry = new HashMap<>();
        size = stream.readInt();
        for(int i = 0;i < size;i++){
            modSoundRegistry.put(stream.readInt(), Identifier.parse(stream.readUTF()));
        }
        this.soundRegistry = Collections.unmodifiableMap(modSoundRegistry);
        int clientDataSize = stream.readInt();
        this.clientData = new byte[clientDataSize];
        for(int i = 0;i < clientDataSize;i++){
            clientData[i] = stream.readByte();
        }
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeInt(entityRegistry.size());
        for(var entry : entityRegistry.entrySet()){
            stream.writeInt(entry.getKey());
            stream.writeUTF(entry.getValue().toString());
        }
        stream.writeInt(soundRegistry.size());
        for(var entry : soundRegistry.entrySet()){
            stream.writeInt(entry.getKey());
            stream.writeUTF(entry.getValue().toString());
        }
        stream.writeInt(clientData.length);
        for(int i = 0;i < clientData.length;i++){
            stream.writeByte(clientData[i]);
        }
    }
    public static MessageRegistry.MessageDescriptor<GameDataPacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(GameDataPacket.class, stream -> new GameDataPacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
}
