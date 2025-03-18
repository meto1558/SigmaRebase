package com.mentalfrostbyte.jello.module.impl.movement;

import com.mentalfrostbyte.jello.event.impl.player.action.EventInputOptions;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventJump;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventMoveRelative;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.util.client.rotation.RotationCore;
import com.mentalfrostbyte.jello.util.game.player.MovementUtil;
import team.sdhq.eventBus.annotations.EventTarget;
import team.sdhq.eventBus.annotations.priority.HighestPriority;

public class CorrectMovement extends Module {
    public CorrectMovement() {
        super(ModuleCategory.MOVEMENT, "CorrectMovement", "Correct your movement.");
    }

    @EventTarget
    @HighestPriority
    public void onInput(EventInputOptions event) {
        MovementUtil.silentStrafe(event, RotationCore.currentYaw);
    }

    @EventTarget
    @HighestPriority
    private void onJump(EventJump event) {
        event.yaw = (RotationCore.currentYaw);
    }

    @EventTarget
    @HighestPriority
    private void onStrafe(EventMoveRelative event) {
        event.setYaw(RotationCore.currentYaw);
    }
}