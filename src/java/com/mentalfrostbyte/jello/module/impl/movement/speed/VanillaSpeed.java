package com.mentalfrostbyte.jello.module.impl.movement.speed;

import com.mentalfrostbyte.jello.event.impl.EventMove;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.settings.impl.NumberSetting;
import com.mentalfrostbyte.jello.util.minecraft.MovementUtil;
import team.sdhq.eventBus.annotations.EventTarget;

public class VanillaSpeed extends Module {
    public VanillaSpeed() {
        super(ModuleCategory.MOVEMENT, "Vanilla", "Vanilla speed");
        this.registerSetting(new NumberSetting<Float>("Speed", "Speed value", 4.0F, Float.class, 1.0F, 10.0F, 0.1F));
    }

    @EventTarget
    public void EventMove(EventMove event) {
        if (this.isEnabled()) {
            double speedInput= MovementUtil.getSpeed() * (double) this.getNumberValueBySettingName("Speed");
            MovementUtil.setSpeed(event, speedInput);
        }
    }
}
