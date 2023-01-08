package com.github.industrialcraft.paperbyte.server;

import com.github.industrialcraft.paperbyte.server.world.ServerPlayerEntity;

import java.util.HashMap;

public class PlayerMap<T> extends HashMap<ServerPlayerEntity,T> {
    public PlayerMap(GameServer server){
        server.addPlayerMap(this);
    }
}
