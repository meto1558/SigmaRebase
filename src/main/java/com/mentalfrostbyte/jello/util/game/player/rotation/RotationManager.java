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

    // Tick-based timing instead of system time
    private static final float TICK_TIME = 0.05f; // 1/20 seconds per tick

    // Feed-forward tracking
    private float lastYawDiff = 0;
    private float lastPitchDiff = 0;

    // Control parameters
    private static final float YAW_PROPORTIONAL_GAIN = 15.0f;    // Proportional control gain
    private static final float PITCH_PROPORTIONAL_GAIN = 12.0f;  // Proportional control gain
    private static final float YAW_FEEDFORWARD_GAIN = 0.6f;      // Feed-forward gain
    private static final float PITCH_FEEDFORWARD_GAIN = 0.5f;    // Feed-forward gain
    private static final float MAX_YAW_SPEED = 720.0f;           // Maximum yaw speed (degrees/sec)
    private static final float MAX_PITCH_SPEED = 360.0f;         // Maximum pitch speed (degrees/sec)

    /**
     * Initialize rotation values
     */
    public void initialize() {
        currentYaw = mc.player != null ? mc.player.rotationYaw : 0;
        currentPitch = mc.player != null ? mc.player.rotationPitch : 0;
        targetYaw = currentYaw;
        targetPitch = currentPitch;
        rotating = false;
        lastYawDiff = 0;
        lastPitchDiff = 0;
    }

    /**
     * Set target rotation and start rotating
     */
    public void setTargetRotation(float yaw, float pitch) {
        targetYaw = yaw;
        targetPitch = MathHelper.clamp(pitch, -90.0f, 90.0f);
        rotating = true;
        // Reset feed-forward tracking when setting a new target
        lastYawDiff = 0;
        lastPitchDiff = 0;
    }

    /**
     * Update rotations based on target
     */
    public void updateRotations() {
        if (!rotating || mc.player == null) return;

        // Use fixed tick time
        float deltaTime = TICK_TIME;

        // Calculate rotation differences
        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float pitchDiff = targetPitch - currentPitch;

        // Calculate angular velocities (how fast the error is changing)
        float yawAngularVelocity = (yawDiff - lastYawDiff) / deltaTime;
        float pitchAngularVelocity = (pitchDiff - lastPitchDiff) / deltaTime;

        // Store current differences for next tick
        lastYawDiff = yawDiff;
        lastPitchDiff = pitchDiff;

        // YAW CONTROL
        // Pure proportional control - speed scales with error magnitude
        float yawBaseSpeed = Math.abs(yawDiff) * YAW_PROPORTIONAL_GAIN;
        // Ensure a minimum speed to catch up with strafing targets
        yawBaseSpeed = Math.max(yawBaseSpeed, 30.0f);

        // Add feed-forward term based on angular velocity (preserve sign)
        float yawFeedForward = yawAngularVelocity * YAW_FEEDFORWARD_GAIN;

        // Calculate total speed (capped at maximum)
        float totalYawSpeed = Math.min(yawBaseSpeed + yawFeedForward, MAX_YAW_SPEED);

        // Calculate maximum change per tick
        float maxYawChange = totalYawSpeed * deltaTime;

        // Calculate step with clamping to prevent overshooting
        float stepYaw = Math.signum(yawDiff) * Math.min(Math.abs(yawDiff), maxYawChange);

        // Apply yaw change
        currentYaw = MathHelper.wrapDegrees(currentYaw + stepYaw);

        // PITCH CONTROL
        // Pure proportional control - speed scales with error magnitude
        float pitchBaseSpeed = Math.abs(pitchDiff) * PITCH_PROPORTIONAL_GAIN;
        // Ensure a minimum speed
        pitchBaseSpeed = Math.max(pitchBaseSpeed, 25.0f);

        // Add feed-forward term based on angular velocity (preserve sign)
        float pitchFeedForward = pitchAngularVelocity * PITCH_FEEDFORWARD_GAIN;

        // Calculate total speed (capped at maximum)
        float totalPitchSpeed = Math.min(pitchBaseSpeed + pitchFeedForward, MAX_PITCH_SPEED);

        // Calculate maximum change per tick
        float maxPitchChange = totalPitchSpeed * deltaTime;

        // Calculate step with clamping to prevent overshooting
        float stepPitch = Math.signum(pitchDiff) * Math.min(Math.abs(pitchDiff), maxPitchChange);

        // Apply pitch change
        currentPitch = MathHelper.clamp(currentPitch + stepPitch, -90.0f, 90.0f);

        // Check if we've reached the target (wider threshold)
        if (Math.abs(yawDiff) < 0.5f && Math.abs(pitchDiff) < 0.5f) {
            rotating = false;
            // Snap exactly to target
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