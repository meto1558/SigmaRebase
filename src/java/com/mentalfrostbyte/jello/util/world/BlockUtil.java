package com.mentalfrostbyte.jello.util.world;

import com.mentalfrostbyte.jello.event.impl.EventUpdate;
import com.mentalfrostbyte.jello.util.player.MovementUtil;
import com.mentalfrostbyte.jello.util.unmapped.Class7306;
import com.mentalfrostbyte.jello.util.unmapped.Class7843;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import org.apache.commons.lang3.RandomUtils;

public class BlockUtil {
   public static Minecraft mc = Minecraft.getInstance();

   public static boolean method34578(BlockPos blockPos) {
      return false;
   }

   public static Class7843 method34575(BlockPos var0, boolean var1) {
      Vector3i[] var4 = new Vector3i[]{
              new Vector3i(0, 0, 0), new Vector3i(-1, 0, 0), new Vector3i(1, 0, 0), new Vector3i(0, 0, 1), new Vector3i(0, 0, -1)
      };
      Class7306[] var5 = new Class7306[]{
              new Class7306(1, 1, 1, false),
              new Class7306(2, 1, 2, false),
              new Class7306(3, 1, 3, false),
              new Class7306(4, 1, 4, false),
              new Class7306(0, -1, 0, true)
      };

      for (Class7306 var9 : var5) {
         for (Vector3i var13 : var4) {
            Vector3i var14 = !var9.field31325
                    ? new Vector3i(var13.getX() * var9.field31322, var13.getY() * var9.field31323, var13.getZ() * var9.field31324)
                    : new Vector3i(var13.getX() + var9.field31322, var13.getY() + var9.field31323, var13.getZ() + var9.field31324);

            for (Direction var18 : Direction.values()) {
               if ((var18 != Direction.DOWN || !var1) && method34578(var0.add(var14).offset(var18, -1))) {
                  return new Class7843(var0.add(var14).offset(var18, -1), var18);
               }
            }
         }
      }

      return null;
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

   public static BlockRayTraceResult method34568(float yaw, float pitch, float v, EventUpdate event) {
      return null;
   }

   public static float[] method34543(BlockPos blockPos, Direction direction) {
      return null;
   }

   public static RayTraceResult method34569(float var0, float var1, float var2, float var3) {
      double var6 = Math.cos((double) MovementUtil.method37086() * Math.PI / 180.0) * (double)var3;
      double var8 = Math.sin((double) MovementUtil.method37086() * Math.PI / 180.0) * (double)var3;
      Vector3d var10 = new Vector3d(
              mc.player.getPosX() + var6,
              mc.player.getPosY() + (double) mc.player.getEyeHeight(),
              mc.player.getPosZ() + var8
      );
      var0 = (float)Math.toRadians((double)var0);
      var1 = (float)Math.toRadians((double)var1);
      float var11 = -MathHelper.sin(var0) * MathHelper.cos(var1);
      float var12 = -MathHelper.sin(var1);
      float var13 = MathHelper.cos(var0) * MathHelper.cos(var1);
      if (var2 == 0.0F) {
         var2 = mc.playerController.getBlockReachDistance();
      }

      Vector3d var14 = new Vector3d(
              mc.player.lastReportedPosX + (double)(var11 * var2),
              mc.player.lastReportedPosY + (double)(var12 * var2) + (double) mc.player.getEyeHeight(),
              mc.player.lastReportedPosZ + (double)(var13 * var2)
      );
      Entity var15 = mc.getRenderViewEntity();
      return mc.world.rayTraceBlocks(new RayTraceContext(var10, var14, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, var15));
   }

   public static RayTraceResult method34570(BlockPos var0) {
      Vector3d var3 = new Vector3d(
              mc.player.getPosX(), mc.player.getPosY() + (double) mc.player.getEyeHeight(), mc.player.getPosZ()
      );
      Vector3d var4 = new Vector3d(
              (double)var0.getX() + 0.5 + RandomUtils.nextDouble(0.01, 0.04),
              (double)var0.getY(),
              (double)var0.getZ() + 0.5 + RandomUtils.nextDouble(0.01, 0.04)
      );
      return mc.world.rayTraceBlocks(new RayTraceContext(var3, var4, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, mc.getRenderViewEntity()));
   }

   public static float[] method34542(BlockPos var0, Direction var1) {
      float var4 = 0.0F;
      float var5 = 0.0F;
      float var6 = 0.0F;
      switch (var1) {
         case Direction.EAST:
            var4 += 0.49F;
            break;
         case Direction.NORTH:
            var5 -= 0.49F;
            break;
         case Direction.SOUTH:
            var5 += 0.49F;
            break;
         case Direction.WEST:
            var4 -= 0.49F;
            break;
         case Direction.UP:
            var6 += 0.0F;
         case Direction.DOWN:
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


   public static boolean method34538(Block var0, BlockPos var1) {
      VoxelShape var4 = var0.getDefaultState().getCollisionShape(mc.world, var1);
      return !method34578(var1)
              && mc.world.checkNoEntityCollision(mc.player, var4)
              && var1.getY() <= mc.player.getPosition().getY();
   }

   public static final Block getBlockFromPosition(BlockPos blockPos) {
      return mc.world.getBlockState(blockPos).getBlock();
   }

   public static float getBlockReachDistance() {
      return mc.playerController.getBlockReachDistance();
   }

   public static BlockRayTraceResult method34566(float var0) {
      Vector3d var3 = new Vector3d(mc.player.lastReportedPosX, mc.player.lastReportedPosY - 0.8F, mc.player.lastReportedPosZ);
      var0 = (float)Math.toRadians((double)var0);
      float var4 = 0.0F;
      float var5 = -MathHelper.sin(var0) * MathHelper.cos(var4);
      float var6 = MathHelper.cos(var0) * MathHelper.cos(var4);
      float var7 = 2.3F;
      Vector3d var8 = new Vector3d(
              mc.player.lastReportedPosX + (double)(var5 * var7),
              mc.player.lastReportedPosY - 0.8F - (double)(!mc.player.isJumping ? 0.0F : 0.6F),
              mc.player.lastReportedPosZ + (double)(var6 * var7)
      );
      Entity var9 = mc.getRenderViewEntity();
      return mc.world.rayTraceBlocks(new RayTraceContext(var3, var8, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, var9));
   }

   public static float[] method34565() {
      BlockRayTraceResult var2 = method34566(MovementUtil.method37086() - 270.0F);
      if (var2.getType() != RayTraceResult.Type.MISS) {
         double var3 = var2.getHitVec().x - (double)var2.getPos().getX();
         double var5 = var2.getHitVec().z - (double)var2.getPos().getZ();
         double var7 = var2.getHitVec().y - (double)var2.getPos().getY();
         double var9 = (double)var2.getPos().getX() - Minecraft.getInstance().player.getPosX() + var3;
         double var11 = (double)var2.getPos().getY()
                 - (Minecraft.getInstance().player.getPosY() + (double) Minecraft.getInstance().player.getEyeHeight())
                 + var7;
         double var13 = (double)var2.getPos().getZ() - Minecraft.getInstance().player.getPosZ() + var5;
         double var15 = (double) MathHelper.sqrt(var9 * var9 + var13 * var13);
         float var17 = (float)(Math.atan2(var13, var9) * 180.0 / Math.PI) - 90.0F;
         float var18 = (float)(-(Math.atan2(var11, var15) * 180.0 / Math.PI));
         return new float[]{
                 Minecraft.getInstance().player.rotationYaw + MathHelper.wrapDegrees(var17 - Minecraft.getInstance().player.rotationYaw),
                 Minecraft.getInstance().player.rotationPitch + MathHelper.wrapDegrees(var18 - Minecraft.getInstance().player.rotationPitch)
         };
      } else {
         return null;
      }
   }

}
