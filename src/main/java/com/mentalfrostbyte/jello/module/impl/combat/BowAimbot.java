package com.mentalfrostbyte.jello.module.impl.combat;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.CancellableEvent;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender3D;
import com.mentalfrostbyte.jello.event.impl.player.rotation.EventRotation;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.impl.combat.bowaimbot.BowAngleSorter;
import com.mentalfrostbyte.jello.module.impl.combat.bowaimbot.BowRangeSorter;
import com.mentalfrostbyte.jello.module.settings.impl.BooleanSetting;
import com.mentalfrostbyte.jello.module.settings.impl.ModeSetting;
import com.mentalfrostbyte.jello.module.settings.impl.NumberSetting;
import com.mentalfrostbyte.jello.util.game.player.PlayerUtil;
import com.mentalfrostbyte.jello.util.game.player.combat.CombatUtil;
import com.mentalfrostbyte.jello.util.game.player.combat.RotationUtil;
import com.mentalfrostbyte.jello.util.game.player.constructor.Rotation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import team.sdhq.eventBus.annotations.EventTarget;
import team.sdhq.eventBus.annotations.priority.HighestPriority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class BowAimbot extends Module {
    private List<Entity> field23754 = new ArrayList<Entity>();

    public BowAimbot() {
        super(ModuleCategory.COMBAT, "BowAimbot", "Automatically aims at players while using a bow");
        this.registerSetting(new ModeSetting("Sort mode", "Sort mode", 0, "Angle", "Range"));
        this.registerSetting(new NumberSetting<Float>("Range", "Range value", 70.0F, Float.class, 10.0F, 100.0F, 1.0F));
        this.registerSetting(new BooleanSetting("Silent", "Server-sided rotations.", false));
        this.registerSetting(new BooleanSetting("Teams", "Target team", true));
        this.registerSetting(new BooleanSetting("Players", "Target players", true));
        this.registerSetting(new BooleanSetting("Animals/Monsters", "Target animals and monsters", false));
        this.registerSetting(new BooleanSetting("Anti-Bot", "Doesn't target bots", true));
        this.registerSetting(new BooleanSetting("Invisible", "Target invisible entites", true));
    }

    @Override
    public void onDisable() {
        this.field23754.clear();
    }

    @EventTarget
    @HighestPriority
    public void onRotation(EventRotation event) {
        if (this.isEnabled() && event.state == CancellableEvent.EventState.PRE) {
            if (!(mc.player.getActiveItemStack().getItem() instanceof BowItem)) {
                this.field23754.clear();
            } else {
                this.field23754 = this.validEntity(this.access().getNumberValueBySettingName("Range"));
            }

            if (!this.field23754.isEmpty() && this.getBooleanValueFromSettingName("Silent")) {
                Client.getInstance().rotationManager.setRotations(RotationUtil.method34146((LivingEntity) this.field23754.get(0)), event);
            }
        }
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (this.isEnabled() && !this.getBooleanValueFromSettingName("Silent")) {
            if (!this.field23754.isEmpty()) {
                Rotation rots = RotationUtil.method34146((LivingEntity) this.field23754.get(0));
                mc.player.rotationYaw = rots.yaw;
                mc.player.rotationPitch = rots.pitch;
            }
        }
    }

    public List<Entity> validEntity(float distance) {
        List<Entity> entities = PlayerUtil.getAllEntitiesInWorld();
        Iterator<Entity> iter = entities.iterator();

        while (iter.hasNext()) {
            Entity livingEntity = iter.next();
            if (livingEntity == mc.player) {
                iter.remove();
            } else if (Client.getInstance().friendManager.method26997(livingEntity)) {
                iter.remove();
            } else if (!(livingEntity instanceof LivingEntity)) {
                iter.remove();
            } else if (((LivingEntity) livingEntity).getHealth() == 0.0F) {
                iter.remove();
            } else if (mc.player.getDistance(livingEntity) > distance) {
                iter.remove();
            } else if (!mc.player.canAttack((LivingEntity) livingEntity)) {
                iter.remove();
            } else if (livingEntity instanceof ArmorStandEntity) {
                iter.remove();
            } else if (!this.getBooleanValueFromSettingName("Players") && livingEntity instanceof PlayerEntity) {
                iter.remove();
            } else if (this.getBooleanValueFromSettingName("Anti-Bot") && livingEntity instanceof PlayerEntity && Client.getInstance().combatManager.isTargetABot(livingEntity)) {
                iter.remove();
            } else if (!this.getBooleanValueFromSettingName("Invisible") && livingEntity.isInvisible()) {
                iter.remove();
            } else if (!this.getBooleanValueFromSettingName("Animals/Monsters") && !(livingEntity instanceof PlayerEntity)) {
                iter.remove();
            } else if (mc.player.getRidingEntity() != null && mc.player.getRidingEntity().equals(livingEntity)) {
                iter.remove();
            } else if (livingEntity.isInvulnerable()) {
                iter.remove();
            } else if (livingEntity instanceof PlayerEntity && CombatUtil.arePlayersOnSameTeam((PlayerEntity) livingEntity) && !this.getBooleanValueFromSettingName("Teams")) {
                iter.remove();
            }
        }

        String var8 = this.getStringSettingValueByName("Sort mode");
        switch (var8) {
            case "Range":
                Collections.sort(entities, new BowRangeSorter(this));
                break;
            case "Angle":
                Collections.sort(entities, new BowAngleSorter(this));
        }

        return entities;
    }
}