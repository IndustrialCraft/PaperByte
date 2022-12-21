package com.github.industrialcraft.paperbyte.common.net;

import com.github.industrialcraft.netx.MessageRegistry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CameraUpdatePacket {
    public final float x;
    public final float y;
    public final float zoom;
    public CameraUpdatePacket(float x, float y, float zoom) {
        this.x = x;
        this.y = y;
        this.zoom = zoom;
    }
    public CameraUpdatePacket(DataInputStream stream) throws IOException {
        this.x = stream.readFloat();
        this.y = stream.readFloat();
        this.zoom = stream.readFloat();
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeFloat(x);
        stream.writeFloat(y);
        stream.writeFloat(zoom);
    }
    public static MessageRegistry.MessageDescriptor<CameraUpdatePacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(CameraUpdatePacket.class, stream -> new CameraUpdatePacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
}
