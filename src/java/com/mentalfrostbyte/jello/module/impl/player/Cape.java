package com.mentalfrostbyte.jello.module.impl.player;

import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class Cape extends Module {

    private final Minecraft mc = Minecraft.getInstance();

    public Cape() {
        super(ModuleCategory.PLAYER, "Cape", "gives you a cape (wow)");
    }

    public ResourceLocation getCape() {
        return new ResourceLocation("com/mentalfrostbyte/gui/resources/jello/capes/cape.png");
    }

    public boolean canRender(AbstractClientPlayerEntity player) {
        return player == mc.player || isEnabled();
    }
}
