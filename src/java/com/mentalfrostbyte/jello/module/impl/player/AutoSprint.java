package com.mentalfrostbyte.jello.module.impl.player;

import com.mentalfrostbyte.jello.event.impl.EventRayTraceResult;
import com.mentalfrostbyte.jello.event.impl.TickEvent;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.settings.impl.BooleanSetting;
import team.sdhq.eventBus.annotations.EventTarget;

public class AutoSprint extends Module {
    private final double[] TrackMotion = new double[]{0.0, 0.0};
    private boolean isSprinting;

    public AutoSprint() {
        super(ModuleCategory.PLAYER, "AutoSprint", "Sprints for you");
        this.registerSetting(new BooleanSetting("Keep Sprint", "Keep Sprinting after hitting a player", true));
    }

    @EventTarget
    public void TickEvent(TickEvent event) {
        if (mc.player != null)
            mc.player.setSprinting(mc.player.moveForward > 0.0F);
    }

    @EventTarget
    public void RayTraceEvent(EventRayTraceResult event) {
        if (mc.player != null)
            if (this.getBooleanValueFromSettingName("Keep Sprint")) {
                if (!event.isHovering()) {
                    if (this.TrackMotion.length == 2) {
                        double MotionX = this.TrackMotion[0] - mc.player.getMotion().x;
                        double MotionZ = this.TrackMotion[1] - mc.player.getMotion().z;
                        if (MotionX != 0.0 || MotionZ != 0.0) {
                            mc.player.setMotion(this.TrackMotion[0], mc.player.getMotion().y, this.TrackMotion[1]);
                        }

                        if (this.isSprinting && !mc.player.isSprinting()) {
                            mc.player.setSprinting(true);
                        }
                    }
                } else {
                    this.TrackMotion[0] = mc.player.getMotion().x;
                    this.TrackMotion[1] = mc.player.getMotion().z;
                    this.isSprinting = mc.player.isSprinting();
                }
            }
    }
}
