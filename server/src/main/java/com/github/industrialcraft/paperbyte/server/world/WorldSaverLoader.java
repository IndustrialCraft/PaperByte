package com.github.industrialcraft.paperbyte.server.world;

import com.github.industrialcraft.identifier.Identifier;

import java.io.*;

public class WorldSaverLoader {
    public static void save(ServerWorld world, DataOutputStream stream) throws IOException {
        var entities = world.getEntities().stream().filter(entity -> !(entity.isRemoved() || entity instanceof ServerPlayerEntity)).toList();
        stream.writeInt(entities.size());
        for(ServerEntity entity : entities){
            ByteArrayOutputStream bufStream = new ByteArrayOutputStream();
            entity.toStream(new DataOutputStream(bufStream));
            byte[] entityBytes = bufStream.toByteArray();
            stream.writeUTF(entity.getIdentifier().toString());
            stream.writeInt(entityBytes.length);
            stream.write(entityBytes);
        }
    }
    public static void load(EntityRegistry registry, ServerWorld world, DataInputStream stream) throws IOException {
        int size = stream.readInt();
        for(int i = 0;i < size;i++){
            Identifier id = Identifier.parse(stream.readUTF());
            int byteSize = stream.readInt();
            byte[] entityBytes = stream.readNBytes(byteSize);
            ByteArrayInputStream bufStream = new ByteArrayInputStream(entityBytes);
            registry.createEntity(id, new DataInputStream(bufStream), world);//todo: add unknown entity
        }
    }
}
