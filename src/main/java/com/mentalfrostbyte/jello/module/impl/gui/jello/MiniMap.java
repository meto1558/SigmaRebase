package com.mentalfrostbyte.jello.module.impl.gui.jello;

import com.mentalfrostbyte.jello.event.impl.game.render.EventRender2DOffset;
import com.mentalfrostbyte.jello.event.impl.game.world.EventLoadWorld;
import com.mentalfrostbyte.jello.event.impl.player.EventUpdate;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.data.ModuleCategory;
import com.mentalfrostbyte.jello.module.impl.gui.jello.minimap.MinimapChunkHandler;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import com.mentalfrostbyte.jello.util.game.player.MovementUtil;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;
import team.sdhq.eventBus.annotations.EventTarget;
import team.sdhq.eventBus.annotations.priority.HigherPriority;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MiniMap extends Module {
    private static float field23708 = 64.0F;
    public ByteBuffer field23704 = null;
    public List<MinimapChunkHandler> field23716 = new ArrayList<>();
    private int field23707;
    private final int field23715 = 10;
    private double field23717;
    private double field23718;

    public MiniMap() {
        super(ModuleCategory.GUI, "MiniMap", "Shows a mini map");
        this.setAvailableOnClassic(false);
    }

    public static void method16503(ByteBuffer buff) {
        int var3 = -7687425;

        for (int var4 = 0; var4 < 16; var4++) {
            for (int var5 = 0; var5 < 16; var5++) {
                buff.put((byte) (var3 >> 16 & 0xFF));
                buff.put((byte) (var3 >> 8 & 0xFF));
                buff.put((byte) (var3 & 0xFF));
            }
        }

        buff.flip();
    }

    @EventTarget
    public void onWorldLoad(EventLoadWorld event) {
        this.field23716.clear();
    }

    @Override
    public void onDisable() {
        this.field23716.clear();
    }

    @EventTarget
    public void onTick(EventUpdate event) {
        if (this.isEnabled() && mc.player != null && mc.world != null) {
            this.field23707++;
            if ((double) field23708 < mc.player.getPosY() && mc.player.onGround) {
                field23708 += 0.5F;
            }

            if ((double) field23708 > mc.player.getPosY() && mc.player.onGround) {
                field23708 -= 0.5F;
            }

            if (this.field23707 >= 1) {
                List<Chunk> chunks = new ArrayList<>();

                for (int var5 = -this.field23715 / 2; var5 < this.field23715 / 2; var5++) {
                    for (int var6 = -this.field23715 / 2; var6 < this.field23715 / 2; var6++) {
                        chunks.add(mc.world.getChunk(mc.player.chunkCoordX + var5, mc.player.chunkCoordZ + var6));
                    }
                }

                Iterator var11 = this.field23716.iterator();

                while (var11.hasNext()) {
                    MinimapChunkHandler var12 = (MinimapChunkHandler) var11.next();
                    int var7 = var12.chunk.getPos()
                            .getChessboardDistance(new ChunkPos(mc.player.chunkCoordX, mc.player.chunkCoordZ));
                    if (var7 > 7) {
                        var11.remove();
                    }
                }

                for (Chunk chunk : chunks) {
                    if (chunk == null) {
                        return;
                    }

                    boolean var8 = false;

                    for (MinimapChunkHandler var10 : this.field23716) {
                        if (var10.matchesChunk(chunk)) {
                            var8 = true;
                            break;
                        }
                    }

                    if (!var8) {
                        this.field23716.add(new MinimapChunkHandler(chunk));
                        break;
                    }
                }

                for (MinimapChunkHandler var16 : this.field23716) {
                    var16.checkAndUpdateBuffer();
                }

                this.field23717 = (mc.player.getPosX() - (double) (mc.player.chunkCoordX * 16)) / 16.0;
                this.field23718 = (mc.player.getPosZ() - (double) (mc.player.chunkCoordZ * 16)) / 16.0;
                int field23706 = 95;
                this.field23704 = this.method16502(field23706);
                this.field23707 = 0;
            }
        }
    }

    @EventTarget
    @HigherPriority
    public void onRender2D(EventRender2DOffset event) {
        if (this.isEnabled() && mc.player != null && mc.world != null) {
            if (this.field23704 != null) {
                if (!Minecraft.getInstance().gameSettings.showDebugInfo) {
                    if (!Minecraft.getInstance().gameSettings.hideGUI) {
                        ByteBuffer var4 = this.field23704;
                        int yOffset = event.getYOffset();
                        int field23709 = 150;
                        if (var4 != null) {
                            String arrow = "^";
                            TrueTypeFont var6 = ResourceRegistry.JelloMediumFont20;
                            float var7 = 1.5F;
                            int field23710 = 150;
                            int field23711 = 10;
                            RenderUtil.drawRectNormalised((float) field23711, (float) yOffset,
                                    (float) field23710, (float) field23709, -7687425);
                            GL11.glPushMatrix();
                            float var8 = (float) (field23710 / this.field23715);
                            float var9 = (float) ((double) (var8 * var7) * this.field23718);
                            float var10 = (float) ((double) (-var8 * var7) * this.field23717);
                            GL11.glTranslatef((float) (field23711 + field23710 / 2),
                                    (float) (yOffset + field23709 / 2), 0.0F);
                            GL11.glRotatef(90.0F - mc.player.rotationYaw, 0.0F, 0.0F, 1.0F);
                            GL11.glTranslatef((float) (-field23710 / 2), (float) (-field23709 / 2), 0.0F);
                            float var11 = (float) field23710 * var7;
                            float var12 = (float) field23709 * var7;
                            RenderUtil.startScissorUnscaled(field23711, yOffset,
                                    field23711 + field23710, yOffset + field23709);
                            RenderUtil.drawImage(0.0F, 0.0F, 0.0F, 0.0F, Resources.shoutIconPNG);
                            float var13 = -var11 / 2.0F + (float) (field23710 / 2) + var9;
                            float var14 = -var12 / 2.0F + (float) (field23709 / 2) + var10;
                            RenderUtil.drawImage(0.0F, 0.0F, 0.0F, 0.0F, Resources.gingerbreadIconPNG);
                            RenderUtil.drawTexturedQuad(
                                    var13,
                                    var14,
                                    var11,
                                    var12,
                                    var4,
                                    ClientColors.LIGHT_GREYISH_BLUE.getColor(),
                                    0.0F,
                                    0.0F,
                                    (float) (this.field23715 * 16),
                                    (float) (this.field23715 * 16),
                                    true,
                                    false);
                            RenderUtil.endScissor();
                            GL11.glPopMatrix();
                            GL11.glPushMatrix();
                            int direction = (int) MovementUtil.getDirection();
                            GL11.glTranslatef((float) (field23711 + field23710 / 2 + 1),
                                    (float) (yOffset + field23709 / 2 + 3), 0.0F);
                            GL11.glRotatef((float) (direction) - mc.player.rotationYaw, 0.0F, 0.0F, 1.0F);
                            GL11.glTranslatef((float) (-(field23711 + field23710 / 2 + 1)),
                                    (float) (-(yOffset + field23709 / 2)), 0.0F);
                            RenderUtil.drawString(
                                    var6, (float) (field23711 + field23710 / 2 - 4),
                                    (float) (yOffset + field23709 / 2 - 8), arrow, 1879048192);
                            GL11.glPopMatrix();
                            GL11.glPushMatrix();
                            GL11.glTranslatef((float) (field23711 + field23710 / 2 + 1),
                                    (float) (yOffset + field23709 / 2), 0.0F);
                            GL11.glRotatef((float) (direction) - mc.player.rotationYaw, 0.0F, 0.0F, 1.0F);
                            GL11.glTranslatef((float) (-(field23711 + field23710 / 2 + 1)),
                                    (float) (-(yOffset + field23709 / 2)), 0.0F);
                            RenderUtil.drawString(
                                    var6,
                                    (float) (field23711 + field23710 / 2 - 4),
                                    (float) (yOffset + field23709 / 2 - 8),
                                    arrow,
                                    ClientColors.LIGHT_GREYISH_BLUE.getColor());
                            GL11.glPopMatrix();
                            RenderUtil.drawShadowedBorder((float) field23711, (float) yOffset,
                                    (float) field23710, (float) field23709, 23.0F, 0.75F);
                            RenderUtil.drawRoundedRect((float) field23711, (float) yOffset,
                                    (float) field23710, (float) field23709, 8.0F, 0.7F);
                        }

                        event.addOffset(field23709 + 10);
                    }
                }
            }
        }
    }

    public ByteBuffer method16502(int var1) {
        List<Chunk> var4 = new ArrayList();

        for (int var5 = -this.field23715 / 2; var5 < this.field23715 / 2; var5++) {
            for (int var6 = -this.field23715 / 2; var6 < this.field23715 / 2; var6++) {
                var4.add(mc.world.getChunk(mc.player.chunkCoordX + var5, mc.player.chunkCoordZ + var6));
            }
        }

        ByteBuffer var16 = BufferUtils.createByteBuffer(this.field23715 * 16 * this.field23715 * 16 * 3);
        int var17 = 0;
        int var7 = var16.position();

        for (Chunk var9 : var4) {
            ByteBuffer var10 = BufferUtils.createByteBuffer(768);
            method16503(var10);
            MinimapChunkHandler var11 = null;

            for (MinimapChunkHandler var13 : this.field23716) {
                if (var13.matchesChunk(var9)) {
                    var11 = var13;
                    break;
                }
            }

            if (var11 != null && var11.chunkBuffer != null) {
                var10 = var11.chunkBuffer;
            }

            int var18 = var16.position();
            int var19 = var16.position();

            for (int var14 = 0; var14 < 16; var14++) {
                for (int var15 = 0; var15 < 16; var15++) {
                    var16.put(var10.get());
                    var16.put(var10.get());
                    var16.put(var10.get());
                }

                var18 += 16 * this.field23715 * 3;
                if (var18 < var16.limit()) {
                    var16.position(var18);
                }
            }

            var7 += 48;
            if (var19 + 48 < var16.limit()) {
                var16.position(var19 + 48);
            }

            if (var17 != var7 / (48 * this.field23715)) {
                var17 = var7 / (48 * this.field23715);
                if (256 * this.field23715 * 3 * var17 < var16.limit()) {
                    var16.position(256 * this.field23715 * 3 * var17);
                }
            }

            var10.position(0);
        }

        var16.position(16 * this.field23715 * 16 * this.field23715 * 3);
        var16.flip();
        return var16;
    }
}
