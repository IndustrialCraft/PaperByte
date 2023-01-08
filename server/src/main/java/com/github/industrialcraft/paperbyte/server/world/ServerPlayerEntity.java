package com.github.industrialcraft.paperbyte.server.world;

import com.github.industrialcraft.paperbyte.common.net.CameraUpdatePacket;
import com.github.industrialcraft.paperbyte.common.net.ClientInputPacket;
import com.github.industrialcraft.paperbyte.common.net.SetGUIPacket;
import com.github.industrialcraft.paperbyte.common.util.Position;
import com.github.industrialcraft.paperbyte.server.SocketUserData;
import com.github.industrialcraft.paperbyte.server.events.SendGUIEvent;
import net.cydhra.eventsystem.EventManager;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class ServerPlayerEntity extends ServerEntity {
    public final SocketUserData socketUserData;
    private float cameraZoom;
    private boolean shouldResyncUI;
    public ServerPlayerEntity(Position position, ServerWorld world, SocketUserData socketUserData) {
        super(position, world);
        this.socketUserData = socketUserData;
        this.cameraZoom = 1;
        markUIDirty();
    }

    @Override
    public void tick() {
        super.tick();
        if(shouldResyncUI){
            SetGUIPacket packet = new SetGUIPacket();
            EventManager.callEvent(new SendGUIEvent(getWorld().parent, this, packet));
            socketUserData.socketUser.send(packet, true);
            shouldResyncUI = false;
        }
    }

    @Override
    public void teleport(Position newPosition) {
        super.teleport(newPosition);
        updateCamera();
    }
    @Override
    public void teleport(Position newPosition, ServerWorld newWorld) {
        super.teleport(newPosition, newWorld);
        updateCamera();
    }
    public void markUIDirty(){
        this.shouldResyncUI = true;
    }
    public float getCameraZoom() {
        return cameraZoom;
    }
    public void setCameraZoom(float cameraZoom) {
        this.cameraZoom = cameraZoom;
        updateCamera();
    }
    public void updateCamera(){
        Position pos = getPosition();
        socketUserData.socketUser.send(new CameraUpdatePacket(pos.x(), pos.y(), cameraZoom), false);
    }
    public abstract void handleClientInput(ClientInputPacket message);
}
