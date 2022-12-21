package com.github.industrialcraft.paperbyte.server.events;

import com.github.industrialcraft.paperbyte.server.GameServer;

public class GameTickEvent extends ServerEvent{
    public GameTickEvent(GameServer server) {
        super(server);
    }
}
