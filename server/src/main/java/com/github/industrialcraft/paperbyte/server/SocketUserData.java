package com.github.industrialcraft.paperbyte.server;

import com.github.industrialcraft.netx.SocketUser;
import com.github.industrialcraft.paperbyte.server.world.ServerEntity;
import com.github.industrialcraft.paperbyte.server.world.ServerPlayerEntity;

public class SocketUserData {
    public final SocketUser socketUser;
    private ServerPlayerEntity playerEntity;
    public SocketUserData(SocketUser socketUser) {
        this.socketUser = socketUser;
    }
    public ServerPlayerEntity getPlayerEntity() {
        return playerEntity;
    }
    public void setPlayerEntity(ServerPlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }
}
