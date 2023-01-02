package com.github.industrialcraft.paperbyte.server.testmod;

import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.paperbyte.common.util.Position;
import com.github.industrialcraft.paperbyte.server.world.ServerEntity;
import com.github.industrialcraft.paperbyte.server.world.ServerWorld;

import java.io.DataInputStream;
import java.io.IOException;

public class PlatformEntity extends ServerEntity {
    public PlatformEntity(Position position, ServerWorld world) {
        super(position, world);
    }
    public PlatformEntity(DataInputStream stream, ServerWorld world) throws IOException {
        super(stream, world);
    }
    @Override
    public Identifier getIdentifier() {
        return null;
    }
}
