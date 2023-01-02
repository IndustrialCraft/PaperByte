package com.github.industrialcraft.paperbyte.server.world;

import com.badlogic.gdx.physics.box2d.*;
import com.github.industrialcraft.paperbyte.server.GameServer;
import com.github.industrialcraft.paperbyte.server.events.BeginContactEvent;
import com.github.industrialcraft.paperbyte.server.events.CreateWorldEvent;
import com.github.industrialcraft.paperbyte.server.events.EndContactEvent;
import com.github.industrialcraft.paperbyte.server.events.WorldTickEvent;
import net.cydhra.eventsystem.EventManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class ServerWorld {
    public final GameServer parent;
    public final WorldPacketAnnouncer worldPacketAnnouncer;
    private final LinkedList<ServerEntity> entitiesToAdd;
    private final HashSet<ServerEntity> entities;
    private boolean isRemoved;
    private World physicsWorld;
    public ServerWorld(GameServer parent) {
        this.parent = parent;
        this.worldPacketAnnouncer = new WorldPacketAnnouncer(this);
        this.entities = new HashSet<>();
        this.entitiesToAdd = new LinkedList<>();
        this.isRemoved = false;
        CreateWorldEvent createWorldEvent = new CreateWorldEvent(parent, this);
        EventManager.callEvent(createWorldEvent);
        this.physicsWorld = new World(createWorldEvent.gravity, true);
        this.physicsWorld.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                EventManager.callEvent(new BeginContactEvent(parent, ServerWorld.this, contact));
            }
            @Override
            public void endContact(Contact contact) {
                EventManager.callEvent(new EndContactEvent(parent, ServerWorld.this, contact));
            }
            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }
    public void tick(){
        this.entities.addAll(entitiesToAdd);
        this.entitiesToAdd.clear();
        this.entities.removeIf(entity -> entity.isRemoved() || entity.getWorld() != this);
        this.entities.forEach(ServerEntity::tick);
        EventManager.callEvent(new WorldTickEvent(parent, this));
        this.worldPacketAnnouncer.sendPositionUpdates();
        this.physicsWorld.step(1f/parent.getTps(), 10, 10);
    }
    public World getPhysicsWorld() {
        return physicsWorld;
    }
    public Set<ServerEntity> getEntities() {
        return Collections.unmodifiableSet(entities);
    }
    public void remove(){
        if(!this.isRemoved){
            //todo: kick players to lobby
        }
        this.isRemoved = true;
    }
    public boolean isRemoved() {
        return isRemoved;
    }
    public void addEntity(ServerEntity entity){
        this.entitiesToAdd.add(entity);
    }
}
