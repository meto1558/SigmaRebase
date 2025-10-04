package com.mentalfrostbyte.jello.module.impl.render.jello;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.game.network.EventReceivePacket;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender3D;
import com.mentalfrostbyte.jello.event.impl.game.world.EventLoadWorld;
import com.mentalfrostbyte.jello.gui.impl.jello.ingame.options.Waypoint2;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.data.ModuleCategory;
import com.mentalfrostbyte.jello.module.impl.movement.ClickTP;
import com.mentalfrostbyte.jello.module.settings.impl.BooleanSetting;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.game.world.PositionUtil;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.server.SDestroyEntitiesPacket;
import net.minecraft.network.play.server.SSpawnMobPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;
import team.sdhq.eventBus.annotations.EventTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Waypoints extends Module {
    public HashMap<UUID, Waypoint2> field23572 = new HashMap<>();

    public Waypoints() {
        super(ModuleCategory.RENDER, "Waypoints", "Renders waypoints you added in Jello maps");
        this.registerSetting(new BooleanSetting("Unspawn Positions", "Adds a waypoint when a player unspawns", false));
        this.setAvailableOnClassic(false);
    }

    @EventTarget
    public void method16274(EventLoadWorld var1) {
        this.field23572.clear();
    }

    @EventTarget
    public void method16275(EventReceivePacket var1) {
        if (mc.world != null) {
            if (!(var1.packet instanceof SDestroyEntitiesPacket sDestroyEntitiesPacket)) {
                if (!(var1.packet instanceof SSpawnObjectPacket var11)) {
                    if (!(var1.packet instanceof SSpawnMobPacket var10)) {
                        if (var1.packet instanceof SSpawnPlayerPacket var4) {
                            this.field23572.remove(var4.getUniqueId());
                        }
                    } else {
                        this.field23572.remove(var10.getUniqueId());
                    }
                } else {
                    this.field23572.remove(var11.getUniqueId());
                }
            } else {

				for (int var8 : sDestroyEntitiesPacket.getEntityIDs()) {
                    Entity var9 = mc.world.getEntityByID(var8);
                    if (var9 != null && var9 instanceof PlayerEntity) {
                        this.field23572.remove(var9.getUniqueID());

                        this.field23572
                                .put(
                                        var9.getUniqueID(),
                                        new Waypoint2(
                                                var9.getName().getUnformattedComponentText() + " Unspawn",
                                                (int) var9.getPosX(),
                                                (int) var9.getPosY(),
                                                (int) var9.getPosZ(),
                                                ClientColors.DARK_OLIVE.getColor()));
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        this.field23572.clear();
    }

    public List<Waypoint2> method16276(List<Waypoint2> var1) {
        List<Waypoint2> var4 = new ArrayList<>(var1);
        if (this.getBooleanValueFromSettingName("Unspawn Positions")) {
            var4.addAll(this.field23572.values());
        }

        var4.sort(
                (var0, var1x) -> !(mc.player.getDistanceSq(var0.x, var0.field35893,
                        var0.z) < mc.player.getDistanceSq(var1x.x,
                        var1x.field35893, var1x.z))
                        ? -1
                        : 1);
        return var4;
    }

    @EventTarget
    public void method16277(EventRender3D var1) {
        if (this.isEnabled()) {
            for (Waypoint2 var5 : this.method16276(Client.getInstance().waypointsManager.getWaypoints())) {
                BlockPos var6 = new BlockPos(
                        var5.x - (var5.x <= 0 ? 1 : 0), var5.field35893,
                        var5.z - (var5.z <= 0 ? 1 : 0));
                double var7 = Math.sqrt(PositionUtil.calculateDistanceSquared(var6));
                if (!(var7 > 300.0)) {
                    if (mc.world.getChunk(var6) != null && var5.config) {
                        int var9 = var6.getX() % 16;
                        int var10 = var6.getZ() % 16;
                        if (var10 < 0) {
                            var10 += 16;
                        }

                        if (var9 < 0) {
                            var9 += 16;
                        }

                        int var11 = mc.world.getChunk(var6).getHeightmap(Heightmap.Type.WORLD_SURFACE).getHeight(var9,
                                var10);
                        if (var11 == 0) {
                            var11 = 64;
                        }

                        if ((float) var11 != var5.field35893) {
                            var5.field35893 = var5.field35893 + ((float) var11 - var5.field35893) * 0.1F;
                        }
                    }

                    float var13 = (float) ((double) var5.field35893
                            - Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getPos().getY());
                    float var14 = (float) ((double) var5.x
                            - Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getPos().getX());
                    float var15 = (float) ((double) var5.z
                            - Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getPos().getZ());
                    if (var5.x < 0) {
                        var14--;
                    }

                    if (var5.z < 0) {
                        var15--;
                    }

                    float var12 = 1.0F;
                    var12 = (float) Math.max(1.0, Math.sqrt(PositionUtil.calculateDistanceSquared(var6) / 30.0));
                    this.method16283(var14, var13, var15, var5.name, var5.color, var12);
                }
            }

            RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
            TextureImpl.unbind();
            mc.getTextureManager().bindTexture(TextureManager.RESOURCE_LOCATION_EMPTY);
        }
    }

    private void method16278(int var1) {
        for (int var4 = 0; var4 <= 270; var4 += 90) {
            GL11.glPushMatrix();
            GL11.glRotatef(var4, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
            this.method16279(
                    MathHelper.blendARGB(ClientColors.DEEP_TEAL.getColor(), var1, 0.04F * (float) var4 / 90.0F));
            GL11.glPopMatrix();
        }

        for (int var5 = 0; var5 <= 270; var5 += 90) {
            GL11.glPushMatrix();
            GL11.glRotatef(var5, 0.0F, 1.0F, 0.0F);
            this.method16279(
                    MathHelper.blendARGB(ClientColors.DEEP_TEAL.getColor(), var1, 0.04F * (float) var5 / 90.0F));
            GL11.glPopMatrix();
        }
    }

    private void method16279(int var1) {
        GL11.glColor4fv(MathHelper.argbToNormalizedRGBA(var1));
		ClickTP.rotationThingy();
	}

    private void method16280(float var1, float var2, float var3, float var4) {
        GL11.glColor4f(var1 / 255.0F, var2 / 255.0F, var3 / 255.0F, var4);
        GL11.glTranslatef(0.0F, 0.0F, 0.3F);
        GL11.glNormal3f(0.0F, 0.0F, 1.0F);
        GL11.glRotated(-37.0, 1.0, 0.0, 0.0);
        GL11.glBegin(6);
        GL11.glVertex2f(0.0F, 0.0F);
        GL11.glVertex2f(0.0F, 0.5F);
        GL11.glVertex2f(0.5F, 0.5F);
        GL11.glVertex2f(0.5F, 0.0F);
        GL11.glEnd();
    }

    private void method16281(float var1) {
        GL11.glBegin(2);

        for (int var4 = 0; var4 < 360; var4++) {
            double var5 = (double) var4 * Math.PI / 180.0;
            GL11.glVertex2d(Math.cos(var5) * (double) var1, Math.sin(var5) * (double) var1);
        }

        GL11.glEnd();
    }

    private void method16282(float var1) {
        GL11.glBegin(6);

        for (int var4 = 0; var4 < 360; var4++) {
            double var5 = (double) var4 * Math.PI / 180.0;
            GL11.glVertex2d(Math.cos(var5) * (double) var1, Math.sin(var5) * (double) var1);
        }

        GL11.glEnd();
    }

    public void method16283(float var1, float var2, float var3, String var4, int var5, float var6) {
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDisable(2896);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        GL11.glAlphaFunc(519, 0.0F);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.114F);
        GL11.glTranslated((double) var1 + 0.5, var2, (double) var3 + 0.5);
        GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);
        this.method16282(0.5F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glColor4fv(MathHelper.argbToNormalizedRGBA(var5));
        GL11.glTranslated((double) var1 + 0.5, var2 + 0.7F, (double) var3 + 0.5);
        GL11.glRotatef((float) (mc.player.ticksExisted % 90 * 4), 0.0F, -1.0F, 0.0F);
        GL11.glLineWidth(1.4F + 1.0F / var6 * 1.4F);
        this.method16281(0.6F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated((double) var1 + 0.5, var2 + 0.7F, (double) var3 + 0.5);
        GL11.glRotatef((float) (mc.player.ticksExisted % 90 * 4), 0.0F, 1.0F, 0.0F);
        this.method16278(var5);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glAlphaFunc(519, 0.0F);
        GL11.glTranslated((double) var1 + 0.5, (double) var2 + 1.9, (double) var3 + 0.5);
        GL11.glRotatef(mc.gameRenderer.getActiveRenderInfo().getYaw(), 0.0F, -1.0F, 0.0F);
        GL11.glRotatef(mc.gameRenderer.getActiveRenderInfo().getPitch(), 1.0F, 0.0F, 0.0F);
        TrueTypeFont var9 = ResourceRegistry.JelloLightFont25;
        GL11.glPushMatrix();
        GL11.glScalef(-0.009F * var6, -0.009F * var6, -0.009F * var6);
        GL11.glTranslated(0.0, -20.0 * Math.sqrt(Math.sqrt(var6)), 0.0);
        int var11 = MathHelper.applyAlpha(MathHelper.blendARGB(ClientColors.LIGHT_GREYISH_BLUE.getColor(),
                ClientColors.DEEP_TEAL.getColor(), 75.0F), 0.5F);
        RenderUtil.drawRect(
                (float) (-var9.getWidth(var4) / 2 - 14), -5.0F, (float) var9.getWidth(var4) / 2.0F + 14.0F,
                (float) (var9.getHeight() + 7), var11);
        RenderUtil.drawRoundedRect(
                (float) (-var9.getWidth(var4) / 2 - 14), -5.0F, (float) (var9.getWidth(var4) + 28),
                (float) (var9.getHeight() + 12), 20.0F, 0.5F);
        GL11.glTranslated(-var9.getWidth(var4) / 2, 0.0, 0.0);
        RenderUtil.drawString(var9, 0.0F, 0.0F, var4,
                MathHelper.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.8F));
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glEnable(2896);
        GL11.glDisable(2848);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
    }
}
