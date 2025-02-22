
/*
package com.mentalfrostbyte.jello.module.impl.world.disabler;


import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.event.impl.game.network.EventReceivePacket;
import com.mentalfrostbyte.jello.util.PacketCollectorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import team.sdhq.eventBus.annotations.EventTarget;

public class CustomDisabler  extends Module {

    private final MultiStringSetting
            packetType = new MultiStringSetting("Packet type", "What type of packets should be focused on?", new String[]{"Incoming"}, new String[]{"Outgoing", "Incoming"}),
            outgoing = new MultiStringSetting("Outgoing", "Which outgoing packets should we cancel?", new String[]{"C07PacketPlayerDigging"}, PacketCollectorUtil.getPacketClasses("Outgoing")).hide(() -> !packetType.enabled("Outgoing")),
            incoming = new MultiStringSetting("Incoming", "Which incoming packets should we cancel?", new String[]{"S0EPacketSpawnObject"}, PacketCollectorUtil.getPacketClasses("Incoming")).hide(() -> !packetType.enabled("Incoming")),
            c07 = new MultiStringSetting("C07 Actions", "What actions should be cancelled for C07PacketPlayerDigging?", new String[]{"START_DESTROY_BLOCK"}, new String[]{"START_DESTROY_BLOCK", "ABORT_DESTROY_BLOCK", "STOP_DESTROY_BLOCK", "DROP_ITEM", "DROP_ALL_ITEMS"}),
            c0b = new MultiStringSetting("C0B Actions", "What actions should be cancelled for C0BPacketEntityAction?", new String[]{"START_SNEAKING"}, new String[]{"START_SNEAKING", "STOP_SNEAKING", "START_SPRINTING", "STOP_SPRINTING", "RIDING_JUMP", "OPEN_INVENTORY"});

    public CustomDisabler() {
        super(ModuleCategory.EXPLOIT, "Custom", "Customizable disabler ");
    }

    @EventTarget
    private void RecievePacketEvent(EventReceivePacket event) {
        if (packetType.enabled("Incoming") && event.getType() == PacketEvent.Type.INCOMING) {
            String packetName = event.getPacket().getClass().getSimpleName();

            if (incoming.enabled(packetName)) {
                System.out.println("Cancelling INCOMING packet: " + packetName);
                event.cancelled = true;
                return;
            }
        }

        if (packetType.enabled("Outgoing") && event.getType() == PacketEvent.Type.OUTGOING) {
            String packetName = event.getPacket().getClass().getSimpleName();

            if (outgoing.enabled(packetName)) {
                Minecraft.getInstance().player.sendMessage(new StringTextComponent("Cancelling OUTGOING packet: " + packetName), Util.DUMMY_UUID);
                event.cancelled = true;
            }

            if (event.packet instanceof CPlayerDiggingPacket) {
                if (c07.enabled(packet.getStatus().name())) {
                    System.out.println("Cancelling C07PacketPlayerDigging action: " + packet.getStatus());
                    event.cancelled =true;
                }
            }

            if (event.packet instanceof CEntityActionPacket) {
                if (c0b.enabled(packet.getAction().name())) {
                    System.out.println("Cancelling C0BPacketEntityAction action: " + packet.getaction());
                    event.cancelled = true;
                }
            }
        }
    }
}
*/
