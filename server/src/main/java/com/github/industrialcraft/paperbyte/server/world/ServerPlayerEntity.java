package com.github.industrialcraft.paperbyte.server.world;

import com.github.industrialcraft.paperbyte.common.net.ClientInputPacket;
import com.github.industrialcraft.paperbyte.common.util.Position;
import com.github.industrialcraft.paperbyte.server.SocketUserData;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class ServerPlayerEntity extends ServerEntity {
    public final SocketUserData socketUserData;
    public ServerPlayerEntity(Position position, ServerWorld world, SocketUserData socketUserData) {
        super(position, world);
        this.socketUserData = socketUserData;
    }
    public ServerPlayerEntity(DataInputStream stream, ServerWorld world, SocketUserData socketUserData) throws IOException {
        super(stream, world);
        this.socketUserData = socketUserData;
    }
    public abstract void handleClientInput(ClientInputPacket message);
}
