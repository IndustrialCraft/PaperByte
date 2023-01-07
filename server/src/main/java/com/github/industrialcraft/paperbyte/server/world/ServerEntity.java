package com.github.industrialcraft.paperbyte.server.world;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.paperbyte.common.net.ChangeWorldPacket;
import com.github.industrialcraft.paperbyte.common.util.Position;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ServerEntity {
    public static final AtomicInteger ENTITY_ID_GENERATOR = new AtomicInteger(0);

    public final int entityId;
    private Position position;
    private ServerWorld world;
    private boolean isRemoved;
    private Body physicsBody;
    public ServerEntity(Position position, ServerWorld world) {
        this.entityId = ENTITY_ID_GENERATOR.incrementAndGet();
        this.position = position;
        this.world = world;
        this.isRemoved = false;
        this.world.addEntity(this);
        this.world.worldPacketAnnouncer.announceEntityAdd(this);

        this.physicsBody = createPhysicsBody(world.getPhysicsWorld());
        if(this.physicsBody != null) {
            this.physicsBody.setTransform(position.x(), position.y(), this.physicsBody.getAngle());
            this.physicsBody.setUserData(this);
        }
    }
    public ServerEntity(DataInputStream stream, ServerWorld world) throws IOException {
        this.entityId = stream.readInt();
        this.position = Position.fromStream(stream);
        this.world = world;
        this.isRemoved = false;
        this.world.addEntity(this);
        this.world.worldPacketAnnouncer.announceEntityAdd(this);

        this.physicsBody = createPhysicsBody(world.getPhysicsWorld());
        if(this.physicsBody != null)
            this.physicsBody.setTransform(position.x(), position.y(), this.physicsBody.getAngle());
    }
    public Body createPhysicsBody(World world){
        return null;
    }
    public Body getPhysicsBody() {
        return physicsBody;
    }
    public void tick(){
        if(this.physicsBody != null) {
            this.position = Position.fromVector2(physicsBody.getPosition());
            this.world.worldPacketAnnouncer.announceEntityMove(this);
            if(this instanceof ServerPlayerEntity serverPlayerEntity)
                serverPlayerEntity.updateCamera();
        }
    }
    public void teleport(Position newPosition){
        this.position = newPosition;
        if(this.physicsBody != null)
            this.physicsBody.setTransform(position.x(), position.y(), this.physicsBody.getAngle());
        this.world.worldPacketAnnouncer.announceEntityMove(this);
    }
    public void teleport(Position newPosition, ServerWorld newWorld){
        if(world.isRemoved())
            return;
        this.position = newPosition;
        if(this.physicsBody != null)
            this.physicsBody.setTransform(position.x(), position.y(), this.physicsBody.getAngle());
        if(this.world != newWorld) {
            if(physicsBody != null) {
                world.getPhysicsWorld().destroyBody(physicsBody);
                this.physicsBody = createPhysicsBody(newWorld.getPhysicsWorld());
            }
            world.worldPacketAnnouncer.announceWorldChange(this, world, newWorld);
            this.world = newWorld;
            this.world.addEntity(this);
            if(this instanceof ServerPlayerEntity serverPlayerEntity) {
                serverPlayerEntity.socketUserData.socketUser.send(new ChangeWorldPacket(), true);
                world.worldPacketAnnouncer.syncEntitiesToNewPlayer(serverPlayerEntity.socketUserData.socketUser, false);
            }
        } else {
            this.world.worldPacketAnnouncer.announceEntityMove(this);
        }
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeInt(this.entityId);
        this.position.toStream(stream);
    }
    public Position getPosition(){
        return this.position;
    }
    public ServerWorld getWorld(){
        return this.world;
    }
    public void remove(){
        if(!isRemoved){
            this.world.worldPacketAnnouncer.announceEntityRemove(this);
        }
        this.isRemoved = true;
    }
    public boolean isRemoved() {
        return isRemoved;
    }

    public abstract Identifier getIdentifier();
}
