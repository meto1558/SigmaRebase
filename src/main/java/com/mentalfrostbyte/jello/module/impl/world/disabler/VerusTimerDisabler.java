package com.mentalfrostbyte.jello.module.impl.world.disabler;

import com.mentalfrostbyte.jello.event.impl.game.network.EventSendPacket;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import team.sdhq.eventBus.annotations.EventTarget;

public class VerusTimerDisabler extends Module {
    private static long lastPlayerPacketTime = 0;
    private static final long MOVEMENT_THRESHOLD = 100; // 100ms

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
                if (currentTime - lastPlayerPacketTime < MOVEMENT_THRESHOLD) {
                    event.cancelled = true; // Cancel packet if sent too frequently
                }
                lastPlayerPacketTime = currentTime;
            }
        }
    }
}
