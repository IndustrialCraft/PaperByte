package com.github.industrialcraft.paperbyte;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.industrialcraft.paperbyte.common.gui.BasicUIComponent;
import com.github.industrialcraft.paperbyte.common.gui.RectUIComponent;
import com.github.industrialcraft.paperbyte.common.gui.TextUIComponent;
import com.github.industrialcraft.paperbyte.common.net.SetGUIPacket;

import java.util.Collections;
import java.util.List;

public class GUI {
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private Camera camera;
    private List<BasicUIComponent> uiComponents;
    private BitmapFont font;
    public GUI() {
        this.shapeRenderer = new ShapeRenderer();
        this.spriteBatch = new SpriteBatch();
        this.camera = new OrthographicCamera(200, 200);
        this.shapeRenderer.setProjectionMatrix(camera.combined);
        this.spriteBatch.setProjectionMatrix(camera.combined);
        this.shapeRenderer.setAutoShapeType(true);
        this.font = new BitmapFont();
        this.uiComponents = Collections.EMPTY_LIST;
    }
    public void draw(){
        spriteBatch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(BasicUIComponent component : uiComponents){
            if(component instanceof RectUIComponent rectUIComponent){
                shapeRenderer.setColor(rectUIComponent.color);
                shapeRenderer.rect(rectUIComponent.x, rectUIComponent.y, rectUIComponent.width, rectUIComponent.height);
            }
            if(component instanceof TextUIComponent textUIComponent){
                spriteBatch.setColor(textUIComponent.color);
                font.draw(spriteBatch, textUIComponent.text, textUIComponent.x, textUIComponent.y);
            }
        }
        spriteBatch.end();
        shapeRenderer.end();
    }
    public void fromPacket(SetGUIPacket packet){
        this.uiComponents = packet.uiComponents.stream().toList();
    }
}
