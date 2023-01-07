package com.github.industrialcraft.paperbyte.server;

import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.paperbyte.server.world.EntityRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class RenderDataBundler {
    private final HashMap<Identifier, ZipInputStream> renderData;
    private final GameServer server;
    public RenderDataBundler(GameServer server) {
        this.server = server;
        this.renderData = new HashMap<>();
    }
    public void addEntity(Identifier id, EntityRegistry.EntityRegistryData registryData){
        if(renderData.containsKey(id))
            throw new IllegalStateException("entity " + id + " already registered for render data bundling");
        this.renderData.put(id, registryData.renderData().get());
    }
    public void createZip(OutputStream stream) throws IOException {
        ZipOutputStream zip = new ZipOutputStream(stream);
        for(var e : renderData.entrySet()){
            if(e.getValue() == null) {
                server.logger.warn("Entity %s registered without renderdata", e.getKey());
                continue;
            }
            ZipEntry entry;
            while((entry = e.getValue().getNextEntry()) != null){
                zip.putNextEntry(new ZipEntry(e.getKey() + "/" + entry.getName()));
                e.getValue().transferTo(zip);
                zip.closeEntry();
            }
        }
        zip.close();
    }
}
