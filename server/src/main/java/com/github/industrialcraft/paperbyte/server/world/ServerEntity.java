package com.github.industrialcraft.paperbyte.server.world;

import com.badlogic.gdx.physics.box2d.World;
import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.paperbyte.common.util.Position;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class ServerEntity {
    public static final AtomicInteger ENTITY_ID_GENERATOR = new AtomicInteger(0);

    public final int entityId;
    private Position position;
    private ServerWorld world;
    private boolean isRemoved;
    public ServerEntity(Position position, ServerWorld world) {
        this.entityId = ENTITY_ID_GENERATOR.incrementAndGet();
        this.position = position;
        this.world = world;
        this.isRemoved = false;
        this.world.addEntity(this);
        this.world.worldPacketAnnouncer.announceEntityAdd(this);
    }
    public void tick(){

    }
    public void teleport(Position newPosition){
        this.position = newPosition;
        this.world.worldPacketAnnouncer.announceEntityMove(this);
    }
    public void teleport(Position newPosition, ServerWorld newWorld){
        this.position = newPosition;
        this.world = newWorld;
        this.world.addEntity(this);
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
