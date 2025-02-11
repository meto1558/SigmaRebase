package com.mentalfrostbyte.jello.event.impl.game.render;

import com.mentalfrostbyte.jello.event.CancellableEvent;
import net.minecraft.entity.LivingEntity;

public class EventRenderEntity extends CancellableEvent {

    private float interpolatedYawOffset;
    private float interpolatedHeadYaw;
    private float yaw;
    private float lerpedPitch;
    private final float partialTicks;
    private final LivingEntity entity;
    private boolean field21533 = true;
    private boolean field21534 = true;
    private RenderState state;

    public EventRenderEntity(float interpolatedYawOffset, float interpolatedHeadYaw, float yaw, float lerpedPitch, float partialTicks, LivingEntity entity) {
        this.interpolatedYawOffset = interpolatedYawOffset;
        this.interpolatedHeadYaw = interpolatedHeadYaw;
        this.yaw = yaw;
        this.lerpedPitch = lerpedPitch;
        this.partialTicks = partialTicks;
        this.entity = entity;
        this.state = RenderState.DEFAULT;
    }

    public void setState(RenderState state) {
        this.state = state;
    }

    public RenderState getState() {
        return this.state;
    }

    public float getInterpolatedYawOffset() {
        return this.interpolatedYawOffset;
    }

    public float getInterpolatedHeadYaw() {
        return this.interpolatedHeadYaw;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getLerpedPitch() {
        return this.lerpedPitch;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setInterpolatedYawOffset(float interpolatedYawOffset) {
        this.interpolatedYawOffset = interpolatedYawOffset;
    }

    public void setInterpolatedHeadYaw(float interpolatedHeadYaw) {
        this.interpolatedHeadYaw = interpolatedHeadYaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setLerpedPitch(float lerpedPitch) {
        this.lerpedPitch = lerpedPitch;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public boolean method13954() {
        return this.field21533;
    }

    public void method13955(boolean var1) {
        this.field21534 = var1;
    }

    public boolean method13956() {
        return this.field21534;
    }

    public void method13957(boolean var1) {
        this.field21533 = var1;
    }

    public enum RenderState {
        DEFAULT,
        field13213,
        field13214
    }
}
