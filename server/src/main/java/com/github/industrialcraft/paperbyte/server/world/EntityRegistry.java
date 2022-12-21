package com.github.industrialcraft.paperbyte.server.world;

import com.github.industrialcraft.identifier.Identifier;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class EntityRegistry {
    private final HashMap<Identifier, Function<DataInputStream,ServerEntity>> entityConstructors;
    private final HashMap<Identifier, Integer> networkIds;
    private int entityNetworkIdGenerator;
    private boolean locked;
    public EntityRegistry() {
        this.entityConstructors = new HashMap<>();
        this.networkIds = new HashMap<>();
        this.locked = false;
        this.entityNetworkIdGenerator = 0;
    }
    public void lock(){
        this.locked = true;
    }
    public void register(Identifier identifier, Function<DataInputStream,ServerEntity> creator){
        if(locked)
            throw new IllegalStateException("registry already locked");
        if(entityConstructors.containsKey(identifier))
            throw new IllegalStateException("Entity " + identifier + " already registered");
        this.entityConstructors.put(identifier, creator);
        this.networkIds.put(identifier, entityNetworkIdGenerator);
        entityNetworkIdGenerator++;
    }
    public ServerEntity createEntity(Identifier identifier, DataInputStream data){
        var creator = entityConstructors.get(identifier);
        if(creator == null)
            throw new IllegalStateException("Entity " + identifier + " is not registered");
        return creator.apply(data);
    }
    public int resolveNetworkId(Identifier identifier){
        Integer entityId = this.networkIds.get(identifier);
        if(entityId == null)
            throw new IllegalStateException("Entity " + identifier + " is not registered");
        return entityId;
    }
    public int resolveNetworkId(ServerEntity entity){
        return resolveNetworkId(entity.getIdentifier());
    }
}
