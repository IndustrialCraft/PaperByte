package com.github.industrialcraft.paperbyte.server.events;

import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.world.ServerPlayerEntity;

public class PlayerLeaveEvent extends PlayerEvent{
    public PlayerLeaveEvent(GameServer server, ServerPlayerEntity playerEntity) {
        super(server, playerEntity);
    }
}
