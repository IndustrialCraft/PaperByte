package com.github.industrialcraft.paperbyte.server.events;

import com.github.industrialcraft.paperbyte.common.util.Position;
import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.SocketUserData;
import com.github.industrialcraft.paperbyte.server.world.ServerPlayerEntity;
import com.github.industrialcraft.paperbyte.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.function.Supplier;

public class PlayerJoinEvent extends ServerEvent {
    public final SocketUserData socketUserData;
    public Supplier<ServerPlayerEntity> playerSupplier;
    public ServerWorld world;
    public Position position;
    public final ArrayList<Object> joinData;
    public final String locale;
    public PlayerJoinEvent(GameServer server, SocketUserData socketUserData, String locale) {
        super(server);
        this.socketUserData = socketUserData;
        this.locale = locale;
        this.joinData = new ArrayList<>();
        this.playerSupplier = null;
        this.world = null;
        this.position = new Position(0, 0);
    }
}
