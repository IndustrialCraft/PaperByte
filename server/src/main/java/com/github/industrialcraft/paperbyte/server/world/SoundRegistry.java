package com.github.industrialcraft.paperbyte.server.world;

import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.paperbyte.server.ClientDataBundler;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SoundRegistry {
    private int networkIdGenerator;
    private boolean locked;
    private final HashMap<Identifier, Supplier<InputStream>> soundData;
    private final HashMap<Identifier,Integer> networkIds;
    private Map<Integer,Identifier> reversedNetworkIds;
    public SoundRegistry() {
        this.networkIdGenerator = 0;
        this.locked = false;
        this.soundData = new HashMap<>();
        this.networkIds = new HashMap<>();
    }
    public void lock(){
        if(locked)
            return;
        this.locked = true;
        this.reversedNetworkIds = Collections.unmodifiableMap(networkIds.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
    }
    public void register(Identifier id, Supplier<InputStream> data){
        if(locked)
            throw new IllegalStateException("registry already locked");
        if(soundData.containsKey(id))
            throw new IllegalStateException("sound " + id + " already registered");
        this.soundData.put(id, data);
        this.networkIds.put(id, networkIdGenerator);
        networkIdGenerator++;
    }
    public Map<Integer,Identifier> getRegisteredSounds(){
        return this.reversedNetworkIds;
    }
    public void registerToBundler(ClientDataBundler clientDataBundler){
        for(var e : soundData.entrySet()){
            clientDataBundler.addSound(e.getKey(), e.getValue());
        }
    }
}
