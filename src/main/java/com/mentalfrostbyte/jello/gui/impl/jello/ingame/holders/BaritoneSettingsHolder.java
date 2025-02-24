package com.mentalfrostbyte.jello.gui.impl.jello.ingame.holders;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

public class BaritoneSettingsHolder extends Screen {

    public BaritoneSettingsHolder(ITextComponent var1) {
        super(var1);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
