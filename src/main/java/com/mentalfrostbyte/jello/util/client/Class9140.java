package com.mentalfrostbyte.jello.util.client;

import com.mentalfrostbyte.jello.util.game.MinecraftUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class Class9140 implements MinecraftUtil {
    private static final int field41997 = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
    private static final int field41998 = field41997;
    private static final int field41999 = 64 - field41997 - field41998;
    private static final int field42000 = field41998;
    private static final int field42001 = field42000 + field41999;
    private static final long field42002 = (1L << field41997) - 1L;
    private static final long field42003 = (1L << field41999) - 1L;
    private static final long field42004 = (1L << field41998) - 1L;
    public static double x;
    public static double y;
    public static double z;
    public static float field42008;

    public static long method34122(BlockPos var0) {
        return ((long) var0.getX() & field42002) << field42001
                | ((long) var0.getY() & field42003) << field42000
                | ((long) var0.getZ() & field42004);
    }

    public static void method34123(float var0, float var1, float var2, float var3) {
        float var6 = var0 * var0 + var1 * var1 + var2 * var2;
        if (var6 >= 1.0E-4F) {
            var6 = MathHelper.sqrt(var6);
            if (var6 < 1.0F) {
                var6 = 1.0F;
            }

            var6 = var3 / var6;
            var0 *= var6;
            var1 *= var6;
            var2 *= var6;
            float var7 = MathHelper.sin(mc.player.rotationYaw * (float) (Math.PI / 180.0));
            float var8 = MathHelper.cos(mc.player.rotationYaw * (float) (Math.PI / 180.0));
            x += (double) (var0 * var8 - var2 * var7);
            y += (double) var1;
            z += (double) (var2 * var8 + var0 * var7);
        }
    }

    public static void method34124(float var0, float var1, float var2) {
        if ((mc.player.isServerWorld() || mc.player.canPassengerSteer()) && mc.player.isInWater()) {
            double var5 = mc.player.getPosY();
            float var7 = method34128();
            float var8 = 0.02F;
            float var9 = (float) EnchantmentHelper.getDepthStriderModifier(mc.player);
            if (var9 > 3.0F) {
                var9 = 3.0F;
            }

            if (!mc.player.onGround) {
                var9 *= 0.5F;
            }

            if (var9 > 0.0F) {
                var7 += (0.54600006F - var7) * var9 / 3.0F;
                var8 += (mc.player.getAIMoveSpeed() - var8) * var9 / 3.0F;
            }

            method34123(var0, var1, var2, var8);
            method34126(x, y, z);
            x *= (double) var7;
            y *= 0.8F;
            z *= (double) var7;
            if (!mc.player.hasNoGravity()) {
                y -= 0.02;
            }

            if (mc.player.collidedHorizontally
                    && mc.player.isOffsetPositionInLiquid(x, y + 0.6F - mc.player.getPosY() + var5, z)) {
                y = 0.3F;
            }
        }
    }

    public static boolean method34125(AxisAlignedBB bb, Material material) {
        int var4 = MathHelper.floor(bb.minX);
        int var5 = MathHelper.ceil(bb.maxX);
        int var6 = MathHelper.floor(bb.minY);
        int var7 = MathHelper.ceil(bb.maxY);
        int var8 = MathHelper.floor(bb.minZ);
        int var9 = MathHelper.ceil(bb.maxZ);
        if (!mc.world.isAreaLoaded(var4, var6, var8, var5, var7, var9)) {
            return false;
        } else {
            for (int var10 = var4; var10 < var5; var10++) {
                for (int var11 = var6; var11 < var7; var11++) {
                    for (int var12 = var8; var12 < var9; var12++) {
                        BlockState var13 = mc.world.getBlockState(new BlockPos(var10, var11, var12));
                        if (var13.getMaterial() == material) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }
    }

    public static void method34126(double var0, double var2, double var4) {
    }

    public static void method34127() {
        y += 0.04F;
    }

    public static float method34128() {
        return 0.8F;
    }

    public static boolean method34129() {
        return method34125(mc.player.getBoundingBox().expand(0.0, -0.4F, 0.0).contract(0.001, 0.001, 0.001), Material.WATER);
    }
}
