package com.github.industrialcraft.paperbyte.lwjgl3;

import com.github.industrialcraft.paperbyte.CustomAPI;
import org.lwjgl.openal.AL10;

public class LWJGLCustomApi implements CustomAPI {
    @Override
    public boolean isPlaying(int id) {
        return AL10.alGetSourcei(id, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }
}
