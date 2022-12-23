package com.github.industrialcraft.paperbyte.server.world;

import com.github.industrialcraft.netx.SocketUser;
import com.github.industrialcraft.paperbyte.common.net.AddEntityPacket;
import com.github.industrialcraft.paperbyte.common.net.MoveEntitiesPacket;
import com.github.industrialcraft.paperbyte.common.net.RemoveEntityPacket;
import com.github.industrialcraft.paperbyte.common.util.Position;
import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.SocketUserData;

import java.util.ArrayList;
import java.util.HashSet;

public class WorldPacketAnnouncer {
    public final ServerWorld world;
    private final HashSet<ServerEntity> movedEntities;
    public WorldPacketAnnouncer(ServerWorld world) {
        this.movedEntities = new HashSet<>();
        this.world = world;
    }
    public void sendPositionUpdates(){
        ArrayList<Integer> entityIds = new ArrayList<>();
        ArrayList<Position> entityPositions = new ArrayList<>();
        for(ServerEntity movedEntity : movedEntities){
            entityIds.add(movedEntity.entityId);
            entityPositions.add(movedEntity.getPosition());
        }
        this.world.parent.getNetworkServer().broadcast(new MoveEntitiesPacket(entityIds, entityPositions), socketUser -> socketUser.<SocketUserData>getUserData().getPlayerEntity().getWorld()==world, true);
    }
    public void syncEntitiesToNewPlayer(SocketUser newPlayer, boolean dontSendPlayer){
        ServerPlayerEntity newPlayerEntity = newPlayer.<SocketUserData>getUserData().getPlayerEntity();
        for(ServerEntity entity : world.getEntities()){
            if(dontSendPlayer && entity == newPlayerEntity)
                continue;
            newPlayer.setUserData(new AddEntityPacket(entity.entityId, world.parent.getEntityRegistry().resolveNetworkId(entity), entity.getPosition()));
        }
    }
    public void announceEntityAdd(ServerEntity entity){
        GameServer server = this.world.parent;
        server.getNetworkServer().broadcast(new AddEntityPacket(entity.entityId, server.getEntityRegistry().resolveNetworkId(entity), entity.getPosition()), socketUser -> socketUser.<SocketUserData>getUserData().getPlayerEntity() != null && socketUser.<SocketUserData>getUserData().getPlayerEntity().getWorld() == world, false);
    }
    public void announceEntityRemove(ServerEntity entity){
        this.world.parent.getNetworkServer().broadcast(new RemoveEntityPacket(entity.entityId), socketUser -> socketUser.<SocketUserData>getUserData().getPlayerEntity().getWorld()==world, false);
    }
    public void announceWorldChange(ServerEntity entity, ServerWorld oldWorld, ServerWorld newWorld){
        this.world.parent.getNetworkServer().broadcast(new AddEntityPacket(entity.entityId, world.parent.getEntityRegistry().resolveNetworkId(entity), entity.getPosition()), socketUser -> socketUser.<SocketUserData>getUserData().getPlayerEntity().getWorld()==newWorld, false);
        this.world.parent.getNetworkServer().broadcast(new RemoveEntityPacket(entity.entityId), socketUser -> socketUser.<SocketUserData>getUserData().getPlayerEntity().getWorld()==oldWorld, false);
    }
    public void announceEntityMove(ServerEntity entity){
        this.movedEntities.add(entity);
    }
}
