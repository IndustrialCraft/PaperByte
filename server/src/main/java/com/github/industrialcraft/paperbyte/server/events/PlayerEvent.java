package com.github.industrialcraft.paperbyte.server.events;

import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.world.ServerPlayerEntity;

public class PlayerEvent extends ServerEvent{
    public final ServerPlayerEntity playerEntity;
    public PlayerEvent(GameServer server, ServerPlayerEntity playerEntity) {
        super(server);
        this.playerEntity = playerEntity;
    }
}
