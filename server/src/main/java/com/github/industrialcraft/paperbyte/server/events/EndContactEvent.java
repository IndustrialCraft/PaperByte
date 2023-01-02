package com.github.industrialcraft.paperbyte.server.events;

import com.badlogic.gdx.physics.box2d.Contact;
import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.world.ServerWorld;

public class EndContactEvent extends WorldEvent{
    public final Contact contact;
    public EndContactEvent(GameServer server, ServerWorld world, Contact contact) {
        super(server, world);
        this.contact = contact;
    }
}