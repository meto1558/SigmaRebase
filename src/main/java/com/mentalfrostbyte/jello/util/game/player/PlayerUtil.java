package com.mentalfrostbyte.jello.util.game.player;


import com.mentalfrostbyte.jello.util.system.other.SimpleEntryPair;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SlimeEntity;
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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import totalcross.json.JSONObject;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static com.mentalfrostbyte.jello.module.Module.mc;

public class PlayerUtil {

    public static final float[] field24951 = new float[4];
    public static final float[] field24952 = new float[4];

    public static Vector3d method17751(Entity entity) {
        return method17752(entity.getBoundingBox());
    }

    public static boolean method17686() {
        return mc.player.moveStrafing != 0.0F || mc.player.moveForward != 0.0F;
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

    public static long method17762() {
        long var2 = System.currentTimeMillis() / 720000L;
        var2 <<= 1;
        var2 = var2 % 2L != 0L ? var2 >> 2 : var2 << 1;
        return var2 % 3L != 0L ? var2 * 2L : var2 / 2L;
    }

    public static List<String> getMobOwners(String uuid) throws Exception {
        ArrayList<String> names = new ArrayList<>();
        String apiUrl = "https://api.ashcon.app/mojang/v2/user/" + uuid;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 404) {
                    names.add("Unknown owner");
                    return names;
                }

                String responseBody = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(responseBody);
                if (!json.has("username")) {
                    names.add("Unknown owner");
                    return names;
                }

                String username = json.getString("username");
                names.add(username);
                return names;
            }
        }
    }

    public static List<Entity> getEntitesInWorld() {
        ArrayList<Entity> entities = new ArrayList<>();
        mc.world.entitiesById.forEach((entity1, entity2) -> entities.add(entity2));
        return entities;
    }

    public static void transformVector(FloatBuffer matrixBuffer, float[] inputVector, float[] outputVector) {
        for (int i = 0; i < 4; i++) {
            outputVector[i] = inputVector[0] * matrixBuffer.get(matrixBuffer.position() + i)
                    + inputVector[1] * matrixBuffer.get(matrixBuffer.position() + 4 + i)
                    + inputVector[2] * matrixBuffer.get(matrixBuffer.position() + 8 + i)
                    + inputVector[3] * matrixBuffer.get(matrixBuffer.position() + 12 + i);
        }
    }

    public static SimpleEntryPair<Direction, Vector3d> findCollisionDirection(double var0) {
        AxisAlignedBB playerBoundBox = mc.player.getBoundingBox();
        Direction[] directions = new Direction[]{Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH};

        for (Direction direction : directions) {
            Iterator<VoxelShape> collisionShapes = mc.world
                    .getCollisionShapes(mc.player,
                            playerBoundBox.expand(var0 * (double) direction.getXOffset(), 0.0, var0 * (double) direction.getZOffset()))
                    .iterator();
            if (collisionShapes.hasNext()) {
                Vector3d position = mc.player
                        .getPositionVec()
                        .add(mc.player
                                .getAllowedMovement(new Vector3d(direction.getXOffset(), 0.0, direction.getZOffset())));
                return new SimpleEntryPair<>(direction, position);
            }
        }

        return null;
    }

    public static boolean method17761() {
        double var2 = 1.0E-7;
        return mc.world
                .getCollisionShapes(mc.player, mc.player.getBoundingBox().expand(var2, 0.0, var2).expand(-var2, 0.0, -var2))
                .findAny().isPresent();
    }

    public static List<PlayerEntity> method17680() {
        ArrayList<PlayerEntity> var2 = new ArrayList<>();
        mc.world.entitiesById.forEach((var1, var2x) -> {
            if (var2x instanceof PlayerEntity) {
                var2.add((PlayerEntity) var2x);
            }
        });
        return var2;
    }

    public static List<Entity> method17708() {
        ArrayList var2 = new ArrayList();
        mc.world.entitiesById.forEach((var1, var2x) -> var2.add(var2x));
        return var2;
    }

    public static Class2258 method17744(Entity var0) {
        if (var0 instanceof LivingEntity) {
            if (!(var0 instanceof PlayerEntity)) {
                return !(var0 instanceof MobEntity) && !(var0 instanceof MonsterEntity) && !(var0 instanceof SlimeEntity) && !(var0 instanceof FlyingEntity)
                        ? Class2258.field14691
                        : Class2258.field14689;
            } else {
                return Class2258.field14690;
            }
        } else {
            return Class2258.field14692;
        }
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

    public static boolean isCubecraft() {
        return mc.getIntegratedServer() == null && mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.toLowerCase().contains("cubecraft.net");
    }

    public static List<PlayerEntity> getPlayers() {
        ArrayList<PlayerEntity> players = new ArrayList<>();
        mc.world.entitiesById.forEach((entityId, entity) -> {
            if (entity instanceof PlayerEntity) {
                players.add((PlayerEntity) entity);
            }
        });
        return players;
    }

    /**
     * Sends a chat message as the player
     *
     * @param text
     */
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

    public static void block() {
        mc.getConnection().sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
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
        int var9 = 49 + NewMovementUtil.getJumpBoost() * 17;

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
        return new double[]{0.0, 0.0625, 0.125, 0.25, 0.3125, 0.5, 0.625, 0.75, 0.8125, 0.875, 0.9375, 1.0, 1.0625,
                1.125, 1.25, 1.3125, 1.375};
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

    public enum Class2258 {
        field14689,
        field14690,
        field14691,
        field14692;

    }
}