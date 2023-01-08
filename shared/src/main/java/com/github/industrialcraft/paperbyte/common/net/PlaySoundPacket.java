package com.github.industrialcraft.paperbyte.common.net;

import com.github.industrialcraft.netx.MessageRegistry;
import com.github.industrialcraft.paperbyte.common.util.Position;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlaySoundPacket {
    public final int soundId;
    public final int soundType;
    public final Position position;
    public PlaySoundPacket(int soundId, int soundType, Position position) {
        this.soundId = soundId;
        this.soundType = soundType;
        this.position = position;
    }
    public PlaySoundPacket(DataInputStream stream) throws IOException {
        this.soundId = stream.readInt();
        this.soundType = stream.readInt();
        this.position = Position.fromStream(stream);
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeInt(soundId);
        stream.writeInt(soundType);
        this.position.toStream(stream);
    }
    public static MessageRegistry.MessageDescriptor<PlaySoundPacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(PlaySoundPacket.class, stream -> new PlaySoundPacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
}
