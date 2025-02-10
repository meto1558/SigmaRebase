package com.mentalfrostbyte.jello.module.impl.movement.speed;

import com.mentalfrostbyte.jello.event.impl.player.movement.EventUpdateWalkingPlayer;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.util.game.player.NewMovementUtil;
import net.minecraft.client.settings.KeyBinding;
import team.sdhq.eventBus.annotations.EventTarget;

public class VulcanSpeed extends Module {
    public VulcanSpeed() {
        super(ModuleCategory.MOVEMENT, "Vulcan", "Vulcan speed");
    }

    private int offGroundTicks;

    @EventTarget
    public void onEvent(EventUpdateWalkingPlayer event) {
        if (mc.player.onGround) {
            offGroundTicks = 0;
        } else {
            offGroundTicks++;
        }

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.keyCode, true);

        if (mc.player.onGround && NewMovementUtil.isMoving()) {
            mc.player.jump();
            NewMovementUtil.strafe(0.43);
        }

        if (offGroundTicks < 4) {
            NewMovementUtil.strafe();
        }
    }
}
