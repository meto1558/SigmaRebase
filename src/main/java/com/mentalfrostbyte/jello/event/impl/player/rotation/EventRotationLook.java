package com.mentalfrostbyte.jello.event.impl.player.rotation;


import com.mentalfrostbyte.jello.event.CancellableEvent;
import net.minecraft.util.math.vector.Vector3d;

public class EventRotationLook extends CancellableEvent {
    public Vector3d rotationVector;
    public final float partialTicks;

    public EventRotationLook(Vector3d rotationVector, float partialTicks) {
        this.rotationVector = rotationVector;
        this.partialTicks = partialTicks;
    }
}
