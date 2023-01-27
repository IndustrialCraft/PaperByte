package com.github.industrialcraft.paperbyte.server.world;

import com.github.industrialcraft.paperbyte.common.net.CameraUpdatePacket;
import com.github.industrialcraft.paperbyte.common.net.ClientInputPacket;
import com.github.industrialcraft.paperbyte.common.net.SetGUIPacket;
import com.github.industrialcraft.paperbyte.common.util.Position;
import com.github.industrialcraft.paperbyte.server.SocketUserData;
import com.github.industrialcraft.paperbyte.server.events.PlayerInputEvent;
import com.github.industrialcraft.paperbyte.server.events.SendGUIEvent;
import net.cydhra.eventsystem.EventManager;

public abstract class ServerPlayerEntity extends ServerEntity {
    public final SocketUserData socketUserData;
    private float cameraZoom;
    private boolean shouldResyncUI;
    private boolean sendHitBoxes;
    private ClientInputPacket lastInput;
    public ServerPlayerEntity(Position position, ServerWorld world, SocketUserData socketUserData) {
        super(position, world);
        this.socketUserData = socketUserData;
        this.cameraZoom = 1;
        markUIDirty();
        this.sendHitBoxes = false;
        this.lastInput = null;
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
    public boolean shouldSendHitBoxes() {
        return sendHitBoxes;
    }
    public void setSendHitBoxes(boolean sendHitBoxes) {
        this.sendHitBoxes = sendHitBoxes;
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
    public void handleClientInput(ClientInputPacket message){
        this.lastInput = message;
        PlayerInputEvent event = new PlayerInputEvent(getWorld().parent, this);
        EventManager.callEvent(event);
        event.firePacket(message);
    }
    public ClientInputPacket getLastInput() {
        return lastInput;
    }
}
