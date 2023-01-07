package com.github.industrialcraft.paperbyte.server.world;

import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.paperbyte.common.util.Position;
import com.github.industrialcraft.paperbyte.server.RenderDataBundler;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class EntityRegistry {
    private final HashMap<Identifier, EntityRegistryData> entityData;
    private final HashMap<Identifier, Integer> networkIds;
    private Map<Integer,Identifier> reversedNetworkIds;
    private int entityNetworkIdGenerator;
    private boolean locked;
    public EntityRegistry() {
        this.entityData = new HashMap<>();
        this.networkIds = new HashMap<>();
        this.locked = false;
        this.entityNetworkIdGenerator = 0;
    }
    public void lock(){
        this.locked = true;
        this.reversedNetworkIds = Collections.unmodifiableMap(networkIds.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
    }
    public void register(Identifier identifier, EntityRegistryData creator){
        if(locked)
            throw new IllegalStateException("registry already locked");
        if(entityData.containsKey(identifier))
            throw new IllegalStateException("Entity " + identifier + " already registered");
        this.entityData.put(identifier, creator);
        this.networkIds.put(identifier, entityNetworkIdGenerator);
        entityNetworkIdGenerator++;
    }
    public ServerEntity createEntity(Identifier identifier, DataInputStream data, ServerWorld world){
        var entityData = this.entityData.get(identifier);
        if(entityData == null)
            throw new IllegalStateException("Entity " + identifier + " is not registered");
        try {
            return entityData.streamCreator().create(data, world);
        } catch (IOException e) {
            throw new IllegalStateException("Entity " + identifier + " couldnt be deserialized", e);
        }
    }
    public ServerEntity createEntity(Identifier identifier, Position position, ServerWorld world){
        var entityData = this.entityData.get(identifier);
        if(entityData == null)
            throw new IllegalStateException("Entity " + identifier + " is not registered");
        return entityData.positionCreator().apply(position, world);
    }
    public int resolveNetworkId(Identifier identifier){
        Integer entityId = this.networkIds.get(identifier);
        if(entityId == null)
            throw new IllegalStateException("Entity " + identifier + " is not registered");
        return entityId;
    }
    public void registerToBundler(RenderDataBundler renderDataBundler){
        for(var e : entityData.entrySet()){
            renderDataBundler.addEntity(e.getKey(), e.getValue());
        }
    }
    public int resolveNetworkId(ServerEntity entity){
        return resolveNetworkId(entity.getIdentifier());
    }
    public Map<Integer,Identifier> getRegisteredEntities(){
        return this.reversedNetworkIds;
    }
    public record EntityRegistryData(EntityFromStreamCreator streamCreator, BiFunction<Position,ServerWorld,ServerEntity> positionCreator, Supplier<ZipInputStream> renderData){

    }
    public interface EntityFromStreamCreator{
        ServerEntity create(DataInputStream stream, ServerWorld world) throws IOException;
    }
}
