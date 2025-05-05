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

    // Create a single shared neural network instance
    private static final NeuralNetwork neuralNetwork = new NeuralNetwork();
    private static final RotationManager rotationManager = new RotationManager();

    // Pass the shared network to managers
    private static final TrainingManager trainingManager = new TrainingManager(neuralNetwork);
    private static final ReinforcementManager reinforcementManager =
            new ReinforcementManager(neuralNetwork, trainingManager);

    // Singleton instance for static access
    private static JelloAI instance;

    // Constants for reinforcement learning
    public static final float HIT_REWARD = 1.0f;
    public static final float MISS_PENALTY = -0.2f;

    /**
     * Initialize the AI system
     */
    public static void init() {
        instance = new JelloAI();
        rotationManager.initialize();
        neuralNetwork.initialize();
        trainingManager.initialize();
        trainingManager.startTrainingThread();
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
     * Face an entity with AI-calculated rotations
     */
    public static void faceEntity(Entity entity) {
        if (instance == null || entity == null || mc.player == null) return;

        // Get current player position
        double playerX = mc.player.getPosX();
        double playerY = mc.player.getPosY() + mc.player.getEyeHeight();
        double playerZ = mc.player.getPosZ();

        // Get target position with improved prediction
        double targetX = entity.getPosX();
        double targetY = entity.getPosY() + entity.getEyeHeight(); // FIXED: Use consistent eye height
        double targetZ = entity.getPosZ();

        // Enhanced motion prediction based on entity velocity and distance
        double distance = entity.getDistance(mc.player);
        double motionFactor = Math.min(distance * 0.15, 2.0); // Scale prediction with distance

        // Apply motion prediction
        targetX += entity.getMotion().x * motionFactor;
        targetY += entity.getMotion().y * motionFactor;
        targetZ += entity.getMotion().z * motionFactor;

        // Calculate differences
        double diffX = targetX - playerX;
        double diffY = targetY - playerY;
        double diffZ = targetZ - playerZ;

        // Calculate distance
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        // Calculate ideal rotations
        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F);
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));

        // Use neural network to adjust rotations if trained
        if (instance.neuralNetwork.isInitialized()) {
            float[] inputs = getEntityInputs(entity);
            float[] outputs = instance.neuralNetwork.predict(inputs);

            // Only use AI rotations if confidence is high enough
            if (instance.neuralNetwork.getConfidence() > 0.6f) {
                // FIXED: Properly denormalize tanh outputs from [-1,1] to angle ranges
                yaw = outputs[0] * 180.0f;  // maps [-1,1] → [-180,180]
                pitch = outputs[1] * 90.0f;  // maps [-1,1] → [-90,90]
            }
        }

        // Set target rotation with improved smoothing
        instance.rotationManager.setTargetRotation(yaw, pitch);
    }

    // Helper methods
    // In getEntityInputs method
    private static float[] getEntityInputs(Entity entity) {
        if (entity == null || mc.player == null) return new float[NeuralNetwork.INPUT_SIZE];

        float[] inputs = new float[NeuralNetwork.INPUT_SIZE];

        // Relative position
        double playerX = mc.player.getPosX();
        double playerY = mc.player.getPosY() + mc.player.getEyeHeight();
        double playerZ = mc.player.getPosZ();

        // FIXED: Use consistent eye height calculation
        double entityX = entity.getPosX();
        double entityY = entity.getPosY() + entity.getEyeHeight(); // Now using full eye height
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

    // In calculateIdealRotations method
    private static float[] calculateIdealRotations(Entity entity) {
        double playerX = mc.player.getPosX();
        double playerY = mc.player.getPosY() + mc.player.getEyeHeight();
        double playerZ = mc.player.getPosZ();

        // FIXED: Use consistent eye height calculation
        double entityX = entity.getPosX();
        double entityY = entity.getPosY() + entity.getEyeHeight(); // Now matches getEntityInputs
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

    /**
     * Normalize rotations for neural network input
     */
    private static float[] normalizeRotations(float yaw, float pitch) {
        // FIXED: Normalize to [-1,1] range for tanh activation
        return new float[] {
                yaw / 180.0f,    // maps [-180,180] → [-1,1]
                pitch / 90.0f    // maps [-90,90] → [-1,1]
        };
    }

    /**
     * Record a successful hit on an entity
     */
    public static void recordHit(Entity entity, boolean wasMoving) {
        if (instance == null || entity == null || mc.player == null) return;

        // Get current rotations
        float currentYaw = instance.rotationManager.getCurrentYaw();
        float currentPitch = instance.rotationManager.getCurrentPitch();

        // FIXED: Normalize rotations to [-1,1] for tanh
        float[] normalizedRotations = normalizeRotations(currentYaw, currentPitch);

        // Record hit with normalized rotations
        instance.reinforcementManager.recordHit(entity, wasMoving, normalizedRotations[0], normalizedRotations[1]);
    }

    /**
     * Record a missed attack
     */
    public static void recordMiss(Entity entity) {
        if (instance == null || entity == null || mc.player == null) return;

        // Calculate ideal rotations
        double playerX = mc.player.getPosX();
        double playerY = mc.player.getPosY() + mc.player.getEyeHeight();
        double playerZ = mc.player.getPosZ();

        // FIXED: Use consistent eye height calculation
        double entityX = entity.getPosX();
        double entityY = entity.getPosY() + entity.getEyeHeight(); // Now using full eye height
        double entityZ = entity.getPosZ();

        double diffX = entityX - playerX;
        double diffY = entityY - playerY;
        double diffZ = entityZ - playerZ;

        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float idealYaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F);
        float idealPitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));

        // FIXED: Normalize rotations to [-1,1] for tanh
        float[] normalizedRotations = normalizeRotations(idealYaw, idealPitch);

        // Get inputs for this entity
        float[] inputs = getEntityInputs(entity);

        // Create expected outputs with normalized rotations
        float[] expectedOutputs = new float[NeuralNetwork.OUTPUT_SIZE];
        expectedOutputs[0] = normalizedRotations[0];  // yaw in [-1,1]
        expectedOutputs[1] = normalizedRotations[1];  // pitch in [-1,1]

        // Record miss with normalized rotations
        instance.reinforcementManager.recordMiss(entity, inputs, expectedOutputs);
    }
}