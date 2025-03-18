package com.mentalfrostbyte.jello.module.impl.combat;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.player.EventPlayerTick;
import com.mentalfrostbyte.jello.event.impl.player.action.EventPlace;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventUpdateWalkingPlayer;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.impl.movement.BlockFly;
import com.mentalfrostbyte.jello.module.settings.impl.BooleanSetting;
import com.mentalfrostbyte.jello.module.settings.impl.ModeSetting;
import com.mentalfrostbyte.jello.module.settings.impl.NumberSetting;
import com.mentalfrostbyte.jello.util.client.rotation.RotationCore;
import com.mentalfrostbyte.jello.util.client.rotation.util.RotationHelper;
import com.mentalfrostbyte.jello.util.client.rotation.util.RotationUtils;
import com.mentalfrostbyte.jello.util.game.player.PlayerUtil;
import com.mentalfrostbyte.jello.util.game.player.constructor.Rotation;
import com.mentalfrostbyte.jello.util.game.world.EntityUtil;
import com.mentalfrostbyte.jello.util.system.math.counter.Counter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import team.sdhq.eventBus.annotations.EventTarget;
import team.sdhq.eventBus.annotations.priority.HighestPriority;

import java.util.ArrayList;
import java.util.List;

public class NewAura extends Module {

    private final ModeSetting sortMode = new ModeSetting("Sort mode", "In what order should entities be sorted in?", 0, "Range", "Health", "Armor", "Ticks");

    private final ModeSetting clickMode = new ModeSetting("Click mode", "What should be the clicking mode?", 0, "CPS", "1.9");
    private final NumberSetting<Long> minCPS = new NumberSetting<>("Min CPS", "What should be the minimal CPS?", 10, Long.class, 1, 20, 1.0f);
    private final NumberSetting<Long> maxCPS = new NumberSetting<>("Max CPS", "What should be the maximal CPS?", 11, Long.class, 2, 21, 1.0f);

    private final NumberSetting<Float> searchRange = new NumberSetting<>("Search range", "What should be the search range for entities?", 4, Float.class, 1, 7, 0.1f);
    private final NumberSetting<Float> attackRange = new NumberSetting<>("Attack range", "What should be the attack range for entities?", 3.4f, Float.class, 1, 6, 0.1f);

    private final BooleanSetting players = new BooleanSetting("Players", "Should aura attack players?", true);
    private final BooleanSetting monsters = new BooleanSetting("Monsters", "Should aura target monsters?", false);
    private final BooleanSetting animals = new BooleanSetting("Animals", "Should aura target animals?", false);
    private final BooleanSetting invisibles = new BooleanSetting("Invisibles", "Should aura target invisible entities?", false);
    private final BooleanSetting throughWalls = new BooleanSetting("Through walls", "Should aura attack entities through walls?", false);
    private final BooleanSetting raycast = new BooleanSetting("Raycast", "Should aura raycast a line to each target?", true);

    public NewAura() {
        super(ModuleCategory.COMBAT, "NewAura", "Attacks entities.");
        registerSetting(
                sortMode, clickMode,
                minCPS
                        .hide(() -> clickMode.currentValue.equals("1.9"))
                        .addObserver((a) -> {
                            if (minCPS.currentValue >= maxCPS.currentValue) {
                                minCPS.setCurrentValue(maxCPS.getCurrentValue());
                            }
                        }),
                maxCPS
                        .hide(() -> clickMode.currentValue.equals("1.9"))
                        .addObserver((a) -> {
                            if (maxCPS.currentValue <= minCPS.currentValue) {
                                maxCPS.setCurrentValue(minCPS.getCurrentValue());
                            }
                        }),
                searchRange, attackRange,
                players, monsters, animals, invisibles,
                throughWalls, raycast
        );
    }

    private final Counter attackCounter = new Counter();
    private LivingEntity target;
    private List<LivingEntity> targets = new ArrayList<>();
    private Rotation rots;

    @Override
    public void onEnable() {
        super.onEnable();

        target = null;
        attackCounter.reset();
        rots = new Rotation(mc.player.rotationYaw, mc.player.rotationPitch);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        rots = null;
        target = null;
        attackCounter.reset();
    }

    @EventTarget
    @HighestPriority
    public void onTick(EventPlayerTick event) {
        if (Client.getInstance().moduleManager.getModuleByClass(BlockFly.class).enabled)
            return;

        if (target != null && !target.isAlive()) {
            target = null;
        }

        targets = PlayerUtil.getAllEntitiesInWorld().stream()
                .filter(entity -> {
                    if (!(entity instanceof LivingEntity livingEntity)) return false;

                    boolean isPlayer = entity instanceof PlayerEntity && this.players.currentValue;
                    boolean isAnimal = (entity instanceof AnimalEntity || entity instanceof VillagerEntity) && this.animals.currentValue;
                    boolean isMonster = entity instanceof MonsterEntity && this.monsters.currentValue;
                    boolean isValid = isPlayer || isAnimal || isMonster;

                    boolean withinRange = mc.player.getDistance(entity) <= searchRange.currentValue;

                    return livingEntity.getHealth() > 0
                            && entity != mc.player
                            && entity.isLiving()
                            && (invisibles.currentValue || !entity.isInvisible())
                            && entity.getName() != null
                            && entity.getDisplayName() != null
                            && !Client.getInstance().friendManager.isFriend(entity)
                            && !Client.getInstance().botManager.isBot(entity)
                            && isValid
                            && withinRange;
                })
                .map(entity -> (LivingEntity) entity)
                .sorted(EntityUtil.sortEntities(sortMode.currentValue))
                .toList();

        if (!targets.isEmpty()) {
            target = targets.get(0);
        }

        if (target != null) {
            Rotation rots = RotationUtils.getAdvancedRotation(target, !raycast.currentValue);
            if (rots != null) {
                this.rots.yaw = rots.yaw;
                this.rots.pitch = rots.pitch;
            }
        }
    }

    @EventTarget
    @HighestPriority
    public void onMotion(EventUpdateWalkingPlayer event) {
        if (Client.getInstance().moduleManager.getModuleByClass(BlockFly.class).enabled)
            return;

        if (target != null) {
            event.setYaw(rots.yaw);
            event.setPitch(rots.pitch);
        }
    }

    //mainly taken from vanta, no randomisation, smoothing at all
    @EventTarget
    @HighestPriority
    public void onPlace(EventPlace event) {
        if (Client.getInstance().moduleManager.getModuleByClass(BlockFly.class).enabled)
            return;

        if (target != null && target.getDistance(mc.player) <= attackRange.currentValue) {
            if (throughWalls.currentValue && !mc.player.canEntityBeSeen(target)) {
                return;
            }
            switch (clickMode.currentValue) {
                case "CPS" -> {
                    if (attackCounter.hasElapsed(calculateAttackDelay(), true)) {
                        mc.player.swingArm(Hand.MAIN_HAND);
                        mc.playerController.attackEntity(mc.player, target);
                    }
                }
                case "1.9" -> {
                    if (mc.player.getCooledAttackStrength(0) >= 1) {
                        mc.player.swingArm(Hand.MAIN_HAND);
                        mc.playerController.attackEntity(mc.player, target);
                    }
                }
            }
        }
    }

    private long calculateAttackDelay() {
        long cps = (minCPS.currentValue.longValue() + maxCPS.currentValue.longValue()) / 2;
        return 1000 / cps;
    }
}
