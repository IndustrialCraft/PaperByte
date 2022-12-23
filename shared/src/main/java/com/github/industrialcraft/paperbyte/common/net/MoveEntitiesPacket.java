package com.github.industrialcraft.paperbyte.common.net;

import com.github.industrialcraft.netx.MessageRegistry;
import com.github.industrialcraft.paperbyte.common.util.Position;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MoveEntitiesPacket {
    public final ArrayList<Integer> entityIds;
    public final ArrayList<Position> entityPositions;
    public MoveEntitiesPacket(ArrayList<Integer> entityIds, ArrayList<Position> entityPositions) {
        this.entityIds = entityIds;
        this.entityPositions = entityPositions;
    }
    public MoveEntitiesPacket(DataInputStream stream) throws IOException {
        this.entityIds = new ArrayList<>();
        this.entityPositions = new ArrayList<>();
        int size = stream.readInt();
        for(int i = 0;i < size;i++){
            this.entityIds.add(stream.readInt());
            this.entityPositions.add(Position.fromStream(stream));
        }
    }
    public void toStream(DataOutputStream stream) throws IOException {
        int size = entityIds.size();
        stream.writeInt(size);
        for(int i = 0;i < size;i++){
            stream.writeInt(entityIds.get(i));
            entityPositions.get(i).toStream(stream);
        }
    }
    public static MessageRegistry.MessageDescriptor<MoveEntitiesPacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(MoveEntitiesPacket.class, stream -> new MoveEntitiesPacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
}
