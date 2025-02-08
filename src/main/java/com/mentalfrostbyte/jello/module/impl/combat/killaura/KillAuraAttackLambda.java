package com.mentalfrostbyte.jello.module.impl.combat.killaura;

import com.mentalfrostbyte.jello.Client;
import com.mentalfrostbyte.jello.gui.base.JelloPortal;
import com.mentalfrostbyte.jello.module.impl.combat.Criticals;
import com.mentalfrostbyte.jello.module.impl.combat.KillAura;
import com.mentalfrostbyte.jello.util.game.world.EntityUtil;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;

public class KillAuraAttackLambda implements Runnable {
    public final float expandAmount;
    public final KillAura killauraModule;

    public KillAuraAttackLambda(KillAura killaura, float expandAmount) {
        this.killauraModule = killaura;
        this.expandAmount = expandAmount;
    }

    private void handleAnimationAndAttack(Minecraft mc, Entity entity, boolean isOnePointEight) {
        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(12),
                mc.player.getHeldItem(Hand.MAIN_HAND)) > 0) {
            mc.particles.addParticleEmitter(entity, ParticleTypes.ENCHANTED_HIT);
        }

        boolean canSwing = (double) mc.player.getCooledAttackStrength(0.5F) >= 1.0 || isOnePointEight;
        boolean attackable = canSwing
                && mc.player.fallDistance > 0.0F
                && !mc.player.onGround
                && !mc.player.isOnLadder()
                && !mc.player.isInWater()
                && !mc.player.isPotionActive(Effects.BLINDNESS)
                && !mc.player.isPassenger();

        if (attackable
                || mc.player.onGround && Client.getInstance().moduleManager.getModuleByClass(Criticals.class).isEnabled()) {
            mc.particles.addParticleEmitter(entity, ParticleTypes.CRIT);
        }

        mc.playerController.attackEntity(mc.player, entity);
    }

    @Override
    public void run() {
        boolean var3 = (float) Math.round((float) Math.random() * 100.0F) <= this.killauraModule
                .getNumberValueBySettingName("Hit Chance");

        float range = Math.max(KillAura.mc.player.getDistance(KillAura.currentTimedEntity.getEntity()),
                this.killauraModule.getNumberValueBySettingName("Range"));

        EntityRayTraceResult rayTraceResult;
        if (!this.killauraModule.getStringSettingValueByName("Attack Mode").equals("Pre")) {
            rayTraceResult = EntityUtil.rayTraceFromPlayer(
                    KillAura.getRotations(this.killauraModule).yaw, KillAura.getRotations(this.killauraModule).pitch, range,
                    (double) this.expandAmount);
        } else {
            double motionSpeed = Math.sqrt(
                    KillAura.mc.player.getMotion().x * KillAura.mc.player.getMotion().x
                            + KillAura.mc.player.getMotion().z * KillAura.mc.player.getMotion().z);
            rayTraceResult = EntityUtil.rayTraceFromPlayer(KillAura.getRotations2(this.killauraModule).yaw,
                    KillAura.getRotations2(this.killauraModule).pitch, range, (double) this.expandAmount + motionSpeed);
        }

        if (KillAura.currentTarget != null && KillAura.interactAB.isBlocking()
                && !this.killauraModule.getStringSettingValueByName("Autoblock Mode").equals("Vanilla")) {
            KillAura.interactAB.doUnblock();
        }

        String mode = this.killauraModule.getStringSettingValueByName("Mode");
        if (var3 && (rayTraceResult != null || !this.killauraModule.getBooleanValueFromSettingName("Raytrace")
                || mode.equals("Multi"))) {
            for (TimedEntity timedEnt : KillAura.targetEntities) {
                Entity entity = timedEnt.getEntity();
                if (rayTraceResult != null && this.killauraModule.getBooleanValueFromSettingName("Raytrace")
                        && !mode.equals("Multi")) {
                    entity = rayTraceResult.getEntity();
                }

                if (entity == null) {
                    return;
                }

                boolean noSwing = this.killauraModule.getBooleanValueFromSettingName("No swing");
                Minecraft mc = KillAura.mc;

                boolean raytrace = this.killauraModule.getBooleanValueFromSettingName("Raytrace");
                boolean walls = this.killauraModule.getBooleanValueFromSettingName("Through walls");
                boolean properTrace = EntityUtil.rayTraceEntity(mc.player, entity);
                boolean isOnePointEight = JelloPortal.getVersion().newerThan(ProtocolVersion.v1_8);

                if (raytrace) {
                    if (properTrace && !walls || walls) {
                        handleAnimationAndAttack(mc, entity, isOnePointEight);
                        mc.player.resetCooldown();

                        if (!noSwing) {
                            mc.player.swingArm(Hand.MAIN_HAND);
                        }

                        mc.getConnection().getNetworkManager().sendNoEventPacket(new CUseEntityPacket(entity, mc.player.isSneaking()));
                    } else {
                        KillAura.currentTarget = null;
                        KillAura.targetEntities = null;
                        KillAura.currentTimedEntity = null;
                    }
                } else {
                    handleAnimationAndAttack(mc, entity, isOnePointEight);
                    mc.player.resetCooldown();

                    if (!noSwing) {
                        mc.player.swingArm(Hand.MAIN_HAND);
                    }

                    mc.getConnection().getNetworkManager()
                            .sendNoEventPacket(new CUseEntityPacket(entity, mc.player.isSneaking()));
                }
            }

            if (mode.equals("Multi2")) {
                KillAura.setTargetIndex(this.killauraModule, KillAura.getTargetIndex(this.killauraModule) + 1);
            }
        } else if (!this.killauraModule.getBooleanValueFromSettingName("No swing")) {
            KillAura.mc.player.swingArm(Hand.MAIN_HAND);
        }

        if (KillAura.currentTarget != null && KillAura.interactAB.canBlock()
                && this.killauraModule.getStringSettingValueByName("Autoblock Mode").equals("Basic1")) {
            KillAura.interactAB.block(KillAura.currentTarget, KillAura.getRotations(this.killauraModule).yaw,
                    KillAura.getRotations(this.killauraModule).pitch);
        }
    }
}