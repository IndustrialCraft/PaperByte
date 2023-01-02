package com.github.industrialcraft.paperbyte.server.world;

import com.github.industrialcraft.paperbyte.common.net.CameraUpdatePacket;
import com.github.industrialcraft.paperbyte.common.net.ClientInputPacket;
import com.github.industrialcraft.paperbyte.common.util.Position;
import com.github.industrialcraft.paperbyte.server.SocketUserData;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class ServerPlayerEntity extends ServerEntity {
    public final SocketUserData socketUserData;
    private float cameraZoom;
    public ServerPlayerEntity(Position position, ServerWorld world, SocketUserData socketUserData) {
        super(position, world);
        this.socketUserData = socketUserData;
        this.cameraZoom = 1;
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
