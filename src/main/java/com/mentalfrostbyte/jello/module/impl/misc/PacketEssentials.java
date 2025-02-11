package com.mentalfrostbyte.jello.module.impl.misc;

import com.mentalfrostbyte.jello.event.impl.game.network.EventReceivePacket;
import com.mentalfrostbyte.jello.module.InDevelopment;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.network.play.server.*;
import team.sdhq.eventBus.annotations.EventTarget;

@InDevelopment
public class PacketEssentials extends Module {
    public PacketEssentials() {
        super(ModuleCategory.MISC, "PacketEssentials", "Ignores all types of junk cosmetic packets to improve fps");
    }

    @EventTarget
    public void onReceivePacket(EventReceivePacket event) {
        if (this.isEnabled()) {
            if (!(event.getPacket() instanceof SSpawnObjectPacket sSpawnObjectPacket)) {
                if (!(event.getPacket() instanceof SSpawnMobPacket sSpawnMobPacket)) {
                    if (!(event.getPacket() instanceof SSpawnParticlePacket)) {
                        if (!(event.getPacket() instanceof SUpdateBossInfoPacket)) {
                            if (!(event.getPacket() instanceof SScoreboardObjectivePacket)) {
                                if (event.getPacket() instanceof SEntityMetadataPacket) {
                                    event.cancelled = true;
                                }
                            } else {
                                event.cancelled = true;
                            }
                        } else {
                            event.cancelled = true;
                        }
                    } else {
                        event.cancelled = true;
                    }
                } else {
                    if (sSpawnMobPacket.getEntityType() == 1) {
                        event.cancelled = true;
                    }
                }
            } else {
                if (sSpawnObjectPacket.getType() == EntityType.ARMOR_STAND) {
                    event.cancelled = true;
                }

                if (sSpawnObjectPacket.getType() == EntityType.GIANT) {
                    event.cancelled = true;
                }
            }
        }
    }
}
