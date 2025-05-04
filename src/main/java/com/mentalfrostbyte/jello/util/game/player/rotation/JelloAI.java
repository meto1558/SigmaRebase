package com.mentalfrostbyte.jello.util.game.player.rotation;

import com.mentalfrostbyte.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * JelloAI - Neural network based rotation system
 * Uses machine learning to create human-like rotations
 */
public class JelloAI {
    private static final Minecraft mc = Minecraft.getInstance();

    // Core components
    private static final NeuralNetwork neuralNetwork = new NeuralNetwork();
    private static final RotationManager rotationManager = new RotationManager();
    private static final TrainingManager trainingManager = new TrainingManager();
    private static final ReinforcementManager reinforcementManager = new ReinforcementManager();

    /**
     * Initialize the AI system
     */
    public static void init() {
        rotationManager.initialize();
        neuralNetwork.initialize();
        trainingManager.initialize();
        trainingManager.startTrainingThread();
    }

    /**
     * Face a specific entity using the neural network
     */
    public static void faceEntity(Entity entity) {
        if (entity == null || mc.player == null) return;

        // Calculate inputs for the neural network
        float[] inputs = getEntityInputs(entity);

        // Run the neural network forward pass
        float[] outputs = neuralNetwork.forwardPass(inputs);

        // Convert outputs to rotations
        float yaw = outputs[0] * 360.0f - 180.0f; // Convert from 0-1 to -180 to 180
        float pitch = outputs[1] * 180.0f - 90.0f; // Convert from 0-1 to -90 to 90

        // Calculate "ideal" rotations for training
        float[] idealRotations = calculateIdealRotations(entity);
        float[] expectedOutputs = normalizeRotations(idealRotations[0], idealRotations[1]);

        // Add to training samples
        trainingManager.addTrainingSample(inputs, expectedOutputs);

        // Set target rotation
        rotationManager.setTargetRotation(yaw, pitch);
    }

    /**
     * Face a specific block using the neural network
     */
    public static void faceBlock(BlockPos pos) {
        if (pos == null || mc.player == null) return;

        // Calculate inputs for the neural network
        float[] inputs = getBlockInputs(pos);

        // Run the neural network forward pass
        float[] outputs = neuralNetwork.forwardPass(inputs);

        // Convert outputs to rotations
        float yaw = outputs[0] * 360.0f - 180.0f; // Convert from 0-1 to -180 to 180
        float pitch = outputs[1] * 180.0f - 90.0f; // Convert from 0-1 to -90 to 90

        // Calculate "ideal" rotations for training
        float[] idealRotations = calculateIdealBlockRotations(pos);
        float[] expectedOutputs = normalizeRotations(idealRotations[0], idealRotations[1]);

        // Add to training samples
        trainingManager.addTrainingSample(inputs, expectedOutputs);

        // Set target rotation
        rotationManager.setTargetRotation(yaw, pitch);
    }

    /**
     * Update rotations based on target
     */
    public static void updateRotations() {
        rotationManager.updateRotations();
    }

    /**
     * Get current yaw
     */
    public static float getCurrentYaw() {
        return rotationManager.getCurrentYaw();
    }

    /**
     * Get current pitch
     */
    public static float getCurrentPitch() {
        return rotationManager.getCurrentPitch();
    }

    /**
     * Apply server-side rotations without changing client-side camera
     */
    public static void applyServerRotation() {
        if (!rotationManager.isRotating() || mc.player == null) return;

        // Store original rotations
        float originalYaw = mc.player.rotationYaw;
        float originalPitch = mc.player.rotationPitch;

        // Apply calculated rotations
        mc.player.rotationYaw = rotationManager.getCurrentYaw();
        mc.player.rotationPitch = rotationManager.getCurrentPitch();

        // Restore client-side rotations (for camera)
        mc.player.prevRotationYaw = originalYaw;
        mc.player.prevRotationPitch = originalPitch;
    }

    /**
     * Get smoothed rotation values for a target position
     */
    public static float[] getRotationsToPosition(double targetX, double targetY, double targetZ) {
        if (mc.player == null) return new float[] {0, 0};

        float[] inputs = getPositionInputs(targetX, targetY, targetZ);
        float[] outputs = neuralNetwork.forwardPass(inputs);

        // Convert outputs to rotations
        float smoothedYaw = outputs[0] * 360.0f - 180.0f;
        float smoothedPitch = outputs[1] * 180.0f - 90.0f;

        return new float[] {smoothedYaw, smoothedPitch};
    }

    /**
     * Check if current rotation is close enough to target
     */
    public static boolean isRotationClose(float targetYaw, float targetPitch, float threshold) {
        if (mc.player == null) return false;

        float yawDiff = Math.abs(MathHelper.wrapDegrees(targetYaw - mc.player.rotationYaw));
        float pitchDiff = Math.abs(targetPitch - mc.player.rotationPitch);

        return yawDiff <= threshold && pitchDiff <= threshold;
    }

    /**
     * Gradually reset rotations to player's view
     */
    public static void smoothResetRotations() {
        if (mc.player == null) return;
        rotationManager.smoothResetRotations(mc.player.rotationYaw, mc.player.rotationPitch);
    }

    /**
     * Record a successful hit on an entity
     */
    public static void recordHit(Entity entity, boolean wasMoving) {
        if (entity == null || mc.player == null) return;
        reinforcementManager.recordHit(entity, wasMoving, rotationManager.getCurrentYaw(), rotationManager.getCurrentPitch());
    }

    /**
     * Record a missed attack
     */
    public static void recordMiss(Entity entity) {
        if (entity == null || mc.player == null) return;
        reinforcementManager.recordMiss(entity);
    }

    // Helper methods
    private static float[] getEntityInputs(Entity entity) {
        if (entity == null || mc.player == null) return new float[NeuralNetwork.INPUT_SIZE];

        float[] inputs = new float[NeuralNetwork.INPUT_SIZE];

        // Relative position
        double playerX = mc.player.getPosX();
        double playerY = mc.player.getPosY() + mc.player.getEyeHeight();
        double playerZ = mc.player.getPosZ();

        double entityX = entity.getPosX();
        double entityY = entity.getPosY() + entity.getEyeHeight() * 0.85;
        double entityZ = entity.getPosZ();

        double diffX = entityX - playerX;
        double diffY = entityY - playerY;
        double diffZ = entityZ - playerZ;

        // Normalize inputs
        inputs[0] = (float) (diffX / 20.0);
        inputs[1] = (float) (diffY / 10.0);
        inputs[2] = (float) (diffZ / 20.0);

        // Entity velocity (normalized)
        inputs[3] = (float) (entity.getMotion().x / 2.0);
        inputs[4] = (float) (entity.getMotion().y / 2.0);
        inputs[5] = (float) (entity.getMotion().z / 2.0);

        // Current rotations (normalized)
        inputs[6] = mc.player.rotationYaw / 180.0f;
        inputs[7] = mc.player.rotationPitch / 90.0f;

        return inputs;
    }

    private static float[] getBlockInputs(BlockPos pos) {
        if (pos == null || mc.player == null) return new float[NeuralNetwork.INPUT_SIZE];

        float[] inputs = new float[NeuralNetwork.INPUT_SIZE];

        // Relative position
        double playerX = mc.player.getPosX();
        double playerY = mc.player.getPosY() + mc.player.getEyeHeight();
        double playerZ = mc.player.getPosZ();

        double blockX = pos.getX() + 0.5;
        double blockY = pos.getY() + 0.5;
        double blockZ = pos.getZ() + 0.5;

        double diffX = blockX - playerX;
        double diffY = blockY - playerY;
        double diffZ = blockZ - playerZ;

        // Normalize inputs
        inputs[0] = (float) (diffX / 20.0);
        inputs[1] = (float) (diffY / 10.0);
        inputs[2] = (float) (diffZ / 20.0);

        // No velocity for blocks
        inputs[3] = 0;
        inputs[4] = 0;
        inputs[5] = 0;

        // Current rotations (normalized)
        inputs[6] = mc.player.rotationYaw / 180.0f;
        inputs[7] = mc.player.rotationPitch / 90.0f;

        return inputs;
    }

    private static float[] getPositionInputs(double targetX, double targetY, double targetZ) {
        if (mc.player == null) return new float[NeuralNetwork.INPUT_SIZE];

        float[] inputs = new float[NeuralNetwork.INPUT_SIZE];

        // Relative position
        double playerX = mc.player.getPosX();
        double playerY = mc.player.getPosY() + mc.player.getEyeHeight();
        double playerZ = mc.player.getPosZ();

        double diffX = targetX - playerX;
        double diffY = targetY - playerY;
        double diffZ = targetZ - playerZ;

        // Normalize inputs
        inputs[0] = (float) (diffX / 20.0);
        inputs[1] = (float) (diffY / 10.0);
        inputs[2] = (float) (diffZ / 20.0);

        // No velocity
        inputs[3] = 0;
        inputs[4] = 0;
        inputs[5] = 0;

        // Current rotations (normalized)
        inputs[6] = mc.player.rotationYaw / 180.0f;
        inputs[7] = mc.player.rotationPitch / 90.0f;

        return inputs;
    }

    private static float[] calculateIdealRotations(Entity entity) {
        double playerX = mc.player.getPosX();
        double playerY = mc.player.getPosY() + mc.player.getEyeHeight();
        double playerZ = mc.player.getPosZ();

        double entityX = entity.getPosX();
        double entityY = entity.getPosY() + entity.getHeight() / 2;
        double entityZ = entity.getPosZ();

        double diffX = entityX - playerX;
        double diffY = entityY - playerY;
        double diffZ = entityZ - playerZ;

        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float idealYaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float idealPitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

        return new float[] {idealYaw, idealPitch};
    }

    private static float[] calculateIdealBlockRotations(BlockPos pos) {
        double playerX = mc.player.getPosX();
        double playerY = mc.player.getPosY() + mc.player.getEyeHeight();
        double playerZ = mc.player.getPosZ();

        double blockX = pos.getX() + 0.5;
        double blockY = pos.getY() + 0.5;
        double blockZ = pos.getZ() + 0.5;

        double diffX = blockX - playerX;
        double diffY = blockY - playerY;
        double diffZ = blockZ - playerZ;

        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float idealYaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float idealPitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

        return new float[] {idealYaw, idealPitch};
    }

    private static float[] normalizeRotations(float yaw, float pitch) {
        float[] normalized = new float[2];
        normalized[0] = (yaw + 180.0f) / 360.0f; // Convert from -180 to 180 to 0-1
        normalized[1] = (pitch + 90.0f) / 180.0f; // Convert from -90 to 90 to 0-1
        return normalized;
    }
}