package com.mentalfrostbyte.jello.module.impl.combat;

import com.mentalfrostbyte.jello.Client;
import com.mentalfrostbyte.jello.event.impl.game.network.EventReceivePacket;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender2DOffset;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender3D;
import com.mentalfrostbyte.jello.event.impl.game.world.EventLoadWorld;
import com.mentalfrostbyte.jello.event.impl.player.EventPlayerTick;
import com.mentalfrostbyte.jello.event.impl.player.action.EventStopUseItem;
import com.mentalfrostbyte.jello.event.impl.player.movement.EventUpdateWalkingPlayer;
import com.mentalfrostbyte.jello.gui.base.Animation;
import com.mentalfrostbyte.jello.managers.util.notifs.Notification;
import com.mentalfrostbyte.jello.util.system.other.Pair;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.ModuleWithModuleSettings;
import com.mentalfrostbyte.jello.module.impl.combat.killaura.ExpirationTimer;
import com.mentalfrostbyte.jello.module.impl.combat.killaura.InteractAutoBlock;
import com.mentalfrostbyte.jello.module.impl.combat.killaura.KillAuraAttackLambda;
import com.mentalfrostbyte.jello.module.impl.combat.killaura.TimedEntity;
import com.mentalfrostbyte.jello.module.impl.movement.Jesus;
import com.mentalfrostbyte.jello.module.impl.movement.Speed;
import com.mentalfrostbyte.jello.module.impl.movement.Step;
import com.mentalfrostbyte.jello.module.settings.impl.BooleanSetting;
import com.mentalfrostbyte.jello.module.settings.impl.ColorSetting;
import com.mentalfrostbyte.jello.module.settings.impl.ModeSetting;
import com.mentalfrostbyte.jello.module.settings.impl.NumberSetting;
import com.mentalfrostbyte.jello.util.client.ClientColors;
import com.mentalfrostbyte.jello.util.game.world.EntityUtil;
import com.mentalfrostbyte.jello.util.game.player.MovementUtil2;
import com.mentalfrostbyte.jello.util.game.player.combat.RotationHelper;
import com.mentalfrostbyte.jello.util.game.player.constructor.Rotation;
import com.mentalfrostbyte.jello.util.game.player.combat.Rots;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.SwordItem;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;
import team.sdhq.eventBus.annotations.EventTarget;
import team.sdhq.eventBus.annotations.priority.LowestPriority;

import java.util.*;
import java.util.Map.Entry;

import static com.mentalfrostbyte.jello.util.game.render.RenderUtil.drawCircle;

@SuppressWarnings({ "unused", "cast" })
public class KillAura extends Module {
    public static boolean isAuraActive = false;
    public static Entity currentTarget;
    public static TimedEntity currentTimedEntity;
    public static Rotation previousRotation = new Rotation(0.0F, 0.0F);
    public static int attackCooldown;
    public HashMap<Entity, Animation> entityGlowAnimations = new HashMap<>();
    public static InteractAutoBlock interactAB;
    private int attackDelay;
    private int blockDelay;
    private int groundTicks;
    private int targetIndex;
    private int currentItemIndex;
    private int swingDelay;
    private int blockCooldown;
    private int hitDelay;
    public static List<TimedEntity> targetEntities;
    private Rotation currentRotation;
    private Rotation secondaryRotation;
    private double randomOffset;
    private float rotationSpeed;
    private float rotationProgress;
    private float rotationAdjustment;
    private boolean isBlocking;
    private double[] positionOffset;
    float randomAngle = 0;

    public KillAura() {
        super(ModuleCategory.COMBAT, "KillAura", "Automatically attacks entities");
        this.registerSetting(
                new ModeSetting("Mode", "Mode", 0, "Single", "Switch", "Multi", "Multi2"));
        this.registerSetting(
                new ModeSetting("Autoblock Mode", "Autoblock Mode", 0, "None", "NCP", "Basic1", "Basic2", "Vanilla", "Fake"));
        this.registerSetting(
                new ModeSetting("Sort Mode", "Sort Mode", 0, "Range", "Health", "Angle", "Armor", "Prev Range"));
        this.registerSetting(
                new ModeSetting("Attack Mode", "Attacks after or before sending the movement", 0, "Pre", "Post"));
        this.registerSetting(new ModeSetting("Rotation Mode", "The way you will look at entities", 0, "NCP", "New", "Smooth", "LockView", "None"));
        this.registerSetting(new NumberSetting<>("Range", "Range value", 4.0F, Float.class, 2.8F, 8.0F, 0.01F));
        this.registerSetting(
                new NumberSetting<>("Block Range", "Block Range value", 4.0F, Float.class, 2.8F, 8.0F, 0.2F));
        this.registerSetting(
                new NumberSetting<>("Min CPS", "Min CPS value", 8.0F, Float.class, 1.0F, 20.0F, 1.0F)
                        .addObserver(setting -> interactAB.method36818()));
        this.registerSetting(
                new NumberSetting<>("Max CPS", "Max CPS value", 8.0F, Float.class, 1.0F, 20.0F, 1.0F)
                        .addObserver(setting -> interactAB.method36818()));
        this.registerSetting(
                new NumberSetting<>("Hit box expand", "Hit Box expand", 0.05F, Float.class, 0.0F, 1.0F, 0.01F));
        this.registerSetting(new NumberSetting<>("Hit Chance", "Hit Chance", 100.0F, Float.class, 25.0F, 100.0F, 1.0F));
        this.registerSetting(new BooleanSetting("Interact auto block", "Send interact packet when blocking", true));
        this.registerSetting(new BooleanSetting("Players", "Hit players", true));
        this.registerSetting(new BooleanSetting("Animals", "Hit animals", false));
        this.registerSetting(new BooleanSetting("Monsters", "Hit monsters", false));
        this.registerSetting(new BooleanSetting("Invisible", "Hit invisible entities", true));
        this.registerSetting(new BooleanSetting("Raytrace", "Helps the aura become more legit", true));
        this.registerSetting(new BooleanSetting("Cooldown", "Use attack cooldown (1.9+)", false));
        this.registerSetting(new BooleanSetting("No swing", "Hit without swinging", false));
        this.registerSetting(new BooleanSetting("Disable on death", "Disable on death", true));
        this.registerSetting(new BooleanSetting("Through walls", "Target entities through walls", true));
        this.registerSetting(new BooleanSetting("Smart Reach", "Allows you to get more reach (depends on your ping)", true));
        this.registerSetting(new BooleanSetting("Silent", "Silent rotations", true));
        this.registerSetting(new BooleanSetting("ESP", "ESP on targets", true));
        this.registerSetting(new ColorSetting("ESP Color", "The render color", ClientColors.LIGHT_GREYISH_BLUE.getColor()));
    }

    private KillAuraAttackLambda attack;

    public static Rotation getRotations2(KillAura killaura) {
        return killaura.secondaryRotation;
    }

    public static Rotation getRotations(KillAura killaura) {
        return killaura.currentRotation;
    }

    public static int getTargetIndex(KillAura killaura) {
        return killaura.targetIndex;
    }

    public static void setTargetIndex(KillAura killaura, int targetIndex) {
        killaura.targetIndex = targetIndex;
    }

    @Override
    public void initialize() {
        super.initialize();

        targetEntities = new ArrayList<>();
        interactAB = new InteractAutoBlock(this);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        resetState();
        initializeRotations();
        interactAB.setBlocking(isHoldingSword() && isUseItemKeyDown());
    }

    private void resetState() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        targetEntities = new ArrayList<>();
        currentTarget = null;
        currentTimedEntity = null;
        attackDelay = 0;
        blockDelay = 0;
        targetIndex = 0;
        attackCooldown = 0;
        blockCooldown = -1;
        entityGlowAnimations.clear();
        if (mc.player.isOnGround()) {
            groundTicks = 1;
        }
    }

    private void initializeRotations() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        secondaryRotation = new Rotation(mc.player.rotationYaw, mc.player.rotationPitch);
        currentRotation = new Rotation(mc.player.rotationYaw, mc.player.rotationPitch);
        previousRotation = new Rotation(mc.player.rotationYaw, mc.player.rotationPitch);
        rotationProgress = -1.0F;
    }

    private boolean isHoldingSword() {
        if (mc.player == null || mc.world == null) {
            return false;
        }

        return mc.player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof SwordItem;
    }

    private boolean isUseItemKeyDown() {
        return mc.gameSettings.keyBindUseItem.isKeyDown();
    }

    @Override
    public void onDisable() {
        Rots.rotating = false;
        currentTarget = null;
        currentTimedEntity = null;
        targetEntities = null;
        isAuraActive = false;
        super.onDisable();
    }

    @EventTarget
    public void onWorldChange(EventLoadWorld event) {
        if (this.isEnabled() && this.getBooleanValueFromSettingName("Disable on death")) {
            Client.getInstance().notificationManager.send(new Notification("Aura", "Aura disabled due to respawn"));
            this.toggle();
        }
    }

    @EventTarget
    public void onTick(EventPlayerTick event) {
        if (currentTarget == null) {
            Rots.rotating = false;
        } else
            Rots.rotating = true;

        if (mc.player == null || mc.world == null) {
            return;
        }

        if (this.isEnabled()) {
            if (this.rotationProgress != -1.0F) {
                this.rotationProgress++;
            }

            if (this.getBooleanValueFromSettingName("Disable on death")) {
                if (!mc.player.isAlive()) {
                    this.toggle();
                    Client.getInstance().notificationManager
                            .send(new Notification("Aura", "Aura disabled due to death"));
                }
            }
        }
    }

    @EventTarget
    public void onStopuseItem(EventStopUseItem event) {
        if (this.isEnabled()) {
            if (!this.getStringSettingValueByName("Autoblock Mode").equals("None")
                    && (Objects.requireNonNull(mc.player).getHeldItemMainhand().getItem() instanceof SwordItem
                            || this.currentItemIndex != mc.player.inventory.currentItem)
                    && currentTarget != null) {
                event.setCancelled(true);
            } else if (Objects.requireNonNull(mc.player).getHeldItemMainhand().getItem() instanceof SwordItem) {
                this.swingDelay = 2;
            }
        }
    }

    @EventTarget
    @LowestPriority
    public void onUpdate(EventUpdateWalkingPlayer event) {
        if (!event.isPre()) {
            currentItemIndex = mc.player.inventory.currentItem;
            if (currentTarget != null && interactAB.canBlock() && currentRotation != null) {
                if (this.getStringSettingValueByName("Autoblock Mode").equals("Fake")) {
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(
                            mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(),
                            mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround
                    ));
                } else {
                    interactAB.block(currentTarget, currentRotation.yaw, currentRotation.pitch);
                    isBlocking = true;
                }
            }
            return;
        }
        if (interactAB.isBlocking() && (!(mc.player.getHeldItemMainhand().getItem() instanceof SwordItem) || currentTarget == null)) {
            if (!this.getStringSettingValueByName("Autoblock Mode").equals("Fake")) {
                interactAB.setBlocking(false);
                mc.getConnection().sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));

            }
        }

        if (currentTarget == null) {
            Rots.rotating = false;
        }

        if (!isEnabled() || mc.player == null) {
            return;
        }

        if (!event.isPre()) {
            currentItemIndex = mc.player.inventory.currentItem;

            if (currentTarget != null && interactAB.canBlock() && currentRotation != null) {
                interactAB.block(currentTarget, currentRotation.yaw, currentRotation.pitch);
            }
            return;
        }

        if (swingDelay > 0) {
            swingDelay--;
        }

        if (currentTarget != null
                && interactAB.isBlocking()
                && com.mentalfrostbyte.jello.util.game.player.MovementUtil.isMoving()
                && getStringSettingValueByName("Autoblock Mode").equals("NCP")) {
            interactAB.doUnblock();
        }

        if (interactAB.isBlocking()
                && (!(mc.player.getHeldItemMainhand().getItem() instanceof SwordItem) || currentTarget == null)) {
            interactAB.setBlocking(false);
        }

        if (blockCooldown >= 0) {
            if (blockCooldown == 0) {
                interactAB.doUnblock();
                interactAB.setBlocking(true);
            }
            blockCooldown--;
        }

        handleAura();

        if (targetEntities == null || targetEntities.isEmpty()) {
            return;
        }

        attackDelay++;

        float hitboxExpand = getNumberValueBySettingName("Hit box expand");
        ModuleWithModuleSettings criticalsModule = (ModuleWithModuleSettings) Client.getInstance().moduleManager
                .getModuleByClass(Criticals.class);

        if (criticalsModule.isEnabled()
                && criticalsModule.getStringSettingValueByName("Type").equalsIgnoreCase("Minis")) {
            handleEventUpdate(
                    event,
                    criticalsModule.getModWithTypeSetToName().getStringSettingValueByName("Mode"),
                    criticalsModule.getModWithTypeSetToName().getBooleanValueFromSettingName("Avoid Fall Damage"));
        }

        setRotation();

        if (event.getYaw() - mc.player.rotationYaw != 0.0F) {
            currentRotation.yaw = event.getYaw();
            currentRotation.pitch = event.getPitch();
        }

        if (currentTarget != null) {
            Rots.prevYaw = currentRotation.yaw;
            Rots.prevPitch = currentRotation.pitch;
            event.setYaw(currentRotation.yaw);
            event.setPitch(currentRotation.pitch);
            Rots.yaw = currentRotation.yaw;
            Rots.pitch = currentRotation.pitch;

            mc.player.rotationYawHead = event.getYaw();
            mc.player.renderYawOffset = event.getYaw();
        }

        boolean canAttack = interactAB.method36821(attackDelay);
        float attackCooldownThreshold = (mc.player.getCooldownPeriod() < 1.26
                && getBooleanValueFromSettingName("Cooldown"))
                        ? mc.player.getCooledAttackStrength(0.0F)
                        : 1.0F;

        boolean shouldAttack = attackCooldown == 0 && canAttack && attackCooldownThreshold >= 1.0F;

        if (canAttack) {
            interactAB.setupDelay();
        }

        if (shouldAttack) {
            if (attack == null) {
                attack = new KillAuraAttackLambda(this, hitboxExpand);
            }
            boolean isPre = getStringSettingValueByName("Attack Mode").equals("Pre");
            if (!isPre) {
                event.attackPost(attack);
            } else {
                attack.run();
            }
            attackDelay = 0;
        }

        if (attackCooldown > 0) {
            attackCooldown--;
        }
    }

    @EventTarget
    public void onRender2D(EventRender2DOffset event) {

        if (mc.player == null || mc.world == null) {
            return;
        }

        if (currentTimedEntity == null || getBooleanValueFromSettingName("Silent")
                || getStringSettingValueByName("Rotation Mode").equals("None")) {
            return;
        }

        float interpolatedYaw = MathHelper.wrapDegrees(
                secondaryRotation.yaw + (currentRotation.yaw - secondaryRotation.yaw) * mc.getRenderPartialTicks());
        float interpolatedPitch = MathHelper.wrapDegrees(secondaryRotation.pitch
                + (currentRotation.pitch - secondaryRotation.pitch) * mc.getRenderPartialTicks());

        mc.player.rotationYaw = interpolatedYaw;
        mc.player.rotationPitch = interpolatedPitch;
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (targetEntities == null) {
            return;
        }

        Iterator<Entry<Entity, Animation>> animationIterator = entityGlowAnimations.entrySet().iterator();

        while (animationIterator.hasNext()) {
            Entry<Entity, Animation> entry = animationIterator.next();
            Animation animation = entry.getValue();

            animation.changeDirection(Animation.Direction.BACKWARDS);

            if (animation.calcPercent() == 0.5F) {
                animationIterator.remove();
            }
        }

        for (TimedEntity timedEntity : targetEntities) {
            if (timedEntity != null) {
                Entity entity = timedEntity.getEntity();

                if (!entityGlowAnimations.containsKey(entity)) {
                    entityGlowAnimations.put(entity, new Animation(250, 250));
                } else {
                    entityGlowAnimations.get(entity).changeDirection(Animation.Direction.FORWARDS);
                }
            }
        }

        for (Entry<Entity, Animation> entry : entityGlowAnimations.entrySet()) {
            renderTargetESP(entry.getKey());
        }
    }

    @EventTarget
    public void onReceivePacket(EventReceivePacket event) {

        if (mc.player == null || mc.world == null) {
            return;
        }

        IPacket<?> packet = event.getPacket();

        if (packet instanceof SEntityStatusPacket statusPacket) {

            if (statusPacket.getOpCode() == 3) {
                Entity entity = statusPacket.getEntity(mc.world);
                interactAB.field44349.remove(entity);
            }
            return;
        }

        if (packet instanceof SEntityPacket entityPacket) {
            if (entityPacket.func_229745_h_()
                    && (entityPacket.posX != 0 || entityPacket.posY != 0 || entityPacket.posZ != 0)) {
                for (Entry<Entity, List<Pair<Vector3d, Long>>> entry : interactAB.field44349.entrySet()) {
                    Entity trackedEntity = entry.getKey();
                    List<Pair<Vector3d, Long>> trackedPositions = entry.getValue();

                    if (entityPacket.getEntity(mc.world) == trackedEntity) {
                        Vector3d scaledVector = trackedEntity.field_242272_av.scale(2.4414062E-4F);
                        trackedPositions.add(new Pair<>(scaledVector, System.currentTimeMillis()));
                    }
                }
            }
        }
    }

    public void renderTargetESP(Entity targetEntity) {
        if (targetEntity == null || !targetEntity.isAlive()) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glLineWidth(1.4F);

        double partialTicks = Minecraft.getInstance().timer.renderPartialTicks;
        double interpolatedX = targetEntity.lastTickPosX
                + (targetEntity.getPosX() - targetEntity.lastTickPosX) * partialTicks;
        double interpolatedY = targetEntity.lastTickPosY
                + (targetEntity.getPosY() - targetEntity.lastTickPosY) * partialTicks;
        double interpolatedZ = targetEntity.lastTickPosZ
                + (targetEntity.getPosZ() - targetEntity.lastTickPosZ) * partialTicks;

        double cameraX = mc.gameRenderer.getActiveRenderInfo().getProjectedView().getX();
        double cameraY = mc.gameRenderer.getActiveRenderInfo().getProjectedView().getY();
        double cameraZ = mc.gameRenderer.getActiveRenderInfo().getProjectedView().getZ();

        GL11.glTranslated(interpolatedX - cameraX, interpolatedY - cameraY, interpolatedZ - cameraZ);

        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);

        long cycleDurationMs = 1425;
        float glowProgress = (float) (System.currentTimeMillis() % cycleDurationMs) / cycleDurationMs;
        boolean isFadingOut = glowProgress > 0.5F;
        glowProgress = !isFadingOut ? glowProgress * 2.0F : 1.0F - (glowProgress * 2.0F % 1.0F);

        GL11.glTranslatef(0.0F, (targetEntity.getHeight() + 0.4F) * glowProgress, 0.0F);

        float glowFactor = (float) Math.sin(glowProgress * Math.PI);
        Animation animation = this.entityGlowAnimations.get(targetEntity);
        if (animation != null) {
            drawCircle(isFadingOut, 0.45F * glowFactor, 0.6F, 0.35F * glowFactor, animation.calcPercent());
        }

        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    public boolean isAutoBlockEnabled() {
        if (mc.player == null) {
            return false;
        }

        return currentTarget != null && !mc.player.getHeldItemMainhand().isEmpty()
                && mc.player.getHeldItemMainhand().getItem() instanceof SwordItem &&
                !this.getStringSettingValueByName("Autoblock Mode").equals("None");
    }

    @Override
    public boolean isEnabled2() {
        return this.isEnabled() && this.isAutoBlockEnabled();
    }

    public void handleEventUpdate(EventUpdateWalkingPlayer event, String serverType, boolean avoidFallDamage) {

        if (mc.player == null || mc.world == null) {
            return;
        }

        double yOffset = !serverType.equals("Hypixel") ? 0.0 : 1.0E-14;
        boolean shouldSetGround = true;

        if (blockDelay == 0 && groundTicks >= 1 && Step.updateTicksBeforeStep > 1) {
            if (interactAB.method36820(attackDelay)) {
                blockDelay = 1;
                shouldSetGround = avoidFallDamage;
                yOffset = !serverType.equals("Cubecraft") ? 0.0626 : com.mentalfrostbyte.jello.util.game.player.MovementUtil.getJumpValue() / 10.0;
                positionOffset = new double[] { event.getX(), event.getY() + yOffset, event.getZ() };
            }
        } else if (blockDelay == 1) {
            blockDelay = 0;
            shouldSetGround = false;
            if (serverType.equals("Hypixel") && positionOffset != null && mc.player.getMotion().y < 0.0) {
                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPlayerPacket.PositionPacket(
                        positionOffset[0], positionOffset[1], positionOffset[2], false));
                positionOffset = null;
            }
        }

        boolean isOnGroundOrAboveBounds = !Jesus.isWalkingOnLiquid()
                && (Objects.requireNonNull(mc.player).isOnGround() || MovementUtil2.isAboveBounds(mc.player, 0.001F));
        if (!isOnGroundOrAboveBounds) {
            groundTicks = 0;
            blockDelay = 0;
        } else {
            groundTicks++;
            if ((!Client.getInstance().moduleManager.getModuleByClass(Speed.class).isEnabled()
                    || Client.getInstance().moduleManager.getModuleByClass(Speed.class)
                            .getStringSettingValueByName("Type").equalsIgnoreCase("Cubecraft")
                    || Client.getInstance().moduleManager.getModuleByClass(Speed.class)
                            .getStringSettingValueByName("Type").equalsIgnoreCase("Vanilla"))
                    && mc.player.collidedVertically && !mc.player.isJumping && !mc.player.isInWater()
                    && !mc.gameSettings.keyBindJump.isKeyDown()) {
                isAuraActive = yOffset > 0.001;

                event.setY(mc.player.getPosY() + yOffset);
                event.setGround(shouldSetGround);
            }
        }
    }

    private Entity getClosestEntity(List<TimedEntity> var1) {

        if (mc.player == null || mc.world == null) {
            return null;
        }

        var1 = interactAB.sortEntities(var1);
        return !var1.isEmpty()
                && var1.get(0).getEntity().getDistance(mc.player) <= this.getNumberValueBySettingName("Block Range")
                        ? var1.get(0).getEntity()
                        : null;
    }

    private void handleAura() {
        float blockRange = getNumberValueBySettingName("Block Range");
        float range = getNumberValueBySettingName("Range");
        String mode = getStringSettingValueByName("Mode");

        List<TimedEntity> potentialTargets = interactAB.getEntitiesInRange(Math.max(blockRange, range));
        if (potentialTargets == null || potentialTargets.isEmpty() || mc.gameSettings.keyBindAttack.isPressed()) {
            resetState();
            return;
        }

        potentialTargets = interactAB.sortEntities(potentialTargets);
        Entity closestEntity = getClosestEntity(potentialTargets);
        if (currentRotation == null) {
            onEnable();
        }

        currentTarget = closestEntity;
        potentialTargets = interactAB.getEntitiesInRange(range);

        if (mode.equals("Single") || mode.equals("Multi")) {
            potentialTargets = interactAB.sortEntities(potentialTargets);
        }

        if (potentialTargets.isEmpty()) {
            resetState();
        } else {
            processTargets(potentialTargets, mode, range);
        }
    }

    private void processTargets(List<TimedEntity> targets, String mode, float range) {
        if (rotationProgress == -1.0F) {
            float targetYaw = RotationHelper
                    .getRotationsToVector(MovementUtil2.method17751(targets.get(0).getEntity())).yaw;
            float yawDifference = Math.abs(getYawDifference(targetYaw, previousRotation.yaw));
            rotationSpeed = yawDifference * 1.95F / 50.0F;
            rotationProgress++;
            randomOffset = Math.random();
        }

        targetEntities = targets;

        if (!mode.equals("Switch")) {
            if (mode.equals("Single")) {
                currentTimedEntity = targets.get(0);
            } else if (mode.equals("Multi2")) {
                if (targetIndex >= targets.size()) {
                    targetIndex = 0;
                }
                currentTimedEntity = targets.get(targetIndex);
            } else if (currentTimedEntity == null || currentTimedEntity.getEntity() != targets.get(0).getEntity()) {
                float targetYaw = RotationHelper
                        .getRotationsToVector(MovementUtil2.method17751(targets.get(0).getEntity())).yaw;
                float yawDifference = Math.abs(getYawDifference(targetYaw, previousRotation.yaw));
                rotationSpeed = yawDifference * 1.95F / 50.0F;
                rotationProgress++;
                randomOffset = Math.random();

                currentTimedEntity = targets.get(0);
            }
        } else {
            boolean shouldSwitch = currentTimedEntity == null
                    || currentTimedEntity.isExpired()
                    || !targets.contains(currentTimedEntity)
                    || Objects.requireNonNull(mc.player).getDistance(currentTimedEntity.getEntity()) > range;

            if (shouldSwitch && !targets.isEmpty()) {
                targetIndex = (targetIndex + 1) % targets.size();
                TimedEntity nextTarget = targets.get(targetIndex);
                float targetYaw = RotationHelper
                        .getRotationsToVector(MovementUtil2.method17751(nextTarget.getEntity())).yaw;
                float yawDifference = Math.abs(getYawDifference(targetYaw, previousRotation.yaw));
                rotationSpeed = yawDifference * 1.95F / 50.0F;
                randomOffset = Math.random();
                currentTimedEntity = new TimedEntity(nextTarget.getEntity(), createExpirationTimer());
            }
        }

        if (!mode.equals("Multi")) {

            if (currentTimedEntity == null)
                return;

            targetEntities = List.of(currentTimedEntity);
        }
    }

    private float getYawDifference(float targetYaw, float currentYaw) {
        return MovementUtil2.method17756(targetYaw, currentYaw);
    }

    private ExpirationTimer createExpirationTimer() {
        boolean isNCPMode = getStringSettingValueByName("Rotation Mode").equals("NCP");
        return new ExpirationTimer(isNCPMode ? 270L : 500L);
    }

    private void setRotation() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        Entity targ = currentTimedEntity.getEntity();
        Rotation newRots = RotationHelper.getRotations(targ, !getBooleanValueFromSettingName("Through walls"));
        if (newRots == null) {
            System.out.println("[KillAura] newRots is null??? on line 712");
            return;
        }

        float yawDifference = RotationHelper.getShortestAngleDifference(currentRotation.yaw, newRots.yaw);
        float pitchDifference = newRots.pitch - currentRotation.pitch;

        String rotationMode = getStringSettingValueByName("Rotation Mode");
        float range = getNumberValueBySettingName("Range");
        switch (rotationMode) {
            case "NCP":
                secondaryRotation.yaw = currentRotation.yaw;
                secondaryRotation.pitch = currentRotation.pitch;
                currentRotation = newRots;
                break;
            case "New":
                updateRotations(mc.player, currentTarget, currentRotation, secondaryRotation, 0.1, 0.1);
                break;
            case "Smooth":
                secondaryRotation.yaw = currentRotation.yaw;
                secondaryRotation.pitch = currentRotation.pitch;
                currentRotation.yaw += (float) (yawDifference * 2.0F / 5.0);
                currentRotation.pitch += (float) (pitchDifference * 2.0F / 5.0);
                break;
            case "None":
                secondaryRotation.yaw = currentRotation.yaw;
                secondaryRotation.pitch = currentRotation.pitch;
                currentRotation.yaw = mc.player.rotationYaw;
                currentRotation.pitch = mc.player.rotationPitch;
                break;
            case "LockView":
                secondaryRotation.yaw = currentRotation.yaw;
                secondaryRotation.pitch = currentRotation.pitch;
                EntityRayTraceResult ray = EntityUtil.method17714(targ, currentRotation.yaw, currentRotation.pitch,
                        e -> true, getNumberValueBySettingName("Range"));
                if (ray == null || ray.getEntity() != targ) {
                    currentRotation = newRots;
                }
                break;
        }
    }

    public void updateRotations(Entity player, Entity target, Rotation currentRotation, Rotation secondaryRotation,
                                double mouseDeltaX, double mouseDeltaY) {
        if (player == null || target == null || currentRotation == null || secondaryRotation == null) {
            return;
        }

        List<Vector3d> aimPoints = new ArrayList<>();
        Vector3d closestPoint = findClosestPoint(player.getPositionVec(), target.getBoundingBox(), 25.0f, 0.05);
        if (closestPoint != null) {
            aimPoints.add(closestPoint);
        }

        Vector3d aimPoint = aimPoints.get(0);

        double targetEyeX = aimPoint.x;
        double targetEyeY = aimPoint.y;
        double targetEyeZ = aimPoint.z;

        double playerEyeX = player.getPosX();
        double playerEyeY = player.getPosY() + player.getEyeHeight();
        double playerEyeZ = player.getPosZ();

        double deltaX = targetEyeX - playerEyeX;
        double deltaY = targetEyeY - playerEyeY;
        double deltaZ = targetEyeZ - playerEyeZ;

        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float targetYaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(deltaY, distanceXZ));

        final float factor = Math.min(Math.max(mc.player.getDistance(target), 0.0F), 3.0F) / 3.0F
                * (float) (1.0F + Math.sin(randomAngle) * 2.0F) / 2.0F;

        targetYaw = normalizeAngle(targetYaw);
        targetPitch = Math.max(-90.0F, Math.min(90.0F, targetPitch));

        {
            final float radius = 10.0F * factor;

            float x = radius * (float) Math.cos(randomAngle);
            float y = radius * (float) Math.sin(randomAngle);
            float z = radius * (float) Math.sin(randomAngle / 2.0F);

            targetYaw += x * (float) Math.cos(randomAngle / 3.0F);
            targetPitch += y * (float) Math.sin(randomAngle / 3.0F);

            randomAngle += 0.25F;
        }

        double radianFactor = Math.sin(Math.toRadians(targetYaw)) * Math.cos(Math.toRadians(targetPitch));
        double advancedCalculationX = deltaX * radianFactor + mouseDeltaX;
        double advancedCalculationY = deltaY * radianFactor + mouseDeltaY;
        double advancedCalculationZ = deltaZ * radianFactor;

        currentRotation.yaw = (float) (targetYaw + advancedCalculationX);
        currentRotation.pitch = (float) (targetPitch + advancedCalculationY);

        secondaryRotation.yaw = (float) (targetYaw + advancedCalculationZ);
        secondaryRotation.pitch = (float) (targetPitch + advancedCalculationY);
    }

    // simple because i didnt bother making better one.
    private Vector3d findClosestPoint(Vector3d origin, AxisAlignedBB boundingBox, float maxDistance, double stepSize) {
        Vector3d closestPoint = null;
        double closestDistanceSquared = maxDistance * maxDistance;
        List<Vector3d> points = new ArrayList<>();

        double minX = boundingBox.minX;
        double maxX = boundingBox.maxX;
        double minY = boundingBox.maxY - 0.4;
        double maxY = boundingBox.maxY;
        double minZ = boundingBox.minZ;
        double maxZ = boundingBox.maxZ;

        for (double x = minX; x <= maxX; x += stepSize) {
            for (double y = minY; y <= maxY; y += stepSize) {
                for (double z = minZ; z <= maxZ; z += stepSize) {
                    points.add(new Vector3d(x, y, z));
                }
            }
        }

        for (Vector3d point : points) {
            double distanceSquared = origin.squareDistanceTo(point);
            if (distanceSquared <= closestDistanceSquared) {
                if (closestPoint == null || origin.squareDistanceTo(closestPoint) > distanceSquared) {
                    closestPoint = point;
                    closestDistanceSquared = distanceSquared;
                }
            }
        }

        return closestPoint;
    }

    private float normalizeAngle(float angle) {
        while (angle <= -180.0F) {
            angle += 360.0F;
        }
        while (angle > 180.0F) {
            angle -= 360.0F;
        }
        return angle;
    }
}