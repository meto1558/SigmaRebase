package com.mentalfrostbyte.jello.util;


import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import com.mentalfrostbyte.jello.util.player.MovementUtil;

import static com.mentalfrostbyte.jello.module.Module.mc;

public class MultiUtilities {

    public static boolean isMoving() {
        return mc.player.moveStrafing != 0.0F || mc.player.moveForward != 0.0F;
    }


    public static Vector3d method17751(Entity entity) {
        return method17752(entity.getBoundingBox());
    }

    public static Vector3d method17752(AxisAlignedBB var0) {
        double var3 = var0.getCenter().x;
        double var5 = var0.minY;
        double var7 = var0.getCenter().z;
        double var9 = (var0.maxY - var5) * 0.95;
        double var11 = (var0.maxX - var0.minX) * 0.95;
        double var13 = (var0.maxZ - var0.minZ) * 0.95;
        double var15 = Math.max(var5, Math.min(var5 + var9, mc.player.getPosY() + (double) mc.player.getEyeHeight()));
        double var17 = Math.max(var3 - var11 / 2.0, Math.min(var3 + var11 / 2.0, mc.player.getPosX()));
        double var19 = Math.max(var7 - var13 / 2.0, Math.min(var7 + var13 / 2.0, mc.player.getPosZ()));
        return new Vector3d(var17, var15, var19);
    }

    public static double method17754(Vector3d vec) {
        double var3 = mc.player.getPosX() - vec.x;
        double var5 = mc.player.getPosY() + (double) mc.player.getEyeHeight() - vec.y;
        double var7 = mc.player.getPosZ() - vec.z;
        return Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
    }

    public static double method17755(AxisAlignedBB var0) {
        Vector3d var3 = method17752(var0);
        return method17754(var3);
    }

    public static float method17756(float var0, float var1) {
        var0 %= 360.0F;
        var1 %= 360.0F;
        if (var0 < 0.0F) {
            var0 += 360.0F;
        }

        if (var1 < 0.0F) {
            var1 += 360.0F;
        }

        float var4 = var1 - var0;
        return !(var4 > 180.0F) ? (!(var4 < -180.0F) ? var4 : var4 + 360.0F) : var4 - 360.0F;
    }

    public static boolean isAboveBounds(Entity var0, float var1) {
        AxisAlignedBB var4 = new AxisAlignedBB(
                var0.getBoundingBox().minX,
                var0.getBoundingBox().minY - (double) var1,
                var0.getBoundingBox().minZ,
                var0.getBoundingBox().maxX,
                var0.getBoundingBox().maxY,
                var0.getBoundingBox().maxZ
        );
        Stream<VoxelShape> var5 = mc.world.getCollisionShapes(mc.player, var4);
        return var5.findAny().isPresent();
    }

    public static boolean isCubecraft() {
        return mc.getIntegratedServer() == null && mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.toLowerCase().contains("cubecraft.net");
    }

    public static List<PlayerEntity> getPlayers() {
        ArrayList<PlayerEntity> players = new ArrayList<>();
        mc.world.entitiesById.forEach((entityId, entity) -> {
            if (entity instanceof PlayerEntity) {
                players.add((PlayerEntity)entity);
            }
        });
        return players;
    }

    public static void sendChatMessage(String text) {
        mc.getConnection().sendPacket(new CChatMessagePacket(text));
    }


    public static boolean isHypixel() {
        return mc.getIntegratedServer() == null
                && mc.getCurrentServerData() != null
                && mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net");
    }

    public static double setPlayerYMotion(double var0) {
        mc.player.setMotion(mc.player.getMotion().x, var0, mc.player.getMotion().z);
        return var0;
    }
    public static boolean method17729() {
        AxisAlignedBB var2 = mc.player.getBoundingBox().offset(0.0, -1.0, 0.0);
        if (mc.player.getRidingEntity() != null) {
            double var4 = mc.player.getRidingEntity().prevPosX - mc.player.getRidingEntity().getPosX();
            double var6 = mc.player.getRidingEntity().prevPosZ - mc.player.getRidingEntity().getPosZ();
            var2 = mc.player.getRidingEntity().getBoundingBox().expand(Math.abs(var4), 1.0, Math.abs(var6));
        }

        Stream<VoxelShape> var3 = mc.world.getCollisionShapes(mc.player, var2);
        return var3.findAny().isPresent();
    }
    public static int applyAlpha(int color, float alpha) {
        return (int)(alpha * 255.0F) << 24 | color & 16777215;
    }


    public static void block() {
        mc.getConnection().sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        mc.getConnection().sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
    }
    public static void unblock() {
        mc.getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), Direction.DOWN));
    }

    public static double method17750() {
        return Math.random() * 1.0E-8;
    }

    public static void method17749(boolean var0) {
        double var3 = mc.player.getPosX();
        double var5 = mc.player.getPosY();
        double var7 = mc.player.getPosZ();
        int var9 = 49 + MovementUtil.getJumpBoost() * 17;

        for (int var10 = 0; var10 < var9; var10++) {
            double var11 = !var0 ? 0.0 : method17750();
            mc.getConnection().sendPacket(new CPlayerPacket.PositionPacket(var3 + var11, var5 + 0.06248 + method17750(), var7 + var11, false));
            if (isHypixel()) {
                mc.getConnection().sendPacket(new CPlayerPacket.PositionPacket(var3 + var11, var5 + 0.05 + method17750(), var7 + var11, false));
            }
        }
    }

    public static boolean inLiquid(Entity entity) {
        ClientWorld world = mc.world;
        AxisAlignedBB boundingBox = entity.getBoundingBox();
        return world.containsAnyLiquid(boundingBox);
    }

    // magic numbers used in hypixel fly
    public static double[] method17747() {
        return new double[] { 0.0, 0.0625, 0.125, 0.25, 0.3125, 0.5, 0.625, 0.75, 0.8125, 0.875, 0.9375, 1.0, 1.0625,
                1.125, 1.25, 1.3125, 1.375 };
    }

    public static boolean method17763(Entity entity) {
        if (!(entity.getPosY() < 1.0)) {
            if (!entity.isOnGround()) {
                AxisAlignedBB var3 = entity.getBoundingBox();
                var3 = var3.expand(0.0, -entity.getPosY(), 0.0);
                return mc.world.getCollisionShapes(mc.player, var3).count() == 0L;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}






