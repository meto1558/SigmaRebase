package com.mentalfrostbyte.jello.module.impl.gui.jello.tabgui;

import com.mentalfrostbyte.jello.gui.base.animations.Animation;

public class AnimationEntry {
    public boolean isModuleList;
    public Animation animation;

    public AnimationEntry(boolean isModuleList) {
        this.animation = new Animation(250, 0);
        this.isModuleList = isModuleList;
    }
}
