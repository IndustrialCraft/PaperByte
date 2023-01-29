package com.github.industrialcraft.paperbyte.common.net;

import com.github.industrialcraft.netx.MessageRegistry;
import com.github.industrialcraft.paperbyte.common.util.Range;
import com.github.industrialcraft.paperbyte.common.gui.ImageUIComponent;
import com.github.industrialcraft.paperbyte.common.util.Position;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParticleSystemPacket {
    public final ImageUIComponent.Image image;
    public final Position position;
    public final int count;
    public final Range velocityX;
    public final Range velocityY;
    public final float gravity;
    public final Range lifetime;
    public ParticleSystemPacket(ImageUIComponent.Image image, Position position, int count, Range velocityX, Range velocityY, float gravity, Range lifetime) {
        this.image = image;
        this.position = position;
        this.count = count;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.gravity = gravity;
        this.lifetime = lifetime;
    }
    public ParticleSystemPacket(DataInputStream stream) throws IOException {
        this.image = new ImageUIComponent.Image(stream.readInt());
        this.position = Position.fromStream(stream);
        this.count = stream.readInt();
        this.velocityX = new Range(stream);
        this.velocityY = new Range(stream);
        this.gravity = stream.readFloat();
        this.lifetime = new Range(stream);
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeInt(image.netId());
        this.position.toStream(stream);
        stream.writeInt(count);
        this.velocityX.toStream(stream);
        this.velocityY.toStream(stream);
        stream.writeFloat(gravity);
        this.lifetime.toStream(stream);
    }
    public static MessageRegistry.MessageDescriptor<ParticleSystemPacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(ParticleSystemPacket.class, stream -> new ParticleSystemPacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
}
