package com.github.industrialcraft.paperbyte.server.testmod;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
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

    private ClientInputPacket lastInput;
    private boolean isOnGround;
    public TestPlayer(Position position, ServerWorld world, SocketUserData socketUserData) {
        super(position, world, socketUserData);
        this.lastInput = null;
    }
    @Override
    public void tick() {
        super.tick();
        if(lastInput != null) {
            float speed = 1f;
            float xMod = 0;
            float yMod = 0;
            if (lastInput.isKeyDown('w')) {
                yMod++;
            }
            if (lastInput.isKeyDown('a')) {
                xMod--;
            }
            if (lastInput.isKeyDown('d')) {
                xMod++;
            }
            if (xMod != 0 || yMod != 0) {
                getPhysicsBody().applyLinearImpulse(new Vector2(xMod * speed, yMod * speed * (isOnGround?15:0)), getPhysicsBody().getWorldCenter(), true);
            }
        }
    }
    @Override
    public void handleClientInput(ClientInputPacket message) {
        this.lastInput = message;
    }
    @Override
    public Body createPhysicsBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1, 1);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        FixtureDef sensor = new FixtureDef();
        sensor.isSensor = true;
        PolygonShape shape1 = new PolygonShape();
        shape1.setAsBox(0.95f, 0.1f, new Vector2(0f, -1.1f), 0);
        sensor.shape = shape1;
        body.createFixture(sensor);
        return body;
    }
    public boolean isOnGround() {
        return isOnGround;
    }
    public void setOnGround(boolean onGround) {
        isOnGround = onGround;
    }
    @Override
    public Identifier getIdentifier() {
        return TEST_PLAYER;
    }
}
