package com.github.industrialcraft.paperbyte.common.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record Position(float x, float y) {
    public static Position fromStream(DataInputStream stream) throws IOException {
        return new Position(stream.readFloat(), stream.readFloat());
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeFloat(x);
        stream.writeFloat(y);
    }
}
