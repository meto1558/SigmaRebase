package com.mentalfrostbyte.jello.module.impl.movement.speed;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.game.network.EventReceivePacket;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender2D;
import com.mentalfrostbyte.jello.event.impl.game.world.EventLoadWorld;
import com.mentalfrostbyte.jello.event.impl.player.EventPlayerTick;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventJump;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventMove;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventUpdateWalkingPlayer;
import com.mentalfrostbyte.jello.gui.base.JelloPortal;
import com.mentalfrostbyte.jello.misc.Class2094;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.impl.combat.Criticals;
import com.mentalfrostbyte.jello.module.impl.combat.KillAura;
import com.mentalfrostbyte.jello.module.impl.movement.BlockFly;
import com.mentalfrostbyte.jello.module.impl.movement.Fly;
import com.mentalfrostbyte.jello.module.impl.movement.Jesus;
import com.mentalfrostbyte.jello.module.impl.movement.Step;
import com.mentalfrostbyte.jello.module.impl.world.Disabler;
import com.mentalfrostbyte.jello.module.impl.world.Timer;
import com.mentalfrostbyte.jello.module.impl.world.disabler.HypixelPredictionDisabler;
import com.mentalfrostbyte.jello.module.settings.impl.BooleanSetting;
import com.mentalfrostbyte.jello.util.MultiUtilities;
import com.mentalfrostbyte.jello.util.player.MovementUtil;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.potion.Effects;
import team.sdhq.eventBus.annotations.EventTarget;
import team.sdhq.eventBus.annotations.priority.HigherPriority;
import team.sdhq.eventBus.annotations.priority.LowerPriority;

// pasted from titties client, as requested by AwayXD (lol)
public class HypixelNewSpeed extends Module {
//    public static boolean canLowHop = false;
    private int fallTicks;

    public HypixelNewSpeed() {
        super(ModuleCategory.MOVEMENT, "HypixelNew", "Speed for Hypixel");
    }

    @EventTarget
    public void onTick(EventPlayerTick __) {
        if (mc.player == null) return;
        fallTicks = mc.player.isOnGround() ? 0 : ++fallTicks;
    }

    @EventTarget
    public void onUpdate(EventUpdateWalkingPlayer event) {
        if (mc.player != null && mc.world != null && !mc.player.isInWater() && !mc.player.isSpectator()) {
            if (Client.getInstance().moduleManager.getModuleByClass(Disabler.class).isEnabled() && HypixelPredictionDisabler.watchDogDisabled) {
            } else if (mc.player.isOnGround()) {
                if (mc.player.isOnGround()) {
                    if (MovementUtil.isMoving()) {
                        mc.player.jump();
                    }

                    if (mc.player.isPotionActive(Effects.SPEED)) {
                        MovementUtil.strafe(
                                0.46F
                                        + (
                                        ((float)mc.player.getActivePotionEffect(Effects.SPEED).getAmplifier() + 1.0F) * (float)mc.player.getActivePotionEffect(Effects.SPEED).getAmplifier()
                                                == 0.0F
                                                ? 0.036F
                                                : 0.12F
                                )
                        );
                    } else {
                        MovementUtil.strafe(0.467F);
                    }
                } else if (!mc.player.collidedHorizontally && !mc.player.isPotionActive(Effects.JUMP_BOOST) && mc.player.hurtTime == 0) {
                    double baseVelocityY = mc.player.getMotion().y;
                    switch (fallTicks) {
                        case 1:
                            MovementUtil.strafe(0.33F);
                            if (mc.player.isPotionActive(Effects.SPEED)) {
                                MovementUtil.strafe(0.36F);
                            }

                            event.setY(baseVelocityY + 0.057);
                            MovementUtil.setPlayerYMotion(event.getY());
                        case 2:
                        default:
                            break;
                        case 3:
                            event.setY(baseVelocityY - 0.1309);
                            MovementUtil.setPlayerYMotion(event.getY());
                            break;
                        case 4:
                            event.setY(baseVelocityY - 0.2);
                            MovementUtil.setPlayerYMotion(event.getY());
                    }
                }
                if (MovementUtil.isMoving()) {
                    mc.player.jump();
                }

                if (mc.player.isPotionActive(Effects.SPEED)) {
                    MovementUtil.strafe(
                            0.46F
                                    + (
                                    ((float)mc.player.getActivePotionEffect(Effects.SPEED).getAmplifier() + 1.0F) * (float)mc.player.getActivePotionEffect(Effects.SPEED).getAmplifier()
                                            == 0.0F
                                            ? 0.036F
                                            : 0.12F
                            )
                    );
                } else {
                    MovementUtil.strafe(0.465F);
                }
            }
        }
    }

}