package com.mentalfrostbyte.jello.util.game.player.rotation;

import com.mentalfrostbyte.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages reinforcement learning for the AI
 */
public class ReinforcementManager {
    private static final Minecraft mc = Minecraft.getInstance();

    // Reward values
    private static final float HIT_REWARD = 1.0f;
    private static final float MOVING_HIT_REWARD = 1.5f;
    private static final float MISS_PENALTY = -0.2f;

    // Entity tracking
    private Map<Entity, Long> lastHitTimes = new HashMap<>();
    private Map<Entity, Float> entityRewards = new HashMap<>();

    // References to other components
    private final NeuralNetwork neuralNetwork;
    private final TrainingManager trainingManager;

    // Updated constructor to accept shared instances
    public ReinforcementManager(NeuralNetwork sharedNetwork, TrainingManager sharedTrainer) {
        this.neuralNetwork = sharedNetwork;
        this.trainingManager = sharedTrainer;
    }

    public ReinforcementManager() {
        this.neuralNetwork = new NeuralNetwork();
        this.trainingManager = new TrainingManager();
    }

    /**
     * Record a successful hit on an entity
     */
    public void recordHit(Entity entity, boolean wasMoving, float currentYaw, float currentPitch) {
        if (entity == null || mc.player == null) return;

        // Calculate reward based on whether player was moving
        float reward = wasMoving ? MOVING_HIT_REWARD : HIT_REWARD;

        // Store hit time and reward
        lastHitTimes.put(entity, System.currentTimeMillis());

        // Update entity reward
        entityRewards.put(entity, entityRewards.getOrDefault(entity, 0f) + reward);

        // Get the inputs that led to this successful hit
        float[] inputs = getEntityInputs(entity);

        // Get the current outputs (rotations that led to the hit)
        float[] currentOutputs = new float[NeuralNetwork.OUTPUT_SIZE];
        currentOutputs[0] = (currentYaw + 180.0f) / 360.0f;
        currentOutputs[1] = (currentPitch + 90.0f) / 180.0f;

        // Create a reinforced training sample with higher weight
        for (int i = 0; i < 3; i++) { // Add multiple samples to emphasize this success
            trainingManager.addTrainingSample(inputs, currentOutputs, reward);
        }

        // Train immediately for faster adaptation
        neuralNetwork.trainNetworkImmediate(inputs, currentOutputs, reward);

        Client.logger.info("JelloAI: Recorded hit on " + entity.getName().getString() +
                " with reward " + reward + " (Total: " + entityRewards.get(entity) + ")");
    }

    /**
     * Record a missed attack
     */
    public void recordMiss(Entity entity, float[] inputs, float[] expectedOutputs) {
        if (entity == null || mc.player == null) return;

        // Apply a small penalty for missing
        float penalty = MISS_PENALTY;

        // Update entity reward
        entityRewards.put(entity, entityRewards.getOrDefault(entity, 0f) + penalty);

        // Add a training sample with the better rotations
        trainingManager.addTrainingSample(inputs, expectedOutputs, Math.abs(penalty));

        // Train immediately for faster adaptation
        neuralNetwork.trainNetworkImmediate(inputs, expectedOutputs, Math.abs(penalty));

        Client.logger.info("JelloAI: Recorded miss on " + entity.getName().getString() +
                " with penalty " + penalty + " (Total: " + entityRewards.get(entity) + ")");
    }

    /**
     * Get neural network inputs for an entity
     */
    private float[] getEntityInputs(Entity entity) {
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
}