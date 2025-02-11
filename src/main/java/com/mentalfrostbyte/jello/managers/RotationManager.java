package com.mentalfrostbyte.jello.managers;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.game.EventRunTick;
import com.mentalfrostbyte.jello.event.impl.player.rotation.*;
import com.mentalfrostbyte.jello.module.impl.combat.NewKillAura;
import com.mentalfrostbyte.jello.util.game.MinecraftUtil;
import com.mentalfrostbyte.jello.util.game.player.combat.RotationUtil;
import com.mentalfrostbyte.jello.util.game.player.constructor.Rotation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import team.sdhq.eventBus.EventBus;
import team.sdhq.eventBus.annotations.EventTarget;
import team.sdhq.eventBus.annotations.priority.HighestPriority;

public class RotationManager implements MinecraftUtil {
    public Entity target;

    // MODULES THAT USE ROTATIONS
    private NewKillAura killAura;

    public Rotation rotations;

    public void init() {
        EventBus.register(this);
        killAura = (NewKillAura) Client.getInstance().moduleManager.getModuleByClass(NewKillAura.class);
    }


    @EventTarget
    @HighestPriority
    public void onTick(EventRunTick event) {
        if (!shouldRotate() && this.rotations == null && mc.player != null) {
            this.rotations = new Rotation(mc.player.rotationYaw, mc.player.rotationPitch);
        }
    }

    public void setRotations(Rotation rotations) {
        this.rotations = rotations;
    }

    public void setRotations(float[] rotations) {
        setRotations(new Rotation(rotations[0], rotations[1]));
    }

    public void setRotations(Rotation rotations, EventRotation event) {
        this.rotations = rotations;

        this.rotations.lastYaw = event.yaw;
        this.rotations.lastPitch = event.pitch;

        event.yaw = rotations.yaw;
        event.pitch = rotations.pitch;

        this.rotations.yaw = event.yaw;
        this.rotations.pitch = event.pitch;
    }

    public void setRotations(float[] rotations, EventRotation event) {
        setRotations(new Rotation(rotations[0], rotations[1]), event);
    }

    @EventTarget
    @HighestPriority
    public void onLook(EventRotationLook event) {
        if (shouldRotate()) {
            event.rotationVector = RotationUtil.getVectorForRotation(rotations.pitch, rotations.yaw);
        }
    }

    @EventTarget
    @HighestPriority
    public void onYawOffset(EventRotationYawOffset event) {
        if (event.entity.equals(mc.player) && shouldRotate()) {
            event.f = MathHelper.interpolateAngle(event.partialTicks, rotations.lastYaw, rotations.yaw);
        }
    }

    @EventTarget
    @HighestPriority
    public void onYawHead(EventRotationYawHead event) {
        if (event.entity.equals(mc.player) && shouldRotate()) {
            event.f1 = MathHelper.interpolateAngle(event.partialTicks, rotations.lastYaw, rotations.yaw);
        }
    }

    @EventTarget
    @HighestPriority
    public void onPitch(EventRotationPitchHead event) {
        if (event.entity.equals(mc.player) && shouldRotate()) {
            event.f7 = MathHelper.interpolateAngle(event.partialTicks, rotations.lastPitch, rotations.pitch);
        }
    }

    public boolean shouldRotate() {
        return (killAura.enabled && target != null) || rotations != null;
    }
}
