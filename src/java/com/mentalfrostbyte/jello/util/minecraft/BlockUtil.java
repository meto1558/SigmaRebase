package com.mentalfrostbyte.jello.util.minecraft;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;

import static com.mentalfrostbyte.jello.util.minecraft.MinecraftUtil.mc;

public class BlockUtil {

    public static List<BlockPos> method34545(List<BlockPos> var0) {
        var0.sort((var0x, var1) -> {
            float var4 = method34550(mc.player, var0x);
            float var5 = method34550(mc.player, var1);
            if (!(var4 > var5)) {
                return var4 != var5 ? -1 : 0;
            } else {
                return 1;
            }
        });
        return var0;
    }

    public static float method34550(Entity var0, BlockPos var1) {
        return method34553(var0, (double)var1.getX(), (double)var1.getY(), (double)var1.getZ());
    }

    public static float method34553(Entity var0, double var1, double var3, double var5) {
        float var9 = (float)(var0.getPosX() - var1);
        float var10 = (float)(var0.getPosY() - var3);
        float var11 = (float)(var0.getPosZ() - var5);
        return method34558(var9, var10, var11);
    }

    private static float method34558(float var0, float var1, float var2) {
        return MathHelper.sqrt((var0 - 0.5F) * (var0 - 0.5F) + (var1 - 0.5F) * (var1 - 0.5F) + (var2 - 0.5F) * (var2 - 0.5F));
    }

    public static Direction method34580(BlockPos var0) {
        Direction var3 = Direction.UP;
        float var4 = MathHelper.wrapDegrees(method34581(var0, Direction.UP)[0]);
        if (var4 >= 45.0F && var4 <= 135.0F) {
            var3 = Direction.EAST;
        } else if ((!(var4 >= 135.0F) || !(var4 <= 180.0F)) && (!(var4 <= -135.0F) || !(var4 >= -180.0F))) {
            if (var4 <= -45.0F && var4 >= -135.0F) {
                var3 = Direction.WEST;
            } else if (var4 >= -45.0F && var4 <= 0.0F || var4 <= 45.0F && var4 >= 0.0F) {
                var3 = Direction.NORTH;
            }
        } else {
            var3 = Direction.SOUTH;
        }

        if (MathHelper.wrapDegrees(method34581(var0, Direction.UP)[1]) > 75.0F || MathHelper.wrapDegrees(method34581(var0, Direction.UP)[1]) < -75.0F) {
            var3 = Direction.UP;
        }

        return var3;
    }

    public static float[] method34581(BlockPos var0, Direction var1) {
        double var4 = (double)var0.getX() + 0.5 - mc.player.getPosX() + (double)var1.getXOffset() / 2.0;
        double var6 = (double)var0.getZ() + 0.5 - mc.player.getPosZ() + (double)var1.getZOffset() / 2.0;
        double var8 = mc.player.getPosY() + (double) mc.player.getEyeHeight() - ((double)var0.getY() + 0.5);
        double var10 = (double) MathHelper.sqrt(var4 * var4 + var6 * var6);
        float var12 = (float)(Math.atan2(var6, var4) * 180.0 / Math.PI) - 90.0F;
        float var13 = (float)(Math.atan2(var8, var10) * 180.0 / Math.PI);
        if (var12 < 0.0F) {
            var12 += 360.0F;
        }

        return new float[]{var12, var13};
    }

    public static float[] method34542(BlockPos var0, Direction var1) {
        float var4 = 0.0F;
        float var5 = 0.0F;
        float var6 = 0.0F;
        switch (var1.ordinal()) {
            case 1:
                var4 += 0.49F;
                break;
            case 2:
                var5 -= 0.49F;
                break;
            case 3:
                var5 += 0.49F;
                break;
            case 4:
                var4 -= 0.49F;
                break;
            case 5:
                var6 += 0.0F;
            case 6:
                var6++;
        }

        double var7 = (double)var0.getX() + 0.5 - Minecraft.getInstance().player.getPosX() + (double)var4;
        double var9 = (double)var0.getY()
                - 0.02
                - (Minecraft.getInstance().player.getPosY() + (double) Minecraft.getInstance().player.getEyeHeight())
                + (double)var6;
        double var11 = (double)var0.getZ() + 0.5 - Minecraft.getInstance().player.getPosZ() + (double)var5;
        double var13 = (double) MathHelper.sqrt(var7 * var7 + var11 * var11);
        float var15 = (float)(Math.atan2(var11, var7) * 180.0 / Math.PI) - 90.0F;
        float var16 = (float)(-(Math.atan2(var9, var13) * 180.0 / Math.PI));
        return new float[]{
                Minecraft.getInstance().player.rotationYaw + MathHelper.wrapDegrees(var15 - Minecraft.getInstance().player.rotationYaw),
                Minecraft.getInstance().player.rotationPitch + MathHelper.wrapDegrees(var16 - Minecraft.getInstance().player.rotationPitch)
        };
    }

    public static BlockPos method34564(float var0, float var1, float var2) {
        BlockRayTraceResult var5 = rayTrace(var0, var1, var2);
        return var5 != null ? var5.getPos() : null;
    }

    public static BlockRayTraceResult rayTrace(float var0, float var1, float var2) {
        Vector3d var5 = new Vector3d(
                mc.player.lastReportedPosX, mc.player.lastReportedPosY + (double) mc.player.getEyeHeight(), mc.player.lastReportedPosZ
        );
        var0 = (float)Math.toRadians((double)var0);
        var1 = (float)Math.toRadians((double)var1);
        float var6 = -MathHelper.sin(var0) * MathHelper.cos(var1);
        float var7 = -MathHelper.sin(var1);
        float var8 = MathHelper.cos(var0) * MathHelper.cos(var1);
        if (var2 == 0.0F) {
            var2 = mc.playerController.getBlockReachDistance();
        }

        Vector3d var9 = new Vector3d(
                mc.player.lastReportedPosX + (double)(var6 * var2),
                mc.player.lastReportedPosY + (double)(var7 * var2) + (double) mc.player.getEyeHeight(),
                mc.player.lastReportedPosZ + (double)(var8 * var2)
        );
        Entity var10 = mc.getRenderViewEntity();
        return mc.world.rayTraceBlocks(new RayTraceContext(var5, var9, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, var10));
    }

    public static boolean method34578(BlockPos var0) {
        if (var0 != null) {
            Block var3 = mc.world.getBlockState(var0).getBlock();
            return !var3.getDefaultState().isSolid() && var3.getDefaultState().getMaterial().isReplaceable()
                    ? false
                    : !(var3 instanceof SnowBlock) || method34573(mc.world.getBlockState(var0)) != 0;
        } else {
            return false;
        }
    }

    public static int method34573(BlockState var0) {
        Block var3 = var0.getBlock();
        StateContainer var4 = var3.getStateContainer();
        ImmutableList var5 = var4.getValidStates();
        return var5.indexOf(var0);
    }
}
