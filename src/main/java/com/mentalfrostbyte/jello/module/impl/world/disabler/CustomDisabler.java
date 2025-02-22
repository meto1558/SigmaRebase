package com.mentalfrostbyte.jello.module.impl.world.disabler;

import com.mentalfrostbyte.jello.event.impl.game.network.EventSendPacket;
import com.mentalfrostbyte.jello.gui.impl.others.ChatUtil;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.event.impl.game.network.EventReceivePacket;
import com.mentalfrostbyte.jello.module.settings.impl.*;
import com.mentalfrostbyte.jello.util.PacketCollectorUtil;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import team.sdhq.eventBus.annotations.EventTarget;

import java.util.Arrays;

public class CustomDisabler  extends Module {

    public CustomDisabler() {
        super(ModuleCategory.EXPLOIT, "Custom", "Customizable disabler ");

        Arrays.stream(PacketCollectorUtil.getPacketClasses("Outgoing")).sorted()
                .map(packetClass -> new BooleanSetting(packetClass, "Cancel Outgoing " + packetClass, false))
                .forEach(this::registerSetting);

        Arrays.stream(PacketCollectorUtil.getPacketClasses("Incoming")).sorted()
                .map(packetClass -> new BooleanSetting(packetClass, "Cancel Incoming " + packetClass, false))
                .forEach(this::registerSetting);

        Arrays.stream(CEntityActionPacket.Action.values())
                .map(action -> new BooleanSetting(action.name(), "Cancel CEntityActionPacket " + action.name(), false))
                .forEach(this::registerSetting);

        Arrays.stream(CPlayerDiggingPacket.Action.values())
                .map(action -> new BooleanSetting(action.name(), "Cancel CPlayerDiggingPacket " + action.name(), false))
                .forEach(this::registerSetting);

    }

    @EventTarget
    public void onRecievePacket(EventReceivePacket event) {
        String packetName = event.getPacket().getClass().getSimpleName();
        if (this.getBooleanValueFromSettingName(packetName)) {
            ChatUtil.method32487("Cancelling INCOMING packet: " + packetName);
            event.cancelled = true;
        }
    }

    @EventTarget
    public void onSendPacket(EventSendPacket packet) {
        String packetName = packet.packet.getClass().getSimpleName();
        if (this.getBooleanValueFromSettingName(packetName)) {
            ChatUtil.method32487("Cancelling OUTGOING packet: " + packetName);
            packet.cancelled = true;
        }


        if (packet.packet instanceof CPlayerDiggingPacket) {
            if (this.getBooleanValueFromSettingName(((CPlayerDiggingPacket) packet.packet).getAction().name())) {
                ChatUtil.method32487("Cancelling CPlayerDiggingPacket action: " + ((CPlayerDiggingPacket) packet.packet).getAction().name());
                packet.cancelled = true;
            }
        }

        if (packet.packet instanceof CEntityActionPacket) {
            if (this.getBooleanValueFromSettingName(((CEntityActionPacket) packet.packet).getAction().name())) {
                ChatUtil.method32487("Cancelling CEntityActionPacket action: " + ((CEntityActionPacket) packet.packet).getAction().name());
                packet.cancelled = true;
            }
        }
    }
}

