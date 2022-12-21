package com.github.industrialcraft.paperbyte.server.events;

import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.world.ServerWorld;

public class WorldTickEvent extends ServerEvent{
    public final ServerWorld world;
    public WorldTickEvent(GameServer server, ServerWorld world) {
        super(server);
        this.world = world;
    }
}
