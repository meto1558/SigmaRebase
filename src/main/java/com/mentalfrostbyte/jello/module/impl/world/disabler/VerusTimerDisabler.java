package com.mentalfrostbyte.jello.module.impl.world.disabler;

import com.mentalfrostbyte.jello.event.impl.game.network.EventSendPacket;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.data.ModuleCategory;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import team.sdhq.eventBus.annotations.EventTarget;

public class VerusTimerDisabler extends Module {
    private long lastPlayerPacketTime = 0;

    public VerusTimerDisabler() {
        super(ModuleCategory.EXPLOIT, "VerusTimer", "Disable Verus timer checks.");
    }

    @EventTarget
    public void onSendPacket(EventSendPacket event) {
        if (mc.player != null) {
            // Cancel KeepAlivePacket to bypass timer checks
            if (event.packet instanceof CKeepAlivePacket) {
                event.cancelled = true;
            }

            // Cancel CPlayerPacket but allow some interval between cancellations
            if (event.packet instanceof CPlayerPacket) {
                long currentTime = System.currentTimeMillis();
                long MOVEMENT_THRESHOLD_MS = 100;
                if (currentTime - lastPlayerPacketTime < MOVEMENT_THRESHOLD_MS) {
                    event.cancelled = true; // Cancel packet if sent too frequently
                }
                lastPlayerPacketTime = currentTime;
            }
        }
    }
}
