package com.mentalfrostbyte.jello.event.impl.game.network;

import com.mentalfrostbyte.jello.event.CancellableEvent;
import net.minecraft.network.IPacket;

import java.util.ArrayList;
import java.util.List;

public class EventSendPacket extends CancellableEvent {
    private IPacket packet;
    private final List<IPacket> packets = new ArrayList<IPacket>();

    public EventSendPacket(IPacket var1) {
        this.packet = var1;
        this.packets.add(var1);
    }

    public IPacket getPacket() {
        return this.packet;
    }
}