package com.github.industrialcraft.paperbyte.server.events;

import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.ImageRegistry;
import com.github.industrialcraft.paperbyte.server.world.EntityRegistry;
import com.github.industrialcraft.paperbyte.server.world.SoundRegistry;

public class InitializeRegistriesEvent extends ServerEvent {
    public final EntityRegistry entityRegistry;
    public final SoundRegistry soundRegistry;
    public final ImageRegistry imageRegistry;
    public InitializeRegistriesEvent(GameServer server, EntityRegistry entityRegistry, SoundRegistry soundRegistry, ImageRegistry imageRegistry) {
        super(server);
        this.entityRegistry = entityRegistry;
        this.soundRegistry = soundRegistry;
        this.imageRegistry = imageRegistry;
    }
}
