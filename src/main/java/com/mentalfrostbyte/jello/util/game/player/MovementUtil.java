package com.mentalfrostbyte.jello.util.game.player;

import com.mentalfrostbyte.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovementInput;

public class MovementUtil {
    private static final Minecraft mc = Minecraft.getInstance();

    public static float[] getDirection(float forward, float strafe) {
        float yaw = mc.player.rotationYaw + 90.0F;
        if (Client.getInstance().minerTracker.getAdjustedYaw() != -999.0f) {
            yaw = Client.getInstance().minerTracker.getAdjustedYaw() + 90.0f;
        }

        if (forward != 0.0F) {
            if (!(strafe >= 1.0F)) {
                if (strafe <= -1.0F) {
                    yaw += (float) (!(forward > 0.0F) ? -45 : 45);
                    strafe = 0.0F;
                }
            } else {
                yaw += (float) (!(forward > 0.0F) ? 45 : -45);
                strafe = 0.0F;
            }

            if (!(forward > 0.0F)) {
                if (forward < 0.0F) {
                    forward = -1.0F;
                }
            } else {
                forward = 1.0F;
            }
        }

        return new float[]{yaw, forward, strafe};
    }

    /**
     * Calculates adjusted strafe values based on the player's current movement input.
     *
     * @return A float array containing adjusted yaw, forward, and strafe values.
     */
    public static float[] getDirection() {
        MovementInput input = mc.player.movementInput;
        return getDirection(input.moveForward, input.moveStrafe);
    }

    /**
     * Applies strafing movement to the player based on the given speed.
     *
     * @param speed The speed at which to strafe.
     */
    public static void strafe(double speed) {
        float[] adjusted = getDirection();
        float forward = adjusted[1];
        float side = adjusted[2];
        float yaw = adjusted[0];

        if (forward == 0.0F && side == 0.0F) {
            setPlayerXMotion(0.0);
            setPlayerZMotion(0.0);
        }

        double cos = Math.cos(Math.toRadians(yaw));
        double sin = Math.sin(Math.toRadians(yaw));
        double x = (forward * cos + side * sin) * speed;
        double z = (forward * sin - side * cos) * speed;
        mc.player.setMotion(x, mc.player.getMotion().y, z);
    }

    /**
     * Sets the player's X motion component.
     *
     * @param x The new X motion value.
     */
    public static void setPlayerXMotion(double x) {
        mc.player.setMotion(x, mc.player.getMotion().y, mc.player.getMotion().z);
    }

    /**
     * Sets the player's Y motion component.
     *
     * @param y The new Y motion value.
     * @return The set Y motion value.
     */
    public static double setPlayerYMotion(double y) {
        mc.player.setMotion(mc.player.getMotion().x, y, mc.player.getMotion().z);
        return y;
    }

    /**
     * Sets the player's Z motion component.
     *
     * @param z The new Z motion value.
     * @return The set Z motion value.
     */
    public static double setPlayerZMotion(double z) {
        mc.player.setMotion(mc.player.getMotion().x, mc.player.getMotion().y, z);
        return z;
    }

}
