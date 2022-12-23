package com.github.industrialcraft.paperbyte.server.events;

import com.github.industrialcraft.paperbyte.common.util.Position;
import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.SocketUserData;
import com.github.industrialcraft.paperbyte.server.world.ServerEntity;
import com.github.industrialcraft.paperbyte.server.world.ServerPlayerEntity;
import com.github.industrialcraft.paperbyte.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

public class PlayerJoinEvent extends ServerEvent {
    public final SocketUserData socketUserData;
    public Supplier<ServerPlayerEntity> playerSupplier;
    public ServerWorld world;
    public Position position;
    public final ArrayList<Object> joinData;
    public PlayerJoinEvent(GameServer server, SocketUserData socketUserData) {
        super(server);
        this.socketUserData = socketUserData;
        this.joinData = new ArrayList<>();
        this.playerSupplier = null;
        this.world = null;
        this.position = new Position(0, 0);
    }
}
