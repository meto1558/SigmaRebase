package com.mentalfrostbyte.jello.module.impl.render;

import com.mentalfrostbyte.jello.event.impl.game.network.EventReceivePacket;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender3D;
import com.mentalfrostbyte.jello.event.impl.game.world.EventLoadWorld;
import com.mentalfrostbyte.jello.event.impl.player.EventPlayerTick;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import com.mentalfrostbyte.jello.module.impl.render.search.ChunkRegion;
import com.mentalfrostbyte.jello.module.settings.impl.BooleanListSetting;
import com.mentalfrostbyte.jello.module.settings.impl.BooleanSetting;
import com.mentalfrostbyte.jello.module.settings.impl.ColorSetting;
import com.mentalfrostbyte.jello.module.settings.impl.NumberSetting;
import com.mentalfrostbyte.jello.util.MultiUtilities;
import com.mentalfrostbyte.jello.util.ClientColors;
import com.mentalfrostbyte.jello.util.render.Box3D;
import com.mentalfrostbyte.jello.util.render.RenderUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import org.lwjgl.opengl.GL11;
import team.sdhq.eventBus.annotations.EventTarget;

import java.util.*;

public class Search extends Module {
    public List<ChunkRegion> field23499 = new ArrayList<ChunkRegion>();
    public Set<ChunkPos> field23500 = new HashSet<ChunkPos>();

    public Search() {
        super(ModuleCategory.RENDER, "Search", "Searches blocks through the world");
        NumberSetting var3;
        this.registerSetting(var3 = new NumberSetting<Float>("Chunk Range", "Range at which search scans blocks", 5.0F,
                Float.class, 1.0F, 12.0F, 1.0F));
        BooleanSetting var4;
        this.registerSetting(var4 = new BooleanSetting("Holes", "Shows 1x1 explosion protection holes", false));
        this.registerSetting(
                new ColorSetting("Color", "The rendered block color", ClientColors.MID_GREY.getColor(), true));
        BooleanListSetting var5;
        this.registerSetting(var5 = new BooleanListSetting("Blocks", "Blocks to render", true));
        var5.addObserver(var1 -> this.field23499.clear());
        var3.addObserver(var1 -> this.field23499.clear());
        var4.addObserver(var1 -> this.field23499.clear());
    }

    @EventTarget
    public void method16163(EventReceivePacket var1) {
        if (this.isEnabled()) {
            if (var1.getPacket() instanceof SChangeBlockPacket) {
                SChangeBlockPacket var4 = (SChangeBlockPacket) var1.getPacket();
                this.method16164(mc.world.getChunkAt(var4.getPos()).getPos());
            }

            if (var1.getPacket() instanceof SMultiBlockChangePacket) {
                SMultiBlockChangePacket var5 = (SMultiBlockChangePacket) var1.getPacket();
                this.method16164(new ChunkPos(var5.getSectionPos().x, var5.getSectionPos().z));
            }

            if (var1.getPacket() instanceof SChunkDataPacket && Minecraft.getInstance().world != null) {
                SChunkDataPacket var6 = (SChunkDataPacket) var1.getPacket();
                this.method16164(new ChunkPos(var6.getChunkX(), var6.getChunkZ()));
            }
        }
    }

    public void method16164(ChunkPos var1) {
        for (ChunkRegion var5 : this.field23499) {
            if (var5.isSameChunk(var1)) {
                this.field23500.add(var5.getChunkPosition());
            }
        }
    }

    @EventTarget
    public void method16165(EventLoadWorld var1) {
        this.field23499.clear();
        this.field23500.clear();
    }

    public List<BlockPos> method16166(ChunkPos var1) {
        ArrayList var4 = new ArrayList();
        int var5 = var1.x * 16;
        int var6 = var1.z * 16;
        float var7 = 1;
        int var8 = var5 + 15;
        int var9 = var6 + 15;
        float var10 = 255;

        for (float var11 = var7; var11 <= (float) var10 && !(var11 > 100.0F); var11++) {
            for (float var12 = (float) var5; var12 <= (float) var8; var12++) {
                for (float var13 = (float) var6; var13 <= (float) var9; var13++) {
                    BlockPos var14 = new BlockPos(var12, var11, var13);
                    var4.add(var14);
                }
            }
        }

        return var4;
    }

    public List<BlockPos> method16167(ChunkPos var1) {
        ArrayList var4 = new ArrayList();
        if (var1 == null) {
            return null;
        } else {
            List var5 = (List) this.getSettingValueBySettingName("Blocks");

            for (BlockPos var7 : this.method16166(var1)) {
                String var8 = Registry.BLOCK.getKey(mc.world.getBlockState(var7).getBlock()).toString();
                if (var5.contains(var8)) {
                    var4.add(var7);
                }
            }

            if (this.getBooleanValueFromSettingName("Holes")) {
                label57: for (BlockPos var13 : this.method16166(var1)) {
                    if (mc.world.getBlockState(var13).getBlock() == Blocks.AIR) {
                        for (Direction var11 : Direction.values()) {
                            if (var11 != Direction.UP
                                    && mc.world.getBlockState(var13.add(var11.getDirectionVec()))
                                    .getBlock() != Blocks.OBSIDIAN
                                    && mc.world.getBlockState(var13.add(var11.getDirectionVec()))
                                    .getBlock() != Blocks.BEDROCK) {
                                continue label57;
                            }
                        }

                        var4.add(var13.down());
                    }
                }
            }

            return var4;
        }
    }

    @EventTarget
    public void method16168(EventPlayerTick var1) {
        if (this.isEnabled()) {
            if (mc.player.ticksExisted < 20) {
                this.field23499.clear();
            } else {
                int var4 = (int) this.getNumberValueBySettingName("Chunk Range");
                List<ChunkPos> var5 = new ArrayList();

                for (int var6 = -5; var6 < 5; var6++) {
                    for (int var7 = -5; var7 < 5; var7++) {
                        var5.add(new ChunkPos(mc.player.chunkCoordX + var6, mc.player.chunkCoordZ + var7));
                    }
                }

                Iterator var11 = this.field23499.iterator();

                while (var11.hasNext()) {
                    ChunkRegion var12 = (ChunkRegion) var11.next();
                    if (var12.getDistanceFromChunk(new ChunkPos(mc.player.chunkCoordX, mc.player.chunkCoordZ)) > 7
                            || this.field23500.contains(var12.getChunkPosition())) {
                        var11.remove();
                    }
                }

                this.field23500.clear();

                label52: for (ChunkPos var8 : var5) {
                    for (ChunkRegion var10 : this.field23499) {
                        if (var10.isSameChunk(var8)) {
                            continue label52;
                        }
                    }

                    this.field23499.add(new ChunkRegion(var8.x, var8.z, this.method16167(var8)));
                    break;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        this.field23499.clear();
        this.field23500.clear();
    }

    @EventTarget
    public void method16169(EventRender3D var1) {
        if (this.isEnabled()) {
            this.method16170();
        }
    }

    public void method16170() {
        int var3 = MultiUtilities.applyAlpha(this.parseSettingValueToIntBySettingName("Color"), 0.14F);
        GL11.glPushMatrix();
        GL11.glDisable(2929);

        for (ChunkRegion var5 : this.field23499) {
            for (BlockPos var7 : var5.blockPositions) {
                double var8 = (double) var7.getX() - mc.gameRenderer.getActiveRenderInfo().getPos().getX();
                double var10 = (double) var7.getY() - mc.gameRenderer.getActiveRenderInfo().getPos().getY();
                double var12 = (double) var7.getZ() - mc.gameRenderer.getActiveRenderInfo().getPos().getZ();
                Box3D var14 = new Box3D(var8, var10, var12, var8 + 1.0, var10 + 1.0, var12 + 1.0);
                RenderUtil.render3DColoredBox(var14, var3);
            }
        }

        GL11.glEnable(2929);
        GL11.glPopMatrix();
    }
}
