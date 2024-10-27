package com.mentalfrostbyte.jello.module.impl.player;

import com.mentalfrostbyte.jello.events.impl.TickEvent;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import team.sdhq.eventBus.annotations.EventTarget;

public class Cape extends Module {

    private final Minecraft mc = Minecraft.getInstance();

    public Cape() {
        super(ModuleCategory.PLAYER, "Cape", "gives you a cape (wow)");
    }

    public ResourceLocation getCape() {
        return new ResourceLocation("minecraft", "com/mentalfrostbyte/gui/resources/jello/capes/cape.png");
    }

    public void onDisable() {
        if (mc.player != null) {
            mc.player.setLocationOfCape(null);
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (mc.player != null) {
            mc.player.setLocationOfCape(getCape());
        }
    }
}