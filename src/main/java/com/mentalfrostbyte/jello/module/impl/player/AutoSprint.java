package com.mentalfrostbyte.jello.module.impl.player;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.game.EventRayTraceResult;
import com.mentalfrostbyte.jello.event.impl.player.EventGetFovModifier;
import com.mentalfrostbyte.jello.event.impl.player.EventPlayerTick;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.ModuleWithModuleSettings;
import com.mentalfrostbyte.jello.module.impl.movement.BlockFly;
import com.mentalfrostbyte.jello.module.impl.movement.blockfly.BlockFlyAACMode;
import com.mentalfrostbyte.jello.module.settings.impl.BooleanSetting;
import com.mentalfrostbyte.jello.util.game.player.MovementUtil;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.Items;
import team.sdhq.eventBus.annotations.EventTarget;

public class AutoSprint extends Module {
    private final double[] trackMotion = new double[]{0.0, 0.0};
    private boolean sprinting;

    public AutoSprint() {
        super(ModuleCategory.PLAYER, "AutoSprint", "Sprints for you");
        this.registerSetting(new BooleanSetting("Keep Sprint", "Keep Sprinting after hitting a player", true));
    }

    @EventTarget
    public void TickEvent(EventPlayerTick event) {
        ModuleWithModuleSettings getModule = (ModuleWithModuleSettings) Client.getInstance().moduleManager.getModuleByClass(BlockFly.class);
        Module BlockFly = getModule.parentModule;
        if (BlockFly == null || !BlockFly.isEnabled() || !(BlockFly instanceof BlockFlyAACMode) || BlockFly.getBooleanValueFromSettingName("Haphe (AACAP)")) {
            mc.player.setSprinting(mc.player.moveForward > 0.0F && !((BlockFly) Client.getInstance().moduleManager.getModuleByClass(BlockFly.class)).isEnabled2());
        }
    }

    @EventTarget
    public void onFOV(EventGetFovModifier event) {
        if (this.isEnabled()
                && !(mc.player.moveForward <= 0.0F)
                && (!mc.player.isHandActive() || mc.player.getActiveItemStack().getItem() != Items.BOW)
                && !((BlockFly) Client.getInstance().moduleManager.getModuleByClass(BlockFly.class)).isEnabled2()) {
            ModifiableAttributeInstance getAttribute = mc.player.getAttribute(Attributes.MOVEMENT_SPEED);
            float BlockFly = (float) (
                    (getAttribute.getBaseValue() + 0.03F + (double) (0.015F * (float) MovementUtil.getSpeedBoost())) / (double) mc.player.abilities.getWalkSpeed() + 1.0
            )
                    / 2.0F;
            event.fovModifier = BlockFly;
        }
    }

    @EventTarget
    public void RayTraceEvent(EventRayTraceResult event) {
        if (this.isEnabled() && this.getBooleanValueFromSettingName("Keep Sprint")) {
            if (!event.isHovering()) {
                if (this.trackMotion.length == 2) {
                    double MotionX = this.trackMotion[0] - mc.player.getMotion().x;
                    double MotionZ = this.trackMotion[1] - mc.player.getMotion().z;
                    if (MotionX != 0.0 || MotionZ != 0.0) {
                        mc.player.setMotion(this.trackMotion[0], mc.player.getMotion().y, this.trackMotion[1]);
                    }

                    if (this.sprinting && !mc.player.isSprinting()) {
                        mc.player.setSprinting(true);
                    }
                }
            } else {
                this.trackMotion[0] = mc.player.getMotion().x;
                this.trackMotion[1] = mc.player.getMotion().z;
                this.sprinting = mc.player.isSprinting();
            }
        }
    }
}
