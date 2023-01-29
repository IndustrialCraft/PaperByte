package com.github.industrialcraft.paperbyte.common.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

public record Range(float min, float max){
    public Range(DataInputStream stream) throws IOException {
        this(stream.readFloat(), stream.readFloat());
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeFloat(min);
        stream.writeFloat(max);
    }
    public float getRandom(Random random){
        return min + (random.nextFloat()*(max-min));
    }
}