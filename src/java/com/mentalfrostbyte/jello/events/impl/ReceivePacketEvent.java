package com.mentalfrostbyte.jello.events.impl;

import com.mentalfrostbyte.jello.events.CancellableEvent;
import net.minecraft.network.IPacket;

public class ReceivePacketEvent extends CancellableEvent {
    private IPacket<?> packet;

    public ReceivePacketEvent(IPacket<?> var1) {
        this.packet = var1;
    }

    public IPacket<?> getPacket() {
        return this.packet;
    }
}
