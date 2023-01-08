package com.github.industrialcraft.paperbyte.server;

import com.badlogic.gdx.graphics.Color;
import com.github.industrialcraft.netx.NetXServer;
import com.github.industrialcraft.netx.ServerMessage;
import com.github.industrialcraft.netx.SocketUser;
import com.github.industrialcraft.paperbyte.common.gui.RectUIComponent;
import com.github.industrialcraft.paperbyte.common.net.AddEntityPacket;
import com.github.industrialcraft.paperbyte.common.net.ClientInputPacket;
import com.github.industrialcraft.paperbyte.common.net.GameDataPacket;
import com.github.industrialcraft.paperbyte.common.net.MessageRegistryCreator;
import com.github.industrialcraft.paperbyte.server.events.*;
import com.github.industrialcraft.paperbyte.server.world.EntityRegistry;
import com.github.industrialcraft.paperbyte.server.world.ServerPlayerEntity;
import com.github.industrialcraft.paperbyte.server.world.ServerWorld;
import com.github.industrialcraft.paperbyte.server.world.SoundRegistry;
import net.cydhra.eventsystem.EventManager;
import net.cydhra.eventsystem.listeners.EventHandler;
import org.pf4j.DefaultPluginManager;
import org.pf4j.Plugin;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;

public class GameServer extends Thread{
    private final NetXServer networkServer;
    private final EntityRegistry entityRegistry;
    private final SoundRegistry soundRegistry;
    private final LinkedList<ServerWorld> worldsToAdd;
    private final ArrayList<ServerWorld> worlds;
    private long serverStartTime;
    private int serverAliveTicks;
    private int tps;
    private PluginManager pluginManager;
    private ClientDataBundler clientDataBundler;
    public final Logger logger;
    private Map<Class, Plugin> plugins;
    private List<WeakReference<PlayerMap>> playerMaps;
    public GameServer() {
        this.logger = new Logger(this);
        loadMods();
        this.networkServer = new NetXServer(4321, MessageRegistryCreator.createMessageRegistry());
        this.entityRegistry = new EntityRegistry();
        this.soundRegistry = new SoundRegistry();
        this.worldsToAdd = new LinkedList<>();
        this.worlds = new ArrayList<>();
        this.serverAliveTicks = 0;
        EventManager.callEvent(new InitializeRegistriesEvent(this, entityRegistry, soundRegistry));
        this.entityRegistry.lock();
        this.soundRegistry.lock();
        this.logger.info("Loaded %s entities", this.entityRegistry.getRegisteredEntities().size());
        this.clientDataBundler = new ClientDataBundler(this);
        this.entityRegistry.registerToBundler(clientDataBundler);
        this.soundRegistry.registerToBundler(clientDataBundler);
        try {
            FileOutputStream stream = new FileOutputStream("out.zip");
            this.clientDataBundler.createZip(stream);
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.playerMaps = new ArrayList<>();
    }
    public ServerWorld createWorld(){
        ServerWorld world = new ServerWorld(this);
        this.worldsToAdd.add(world);
        return world;
    }
    public void loadMods(){
        this.pluginManager = new DefaultPluginManager();
        this.pluginManager.loadPlugins();
        this.pluginManager.startPlugins();
        this.plugins = pluginManager.getPlugins().stream().map(PluginWrapper::getPlugin).collect(Collectors.toMap(Plugin::getClass, o -> o));
        this.logger.info("Loaded %s mods[%s]", this.pluginManager.getPlugins().size(), this.pluginManager.getPlugins().stream().map(PluginWrapper::getPluginId).collect(Collectors.joining(",")));
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
            this.playerMaps.removeIf(playerMapWeakReference -> playerMapWeakReference.get()==null);
            EventManager.callEvent(new GameTickEvent(this));
            processNetworkMessages();
            while(System.currentTimeMillis()<serverStartTime+((serverAliveTicks+1)*(1000/tps))){
                processNetworkMessages();
                Thread.yield();
            }
            serverAliveTicks++;
        }
    }
    public List<ServerPlayerEntity> getPlayers(){
        return networkServer.getUsers().stream().map(socketUser -> socketUser.<SocketUserData>getUserData().getPlayerEntity()).filter(Objects::nonNull).toList();
    }
    public <T extends Plugin> T getPlugin(Class<T> clazz){
        return (T) plugins.get(clazz);
    }
    public void addPlayerMap(PlayerMap playerMap){
        this.playerMaps.add(new WeakReference<>(playerMap));
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
    private final ServerMessage.Visitor SERVER_MESSAGE_VISITOR = new ServerMessage.Visitor() {
        @Override
        public void connect(SocketUser user) {
            user.send(new GameDataPacket(entityRegistry.getRegisteredEntities(), soundRegistry.getRegisteredSounds()), true);
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
            ServerPlayerEntity playerEntity = joinEvent.playerSupplier.get();
            socketUserData.setPlayerEntity(playerEntity);
            playerEntity.getWorld().worldPacketAnnouncer.syncEntitiesToNewPlayer(user, true);
            user.send(new AddEntityPacket(playerEntity.entityId, getEntityRegistry().resolveNetworkId(playerEntity), playerEntity.getPosition()));
        }
        @Override
        public void disconnect(SocketUser user) {
            if(user.<SocketUserData>getUserData().getPlayerEntity() != null) {
                for(WeakReference<PlayerMap> playerMapRef : playerMaps){
                    PlayerMap playerMap = playerMapRef.get();
                    if(playerMap != null)
                        playerMap.remove(user.<SocketUserData>getUserData().getPlayerEntity());
                }
                EventManager.callEvent(new PlayerLeaveEvent(GameServer.this, user.<SocketUserData>getUserData().getPlayerEntity()));
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
