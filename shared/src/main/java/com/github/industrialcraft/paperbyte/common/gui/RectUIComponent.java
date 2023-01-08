package com.github.industrialcraft.paperbyte.common.gui;

import com.badlogic.gdx.graphics.Color;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RectUIComponent extends BasicUIComponent{
    public static final byte SER_ID = 1;

    public final float width;
    public final float height;
    public RectUIComponent(Color color, float x, float y, float width, float height) {
        super(color, x, y);
        this.width = width;
        this.height = height;
    }
    public RectUIComponent(DataInputStream stream) throws IOException {
        super(stream);
        this.width = stream.readFloat();
        this.height = stream.readFloat();
    }
    @Override
    public void toStream(DataOutputStream stream) throws IOException {
        super.toStream(stream);
        stream.writeFloat(width);
        stream.writeFloat(height);
    }
    @Override
    public byte getSerializationID() {
        return SER_ID;
    }
}
