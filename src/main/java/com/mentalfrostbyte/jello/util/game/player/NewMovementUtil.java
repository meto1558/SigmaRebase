package com.mentalfrostbyte.jello.util.game.player;

import com.mentalfrostbyte.jello.util.game.MinecraftUtil;

public class NewMovementUtil implements MinecraftUtil {

    public static double getSpeed() {
        if (mc.player == null) {
            return 0.0;
        }

        double motionX = mc.player.getMotion().x;
        double motionZ = mc.player.getMotion().z;
        return Math.sqrt(motionX * motionX + motionZ * motionZ);
    }

    public static void setSpeed(double moveSpeed, float yaw, double strafe, double forward) {
        if (forward != 0.0D) {
            yaw += (strafe > 0.0D) ? (forward > 0.0D ? -45 : 45) : (strafe < 0.0D) ? (forward > 0.0D ? 45 : -45) : 0;
            strafe = 0.0D;
            forward = (forward > 0.0D) ? 1.0D : -1.0D;
        }

        if (strafe != 0.0D) {
            strafe = (strafe > 0.0D) ? 1.0D : -1.0D;
        }

        double radianYaw = Math.toRadians(yaw + 90.0F);
        double cosYaw = Math.cos(radianYaw);
        double sinYaw = Math.sin(radianYaw);

        mc.player.setMotion(
                forward * moveSpeed * cosYaw + strafe * moveSpeed * sinYaw,
                mc.player.getMotion().y,
                forward * moveSpeed * sinYaw - strafe * moveSpeed * cosYaw
        );
    }

    public static void setSpeed(double moveSpeed) {
        setSpeed(moveSpeed, mc.player.rotationYaw, mc.player.movementInput.moveStrafe, mc.player.movementInput.moveForward);
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static void strafe(double moveSpeed) {
        if (mc.player.movementInput.moveForward != 0.0) {
            mc.player.movementInput.moveForward = (mc.player.movementInput.moveForward > 0.0) ? 1.0f : -1.0f;
        }

        if (mc.player.movementInput.moveStrafe != 0.0) {
            mc.player.movementInput.moveStrafe = (mc.player.movementInput.moveStrafe > 0.0) ? 1.0f : -1.0f;
        }

        if (mc.player.movementInput.moveForward == 0.0 && mc.player.movementInput.moveStrafe == 0.0) {
            mc.player.setMotion(0.0, mc.player.getMotion().y, 0.0);
            return;
        }

        if (mc.player.movementInput.moveForward != 0.0 && mc.player.movementInput.moveStrafe != 0.0) {
            mc.player.movementInput.moveForward *= (float) Math.sin(Math.toRadians(36.67));
            mc.player.movementInput.moveStrafe *= (float) Math.cos(Math.toRadians(36.67));
        }

        double yawRadians = Math.toRadians(mc.player.rotationYaw);

        mc.player.setMotion(
                mc.player.movementInput.moveForward * moveSpeed * -Math.sin(yawRadians)
                + mc.player.movementInput.moveStrafe * moveSpeed * Math.cos(yawRadians),
                mc.player.getMotion().y,
                mc.player.movementInput.moveForward * moveSpeed * Math.cos(yawRadians)
                - mc.player.movementInput.moveStrafe * moveSpeed * -Math.sin(yawRadians
                )
        );
    }

    public static boolean isMoving() {
        boolean forward = mc.gameSettings.keyBindForward.isKeyDown();
        boolean left = mc.gameSettings.keyBindLeft.isKeyDown();
        boolean right = mc.gameSettings.keyBindRight.isKeyDown();
        boolean back = mc.gameSettings.keyBindBack.isKeyDown();
        return forward || left || right || back;
    }

}
