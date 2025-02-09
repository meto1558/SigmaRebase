package com.mentalfrostbyte.jello.module.impl.movement.phase;

import com.mentalfrostbyte.jello.event.impl.player.movement.EventUpdateWalkingPlayer;
import com.mentalfrostbyte.jello.gui.impl.others.ChatUtil;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import team.sdhq.eventBus.annotations.EventTarget;

public class VClipPhase extends Module {

    private long lastMessageTime = 0;

    public VClipPhase() {
        super(ModuleCategory.MOVEMENT, "VClip", "Vclip phase (click shift)");
    }

    @EventTarget
    public void EventUpdate(EventUpdateWalkingPlayer event) {
        if (this.isEnabled() && mc.gameSettings.keyBindSneak.isKeyDown()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastMessageTime >= 1000) {
                ChatUtil.sendChatMessage(".vclip -4");
                lastMessageTime = currentTime;
            }
        }
    }
}
