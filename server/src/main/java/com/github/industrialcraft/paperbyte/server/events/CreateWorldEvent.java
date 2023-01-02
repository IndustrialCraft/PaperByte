package com.github.industrialcraft.paperbyte.server.events;

import com.badlogic.gdx.math.Vector2;
import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.world.ServerWorld;

public class CreateWorldEvent extends WorldEvent{
    public Vector2 gravity;
    public CreateWorldEvent(GameServer server, ServerWorld world) {
        super(server, world);
        this.gravity = new Vector2(0, -10f);
    }
}
