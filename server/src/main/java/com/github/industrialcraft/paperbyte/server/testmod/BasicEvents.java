package com.github.industrialcraft.paperbyte.server.testmod;

import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.events.InitializeRegistriesEvent;
import com.github.industrialcraft.paperbyte.server.events.PlayerJoinEvent;
import com.github.industrialcraft.paperbyte.server.events.StartGameEvent;
import com.github.industrialcraft.paperbyte.server.world.ServerWorld;
import net.cydhra.eventsystem.listeners.EventHandler;

public class BasicEvents {
    public static ServerWorld LOBBY;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        event.world = LOBBY;
        event.playerSupplier = () -> new TestPlayer(event.position, event.world, event.socketUserData);
    }
    @EventHandler
    public void onRegistryInit(InitializeRegistriesEvent event){
        event.entityRegistry.register(TestPlayer.TEST_PLAYER, (stream, world) -> {throw new IllegalStateException("player cannot be created");});
    }
    @EventHandler
    public void onGameStart(StartGameEvent event){
        LOBBY = event.server.createWorld();
    }
}
