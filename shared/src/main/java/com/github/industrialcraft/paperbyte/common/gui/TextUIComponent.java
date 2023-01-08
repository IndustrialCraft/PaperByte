package com.github.industrialcraft.paperbyte.common.gui;

import com.badlogic.gdx.graphics.Color;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TextUIComponent extends BasicUIComponent{
    public static final byte SER_ID = 2;

    public final String text;
    public TextUIComponent(Color color, float x, float y, String text) {
        super(color, x, y);
        this.text = text;
    }
    public TextUIComponent(DataInputStream stream) throws IOException {
        super(stream);
        this.text = stream.readUTF();
    }
    @Override
    public void toStream(DataOutputStream stream) throws IOException {
        super.toStream(stream);
        stream.writeUTF(text);
    }

    @Override
    public byte getSerializationID() {
        return SER_ID;
    }
}
