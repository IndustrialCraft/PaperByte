package com.github.industrialcraft.paperbyte.server;

import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.paperbyte.common.gui.ImageUIComponent;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.HashMap;

public class ImageRegistry {
    private int idGenerator;
    private HashMap<Identifier,Integer> identifierNetIdMap;
    private HashMap<Identifier, InputStream> imageDataMap;
    private boolean locked;
    public ImageRegistry() {
        this.idGenerator = 0;
        this.identifierNetIdMap = new HashMap<>();
        this.imageDataMap = new HashMap<>();
        this.locked = false;
    }
    public void lock(){
        this.locked = true;
    }
    public ImageUIComponent.Image register(Identifier identifier, InputStream stream){
        if(locked)
            throw new IllegalStateException("registry locked");
        if(identifierNetIdMap.containsKey(identifier))
            throw new IllegalStateException("image " + identifier + " already registered");
        int netId = idGenerator++;
        identifierNetIdMap.put(identifier, netId);
        imageDataMap.put(identifier, stream);
        return new ImageUIComponent.Image(netId);
    }
    public void registerToBundler(ClientDataBundler clientDataBundler){
        for(var e : imageDataMap.entrySet()){
            clientDataBundler.addImage(e.getKey(), e::getValue);
        }
    }
    public ImageUIComponent.Image get(Identifier id){
        Integer netId = identifierNetIdMap.get(id);
        if(netId == null)
            return null;
        return new ImageUIComponent.Image(netId);
    }
}
