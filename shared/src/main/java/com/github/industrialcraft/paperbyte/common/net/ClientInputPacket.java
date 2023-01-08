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
    public final float scrollX;
    public final float scrollY;
    public final boolean isMouse1;
    public final boolean isMouse2;
    public final boolean isMouse3;
    public final boolean[] keys;
    public final int[] typed;
    private int readerIndex = 0;
    public ClientInputPacket(float worldMouseX, float worldMouseY, int screenMouseX, int screenMouseY, int screenSizeX, int screenSizeY, float scrollX, float scrollY, boolean isMouse1, boolean isMouse2, boolean isMouse3, boolean[] keys, int[] typed) {
        this.worldMouseX = worldMouseX;
        this.worldMouseY = worldMouseY;
        this.screenMouseX = screenMouseX;
        this.screenMouseY = screenMouseY;
        this.screenSizeX = screenSizeX;
        this.screenSizeY = screenSizeY;
        this.scrollX = scrollX;
        this.scrollY = scrollY;
        this.isMouse1 = isMouse1;
        this.isMouse2 = isMouse2;
        this.isMouse3 = isMouse3;
        this.keys = keys;
        this.typed = typed;
    }
    public ClientInputPacket(DataInputStream stream) throws IOException {
        this.worldMouseX = stream.readFloat();
        this.worldMouseY = stream.readFloat();
        this.screenMouseX = stream.readInt();
        this.screenMouseY = stream.readInt();
        this.screenSizeX = stream.readInt();
        this.screenSizeY = stream.readInt();
        this.scrollX = stream.readFloat();
        this.scrollY = stream.readFloat();
        this.isMouse1 = stream.readBoolean();
        this.isMouse2 = stream.readBoolean();
        this.isMouse3 = stream.readBoolean();
        this.keys = new boolean[stream.readInt()];
        for(int i = 0;i < keys.length;i++)
            this.keys[i] = stream.readBoolean();
        this.typed = new int[stream.readInt()];
        for(int i = 0;i < typed.length;i++){
            this.typed[i] = stream.readInt();
        }
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeFloat(worldMouseX);
        stream.writeFloat(worldMouseY);
        stream.writeInt(screenMouseX);
        stream.writeInt(screenMouseY);
        stream.writeInt(screenSizeX);
        stream.writeInt(screenSizeY);
        stream.writeFloat(scrollX);
        stream.writeFloat(scrollY);
        stream.writeBoolean(isMouse1);
        stream.writeBoolean(isMouse2);
        stream.writeBoolean(isMouse3);
        stream.writeInt(keys.length);
        for(int i = 0;i < keys.length;i++)
            stream.writeBoolean(keys[i]);
        stream.writeInt(typed.length);
        for(int i = 0;i < typed.length;i++)
            stream.writeInt(typed[i]);
    }
    public boolean isKeyDown(char ch){
        return keys[getKey(ch)];
    }
    public static int getKey(char ch){
        ch = Character.toLowerCase(ch);
        if(Character.isLetter(ch))
            return ch-'a';
        return -1;
    }
    public boolean readTyped(Visitor visitor){
        if(readerIndex >= typed.length)
            return false;
        int val = typed[readerIndex];
        if((val & (1<<31)) != 0){
            visitor.keyTyped((char)val);
        } else if((val & (1<<30)) != 0){
            visitor.keyUp(val & ~(1<<30));
        } else {
            visitor.keyDown(val);
        }
        readerIndex++;
        return true;
    }
    public interface Visitor{
        void keyTyped(char ch);
        void keyDown(int keycode);
        void keyUp(int keycode);
    }
    public static MessageRegistry.MessageDescriptor<ClientInputPacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(ClientInputPacket.class, stream -> new ClientInputPacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
}
