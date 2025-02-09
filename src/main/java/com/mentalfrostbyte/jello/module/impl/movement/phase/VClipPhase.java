package com.mentalfrostbyte.jello.module.impl.movement.phase;

import com.mentalfrostbyte.jello.event.impl.player.movement.EventUpdateWalkingPlayer;
import com.mentalfrostbyte.jello.gui.impl.others.ChatUtil;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import team.sdhq.eventBus.annotations.EventTarget;

public class VClipPhase extends Module {

    public VClipPhase() {
        super(ModuleCategory.MOVEMENT, "VClip", "Vclip phase (click shift)");
    }

    @EventTarget
    public void EventUpdate(EventUpdateWalkingPlayer event) {
        if (this.isEnabled()) {
            if (isEnabled() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                ChatUtil.sendChatMessage(".vclip -4");
            }
        }
    }
}
