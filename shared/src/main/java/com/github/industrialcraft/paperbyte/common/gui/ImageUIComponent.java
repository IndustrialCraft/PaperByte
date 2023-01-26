package com.github.industrialcraft.paperbyte.common.gui;

import com.badlogic.gdx.graphics.Color;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ImageUIComponent extends BasicUIComponent{
    public static final byte SER_ID = 3;

    public final float width;
    public final float height;
    public final Image image;
    public ImageUIComponent(Color color, float x, float y, float width, float height, Image image) {
        super(color, x, y);
        this.width = width;
        this.height = height;
        this.image = image;
    }
    public ImageUIComponent(float x, float y, float width, float height, Image image) {
        this(Color.WHITE, x, y, width, height, image);
    }
    public ImageUIComponent(DataInputStream stream) throws IOException {
        super(stream);
        this.width = stream.readFloat();
        this.height = stream.readFloat();
        this.image = new Image(stream.readInt());
    }
    @Override
    public void toStream(DataOutputStream stream) throws IOException {
        super.toStream(stream);
        stream.writeFloat(width);
        stream.writeFloat(height);
        stream.writeInt(image.netId);
    }
    @Override
    public byte getSerializationID() {
        return SER_ID;
    }

    public record Image(int netId){}
}
