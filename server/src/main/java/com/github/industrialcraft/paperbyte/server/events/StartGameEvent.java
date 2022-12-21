package com.github.industrialcraft.paperbyte.server.events;

import com.github.industrialcraft.paperbyte.server.GameServer;
import net.cydhra.eventsystem.events.Event;

public class StartGameEvent extends ServerEvent {
    public int tps;
    public StartGameEvent(GameServer server) {
        super(server);
        this.tps = 30;
    }
}
