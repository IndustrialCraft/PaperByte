package com.github.industrialcraft.paperbyte;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.industrialcraft.identifier.Identifier;
import com.github.industrialcraft.paperbyte.common.util.Position;

public class ClientEntity {
    public final int entityId;
    public final Identifier type;
    public Position position;
    public ClientEntity(int entityId, Identifier type, Position position) {
        this.entityId = entityId;
        this.type = type;
        this.position = position;
    }
    public void render(SpriteBatch batch){
        //System.out.println("rendering: " + entityId + ":" + type.toString());
        PaperByteMain.FONT.draw(batch, ""+entityId, position.x() * PaperByteMain.METER_TO_PIXEL, position.y() * PaperByteMain.METER_TO_PIXEL);
        PaperByteMain.FONT.draw(batch, type.toString(), position.x() * PaperByteMain.METER_TO_PIXEL, (position.y()+1) * PaperByteMain.METER_TO_PIXEL);
    }
}
