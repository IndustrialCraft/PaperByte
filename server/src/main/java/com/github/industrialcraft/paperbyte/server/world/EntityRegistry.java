package com.github.industrialcraft.paperbyte.server.world;

import com.github.industrialcraft.identifier.Identifier;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityRegistry {
    private final HashMap<Identifier, BiFunction<DataInputStream,ServerWorld,ServerEntity>> entityConstructors;
    private final HashMap<Identifier, Integer> networkIds;
    private Map<Integer,Identifier> reversedNetworkIds;
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
        this.reversedNetworkIds = Collections.unmodifiableMap(networkIds.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
    }
    public void register(Identifier identifier, BiFunction<DataInputStream,ServerWorld,ServerEntity> creator){
        if(locked)
            throw new IllegalStateException("registry already locked");
        if(entityConstructors.containsKey(identifier))
            throw new IllegalStateException("Entity " + identifier + " already registered");
        this.entityConstructors.put(identifier, creator);
        this.networkIds.put(identifier, entityNetworkIdGenerator);
        entityNetworkIdGenerator++;
    }
    public ServerEntity createEntity(Identifier identifier, DataInputStream data, ServerWorld world){
        var creator = entityConstructors.get(identifier);
        if(creator == null)
            throw new IllegalStateException("Entity " + identifier + " is not registered");
        return creator.apply(data, world);
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
    public Map<Integer,Identifier> getRegisteredEntities(){
        return this.reversedNetworkIds;
    }
}
