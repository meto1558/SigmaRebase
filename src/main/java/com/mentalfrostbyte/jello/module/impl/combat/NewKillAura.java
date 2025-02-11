package com.mentalfrostbyte.jello.module.impl.combat;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.CancellableEvent;
import com.mentalfrostbyte.jello.event.impl.game.EventRunTick;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventUpdateWalkingPlayer;
import com.mentalfrostbyte.jello.event.impl.player.rotation.EventRotation;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.settings.Setting;
import com.mentalfrostbyte.jello.module.settings.SettingObserver;
import com.mentalfrostbyte.jello.module.settings.impl.BooleanSetting;
import com.mentalfrostbyte.jello.module.settings.impl.ModeSetting;
import com.mentalfrostbyte.jello.module.settings.impl.NumberSetting;
import com.mentalfrostbyte.jello.util.game.player.PlayerUtil;
import com.mentalfrostbyte.jello.util.game.player.combat.CombatUtil;
import com.mentalfrostbyte.jello.util.game.player.combat.RotationUtil;
import com.mentalfrostbyte.jello.util.game.player.combat.SimpleCounter;
import com.mentalfrostbyte.jello.util.game.player.constructor.Rotation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.util.Hand;
import team.sdhq.eventBus.annotations.EventTarget;
import team.sdhq.eventBus.annotations.priority.HighestPriority;

import java.util.ArrayList;
import java.util.List;

public class NewKillAura extends Module {

    public final ModeSetting attackMode = new ModeSetting("Mode", "Mode", 0, "Single", "Switch");
    public final ModeSetting swingMode = new ModeSetting("Swing Mode", "Swing mode", 0, "Legit", "Blatant");
    public final ModeSetting sortMode = new ModeSetting("Sort Mode", "Sort mode", 0, "Range", "Health", "Armor", "Ticks existed", "Hurt-time");

    private final NumberSetting<Float> attackRange = new NumberSetting<>("Attack Range", "Attack range value", 3.4F, Float.class, 2.8F, 8.0F, 0.01F);
    private final NumberSetting<Float> searchRange = new NumberSetting<>("Search Range", "Search range value", 4.2F, Float.class, 2.8F, 8.0F, 0.01F);
    private final NumberSetting<Long> minCPS = new NumberSetting<>("Min CPS", "Min CPS value", 8, Long.class, 1, 20, 1.0F);
    private final NumberSetting<Long> maxCPS = new NumberSetting<>("Max CPS", "Max CPS value", 8, Long.class, 1, 20, 1.0F);

    private final BooleanSetting noSwing = new BooleanSetting("No swing", "Hit without swinging", false);
    private final BooleanSetting players = new BooleanSetting("Players", "Hit players", true);
    private final BooleanSetting animals = new BooleanSetting("Animals", "Hit animals", false);
    private final BooleanSetting monsters = new BooleanSetting("Monsters", "Hit monsters", false);
    private final BooleanSetting invisibles = new BooleanSetting("Invisible", "Hit invisible entities", true);
    private final BooleanSetting cooldown = new BooleanSetting("Cooldown", "Use attack cooldown (1.9+)", true);

    public NewKillAura() {
        super(ModuleCategory.COMBAT, "NewKillAura", "Attacks entities in range.");
        maxCPS.addObserver(setting -> {
            NumberSetting<Long> _t = (NumberSetting<Long>) setting;
            if (_t.currentValue < minCPS.currentValue) {
                _t.currentValue = minCPS.currentValue;
            }
        });

        minCPS.addObserver(setting -> {
            NumberSetting<Long> _t = (NumberSetting<Long>) setting;
            if (_t.currentValue > maxCPS.currentValue) {
                _t.currentValue = maxCPS.currentValue;
            }
        });

        registerSetting(attackMode, swingMode, sortMode, attackRange, searchRange, minCPS, maxCPS, noSwing, players, animals, monsters, invisibles, cooldown);
    }

    private final SimpleCounter attackCounter = new SimpleCounter();

    public List<LivingEntity> list = new ArrayList<>();

    @Override
    public void onDisable() {
        super.onDisable();

        Client.getInstance().rotationManager.target = null;
    }

    private long calculateAttackDelay() {
        long cps = (minCPS.currentValue.longValue() + maxCPS.currentValue.longValue()) / 2;
        return 1000 / cps;
    }

    @EventTarget
    @HighestPriority
    public void onRots(EventRotation event) {
        if (Client.getInstance().rotationManager.shouldRotate()) {
            Client.getInstance().rotationManager.rotations = RotationUtil.getRotations(Client.getInstance().rotationManager.target, true);

            Client.getInstance().rotationManager.rotations.lastYaw = event.yaw;
            Client.getInstance().rotationManager.rotations.lastPitch = event.pitch;

            event.yaw = Client.getInstance().rotationManager.rotations.yaw;
            event.pitch = Client.getInstance().rotationManager.rotations.pitch;

            Client.getInstance().rotationManager.rotations.yaw = event.yaw;
            Client.getInstance().rotationManager.rotations.pitch = event.pitch;
        }
    }

    @EventTarget
    public void onRunTick(EventRunTick event) {
        if (mc.player != null && event.state == CancellableEvent.EventState.PRE && Client.getInstance().rotationManager.target != null) {
            switch (attackMode.currentValue) {
                case "Single":
                    if ((cooldown.currentValue && mc.player.getCooledAttackStrength(0.5F) >= 1.0 || attackCounter.hasElapsed(calculateAttackDelay(), true)) && Client.getInstance().rotationManager.target.getDistance(mc.player) <= attackRange.currentValue) {
                        if (!noSwing.currentValue)
                            mc.player.swingArm(Hand.MAIN_HAND);
                        else
                            mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));

                        switch (swingMode.currentValue) {
                            case "Legit":
                                mc.clickMouse();
                                break;
                            case "Blatant":
                                mc.playerController.attackEntity(mc.player, Client.getInstance().rotationManager.target);
                                break;
                        }
                    }
                    break;
            }
        }
    }

    @EventTarget
    public void onUpdate(EventUpdateWalkingPlayer event) {
        list.clear();

        PlayerUtil.getAllEntitiesInWorld().stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(e -> CombatUtil.isValid(e, true, searchRange.currentValue, players.currentValue, animals.currentValue, monsters.currentValue, invisibles.currentValue))
                .sorted(CombatUtil.getComparatorForSorting(sortMode.currentValue))
                .forEachOrdered(list::add);

        Client.getInstance().rotationManager.target = list.isEmpty() ? null : list.get(0);
    }
}