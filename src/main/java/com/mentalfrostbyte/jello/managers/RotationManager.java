package com.mentalfrostbyte.jello.managers;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.game.EventRunTick;
import com.mentalfrostbyte.jello.event.impl.player.rotation.*;
import com.mentalfrostbyte.jello.module.impl.combat.NewKillAura;
import com.mentalfrostbyte.jello.module.impl.movement.BlockFly;
import com.mentalfrostbyte.jello.util.game.MinecraftUtil;
import com.mentalfrostbyte.jello.util.game.player.combat.RotationUtil;
import com.mentalfrostbyte.jello.util.game.player.constructor.Rotation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import team.sdhq.eventBus.EventBus;
import team.sdhq.eventBus.annotations.EventTarget;
import team.sdhq.eventBus.annotations.priority.HighestPriority;

import javax.annotation.Nullable;

public class RotationManager implements MinecraftUtil {
    public Entity target;

    // MODULES THAT USE ROTATIONS
    private NewKillAura killAura;
    private BlockFly blockFly;

    public @Nullable Rotation rotations;

    public void init() {
        EventBus.register(this);
        killAura = (NewKillAura) Client.getInstance().moduleManager.getModuleByClass(NewKillAura.class);
        blockFly = (BlockFly) Client.getInstance().moduleManager.getModuleByClass(BlockFly.class);
    }

    @EventTarget
    @HighestPriority
    public void onTick(EventRunTick event) {

    }

    public void setRotations(@Nullable Rotation rotations) {
        this.rotations = rotations;
    }

    public void setRotations(float @NotNull [] rotations) {
        setRotations(new Rotation(rotations[0], rotations[1]));
    }

    public void setRotations(@NotNull Rotation rotations, @NotNull EventRotation event) {
        this.rotations = rotations;
        event.yaw = rotations.yaw;
        event.pitch = rotations.pitch;
    }

    public void setRotations(float @NotNull [] rotations, EventRotation event) {
        setRotations(new Rotation(rotations[0], rotations[1]), event);
    }

    @EventTarget
    @HighestPriority
    public void onLook(EventRotationLook event) {
        if (shouldRotate()) {
			assert rotations != null;
			event.rotationVector = RotationUtil.getVectorForRotation(rotations.pitch, rotations.yaw);
        }
    }

    @EventTarget
    @HighestPriority
    public void onYawOffset(@NotNull EventRotationYawOffset event) {
        if (event.entity.equals(mc.player) && shouldRotate()) {
			assert rotations != null;
			event.f = MathHelper.interpolateAngle(event.partialTicks, rotations.lastYaw, rotations.yaw);
        }
    }

    @EventTarget
    @HighestPriority
    public void onYawHead(@NotNull EventRotationYawHead event) {
        if (event.entity.equals(mc.player) && shouldRotate()) {
			assert rotations != null;
			event.f1 = MathHelper.interpolateAngle(event.partialTicks, rotations.lastYaw, rotations.yaw);
        }
    }

    @EventTarget
    @HighestPriority
    public void onPitch(@NotNull EventRotationPitchHead event) {
        if (event.entity.equals(mc.player) && shouldRotate()) {
			assert rotations != null;
			event.f7 = MathHelper.interpolateAngle(event.partialTicks, rotations.lastPitch, rotations.pitch);
        }
    }

    public boolean shouldRotate() {
        return rotations != null;
    }
}
