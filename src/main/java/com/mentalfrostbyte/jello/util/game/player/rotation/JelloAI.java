package com.mentalfrostbyte.jello.util.game.player.rotation;

// Fix imports - remove duplicate Client import
import com.mentalfrostbyte.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * JelloAI - Neural network based rotation system
 * Uses machine learning to create human-like rotations
 */
public class JelloAI {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final Random random = new Random();

    // Neural Network parameters
    private static final int INPUT_SIZE = 8;
    private static final int HIDDEN_SIZE = 16;
    private static final int OUTPUT_SIZE = 2;

    // Neural Network weights
    private static float[][] weightsInputToHidden = new float[INPUT_SIZE][HIDDEN_SIZE];
    private static float[][] weightsHiddenToOutput = new float[HIDDEN_SIZE][OUTPUT_SIZE];

    // Biases
    private static float[] hiddenBiases = new float[HIDDEN_SIZE];
    private static float[] outputBiases = new float[OUTPUT_SIZE];

    // Learning parameters
    private static final float LEARNING_RATE = 0.01f;
    private static final float MOMENTUM = 0.9f;

    // Previous weight changes (for momentum)
    private static float[][] prevDeltaInputHidden = new float[INPUT_SIZE][HIDDEN_SIZE];
    private static float[][] prevDeltaHiddenOutput = new float[HIDDEN_SIZE][OUTPUT_SIZE];

    // Training data
    private static final int MAX_TRAINING_SAMPLES = 1000;
    private static Queue<TrainingSample> trainingSamples = new ConcurrentLinkedQueue<>();

    // Current rotation values
    private static float currentYaw;
    private static float currentPitch;

    // Target rotation values
    private static float targetYaw;
    private static float targetPitch;

    // Rotation state
    private static boolean rotating = false;
    private static long lastRotationTime = 0;
    private static long lastTrainingTime = 0;

    // File paths for saving/loading the neural network
    private static final String WEIGHTS_FILE = "jelloai_weights.dat";
    private static final String TRAINING_FILE = "jelloai_training.dat";

    /**
     * Training sample class to store input-output pairs
     */
    private static class TrainingSample implements Serializable {
        private static final long serialVersionUID = 1L;
        float[] inputs;
        float[] expectedOutputs;

        public TrainingSample(float[] inputs, float[] expectedOutputs) {
            this.inputs = inputs;
            this.expectedOutputs = expectedOutputs;
        }
    }

    /**
     * Initialize the AI system
     */
    public static void init() {
        currentYaw = mc.player != null ? mc.player.rotationYaw : 0;
        currentPitch = mc.player != null ? mc.player.rotationPitch : 0;
        targetYaw = currentYaw;
        targetPitch = currentPitch;
        rotating = false;

        // Try to load existing weights
        if (!loadWeights()) {
            // Initialize with random weights if loading fails
            initializeRandomWeights();
        }

        // Load training data
        loadTrainingData();

        // Start background training thread
        startTrainingThread();
    }

    /**
     * Initialize weights with small random values
     */
    private static void initializeRandomWeights() {
        for (int i = 0; i < INPUT_SIZE; i++) {
            for (int j = 0; j < HIDDEN_SIZE; j++) {
                weightsInputToHidden[i][j] = (random.nextFloat() - 0.5f) * 0.1f;
                prevDeltaInputHidden[i][j] = 0;
            }
        }

        for (int i = 0; i < HIDDEN_SIZE; i++) {
            hiddenBiases[i] = (random.nextFloat() - 0.5f) * 0.1f;

            for (int j = 0; j < OUTPUT_SIZE; j++) {
                weightsHiddenToOutput[i][j] = (random.nextFloat() - 0.5f) * 0.1f;
                prevDeltaHiddenOutput[i][j] = 0;
            }
        }

        for (int i = 0; i < OUTPUT_SIZE; i++) {
            outputBiases[i] = (random.nextFloat() - 0.5f) * 0.1f;
        }
    }

    /**
     * Start background training thread
     */
    private static void startTrainingThread() {
        Thread trainingThread = new Thread(() -> {
            while (true) {
                try {
                    // Train every 30 seconds if we have samples
                    if (System.currentTimeMillis() - lastTrainingTime > 30000 && !trainingSamples.isEmpty()) {
                        trainNetwork();
                        lastTrainingTime = System.currentTimeMillis();

                        // Save weights and training data periodically
                        saveWeights();
                        saveTrainingData();
                    }

                    Thread.sleep(1000); // Check every second
                } catch (Exception e) {
                    // Fixed Client reference
                    Client.logger.error("Error in JelloAI training thread", e);
                }
            }
        }, "JelloAI-Training");

        trainingThread.setDaemon(true);
        trainingThread.start();
    }

    /**
     * Face a specific entity using the neural network
     */
    /**
     * Face an entity with smooth rotations
     * @param entity The entity to face
     */
    public static void faceEntity(Entity entity) {
        if (entity == null || mc.player == null) return;

        // Get precise entity position (center of hitbox)
        double entityX = entity.getPosX();
        double entityY = entity.getPosY() + entity.getEyeHeight() * 0.85; // Target slightly below eye level
        double entityZ = entity.getPosZ();

        // Get player eye position
        double playerX = mc.player.getPosX();
        double playerY = mc.player.getPosY() + mc.player.getEyeHeight();
        double playerZ = mc.player.getPosZ();

        // Calculate differences
        double diffX = entityX - playerX;
        double diffY = entityY - playerY;
        double diffZ = entityZ - playerZ;

        // Calculate distance in XZ plane
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        // Calculate target angles - FIXED YAW CALCULATION
        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F);
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));

        // Ensure yaw is properly wrapped
        yaw = MathHelper.wrapDegrees(yaw);

        // Clamp pitch to valid range
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);

        // Debug info
        Client.logger.info("Target: " + entity.getName().getString() +
                " Pos: " + entityX + "," + entityY + "," + entityZ +
                " Calculated Yaw: " + yaw + " Pitch: " + pitch);

        // Set target rotations
        targetYaw = yaw;
        targetPitch = pitch;
        rotating = true;
        lastRotationTime = System.currentTimeMillis();

        // Add training sample for this rotation
        float[] inputs = new float[INPUT_SIZE];
        inputs[0] = (float) (diffX / 20.0);
        inputs[1] = (float) (diffY / 10.0);
        inputs[2] = (float) (diffZ / 20.0);
        inputs[3] = 0;
        inputs[4] = 0;
        inputs[5] = 0;
        inputs[6] = mc.player.rotationYaw / 180.0f;
        inputs[7] = mc.player.rotationPitch / 90.0f;

        float[] expectedOutputs = new float[OUTPUT_SIZE];
        expectedOutputs[0] = (yaw + 180.0f) / 360.0f;
        expectedOutputs[1] = (pitch + 90.0f) / 180.0f;

        addTrainingSample(inputs, expectedOutputs);
    }

    /**
     * Update rotations based on current target
     */
    /**
     * Update rotations based on current target
     */
    public static void updateRotations() {
        if (!rotating || mc.player == null) return;

        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastRotationTime) / 1000.0f;
        lastRotationTime = currentTime;

        // Calculate smooth rotation step
        float yawDifference = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float pitchDifference = targetPitch - currentPitch;

        // Fixed rotation speeds - more predictable than neural network for now
        float yawSpeed = 10.0f + (Math.abs(yawDifference) * 0.5f); // Faster for larger differences
        float pitchSpeed = 8.0f + (Math.abs(pitchDifference) * 0.3f);

        // Apply rotation with speed limit
        if (Math.abs(yawDifference) > 0.1f) {
            float yawChange = Math.min(Math.abs(yawDifference), yawSpeed * deltaTime) * Math.signum(yawDifference);
            currentYaw = MathHelper.wrapDegrees(currentYaw + yawChange);
        } else {
            currentYaw = targetYaw; // Snap to target when very close
        }

        if (Math.abs(pitchDifference) > 0.1f) {
            float pitchChange = Math.min(Math.abs(pitchDifference), pitchSpeed * deltaTime) * Math.signum(pitchDifference);
            currentPitch = MathHelper.clamp(currentPitch + pitchChange, -90.0f, 90.0f);
        } else {
            currentPitch = targetPitch; // Snap to target when very close
        }

        // Debug info occasionally
        if (random.nextInt(100) == 0) {
            Client.logger.info("Current Yaw: " + currentYaw + " Target Yaw: " + targetYaw +
                    " Current Pitch: " + currentPitch + " Target Pitch: " + targetPitch);
        }
    }

    /**
     * Face a specific block using the neural network
     */
    public static void faceBlock(BlockPos pos) {
        if (pos == null || mc.player == null) return;

        // Calculate inputs for the neural network
        float[] inputs = new float[INPUT_SIZE];

        // Relative position - using direct coordinate access
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
        inputs[0] = (float) (diffX / 20.0); // Normalize to roughly -1 to 1
        inputs[1] = (float) (diffY / 10.0);
        inputs[2] = (float) (diffZ / 20.0);

        // No velocity for blocks
        inputs[3] = 0;
        inputs[4] = 0;
        inputs[5] = 0;

        // Current rotations (normalized)
        inputs[6] = mc.player.rotationYaw / 180.0f;
        inputs[7] = mc.player.rotationPitch / 90.0f;

        // Run the neural network forward pass
        float[] outputs = forwardPass(inputs);

        // Convert outputs to rotations
        float yaw = outputs[0] * 360.0f - 180.0f; // Convert from 0-1 to -180 to 180
        float pitch = outputs[1] * 180.0f - 90.0f; // Convert from 0-1 to -90 to 90

        // Calculate "ideal" rotations for training
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float idealYaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float idealPitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

        // Normalize ideal rotations for training
        float[] expectedOutputs = new float[OUTPUT_SIZE];
        expectedOutputs[0] = (idealYaw + 180.0f) / 360.0f; // Convert from -180 to 180 to 0-1
        expectedOutputs[1] = (idealPitch + 90.0f) / 180.0f; // Convert from -90 to 90 to 0-1

        // Add to training samples
        addTrainingSample(inputs, expectedOutputs);

        // Set target rotation
        setTargetRotation(yaw, pitch);
    }

    /**
     * Add a training sample to the queue
     */
    private static void addTrainingSample(float[] inputs, float[] expectedOutputs) {
        trainingSamples.add(new TrainingSample(inputs, expectedOutputs));

        // Keep the queue size limited
        while (trainingSamples.size() > MAX_TRAINING_SAMPLES) {
            trainingSamples.poll();
        }
    }

    /**
     * Forward pass through the neural network
     */
    private static float[] forwardPass(float[] inputs) {
        // Hidden layer
        float[] hiddenOutputs = new float[HIDDEN_SIZE];
        for (int i = 0; i < HIDDEN_SIZE; i++) {
            float sum = hiddenBiases[i];
            for (int j = 0; j < INPUT_SIZE; j++) {
                sum += inputs[j] * weightsInputToHidden[j][i];
            }
            hiddenOutputs[i] = sigmoid(sum);
        }

        // Output layer
        float[] outputs = new float[OUTPUT_SIZE];
        for (int i = 0; i < OUTPUT_SIZE; i++) {
            float sum = outputBiases[i];
            for (int j = 0; j < HIDDEN_SIZE; j++) {
                sum += hiddenOutputs[j] * weightsHiddenToOutput[j][i];
            }
            outputs[i] = sigmoid(sum);
        }

        return outputs;
    }

    /**
     * Train the neural network using backpropagation
     */
    private static void trainNetwork() {
        if (trainingSamples.isEmpty()) return;

        // Convert queue to array for training
        TrainingSample[] samples = trainingSamples.toArray(new TrainingSample[0]);

        // Train for multiple epochs
        for (int epoch = 0; epoch < 5; epoch++) {
            // Shuffle samples
            shuffleArray(samples);

            // Process each sample
            for (TrainingSample sample : samples) {
                // Forward pass
                float[] hiddenOutputs = new float[HIDDEN_SIZE];
                for (int i = 0; i < HIDDEN_SIZE; i++) {
                    float sum = hiddenBiases[i];
                    for (int j = 0; j < INPUT_SIZE; j++) {
                        sum += sample.inputs[j] * weightsInputToHidden[j][i];
                    }
                    hiddenOutputs[i] = sigmoid(sum);
                }

                float[] outputs = new float[OUTPUT_SIZE];
                for (int i = 0; i < OUTPUT_SIZE; i++) {
                    float sum = outputBiases[i];
                    for (int j = 0; j < HIDDEN_SIZE; j++) {
                        sum += hiddenOutputs[j] * weightsHiddenToOutput[j][i];
                    }
                    outputs[i] = sigmoid(sum);
                }

                // Backpropagation
                // Output layer error
                float[] outputErrors = new float[OUTPUT_SIZE];
                for (int i = 0; i < OUTPUT_SIZE; i++) {
                    outputErrors[i] = (sample.expectedOutputs[i] - outputs[i]) * sigmoidDerivative(outputs[i]);
                }

                // Hidden layer error
                float[] hiddenErrors = new float[HIDDEN_SIZE];
                for (int i = 0; i < HIDDEN_SIZE; i++) {
                    float error = 0;
                    for (int j = 0; j < OUTPUT_SIZE; j++) {
                        error += outputErrors[j] * weightsHiddenToOutput[i][j];
                    }
                    hiddenErrors[i] = error * sigmoidDerivative(hiddenOutputs[i]);
                }

                // Update weights and biases with momentum
                // Hidden to output weights
                for (int i = 0; i < HIDDEN_SIZE; i++) {
                    for (int j = 0; j < OUTPUT_SIZE; j++) {
                        float delta = LEARNING_RATE * outputErrors[j] * hiddenOutputs[i] + MOMENTUM * prevDeltaHiddenOutput[i][j];
                        weightsHiddenToOutput[i][j] += delta;
                        prevDeltaHiddenOutput[i][j] = delta;
                    }
                }

                // Output biases
                for (int i = 0; i < OUTPUT_SIZE; i++) {
                    outputBiases[i] += LEARNING_RATE * outputErrors[i];
                }

                // Input to hidden weights
                for (int i = 0; i < INPUT_SIZE; i++) {
                    for (int j = 0; j < HIDDEN_SIZE; j++) {
                        float delta = LEARNING_RATE * hiddenErrors[j] * sample.inputs[i] + MOMENTUM * prevDeltaInputHidden[i][j];
                        weightsInputToHidden[i][j] += delta;
                        prevDeltaInputHidden[i][j] = delta;
                    }
                }

                // Hidden biases
                for (int i = 0; i < HIDDEN_SIZE; i++) {
                    hiddenBiases[i] += LEARNING_RATE * hiddenErrors[i];
                }
            }
        }
    }

    /**
     * Sigmoid activation function
     */
    private static float sigmoid(float x) {
        return (float) (1.0 / (1.0 + Math.exp(-x)));
    }

    /**
     * Derivative of sigmoid function
     */
    private static float sigmoidDerivative(float x) {
        return x * (1 - x);
    }

    /**
     * Shuffle an array
     */
    private static void shuffleArray(Object[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            Object temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    /**
     * Set target rotation
     */
    public static void setTargetRotation(float yaw, float pitch) {
        targetYaw = limitAngle(yaw, currentYaw, 180);
        targetPitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        rotating = true;
        lastRotationTime = System.currentTimeMillis();
    }

    /**
     * Update rotations - should be called every tick
     */
    /**
     * Get current yaw rotation
     * @return current yaw value
     */
    public static float getCurrentYaw() {
        return currentYaw;
    }

    /**
     * Get current pitch rotation
     * @return current pitch value
     */
    public static float getCurrentPitch() {
        return currentPitch;
    }


    /**
     * Reset rotations to player's current view
     */
    public static void resetRotations() {
        if (mc.player == null) return;

        currentYaw = mc.player.rotationYaw;
        currentPitch = mc.player.rotationPitch;
        targetYaw = currentYaw;
        targetPitch = currentPitch;
        rotating = false;
    }

    /**
     * Check if currently rotating
     */
    public static boolean isRotating() {
        return rotating;
    }




    /**
     * Limit an angle between a min and max value
     */
    private static float limitAngle(float angleToLimit, float angleReference, float maxChange) {
        float angleDifference = MathHelper.wrapDegrees(angleToLimit - angleReference);

        if (angleDifference > maxChange) {
            angleDifference = maxChange;
        }

        if (angleDifference < -maxChange) {
            angleDifference = -maxChange;
        }

        return MathHelper.wrapDegrees(angleReference + angleDifference);
    }

    /**
     * Save neural network weights to file
     */
    private static void saveWeights() {
        try {
            // Fixed file path reference
            File file = new File(Client.getInstance().file, WEIGHTS_FILE);
            DataOutputStream out = new DataOutputStream(new FileOutputStream(file));

            // Save input to hidden weights
            for (int i = 0; i < INPUT_SIZE; i++) {
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    out.writeFloat(weightsInputToHidden[i][j]);
                }
            }

            // Save hidden biases
            for (int i = 0; i < HIDDEN_SIZE; i++) {
                out.writeFloat(hiddenBiases[i]);
            }

            // Save hidden to output weights
            for (int i = 0; i < HIDDEN_SIZE; i++) {
                for (int j = 0; j < OUTPUT_SIZE; j++) {
                    out.writeFloat(weightsHiddenToOutput[i][j]);
                }
            }

            // Save output biases
            for (int i = 0; i < OUTPUT_SIZE; i++) {
                out.writeFloat(outputBiases[i]);
            }

            out.close();
        } catch (Exception e) {
            // Fixed Client reference
            Client.logger.error("Error saving JelloAI weights", e);
        }
    }

    /**
     * Load neural network weights from file
     */
    private static boolean loadWeights() {
        try {
            // Fixed file path reference
            File file = new File(Client.getInstance().file, WEIGHTS_FILE);
            if (!file.exists()) return false;

            DataInputStream in = new DataInputStream(new FileInputStream(file));

            // Load input to hidden weights
            for (int i = 0; i < INPUT_SIZE; i++) {
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    weightsInputToHidden[i][j] = in.readFloat();
                }
            }

            // Load hidden biases
            for (int i = 0; i < HIDDEN_SIZE; i++) {
                hiddenBiases[i] = in.readFloat();
            }

            // Load hidden to output weights
            for (int i = 0; i < HIDDEN_SIZE; i++) {
                for (int j = 0; j < OUTPUT_SIZE; j++) {
                    weightsHiddenToOutput[i][j] = in.readFloat();
                }
            }

            // Load output biases
            for (int i = 0; i < OUTPUT_SIZE; i++) {
                outputBiases[i] = in.readFloat();
            }

            in.close();
            return true;
        } catch (Exception e) {
            // Fixed Client reference
            Client.logger.error("Error loading JelloAI weights", e);
            return false;
        }
    }

    /**
     * Save training data to file
     */
    private static void saveTrainingData() {
        try {
            // Fixed file path reference - corrected syntax error
            File file = new File(Client.getInstance().file, TRAINING_FILE);
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));

            // Convert queue to array for saving
            TrainingSample[] samples = trainingSamples.toArray(new TrainingSample[0]);
            out.writeObject(samples);

            out.close();
        } catch (Exception e) {
            // Fixed Client reference
            Client.logger.error("Error saving JelloAI training data", e);
        }
    }

    /**
     * Load training data from file
     */
    private static void loadTrainingData() {
        try {
            // Fixed file path reference
            File file = new File(Client.getInstance().file, TRAINING_FILE);
            if (!file.exists()) return;

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));

            // Read samples and add to queue
            TrainingSample[] samples = (TrainingSample[]) in.readObject();
            for (TrainingSample sample : samples) {
                trainingSamples.add(sample);
            }

            in.close();
        } catch (Exception e) {
            // Fixed Client reference
            Client.logger.error("Error loading JelloAI training data", e);

        }
    }

    /**
     * Apply server-side rotations without changing client-side camera
     */
    public static void applyServerRotation() {
        if (!rotating || mc.player == null) return;

        // Store original rotations
        float originalYaw = mc.player.rotationYaw;
        float originalPitch = mc.player.rotationPitch;

        // Apply calculated rotations
        mc.player.rotationYaw = currentYaw;
        mc.player.rotationPitch = currentPitch;

        // Restore client-side rotations (for camera)
        mc.player.prevRotationYaw = originalYaw;
        mc.player.prevRotationPitch = originalPitch;
    }

    /**
     * Get smoothed rotation values for a target position
     * @param targetX Target X position
     * @param targetY Target Y position
     * @param targetZ Target Z position
     * @return float[] containing yaw and pitch
     */
    public static float[] getRotationsToPosition(double targetX, double targetY, double targetZ) {
        if (mc.player == null) return new float[] {0, 0};

        double playerX = mc.player.getPosX();
        double playerY = mc.player.getPosY() + mc.player.getEyeHeight();
        double playerZ = mc.player.getPosZ();

        double diffX = targetX - playerX;
        double diffY = targetY - playerY;
        double diffZ = targetZ - playerZ;

        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

        // Apply neural network smoothing
        float[] inputs = new float[INPUT_SIZE];
        inputs[0] = (float) (diffX / 20.0);
        inputs[1] = (float) (diffY / 10.0);
        inputs[2] = (float) (diffZ / 20.0);
        inputs[3] = 0;
        inputs[4] = 0;
        inputs[5] = 0;
        inputs[6] = mc.player.rotationYaw / 180.0f;
        inputs[7] = mc.player.rotationPitch / 90.0f;

        float[] outputs = forwardPass(inputs);

        // Convert outputs to rotations
        float smoothedYaw = outputs[0] * 360.0f - 180.0f;
        float smoothedPitch = outputs[1] * 180.0f - 90.0f;

        return new float[] {smoothedYaw, smoothedPitch};
    }

    /**
     * Check if current rotation is close enough to target
     * @param targetYaw Target yaw
     * @param targetPitch Target pitch
     * @param threshold Angle threshold in degrees
     * @return true if rotation is within threshold
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

        // Set target to current player view
        targetYaw = mc.player.rotationYaw;
        targetPitch = mc.player.rotationPitch;

        // Keep rotating flag true to smoothly transition
        rotating = true;
        lastRotationTime = System.currentTimeMillis();
    }
}