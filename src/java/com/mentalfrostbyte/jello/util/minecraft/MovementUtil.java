package com.mentalfrostbyte.jello.util.minecraft;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.EventMove;
import com.mentalfrostbyte.jello.util.combat.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class MovementUtil {
    protected static Minecraft mc = Minecraft.getInstance();

    public static boolean isMoving() {
        boolean forward = mc.gameSettings.keyBindForward.isKeyDown();
        boolean left = mc.gameSettings.keyBindLeft.isKeyDown();
        boolean right = mc.gameSettings.keyBindRight.isKeyDown();
        boolean back = mc.gameSettings.keyBindBack.isKeyDown();
        return forward || left || right || back;
    }

    public static double method37080() {
        return 0.42F + (double)method37079() * 0.1;
    }

    public static int method37079() {
        return ! mc.player.isPotionActive(Effects.JUMP_BOOST) ? 0 : mc.player.getActivePotionEffect(Effects.JUMP_BOOST).getAmplifier() + 1;
    }

    public static float[] lenientStrafe() {
        MovementInput movementInput = mc.player.movementInput;
        float forward = movementInput.moveForward;
        float strafe = movementInput.moveStrafe;
        return method37084(forward, strafe);
    }

    public static float[] method37084(float var0, float var1) {
        float var4 = mc.player.rotationYaw + 90.0F;
        if (Client.getInstance().field28989.method31744() != -999.0F) {
            var4 = Client.getInstance().field28989.method31744() + 90.0F;
        }

        if (var0 != 0.0F) {
            if (!(var1 >= 1.0F)) {
                if (var1 <= -1.0F) {
                    var4 += (float)(!(var0 > 0.0F) ? -45 : 45);
                    var1 = 0.0F;
                }
            } else {
                var4 += (float)(!(var0 > 0.0F) ? 45 : -45);
                var1 = 0.0F;
            }

            if (!(var0 > 0.0F)) {
                if (var0 < 0.0F) {
                    var0 = -1.0F;
                }
            } else {
                var0 = 1.0F;
            }
        }

        if (Client.getInstance().field28989.method31742()
                && !Client.getInstance().field28989.method31741()
                && (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F)) {
            var0 = 1.0F;
        }

        return new float[]{var4, var0, var1};
    }

    public static double setPlayerXMotion(double var0) {
        mc.player.setMotion(var0, mc.player.getMotion().y, mc.player.getMotion().z);
        return var0;
    }

    public static double setPlayerYMotion(double var0) {
        mc.player.setMotion(mc.player.getMotion().x, var0, mc.player.getMotion().z);
        return var0;
    }

    public static double setPlayerZMotion(double var0) {
        mc.player.setMotion(mc.player.getMotion().x, mc.player.getMotion().y, var0);
        return var0;
    }

    public static void setSpeed(EventMove moveEvent, double motionSpeed) {
        float[] var5 = lenientStrafe();
        float var6 = var5[1];
        float var7 = var5[2];
        float var8 = var5[0];
        if (var6 == 0.0F && var7 == 0.0F) {
            moveEvent.setX(0.0);
            moveEvent.setZ(0.0);
        }

        double var9 = Math.cos(Math.toRadians((double)var8));
        double var11 = Math.sin(Math.toRadians((double)var8));
        double var13 = ((double)var6 * var9 + (double)var7 * var11) * motionSpeed;
        double var15 = ((double)var6 * var11 - (double)var7 * var9) * motionSpeed;
        moveEvent.setX(var13);
        moveEvent.setZ(var15);
        setPlayerXMotion(moveEvent.getX());
        setPlayerZMotion(moveEvent.getZ());
    }

    public static int method37078() {
        return ! mc.player.isPotionActive(Effects.SPEED) ? 0 : mc.player.getActivePotionEffect(Effects.SPEED).getAmplifier() + 1;
    }

    public static boolean method17686() {
        return mc.player.moveStrafing != 0.0F || mc.player.moveForward != 0.0F;
    }

    public static void strafe(double speed) {
        float[] var4 = lenientStrafe();
        float var5 = var4[1];
        float var6 = var4[2];
        float var7 = var4[0];
        if (var5 == 0.0F && var6 == 0.0F) {
            setPlayerXMotion(0.0);
            setPlayerZMotion(0.0);
        }

        double var8 = Math.cos(Math.toRadians((double)var7));
        double var10 = Math.sin(Math.toRadians((double)var7));
        double var12 = ((double)var5 * var8 + (double)var6 * var10) * speed;
        double var14 = ((double)var5 * var10 - (double)var6 * var8) * speed;
        setPlayerXMotion(var12);
        setPlayerZMotion(var14);
    }

    public static float[] method37083() {
        MovementInput var2 = mc.player.movementInput;
        float var3 = var2.moveForward;
        float var4 = var2.moveStrafe;
        return method37085(var3, var4);
    }

    public static float[] method37085(float var0, float var1) {
        float var4 = mc.player.rotationYaw + 90.0F;
        if (var0 == 0.0F) {
            if (var1 != 0.0F) {
                var4 += (float)(!(var1 > 0.0F) ? 90 : -90);
                var1 = 0.0F;
            }
        } else {
            if (!(var1 >= 1.0F)) {
                if (var1 <= -1.0F) {
                    var4 += (float)(!(var0 > 0.0F) ? -45 : 45);
                    var1 = 0.0F;
                }
            } else {
                var4 += (float)(!(var0 > 0.0F) ? 45 : -45);
                var1 = 0.0F;
            }

            if (!(var0 > 0.0F)) {
                if (var0 < 0.0F) {
                    var0 = -1.0F;
                    var4 -= 180.0F;
                }
            } else {
                var0 = 1.0F;
            }
        }

        return new float[]{var4, var0, var1};
    }

    public static double getSpeed() {
        double var2 = 0.2873;
        float var4 = 1.0F;
        ModifiableAttributeInstance var5 = mc.player.getAttribute(Attributes.MOVEMENT_SPEED);
        var4 = (float)((double)var4 * ((var5.getValue() / (double) mc.player.abilities.getWalkSpeed() + 1.0) / 2.0));
        if (mc.player.isSprinting()) {
            var4 = (float)((double)var4 - 0.15);
        }

        if (mc.player.isPotionActive(Effects.SPEED) && mc.player.isSprinting()) {
            var4 = (float)((double)var4 - 0.03000002 * (double)(mc.player.getActivePotionEffect(Effects.SPEED).getAmplifier() + 1));
        }

        if (mc.player.isSneaking()) {
            var2 *= 0.25;
        }

        if (isInWater()) {
            var2 *= 0.3;
        }

        return var2 * (double)var4;
    }

    public static boolean isInWater() {
        return mc.player.isInWater();
    }

    public static float method37092(EventMove var0, double var1, float var3, float var4, float var5) {
        float var8 = RotationUtil.angleDiff(var4, var3);
        if (!(var8 > var5)) {
            var4 = var3;
        } else {
            var4 += !(MathHelper.wrapDegrees(var3 - var4) > 0.0F) ? -var5 : var5;
        }

        float var9 = (var4 - 90.0F) * (float) (Math.PI / 180.0);
        var0.setX((double)(-MathHelper.sin(var9)) * var1);
        var0.setZ((double) MathHelper.cos(var9) * var1);
        setPlayerXMotion(var0.getX());
        setPlayerZMotion(var0.getZ());
        return var4;
    }

    public static float method37093(double var0, float var2, float var3, float var4) {
        float var7 = RotationUtil.angleDiff(var3, var2);
        if (!(var7 > var4)) {
            var3 = var2;
        } else {
            var3 += !(MathHelper.wrapDegrees(var2 - var3) > 0.0F) ? -var4 : var4;
        }

        float var8 = (var3 - 90.0F) * (float) (Math.PI / 180.0);
        setPlayerXMotion((double)(-MathHelper.sin(var8)) * var0);
        setPlayerZMotion((double) MathHelper.cos(var8) * var0);
        return var3;
    }

    public static double method37076() {
        double var2 = 0.2873 + (double)method37078() * 0.057;
        if (mc.player.isSneaking()) {
            var2 *= 0.25;
        }

        return var2;
    }

    public static boolean method17684(Entity var0) {
        ClientWorld var3 = mc.world;
        AxisAlignedBB var4 = var0.getBoundingBox();
        return var3.containsAnyLiquid(var4);
    }
}
