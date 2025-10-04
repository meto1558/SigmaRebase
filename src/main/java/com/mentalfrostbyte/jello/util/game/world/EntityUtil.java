package com.mentalfrostbyte.jello.util.game.world;

import com.mentalfrostbyte.jello.gui.base.JelloPortal;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class EntityUtil {
    private static final Minecraft mc = Minecraft.getInstance();

    public static Comparator<LivingEntity> sortEntities(String mode) {
        switch (mode) {
            case "Health":
                return Comparator.comparingDouble(LivingEntity::getHealth);

            case "Armor":
                return Comparator.comparingDouble(LivingEntity::getTotalArmorValue);

            case "Ticks":
                return Comparator.comparingDouble(LivingEntity::getTicksExisted);

            default:
                return Comparator.comparingDouble(e -> e.getDistance(mc.player));
        }
    }

    public static void swing(Entity target, boolean swing) {
        if (target == null) {
            return;
        }

        boolean isOnePointEight = JelloPortal.getVersion().equalTo(ProtocolVersion.v1_8);

        if (isOnePointEight && swing) {
            mc.player.swingArm(Hand.MAIN_HAND);
        }

        // 1. Send attack packet first
        mc.getConnection().getNetworkManager().sendNoEventPacket(new CUseEntityPacket(target, mc.player.isSneaking()));

        // 2. Call attack logic
        mc.playerController.attackEntity(mc.player, target);

        // 3. Reset cooldown AFTER attack
        if (!isOnePointEight) {
            mc.player.resetCooldown();
        }

        // 4. Swing arm for animation (1.9+ only if allowed)
        boolean canSwing = mc.player.getCooledAttackStrength(0.5F) > 0.9 || isOnePointEight;
        if (!isOnePointEight && swing && canSwing) {
            mc.player.swingArm(Hand.MAIN_HAND);
        }
    }

    public static <T extends Entity> List<T> getEntitesInWorld(Predicate<T> filter) {
        return StreamSupport.stream(mc.world.getAllEntities().spliterator(), true)
                .filter((Predicate<Entity>)filter).map(entity -> (T)entity).toList();
    }

    public static boolean isVecWithinBox(Vector3d vec, AxisAlignedBB box) {
        return vec.x >= box.minX
                && vec.x <= box.maxX
                && vec.y >= box.minY
                && vec.y <= box.maxY
                && vec.z >= box.minZ
                && vec.z <= box.maxZ;
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
                if (isVecWithinBox(sourceEntity.getPositionVec(), expandedBox)) {
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


    public static Vector3d getCenteredPosition(AxisAlignedBB var0) {
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

    public static List<PlayerEntity> getPlayerEntities() {
        ArrayList<PlayerEntity> result = new ArrayList<>();
        assert mc.world != null;
        mc.world.entitiesById.forEach((var1, var2x) -> {
            if (var2x instanceof PlayerEntity) {
                result.add((PlayerEntity) var2x);
            }
        });
        return result;
    }
}
