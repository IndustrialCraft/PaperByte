package com.github.industrialcraft.paperbyte.server.testmod;

import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.paperbyte.common.net.ClientInputPacket;
import com.github.industrialcraft.paperbyte.common.util.Position;
import com.github.industrialcraft.paperbyte.server.SocketUserData;
import com.github.industrialcraft.paperbyte.server.world.ServerPlayerEntity;
import com.github.industrialcraft.paperbyte.server.world.ServerWorld;

import java.io.DataInputStream;
import java.io.IOException;

public class TestPlayer extends ServerPlayerEntity {
    public static final Identifier TEST_PLAYER = Identifier.of("test", "player");

    public TestPlayer(Position position, ServerWorld world, SocketUserData socketUserData) {
        super(position, world, socketUserData);
    }
    public TestPlayer(DataInputStream stream, ServerWorld world, SocketUserData socketUserData) throws IOException {
        super(stream, world, socketUserData);
    }
    @Override
    public void handleClientInput(ClientInputPacket message) {
        float speed = 0.05f;
        float xMod = 0;
        float yMod = 0;
        if(message.isKeyDown('w')){
            yMod++;
        }
        if(message.isKeyDown('s')){
            yMod--;
        }
        if(message.isKeyDown('a')){
            xMod--;
        }
        if(message.isKeyDown('d')){
            xMod++;
        }
        if(xMod != 0 || yMod != 0){
            teleport(getPosition().add(xMod*speed, yMod*speed));
        }
    }
    @Override
    public Identifier getIdentifier() {
        return TEST_PLAYER;
    }
}
