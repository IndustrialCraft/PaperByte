package com.github.industrialcraft.paperbyte.server.events;

import com.github.industrialcraft.paperbyte.common.net.SetGUIPacket;
import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.world.ServerPlayerEntity;

public class SendGUIEvent extends PlayerEvent{
    public final SetGUIPacket packet;
    public SendGUIEvent(GameServer server, ServerPlayerEntity playerEntity, SetGUIPacket packet) {
        super(server, playerEntity);
        this.packet = packet;
    }
}
