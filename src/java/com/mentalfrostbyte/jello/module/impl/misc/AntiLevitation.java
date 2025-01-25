package com.mentalfrostbyte.jello.module.impl.misc;

import com.mentalfrostbyte.jello.event.impl.game.TickEvent;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import net.minecraft.potion.Effects;
import team.sdhq.eventBus.annotations.EventTarget;

public class AntiLevitation extends Module {
    public AntiLevitation() {
        super(ModuleCategory.MISC, "AntiLevitation", "Removes levitation effects");
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (this.isEnabled()) {
            mc.player.removeActivePotionEffect(Effects.LEVITATION);
        }
    }
}
