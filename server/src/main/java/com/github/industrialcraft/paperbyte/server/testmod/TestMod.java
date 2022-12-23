package com.github.industrialcraft.paperbyte.server.testmod;

import net.cydhra.eventsystem.EventManager;

public class TestMod {
    public TestMod() {
        EventManager.registerListeners(new BasicEvents());
    }
}
