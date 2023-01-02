package com.github.industrialcraft.paperbyte.server.testmod;

import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.physics.box2d.*;
import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.paperbyte.common.util.Position;
import com.github.industrialcraft.paperbyte.server.world.ServerEntity;
import com.github.industrialcraft.paperbyte.server.world.ServerWorld;

import java.io.DataInputStream;
import java.io.IOException;

public class TestPlatformEntity extends ServerEntity {
    public static final Identifier TEST_PLATFORM = Identifier.of("test", "platform");

    public TestPlatformEntity(Position position, ServerWorld world) {
        super(position, world);
    }
    public TestPlatformEntity(DataInputStream stream, ServerWorld world) throws IOException {
        super(stream, world);
    }

    @Override
    public Body createPhysicsBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1, 1);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        return body;
    }

    @Override
    public Identifier getIdentifier() {
        return TEST_PLATFORM;
    }
}
