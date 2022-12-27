package com.github.industrialcraft.paperbyte.common.util;

import com.badlogic.gdx.math.Vector2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record Position(float x, float y) {
    public Position add(float x, float y){
        return new Position(this.x+x, this.y+y);
    }
    public static Position fromVector2(Vector2 vector){
        return new Position(vector.x, vector.y);
    }
    public static Position fromStream(DataInputStream stream) throws IOException {
        return new Position(stream.readFloat(), stream.readFloat());
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeFloat(x);
        stream.writeFloat(y);
    }
}
