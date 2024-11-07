package com.mentalfrostbyte.jello.util.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class WorldUtil {

    private static Minecraft mc = Minecraft.getInstance();

    public static List<Entity> getEntitesInWorld() {
        ArrayList<Entity> entities = new ArrayList<>();
        mc.world.entitiesById.forEach((entity1, entity2) -> entities.add(entity2));
        return entities;
    }

    public static boolean method17763(Entity var0) {
        if (!(var0.getPosY() < 1.0)) {
            if (!var0.onGround) {
                AxisAlignedBB var3 = var0.getBoundingBox();
                var3 = var3.expand(0.0, -var0.getPosY(), 0.0);
                return mc.world.getCollisionShapes(mc.player, var3).count() == 0L;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean isHypixel() {
        return mc.getIntegratedServer() == null
                && mc.getCurrentServerData() != null
                && mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net");
    }

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
}
