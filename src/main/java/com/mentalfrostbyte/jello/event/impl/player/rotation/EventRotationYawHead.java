package com.mentalfrostbyte.jello.event.impl.player.rotation;

import com.mentalfrostbyte.jello.event.CancellableEvent;
import net.minecraft.entity.Entity;

public class EventRotationYawHead extends CancellableEvent {
    public final Entity entity;
    public float f1;
    public final float partialTicks;

    public EventRotationYawHead(Entity entity, float f1, float partialTicks) {
        this.entity = entity;
        this.f1 = f1;
        this.partialTicks = partialTicks;
    }
}
