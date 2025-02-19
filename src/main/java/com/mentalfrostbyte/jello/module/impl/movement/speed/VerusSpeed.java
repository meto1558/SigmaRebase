package com.mentalfrostbyte.jello.module.impl.movement.speed;

import com.mentalfrostbyte.jello.event.impl.player.movement.EventUpdateWalkingPlayer;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.settings.impl.BooleanSetting;
import com.mentalfrostbyte.jello.module.settings.impl.ModeSetting;
import com.mentalfrostbyte.jello.util.game.player.MovementUtil;
import net.minecraft.potion.Effects;
import team.sdhq.eventBus.annotations.EventTarget;

/**
 * @author alarmingly_good (on discord)
 */
public class VerusSpeed extends Module {

    public VerusSpeed() {
        super(ModuleCategory.MOVEMENT, "Verus", "Speed for Verus.");
        registerSetting(new ModeSetting("Mode", "Mode", 0, "Basic", "Low", "Ground", "Glide"));
        registerSetting(new BooleanSetting("Damage boost", "Boost on damage", false));
    }

    private double speed = 0;
    private int airTicks = 0;

    @EventTarget
    public void onMotion(EventUpdateWalkingPlayer event) {
        boolean dmgBoost = getBooleanValueFromSettingName("Damage boost");
        switch (getStringSettingValueByName("Mode")) {
            case "Basic" -> {
                if (!mc.player.onGround) {
                    speed *= 0.9999999999999999D;
                    airTicks++;
                } else {
                    airTicks = 0;
                    if (mc.player.isPotionActive(Effects.SPEED)) {
                        speed = 0.498D;
                    } else {
                        speed = 0.377D;
                    }
                    if (!mc.player.isSprinting())
                        speed *= 0.78D;

                    mc.player.jump();
                }

                if (mc.player.hurtTime > 0 && dmgBoost)
                    MovementUtil.strafe(0.86);
                else
                    MovementUtil.strafe(speed);
            }

            case "Low" -> {
                if (!mc.player.onGround) {
                    speed *= 0.9999999999999999D;
                    airTicks++;
                } else {
                    airTicks = 0;
                    if (mc.player.isPotionActive(Effects.SPEED)) {
                        speed = 0.498D;
                    } else {
                        speed = 0.377D;
                    }

                    if (!mc.player.isSprinting())
                        speed *= 0.78D;

                    mc.player.jump();
                }

                if (airTicks == 2) {
                    mc.player.setMotion(mc.player.getMotion().x, -0.0784000015258789, mc.player.getMotion().z);
                }

                if (mc.player.hurtTime > 0 && dmgBoost)
                    MovementUtil.strafe(0.86);
                else
                    MovementUtil.strafe(speed);
            }

            case "Ground" -> {
                if (mc.player.onGround) {
                    MovementUtil.strafe(0.23 + (Math.random() / 150d));
                }

                if (mc.player.hurtTime > 0 && dmgBoost) {
                    MovementUtil.strafe(1.2);
                    mc.player.setMotion(mc.player.getMotion().x, -1, mc.player.getMotion().z);
                }
            }

            case "Glide" -> {
                if (!mc.player.onGround) {
                    speed *= 0.9999999999999999D;
                    airTicks++;
                } else {
                    airTicks = 0;
                    if (mc.player.isPotionActive(Effects.SPEED)) {
                        speed = 0.498D;
                    } else {
                        speed = 0.377D;
                    }

                    mc.player.jump();
                }

                if (!mc.player.isSprinting())
                    speed *= 0.78D;

                mc.player.setMotion(mc.player.getMotion().x, Math.max(mc.player.getMotion().y, -0.09800000190734863), mc.player.getMotion().z);

                if (mc.player.hurtTime > 0 && dmgBoost)
                    MovementUtil.strafe(0.86);
                else
                    MovementUtil.strafe(speed);
            }
        }
    }
}
