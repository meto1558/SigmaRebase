package com.mentalfrostbyte.jello.util.player;

import com.mentalfrostbyte.jello.event.impl.EventMove;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.MovementInput;

public class MovementUtil {
    protected static Minecraft mc = Minecraft.getInstance();

    public static int getSpeedBoost() {
        return ! mc.player.isPotionActive(Effects.SPEED) ? 0 : mc.player.getActivePotionEffect(Effects.SPEED).getAmplifier() + 1;
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


    public static float[] method37084(float var0, float var1) {
        float var4 = mc.player.rotationYaw + 90.0F;
//        if (Client.getInstance().method19950().method31744() != -999.0F) {
//            var4 = Client.getInstance().method19950().method31744() + 90.0F;
//        }

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

        if (/*Client.getInstance().method19950().method31742()
                && !Client.getInstance().method19950().method31741()
                &&*/ (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F)) {
            var0 = 1.0F;
        }

        return new float[]{var4, var0, var1};
    }


    public static float[] lenientStrafe() {
        MovementInput input = mc.player.movementInput;
        float var3 = input.moveForward;
        float var4 = input.moveStrafe;
        return method37084(var3, var4);
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

        double var8 = Math.cos(Math.toRadians(var7));
        double var10 = Math.sin(Math.toRadians(var7));
        double var12 = ((double)var5 * var8 + (double)var6 * var10) * speed;
        double var14 = ((double)var5 * var10 - (double)var6 * var8) * speed;
        setPlayerXMotion(var12);
        setPlayerZMotion(var14);
    }

    public static double setPlayerXMotion(double x) {
        mc.player.setMotion(x, mc.player.getMotion().y, mc.player.getMotion().z);
        return x;
    }

    public static double setPlayerYMotion(double y) {
        mc.player.setMotion(mc.player.getMotion().x, y, mc.player.getMotion().z);
        return y;
    }

    public static double setPlayerZMotion(double z) {
        mc.player.setMotion(mc.player.getMotion().x, mc.player.getMotion().y, z);
        return z;
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

    public static boolean isInWater() {
        return mc.player.isInWater();
    }

    public static boolean isMoving() {
        boolean forward = mc.gameSettings.keyBindForward.isKeyDown();
        boolean left = mc.gameSettings.keyBindLeft.isKeyDown();
        boolean right = mc.gameSettings.keyBindRight.isKeyDown();
        boolean back = mc.gameSettings.keyBindBack.isKeyDown();
        return forward || left || right || back;
    }
}
