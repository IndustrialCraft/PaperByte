package com.github.industrialcraft.paperbyte.server.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.github.industrialcraft.netx.SocketUser;
import com.github.industrialcraft.paperbyte.common.net.*;
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
        ArrayList<ServerCollisionsDebugPacket.RenderData> collisionRenderData = new ArrayList<>();
        for(var ent : world.getEntities()){
            Body body = ent.getPhysicsBody();
            if(body != null && body.isActive()){
                Transform transform = body.getTransform();
                transform = new Transform(transform.getPosition(), transform.getRotation());
                for(var fixture : body.getFixtureList()){
                    collisionRenderData.add(switch (fixture.getShape().getType()){
                        case Circle -> {
                            CircleShape shape = (CircleShape) fixture.getShape();
                            yield new ServerCollisionsDebugPacket.ServerCollisionRenderDataCircle(transform, shape.getPosition().cpy(), shape.getRadius());
                        }
                        case Edge -> {
                            EdgeShape shape = (EdgeShape) fixture.getShape();
                            Vector2 v1 = new Vector2();
                            Vector2 v2 = new Vector2();
                            shape.getVertex1(v1);
                            shape.getVertex2(v2);
                            yield new ServerCollisionsDebugPacket.ServerCollisionRenderDataEdge(transform, v1, v2);
                        }
                        case Polygon -> {
                            PolygonShape shape = (PolygonShape) fixture.getShape();
                            Vector2[] vertices = new Vector2[shape.getVertexCount()];
                            for(int i = 0;i < vertices.length;i++) {
                                vertices[i] = new Vector2();
                                shape.getVertex(i, vertices[i]);
                            }
                            yield new ServerCollisionsDebugPacket.ServerCollisionRenderDataPolygon(transform, vertices);
                        }
                        case Chain -> {
                            ChainShape shape = (ChainShape) fixture.getShape();
                            Vector2[] vertices = new Vector2[shape.getVertexCount()];
                            for(int i = 0;i < vertices.length;i++) {
                                vertices[i] = new Vector2();
                                shape.getVertex(i, vertices[i]);
                            }
                            yield new ServerCollisionsDebugPacket.ServerCollisionRenderDataChain(transform, vertices);
                        }
                    });
                }
            }
        }
        this.world.parent.getNetworkServer().broadcast(new ServerCollisionsDebugPacket(collisionRenderData), socketUser -> isUserInWorld(socketUser, world) && socketUser.<SocketUserData>getUserData().getPlayerEntity().shouldSendHitBoxes(), true);

        ArrayList<Integer> entityIds = new ArrayList<>();
        ArrayList<Position> entityPositions = new ArrayList<>();
        for(ServerEntity movedEntity : movedEntities){
            entityIds.add(movedEntity.entityId);
            entityPositions.add(movedEntity.getPosition());
        }
        this.world.parent.getNetworkServer().broadcast(new MoveEntitiesPacket(entityIds, entityPositions), socketUser -> isUserInWorld(socketUser, world), true);
    }
    public void syncEntitiesToNewPlayer(SocketUser newPlayer, boolean dontSendPlayer){
        ServerPlayerEntity newPlayerEntity = newPlayer.<SocketUserData>getUserData().getPlayerEntity();
        for(ServerEntity entity : world.getEntities()){
            if(dontSendPlayer && entity == newPlayerEntity)
                continue;
            //todo: sync animations start times
            newPlayer.send(new AddEntityPacket(entity.entityId, world.parent.getEntityRegistry().resolveNetworkId(entity), entity.getPosition(), entity.getAnimationController().getCurrentAnimation()), false);
        }
    }
    public void announceEntityAdd(ServerEntity entity){
        GameServer server = this.world.parent;
        server.getNetworkServer().broadcast(new AddEntityPacket(entity.entityId, server.getEntityRegistry().resolveNetworkId(entity), entity.getPosition(), entity.getAnimationController().getCurrentAnimation()), socketUser -> isUserInWorld(socketUser, world), false);
    }
    public void announceEntityRemove(ServerEntity entity){
        this.world.parent.getNetworkServer().broadcast(new RemoveEntityPacket(entity.entityId), socketUser -> isUserInWorld(socketUser, world), false);
    }
    public void announceEntityAnimation(ServerEntity entity, String animation){
        this.world.parent.getNetworkServer().broadcast(new EntityAnimationPacket(entity.entityId, animation), socketUser -> isUserInWorld(socketUser, world), false);
    }
    public void announceWorldChange(ServerEntity entity, ServerWorld oldWorld, ServerWorld newWorld){
        this.world.parent.getNetworkServer().broadcast(new AddEntityPacket(entity.entityId, world.parent.getEntityRegistry().resolveNetworkId(entity), entity.getPosition(), entity.getAnimationController().getCurrentAnimation()), socketUser -> isUserInWorld(socketUser, newWorld), false);
        this.world.parent.getNetworkServer().broadcast(new RemoveEntityPacket(entity.entityId), socketUser -> isUserInWorld(socketUser, oldWorld), false);
    }
    public void sendToEveryoneInWorld(Object message, boolean flush){
        this.world.parent.getNetworkServer().broadcast(message, socketUser -> isUserInWorld(socketUser, world), flush);
    }
    private static boolean isUserInWorld(SocketUser socketUser, ServerWorld world){
        return socketUser.getUserData() != null && socketUser.<SocketUserData>getUserData().getPlayerEntity() != null && socketUser.<SocketUserData>getUserData().getPlayerEntity().getWorld() == world;
    }
    public void announceEntityMove(ServerEntity entity){
        this.movedEntities.add(entity);
    }
}
