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
    private long lastRotationTime = 0;

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
        rotating = true;
        lastRotationTime = System.currentTimeMillis();
    }

    /**
     * Update rotations based on target
     */
    public void updateRotations() {
        if (!rotating || mc.player == null) return;

        // Calculate time since last rotation
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastRotationTime) / 1000.0f;
        lastRotationTime = currentTime;

        // Calculate rotation speed based on distance to target
        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float pitchDiff = targetPitch - currentPitch;

        // Calculate rotation speed with some randomness
        float baseYawSpeed = 10.0f + random.nextFloat() * 5.0f;
        float basePitchSpeed = 8.0f + random.nextFloat() * 4.0f;

        // Scale speed based on difference (faster for larger differences)
        float yawSpeed = Math.min(baseYawSpeed * (1.0f + Math.abs(yawDiff) / 45.0f), 30.0f);
        float pitchSpeed = Math.min(basePitchSpeed * (1.0f + Math.abs(pitchDiff) / 20.0f), 25.0f);

        // Apply speed
        float maxYawChange = yawSpeed * deltaTime;
        float maxPitchChange = pitchSpeed * deltaTime;

        // Limit rotation change
        if (Math.abs(yawDiff) <= maxYawChange) {
            currentYaw = targetYaw;
        } else {
            currentYaw += maxYawChange * Math.signum(yawDiff);
        }

        if (Math.abs(pitchDiff) <= maxPitchChange) {
            currentPitch = targetPitch;
        } else {
            currentPitch += maxPitchChange * Math.signum(pitchDiff);
        }

        // Normalize yaw
        currentYaw = MathHelper.wrapDegrees(currentYaw);

        // Clamp pitch
        currentPitch = MathHelper.clamp(currentPitch, -90.0f, 90.0f);

        // Check if we've reached the target
        if (Math.abs(yawDiff) < 0.1f && Math.abs(pitchDiff) < 0.1f) {
            rotating = false;
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