package com.github.industrialcraft.paperbyte.server;

import com.github.industrialcraft.netx.NetXServer;
import com.github.industrialcraft.netx.ServerMessage;
import com.github.industrialcraft.netx.SocketUser;
import com.github.industrialcraft.paperbyte.common.net.ClientInputPacket;
import com.github.industrialcraft.paperbyte.common.net.MessageRegistryCreator;
import com.github.industrialcraft.paperbyte.server.events.*;
import com.github.industrialcraft.paperbyte.server.world.EntityRegistry;
import com.github.industrialcraft.paperbyte.server.world.ServerEntity;
import com.github.industrialcraft.paperbyte.server.world.ServerPlayerEntity;
import com.github.industrialcraft.paperbyte.server.world.ServerWorld;
import net.cydhra.eventsystem.EventManager;

import java.util.ArrayList;
import java.util.LinkedList;

public class GameServer extends Thread{
    private final NetXServer networkServer;
    private final EntityRegistry entityRegistry;
    private final LinkedList<ServerWorld> worldsToAdd;
    private final ArrayList<ServerWorld> worlds;
    private long serverStartTime;
    private int serverAliveTicks;
    private int tps;
    public GameServer() {
        this.networkServer = new NetXServer(4321, MessageRegistryCreator.createMessageRegistry());
        this.entityRegistry = new EntityRegistry();
        this.worldsToAdd = new LinkedList<>();
        this.worlds = new ArrayList<>();
        this.serverAliveTicks = 0;
        EventManager.callEvent(new InitializeRegistriesEvent(this, entityRegistry));
        this.entityRegistry.lock();
    }
    @Override
    public void run() {
        this.serverStartTime = System.currentTimeMillis();
        networkServer.start();
        StartGameEvent startGameEvent = new StartGameEvent(this);
        EventManager.callEvent(startGameEvent);
        this.tps = startGameEvent.tps;
        while(true){
            this.worlds.addAll(worldsToAdd);
            this.worldsToAdd.clear();
            this.worlds.removeIf(ServerWorld::isRemoved);
            this.worlds.forEach(ServerWorld::tick);
            EventManager.callEvent(new GameTickEvent(this));
            processNetworkMessages();
            while(System.currentTimeMillis()<serverStartTime+((serverAliveTicks+1)*(1000f/tps))){
                processNetworkMessages();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {throw new RuntimeException(e);}
            }
            serverAliveTicks++;
        }
    }
    public EntityRegistry getEntityRegistry() {
        return entityRegistry;
    }
    public int getServerAliveTicks() {
        return serverAliveTicks;
    }
    public int getTps() {
        return tps;
    }
    public NetXServer getNetworkServer() {
        return networkServer;
    }
    private ServerMessage.Visitor SERVER_MESSAGE_VISITOR = new ServerMessage.Visitor() {
        @Override
        public void connect(SocketUser user) {
            SocketUserData socketUserData = new SocketUserData(user);
            user.setUserData(socketUserData);
            PlayerJoinEvent joinEvent = new PlayerJoinEvent(GameServer.this, socketUserData);
            EventManager.callEvent(joinEvent);
            if(joinEvent.playerSupplier == null){
                user.disconnect();
                throw new IllegalStateException("player supplier not specified");
            }
            if(joinEvent.world == null){
                user.disconnect();
                throw new IllegalStateException("player spawn world not specified");
            }
            ServerPlayerEntity playerEntity = joinEvent.playerSupplier.apply(joinEvent);
            socketUserData.setPlayerEntity(playerEntity);
            playerEntity.getWorld().worldPacketAnnouncer.syncEntitiesToNewPlayer(user);
        }
        @Override
        public void disconnect(SocketUser user) {
            if(user.<SocketUserData>getUserData().getPlayerEntity() != null) {
                user.<SocketUserData>getUserData().getPlayerEntity().remove();
                user.<SocketUserData>getUserData().setPlayerEntity(null);
            }
        }
        @Override
        public void message(SocketUser user, Object msg) {
            if(msg instanceof ClientInputPacket clientInputPacket){
                user.<SocketUserData>getUserData().getPlayerEntity().handleClientInput(clientInputPacket);
            }
        }
        @Override
        public void exception(SocketUser user, Throwable exception) {
            ServerMessage.Visitor.super.exception(user, exception);
        }
    };
    private void processNetworkMessages(){
        while(networkServer.visitMessage(SERVER_MESSAGE_VISITOR));
    }
}
