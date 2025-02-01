package com.mentalfrostbyte.jello.module.impl.world.disabler;

import com.mentalfrostbyte.jello.event.impl.player.movement.EventMove;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventUpdateWalkingPlayer;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.settings.impl.BooleanSetting;
import com.mentalfrostbyte.jello.util.player.MovementUtil;
import team.sdhq.eventBus.annotations.EventTarget;

// Skidded from titties client lol
public class HypixelPredictionDisabler extends Module {
    public static boolean watchDogDisabled = false;
    public static boolean stuckOnAir = false;
    public static boolean startDisabler = false;
    public static int airTicks = 0;
    private final BooleanSetting motion;

    public HypixelPredictionDisabler() {
        super(
                ModuleCategory.EXPLOIT,
                "Hypixel Prediction",
                "Disables some checks for Hypixel's Prediction anti-cheat (untested)"
        );
        this.registerSetting(this.motion = new BooleanSetting("Motion", "Motion check disabler", true));
    }
    @EventTarget
    public void onUpdate(EventUpdateWalkingPlayer __) {
        if (HypixelPredictionDisabler.mc.player == null)
            return;

        if (motion.currentValue && !watchDogDisabled) {
            airTicks = HypixelDisabler.mc.player.isOnGround() ? 0 : ++airTicks;
            if (stuckOnAir && airTicks >= 9) {
                MovementUtil.stopMoving();
            }
        }
    }

    @EventTarget
    public void onMove(EventMove event) {
        if (HypixelDisabler.mc.player == null) {
            return;
        }
        if (motion.currentValue && !watchDogDisabled) {
            if (startDisabler && HypixelDisabler.mc.player.isOnGround()) {
                HypixelDisabler.mc.player.jump();
                startDisabler = false;
                stuckOnAir = true;
            } else if (stuckOnAir && airTicks >= 9) {
                if (airTicks % 2 == 0) {
                    event.setZ(event.getZ() + 0.095);
                }
                MovementUtil.setPlayerYMotion(0.0);
            }
        }
    }
}
