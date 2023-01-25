package com.github.industrialcraft.paperbyte.server;

import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.paperbyte.server.world.EntityRegistry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ClientDataBundler {
    private final HashMap<Identifier, ZipInputStream> renderData;
    private final HashMap<Identifier, InputStream> soundData;
    private final HashMap<Identifier, InputStream> imageData;
    private final GameServer server;
    private byte[] data;
    public ClientDataBundler(GameServer server) {
        this.server = server;
        this.renderData = new HashMap<>();
        this.soundData = new HashMap<>();
        this.imageData = new HashMap<>();
    }
    public void addEntity(Identifier id, EntityRegistry.EntityRegistryData registryData){
        if(renderData.containsKey(id))
            throw new IllegalStateException("entity " + id + " already registered for render data bundling");
        this.renderData.put(id, registryData.renderData().get());
    }
    public void addSound(Identifier id, Supplier<InputStream> sound){
        if(soundData.containsKey(id))
            throw new IllegalStateException("sound " + id + " already registered for sound data bundling");
        this.soundData.put(id, sound.get());
    }
    public void addImage(Identifier id, Supplier<InputStream> image){
        if(soundData.containsKey(id))
            throw new IllegalStateException("image " + id + " already registered for image data bundling");
        this.soundData.put(id, image.get());
    }
    public void compileData() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        createZip(stream);
        this.data = stream.toByteArray();
    }
    public byte[] getData() {
        return data;
    }
    private void createZip(OutputStream stream) throws IOException {
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
        for(var e : soundData.entrySet()){
            zip.putNextEntry(new ZipEntry("sounds/" + e.getKey() + ".wav"));
            e.getValue().transferTo(zip);
            zip.closeEntry();
        }
        for(var e : imageData.entrySet()){
            zip.putNextEntry(new ZipEntry("images/" + e.getKey() + ".png"));
            e.getValue().transferTo(zip);
            zip.closeEntry();
        }
        zip.close();
    }
}
