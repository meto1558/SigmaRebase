package com.mentalfrostbyte.jello.util.combat;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class RotationUtil {

    public static final Minecraft mc = Minecraft.getInstance();

    public static Rotations method34147(Entity var0) {
        float[] var3 = calculateEntityRotations(var0, mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());
        return new Rotations(var3[0], var3[1]);
    }

    public static float[] calculateEntityRotations(Entity entity, double playerX, double playerZ, double playerY) {
        // Calculate the interpolated position of the entity based on render ticks
        double interpolatedPosX = entity.getPosX() + (entity.getPosX() - entity.lastTickPosX) * mc.getRenderPartialTicks();
        double interpolatedPosZ = entity.getPosZ() + (entity.getPosZ() - entity.lastTickPosZ) * mc.getRenderPartialTicks();
        double interpolatedPosY = entity.getPosY() + (entity.getPosY() - entity.lastTickPosY) * mc.getRenderPartialTicks();

        // Calculate the differences in position
        double deltaX = interpolatedPosX - playerX;
        double deltaY = interpolatedPosY - mc.player.getEyeHeight() - 0.02F + entity.getEyeHeight() - playerY;
        double deltaZ = interpolatedPosZ - playerZ;

        // Calculate horizontal distance and yaw/pitch angles
        double horizontalDistance = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yaw = calculate(mc.player.rotationYaw, (float)(Math.atan2(deltaZ, deltaX) * 180.0 / Math.PI) - 90.0F, 360.0F);
        float pitch = calculate(mc.player.rotationPitch, (float)(-(Math.atan2(deltaY, horizontalDistance) * 180.0 / Math.PI)), 360.0F);

        // Return the calculated yaw and pitch
        return new float[]{yaw, pitch};
    }


    public static float angleDiff(float var0, float var1) {
        float var4 = Math.abs(var0 - var1) % 360.0F;
        if (var4 > 180.0F) {
            var4 = 360.0F - var4;
        }

        return var4;
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

    public static Vector3d getLookVector(float var0, float var1) {
        float var4 = var0 * (float) (Math.PI / 180.0);
        float var5 = -var1 * (float) (Math.PI / 180.0);
        float var6 = MathHelper.cos(var5);
        float var7 = MathHelper.sin(var5);
        float var8 = MathHelper.cos(var4);
        float var9 = MathHelper.sin(var4);
        return new Vector3d((double)(var7 * var8), (double)(-var9), (double)(var6 * var8));
    }

    public static Entity getEntityFromRayTrace(float yaw, float pitch, float reachDistanceModifier, double boundingBoxExpansion) {
        EntityRayTraceResult rayTraceResult = rayTraceFromPlayer(yaw, pitch, reachDistanceModifier, boundingBoxExpansion);
        return rayTraceResult == null ? null : rayTraceResult.getEntity();
    }

    public static EntityRayTraceResult rayTraceFromPlayer(float yaw, float pitch, float reachDistanceModifier, double boundingBoxExpansion) {
        Vector3d playerEyesPos = new Vector3d(
                mc.player.getPosX(), mc.player.getPosY() + (double) mc.player.getEyeHeight(), mc.player.getPosZ()
        );
        Entity renderViewEntity = mc.getRenderViewEntity();

        if (renderViewEntity != null && mc.world != null) {
            double reachDistance = (double) mc.playerController.getBlockReachDistance();
            if (reachDistanceModifier != 0.0F) {
                reachDistance = (double) reachDistanceModifier;
            }

            Vector3d lookVector = getLookVector(pitch, yaw);
            Vector3d rayEndPos = playerEyesPos.add(lookVector.x * reachDistance, lookVector.y * reachDistance, lookVector.z * reachDistance);
            AxisAlignedBB searchBox = renderViewEntity.getBoundingBox().expand(lookVector.scale(reachDistance)).grow(1.0, 1.0, 1.0);

            return traceEntityRay(
                    mc.world, renderViewEntity, playerEyesPos, rayEndPos, searchBox,
                    entity -> entity instanceof LivingEntity || entity instanceof FallingBlockEntity,
                    (double) (reachDistanceModifier * reachDistanceModifier), boundingBoxExpansion
            );
        } else {
            return null;
        }
    }

    public static boolean method17715(Vector3d var0, AxisAlignedBB var1) {
        return var0.x >= var1.minX
                && var0.x <= var1.maxX
                && var0.y >= var1.minY
                && var0.y <= var1.maxY
                && var0.z >= var1.minZ
                && var0.z <= var1.maxZ;
    }

    public static EntityRayTraceResult traceEntityRay(
            World world, Entity sourceEntity, Vector3d startPos, Vector3d endPos, AxisAlignedBB searchBox,
            Predicate<Entity> entityFilter, double maxDistance, double boundingBoxExpansion
    ) {
        double closestDistance = maxDistance;
        Entity closestEntity = null;

        for (Entity entity : world.getEntitiesInAABBexcluding(sourceEntity, searchBox, entityFilter)) {
            AxisAlignedBB expandedBox = entity.getBoundingBox().grow(boundingBoxExpansion);
            Optional<Vector3d> hitResult = expandedBox.rayTrace(startPos, endPos);

            if (!hitResult.isPresent()) {
                if (method17715(sourceEntity.getPositionVec(), expandedBox)) {
                    closestEntity = entity;
                    break;
                }
            } else {
                double distanceToHit = startPos.squareDistanceTo(hitResult.get());
                if (distanceToHit < closestDistance) {
                    closestEntity = entity;
                    closestDistance = distanceToHit;
                }
            }
        }

        return closestEntity != null ? new EntityRayTraceResult(closestEntity) : null;
    }

    public static EntityRayTraceResult raytrace(Entity var0, float var1, float var2, Predicate<Entity> var3, double var4) {
        double var8 = var4 * var4;
        Entity var10 = null;
        Vector3d var11 = null;
        Vector3d var12 = new Vector3d(
                mc.player.getPosX(), mc.player.getPosY() + (double) mc.player.getEyeHeight(), mc.player.getPosZ()
        );
        Vector3d var13 = getLookVector(var2, var1);
        Vector3d var14 = var12.add(var13.x * var8, var13.y * var8, var13.z * var8);

        for (Entity var16 : mc.world
                .getEntitiesInAABBexcluding(mc.player, mc.player.getBoundingBox().expand(var13.scale(var8)).grow(1.0, 1.0, 1.0), var3)) {
            AxisAlignedBB var17 = var16.getBoundingBox();
            Optional<Vector3d> var18 = var17.rayTrace(var12, var14);
            if (var18.isPresent()) {
                double var19 = var12.squareDistanceTo(var18.get());
                if (var19 < var8 && (var16 == var0 || var0 == null)) {
                    var11 = var18.get().subtract(var16.getPosX(), var16.getPosY(), var16.getPosZ());
                    var10 = var16;
                    var8 = var19;
                }
            }
        }

        return var10 != null && var11 != null ? new EntityRayTraceResult(var10, var11) : null;
    }

    public static float method34152(float var0, float var1) {
        return MathHelper.wrapDegrees(-(var0 - var1));
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

    public static Vector3d method17751(Entity var0) {
        return method17752(var0.getBoundingBox());
    }

    public static Rotations getRotations(Entity targetIn, boolean throughWalls) {
        Vector3d var4 = method17751(targetIn);
        if (throughWalls && !raytraceVector(var4)) {
            for (int var5 = -1; var5 < 2; var5++) {
                double var6 = (double)var5;
                if (var5 != -1) {
                    var6 *= targetIn.getBoundingBox().getYSize();
                } else {
                    var6 = (double)(targetIn.getEyeHeight() - 0.02F);
                }

                double xPos = targetIn.getPosX();
                double zPos = targetIn.getPosZ();
                double yPos = targetIn.getPosY() + var6 + 0.05;
                double playerxPos = xPos - mc.player.getPosX();
                double playeryPos = yPos - (double) mc.player.getEyeHeight() - 0.02F - mc.player.getPosY();
                double playerzPos = zPos - mc.player.getPosZ();
                double var20 = (double) MathHelper.sqrt(playerxPos * playerxPos + playerzPos * playerzPos);
                float yaw = calculate(mc.player.rotationYaw, (float)(Math.atan2(playerzPos, playerxPos) * 180.0 / Math.PI) - 90.0F, 360.0F);
                float pitch = calculate(mc.player.rotationPitch, (float)(-(Math.atan2(playeryPos, var20) * 180.0 / Math.PI)), 360.0F);
                boolean position = raytraceVector(new Vector3d(xPos, yPos, zPos));
                if (position) {
                    return new Rotations(yaw, pitch);
                }

                for (int var25 = -1; var25 < 2; var25 += 2) {
                    xPos = targetIn.getPosX() + (targetIn.getPosX() - targetIn.lastTickPosX) * (double) mc.getRenderPartialTicks();
                    zPos = targetIn.getPosZ() + (targetIn.getPosZ() - targetIn.lastTickPosZ) * (double) mc.getRenderPartialTicks();
                    yPos = targetIn.getPosY() + 0.05 + (targetIn.getPosY() - targetIn.lastTickPosY) * (double) mc.getRenderPartialTicks() + var6;
                    double var26 = targetIn.getBoundingBox().getXSize() / 2.5 * (double)var25;
                    double var28 = targetIn.getBoundingBox().getZSize() / 2.5 * (double)var25;
                    if (!(mc.player.getPosX() < xPos + var26)) {
                        if (mc.player.getPosX() > xPos + var26) {
                            if (!(mc.player.getPosZ() < zPos - var28)) {
                                xPos += var26;
                            } else {
                                xPos -= var26;
                            }

                            if (!(mc.player.getPosX() > xPos + var26)) {
                                zPos += var28;
                            } else {
                                zPos -= var28;
                            }
                        }
                    } else {
                        if (!(mc.player.getPosZ() > zPos + var28)) {
                            xPos -= var26;
                        } else {
                            xPos += var26;
                        }

                        if (!(mc.player.getPosX() < xPos - var26)) {
                            zPos -= var28;
                        } else {
                            zPos += var28;
                        }
                    }

                    playerxPos = xPos - mc.player.getPosX();
                    playeryPos = yPos - (double) mc.player.getEyeHeight() - 0.02 - mc.player.getPosY();
                    playerzPos = zPos - mc.player.getPosZ();
                    var20 = (double) MathHelper.sqrt(playerxPos * playerxPos + playerzPos * playerzPos);
                    yaw = calculate(mc.player.rotationYaw, (float)(Math.atan2(playerzPos, playerxPos) * 180.0 / Math.PI) - 90.0F, 360.0F);
                    pitch = calculate(mc.player.rotationPitch, (float)(-(Math.atan2(playeryPos, var20) * 180.0 / Math.PI)), 360.0F);
                    position = raytraceVector(new Vector3d(xPos, yPos, zPos));
                    if (position) {
                        return new Rotations(yaw, pitch);
                    }
                }
            }

            return null;
        } else {
            return getRotationsToVector(var4);
        }
    }

    public static float[] method34145(Vector3d var0, Vector3d var1) {
        double var4 = var1.x - var0.x;
        double var6 = var1.z - var0.z;
        double var8 = var1.y - var0.y;
        double var10 = (double) MathHelper.sqrt(var4 * var4 + var6 * var6);
        float var12 = calculate(0.0F, (float)(Math.atan2(var6, var4) * 180.0 / Math.PI) - 90.0F, 360.0F);
        float var13 = calculate(mc.player.rotationPitch, (float)(-(Math.atan2(var8, var10) * 180.0 / Math.PI)), 360.0F);
        return new float[]{var12, var13};
    }

    public static Rotations getRotationsToVector(Vector3d var0) {
        float[] var3 = method34145(mc.player.getPositionVec().add(0.0, (double) mc.player.getEyeHeight(), 0.0), var0);
        return new Rotations(var3[0], var3[1]);
    }

    public static boolean raytraceVector(Vector3d vec) {
        Vector3d var3 = new Vector3d(
                mc.player.getPosX(), mc.player.getPosY() + (double) mc.player.getEyeHeight(), mc.player.getPosZ()
        );
        RayTraceContext var4 = new RayTraceContext(var3, vec, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, mc.player);
        BlockRayTraceResult var5 = mc.world.rayTraceBlocks(var4);
        boolean var6 = var5.getType() == RayTraceResult.Type.MISS || var5.getType() == RayTraceResult.Type.ENTITY;
        Block var7 = mc.world.getBlockState(var5.getPos()).getBlock();
        return var6;
    }

    public static float calculate(float var0, float var1, float var2) {
        float var5 = MathHelper.wrapDegrees(var1 - var0);
        if (var5 > var2) {
            var5 = var2;
        }

        if (var5 < -var2) {
            var5 = -var2;
        }

        return var0 + var5;
    }

    public static double method17754(Vector3d var0) {
        double var3 = mc.player.getPosX() - var0.x;
        double var5 = mc.player.getPosY() + (double) mc.player.getEyeHeight() - var0.y;
        double var7 = mc.player.getPosZ() - var0.z;
        return Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
    }

    public static double method17755(AxisAlignedBB var0) {
        Vector3d var3 = method17752(var0);
        return method17754(var3);
    }

    public static boolean rayTraceEntity(PlayerEntity player, Entity entity) {
        Minecraft mc = Minecraft.getInstance();

        Vector3d playerEyesPos = player.getEyePosition(1.0F);
        Vector3d lookDirection = player.getLook(1.0F);

        double reachDistance = mc.playerController.getBlockReachDistance();
        Vector3d endPos = playerEyesPos.add(lookDirection.x * reachDistance, lookDirection.y * reachDistance, lookDirection.z * reachDistance);

        AxisAlignedBB entityBoundingBox = entity.getBoundingBox().grow(0.3D);

        RayTraceContext context = new RayTraceContext(
                playerEyesPos,
                endPos,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                player
        );
        RayTraceResult rayTraceResult = mc.world.rayTraceBlocks(context);

        if (rayTraceResult.getType() == RayTraceResult.Type.MISS) {
            Optional<Vector3d> hitResult = entityBoundingBox.rayTrace(playerEyesPos, endPos);

            return hitResult.isPresent();
        }

        return false;
    }

    public static float[] method34144(double var0, double var2, double var4) {
        double var8 = var0 - mc.player.getPosX();
        double var10 = var2 - mc.player.getPosZ();
        double var12 = var4 - mc.player.getPosY() - 1.2;
        double var14 = (double) MathHelper.sqrt(var8 * var8 + var10 * var10);
        float var16 = (float)(Math.atan2(var10, var8) * 180.0 / Math.PI) - 90.0F;
        float var17 = (float)(-(Math.atan2(var12, var14) * 180.0 / Math.PI));
        return new float[]{var16, var17};
    }
}
