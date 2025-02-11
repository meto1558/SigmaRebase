package com.mentalfrostbyte.jello.event.impl.player.rotation;

import com.mentalfrostbyte.jello.event.CancellableEvent;
import net.minecraft.entity.Entity;

public class EventRotationYawOffset extends CancellableEvent {
    public final Entity entity;
    public float f;
    public final float partialTicks;

    public EventRotationYawOffset(Entity entity, float f, float partialTicks) {
        this.entity = entity;
        this.f = f;
        this.partialTicks = partialTicks;
    }
}
