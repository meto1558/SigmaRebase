package com.mentalfrostbyte.jello.module.impl.combat;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.*;
import com.mentalfrostbyte.jello.gui.base.Animation;
import com.mentalfrostbyte.jello.gui.base.Direction;
import com.mentalfrostbyte.jello.managers.impl.notifs.Notification;
import com.mentalfrostbyte.jello.misc.Pair;
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
import com.mentalfrostbyte.jello.util.ClientColors;
import com.mentalfrostbyte.jello.util.EntityUtil;
import com.mentalfrostbyte.jello.util.MathUtils;
import com.mentalfrostbyte.jello.util.MultiUtilities;
import com.mentalfrostbyte.jello.util.player.MovementUtil;
import com.mentalfrostbyte.jello.util.player.RotationHelper;
import com.mentalfrostbyte.jello.util.player.Rotations;
import com.mentalfrostbyte.jello.util.player.Rots;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.SwordItem;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;
import team.sdhq.eventBus.annotations.EventTarget;
import team.sdhq.eventBus.annotations.priority.LowestPriority;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class KillAura extends Module {
    public static boolean field23937 = false;
    public static Entity target;
    public static TimedEntity timedEntityIdk;
    public static Rotations previousRotations = new Rotations(0.0F, 0.0F);
    public static int field23954;
    public HashMap<Entity, Animation> entityGlowMap = new HashMap<>();
    public static InteractAutoBlock interactAB;
    private int field23939;
    private int field23940;
    private int field23941;
    private int field23942;
    private int currentitem;
    private int field23945;
    private int field23946;
    private int field23947;
    public static List<TimedEntity> entities;
    private Rotations rotations;
    private Rotations rotations2;
    private double field23955;
    private float field23956;
    private float field23957;
    private float field23958;
    private boolean field23959;
    private double[] field23960;

    public KillAura() {
        super(ModuleCategory.COMBAT, "KillAura", "Automatically attacks entities");
        this.registerSetting(new ModeSetting("Mode", "Mode", 0, "Single", "Switch", "Multi", "Multi2"));
        this.registerSetting(new ModeSetting("Autoblock Mode", "Autoblock Mode", 0, "None", "NCP", "Basic1", "Basic2", "Vanilla"));
        this.registerSetting(new ModeSetting("Sort Mode", "Sort Mode", 0, "Range", "Health", "Angle", "Armor", "Prev Range"));
        this.registerSetting(new ModeSetting("Attack Mode", "Attacks after or before sending the movement", 0, "Pre", "Post"));
        this.registerSetting(new ModeSetting("Rotation Mode", "The way you will look at entities", 0, "NCP", "AAC", "Smooth", "LockView", "Test", "Test2", "None"));
        this.registerSetting(new NumberSetting<>("Range", "Range value", 4.0F, Float.class, 2.8F, 8.0F, 0.01F));
        this.registerSetting(new NumberSetting<>("Block Range", "Block Range value", 4.0F, Float.class, 2.8F, 8.0F, 0.2F));
        this.registerSetting(
                new NumberSetting<>("Min CPS", "Min CPS value", 8.0F, Float.class, 1.0F, 20.0F, 1.0F).addObserver(var1 -> this.interactAB.method36818())
        );
        this.registerSetting(
                new NumberSetting<>("Max CPS", "Max CPS value", 8.0F, Float.class, 1.0F, 20.0F, 1.0F).addObserver(var1 -> this.interactAB.method36818())
        );
        this.registerSetting(new NumberSetting<>("Hit box expand", "Hit Box expand", 0.05F, Float.class, 0.0F, 1.0F, 0.01F));
        this.registerSetting(new NumberSetting<>("Hit Chance", "Hit Chance", 100.0F, Float.class, 25.0F, 100.0F, 1.0F));
        this.registerSetting(new BooleanSetting("Interact autoblock", "Send interact packet when blocking", true));
        this.registerSetting(new BooleanSetting("Players", "Hit players", true));
        this.registerSetting(new BooleanSetting("Animals", "Hit animals", false));
        this.registerSetting(new BooleanSetting("Monsters", "Hit monsters", false));
        this.registerSetting(new BooleanSetting("Invisible", "Hit invisible entites", true));
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

    public static Rotations getRotations2(KillAura killaura) {
        return killaura.rotations2;
    }

    public static Rotations getRotations(KillAura killaura) {
        return killaura.rotations;
    }

    public static int method16846(KillAura killaura) {
        return killaura.field23942;
    }

    public static int method16847(KillAura killaura, int var1) {
        return killaura.field23942 = var1;
    }

    @Override
    public void initialize() {
        entities = new ArrayList<>();
        interactAB = new InteractAutoBlock(this);
        super.initialize();
    }

    @Override
    public void onEnable() {
        entities = new ArrayList<>();
        target = null;
        timedEntityIdk = null;
        this.field23939 = (int) interactAB.method36819(0);
        this.field23940 = 0;
        this.field23942 = 0;
        field23954 = 0;
        this.rotations2 = new Rotations(mc.player.rotationYaw, mc.player.rotationPitch);
        this.rotations = new Rotations(mc.player.rotationYaw, mc.player.rotationPitch);
        previousRotations = new Rotations(mc.player.rotationYaw, mc.player.rotationPitch);
        this.field23957 = -1.0F;
        interactAB.setBlocking(mc.player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof SwordItem && mc.gameSettings.keyBindUseItem.isKeyDown());
        this.field23959 = false;
        this.field23946 = -1;
        interactAB.field44349.clear();
        this.entityGlowMap.clear();
        if (mc.player.isOnGround()) {
            this.field23941 = 1;
        }

        super.onEnable();
    }

    @Override
    public void onDisable() {
        Rots.rotating = false;
        target = null;
        timedEntityIdk = null;
        entities = null;
        field23937 = false;
        super.onDisable();
    }

    @EventTarget
    public void onWorldChange(WorldLoadEvent event) {
        if (this.isEnabled() && this.getBooleanValueFromSettingName("Disable on death")) {
            Client.getInstance().notificationManager.send(new Notification("Aura", "Aura disabled due to respawn"));
            this.toggle();
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (this.isEnabled()) {
            if (this.field23957 != -1.0F) {
                this.field23957++;
            }

            if (this.getBooleanValueFromSettingName("Disable on death")) {
                if (!mc.player.isAlive()) {
                    this.toggle();
                    Client.getInstance().notificationManager.send(new Notification("Aura", "Aura disabled due to death"));
                }
            }

            if (target != null) {
                Rots.rotating = true;
            }
        }
    }

    @EventTarget
    public void onStopuseItem(StopUseItemEvent event) {
        if (this.isEnabled()) {
            if (!this.getStringSettingValueByName("Autoblock Mode").equals("None")
                    && (mc.player.getHeldItemMainhand().getItem() instanceof SwordItem || this.currentitem != mc.player.inventory.currentItem)
                    && target != null) {
                event.setCancelled(true);
            } else if (mc.player.getHeldItemMainhand().getItem() instanceof SwordItem) {
                this.field23945 = 2;
            }
        }
    }

    @EventTarget
    @LowestPriority
    public void onUpdate(EventUpdate event) {
        if (this.isEnabled() && mc.player != null) {
            if (!event.isPre()) {
                this.currentitem = mc.player.inventory.currentItem;
                if (target != null && interactAB.canBlock() && this.rotations != null) {
                    interactAB.block(target, this.rotations.yaw, this.rotations.pitch);
                }
            } else {
                if (this.field23945 > 0) {
                    this.field23945--;
                }

                if (target != null && interactAB.isBlocking() && MovementUtil.isMoving() && this.getStringSettingValueByName("Autoblock Mode").equals("NCP")) {
                    interactAB.method36816();
                }

                if (interactAB.isBlocking() && (!(mc.player.getHeldItemMainhand().getItem() instanceof SwordItem) || target == null)) {
                    interactAB.setBlocking(false);
                }

                if (this.field23946 >= 0) {
                    if (this.field23946 == 0) {
                        interactAB.method36816();
                        interactAB.setBlocking(true);
                    }

                    this.field23946--;
                }

                this.method16830();
                if (entities != null && !entities.isEmpty()) {
                    this.field23939++;
                    float var4 = this.getNumberValueBySettingName("Hit box expand");
                    ModuleWithModuleSettings var5 = (ModuleWithModuleSettings) Client.getInstance().moduleManager.getModuleByClass(Criticals.class);
                    if (var5.isEnabled() && var5.getStringSettingValueByName("Type").equalsIgnoreCase("Minis")) {
                        this.method16828(event, var5.getModWithTypeSetToName().getStringSettingValueByName("Mode"), var5.getModWithTypeSetToName().getBooleanValueFromSettingName("Avoid Fall Damage"));
                    }

                    this.method16831();
                    if (event.getYaw() - mc.player.rotationYaw != 0.0F) {
                        this.rotations.yaw = event.getYaw();
                        this.rotations.pitch = event.getPitch();
                    }

                    if (target != null) {
                        Rots.prevYaw = this.rotations.yaw;
                        Rots.prevPitch = this.rotations.pitch;
                        event.setYaw(this.rotations.yaw);
                        event.setPitch(this.rotations.pitch);
                        Rots.yaw = this.rotations.yaw;
                        Rots.pitch = this.rotations.pitch;

                        mc.player.rotationYawHead = event.getYaw();
                        mc.player.renderYawOffset = event.getYaw();
                    }

                    boolean var6 = interactAB.method36821(this.field23939);
                    float cooldown19 = !((double) mc.player.getCooldownPeriod() < 1.26) && this.getBooleanValueFromSettingName("Cooldown") ? mc.player.getCooledAttackStrength(0.0F) : 1.0F;
                    boolean var8 = field23954 == 0 && var6 && cooldown19 >= 1.0F;
                    if (var6) {
                        interactAB.setupDelay();
                    }

                    if (var8) {
                        KillAuraAttackLambda attack = new KillAuraAttackLambda(this, var4);
                        boolean isPre = this.getStringSettingValueByName("Attack Mode").equals("Pre");
                        if (!isPre) {
                            event.attackPost(attack);
                        } else {
                            attack.run();
                        }

                        this.field23939 = 0;
                    }

                    if (field23954 > 0) {
                        field23954--;
                    }
                }
            }
        }
    }

    @EventTarget
    public void on2D(EventRender var1) {
        if (timedEntityIdk != null && !this.getBooleanValueFromSettingName("Silent") && !this.getStringSettingValueByName("Rotation Mode").equals("None")) {
            float var4 = MathHelper.wrapDegrees(this.rotations2.yaw + (this.rotations.yaw - this.rotations2.yaw) * mc.getRenderPartialTicks());
            float var5 = MathHelper.wrapDegrees(this.rotations2.pitch + (this.rotations.pitch - this.rotations2.pitch) * mc.getRenderPartialTicks());
            mc.player.rotationYaw = var4;
            mc.player.rotationPitch = var5;
        }
    }

    @EventTarget
    public void on3D(Render3DEvent var1) {
        if (entities != null) {
            Iterator<Entry<Entity, Animation>> var4 = this.entityGlowMap.entrySet().iterator();

            while (var4.hasNext()) {
                Entry<Entity, Animation> var5 = var4.next();

                var5.getValue().changeDirection(Direction.BACKWARDS);
                if (var5.getValue().calcPercent() == 1.0F) {
                    var4.remove();
                }
            }

            for (TimedEntity var10 : entities) {
                if (var10 != null) {
                    if (!this.entityGlowMap.containsKey(var10.getEntity())) {
                        this.entityGlowMap.put(var10.getEntity(), new Animation(250, 250));
                    } else {
                        this.entityGlowMap.get(var10.getEntity()).changeDirection(Direction.FORWARDS);
                    }
                }
            }

            for (Entry entity : this.entityGlowMap.entrySet()) {
                this.renderTargetESP((Entity) entity.getKey());
            }
        }
    }

    @EventTarget
    public void onReceivePacket(ReceivePacketEvent event) {
        IPacket<?> packet = event.getPacket();
        if (!(packet instanceof SEntityPacket)) {
            if (packet instanceof SEntityStatusPacket) {
                SEntityStatusPacket var5 = (SEntityStatusPacket) packet;
                if (var5.getOpCode() == 3) {
                    interactAB.field44349.remove(var5.getEntity(mc.world));
                }
            }
        } else {
            SEntityPacket var11 = (SEntityPacket) packet;
            if (var11.func_229745_h_() && (var11.posX != 0 || var11.posY != 0 || var11.posZ != 0)) {
                for (Entry var7 : interactAB.field44349.entrySet()) {
                    Entity var8 = (Entity) var7.getKey();
                    List var9 = (List) var7.getValue();
                    if (var11.getEntity(mc.world) == var8) {
                        Vector3d var10 = var8.field_242272_av.scale(2.4414062E-4F);
                        var9.add(new Pair<>(var10, System.currentTimeMillis()));
                    }
                }
            }
        }
    }

    public void renderTargetESP(Entity targetEntity) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glLineWidth(1.4F);

        double partialTicks = Minecraft.getInstance().timer.renderPartialTicks;
        if (!targetEntity.isAlive()) {
            partialTicks = 0.0;
        }

        double interpolatedX = targetEntity.lastTickPosX + (targetEntity.getPosX() - targetEntity.lastTickPosX) * partialTicks;
        double interpolatedY = targetEntity.lastTickPosY + (targetEntity.getPosY() - targetEntity.lastTickPosY) * partialTicks;
        double interpolatedZ = targetEntity.lastTickPosZ + (targetEntity.getPosZ() - targetEntity.lastTickPosZ) * partialTicks;

        double cameraX = mc.gameRenderer.getActiveRenderInfo().getProjectedView().getX();
        double cameraY = mc.gameRenderer.getActiveRenderInfo().getProjectedView().getY();
        double cameraZ = mc.gameRenderer.getActiveRenderInfo().getProjectedView().getZ();

        GL11.glTranslated(interpolatedX - cameraX, interpolatedY - cameraY, interpolatedZ - cameraZ);

        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);

        long cycleDurationMs = 1800; // Duration of the glow cycle
        float glowProgress = (float) (System.currentTimeMillis() % cycleDurationMs) / cycleDurationMs;
        boolean isFadingOut = glowProgress > 0.5F;
        glowProgress = !isFadingOut ? glowProgress * 2.0F : 1.0F - glowProgress * 2.0F % 1.0F;

        GL11.glTranslatef(0.0F, (targetEntity.getHeight() + 0.4F) * glowProgress, 0.0F);

        float glowFactor = (float) Math.sin(glowProgress * Math.PI);
        drawCircle(isFadingOut, 0.45F * glowFactor, 0.6F, 0.35F * glowFactor, this.entityGlowMap.get(targetEntity).calcPercent());

        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    public void drawCircle(boolean isFadingOut, float circleHeight, float radius, float glowStrength, float glowOpacity) {
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBegin(GL11.GL_QUAD_STRIP);

        int angleStep = (int) (360.0F / (40.0F * radius));

        Color espColor = new Color(this.parseSettingValueToIntBySettingName("ESP Color"));
        float red = (float) espColor.getRed() / 255.0F;
        float green = (float) espColor.getGreen() / 255.0F;
        float blue = (float) espColor.getBlue() / 255.0F;

        for (int angle = 0; angle <= 360 + angleStep; angle += angleStep) {
            int effectiveAngle = angle > 360 ? 0 : angle;

            double x = Math.sin(Math.toRadians(effectiveAngle)) * radius;
            double z = Math.cos(Math.toRadians(effectiveAngle)) * radius;

            GL11.glColor4f(red, green, blue, isFadingOut ? 0.0F : glowStrength * glowOpacity);
            GL11.glVertex3d(x, 0.0, z);

            GL11.glColor4f(red, green, blue, isFadingOut ? glowStrength * glowOpacity : 0.0F);
            GL11.glVertex3d(x, circleHeight, z);
        }

        GL11.glEnd();

        GL11.glLineWidth(2.2F);
        GL11.glBegin(GL11.GL_LINE_LOOP);

        for (int angle = 0; angle <= 360 + angleStep; angle += angleStep) {
            int effectiveAngle = angle > 360 ? 0 : angle;

            double x = Math.sin(Math.toRadians(effectiveAngle)) * radius;
            double z = Math.cos(Math.toRadians(effectiveAngle)) * radius;

            GL11.glColor4f(red, green, blue, (0.5F + 0.5F * glowStrength) * glowOpacity);
            GL11.glVertex3d(x, isFadingOut ? 0.0 : circleHeight, z);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderSystem.shadeModel(GL11.GL_FLAT);
    }

    public boolean isAutoBlockEnabled() {
        return target != null
                && mc.player.getHeldItemMainhand() != null
                && mc.player.getHeldItemMainhand().getItem() instanceof SwordItem
                && !this.getStringSettingValueByName("Autoblock Mode").equals("None");
    }

    @Override
    public boolean isEnabled2() {
        return this.isEnabled() && this.isAutoBlockEnabled();
    }

    public void method16828(EventUpdate var1, String var2, boolean var3) {
        double var6 = !var2.equals("Hypixel") ? 0.0 : 1.0E-14;
        boolean var8 = true;
        if (this.field23940 == 0 && this.field23941 >= 1 && Step.updateTicksBeforeStep > 1) {
            if (interactAB.method36820(this.field23939)) {
                this.field23940 = 1;
                var8 = var3;
                var6 = !var2.equals("Cubecraft") ? 0.0626 : MovementUtil.getJumpValue() / 10.0;
                this.field23960 = new double[]{var1.getX(), var1.getY() + var6, var1.getZ()};
            }
        } else if (this.field23940 == 1) {
            this.field23940 = 0;
            var8 = false;
            if (var2.equals("Hypixel") && this.field23960 != null && mc.player.getMotion().y < 0.0) {
                mc.getConnection().sendPacket(new CPlayerPacket.PositionPacket(this.field23960[0], this.field23960[1], this.field23960[2], false));
                this.field23960 = null;
            }
        }

        boolean var9 = !Jesus.isWalkingOnLiquid() && (mc.player.isOnGround() || MultiUtilities.isAboveBounds(mc.player, 0.001F));
        if (!var9) {
            this.field23941 = 0;
            this.field23940 = 0;
        } else {
            this.field23941++;
            if ((
                    !Client.getInstance().moduleManager.getModuleByClass(Speed.class).isEnabled()
                            || Client.getInstance().moduleManager.getModuleByClass(Speed.class).getStringSettingValueByName("Type").equalsIgnoreCase("Cubecraft")
                            || Client.getInstance().moduleManager.getModuleByClass(Speed.class).getStringSettingValueByName("Type").equalsIgnoreCase("Vanilla")
            )
                    && mc.player.collidedVertically
                    && var9
                    && !mc.player.isJumping
                    && !mc.player.isInWater()
                    && !mc.gameSettings.keyBindJump.isKeyDown()) {
                field23937 = var6 > 0.001;

                var1.setY(mc.player.getPosY() + var6);
                var1.setGround(var8);
            }
        }
    }

    private Entity method16829(List<TimedEntity> var1) {
        var1 = interactAB.sortEntities(var1);
        return !var1.isEmpty() && var1.get(0).getEntity().getDistance(mc.player) <= this.getNumberValueBySettingName("Block Range")
                ? var1.get(0).getEntity()
                : null;
    }

    private void method16830() {
        float blockingRange = this.getNumberValueBySettingName("Block Range");
        float range = this.getNumberValueBySettingName("Range");
        String mode = this.getStringSettingValueByName("Mode");
        List<TimedEntity> var6 = interactAB.method36823(Math.max(blockingRange, range));
        var6 = interactAB.sortEntities(var6);
        if (this.rotations == null) {
            this.onEnable();
        }

        if (var6 != null && !var6.isEmpty() && !mc.gameSettings.keyBindAttack.isPressed()) {
            target = this.method16829(var6);
            var6 = interactAB.method36823(range);
            if (mode.equals("Single") || mode.equals("Multi")) {
                var6 = interactAB.sortEntities(var6);
            }

            if (var6.isEmpty()) {
                timedEntityIdk = null;
                if (entities != null)
                    entities.clear();
                this.field23939 = (int) interactAB.method36819(0);
                this.field23940 = 0;
                field23937 = false;
                this.rotations.yaw = mc.player.rotationYaw;
                this.rotations.pitch = mc.player.rotationPitch;
                previousRotations.yaw = this.rotations.yaw;
                previousRotations.pitch = this.rotations.pitch;
                this.field23957 = -1.0F;
                this.field23955 = Math.random();
                this.field23946 = -1;
            } else {
                if (this.field23957 == -1.0F) {
                    float var7 = RotationHelper.getRotationsToVector(MultiUtilities.method17751(((TimedEntity) var6.get(0)).getEntity())).yaw;
                    float var8 = Math.abs(MultiUtilities.method17756(var7, previousRotations.yaw));
                    this.field23956 = var8 * 1.95F / 50.0F;
                    this.field23957++;
                    this.field23955 = Math.random();
                }

                entities = var6;
                float var12 = RotationHelper.getRotationsToVector(MultiUtilities.method17751(entities.get(0).getEntity())).yaw;
                if (!entities.isEmpty() & !mode.equals("Switch")) {
                    if (timedEntityIdk != null && timedEntityIdk.getEntity() != entities.get(0).getEntity()) {
                        float var13 = Math.abs(MultiUtilities.method17756(var12, previousRotations.yaw));
                        this.field23956 = var13 * 1.95F / 50.0F;
                        this.field23955 = Math.random();
                    }

                    timedEntityIdk = entities.get(0);
                }

                if (!mode.equals("Switch")) {
                    if (!mode.equals("Multi2")) {
                        if (mode.equals("Single")
                                && !entities.isEmpty()
                                && (timedEntityIdk == null || timedEntityIdk.getEntity() != entities.get(0).getEntity())) {
                            timedEntityIdk = entities.get(0);
                        }
                    } else {
                        if (this.field23942 >= entities.size()) {
                            this.field23942 = 0;
                        }

                        timedEntityIdk = entities.get(this.field23942);
                    }
                } else if ((
                        timedEntityIdk == null
                                || timedEntityIdk.getTimer() == null
                                || timedEntityIdk.isExpired()
                                || !entities.contains(timedEntityIdk)
                                || mc.player.getDistance(timedEntityIdk.getEntity()) > range
                )
                        && !entities.isEmpty()) {
                    if (this.field23942 + 1 < entities.size()) {
                        if (timedEntityIdk != null && !Client.getInstance().friendManager.isFriend(entities.get(this.field23942).getEntity())) {
                            this.field23942++;
                        }
                    } else {
                        this.field23942 = 0;
                    }

                    Vector3d var14 = MultiUtilities.method17751(entities.get(this.field23942).getEntity());
                    float var9 = Math.abs(MultiUtilities.method17756(RotationHelper.getRotationsToVector(var14).yaw, previousRotations.yaw));
                    this.field23956 = var9 * 1.95F / 50.0F;
                    this.field23955 = Math.random();
                    timedEntityIdk = new TimedEntity(
                            entities.get(this.field23942).getEntity(), new ExpirationTimer(!this.getStringSettingValueByName("Rotation Mode").equals("NCP") ? 500L : 270L)
                    );
                }

                if (this.field23942 >= entities.size()) {
                    this.field23942 = 0;
                }

                if (!mode.equals("Multi")) {
                    entities.clear();
                    entities.add(timedEntityIdk);
                }
            }
        } else {
            timedEntityIdk = null;
            target = null;
            if (entities != null) {
                entities.clear();
            }

            this.field23939 = (int) interactAB.method36819(0);
            this.field23940 = 0;
            field23937 = false;
            this.rotations.yaw = mc.player.rotationYaw;
            this.rotations.pitch = mc.player.rotationPitch;
            previousRotations.yaw = this.rotations.yaw;
            previousRotations.pitch = this.rotations.pitch;
            this.field23957 = -1.0F;
            this.field23955 = Math.random();
            this.field23946 = -1;
        }
    }

    private void method16831() {
        Entity targ = timedEntityIdk.getEntity();
        Rotations newRots = RotationHelper.getRotations(targ, !this.getBooleanValueFromSettingName("Through walls"));
        if (newRots == null) {
            System.out.println("[KillAura] newRots is null??? on line 612");
            return;
        }
        float var5 = RotationHelper.method34152(this.rotations.yaw, newRots.yaw);
        float var6 = newRots.pitch - this.rotations.pitch;
        String var7 = this.getStringSettingValueByName("Rotation Mode");
        switch (var7) {
            case "Test":
                this.rotations2.yaw = this.rotations.yaw;
                this.rotations2.pitch = this.rotations.pitch;
                if (Math.abs(var5) > 80.0F) {
                    float var9 = (float) this.method16832(-10.2, 10.2);
                    float var30 = var5 * var5 * 1.13F / 2.0F + var9;
                    this.rotations.yaw += var30;
                    this.field23958 = var30;
                } else if (Math.abs(var5) > 30.0F) {
                    float var26 = (float) this.method16832(-10.2, 10.2);
                    float var31 = var5 * 1.03F / 2.0F + var26;
                    this.rotations.yaw += var31;
                    this.field23958 = var31;
                } else if (Math.abs(var5) > 10.0F) {
                    Entity var27 = EntityUtil.getEntityFromRayTrace(
                            this.rotations.pitch, this.rotations.yaw, this.getNumberValueBySettingName("Range"), this.getNumberValueBySettingName("Hit box expand")
                    );
                    double var11 = var27 == null ? 13.4 : 1.4;
                    this.field23958 = (float) ((double) this.field23958 * 0.5296666666666666);
                    if (Math.abs(var5) < 20.0F) {
                        this.field23958 = var5 * 0.5F;
                    }

                    this.rotations.yaw = this.rotations.yaw + var5 + this.field23958 + (float) this.method16832(-var11, var11);
                } else {
                    this.field23958 = (float) ((double) this.field23958 * 0.05);
                    double var13 = 0.0;
                    this.rotations.yaw = this.rotations.yaw + this.field23958 + (float) this.method16832(-var13, var13);
                }

                if (mc.player.ticksExisted % 5 == 0) {
                    double var32 = 10.0;
                    this.rotations.yaw = this.rotations.yaw
                            + (float) this.method16832(-var32, var32) / (mc.player.getDistance(targ) + 1.0F);
                    this.rotations.pitch = this.rotations.pitch
                            + (float) this.method16832(-var32, var32) / (mc.player.getDistance(targ) + 1.0F);
                }

                if (Math.abs(var6) > 10.0F) {
                    this.rotations.pitch = (float) ((double) this.rotations.pitch + (double) var6 * 0.81 + this.method16832(-2.0, 2.0));
                }

                Entity var28 = EntityUtil.getEntityFromRayTrace(
                        this.rotations2.pitch, this.rotations2.yaw, this.getNumberValueBySettingName("Range"), this.getNumberValueBySettingName("Hit box expand")
                );
                if (var28 != null && (double) this.field23947 > this.method16832(2.0, 5.0)) {
                    this.field23947 = 0;
                    EntityUtil.swing(var28, true);
                }
                break;
            case "NCP":
                this.rotations2.yaw = this.rotations.yaw;
                this.rotations2.pitch = this.rotations.pitch;
                this.rotations = newRots;
                break;
            case "AAC":
                if (!RotationHelper.raytraceVector(
                        new Vector3d(targ.getPosX(), targ.getPosY() - 1.6 - this.field23955 + (double) targ.getEyeHeight(), targ.getPosZ())
                )) {
                }

                float var29 = this.field23957 / Math.max(1.0F, this.field23956);
                double var33 = targ.getPosX() - targ.lastTickPosX;
                double var34 = targ.getPosZ() - targ.lastTickPosZ;
                float var35 = (float) Math.sqrt(var33 * var33 + var34 * var34);
                float var36 = MathUtils.lerp(var29, 0.57, -0.135, 0.095, -0.3);
                float var37 = Math.min(1.0F, MathUtils.lerp(var29, 0.57, -0.135, 0.095, -0.3));
                if (this.field23959) {
                    var36 = MathUtils.lerp(var29, 0.18, 0.13, 1.0, 1.046);
                    var37 = Math.min(1.0F, MathUtils.lerp(var29, 0.18, 0.13, 1.0, 1.04));
                }

                float var38 = MultiUtilities.method17756(previousRotations.yaw, newRots.yaw);
                float var39 = newRots.pitch - previousRotations.pitch;
                this.rotations2.yaw = this.rotations.yaw;
                this.rotations2.pitch = this.rotations.pitch;
                this.rotations.yaw = previousRotations.yaw + var36 * var38;
                this.rotations.pitch = (previousRotations.pitch + var37 * var39) % 90.0F;
                if (var29 == 0.0F || var29 >= 1.0F || (double) var35 > 0.1 && this.field23956 < 4.0F) {
                    float var41 = Math.abs(MultiUtilities.method17756(newRots.yaw, previousRotations.yaw));
                    this.field23956 = (float) Math.round(var41 * 1.8F / 50.0F);
                    if (this.field23956 <= 1.0F && Math.abs(MultiUtilities.method17756(newRots.yaw, this.rotations.yaw)) > 10.0F) {
                    }

                    this.field23957 = 0.0F;
                    if (mc.pointedEntity == null && var29 != 1.0F) {
                        this.field23955 = Math.random() * 0.5 + 0.25;
                    }

                    previousRotations.yaw = this.rotations.yaw;
                    previousRotations.pitch = this.rotations.pitch;
                }
                break;
            case "Smooth":
                this.rotations2.yaw = this.rotations.yaw;
                this.rotations2.pitch = this.rotations.pitch;
                this.rotations.yaw = (float) ((double) this.rotations.yaw + (double) (var5 * 2.0F) / 5.0);
                this.rotations.pitch = (float) ((double) this.rotations.pitch + (double) (var6 * 2.0F) / 5.0);
                break;
            case "None":
                this.rotations2.yaw = this.rotations.yaw;
                this.rotations2.pitch = this.rotations.pitch;
                this.rotations.yaw = mc.player.rotationYaw;
                this.rotations.pitch = mc.player.rotationPitch;
                break;
            case "LockView":
                this.rotations2.yaw = this.rotations.yaw;
                this.rotations2.pitch = this.rotations.pitch;
                EntityRayTraceResult ray = EntityUtil.method17714(
                        targ, this.rotations.yaw, this.rotations.pitch, e -> true, this.getNumberValueBySettingName("Range")
                );
                if (ray == null || ray.getEntity() != targ) {
                    this.rotations = newRots;
                }
                break;
            case "Test2":
                EntityRayTraceResult var24 = EntityUtil.method17714(
                        targ, this.rotations.yaw, this.rotations.pitch, var0 -> true, this.getNumberValueBySettingName("Range")
                );
                if (var24 != null && var24.getEntity() == targ) {
                    this.rotations2.yaw = this.rotations.yaw;
                    this.rotations2.pitch = this.rotations.pitch;
                    this.rotations.yaw = (float) ((double) this.rotations.yaw + (Math.random() - 0.5) * 2.0 + (double) (var5 / 10.0F));
                    this.rotations.pitch = (float) ((double) this.rotations.pitch + (Math.random() - 0.5) * 2.0 + (double) (var6 / 10.0F));
                    this.field23957 = 0.0F;
                    this.field23956 = 3.0F;
                    return;
                }

                float var10 = this.field23957 / Math.max(1.0F, this.field23956);
                double var15 = targ.getPosX() - targ.lastTickPosX;
                double var17 = targ.getPosZ() - targ.lastTickPosZ;
                float var19 = (float) Math.sqrt(var15 * var15 + var17 * var17);
                float var20 = MathUtils.lerp(var10, 0.57, -0.135, 0.095, -0.3);
                float var21 = Math.min(1.0F, MathUtils.lerp(var10, 0.57, -0.135, 0.095, -0.3));
                float var22 = MultiUtilities.method17756(previousRotations.yaw, newRots.yaw);
                float var23 = newRots.pitch - previousRotations.pitch;
                this.rotations2.yaw = this.rotations.yaw;
                this.rotations2.pitch = this.rotations.pitch;
                this.rotations.yaw = previousRotations.yaw + var20 * var22;
                this.rotations.pitch = (previousRotations.pitch + var21 * var23) % 90.0F;
                if (var10 == 0.0F || var10 >= 1.0F || (double) var19 > 0.1 && this.field23956 < 4.0F) {
                    float var25 = Math.abs(MultiUtilities.method17756(newRots.yaw, previousRotations.yaw));
                    this.field23956 = (float) Math.round(var25 * 1.8F / 50.0F);
                    if (this.field23956 < 3.0F) {
                        this.field23956 = 3.0F;
                    }

                    this.field23957 = 0.0F;
                    if (mc.pointedEntity == null && var10 != 1.0F) {
                        this.field23955 = Math.random() * 0.5 + 0.25;
                    }

                    previousRotations.yaw = this.rotations.yaw;
                    previousRotations.pitch = this.rotations.pitch;
                }
        }
    }

    private double method16832(double var1, double var3) {
        return var1 + Math.random() * (var3 - var1);
    }
}