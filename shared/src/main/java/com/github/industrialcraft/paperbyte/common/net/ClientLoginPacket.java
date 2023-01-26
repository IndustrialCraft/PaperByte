package com.github.industrialcraft.paperbyte.common.net;

import com.github.industrialcraft.netx.MessageRegistry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientLoginPacket {
    public final String locale;
    public ClientLoginPacket(String locale) {
        this.locale = locale;
    }
    public ClientLoginPacket(DataInputStream stream) throws IOException {
        this.locale = stream.readUTF();
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeUTF(locale);
    }
    public static MessageRegistry.MessageDescriptor<ClientLoginPacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(ClientLoginPacket.class, stream -> new ClientLoginPacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
}
