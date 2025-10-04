package com.mentalfrostbyte.jello.managers;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.player.EventUpdate;
import com.mentalfrostbyte.jello.event.impl.game.world.EventLoadWorld;
import com.mentalfrostbyte.jello.gui.base.elements.impl.maps.Chunk;
import com.mentalfrostbyte.jello.gui.impl.jello.ingame.options.Waypoint2;
import com.mentalfrostbyte.jello.managers.data.Manager;
import com.mentalfrostbyte.jello.managers.util.waypoints.Class2531;
import com.mentalfrostbyte.jello.managers.util.waypoints.Class7927;
import com.mentalfrostbyte.jello.util.system.FileUtil;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import team.sdhq.eventBus.annotations.EventTarget;
import org.lwjgl.BufferUtils;

public class WaypointsManager extends Manager {
    private final Minecraft field36365 = Minecraft.getInstance();
    private final List<ChunkPos> field36366 = new ArrayList<ChunkPos>();
    private final List<ChunkPos> field36367 = new ArrayList<ChunkPos>();
    private final List<Waypoint2> field36368 = new ArrayList<Waypoint2>();
    private boolean field36369 = false;
    private final int field36370 = 10;
    private String field36371 = null;
    public HashMap<Long, Class7927> field36372 = new HashMap<Long, Class7927>();
    public int field36373 = 0;
    public List<Class2531> field36374 = new ArrayList<Class2531>();
    public HashMap<Long, ByteBuffer> field36375 = new HashMap<Long, ByteBuffer>();
    public ByteBuffer field36376 = BufferUtils.createByteBuffer(this.field36370 * 16 * this.field36370 * 16 * 3);

    @Override
    public void init() {
        super.init();
        int var3 = -7687425;

        for (int var4 = 0; var4 < 16; var4++) {
            for (int var5 = 0; var5 < 16; var5++) {
                this.field36376.put((byte) (var3 >> 16 & 0xFF));
                this.field36376.put((byte) (var3 >> 8 & 0xFF));
                this.field36376.put((byte) (var3 & 0xFF));
            }
        }

        ((Buffer) this.field36376).flip();
    }

    public List<Waypoint2> getWaypoints() {
        if (!this.field36369) {
            this.method29992();
        }

        return this.field36368;
    }

    public void method29990(Waypoint2 var1) {
        this.field36368.add(var1);
        this.method29991();
    }

    public void method29991() {
        if (this.field36371 != null) {
            File var3 = new File(this.field36371 + "/waypoints.json");
            JsonArray var4 = new JsonArray();

            for (Waypoint2 var6 : this.field36368) {
                var4.add(var6.method29263());
            }

            JsonObject var8 = new JsonObject();
            var8.add("waypoints", var4);

            try {
                FileUtil.save(var8, var3);
            } catch (IOException | JsonParseException exc) {
                Client.logger.error(exc);
            }
        }
    }

    private void method29992() {
        File var3 = new File(this.field36371 + "/waypoints.json");

        try {
            JsonObject var4 = FileUtil.readFile(var3);
            if (!var4.has("waypoints")) {
                var4.add("waypoints", new JsonArray());
            }

            for (Object var6 : var4.getAsJsonArray("waypoints")) {
                this.field36368.add(new Waypoint2((JsonObject) var6));
            }

            this.field36369 = true;
        } catch (IOException exc) {
            Client.logger.error(exc);
        }
    }

    public void method29993(Waypoint2 var1) {
        this.field36368.remove(var1);
    }

    @EventTarget
    public void onLoadWorld(EventLoadWorld event) {
        try {
            this.method29991();
            this.method29997();
        } catch (IOException var5) {
            Client.logger.error(var5);
        }

        this.field36371 = this.method29999();
        this.field36372.clear();
        this.field36366.clear();
        this.field36367.clear();
        this.field36369 = false;
        this.field36368.clear();
    }

    @EventTarget
    public void onTick(EventUpdate event) {
        if (this.field36365.world != null) {
            if (this.field36371 != null) {
                boolean var4 = false;
                if (!var4) {
                    if (this.field36365.player.ticksExisted % 140 == 0) {
                        Class2531 var5 = Class7927.method26605(this.field36365.world.getChunk(this.field36365.player.getPosition()).getPos());
                        Iterator<Entry<Long, Class7927>> var6 = this.field36372.entrySet().iterator();

                        while (var6.hasNext()) {
                            Entry var7 = var6.next();
                            Class2531 var8 = new Class2531((Long) var7.getKey());
                            double var9 = var5.field16734 - var8.field16734;
                            double var11 = var5.field16735 - var8.field16735;
                            double var13 = Math.sqrt(var9 * var9 + var11 * var11);
                            if (var13 > 2.0) {
                                try {
                                    ObjectOutputStream var15 = new ObjectOutputStream(
                                            new FileOutputStream(this.method30001(this.field36371, (Class7927) var7.getValue()))
                                    );
                                    ((Class7927) var7.getValue()).method26603(var15);
                                    var15.close();
                                } catch (IOException exc) {
                                    Client.logger.warn(exc);
                                }

                                this.field36373 = Math.max(0, this.field36373 - ((Class7927) var7.getValue()).field33959.size());
                                var6.remove();
                            }
                        }
                    }

                    String var23 = this.field36371;
                    int var24 = 0;

                    for (int var25 = 0; var25 < this.field36365.world.getChunkProvider().array.chunks.length(); var25++) {
                        net.minecraft.world.chunk.Chunk var17 = this.field36365.world.getChunkProvider().array.chunks.get(var25);
                        if (var17 != null) {
                            boolean var18 = this.field36366.contains(var17.getPos());
                            boolean var19 = this.field36367.contains(var17.getPos());
                            if ((!var18 || var19)
                                    && !var17.isEmpty()
                                    && this.field36365.world.getChunkProvider().isChunkLoaded(var17.getPos())
                                    && this.field36365.world.getDimensionKey() == World.OVERWORLD) {
                                if (!var18) {
                                    this.field36366.add(var17.getPos());
                                }

                                boolean var20 = this.method30004(var17);
                                if (!var20 && !var19) {
                                    this.field36367.add(var17.getPos());
                                } else if (var20 && var19) {
                                    this.field36367.remove(var17.getPos());
                                } else if (!var20 && var19) {
                                    continue;
                                }

                                new Thread(() -> {
                                    try {
                                        new File(var23).mkdirs();
                                        File var5x = new File(this.method30002(var23, var17));
                                        Class2531 var6x = Class7927.method26605(var17.getPos());
                                        Class7927 var7x = this.field36372.get(var6x.method10678());
                                        ByteBuffer var8x = this.method30005(var17, this.method30004(var17));
                                        if (var7x != null) {
                                            var7x.method26599(var8x, var17.getPos());
                                        } else if (!var5x.exists()) {
                                            var7x = new Class7927(var6x.field16734, var6x.field16735);
                                            var7x.method26599(var8x, var17.getPos());
                                            this.field36372.put(var6x.method10678(), var7x);
                                            this.field36374.clear();
                                        } else if (this.method29996(var6x)) {
                                            var7x = this.field36372.get(var6x.method10678());
                                            var7x.method26599(var8x, var17.getPos());
                                        }

                                        this.field36373++;
                                    } catch (IOException exc) {
                                        Client.logger.warn(exc);
                                    }
                                }).start();
                                if (++var24 > 6) {
                                    break;
                                }
                            }
                        }
                    }

                    if (this.field36373 > 32) {
                        this.field36373 = 0;

                        try {
                            this.method29997();
                        } catch (IOException exc) {
                            Client.logger.warn(exc);
                        }
                    }
                }
            }
        }
    }

    public boolean method29996(Class2531 var1) throws IOException {
        if (!this.field36374.contains(var1)) {
            String var4 = this.field36371;
            File var5 = new File(this.method30000(var4, var1));
            if (var5.exists()) {
                FileInputStream var6 = new FileInputStream(var5);
                ObjectInputStream var7 = new ObjectInputStream(var6);
                Class7927 var8 = new Class7927(var1.field16734, var1.field16735);
                var8.method26604(var7);
                this.field36372.put(var1.method10678(), var8);
                return true;
            } else {
                this.field36374.add(var1);
                return false;
            }
        } else {
            return false;
        }
    }

    public void method29997() throws IOException {
        if (this.field36371 != null) {
            String var3 = this.field36371;

            try {
                for (Entry var5 : this.field36372.entrySet()) {
                    ObjectOutputStream var6 = new ObjectOutputStream(new FileOutputStream(this.method30001(var3, (Class7927) var5.getValue())));
                    ((Class7927) var5.getValue()).method26603(var6);
                    var6.close();
                }
            } catch (ConcurrentModificationException exc) {
                Client.logger.warn(exc);
            }
        }
    }

    public String method29998() {
        String var3 = "local/local";
        if (this.field36365.getIntegratedServer() == null && this.field36365.getCurrentServerData() != null) {
            var3 = "server/" + this.field36365.getCurrentServerData().serverIP.replace("/", ":");
        } else if (this.field36365.getIntegratedServer() != null) {
            var3 = "local/" + this.field36365.getIntegratedServer().func_240793_aU_().getWorldName();
        }

        return var3;
    }

    public String method29999() {
        return new File("jello") + "/maps/" + this.method29998();
    }

    public String method30000(String var1, Class2531 var2) throws FileNotFoundException {
        return var1 + "/" + var2.field16734 + "c" + var2.field16735 + ".jmap";
    }

    public String method30001(String var1, Class7927 var2) throws FileNotFoundException {
        return var1 + "/" + var2.field33957 + "c" + var2.field33958 + ".jmap";
    }

    public String method30002(String var1, net.minecraft.world.chunk.Chunk var2) throws FileNotFoundException {
        Class2531 var5 = Class7927.method26605(var2.getPos());
        return var1 + "/" + var5.field16734 + "c" + var5.field16735 + ".jmap";
    }

    public Chunk method30003(ChunkPos var1, int var2) {
        List<ChunkPos> var5 = new ArrayList<>();

        for (int var6 = -var2 / 2; var6 < var2 / 2; var6++) {
            for (int var7 = -var2 / 2; var7 < var2 / 2; var7++) {
                var5.add(new ChunkPos(var1.x + var6, var1.z + var7));
            }
        }

        ByteBuffer var21 = BufferUtils.createByteBuffer(var2 * 16 * var2 * 16 * 3);
        int var22 = 0;
        int var8 = var21.position();
        String var9 = this.field36371;

        for (ChunkPos var11 : var5) {
            ByteBuffer var12 = this.field36376.duplicate();
            Long var13 = ChunkPos.asLong(var11.x, var11.z);
            ((Buffer) var12).position(0);
            Class2531 var14 = Class7927.method26605(var11);
            Class7927 var15 = this.field36372.get(var14.method10678());
            if (var15 != null) {
                ByteBuffer var16 = var15.method26600(var11);
                if (var16 != null) {
                    var12 = var16.duplicate();
                }
            } else {
                try {
                    if (this.method29996(var14)) {
                        var15 = this.field36372.get(var14.method10678());
                        ByteBuffer var24 = var15.method26600(var11);
                        if (var24 != null) {
                            var12 = var24.duplicate();
                        }
                    }
                } catch (IOException exc) {
                    Client.logger.warn(exc);
                }
            }

            int var25 = var21.position();
            int var17 = var21.position();

            for (int var18 = 0; var18 < 16; var18++) {
                for (int var19 = 0; var19 < 16; var19++) {
                    var21.put(var12.get());
                    var21.put(var12.get());
                    var21.put(var12.get());
                }

                var25 += 16 * var2 * 3;
                if (var25 < var21.limit()) {
                    ((Buffer) var21).position(var25);
                }
            }

            var8 += 48;
            if (var17 + 48 < var21.limit()) {
                ((Buffer) var21).position(var17 + 48);
            }

            if (var22 != var8 / (48 * var2)) {
                var22 = var8 / (48 * var2);
                if (256 * var2 * 3 * var22 < var21.limit()) {
                    ((Buffer) var21).position(256 * var2 * 3 * var22);
                }
            }

            ((Buffer) var12).position(0);
        }

        ((Buffer) var21).position(16 * var2 * 16 * var2 * 3);
        ((Buffer) var21).flip();
        return new Chunk(var21, 16 * var2, 16 * var2);
    }

    private boolean method30004(net.minecraft.world.chunk.Chunk var1) {
        net.minecraft.world.chunk.Chunk var4 = this.field36365.world.getChunk(var1.getPos().x, var1.getPos().z + 1);
        net.minecraft.world.chunk.Chunk var5 = this.field36365.world.getChunk(var1.getPos().x, var1.getPos().z - 1);
        return var4 != null && !var4.isEmpty() && var5 != null && !var5.isEmpty();
    }

    public ByteBuffer method30005(net.minecraft.world.chunk.Chunk var1, boolean var2) {
        ByteBuffer var5 = BufferUtils.createByteBuffer(768);
        int var6 = var1.getPos().x * 16;
        int var7 = var1.getPos().z * 16;

        for (int var8 = 0; var8 < 16; var8++) {
            for (int var9 = 0; var9 < 16; var9++) {
                BlockPos var10 = new BlockPos(var6 + var8, 64, var7 + var9);
                int var11 = this.getWaypointHeight(
                        new BlockPos(var10.getX(), var1.getHeightmap(Heightmap.Type.WORLD_SURFACE).getHeight(var8, var9) - 1, var10.getZ()), var2
                );
                var5.put((byte) (var11 >> 16 & 0xFF));
                var5.put((byte) (var11 >> 8 & 0xFF));
                var5.put((byte) (var11 & 0xFF));
            }
        }

        ((Buffer) var5).flip();
        return var5;
    }

    public int getWaypointHeight(BlockPos var1, boolean var2) {
        if (this.field36365.world.getBlockState(var1).getBlock() == Blocks.AIR) {
            var1 = var1.down();
        }

        MaterialColor var5 = this.field36365.world.getBlockState(var1).getMaterial().getColor();
        int var6 = var5.colorValue;
        Material var7 = this.field36365.world.getBlockState(var1.up()).getMaterial();
        if (var7 != Material.SNOW) {
            if (var7 == Material.LAVA) {
                var6 = var7.getColor().colorValue;
            }
        } else {
            var6 = -1;
        }

        if (this.field36365.world.getBlockState(var1).hasProperty(BlockStateProperties.WATERLOGGED)) {
            var6 = Material.WATER.getColor().colorValue;
        }

        int var8 = (var6 & 0xFF0000) >> 16;
        int var9 = (var6 & 0xFF00) >> 8;
        int var10 = var6 & 0xFF;
        var6 = new Color(var8, var9, var10).getRGB();
        boolean var11 = Math.abs(var1.getZ() % 16) != 15 && Math.abs(var1.getZ() % 16) != 0;
        if (var1.getZ() < 0) {
            var11 = Math.abs(var1.getZ() % 16) != 16 && Math.abs(var1.getZ() % 16) != 0;
        }

        if (var2 || var11) {
            Material var12 = this.field36365.world.getBlockState(var1.north()).getMaterial();
            Material var13 = this.field36365.world.getBlockState(var1.south()).getMaterial();
            if (var12 == Material.AIR || var12 == Material.SNOW) {
                var6 = MathHelper.blendColor(new Color(var6, true), Color.BLACK, 0.6F).getRGB();
            } else if (var13 == Material.AIR || var13 == Material.SNOW) {
                var6 = MathHelper.blendColor(new Color(var6, true), Color.WHITE, 0.6F).getRGB();
            }
        }

        if (var6 != -16777216) {
            var6 = new Color(var6, true).getRGB();
        } else {
            var6 = -7687425;
        }

        return var6;
    }
}
