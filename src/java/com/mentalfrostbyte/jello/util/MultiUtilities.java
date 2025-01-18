package com.mentalfrostbyte.jello.util;


import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.stream.Stream;

import static com.mentalfrostbyte.jello.module.Module.mc;

public class MultiUtilities {

    public static boolean isAboveBounds(Entity var0, float var1) {
        AxisAlignedBB var4 = new AxisAlignedBB(
                var0.getBoundingBox().minX,
                var0.getBoundingBox().minY - (double)var1,
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
    public static double setPlayerYMotion(double var0) {
        mc.player.setMotion(mc.player.getMotion().x, var0, mc.player.getMotion().z);
        return var0;
    }

}
