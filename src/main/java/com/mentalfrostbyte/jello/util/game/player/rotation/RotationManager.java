package com.mentalfrostbyte.jello.util.game.player.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

/**
 * Manages rotation values and smoothing
 */
public class RotationManager {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final Random random = new Random();

    // Current rotation values
    private float currentYaw;
    private float currentPitch;

    // Target rotation values
    private float targetYaw;
    private float targetPitch;

    // Rotation state
    private boolean rotating = false;

    // Fixed rotation speeds (calculated once per target)
    private float yawSpeed;
    private float pitchSpeed;

    // Tick-based timing instead of system time
    private static final float TICK_TIME = 0.05f; // 1/20 seconds per tick

    /**
     * Initialize rotation values
     */
    public void initialize() {
        currentYaw = mc.player != null ? mc.player.rotationYaw : 0;
        currentPitch = mc.player != null ? mc.player.rotationPitch : 0;
        targetYaw = currentYaw;
        targetPitch = currentPitch;
        rotating = false;
    }

    /**
     * Set target rotation and start rotating
     */
    public void setTargetRotation(float yaw, float pitch) {
        targetYaw = yaw;
        targetPitch = MathHelper.clamp(pitch, -90.0f, 90.0f);

        // Calculate fixed rotation speeds with small random component
        // This is calculated ONCE per target, not per tick
        yawSpeed = 10.0f + (random.nextFloat() * 2.0f); // Reduced randomness
        pitchSpeed = 8.0f + (random.nextFloat() * 1.5f); // Reduced randomness

        rotating = true;
    }

    /**
     * Update rotations based on target
     */
    public void updateRotations() {
        if (!rotating || mc.player == null) return;

        // Use fixed tick time instead of system time
        float deltaTime = TICK_TIME;

        // Calculate rotation speed based on distance to target
        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float pitchDiff = targetPitch - currentPitch;

        // Scale speed based on difference (with less aggressive scaling)
        float adjustedYawSpeed = Math.min(yawSpeed * (1.0f + Math.abs(yawDiff) / 90.0f), 20.0f);
        float adjustedPitchSpeed = Math.min(pitchSpeed * (1.0f + Math.abs(pitchDiff) / 40.0f), 15.0f);

        // Calculate change with clamping to prevent overshooting
        float yawChange = Math.min(adjustedYawSpeed * deltaTime, Math.abs(yawDiff)) * Math.signum(yawDiff);
        float pitchChange = Math.min(adjustedPitchSpeed * deltaTime, Math.abs(pitchDiff)) * Math.signum(pitchDiff);

        // Apply changes
        currentYaw = MathHelper.wrapDegrees(currentYaw + yawChange);
        currentPitch = MathHelper.clamp(currentPitch + pitchChange, -90.0f, 90.0f);

        // Re-calculate differences AFTER updating to check completion
        yawDiff = MathHelper.wrapDegrees(targetYaw - currentYaw);
        pitchDiff = targetPitch - currentPitch;

        // Check if we've reached the target
        if (Math.abs(yawDiff) < 0.1f && Math.abs(pitchDiff) < 0.1f) {
            rotating = false;
            // Snap to exact target when we're close enough
            currentYaw = targetYaw;
            currentPitch = targetPitch;
        }
    }

    /**
     * Smoothly reset rotations to player's view
     */
    public void smoothResetRotations(float playerYaw, float playerPitch) {
        setTargetRotation(playerYaw, playerPitch);
    }

    /**
     * Get current yaw
     */
    public float getCurrentYaw() {
        return currentYaw;
    }

    /**
     * Get current pitch
     */
    public float getCurrentPitch() {
        return currentPitch;
    }

    /**
     * Check if currently rotating
     */
    public boolean isRotating() {
        return rotating;
    }
}