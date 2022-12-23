package com.github.industrialcraft.paperbyte.common.net;

import com.badlogic.gdx.Input;
import com.github.industrialcraft.netx.MessageRegistry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientInputPacket {
    public static final int KEYS[] = {
            Input.Keys.A,
            Input.Keys.B,
            Input.Keys.C,
            Input.Keys.D,
            Input.Keys.E,
            Input.Keys.F,
            Input.Keys.G,
            Input.Keys.H,
            Input.Keys.I,
            Input.Keys.J,
            Input.Keys.K,
            Input.Keys.L,
            Input.Keys.M,
            Input.Keys.N,
            Input.Keys.O,
            Input.Keys.P,
            Input.Keys.Q,
            Input.Keys.R,
            Input.Keys.S,
            Input.Keys.T,
            Input.Keys.U,
            Input.Keys.V,
            Input.Keys.W,
            Input.Keys.X,
            Input.Keys.Y,
            Input.Keys.Z,
    };
    public final float worldMouseX;
    public final float worldMouseY;
    public final int screenMouseX;
    public final int screenMouseY;
    public final int screenSizeX;
    public final int screenSizeY;
    public final boolean isMouse1;
    public final boolean isMouse2;
    public final boolean isMouse3;
    public final boolean[] keys;
    public ClientInputPacket(float worldMouseX, float worldMouseY, int screenMouseX, int screenMouseY, int screenSizeX, int screenSizeY, boolean isMouse1, boolean isMouse2, boolean isMouse3, boolean[] keys) {
        this.worldMouseX = worldMouseX;
        this.worldMouseY = worldMouseY;
        this.screenMouseX = screenMouseX;
        this.screenMouseY = screenMouseY;
        this.screenSizeX = screenSizeX;
        this.screenSizeY = screenSizeY;
        this.isMouse1 = isMouse1;
        this.isMouse2 = isMouse2;
        this.isMouse3 = isMouse3;
        this.keys = keys;
    }
    public ClientInputPacket(DataInputStream stream) throws IOException {
        this.worldMouseX = stream.readFloat();
        this.worldMouseY = stream.readFloat();
        this.screenMouseX = stream.readInt();
        this.screenMouseY = stream.readInt();
        this.screenSizeX = stream.readInt();
        this.screenSizeY = stream.readInt();
        this.isMouse1 = stream.readBoolean();
        this.isMouse2 = stream.readBoolean();
        this.isMouse3 = stream.readBoolean();
        int keysSize = stream.readInt();
        this.keys = new boolean[keysSize];
        for(int i = 0;i < keysSize;i++)
            this.keys[i] = stream.readBoolean();
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeFloat(worldMouseX);
        stream.writeFloat(worldMouseY);
        stream.writeInt(screenMouseX);
        stream.writeInt(screenMouseY);
        stream.writeInt(screenSizeX);
        stream.writeInt(screenSizeY);
        stream.writeBoolean(isMouse1);
        stream.writeBoolean(isMouse2);
        stream.writeBoolean(isMouse3);
        stream.writeInt(keys.length);
        for(int i = 0;i < keys.length;i++)
            stream.writeBoolean(keys[i]);
    }
    public static MessageRegistry.MessageDescriptor<ClientInputPacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(ClientInputPacket.class, stream -> new ClientInputPacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
}
