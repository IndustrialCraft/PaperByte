package com.github.industrialcraft.paperbyte;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.github.industrialcraft.paperbyte.common.net.ParticleSystemPacket;

import java.util.ArrayList;

public class ParticleSystem {
    public final PaperByteMain main;
    public final ParticleSystemPacket packet;
    private ArrayList<Particle> particles;
    public ParticleSystem(PaperByteMain main, ParticleSystemPacket packet) {
        this.main = main;
        this.packet = packet;
        this.particles = new ArrayList<>();
        for(int i = 0;i < packet.count;i++){
            this.particles.add(new Particle());
        }
    }
    public void render(SpriteBatch batch){
        this.particles.removeIf(particle -> particle.lifetime <= 0);
        for(var particle : particles){
            particle.render(batch);
        }
    }
    public boolean shouldRemove(){
        return particles.size() == 0;
    }
    public class Particle{
        private Vector2 position;
        private Vector2 velocity;
        private float lifetime;
        public Particle() {
            this.position = packet.position.toVector();
            this.velocity = new Vector2(packet.velocityX.getRandom(main.getRandom()), packet.velocityY.getRandom(main.getRandom()));
            this.lifetime = packet.lifetime.getRandom(main.getRandom());
        }
        public void render(SpriteBatch batch){
            float deltaTime = Gdx.graphics.getDeltaTime();
            position.x += velocity.x*deltaTime;
            position.y += velocity.y*deltaTime;
            velocity.y -= packet.gravity*deltaTime;
            lifetime = Math.max(0, lifetime - deltaTime);
            batch.setColor(1, 1, 1, Math.min(lifetime, 1));
            batch.draw(main.getImageTextures().get(main.getImagesNetId().get(packet.image.netId())), position.x * PaperByteMain.METER_TO_PIXEL, position.y * PaperByteMain.METER_TO_PIXEL);
        }
    }
}
