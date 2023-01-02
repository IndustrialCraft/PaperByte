package com.github.industrialcraft.paperbyte.server.testmod;

import com.github.industrialcraft.paperbyte.common.util.Position;
import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.events.*;
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
        event.entityRegistry.register(TestPlatformEntity.TEST_PLATFORM, TestPlatformEntity::new);
    }
    @EventHandler
    public void onGameStart(StartGameEvent event){
        LOBBY = event.server.createWorld();
        new TestPlatformEntity(new Position(0, -5), LOBBY);
    }
    @EventHandler
    public void onCollision(BeginContactEvent contactEvent){
        if(contactEvent.contact.getFixtureA().getBody().getUserData() instanceof TestPlayer player){
            if(contactEvent.contact.getFixtureA().isSensor()){
                player.setOnGround(true);
            }
        }
        if(contactEvent.contact.getFixtureB().getBody().getUserData() instanceof TestPlayer player){
            if(contactEvent.contact.getFixtureB().isSensor()){
                player.setOnGround(true);
            }
        }
    }
    @EventHandler
    public void onCollisionEnd(EndContactEvent contactEvent){
        if(contactEvent.contact.getFixtureA().getBody().getUserData() instanceof TestPlayer player){
            if(contactEvent.contact.getFixtureA().isSensor()){
                player.setOnGround(false);
            }
        }
        if(contactEvent.contact.getFixtureB().getBody().getUserData() instanceof TestPlayer player){
            if(contactEvent.contact.getFixtureB().isSensor()){
                player.setOnGround(false);
            }
        }
    }
}
