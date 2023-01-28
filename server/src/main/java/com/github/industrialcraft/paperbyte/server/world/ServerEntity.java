package com.github.industrialcraft.paperbyte.server.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.paperbyte.common.net.ChangeWorldPacket;
import com.github.industrialcraft.paperbyte.common.util.Position;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ServerEntity {
    public static final AtomicInteger ENTITY_ID_GENERATOR = new AtomicInteger(0);

    public final int entityId;
    public final UUID uuid;
    private Position position;
    private ServerWorld world;
    private boolean isRemoved;
    private Body physicsBody;
    private AnimationController animationController;
    public ServerEntity(Position position, ServerWorld world) {
        this.entityId = ENTITY_ID_GENERATOR.incrementAndGet();
        this.uuid = UUID.randomUUID();
        this.position = position;
        this.world = world;
        this.isRemoved = false;
        this.animationController = createAnimationController();
        this.world.addEntity(this);
        this.world.worldPacketAnnouncer.announceEntityAdd(this);

        this.physicsBody = createPhysicsBody(world.getPhysicsWorld());
        if(this.physicsBody != null) {
            this.physicsBody.setTransform(position.x(), position.y(), this.physicsBody.getAngle());
            this.physicsBody.setUserData(this);
        }
    }
    public ServerEntity(DataInputStream stream, ServerWorld world) throws IOException {
        this.entityId = ENTITY_ID_GENERATOR.incrementAndGet();
        this.uuid = new UUID(stream.readLong(), stream.readLong());
        this.position = Position.fromStream(stream);
        this.world = world;
        this.isRemoved = false;
        this.animationController = createAnimationController();
        this.world.addEntity(this);
        this.world.worldPacketAnnouncer.announceEntityAdd(this);

        this.physicsBody = createPhysicsBody(world.getPhysicsWorld());
        if(this.physicsBody != null) {
            this.physicsBody.setTransform(position.x(), position.y(), this.physicsBody.getAngle());
            this.physicsBody.setUserData(this);
        }
    }
    public AnimationController getAnimationController() {
        return animationController;
    }
    protected AnimationController createAnimationController(){
        return new AnimationController(this);
    }
    protected Body createPhysicsBody(World world){
        return null;
    }
    public Body getPhysicsBody() {
        return physicsBody;
    }
    public void tick(){
        this.animationController.tick();
        if(this.physicsBody != null) {
            if(scheduledBodyPosition != null) {
                physicsBody.setTransform(scheduledBodyPosition.x(), scheduledBodyPosition.y(), physicsBody.getAngle());
                scheduledBodyPosition = null;
            }
            this.position = Position.fromVector2(physicsBody.getPosition());
            this.world.worldPacketAnnouncer.announceEntityMove(this);
            if(this instanceof ServerPlayerEntity serverPlayerEntity)
                serverPlayerEntity.updateCamera();
        }
    }
    private Position scheduledBodyPosition = null;
    public void teleport(Position newPosition){
        this.position = newPosition;
        if(this.physicsBody != null)
            scheduledBodyPosition = newPosition;
        this.world.worldPacketAnnouncer.announceEntityMove(this);
    }
    public void teleport(Position newPosition, ServerWorld newWorld){
        if(world.isRemoved())
            return;
        this.position = newPosition;
        if(this.physicsBody != null)
            scheduledBodyPosition = position;
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
        stream.writeLong(uuid.getMostSignificantBits());
        stream.writeLong(uuid.getLeastSignificantBits());
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
    public boolean shouldCollide(ServerEntity other){
        return true;
    }
    public void onCollision(ServerEntity other, Contact contact, boolean isA){}
    public void onEndCollision(ServerEntity other, Contact contact, boolean isA){}
    public boolean isRemoved() {
        return isRemoved;
    }

    public abstract Identifier getIdentifier();
}
