package com.github.industrialcraft.paperbyte;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.industrialcraft.folder.Node;
import com.github.industrialcraft.folder.Transform;
import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.paperbyte.common.util.Position;

import java.util.Map;

public class ClientEntity {
    public final int entityId;
    public final Identifier type;
    public Position position;
    private String animation;
    private float time;
    public ClientEntity(int entityId, Identifier type, Position position) {
        this.entityId = entityId;
        this.type = type;
        this.position = position;
        this.animation = "default";
        this.time = 0;
    }
    public void render(SpriteBatch batch, Map<Identifier, Node> entityNodes){
        if(entityNodes.containsKey(type)){
            entityNodes.get(type).drawRecursively(new Transform(position.x()*PaperByteMain.METER_TO_PIXEL, position.y()*PaperByteMain.METER_TO_PIXEL, 0, 1, 1), batch, animation, time);
        } else {
            //System.out.println("rendering: " + entityId + ":" + type.toString());
            PaperByteMain.FONT.draw(batch, "" + entityId, position.x() * PaperByteMain.METER_TO_PIXEL, position.y() * PaperByteMain.METER_TO_PIXEL);
            PaperByteMain.FONT.draw(batch, type.toString(), position.x() * PaperByteMain.METER_TO_PIXEL, (position.y() + 1) * PaperByteMain.METER_TO_PIXEL);
        }
        time += Gdx.graphics.getDeltaTime();
    }
    public void setAnimation(int animation){
        //set animation string
        this.time = 0;
    }
}
