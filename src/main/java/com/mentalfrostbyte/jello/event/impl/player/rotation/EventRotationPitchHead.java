package com.mentalfrostbyte.jello.event.impl.player.rotation;

import com.mentalfrostbyte.jello.event.CancellableEvent;
import net.minecraft.entity.Entity;

public class EventRotationPitchHead extends CancellableEvent {
    public final Entity entity;
    public float f7;
    public final float partialTicks;

    public EventRotationPitchHead(Entity entity, float f7, float partialTicks) {
        this.entity = entity;
        this.f7 = f7;
        this.partialTicks = partialTicks;
    }
}
