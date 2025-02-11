package com.mentalfrostbyte.jello.module.impl.movement.speed;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.player.movement.*;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.util.game.player.MovementUtil;
import team.sdhq.eventBus.annotations.EventTarget;
import team.sdhq.eventBus.annotations.priority.LowerPriority;

public class LowHopSpeed extends Module {
    private int tickCounter;
    private double moveSpeed;

    public LowHopSpeed() {
        super(ModuleCategory.MOVEMENT, "LowHop", "Low-hop speed");
    }

    @Override
    public void onEnable() {
        tickCounter = 1;
        double motionX = mc.player.getMotion().x;
        double motionZ = mc.player.getMotion().z;
        moveSpeed = Math.sqrt(motionX * motionX + motionZ * motionZ);
    }

    @EventTarget
    public void onMove(EventMove event) {
        if (this.isEnabled() && mc.player != null) {
            if (!mc.player.isInWater() && !mc.player.isOnLadder()) {
                if (mc.player.isOnGround()) {
                    tickCounter = 0;
                    moveSpeed *= 1.05;
                    event.setY(mc.player.getMotion().y = 0.2);
                } else {
                    moveSpeed = Math.max(MovementUtil.lowHopSpeed(), moveSpeed * 0.98);
                }
                MovementUtil.setMotion(event, moveSpeed);
            }
        }
    }

    @EventTarget
    @LowerPriority
    public void onStep(EventStep event) {
        if (this.isEnabled() && event.getHeight() >= 0.9) {
            tickCounter = 0;
        }
    }
}
