package com.github.industrialcraft.paperbyte.server.events;

import com.github.industrialcraft.paperbyte.common.net.ClientInputPacket;
import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.world.ServerPlayerEntity;

import java.util.ArrayList;

public class PlayerInputEvent extends PlayerEvent{
    public final ArrayList<ClientInputPacket.Visitor> listeners;
    public PlayerInputEvent(GameServer server, ServerPlayerEntity playerEntity) {
        super(server, playerEntity);
        this.listeners = new ArrayList<>();
    }
    public void addListener(ClientInputPacket.Visitor listener){
        this.listeners.add(listener);
    }
    private ClientInputPacket.Visitor VISITOR = new ClientInputPacket.Visitor() {
        @Override
        public void keyTyped(char ch) {
            for(ClientInputPacket.Visitor listener : listeners)
                listener.keyTyped(ch);
        }
        @Override
        public void keyDown(int keycode) {
            for(ClientInputPacket.Visitor listener : listeners)
                listener.keyDown(keycode);
        }
        @Override
        public void keyUp(int keycode) {
            for(ClientInputPacket.Visitor listener : listeners)
                listener.keyUp(keycode);
        }
    };
    public void firePacket(ClientInputPacket clientInputPacket){
        while(clientInputPacket.readTyped(VISITOR));
    }
}
