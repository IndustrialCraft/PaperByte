package com.github.industrialcraft.paperbyte.server.world;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AnimationController {
    public final String defaultAnimation;
    public final ServerEntity parent;
    private String currentAnimation;
    private int currentAnimationLength;
    private Consumer<AnimationController> chooseNextCallback;
    private Runnable finishCallback;
    public AnimationController(ServerEntity parent){
        this("default", parent);
    }
    public AnimationController(String defaultAnimation, ServerEntity parent) {
        this.defaultAnimation = defaultAnimation;
        this.parent = parent;
        this.currentAnimation = defaultAnimation;
        this.currentAnimationLength = 0;
        this.chooseNextCallback = null;
        this.finishCallback = null;
    }
    public void tick(){
        if(currentAnimationLength > 0){
            currentAnimationLength--;
            if(currentAnimationLength == 0){
                this.currentAnimation = defaultAnimation;
                var fc = finishCallback;
                var cc = chooseNextCallback;
                this.finishCallback = null;
                this.chooseNextCallback = null;
                if(fc != null)
                    fc.run();
                if(cc != null)
                    cc.accept(this);
            }
        }
    }
    public String getCurrentAnimation() {
        return currentAnimation;
    }
    public void clearAnimation(){
        this.currentAnimation = defaultAnimation;
        var fc = finishCallback;
        var cc = chooseNextCallback;
        this.finishCallback = null;
        this.chooseNextCallback = null;
        if(fc != null)
            fc.run();
        if(cc != null)
            cc.accept(this);
    }
    public void setAnimation(String newAnimation, int length, Consumer<AnimationController> chooseNextCallback, Runnable finishCallback){
        if(this.finishCallback != null)
            this.finishCallback.run();
        this.currentAnimation = newAnimation;
        this.currentAnimationLength = length;
        this.chooseNextCallback = chooseNextCallback;
        this.finishCallback = finishCallback;
        parent.getWorld().worldPacketAnnouncer.announceEntityAnimation(parent, currentAnimation);
    }
}
