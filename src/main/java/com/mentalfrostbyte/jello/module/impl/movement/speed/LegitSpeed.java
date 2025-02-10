package com.mentalfrostbyte.jello.module.impl.movement.speed;

import com.mentalfrostbyte.jello.event.impl.player.movement.EventUpdateWalkingPlayer;
import com.mentalfrostbyte.jello.util.game.player.MovementUtil;
import com.mentalfrostbyte.jello.util.game.player.NewMovementUtil;
import team.sdhq.eventBus.annotations.EventTarget;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.settings.impl.BooleanSetting;

public class LegitSpeed extends Module {
    public LegitSpeed() {
        super(ModuleCategory.MOVEMENT, "Legit", "Legit Sprint jumping.");
        this.registerSetting(new BooleanSetting("Sprint", "Sprints when walking", true));
        this.registerSetting(new BooleanSetting("AutoJump", "Automatically jumps for you.", true));
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.setPressed(false);
        mc.gameSettings.keyBindJump.setPressed(false);
    }

    @EventTarget
    public void onMotion(EventUpdateWalkingPlayer event) {
        if (NewMovementUtil.isMoving()) {
            if (getBooleanValueFromSettingName("Sprint")) {
                mc.gameSettings.keyBindSprint.setPressed(true);
            }

            if (getBooleanValueFromSettingName("AutoJump")) {
                mc.gameSettings.keyBindJump.setPressed(true);
            }
        }
    }
}
