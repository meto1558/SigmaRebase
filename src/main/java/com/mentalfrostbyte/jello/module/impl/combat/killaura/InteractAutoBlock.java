package com.mentalfrostbyte.jello.module.impl.combat.killaura;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.util.game.player.rotation.util.RotationHelper;
import com.mentalfrostbyte.jello.util.game.player.combat.CombatUtil;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.impl.combat.Teams;
import com.mentalfrostbyte.jello.module.impl.combat.killaura.sorters.*;
import com.mentalfrostbyte.jello.module.impl.player.Blink;
import com.mentalfrostbyte.jello.util.game.player.constructor.Rotation;
import com.mentalfrostbyte.jello.util.game.world.EntityUtil;
import com.mentalfrostbyte.jello.util.system.other.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import org.jetbrains.annotations.NotNull;
import net.minecraft.util.math.vector.Vector3d;

import java.util.*;

public class InteractAutoBlock {
    private float[] clicks;
    private final Module parent;
    public Minecraft mc = Minecraft.getInstance();
    public boolean blocking;
    public HashMap<Entity, List<Pair<Vector3d, Long>>> field44349 = new HashMap<>();

    public InteractAutoBlock(Module parent) {
        this.parent = parent;
        this.handleCPSSettingChange();
    }

    public boolean isBlocking() {
        return this.blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public void block(Entity var1, float var2, float var3) {
        if (this.parent.getBooleanValueFromSettingName("Interact autoblock")) {
            EntityRayTraceResult var6 = EntityUtil.method17714(
                    !this.parent.getBooleanValueFromSettingName("Raytrace") ? var1 : null, var2, var3, var0 -> true,
					this.parent.getNumberValueBySettingName("Range"));
            if (var6 != null) {
                this.mc
                        .getConnection()
                        .sendPacket(new CUseEntityPacket(var6.getEntity(), Hand.MAIN_HAND, var6.getHitVec(),
                                this.mc.player.isSneaking()));
                // this.mc.getConnection().sendPacket(
                //      new CUseEntityPacket(var6.getEntity(), Hand.MAIN_HAND, this.mc.player.isSneaking())); stop sending dupe packets please Sigma
            }
        }

        CombatUtil.block();
        this.setBlocking(true);
    }

    public void doUnblock() {
        CombatUtil.unblock();
        this.setBlocking(false);
    }

    public boolean canBlock() {
        String settingValue = this.parent.getStringSettingValueByName("Autoblock Mode");
        return settingValue != null && !settingValue.equals("None")
                && Objects.requireNonNull(this.mc.player).getHeldItemMainhand().getItem() instanceof SwordItem
                && !this.isBlocking();
    }

    public void handleCPSSettingChange() {
        this.clicks = new float[3];
        float minCPT = 20.0F / this.parent.getNumberValueBySettingName("Min CPS");
        float maxCPT = 20.0F / this.parent.getNumberValueBySettingName("Max CPS");
        if (minCPT > maxCPT) {
            float mCPT = minCPT;
            minCPT = maxCPT;
            maxCPT = mCPT;
        }

        for (int i = 0; i < 3; i++) {
            float rand = minCPT + (float) Math.random() * (maxCPT - minCPT);
            this.clicks[i] = rand;
        }
    }

    public float getInitialDelay(int var1) {
        return var1 >= 0 && var1 < this.clicks.length ? this.clicks[var1] : -1.0F;
    }

    public boolean method36820(int var1) {
        int var4 = (int) this.getInitialDelay(0) - var1;
        boolean var5 = this.parent.getStringSettingValueByName("Attack Mode").equals("Pre");
        if (!var5) {
            var4++;
        }

        if (this.mc.player.getCooldownPeriod() > 1.26F && this.parent.getBooleanValueFromSettingName("Cooldown")) {
            int var11 = !var5 ? 1 : 2;
            float var12 = this.mc.player.getCooldownPeriod() - (float) this.mc.player.ticksSinceLastSwing
                    - (float) var11;
            return var12 <= 0.0F && var12 > -1.0F;
        } else if (var4 != 2) {
            if (var4 < 2) {
                float var6 = this.getInitialDelay(0);
                float var7 = this.getInitialDelay(1);
                float var8 = this.getInitialDelay(1);
                int var9 = (int) (var7 + var6 - (float) ((int) var6));
                if (var4 + var9 == 2) {
                    return true;
                }

                if (var4 + var9 == 1) {
                    float var10 = var6 + var7 + var8 - (float) ((int) var6 + (int) var7 + (int) var8);
                    return var10 < 1.0F;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    public boolean canAttack(int cps) {
        return cps >= (int) this.clicks[0];
    }

    public float[] getCPTs() {
        float minCPT = 20.0F / this.parent.getNumberValueBySettingName("Min CPS");
        float maxCPT = 20.0F / this.parent.getNumberValueBySettingName("Max CPS");
        if (minCPT > maxCPT) {
            float mCPT = minCPT;
            minCPT = maxCPT;
            maxCPT = mCPT;
        }
        return new float[]{minCPT, maxCPT};
    }

    public void setupDelay() {
        var CPTs = this.getCPTs();
        float minCPT = CPTs[0];
        float maxCPT = CPTs[1];

        float var8 = this.clicks[0] - (float) ((int) this.clicks[0]);
        this.clicks[0] = this.clicks[1] + var8;

        for (int var6 = 1; var6 < 3; var6++) {
            float var7 = minCPT + (float) Math.random() * (maxCPT - minCPT);
            this.clicks[1] = var7;
        }
    }

    public List<TimedEntity> getEntitiesInRange(float range) {
        ArrayList<TimedEntity> timedEntityList = new ArrayList<>();

        for (Entity ent : EntityUtil.getEntitesInWorld(__ -> true)) {
            timedEntityList.add(new TimedEntity(ent));
        }

        Iterator<TimedEntity> iterator = timedEntityList.iterator();

        var filtered = timedEntityList.stream().filter(targetData -> {
            var entity = targetData.getEntity();
            return !(entity == this.mc.player
                    || entity == Blink.clientPlayerEntity);
        }).filter(targetData -> {
            var entity = targetData.getEntity();
            return (entity instanceof LivingEntity le && le.getHealth() >= 0 && mc.player.canAttack(le)) && !(entity instanceof ArmorStandEntity);
        }).filter(targetData -> Objects.equals(mc.player.getRidingEntity(), targetData.getEntity())).filter(
                targetData -> {
            var entity = targetData.getEntity();
            return (!this.parent.getBooleanValueFromSettingName("Players") && entity instanceof PlayerEntity)
                    || (!this.parent.getBooleanValueFromSettingName("Invisible") && entity.isInvisible())
                    || (!this.parent.getBooleanValueFromSettingName("Animals") && (entity instanceof AnimalEntity || entity instanceof VillagerEntity))
                    || !this.parent.getBooleanValueFromSettingName("Monsters") && entity instanceof MonsterEntity;
        }).filter(targetData -> {
            var entity = targetData.getEntity();
            return (entity instanceof PlayerEntity && !Client.getInstance().botManager.isBot(entity));
        }).filter(targetData -> {
            var entity = targetData.getEntity();
            return !Client.getInstance().friendManager.isFriendPure(entity);
        }).filter(targetData -> {
            var entity = targetData.getEntity();
            return mc.player.getDistance(entity) <= range;
        }).toList();

        for (var targetData : timedEntityList) {
            Entity entity = targetData.getEntity();
            if (entity instanceof PlayerEntity && Client.getInstance().botManager.isBot(entity)) {
                iterator.remove();
            } if (!this.parent.getBooleanValueFromSettingName("Monsters") && entity instanceof MonsterEntity) {
                iterator.remove();
            } else if (this.mc.player.getRidingEntity() != null && this.mc.player.getRidingEntity().equals(entity)) {
                iterator.remove();
            } else if (entity.isInvulnerable()) {
                iterator.remove();
            } else if (!(entity instanceof PlayerEntity) || !CombatUtil.arePlayersOnSameTeam((PlayerEntity) entity) || !Client.getInstance().moduleManager.getModuleByClass(Teams.class).isEnabled()) {

                if (!this.parent.getBooleanValueFromSettingName("Through walls")) {
                    Rotation var28 = RotationHelper.getRotations(entity, true);
                    if (var28 == null) {
                        iterator.remove();
                    }
                }

            } else {
                iterator.remove();
            }
        }
        return filtered;
    }

    private @NotNull AxisAlignedBB getAxisAlignedBBThing(Pair<Vector3d, Long> var30) {
        Vector3d k = var30.getKey();
        double var19 = 0.15;
		return new AxisAlignedBB(
				k.x - var19,
				k.y,
				k.z - var19,
				k.x + var19,
				k.y + this.mc.player.getBoundingBox().getYSize(),
				k.z + var19);
    }

    public List<TimedEntity> sortEntities(List<TimedEntity> timedEntities) {
        switch (this.parent.getStringSettingValueByName("Sort Mode")) {
            case "Range":
                timedEntities.sort(new RangeSorter(this));
                break;
            case "Health":
                timedEntities.sort(new HealthSorter(this));
                break;
            case "Angle":
                timedEntities.sort(new AngleSorter(this));
                break;
            case "Prev Range":
                timedEntities.sort(new PrevRangeSorter(this));
                break;
            case "Armor":
                timedEntities.sort(new ArmorSorter(this));
        }

        timedEntities.sort(new FriendSorter(this));
        return timedEntities;
    }
}