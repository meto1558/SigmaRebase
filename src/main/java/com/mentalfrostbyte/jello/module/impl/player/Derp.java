package com.mentalfrostbyte.jello.module.impl.player;

import com.mentalfrostbyte.jello.event.CancellableEvent;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventUpdateWalkingPlayer;
import com.mentalfrostbyte.jello.event.impl.player.rotation.EventRotation;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;

import com.mentalfrostbyte.jello.module.settings.impl.BooleanSetting;
import com.mentalfrostbyte.jello.module.settings.impl.ModeSetting;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.util.Hand;
import team.sdhq.eventBus.annotations.EventTarget;

import java.util.Random;

public class Derp extends Module {
    private Random random = new Random();
    private boolean releaseShift;
    private int hitCounter;
    private int spinCounter;

    public Derp() {
        super(ModuleCategory.PLAYER, "Derp", "Spazzes around");
        this.registerSetting(new ModeSetting("Rotation Mode", "Rotation Mode", 0, "Random", "Spin", "None"));
        this.registerSetting(new BooleanSetting("Hit", "Randomly hit", true));
        this.registerSetting(new BooleanSetting("Sneak", "Randomly sneak", true));
    }

    @EventTarget
    public void onUpdate(EventRotation event) {
        if (this.isEnabled() && event.state == CancellableEvent.EventState.PRE) {
            if (this.getBooleanValueFromSettingName("Sneak")) {
                if (this.releaseShift) {
                    mc.getConnection().sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY));
                } else {
                    mc.getConnection().sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.PRESS_SHIFT_KEY));
                }
            }

            this.releaseShift = !this.releaseShift;
            this.hitCounter++;
            if (this.getBooleanValueFromSettingName("Hit") && this.hitCounter > this.random.nextInt(5) + 3) {
                this.hitCounter = 0;
                Hand hand = Hand.values()[this.random.nextInt(1)];
                mc.player.swingArm(hand);
            }

            String rotationMode = this.getStringSettingValueByName("Rotation Mode");
            switch (rotationMode) {
                case "Random":
                    event.yaw = this.random.nextFloat() * 360.0F;
                    event.pitch = this.random.nextFloat() * 180.0F - 90.0F;
                    break;
                case "Spin":
                    this.spinCounter += 20;

                    while (this.spinCounter > 360) {
                        this.spinCounter -= 360;
                    }

                    event.yaw = (float) this.spinCounter + this.random.nextFloat();
                    break;
            }
        }
    }

    @Override
    public void onEnable() {
        this.spinCounter = (int) mc.player.rotationYaw;
    }
}
