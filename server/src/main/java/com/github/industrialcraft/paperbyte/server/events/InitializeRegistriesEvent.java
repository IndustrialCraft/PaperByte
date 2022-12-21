package com.github.industrialcraft.paperbyte.server.events;

import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.world.EntityRegistry;

public class InitializeRegistriesEvent extends ServerEvent {
    public final EntityRegistry entityRegistry;
    public InitializeRegistriesEvent(GameServer server, EntityRegistry entityRegistry) {
        super(server);
        this.entityRegistry = entityRegistry;
    }
}
