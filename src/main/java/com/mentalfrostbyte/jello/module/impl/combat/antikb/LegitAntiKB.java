package com.mentalfrostbyte.jello.module.impl.combat.antikb;

import com.mentalfrostbyte.jello.event.impl.game.network.EventReceivePacket;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventMoveButton;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventMoveInput;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.util.client.rotation.RotationCore;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import team.sdhq.eventBus.annotations.EventTarget;
import team.sdhq.eventBus.annotations.priority.HighestPriority;

public class LegitAntiKB extends Module {
    public LegitAntiKB() {
        super(ModuleCategory.COMBAT, "Legit", "Use jump-reset mechanism to reduce velocity.");
    }

    private boolean working = false, attacking = false;

    @EventTarget
    @HighestPriority
    public void onReceivePackett(EventReceivePacket event) {
        if (working) {
            if (mc.player.hurtTime != 0) {
                mc.player.setSprinting(false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.keyCode, false);

                if (!mc.player.onGround) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.keyCode, true);
                }

                if (mc.player.isSwingInProgress) {
                    attacking = true;
                }

                if (attacking) {
                    mc.player.setMotion(mc.player.getMotion().x * 0.6, mc.player.getMotion().y, mc.player.getMotion().x * 0.6);
                }

                attacking = false;
            }
            working = false;
        }
    }
}