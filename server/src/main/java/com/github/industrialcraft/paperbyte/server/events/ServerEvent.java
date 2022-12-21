package com.github.industrialcraft.paperbyte.server.events;

import com.github.industrialcraft.paperbyte.server.GameServer;
import net.cydhra.eventsystem.events.Event;

public class ServerEvent extends Event {
    public final GameServer server;
    public ServerEvent(GameServer server) {
        this.server = server;
    }
}
