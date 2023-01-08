package com.github.industrialcraft.paperbyte.common.gui;

import com.badlogic.gdx.graphics.Color;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class BasicUIComponent {
    public final Color color;
    public final float x;
    public final float y;
    public BasicUIComponent(Color color, float x, float y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }
    public BasicUIComponent(DataInputStream stream) throws IOException {
        this.color = new Color(stream.readInt());
        this.x = stream.readFloat();
        this.y = stream.readFloat();
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeInt(Color.rgba8888(this.color));
        stream.writeFloat(x);
        stream.writeFloat(y);
    }
    public static BasicUIComponent createFromStream(byte id, DataInputStream stream) throws IOException {
        return switch (id){
            case RectUIComponent.SER_ID -> new RectUIComponent(stream);
            case TextUIComponent.SER_ID -> new TextUIComponent(stream);
            default -> throw new IllegalStateException("ui component " + id + " not found");
        };
    }
    public abstract byte getSerializationID();
}
