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
import com.mentalfrostbyte.jello.util.game.player.MovementUtil2;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.game.world.BoundingBox;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import org.lwjgl.opengl.GL11;
import team.sdhq.eventBus.annotations.EventTarget;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Search extends Module {
    private List<ChunkRegion> chunkRegions = new ArrayList<>();
    private Set<ChunkPos> processedChunks = new HashSet<>();
    private int tickCounter = 0;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    public Search() {
        super(ModuleCategory.RENDER, "Search", "Searches blocks through the world");

        NumberSetting chunkRangeSetting = new NumberSetting<Float>("Chunk Range", "Range at which search scans blocks", 5.0F, Float.class, 1.0F, 12.0F, 1.0F);
        this.registerSetting(chunkRangeSetting);

        NumberSetting tickDelay = new NumberSetting<Integer>("Tick Delay", "Delay between each refresh (greatly increases performance)", 50, Integer.class, 0, 500, 50);
        this.registerSetting(tickDelay);

        BooleanSetting showHolesSetting = new BooleanSetting("Holes", "Shows 1x1 explosion protection holes", false);
        this.registerSetting(showHolesSetting);

        ColorSetting renderColorSetting = new ColorSetting("Color", "The rendered block color", ClientColors.MID_GREY.getColor(), true);
        this.registerSetting(renderColorSetting);

        BooleanListSetting blocksToRenderSetting = new BooleanListSetting("Blocks", "Blocks to render", true);
        this.registerSetting(blocksToRenderSetting);

        blocksToRenderSetting.addObserver(event -> this.chunkRegions.clear());
        chunkRangeSetting.addObserver(event -> this.chunkRegions.clear());
        showHolesSetting.addObserver(event -> this.chunkRegions.clear());
    }

    @EventTarget
    public void onPacketReceive(EventReceivePacket event) {
        if (this.isEnabled()) {
            if (event.getPacket() instanceof SChangeBlockPacket) {
                SChangeBlockPacket packet = (SChangeBlockPacket) event.getPacket();
                this.updateChunkPosition(mc.world.getChunkAt(packet.getPos()).getPos());
            }

            if (event.getPacket() instanceof SMultiBlockChangePacket) {
                SMultiBlockChangePacket packet = (SMultiBlockChangePacket) event.getPacket();
                this.updateChunkPosition(new ChunkPos(packet.getSectionPos().x, packet.getSectionPos().z));
            }

            if (event.getPacket() instanceof SChunkDataPacket && Minecraft.getInstance().world != null) {
                SChunkDataPacket packet = (SChunkDataPacket) event.getPacket();
                this.updateChunkPosition(new ChunkPos(packet.getChunkX(), packet.getChunkZ()));
            }
        }
    }

    public void updateChunkPosition(ChunkPos chunkPos) {
        for (ChunkRegion region : this.chunkRegions) {
            if (region.isSameChunk(chunkPos)) {
                this.processedChunks.add(region.getChunkPosition());
            }
        }
    }

    @EventTarget
    public void onWorldLoad(EventLoadWorld event) {
        this.chunkRegions.clear();
        this.processedChunks.clear();
    }

    public List<BlockPos> getBlocksInChunk(ChunkPos chunkPos) {
        List<BlockPos> blockPositions = new ArrayList<>();
        int chunkStartX = chunkPos.x * 16;
        int chunkStartZ = chunkPos.z * 16;
        int chunkEndX = chunkStartX + 15;
        int chunkEndZ = chunkStartZ + 15;

        Chunk chunk = mc.world.getChunk(chunkPos.x, chunkPos.z);

        for (int x = chunkStartX; x <= chunkEndX; x++) {
            for (int z = chunkStartZ; z <= chunkEndZ; z++) {
                int height = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE).getHeight(x - chunk.getPos().getXStart(), z - chunk.getPos().getZStart());
                for (int y = -64; y <= height; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (mc.world != null && !mc.world.getBlockState(pos).isAir()) { // Skip air blocks
                        blockPositions.add(pos);
                    }
                }
            }
        }

        return blockPositions;
    }

    public List<BlockPos> getFilteredBlocksInChunk(ChunkPos chunkPos) {
        if (chunkPos == null) return Collections.emptyList();

        List<BlockPos> blocksInChunk = this.getBlocksInChunk(chunkPos); // Cache the result
        Set<String> blocksToRender = new HashSet<>((List<String>) this.getSettingValueBySettingName("Blocks"));
        List<BlockPos> filteredPositions = new ArrayList<>();

        for (BlockPos pos : blocksInChunk) {
            String blockName = Registry.BLOCK.getKey(mc.world.getBlockState(pos).getBlock()).toString();
            if (blocksToRender.contains(blockName)) {
                filteredPositions.add(pos);
            }
        }

        // Optimize explosion protection holes
        if (this.getBooleanValueFromSettingName("Holes")) {
            for (BlockPos pos : blocksInChunk) {
                if (mc.world.getBlockState(pos).isAir()) {
                    boolean isProtected = true;
                    for (Direction direction : Direction.values()) {
                        if (direction != Direction.UP) {
                            Block block = mc.world.getBlockState(pos.offset(direction)).getBlock();
                            if (block != Blocks.OBSIDIAN && block != Blocks.BEDROCK) {
                                isProtected = false;
                                break;
                            }
                        }
                    }
                    if (isProtected) {
                        filteredPositions.add(pos.down());
                    }
                }
            }
        }

        return filteredPositions;
    }

    @EventTarget
    public void onPlayerTick(EventPlayerTick event) {
        if (!this.isEnabled()) return;

        if (mc.player.ticksExisted < 20) {
            this.chunkRegions.clear();
            return;
        }

        tickCounter++;
        if (tickCounter % this.getNumberValueBySettingName("Tick Delay") != 0) return;


        int chunkRange = (int) this.getNumberValueBySettingName("Chunk Range");
        ChunkPos playerChunk = new ChunkPos(mc.player.chunkCoordX, mc.player.chunkCoordZ);
        Set<ChunkPos> nearbyChunks = new HashSet<>();

        for (int xOffset = -chunkRange; xOffset <= chunkRange; xOffset++) {
            for (int zOffset = -chunkRange; zOffset <= chunkRange; zOffset++) {
                ChunkPos chunkPos = new ChunkPos(playerChunk.x + xOffset, playerChunk.z + zOffset);
                nearbyChunks.add(chunkPos);
            }
        }

        Iterator<ChunkRegion> iterator = this.chunkRegions.iterator();
        while (iterator.hasNext()) {
            ChunkRegion region = iterator.next();
            if (!nearbyChunks.contains(region.getChunkPosition())) {
                iterator.remove();
            }
        }

        for (ChunkPos chunkPos : nearbyChunks) {
            if (!processedChunks.contains(chunkPos)) {
                executorService.submit(() -> {
                    List<BlockPos> filteredBlocks = this.getFilteredBlocksInChunk(chunkPos);
                    if (!filteredBlocks.isEmpty()) {
                        synchronized (this.chunkRegions) {
                            this.chunkRegions.add(new ChunkRegion(chunkPos.x, chunkPos.z, filteredBlocks));
                        }
                    }
                });
            }

        }
    }

    @Override
    public void onEnable() {
        this.chunkRegions.clear();
        this.processedChunks.clear();
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (this.isEnabled()) {
            this.renderChunkRegions();
        }
    }

    public void renderChunkRegions() {
        int color = MovementUtil2.applyAlpha(this.parseSettingValueToIntBySettingName("Color"), 0.14F);
        GL11.glPushMatrix();
        GL11.glDisable(2929);

        for (ChunkRegion region : this.chunkRegions) {
            for (BlockPos pos : region.blockPositions) {
                double offsetX = (double) pos.getX() - mc.gameRenderer.getActiveRenderInfo().getPos().getX();
                double offsetY = (double) pos.getY() - mc.gameRenderer.getActiveRenderInfo().getPos().getY();
                double offsetZ = (double) pos.getZ() - mc.gameRenderer.getActiveRenderInfo().getPos().getZ();
                BoundingBox boundingBox = new BoundingBox(offsetX, offsetY, offsetZ, offsetX + 1.0, offsetY + 1.0, offsetZ + 1.0);
                RenderUtil.renderWireframeBox(boundingBox, color);
            }
        }

        GL11.glEnable(2929);
        GL11.glPopMatrix();
    }

}
