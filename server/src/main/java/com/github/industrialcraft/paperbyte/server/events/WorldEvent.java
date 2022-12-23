package com.github.industrialcraft.paperbyte.server.events;

import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.world.ServerWorld;

public class WorldEvent extends ServerEvent{
    public final ServerWorld world;
    public WorldEvent(GameServer server, ServerWorld world) {
        super(server);
        this.world = world;
    }
}
