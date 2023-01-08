package com.github.industrialcraft.paperbyte.common.net;

import com.github.industrialcraft.netx.MessageRegistry;
import com.github.industrialcraft.paperbyte.common.gui.BasicUIComponent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SetGUIPacket {
    public final ArrayList<BasicUIComponent> uiComponents;
    public SetGUIPacket() {
        this.uiComponents = new ArrayList<>();
    }
    public SetGUIPacket(DataInputStream stream) throws IOException {
        this.uiComponents = new ArrayList<>();
        int size = stream.readInt();
        for(int i = 0;i < size;i++){
            addUIComponent(BasicUIComponent.createFromStream(stream.readByte(), stream));
        }
    }
    public void addUIComponent(BasicUIComponent component){
        this.uiComponents.add(component);
    }
    public void toStream(DataOutputStream stream) throws IOException {
        stream.writeInt(uiComponents.size());
        for(BasicUIComponent component : uiComponents){
            stream.writeByte(component.getSerializationID());
            component.toStream(stream);
        }
    }
    public static MessageRegistry.MessageDescriptor<SetGUIPacket> createDescriptor(){
        return new MessageRegistry.MessageDescriptor<>(SetGUIPacket.class, stream -> new SetGUIPacket(new DataInputStream(stream)), (obj, stream) -> obj.toStream(new DataOutputStream(stream)));
    }
}
