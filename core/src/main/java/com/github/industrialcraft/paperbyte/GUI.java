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
        this.camera = new OrthographicCamera(2000, 2000);
        this.shapeRenderer.setProjectionMatrix(camera.combined);
        this.spriteBatch.setProjectionMatrix(camera.combined);
        this.shapeRenderer.setAutoShapeType(true);
        this.font = new BitmapFont();
        this.uiComponents = Collections.EMPTY_LIST;
    }
    public void draw(){
        for(BasicUIComponent component : uiComponents){
            if(component instanceof RectUIComponent rectUIComponent){
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(rectUIComponent.color);
                shapeRenderer.rect(rectUIComponent.x, rectUIComponent.y, rectUIComponent.width, rectUIComponent.height);
                shapeRenderer.end();
            }
            if(component instanceof TextUIComponent textUIComponent){
                spriteBatch.begin();
                spriteBatch.setColor(textUIComponent.color);
                font.getData().setScale(textUIComponent.scale);
                font.draw(spriteBatch, textUIComponent.text, textUIComponent.x, textUIComponent.y);
                font.getData().setScale(1/textUIComponent.scale);
                spriteBatch.end();
            }
        }
    }
    public void fromPacket(SetGUIPacket packet){
        this.uiComponents = packet.uiComponents.stream().toList();
    }
}
