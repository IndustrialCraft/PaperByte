package com.github.industrialcraft.paperbyte.common.net;

import com.github.industrialcraft.netx.MessageRegistry;

public class MessageRegistryCreator {
    public static MessageRegistry createMessageRegistry(){
        MessageRegistry messageRegistry = new MessageRegistry();
        messageRegistry.register(1, AddEntityPacket.createDescriptor());
        messageRegistry.register(2, CameraUpdatePacket.createDescriptor());
        messageRegistry.register(3, ClientInputPacket.createDescriptor());
        messageRegistry.register(4, EntityAnimationPacket.createDescriptor());
        messageRegistry.register(5, MoveEntitiesPacket.createDescriptor());
        messageRegistry.register(6, RemoveEntityPacket.createDescriptor());
        return messageRegistry;
    }
}
